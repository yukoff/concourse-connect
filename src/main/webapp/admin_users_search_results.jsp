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
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="userList" class="com.concursive.connect.web.modules.login.dao.UserList" scope="request"/>
<jsp:useBean id="adminUserListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="admin_users_search_results_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<%-- begin page --%>
<div class="adminContainer">
  <div class="adminHeaderContainer">
    <h1>Search Results</h1>
    <p>Back to <a href="<%= ctx %>/admin" title="System Administration">System Administration</a> > <a href="<%= ctx %>/AdminUsers.do" title="Manage Users">Manage Users</a></p>
  </div>
  <div class="adminBodyContainer">
    <div class="adminUsersSearchResultsContainer">
      <%-- Temp. fix for Weblogic --%>
  <%
  String actionError = showError(request, "actionError");
  %>
      <div>
        <ccp:pagedListStatus label="Users" title="<%= actionError %>" object="adminUserListInfo"/>
      </div>
      <table class="pagedList">
        <thead>
          <tr>
            <th width="8">Action</th>
            <th nowrap>Name / Organization</th>
            <th nowrap>Email Address</th>
            <th nowrap>Last Login</th>
            <th nowrap>Enabled</th>
          </tr>
        </thead>
        <tbody>
            <ccp:evaluate if="<%= userList.size() == 0 %>">
            <tr class="row2">
              <td colspan="5">No users to display.</td>
            </tr>
            </ccp:evaluate>
          <%
            int count = 0;
            int rowid = 0;
            Iterator i = userList.iterator();
            while (i.hasNext()) {
              ++count;
              rowid = (rowid != 1?1:2);
              User thisUser = (User) i.next();
              request.setAttribute("thisUser", thisUser);
          %>
            <tr class="row<%= rowid %>">
              <td valign="top" nowrap>
                <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuListItem', <%= thisUser.getId() %>);"
                   onMouseOver="over(0, <%= count %>)"
                   onmouseout="out(0, <%= count %>); hideMenu('menuListItem');"><img
                   src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0"></a>
              </td>
              <td valign="top" nowrap>
                <ccp:username id="${thisUser.id}" showProfile="true" showPresence="true" showCityState="true" />
                (<a href="${ctx}/AdminUserDetails.do?command=Modify&id=${thisUser.id}&resetList=true">edit</a>)<br />
                <%= toHtml(thisUser.getCompany()) %>
              </td>
              <td valign="top" nowrap>
                <%= toHtml(thisUser.getEmail()) %>
              </td>
              <td valign="top" nowrap>
                <ccp:tz timestamp="<%= thisUser.getLastLogin() %>" default="&nbsp;"/>
              </td>
              <td valign="top">
                <%= thisUser.getEnabled() %>
              </td>
            </tr>
          <%}%>
        </tbody>
      </table>
    </div>
  </div>
  <div class="pagination">
    <ccp:paginationControl object="adminUserListInfo"/>
  </div>
</div>
