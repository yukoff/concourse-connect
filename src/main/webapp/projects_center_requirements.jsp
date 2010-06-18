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
<%@ page import="com.concursive.connect.web.modules.plans.dao.AssignedUser" %>
<%@ page import="com.concursive.connect.web.modules.plans.dao.Assignment" %>
<%@ page import="com.concursive.connect.web.modules.plans.dao.Requirement" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="projectRequirementsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="requirements" class="com.concursive.connect.web.modules.plans.dao.RequirementList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_requirements_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<SCRIPT LANGUAGE="JavaScript" type="text/javascript" src="<%= ctx %>/javascript/popURL.js"></script>
<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" src="<%= ctx %>/javascript/confirmDelete.js"></SCRIPT>
<script type="text/javascript">
  function showTemplates() {
    popURL('<%= ctx %>/ProjectManagementRequirements.do?command=Templates&pid=<%= project.getId() %>&popup=true','550','520','yes','yes');
  }
</script>
<ccp:permission name="project-plan-outline-add">
  <img border="0" src="<%= ctx %>/images/icons/stock_new_bullet-16.gif" align="absmiddle">
  <a href="<%= ctx %>/ProjectManagementRequirements.do?command=Add&pid=<%= project.getId() %>"><ccp:label name="projectsCenterRequirements.newOutline">New Outline</ccp:label></a> |
  <a href="javascript:showTemplates()"><ccp:label name="projectsCenterRequirements.templates">Templates</ccp:label></a>
  <br />
  <br />
</ccp:permission>
<%-- Temp. fix for Weblogic --%>
<%
String actionError = showError(request, "actionError");
%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
  <tr>
    <form name="reqView" method="get" action="<%= ctx %>/ProjectManagement.do">
    <td align="left">
      <input type="hidden" name="command" value="ProjectCenter" />
      <input type="hidden" name="section" value="Requirements" />
      <input type="hidden" name="pid" value="<%= project.getId() %>" />
      <img alt="" src="<%= ctx %>/images/icons/stock_filter-data-by-criteria-16.gif" align="absmiddle">
      <select name="listView" onChange="document.forms['reqView'].submit();">
        <option <%= projectRequirementsInfo.getOptionValue("all") %>><ccp:label name="projectsCenterRequirements.allOutlines">All Outlines</ccp:label></option>
        <option <%= projectRequirementsInfo.getOptionValue("open") %>><ccp:label name="projectsCenterRequirements.openOutlines">Open Outlines</ccp:label></option>
        <option <%= projectRequirementsInfo.getOptionValue("closed") %>><ccp:label name="projectsCenterRequirements.closedOutlines">Closed Outlines</ccp:label></option>
      </select>
    </td>
    <td>
      <ccp:pagedListStatus title="<%= actionError %>" object="projectRequirementsInfo"/>
    </td>
    </form>
  </tr>
</table>
<table class="pagedList">
  <thead>
     <tr>
      <th width="8"><ccp:label name="projectsCenterRequirements.action">Action</ccp:label></th>
      <th nowrap><ccp:label name="projectsCenterRequirements.startDate">Start Date</ccp:label></th>
      <th width="100%"><ccp:label name="projectsCenterRequirements.description">Description</ccp:label></th>
      <th><ccp:label name="projectsCenterRequirements.progress">Progress</ccp:label></th>
      <th width="118"><ccp:label name="projectsCenterRequirements.effort">Effort</ccp:label></th>
    </tr>
  </thead>
  <tbody>
