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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="login" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<pg:favicon />
</head>
<body>
<form method="post" action="j_security_check">
<div id="loginDialog"><img src="<c:url value="/images/pg.png"/>" />
<div style="padding-top: 20px; padding-bottom:20px;">Please log in
to access the editing area.</div>
<table border="0" cellpadding="4" cellspacing="0">
	<tr>
		<td>Username</td>
		<td><input type="text" name="j_username" /></td>
	</tr>
	<tr>
		<td>Password</td>
		<td><input type="password" name="j_password" /></td>
	</tr>
	<tr>
		<td></td>
		<td><input type="submit" value="Submit" /> <input type="button"
			onclick="window.location='<c:url value="/showGalleries.do"/>'"
			value="Cancel" /></td>
	</tr>
</table>
</div>
</form>
</body>
</html>
