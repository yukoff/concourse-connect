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
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.web.modules.discussion.dao.Topic" %>
<%@ page import="com.concursive.connect.web.modules.blog.dao.BlogPost" %>
<%@ page import="com.concursive.connect.web.modules.wiki.dao.Wiki" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="overviewListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="newsList" class="com.concursive.connect.web.modules.blog.dao.BlogPostList" scope="request"/>
<jsp:useBean id="overviewNewsListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="issueList" class="com.concursive.connect.web.modules.discussion.dao.TopicList" scope="request"/>
<jsp:useBean id="overviewIssueListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="overviewFileItemListListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="wikiList" class="com.concursive.connect.web.modules.wiki.dao.WikiList" scope="request"/>
<jsp:useBean id="overviewWikiListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript" src="<%= ctx %>/javascript/projects_watch.js"></script>
<ccp:evaluate if="<%= !clientType.getMobile() %>">
<table class="note" cellspacing="0">
<tr>
  <th>
    <img src="<%= ctx %>/images/icons/stock_form-open-in-design-mode-16.gif" border="0" align="absmiddle" />
  </th>
  <td>
    <ccp:label name="projectsOverview.title">The following items are the latest from each of your projects.</ccp:label>
  </td>
</tr>
</table>
<table width="100%" border="0">
  <tr>
    <form name="listView" method="post" action="<%= ctx %>/ProjectManagement.do?command=Overview">
      <td align="left" valign="bottom" nowrap>
        <img src="<%= ctx %>/images/icons/stock_filter-data-by-criteria-16.gif" border="0" align="absmiddle" />
        <select size="1" name="listView" onChange="document.forms['listView'].submit();">
          <option <%= overviewListInfo.getOptionValue("today") %>><ccp:label name="projectsOverview.item.today">Today</ccp:label></option>
          <option <%= overviewListInfo.getOptionValue("24hours") %>><ccp:label name="projectsOverview.item.last24h">Last 24 Hours</ccp:label></option>
          <option <%= overviewListInfo.getOptionValue("48hours") %>><ccp:label name="projectsOverview.item.last48h">Last 48 Hours</ccp:label></option>
          <option <%= overviewListInfo.getOptionValue("7days") %>><ccp:label name="projectsOverview.item.last7Days">Last 7 Days</ccp:label></option>
          <option <%= overviewListInfo.getOptionValue("14days") %>><ccp:label name="projectsOverview.item.last14Days">Last 14 Days</ccp:label></option>
          <option <%= overviewListInfo.getOptionValue("30days") %>><ccp:label name="projectsOverview.item.last30Days">Last 30 Days</ccp:label></option>
        </select>
      </td>
    </form>
  </tr>
</table>
</ccp:evaluate>
<% int rowid = 0; %>

<table cellpadding="0" cellspacing="0" width="100%" border="0">
<tr>
<td valign="top">

<%-- Start news --%>
<ccp:evaluate if="<%= !overviewIssueListInfo.getExpandedSelection() && !overviewFileItemListListInfo.getExpandedSelection() && !overviewWikiListInfo.getExpandedSelection() %>">
<ccp:pagedListStatus tableClass="pagedListTab" showExpandLink="true" title="News" object="overviewNewsListInfo" rss='<%= "http://" + getServerUrl(request) + "/feed/news.xml" %>'/>
<table cellpadding="4" cellspacing="0" width="100%">
  <ccp:evaluate if="<%= newsList.isEmpty() %>">
    <tr>
      <td colspan="3">
        <ccp:label name="projectsOverview.noNewsFound">No News found.</ccp:label>
      </td>
    </tr>
  </ccp:evaluate>
<%
    rowid = 0;
    Iterator newsIterator = newsList.iterator();
    while (newsIterator.hasNext()) {
      rowid = (rowid != 1?1:2);
      BlogPost thisArticle = (BlogPost) newsIterator.next();
      Project thisProject = ProjectUtils.loadProject(thisArticle.getProjectId());
%>
    <tr class="overviewrow<%= rowid %>">
      <td width="65%">
        <table border="0" cellpadding="2" cellspacing="0">
          <tr>
            <td valign="top">
              <img src="<%= ctx %>/images/icons/stock_announcement-16.gif" border="0" align="absmiddle" />
            </td>
            <td>
              <a href="<%= ctx %>/show/<%=thisProject.getUniqueId() %>/post/<%= thisArticle.getId() %>"><%= toHtml(thisArticle.getSubject()) %></a><br />
              <a class="searchLink" href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&pid=<%= thisArticle.getProjectId() %>&section=News"><ccp:project id="<%= thisArticle.getProjectId() %>"/></a>
            </td>
          </tr>
        </table>
      </td>
      <td colspan="2" width="35%">
        <ccp:username id="<%= thisArticle.getEnteredBy() %>"/><br />
        <ccp:tz timestamp="<%= thisArticle.getStartDate() %>" dateOnly="true" default="&nbsp;"/>
      </td>
    </tr>
<%
    }
