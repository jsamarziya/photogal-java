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
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<c:url var="thumbSmall" value="/edit/showImageFile.do">
	<c:param name="file" value="${image.location}" />
	<c:param name="size" value="s" />
</c:url>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<link href="<c:url value="/edit.css"/>" rel="stylesheet" type="text/css" />
</head>
<body>
<div id="bodyContent">
<div><img id="thumbnail" src="${thumbSmall}" /></div>
<div style="margin-top: 50px">Are you sure you wish to remove this
image from the gallery?</div>
<div style="margin-top: 25px">
<form name="theForm" method="post"
	action="<c:url value="/edit/deleteImage.do"/>"><input
	type="hidden" name="action" value="delete" /> <input type="hidden"
	name="galleryId" value="${galleryId}" /> <input type="hidden"
	name="imageId" value="${image.id}" />
<button type="submit">&nbsp;&nbsp;OK&nbsp;&nbsp;</button>
<button type="submit" onclick="document.theForm.action.value='cancel'"
	style="margin-left: 50px">Cancel</button>
</form>
</div>

<pg:imageInfoBox location="${image.location}" width="${image.width}"
	height="${image.height}" isPublic="${image.public}"
	galleries="${image.galleries}" /></div>
</body>
</html>
