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
<%@ page import="com.concursive.connect.web.modules.plans.dao.AssignmentFolder" %>
<%@ page import="com.concursive.connect.web.modules.plans.dao.Requirement" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="requirement" class="com.concursive.connect.web.modules.plans.dao.Requirement" scope="request"/>
<jsp:useBean id="assignment" class="com.concursive.connect.web.modules.plans.dao.Assignment" scope="request"/>
<%@ include file="initPage.jsp" %>
<table cellpadding="0" cellspacing="0" width="100%" border="0">
<tr>
  <td>
    <ccp:label name="projectsCenterHierarchy.message">Select a folder to move the item to:</ccp:label><br>
    <%= assignment.getStatusGraphicTag(ctx) %>
    <%= toHtml(assignment.getRole()) %>
  </td>
</tr>
</table>
<br />
<table cellpadding="0" cellspacing="0" width="100%" border="1" rules="cols">
<%
Requirement thisRequirement = requirement;
%>
  <tr class="section">
    <td valign="top" width="100%">
      <img alt="" src="<%= ctx %>/images/tree7o.gif" border="0" align="absmiddle" height="16" width="19"/>
      <img alt="" src="<%= ctx %>/images/folder1open.gif" border="0" align="absmiddle" height="16" width="19"/>
      <a href="<%= ctx %>/ProjectManagementAssignments.do?command=SaveMove&pid=<%= project.getId() %>&rid=<%= requirement.getId() %>&aid=<%= assignment.getId() %>&popup=true&parent=0&return=ProjectAssignments&param=<%= project.getId() %>&param2=<%= requirement.getId() %>"><%= toHtml(thisRequirement.getShortDescription()) %></a>
    </td>
  </tr>
<%
    AssignmentFolder plan = thisRequirement.getPlan();
    Iterator planIterator = plan.getPlanIterator();
    HashMap treeStatus = new HashMap();
    int rowid = 0;
    while (planIterator.hasNext()) {
      rowid = (rowid != 1?1:2);
      Object planItem = (Object) planIterator.next();
      if (planItem instanceof AssignmentFolder) {
        //AssignmentFolders
        AssignmentFolder thisFolder = (AssignmentFolder) planItem;
%>
  <tr class="row<%= rowid %>">
    <td valign="top">
<%
      treeStatus.put(new Integer(0), new Boolean(false));
      treeStatus.put(new Integer(thisFolder.getDisplayLevel()), new Boolean(thisFolder.getLevelOpen()));
      for (int count = 0; count < thisFolder.getDisplayLevel() + 1; count++) {
        boolean folderOpen = ((Boolean) treeStatus.get(new Integer(count))).booleanValue();
%>
    <ccp:evaluate if="<%= folderOpen && count != thisFolder.getDisplayLevel() %>">
      <img border="0" src="<%= ctx %>/images/tree2.gif" align="absmiddle" height="16" width="19">
    </ccp:evaluate>
    <ccp:evaluate if="<%= folderOpen && count == thisFolder.getDisplayLevel() %>">
      <img border="0" src="<%= ctx %>/images/tree5o.gif" align="absmiddle" height="16" width="19">
    </ccp:evaluate>
    <ccp:evaluate if="<%= !folderOpen && count == thisFolder.getDisplayLevel() %>">
      <img border="0" src="<%= ctx %>/images/tree5o.gif" align="absmiddle" height="16" width="19">
    </ccp:evaluate>
    <ccp:evaluate if="<%= !folderOpen && count != thisFolder.getDisplayLevel() %>">
      <img border="0" src="<%= ctx %>/images/treespace.gif" align="absmiddle" height="16" width="19">
    </ccp:evaluate>
<%    }   %>
      <img border="0" src="<%= ctx %>/images/folder1open.gif" align="absmiddle" align="absmiddle" height="16" width="19">
      <a href="<%= ctx %>/ProjectManagementAssignments.do?command=SaveMove&pid=<%= project.getId() %>&rid=<%= requirement.getId() %>&aid=<%= assignment.getId() %>&popup=true&parent=<%= thisFolder.getId() %>&return=ProjectAssignments&param=<%= project.getId() %>&param2=<%= requirement.getId() %>"><%= toHtml(thisFolder.getName()) %></a>
      <ccp:evaluate if="<%= assignment.getFolderId() == thisFolder.getId() %>">
      <ccp:label name="projectsCenterHierarchy.currentFolder">(current folder)</ccp:label>
      </ccp:evaluate>
    </td>
  </tr>
<%
      }
  }
%>
</table>
<br />
<input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.close()">
