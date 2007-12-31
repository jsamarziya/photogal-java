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

<c:set var="galleriesPerRow" value="5" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="Photo Galleries" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<pg:favicon />
</head>
<body>
<pg:header selected="galleries" />
<div class="title">Photo Galleries</div>

<c:set var="galleryCount" value="0" />
<table border="0" cellpadding="0" cellspacing="0"
	style="margin-top: 25px;">
	<c:forEach var="gallery" items="${galleryList}">
		<c:if test="${!empty gallery.galleryImageOrDefault}">
			<c:url var="showGalleryURL"
				value="showGallery.do?galleryId=${gallery.id}" />
			<c:set var="imageSize"
				value="${pg:scaledSize(scaledImageCalculator, gallery.galleryImageOrDefault.size, 't')}" />
			<c:set var="imageHeight"
				value="${fn:substringBefore(imageSize.height, '.')}" />
			<c:set var="imageWidth"
				value="${fn:substringBefore(imageSize.width, '.')}" />
			<c:set var="marginTop" value="${(110 - imageHeight)/2}" />

			<c:if test="${galleryCount % galleriesPerRow == 0}">
				<%="<tr class='galleryRow'>"%>
			</c:if>
			<td class="galleryCell"><c:url var="imageURL"
				value="showImage.do?imageId=${gallery.galleryImageOrDefault.id}&size=t" />
			<div class="gallery"><a href="${showGalleryURL}"><img
				src="${imageURL}" class="thumbnail" width="${imageWidth}"
				height="${imageHeight}" style="margin-top: ${marginTop}px" /></a></div>
			<div class="galleryTitle"><a href="${showGalleryURL}"
				class="galleryTitle"><c:out value="${gallery.name}" /></a><c:if
				test="${not gallery.public}">
				<span class="smallText"><fmt:message key="marker.private" /></span>
			</c:if></div>
			<div class="smallText"><fmt:message key="gallery.imageCount">
				<fmt:param value="${gallery.imageCount}" />
			</fmt:message></div>
			</td>
			<c:set var="galleryCount" value="${galleryCount + 1}" />
			<c:if test="${galleryCount % galleriesPerRow == 0}">
				<%="</tr>"%>
			</c:if>
		</c:if>
	</c:forEach>
	<c:if
		test="${galleryCount != 0 && galleryCount % galleriesPerRow != 0}">
		<%="</tr>"%>
	</c:if>
</table>
<pg:footer />
</body>
</html>
