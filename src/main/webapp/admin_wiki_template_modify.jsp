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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="wikiTemplate" class="com.concursive.connect.web.modules.wiki.dao.WikiTemplate" scope="request" />
<jsp:useBean id="projectCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminApplication.do">Manage Application Settings</a> >
<a href="<%= ctx %>/AdminWikiTemplates.do?command=List">Wiki Templates</a> >
<% if (wikiTemplate.getId() != -1) { %>
Modifying an existing wiki template
<% } else { %>
Adding a new wiki template
<% } %>
<br /><br />
<script type="text/javascript" language="JavaScript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required field
    if (form.title.value == "") {
      messageText += "- Title is a required field.\r\n";
      formTest = false;
    }
    if (form.content.value == "") {
      messageText += "- Content is a required field.\r\n";
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
<form name="inputForm" method="post" action="<%= ctx %>/AdminWikiTemplates.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);">
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
         Wiki Template Information
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel">Site Category</td>
      <td>
        <%= projectCategoryList.getHtmlSelect("projectCategoryId", wikiTemplate.getProjectCategoryId()) %>
      </td>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="wikiTemplate.content">Title</ccp:label></td>
        <td>
          <input type="text" name="title" value="<%=toHtmlValue(wikiTemplate.getTitle()) %>"/><span class="required">*</span>
          <%= showAttribute(request,"titleError") %>
        </td>
      </tr>
      <tr class="containerBody">
          <td nowrap class="formLabel" valign="top">
              <ccp:label name="wikiTemplate.content">Content</ccp:label>
          </td>
          <td>
            <table border="0" cellpadding="0" cellspacing="0" class="empty" width="100%">
              <tr>
                <td width="100%">
                  <textarea name="content" id="content" rows="25" cols="80" style="width:100%;"><c:out value="<%= wikiTemplate.getContent() %>" /></textarea>
                </td>
                <td nowrap="nowrap" valign="top">
                  <span class="required">*</span>
                  <%= showAttribute(request, "contentError") %>
                </td>
              </tr>
            </table>
          </td>
      </tr>
    </tbody>
  </table>
  <input type="hidden" name="id" value="<%= wikiTemplate.getId() %>">
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>">
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/AdminWikiTemplates.do?command=List'">
</form>
