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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="category" class="com.concursive.connect.web.modules.lists.dao.TaskCategory" scope="request"/>
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
    messageText += "- Name is a required field\r\n";
    formTest = false;
  }
  if (formTest == false) {
    messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
    form.dosubmit.value = "true";
    alert(messageText);
    return false;
  } else {
    return true;
  }
}
</script>
<form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementListsCategory.do?command=<%= category.getId()!=-1?"UpdateCategory":"InsertCategory" %>&id=<%= category.getId() %>&auto-populate=true" onSubmit="return checkForm(this);">
  <%= showError(request, "actionError") %>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:evaluate if="<%= category.getId() == -1 %>">
              <ccp:label name="projectsCenterLists.categories.add">Add</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= category.getId() != -1 %>">
              <ccp:label name="projectsCenterLists.categories.update">Update</ccp:label>
          </ccp:evaluate>
          <ccp:label name="projectsCenterLists.categories">List</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.categories.name">Name</ccp:label></td>
        <td>
          <input type="text" name="description" size="57" maxlength="80" value="<%= toHtmlValue(category.getDescription()) %>"><span class="required">*</span> <%= showAttribute(request, "descriptionError") %>
        </td>
      </tr>
    </tbody>
  </table>
  <input type="submit" class="submit" value="<ccp:label name="button.submit">Submit</ccp:label>">
  <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/lists';">
  <input type="hidden" name="level" value="<%= category.getLevel() %>">
  <input type="hidden" name="enabled" value="<%= category.getEnabled() %>">
  <input type="hidden" name="defaultItem" value="<%= category.getDefaultItem() %>">
  <input type="hidden" name="pid" value="<%= project.getId() %>">
  <input type="hidden" name="dosubmit" value="true">
</form>
</body>