%>
</table>
<br />
</ccp:evaluate>

<%-- Start discussion topics --%>
<ccp:evaluate if="<%= !overviewNewsListInfo.getExpandedSelection() && !overviewFileItemListListInfo.getExpandedSelection() && !overviewWikiListInfo.getExpandedSelection() %>">
<ccp:pagedListStatus tableClass="pagedListTab" showExpandLink="true" title="Discussion" object="overviewIssueListInfo" rss='<%= "http://" + getServerUrl(request) + "/feed/discussion.xml" %>'/>
<table cellpadding="4" cellspacing="0" width="100%">
  <ccp:evaluate if="<%= issueList.isEmpty() %>">
    <tr>
      <td colspan="3">
        <ccp:label name="projectsOverview.noTopicsFound">No Topics found.</ccp:label>
      </td>
    </tr>
  </ccp:evaluate>
<%
    rowid = 0;
    Iterator issueIterator = issueList.iterator();
    while (issueIterator.hasNext()) {
      rowid = (rowid != 1?1:2);
      Topic thisTopic = (Topic) issueIterator.next();
      Project thisProject = ProjectUtils.loadProject(thisTopic.getProjectId());
%>
    <tr class="overviewrow<%= rowid %>">
      <td width="65%">
        <table border="0" cellpadding="2" cellspacing="0">
          <tr>
            <td valign="top">
              <img src="<%= ctx %>/images/icons/discussion_16x16.png" border="0" align="absmiddle" />
            </td>
            <td>
              <a href="<%= ctx %>/show/<%= thisProject.getUniqueId() %>/topic/<%= thisTopic.getId() %>?resetList=true"><%= toHtml(thisTopic.getSubject()) %></a><br />
              <a class="searchLink" href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&pid=<%= thisTopic.getProjectId() %>&section=Issues_Categories"><ccp:project id="<%= thisTopic.getProjectId() %>"/></a>
            </td>
          </tr>
        </table>
      </td>
      <td colspan="2" width="35%">
        <ccp:evaluate if="<%= thisTopic.getReplyBy() > -1 %>">
          <ccp:username id="<%= thisTopic.getReplyBy() %>" />
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisTopic.getReplyBy() == -1 %>">
          <ccp:username id="<%= thisTopic.getEnteredBy() %>" />
        </ccp:evaluate>
        <br />
        <ccp:tz timestamp="<%= thisTopic.getReplyDate() %>" dateOnly="true" default="&nbsp;" />
        <ccp:evaluate if="<%= thisTopic.getReplyCount() > 0 %>">
          (<%= thisTopic.getReplyCount() %> repl<%= thisTopic.getReplyCount() > 1 ? "ies" : "y" %>)
        </ccp:evaluate>
      </td>
    </tr>
<%
    }
%>
<%-- Watch Information --%>
    <tr>
      <td colspan="3" align="center">
        <ccp:evaluate if="<%= User.getId() > -1 %>">
          <span id="forumsWatchSpan" <ccp:evaluate if="<%= User.getWatchForums() %>">style="display:none"</ccp:evaluate>>
            <img border="0" src="<%= ctx %>/images/icons/stock_macro-watch-variable-16.gif" align="absmiddle" />
            <a href="javascript:changeWatch('forums','true');"><ccp:label name="projectsOverview.emailReceiveAllMessages">Receive all discussion messages by email</ccp:label></a>
          </span>
          <span id="forumsCancelSpan" <ccp:evaluate if="<%= !User.getWatchForums() %>">style="display:none"</ccp:evaluate>>
            <img border="0" src="<%= ctx %>/images/icons/stock_macro-stop-watching-16.gif" align="absmiddle" />
            <a href="javascript:changeWatch('forums','false');"><ccp:label name="projectsOverview.emailCancelReceiveAllMessages">Cancel receiving all discussion messages by email</ccp:label></a>
          </span>
        </ccp:evaluate>

      </td>
    </tr>
</table>
<br />
</ccp:evaluate>

<ccp:evaluate if="<%= !clientType.getMobile() %>">
</td>
<%-- gutter --%>
<td width="8" nowrap>&nbsp;</td>
<%-- 2nd column --%>
<td valign="top">
</ccp:evaluate>

<%-- Start wikis --%>
<ccp:evaluate if="<%= !overviewIssueListInfo.getExpandedSelection() && !overviewNewsListInfo.getExpandedSelection() && !overviewFileItemListListInfo.getExpandedSelection() %>">
<ccp:pagedListStatus tableClass="pagedListTab" showExpandLink="true" title="Wiki" object="overviewWikiListInfo" rss='<%= "http://" + getServerUrl(request) + "/feed/wiki.xml" %>'/>
<table cellpadding="4" cellspacing="0" width="100%">
  <ccp:evaluate if="<%= wikiList.isEmpty() %>">
    <tr>
      <td>
        <ccp:label name="projectsOverview.noWikisFound">No Wiki pages found.</ccp:label>
      </td>
    </tr>
  </ccp:evaluate>
