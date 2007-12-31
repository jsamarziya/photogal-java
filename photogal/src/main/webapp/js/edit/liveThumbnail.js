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

/*
 * liveThumbnail.js
 *
 * Provides support for creating thumbnails with editing buttons that appear
 * when hovered on.
 */

// The prefix used to form thumbnail div ids.
var THUMBNAIL_DIV_ID_PREFIX = "thumbnailDiv";

// The prefix used to form thumbnail image ids.
var THUMBNAIL_IMG_ID_PREFIX = "thumbnailImg";

// The background color for thumbnails when they are selected as a drop target.
var thumbnailBackgroundSelected = "#ddddff";

// The background color for thumbnails when they are not selected as a drop target.
var thumbnailBackground = "#ffffff";

// The thumbnail image to use if the image location is unspecified.
var emptyImageLocation = "/images/no_photos.png";

/*
 * Returns the thumbnail div for the specified id.
 */
function getThumbnailDiv(thumbnailId) {
    return document.getElementById(THUMBNAIL_DIV_ID_PREFIX + thumbnailId);
}

/*
 * Returns the thumbnail image for the specified id.
 */
function getThumbnailImage(thumbnailId) {
    return document.getElementById(THUMBNAIL_IMG_ID_PREFIX + thumbnailId);
}

/*
 * Creates a thumbnail.
 * 
 * @param parent the parent element
 * @param thumbnailId the id of the thumbnail
 * @param thumbnailTitle the title of the thumbnail image
 * @param imageLocation the location of the thumbnail image
 */
function createThumbnail(parent, className, thumbnailId, thumbnailTitle, imageLocation) {
    var div = document.createElement("div");
    div.id = THUMBNAIL_DIV_ID_PREFIX + thumbnailId;
    div.className = className;
    div.thumbnailId = thumbnailId;
    div.imageLocation = imageLocation;
    div.buttons = new Object();
    
    var img = document.createElement("img");
    img.id = THUMBNAIL_IMG_ID_PREFIX + thumbnailId;
    img.className = "thumbnail";
    img.style.visibility = "hidden";
    img.onload = function() { verticallyCenterImageOnload(this) };
    if (imageLocation == null) {
        img.src = baseURL + emptyImageLocation;
    } else {
        img.src = baseURL + "/edit/showImageFile.do?file=" + encodeURIComponent(imageLocation) + "&size=t";
    }
    img.title = thumbnailTitle;
    div.appendChild(img);

	div.onmouseover = function() { showObjects(div.buttons) };
	div.onmouseout = function() { hideObjects(div.buttons) };
    parent.appendChild(div);
    ADD_DHTML(div.id + NO_DRAG + RESET_Z);
	addDDObject(div.id);
	return div;
}

/*
 * Adds a button to a thumbnail.
 * 
 * @param thumbnail the thumbnail
 * @param buttonName the name of the button (used as a key for thumbnail.buttons)
 * @param className the CSS class of the button
 * @param imgSrc the URL for the button image
 * @param clickHandler the function called when the button is clicked
 */
function addButton(thumbnail, buttonName, className, imgSrc, clickHandler) {
    var button = document.createElement("img");
    button.className = className;
    button.src = imgSrc;
    button.style.visibility = "hidden";
    button.thumbnailId = thumbnail.thumbnailId;
    button.onclick = clickHandler;
    thumbnail.appendChild(button);
    thumbnail.buttons[buttonName] = button;
    return button;
}

/*
 * Convenience method for adding a delete button.
 */
function addDeleteButton(thumbnail) {
    var buttonName = "delete";
    var className = "deleteThumbnailButton";
    var imgSrc = baseURL + "/images/close.gif";
    var handler = deleteButtonClicked;
    return addButton(thumbnail, buttonName, className, imgSrc, handler);
}

/*
 * Convenience method for adding an edit button.
 */
function addEditButton(thumbnail) {
    var buttonName = "edit";
    var className = "editThumbnailButton";
    var imgSrc = baseURL + "/images/edit.png";
    var handler = editButtonClicked;
    return addButton(thumbnail, buttonName, className, imgSrc, handler);
}

/*
 * Convenience method for adding a move button.
 */
function addMoveButton(thumbnail) {
    var buttonName = "move";
    var className = "moveThumbnailButton";
    var imgSrc = baseURL + "/images/move-blue.png";
    var button = addButton(thumbnail, buttonName, className, imgSrc, null);
    button.onmousedown = function() { dd.elements[thumbnail.id].setDraggable(true) };
    return button;
}

/*
 * Convenience method for adding an info button.
 */
function addInfoButton(thumbnail) {
    var buttonName = "info";
    var className = "infoThumbnailButton";
    var imgSrc = baseURL + "/images/info.png";
    var handler = infoButtonClicked;
    return addButton(thumbnail, buttonName, className, imgSrc, handler);
}


function targetSelected(target, selected) {
    target.setBgColor(selected ? thumbnailBackgroundSelected : thumbnailBackground);
}

/*
 * A hook called when an edit button is clicked.  When called, "this" references
 * the thumbnail div containing the button that was clicked.
 */
function editButtonClicked() {
}

/*
 * A hook called when a delete button is clicked.  When called, "this" references
 * the thumbnail div containing the button that was clicked.
 */
function deleteButtonClicked() {
}

/*
 * A hook called when an info button is clicked.  When called, "this" references
 * the thumbnail div containing the button that was clicked.
 */
function infoButtonClicked() {
}
