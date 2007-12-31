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

function getImage() {
    return document.getElementById("image");
}

function setImageSize(size) {
    var image = getImage();
    image.src = imageURL + "&size=" + size;
    updateSizeLinks(size);
}

function updateSizeLinks(selectedSize) {
    var sizes = ['s', 'm', 'l', 'o'];
    for (var i in sizes) {
        var size = sizes[i];
        var link = document.getElementById('size_' + size);
        if (link != null) {
            link.className = selectedSize == size? "imageSizeSelected" : "imageSize";
        }
    }
}
