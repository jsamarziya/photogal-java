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
<%@ tag body-content="empty" description="Writes a year/month index"%>
<%@ attribute name="dateCountMap" required="true" type="java.util.Map"%>
<%@ attribute name="dateType" required="true"%>
<%@ attribute name="dateFormatPattern" required="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<table border="0" cellpadding="0" cellspacing="0"
	style="margin-left: 10px;">
	<c:forEach var="yl" items="${dateCountMap}">
		<tr>
			<c:set var="year" value="${yl.key}" />
			<c:url var="showImagesByYearURL" value="/showImagesByDate.do">
				<c:param name="dateType" value="${dateType}" />
				<c:param name="allMonths" value="true" />
				<c:param name="year" value="${year}" />
			</c:url>
			<td valign="top" style="padding-top: 10px;"><a
				href="${showImagesByYearURL}"><c:out value="${year}" /></a></td>
			<td valign="top" style="padding-top: 10px; padding-left:25px;"><c:forEach
				var="mc" items="${yl.value}">
				<c:set var="date" value="${mc.key}" />
				<c:set var="imageCount" value="${mc.value}" />
				<c:url var="showImagesByDateURL" value="/showImagesByDate.do">
					<c:param name="dateType" value="${dateType}" />
					<c:param name="year" value="${date.year}" />
					<c:param name="month" value="${date.month}" />
				</c:url>
				<a href="${showImagesByDateURL}"><pg:formatCalendarDate
					value="${date}" pattern="${dateFormatPattern}" /></a> (<c:out
					value="${imageCount}" />)<br />
			</c:forEach></td>
		</tr>
	</c:forEach>
</table>
