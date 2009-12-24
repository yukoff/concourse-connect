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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/><jsp:useBean id="register" class="com.concursive.connect.web.modules.register.beans.RegisterBean" scope="request"/><%@ include file="initPage.jsp" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request" />
<div class="portletWrapper projectAskToBecomeMemberContainer">
  <div class="formContainer">
    <form name="join" method="post" action="<%= ctx %>/ProjectManagementTeam.do?command=AskToBecomeMember&pid=${project.id}">
      <c:if test="${'true' eq param.popup || 'true' eq popup}">
        <input type="hidden" name="popup" value="true" />
      </c:if>
      <%= showError(request, "actionError", false) %>
      <fieldset>
        <legend><c:out value="${project.title}"/></legend>
        <c:if test="${!project.profile && !project.features.allowGuests}">
          <div class="portlet-message-status">
            <p>This profile requires approval from a manager before you can view content or collaborate.</p>
          </div>
        </c:if>
        <div class="detailed-list">
          <ol>
            <li>
              <c:choose>
                <c:when test="${!empty project.logo}">
                  <img alt="<c:out value="${project.title}"/> photo" width="45" height="45" src="${ctx}/image/<%= project.getLogo().getUrlName(45,45) %>" />
                </c:when>
                <c:when test="${!empty project.category.logo}">
                  <img alt="Default photo" width="45" height="45" src="${ctx}/image/<%= project.getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
                </c:when>
              </c:choose>
              <h3><c:out value="${project.title}"/></h3>
              <c:if test="${!empty project.shortDescription}">
                <p><c:out value="${project.shortDescription}"/></p>
              </c:if>
              <c:if test="${!empty project.location}">
                <address><c:out value="${project.location}"/></address>
              </c:if>
            </li>
          </ol>
        </div>
      </fieldset>
      <fieldset id="Join">
        <legend><ccp:label name="project.join">Join</ccp:label></legend>
          <div>
            <c:choose>
              <c:when test="${project.profile}">
                <p>
                  Once you are a friend, you can stay informed about <strong><c:out value="${project.title}"/></strong> by choosing one or more of the following email options.
                  You can adjust your notifications later from your 'Me' page. Your choice will not become active unless you are approved.
                </p>
                <label for="notification" style="display:inline">Do you want to opt-in to receive broadcast email messages from this friend?</label><br />
              </c:when>
              <c:otherwise>
                <p>
                  Once you are a member, you can stay informed about <strong><c:out value="${project.title}"/></strong> by choosing one or more of the following email options.
                  You can adjust your notifications later from your 'Me' page. Your choice will not become active unless you are approved.
                </p>
                <label for="notification" style="display:inline">Do you want to opt-in to receive broadcast email messages from the members of this profile?</label><br />
              </c:otherwise>
            </c:choose>
            <input type="radio" name="notification" id="notification" value="yes" checked="true">Yes
            <input type="radio" name="notification" id="notification" value="no">No <br /><br />
            <label for="notification" style="display:inline">Do you want to opt-in to receive an automated email digest of activity for this profile? Choose how often would you like to receive the updates:</label><br />
            <input type="radio" name="emailNotification" id="emailNotification" value="<%= TeamMember.EMAIL_OFTEN %>"> Often (every few hours)
            <input type="radio" name="emailNotification" id="emailNotification" value="<%= TeamMember.EMAIL_DAILY %>" checked="true"> Daily
            <input type="radio" name="emailNotification" id="emailNotification" value="<%= TeamMember.EMAIL_WEEKLY %>"> Weekly
            <input type="radio" name="emailNotification" id="emailNotification" value="<%= TeamMember.EMAIL_MONTHLY %>"> Monthly
            <input type="radio" name="emailNotification" id="emailNotification" value="<%= TeamMember.EMAIL_NEVER %>"> Never
          </div>
      </fieldset>  
      <c:choose>
        <c:when test="${project.profile}">
          <p><c:out value="${project.title}"/> will confirm that you are friends.</p>
          <input type="submit" class="submit" value="Add as Friend" />
        </c:when>
        <c:otherwise>
          <p>If you decide to join, you will be notified by email once your request has been approved or denied.</p>
          <input type="submit" class="submit" value="Ask to Join" />
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${'true' eq param.popup || 'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
        </c:when>
        <c:otherwise>
          <a href="${ctx}/${fn:toLowerCase(project.category.description)}.shtml" class="cancel">Cancel</a>
        </c:otherwise>
      </c:choose>
    </form>
  </div>
</div>
