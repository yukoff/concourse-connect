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
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.utils.HtmlSelect" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="Assignment" class="com.concursive.connect.web.modules.plans.dao.Assignment" scope="request"/>
<jsp:useBean id="PriorityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="StatusList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="StatusPercentList" class="com.concursive.connect.web.utils.HtmlPercentList" scope="request"/>
<jsp:useBean id="LoeList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="activityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="teamMemberList" class="com.concursive.connect.web.modules.members.dao.TeamMemberList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  HtmlSelect team = new HtmlSelect();
  team.addItem(-1, "-- None Selected --");
  Iterator iTeam = teamMemberList.iterator();
  while (iTeam.hasNext()) {
    TeamMember thisMember = (TeamMember)iTeam.next();
    team.addItem(thisMember.getUserId(),
         ((User)thisMember.getUser()).getNameLastFirst());
  }
  String onLoad = "refreshUsers();refreshView();";
  if ("true".equals(request.getParameter("donew"))) {
    onLoad += "window.opener.scrollReload('" + ctx + "/ProjectManagement.do?command=ProjectCenter&section=Assignments&pid=" + project.getId() + "&rid=" +  (Assignment.getId() == -1?StringUtils.encodeUrl(request.getParameter("rid")):String.valueOf(Assignment.getRequirementId())) + "')";
  }
  //Only evaluate on an insert
  int maxIndent = 0;
  if (Assignment.getId() == -1) {
    maxIndent = (Assignment.getPrevIndent() > -1 ? Assignment.getPrevIndent() + 1 : Integer.parseInt(request.getParameter("prevIndent")) + 1);
  }
%>
<body onLoad="document.inputForm.role.focus();<%= onLoad %>">
<script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/projects_center_assignments_list.js"></script>
<script language="JavaScript">
  var projectId = <%= project.getId() %>;
  var vAssigned = "<%= Assignment.getAssignedUserList().getAssignedCSV("|") %>".split("|");
  var vRole = "<%= Assignment.getAssignedUserList().getRoleCSV("|") %>".split("|");
  var vState = "<%= Assignment.getAssignedUserList().getStateCSV("|") %>".split("|");
</script>
<script language="JavaScript">
  function checkForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";
    //Check required field
    if (form.role.value == "") {
      messageText += "- Description field is required\r\n";
      formTest = false;
    }
<ccp:evaluate if="<%= Assignment.getId() == -1 %>">
    //Check max indent
    if (form.indent.value > <%= maxIndent %>) {
      messageText += "- Indent level must be between 0 and <%= maxIndent %>\r\n";
      formTest = false;
    }
