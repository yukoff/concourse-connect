/*
 * ConcourseConnect
 * Copyright 2009 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect, an open source social business
 * software and community platform.
 *
 * Concursive ConcourseConnect is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * Under the terms of the GNU Affero General Public License you must release the
 * complete source code for any application that uses any part of ConcourseConnect
 * (system header files and libraries used by the operating system are excluded).
 * These terms must be included in any work that has ConcourseConnect components.
 * If you are developing and distributing open source applications under the
 * GNU Affero General Public License, then you are free to use ConcourseConnect
 * under the GNU Affero General Public License. Ê
 *
 * If you are deploying a web site in which users interact with any portion of
 * ConcourseConnect over a network, the complete source code changes must be made
 * available.  For example, include a link to the source archive directly from
 * your web site.
 *
 * For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
 * products, and do not license and distribute their source code under the GNU
 * Affero General Public License, Concursive provides a flexible commercial
 * license.
 *
 * To anyone in doubt, we recommend the commercial license. Our commercial license
 * is competitively priced and will eliminate any confusion about how
 * ConcourseConnect can be used and distributed.
 *
 * ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
};

String.prototype.ltrim = function() {
	return this.replace(/^\s+/,"");
};

String.prototype.rtrim = function() {
	return this.replace(/\s+$/,"");
};

function myXMLHttpRequest() {
  var xmlhttplocal;
  if (window.XMLHttpRequest) {
    // If IE7, Mozilla, Safari, and so on: Use native object.
    xmlhttplocal = new XMLHttpRequest();
  } else {
    if (window.ActiveXObject) {
       // ...otherwise, use the ActiveX control for IE5.x and IE6.
       xmlhttplocal = new ActiveXObject('MSXML2.XMLHTTP.3.0');
    }
  }
  return xmlhttplocal;
}

var xmlhttp = myXMLHttpRequest();

function callURL(url) {
  url += ((url.indexOf('?') == -1)?'?':'&') + "rnd=" + new Date().valueOf().toString();
	xmlhttp.open('get', url);
  xmlhttp.onreadystatechange = handleResponse;
  xmlhttp.send(null);
}

function handleResponse() {
  if(xmlhttp.readyState == 4){
    if (xmlhttp.status == 200){
      var response = xmlhttp.responseText;
      var update = new Array();
      if (response.indexOf('|') != -1) {
        update = response.split('|');
        for (var i = 0; i < update.length-1; i=i+2) {
          changeText(update[i].trim(), update[i+1]);
        }
      }
		}
  }
}

function changeText(div2show,text) {
  // Detect Browser
  var IE = (document.all) ? 1 : 0;
  var DOM = 0;
  if (parseInt(navigator.appVersion) >=5) {DOM=1;};
  // Grab the content from the requested "div" and show it in the "container"
  if (DOM) {
      document.getElementById(div2show).innerHTML=text;
  } else if(IE) {
      document.all[div2show].innerHTML=text;
  }
}

/*Function is used when server response is passed through a JSP
  @url is the request path
  @id is the div id
 */
function sendRequest(url,id) {
  var xmlHttpReq = myXMLHttpRequest();
  url += ((url.indexOf('?') == -1)?'?':'&') + "rnd=" + new Date().valueOf().toString();
  xmlHttpReq.open('get', url);
  xmlHttpReq.onreadystatechange = function(){getResponse(xmlHttpReq,id);};
  xmlHttpReq.send(null);
}


/*Function is used to get response when server response is passed through a JSP
  @ id is the div id
*/
function getResponse(xmlHttpReq,id) {
  if(xmlHttpReq.readyState == 4){
    if (xmlHttpReq.status == 200){
      var viewer = document.getElementById(id) ;
      viewer.innerHTML=xmlHttpReq.responseText;
    }
  }
}

function copyRequest(url,id,contentId) {
  var xmlHttpReq = myXMLHttpRequest();
  url += ((url.indexOf('?') == -1)?'?':'&') + "rnd=" + new Date().valueOf().toString();
  xmlHttpReq.open('get', url);
  xmlHttpReq.onreadystatechange = function(){getCopyResponse(xmlHttpReq, id, contentId);};
  xmlHttpReq.send(null);
}

function getCopyResponse(xmlHttpReq,id,contentId) {
    if(xmlHttpReq.readyState == 4){
    if (xmlHttpReq.status == 200){
        var viewer = document.getElementById(id) ;
        var content = document.getElementById(contentId);
        viewer.innerHTML=content.innerHTML;
    }
  }
}

function callbackRequest(url,callbackFunction,param) {
    var xmlHttpReq = myXMLHttpRequest();
    url += ((url.indexOf('?') == -1)?'?':'&') + "rnd=" + new Date().valueOf().toString();
    xmlHttpReq.open('get', url);
    xmlHttpReq.onreadystatechange = function(){getCallbackResponse(xmlHttpReq,callbackFunction,param);};
    xmlHttpReq.send(null);
}

function getCallbackResponse(xmlHttpReq,callbackFunction,param) {
    if(xmlHttpReq.readyState == 4){
    if (xmlHttpReq.status == 200){
      if (arguments.length == 3) {
        eval(callbackFunction + "('" + param + "');");
      } else {
        eval(callbackFunction);
      }
    }
  }
}
