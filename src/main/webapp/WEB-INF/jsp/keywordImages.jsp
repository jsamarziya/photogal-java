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
	<fmt:param value="Photos matching keyword ${fn:escapeXml(keyword)}" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<pg:favicon />
</head>
<body>
<pg:header />
<div class="title">Photos matching keyword <c:out
	value="${command.keyword}" /></div>
<c:choose>
	<c:when test="${imageCount > 0}">
		<pg:displayThumbnails images="${images}"
			scaledImageCalculator="${scaledImageCalculator}" imagesPerRow="6" />
	</c:when>
	<c:otherwise>
		<div>No matching images.</div>
	</c:otherwise>
</c:choose>
<c:url var="pageURL" value="/showKeywordImages.do">
	<c:param name="keyword" value="${command.keyword}" />
</c:url>
<pg:pager currentPage="${command.currentPage}" itemCount="${imageCount}"
	itemsPerPage="${command.itemsPerPage}" pageURL="${pageURL}" />
<pg:footer />
</body>
</html>
