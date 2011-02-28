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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script type="text/javascript">
function updateGallerySelector(editAction) {
  var id = ${image.id};
  var title = '${pg:escapeJavaScript(image.title)}';
  var location = '${pg:escapeJavaScript(image.location)}';
  var selectorWindow = top.gallerySelector;
  var fileBrowserWindow = top.fileSelector;
  if (editAction == 'add') {
    selectorWindow.addImage(id, title, location);
    fileBrowserWindow.imageAdded(location, ${galleryId});
  } else if (editAction == 'edit') {
    selectorWindow.updateImage(id, title);
  } else {
    throw new Error("unknown edit action " + editAction);
  }
}
</script>
</head>
<body onload="updateGallerySelector('${editAction}');">
</body>
</html>
