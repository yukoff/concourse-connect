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
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page
    import="com.concursive.connect.web.modules.discussion.dao.Forum" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="forumList" class="com.concursive.connect.web.modules.discussion.dao.ForumList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_issues_categories_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<h1><ccp:tabLabel name="Discussion" object="project"/></h1>
<ccp:evaluate if="<%= !User.isLoggedIn() && project.getFeatures().getAllowGuests() %>">
  <p class="portlet-message-info"><ccp:label name="projectsCenterIssues.categories.neddLogin">You need to be logged in to post messages.</ccp:label></p>
</ccp:evaluate>
<c:if test="${empty forumList}">
  <p>There are currently no forums setup for posting messages.</p>
</c:if>
<c:if test="${!empty forumList}">
  <ol>
    <%
      int count = 0;
      int rowid = 0;
      Iterator i = forumList.iterator();
      while (i.hasNext()) {
        ++count;
        rowid = (rowid != 1?1:2);
        Forum thisCategory = (Forum) i.next();
        request.setAttribute("thisCategory", thisCategory);
    %>
    <li>
      <h2>
        <portlet:renderURL var="detailsUrl">
          <portlet:param name="portlet-action" value="show"/>
          <portlet:param name="portlet-object" value="forum"/>
          <portlet:param name="portlet-value" value="${thisCategory.id}"/>
        </portlet:renderURL>
        <portlet:renderURL var="modifyForumUrl">
          <portlet:param name="portlet-action" value="modify"/>
          <portlet:param name="portlet-object" value="forum"/>
          <portlet:param name="portlet-value" value="${thisCategory.id}"/>
        </portlet:renderURL>
        <portlet:actionURL var="deleteForumUrl">
          <portlet:param name="portlet-command" value="forum-delete"/>
          <portlet:param name="forum" value="${thisCategory.id}"/>
        </portlet:actionURL>
        <a href="${detailsUrl}"><%= toHtml(thisCategory.getSubject()) %></a>
        <span>(<%= ((thisCategory.getPostsCount()==0) ? "0" : "" + thisCategory.getPostsCount()) %> posts)</span>
        <ccp:permission name="project-discussion-forums-edit,project-discussion-forums-delete" if="any">
          <span>
            <ccp:permission name="project-discussion-forums-edit">
              <a href="${modifyForumUrl}">modify</a>
            </ccp:permission>
            <ccp:permission name="project-discussion-forums-delete">
              <a href="${deleteForumUrl}">delete</a>
            </ccp:permission>
          </span>
        </ccp:permission>
      </h2>
      <p>
        <ccp:evaluate if="<%= thisCategory.getPostsCount() > 0 %>">
          Last post by <ccp:username id="<%= thisCategory.getLastPostBy() %>"/> on <%= thisCategory.getLastPostDateTimeString()%>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisCategory.getPostsCount() == 0 %>">
          No posts yet.
        </ccp:evaluate>
      </p>
    </li>
  <%
    }
  %>
  </ol>
  <div class="pagination">
    <ccp:paginationControl object="projectForumInfo"/>
  </div>
</c:if>
