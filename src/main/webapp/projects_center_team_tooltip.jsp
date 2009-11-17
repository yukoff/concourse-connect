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
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="teamMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<c:set var="profileProject" value="${teamMember.user.profileProject}" scope="request" />
<jsp:useBean id="profileProject" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="STATUS_ADDED"><%= TeamMember.STATUS_ADDED %></c:set>
<c:set var="STATUS_JOINED"><%= TeamMember.STATUS_JOINED %></c:set>
<c:set var="STATUS_PENDING"><%= TeamMember.STATUS_PENDING %></c:set>
<c:set var="STATUS_INVITING"><%= TeamMember.STATUS_INVITING %></c:set>
<c:set var="STATUS_MAILERROR"><%= TeamMember.STATUS_MAILERROR %></c:set>
<c:set var="STATUS_REFUSED"><%= TeamMember.STATUS_REFUSED %></c:set>
<c:set var="STATUS_JOINED_NEEDS_APPROVAL"><%= TeamMember.STATUS_JOINED_NEEDS_APPROVAL %></c:set>
<div class="portletWrapper projectsCenterTeamTooltip">
  <h2><ccp:label name="projectsCenterTeam.tooltip.status">Status</ccp:label></h2>
  <div class="leftColumn">
    <div class="profileImageContainer">
      <div class="profileImageBackground">
        <div class="profileImage">
          <%-- If no photo, show no photo text --%>
          <c:if test="${empty profileProject.logo}">
            <c:choose>
              <c:when test="${!empty profileProject.category.logo}">
                <img src="${ctx}/image/<%= profileProject.getCategory().getLogo().getUrlName(50,50) %>" width="50" height="50" class="default-photo" />
              </c:when>
              <c:otherwise>
                <div class="noPhoto"><p><span>no image</span></p></div>
              </c:otherwise>
            </c:choose>
          </c:if>
          <%-- show the main image --%>
          <c:if test="${!empty profileProject.logo}">
            <img src="${ctx}/show/${profileProject.uniqueId}/image/<%= profileProject.getLogo().getUrlName(50,50) %>" width="50" height="50" />
          </c:if>
        </div>
      </div>
    </div>
  </div>
  <div class="rightColumn">
    <c:if test="${teamMember.status == STATUS_ADDED}">
      <p><ccp:username id="${teamMember.userId}"/> was added on <ccp:tz timestamp="${teamMember.modified}" dateOnly="true"/>.</p>
    </c:if>
    <c:if test="${teamMember.status == STATUS_JOINED}">
      <p><ccp:username id="${teamMember.userId}"/> joined on <ccp:tz timestamp="${teamMember.modified}" dateOnly="true"/>.</p>
    </c:if>
    <c:if test="${teamMember.status == STATUS_PENDING}">
      <p><ccp:username id="${teamMember.userId}"/> was invited on <ccp:tz timestamp="${teamMember.modified}" dateOnly="true"/>.</p>
    </c:if>
    <c:if test="${teamMember.status == STATUS_INVITING}">
      <p><ccp:username id="${teamMember.userId}"/> was invited on <ccp:tz timestamp="${teamMember.modified}" dateOnly="true"/>.</p>
    </c:if>
    <c:if test="${teamMember.status == STATUS_MAILERROR}">
      <p><ccp:username id="${teamMember.userId}"/> was invited on <ccp:tz timestamp="${teamMember.modified}" dateOnly="true"/>,
      but the invitation could not be sent to the email address he or she registered with.</p>
    </c:if>
    <c:if test="${teamMember.status == STATUS_REFUSED}">
      <p><ccp:username id="${teamMember.userId}"/> did not accept the invitation that was sent on
      <ccp:tz timestamp="${teamMember.modified}" dateOnly="true"/>.</p>
    </c:if>
    <c:if test="${teamMember.status == STATUS_JOINED_NEEDS_APPROVAL}">
      <p><ccp:username id="${teamMember.userId}"/> has chosen to join, but a manager needs to approve or deny the request.</p>
    </c:if>
    <c:if test="${empty teamMember.lastAccessed}">
      <p>This member has not accessed <c:out value="${project.title}"/>.</p>
    </c:if>
    <c:if test="${!empty teamMember.lastAccessed}">
      <p>This member last accessed <c:out value="${project.title}"/> on <ccp:tz timestamp="${teamMember.lastAccessed}" dateOnly="true" default="--"/>.</p>
    </c:if>
  </div>
</div>
