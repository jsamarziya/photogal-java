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
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<c:url var="gallerySelectorURL" value="/edit/organizeGallerySelector.do">
	<c:param name="galleryId" value="${galleryId}" />
	<c:param name="returnTo" value="${returnTo}" />
	<c:param name="returnToName" value="${returnToName}" />
</c:url>
<c:url var="fileBrowserURL" value="/edit/showImageFileBrowser.do">
	<c:param name="dir" value="" />
	<c:param name="galleryId" value="${galleryId}" />
</c:url>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="Edit gallery" />
</fmt:message></title>
<pg:favicon />
<script src="<c:url value="/js/edit/organizeGallery.js"/>"
	type="text/javascript"></script>
</head>
<frameset cols="380,*">
	<frame name="gallerySelector" src="${gallerySelectorURL}" />
	<frameset id="rightSide" rows="*,190">
		<frame name="galleryEditor" />
		<frame name="fileSelector" src="${fileBrowserURL}" />
	</frameset>
</frameset>
</html>
