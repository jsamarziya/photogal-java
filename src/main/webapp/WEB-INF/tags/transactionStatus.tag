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
<%@ tag body-content="empty"
	description="Writes a transaction status div"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div id="transactionStatusDiv" class="transactionStatus">
<table border="0" cellpaddng="0" cellspacing="0">
	<tr>
		<td valign="middle"><img
			src="<c:url value="/images/throbber.gif"/>" width="32" height="32" /></td>
		<td valign="middle"><span id="transactionStatusMessage"
			style="margin-left: 5px"></span></td>
	</tr>
</table>
</div>
