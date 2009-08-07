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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.search.beans.SearchBean" %>
<%@ page import="com.concursive.connect.web.utils.HtmlSelect" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.concursive.connect.config.ApplicationVersion" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ page import="com.concursive.connect.cms.portal.dao.DashboardPage" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="Tracker" class="com.concursive.connect.cms.portal.utils.Tracker" scope="application"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="searchBean" class="com.concursive.connect.web.modules.search.beans.SearchBean" scope="session"/>
<jsp:useBean id="projectView" class="java.lang.String" scope="session"/>
<jsp:useBean id="publicProjects" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="PageBody" class="java.lang.String" scope="request"/>
<jsp:useBean id="requestInvitationCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="requestWhatsAssignedCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="requestWhatsNewCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="portal" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="menuList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="menuCategoryList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="webSiteLanguageList" class="com.concursive.connect.web.modules.translation.dao.WebSiteLanguageList" scope="request"/>
<%@ include file="../../../initPage.jsp" %>
<%!
  public static String selected(SearchBean search, int section) {
    if (search.getSection() == section) {
      return "selected";
    }
    return "";
  }
%>
<%
  if (clientType.getType() == -1) {
    clientType.setParameters(request);
  }

  response.setHeader("Pragma", "no-cache"); // HTTP 1.0
  response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
  response.setDateHeader("Expires", 0); //prevents caching at the proxy server

  boolean inProject = request.getAttribute("project") != null &&
      ((Project) request.getAttribute("project")).getId() > -1 &&
      !((Project) request.getAttribute("project")).getPortal();

  String mainTab = portal.getPortalKey();
  if (mainTab == null) {
    mainTab = "none";
  }
  if (PageBody.indexOf("/projects_overview") > -1 ||
      PageBody.indexOf("/projects_dashboard") > -1 ||
      PageBody.indexOf("/projects_assignments") > -1 ||
      PageBody.indexOf("/rss") > -1 ||
      PageBody.indexOf("/reports_") > -1) {
    mainTab = "user_home";
  } else if (PageBody.indexOf("/projects_discussion") > -1) {
    mainTab = "discussions";
  } else if (PageBody.indexOf("/user_") > -1) {
    mainTab = "profile";
  } else if (PageBody.indexOf("/contacts_") > -1) {
    mainTab = "contacts";
  } else if (inProject ||
      request.getAttribute("projectList") != null ||
      PageBody.indexOf("/projects_timesheet") > -1 ||
      PageBody.indexOf("/projects_resources") > -1) {
    mainTab = "projects";
  }

  boolean isMaximized = "MAXIMIZED".equals(projectView);
  boolean isUserLoggedIn = User.isLoggedIn();
  boolean doScrollTop = request.getParameter("scrollTop") != null;
  boolean hasFeaturedProjects = publicProjects.size() > 0;
  boolean hasInvitations = hasText(requestInvitationCount) && !"0".equals(requestInvitationCount);
  boolean hasWhatsAssignedCount = hasText(requestWhatsAssignedCount) && !"0".equals(requestWhatsAssignedCount);
  boolean hasWhatsNewCount = hasText(requestWhatsNewCount) && !"0".equals(requestWhatsNewCount);
  boolean sslEnabled = "true".equals(applicationPrefs.get("SSL"));
  boolean hasAdminAccess = User.getAccessAdmin();
  boolean hasContentEditorAccess = (User.hasContentEditorAccess(portal.getLanguageId()));
  int searchProjectId = (inProject ? ((Project) request.getAttribute("project")).getId() : searchBean.getProjectId());
  boolean searchThis = searchBean.getScope() == SearchBean.THIS;
  boolean hasReportsAccess = User.getAccessRunReports();

  HtmlSelect webSiteLanguageSelect = webSiteLanguageList.getHtmlSelectByLocale();
  webSiteLanguageSelect.setJsEvent("onChange='document.webSiteLanguageForm.submit();'");

  String pageTitle = applicationPrefs.get("TITLE");
  String metaDescription = applicationPrefs.get("TITLE") + " - commercial, non-commercial, businesses, organizations, groups, and personal, completely connected social networking and tools";
  String metaKeywords = "small business, crm, tools, directory, groups, social networking";
  if (inProject) {
    Project thisProject = (Project) request.getAttribute("project");
    String section = (String) request.getAttribute("IncludeSection");
    String sectionTitle = ProjectUtils.decodeLabel(thisProject, section);
    if (sectionTitle != null) {
      pageTitle = thisProject.getTitle() + " - " + ProjectUtils.decodeLabel(thisProject, section) + " - " + applicationPrefs.get("TITLE");
    }
    if (thisProject.getFeatures().getAllowGuests()) {
      // public projects have a public description
      if (hasText(thisProject.getShortDescription())) {
        metaDescription = thisProject.getShortDescription();
        // Do not use the standard keywords if the description is being changed
        metaKeywords = null;
        // member only projects DO NOT have public keywords
        if (!thisProject.getFeatures().getMembershipRequired()) {
          if (hasText(thisProject.getKeywords())) {
            metaKeywords = thisProject.getKeywords();
          }
        }
      }
    }
  } else {
    DashboardPage dashboardPage = (DashboardPage) request.getAttribute("dashboardPage");

    if (dashboardPage != null) {
      if (hasText(dashboardPage.getTitle())) {
        pageTitle = dashboardPage.getTitle() + " - " + pageTitle;
        String generatedTitle = (String) request.getAttribute(Constants.REQUEST_GENERATED_TITLE);
        if (generatedTitle != null) {
          pageTitle = generatedTitle + " - " + pageTitle;
        }
      }
      String generatedCategory = (String) request.getAttribute(Constants.REQUEST_GENERATED_CATEGORY);
      if (generatedCategory == null) {
        if (hasText(dashboardPage.getCategory())) {
          generatedCategory = dashboardPage.getCategory();
        }
      }
      request.setAttribute("dashboardPageCategory", generatedCategory);
      if (hasText(dashboardPage.getDescription())) {
        metaDescription = dashboardPage.getDescription();
      }
      if (hasText(dashboardPage.getKeywords())) {
        metaKeywords = dashboardPage.getKeywords();
      }
    }
  }
  boolean useLocations = "true".equals(applicationPrefs.get("USE_LOCATIONS"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <ccp:evaluate if="<%= hasText(metaDescription) %>">
      <meta name="description" content="<%= toHtml(metaDescription) %>"/>
    </ccp:evaluate>
    <ccp:evaluate if="<%= hasText(metaKeywords) %>">
      <meta name="keywords" content="<%= toHtml(metaKeywords) %>"/>
    </ccp:evaluate>
    <title><%= toHtml(pageTitle) %></title>
    <%--
      <link rel="shortcut icon" href="<%= ctx %>/favicon.ico" type="image/x-icon" />
      <link rel="apple-touch-icon" href="<%= ctx %>/images/apple-touch-icon.png" type="image/png" />
    --%>
    <jsp:include page="../../../css_include.jsp" flush="true"/>
    <link rel="SHORTCUT ICON" href="${ctx}/images/icons/favicon.ico"/>
  </head>
  <body>
    <p class="access"><a href="#content" accesskey="1">Skip Navigation and Search to Content</a></p>
    <div class="ccp-container">
      <div class="ccp-header">
        <div class="ccp-user-navigation">
          <div class="ccp-site-menu">
            <ul>
              <li class="first">
                <a href="<%= ctx %>/show/main-profile" title="Get information about <%= toHtml(applicationPrefs.get("TITLE")) %>">About</a>
              </li>
              <ccp:permission object="requestMainProfile" name="project-news-view">
                <c:if test="${requestMainProfile.features.showBlog}">
                  <li><a href="<%= ctx %>/show/main-profile/blog" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Blog" object="requestMainProfile"/>"><ccp:tabLabel name="Blog" object="requestMainProfile"/></a></li>
                </c:if>
              </ccp:permission>
              <ccp:permission object="requestMainProfile" name="project-wiki-view">
                <c:if test="${requestMainProfile.features.showWiki}">
                  <li><a href="<%= ctx %>/show/main-profile/wiki" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Wiki" object="requestMainProfile"/>"><ccp:tabLabel name="Wiki" object="requestMainProfile"/></a></li>
                </c:if>
              </ccp:permission>
              <ccp:permission object="requestMainProfile" name="project-calendar-view">
                <c:if test="${requestMainProfile.features.showCalendar}">
                  <li><a href="<%= ctx %>/show/main-profile/calendar" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Calendar" object="requestMainProfile"/>"><ccp:tabLabel name="Calendar" object="requestMainProfile"/></a></li>
                </c:if>
              </ccp:permission>
              <ccp:permission object="requestMainProfile" name="project-discussion-forums-view">
                <c:if test="${requestMainProfile.features.showDiscussion}">
                  <li><a href="<%= ctx %>/show/main-profile/discussion" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Discussion" object="requestMainProfile"/>"><ccp:tabLabel name="Discussion" object="requestMainProfile"/></a></li>
                </c:if>
              </ccp:permission>
              <ccp:permission object="requestMainProfile" name="project-classifieds-view">
                <c:if test="${requestMainProfile.features.showClassifieds}">
                  <li><a href="<%= ctx %>/show/main-profile/classifieds" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Classifieds" object="requestMainProfile"/>"><ccp:tabLabel name="Classifieds" object="requestMainProfile"/></a></li>
                </c:if>
              </ccp:permission>
              <ccp:permission object="requestMainProfile" name="project-tickets-view">
                <c:if test="${requestMainProfile.features.showIssues}">
                  <li><a href="<%= ctx %>/show/main-profile/issues" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Issues" object="requestMainProfile"/>"><ccp:tabLabel name="Issues" object="requestMainProfile"/></a></li>
                </c:if>
              </ccp:permission>
              <li class="last"><a href="<%= ctx %>/contact-us" title="Contact <%= toHtml(applicationPrefs.get("TITLE")) %>">Contact Us</a></li>
            </ul>
          </div>
          <div class="ccp-user-menu">
            <ul>
              <%-- Display User Actions if user is logged in --%>
              <ccp:evaluate if="<%= isUserLoggedIn %>">
                <li class="first">
                  Hello <ccp:username id="<%= User.getId() %>"/>
                </li>
                <li>
                  <a href="<%= ctx %>/settings">Settings</a>
                </li>
                <ccp:evaluate if="<%= User.getAccessAdmin() %>">
                  <ccp:evaluate if="<%= User.isConnectCRMAdmin() || User.isConnectCRMManager() %>">
                    <li>
                      <a href="<%= ctx %>/show/main-profile/crm" target="_blank">CRM</a>
                    </li>
                  </ccp:evaluate>
                  <li>
                    <a href="<%= ctx %>/admin">Admin</a>
                  </li>
                </ccp:evaluate>
                <%--
                <ccp:evaluate if="<%= hasReportsAccess %>">
                  <li>
                    <a class="rollover" href="<%= ctx %>/reports">Reports</a>
                  </li>
                </ccp:evaluate>
                --%>
                <li class="last">
                  <a href="http://<%= getServerUrl(request) %>/logout">Sign Out</a>
                </li>
              </ccp:evaluate>
              <%-- Display Login and register actions if user is not logged in --%>
              <ccp:evaluate if="<%= !isUserLoggedIn %>">
                <li class="first">
                  <a
                    href="http<ccp:evaluate if="<%= sslEnabled %>">s</ccp:evaluate>://<%= getServerUrl(request) %>/login<ccp:evaluate if='<%= request.getAttribute("requestedURL") != null %>'>?redirectTo=<%= URLEncoder.encode((String)request.getAttribute("requestedURL"), "UTF-8") %></ccp:evaluate>"
                    title="Login to <%= toHtml(applicationPrefs.get("TITLE")) %>" accesskey="s" rel="nofollow">Sign In</a>
                </li>
                <ccp:evaluate if='<%= "true".equals(applicationPrefs.get("REGISTER")) %>'>
                  <li class="last">
                    <a
                      href="http<ccp:evaluate if="<%= sslEnabled %>">s</ccp:evaluate>://<%= getServerUrl(request) %>/register"
                      title="Register with <%= toHtml(applicationPrefs.get("TITLE")) %>" accesskey="r"
                      rel="nofollow">Register</a>
                  </li>
                </ccp:evaluate>
              </ccp:evaluate>
            </ul>
          </div>
        </div>
        <%-- TODO: Make this section a portlet
        <p class="welcome">
          Welcome <ccp:username id="<%= User.getId() %>"/>
        --%>

        <ccp:evaluate if="<%= isUserLoggedIn %>">
          <div class="ccp-alert">
            <ccp:evaluate
                if="<%= (Integer.parseInt((String)request.getAttribute(\"requestPrivateMessageCount\"))) > 0 %>">
              You have <a href="${ctx}/show/<%= User.getProfileProject().getUniqueId() %>"><%= (String) request.getAttribute("requestPrivateMessageCount") %> new message<ccp:evaluate
                if="<%= (Integer.parseInt((String)request.getAttribute(\"requestPrivateMessageCount\"))) > 1 %>">s</a>
              </ccp:evaluate>
            </ccp:evaluate>
            <ccp:evaluate if="<%= (Integer.parseInt((String)request.getAttribute(\"requestInvitationCount\"))) > 0 %>">
              You have <a href="${ctx}/show/<%= User.getProfileProject().getUniqueId() %>"><%= (String) request.getAttribute("requestInvitationCount") %> new invitation<ccp:evaluate
                  if="<%= (Integer.parseInt((String)request.getAttribute(\"requestInvitationCount\"))) > 1 %>">s</a>
                </ccp:evaluate>
            </ccp:evaluate>
          </div>
        </ccp:evaluate>
        <%--
        <p class="messages">We have <%= Tracker.getGuestCount() %> guest<ccp:evaluate
            if="<%= Tracker.getGuestCount() != 1 %>">s</ccp:evaluate>
          and
          <ccp:evaluate if="<%= User.getAccessAdmin() %>"><a
              href="javascript:popURL('<%= RequestUtils.getAbsoluteServerUrl(request) %>/AdminMembers.do?popup=true','Web_Members','400','500','yes','yes');">
            </ccp:evaluate>
            <%= Tracker.getUserCount() %>
            member<ccp:evaluate if="<%= Tracker.getUserCount() != 1 %>">s</ccp:evaluate></a> online.
        </p>
        --%>
        <a id="ccp-header-title-link-id" href="${ctx}/" accesskey="h" title="<%= toHtml(applicationPrefs.get("TITLE")) %>">
          <ccp:evaluate if="<%= !applicationPrefs.has(\"LOGO\") %>">
            <h1 id="ccp-header-title-item-id"><%= toHtml(applicationPrefs.get("TITLE")) %></h1>
         </ccp:evaluate>
          <ccp:evaluate if="<%= applicationPrefs.has(\"LOGO\") %>">
            <img id="ccp-header-title-image-id" src="${ctx}/image/<%= applicationPrefs.get("LOGO") %>/logo.png" alt="<%= toHtml(applicationPrefs.get("TITLE")) %>" />
         </ccp:evaluate>
        </a>
        <div class="ccp-search-form">
          <form action="<%= ctx %>/search" method="get">
            <fieldset>
              <legend>Search <%= toHtml(pageTitle) %></legend>
              <ccp:evaluate if="<%= menuCategoryList.size() > 1 %>">
                <label for="categoryId">Search</label>
                <%= menuCategoryList.getHtml("categoryId", searchBean.getCategoryId()) %>
              </ccp:evaluate>
              <label for="query">for</label>
              <%-- TODO: Add id attribute for properly labeling --%>
              <input type="text" size="20" name="query" value="<%= toHtmlValue(searchBean.getQuery()) %>"/>
              <ccp:evaluate if="<%= useLocations %>">
                <label for="location">near</label>
                <%-- TODO: Add id attribute for properly labeling --%>
                <input type="text" size="20" name="location" value="<%= toHtmlValue(searchBean.getLocation()) %>" />
              </ccp:evaluate>
              <input type="submit" alt="Search" value="Go" />
              <%-- Removed by popular request
              <ccp:evaluate if="<%= inProject || searchThis %>">
                <input type="radio" onClick="this.form.query.focus()" name="type" value="all" <%= !searchThis ? "checked" : "" %> />
                All
                <input type="radio" onClick="this.form.query.focus()" name="type" value="this" <%= searchThis ? "checked" : "" %> />
                This profile
              </ccp:evaluate>
              <ccp:evaluate if="<%= !inProject && !searchThis %>">
                <input type="hidden" name="type" value="all" />
              </ccp:evaluate>
              --%>
              <input type="hidden" name="type" value="all"/>
              <input type="hidden" name="scope" value="<%= searchBean.getScopeText() %>"/>
              <input type="hidden" name="filter" value="<%= searchBean.getFilter() %>"/>
              <input type="hidden" name="projectId" value="<%= searchProjectId %>"/>
              <input type="hidden" name="openProjectsOnly" value="true"/>
              <input type="hidden" name="auto-populate" value="true"/>
              <%--
              <a href="javascript:popURL('<%= ctx %>/Search.do?command=Tips&popup=true','Search_Tips','500','325','yes','yes')">tips</a>
              --%>
            </fieldset>
          </form>
        </div>
        <div class="ccp-navigation">
          <ul>
            <c:choose>
              <c:when test="${(empty project || empty project.category) && empty dashboardPageCategory}">
                <c:set var="tabbedMenuValue" value="${fn:substringBefore(chosenTab,'.shtml')}"/>
              </c:when>
              <c:when test="${!empty dashboardPageCategory}">
                <c:set var="tabbedMenuValue" value="${dashboardPageCategory}"/>
              </c:when>
              <c:otherwise>
                <c:set var="tabbedMenuValue" value="${fn:toLowerCase(project.category.description)}"/>
              </c:otherwise>
            </c:choose>
            <ccp:tabbedMenu text="<%= \"Home\" %>" key="home" value="${tabbedMenuValue}" url="${ctx}/" type="li"/>
            <c:forEach items="${tabCategoryList}" var="tabCategory" varStatus="status">
              <ccp:tabbedMenu text="${tabCategory.description}"
                                 key="${fn:toLowerCase(fn:replace(tabCategory.description,' ','_'))}"
                                 value="${tabbedMenuValue}"
                                 url='${ctx}/${fn:toLowerCase(fn:replace(tabCategory.description," ","_"))}.shtml'
                                 type="li"/>
            </c:forEach>
            <%--
            <c:choose>
            	<c:when test="${chosenTab eq \"home.shtml\"}">
		            <link type="application/rss+xml" rel="alternate" href="${ctx}/feed/rss.xml" title="Home" />
	                <li><a type="application/rss+xml" rel="alternate" href="${ctx}/feed/rss.xml">Subscribe</a><img src="${ctx}/images/feed-icon-16x16.gif" /></li>
                </c:when>
                <c:otherwise>
		            <link type="application/rss+xml" rel="alternate" href="${ctx}/feed/${fn:toLowerCase(fn:replace(chosenCategory," ","_"))}/rss.xml" title="<c:out value="${chosenCategory}" /> Home" />
	                <li><a type="application/rss+xml" rel="alternate" href="${ctx}/feed/${fn:toLowerCase(fn:replace(chosenCategory," ","_"))}/rss.xml">Subscribe</a><img src="${ctx}/images/feed-icon-16x16.gif" /></li>
                </c:otherwise>
            </c:choose>
            --%>
          </ul>
        </div>
      </div><%-- End ccp-header --%>
      <div class="ccp-body">
        <p class="access"><a name="content">Main Content</a></p>
        <jsp:include page="<%= PageBody %>" flush="true"/>
      </div>
      <div class="ccp-footer">
        <ul>
          <ccp:tabbedMenu text="<%= \"Home\" %>" key="nokey" value="novalue" url="${ctx}/" type="li"/>
          <c:forEach items="${tabCategoryList}" var="tabCategory" varStatus="status">
            <ccp:tabbedMenu text="${tabCategory.description}"
                               key="nokey"
                               value="novalue"
                               url='${ctx}/${fn:toLowerCase(fn:replace(tabCategory.description," ","_"))}.shtml'
                               type="li"/>
          </c:forEach>
          <li>
            <a href="${ctx}/rss" title="RSS Feeds"><em>RSS</em></a>
          </li>
          <li class="last">
            <a href="${ctx}/show/main-profile/wiki/Site+Guidelines" rel="nofollow" title="Site Guildlines"><em>Guidelines</em></a>
          </li>
        </ul>
        <ul class="site-menu">
          <li class="first">
            <a href="<%= ctx %>/show/main-profile" title="Get information about <%= toHtml(applicationPrefs.get("TITLE")) %>">
              About
            </a>
          </li>
          <ccp:permission object="requestMainProfile" name="project-news-view">
            <c:if test="${requestMainProfile.features.showBlog}">
              <li>
                <a href="<%= ctx %>/show/main-profile/blog" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Blog" object="requestMainProfile"/>">
                  <em><ccp:tabLabel name="Blog" object="requestMainProfile"/></em>
                </a>
              </li>
            </c:if>
          </ccp:permission>
          <ccp:permission object="requestMainProfile" name="project-wiki-view">
            <c:if test="${requestMainProfile.features.showWiki}">
              <li>
                <a href="<%= ctx %>/show/main-profile/wiki" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Wiki" object="requestMainProfile"/>">
                  <em><ccp:tabLabel name="Wiki" object="requestMainProfile"/></em>
                </a>
              </li>
            </c:if>
          </ccp:permission>
          <ccp:permission object="requestMainProfile" name="project-calendar-view">
            <c:if test="${requestMainProfile.features.showCalendar}">
              <li>
                <a href="<%= ctx %>/show/main-profile/calendar" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Calendar" object="requestMainProfile"/>">
                  <em><ccp:tabLabel name="Calendar" object="requestMainProfile"/></em>
                </a>
              </li>
            </c:if>
          </ccp:permission>
          <ccp:permission object="requestMainProfile" name="project-discussion-forums-view">
            <c:if test="${requestMainProfile.features.showDiscussion}">
              <li>
                <a href="<%= ctx %>/show/main-profile/discussion" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Discussion" object="requestMainProfile"/>">
                  <em><ccp:tabLabel name="Discussion" object="requestMainProfile"/></em>
                </a>
              </li>
            </c:if>
          </ccp:permission>
          <ccp:permission object="requestMainProfile" name="project-classifieds-view">
            <c:if test="${requestMainProfile.features.showClassifieds}">
              <li>
                <a href="<%= ctx %>/show/main-profile/classifieds" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Classifieds" object="requestMainProfile"/>">
                  <em><ccp:tabLabel name="Classifieds" object="requestMainProfile"/></em>
                </a>
              </li>
            </c:if>
          </ccp:permission>
          <ccp:permission object="requestMainProfile" name="project-tickets-view">
            <c:if test="${requestMainProfile.features.showIssues}">
              <li>
                <a href="<%= ctx %>/show/main-profile/issues" title="<%= toHtml(applicationPrefs.get("TITLE")) %> <ccp:tabLabel name="Issues" object="requestMainProfile"/>">
                  <em><ccp:tabLabel name="Issues" object="requestMainProfile"/></em>
                </a>
              </li>
            </c:if>
          </ccp:permission>
          <li class="last">
            <a href="<%= ctx %>/contact-us" title="Contact <%= toHtml(applicationPrefs.get("TITLE")) %>">
              <em>Contact Us</em>
            </a>
          </li>
        </ul>
        <p>
          Powered by <a title="The Open Source Community and Collaboration Solution" href="http://www.concursive.com/show/concourseconnect">Concursive ConcourseConnect</a>,
          <a title="The Open Source Business Social Software Platform" href="http://www.concursive.com">The Open Source Community and Collaboration Solution</a>
        </p>
      </div>
    </div>
    <%-- Allow pages to have a scrollTo... must be at end of html --%>
    <ccp:evaluate if="<%= doScrollTop %>">
      <script type="text/javascript">
        if (window.scrollTo) window.scrollTo(<%= request.getParameter("scrollLeft") %>, <%= request.getParameter("scrollTop") %>);
      </script>
    </ccp:evaluate>
    <div class="yui-skin-sam" id="popupLayer"></div>
  </body>
</html>
