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

function getThumbnailGroupDiv() {
    return document.getElementById("thumbnailGroup");
}

/*
 * Adds a thumbnail to the page.
 */
function addImage(imageId, imageTitle, imageLocation) {
    var title = imageTitle;
    if (title.length == 0) {
      title = imageLocation;
    } else {
      title += " (" + imageLocation + ")";
    }
    var thumbnail = createThumbnail(getThumbnailGroupDiv(), "thumbnailFloat", imageId, title, imageLocation);
    addEditButton(thumbnail);
    addDeleteButton(thumbnail);
    addMoveButton(thumbnail);
    return thumbnail;
}

/*
 * Updates the thumbnail for the specified image.
 */
function updateImage(id, title) {
    var image = getThumbnailImage(id);
    if (image == null) {
        throw new Error("unable to find image " + id);
    }
    image.title = title;
}

/*
 * Performs actions in response to a user request to edit an image.
 */
function editButtonClicked() {
    var imageId = this.thumbnailId;
	top.galleryEditor.location = baseURL + "/edit/editImage.do?galleryId=" + galleryId + "&imageId=" + imageId + "&returnTo=" + encodeURIComponent("/edit/editImageDone") + "&cancelTo=" + encodeURIComponent("redirect:/blank.html");
}

/*
 * Performs actions in response to a user request to remove an image from the 
 * gallery.
 */
function deleteButtonClicked() {
    var imageId = this.thumbnailId;
	top.galleryEditor.location = baseURL + "/edit/deleteImage.do?galleryId=" + galleryId + "&imageId=" + imageId;
}

/*
 * Removes the specified thumbnail from the page.
 */
function removeImage(imageId) {
    var thumbnailDiv = getThumbnailDiv(imageId);
    if (thumbnailDiv == null) {
        throw new Error("unable to find image " + imageId);
    }
    removeDDObject(thumbnailDiv.id);
    dd.elements[thumbnailDiv.id].del();
    getThumbnailGroupDiv().removeChild(thumbnailDiv);
    dd.recalc();
}

function ddObjectMoved(oldIndex, newIndex) {
    var url = baseURL + "/edit/xml/moveImage.do";
    var query = new Object();
    query.gallery = galleryId;
    query.from = oldIndex;
    query.to = newIndex;
    sendXMLHttpRequest("moveImage", url, query, handleMoveImageResponse);
}

function handleMoveImageResponse(req, transactionName, url) {
    handleTransactionResponse(req, transactionName, url);
}