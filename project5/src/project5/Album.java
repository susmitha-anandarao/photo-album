/*
 * References
 * 1. http://docs.oracle.com/javaee/6/api/javax/faces/context/ExternalContext.html
 * 2. http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
 * */

package project5;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

@ManagedBean(name = "album")
@SessionScoped
public class Album {
	Part file;
	String display;
	Map<String, String> photos = new LinkedHashMap<String, String>();
	String delete;
	String photoURL;
	public String getErrorFile() {
		return errorFile;
	}

	public void setErrorFile(String errorFile) {
		this.errorFile = errorFile;
	}

	String errorFile;
	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public Map<String, String> getPhotos() {
		// the photos in the dropbox folder cse5335album are stored in this map
		// with key as name and value as path of file
		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		String accessToken = "f1yZab4fOd8AAAAAAAABlQZdthVnzPdkUiMQ3e6_XQakd_k1ZZU09udM7WlyBkEC";
		DbxClient client = new DbxClient(config, accessToken);

		DbxEntry.WithChildren listing = null;
		try {
			listing = client.getMetadataWithChildren("/");
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (DbxEntry child : listing.children) {
			photos.put(child.name, child.path);
		}
		return photos;
	}

	public void setPhotos(Map<String, String> photos) {
		this.photos = photos;
	}

	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	// stores the file part from inputfile tag of jsf
	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public void upload() throws DbxException, IOException {
		// upload a given file to dropbox cse5335album folder
		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		String accessToken = "f1yZab4fOd8AAAAAAAABlQZdthVnzPdkUiMQ3e6_XQakd_k1ZZU09udM7WlyBkEC";
		DbxClient client = new DbxClient(config, accessToken);
		// get the file part from the form
		String filename = getFilename(getFile());
		if (!filename.endsWith(".jpg")) {
			setErrorFile(filename + " does not have a jpg extension");
			ExternalContext externalContext = FacesContext
					.getCurrentInstance().getExternalContext();
			externalContext.redirect("album.jsp");	

		} else {
			// get inputstream of the part
			
			FileInputStream inputStream = (FileInputStream) getFile()
					.getInputStream();
			try {
				// upload the file to dropbox
				DbxEntry.File uploadedFile = client.uploadFile("/" + filename,
						DbxWriteMode.add(), inputStream.available(),
						inputStream);

			} finally {
				// close the input stream and refresh the page
				inputStream.close();
				setErrorFile("");
				ExternalContext externalContext = FacesContext
						.getCurrentInstance().getExternalContext();
				externalContext.redirect("album.jsp");
			}
		}

	}

	public String getFilename(Part part) {
		// this method extracts the filename from the part using its header
		// information
		String filename = "";
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				filename = content.substring(content.indexOf('=') + 1).trim()
						.replace("\"", "");
				return filename;
			}
		}
		return filename;
	}

	public void displayit() {
		// display the image on the specified space of the webpage
		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		String accessToken = "f1yZab4fOd8AAAAAAAABlQZdthVnzPdkUiMQ3e6_XQakd_k1ZZU09udM7WlyBkEC";
		DbxClient client = new DbxClient(config, accessToken);
		try {
			// shareable url of the image stored in dropbox is fetched
			String url = client.createShareableUrl(getDisplay()).replace(
					"://www.", "://dl.");
			// photoURL variable is updated
			setPhotoURL(url);
			// refresh the page
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.redirect("album.jsp");
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// outputStream.close();
		}
	}

	public void deleteit() {
		// delete a file from the dropbox cse5335album folder
		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		String accessToken = "f1yZab4fOd8AAAAAAAABlQZdthVnzPdkUiMQ3e6_XQakd_k1ZZU09udM7WlyBkEC";
		DbxClient client = new DbxClient(config, accessToken);
		try {
			// delete file by giving the pathname
			client.delete(getDelete());
			DbxEntry.WithChildren listing = null;
			try {
				listing = client.getMetadataWithChildren("/");
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// update the photos map to display the list of files after deletion
			photos = new LinkedHashMap<String, String>();
			for (DbxEntry child : listing.children) {
				photos.put(child.name, child.path);
			}
			setPhotos(photos);
			// refresh page
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.redirect("album.jsp");
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}