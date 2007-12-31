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
<%@ tag body-content="empty" description="Writes the image navigator"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td width="33%" align="left" nowrap="nowrap" valign="middle"><c:choose>
			<c:when test="${previousImage == null}">
				<span class="imageNavigatorDisabled"><fmt:message
					key="imageNavigator.previous" /></span>
			</c:when>
			<c:otherwise>
				<c:url var="showPreviousImageURL" value="/showImagePage.do">
					<c:param name="imageId" value="${previousImage.id}" />
					<c:param name="galleryId" value="${gallery.id}" />
				</c:url>
				<a href="${showPreviousImageURL}" class="imageNavigator"><fmt:message
					key="imageNavigator.previous" /></a>
			</c:otherwise>
		</c:choose></td>
		<td width="33%" align="center" nowrap="nowrap" valign="middle"><span
			class="imagePosition">${imageIndex + 1} of
		${gallery.imageCount}</span></td>
		<td width="33%" align="right" nowrap="nowrap" valign="middle"><c:choose>
			<c:when test="${nextImage == null}">
				<span class="imageNavigatorDisabled"><fmt:message
					key="imageNavigator.next" /></span>
			</c:when>
			<c:otherwise>
				<c:url var="showNextImageURL" value="/showImagePage.do">
					<c:param name="imageId" value="${nextImage.id}" />
					<c:param name="galleryId" value="${gallery.id}" />
				</c:url>
				<a href="${showNextImageURL}" class="imageNavigator"><fmt:message
					key="imageNavigator.next" /></a>
			</c:otherwise>
		</c:choose></td>
	</tr>
</table>
