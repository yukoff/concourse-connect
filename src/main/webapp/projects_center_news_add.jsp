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
<%@ page import="com.concursive.commons.text.StringUtils" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="blog" class="com.concursive.connect.web.modules.blog.dao.BlogPost" scope="request"/>
<jsp:useBean id="blogCategoryList" class="com.concursive.connect.web.modules.blog.dao.BlogPostCategoryList" scope="request"/>
<jsp:useBean id="taskCategoryList" class="com.concursive.connect.web.modules.lists.dao.TaskCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<%-- Use the minimal TinyMCE Editor for blog posts --%>
<jsp:include page="tinymce_blog_include.jsp" flush="true"/>
<script language="javascript" type="text/javascript">
  initEditor('intro,message');
  var ilId = <%= project.getId() %>;
</script>
<script language="JavaScript" type="text/javascript">
  <%-- Onload --%>
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.subject.focus(); });
  <%-- Validations --%>
  function checkForm(form) {
    try { tinyMCE.triggerSave(false); } catch(e) { }
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (document.inputForm.subject.value == "" ||
        document.inputForm.subject.value == "<p>&nbsp;</p>") {
      messageText += "- Subject is a required field\r\n";
      formTest = false;
    }
    if (document.inputForm.intro.value == "" ||
        document.inputForm.intro.value == "<p>&nbsp;</p>") {
      messageText += "- Summary or introduction is a required field\r\n";
      formTest = false;
    }
    //Check date field
    if ((document.inputForm.startDate.value != "") && (!checkDate(document.inputForm.startDate.value))) {
        messageText += "- Start date was not properly entered\r\n";
        formTest = false;
    }
    if (!formTest) {
        messageText = "The message could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
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
  <portlet:param name="portlet-command" value="saveForm"/>
</portlet:actionURL>
<form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="try {return checkForm(this);}catch(e){return true;}">
  <%= showError(request, "actionError") %>
  <ccp:evaluate if="<%= blog.getId() == -1 %>">
    <fieldset id="<ccp:label name="projectsCenterNews.add.add">New Post</ccp:label>">
       <legend><ccp:label name="projectsCenterNews.add.add">New Post</ccp:label></legend>
  </ccp:evaluate>
  <ccp:evaluate if="<%= blog.getId() != -1 %>">
    <fieldset id="<ccp:label name="projectsCenterNews.add.update">Update Post</ccp:label>">
       <legend><ccp:label name="projectsCenterNews.add.update">Update Post</ccp:label></legend>
  </ccp:evaluate>
    <label for="subject"><ccp:label name="projectsCenterNews.add.subject">Subject</ccp:label> <span class="required">*</span></label>
    <%= showAttribute(request, "subjectError") %>
    <input type="text" name="subject" id="subject" value="<%= toHtmlValue(blog.getSubject()) %>" />
    <span class="characterCounter">255 characters max</span>
    <label for="intro"><ccp:label name="projectsCenterNews.add.summaryOrIntroduction">Summary or Introduction</ccp:label></label>
    <textarea id="intro" name="intro"><%= toString(blog.getIntro()) %></textarea>
    <ccp:label name="projectsCenterNews.add.body">Body</ccp:label>
    <textarea rows="20" id="message" name="message" class="height300"><%= toString(blog.getMessage()) %></textarea>

    <fieldset>
      <legend><ccp:label name="projectsCenterNews.add.startDate">Start Date/Time</ccp:label></legend>
      <%= showAttribute(request, "startDateError") %>
      <input type="text" name="startDate" id="startDate" value="<ccp:tz timestamp="<%= blog.getStartDate() %>" dateOnly="true"/>">
      <a href="javascript:popCalendar('inputForm', 'startDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
      <ccp:label name="projectsCenterNews.add.at">at</ccp:label>
      <ccp:timeSelect baseName="startDate" value="<%= blog.getStartDate() %>" timeZone="<%= User.getTimeZone() %>"/>
      <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
    </fieldset>

    <label for="categoryId"><ccp:label name="projectsCenterNews.add.category">Category</ccp:label></label>
    <span>
      <%= showAttribute(request, "categoryIdError") %>
      <%= blogCategoryList.getHtmlSelect("categoryId", blog.getCategoryId()) %>
    </span>
    <ccp:permission name="project-news-add">
      <a name="lists" id="lists" href="javascript:popURL('<%= ctx %>/BlogActions.do?command=EditCategoryList&pid=<%= project.getId() %>&form=inputForm&field=categoryId&previousId=' + document.inputForm.categoryId.options[document.inputForm.categoryId.selectedIndex].value + '&popup=true','EditList','600','300','yes','yes');"><ccp:label name="projectsCenterNews.add.editList">Add/Modify Categories</ccp:label></a>
    </ccp:permission>
    <span>
      <label for="priorityId"><input type="checkbox" name="priorityId" id="priorityId" value="5" class="checkbox" <ccp:evaluate if="<%= blog.getPriorityId() < 10 %>" >checked</ccp:evaluate> />
      Always keep this post at the top of all other posts</label>
    </span>
  </fieldset>
  <%-- These names are updated in the BlogPost bean --%>
  <input type="submit" name="status" value="Publish" class="submit" />
  <input type="submit" name="status" value="Save as Draft" class="submit" />
  <input type="submit" name="status" value="Save for Review" class="submit" />
  <c:choose>
    <c:when test="${'true' eq param.popup || 'true' eq popup}">
      <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
    </c:when>
    <c:otherwise>
      <portlet:renderURL var="cancelUrl">
        <portlet:param name="portlet-action" value="show"/>
        <portlet:param name="portlet-object" value="blog"/>
      </portlet:renderURL>
      <a href="${cancelUrl}" class="cancel">Cancel</a>
    </c:otherwise>
  </c:choose>
  <input type="hidden" name="id" value="${blog.id}" />
  <input type="hidden" name="modified" value="<%= blog.getModified() %>" />
  <c:if test="${'true' eq param.popup || 'true' eq popup}">
    <input type="hidden" name="popup" value="true" />
  </c:if>
  <input type="hidden" name="param" value="<%= project.getId() %>"/>
  <input type="hidden" name="param2" value="<%= blog.getId() %>"/>
</form>
