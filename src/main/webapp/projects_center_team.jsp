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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://packtag.sf.net" prefix="pack" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="teamMemberMap" class="java.util.LinkedHashMap" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="STATUS_ADDED"><%= TeamMember.STATUS_ADDED %></c:set>
<c:set var="STATUS_PENDING"><%= TeamMember.STATUS_PENDING %></c:set>
<c:set var="STATUS_INVITING"><%= TeamMember.STATUS_INVITING %></c:set>
<c:set var="STATUS_MAILERROR"><%= TeamMember.STATUS_MAILERROR %></c:set>
<c:set var="STATUS_REFUSED"><%= TeamMember.STATUS_REFUSED %></c:set>
<c:set var="STATUS_JOINED_NEEDS_APPROVAL"><%= TeamMember.STATUS_JOINED_NEEDS_APPROVAL %></c:set>
<%-- user specific variables --%>
<c:set var="userId"><%= User.getId() %></c:set>
<c:set var="addedFlag"><%= TeamMember.STATUS_ADDED %></c:set>
<c:set var="inviteFlag"><%= TeamMember.STATUS_INVITING %></c:set>
<c:set var="canJoinFlag"><%= project.getFeatures().getAllowParticipants() && !project.getFeatures().getMembershipRequired() && !project.getTeam().hasUserId(User.getId()) %></c:set>
<%-- Initialize the drop-down menus --%>
<c:if test="${!project.profile}">
  <c:choose>
    <c:when test="${project.owner > -1}">
      <ccp:permission name="project-details-edit">
        <c:set var="canUnsetOwner" value="${true}" scope="request"/>
      </ccp:permission>
      <c:set var="ownerId" scope="request" value="${project.owner}"/>
    </c:when>
    <c:otherwise>
      <ccp:permission name="project-details-edit">
        <c:set var="canSetOwner" value="${true}" scope="request"/>
      </ccp:permission>
    </c:otherwise>
  </c:choose>
</c:if>
<%@ include file="projects_center_team_menu.jspf" %>
<%-- Initialize the tooltip --%>
<pack:script>
  <src>/javascript/projects_center_team_list.js</src>
