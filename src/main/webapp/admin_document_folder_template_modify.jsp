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
<jsp:useBean id="documentFolderTemplate" class="com.concursive.connect.web.modules.documents.dao.DocumentFolderTemplate" scope="request" />
<jsp:useBean id="projectCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminApplication.do">Manage Application Settings</a> >
<a href="<%= ctx %>/AdminDocumentFolderTemplates.do?command=List">Document Folder Templates</a> >
<% if (documentFolderTemplate.getId() != -1) { %>
Modifying an existing document folder template
<% } else { %>
Adding a new document folder template
<% } %>
<br /><br />
<script type="text/javascript" language="JavaScript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required field
    if (form.folderNames.value == "") {
      messageText += "- Folder names is a required field.\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form name="inputForm" method="post" action="<%= ctx %>/AdminDocumentFolderTemplates.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);">
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
         Document Folder Template Information
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel">Site Category</td>
      <td>
        <%= projectCategoryList.getHtmlSelect("projectCategoryId", documentFolderTemplate.getProjectCategoryId()) %>
      </td>
      <tr class="containerBody">
        <td valign="top" nowrap class="formLabel"><ccp:label name="documentFolderTemplate.content">Folder Names</ccp:label></td>
        <td>
            <table border="0" cellpadding="0" cellspacing="0" class="empty" width="100%">
              <tr>
                <td>
              <textarea name="folderNames" cols="30" rows="5"><%=toString(documentFolderTemplate.getFolderNames())%></textarea>
                </td>
                <td nowrap="nowrap" valign="top" align="left" width="100%">
                  <span class="required">*</span>
            <%= showAttribute(request,"folderNamesError") %>
                </td>
              </tr>
            </table>
        </td>
      </tr>
    </tbody>
  </table>
  <br />
  <input type="hidden" name="id" value="<%= documentFolderTemplate.getId() %>">
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>">
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/AdminDocumentFolderTemplates.do?command=List'">
</form>
