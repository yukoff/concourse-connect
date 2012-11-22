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
<%@ page import="com.concursive.connect.web.utils.ClientType" %>
<%@ page import="com.concursive.connect.web.modules.plans.dao.*" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="requirement" class="com.concursive.connect.web.modules.plans.dao.Requirement" scope="request"/>
<jsp:useBean id="projectAssignmentsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="mapList" class="com.concursive.connect.web.modules.plans.dao.RequirementMapList" scope="request"/>
<jsp:useBean id="assignments" class="com.concursive.connect.web.modules.plans.dao.AssignmentList" scope="request"/>
<jsp:useBean id="folders" class="com.concursive.connect.web.modules.plans.dao.AssignmentFolderList" scope="request"/>
<jsp:useBean id="PriorityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_assignments_menu.jspf" %>
<%-- Preload image rollovers for drop-down menu --%>
<script language="JavaScript" type="text/javascript">
  loadImages('menu');
</script>
<script language="JavaScript" type="text/javascript" src="<%= ctx %>/javascript/popURL.js"></script>
<script type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Required fields
    if (form.attachmentList.value == "") {
      messageText += "- Attachment is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The import form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
  }
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }
  function showTemplates() {
    popURL('<%= ctx %>/ProjectManagementRequirements.do?command=Templates&pid=<%= project.getId() %>&popup=true','550','520','yes','yes');
  }

</script>
<p><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/plans">Back to list</a></p>
<h2><%= toHtml(requirement.getShortDescription()) %></h2>
<ccp:evaluate if="<%= requirement.getReadOnly()%>">
  <div class="portlet-message-info"><p><ccp:label name="projectsCenterAssignments.planIsReadOnly">plan is marked read-only</ccp:label></p></div>
</ccp:evaluate>
<p>
    <form name="listView" method="post" action="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Assignments&pid=<%= project.getId() %>&rid=<%= requirement.getId() %>">
      <img alt="" src="<%= ctx %>/images/icons/stock_filter-data-by-criteria-16.gif" align="absmiddle">
      <select size="1" name="listView" onChange="document.forms['listView'].submit();">
        <option <%= projectAssignmentsInfo.getOptionValue("all") %>><ccp:label name="projectsCenterAssignments.allActivities">All Activities</ccp:label></option>
        <option <%= projectAssignmentsInfo.getOptionValue("open") %>><ccp:label name="projectsCenterAssignments.openActivities">Open Activities</ccp:label></option>
        <option <%= projectAssignmentsInfo.getOptionValue("closed") %>><ccp:label name="projectsCenterAssignments.closedActivities">Closed Activities</ccp:label></option>
      </select>
<%
    PriorityList.setJsEvent("onChange=\"javascript:document.forms['listView'].submit();\"");
    PriorityList.addItem(-1, "All Priorities");
%>
      <%= PriorityList.getHtmlSelect("listFilter1", projectAssignmentsInfo.getFilterValue("listFilter1")) %>
    </form>
</p>
<table class="pagedList">
  <thead>
  <tr>
    <th width="60%" nowrap colspan="2"><ccp:label name="projectsCenterAssignments.planOutline">Plan Outline</ccp:label></th>
    <th width="8%" align="center"><ccp:label name="projectsCenterAssignments.pri">Pri</ccp:label></th>
    <th width="8%" align="center" nowrap><ccp:label name="projectsCenterAssignments.assignedTo">Assigned To</ccp:label></th>
    <th width="8%" align="center"><ccp:label name="projectsCenterAssignments.estimated">Estimated</ccp:label></th>
    <th width="8%" align="center"><ccp:label name="projectsCenterAssignments.actual">Actual</ccp:label></th>
    <th width="8%" align="center"><ccp:label name="projectsCenterAssignments.progress">Progress</ccp:label></th>
    <th width="8%" align="center"><ccp:label name="projectsCenterAssignments.start">Start</ccp:label></th>
    <th width="8%" align="center" nowrap><ccp:label name="projectsCenterAssignmens.end">End</ccp:label></th>
  </tr>
  </thead>
  <tbody>
