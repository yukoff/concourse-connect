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
<%@ page import="com.concursive.commons.text.StringUtils" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="newsArticle" class="com.concursive.connect.web.modules.blog.dao.BlogPost" scope="request"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="initPage.jsp" %>
<%-- Editor must go here, before the body onload --%>
<jsp:include page="tinymce_include.jsp" flush="true"/>
<script language="javascript" type="text/javascript">
  initEditor('message');
</script>
<%-- Setup Image Library --%>
<script language="javascript" type="text/javascript">
    var ilConstant = <%= Constants.BLOG_POST_FILES %>;
    var ilId = <%= newsArticle.getId() %>;
</script>
<%-- Validations --%>
<script language="javascript" type="text/javascript">
  function checkForm(form) {
    try { tinyMCE.triggerSave(false); } catch(e) { }
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (document.inputForm.message.value == "") {
      messageText += "- Message is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The message could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form method="POST" name="inputForm" action="<%= ctx %>/BlogActions.do?command=SavePage&pid=<%= project.getId() %>&auto-populate=true" onSubmit="return checkForm(this);">
<table border="0" cellpadding="1" cellspacing="0" width="100%">
  <tr class="subtab">
    <td>
      <img src="<%= ctx %>/images/icons/stock_announcement-16.gif" border="0" align="absmiddle">
      <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/blog"><ccp:tabLabel name="News" object="project"/></a> >
      <a href="<%= ctx %>/BlogActions.do?command=Edit&pid=<%= project.getId() %>&id=<%= newsArticle.getId() %>"><ccp:label name="projectsCenterNews.addPage.edit">Edit Article</ccp:label></a> >
      <ccp:label name="projectsCenterNews.addPage.addPage">Add Page</ccp:label>
    </td>
  </tr>
</table>
<br>
  <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>" />
  <ccp:evaluate if="<%= newsArticle.getMessage() != null %>">
    <input type="button" value="<ccp:label name="button.deleteThisPage">Delete this page</ccp:label>" onclick="window.location.href='<%= ctx %>/BlogActions.do?command=DeletePage&pid=<%= project.getId() %>&id=<%= newsArticle.getId() %>';">
  </ccp:evaluate>
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.location.href='<%= ctx %>/BlogActions.do?command=Edit&pid=<%= project.getId() %>&id=<%= newsArticle.getId() %>';"><br>
  <%= showError(request, "actionError") %>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2" align="left">
          <ccp:label name="projectsCenterNews.addPage.addPage">Add Page</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td class="formLabel" valign="top">
          <ccp:label name="projectsCenterNews.addPage.subject">Subject</ccp:label>
        </td>
        <td>
          <%= toHtml(newsArticle.getSubject()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td class="formLabel" valign="top">
          <ccp:label name="projectsCenterNews.addPage.pageTwo">Page 2</ccp:label>
        </td>
        <td>
          <table border="0" cellpadding="0" cellspacing="0" class="empty">
            <tr>
              <td>
                <textarea rows="20" id="message" name="message" cols="80"><%= toString(newsArticle.getMessage()) %></textarea>
              </td>
              <td valign="top">
                <font color="red">*</font>
                <%= showAttribute(request, "messageError") %>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </tbody>
  </table>
  <input type='submit' value='<ccp:label name="button.save">Save</ccp:label>" />
  <ccp:evaluate if="<%= newsArticle.getMessage() != null %>">
    <input type="button" value="<ccp:label name="button.deleteThisPage">Delete this page</ccp:label>" onclick="window.location.href='<%= ctx %>/BlogActions.do?command=DeletePage&pid=<%= project.getId() %>&id=<%= newsArticle.getId() %>';">
  </ccp:evaluate>
  <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.location.href='<%= ctx %>/BlogActions.do?command=Edit&pid=<%= project.getId() %>&id=<%= newsArticle.getId() %>';"><br />
  <input type="hidden" name="projectId" value="<%= project.getId() %>" />
  <input type="hidden" name="id" value="<%= newsArticle.getId() %>" />
  <input type="hidden" name="modified" value="<%= newsArticle.getModified() %>" />
  <input type="hidden" name="newPage" value="false" />
  <input type="hidden" name="return" value="<%= toHtmlValue(request.getParameter("return")) %>"/>
</form>
