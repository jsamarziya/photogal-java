<%--
    Copyright 2007 The Photogal Team.

    This file is part of photogal.

    photogal is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    photogal is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with photogal.  If not, see <http://www.gnu.org/licenses/>.
--%>
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="Image file browser" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<c:url value="/edit.css"/>" rel="stylesheet" type="text/css" />
<script type="text/javascript">
var galleryId = ${gallery.id};
</script>
<pg:defineBaseURL />
<script src="<c:url value="/js/global.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/blendtrans.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/edit/imageFileBrowser.js"/>"
	type="text/javascript"></script>
<script type="text/javascript">
function initializeFileBrowserTable() {
<c:forEach var="entry" items="${imageDescriptors}">
  <c:forEach var="gallery" items="${entry.value.galleries}">
    imageAdded("${pg:escapeJavaScript(pg:normalizedPath(entry.key.path))}", ${gallery.id});
  </c:forEach>
</c:forEach>
}
</script>
</head>
<body onload="initializeFileBrowserTable()">
<div class="smallText" style="position: fixed;">Contents of <c:out
	value="${pg:normalizedPath(currentDirectory.path)}" /><a
	href="javascript:showOptions();" style="margin-left: 4em;"
	class="plain">[Options]</a></div>
<table class="thumbnail" id="fileBrowserTable" style="margin-top: 10px">
	<tr>
		<c:if test="${parentDirectory != null}">
			<c:url value="/edit/showImageFileBrowser.do" var="parentDirectoryURL">
				<c:param name="dir" value="${parentDirectory.path}" />
				<c:param name="galleryId" value="${gallery.id}" />
			</c:url>
			<td class="fileBrowser">
			<div class="thumbnail"><a href="${parentDirectoryURL}"><img
				src="<c:url value="/images/folderUp50x42.png"/>" class="folderIcon" /></a></div>
			<div class="thumbnailLabel"><a href="${parentDirectoryURL}"
				class="smallText plain">(Up to parent directory)</a></div>
			</td>
		</c:if>
		<c:forEach var="dir" items="${subdirectories}">
			<c:url value="/edit/showImageFileBrowser.do" var="subdirectoryURL">
				<c:param name="dir" value="${dir.path}" />
				<c:param name="galleryId" value="${gallery.id}" />
			</c:url>
			<td class="fileBrowser">
			<div class="thumbnail"><a href="${subdirectoryURL}"><img
				src="<c:url value="/images/folder50x42.png"/>" class="folderIcon" /></a></div>
			<div class="thumbnailLabel"><a href="${subdirectoryURL}"
				class="smallText plain"><c:out value="${dir.name}" /></a></div>
			</td>
		</c:forEach>
		<c:forEach var="imageFile" items="${imageFiles}">
			<c:set var="imageFilePath" value="${pg:normalizedPath(imageFile.path) }"/>
			<c:url var="imageFileURL" value="/edit/editImage.do">
				<c:param name="location" value="${imageFilePath}" />
				<c:param name="galleryId" value="${gallery.id}" />
				<c:param name="returnTo" value="/edit/editImageDone" />
				<c:param name="cancelTo" value="redirect:/blank.html" />
			</c:url>
			<c:url var="imgSrc" value="/edit/showImageFile.do">
				<c:param name="file" value="${imageFilePath}" />
				<c:param name="size" value="t" />
			</c:url>
			<c:set var="tdId" value="tn:${imageFilePath}" />
			<c:set var="imgId" value="img:${imageFilePath}" />
			<td class="fileBrowser" id="${pg:escapeId(tdId)}">
			<div class="thumbnail"><img id="${pg:escapeId(imgId)}"
				src="${imgSrc}" class="thumbnail" style="visibility: hidden;"
				onload="verticallyCenterImageOnload(this)"
				onclick="top.galleryEditor.location='${pg:escapeJavaScript(imageFileURL)}'" /></div>
			<div class="thumbnailLabel"><a href="${imageFileURL}"
				target="galleryEditor" class="smallText plain"><c:out
				value="${imageFile.name}" /></a></div>
			</td>
		</c:forEach>
	</tr>
</table>
</body>
</html>
