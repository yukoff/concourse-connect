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
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ page import="com.concursive.connect.web.utils.HtmlSelect" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="category" class="com.concursive.connect.web.modules.lists.dao.TaskCategory" scope="request"/>
<jsp:useBean id="Task" class="com.concursive.connect.web.modules.lists.dao.Task" scope="request"/>
<jsp:useBean id="PriorityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="teamMemberList" class="com.concursive.connect.web.modules.members.dao.TeamMemberList" scope="request"/>
<jsp:useBean id="functionalAreaList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="complexityList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="businessValueList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="targetSprintList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="targetReleaseList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="statusList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="loeRemainingList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="assignedPriorityList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<%@ include file="initPage.jsp" %>
<body onLoad="document.inputForm.description.focus();">
<script language="JavaScript">
  function checkForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.description.value == "") {
      messageText += "- Description is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementLists.do?command=Save&id=<%= Task.getId() %>&auto-populate=true" onSubmit="return checkForm(this);">
<table border="0" cellpadding="1" cellspacing="0" width="100%">
  <tr class="subtab">
    <td>
      <img border="0" src="<%= ctx %>/images/icons/stock_list_enum2-16.gif" align="absmiddle">
      <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/lists"><ccp:label name="projectsCenterLists.add.lists">Lists</ccp:label></a> >
      <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/list/<%= category.getId() %>"><%= toHtml(category.getDescription()) %></a> >
      <ccp:evaluate if="<%= Task.getId() == -1%>">
        <ccp:label name="projectsCenterLists.add.add">Add</ccp:label>
      </ccp:evaluate>
      <ccp:evaluate if="<%= Task.getId() != -1%>">
        <ccp:label name="projectsCenterLists.add.update">Update</ccp:label>
      </ccp:evaluate>
    </td>
  </tr>
</table>
<br>
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>" onClick="this.form.donew.value='false'">
<ccp:evaluate if="<%= Task.getId() == -1 %>">
  <input type="submit" value="<ccp:label name="button.saveAndNew">Save & New</ccp:label>" onClick="this.form.donew.value='true'">
</ccp:evaluate>
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/list/<%= category.getId() %>';"><br />
  <%= showError(request, "actionError") %>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:evaluate if="<%= Task.getId() == -1%>">
              <ccp:label name="projectsCenterLists.add.add">Add</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= Task.getId() != -1%>">
              <ccp:label name="projectsCenterLists.add.update">Update</ccp:label>
          </ccp:evaluate>

          <ccp:label name="projectsCenterLists.add.item">Item</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.description">Description</ccp:label></td>
        <td>
          <input type="text" name="description" size="57" maxlength="255" value="<%= toHtmlValue(Task.getDescription()) %>"><span class="required">*</span> <%= showAttribute(request, "descriptionError") %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.priority">Priority</ccp:label></td>
        <td>
          <%= PriorityList.getHtmlSelect("priority",Task.getPriority()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.status">Status</ccp:label></td>
        <td>
          <input type="checkbox" name="complete" <%=Task.getComplete()?" checked":""%>> <ccp:label name="projectsCenterLists.add.complete">Complete</ccp:label>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterLists.add.notes">Notes</ccp:label></td>
        <td>
          <TEXTAREA NAME="notes" ROWS="8" COLS="55"><%= toString(Task.getNotes()) %></TEXTAREA>
        </td>
      </tr>

  <%
    HtmlSelect team = new HtmlSelect();
    team.addItem(-1, "-- None --");
    Iterator iTeam = teamMemberList.iterator();
    while (iTeam.hasNext()) {
      TeamMember thisMember = (TeamMember)iTeam.next();
      team.addItem(thisMember.getUserId(),
           ((User)thisMember.getUser()).getNameFirstLastInitial());
    }
  %>
      <ccp:evaluate if="<%= team.size() > 1 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.owner">Owner</ccp:label></td>
        <td>
          <%= team.getHtml("owner", Task.getOwner()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= assignedPriorityList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.assignedPriority">Assigned Priority</ccp:label></td>
        <td>
          <%= assignedPriorityList.getHtmlSelect("assignedPriority",Task.getAssignedPriority()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= functionalAreaList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.functionalArea">Functional Area</ccp:label></td>
        <td>
          <%= functionalAreaList.getHtmlSelect("functionalArea",Task.getFunctionalArea()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= complexityList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.complexity">Complexity</ccp:label></td>
        <td>
          <%= complexityList.getHtmlSelect("complexity",Task.getComplexity()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= businessValueList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.businessValue">Business Value</ccp:label></td>
        <td>
          <%= businessValueList.getHtmlSelect("businessValue",Task.getBusinessValue()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= targetSprintList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.targetSprint">Target Sprint</ccp:label></td>
        <td>
          <%= targetSprintList.getHtmlSelect("targetSprint",Task.getTargetSprint()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= targetReleaseList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.targetRelease">Target Release</ccp:label></td>
        <td>
          <%= targetReleaseList.getHtmlSelect("targetRelease",Task.getTargetRelease()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= statusList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.status">Status</ccp:label></td>
        <td>
          <%= statusList.getHtmlSelect("status",Task.getStatus()) %>
        </td>
      </tr>
      </ccp:evaluate>

      <ccp:evaluate if="<%= loeRemainingList.size() > 0 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.add.Remaining">Remaining</ccp:label></td>
        <td>
          <%= loeRemainingList.getHtmlSelect("loeRemaining",Task.getLoeRemaining()) %>
        </td>
      </tr>
      </ccp:evaluate>
    </tbody>
  </table>
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>" onclick="this.form.donew.value='false'">
<ccp:evaluate if="<%= Task.getId() == -1 %>">
  <input type="submit" value="<ccp:label name="button.saveAndNew">Save & New</ccp:label>" onclick="this.form.donew.value='true'">
</ccp:evaluate>
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/list/<%= category.getId() %>';">
  <input type="hidden" name="pid" value="<%= project.getId() %>">
  <input type="hidden" name="categoryId" value="<%= category.getId() %>">
  <input type="hidden" name="modified" value="<%= Task.getModified() %>">
  <input type="hidden" name="dosubmit" value="true">
  <input type="hidden" name="donew" value="false">
</form>
</body>
