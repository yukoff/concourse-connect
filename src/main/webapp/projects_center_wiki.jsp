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
<%@ page import="com.concursive.connect.web.modules.wiki.utils.WikiUtils"%>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember"%>
<%@ page import="com.concursive.connect.web.modules.wiki.dao.WikiComment"%>
<%@ page import="com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="wiki" class="com.concursive.connect.web.modules.wiki.dao.Wiki" scope="request"/>
<jsp:useBean id="wikiStateList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="commentList" class="com.concursive.connect.web.modules.wiki.dao.WikiCommentList" scope="request"/>
<jsp:useBean id="userRating" class="java.lang.String" scope="request"/>
<jsp:useBean id="imageList" class="java.util.HashMap" scope="request"/>
<jsp:useBean id="wikiHtml" class="java.lang.String" scope="request"/>
<jsp:useBean id="trailMap" class="com.concursive.connect.web.utils.TrailMap" scope="request"/>
<portlet:defineObjects/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_wiki_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<div id="message" class="menu">
  <p>Thank you for your valuable feedback.</p>
</div>
  <div id="deleteMessage" class="menu">
    <p>Comment deleted.</p>
  </div>
<div class="articleContainer">
  <c:if test="${empty param.popup && empty popup}">
    <div class="articleHeader">
      <div class="header">
        <ccp:evaluate if="<%= trailMap.size() > 0 %>">
          <portlet:renderURL var="homeUrl" portletMode="view">
            <portlet:param name="portlet-action" value="show"/>
            <portlet:param name="portlet-object" value="wiki"/>
          </portlet:renderURL>
          <p><a href="${homeUrl}"><ccp:label name="projectsCenterWiki.home">Home</ccp:label></a>
                <%
              Iterator i = trailMap.iterator();
              while (i.hasNext()) {
                String thisSubject = (String) i.next();
            %>
            &gt;
            <portlet:renderURL var="trailUrl" portletMode="view">
              <portlet:param name="portlet-action" value="show"/>
              <portlet:param name="portlet-object" value="wiki"/>
              <portlet:param name="portlet-value" value='<%= StringUtils.replace(StringUtils.jsEscape(thisSubject), "%20", "+") %>'/>
            </portlet:renderURL>
          <ccp:evaluate if="<%= i.hasNext() %>"><p><a href="${trailUrl}"></ccp:evaluate><%= toHtml(thisSubject) %><ccp:evaluate if="<%= i.hasNext() %>"></a></ccp:evaluate>
          <%
            }
          %>
          </p>
        </ccp:evaluate>
        <h1>
          <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
            <%= toHtml(wiki.getSubject()) %>
          </ccp:evaluate>
          <ccp:evaluate if="<%= !hasText(wiki.getSubject()) %>">
            <%= toHtml(project.getTitle()) %> Home
          </ccp:evaluate>
        </h1>
        <ccp:permission if="any" name="project-wiki-add,project-wiki-admin">
          <div class="actions">
              <%-- WYSIWYG URL --%>
            <portlet:renderURL var="wysiwygUrl" portletMode="view">
              <portlet:param name="portlet-action" value="modify"/>
              <portlet:param name="portlet-object" value="wiki"/>
              <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
              </ccp:evaluate>
            </portlet:renderURL>
              <%-- Markup Editor URL --%>
            <portlet:renderURL var="markupEditorUrl" portletMode="view">
              <portlet:param name="portlet-action" value="modify"/>
              <portlet:param name="portlet-object" value="wiki"/>
              <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
              </ccp:evaluate>
              <portlet:param name="mode" value="raw"/>
            </portlet:renderURL>
            <ccp:permission name="project-wiki-add">
              <ccp:evaluate if="<%= !wiki.getReadOnly() %>">
                <a href="${wysiwygUrl}" title="Visual Editor">
                  <em>Visual Editor</em>
                </a>
                <a href="${markupEditorUrl}" title="Markup Editor">
                  <em>Markup Editor</em>
                </a>
              </ccp:evaluate>
              <ccp:evaluate if="<%= wiki.getReadOnly() %>">
                <ccp:permission name="project-wiki-locked-edit">
                  <a href="${wysiwygUrl}" title="Visual Editor">
                    <em>Visual Editor</em>
                  </a>
                  <a href="${markupEditorUrl}" title="Markup Editor">
                    <em>Markup Editor</em>
                  </a>
                </ccp:permission>
              </ccp:evaluate>
            </ccp:permission>
            <ccp:permission if="any" name="project-wiki-add,project-wiki-admin">
              <ccp:evaluate if="<%= wiki.getId() > -1 %>">
                <portlet:renderURL var="exportUrl">
                  <portlet:param name="portlet-action" value="export"/>
                  <portlet:param name="portlet-object" value="wiki"/>
                  <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                    <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
                  </ccp:evaluate>
                </portlet:renderURL>
                <a href="${exportUrl}" rel="shadowbox" title="Export to PDF">
                  <img src="${ctx}/images/icons/pdf_export.png" border="0" height="16" width="16" alt="Export to PDF"/>
                </a>
                <ccp:permission name="project-wiki-add">
                  <portlet:renderURL var="versionsUrl">
                    <portlet:param name="portlet-action" value="show"/>
                    <portlet:param name="portlet-object" value="wiki-versions"/>
                    <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                      <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
                    </ccp:evaluate>
                  </portlet:renderURL>
                  <a href="${versionsUrl}" title="Wiki Versions">
                    <img src="<%= ctx %>/images/icons/notebooks.png" border="0" height="16" width="16" alt="Wiki Versions"/>
                  </a>
                </ccp:permission>
                <ccp:evaluate if="<%= !wiki.getReadOnly() %>">
                  <portlet:actionURL var="lockUrl">
                    <portlet:param name="portlet-action" value="show"/>
                    <portlet:param name="portlet-object" value="wiki"/>
                    <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                      <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
                    </ccp:evaluate>
                    <portlet:param name="portlet-command" value="lock"/>
                  </portlet:actionURL>
                  <a href="${lockUrl}" title="Lock Wiki">
                    <img src="<%= ctx %>/images/icons/lock_unlock.png" border="0" height="16" width="16" alt="Lock Wiki"/>
                  </a>
                </ccp:evaluate>
                <ccp:permission name="project-wiki-admin">
                  <ccp:evaluate if="<%= wiki.getReadOnly() %>">
                    <portlet:actionURL var="unlockUrl">
                      <portlet:param name="portlet-action" value="show"/>
                      <portlet:param name="portlet-object" value="wiki"/>
                      <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                        <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
                      </ccp:evaluate>
                      <portlet:param name="portlet-command" value="unlock"/>
                    </portlet:actionURL>
                    <a href="${unlockUrl}" title="Unlock Wiki">
                      <img src="<%= ctx %>/images/icons/lock.png" border="0" height="16" width="16" alt="Unlock Wiki"/>
                    </a>
                  </ccp:evaluate>
                </ccp:permission>
              </ccp:evaluate>
            </ccp:permission>
          </div>
        </ccp:permission>
      </div>

      <%-- TODO: Move average rating to portlet and out of this page
      <ccp:evaluate if="<%= wiki.getId() > -1 %>">
        <div class="rating">
          <p>Page rating:</p>
          <ccp:rating id='<%= wiki.getId() %>'
                         showText='true'
                         count='<%= wiki.getRatingCount() %>'
                         value='<%= wiki.getRatingValue() %>'
                         url=''/>
        </div>
      </ccp:evaluate>
      --%>

      <%--
      <table border="0" cellpadding="0" cellspacing="1" width="100%">
        <tr>
          <ccp:evaluate if="<%= !hasText(wiki.getSubject()) %>">
          <td>
            Create a new page:
            <input id="newPageEntry" type="text" width="25" />
            <input type="button" value="&gt;" onclick="window.location.href='<%= ctx %>/ProjectManagementWiki.do?command=Edit&pid=<%= project.getId() %>&subject=' + escape( newPageEntry )" />
          </td>
          </ccp:evaluate>
          <ccp:evaluate if="<%= wiki.getStateId() > -1 %>">
            <td nowrap>
              [<%= StringUtils.toHtml(wikiStateList.getValueFromId(wiki.getStateId())) %>]
            </td>
          </ccp:evaluate>
          <td nowrap>
            <%= wikiStateList.getHtmlSelect("stateId", wiki.getStateId()) %>
          </td>
        </tr>
      </table>
      --%>
    </div>
  </c:if>
  <div class="articleBody <ccp:permission if="any" name="project-wiki-add,project-wiki-admin">admin</ccp:permission>">
    <ccp:evaluate if="<%= !hasText(wiki.getContent()) %>">
      <p><ccp:label name="projectsCenterWiki.noContent">This page does not have any content.</ccp:label></p>
      <ccp:permission name="project-wiki-add">
        <input type="button" name="Edit" value="<ccp:label name="wiki.button.editThisPage">Edit this page</ccp:label>" onClick="window.location.href='<%= ctx %>/modify/<%= project.getUniqueId() %>/wiki<ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">/<%= wiki.getSubjectLink() %></ccp:evaluate>';" />
        <%--<p>Use one of the following templates to create this page:</p>--%>
      </ccp:permission>
    </ccp:evaluate>
    <ccp:evaluate if="<%= hasText(wiki.getContent()) %>">
      <%= wikiHtml %>
    </ccp:evaluate>
  </div>
  <ccp:evaluate if="<%= wiki.getModified() != null %>">
    <div class="portlet-menu">
      <ul>
        <ccp:permission if="any" name="project-wiki-add,project-wiki-admin">
          <ccp:evaluate if="<%= wiki.getId() > -1 %>">
            <portlet:renderURL var="exportUrl">
              <portlet:param name="portlet-action" value="export"/>
              <portlet:param name="portlet-object" value="wiki"/>
              <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
              </ccp:evaluate>
            </portlet:renderURL>
            <li>
              <a href="javascript:popURL('${exportUrl}?popup=true','Wiki_Export_Options','550','475','yes','yes');" title="Export to PDF">
                <img src="<%= ctx %>/images/icons/pdf_export.png" border="0" height="16" width="16" alt="Export to PDF"/>
                Export
              </a>
            </li>
            <ccp:permission name="project-wiki-add">
              <portlet:renderURL var="versionsUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="wiki-versions"/>
                <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                  <portlet:param name="portlet-value" value="<%= wiki.getSubjectLink() %>"/>
                </ccp:evaluate>
              </portlet:renderURL>
              <li>
                <a href="${versionsUrl}" title="Wiki Versions">
                  <img src="<%= ctx %>/images/icons/notebooks.png" border="0" height="16" width="16" alt="Wiki Versions"/>
                  Versions
                </a>
              </li>
            </ccp:permission>
          </ccp:evaluate>
        </ccp:permission>
        <ccp:evaluate if="<%= wiki.getId() > -1 && User.isLoggedIn() %>">
          <li>
            <a href="#addComment"><img alt="Add a comment icon" src="${ctx}/images/icons/balloon_plus.png"/> Add a Comment</a>
          </li>
        </ccp:evaluate>
      </ul>
    </div>
  </ccp:evaluate>
