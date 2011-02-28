//  Copyright 2007 The Photogal Team.
//  
//  This file is part of photogal.
//
//  photogal is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  photogal is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with photogal.  If not, see <http://www.gnu.org/licenses/>.

// Keeps track of which images are in which galleries
var imageGalleries = new Object();

/*
 * Called to notify the image file browser that an image has been added to a 
 * gallery.
 */
function imageAdded(location, galleryId) {
    var galleryList = getGalleryList(location);
    galleryList[galleryId] = true;
    updateCellDisplay(location);
}

/*
 * Called to notify the image file browser that an image has been removed from
 * a gallery.
 */
function imageRemoved(location, galleryId) {
    var galleryList = getGalleryList(location);
    delete galleryList[galleryId];
    updateCellDisplay(location);
}

function showOptions() {
    window.open(baseURL + "/edit/fileBrowserOptions.jsp", "fileBrowserOptions", "menubar=no,location=no,resizable=yes,scrollbars=yes,status=yes,toolbar=no,height=150,width=500");
}

function saveFileBrowserOptions(galleryImagesDisplayStyle, foreignImagesDisplayStyle) {
    var date = new Date();
    date.setFullYear(date.getFullYear() + 100);
    var cookie = "fileBrowserOptions=";
    cookie += "galleryImages:" + galleryImagesDisplayStyle;
    cookie += "&foreignImages:" + foreignImagesDisplayStyle;
    cookie += "; expires=" + date.toGMTString();
    document.cookie = cookie;
}
 
function getFileBrowserOptions() {
    var cookies = document.cookie;   
    var i = cookies.indexOf("fileBrowserOptions=");
    var options = null;
    if (i != -1) {
        var start = i + 19;
        var end = cookies.indexOf(";", start);
        if (end == -1) {
            end = cookies.length;
        }
        options = cookies.substring(start, end);
    }
    return options;
}

function getOption(name) {
    var option = null;
    var options = getFileBrowserOptions();
    if (options != null) {
        var i = options.indexOf(name + ":");
        if (i != -1) {
            var start = i + name.length + 1;
            var end = options.indexOf("&", start);
            if (end == -1) {
                end = options.length;
            }
            option = options.substring(start, end);
        }
    }
    return option;
}

function getGalleryImagesDisplayStyle() {
    var option = getOption("galleryImages");
    if (option == null) {
        option = "hide";
    }
    return option;
}

function getForeignImagesDisplayStyle() {   
    var option = getOption("foreignImages");
    if (option == null) {
        option = "obscure";
    }
    return option;
}

function updateCellDisplay(location) {
    if (! inCurrentDirectory(location)) {
        return;
    }
    var style;
    if (inCurrentGallery(location)) {
        style = getGalleryImagesDisplayStyle();
    } else if (inGallery(location)) {
        style = getForeignImagesDisplayStyle();
    } else {
        style = "show";
    }
    if (style == "hide") {
        hideCell(location);
    } else if (style == "obscure") {
        obscureCell(location);
    } else {
        showCell(location);
    }
}

function updateCellDisplays() {
    for (var location in imageGalleries) {
        updateCellDisplay(location);
    }
}

function hideCell(location) {
	var thumbnailCell = getThumbnailCell(location);
    thumbnailCell.style.display = "none";
}

function showCell(location) {
	var image = getThumbnailImage(location);
    image.style.visibility = "hidden";
	var thumbnailCell = getThumbnailCell(location);
    thumbnailCell.style.display = "";
    verticallyCenterElement(image);
    image.style.visibility = "visible";
    setCellOpacity(location, 100);
}

function obscureCell(location) {
    showCell(location);
    setCellOpacity(location, 25);
}

function setCellOpacity(location, opacity) {
    changeOpac(opacity, getThumbnailImageId(location));
}

function getThumbnailCell(imageLocation) {
	return document.getElementById(getThumbnailCellId(imageLocation));
}

function getThumbnailCellId(imageLocation) {
	return escapeId("tn:" + imageLocation);
}

function getThumbnailImage(imageLocation) {
    return document.getElementById(getThumbnailImageId(imageLocation));
}

function getThumbnailImageId(imageLocation) {
    return escapeId("img:" + imageLocation);
}

function getGalleryList(location) {
    if (imageGalleries[location] == null) {
        imageGalleries[location] = new Object();
    }
    return imageGalleries[location];
}

function inCurrentGallery(location) {
    var galleryList = getGalleryList(location);
    return galleryList[galleryId] != null;
}

function inGallery(location) {
    var galleryList = getGalleryList(location);
    for (var name in galleryList) {
        if (galleryList[name] != null) {
            return true;
        }
    }
    return false;
}

function inCurrentDirectory(location) {
    return getThumbnailCell(location) != null;
}
