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
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project"%>
<%!
  public static String inlineSearch(HttpServletRequest request, String section) {
    String value = (String) request.getAttribute("inline_search_type");
    if (value != null && value.startsWith(section)) {
      return "selected";
    }
    return "";
  }
%>
<%@ include file="initPage.jsp" %>
<form name="inline_search" action="<%= ctx %>/search" method="get">
<ccp:evaluate if='<%= request.getAttribute("Project") == null || ((Project) request.getAttribute("Project")).getId() == -1 %>'>
  <ccp:label name="search.search">Search</ccp:label>: <input type="text" size="15" name="query" value="" />
  <input type="hidden" name="type" value="all" />
  <select name="scope" onChange="this.form.query.focus()">
    <option value="all" <%= inlineSearch(request, "all") %>><ccp:label name="search.item.all">All Information</ccp:label></option>
    <option value="allDetails" <%= inlineSearch(request, "allDetails") %>><ccp:label name="search.item.projectDetails">Project Details</ccp:label></option>
    <option value="allNews" <%= inlineSearch(request, "allNews") %>><ccp:label name="search.item.news">News</ccp:label></option>
    <option value="allWiki" <%= inlineSearch(request, "allWiki") %>><ccp:label name="search.item.wiki">Wiki</ccp:label></option>
    <option value="allDiscussion" <%= inlineSearch(request, "allDiscussion") %>><ccp:label name="search.item.discussion">Discussion</ccp:label></option>
    <option value="allDocuments" <%= inlineSearch(request, "allDocuments") %>><ccp:label name="search.item.documents">Documents</ccp:label></option>
    <option value="allLists" <%= inlineSearch(request, "allLists") %>><ccp:label name="search.item.lists">Lists</ccp:label></option>
    <option value="allPlan" <%= inlineSearch(request, "allPlan") %>><ccp:label name="search.item.plans">Plans</ccp:label></option>
    <option value="allTickets" <%= inlineSearch(request, "allTickets") %>><ccp:label name="search.item.tickets">Tickets</ccp:label></option>
  </select>
</ccp:evaluate>
<ccp:evaluate if='<%= request.getAttribute("Project") != null && ((Project) request.getAttribute("Project")).getId() > -1%>'>
  <ccp:label name="search.search">Search</ccp:label>: <input type="text" size="15" name="query" value="" />
  <input type="hidden" name="type" value="this" />
  <input type="hidden" name="projectId" value="<%= ((Project) request.getAttribute("Project")).getId() %>" />
  <input type="hidden" name="scope" value="<%= request.getAttribute("inline_search_type") %>" />
</ccp:evaluate>
  <%--
  <input type="image" src="<%= ctx %>/images/icons/stock_zoom-16.gif" systran="yes" border="0" alt="Search" name="Search" value="Search" align="absMiddle" />
  --%>
  <input type="hidden" name="auto-populate" value="true" />
</form>
