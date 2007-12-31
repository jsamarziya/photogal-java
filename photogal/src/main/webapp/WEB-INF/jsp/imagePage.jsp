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

<fmt:message key="format.date.imageCreated" var="imageCreatedDateFormat" />
<fmt:message key="format.date.imageAdded" var="imageAddedDateFormat" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="${fn:escapeXml(image.title)}" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<pg:favicon />
<script src="<c:url value="/js/imagePage.js"/>" type="text/javascript"></script>
<script type="text/javascript">
  var imageURL = '<c:url value="showImage.do?imageId=${image.id}" />';
  function initImage() {
      setImageSize("${pg:isAvailableSize(scaledImageCalculator, image, 'm')? 'm' : 'o'}");
  }
</script>
</head>
<body onload="initImage()">
<pg:header />
<div><span class="title"><c:out value="${image.title}" /></span><c:if
	test="${pg:canEdit(pageContext.request)}">
	<c:set var="returnToURL" value="/edit/returnToImagePage" />
	<c:url var="editImageURL" value="/edit/editImage.do">
		<c:param name="imageId" value="${image.id}" />
		<c:param name="galleryId" value="${gallery.id}" />
		<c:param name="standalone" value="true" />
		<c:param name="returnTo" value="${returnToURL}" />
		<c:param name="cancelTo" value="${returnToURL}" />
	</c:url>
	<a href="${editImageURL}" style="margin-left: 10px;">Edit</a>
</c:if></div>
<div style="float:left;"><img id="image" />
<div class="imageDescription" style=""><c:out
	value="${image.description}" escapeXml="false" /></div>
</div>

<div class="imageInfoBox" style="float:right;"><c:if
	test="${gallery != null}">
	<c:url var="showGalleryURL" value="/showGallery.do">
		<c:param name="galleryId" value="${gallery.id}" />
	</c:url>
	<div class="imageInfoTitle"><a href="${showGalleryURL}"><c:out
		value="${gallery.name}" /></a></div>
	<div class="imageInfo"><pg:imageNavigator /></div>
</c:if>
<div class="imageInfo">Available sizes:</div>
<c:forTokens var="size" items="s m l" delims=" ">
	<c:if test="${pg:isAvailableSize(scaledImageCalculator, image, size)}">
		<c:set var="scaledSize"
			value="${pg:scaledSize(scaledImageCalculator, image.size, size)}" />
		<div class="imageInfoIndented"><a id="size_${size}"
			href="javascript:setImageSize('${size}')" class="imageSize"><fmt:message
			key="imageSize.${size}" /></a> (<c:out
			value="${fn:substringBefore(scaledSize.width, '.')}" /> x <c:out
			value="${fn:substringBefore(scaledSize.height, '.')}" />)</div>
	</c:if>
</c:forTokens>
<div class="imageInfoIndented"><a id="size_o"
	href="javascript:setImageSize('o')" class="imageSize"><fmt:message
	key="imageSize.o" /></a> (<c:out value="${image.width}" /> x <c:out
	value="${image.height}" />)</div>
<c:if test="${not empty image.keywords}">
	<div class="imageInfo">Keywords:</div>
	<c:forEach var="keyword" items="${image.keywords}">
		<div class="imageInfoIndented"><a
			href="<c:url value="/showKeywordImages.do"><c:param name="keyword" value="${keyword}"/></c:url>"><c:out
			value="${keyword}" /></a></div>
	</c:forEach>
</c:if> <c:if test="${not empty image.imageCreationDate}">
	<div class="imageInfo">Taken <c:out
		value="${empty image.imageCreationDate.day?
	'in' : 'on'}" /> <pg:formatCalendarDate
		value="${image.imageCreationDate}" pattern="${imageCreatedDateFormat}" />.</div>
</c:if>
<div class="imageInfo">Added on <fmt:formatDate
	pattern="${imageAddedDateFormat}" value="${image.creationDate}" />.</div>
<c:if test="${not image.public}">
	<div class="imageInfo">This is a private image.</div>
</c:if></div>
<pg:footer />
</body>
</html>
