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

var XMLHttpRequestStates = ["uninitialized", "loading", "loaded", "interactive", "complete"];

function createXMLHttpRequest() {
    var req;
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        try {
            req = new ActiveXObject("Msxml2.XMLHTTP");
        } catch(e) {
            try {
                req = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
                throw new Error("XMLHttpRequest not supported");
            }
        }
    }
    return req;
}

/*
 * Creates and sends an XMLHttpRequest.
 * 
 * @param transactionName the short human-readable name of the request
 * @param url the url to send the request to
 * @param a map of query parameters, may be null
 * @param responseCallback the function to call when the transaction is complete,
 *        may be null
 */
function sendXMLHttpRequest(transactionName, url, query, responseCallback) {
    var queryString = createQueryString(query);
    var req = createXMLHttpRequest();
    req.onreadystatechange = function() {handleRequestStateChange(req, transactionName, url, responseCallback)};
    req.open("POST", url, true);
    req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    req.send(queryString);
}

function createQueryString(query) {
    var queryString = "";
    for (var paramName in query) {
        queryString += "&" + paramName + "=" + encodeURIComponent(query[paramName]);
    }
    queryString = queryString.slice(1);
    return queryString;
}

function handleRequestStateChange(req, transactionName, url, callback) {
    var div = document.getElementById("transactionStatusDiv");
    var messageElement = document.getElementById("transactionStatusMessage");
    if (req.readyState != 4) {
        if (div != null && messageElement != null) {
          var message = "Transaction " + transactionName + ": " + XMLHttpRequestStates[req.readyState];
          messageElement.innerHTML = message;
          div.style.visibility = "visible";
        }
        return;
    }
    if (div != null && messageElement != null) {
        div.style.visibility = "hidden";
        messageElement.innerHTML = "";
    }
    if (callback != null) {
        callback(req, transactionName, url);
    }
}

/*
 * Standard XMLHttpRequest transaction response handler.
 */
function handleTransactionResponse(req, transactionName, url) {
    if (req.status != 200) {
        throw new Error("Unable to complete transaction " + url + ": " + req.status + " " + req.statusText); 
    }
    var response = req.responseXML.documentElement;
    var status = response.getElementsByTagName("status")[0].firstChild.data;
    if (status == "ERROR") {
        var message = response.getElementsByTagName("message")[0].firstChild.data;
        throw new Error("Unable to complete transaction " + url + ": server returned error \"" + message + "\"");
    }
    // alert("transaction completed successfully");
}
