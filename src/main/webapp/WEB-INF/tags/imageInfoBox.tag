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
<%@ tag body-content="empty" description="Writes the image info box"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>
<%@ attribute name="location" required="true"%>
<%@ attribute name="width" required="true"%>
<%@ attribute name="height" required="true"%>
<%@ attribute name="isPublic" required="true"%>
<%@ attribute name="galleries" required="true"
	type="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="imageInfoBox"
	style="position: absolute; right: 0px; top: 0px;">
<div class="smallText" style="margin-bottom: 5px;">Image
Information</div>
<div class="smallText">File: <c:out value="${location}" /></div>
<div class="smallText">Size: ${width}x${height}</div>
<div class="smallText">Public: <c:choose>
	<c:when test="${isPublic}">yes</c:when>
	<c:otherwise>no</c:otherwise>
</c:choose></div>
<div class="smallText">Contained in <c:if
	test="${empty galleries}">
	<span class="containedIn">no galleries</span>
</c:if><c:forEach var="gallery" items="${galleries}">
	<div class="containedIn"><c:out value="${gallery.name}" /></div>
</c:forEach></div>
</div>
