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

<!-- This page is used to redirect back to showImagePage after editing image. -->

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${imageId == null}">
	<c:set var="imageId" value="${image.id}" />
</c:if>

<c:redirect url="/showImagePage.do">
	<c:param name="imageId" value="${imageId}" />
	<c:if test="${galleryId != null}">
		<c:param name="galleryId" value="${galleryId}" />
	</c:if>
</c:redirect>
