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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%--@elvariable id="domain" type="java.lang.String"--%>
<%--@elvariable id="key" type="java.lang.String"--%>
<%--@elvariable id="project" type="com.concursive.connect.web.modules.profile.dao.Project"--%>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<c:set var="project" value="${project}" scope="request" />
<jsp:useBean id="project" type="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%-- Draw the map --%>
<%
  String mapId = "Map" + renderResponse.getNamespace();
  request.setAttribute("mapId", mapId);
%>
<c:set var="mapHeight" value="300px" scope="request"/>
<c:set var="mapVersion" value="2.s" scope="request"/>
<script src="http://maps.google.com/maps?file=api&v=${mapVersion}&key=${key}&hl=en" type="text/javascript"></script>
<style type="text/css">
	v\:* {
		behavior:url(#default#VML);
	}
</style>
<script type="text/javascript">
var ${mapId};
if(document.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#SVG","1.1")) {
	_mSvgEnabled = true;
	_mSvgForced = true;
}
<c:choose>
  <c:when test="${empty project.address}">
    <c:set var="mapZoom" value="4" scope="request"/>
    var thisIcon = new GIcon();
    thisIcon.image = '${ctx}/images/map_markers/zone.png';
    thisIcon.shadow = '${ctx}/images/map_markers/zone.png';
    thisIcon.iconSize = new GSize(120, 120);
    thisIcon.shadowSize = new GSize(120, 120);
    thisIcon.iconAnchor = new GPoint(60, 60);
    thisIcon.infoWindowAnchor = new GPoint(60, 60);
  </c:when>
  <c:otherwise>
    <%-- set zoom level --%>
    <c:set var="mapZoom" value="2" scope="request"/>
    <%-- set icon --%>
    var thisIcon = new GIcon();
    thisIcon.shadow = '${ctx}/images/map_markers/marker_shadow.png';
    thisIcon.iconSize = new GSize(33, 37);
    thisIcon.shadowSize = new GSize(57, 33);
    thisIcon.iconAnchor = new GPoint(16, 34);
    thisIcon.infoWindowAnchor = new GPoint(28, 12);
    <c:choose>
      <c:when test="${project.category.description eq 'Businesses'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_orange_business.png';
      </c:when>
      <c:when test="${project.category.description eq 'Organizations'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_orange_org.png';
      </c:when>
      <c:when test="${project.category.description eq 'People'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_blue_people.png';
      </c:when>
      <c:when test="${project.category.description eq 'Events'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_purple_event.png';
      </c:when>
      <c:when test="${project.category.description eq 'Groups'}">
        thisIcon.image = '${ctx}/images/map_markers/marker_green_group.png';
      </c:when>
      <c:otherwise>
        thisIcon.image = '${ctx}/images/map_markers/marker_gray.png';
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>

function initializeMap${mapId}(e) {
	if (GBrowserIsCompatible()) {
		${mapId} = new GMap(document.getElementById("${mapId}"));
		${mapId}.setMapType(G_NORMAL_MAP);
		${mapId}.centerAndZoom(new GPoint(${project.longitude}, ${project.latitude}), ${mapZoom});
    <c:set var="markerContent" scope="request">
      <c:if test="${!empty project.category.logo}"><c:choose><c:when test="${!empty project.logo}"><img alt='<c:out value="${project.title}"/> photo' align='absmiddle' src='${ctx}/image/<%= project.getLogo().getUrlName(45,45) %>' /> </c:when><c:otherwise><img alt='Default photo' align='absmiddle' src='${ctx}/image/<%= project.getCategory().getLogo().getUrlName(45,45) %>'  class='default-photo' /> </c:otherwise></c:choose></c:if><c:out value="${project.title}"/><c:if test="${!empty project.address}"><br /><c:out value="${project.address}"/></c:if><br /><c:out value="${project.location}"/><c:if test="${!empty project.businessPhone}"><br />Phone: <strong><c:out value="${project.businessPhone}"/></strong></c:if><c:if test="${!empty project.businessFax}"><br />Fax: <strong><c:out value="${project.businessFax}"/></strong></c:if>
    </c:set>
    var marker1 = new GMarker(new GPoint(${project.longitude}, ${project.latitude}), {icon:thisIcon});
    ${mapId}.addOverlay(marker1);
		GEvent.addListener(marker1, 'click', function() {
			marker1.openInfoWindowHtml("<div>${markerContent}</div>");
		});
		${mapId}.addControl(new GSmallMapControl());
		${mapId}.enableDragging();
	}
}
</script>
<div id="${mapId}" style="height: ${mapHeight};width: 100%"></div>
<%-- Show project name when getting directions --%>
<c:set var="titleContent" scope="request">${project.title}</c:set>
<c:set var="titleContent" scope="request">${fn:replace(titleContent, ' ', '+')}</c:set>
<c:set var="titleContent" scope="request">${fn:replace(titleContent, '&', '%26')}</c:set>
<%-- Show project address when getting directions --%>
<c:set var="directionsContent" scope="request">${project.address} ${project.location}</c:set>
<c:set var="directionsContent" scope="request">${fn:replace(directionsContent, ' ', '+')}</c:set>
<c:set var="directionsContent" scope="request">${fn:replace(directionsContent, '&', '%26')}</c:set>
<a target="_blank" href="http://maps.google.com/maps?f=d&hl=en&geocode=&q=to:${directionsContent}(<c:out value="${titleContent}"/>)&ie=UTF8&z=13">get directions</a>
<%-- Use YUI to properly start the map in a portlet --%>
<script type="text/javascript">
  YAHOO.util.Event.addListener(window, "load", initializeMap${mapId});
  YAHOO.util.Event.addListener(window, "unload", GUnload);
</script>