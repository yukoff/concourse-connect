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
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="forum" class="com.concursive.connect.web.modules.discussion.dao.Forum" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<body onLoad="document.inputForm.subject.focus();">
<script language="JavaScript">
  function checkForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.subject.value == "") {
      messageText += "- Forum name is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The message could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
      alert(messageText);
      return false;
    } else {
      if (form.save.value != 'Please Wait...') {
        form.save.value='Please Wait...';
        form.save.disabled = true;
        return true;
      } else {
        return false;
      }
    }
  }
</script>
  <portlet:actionURL var="saveFormUrl">
    <portlet:param name="portlet-command" value="forum-saveForm"/>
  </portlet:actionURL>
  <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
    <h1>
      <ccp:evaluate if="<%= forum.getId() == -1 %>">
        <ccp:label name="projectsCenterIssues.categories.add">Add</ccp:label>
      </ccp:evaluate>
      <ccp:evaluate if="<%= forum.getId() != -1 %>">
        <ccp:label name="projectsCenterIssues.categories.modify">Modify</ccp:label>
      </ccp:evaluate>
    </h1>
    <p><a href="${ctx}/show/${project.uniqueId}/discussion"><ccp:label name="projectsCenterIssues.categories.forums">Back to Forums</ccp:label></a></p>
    <div class="formContainer">
      <span class="error"><%= showError(request, "actionError") %></span>
      <fieldset id="<ccp:evaluate if="<%= forum.getId() == -1 %>"><ccp:label name="projectsCenterIssues.categories.add">Add</ccp:label></ccp:evaluate><ccp:evaluate if="<%= forum.getId() != -1 %>"><ccp:label name="projectsCenterIssues.categories.modify">Modify</ccp:label></ccp:evaluate> <ccp:label name="projectsCenterIssues.categories.Forum">Forum</ccp:label>">
        <legend>
          <ccp:evaluate if="<%= forum.getId() == -1 %>">
            <ccp:label name="projectsCenterIssues.categories.add">Add</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= forum.getId() != -1 %>">
            <ccp:label name="projectsCenterIssues.categories.modify">Modify</ccp:label>
          </ccp:evaluate>
          <ccp:label name="projectsCenterIssues.categories.Forum">Forum</ccp:label>
        </legend>
        <label for="subject"><ccp:label name="projectsCenterIssues.categories.name">Name</ccp:label> <span class="required">*</span></label>
        <%= showAttribute(request, "subjectError") %>
        <input type="text" name="subject" id="subject" size="57" maxlength="255" value="<%= toHtmlValue(forum.getSubject()) %>" />
        <span class="characterCounter">255 characters max</span>
        <%
          String allowFilesCheck = "";
          if (forum.getAllowFileAttachments()) {
            allowFilesCheck = " checked";
          }
        %>
        <fieldset id="<ccp:label name="projectsCenterIssues.categories.options">Options</ccp:label>">
          <label for="allowFileAttachments">
            <input type="checkbox" id="allowFileAttachments" name="allowFileAttachments" value="ON"<%= allowFilesCheck %>><ccp:label name="projectsCenterIssues.categories.option.allowFiles">Allow file attachments in messages</ccp:label>
          </label>
        </fieldset>
      </fieldset>
      <input type="submit" class="submit" name="save" value="<ccp:label name="button.save">Save</ccp:label>">
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.location.href='${ctx}/show/${project.uniqueId}/discussion';">
      <input type="hidden" name="id" value="<%= forum.getId() %>">
      <input type="hidden" name="modified" value="<%= forum.getModified() %>">
      <input type="hidden" name="dosubmit" value="true">
    </div>
  </form>
</body>
