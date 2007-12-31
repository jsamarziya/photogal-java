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

function ZoomPopup() {
    var popup = this;
    this.enabled = new Object();
    this.buttons = new Object();
    
    this.div = document.createElement("div");
    this.div.className = "zoomPopup";
    this.div.style.visibility = "hidden";
    this.div.onmouseout = function(ev) { popup.mouseout(ev) };
    var i = 0;
    for (var s in ZoomPopup.SIZES) {
        if (i > 0) {
            var divider = document.createElement("span");
            divider.className = "divider";
            this.div.appendChild(divider);
        }
        var b = document.createElement("span");
        b.imageSize = s;
        b.onmouseover = function() { if (popup.isSizeEnabled(this.imageSize) && popup.getCurrentSize() != this.imageSize) {this.className = "zoomButtonHover"} };
        b.onmouseout = function() { if (popup.isSizeEnabled(this.imageSize) && popup.getCurrentSize() != this.imageSize) {this.className = "zoomButtonEnabled"} };
        b.onclick = function() { if (popup.isSizeEnabled(this.imageSize)) {popup.setCurrentSize(this.imageSize); popup.hide()} };
        b.innerHTML = ZoomPopup.SIZES[s];
        this.buttons[s] = b;
        this.div.appendChild(b);
        i++;
    }
    for (var i in ZoomPopup.SIZES) {
        this.setSizeEnabled(i, i == 'o');
    }
    document.body.appendChild(this.div);
}

ZoomPopup.SIZES = { s: "Small", m: "Medium", l: "Large", o: "Original" };

ZoomPopup.prototype.getImageURL = function() {
    return this.imageURL;
}

ZoomPopup.prototype.setImageURL = function(url) {
    this.imageURL = url;
}

ZoomPopup.prototype.getCurrentSize = function() {
    return this.currentImageSize;
}

ZoomPopup.prototype.setCurrentSize = function (size) {
    this.checkSize(size);
    var oldButton = this.buttons[this.currentImageSize];
    if (oldButton != null) {
        oldButton.className = this.isSizeEnabled(this.currentImageSize)? "zoomButtonEnabled" : "zoomButtonDisabled";
    }
    this.currentImageSize = size;
    this.buttons[this.currentImageSize].className = "zoomButtonSelected";
    this.setImageSrc(size);
}

ZoomPopup.prototype.isSizeEnabled = function(size) {
    return this.enabled[size] == true;
}

ZoomPopup.prototype.setSizeEnabled = function(size, enabled) {
    if (! this.isValidSize(size)) {
        return;
    }
    this.enabled[size] = enabled;
    this.buttons[size].className = enabled? "zoomButtonEnabled" : "zoomButtonDisabled";
}

ZoomPopup.prototype.setImage = function(image) {
    this.image = image;
    var popup = this;
    image.onmouseover = function() { popup.show() };
    image.onmouseout = function(ev) { popup.mouseout(ev) };
}

ZoomPopup.prototype.show = function() {
    var imageLocation = getPosition(this.image);
    this.div.style.left = (imageLocation.left + 4) + "px";
    this.div.style.top = (imageLocation.top + 4) + "px";
    this.div.style.visibility = "visible";
}

ZoomPopup.prototype.hide = function() {
    this.div.style.visibility = "hidden";
}

ZoomPopup.prototype.mouseout = function(ev) {
    ev = ev || window.event;
    var mousePos = mouseXY(ev);
    if (!(hitTest(mousePos.x, mousePos.y, this.div) || hitTest(mousePos.x, mousePos.y, this.image))) {
        this.hide();
    }
}

// end of public interface  ////////////////////////////////////////////////////

ZoomPopup.prototype.isValidSize = function (size) {
    for (var i in ZoomPopup.SIZES) {
        if (size == i) {
            return true;
        }
    }
    return false;
}

ZoomPopup.prototype.checkSize = function (size) {
    if (! this.isValidSize(size)) {
        throw new Error("Illegal size '" + size + "' specified");
    }
}

ZoomPopup.prototype.setImageSrc = function(size) {
    var url = this.getImageURL();
    if (url.indexOf("?") == -1) {
        url += "?";
    } else {
        url += "&";
    }
    url += "size=" + encodeURIComponent(size);
    this.image.src = url;
}