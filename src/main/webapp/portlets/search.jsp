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
<%@ page import="com.concursive.connect.web.modules.search.beans.SearchBean" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="searchBean" class="com.concursive.connect.web.modules.search.beans.SearchBean" scope="session" />
<jsp:useBean id="searchBeanInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<%@ include file="../initPage.jsp" %>
<%!
  public static String selected(SearchBean search, int section) {
    if (search.getSection() == section) {
      return "selected";
    }
    return "";
  }
%>
<%-- Temp. fix for Weblogic --%>
<%
  boolean inProject = request.getAttribute("Project") != null &&
      ((Project) request.getAttribute("Project")).getId() > -1 &&
      !((Project) request.getAttribute("Project")).getPortal();
  int searchProjectId = (inProject ? ((Project) request.getAttribute("Project")).getId() : searchBean.getProjectId());
  boolean searchThis = searchBean.getScope() == SearchBean.THIS;
%>
<table border="0" cellspacing="0" cellpadding="4" width="100%">
  <tr class="shadow">
    <td align="center" style="border-top: 1px solid #000; border-left: 1px solid #000; border-right: 1px solid #000" nowrap width="100%">
      <b><ccp:label name="search.search">Search</ccp:label></b>
    </td>
  </tr>
  <tr bgColor="#FFFFFF">
    <td style="border-left: 1px solid #000; border-right: 1px solid #000; border-bottom: 1px solid #000;" width="100%">
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <tr>
          <td>
            <form name="search" action="<%= ctx %>/Search.do?auto-populate=true" method="post">
              <input type="hidden" name="projectId" value="<%= searchProjectId %>" />
              <input type="text" size="15" name="query" value="" />
              <input type="image" src="<%= ctx %>/images/icons/stock_zoom-16.gif" systran="yes" border="0" alt="Search" name="Search" value="Search" align="absMiddle" /><br />
              <select name="scope" onChange="this.form.query.focus()">
                <option value="all" <%= selected(searchBean, SearchBean.UNDEFINED) %>><ccp:label name="search.item.all">All Information</ccp:label></option>
                <option value="allWebsite" <%= selected(searchBean, SearchBean.WEBSITE) %>><ccp:label name="search.item.website">Website</ccp:label></option>
                <option value="allDetails" <%= selected(searchBean, SearchBean.DETAILS) %>><ccp:label name="search.item.projects">Project Details</ccp:label></option>
                <option value="allNews" <%= selected(searchBean, SearchBean.NEWS) %>><ccp:label name="search.item.news">News</ccp:label></option>
                <option value="allWiki" <%= selected(searchBean, SearchBean.WIKI) %>><ccp:label name="search.item.wiki">Wiki</ccp:label></option>
                <option value="allDiscussion" <%= selected(searchBean, SearchBean.DISCUSSION) %>><ccp:label name="search.item.discussion">Discussion</ccp:label></option>
                <option value="allDocuments" <%= selected(searchBean, SearchBean.DOCUMENTS) %>><ccp:label name="search.item.documents">Documents</ccp:label></option>
                <option value="allLists" <%= selected(searchBean, SearchBean.LISTS) %>><ccp:label name="search.item.lists">Lists</ccp:label></option>
                <option value="allPlan" <%= selected(searchBean, SearchBean.PLAN) %>><ccp:label name="search.item.plans">Plans</ccp:label></option>
                <option value="allTickets" <%= selected(searchBean, SearchBean.TICKETS) %>><ccp:label name="search.item.tickets">Tickets</ccp:label></option>
              </select>
              <ccp:evaluate if="<%= inProject || searchThis %>">
                <br />
                <input type="radio" class="radio" onClick="this.form.query.focus()" name="type" value="all" <%= !searchThis ? "checked" : "" %> /><ccp:label name="search.allProjects">All projects</ccp:label><br />
                <input type="radio" class="radio" onClick="this.form.query.focus()" name="type" value="this" <%= searchThis ? "checked" : "" %> /><ccp:label name="search.thisProject">This project</ccp:label>
              </ccp:evaluate>
              <ccp:evaluate if="<%= !inProject && !searchThis %>">
                <input type="hidden" name="type" value="all" />
              </ccp:evaluate>
            </form>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
