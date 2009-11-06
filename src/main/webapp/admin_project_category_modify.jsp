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
<%@ page import="com.concursive.connect.Constants" %>
<jsp:useBean id="projectCategory" class="com.concursive.connect.web.modules.profile.dao.ProjectCategory" scope="request" />
<jsp:useBean id="parentCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request" />
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<script language="JavaScript" TYPE="text/javascript" SRC="javascript/checkNumber.js"></script>
<%@ include file="initPage.jsp" %>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminApplication.do">Manage Application Settings</a> >
<a href="<%= ctx %>/AdminProjectCategories.do?command=List">Site Categories</a> >
<% if (projectCategory.getId() != -1) { %>
Modify Site Category
<% } else { %>
Add Site Category
<% } %>
<br /><br />
<script language="JavaScript" type="text/javascript">
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.description.focus();} );
  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
  }
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }
  function checkForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";

    //Check required field
    if (form.description.value == "") {
      messageText += "- Name is a required field.\r\n";
      formTest = false;
    }
    if (!checkNumber(form.level.value)) {
      messageText += "- A number is required for display order.\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The site category form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form name="inputForm" method="post" action="<%= ctx %>/AdminProjectCategories.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);">
  <table cellpadding="4" cellspacing="0" width="100%" class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
            Site Category Information
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="">Name</ccp:label></td>
        <td>
          <input type="text" name="description" size="30" value="<%=toHtmlValue(projectCategory.getDescription()) %>"/><span class="required">*</span>
          <%= showAttribute(request,"descriptionError") %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="">Parent Category</ccp:label></td>
        <td>
          <% parentCategoryList.setEmptyHtmlSelectRecord("None"); %>
          <%= parentCategoryList.getHtmlSelect("parentCategoryId", projectCategory.getParentCategoryId()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td class="formLabel">
          Image
        </td>
        <td>
          <%
            Iterator files = fileItemList.iterator();
            while (files.hasNext()) {
              FileItem thisFile = (FileItem)files.next();
              if (thisFile.getId() == projectCategory.getLogoId()){
          %>
              <%= thisFile.getFullImageFromAdmin(ctx) %>&nbsp;
          <%
              }
            }
          %>
          <ccp:evaluate if="<%= fileItemList.size() > 0 %>"><br /></ccp:evaluate>
          <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
          <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.PROJECT_CATEGORY_FILES %>&liid=<%= projectCategory.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&popup=true"
             rel="shadowbox" title="Share an attachment">
          <% if (projectCategory.getLogoId() != -1) { %>
                Replace Image
          <%} else {%>
                Attach Image
              <%}%>
          </a>
          <input type="hidden" id="attachmentList" name="attachmentList" value="" />
          <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
          <% if (projectCategory.getLogoId() != -1) { %>
            <input type="hidden" name="logoId" value="<%= projectCategory.getLogoId() %>" />
          <%}%>
        </td>
       </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel">Enabled</td>
        <td>
          <input type="checkbox" name="enabled" value="true" <%= projectCategory.getEnabled() ? " CHECKED":"" %> />
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel">For logged in users only</td>
        <td>
          <input type="checkbox" name="sensitive" value="true" <%= projectCategory.getSensitive() ? " CHECKED":"" %> />
        </td>
      </tr>
      <tr class="containerBody">
        <td class="formLabel" valign="top">
          Style
        </td>
        <td>
          <textarea name="style" cols="80" rows="10"><%= toString(projectCategory.getStyle()) %></textarea>
        </td>
       </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel">Style Enabled</td>
        <td>
          <input type="checkbox" name="styleEnabled" value="true" <%= projectCategory.getStyleEnabled() ? " CHECKED":"" %> />
        </td>
      </tr>
      <tr>
        <td nowrap class="formLabel">Display Order</td>
        <td>
          <input type="text" name="level" size="5" maxlength="5" value='<%= projectCategory.getLevel() == -1 ? "" : projectCategory.getLevel() %>'/>
        </td>
      </tr>
    </tbody>
  </table>
  <input type="hidden" name="id" value="<%= projectCategory.getId() %>">
  <input type="hidden" name="dosubmit" value="true">
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>">
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="this.form.dosubmit.value='false';window.location.href='<%= ctx %>/AdminProjectCategories.do?command=List'">
</form>