</div>
<ccp:evaluate if="<%= wiki.getId() > 0 %>">
  <div class="userInputFooter">
    <ccp:evaluate if="<%= wiki.getRatingCount() > 0 %>">
      <p>(<%= wiki.getRatingValue() %> out of <%= wiki.getRatingCount() %> <%= wiki.getRatingCount() == 1 ? " person" : " people"%> found this wiki useful.)</p>
    </ccp:evaluate>
    <ccp:evaluate if="<%= wiki.getInappropriateCount() > 0 && ProjectUtils.hasAccess(wiki.getProjectId(), User, \"project-wiki-admin\")%>">
      <p>(<%= wiki.getInappropriateCount() %><%= wiki.getInappropriateCount() == 1? " person" : " people"%> found this wiki inappropriate.)</p>
    </ccp:evaluate>
       <ccp:permission name="project-wiki-view">
      <%-- any user who is not the author of the wiki can mark the rate the wiki  --%>
      <ccp:evaluate if="<%= (wiki.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
        <p>Is this wiki useful?
        <portlet:renderURL var="ratingUrl" windowState="maximized">
          <portlet:param name="portlet-command" value="setRating"/>
          <portlet:param name="id" value="${wiki.id}"/>
          <portlet:param name="v" value="1"/>
          <portlet:param name="out" value="text"/>
        </portlet:renderURL>
        <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + wiki.getId() %>','message');">Yes</a>&nbsp;
        <portlet:renderURL var="ratingUrl" windowState="maximized">
          <portlet:param name="portlet-command" value="setRating"/>
          <portlet:param name="id" value="${wiki.id}"/>
          <portlet:param name="v" value="0"/>
          <portlet:param name="out" value="text"/>
        </portlet:renderURL>
        <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + wiki.getId() %>','message');">No</a>&nbsp;
      <ccp:evaluate if="<%= wiki.getId() > -1 && User.isLoggedIn() %>">
  <a href="javascript:showPanel('Mark this review as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=wiki&pid=${project.id}&id=${wiki.id}',700)">Report this as inappropriate</a>
   </ccp:evaluate>
   </p>
        <div id="message_<%= wiki.getId() %>"></div>
      </ccp:evaluate>
      </ccp:permission>
  </div>
