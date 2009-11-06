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
<%@ page import="com.concursive.connect.web.modules.discussion.dao.Reply" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="forum" class="com.concursive.connect.web.modules.discussion.dao.Forum" scope="request"/>
<jsp:useBean id="topic" class="com.concursive.connect.web.modules.discussion.dao.Topic" scope="request"/>
<jsp:useBean id="reply" class="com.concursive.connect.web.modules.discussion.dao.Reply" scope="request"/>
<jsp:useBean id="messageToQuote" class="java.lang.String" scope="request"/>
<jsp:useBean id="quoteMessage" class="java.lang.String" scope="request"/>
<%--
<jsp:useBean id="distributionList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
--%>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<body onLoad="document.inputForm.body.focus();">
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
  function quoteThisMessage() {
  	document.getElementById('messageBody').value = '<%= StringUtils.jsStringEscape(messageToQuote) %>' + document.getElementById('messageBody').value + '\r\n';
  }
  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
  }
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }
</script>
<div class="portletWrapper">
  <h1><ccp:label name="projectsCenterIssues.reply.reply">Reply</ccp:label></h1>
  <div class="formContainer">
    <%= showError(request, "actionError") %>
    <portlet:actionURL var="saveFormUrl">
      <portlet:param name="portlet-command" value="reply-saveForm"/>
    </portlet:actionURL>
    <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
      <input type="hidden" name="issueId" value="${topic.id}">
      <input type="hidden" name="id" value="${reply.id}">
      <c:if test="${reply.replyToId > -1}">
        <input type="hidden" name="replyToId" value="${reply.replyToId}" />
      </c:if>
      <input type="hidden" name="modified" value="<%= reply.getModified() %>">
      <fieldset id="<ccp:label name="projectsCenterIssues.reply.messageReplyingTo">Message replying to...</ccp:label>">
        <legend><ccp:label name="projectsCenterIssues.reply.replyToMessage">Reply to Message</ccp:label></legend>
        <%-- Original message --%>
        <ccp:evaluate if='<%= "true".equals(quoteMessage)%>'>
          <fieldset id="<ccp:label name="projectsCenterIssues.reply.messageReplyingTo">Message replying to...</ccp:label>">
					  <legend><ccp:label name="projectsCenterIssues.reply.messageReplyingTo">Message replying to...</ccp:label></legend>
						<textarea disabled="true"><%= toString(messageToQuote) %></textarea>
						<input type="button" class="submit" value="<ccp:label name="projectsCenterIssues.reply.quoteThisMessage">Quote this message</ccp:label>" onclick="javascript:quoteThisMessage()" />
          </fieldset>
        </ccp:evaluate>
        <%-- If a question, and this is the author or admin --%>
        <c:if test="${topic.question && reply.replyToId > -1}">
          <fieldset>
            <legend>How would you rate the reply?</legend>
            <div>
              <label><input type="radio" class="radio" name="answered" value="<%= Reply.ANSWERED %>" <%= (reply.getAnswered() == Reply.ANSWERED || reply.getSolution())? " checked":""%> />
              Answers the question</label>
            </div>
            <div>
              <label><input type="radio" class="radio" name="answered" value="<%= Reply.HELPFUL %>" <%= (reply.getAnswered() == Reply.HELPFUL || reply.getHelpful())? " checked":""%> />
              Helpful, but doesn't answer the question</label>
            </div>
            <div>
              <label><input type="radio" class="radio" name="answered" value="<%= Reply.NOT_ANSWERED %>" <%= (reply.getAnswered() == Reply.NOT_ANSWERED)? " checked":""%> />
              Isn't helpful and doesn't answer the question</label>
            </div>
            <div>
              <label><input type="radio" class="radio" name="answered" value="<%= Reply.ANSWER_NOT_REQUIRED %>" <%= (reply.getAnswered() == Reply.ANSWER_NOT_REQUIRED)? " checked":""%> />
              The question no longer requires an answer</label>
            </div>
          </fieldset>
        </c:if>
        <label for="name"><ccp:label name="projectsCenterIssues.reply.subject">Reply Subject</ccp:label></label>
        <%= showAttribute(request, "subjectError") %>
				<input type="text" id="name" name="subject" size="57" maxlength="255" value="<%= toHtmlValue(reply.getSubject()) %>"><font color="red">*</font>
        <label for="messageBody"><ccp:label name="projectsCenterIssues.reply.message">Message <span class="required">*</span></ccp:label></label>
        <%= showAttribute(request, "bodyError") %>
        <textarea rows="10" name="body" id="messageBody" cols="70"><%= toString(reply.getBody()) %></textarea>
        <ccp:evaluate if="<%= forum.getAllowFileAttachments() && reply.getId() == -1 %>">
          <label>File Attachments</label>
          <%
            Iterator files = reply.getFiles().iterator();
            while (files.hasNext()) {
              FileItem thisFile = (FileItem)files.next();
          %>
                <%= toHtml(thisFile.getClientFilename()) %><ccp:evaluate if="<%= files.hasNext() %>">;</ccp:evaluate>
          <%
            }
          %>
          <%-- <ccp:evaluate if="<%= reply.getFiles().size() > 0 %>"><br /></ccp:evaluate> --%>
          <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
          <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.DISCUSSION_FILES_REPLY %>&pid=${project.id}&liid=${reply.id}&selectorId=<%= FileItem.createUniqueValue() %>"
             rel="shadowbox" title="Share a file"><ccp:label name="projectsCenterIssues.reply.attachFile">Attach Files</ccp:label></a>
          <input type="hidden" id="attachmentList" name="attachmentList" value="" />
          <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
        </ccp:evaluate>
        <%--<ccp:evaluate if="<%= reply.getId() == -1 || !distributionList.hasKey(String.valueOf(User.getId())) %>">
          <label for="emailUpdates"><input type="checkbox" name="emailUpdates" id="emailUpdates" value="ON" class="checkbox" /> Send me an email every time this message is replied to</label>
        </ccp:evaluate>
        <ccp:evaluate if="<%= reply.getId() > -1 && distributionList.hasKey(String.valueOf(User.getId())) %>">
          <label for="doNotEmailUpdates"><input type="checkbox" name="doNotEmailUpdates" id="doNotEmailUpdates" value="ON" class="checkbox" /> Do not send me an email when this message is replied to (currently subscribed)</label>
        </ccp:evaluate>--%>
      </fieldset>
      <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>" name="save" class="submit">
      <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/topic/<%= topic.getId() %>';" class="cancel">
    </form>
  </div>
</div>
</body>
