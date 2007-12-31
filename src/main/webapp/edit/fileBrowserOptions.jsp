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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><fmt:message key="head.title">
	<fmt:param value="File Browser Options" />
</fmt:message></title>
<link href="<c:url value="/styles.css"/>" rel="stylesheet"
	type="text/css" />
<script src="<c:url value="/js/edit/imageFileBrowser.js"/>"
	type="text/javascript"></script>
<script src="<c:url value="/js/global.js"/>" type="text/javascript"></script>
<script type="text/javascript">
function getRadioIndex(value) {
    if (value == "show") {
        return 0;
    } else if (value == "obscure") {
        return 1;
    } else if (value == "hide") {
        return 2;
    } else {
        throw new Error("illegal radio value " + value);
    }
}
function saveOptions() {
    var galleryImagesDisplayStyle = getCheckedRadioValue(document.theForm.galleryImages);
    var foreignImagesDisplayStyle = getCheckedRadioValue(document.theForm.foreignImages)
    saveFileBrowserOptions(galleryImagesDisplayStyle, foreignImagesDisplayStyle);
    window.opener.updateCellDisplays();
}
</script>
</head>
<body>
<div class="heading">File Browser Options</div>
<form name="theForm">
<div style="background: #eeeeee"><input type="radio"
	name="galleryImages" value="show" />Show <input type="radio"
	name="galleryImages" value="obscure" />Obscure <input type="radio"
	name="galleryImages" value="hide" />Hide gallery images</div>
<div style="background: #f8f8f8"><input type="radio"
	name="foreignImages" value="show" />Show <input type="radio"
	name="foreignImages" value="obscure" />Obscure <input type="radio"
	name="foreignImages" value="hide" />Hide images contained in other
galleries</div>
<div style="margin-top: 20px;">
<button type="button" onclick="saveOptions();window.close()">&nbsp;Save&nbsp;</button>
<button type="button" onclick="window.close()" style="margin-left: 25px">Cancel</button>
</div>
</form>
<script type="text/javascript">
document.theForm.galleryImages[getRadioIndex(getGalleryImagesDisplayStyle())].checked = true;
document.theForm.foreignImages[getRadioIndex(getForeignImagesDisplayStyle())].checked = true;
</script>
</body>
</html>
