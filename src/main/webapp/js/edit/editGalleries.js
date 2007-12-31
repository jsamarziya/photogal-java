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

var GALLERY_INFO_DIV_ID_PREFIX = "galleryInfo:";

function getThumbnailGroupDiv() {
    return document.getElementById("thumbnailGroup");
}

function getGalleryInfoDiv(galleryId) {
    return document.getElementById(GALLERY_INFO_DIV_ID_PREFIX + galleryId);
}

function addGallery(galleryId, title, imageLocation, isPublic) {
    var className = isPublic? "gallery" : "galleryPrivate";
    var thumbnail = createThumbnail(getThumbnailGroupDiv(), className, galleryId, title, imageLocation);
    var editButton = addEditButton(thumbnail);
    editButton.onmouseover = function() {showGalleryInfo(galleryId, true)};
    editButton.onmouseout = function() {showGalleryInfo(galleryId, false)};
    addDeleteButton(thumbnail);
    addMoveButton(thumbnail);
    return thumbnail;
}

function editButtonClicked() {
    var galleryId = this.thumbnailId;
	top.location = baseURL + "/edit/organizeGallery.do?galleryId=" + galleryId + "&returnTo=" + encodeURIComponent("/edit/editGalleries.do") + "&returnToName=" + encodeURIComponent("gallery list");
}

function deleteButtonClicked() {
    var galleryId = this.thumbnailId;
	top.location = baseURL + "/edit/deleteGallery.do?galleryId=" + galleryId;
}

function ddObjectMoved(oldIndex, newIndex) {
    var url = baseURL + "/edit/xml/moveGallery.do";
    var query = new Object();
    query.from = oldIndex;
    query.to = newIndex;
    sendXMLHttpRequest("moveGallery", url, query, handleMoveGalleryResponse);
}

function handleMoveGalleryResponse(req, transactionName, url) {
    handleTransactionResponse(req, transactionName, url);
}

function showGalleryInfo(galleryId, visible) {
    var infoDiv = getGalleryInfoDiv(galleryId);
    if (visible) {
        var thumbnail = getThumbnailDiv(galleryId);
        var thumbnailLocation = getPosition(thumbnail);
        infoDiv.style.left = (thumbnailLocation.right - 50) + "px";
        infoDiv.style.top = (thumbnailLocation.bottom - 50) + "px";
        if (infoDiv.style.visibility != "visible") {
            changeOpac(0, infoDiv.id);
            infoDiv.style.visibility = "visible";
        }
        currentOpac(infoDiv.id, 90, 100);
    } else {
        infoDiv.style.visibility = "hidden";
    }
}

