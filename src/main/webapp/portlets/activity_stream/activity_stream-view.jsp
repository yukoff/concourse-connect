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
<%@ page import="com.concursive.connect.web.modules.activity.dao.ProjectHistoryList" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.commons.date.DateUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ page import="java.sql.Timestamp" %>
<jsp:useBean id="projectHistoryArrayList" class="java.util.ArrayList" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<c:set var="add_activity_constant">
  <%= ProjectHistoryList.ADD_ACTIVITY_ENTRY_EVENT %>
</c:set>
<h3><c:out value="${title}"/></h3>
<c:if test="${!empty projectHistoryArrayList}">
  <c:forEach items="${projectHistoryArrayList}" var="dayList" varStatus="dayStatus">
    <c:set var="drawDay" value="true"/>
    <c:forEach items="${dayList}" var="ownerList" varStatus="ownerStatus">
      <c:set var="drawOwner" value="true"/>
      <c:forEach items="${ownerList}" var="projectHistory" varStatus="ownerStatus">
        <c:set var="projectHistory" value="${projectHistory}"/>
        <jsp:useBean id="projectHistory" type="com.concursive.connect.web.modules.activity.dao.ProjectHistory"/>
        <%-- Draw the day --%>
        <c:if test="${!empty drawDay}">
          <c:remove var="drawDay"/>
          <div class="portlet-section-subheader">
            <h4><ccp:tz timestamp="${projectHistory.entered}" pattern="MMMM dd" /></h4>
          </div>
        </c:if>
        <%-- Draw the owner --%>
        <c:if test="${!empty drawOwner}">
          <c:remove var="drawOwner"/>
          <ol>
            <li>
          <c:choose>
            <%-- use the project's logo for manually entered events --%>
            <c:when test="${projectHistory.eventType == add_activity_constant}">
              <c:choose>
                <c:when test="${!empty projectHistory.project.logo}">
                <img alt="<c:out value="${projectHistory.project.title}"/> photo"
                     src="${ctx}/image/<%= projectHistory.getProject().getLogo().getUrlName(45,45) %>"/>
                </c:when>
                <c:when test="${!empty projectHistory.project.category.logo}">
                  <img alt="Default user photo"
                       src="${ctx}/image/<%= projectHistory.getProject().getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
                </c:when>
              </c:choose>
            </c:when>
            <%-- use the user's logo --%>
            <c:when test="${!empty projectHistory.user.profileProject.logo}">
              <img alt="<c:out value="${projectHistory.user.profileProject.title}"/> photo"
                   src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getLogo().getUrlName(45,45) %>"/>
            </c:when>
            <c:when test="${!empty projectHistory.user.profileProject.category.logo}">
              <img alt="Default user photo"
                   src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
            </c:when>
          </c:choose>
        </c:if>
        <%-- Draw the activity --%>
        <%-- <p> --%>${projectHistory.htmlLink}<%-- </p> --%>
        <c:if test="${ownerStatus.last}">
            </li>
          </ol>
        </c:if>
      </c:forEach>
    </c:forEach>
  </c:forEach>
</c:if>
<c:if test="${empty projectHistoryArrayList}">
  <p>There are no activities to report at this time.</p>
</c:if>
