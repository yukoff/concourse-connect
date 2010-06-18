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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.badges.dao.Badge" %>
<portlet:defineObjects/>
<%@ include file="initPage.jsp" %>
<div class="portletWrapper">
  <%--<h1><ccp:tabLabel name="Badges" object="project"/></h1>--%>
  <c:choose>
    <c:when test="${empty badgeList}">
      <div class="portlet-message-info"><p><ccp:label name="projectsCenterBadges.noBadges">This profile doesn't have any badges to display.</ccp:label></p></div>
    </c:when>
    <c:otherwise>
      <ul class="bucketlist bucketlistBadges">
        <c:forEach var="badge" items="${badgeList}" varStatus="status">
          <c:set var="badge" scope="request" value="${badge}"/>
          <c:set var="projectBadge" scope="request" value="${projectBadgeMap[badge.id]}" />
          <li>
            <div class="bucket">
              <c:if test="${!empty badge.logo}">
                <img alt="<c:out value="${badge.title}"/>" src="<%= ctx %>/image/<%= ((Badge)request.getAttribute("badge")).getLogo().getUrlName(45,45) %>" class="badgeImage" />
              </c:if>
              <div class="content">
                <h2><a href="<%= ctx %>/badge/${badge.id}" rel="shadowbox" title="Badge Details for <c:out value="${badge.title}" />"><c:out value="${badge.title}" /></a></h2>
                <ul>
                  <div id="badge_${badge.id}">
                    <c:set var="memberCount" scope="request" value="${badgeMemberCountMap[badge.id]}" />
                    <c:if test="${!empty memberCount && memberCount ne '1'}">
                      <li>Shared by <c:out value="${memberCount}"/> listings</li>
                    </c:if>
                    <li>Added <ccp:tz timestamp="${projectBadge.entered}" dateOnly="true"/></li>
                 </div>
                </ul>
              </div>
            </div>
          </li>
        </c:forEach>
      </ul>
    </c:otherwise>
  </c:choose>
</div>
