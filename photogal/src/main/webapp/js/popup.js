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

function createPopup(content, includeCloseButton) {
    var popup = document.createElement("div");
    popup.className = "popup";
    var titlebar = document.createElement("div");
    titlebar.className = "popupTitle";
    popup.appendChild(titlebar);
    if (includeCloseButton) {
        var closeButton = document.createElement("img");
        closeButton.className = "popupClose";
        closeButton.src = baseURL + "/images/popupClose.png";
        closeButton.onmouseover = function() {closeButton.src = baseURL + "/images/popupCloseOver.png"};
        closeButton.onmouseout = function() {closeButton.src = baseURL + "/images/popupClose.png"};
        closeButton.onclick = function() {document.body.removeChild(popup)};
        titlebar.appendChild(closeButton);
    }
    var contentDiv = document.createElement("div");
    contentDiv.className = "popupContent";
    contentDiv.innerHTML = content;
    popup.appendChild(contentDiv);
    document.body.appendChild(popup);
    return popup;
}
