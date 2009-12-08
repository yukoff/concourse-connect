<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" />
<h3><c:out value="${title}"/></h3>
<c:set var="actionURL" scope="request">
  <portlet:actionURL portletMode="view" />
</c:set>
<c:set var="maxActionURL" scope="request">
  <portlet:actionURL portletMode="view" windowState="maximized"/>
</c:set>
<c:choose>
  <c:when test="${!empty eventList}">
    <ol>
      <c:forEach var="meeting" items="${eventList}">
        <c:set var="project" value="${projectByIdMap[meeting.projectId]}" scope="request"/>
        <c:set var="attendee" value="${attendeeByMeetingIdMap[meeting.id]}" />
        <jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
        <li>
          <c:choose>
            <c:when test="${!empty project.logo}">
              <img alt="<c:out value="${project.title}"/> photo" width="45" height="45" src="${ctx}/image/<%= project.getLogo().getUrlName(45,45) %>" />
            </c:when>
            <c:when test="${!empty project.category.logo}">
              <img alt="Default photo" width="45" height="45" src="${ctx}/image/<%= project.getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
            </c:when>
          </c:choose>
          <h4><a href="${ctx}/show/${project.uniqueId}" title="<c:out value="${meeting.title}"/> link"><c:out value="${meeting.title}"/></a></h4>
          <c:set var="startDate"><ccp:tz timestamp="${meeting.startDate}" dateOnly="true"/></c:set>
          <c:set var="endDate"><ccp:tz timestamp="${meeting.endDate}" dateOnly="true"/></c:set>
          <c:choose>
            <c:when test="${startDate == endDate}">
              <p>
                ${startDate}
                <ccp:tz timestamp="${meeting.startDate}" timeOnly="true"/>
                <c:if test="${meeting.startDate ne meeting.endDate}">- <ccp:tz timestamp="${meeting.endDate}" timeOnly="true"/></c:if>
              </p>
            </c:when>
            <c:otherwise>
              <p>Starts ${startDate} at <ccp:tz timestamp="${meeting.startDate}" timeOnly="true"/></p>
              <p>Ends ${endDate} at <ccp:tz timestamp="${meeting.endDate}" timeOnly="true"/></p>
            </c:otherwise>
          </c:choose>
          <c:if test="${!empty meeting.location}">
            <address><c:out value="${meeting.location}"/></address>
          </c:if>
        </li>
      </c:forEach>
      <c:if test="${!empty hasMoreURL}">
        <li class="more"><a href='${ctx}${hasMoreURL}' title='more events'><ccp:label
            name="results.more">More...</ccp:label></a></li>
      </c:if>
    </ol>
  </c:when>
  <c:otherwise>
    <p><ccp:label name="events.upcoming.none">There are no upcoming events at this time, please check back later or add your own event.</ccp:label></p>
  </c:otherwise>
</c:choose>