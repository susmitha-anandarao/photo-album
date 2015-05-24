<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<f:view>
		<h:form enctype="multipart/form-data">
			<h:panelGrid border="0" columns="2">
				<h:panelGrid border="0" columns="1">
					<h:panelGrid border="0" columns="2">
						<h:inputFile value="#{album.file}" />
						<h:commandButton value="Upload" type="submit"
							action="#{album.upload}" />
					</h:panelGrid>
					<h:panelGrid border="0" columns="2">
						<h:selectOneListbox value="#{album.display}">
							<f:selectItems value="#{album.photos}" />
						</h:selectOneListbox>
						<h:commandButton value="Display" action="#{album.displayit()}"></h:commandButton>
					</h:panelGrid>
					<h:panelGrid border="0" columns="2">
						<h:selectOneListbox value="#{album.delete}">
							<f:selectItems value="#{album.photos}" />
						</h:selectOneListbox>
						<h:commandButton value="Delete" action="#{album.deleteit}"></h:commandButton>
					</h:panelGrid>
				</h:panelGrid>
				<h:graphicImage url="#{album.photoURL}" width="300" height=""></h:graphicImage>

			</h:panelGrid>
			<h:panelGroup>
				<h:outputText value = "#{album.errorFile }"></h:outputText>
			</h:panelGroup>
		</h:form>
	</f:view>
</body>
</html>