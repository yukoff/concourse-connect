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
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.connect.web.modules.login.utils.UserUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.web.modules.discussion.dao.Reply" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="forum" class="com.concursive.connect.web.modules.discussion.dao.Forum" scope="request"/>
<jsp:useBean id="topic" class="com.concursive.connect.web.modules.discussion.dao.Topic" scope="request"/>
<jsp:useBean id="projectReplyInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="request"/>
<jsp:useBean id="replyList" class="com.concursive.connect.web.modules.discussion.dao.ReplyList" scope="request"/>
<jsp:useBean id="reply" class="com.concursive.connect.web.modules.discussion.dao.Reply" scope="request"/>
<jsp:useBean id="quoteMessage" class="java.lang.String" scope="request"/>
<jsp:useBean id="messageToQuote" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<portlet:renderURL var="replyToTopicUrl">
  <portlet:param name="portlet-action" value="create"/>
  <portlet:param name="portlet-object" value="reply"/>
  <portlet:param name="topic" value="${topic.id}"/>
</portlet:renderURL>
<portlet:renderURL var="modifyTopicUrl">
  <portlet:param name="portlet-action" value="modify"/>
  <portlet:param name="portlet-object" value="topic"/>
  <portlet:param name="portlet-value" value="${topic.id}"/>
</portlet:renderURL>
<portlet:actionURL var="deleteTopicUrl">
  <portlet:param name="portlet-action" value="show"/>
  <portlet:param name="portlet-object" value="topic"/>
  <portlet:param name="portlet-value" value="${topic.id}"/>
  <portlet:param name="portlet-command" value="topic-delete"/>
</portlet:actionURL>
<%
  User thisUser = UserUtils.loadUser(topic.getEnteredBy());
  request.setAttribute("thisUser", thisUser);
%>