<%
  Requirement thisRequirement = requirement;
%>    
  <tr class="section">
    <td align="center">
      #
    </td>
    <td valign="top" width="60%">
      &nbsp;
      <img border="0" src="<%= ctx %>/images/icons/stock_list_bullet-16.gif" align="absmiddle">
        <a class="rollover" name="r<%= thisRequirement.getId() %>" id="r<%= thisRequirement.getId() %>" href="javascript:displayMenu('r<%= thisRequirement.getId() %>', 'menuRequirement',<%= project.getId() %>,<%= thisRequirement.getId() %>,-1,-1,-1,-1);"
           onMouseOver="window.status='Click to show drop-down menu';return true;"
           onmouseout="window.status='';hideMenu('menuRequirement');"><%= toHtml(thisRequirement.getShortDescription()) %></a>
      (<%= mapList.size() %> item<%= (mapList.size() == 1?"":"s") %>)
      <a href="javascript:popURL('<%= ctx %>/ProjectManagementRequirements.do?command=Details&pid=<%= project.getId() %>&rid=<%= requirement.getId() %>&popup=true','650','375','yes','yes');"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absbottom" /></a>
      <ccp:permission name="project-wiki-view">
        <ccp:evaluate if="<%= hasText(requirement.getWikiLink()) %>">
          <a href="javascript:popURL('<%= ctx %>/show/<%= project.getUniqueId() %>/wiki/<%= requirement.getWikiSubject() %>?popup=true','700','600','yes','yes');"><img src="<%= ctx %>/images/icons/stock_macro-objects-16.gif" border="0" align="absbottom" /></a>
        </ccp:evaluate>
      </ccp:permission>
    </td>
    <td width="8%">
      &nbsp;
    </td>
    <td width="8%">
      &nbsp;
    </td>
    <td valign="top" align="center" width="8%" nowrap>
      <%= thisRequirement.getEstimatedLoeString() %>
    </td>
    <td valign="top" align="center" width="8%" nowrap>
      --
    </td>
    <td valign="top" align="center" width="8%" nowrap>
      --
    </td>
    <td valign="top" align="center" width="8%" nowrap>
      &nbsp;<ccp:tz timestamp="<%= thisRequirement.getStartDate() %>" dateOnly="true"/>&nbsp;
    </td>
    <td valign="top" align="center" width="8%" nowrap>
      &nbsp;<ccp:tz timestamp="<%= thisRequirement.getDeadline() %>" dateOnly="true"/>&nbsp;
    </td>
  </tr>