</ccp:evaluate>
    //Check number field
    var valid = "0123456789.";
    var ok = true;
    if (form.estimatedLoe.value != "") {
      for (var i=0; i<form.estimatedLoe.value.length; i++) {
        temp = "" + form.estimatedLoe.value.substring(i, i+1);
        if (valid.indexOf(temp) == "-1") {
          ok = false;
        }
      }
      if (ok == false) {
        messageText += "- Only numbers are allowed in the LOE field\r\n";
        formTest = false;
      }
    }
    //Check date field
    if ((form.dueDate.value != "") && (!checkDate(form.dueDate.value))) {
      messageText += "- Due date was not properly entered\r\n";
      formTest = false;
    }
    if ((form.estStartDate.value != "") && (!checkDate(form.estStartDate.value))) {
      messageText += "- Start date was not properly entered\r\n";
      formTest = false;
    }
    if (formTest == false) {
      messageText = "The activity form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementAssignments.do?command=Save&pid=<%= project.getId() %>&rid=<%= (Assignment.getId() == -1?StringUtils.encodeUrl(request.getParameter("rid")):String.valueOf(Assignment.getRequirementId())) %>&auto-populate=true<%= (request.getParameter("popup") != null?"&popup=true":"") %>" onSubmit="return checkForm(this);">
  <%= showError(request, "actionError", false) %>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:evaluate if="<%= Assignment.getId() == -1%>">
              <ccp:label name="projcetsCenterAssignments.add.add">Add</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= Assignment.getId() != -1%>">
              <ccp:label name="projectsCenterAssignments.add.update">Update</ccp:label>
          </ccp:evaluate>
          <ccp:label name="projectsCenterAssignments.add.activity">Activity</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td valign="top" nowrap class="formLabel"><ccp:label name="projectsCenterAssignments.add.description">Description</ccp:label></td>
        <td valign="top" nowrap>
          <input type="text" name="role" size="57" maxlength="150" value="<%= toHtmlValue(Assignment.getRole()) %>"><span class="required">*</span>
          <%= showAttribute(request, "roleError") %>
        </td>
      </tr>
  <ccp:evaluate if="<%= Assignment.getId() == -1 %>">
  <%-- Temp. fix for Weblogic --%>
  <%
  int assignmentIndent = Assignment.getIndent() > -1 ? Assignment.getIndent() : Integer.parseInt(request.getParameter("prevIndent"));
  %>
      <tr>
        <td class="formLabel" nowrap>
          <ccp:label name="projectsCenterAssignments.add.indentLevel">Indent Level</ccp:label>
        </td>
        <td>
          <ccp:spinner name="indent" value="<%= assignmentIndent %>" min="0" max="<%= maxIndent %>"/>
        </td>
      </tr>
  </ccp:evaluate>
      <tr class="containerBody">
        <td class="formLabel"><ccp:label name="projectsCenterAssignments.add.priority">Priority</ccp:label></td>
        <td valign="top">
          <%= PriorityList.getHtmlSelect("priorityId", Assignment.getPriorityId()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td class="formLabel" valign="top" nowrap>
          <ccp:label name="projectsCenterAssignments.add.levelOfEffort">Level of Effort</ccp:label>
        </td>
        <td>
          <table border="0" cellspacing="0" cellpadding="0" class="empty">
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterAssignments.add.estimated">Estimated:</ccp:label>
              </td>
              <td>
                <input type="text" name="estimatedLoe" size="4" value="<%= Assignment.getEstimatedLoeValue() %>">
                <%= LoeList.getHtmlSelect("estimatedLoeTypeId", Assignment.getEstimatedLoeTypeId()) %>
              </td>
            </tr>
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterAssignments.add.actual">Actual:</ccp:label>
              </td>
              <td>
                <input type="text" name="actualLoe" size="4" value="<%= Assignment.getActualLoeValue() %>">
                <%= LoeList.getHtmlSelect("actualLoeTypeId", Assignment.getActualLoeTypeId()) %>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterAssignments.add.startDate">Start Date</ccp:label></td>
        <td valign="top">
          <input type="text" name="estStartDate" id="estStartDate" size="10" value="<ccp:tz timestamp="<%= Assignment.getEstStartDate() %>" dateOnly="true"/>">
          <a href="javascript:popCalendar('inputForm', 'estStartDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterAssignments.add.dueDate">Due Date</ccp:label></td>
        <td valign="top">
          <input type="text" name="dueDate" id="dueDate" size="10" value="<ccp:tz timestamp="<%= Assignment.getDueDate() %>" dateOnly="true"/>">
          <a href="javascript:popCalendar('inputForm', 'dueDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterAssignments.add.keywords">Keywords</ccp:label></td>
        <td valign="top">
          <input type="text" name="technology" size="24" maxlength="50" value="<%= toHtmlValue(Assignment.getTechnology()) %>">
        </td>
      </tr>
    </tbody>
  </table>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:label name="projcetsCenterAssignments.add.progress">Progress</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td class="formLabel"><ccp:label name="projectsCenterAssignments.add.status">Status</ccp:label></td>
        <td>
          <%= StatusList.getHtmlSelect("statusId", Assignment.getStatusId()) %>
          <%= StatusPercentList.getHtml("percentComplete", Assignment.getPercentComplete()) %>
          <%= showAttribute(request, "statusIdError") %>
        </td>
      </tr>
      <tr class="containerBody">
        <td class="formLabel" valign="top"><ccp:label name="projectsCenterAssignments.add.additionalNote">Additional Note</ccp:label></td>
        <td>
          <table border="0" class="empty">
            <tr>
              <td>
                <textarea name="additionalNote" cols="55" rows="2"><%= toString(Assignment.getAdditionalNote()) %></textarea>
              </td>
              <td valign="top" nowrap>
                <a href="javascript:popURL('<%= ctx %>/ProjectManagementAssignments.do?command=ShowNotes&pid=<%= Assignment.getProjectId() %>&aid=<%= Assignment.getId() %>&popup=true','400','500','yes','yes');"><ccp:evaluate if="<%= Assignment.hasNotes() %>"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absmiddle" alt="Review all notes"/></ccp:evaluate><ccp:evaluate if="<%= !Assignment.hasNotes() %>"><img src="<%= ctx %>/images/icons/stock_insert-note-gray-16.gif" border="0" align="absmiddle" alt="Review all notes"/></ccp:evaluate></a>
                <%= Assignment.getNoteCount() %>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </tbody>
  </table>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:label name="projectsCenterAssignments.add.assignment">Assignment</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td valign="top" nowrap class="formLabel">&nbsp;</td>
        <td valign="top">
          <%-- Dropdown to assign users --%>
          <%= team.getHtml("userAssignedId") %>
          <ccp:evaluate if="<%= activityList.size() > 1 %>">
            <%= activityList.getHtmlSelect("userAssignedRoleId", -1) %>
          </ccp:evaluate>
          <ccp:evaluate if="<%= activityList.size() <= 1 %>">
            <input type="hidden" name="userAssignedRoleId" value="-1" />
          </ccp:evaluate>
          <input type="button" name="assign"
                               value="<ccp:label name="button.add">Add</ccp:label>"
                               onclick="assignUser();" />
          <%= showAttribute(request, "userAssignedIdError") %>
        </td>
      </tr>
      <tr class="containerBody">
        <td valign="top" nowrap class="formLabel"><ccp:label name="projectsCenterAssignments.add.assignTo">Assigned To</ccp:label></td>
        <td valign="top">
          <%-- List of assigned users; change to use AJAX --%>
          <iframe id="server_list" name="server_list" width="100%" style="overflow: hidden;" border="0" frameborder="0" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/empty.html"></iframe>
          <input type="hidden" name="assignedUserList_requestItems" value="" />
        </td>
      </tr>
    </tbody>
  </table>
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>" onclick="this.form.donew.value='false'">
<ccp:evaluate if="<%= Assignment.getId() == -1 %>">
  <input type="submit" value="<ccp:label name="button.saveAndNew">Save & New</ccp:label>" onclick="this.form.donew.value='true'">
</ccp:evaluate>
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="this.form.dosubmit.value='false';<%= (isPopup(request)?"window.close();":"window.location.href='" + ctx + "/ProjectManagement.do?command=ProjectCenter&section=Assignments&pid=" + project.getId()  + "&rid=" + String.valueOf(Assignment.getRequirementId()) + "';") %>">
  <input type="hidden" name="id" value="<%= Assignment.getId() %>">
  <input type="hidden" name="folderId" value="<%= (Assignment.getId() == -1?StringUtils.toHtmlValue(request.getParameter("folderId")):String.valueOf(Assignment.getFolderId())) %>">
  <input type="hidden" name="projectId" value="<%= project.getId() %>">
  <input type="hidden" name="requirementId" value="<%= (Assignment.getId() == -1?StringUtils.toHtmlValue(request.getParameter("rid")):String.valueOf(Assignment.getRequirementId())) %>">
  <input type="hidden" name="modified" value="<%= Assignment.getModifiedString() %>">
  <input type="hidden" name="dosubmit" value="true">
  <input type="hidden" name="donew" value="false">
  <input type="hidden" name="return" value="<%= request.getAttribute("return") %>">
  <input type="hidden" name="param" value="<%= project.getId() %>">
  <input type="hidden" name="param2" value="<%= (Assignment.getId() == -1?StringUtils.toHtmlValue(request.getParameter("rid")):String.valueOf(Assignment.getRequirementId())) %>">
  <input type="hidden" name="prevIndent" value="<%= Assignment.getPrevIndent() > -1 ? String.valueOf(Assignment.getPrevIndent()) : StringUtils.toHtmlValue(request.getParameter("prevIndent")) %>">
  <input type="hidden" name="prevMapId" value="<%= Assignment.getPrevMapId() > -1 ? String.valueOf(Assignment.getPrevMapId()) : StringUtils.toHtmlValue(request.getParameter("prevMapId")) %>">
</form>
</body>
