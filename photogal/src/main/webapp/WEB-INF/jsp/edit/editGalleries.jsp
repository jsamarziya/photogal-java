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
	<fmt:param value="Edit galleries" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<c:url value="/edit.css"/>" rel="stylesheet" type="text/css" />
<pg:favicon />
</head>
<body>
<pg:header />
<pg:defineBaseURL />
<script src="<c:url value="/js/wz_dragdrop.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/blendtrans.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/global.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/xmlrequest.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/edit/ddSupport.js"/>"
	type="text/javascript"></script>
<script src="<c:url value="/js/edit/liveThumbnail.js"/>"
	type="text/javascript"></script>
<script src="<c:url value="/js/edit/editGalleries.js"/>"
	type="text/javascript"></script>

<div class="title">Edit Galleries</div>

<div id="thumbnailGroup" class="thumbnailGroup"></div>

<c:url var="createGalleryURL" value="/edit/editGallery.do">
	<c:param name="id" value="new" />
	<c:param name="standalone" value="true" />
	<c:param name="returnTo" value="redirect:/edit/editGalleries.do" />
	<c:param name="cancelTo" value="redirect:/edit/editGalleries.do" />
</c:url>
<div style="padding-top: 25px; clear: left;"><a
	href="${createGalleryURL}">[Create New Gallery]</a></div>

<pg:transactionStatus />

<c:forEach items="${galleryList}" var="gallery">
	<div id="galleryInfo:${gallery.id}" class="popup"
		style="visibility: hidden;">
	<div class="popupTitle"></div>
	<div class="popupContent">
	<div class="galleryInfoName"><c:out value="${gallery.name}" /></div>
	<div class="galleryInfoDetail"><fmt:message
		key="gallery.imageCount">
		<fmt:param value="${gallery.imageCount}" />
	</fmt:message></div>
	<div class="galleryInfoDetail"><c:choose>
		<c:when test="${gallery.public}">Public</c:when>
		<c:otherwise>Private</c:otherwise>
	</c:choose> gallery</div>
	<div class="galleryInfoDetail">Last Modified <fmt:formatDate
		value="${gallery.lastModified}" pattern="M/d/yyyy" /></div>
	</div>
	</div>
</c:forEach>

<script type="text/javascript">
SET_DHTML();
</script>

<script type="text/javascript">
<c:forEach items="${galleryList}" var="gallery">
  <c:choose>
    <c:when test="${gallery.galleryImageOrDefault.location == null}">
      <c:set var="imageLocation" value="null"/>
    </c:when>
    <c:otherwise>
      <c:set var="imageLocation" value="'${fn:escapeXml(gallery.galleryImageOrDefault.location)}'"/>
    </c:otherwise>
  </c:choose>
    addGallery(${gallery.id}, '${fn:escapeXml(gallery.name)}', ${imageLocation}, ${gallery.public});
</c:forEach>
</script>
<pg:footer />
</body>
</html>
