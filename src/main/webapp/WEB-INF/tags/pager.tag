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
<%@ tag body-content="empty" description="Writes a pager control"%>
<%@ attribute name="currentPage" required="true"%>
<%@ attribute name="itemCount" required="true"%>
<%@ attribute name="itemsPerPage" required="true"%>
<%@ attribute name="pageURL" required="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<c:set var="pageCount" value="${pg:pageCount(itemCount, itemsPerPage)}" />
<c:if test="${pageCount > 1}">
	<div class="pager"><c:if test="${currentPage > 1}">
		<c:url var="prevURL" value="${pageURL}" context="/">
			<c:param name="currentPage" value="${currentPage - 1}" />
			<c:param name="itemsPerPage" value="${itemsPerPage}" />
		</c:url>
		<a href="${prevURL}"><fmt:message key="pager.previous" /></a>&nbsp;&nbsp;
	</c:if> <fmt:message key="pager.index">
		<fmt:param value="${currentPage}" />
		<fmt:param value="${pageCount}" />
	</fmt:message> <c:if test="${currentPage < pageCount}">
		<c:url var="nextURL" value="${pageURL}" context="/">
			<c:param name="currentPage" value="${currentPage + 1}" />
			<c:param name="itemsPerPage" value="${itemsPerPage}" />
		</c:url>
		&nbsp;&nbsp;<a href="${nextURL}"><fmt:message key="pager.next" /></a>
	</c:if></div>
</c:if>