</pack:script>
  <h1><ccp:tabLabel name="Team" object="project"/></h1>
  <c:choose>
    <%-- if user logged in and is not a member or has been invited but not joined display join link --%>
    <c:when test="${userId > 0  && canJoinFlag && (currentMember.id == -1 || (currentMember.status != addedFlag && currentMember.status != inviteFlag))}">
        <a rel="shadowbox" href="<%= ctx %>/ProjectManagementTeam.do?command=ConfirmJoin&pid=<%= project.getId() %>"><ccp:label name="user.joinTeam">Become a member</ccp:label></a>
    </c:when>
    <c:when test="${!project.profile && currentMember.id > -1 && currentMember.status == addedFlag}">
        <a href="<%= ctx%>/ProjectManagementTeam.do?command=Leave&pid=<%= project.getId()%>">Remove yourself from <c:out value="${project.title}"/></a>
    </c:when>
  </c:choose>
  <c:if test="${userId < 0 && canJoinFlag}">
    <div class="portlet-msg-alert">
      <ccp:label name="user.loginToJoin">Please sign in to become a member.</ccp:label>
    </div>
  </c:if>
  <%-- Default the view to big images until changed to small images --%>
  <c:set var="outputItems" value="false"/>
  <c:set var="listClass" value="dList"/>
  <%-- For each Role, Display projectCenterTeamRole layer --%>
  <c:forEach items="${teamMemberMap}" var="role" varStatus="roleCounter">
    <c:if test="${!empty role.value}">
      <c:set var="outputItems" value="true"/>
      <c:if test="${fn:length(role.value) > 8}">
        <c:set var="listClass" value="sList"/>
      </c:if>
        <div class="portlet-section-header">
          <h3><c:out value="${role.key.description}"/></h3>
        </div>
        <div class="portlet-section-body">
        <ol id="role_${role.key.id}" class="${listClass}">
          <c:forEach items="${role.value}" var="teamMember" varStatus="teamMemberCounter">
            <%--@elvariable id="teamMember" type="com.concursive.connect.web.modules.members.dao.TeamMember"--%>
            <c:set var="profileProject" value="${teamMember.user.profileProject}" scope="request" />
            <jsp:useBean id="profileProject" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
            <li id="teamMember_${teamMember.id}">
                <%-- the handle is used for dragging the item
                <span id="teamMemberHandle_${teamMember.id}" class="projectCenterTeamMembersHandle">handle</span>
                --%>
                <c:if test="${profileProject.id > 0}"><a href="${ctx}/show/${profileProject.uniqueId}"></c:if>
                  <%-- If no photo, show no photo text --%>
                  <c:if test="${empty profileProject.logo}">
                    <div class="pContainer">
                      <div class="profileImageBackground">
                        <c:choose>
                          <c:when test="${!empty profileProject.category.logo}">
                            <img src="${ctx}/image/<%= profileProject.getCategory().getLogo().getUrlName(100,100) %>" width="100" height="100" class="default-photo" />
                          </c:when>
                          <c:otherwise>
                            <div class="noPhoto">
                              <c:if test="${listClass eq 'dList'}">
                                <p><span>no image</span></p>
                              </c:if>
                            </div>
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </div>
                  </c:if>
                  <%-- show the main image --%>
                  <c:if test="${!empty profileProject.logo}">
                    <div class="profileImageContainer">
                      <div class="profileImageBackground">
                        <div class="profileImageContainer">
                      		<img src="<%= ctx %>/show/${profileProject.uniqueId}/image/<%= profileProject.getLogo().getUrlName(100,100) %>" width="100" height="100" />
                        </div>
                      </div>
                    </div>
                  </c:if>
                  <div class="portlet-section-footer">
                    <div id="tooltip_${teamMember.id}" class="toolTip">
                      <p><ccp:username id="${teamMember.userId}" idTag="teamMemberLink_${teamMember.id}" showLinkTitle="false"/></p>
                      <c:if test="${teamMember.status == STATUS_JOINED_NEEDS_APPROVAL}"><p>(Needs Approval)</p></c:if>
                      <c:if test="${teamMember.status == STATUS_PENDING}"><p>(Invitation Pending)</p></c:if>
                      <c:if test="${teamMember.userId == project.owner}"><p>(Profile Owner)</p></c:if>
                    </div>
                    <c:if test="${profileProject.id > 0}"></a></c:if>
                    <%-- prepare the drop-down menu to this specific user --%>
                    <ccp:permission name="project-team-edit">
                      <c:set var="count">${roleCounter.count}${teamMemberCounter.count}</c:set>
                      <c:choose>
                        <c:when test="${teamMember.status == STATUS_MAILERROR ||
                                    teamMember.status == STATUS_PENDING ||
                                    teamMember.status == STATUS_INVITING ||
                                    teamMember.status == STATUS_REFUSED}">
                          <c:set var="showResendInvitation">true</c:set>
                        </c:when>
                        <c:otherwise>
                          <c:set var="showResendInvitation">false</c:set>
                        </c:otherwise>
                      </c:choose>
                      <c:choose>
                        <c:when test="${teamMember.status == STATUS_JOINED_NEEDS_APPROVAL}">
                          <c:set var="needsApproval">true</c:set>
                        </c:when>
                        <c:otherwise>
                          <c:set var="needsApproval">false</c:set>
                        </c:otherwise>
                      </c:choose>
                    </ccp:permission>
                </div>
				<ccp:permission name="project-team-edit">
                <div class="portlet-menu">
                  <div id="select_${count}">
                    <a href="javascript:displayMenu('select_${count}',${teamMember.userId},'${profileProject.uniqueId}',${showResendInvitation},${needsApproval},${teamMember.tools},${ownerId == teamMember.userId});">edit</a>
                  </div>
                </div>
               </ccp:permission>
            </li>
          </c:forEach>
        </ol>
      </div>
    </c:if>
  </c:forEach>
  <c:if test="${outputItems eq 'false'}">
    <p>There are currently no users added.</p>
  </c:if>
<div id="projectCenterTeamMemberToolTip" class="projectCenterTeamMemberToolTip"></div>
