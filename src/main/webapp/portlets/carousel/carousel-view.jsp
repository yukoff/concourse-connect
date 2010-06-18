<%--
  ~ ConcourseConnect
  ~ Copyright 2009 Concursive Corporation
  ~ http://www.concursive.com
  ~
  ~ This file is part of ConcourseConnect, an open source social business
  ~ software and community platform.
  ~
  ~ Concursive ConcourseConnect is free software: you can redistribute it and/or
  ~ modify it under the terms of the GNU Affero General Public License as published
  ~ by the Free Software Foundation, version 3 of the License.
  ~
  ~ Under the terms of the GNU Affero General Public License you must release the
  ~ complete source code for any application that uses any part of ConcourseConnect
  ~ (system header files and libraries used by the operating system are excluded).
  ~ These terms must be included in any work that has ConcourseConnect components.
  ~ If you are developing and distributing open source applications under the
  ~ GNU Affero General Public License, then you are free to use ConcourseConnect
  ~ under the GNU Affero General Public License.
  ~
  ~ If you are deploying a web site in which users interact with any portion of
  ~ ConcourseConnect over a network, the complete source code changes must be made
  ~ available.  For example, include a link to the source archive directly from
  ~ your web site.
  ~
  ~ For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
  ~ products, and do not license and distribute their source code under the GNU
  ~ Affero General Public License, Concursive provides a flexible commercial
  ~ license.
  ~
  ~ To anyone in doubt, we recommend the commercial license. Our commercial license
  ~ is competitively priced and will eliminate any confusion about how
  ~ ConcourseConnect can be used and distributed.
  ~
  ~ ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
  ~ ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  ~ FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
  ~ details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ Attribution Notice: ConcourseConnect is an Original Work of software created
  ~ by Concursive Corporation
  --%>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItemList"%>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem"%>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project"%>
<%@ page import="com.concursive.connect.web.portal.PortalUtils"%>
<%@ include file="../../initPage.jsp"%>
<portlet:defineObjects/>
<h3><c:out value="${title}"/></h3>
<div class="yui-skin-sam">
	<div id="container" class="yui-carousel-element" style="width:${width}px;">
		<ol id="carousel" class="yui-carousel-content">
			<c:if test="${!empty fileItemList}">
			  <c:forEach items="${fileItemList}" var="fileItem" varStatus="fileStatus">
			  	<li>
						<table class="itemTable">
							<tr>
								<td><a href="${ctx}/show/${fileItem.project.uniqueId}"> <img
									width="210" height="150" alt=""
									src="${ctx}/image/<%=((FileItem)pageContext.getAttribute("fileItem")).getUrlName(210,150) %>"> </a></td>
							</tr>
							<tr>
								<td><span><c:out value="${fileItem.comment}"/></span></td>
							</tr>
              <tr>
                <td><a href="${ctx}/show/${fileItem.project.uniqueId}"><c:out value="${fileItem.project.title}"/></a></td>
              </tr>
  					</table>
					</li>
			  </c:forEach>
			</c:if>
		</ol>
		<div class="yui-carousel-nav"></div>
	</div>
</div>

<script type="text/javascript">

  (function () {
    var maxRecords = ${maxRecords};
    var curpos = ${visiblePhotos};
    function getImageTag(data) {
      return "<table  class=\"itemTable\">" +
        "<tr><td><a href=\"${ctx}/show/" + data.uniqueId + "\">" +
        "<img width=\"210\" height=\"150\" alt=\"\" src=\"${ctx}/image/" + data.url +
        "\" title=\"" + data.name + " photo\" " + "/>" +
        "</a></td></tr>" +
        "<tr><td><span >" + data.comment + "</span></td></tr>" +
        "<tr><td><a href=\"${ctx}/show/" + data.uniqueId + "\">" + data.name + "</a></td></tr>" +
        "</table>";
    }

    function getImages(o) {
      var carousel = this;
      <portlet:renderURL var="imageListURL" portletMode="view" windowState="maximized" />
      YAHOO.util.Connect.asyncRequest("GET", "${imageListURL}" +
          "&__rp<%=PortalUtils.getDashboardPortlet((PortletRequest)request).getWindowConfigId()%>_offset=" + (curpos + ${visiblePhotos}) +
          "&__rp<%=PortalUtils.getDashboardPortlet((PortletRequest)request).getWindowConfigId()%>_limit=" + o.num +
              "&out=text",
              {
                  success: function (o) {
                      var i = curpos,
                          j = 0,
                          r = eval('(' + o.responseText + ')');
                      curpos += r.length;
                      while (i < curpos) {
                        if (r[j]) {
                          carousel.addItem(getImageTag(r[j]));
                        } else {
                          break;
                        }
                        i++;
                        j++;
                      }
                  },

                  failure: function (o) {
                  }
      });
    }

    YAHOO.util.Event.onContentReady("container", function (ev) {
      var carousel;
      var containerElement = document.getElementById("container");
      var containerWidth = YAHOO.util.Dom.getStyle(containerElement, "width");
      YAHOO.widget.Carousel.prototype.CONFIG.MAX_PAGER_BUTTONS = 10;
      carousel = new YAHOO.widget.Carousel("container", {
            numItems: maxRecords,
            numVisible: ${visiblePhotos},
            isCircular: true,
            autoPlayInterval: ${3000 * visiblePhotos},
            revealAmount: 2,
            animation: { speed: 0.50, effect: YAHOO.util.Easing.easeIn }
      });

      carousel.on("loadItems", function (o) {
        getImages.call(this, o);
      });

      // fix bug when going back to page 0 and odd number in display
     	carousel.on('pageChange', function (pageNo) {
        var animation = carousel.get('animation');
        if(pageNo == 0) {
          carousel.set('selectedItem', 0);
          carousel.set('firstVisible', 0);
        }
      });

      carousel.render();
      carousel.show();
      carousel.startAutoPlay();

      YAHOO.util.Dom.setStyle(containerElement, "width", containerWidth);
      var elements = YAHOO.util.Dom.getElementsByClassName('yui-carousel-content', 'div');
      YAHOO.util.Dom.setStyle(elements[0], "width",containerWidth);
    });
  })();
</script>