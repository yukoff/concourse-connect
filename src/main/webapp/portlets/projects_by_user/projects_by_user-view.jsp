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
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ page import="com.concursive.connect.web.modules.reviews.dao.ProjectRating" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<jsp:useBean id="projectRatingMap" class="java.util.HashMap" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
  <h3><c:out value="${title}"/></h3>
  <c:if test="${!empty teamMemberList}">
  <ol class="portletList">
    <c:forEach items="${teamMemberList}" var="teamMember">
      <c:set var="teamMember" value="${teamMember}" />
      <jsp:useBean id="teamMember" type="com.concursive.connect.web.modules.members.dao.TeamMember" />
      <li>
        <h4>
          <a href="${ctx}/show/${teamMember.project.uniqueId}" title="<c:out value="${teamMember.project.title}" />"><c:out value="${teamMember.project.title}"/></a>
          <ccp:evaluate if="<%= teamMember.getRoleId() < TeamMember.MEMBER %>">
            <span style="font-weight: normal;">(<ccp:role id="${teamMember.userLevel}"/>)
            <c:if test="${!empty privateMessageMap[teamMember.project.id] and privateMessageMap[teamMember.project.id] > 0}">
              <a href="${ctx}/show/${teamMember.project.uniqueId}/messages">${privateMessageMap[teamMember.project.id]} message<c:if test="${privateMessageMap[teamMember.project.id] > 1}">s</c:if></a>
            </c:if>
            </span>
          </ccp:evaluate>
        </h4>
        <c:if test="${modifyNotification eq 'true'}">
          <portlet:renderURL var="deleteNotificationURL" portletMode="view" windowState="maximized">
            <portlet:param name="teamMemberId" value="${teamMember.id}"/>
            <portlet:param name="notification" value="false"/>
            <portlet:param name="viewType" value="setNotification"/>
          </portlet:renderURL>
          <portlet:renderURL var="addNotificationURL" portletMode="view" windowState="maximized">
            <portlet:param name="teamMemberId" value="${teamMember.id}"/>
            <portlet:param name="notification" value="true"/>
            <portlet:param name="viewType" value="setNotification"/>
          </portlet:renderURL>
          <c:set var="deleteURL" scope="page">
            javascript:copyRequest('<%= pageContext.getAttribute("deleteNotificationURL") %>&out=text','<portlet:namespace/>notification_${teamMember.id}','add_${teamMember.id}');
          </c:set>
          <c:set var="addURL" scope="page">
            javascript:copyRequest('<%= pageContext.getAttribute("addNotificationURL") %>&out=text','<portlet:namespace/>notification_${teamMember.id}','remove_${teamMember.id}');
          </c:set>
          <div id="remove_${teamMember.id}" class="menu"><a href="${deleteURL}">Remove Notifications?</a></div>
          <div id="add_${teamMember.id}" class="menu"><a href="${addURL}">Add Notifications?</a></div>
          <c:choose>
            <c:when test="${teamMember.notification eq true}">
              <div id="<portlet:namespace/>notification_${teamMember.id}"><a href="${deleteURL}">Remove Notifications?</a></div>
            </c:when>
            <c:otherwise>
              <div id="<portlet:namespace/>notification_${teamMember.id}"><a href="${addURL}">Add Notifications?</a></div>
            </c:otherwise>
          </c:choose>
        </c:if>
      </li>
    </c:forEach>
  </ol>
</c:if>
<c:if test="${empty teamMemberList}">
  <p><c:out value="This user does not have ${categoryName}"/></p>
</c:if>
