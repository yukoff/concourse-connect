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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="assignmentFolder" class="com.concursive.connect.web.modules.plans.dao.AssignmentFolder" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  String onLoad = "";
  if ("true".equals(request.getParameter("donew"))) {
    onLoad = "window.opener.scrollReload('" + ctx + "/ProjectManagement.do?command=ProjectCenter&section=Assignments&pid=" + project.getId() + "&rid=" + (assignmentFolder.getId() == -1 ? StringUtils.encodeUrl(request.getParameter("rid")):String.valueOf(assignmentFolder.getRequirementId())) + "')";
  }
  //Only evaluate on an insert
  int maxIndent = 0;
  if (assignmentFolder.getId() == -1) {
    maxIndent = (assignmentFolder.getPrevIndent() > -1 ? assignmentFolder.getPrevIndent() + 1 : Integer.parseInt(request.getParameter("prevIndent")) + 1);
  }
%>
<body onLoad="document.inputForm.name.focus();<%= onLoad %>">
<script language="JavaScript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";

    //Check required field
    if (form.name.value == "") {
      messageText += "- Name field is required\r\n";
      formTest = false;
    }
<ccp:evaluate if="<%= assignmentFolder.getId() == -1 %>">
    //Check max indent
    if (form.indent.value > <%= maxIndent %>) {
      messageText += "- Indent level must be between 0 and <%= maxIndent %>\r\n";
      formTest = false;
    }
</ccp:evaluate>
<%--
    if (form.description.value == "") {
      messageText += "- Description field is required\r\n";
      formTest = false;
    }
--%>
    if (formTest == false) {
      messageText = "The activity folder form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementAssignmentsFolder.do?command=SaveFolder&pid=<%= project.getId() %>&rid=<%= (assignmentFolder.getId() == -1 ? StringUtils.encodeUrl(request.getParameter("rid")):String.valueOf(assignmentFolder.getRequirementId())) %>&auto-populate=true<%= (request.getParameter("popup") != null?"&popup=true":"") %>" onSubmit="return checkForm(this);">
  <%= showError(request, "actionError", false) %>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:evaluate if="<%= assignmentFolder.getId() == -1 %>">
              <ccp:label name="projectsCenterAssignments.folderAdd.addFolder">Add Activity Folder</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= assignmentFolder.getId() != -1 %>">
              <ccp:label name="projectsCenterAssignments.folderAdd.updateFoldser">Update Activity Folder</ccp:label>
          </ccp:evaluate>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td valign="top" nowrap class="formLabel"><ccp:label name="projectsCenterActivities.addFolder.name">Name</ccp:label></td>
        <td valign="top" nowrap>
          <input type="text" name="name" size="57" maxlength="150" value="<%= toHtmlValue(assignmentFolder.getName()) %>"><span class="required">*</span> <%= showAttribute(request, "nameError") %>
        </td>
      </tr>
  <ccp:evaluate if="<%= assignmentFolder.getId() == -1 %>">
  <%-- Temp. fix for Weblogic --%>
  <%
  int assignmentFolderIndent = assignmentFolder.getIndent() > -1 ? assignmentFolder.getIndent() : Integer.parseInt(request.getParameter("prevIndent"));
  %>
      <tr>
        <td class="formLabel" nowrap>
          <ccp:label name="projectsCenterActivities.addFolder.indentLevel">Indent Level</ccp:label>
        </td>
        <td>
          <ccp:spinner name="indent" value="<%= assignmentFolderIndent %>" min="0" max="<%= maxIndent %>"/>
        </td>
      </tr>
  </ccp:evaluate>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterActivities.addFolder.details">Details</ccp:label></td>
        <td>
          <table border="0" cellpadding="0" cellspacing="0" class="empty">
            <tr>
              <td>
                <textarea rows="8" name="description" cols="55"><c:out value="<%= assignmentFolder.getDescription() %>" /></textarea><br />
                <%= showAttribute(request, "descriptionError") %>
              </td>
              <td valign="top">
                &nbsp;<%--<font color="red">*</font>--%>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </tbody>
  </table>
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>" onclick="this.form.donew.value='false'">
<ccp:evaluate if="<%= assignmentFolder.getId() == -1 %>">
  <input type="submit" value="<ccp:label name="button.saveAndNew">Save & New</ccp:label>" onclick="this.form.donew.value='true'">
</ccp:evaluate>
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="<%= (request.getParameter("popup") != null?"window.close();":"window.location.href='" + ctx + "/ProjectManagement.do?command=ProjectCenter&pid=" + project.getId()  + ("Requirements".equals(request.getParameter("return"))?"&section=Requirements":"&section=Assignments") + "';") %>;">
  <input type="hidden" name="id" value="<%= assignmentFolder.getId() %>">
  <input type="hidden" name="parentId" value="<%= (assignmentFolder.getId() == -1 ? StringUtils.toHtmlValue(request.getParameter("parentId")):String.valueOf(assignmentFolder.getParentId())) %>">
  <input type="hidden" name="projectId" value="<%= project.getId() %>">
  <input type="hidden" name="requirementId" value="<%= (assignmentFolder.getId() == -1 ? StringUtils.toHtmlValue(request.getParameter("rid")):String.valueOf(assignmentFolder.getRequirementId())) %>">
  <input type="hidden" name="modified" value="<%= assignmentFolder.getModifiedString() %>">
  <input type="hidden" name="donew" value="false">
  <input type="hidden" name="return" value="<%= request.getAttribute("return") %>">
  <input type="hidden" name="param" value="<%= project.getId() %>">
  <input type="hidden" name="param2" value="<%= (assignmentFolder.getId() == -1 ? StringUtils.toHtmlValue(request.getParameter("rid")):String.valueOf(assignmentFolder.getRequirementId())) %>">
  <input type="hidden" name="prevIndent" value="<%= assignmentFolder.getPrevIndent() > -1 ? String.valueOf(assignmentFolder.getPrevIndent()) : StringUtils.toHtmlValue(request.getParameter("prevIndent")) %>">
  <input type="hidden" name="prevMapId" value="<%= assignmentFolder.getPrevMapId() > -1 ? String.valueOf(assignmentFolder.getPrevMapId()) : StringUtils.toHtmlValue(request.getParameter("prevMapId")) %>">
</form>
</body>
