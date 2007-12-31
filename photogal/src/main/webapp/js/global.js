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

function getCheckedRadioValue(radioGroup) {
    for (var i = 0; i < radioGroup.length; i++) {
        if (radioGroup[i].checked) {
            return radioGroup[i].value;
        }
    }
    return null;
}

/*
 * Returns the location of an element on the page.
 */
function getPosition(obj) {
    var p = new Object();
    p.top = 0;
    p.left = 0;
    p.right = obj.offsetWidth;
    p.bottom = obj.offsetHeight;
    while (obj != null) {
        p.left += obj.offsetLeft;
        p.top += obj.offsetTop;
        obj = obj.offsetParent;
    }
    p.right += p.left;
    p.bottom += p.top;
    return p;
}

/*
 * Return the document-relative coordinates of the mouse for a given event.
 */
function mouseXY(ev) {
    if(ev.pageX || ev.pageY){
        return {x:ev.pageX, y:ev.pageY};
    }
    return {
        x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
        y:ev.clientY + document.body.scrollTop  - document.body.clientTop
    };
}

function hitTest(x, y, obj) {
    var location = getPosition(obj);
    return x > location.left && x < location.right && y > location.top && y < location.bottom;
}

/*
 * An onload handler function used for vertically centering images.  This function
 * sets a half-second timeout, after which the image is centered and made visible.
 */
function verticallyCenterImageOnload(image) {
    window.setTimeout("var img = document.getElementById('" + image.id + "'); verticallyCenterElement(img); img.style.visibility = 'visible';", 500);
}

function verticallyCenterElement(element) {
    var parentHeight = element.parentNode.clientHeight;
    element.style.marginTop = ((parentHeight - element.offsetHeight) / 2) + 'px';
}

/*
 * Makes a set of objects visible.
 * 
 * @param objects an object, the properties of which are made visible
 */
function showObjects(objects) {
    for (var objectName in objects) {
        objects[objectName].style.visibility = "visible";
    }
}

/*
 * Hides a set of objects.
 * 
 * @param objects an object, the properties of which are made invisible
 */
function hideObjects(objects) {
    for (var objectName in objects) {
        objects[objectName].style.visibility = "hidden";
    }
}

/*
 * Returns an array containing all descendent nodes of the given node.
 */
function allNodes(node) {
    var retval = new Array();
    for (var i in node.childNodes) {
        var child = node.childNodes[i];
        retval.push(child);
        retval = retval.concat(allNodes(child));
    }
    return retval;
}
