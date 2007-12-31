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
	<fmt:param value="Edit gallery" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<c:url value="/edit.css"/>" rel="stylesheet" type="text/css" />
</head>
<body>
<script type="text/javascript">
    var galleryId = ${gallery.id};
</script>
<pg:defineBaseURL />
<script src="<c:url value="/js/wz_dragdrop.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/global.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/xmlrequest.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/edit/ddSupport.js"/>"
	type="text/javascript"></script>
<script src="<c:url value="/js/edit/liveThumbnail.js"/>"
	type="text/javascript"></script>
<script src="<c:url value="/js/edit/gallerySelector.js"/>"
	type="text/javascript"></script>
<c:url var="editGalleryURL" value="/edit/editGallery.do">
	<c:param name="id" value="${gallery.id}" />
	<c:param name="standalone" value="false" />
	<c:param name="returnTo" value="/edit/editGalleryDone" />
	<c:param name="cancelTo" value="redirect:/blank.html" />
</c:url>
<div class="title">Edit Gallery <i><c:out
	value="${gallery.name}" /></i></div>
<div style="margin-bottom: 10px;"><a href="${editGalleryURL}"
	target="galleryEditor">Edit gallery info</a></div>
<div id="thumbnailGroup" class="thumbnailGroup"></div>
<div style="padding-top: 25px; clear: left;"><a
	href="<c:url value="${returnTo}"/>" target="_top">[Return to <c:out
	value="${returnToName}" />]</a></div>
<pg:transactionStatus />
<script type="text/javascript">
SET_DHTML();
</script>
<script type="text/javascript">
<c:forEach items="${gallery.images}" var="image">
  addImage(${image.id}, '${fn:replace(image.title, "'", "\\'")}', '${fn:escapeXml(image.location)}');
</c:forEach>
</script>
</body>
</html>
