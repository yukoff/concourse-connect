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
<%@ page import="com.concursive.commons.text.StringUtils"%>
<%@ page import="com.concursive.connect.web.modules.wiki.utils.WikiUtils" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="wiki" class="com.concursive.connect.web.modules.wiki.dao.Wiki" scope="request"/>
<jsp:useBean id="imageList" class="java.util.HashMap" scope="request"/>
<jsp:useBean id="wikiHtml" class="java.lang.String" scope="request"/>
<portlet:defineObjects/>
<%@ include file="initPage.jsp" %>
<%-- Editor must go here, before the body onload --%>
<jsp:include page="tinymce_wiki_include.jsp" flush="true"/>
<script language="javascript" type="text/javascript">
  // initialize the editor
  initEditor('content');
  // image constant
  var ilId = <%= wiki.getProjectId() %>;
  function checkForm(form) {
    try { tinyMCE.triggerSave(false); } catch(e) { }
    var formTest = true;
    var messageText = "";
    if (document.inputForm.content.value == "" ||
        document.inputForm.content.value == "<p>&nbsp;</p>") {
      messageText += "- Content is a required field\r\n";
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
<div class="portletWrapper projectsCenterWikiEditor">
  <h1>
    <ccp:label name="projectsCenterWiki.add.editing">Editing</ccp:label>
    <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
      <%= toHtml(wiki.getSubject()) %>
    </ccp:evaluate>
    <ccp:evaluate if="<%= !hasText(wiki.getSubject()) %>">
      Home
    </ccp:evaluate>
  </h1>
  <div class="formContainer">
    <portlet:actionURL var="saveFormUrl">
      <portlet:param name="portlet-command" value="save"/>
    </portlet:actionURL>
    <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
    <fieldset id="wikiEditor">
      <legend>
        <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
          <%= toHtml(wiki.getSubject()) %>
        </ccp:evaluate>
        <ccp:evaluate if="<%= !hasText(wiki.getSubject()) %>">
          <%= toHtml(project.getTitle()) %>
        </ccp:evaluate>
      </legend>
      <textarea tabindex="1" accesskey="," name="content" id="content" class="height400"><ccp:evaluate if="<%= hasText(wiki.getContent()) %>"><c:out value="<%= wikiHtml %>" /><p>&nbsp;</p></ccp:evaluate></textarea>
      <input type="hidden" name="id" value="<%= wiki.getId() %>" />
      <input type="hidden" name="subject" value="<%= toHtmlValue(wiki.getSubject()) %>" />
      <input type="hidden" name="modified" value="<%= wiki.getModified() %>" />
      <ccp:evaluate if='<%= StringUtils.hasText(request.getParameter("section"))%>'>
        <input type="hidden" name="section" value="<%= StringUtils.toHtmlValue(request.getParameter("section")) %>" />
      </ccp:evaluate>
      <c:if test="${'true' eq param.popup || 'true' eq popup}">
        <input type="hidden" name="popup" value="true"/>
        <input type="hidden" name="returnURL" value="${ctx}<%= toHtmlValue(request.getParameter("returnURL")) %>">
      </c:if>
    </fieldset>
    <input type="submit" class="submit" name="save" value="<ccp:label name="button.savePage">Save Page</ccp:label>" />
      <c:choose>
        <c:when test="${'true' eq param.popup || 'true' eq popup}">
          <input type="button" class="cancel" name="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.close();" />
        </c:when>
        <c:otherwise>
          <input type="button" class="cancel" name="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/wiki<ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">/<%= wiki.getSubjectLink() %></ccp:evaluate>';" />
        </c:otherwise>
      </c:choose>
   </form>
 </div>
</div>
