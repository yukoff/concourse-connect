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
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="forum" class="com.concursive.connect.web.modules.discussion.dao.Forum" scope="request"/>
<jsp:useBean id="topic" class="com.concursive.connect.web.modules.discussion.dao.Topic" scope="request"/>
<%--
<jsp:useBean id="distributionList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
--%>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<body onLoad="document.inputForm.subject.focus();">
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.subject.value == "") {
      messageText += "- Subject is a required field\r\n";
      formTest = false;
    }
    if (form.body.value == "") {
      messageText += "- Message is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
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
  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
  }
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }
</script>
<portlet:actionURL var="saveFormUrl">
  <portlet:param name="portlet-command" value="topic-saveForm"/>
</portlet:actionURL>
<form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
  <div class="portletWrapper">
    <h1>
      <%= toHtml(forum.getSubject()) %>
    </h1>
    <p><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/discussion"><ccp:label name="projectsCenterIssues.add.forums">Back to Forums</ccp:label></a></p>
    <div class="formContainer">
      <%--  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>">
      <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/forum/<%= forum.getId() %>';"> --%>
      <%= showError(request, "actionError") %>
      <fieldset id="<ccp:label name="projectsCenterIssues.add.topic">Topic</ccp:label>">
        <legend><ccp:label name="projectsCenterIssues.add.topic">Topic</ccp:label></legend>
        <input type="hidden" name="categoryId" value="<%= forum.getId() %>">
        <input type="hidden" name="id" value="<%= topic.getId() %>">
        <input type="hidden" name="modified" value="<%= topic.getModified() %>">
        <input type="hidden" name="return" value="<%= StringUtils.toHtmlValue(request.getParameter("return")) %>">
        <label for="subject"><ccp:label name="projectsCenterIssues.add.subject">Subject</ccp:label> <span class="required">*</span></label>
        <input type="text" name="subject" id="subject" size="57" maxlength="255" value="<%= toHtmlValue(topic.getSubject()) %>">
        <%= showAttribute(request, "subjectError") %>
        <span class="characterCounter">255 characters max</span>
        <fieldset id="<ccp:label name="projectsCenterIssues.categories.options">Options</ccp:label>">
          <label for="question">
            <input type="checkbox" id="question" name="question" <ccp:evaluate if="<%= topic.getQuestion() %>">checked</ccp:evaluate>/>Mark this topic as a question - this will help you track answers
          </label>
        </fieldset>
        <label for="body"><ccp:label name="projectsCenterIssues.add.message">Message <span class="required">*</span></ccp:label></label>
        <%= showAttribute(request, "bodyError") %>
        <textarea rows="10" name="body" id="body" cols="70"><%= toString(topic.getBody()) %></textarea>
        <ccp:evaluate if="<%= forum.getAllowFileAttachments() && topic.getId() == -1 %>">
          <label for="projectsCenterIssues.add.fileAttachments"><ccp:label name="projectsCenterIssues.add.fileAttachments">File attachments</ccp:label></label>
          <%
            Iterator files = topic.getFiles().iterator();
            while (files.hasNext()) {
              FileItem thisFile = (FileItem)files.next();
          %>
                <%= toHtml(thisFile.getClientFilename()) %><ccp:evaluate if="<%= files.hasNext() %>">;</ccp:evaluate>
          <%
            }
          %>
          <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
          <a href="${ctx}/FileAttachments.do?command=ShowForm&pid=<%= project.getId() %>&lmid=<%= Constants.DISCUSSION_FILES_TOPIC %>&liid=<%= topic.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&popup=true"
             rel="shadowbox" title="Share an attachment">Attach Files</a>
          <input type="hidden" id="attachmentList" name="attachmentList" value="" />
          <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
        </ccp:evaluate>
        <%--
        <tr class="containerBody">
          <td nowrap class="formLabel" valign="top">Watch</td>
          <td>
            <ccp:evaluate if="<%= topic.getId() == -1 || !distributionList.hasKey(String.valueOf(User.getId())) %>">
              <input type="checkbox" name="emailUpdates" value="ON" /> Send me an email every time this message is replied to
            </ccp:evaluate>
            <ccp:evaluate if="<%= topic.getId() > -1 && distributionList.hasKey(String.valueOf(User.getId())) %>">
              <input type="checkbox" name="doNotEmailUpdates" value="ON" /> Do not send me an email when this message is replied to (currently subscribed)
            </ccp:evaluate>
          </td>
        </tr>
        --%>
      </fieldset>
      <input type="submit" class="submit" value="<ccp:label name="button.save">Save</ccp:label>" name="save">
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/forum/<%= forum.getId() %>';"><br>
    </div>
  </div>
</form>
</body>
