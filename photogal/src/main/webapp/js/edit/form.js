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

// Adapted from code located at http://www.eggheadcafe.com/articles/20010406.asp

function isDirty(form) {
    for (var i = 0; i < form.elements.length; i++) {
        var formElement = form.elements[i];
        if ("text" == formElement.type || "TEXTAREA" == formElement.tagName) {
            if (formElement.value != formElement.defaultValue) {
                return true;
            }
        } else if ("checkbox" == formElement.type || "radio" == formElement.type) {
            if (formElement.checked != formElement.defaultChecked) {
                return true;
            }
        } else if ("SELECT" == formElement.tagName) {
            var options = formElement.options;
            for (var j = 0; j < options.length; j++) {
                var option = options[j];
                if (option.selected != option.defaultSelected) {
                    return true;
                }
            }
        }
    }
    return false;
}

function checkUnsavedChanges(event, form) {
    if (! form.submitted && isDirty(form)) {
        event.returnValue = "You have modified the information on this page.";
    }
}

