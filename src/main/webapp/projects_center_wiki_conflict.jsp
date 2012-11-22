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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="wiki" class="com.concursive.connect.web.modules.wiki.dao.Wiki" scope="request"/>
<jsp:useBean id="originalWiki" class="com.concursive.connect.web.modules.wiki.dao.Wiki" scope="request"/>
<portlet:defineObjects/>
<%@ include file="initPage.jsp" %>
<body onLoad="document.inputForm.content.focus();">
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    if (document.inputForm.content.value == "") {
      messageText += "- Content is a required field\r\n";
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
<div class="portletWrapper">
  <portlet:actionURL var="saveFormUrl">
    <portlet:param name="portlet-command" value="save"/>
  </portlet:actionURL>
  <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
    <h2><ccp:label name="projectsCenterWiki.conflict.editing">Editing</ccp:label>
      <ccp:evaluate if="<%= hasText(originalWiki.getSubject()) %>">
        <%= toHtml(wiki.getSubject()) %>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !hasText(originalWiki.getSubject()) %>">
        <ccp:label name="projectsCenterWiki.conflict.home">Home</ccp:label>
      </ccp:evaluate>
    </h2>
    <%-- Diff Alert --%>
    <div class="headerDetails">
      <legend><ccp:label name="projectsCenterWiki.conflict.title">A conflict has occurred which needs to be resolved!</ccp:label></legend>
      <p>The changes that were made first by <ccp:username id="<%= originalWiki.getModifiedBy() %>" />
      are displayed in the top pane, your changes are displayed in the bottom pane.</p>
      <p>Please review and update the top pane with your changes, then try submitting again.</p>
      <p>If you do not update the top pane with your changes, then your changes will be lost.</p>
    </div>
    <ccp:label name="projectsCenterWiki.conflict.finalVersion">Final version:</ccp:label><br />
    <textarea tabindex="1" accesskey="," name="content" id="content" rows="15" cols="80" style="width:100%;"><c:out value="<%= originalWiki.getContent() %>" /></textarea>
    <div class="wikiHeader">
      <label for="temporaryContent"><ccp:label name="projectsCenterWiki.conflict.conflictingVersion">Conflicting version:</ccp:label></label>
    </div>
    <div class="wikiBody">      
      <textarea tabindex="1" accesskey="," name="temporaryContent" id="temporaryContent" rows="15" cols="80" style="width:100%;"><c:out value="<%= wiki.getContent() %>" /></textarea>
      <input type="hidden" name="id" value="<%= originalWiki.getId() %>" />
      <input type="hidden" name="subject" value="<%= toHtmlValue(originalWiki.getSubject()) %>" />
      <input type="hidden" name="modified" value="<%= originalWiki.getModified() %>" />
      <input type="hidden" name="mode" value="raw"/>
      <c:if test="${'true' eq param.popup || 'true' eq popup}">
        <input type="hidden" name="popup" value="true"/>
      </c:if>      
    </div>
    <input type="submit" name="<ccp:label name="button.savePage">Save Page</ccp:label>" value="Save Page" />
    <input type="button" name="<ccp:label name="button.cancel">Cancel</ccp:label>" value="Cancel" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/wiki<ccp:evaluate if="<%= hasText(originalWiki.getSubject()) %>">/<%= originalWiki.getSubjectLink() %></ccp:evaluate>';" />
  </form>
</div>
</body>
