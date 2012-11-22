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
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="Requirement" class="com.concursive.connect.web.modules.plans.dao.Requirement" scope="request"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="LoeList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<%@ include file="initPage.jsp" %>
<body onLoad="document.inputForm.shortDescription.focus();">
<script language="JavaScript">
  function checkForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";
    //Required fields
    if (form.shortDescription.value == "") {    
      messageText += "- Title is a required field\r\n";
      formTest = false;
    }
    if (form.description.value == "") {    
      messageText += "- Description is a required field\r\n";
      formTest = false;
    }
    
    //Check LOE number field
    var valid = "0123456789.,";
    var ok = true;
    if (form.estimatedLoe.value != "") {
      for (var i=0; i<form.estimatedLoe.value.length; i++) {
        temp = "" + form.estimatedLoe.value.substring(i, i+1);
        if (valid.indexOf(temp) == "-1") {
          ok = false;
        }
      }
      if (!ok) {
        messageText += "- Only numbers are allowed in the LOE field\r\n";
        formTest = false;
      }
    }
    
    //Check date fields
    if ((form.deadline.value != "") && (!checkDate(form.deadline.value))) {
      messageText += "- Outline due date was not properly entered\r\n";
      formTest = false;
    }
  
    if (!formTest) {
      messageText = "The outline form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
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
<form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementRequirements.do?command=<%= Requirement.getId() == -1?"Insert":"Update" %>&auto-populate=true" onSubmit="return checkForm(this);">
  <%= showError(request, "actionError") %>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:evaluate if="<%= Requirement.getId() == -1 %>">
              <ccp:label name="projectsCenterRequirements.add.add">Add</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= Requirement.getId() != -1 %>">
              <ccp:label name="projectsCenterRequirements.add.update">Update</ccp:label>
          </ccp:evaluate>
          <ccp:label name="projectsCenterRequirements.add.outline">Outline</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterRequirements.add.title">Title</ccp:label></td>
        <td>
          <input type="text" name="shortDescription" size="57" maxlength="255" value="<%= toHtmlValue(Requirement.getShortDescription()) %>"><span class="required">*</span> <%= showAttribute(request, "shortDescriptionError") %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterRequirements.add.details">Details</ccp:label></td>
        <td>
          <table border="0" cellpadding="0" cellspacing="0" class="empty">
            <tr>
              <td>
                <textarea name="description" cols="55" rows="8"><c:out value="<%= Requirement.getDescription() %>" /></textarea><br />
                <%= showAttribute(request, "descriptionError") %>
              </td>
              <td valign="top">
                <font color="red">*</font>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel">Wiki Link</td>
        <td>
          <input type="text" name="wikiLink" size="34" maxlength="255" value="<%= toHtmlValue(Requirement.getWikiLink()) %>">
          <%-- @todo BROKEN
          <a href="javascript:popWiki('inputForm','wikiLink','<%= Requirement.getProjectId() %>');"><img src="<%= ctx %>/images/icons/stock_macro-objects-16.gif" border="0" align="absmiddle"></a>
          --%>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterRequirements.add.requestedBy">Requested By</ccp:label></td>
        <td>
          <input type="text" name="submittedBy" size="24" maxlength="50" value="<%= toHtmlValue(Requirement.getSubmittedBy()) %>">
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterRequirements.add.deptOrCompany">Department or<br>Company</ccp:label></td>
        <td valign="top">
          <input type="text" name="departmentBy" size="24" maxlength="50" value="<%= toHtmlValue(Requirement.getDepartmentBy()) %>">
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterRequirements.add.expectedDates">Expected Dates</ccp:label></td>
        <td>
          <table border="0" cellspacing="0" cellpadding="0" class="empty">
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterRequirements.add.start">Start:</ccp:label>
              </td>
              <td>
                <input type="text" name="startDate" id="startDate" size="10" onChange="checkDate(this.value)" value="<ccp:tz timestamp="<%= Requirement.getStartDate() %>" dateOnly="true"/>">
                <a href="javascript:popCalendar('inputForm', 'startDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
                <%=showAttribute(request,"startDateError")%>
              </td>
            </tr>
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterRequirements.add.finish">Finish:</ccp:label>
              </td>
              <td>
                <input type="text" name="deadline" id="deadline" size="10" onChange="checkDate(this.value)" value="<ccp:tz timestamp="<%= Requirement.getDeadline() %>" dateOnly="true"/>">
                <a href="javascript:popCalendar('inputForm', 'deadline', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
                <%= showAttribute(request,"deadlineError") %>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterRequirements.add.levelOfEffort">Level of Effort</ccp:label></td>
        <td>
          <table border="0" cellspacing="0" cellpadding="0" class="empty">
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterRequirements.add.estimated">Estimated:</ccp:label>
              </td>
              <td>
                <input type="text" name="estimatedLoe" size="4" value="<%= Requirement.getEstimatedLoeValue() %>">
                <%= LoeList.getHtmlSelect("estimatedLoeTypeId", Requirement.getEstimatedLoeTypeId()) %>
              </td>
            </tr>
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterRequirements.add.actual">Actual:</ccp:label>
              </td>
              <td>
                <input type="text" name="actualLoe" size="4" value="<%= Requirement.getActualLoeValue() %>">
                <%= LoeList.getHtmlSelect("actualLoeTypeId", Requirement.getActualLoeTypeId()) %>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterRequirements.add.status">Status</ccp:label></td>
        <td>
          <input type="checkbox" name="approved" value="ON" <%= (Requirement.getApproved()?"checked":"") %>>
          <ccp:label name="projectsCenterRequirements.add.outlineApproved">Outline Approved</ccp:label>
          <br>
          <input type="checkbox" name="closed" value="ON" <%= (Requirement.getClosed()?"checked":"") %>>
          <ccp:label name="projectsCenterRequirements.add.outlineClosed">Outline Closed</ccp:label>
        </td>
      </tr>
      <ccp:evaluate if="<%= Requirement.getId() == -1 %>">
        <ccp:permission name="project-plan-outline-edit">
        <tr class="containerBody">
          <td class="formLabel">
            Import plan activities from file
          </td>
          <td>
            <%
              Iterator files = fileItemList.iterator();
              while (files.hasNext()) {
                FileItem thisFile = (FileItem)files.next();
            %>
                  <%= toHtml(thisFile.getClientFilename()) %><ccp:evaluate if="<%= files.hasNext() %>">;</ccp:evaluate>
            <%
              }
            %>
            <ccp:evaluate if="<%= fileItemList.size() > 0 %>"><br /></ccp:evaluate>
            <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
            <a href="${ctx}/FileAttachments.do?command=ShowForm&pid=<%= project.getId() %>&lmid=<%= Constants.PROJECT_REQUIREMENT_FILES %>&liid=<%= Requirement.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&popup=true"
             rel="shadowbox" title="Share an attachment">Attach File</a>
            <input type="hidden" id="attachmentList" name="attachmentList" value="" />
            <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
            <a href="javascript:showTemplates()">templates</a>
          </td>
        </tr>
        </ccp:permission>
      </ccp:evaluate>
      <tr class="containerBody">
        <td class="formLabel">Read-Only</td>
        <td><input type="checkbox" name="readOnly" value="on" <%= (Requirement.getReadOnly()?"checked":"") %>/> Mark assignments as read-only, no updates will be allowed</td>
      </tr>
    </tbody>
  </table>
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>">
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Requirements&pid=<%= project.getId() %>';">
  <input type="hidden" name="id" value="<%= Requirement.getId() %>">
  <input type="hidden" name="pid" value="<%= project.getId() %>">
  <input type="hidden" name="projectId" value="<%= project.getId() %>">
  <input type="hidden" name="modified" value="<%= Requirement.getModifiedString() %>">
  <input type="hidden" name="dosubmit" value="true">
</form>
</body>