<%
    int rowid = 0;
    HashMap nodeStatus = new HashMap();
    Iterator iMapList = mapList.iterator();
    int lastPosition = 0;
    while (iMapList.hasNext()) {
      RequirementMapItem mapItem = (RequirementMapItem) iMapList.next();
      nodeStatus.put(new Integer(mapItem.getIndent()), new Boolean(mapItem.getFinalNode()));
      rowid = (rowid != 1?1:2);
%>
  <tr class="sectionrow<%= rowid %>" onmouseover="swapClass(this,'sectionrowHighlight')" onmouseout="swapClass(this,'sectionrow<%= rowid %>')">
    <td valign="top" nowrap>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr>
<%--
          <td valign="middle">
            <input type="checkbox" name="batch<%= mapItem.getPosition() %>" value="ON" />
          </td>
--%>
          <td valign="middle" align="right">
            <ccp:evaluate if="<%= lastPosition + 1 != mapItem.getPosition() %>"><font color="red"></ccp:evaluate>
            &nbsp;<%= mapItem.getPosition() %>.&nbsp;
            <ccp:evaluate if="<%= lastPosition + 1 != mapItem.getPosition() %>"></font></ccp:evaluate>
          </td>
        </tr>
      </table>
    </td>
    <td valign="top">
      <table cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top" align="center">
      <%--
      <img alt="" src="<%= ctx %>/images/tree/treespace.gif" border="0" align="absmiddle" height="18" width="19"/>
      --%>
      &nbsp;
<%
      for (int count = 0; count < mapItem.getIndent(); count++) {
        Boolean isClosed = (Boolean) nodeStatus.get(new Integer(count));
        if (isClosed == null) {
          isClosed = new Boolean(false);
        }
%>
      <%-- Show spacing --%>
      <ccp:evaluate if="<%= isClosed.booleanValue() %>">
        </td><td><img alt="" src="<%= ctx %>/images/tree/treespace.gif" border="0" align="absmiddle" height="18" width="19"/>
      </ccp:evaluate>
      <%-- Show more nodes --%>
      <ccp:evaluate if="<%= !isClosed.booleanValue() %>">
        </td><td class="repeatLine" valign="top" align="center"><img alt="" src="<%= ctx %>/images/tree/treespace.gif" border="0" align="absmiddle" height="18" width="19"/>
      </ccp:evaluate>
<%
      }
      if (mapItem.getChildren().isEmpty()) {
        Boolean isClosed = (Boolean) nodeStatus.get(new Integer(mapItem.getIndent()));
        if (isClosed == null) {
          isClosed = new Boolean(false);
        }
%>
      <%-- Show final node --%>
      <ccp:evaluate if="<%= isClosed.booleanValue() %>">
        </td><td valign="top" align="center"><img alt="" src="<%= ctx %>/images/tree/tree4.gif" border="0" align="absmiddle" height="18" width="19"/>
      </ccp:evaluate>
      <%-- Show more nodes --%>
      <ccp:evaluate if="<%= !isClosed.booleanValue() %>">
        </td><td class="repeatLine" valign="top" align="center"><img alt="" src="<%= ctx %>/images/tree/tree3.gif" border="0" align="absmiddle" height="18" width="19"/>
      </ccp:evaluate>
<%
      } else {
%>
      <%-- Show Last Node with children --%>
      <ccp:evaluate if="<%= mapItem.getFinalNode() %>">
        </td><td valign="top"><img alt="" src="<%= ctx %>/images/tree/tree6o.gif" border="0" align="absmiddle" height="18" width="19"/>
      </ccp:evaluate>
      <%-- Show Node with children --%>
      <ccp:evaluate if="<%= !mapItem.getFinalNode() %>">
        </td><td class="repeatLine" valign="top" align="center"><img alt="" src="<%= ctx %>/images/tree/tree5o.gif" border="0" align="absmiddle" height="18" width="19"/>
      </ccp:evaluate>
<%
      }
%>
      </td>
      <td valign="top" align="center">
<%
      if (mapItem.getAssignmentId() > -1) {
        Assignment thisAssignment = (Assignment) assignments.getAssignment(mapItem.getAssignmentId());
%>
      <%= thisAssignment.getStatusGraphicTag(ctx) %>
      </td>
      <td>
        <ccp:evaluate if="<%= !thisRequirement.getReadOnly() %>">
          <a class="rollover" name="a<%= thisAssignment.getId() %>" id="a<%= thisAssignment.getId() %>" href="javascript:displayMenu('a<%= thisAssignment.getId() %>', 'menuActivity',<%= project.getId() %>,<%= thisRequirement.getId() %>,-1,<%= thisAssignment.getId() %>,<%= mapItem.getId() %>,<%= mapItem.getIndent() %>);"
             onMouseOver="window.status='Click to show drop-down menu';return true;"
             onmouseout="window.status='';hideMenu('menuActivity');"><%= toHtml(thisAssignment.getRole()) %></a>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisRequirement.getReadOnly() %>">
          <c:out value="<%= thisAssignment.getRole() %>" />
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisAssignment.hasNotes() %>">
          <a href="javascript:popURL('<%= ctx %>/ProjectManagementAssignments.do?command=ShowNotes&pid=<%= thisAssignment.getProjectId() %>&aid=<%= thisAssignment.getId() %>&popup=true','400','500','yes','yes');"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absmiddle" alt="Review all notes"/></a>
        </ccp:evaluate>
      </td>
      </tr>
      </table>
    </td>
    <td valign="top" align="center" nowrap>
      <%= toHtml(PriorityList.getValueFromId(thisAssignment.getPriorityId())) %>
    </td>
    <td valign="top" align="left" nowrap>
<%
        Iterator assignedUsers = thisAssignment.getAssignedUserList().iterator();
        while (assignedUsers.hasNext()) {
          AssignedUser thisAssignedUser = (AssignedUser) assignedUsers.next();
%>
      <ccp:username id="<%= thisAssignedUser.getUserId() %>"/>
<%
          if (assignedUsers.hasNext()) {
%>
      <br />
<%
          }
        }
%>
      <ccp:evaluate if="<%= thisAssignment.getAssignedUserList().size() > 0 && StringUtils.hasText(thisAssignment.getResponsible()) %>"><br /></ccp:evaluate>
      <c:out value="<%= thisAssignment.getResponsible() %>" />
    </td>
    <td valign="top" align="center" nowrap>
      <c:out value="<%= thisAssignment.getEstimatedLoeString() %>" />
    </td>
    <td valign="top" align="center" nowrap>
      <ccp:evaluate if="<%= thisAssignment.isOverBudget() %>"><font color="red"></ccp:evaluate>
      <c:out value="<%= thisAssignment.getActualLoeString() %>" />
      <ccp:evaluate if="<%= thisAssignment.isOverBudget() %>"></font></ccp:evaluate>
    </td>
    <td valign="top" align="center" nowrap>
      <ccp:evaluate if="<%= thisAssignment.getPercentComplete() > 0 %>">
        <%= thisAssignment.getPercentComplete() %>%
      </ccp:evaluate>
      <ccp:evaluate if="<%= thisAssignment.getPercentComplete() == 0 %>">
        --
      </ccp:evaluate>
    </td>
    <td valign="top" align="center" nowrap>
      &nbsp;<ccp:tz timestamp="<%= thisAssignment.getEstStartDate() %>" dateOnly="true"/>&nbsp;
    </td>
    <td valign="top" align="center" nowrap>
      <%-- display whether before today, today, or overdue --%>
      &nbsp;<%= thisAssignment.getRelativeDueDateString(User.getTimeZone(), User.getLocale()) %>&nbsp
    </td>
<%
      } else if (mapItem.getFolderId() > -1) {
        //Assignment Folder
        AssignmentFolder thisFolder = folders.getAssignmentFolder(mapItem.getFolderId());
%>
      <td valign="top" align="center">
        <img border="0" src="<%= ctx %>/images/icons/stock_open-16-19.gif" align="absmiddle" />
      </td>
      <td>
        <ccp:evaluate if="<%= !thisRequirement.getReadOnly() %>">
          <a class="rollover" name="f<%= thisFolder.getId() %>" id="f<%= thisFolder.getId() %>" href="javascript:displayMenu('f<%= thisFolder.getId() %>', 'menuFolder',<%= project.getId() %>,<%= thisRequirement.getId() %>,<%= thisFolder.getId() %>,-1,<%= mapItem.getId() %>,<%= mapItem.getIndent() %>);"
             onMouseOver="window.status='Click to show drop-down menu';return true;"
             onmouseout="window.status='';hideMenu('menuFolder');"><%= toHtml(thisFolder.getName()) %></a>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisRequirement.getReadOnly() %>">
          <%= toHtml(thisFolder.getName()) %>
        </ccp:evaluate>
        <ccp:evaluate if="<%= hasText(thisFolder.getDescription()) %>">
          <a href="javascript:popURL('<%= ctx %>/ProjectManagementAssignmentsFolder.do?command=FolderDetails&pid=<%= project.getId() %>&folderId=<%= mapItem.getFolderId() %>&popup=true','650','375','yes','yes');"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absmiddle"/></a>
        </ccp:evaluate>
      </td>
      </tr>
      </table>
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      &nbsp; <%--Roll-up = <%= thisFolder.getAssignments().getEs%>--%>
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      &nbsp;
    </td>
    <td>
      &nbsp;
    </td>
  </tr>
<%
      }
      lastPosition = mapItem.getPosition();
    }
