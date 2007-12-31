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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<c:url var="imageURL" value="/edit/showImageFile.do">
	<c:param name="file" value="${command.location}" />
</c:url>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="Edit image" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<c:url value="/edit.css"/>" rel="stylesheet" type="text/css" />
<link href="<c:url value="/zoomPopup.css"/>" rel="stylesheet"
	type="text/css" />
<script src="<c:url value="/js/global.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/zoomPopup.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/edit/textarea-resize.js"/>"
	type="text/javascript"></script>
<script src="<c:url value="/js/edit/form.js"/>" type="text/javascript"></script>
<script type="text/javascript">
function init() {
    if (top.minimizeFileSelector) {
        top.minimizeFileSelector();
    }
    initializeTextareaSize(document.imageForm.description);
}
function uninit() {
    if (top.restoreFileSelector) {
        top.restoreFileSelector();
    }
}
</script>
</head>

<body onload="init()"
	onbeforeunload="checkUnsavedChanges(event, document.imageForm)"
	onunload="uninit()">
<c:if test="${command.standalone}">
	<pg:header />
</c:if>
<div id="bodyContent"><c:if test="${command.standalone}">
	<div class="title">Edit Image</div>
</c:if>
<div style="margin-bottom: 5px;"><img id="thumbnail" /></div>
<script type="text/javascript">
var popup = new ZoomPopup();
popup.setImage(document.getElementById("thumbnail"));
popup.setImageURL('${imageURL}');
<c:forEach items="${command.availableSizes}" var="size">
popup.setSizeEnabled('${size.key}', ${size.value}); 
</c:forEach>
popup.setCurrentSize('s');
</script>
<div>
<form name="imageForm" method="post"
	action="<c:url value="/edit/editImage.do"/>"
	onsubmit="this.submitted=true"><input type="hidden"
	name="imageId" value="${command.imageId}" /><input type="hidden"
	name="galleryId" value="${command.galleryId}" /><input type="hidden"
	name="location" value="${command.location}" /><input type="hidden"
	name="returnTo" value="${command.returnTo}" /><input type="hidden"
	name="cancelTo" value="${command.cancelTo}" /><input type="hidden"
	name="action" value="save" />
<table class="form">
	<tr>
		<td class="formHeader">Title</td>
		<td class="form"><input type="text" name="title"
			value="${fn:escapeXml(command.title)}" size="40" class="auto"
			onfocus="this.className='autoHilight'" onblur="this.className='auto'" /></td>
	</tr>
	<tr>
		<td class="formHeader">Description</td>
		<td class="form"><textarea name="description" rows="1" cols="70"
			class="auto" onfocus="this.className='autoHilight'"
			onblur="this.className='auto'" onkeyup="setTextareaSizeToFit(this)"><c:out
			value="${command.description}" /></textarea></td>
	</tr>
	<tr>
		<td class="formHeader">Keywords</td>
		<td class="form"><input type="text" name="keywords"
			value="${fn:escapeXml(command.keywords)}" size="40" class="auto"
			onfocus="this.className='autoHilight'" onblur="this.className='auto'" /></td>
	</tr>
	<tr>
		<td class="formHeader">Date</td>
		<td class="form"><input type="text" name="imageCreationDate"
			value="${fn:escapeXml(command.imageCreationDate)}" size="10"
			class="auto" onfocus="this.className='autoHilight'"
			onblur="this.className='auto'" /></td>
	</tr>
	<c:if test="${command.galleryId != null}">
		<tr>
			<td colspan="2" class="form"><input type="checkbox"
				name="galleryImage" ${command.galleryImage? 'checked="checked" ' : ""} />
			Use this image for the gallery thumbnail image</td>
		</tr>
	</c:if>
</table>
<script type="text/javascript">
    document.imageForm.title.select();
</script>

<div class="form">
<button type="submit" class="form">Save</button>
<button type="submit" class="form" style="margin-left: 10px;"
	onclick="document.imageForm.action.value='cancel'">Cancel</button>
</div>
</form>
</div>
<pg:imageInfoBox location="${command.location}" width="${command.width}"
	height="${command.height}" isPublic="${command.public}"
	galleries="${command.galleries}" /></div>
<c:if test="${command.standalone}">
	<pg:footer />
</c:if>
</body>
</html>
