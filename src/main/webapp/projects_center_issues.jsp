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
<%@ page import="com.concursive.connect.web.modules.discussion.dao.Topic" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="topicList" class="com.concursive.connect.web.modules.discussion.dao.TopicList" scope="request"/>
<jsp:useBean id="forum" class="com.concursive.connect.web.modules.discussion.dao.Forum" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_issues_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<portlet:renderURL var="discussionUrl">
  <portlet:param name="portlet-action" value="show"/>
  <portlet:param name="portlet-object" value="discussion"/>
</portlet:renderURL>
<h1>
  <%= toHtml(forum.getSubject()) %>
</h1>
<p><a href="${discussionUrl}">Back to <ccp:label name="projectsCenterIssues.forums">Forums</ccp:label></a></p>
<ccp:evaluate if="<%= !User.isLoggedIn() && project.getFeatures().getAllowGuests() %>">
  <p><ccp:label name="projectsCenterIssues.needLogin">You need to be logged in to post messages.</ccp:label></p>
</ccp:evaluate>
<%--
  <ccp:label name="projectsCenterIssues.action">Action</ccp:label>
  <ccp:label name="projectsCenterIssues.topic">Topic</ccp:label>
  <ccp:label name="projectsCenterIssues.lastPost">Last Post</ccp:label>
  <ccp:label name="projectsCenterIssues.replies">Replies</ccp:label>
  <ccp:label name="projectsCenterIssues.views">Views</ccp:label>
  <ccp:label name="projectsCenterIssues.author">Author</ccp:label>
  </tr>
--%>
<%
  if (topicList.size() == 0) {
%>
  <p class="portlet-message-info">No messages to display.</p>
<%
  }
  int count = 0;
  int rowid = 0;
  Iterator i = topicList.iterator();
  while (i.hasNext()) {
    ++count;
    rowid = (rowid != 1?1:2);
    Topic thisTopic = (Topic) i.next();
    request.setAttribute("thisIssue", thisTopic);
%>
  <dl>
<%--
  <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuItem', <%= thisIssue.getId() %>, <%= forum.getId() %>);"
     onMouseOver="over(0, <%= count %>)"
     onmouseout="out(0, <%= count %>); hideMenu('menuItem');">
    <img src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0">
  </a>
--%>
    <dt>
      <ccp:evaluate if="<%= thisTopic.getQuestion() %>">
        <ccp:evaluate if="<%= thisTopic.getSolutionReplyId() != -1 %>">
          <img border="0" src="<%= ctx %>/images/icons/stock_help-16.png" align="absmiddle">&nbsp;
          <img border="0" src="<%= ctx %>/images/icons/stock_calc-accept-16.gif" align="absmiddle">
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisTopic.getSolutionReplyId() == -1 %>">
          <img border="0" src="<%= ctx %>/images/icons/stock_help-16.png" align="absmiddle">
        </ccp:evaluate>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !thisTopic.getQuestion() %>">
        <img border="0" src="<%= ctx %>/images/icons/discussion_16x16.png" align="absmiddle">
      </ccp:evaluate>
      <portlet:renderURL var="detailsUrl">
        <portlet:param name="portlet-action" value="show"/>
        <portlet:param name="portlet-object" value="topic"/>
        <portlet:param name="portlet-value" value="${thisIssue.id}"/>
        <portlet:param name="resetList" value="true"/>
      </portlet:renderURL>
      <a href="${detailsUrl}"><%= toHtml(thisTopic.getSubject()) %></a>
      <c:if test="<%= thisTopic.getReplyCount() > 0 || thisTopic.getReadCount() > 0 %>">
        <span>
          <c:choose>
            <c:when test="<%= thisTopic.getReplyCount() == 0 %>">
              (no replies and
            </c:when>
            <c:when test="<%= thisTopic.getReplyCount() == 1 %>">
              (1 reply and
            </c:when>
            <c:otherwise>
              (<%= thisTopic.getReplyCount() %> replies and
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="<%= thisTopic.getReadCount() == 0 %>">
              no views)
            </c:when>
            <c:when test="<%= thisTopic.getReadCount() == 1 %>">
              1 view)
            </c:when>
            <c:otherwise>
              <%= thisTopic.getReadCount() %> views)
            </c:otherwise>
          </c:choose>
        </span>
      </c:if>
    </dt>
    <c:choose>
      <c:when test="<%= thisTopic.getReplyCount() > 0 %>">
        <dd>
          Last reply <ccp:tz timestamp="<%= thisTopic.getReplyDate() %>" pattern="relative" />
          by <ccp:username id="<%= thisTopic.getReplyBy() %>"/>
        </dd>
      </c:when>
      <c:otherwise>
        <dd>
          by <ccp:username id="<%= thisTopic.getEnteredBy() %>"/>
        </dd>
      </c:otherwise>
    </c:choose>
  <%
    }
  %>
</dl>
<ccp:paginationControl object="projectTopicInfo"/>
