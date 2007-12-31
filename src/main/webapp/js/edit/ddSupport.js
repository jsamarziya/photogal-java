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
 * ddList.js
 *
 * Adds reorderable list support to wz_dragdrop.
 */

// used to record the starting location of an object that is being dragged
var dragObjectStartLocation;

// used to record the current drop target, if any
var dropTarget;

// the drag&drop object ids, in order of display
var ddObjectIds = new Array();

/*
 * Adds a drag & drop object to the list.
 * 
 * @param objectId the id of the object to add
 */
function addDDObject(objectId) {
	ddObjectIds.push(objectId);
}

/*
 * Removes a drag & drop object from the list.
 *
 * @param objectId the id of the object to remove
 */
function removeDDObject(objectId) {
    var index = getDDObjectIndex(objectId);
    ddObjectIds.splice(index, 1);
}

/*
 * Returns the index of the specified list object.
 *
 * @param objectId the id of the object
 * @throws Error if objectId is not in the list
 */
function getDDObjectIndex(objectId) {
    for (var i = 0; i < ddObjectIds.length; i++) {
        if (ddObjectIds[i] == objectId) {
            return i;
        }
    }
    throw new Error(objectId + " not found in ddObjectIds");
}

/*
 * Returns the drag & drop element located at the specified location, if any.
 */
function getDropTarget(x, y) {
    for (var i = 0; i < dd.elements.length; i++) {
        var ddElement = dd.elements[i];
        if (x > ddElement.x && x < ddElement.x + ddElement.w
            && y > ddElement.y && y < ddElement.y + ddElement.h) {
            return ddElement;
        }
    }
    return null;
}

function moveDDObject(sourceIndex, targetIndex) {
    if (sourceIndex == targetIndex) {
        return;
    }
    var targetLocation = {x: dd.elements[ddObjectIds[targetIndex]].x, y: dd.elements[ddObjectIds[targetIndex]].y};
    if (sourceIndex < targetIndex) {
        for (var i = targetIndex; i > sourceIndex; i--) {
            dd.elements[ddObjectIds[i]].moveTo(dd.elements[ddObjectIds[i-1]].x, dd.elements[ddObjectIds[i-1]].y);
        }
    } else {
        for (var i = targetIndex; i < sourceIndex; i++) {
            dd.elements[ddObjectIds[i]].moveTo(dd.elements[ddObjectIds[i+1]].x, dd.elements[ddObjectIds[i+1]].y);
        }
    }
    dd.elements[ddObjectIds[sourceIndex]].moveTo(targetLocation.x, targetLocation.y);
    ddObjectIds.splice(targetIndex, 0, ddObjectIds.splice(sourceIndex, 1)[0]);
    ddObjectMoved(sourceIndex, targetIndex);
}


function my_PickFunc() {
    dragObjectStartLocation = {x: dd.obj.x, y: dd.obj.y};
    dragStarted();
}

function my_DragFunc() {
    var target = getDropTarget(dd.obj.x, dd.obj.y);
    if (target != dropTarget) {
        if (dropTarget != null) {
            targetSelected(dropTarget, false);
        }
        if (target != null) {
            targetSelected(target, true);
        }
        dropTarget = target;
    }
}

function my_DropFunc() {
    dd.obj.moveTo(dragObjectStartLocation.x, dragObjectStartLocation.y);
    if (dropTarget != null) {
        targetSelected(dropTarget, false);
        moveDDObject(getDDObjectIndex(dd.obj.id), getDDObjectIndex(dropTarget.id));
    }
    dd.obj.setDraggable(false);
    dragObjectStartLocation = null;
    dropTarget = null;
}

/*
 * A hook called when an item starts to be dragged.
 *
 * @param target the target
 */
function dragStarted() {
}

/*
 * A hook called when a drop target is selected or deselected.
 *
 * @param target the target
 * @param selected true if target has been selected, false if target has been
 *        deselected
 */
function targetSelected(target, selected) {
}

/*
 * A hook called when a drag & drop object is moved.
 *
 * @param oldIndex the old index of the object that was moved
 * @param newIndex the new index of the object that was moved
 */
function ddObjectMoved(oldIndex, newIndex) {
}