<%-- Reply Scripts --%>
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

  <div id="reply" class="menu">
    <div class="portlet-message-success"><p>Thank you for your valuable feedback.</p></div>
  </div>
  <ccp:evaluate if="<%= projectReplyInfo.getCurrentOffset() == 0 %>">
    <h2>
      <%= toHtml(topic.getSubject()) %>
    </h2>
    <p><a href="${ctx}/show/<%= project.getUniqueId() %>/forum/${forum.id}"><ccp:label name="discussion.backToTopics">Back to Topics</ccp:label></a></p>
    <ccp:evaluate if="<%= !User.isLoggedIn() && project.getFeatures().getAllowGuests() %>">
      <p><ccp:label name="projectsCenterIssues.needLogin">You need to be logged in to post messages.</ccp:label></p>
    </ccp:evaluate>
    <ccp:evaluate if="<%= topic.getQuestion() %>">
      <ccp:evaluate if="<%= topic.getSolutionReplyId() != -1 %>">
        <div class="portlet-message-success">
          <img src="${ctx}/images/icons/check_button_green.png" alt="Question Answered Icon">
          this question is answered
        </div>
      </ccp:evaluate>
      <ccp:evaluate if="<%= topic.getSolutionReplyId() == -1 %>">
        <div class="portlet-message-info">
          <img src="${ctx}/images/icons/question_button_orange.png" alt="Not Answered Icon">
          this question is unanswered
        </div>
      </ccp:evaluate>
    </ccp:evaluate>
    <div class="postContainer postFirst">
      <div class="postHeader">
        <h3>
          <%= toHtml(topic.getSubject()) %>
        </h3>
        <ccp:permission name="project-discussion-topics-edit,project-discussion-topics-delete" if="any">
          <div class="portlet-menu">
            <ul>
              <%-- Edit Topic: Either have permission to make changes, or you are the user that wrote the article --%>
              <ccp:permission name="project-discussion-topics-edit">
                <li>
                  <img src="${ctx}/images/icons/pencil.png" alt="Edit This Post" width="10" height="10">
                  <a href="${modifyTopicUrl}">

                    <ccp:label name="projectsCenterIssues.details.edit">Edit</ccp:label>
                  </a>
                </li>
              </ccp:permission>
              <ccp:permission name="project-discussion-topics-edit" if="none">
                <ccp:evaluate if="<%= topic.getEnteredBy() == User.getId() %>">
                  <li>
                    <img src="${ctx}/images/icons/pencil.png" alt="Edit This Post" width="10" height="10">
                    <a href="${modifyTopicUrl}">
                      <ccp:label name="projectsCenterIssues.details.edit">Edit</ccp:label>
                    </a>
                  </li>
                </ccp:evaluate>
              </ccp:permission>
              <%-- Delete Topic --%>
              <ccp:permission name="project-discussion-topics-delete">
                <li>
                  <img src="${ctx}/images/icons/minus_circle.png" alt="Edit This Post" width="10" height="10">
                  <a href="javascript:confirmDelete('${deleteTopicUrl}');">
                    <ccp:label name="projectsCenterIssues.details.delete">Delete</ccp:label>
                  </a>
                </li>
              </ccp:permission>
            </ul>
          </div>
        </ccp:permission>
        <%--<ccp:label name="projectsCenterIssues.details.postedOn">Posted on</ccp:label>--%>
        <span class="date">
           <ccp:tz timestamp="<%= topic.getEntered() %>" pattern="MMM, dd yyyy"/>
        </span>
        <span class="time">
          <ccp:tz timestamp="<%= topic.getEntered() %>" pattern="h:mm a"/>
        </span>
        <ccp:evaluate if="<%= !(topic.getModified().equals(topic.getEntered())) %>">
            <span class="modified"><ccp:label name="projectsCenterIssues.details.edited">(edited)</ccp:label></span>
        </ccp:evaluate>
      </div>
      <div class="postBody">
        <div class="postUser">
          <div class="profileImageContainer">
            <c:choose>
              <c:when test="${!empty project.category.logo}">
                <div class="imageContainer">
                  <c:choose>
                    <c:when test="${!empty thisUser.profileProject.logo}">
                      <img alt="<c:out value="${thisUser.profileProject.title}"/> photo" src="${ctx}/image/<%= thisUser.getProfileProject().getLogo().getUrlName(75,75) %>" />
                    </c:when>
                    <c:when test="${!empty thisUser.profileProject.category.logo}">
                      <img alt="Default user photo" src="${ctx}/image/<%= thisUser.getProfileProject().getCategory().getLogo().getUrlName(75,75) %>" class="default-photo" />
                    </c:when>
                  </c:choose>
                </div>
              </c:when>
              <c:when test="${empty project.category.logo}">
                <div class="profileImageBackground">
                  <div class="noPhoto">
                    <p>no photo</p>
                  </div>
                </div>
              </c:when>
            </c:choose>
          </div>
          <p>
            <ccp:label name="projectsCenterIssues.details.by">By</ccp:label> <ccp:username id="<%= topic.getEnteredBy() %>"/>
          </p>
        </div>


        <div class="postContent">
          <%= toHtml(topic.getBody()) %>
        </div>
      </div>
      <%-- file list --%>
      <div class="postFooter<ccp:evaluate if="<%= topic.hasFiles() %>">Files</ccp:evaluate>" >
        <ccp:evaluate if="<%= topic.hasFiles() %>">
        <p class="files">Files</p>
          <%
            Iterator issueFiles = topic.getFiles().iterator();
            while (issueFiles.hasNext()) {
              FileItem thisFile = (FileItem) issueFiles.next();
          %>
                  <%= thisFile.getImageTag("-23", ctx) %>
                  <a href="<%= ctx %>/DiscussionActions.do?command=Download&pid=<%= project.getId() %>&iid=<%= topic.getId() %>&fid=<%= thisFile.getId() %><%= thisFile.isImageFormat() ? "&view=true&ext=" + thisFile.getExtension() : "" %>"<ccp:evaluate if="<%= thisFile.isImageFormat() %>"> rel="shadowbox[Images]"</ccp:evaluate>><%= toHtml(thisFile.getClientFilename()) %></a>
          <%
            }
          %>
        </ccp:evaluate>
      </div>
      <ccp:permission name="project-discussion-messages-reply">
        <div class="actions">
          <ul>
            <%-- Quote this message --%>
            <ccp:permission name="project-discussion-messages-reply">
              <li><a href="${replyToTopicUrl}"><ccp:label name="projectsCenterIssues.details.reply">Reply</ccp:label></a></li>
            </ccp:permission>
          </ul>
        </div>
      </ccp:permission>
    </div>
    <ccp:permission name="project-discussion-topics-view">
      <div class="userInputFooter">
        <ccp:evaluate if="<%= topic.getRatingCount() > 0 %>">
          <p>(<%= topic.getRatingValue() %> out of <%= topic.getRatingCount() %> <%= topic.getRatingCount() == 1 ? " person" : " people"%> found this post useful.)</p>
        </ccp:evaluate>
        <ccp:evaluate if='<%= topic.getInappropriateCount() > 0 && ProjectUtils.hasAccess(topic.getProjectId(), User, "project-reviews-admin")%>'>
          <p>(<%= topic.getInappropriateCount() %><%= topic.getInappropriateCount() == 1? " person" : " people"%> found this post inappropriate.)</p>
        </ccp:evaluate>
          <ccp:permission name="project-discussion-topics-view">
            <%-- any user who is not the author of the topic can mark the rate the topic  --%>
            <ccp:evaluate if="<%= (topic.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
              <p>Is this topic useful?
                <portlet:renderURL var="ratingUrl" windowState="maximized">
                  <portlet:param name="portlet-command" value="topic-setRating"/>
                  <portlet:param name="id" value="${topic.id}"/>
                  <portlet:param name="v" value="1"/>
                  <portlet:param name="out" value="text"/>
                </portlet:renderURL>
                <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + topic.getId() %>','reply');">Yes</a>&nbsp;
                <portlet:renderURL var="ratingUrl" windowState="maximized">
                  <portlet:param name="portlet-command" value="topic-setRating"/>
                  <portlet:param name="id" value="${topic.id}"/>
                  <portlet:param name="v" value="0"/>
                  <portlet:param name="out" value="text"/>
                </portlet:renderURL>
                <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + topic.getId() %>','reply');">No</a>&nbsp;
                <ccp:evaluate if="<%= topic.getId() > -1 && User.isLoggedIn() %>">
                <a href="javascript:showPanel('Mark this topic as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=topic&pid=${project.id}&id=${topic.id}',700)">Report this as inappropriate</a>
                </ccp:evaluate>
              </p>
              <div id="message_<%= topic.getId() %>"></div>
            </ccp:evaluate>
          </ccp:permission>
        </div>
      </ccp:permission>
    </ccp:evaluate>
