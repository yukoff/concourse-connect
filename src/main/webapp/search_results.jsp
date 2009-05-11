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
<%@ page import="java.util.*,
                 com.concursive.connect.web.modules.search.beans.SearchBean" %>
<%@ page import="com.concursive.commons.search.SearchHTMLUtils" %>
<%@ page import="com.concursive.commons.files.FileUtils" %>
<%@ page import="org.apache.lucene.search.Hits" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ page import="com.concursive.connect.web.modules.search.utils.SearchUtils" %>
<jsp:useBean id="searchBean" class="com.concursive.connect.web.modules.search.beans.SearchBean" scope="session" />
<jsp:useBean id="searchBeanInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="projectIdList" class="java.util.ArrayList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  Hits hits = (Hits) request.getAttribute("hits");
  Long duration = (Long) request.getAttribute("duration");
  StringBuffer title = new StringBuffer();
  if (hits != null) {
    title.append(hits.length() + " result");
    if (hits.length() != 1) {
      title.append("s");
    }
    if (hits.length() > 1) {
      title.append(", sorted by relevance");
    }
    title.append(" for " + toHtml(searchBean.getQuery()));
  }
  if (duration != null) {
    title.append(" (" + duration + " ms)");
  }
  // Temp. fix for Weblogic
  String pagedListTitle = "<strong>" + title.toString() + "</strong>";
%>
<%!
  public static String selected(SearchBean search, int section) {
    if (search.getSection() == section) {
      return "rs";
    }
    return "s";
  }
%>
<script language="javascript" type="text/javascript">
  function limitToProject(projectId) {
    document.results_search.projectId.value = projectId;
    if (projectId == -1) {
      document.results_search.type.value = "all";
    } else {
      document.results_search.type.value = "this";
    }
    document.results_search.submit();
  }
  function changeScope(scopeValue) {
    document.results_search.scope.value = scopeValue;
    document.results_search.submit();
  }
  function changeOpenOnly(openValue) {
    document.results_search.openProjectsOnly.value = openValue;
    document.results_search.submit();
  }
</script>
<ccp:evaluate if="<%= hits != null %>">
  <ccp:pagedListStatus label="Results" title="<%= pagedListTitle %>" object="searchBeanInfo"/>
</ccp:evaluate>
<ccp:evaluate if="<%= hits == null %>">
  <br />
</ccp:evaluate>
<div class="yui-skin-sam" style="padding:4px;">
  <div class="yui-navset">
    <ul class="yui-nav">
<ccp:tabbedMenu text="All Information" key="allInformation" value='<%= searchBean.getSection() == SearchBean.UNDEFINED ? "allInformation" : ""  %>' url="javascript:changeScope('all');" type="li"/>
<ccp:evaluate if="<%= searchBean.getProjectId() == -1 %>">
<ccp:tabbedMenu text="Website" key="website" value='<%= searchBean.getSection() == SearchBean.WEBSITE ? "website" : ""  %>' url="javascript:changeScope('allWebsite');" type="li"/>
</ccp:evaluate>
<ccp:tabbedMenu text="Projects" key="details" value='<%= searchBean.getSection() == SearchBean.DETAILS ? "details" : ""  %>' url="javascript:changeScope('allDetails');" type="li"/>
<ccp:tabbedMenu text="Blogs" key="news" value='<%= searchBean.getSection() == SearchBean.NEWS ? "news" : ""  %>' url="javascript:changeScope('allNews');" type="li"/>
<ccp:tabbedMenu text="Wikis" key="wiki" value='<%= searchBean.getSection() == SearchBean.WIKI ? "wiki" : ""  %>' url="javascript:changeScope('allWiki');" type="li"/>
<ccp:tabbedMenu text="Discussions" key="discussion" value='<%= searchBean.getSection() == SearchBean.DISCUSSION ? "discussion" : ""  %>' url="javascript:changeScope('allDiscussion');" type="li"/>
<ccp:tabbedMenu text="Documents" key="documents" value='<%= searchBean.getSection() == SearchBean.DOCUMENTS ? "documents" : ""  %>' url="javascript:changeScope('allDocuments');" type="li"/>
<ccp:tabbedMenu text="Lists" key="lists" value='<%= searchBean.getSection() == SearchBean.LISTS ? "lists" : ""  %>' url="javascript:changeScope('allLists');" type="li"/>
<ccp:tabbedMenu text="Plans" key="plans" value='<%= searchBean.getSection() == SearchBean.PLAN ? "plans" : ""  %>' url="javascript:changeScope('allPlan');" type="li"/>
<ccp:tabbedMenu text="Tickets" key="tickets" value='<%= searchBean.getSection() == SearchBean.TICKETS ? "tickets" : ""  %>' url="javascript:changeScope('allTickets');" type="li"/>
<ccp:tabbedMenu text="Ads" key="ads" value='<%= searchBean.getSection() == SearchBean.ADS ? "ads" : ""  %>' url="javascript:changeScope('allAds');" type="li"/>
<ccp:tabbedMenu text="Classifieds" key="classifieds" value='<%= searchBean.getSection() == SearchBean.CLASSIFIEDS ? "classifieds" : ""  %>' url="javascript:changeScope('allClassifieds');" type="li"/>
<ccp:tabbedMenu text="Reviews" key="reviews" value='<%= searchBean.getSection() == SearchBean.REVIEWS ? "reviews" : ""  %>' url="javascript:changeScope('allReviews');" type="li"/>
    </ul>
  </div>
