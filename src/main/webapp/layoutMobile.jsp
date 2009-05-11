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
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.connect.web.modules.search.beans.SearchBean" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="menuList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="publicProjects" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="PageBody" class="java.lang.String" scope="request"/>
<jsp:useBean id="projectView" class="java.lang.String" scope="session"/>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="portal" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="invitationCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="whatsAssignedCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="whatsNewCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="Tracker" class="com.concursive.connect.cms.portal.utils.Tracker" scope="application"/>
<jsp:useBean id="searchBean" class="com.concursive.connect.web.modules.search.beans.SearchBean" scope="session" />
<jsp:useBean id="searchBeanInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<%@ include file="initPage.jsp" %>
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
<%
  response.setHeader("Pragma", "no-cache"); // HTTP 1.0
  response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<html>
<head>
  <ccp:evaluate if="<%= !hasText(portal.getTitle()) %>">
    <ccp:evaluate if='<%= !hasText(applicationPrefs.get("TITLE")) %>'>
      <title>ConcourseConnect</title>
    </ccp:evaluate>
    <ccp:evaluate if='<%= hasText(applicationPrefs.get("TITLE")) %>'>
      <title><%= toHtml(applicationPrefs.get("TITLE")) %></title>
    </ccp:evaluate>
    <meta name="description" content="ConcourseConnect Java Web based project management, enterprise 2.0 and collaboration software" />
  </ccp:evaluate>
  <ccp:evaluate if="<%= hasText(portal.getTitle()) %>">
    <ccp:evaluate if='<%= !hasText(applicationPrefs.get("TITLE")) %>'>
      <title>ConcourseConnect: <%= toHtml(portal.getTitle()) %></title>
    </ccp:evaluate>
    <ccp:evaluate if='<%= hasText(applicationPrefs.get("TITLE")) %>'>
      <title><%= toHtml(applicationPrefs.get("TITLE")) %>: <%= toHtml(portal.getTitle()) %></title>
    </ccp:evaluate>
    <meta name="description" content="<%= toHtml(portal.getShortDescription()) %>">
  </ccp:evaluate>
  <meta name="keywords" content="ConcourseConnect, iphone, collaboration, project management" />
  <meta name="viewport" content="width=320" />
  <jsp:include page="css_include.jsp" flush="true"/>
</head>
<%
boolean isUserLoggedIn = User.isLoggedIn();
boolean doScrollTop = request.getParameter("scrollTop") != null;
boolean hasFeaturedProjects = publicProjects.size() > 0;
boolean hasAdminAccess = User.getAccessAdmin();
boolean hasInvitations = hasText(invitationCount) && !"0".equals(invitationCount);
boolean hasWhatsAssignedCount = hasText(whatsAssignedCount) && !"0".equals(whatsAssignedCount);
boolean hasWhatsNewCount = hasText(whatsNewCount) && !"0".equals(whatsNewCount);
%>
<body bgcolor="#FFFFFF" LEFTMARGIN="0" MARGINWIDTH="0" TOPMARGIN="0" MARGINHEIGHT="0">
<ccp:evaluate if="<%= isUserLoggedIn %>">
<table border="0" width="100%" cellspacing="0" cellpadding="0">
  <tr>
    <td nowrap>
      <form name="search" action="<%= ctx %>/search" method="post">
        <input type="hidden" name="projectId" value="<%= searchProjectId %>" />
        <select name="scope" onChange="this.form.query.focus()">
          <option value="all" <%= selected(searchBean, SearchBean.UNDEFINED) %>>All Information</option>
          <option value="allDetails" <%= selected(searchBean, SearchBean.DETAILS) %>>Project Details</option>
          <option value="allNews" <%= selected(searchBean, SearchBean.NEWS) %>>News</option>
          <option value="allWiki" <%= selected(searchBean, SearchBean.WIKI) %>>Wiki</option>
          <option value="allDiscussion" <%= selected(searchBean, SearchBean.DISCUSSION) %>>Discussion</option>
          <option value="allDocuments" <%= selected(searchBean, SearchBean.DOCUMENTS) %>>Documents</option>
          <option value="allLists" <%= selected(searchBean, SearchBean.LISTS) %>>Lists</option>
          <option value="allPlan" <%= selected(searchBean, SearchBean.PLAN) %>>Plans</option>
          <option value="allTickets" <%= selected(searchBean, SearchBean.TICKETS) %>>Tickets</option>
        </select>
        <input type="text" size="10" name="query" value="" />
        <input type="image" src="<%= ctx %>/images/icons/stock_zoom-16.gif" systran="yes" border="0" alt="Search" name="Search" value="Search" align="absMiddle" />
        <ccp:evaluate if="<%= inProject || searchThis %>">
          <br />
          <input type="radio" onClick="this.form.query.focus()" name="type" value="all" <%= !searchThis ? "checked" : "" %> />All projects<br />
          <input type="radio" onClick="this.form.query.focus()" name="type" value="this" <%= searchThis ? "checked" : "" %> />This project
        </ccp:evaluate>
        <ccp:evaluate if="<%= !inProject && !searchThis %>">
          <input type="hidden" name="type" value="all" />
        </ccp:evaluate>
        <input type="hidden" name="auto-populate" value="true" />
      </form>
    </td>
  </tr>

  <tr>
    <td align="center" width="100%">
            <a href="<%= ctx %>/ProjectManagement.do?command=Overview">What's New?</a>
            <ccp:evaluate if="<%= hasWhatsNewCount %>">
              (<font color="red"><%= toHtml(whatsNewCount) %></font>)
            </ccp:evaluate>
        |
            <a href="<%= ctx %>/ProjectManagement.do?command=Assignments">What's Assigned?</a>
            <ccp:evaluate if="<%= hasWhatsAssignedCount %>">
              (<font color="red"><%= toHtml(whatsAssignedCount) %></font>)
            </ccp:evaluate>
      <br />

            <a href="<%= ctx %>/ProjectManagement.do?command=ProjectList">Project List</a>
      |
            <a href="<%= ctx %>/invites">Invites</a>
            <ccp:evaluate if="<%= hasInvitations %>">
              (<font color="red"><%= toHtml(invitationCount) %></font>)
            </ccp:evaluate>
      |
            <a href="<%= ctx %>/ContactsSearch.do?command=Form">Contacts</a>
      <br />
        <ccp:evaluate if="<%= User.getAccessAdmin() %>">
              <a href="<%= ctx %>/admin">Admin</a>
          (<%= Tracker.getGuestCount() %>
          guest<ccp:evaluate if="<%= Tracker.getGuestCount() != 1 %>">s</ccp:evaluate> /
          <a href="javascript:popURL('<%= ctx %>/AdminMembers.do?popup=true','ITEAM_Members','400','500','yes','yes');"><%= Tracker.getUserCount() %> member<ccp:evaluate if="<%= Tracker.getUserCount() != 1 %>">s</ccp:evaluate></a>)<br />
        </ccp:evaluate>
    </td>
  </tr>
</table>
<br />
</ccp:evaluate>
<jsp:include page="<%= PageBody %>" flush="true"/>
</body>
</html>