</ccp:evaluate>
<%-- TODO: Hide the comments and have the user click to view... should use AJAX for displaying comments --%>
<ccp:evaluate if="<%= commentList.size() > 0 %>">
  <div class="userInputContainer">
    <h3>Comments</h3> <span class="count">(<%= commentList.size() %>)</span>
    <%
      Iterator iwcl = commentList.iterator();
      int count = 0;
      while (iwcl.hasNext()) {
        WikiComment wikiComment = (WikiComment) iwcl.next();
        ++count;
    %>
	<c:set var="wikiComment" value="<%= wikiComment %>" />
    <div id="commentContainer${wikiComment.id}" class="userInputContainer">
	    <a name="${wikiComment.id}"></a>
      <div class="userInputBody">
        <p><%= StringUtils.toHtml(wikiComment.getComment()) %></p>
      </div>
      <div class="userInputDetails">
        <div class="details">
          <h5>Written by <ccp:username id="<%= wikiComment.getEnteredBy() %>" /></h5>
          <p><ccp:tz timestamp="<%= wikiComment.getEntered() %>" default="&nbsp;"/></p>
        </div>
      </div>
	    <div class="userInputFooter">
	      <ccp:evaluate if="<%= wikiComment.getRatingCount() > 0 %>">
	        <p>(<%= wikiComment.getRatingValue() %> out of <%= wikiComment.getRatingCount() %> <%= wikiComment.getRatingCount() == 1 ? " person" : " people"%> found this comment useful.)</p>
	      </ccp:evaluate>
	      <ccp:evaluate if="<%= wikiComment.getInappropriateCount() > 0 && ProjectUtils.hasAccess(wiki.getProjectId(), User, \"project-wiki-admin\")%>">
	        <p>(<%= wikiComment.getInappropriateCount() %><%= wikiComment.getInappropriateCount() == 1? " person" : " people"%> found this comment inappropriate.)</p>
	      </ccp:evaluate>
	        <%-- any user who is not the author of the comment can mark the comment as useful  --%>
	       <p>
	       <ccp:permission name="project-wiki-view">
	        <ccp:evaluate if="<%= (wikiComment.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
	          Is this comment useful?
	          <portlet:renderURL var="ratingUrl" windowState="maximized">
	            <portlet:param name="portlet-command" value="comment-setRating"/>
	            <portlet:param name="id" value="${wikiComment.id}"/>
	            <portlet:param name="v" value="1"/>
	            <portlet:param name="out" value="text"/>
	          </portlet:renderURL>
	          <a href="javascript:copyRequest('${ratingUrl}','<%= "wikiComment_" + wikiComment.getId() %>','message');">Yes</a>&nbsp;
	          <portlet:renderURL var="ratingUrl" windowState="maximized">
	            <portlet:param name="portlet-command" value="comment-setRating"/>
	            <portlet:param name="id" value="${wikiComment.id}"/>
	            <portlet:param name="v" value="0"/>
	            <portlet:param name="out" value="text"/>
	          </portlet:renderURL>
	          <a href="javascript:copyRequest('${ratingUrl}','<%= "wikiComment_" + wikiComment.getId() %>','message');">No</a>&nbsp;
	        </ccp:evaluate>
	        </ccp:permission>
	        <%-- a user with admin access can delete the comment  --%>
	       <c:if test="${User.accessAdmin}">
	          <portlet:renderURL var="deleteCommentUrl" windowState="maximized">
	            <portlet:param name="portlet-command" value="comment-delete"/>
	            <portlet:param name="id" value="${wikiComment.id}"/>
	          </portlet:renderURL>
	          <a href="javascript:copyRequest('${deleteCommentUrl}','commentContainer${wikiComment.id}','deleteMessage');">Delete</a>&nbsp;
	        </c:if>
	       <ccp:permission name="project-wiki-view">
	     	  <ccp:evaluate if="<%= wikiComment.getEnteredBy() != User.getId() && User.isLoggedIn() %>">
	 			<a href="javascript:showPanel('Mark this comment as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=wikicomment&pid=${project.id}&id=${wikiComment.id}',700)">Report this as inappropriate</a>
	 		  </ccp:evaluate>
	        </ccp:permission>
	       </p> 
	          <div id="wikiComment_<%= wikiComment.getId() %>"></div>
	    </div>
    </div>
    <%
      }
    %>
  </div>
