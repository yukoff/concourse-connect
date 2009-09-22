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
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.Constants" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="ticket" class="com.concursive.connect.web.modules.issues.dao.Ticket" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript" language="JavaScript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.comment.value == "") {
      messageText += "- Comment is a required field\r\n";
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
  function fileAttachmentSelector() {
    var projectId = '&pid=<%= project.getId() %>';
    var linkModuleId = '&lmid=<%= Constants.PROJECT_TICKET_FILES %>';
    var linkItemId = '&liid=<%= ticket.getId() %>';
    var selectorId = '&selectorId=<%= FileItem.createUniqueValue() %>';
    popURL('<%= ctx %>/FileAttachments.do?command=ShowForm' + projectId + linkModuleId + linkItemId + selectorId + '&popup=true','File_Attachments','480','520','yes','yes');
  }
  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
  }
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }

</script>
<body onLoad="document.ticketForm.comment.focus();">
  <div class="portletWrapper">
    <h1>
      <%--<img src="<%= ctx %>/images/icons/stock_macro-organizer-16.gif" border="0" align="absmiddle">--%>
      <ccp:label name="projectsCenterTickets.comments.comments">Add Comments</ccp:label>
      <%--<a href="<%= ctx %>/show/<%= project.getUniqueId() %>/issues"><ccp:label name="projectsCenterTickets.comments.tickets">Tickets</ccp:label></a> >--%>
      <span><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/issue/<%= ticket.getProjectTicketCount() %>"><ccp:label name="projectsCenterTickets.comments.ticketDetails">Back to Ticket Details</ccp:label></a></span>
    </h1>
    
    <div class="formContainer">
      <%= showError(request, "actionError") %>
      <form name="ticketForm" action="<%= ctx %>/ProjectManagementTickets.do?command=SaveComments" method="post" onSubmit="return checkForm(this);">
        <fieldset id="">
          <legend>
            Ticket # <%= ticket.getProjectTicketCount() %>
            <ccp:evaluate if="<%= ticket.isClosed() %>"> (<ccp:label name="projectsCenterTickets.comments.ticketClosedOn">This ticket was closed on</ccp:label> <ccp:tz timestamp="<%= ticket.getClosed() %>"/>)</ccp:evaluate>
            <ccp:evaluate if="<%= !ticket.isClosed() %>">(<ccp:label name="projectsCenterTickets.comments.open">Open</ccp:label>)</ccp:evaluate>
          </legend>
          <%-- Comments --%>
          <label for="comment"><ccp:label name="projectsCenterTickets.comments.userComments">User Comments</ccp:label></label>
          <textarea name="comment" cols="55" rows="5"></textarea>
          <label for="attachmentList"><ccp:label name="projectsCenterTickets.comments.supportingFiles">Supporting Files</ccp:label></label>
          <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
          <a href="javascript:fileAttachmentSelector();"><ccp:label name="projectsCenterTickets.comments.attachFiles">Attach Files</ccp:label></a>
          <input type="hidden" id="attachmentList" name="attachmentList" value="" />
          &nbsp;&nbsp;<input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
        </fieldset>
        <input type="submit" class="submit" " value="<ccp:label name="button.save">Save</ccp:label>">
        <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=Details&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
        <input type="hidden" name="pid" value="<%= project.getId() %>">
        <input type="hidden" name="id" value="<%= ticket.getId() %>">
        <input type="hidden" name="return" value="<%= toHtmlValue(request.getParameter("return")) %>">
      </form>
    </div>
  </div>
</body>