<%
    rowid = 0;
    Iterator wikiIterator = wikiList.iterator();
    while (wikiIterator.hasNext()) {
      rowid = (rowid != 1?1:2);
      Wiki thisWiki = (Wiki) wikiIterator.next();
%>
    <tr class="overviewrow<%= rowid %>">
      <td width="65%">
        <table border="0" cellpadding="2" cellspacing="0">
          <tr>
            <td valign="top">
              <img src="<%= ctx %>/images/icons/stock_macro-objects-16.gif" border="0" align="absmiddle" />
            </td>
            <td>
              <a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Wiki&pid=<%= thisWiki.getProjectId() %><ccp:evaluate if="<%= hasText(thisWiki.getSubject()) %>">&subject=<%= thisWiki.getSubjectLink() %></ccp:evaluate>"><%= (hasText(thisWiki.getSubject()) ? toHtml(thisWiki.getSubject()) : "Home") %></a><br />
              <a class="searchLink" href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Wiki&pid=<%= thisWiki.getProjectId() %>"><ccp:project id="<%= thisWiki.getProjectId() %>"/></a>
            </td>
          </tr>
        </table>
      </td>
      <td colspan="2" width="35%">
        <ccp:username id="<%= thisWiki.getModifiedBy() %>" /><br />
        <ccp:tz timestamp="<%= thisWiki.getModified() %>" dateOnly="true" default="&nbsp;"/>
      </td>
    </tr>
<%
    }
%>
</table>
<br />
</ccp:evaluate>

<%-- Start documents --%>
<ccp:evaluate if="<%= !overviewIssueListInfo.getExpandedSelection() && !overviewNewsListInfo.getExpandedSelection() && !overviewWikiListInfo.getExpandedSelection() %>">
<ccp:pagedListStatus tableClass="pagedListTab" showExpandLink="true" title="Documents" object="overviewFileItemListListInfo" rss='<%= "http://" + getServerUrl(request) + "/feed/documents.xml" %>'/>
<table cellpadding="4" cellspacing="0" width="100%">
  <ccp:evaluate if="<%= fileItemList.isEmpty() %>">
    <tr>
      <td>
        <ccp:label name="projectsOverview.noDocumentsFound">No Documents found.</ccp:label>
      </td>
    </tr>
  </ccp:evaluate>
<%
    rowid = 0;
    Iterator documentIterator = fileItemList.iterator();
    while (documentIterator.hasNext()) {
      rowid = (rowid != 1?1:2);
      FileItem thisFile = (FileItem) documentIterator.next();
%>
    <tr class="overviewrow<%= rowid %>">
      <td width="65%">
        <table border="0" cellpadding="2" cellspacing="0">
          <tr>
            <td valign="top">
              <%= thisFile.getImageTag("-23", ctx) %>
            </td>
            <td>
              <ccp:evaluate if="<%= thisFile.isImageFormat() %>">
                <a href="<%= ctx %>/ProjectManagementFiles.do?command=Download&pid=<%= thisFile.getLinkItemId() %>&fid=<%= thisFile.getId() %>&folderId=<%= StringUtils.encodeUrl(request.getParameter("folderId")) %>&view=true&ext=<%= StringUtils.encodeUrl(thisFile.getExtension()) %>"<ccp:evaluate if="<%= thisFile.isImageFormat() %>"> rel="shadowbox[Images]"</ccp:evaluate>><%= toHtml(thisFile.getSubject()) %></a><br />
              </ccp:evaluate>
              <ccp:evaluate if="<%= !thisFile.isImageFormat() %>">
                <a href="<%= ctx %>/ProjectManagementFiles.do?command=Details&pid=<%= thisFile.getLinkItemId() %>&fid=<%= thisFile.getId() %>&folderId=<%= thisFile.getFolderId() %>"><%= toHtml(thisFile.getSubject()) %></a><br />
              </ccp:evaluate>
              <a class="searchLink" href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&pid=<%= thisFile.getLinkItemId() %>&section=File_Library&folderId=<%= thisFile.getFolderId() %>"><ccp:project id="<%= thisFile.getLinkItemId() %>"/></a>
            </td>
          </tr>
        </table>
      </td>
      <td colspan="2" width="35%">
        <ccp:username id="<%= thisFile.getModifiedBy() %>" /><br />
        <ccp:tz timestamp="<%= thisFile.getModified() %>" dateOnly="true" default="&nbsp;"/>
      </td>
    </tr>
<%
    }
%>
</table>
<br />
</ccp:evaluate>
</td>
</tr>
</table>
<iframe src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/empty.html" name="server_commands" id="server_commands" style="visibility:hidden" height="0"></iframe>