</ccp:evaluate>
<c:if test="${empty param.popup && empty popup}">
  <c:if test="${wiki.id > -1}">
    <c:choose>
      <c:when test="${User.loggedIn }">
        <a name="addComment"></a>
        <div class="formContainer"  id="thisCommentWindow<%= wiki.getId() %>">
          <portlet:actionURL var="saveCommentUrl" portletMode="view">
            <portlet:param name="portlet-action" value="show"/>
            <portlet:param name="portlet-object" value="wiki"/>
            <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
              <portlet:param name="portlet-value" value="${wiki.subjectLink}"/>
            </ccp:evaluate>
            <portlet:param name="portlet-command" value="saveComments"/>
          </portlet:actionURL>
          <form action="${saveCommentUrl}" method="post">
            <fieldset id="thisCommentWindow<%= wiki.getId() %>">
              <legend>Add a suggestion or comment for this page</legend>
              <div class="commentBox">
                <%= showAttribute(request, "commentError") %>
                <textarea id="comment" name="comment" style="width:100%" rows="4"></textarea>
                <span class="characterCounter">Comments are reset by an administrator when they no longer apply.</span>
              </div>
            </fieldset>
            <input type="submit" name="Save" value="Save" class="submit" />
            <input type="button" value="Cancel" onclick="hideSpan('thisCommentWindow<%= wiki.getId() %>')" class="cancel" />
          </form>
        </div>
      </c:when>
      <c:otherwise>
          <div class="formContainer" id="thisCommentWindow${wiki.id}">
            <p>Sign in to add your comment.</p>
        </div>
      </c:otherwise>
    </c:choose>
  </c:if>
</c:if>