<%
    int rowid = 1;
    int replyCount = projectReplyInfo.getCurrentOffset();
    Iterator i = replyList.iterator();
    while (i.hasNext()) {
      rowid = (rowid != 1?1:2);
      Reply thisReply = (Reply) i.next();
      request.setAttribute("thisReply", thisReply);
      ++replyCount;
      User replyUser = UserUtils.loadUser(thisReply.getEnteredBy());
      request.setAttribute("replyUser", replyUser);
%>
  <div class="postContainer">
    <div class="postHeader">
      <h3><%= toHtml(thisReply.getSubject()) %></h3>
      <portlet:renderURL var="modifyReplyUrl">
        <portlet:param name="portlet-action" value="modify"/>
        <portlet:param name="portlet-object" value="reply"/>
        <portlet:param name="portlet-value" value="${thisReply.id}"/>
      </portlet:renderURL>
      <portlet:actionURL var="deleteReplyUrl">
        <portlet:param name="reply" value="${thisReply.id}"/>
        <portlet:param name="portlet-command" value="reply-delete"/>
      </portlet:actionURL>
      <ccp:permission name="project-discussion-messages-edit,project-discussion-messages-delete" if="any">
        <div class="portlet-menu">
          <ul>
            <%-- Edit Message --%>
            <ccp:permission name="project-discussion-messages-edit">
              <li>
                <a href="${modifyReplyUrl}">
                  <img src="${ctx}/images/icons/pencil.png" alt="Edit This Post" width="10" height="10">
                  <ccp:label name="projectsCenterIssues.details.edit">Edit</ccp:label>
                </a>
              </li>
            </ccp:permission>
            <ccp:permission name="project-discussion-messages-edit" if="none">
              <ccp:evaluate if="<%= thisReply.getEnteredBy() == User.getId() %>">
                <li>
                  <a href="${modifyReplyUrl}">
                    <img src="${ctx}/images/icons/pencil.png" alt="Edit This Post" width="10" height="10">
                    <ccp:label name="projectsCenterIssues.details.edit">Edit</ccp:label>
                  </a>
                </li>
              </ccp:evaluate>
            </ccp:permission>
            <%-- Delete Message --%>
            <ccp:permission name="project-discussion-messages-delete">
              <li>
                <a href="javascript:confirmDelete('${deleteReplyUrl}');">
                  <img src="${ctx}/images/icons/minus_circle.png" alt="Delete this post" width="10" height="10">
                  <ccp:label name="projectsCenterIssues.details.delete">Delete</ccp:label>
                </a>
              </li>
            </ccp:permission>
          </ul>
        </div>
      </ccp:permission>
      <span class="count"><%= replyCount %>.</span>
      <span class="date">
        <%-- <ccp:label name="projectsCenterIssues.details.postedOn">Posted on</ccp:label> --%>
        <ccp:tz timestamp="<%= thisReply.getEntered() %>" pattern="MMM, dd yyyy"/>
      </span>
        <span class="time">
          <ccp:tz timestamp="<%= thisReply.getEntered() %>" pattern="h:mm a"/>
      </span>
      <c:if test="${topic.question && topic.solutionReplyId == thisReply.id}">
        <div class="portlet-message-success">
          <p>this reply is marked as the answer</p>
        </div>
      </c:if>
      <c:if test="${thisReply.inappropriateCount > 0}">
        <div class="portlet-message-info">
          <%= thisReply.getInappropriateCount() %>
          <c:if test="${thisReply.inappropriateCount == 1}">
            person
          </c:if>
          <c:if test="${thisReply.inappropriateCount > 1}">
            people
          </c:if>
          found this inappropriate.
        </div>
      </c:if>
      <c:if test="${thisReply.ratingCount > 0 && thisReply.ratingValue > 0}">
        <div class="portlet-message-info">
          <%= thisReply.getRatingValue() %>
          <c:if test="${thisReply.ratingCount > 1}">
            out of <%= thisReply.getRatingCount() %>
            people
          </c:if>
          <c:if test="${thisReply.ratingCount == 1}">
            person
          </c:if>
          found this helpful.
        </div>
      </c:if>
     </div>
     <div class="postBody">
       <div class="postUser">
        <div class="profileImageContainer">
          <c:choose>
            <c:when test="${!empty project.category.logo}">
              <div class="imageContainer">
                <c:choose>
                  <c:when test="${!empty replyUser.profileProject.logo}">
                    <img alt="<c:out value="${replyUser.profileProject.title}"/> photo" src="${ctx}/image/<%= replyUser.getProfileProject().getLogo().getUrlName(75,75) %>" />
                  </c:when>
                  <c:when test="${!empty replyUser.profileProject.category.logo}">
                    <img alt="Default user photo" src="${ctx}/image/<%= replyUser.getProfileProject().getCategory().getLogo().getUrlName(75,75) %>" class="default-photo" />
                  </c:when>
                </c:choose>
              </div>
            </c:when>
            <c:when test="${empty project.category.logo}">
              <div class="profileImageBackground">
                <div class="noPhoto">
                  <p>no photo</p>
                </div>
              </div>
            </c:when>
          </c:choose>
        </div>
        <ccp:label name="projectsCenterIssues.details.by">By</ccp:label> <ccp:username id="<%= thisReply.getEnteredBy() %>"/>
      </div>

      <div class="postContent">
        <div id="reply_<%= thisReply.getId() %>"></div>
        <ccp:evaluate if="<%= !(thisReply.getModified().equals(thisReply.getEntered())) %>">
          <ccp:label name="projectsCenterIssues.details.edited">(edited)</ccp:label><br />
          <br />
        </ccp:evaluate>
        <%= toHtml(thisReply.getBody()) %>
      </div>
    </div>
    <%-- file list --%>
    <div class="postFooter<ccp:evaluate if="<%= thisReply.hasFiles() %>">Files</ccp:evaluate>" >
      <ccp:evaluate if="<%= thisReply.hasFiles() %>">
        <p class="files">Files</p>
        <%
          Iterator files = thisReply.getFiles().iterator();
          while (files.hasNext()) {
            FileItem thisFile = (FileItem) files.next();
        %>
            <%= thisFile.getImageTag("-23", ctx) %>
            <a href="<%= ctx %>/DiscussionActions.do?command=Download&pid=<%= project.getId() %>&rid=<%= thisReply.getId() %>&fid=<%= thisFile.getId() %><%= thisFile.isImageFormat() ? "&view=true&ext=" + thisFile.getExtension() : "" %>"<ccp:evaluate if="<%= thisFile.isImageFormat() %>"> rel="shadowbox[Images]"</ccp:evaluate>><%= toHtml(thisFile.getClientFilename()) %></a><br />
        <%
          }
        %>
      </ccp:evaluate>
    </div>
    <ccp:permission name="project-discussion-messages-reply">
      <div class="actions">
        <ul>
          <%-- Allow users, other than the author of the reply to mark the reply as inappropriate --%>
          <ccp:permission name="project-discussion-messages-reply">
            <ccp:evaluate if="<%= (thisReply.getEnteredBy() !=  User.getId()) || User.getAccessAdmin() %>">
              <portlet:renderURL var="replySetInappropriate" windowState="maximized">
                <portlet:param name="portlet-command" value="reply-setInappropriate"/>
                <portlet:param name="reply" value="${thisReply.id}"/>
                <portlet:param name="out" value="text"/>
              </portlet:renderURL>
              <li><a href="javascript:copyRequest('${replySetInappropriate}','<%= "reply_" + thisReply.getId() %>','reply');">Report as inappropriate</a></li>
            </ccp:evaluate>
          </ccp:permission>
          <%-- All the issue auther/admin to rate the reply if the issue is a question --%>
          <portlet:renderURL var="replyToReplyUrl">
            <portlet:param name="portlet-action" value="create"/>
            <portlet:param name="portlet-object" value="reply"/>
            <portlet:param name="replyTo" value="${thisReply.id}"/>
          </portlet:renderURL>
          <ccp:evaluate if="<%= topic.getQuestion() %>">
            <ccp:permission name="project-discussion-messages-reply">
              <ccp:evaluate if="<%= (topic.getEnteredBy() ==  User.getId() || User.getAccessAdmin()) %>">
                <li><a href="${replyToReplyUrl}">Rate This</a></li>
              </ccp:evaluate>
            </ccp:permission>
          </ccp:evaluate>
          <%-- Quote this message --%>
          <ccp:permission name="project-discussion-messages-reply">
            <%-- <img src="<%= ctx %>/images/icons/16_add_comment.gif" border="0" align="absmiddle" /> --%>
            <li><a href="${replyToReplyUrl}"><ccp:label name="projectsCenterIssues.details.reply">Reply</ccp:label></a></li>
          </ccp:permission>
        </ul>
      </div>
    </ccp:permission>
  </div>
  <%
    }
  %>
  <div class="pagination">
    <ccp:paginationControl object="projectReplyInfo"/>
  </div>
  <%
    if (request.getParameter("popup") != null) {
  %>
    [<a href="javascript:window.close();"><ccp:label name="projectsCenterIssues.details.closeWindow">Close Window</ccp:label></a>]
  <%
    } else {
  %>
    <%-- Reply Form --%>
    <ccp:permission name="project-discussion-messages-reply">
      <div class="formContainer">
        <%= showError(request, "actionError") %>
        <portlet:actionURL var="saveFormUrl">
          <portlet:param name="portlet-command" value="reply-saveForm"/>
        </portlet:actionURL>
        <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
          <input type="hidden" name="issueId" value="${topic.id}">
          <fieldset>
            <legend><ccp:label name="projectsCenterIssues.reply.postAReply">Post a Reply</ccp:label></legend>
            <label for="name"><ccp:label name="projectsCenterIssues.reply.subject">Reply Subject</ccp:label> <span class="required">*</span></label>
            <input type="text" id="name" name="subject" size="57" maxlength="255" value="RE: <%= toHtmlValue(topic.getSubject()) %>">
            <label for="messageBody"><ccp:label name="projectsCenterIssues.reply.message">Message <span class="required">*</span></ccp:label></label>
            <textarea rows="10" name="body" id="messageBody" cols="70"></textarea>
            <ccp:evaluate if="<%= forum.getAllowFileAttachments() %>">
              <label>File Attachments</label>
              <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" alt="Attachments" />
              <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.DISCUSSION_FILES_REPLY %>&pid=${project.id}&liid=${reply.id}&selectorId=<%= FileItem.createUniqueValue() %>"
                 rel="shadowbox" title="Share an attachment">Attach Files</a>
              <input type="hidden" id="attachmentList" name="attachmentList" value="" />
              <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
            </ccp:evaluate>
          </fieldset>
          <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>" name="save" class="submit">
        </form>
      </div>
    </ccp:permission>
  <%
    }
  %>
