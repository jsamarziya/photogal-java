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
<%@ tag body-content="empty" description="Writes the pg footer"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<br style="clear:both;" />
<div id="pgFooter">
<div id="appVersion" style="float:left;">photogal <c:out
	value="${pg:version()} ${pg:deploymentEnvironment()}" /></div>
<c:if test="${pg:canEdit(pageContext.request)}">
	<div style="float:right;"><a href="<c:url value="/edit/"/>">Edit</a></div>
</c:if></div>
