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
<%@ tag body-content="empty" description="Renders a set of thumbnails"%>
<%@ attribute name="images" required="true" type="java.util.Collection"%>
<%@ attribute name="galleryId" required="false"%>
<%@ attribute name="scaledImageCalculator" required="true"
	type="net.sourceforge.photogal.image.ScaledImageCalculator"%>
<%@ attribute name="imagesPerRow" required="false"
	type="java.lang.Integer"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<c:if test="${empty imagesPerRow}">
	<c:set var="imagesPerRow" value="5" />
</c:if>
<table border="0" cellpadding="0" cellspacing="0"
	style="margin-top: 25px;">
	<c:forEach var="image" varStatus="loopStatus" items="${images}">
		<c:url var="imageURL" value="showImage.do">
			<c:param name="imageId" value="${image.id}" />
			<c:param name="size" value="t" />
		</c:url>
		<c:url var="showImagePageURL" value="showImagePage.do">
			<c:param name="imageId" value="${image.id}" />
			<c:if test="${galleryId != null}">
				<c:param name="galleryId" value="${galleryId}" />
			</c:if>
		</c:url>
		<c:set var="imageSize"
			value="${pg:scaledSize(scaledImageCalculator, image.size, 't')}" />
		<c:set var="imageHeight"
			value="${fn:substringBefore(imageSize.height, '.')}" />
		<c:set var="imageWidth"
			value="${fn:substringBefore(imageSize.width, '.')}" />
		<c:set var="marginTop" value="${(110 - imageHeight)/2}" />
		<c:if test="${(loopStatus.count - 1) % imagesPerRow == 0}">
			<%="<tr>"%>
		</c:if>
		<td class="thumbnail">
		<div class="thumbnail"><a href="${showImagePageURL}"><img
			src="${imageURL}" class="thumbnail" width="${imageWidth}"
			height="${imageHeight}" style="margin-top: ${marginTop}px"
			title="${fn:escapeXml(image.title)}" /></a></div>
		</td>
		<c:if
			test="${loopStatus.count % imagesPerRow == 0 || loopStatus.last}">
			<%="</tr>"%>
		</c:if>
	</c:forEach>
</table>
