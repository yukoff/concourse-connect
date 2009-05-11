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

/* requires ajax.js */
var teamMemberTooltip;
var teamMemberTooltipTargetId;
(function() {

  var Dom = YAHOO.util.Dom;
  var Event = YAHOO.util.Event;
  var DDM = YAHOO.util.DragDropMgr;

  AjaxTooltip = function(el, cfg) {
    AjaxTooltip.superclass.constructor.call(this, el, cfg);
  };

  YAHOO.extend(AjaxTooltip, YAHOO.widget.Tooltip, {
    doShow : function(e, context) {
      var target = Event.getTarget(e);

      if (target.id.indexOf("_") > -1) {
        teamMemberTooltip = this;
        teamMemberTooltipTargetId = target.id;
        // Load the tooltip
        var url = teamelements_ctx + "/ProjectManagementTeam.do?command=Tooltip&id=" + target.id + "&out=text";
        xmlhttp.open('get', url);
        xmlhttp.onreadystatechange = function() {
          if (xmlhttp.readyState == 4) {
            if (target.id == teamMemberTooltipTargetId) {
              if (xmlhttp.status == 200) {
                var htmlData = xmlhttp.responseText;
                teamMemberTooltip.cfg.setProperty('text', htmlData);
              } else {
                teamMemberTooltip.cfg.setProperty('text', "");
              }
            }
          }
        }
        xmlhttp.send(null);
        return AjaxTooltip.superclass.doShow.apply(this, arguments);
      } else {
        this.cfg.setProperty('text', null);
      }
  }});

  TeamDDList = function(id, sGroup, config) {
    TeamDDList.superclass.constructor.call(this, id, sGroup, config);
    var el = this.getDragEl();
    Dom.setStyle(el, "opacity", 0.67); // The proxy is slightly transparent
    this.goingUp = false;
    this.lastY = 0;
  };

  YAHOO.extend(TeamDDList, YAHOO.util.DDProxy, {

    startDrag: function(x, y) {
      // make the proxy look like the source element
      var dragEl = this.getDragEl();
      var clickEl = this.getEl();
      Dom.setStyle(clickEl, "visibility", "hidden");

      dragEl.innerHTML = clickEl.innerHTML;

      Dom.setStyle(dragEl, "color", Dom.getStyle(clickEl, "color"));
      Dom.setStyle(dragEl, "backgroundColor", Dom.getStyle(clickEl, "backgroundColor"));
      Dom.setStyle(dragEl, "border", "2px solid gray");

      // Configure the zones
      var uItems = Dom.getElementsByClassName("projectCenterTeamRole");
      for (var j = 0; j < uItems.length; j++) {
        if (uItems[j].id.indexOf("role_") > -1) {
          Dom.setStyle(uItems[j], "border", "1px solid orange");
          Dom.setStyle(uItems[j], "padding", "0");
          Dom.setStyle(uItems[j], "padding-bottom", "15px");
        }
      }

    },

    endDrag: function(e) {
      var srcEl = this.getEl();
      var proxy = this.getDragEl();

        // Show the proxy element and animate it to the src element's location
      Dom.setStyle(proxy, "visibility", "");
      var a = new YAHOO.util.Motion(
          proxy, {
        points: {
          to: Dom.getXY(srcEl)
        }
      },
          0.2,
          YAHOO.util.Easing.easeOut
          )
      var proxyid = proxy.id;
      var thisid = this.id;

      // Hide the proxy and show the source element when finished with the animation
      a.onComplete.subscribe(function() {
        Dom.setStyle(proxyid, "visibility", "hidden");
        Dom.setStyle(thisid, "visibility", "");
      });
      a.animate();

    },

    onDragDrop: function(e, id) {
      // Configure the zones
      var uItems = Dom.getElementsByClassName("projectCenterTeamRole");
      for (var j = 0; j < uItems.length; j++) {
        if (uItems[j].id.indexOf("role_") > -1) {
          Dom.setStyle(uItems[j], "border", "0");
          Dom.setStyle(uItems[j], "padding", "1px");
        }
      }
      
      // If there is one drop interaction, the item was dropped either on the container,
      // or it was dropped on the current location of the source element.
      if (DDM.interactionInfo.drop.length === 1) {

        // The position of the cursor at the time of the drop (YAHOO.util.Point)
        var pt = DDM.interactionInfo.point;

        // The region occupied by the source element at the time of the drop
        var region = DDM.interactionInfo.sourceRegion;

        // Check to see if we are over the source element's location.  We will
        // append to the bottom of the container once we are sure it was a drop in
        // the negative space (the area of the container without any items)
        if (!region.intersect(pt)) {
          var destEl = Dom.get(id);
          var destDD = DDM.getDDById(id);
          destEl.appendChild(this.getEl());
          destDD.isEmpty = false;
          DDM.refreshCache();
        }
      }
    },

    onDrag: function(e) {
      // Keep track of the direction of the drag for use during onDragOver
      var y = Event.getPageY(e);
      if (y < this.lastY) {
        this.goingUp = true;
      } else if (y > this.lastY) {
        this.goingUp = false;
      }
      this.lastY = y;
    },

    onDragOver: function(e, id) {
      var srcEl = this.getEl();
      var destEl = Dom.get(id);

      // We are only concerned with items, we ignore the dragover
      // notifications for the container.
      if (destEl.nodeName.toLowerCase() == "li") {
        //var orig_p = srcEl.parentNode;
        var p = destEl.parentNode;
        if (this.goingUp) {
          p.insertBefore(srcEl, destEl); // insert above
        } else {
          p.insertBefore(srcEl, destEl.nextSibling); // insert below
        }
        DDM.refreshCache();
      }
    }
  });

})();

function initTeamMemberEditor(e) {
  // Configure the zones
  /*
  var uItems = document.getElementsByTagName("ol");
  for (var j = 0; j < uItems.length; j++) {
    if (uItems[j].id.indexOf("role_") == 0) {
      new YAHOO.util.DDTarget(uItems[j].id);
    }
  }
  */
  // Make items moveable
  var contextElements = [];
  var lItems = document.getElementsByTagName("li");
  //var dd;
  for (var k = 0; k < lItems.length; k++) {
    if (lItems[k].id.indexOf("teamMember_") == 0) {
      //dd = new TeamDDList(lItems[k].id);
      //dd.setHandleElId("teamMemberHandle_" + lItems[k].id.substring(11));
      var tooltipId = "tooltip_" + lItems[k].id.substring(11);
      contextElements[contextElements.length] = tooltipId;
    }
  }
  // Declare dynamic tooltip
  new AjaxTooltip('projectCenterTeamMemberToolTip', {
    preventoverlap: true,
    width:"300px",
    showdelay: 500,
    autodismissdelay: 10000,
    hidedelay: 0,
    context: contextElements});
}
YAHOO.util.Event.addListener(window, "load", initTeamMemberEditor);
