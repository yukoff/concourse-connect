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
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%--@elvariable id="title" type="java.lang.String"--%>
<%--@elvariable id="teamMemberList" type="com.concursive.connect.web.modules.members.dao.TeamMemberList"--%>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<h3><c:out value="${title}"/></h3>
<c:choose>
  <c:when test="${modifyNotification eq true}">
    <%-- Use the drop-down menu for notifications --%>
    <%@ include file="projects_by_user_menu.jspf" %>
    <div class="portlet-message-info">
      <div class="horizontal-list">
        <dl class="ccp-schedule-legend">
          <dt>For each profile, choose a preference:</dt>
          <dd class="ccp-schedule-0">never</dd>
          <dd class="ccp-schedule-1">often</dd>
          <dd class="ccp-schedule-2">daily</dd>
          <dd class="ccp-schedule-3">weekly</dd>
          <dd class="ccp-schedule-4">monthly</dd>
        </dl>
      </div>
    </div>
    <div class="yui-skin-sam">
      <div id="<portlet:namespace/>buttons" class="wrapping-list">
        <c:forEach items="${teamMemberList}" var="teamMember" varStatus="teamMemberCounter">
          <c:choose>
            <c:when test="${teamMember.emailUpdatesSchedule == 4}"><c:set var="emailUpdatesSchedule" value="4"/></c:when>
            <c:when test="${teamMember.emailUpdatesSchedule == 3}"><c:set var="emailUpdatesSchedule" value="3"/></c:when>
            <c:when test="${teamMember.emailUpdatesSchedule == 2}"><c:set var="emailUpdatesSchedule" value="2"/></c:when>
            <c:when test="${teamMember.emailUpdatesSchedule == 1}"><c:set var="emailUpdatesSchedule" value="1"/></c:when>
            <c:otherwise><c:set var="emailUpdatesSchedule" value="0"/></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${teamMember.project.profile && teamMember.project.owner == teamMember.userId}"><c:set var="nameToDisplay" value="Me"/></c:when>
            <c:otherwise><c:set var="nameToDisplay" value="${teamMember.project.title}"/></c:otherwise>
          </c:choose>
          <input class="ccp-schedule-${emailUpdatesSchedule}" type="button" id="<portlet:namespace/>splitbutton_${teamMember.id}" name="${teamMember.id},'${teamMember.project.uniqueId}',${teamMember.notification},${teamMember.emailUpdatesSchedule}" value="<c:out value="${nameToDisplay}" />">
        </c:forEach>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <%-- Show a linkable list --%>
    <div class="tag-list">
      <ul>
        <c:forEach items="${teamMemberList}" var="teamMember" varStatus="teamMemberCounter">
          <li>
            <c:choose>
              <c:when test="${teamMember.project.profile && teamMember.project.owner == teamMember.userId}"><c:set var="nameToDisplay" value="Me"/></c:when>
              <c:otherwise><c:set var="nameToDisplay" value="${teamMember.project.title}"/></c:otherwise>
            </c:choose>
            <a href="${ctx}/show/${teamMember.project.uniqueId}"><c:out value="${nameToDisplay}" /></a>&nbsp;
          </li>
        </c:forEach>
      </ul>
    </div>
  </c:otherwise>
</c:choose>
