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

var imagePanel;
var panelGroups = {}; // stores data on all links with group names in the rel attribute (i.e. <a rel='shadowbox[images]'/>)
var pagingIndex;
var pagingGroupName;
var panelSubmitted = false;

function attachPanel() {
  var links = document.getElementsByTagName("a");
  if (links != null) {
    for (var i in links) {
      var link = links[i];
      if (link) {
        if (link.rel) {
          if (link.rel.length >= 9 && link.rel.substring(0, 9) == "shadowbox") {
            var width = 600;
            var imageWidth = null;
            var imageHeight = null;
            var imageUrl = null;
            var options = link.rel.split(";");
            var groupName = null;
            var url = link.href;
            var hasGroup = link.rel.indexOf("[") > -1 && link.rel.indexOf("]") > -1;
            var isImage = url.toLowerCase().indexOf(".jpg") > -1 || url.toLowerCase().indexOf(".gif") > -1 || url.toLowerCase().indexOf(".png") > -1;
            if (hasGroup) {
              var nameStart = link.rel.indexOf("[") + 1;
              var nameEnd = link.rel.indexOf("]");
              groupName = link.rel.substring(nameStart, nameEnd);
            }
            if (options) {
              for (var k in options) {
                if (options[k].indexOf("width=") == 0) {
                  width = options[k].substring(6);
                  if (width == '') width = null;
                } else if (options[k].indexOf("imageWidth=") == 0) {
                  imageWidth = options[k].substring(11);
                } else if (options[k].indexOf("imageHeight=") == 0) {
                  imageHeight = options[k].substring(12);
                } else if (options[k].indexOf("imageUrl=") == 0) {
                  imageUrl = options[k].substring(9);
                }
              }
            }
            if (link.href && link.href.indexOf("javascript:") == -1) {
              // Update the URL with the panel link
              var thisTitle = link.title.replace(/['"]/g,'');
              if (isImage) {
                link.href = 'javascript:showImage("' + thisTitle + '","' + url + '",' + width + ',' + imageHeight + ',' + imageWidth + ','+(groupName == null ? null : '"' + groupName + '"')+')';
              } else {
                link.href = 'javascript:showPanel("' + thisTitle + '","' + url + '",' + width + ')';
              }
            }
            if (hasGroup) {
              var counter = null; // not sure if this is needed or if there's a simpler way
              if (isImage && imageUrl) {
                url = imageUrl;
              }
              if (panelGroups[groupName]) {
                counter = panelGroups[groupName].length;
              }
              if (!counter) {
                var thisPanelGroup = new Array();
                thisPanelGroup[0] = {
                  "url" : url,
                  "title" : link.title,
                  "width" : width,
                  "imageHeight" : imageHeight,
                  "imageWidth" : imageWidth
                };
                panelGroups[groupName] = thisPanelGroup;
              } else {
                panelGroups[groupName][counter] = {
                  "url" : url,
                  "title" : link.title,
                  "width" : width,
                  "imageHeight" : imageHeight,
                  "imageWidth" : imageWidth
                };
              }
            }
          }
        }
      }
    }
  }
}

function scaleImage(img, maxWidth, maxHeight) {
  var factor;
  var scaledHeight = img.height;
  var scaledWidth = img.width;
  var isRescale = false;
  if (scaledWidth > maxWidth) {
    isRescale = true;
    factor = maxWidth / img.width;
    scaledWidth = maxWidth;
    scaledHeight = factor * img.height;
  }
  if (scaledHeight > maxHeight) {
    isRescale = true;
    factor = maxHeight / img.height;
    scaledWidth = factor * img.width;
    scaledHeight = maxHeight;
  }
  if (isRescale) {
    img.width = scaledWidth;
    img.height = scaledHeight;
  }
}

function showImage(title, url, width) {
  showImage(title, url, width, null, null, null);
}

function showImage(title, url, width, imageHeight, imageWidth, groupName) {
  var maxWidth = YAHOO.util.Dom.getViewportWidth() - (YAHOO.util.Dom.getViewportWidth() / 4);
  var maxHeight = YAHOO.util.Dom.getViewportHeight() - (YAHOO.util.Dom.getViewportHeight() / 4);
  if (width == null || width > maxWidth) {
    width = maxWidth;
  }
  var img = new Image();
  if (imageHeight && imageWidth) {
    img.height = imageHeight;
    img.width = imageWidth;
    scaleImage(img, maxWidth, maxHeight);
  }
  var posx = (YAHOO.util.Dom.getViewportWidth() - width) / 2;
  var panelId = "image-panel";

  if (!imagePanel) {
    // Instantiate a new panel
    imagePanel = new YAHOO.widget.Panel(panelId, {
      width:width + "px",
      x:posx,
      y:0,
      close:false,
      constraintoviewport:true,
      draggable:true,
      underlay:"shadow",
      modal:true});

    // Add some listeners
    var listeners = new Array();
    listeners[0] = new YAHOO.util.KeyListener(document, { keys:27 },
      { fn:imagePanel.hide, scope:imagePanel, correctScope:true }, "keyup");
    listeners[1] = new YAHOO.util.KeyListener(document, { keys:37 },
      { fn:showPreviousImage, scope:imagePanel, correctScope:true }, "keyup");
    listeners[2] = new YAHOO.util.KeyListener(document, { keys:39 },
      { fn:showNextImage, scope:imagePanel, correctScope:true }, "keyup");
    imagePanel.cfg.queueProperty("keylisteners", listeners);
  }

  // Determine the html
  imagePanel.setHeader(title);
  if (img.height > 0 && img.width > 0) { // size image appropriately if parameters or scaled values exist
    imagePanel.setBody("<img src='" + url + "' height='" + img.height + "' width='" + img.width + "'/>");
  } else {
    imagePanel.setBody("<img src='" + url + "' />");
  }
  var paginationString = "";
  if (groupName) {
    paginationString = createImagePagination(groupName, url);
  }
  imagePanel.setFooter(paginationString + "<input type='button' id='panelCloseButton' value='Close' class='cancel'/>");
  imagePanel.render(document.getElementById("popupLayer"));

  // Attach cancel to the buttons
  var divs = YAHOO.util.Dom.getElementsByClassName('cancel');
  for (var i = 0; i < divs.length; i++) {
    YAHOO.util.Event.on(divs[i], "click", function(){
       imagePanel.hide();
    });
  }

  imagePanel.show();
//  imagePanel.cfg.refireEvent("zIndex");

  pagingGroupName = groupName;
  if(paginationString != "") {
    pagingIndex = getCurrentIndexForUrlAndGroupName(groupName, url);
  }
}

function getCurrentIndexForUrlAndGroupName(groupName, currentUrl) {
  for (var tmpGroupName in panelGroups) {
    if (tmpGroupName == groupName) {
      for (var index in panelGroups[tmpGroupName]) {
        var img = panelGroups[tmpGroupName][index];
        var url = img.url;
        if(url == currentUrl) {
            return Number(index);
        }
      }
    }
  }
  return null;
}

function showPreviousImage() {
  var prevImg = panelGroups[pagingGroupName][pagingIndex-1];
  if(prevImg){
      showImage(prevImg.title, prevImg.url, prevImg.width,
              prevImg.imageHeight, prevImg.imageWidth, pagingGroupName);
  }
}

function showNextImage() {
  var nextImg = panelGroups[pagingGroupName][pagingIndex+1];
  if(nextImg){
      showImage(nextImg.title, nextImg.url, nextImg.width,
              nextImg.imageHeight, nextImg.imageWidth, pagingGroupName);
  }
}

function createImagePagination(groupName, currentUrl) {
  var paginationString = "";
  var counter = 1;
  // get all elements from the same group
  for (var tmpGroupName in panelGroups) {
    if (tmpGroupName == groupName) {
      if(panelGroups[tmpGroupName].length <= 1) {
        return ""; //no pagination needed
      }
      var pagStart = "<ol class='pagination'>";
      var pagEnd = "</ol>";
      for (var index in panelGroups[tmpGroupName]) {
        var img = panelGroups[tmpGroupName][index];
        var url = img.url;
        var title = encodeURIComponent(img.title);
        var width = img.width;
        var imageHeight = img.imageHeight;
        var imageWidth = img.imageWidth;
        var link = 'javascript:showImage("' + title + '","' + url + '",' + width +
                     ',' + imageHeight + ',' + imageWidth + ',"' + groupName + '")';
        if (currentUrl == url) { // this is active so no direct link, create prev and next buttons as needed
          // if the first link is not active print enabled prev button, otherwise print disabled prev button
          if(counter == 1) {
            pagStart += "<li class='previous-off'>&lt; Previous</li>";
          } else {
            var prevImg = panelGroups[tmpGroupName][(counter-2)];
            var prevLink = 'javascript:showImage("' + encodeURIComponent(prevImg.title) + '","' + prevImg.url + '",' + prevImg.width +
                     ',' + prevImg.imageHeight + ',' + prevImg.imageWidth + ',"' + groupName + '")';
            pagStart += "<li class='previous'><a href='" + prevLink + "'>&lt; Previous</a></li>";
          }
          // current page
          paginationString += "<li class='active'>" + counter + "</li>";
          // create next link
          if(counter == panelGroups[tmpGroupName].length) {
            pagEnd = "<li class='next-off'>Next &gt;</li>"+pagEnd;
          } else {
            var nextImg = panelGroups[tmpGroupName][counter];
            var nextLink = 'javascript:showImage("' + encodeURIComponent(nextImg.title) + '","' + nextImg.url + '",' + nextImg.width +
                     ',' + nextImg.imageHeight + ',' + nextImg.imageWidth + ',"' + groupName + '")';
            pagEnd = "<li class='next'><a href='" + nextLink + "'>Next &gt;</a></li>"+pagEnd;
          }
        } else {
          paginationString += "<li><a href='" + link + "'>" + counter + "</a></li>";
        }
        counter++;
      }
      paginationString = pagStart + paginationString + pagEnd;
    }
  }
  return paginationString;
}


function showPanel(title, url, width) {
  // Initialize the temporary Panel to display while waiting for external content to load
  var widthWait = 52;
  var posxWait = (YAHOO.util.Dom.getViewportWidth() - widthWait) / 2;
  var waitPanel = new YAHOO.widget.Panel("wait",
	            { width:widthWait + "px",
                x:posxWait,
                y:100,
	              close:false,
	              draggable:false,
	              modal:true
	            }
	        );
	waitPanel.setBody('<img src="' + teamelements_ctx + '/images/loading32.gif" width="32" height="32" />');
	waitPanel.render(document.getElementById("popupLayer"));
  waitPanel.show();

  // Prepare the target panel
  var posx = (YAHOO.util.Dom.getViewportWidth() - width) / 2;
  var panel = new YAHOO.widget.Dialog("dynamic-panel", {
    width:width + "px",
    x:posx,
    y:0,
    close:false,
    constraintoviewport:true,
    draggable:false,
    underlay:"shadow",
    hideaftersubmit:false,
    modal:true});

  // Define various event handlers for Dialog
  var handleUpload = function(o) {
    panel.cancel();
    var url = window.location.href;
    window.location.href = url;
  };

  var handleSuccess = function(o) {
    if (o.status == 200 && o.responseText.indexOf("\"location\":") > -1) {
      // success, found a redirect location
      panel.cancel();
      var url = o.responseText.substring(o.responseText.indexOf('\"location\":') + 12, o.responseText.lastIndexOf('\"'));
      if (url) {
        if (url.indexOf("\r") > -1) {
          url = url.substring(0, url.indexOf("\r"));
        }
        window.location.href = url;
      }
    } else if (o.status == 201) {
      // success, content created
      panel.cancel();
      var url = o.getResponseHeader['Location'];
      if (url) {
        if (url.indexOf("\r") > -1) {
          url = url.substring(0, url.indexOf("\r"));
        }
        // Reload the current page
        url = window.location.href;
        window.location.href = url;
      }
    } else if (o.status == 204) {
      // success, no response
      panel.cancel();
    } else {
      // 200-300
      panel.setBody(o.responseText);
      panel.render();
      attachEvents(panel);
    }
  };

  var handleFailure = function(o) {
    // failed alert("Failed: " + o.status);
  };

  var kl = new YAHOO.util.KeyListener(document, { keys:27 },
    { fn:panel.cancel, scope:panel, correctScope:true }, "keyup");
  panel.cfg.queueProperty("keylisteners", kl);

  // Wire up the success and failure handlers
  panel.callback = { upload: handleUpload, success: handleSuccess, failure: handleFailure };

  // The initial callback for displaying the panel content
  var callback = {
    success : function(o) {
      panel.setBody(o.responseText);
      panel.render(document.getElementById("popupLayer"));
      waitPanel.hide();
      attachEvents(panel);
      panel.show();
      panel.focusFirst();
    },
    failure : function(o) {
      waitPanel.hide();
    }
  };

  // The initial events
  attachEvents(panel);

  var popupURL = url;
  if (popupURL.indexOf("?") > -1) {
    popupURL += "&popup=true";
  } else {
    popupURL += "?popup=true";
  }
  var oConnect = YAHOO.util.Connect.asyncRequest("GET", popupURL, callback);
}

function attachEvents(panel) {
  // Reset the panel
  panelSubmitted = false;

  // Wire up the validate handler to only allow one submit
  panel.validate = function() {
    if (panelSubmitted) {
      return false;
    } else {
      panelSubmitted = true;
      // find the last spinner
      var uItems = YAHOO.util.Dom.getElementsByClassName("submitSpinner");
      if (uItems.length > 0) {
        YAHOO.util.Dom.setStyle(uItems[uItems.length-1], "display", "inline");
      }
      return true;
    }
  };
  
  // Attach cancel to the buttons
  var buttons = YAHOO.util.Dom.getElementsByClassName('cancel');
  for (var i = 0; i < buttons.length; i++) {
    if (buttons[i].id == 'panelCloseButton') {
      YAHOO.util.Event.on(buttons[i], "click", function(){
         panel.cancel();
      });
    }
  }

  // Attach submit to elements
  var tags = YAHOO.util.Dom.getElementsByClassName('submitPanelOnChange');
  for (var i = 0; i < tags.length; i++) {
    YAHOO.util.Event.on(tags[i], "change", function(){
       panel.submit();
    });
  }
}

YAHOO.util.Event.onDOMReady(attachPanel);
