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
<jsp:useBean id="usersArray" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="rolesArray" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="stateArray" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="activityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_assignments_list_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<body onLoad="resizeIframe()" bgcolor="#FFFFFF" LEFTMARGIN="0" MARGINWIDTH="0" TOPMARGIN="0" MARGINHEIGHT="0">
<script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/iframe.js"></script>
<script type="text/javascript">
  function resizeIframe() {
    parent.document.getElementById('server_list').height = getHeight("usersTable");
  }
  function removeUser(id) {
    parent.removeUser(id);
  }
</script>
<table id="usersTable" class="pagedList">
  <thead>
    <tr>
      <th>&nbsp;</th>
      <th><ccp:label name="projectsCenterAssignments.list.user">User</ccp:label></th>
      <ccp:evaluate if="<%= activityList.size() > 1 %>">
        <th><ccp:label name="projectsCenterAssignments.list.role">Role</ccp:label></th>
      </ccp:evaluate>
      <%--
      <th>Notes</th>
      --%>
    </tr>
  </thead>
  <tbody>
    <%
      if (usersArray.size() == 0) {
    %>
      <tr class="row2">
        <td colspan="3"><ccp:label name="projectsCenterAssignments.list.noUsersAssigned">No users assigned.</ccp:label></td>
      </tr>
    <%
      }
      int count = 0;
      int rowid = 0;
      for (int i = 0; i < usersArray.size(); i++) {
        ++count;
        String thisUser = (String) usersArray.get(i);
        rowid = (rowid != 1?1:2);
    %>
      <tr class="row<%= rowid %>">
        <td valign="top" nowrap>
          <%--
          <a href="javascript:removeUser(<%= thisUser %>);">remove</a>
          --%>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuListItem', <%= thisUser %>);"
             onMouseOver="over(0, <%= count %>)"
             onmouseout="out(0, <%= count %>); hideMenu('menuListItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0"></a>

        </td>
        <td width="100%" valign="top" nowrap>
          <ccp:username id="<%= thisUser %>" />
        </td>
        <ccp:evaluate if="<%= activityList.size() > 1 %>">
          <td width="50%" valign="top" nowrap>
            <% activityList.setJsEvent("onChange='parent.changeRole(this);'"); %>
            <%= activityList.getHtmlSelect("roleSelect" + thisUser, Integer.parseInt((String) rolesArray.get(i)))%>
          </td>
        </ccp:evaluate>
        <%--
        <td nowrap>
          <a href="javascript:popURL('<%= ctx %>/ProjectManagementAssignments.do?command=ShowNotes&pid=<%= "1" %>&aid=<%= "1" %>&uid=<%= "1" %>&popup=true','ITEAM_Assignment_Notes','400','500','yes','yes');"><ccp:evaluate if="<%= 1 == 2 %>"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absmiddle" alt="Review all notes"/></ccp:evaluate><ccp:evaluate if="<%= 1 == 1 %>"><img src="<%= ctx %>/images/icons/stock_insert-note-gray-16.gif" border="0" align="absmiddle" alt="Review all notes"/></ccp:evaluate></a>
          0
        </td>
        --%>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
</body>