%>
<%-- Menu system for selected items --%>
  <tr class="sectionTotal">
    <td colspan="4" class="section">
      &nbsp;
    </td>
    <td class="section">
      Est. Total
    </td>
    <td class="section">
      Actual Total
    </td>
    <td colspan="3" class="section">
      &nbsp;
    </td>
  </tr>
  </tbody>
</table>
<%--
<img src="<%= ctx %>/images/icons/stock_new-dir-16.gif" border="0" align="absmiddle" height="16" width="16"/>
<a href="javascript:thisProjectId=<%= project.getId() %>;thisRequirementId=<%= requirement.getId() %>;folderId=-1;thisActivityId=-1;addFolder();">Add Activity Folder</a>
|
<img src="<%= ctx %>/images/New.png" border="0" align="absmiddle" height="16" width="16"/>
<a href="javascript:thisProjectId=<%= project.getId() %>;thisRequirementId=<%= requirement.getId() %>;folderId=-1;thisActivityId=-1;addActivity()">Add Activity</a>
<br>
--%>
<br />
<ccp:permission name="project-plan-outline-add">
<form name="inputForm" action="<%= ctx %>/ProjectManagementRequirements.do?command=ImportPlan" method="post" onSubmit="return checkForm(this);">
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">Import plan activities</th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td class="formLabel">
        File
      </td>
      <td>
        <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
        <a href="${ctx}/FileAttachments.do?command=ShowForm&pid=<%= project.getId() %>&lmid=<%= Constants.PROJECT_REQUIREMENT_FILES %>&liid=<%= requirement.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&popup=true"
           rel="shadowbox" title="Share an attachment">Attach File</a>
        <input type="hidden" id="attachmentList" name="attachmentList" value="" />
        <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
        <a href="javascript:showTemplates()">templates</a>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">Options</td>
      <td>
        <input type="checkbox" name="overwrite" value="on" />
        Overwrite plan (otherwise append to existing plan)
      </td>
    </tr>
  </tbody>
</table>
<input type="submit" name="save" value="Import" />
<input type="hidden" name="rid" value="<%= requirement.getId() %>">
<input type="hidden" name="pid" value="<%= requirement.getProjectId() %>">
</form>
<br />
</ccp:permission>
<%-- legend --%>
<table border="0" width="100%">
  <tr>
    <td>
      <img border="0" src="<%= ctx %>/images/box.gif" alt="Incomplete" align="absmiddle">
      <ccp:label name="projectsCenterAssignments.itemIncomplete">Item is incomplete</ccp:label><br />
      <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="Completed" align="absmiddle">
      <ccp:label name="projectsCenterAssignments.itemCompleted">Item has been completed</ccp:label><br />
      <img border="0" src="<%= ctx %>/images/box-closed.gif" alt="Closed" align="absmiddle">
      <ccp:label name="projectsCenterAssignments.itemClosed">Item has been closed</ccp:label><br />
      <img border="0" src="<%= ctx %>/images/box-hold.gif" alt="On Hold" align="absmiddle">
      <ccp:label name="projectsCenterAssignments.itemOnHold">Item is on hold</ccp:label>
    </td>
  </tr>
</table>
