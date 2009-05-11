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
<%@ page import="com.concursive.commons.files.FileUtils" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="thisUser" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<jsp:useBean id="projects" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="adminUsersProjectsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="admin_users_projects_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<%-- Temp. fix for Weblogic --%>
<%
String detailsUrl = ctx + "AdminUserDetails.do?command=Details&id=" + thisUser.getId();
String projectsUrl = ctx + "AdminUserDetails.do?command=Projects&id=" + thisUser.getId();
String loginsUrl = ctx + "AdminUserDetails.do?command=Logins&id=" + thisUser.getId();
String languagesUrl = ctx + "AdminUserDetails.do?command=Languages&id=" + thisUser.getId();
String webSitesUrl = ctx + "AdminUserDetails.do?command=WebSites&id=" + thisUser.getId();
%>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminUsers.do">Manage Users</a> >
<a href="<%= ctx %>/AdminUsers.do?command=Search">Search Results</a> >
Project Details<br />
<br />
<table border="0" width="100%">
  <ccp:evaluate if="<%= !thisUser.getEnabled() %>">
  <tr>
    <td>
      <img src="<%= ctx %>/images/error.gif" border="0" align="absMiddle"/>
    </td>
    <td width="100%" colspan="2">
      This user is not allowed to login
    </td>
  </tr>
  </ccp:evaluate>
  <tr>
    <td>
      <img src="<%= ctx %>/images/icons/stock_new-bcard-16.gif" border="0" align="absMiddle"/>
    </td>
    <td width="100%">
      <strong><%= toHtml(thisUser.getNameFirstLast()) %></strong>
    </td>
    <td align="right" nowrap>
      (Type: <ccp:evaluate if="<%= thisUser.getAccessAdmin() %>">Administrator</ccp:evaluate><ccp:evaluate if="<%= !thisUser.getAccessAdmin() %>">User</ccp:evaluate>)
      &nbsp;
    </td>
  </tr>
</table>
<div class="tabs-te" id="toptabs">
<table cellpadding="4" cellspacing="0" border="0" width="100%">
  <tr>
    <ccp:tabbedMenu text="Details" key="details" value="projects" url="<%= detailsUrl %>"/>
    <ccp:tabbedMenu text="Projects" key="projects" value="projects" url="<%= projectsUrl %>"/>
    <ccp:tabbedMenu text="Logins" key="logins" value="projects" url="<%= loginsUrl %>"/>
    <ccp:tabbedMenu text="Languages" key="languages" value="projects" url="<%= languagesUrl %>"/>
    <ccp:tabbedMenu text="Web Sites" key="web-sites" value="projects" url="<%= webSitesUrl %>"/>
    <td width="100%" style="background-image: none; background-color: transparent; border: 0; border-bottom: 1px solid #666; cursor: default;">&nbsp;</td>
  </tr>
</table>
</div>
<table cellpadding="4" cellspacing="0" border="0" width="100%">
  <tr>
    <td class="containerBack">
<%-- begin container content --%>
<%-- Temp. fix for Weblogic --%>
<%
String actionError = showError(request, "actionError");
%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
  <tr>
    <form name="pagedListView" method="post" action="<%= ctx %>/AdminUsers.do?command=Projects&id=<%= thisUser.getId() %>">
    <td align="left">
      &nbsp;
    </td>
    <td>
      <ccp:pagedListStatus label="Projects" title="<%= actionError %>" object="adminUsersProjectsInfo"/>
    </td>
    </form>
  </tr>
</table>
<table class="pagedList">
  <thead>
    <tr>
      <th width="8">Action</th>
      <th nowrap><a href="<%= adminUsersProjectsInfo.addParameter(adminUsersProjectsInfo.getLink(), "column", "p.entered") %>">Start Date</a><%= adminUsersProjectsInfo.getSortIcon("p.entered") %></th>
      <th width="100%" nowrap><a href="<%= adminUsersProjectsInfo.addParameter(adminUsersProjectsInfo.getLink(), "column", "title") %>">Project Title</a><%= adminUsersProjectsInfo.getSortIcon("title") %></th>
      <th nowrap><a href="<%= adminUsersProjectsInfo.addParameter(adminUsersProjectsInfo.getLink(), "column", "closedate,approvaldate,title") %>">Status</a><%= adminUsersProjectsInfo.getSortIcon("closedate,approvaldate,title") %></th>
      <th nowrap>Role<%= adminUsersProjectsInfo.getSortIcon("role") %></th>
    </tr>
  </thead>
  <tbody>
    <%
      if (projects.size() == 0) {
    %>
      <tr class="row2">
        <td colspan="5">No projects to display.</td>
      </tr>
    <%
      }
      int rowid = 0;
      int count = 0;
      Iterator i = projects.iterator();
      while (i.hasNext()) {
        rowid = (rowid != 1?1:2);
        Project thisProject = (Project) i.next();
        ++count;
        TeamMember thisMember = thisProject.getTeam().getTeamMember(thisUser.getId());
        // Temp. fix for Weblogic
        //String roleName = "role" + thisMember.getUserId();
        //String roleValue = String.valueOf(thisMember.getUserLevel());
        //String roleOnChange = "javascript:updateRole(" + thisMember.getProjectId() + ", " + thisMember.getUserId() + ", this.options[this.selectedIndex].value);";
    %>
      <tr class="row<%= rowid %>">
        <td valign="top" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuItem', <%= thisProject.getId() %>);"
             onMouseOver="over(0, <%= count %>)"
             onmouseout="out(0, <%= count %>); hideMenu('menuItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0"></a>
        </td>
        <td valign="top" align="center" nowrap>
          <ccp:tz timestamp="<%= thisProject.getRequestDate() %>" dateOnly="true" default="&nbsp;"/>
        </td>
        <td valign="top">
          <%= toHtml(thisProject.getTitle()) %>
        </td>
        <td valign="top" nowrap>
          <ccp:evaluate if="<%= thisProject.getClosed() %>">
            <font color="blue">Closed</font>
          </ccp:evaluate>
          <ccp:evaluate if="<%= !thisProject.getClosed() %>">
            <ccp:evaluate if="<%= thisProject.getApprovalDate() == null %>">
              <font color="red">Under review</font>
            </ccp:evaluate>
            <ccp:evaluate if="<%= thisProject.getApprovalDate() != null %>">
              <font color="darkgreen">Open</font>
            </ccp:evaluate>
          </ccp:evaluate>
        </td>
        <td valign="top" nowrap>
          <ccp:role id="<%= thisMember.getUserLevel() %>"/>
          <%--
          <ccp:roleSelect
              name="<%= roleName %>"
              value="<%= roleValue %>"
              onChange="<%= roleOnChange %>"/>
          --%>
        </td>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
<ccp:pagedListControl object="adminUsersProjectsInfo"/>
<%-- end container content --%>
    </td>
  </tr>
</table>
