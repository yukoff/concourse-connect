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
<%@ page import="com.concursive.connect.web.modules.lists.dao.TaskCategory" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.lists.dao.TaskCategoryList" scope="request"/>
<jsp:useBean id="Task" class="com.concursive.connect.web.modules.lists.dao.Task" scope="request"/>
<%@ include file="initPage.jsp" %>
<table cellpadding="0" cellspacing="0" width="100%" border="0">
<tr>
  <td>
    <ccp:label name="projectsCenterLists.hierarchy.moveMessage">Select a list to move the item to:</ccp:label><br />
    <img border="0" src="<%= ctx %>/images/box<%= Task.getComplete()?"-checked":"" %>.gif" alt="" align="absmiddle" />
    <%= toHtml(Task.getDescription()) %>
  </td>
</tr>
</table>
&nbsp;<br />
<table cellpadding="0" cellspacing="0" width="100%" border="1" rules="cols">
  <tr class="section">
    <td>
      <strong><ccp:label name="projectsCenterLists.hierarchy.lists">Lists</ccp:label></strong>
    </td>
  </tr>
<%
  int rowid = 0;
  Iterator i = categoryList.iterator();
  while (i.hasNext()) {
    rowid = (rowid != 1?1:2);
    TaskCategory thisCategory = (TaskCategory) i.next();
%>
  <tr class="row<%= rowid %>">
    <td valign="top">
      <img border="0" src="<%= ctx %>/images/icons/stock_list_enum-16.gif" align="absmiddle" />
      <a href="<%= ctx %>/ProjectManagementLists.do?command=SaveMove&pid=<%= project.getId() %>&cid=<%= thisCategory.getId() %>&id=<%= Task.getId() %>&popup=true"><%= toHtml(thisCategory.getDescription()) %></a>
      <ccp:evaluate if="<%= Task.getCategoryId() == thisCategory.getId() %>">
        <ccp:label name="projectsCenterLists.hierarchy.currentFolder">(current folder)</ccp:label>
      </ccp:evaluate>
    </td>
  </tr>
<%
  }
%>
</table>
<br />
<input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.close()">