<%
  if (requirements.size() == 0) {
%>
  <tr class="row2">
    <td colspan="5"><ccp:label name="projectsCenterRequirements.noOutlines">No outlines to display.</ccp:label></td>
  </tr>
<%
  }
  int rowid = 0;
  int count = 0;
  Iterator i = requirements.iterator();
  while (i.hasNext()) {
    rowid = (rowid != 1?1:2);
    ++count;
    Requirement thisRequirement = (Requirement) i.next();
%>
    <tr class="row<%= rowid %>">
      <td valign="top" align="center" nowrap>
        <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuItem', <%= thisRequirement.getId() %>, <%= project.getId() %>);"
           onMouseOver="over(0, <%= count %>)"
           onmouseout="out(0, <%= count %>); hideMenu('menuItem');"><img
           src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0"></a>
      </td>
      <td valign="top" align="center" nowrap>
        <ccp:tz timestamp="<%= thisRequirement.getStartDate() %>" dateOnly="true" default="&nbsp;" />
      </td>
      <td valign="top">
        <table border="0" cellpadding="0" cellspacing="0" class="empty">
          <tr>
            <td valign="top" nowrap>
              &nbsp;<%= thisRequirement.getStatusGraphicTag(ctx) %>
            </td>
            <td valign="top">
              <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/plan/<%= thisRequirement.getId() %>"><%= toHtml(thisRequirement.getShortDescription()) %></a>
              <a href="javascript:popURL('<%= ctx %>/ProjectManagementRequirements.do?command=Details&pid=<%= project.getId() %>&rid=<%= thisRequirement.getId() %>&popup=true','650','375','yes','yes');"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absbottom" /></a>
              <ccp:permission name="project-wiki-view">
                <ccp:evaluate if="<%= hasText(thisRequirement.getWikiLink()) %>">
                  <a href="javascript:popURL('<%= ctx %>/show/<%= project.getUniqueId() %>/wiki/<%= thisRequirement.getWikiSubject() %>?popup=true','700','600','yes','yes');"><img src="<%= ctx %>/images/icons/stock_macro-objects-16.gif" border="0" align="absbottom" /></a>
                </ccp:evaluate>
              </ccp:permission>
              <br />
              <i>
              <ccp:evaluate if="<%= hasText(thisRequirement.getSubmittedBy()) || hasText(thisRequirement.getDepartmentBy()) %>">
                  <ccp:label name="projectsCenterRequirements.requestedBy">Requested By</ccp:label>
              </ccp:evaluate>
              <ccp:evaluate if="<%= hasText(thisRequirement.getSubmittedBy()) %>">
                <%= toHtml(thisRequirement.getSubmittedBy()) %>
              </ccp:evaluate>
              <ccp:evaluate if="<%= hasText(thisRequirement.getSubmittedBy()) && hasText(thisRequirement.getDepartmentBy()) %>">
                /
              </ccp:evaluate>
              <ccp:evaluate if="<%= hasText(thisRequirement.getDepartmentBy()) %>">
                <%= toHtml(thisRequirement.getDepartmentBy()) %>
              </ccp:evaluate>
              </i>
            </td>
          </tr>
        </table>
      </td>
      <td valign="top" align="right" nowrap>
        <table cellpadding="1" cellspacing="1" class="empty">
          <tr>
          <td><ccp:label name="projectsCenterRequirements.progress">Progress</ccp:label>:</td>
          <ccp:evaluate if="<%= thisRequirement.getPlanActivityCount() == 0 %>">
            <td width="<%= thisRequirement.getPercentClosed() %>" bgColor="#CCCCCC" nowrap class="progressCell"></td>
          </ccp:evaluate>
          <ccp:evaluate if="<%= thisRequirement.getPlanActivityCount() > 0 %>">
            <ccp:evaluate if="<%= thisRequirement.getPercentClosed() > 0 %>">
              <td width="<%= thisRequirement.getPercentClosed()  %>" bgColor="green" nowrap class="progressCell"></td>
            </ccp:evaluate>
            <ccp:evaluate if="<%= thisRequirement.getPercentUpcoming() > 0 %>">
              <td width="<%= thisRequirement.getPercentUpcoming() %>" bgColor="#99CC66" nowrap class="progressCell"></td>
            </ccp:evaluate>
            <ccp:evaluate if="<%= thisRequirement.getPercentOverdue() > 0 %>">
              <td width="<%= thisRequirement.getPercentOverdue() %>" bgColor="red" nowrap class="progressCell"></td>
            </ccp:evaluate>
          </ccp:evaluate>
          </tr>
        </table>
        <ccp:evaluate if="<%= thisRequirement.getPlanActivityCount() == 0 %>">
          <ccp:label name="projectsCenterRequirements.zeroActivities">(0 activities)</ccp:label>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisRequirement.getPlanActivityCount() > 0 %>">
          (<%= thisRequirement.getPlanClosedCount() %> of <%= thisRequirement.getPlanActivityCount() %>
          activit<%= (thisRequirement.getPlanActivityCount() == 1?"y":"ies") %>
          <%= (thisRequirement.getPlanClosedCount() == 1?"is":"are") %> complete)
        </ccp:evaluate>
      </td>
      <td valign="top" nowrap>
        <ccp:label name="projectsCenterRequirements.due">Due:</ccp:label> <ccp:tz timestamp="<%= thisRequirement.getDeadline() %>" dateOnly="true"/><br />
        <ccp:label name="projectsCenterRequirements.loe">LOE:</ccp:label> <%= thisRequirement.getEstimatedLoeString() %>
      </td>
    </tr>
  <%
      if (thisRequirement.isTreeOpen() && thisRequirement.getAssignments().size() > 0) {
        Iterator assignments = thisRequirement.getAssignments().iterator();
        while (assignments.hasNext()) {
          Assignment thisAssignment = (Assignment)assignments.next();
  %>
    <tr class="row<%= rowid %>">
      <td valign="top" colspan="2">
        &nbsp;
      </td>
      <td valign="top">
        <img border="0" src="<%= ctx %>/images/treespace.gif" align="absmiddle">
        <%= thisAssignment.getStatusGraphicTag(ctx) %>
        <a href="javascript:popURL('<%= ctx %>/ProjectManagementAssignments.do?command=Modify&pid=<%= project.getId() %>&aid=<%= thisAssignment.getId() %>&popup=true&return=ProjectRequirements&param=<%= project.getId() %>','650','600','yes','yes');" style="text-decoration:none;color:black;" onMouseOver="this.style.color='blue';window.status='Update this assignment';return true;" onMouseOut="this.style.color='black';window.status='';return true;"><%= toHtml(thisAssignment.getRole()) %></a>
        (<%
          Iterator assignedUsers = thisAssignment.getAssignedUserList().iterator();
          while (assignedUsers.hasNext()) {
            AssignedUser thisAssignedUser = (AssignedUser) assignedUsers.next();
  %><ccp:username id="<%= thisAssignedUser.getUserId() %>"/><ccp:evaluate if="<%= assignedUsers.hasNext() %>">, </ccp:evaluate><%
          }
  %>)
      </td>
      <td valign="top" nowrap>
        <ccp:label name="projectsCenterRequirements.due">Due:</ccp:label> <%= thisAssignment.getRelativeDueDateString(User.getTimeZone(), User.getLocale()) %><br />
        <ccp:label name="projectsCenterRequirements.loe">LOE:</ccp:label> <%= thisAssignment.getEstimatedLoeString() %>
      </td>
    </tr>
  <%
    }
  }%>

<%}
%>
  </tbody>
</table>
<br>
<ccp:pagedListControl object="projectRequirementsInfo"/>
<br>
<table border="0" width="100%">
  <tr>
    <td>
      <img border="0" src="<%= ctx %>/images/box.gif" alt="Incomplete" align="absmiddle" />
      <ccp:label name="projectsCenterRequirements.itemIsComplete">Item is incomplete</ccp:label><br />
      <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="Completed" align="absmiddle" />
      <ccp:label name="projectsCenterRequirements.itemCompletedOrClosed">Item has been completed (or closed)</ccp:label><br />
      <img border="0" src="<%= ctx %>/images/box-hold.gif" alt="On Hold" align="absmiddle" />
      <ccp:label name="projectsCenterRequirements.itemNotApproved">Item has not been approved</ccp:label>
    </td>
  </tr>
</table>

