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

// Adapted from code located at http://www.felgall.com/jstip45.htm

function initializeTextareaSize(textarea) {
    textarea.minRows = textarea.rows;
    setTextareaSizeToFit(textarea);
}

function setTextareaSizeToFit(textarea) {
    var a = textarea.value.split('\n');
    var b = 1;
    for (var x = 0; x < a.length; x++) {
        if (a[x].length >= textarea.cols) {
            b += Math.floor(a[x].length / textarea.cols);
        }
    }
    b += a.length;
    textarea.rows = Math.max(textarea.minRows, b - 1);
}