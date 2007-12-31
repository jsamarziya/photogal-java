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

<c:set var="editingAction"
	value="${command.newGallery ? 'Create' : 'Edit'}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="${editingAction} gallery" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<c:url value="/edit.css"/>" rel="stylesheet" type="text/css" />
<pg:favicon />
<script src="<c:url value="/js/edit/textarea-resize.js"/>"
	type="text/javascript"></script>
<script src="<c:url value="/js/edit/form.js"/>" type="text/javascript"></script>
<script type="text/javascript">
function init() {
    if (top.minimizeFileSelector) {
        top.minimizeFileSelector();
    }
    initializeTextareaSize(document.galleryForm.description);
    document.galleryForm.name.select();
}
function uninit() {
    if (top.restoreFileSelector) {
        top.restoreFileSelector();
    }
}
</script>
</head>

<body onload="init()"
	onbeforeunload="checkUnsavedChanges(event, document.galleryForm)"
	onunload="uninit()">
<c:if test="${command.standalone}">
	<pg:header />
	<div class="title">${editingAction} Gallery</div>
</c:if>
<form name="galleryForm" method="post"
	action="<c:url value="/edit/editGallery.do"/>"
	onsubmit="this.submitted=true"><input type="hidden" name="id"
	value="${command.id}" /><input type="hidden" name="returnTo"
	value="${command.returnTo}" /><input type="hidden" name="cancelTo"
	value="${command.cancelTo}" /><input type="hidden" name="action"
	value="save" />
<table class="form">
	<tr>
		<td class="formHeader">Name</td>
		<td class="form"><input type="text" name="name"
			value="${fn:escapeXml(command.name)}" size="40" class="auto"
			onfocus="this.className='autoHilight'" onblur="this.className='auto'" />
		</td>
	</tr>
	<tr>
		<td class="formHeader">Description</td>
		<td class="form"><textarea name="description" rows="1" cols="70"
			class="auto" onfocus="this.className='autoHilight'"
			onblur="this.className='auto';" onkeyup="setTextareaSizeToFit(this)"><c:out
			value="${command.description}" /></textarea></td>
	</tr>
	<tr>
		<td class="formHeader">Public</td>
		<td class="form"><input type="radio" name="public" value="true"
			${command.public ? 'checked="true" ' : ''} /> Yes <input
			type="radio" name="public" value="false"
			${command.public ? '' : 'checked="true" '} /> No</td>
	</tr>
</table>
<div class="form">
<button type="submit" class="form">Save</button>
<button type="submit" class="form"
	onclick="document.galleryForm.action.value='cancel'">Cancel</button>
</div>
</form>
<c:if test="${command.standalone}">
	<pg:footer />
</c:if>
</body>
</html>