</div>
<ccp:evaluate if="<%= hits == null %>">
  <ccp:label name="search.results.noResultMessage">
  An invalid search request was made.<br />
  <br />
  This can occur if the search criteria is invalid, you don't have access to the specific filters set, or the search index has not
  been initialized.<br />
  <br />
  Please check the search criteria and try again.
  </ccp:label>
  <br /><br />
</ccp:evaluate>

<%-- Search results --%>
<table cellpadding="4" cellspacing="0" width="100%" border="0">
<%
int rowId = 0;
for (int i = searchBeanInfo.getCurrentOffset() ; hits != null && i < searchBeanInfo.getPageSize() ; i++) {
  //rowId = (rowId == 1 ? 2 : 1);
  rowId = 2;
  Document document = hits.doc(i);
  java.util.Date modified = null;
  try {
    modified = new java.util.Date(Long.parseLong(document.get("modified")));
  } catch (Exception e) {
  }
  String size = null;
  try {
    size = FileUtils.getRelativeSize(Float.parseFloat(document.get("size")), null);
  } catch (Exception e) {
  }
  String type = document.get("type");
  // Temp. fix for Weblogic
  boolean activityFolderType = "activityfolder".equals(type);
  boolean activityType = "activity".equals(type);
  boolean activityNoteType = "activitynote".equals(type);
  boolean fileType = "file".equals(type);
  boolean issueCategoryType = "issuecategory".equals(type);
  boolean issueType = "issue".equals(type);
  boolean issueReplyType = "issuereply".equals(type);
  boolean newsType = "news".equals(type) && document.get("newsPortal") == null;
  boolean websiteType = "news".equals(type) && document.get("newsPortal") != null;
  boolean wikiType = "wiki".equals(type);
  boolean newsStatusDraft = "-1".equals(document.get("newsStatus"));
  boolean newsStatusUnapproved = "1".equals(document.get("newsStatus"));
  boolean projectType = "project".equals(type);
  boolean outlineType = "outline".equals(type);
  boolean listCategoryType = "listcategory".equals(type);
  boolean listType = "list".equals(type);
  boolean ticketType = "ticket".equals(type);
  boolean adsType = "ads".equals(type);
  boolean classifiedsType = "classifieds".equals(type);
  boolean reviewsType = "reviews".equals(type);
  boolean hasFilename = hasText(document.get("filename"));
%>
  <tr class="row<%= rowId %>">
    <td class="searchCount" valign="top" align="right" nowrap><%= i + 1 %>.</td>
    <td width="100%" style="padding-bottom: 12px;">
      <a class="search"
      <ccp:evaluate if="<%= activityFolderType %>">href="<%= ctx %>/ProjectManagementAssignmentsFolder.do?command=FolderDetails&pid=<%= document.get("projectId") %>&folderId=<%= document.get("assignmentFolderId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= activityType %>">href="<%= ctx %>/ProjectManagementAssignments.do?command=Details&pid=<%= document.get("projectId") %>&aid=<%= document.get("assignmentId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= activityNoteType %>">href="<%= ctx %>/ProjectManagementAssignments.do?command=ShowNotes&pid=<%= document.get("projectId") %>&aid=<%= document.get("assignmentId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= fileType %>">href="<%= ctx %>/ProjectManagementFiles.do?command=Details&pid=<%= document.get("projectId") %>&fid=<%= document.get("fileId") %>&folderId=<%= document.get("folderId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= issueCategoryType %>">href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Issues&pid=<%= document.get("projectId") %>&cid=<%= document.get("issueCategoryId") %>&resetList=true"></ccp:evaluate>
      <ccp:evaluate if="<%= issueType %>">href="<%= ctx %>/DiscussionActions.do?command=Details&pid=<%= document.get("projectId") %>&iid=<%= document.get("issueId") %>&resetList=true"></ccp:evaluate>
      <ccp:evaluate if="<%= issueReplyType %>">href="<%= ctx %>/DiscussionActions.do?command=Details&pid=<%= document.get("projectId") %>&iid=<%= document.get("issueId") %>&cid=<%= document.get("issueCategoryId") %>&resetList=true"></ccp:evaluate>
      <ccp:evaluate if="<%= wikiType %>">href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Wiki&pid=<%= document.get("projectId") %>&subject=<%= document.get("subjectLink") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= newsType %>">href="<%= ctx %>/BlogActions.do?command=Details&pid=<%= document.get("projectId") %>&id=<%= document.get("newsId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= websiteType %>">href='<%= document.get("newsPortalKey") %>'></ccp:evaluate>
      <ccp:evaluate if="<%= projectType %>">href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Details&pid=<%= document.get("projectId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= outlineType %>">href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Assignments&rid=<%= document.get("requirementId") %>&pid=<%= document.get("projectId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= listCategoryType %>">href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Lists&pid=<%= document.get("projectId") %>&cid=<%= document.get("listCategoryId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= listType %>">href="<%= ctx %>/ProjectManagementLists.do?command=Details&pid=<%= document.get("projectId") %>&id=<%= document.get("listId") %>"></ccp:evaluate>
      <ccp:evaluate if="<%= ticketType %>">href="<%= ctx %>/ProjectManagementTickets.do?command=Details&pid=<%= document.get("projectId") %>&id=<%= document.get("ticketId") %>&return=details"></ccp:evaluate>
      <ccp:evaluate if="<%= adsType %>">href="<%= ctx %>/ProjectManagementAds.do?command=Details&pid=<%= document.get("projectId") %>&id=<%= document.get("adId") %>&return=details"></ccp:evaluate>
      <ccp:evaluate if="<%= classifiedsType %>">href="<%= ctx %>/ProjectManagementClassifieds.do?command=Details&pid=<%= document.get("projectId") %>&id=<%= document.get("classifiedId") %>&return=details"></ccp:evaluate>
      <ccp:evaluate if="<%= reviewsType %>">href="<%= ctx %>/ProjectManagementReviews.do?command=Details&pid=<%= document.get("projectId") %>&id=<%= document.get("ratingId") %>&return=details"></ccp:evaluate>

      <ccp:evaluate if='<%= hasText(document.get("title")) %>'><%= toHtml(document.get("title")) %></ccp:evaluate></a>

      <ccp:evaluate if="<%= activityFolderType %>">activity folder</ccp:evaluate>
      <ccp:evaluate if="<%= activityType %>">activity</ccp:evaluate>
      <ccp:evaluate if="<%= activityNoteType %>">activity note</ccp:evaluate>
      <ccp:evaluate if="<%= fileType %>">document</ccp:evaluate>
      <ccp:evaluate if="<%= issueCategoryType %>">discussion forum</ccp:evaluate>
      <ccp:evaluate if="<%= issueType %>">discussion topic</ccp:evaluate>
      <ccp:evaluate if="<%= issueReplyType %>">discussion reply</ccp:evaluate>
      <ccp:evaluate if="<%= wikiType %>">wiki</ccp:evaluate>
      <ccp:evaluate if="<%= newsType %>">blog</ccp:evaluate>
      <ccp:evaluate if="<%= projectType %>">project</ccp:evaluate>
      <ccp:evaluate if="<%= outlineType %>">plan</ccp:evaluate>
      <ccp:evaluate if="<%= listCategoryType %>">list</ccp:evaluate>
      <ccp:evaluate if="<%= listType %>">list item</ccp:evaluate>
      <ccp:evaluate if="<%= ticketType %>">ticket</ccp:evaluate>

      <ccp:evaluate if="<%= newsType %>">
        <ccp:evaluate if="<%= newsStatusDraft  %>">(draft)</ccp:evaluate>
        <ccp:evaluate if="<%= newsStatusUnapproved %>">(unapproved)</ccp:evaluate>
      </ccp:evaluate>
      <ccp:evaluate if="<%= adsType %>">ads</ccp:evaluate>
      <ccp:evaluate if="<%= classifiedsType %>">classifieds</ccp:evaluate>
      <ccp:evaluate if="<%= reviewsType %>">reviews</ccp:evaluate>
      <%--<ccp:evaluate if="<%= hasFilename %>">[<%= document.get("filename") %>]</ccp:evaluate>--%>
      <br />
<%
    String highlightedText = SearchHTMLUtils.highlightText(searchBean.getTerms(), document.get("contents"));
    boolean hasHighlightedText = hasText(highlightedText);
%>
      <ccp:evaluate if="<%= hasHighlightedText %>">
        <%= SearchHTMLUtils.highlightedTextToHtml(highlightedText) %>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !hasHighlightedText %>">
        <%= StringUtils.toHtml(document.get("title")) %>
      </ccp:evaluate>
      <br />
<ccp:evaluate if="<%= !websiteType %>">
<%-- Temp. fix for Weblogic --%>
<%
  boolean hasSize = (size != null);
  boolean hasModified = (modified != null);
  String projectId = document.get("projectId");
%>
      <a class="searchLink"
         href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&pid=<%= document.get("projectId") %>">Go to <ccp:project id="<%= projectId %>"/></a>
      <ccp:evaluate if="<%= hasSize %>">
      -
      <%= size %>
      </ccp:evaluate>
      <ccp:evaluate if="<%= hasModified %>">
      -
      <ccp:tz timestamp="<%= new Timestamp(modified.getTime()) %>" dateOnly="true" dateFormat="<%= DateFormat.LONG %>"/>
      </ccp:evaluate>
      <%--
      -
      <%= hits.score(i) %>
      --%>
      <%--
      <ccp:evaluate if="<%= searchBean.getProjectId() == -1 %>">
        -
        [<a href="javascript:limitToProject(<%= document.get("projectId") %>);">this project only</a>]
      </ccp:evaluate>
      --%>
</ccp:evaluate>
    </td>
  </tr>
<%
}
%>
<ccp:evaluate if="<%= hits != null && hits.length() == 0 %>">
  <tr><td>No results found.</td></tr>
</ccp:evaluate>
</table>
<ccp:evaluate if="<%= hits != null && hits.length() > 8 %>">
  <br />
  <center>
  <a class="<%= selected(searchBean, SearchBean.UNDEFINED) %>" href="javascript:changeScope('all');"><ccp:label name="search.results.search.allInformation">All Information</ccp:label></a>
  <ccp:evaluate if="<%= searchBean.getProjectId() == -1 %>">
    <a class="<%= selected(searchBean, SearchBean.WEBSITE) %>" href="javascript:changeScope('allWebsite');"><ccp:label name="search.results.search.website">Website</ccp:label></a>
  </ccp:evaluate>
  <a class="<%= selected(searchBean, SearchBean.DETAILS) %>" href="javascript:changeScope('allDetails');"><ccp:label name="search.results.search.projectDetails">Project Details</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.NEWS) %>" href="javascript:changeScope('allNews');"><ccp:label name="search.results.search.blogs">Blogs</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.WIKI) %>" href="javascript:changeScope('allWiki');"><ccp:label name="search.results.search.wikis">Wikis</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.DISCUSSION) %>" href="javascript:changeScope('allDiscussion');"><ccp:label name="search.results.search.discussions">Discussions</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.DOCUMENTS) %>" href="javascript:changeScope('allDocuments');"><ccp:label name="search.results.search.documents">Documents</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.LISTS) %>" href="javascript:changeScope('allLists');"><ccp:label name="search.results.search.lists">Lists</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.PLAN) %>" href="javascript:changeScope('allPlan');"><ccp:label name="search.results.search.plans">Plans</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.TICKETS) %>" href="javascript:changeScope('allTickets');"><ccp:label name="search.results.search.tickets">Tickets</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.ADS) %>" href="javascript:changeScope('allAds');"><ccp:label name="search.results.search.ads">Ads</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.CLASSIFIEDS) %>" href="javascript:changeScope('allClassifieds');"><ccp:label name="search.results.search.classifieds">Classifieds</ccp:label></a>
  <a class="<%= selected(searchBean, SearchBean.REVIEWS) %>" href="javascript:changeScope('allReviews');"><ccp:label name="search.results.search.reviews">Reviews</ccp:label></a>
  </center>
</ccp:evaluate>
<ccp:evaluate if="<%= hits != null && hits.length() > 0 %>">
  <br />
  <ccp:pagedListControl object="searchBeanInfo"/>
</ccp:evaluate>
<form name="results_search" action="<%= ctx %>/search" method="get">
  <input type="hidden" name="categoryId" value="<%= searchBean.getCategoryId() %>" />
  <input type="hidden" name="query" value="<%= toHtmlValue(searchBean.getQuery()) %>" />
  <input type="hidden" name="scope" value="<%= searchBean.getScope() %>" />
  <input type="hidden" name="type" value="<%= searchBean.getType() %>" />
  <input type="hidden" name="filter" value="<%= searchBean.getFilter() %>" />
  <input type="hidden" name="projectId" value="<%= searchBean.getProjectId() %>" />
  <input type="hidden" name="openProjectsOnly" value="<%= searchBean.getOpenProjectsOnly() %>" />
  <input type="hidden" name="auto-populate" value="true" />
<%--
    <div style="padding:4px">
      <div style="font-size: .8em; display:inline; padding-right: 10px"><a class="<ccp:evaluate if="<%= searchBean.getOpenProjectsOnly() %>">r</ccp:evaluate>s" href="javascript:changeOpenOnly('true');">Open Projects Only</a></div>
      <div style="font-size: .8em; display:inline; padding-right: 10px"><a class="<ccp:evaluate if="<%= !searchBean.getOpenProjectsOnly() %>">r</ccp:evaluate>s" href="javascript:changeOpenOnly('false');">Open and Closed Projects</a></div>
    </div>
    <div style="padding:4px">
<div style="font-size: .8em; display:inline; padding-right: 10px"><a class="<ccp:evaluate if="<%= searchBean.getProjectId() == -1 %>">r</ccp:evaluate>s" href="javascript:limitToProject(-1);">Any Project</a></div>
<%
  for (int i = 0; i < projectIdList.size(); i++) {
    String projectIdValue = (String) projectIdList.get(i);
%>
  <div style="font-size: .8em; display:inline; padding-right: 10px"><a class="<ccp:evaluate if="<%= searchBean.getProjectId() == Integer.parseInt(projectIdValue) %>">r</ccp:evaluate>s" href="javascript:limitToProject(<%= projectIdValue %>);"><ccp:project id="<%= projectIdValue %>" /></a></div>
<%
  }
%>
      </div>
--%>
</form>
<%--
<br />
<br />
<font color="#999999">
Parsed: <%= searchBean.getParsedQuery() %><br />
Actual Query: <%= toHtml((String) request.getAttribute("queryString")) %><br />
</font>
--%>
