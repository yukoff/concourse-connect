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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.concursive.connect.web.modules.activity.dao.ProjectHistoryList" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.commons.date.DateUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ page import="java.sql.Timestamp" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<c:set var="user" value="<%= User %>" scope="request" />
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="projectHistoryList" class="com.concursive.connect.web.modules.activity.dao.ProjectHistoryList" scope="request"/>
<jsp:useBean id="projectHistoryArrayList" class="java.util.ArrayList" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<c:set var="add_activity_constant">
  <%= ProjectHistoryList.ADD_ACTIVITY_ENTRY_EVENT %>
</c:set>
<h3><c:out value="${title}"/></h3>
<c:if test="${!empty content}">
  <h4><c:out value="${content}"/></h4>
</c:if>
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
            <h4>
              <ccp:tz timestamp="${projectHistory.entered}" pattern="MMMM dd" />
              (<ccp:tz timestamp="${projectHistory.entered}" pattern="relative" />)
            </h4>
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
                     src="${ctx}/image/<%= projectHistory.getProject().getLogo().getUrlName(45,45) %>" width="45" height="45" />
                </c:when>
                <c:when test="${!empty projectHistory.project.category.logo}">
                  <img alt="Default user photo"
                       src="${ctx}/image/<%= projectHistory.getProject().getCategory().getLogo().getUrlName(45,45) %>" width="45" height="45" class="default-photo" />
                </c:when>
              </c:choose>
            </c:when>
            <%-- use the user's logo --%>
            <c:when test="${!empty projectHistory.user.profileProject.logo}">
              <img alt="<c:out value="${projectHistory.user.profileProject.title}"/> photo"
                   src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getLogo().getUrlName(45,45) %>" width="45" height="45" />
            </c:when>
            <c:when test="${!empty projectHistory.user.profileProject.category.logo}">
              <img alt="Default user photo"
                   src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getCategory().getLogo().getUrlName(45,45) %>" width="45" height="45" class="default-photo" />
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
  <%-- Show info about twitter, if the capability is enabled --%>
  <c:if test='${!empty applicationPrefs.prefs["TWITTER_HASH"] && fn:contains(eventArrayList, "twitter")}'>
    <c:set var="projectId" value="<%= projectHistoryList.getProjectId() %>"/>
    <c:set var="projectCategoryId" value="<%= projectHistoryList.getProjectCategoryId() %>"/>
    <c:set var="isProfile" value="<%= project.getId() > -1 && project.getProfile() %>"/>
    <c:set var="userProfileProjectId" value="<%= User.getProfileProjectId() %>"/>
<%--
    Proj:<c:out value="${projectId}"/>
    Cat:<c:out value="${projectCategoryId}"/>
    IsUser:<c:out value="${isProfile}"/>
    UsedId:<c:out value="${userProfileProjectId}"/>
--%>
      <c:choose>
        <c:when test="${dashboardPortlet.cached}">
          <%-- Show simple cacheable message --%>
          <p>
            Post to <a href="http://twitter.com" target="_blank">Twitter</a> using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.<br />
            Be sure to login and link your profile to Twitter.
          </p>
        </c:when>
        <c:otherwise>
          <%-- Show personalized message --%>
          <c:choose>
            <%-- Users profile page --%>
            <c:when test="${isProfile eq 'true'}">
              <c:if test="${userProfileProjectId eq project.id}">
                <p>
                  Post to Twitter using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.
                  <c:if test="${empty user.profileProject.twitterId}">
                    <br />
                    <a href="javascript:showPanel('','${ctx}/show/${user.profileProject.uniqueId}/app/edit_profile','600')">Link your Twitter id</a> |
                    <a href="http://twitter.com" target="_blank">Need a Twitter account?</a>
                  </c:if>
                </p>
              </c:if>
            </c:when>
            <%-- Non-Users profile page and access to post --%>
            <c:when test="${projectId ne -1}">
              <ccp:permission if="all" name="project-profile-activity-add">
                <p>
                  Post to <a href="http://twitter.com" target="_blank">Twitter</a> using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.
                  <c:if test="${empty project.twitterId}">
                    <br />
                    <a href="javascript:showPanel('','${ctx}/show/${project.uniqueId}/app/edit_profile','600')">Link your Twitter id</a> |
                    <a href="http://twitter.com" target="_blank">Need a Twitter account?</a>
                  </c:if>
                </p>
              </ccp:permission>
            </c:when>
            <%-- Category page --%>
            <c:when test="${projectCategoryId ne -1}">
              <%--Category tab... TBD--%>
            </c:when>
            <%-- Home page --%>
            <c:otherwise>
              <p>
                Post to <a href="http://twitter.com" target="_blank">Twitter</a> using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.<br />
                <c:choose>
                  <c:when test="${user.profileProjectId eq -1}">
                    Login to link your Twitter id.
                  </c:when>
                  <c:when test="${empty user.profileProject.twitterId}">
                    <a href="javascript:showPanel('','${ctx}/show/${user.profileProject.uniqueId}/app/edit_profile','600')">Link your Twitter id</a>.
                  </c:when>
                </c:choose>
              </p>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
  </c:if>
</c:if>
<c:if test="${empty projectHistoryArrayList}">
  <p>There are no activities to report at this time.</p>
</c:if>
