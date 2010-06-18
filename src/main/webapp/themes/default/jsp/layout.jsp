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
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    <ccp:evaluate if="<%= hasText(metaDescription) %>">
      <meta name="description" content="<%= toHtml(metaDescription) %>"/>
    </ccp:evaluate>
    <ccp:evaluate if="<%= hasText(metaKeywords) %>">
      <meta name="keywords" content="<%= toHtml(metaKeywords) %>"/>
    </ccp:evaluate>
    <%-- Google Analytics --%>
    <ccp:evaluate if='<%= applicationPrefs.has("GOOGLE_ANALYTICS.VERIFY") %>'>
      <meta name="verify-v1" content="<%= applicationPrefs.get("GOOGLE_ANALYTICS.VERIFY") %>" />
    </ccp:evaluate>
    <title><%= toHtml(pageTitle) %></title>
    <%--
      <link rel="shortcut icon" href="${ctx}/favicon.ico" type="image/x-icon" />
      <link rel="apple-touch-icon" href="${ctx}/images/apple-touch-icon.png" type="image/png" />
    --%>
    <jsp:include page="../../../css_include.jsp" flush="true"/>
    <link rel="SHORTCUT ICON" href="${ctx}/favicon.ico"/>
  </head>
  <c:if test="<%= User.getProfileProjectId() > -1 %>">
    <c:set var="userProfile" value='<%= User.getProfileProject() %>'/>
  </c:if>
  <c:choose>
    <c:when test="${(empty project || empty project.category) && empty dashboardPageCategory}">
      <ccp:debug value="active tab: using chosenTab"/>
      <c:set var="tabbedMenuValue" value="${fn:substringBefore(chosenTab,'.shtml')}"/>
    </c:when>
    <c:when test="${!empty dashboardPageCategory}">
      <ccp:debug value="active tab: using dashboardPageCategory"/>
      <c:set var="tabbedMenuValue" value='${fn:toLowerCase(fn:replace(dashboardPageCategory," ","_"))}'/>
    </c:when>
    <c:when test="${!empty project && !empty userProfile && project.id == userProfile.id}">
      <ccp:debug value="active tab: using me"/>
      <c:set var="tabbedMenuValue" value="me"/>
    </c:when>
    <c:otherwise>
      <ccp:debug value="active tab: using project.category.description"/>
      <c:set var="tabbedMenuValue" value='${fn:toLowerCase(fn:replace(project.category.description," ","_"))}'/>
    </c:otherwise>
  </c:choose>
  <body<c:if test="${!empty tabbedMenuValue}"> id="ccp-body-${tabbedMenuValue}"</c:if>>
    <p class="access"><a href="#content" accesskey="1">Skip Navigation and Search to Content</a></p>
    <div class="ccp-container">
      <div class="ccp-header">
        <div class="ccp-header-title">
          <a id="ccp-header-title-link-id" href="${ctx}/" accesskey="h" title="<%= toHtml(applicationPrefs.get("TITLE")) %>">
            <ccp:evaluate if='<%= !applicationPrefs.has("LOGO") %>'>
              <h1 id="ccp-header-title-item-id" class="unitPng"><%= toHtml(applicationPrefs.get("TITLE")) %></h1>
            </ccp:evaluate>
            <ccp:evaluate if='<%= applicationPrefs.has("LOGO") %>'>
              <c:set var="headerLogo" value='<%= applicationPrefs.get("LOGO") %>'/>
              <img id="ccp-header-title-image-id" src="${ctx}/image/${headerLogo}/logo.png" width="${fn:endsWith(headerLogo, "-300x100") ? "300":"250" }" height="100" alt="<%= toHtml(applicationPrefs.get("TITLE")) %>" class="unitPng" />
            </ccp:evaluate>
          </a>
        </div>
        <div class="ccp-header-menu">
          <ccp:evaluate if='<%= isUserLoggedIn || !"true".equals(applicationPrefs.get("SENSITIVE_INFORMATION"))%>'>
            <div class="ccp-site-menu">
              <ul>
                <c:set var="isFirst">class="first"</c:set>
                <li ${isFirst}><a href="${ctx}/page/tags/all" title="<%= toHtml(applicationPrefs.get("TITLE")) %> Tags">Tags</a></li>
                <c:set var="isFirst" value=""/>
                <li ${isFirst}><a href="${ctx}/page/classifieds/all" title="<%= toHtml(applicationPrefs.get("TITLE")) %> Classifieds">Classifieds</a></li>
                <c:set var="isFirst">class="last"</c:set>
                <li ${isFirst}><a href="${ctx}/page/promotions/all" title="<%= toHtml(applicationPrefs.get("TITLE")) %> Promotions">Promotions</a></li>
  <%--
                <li ${isFirst}><a href="${ctx}/page/blogs/all" title="All <%= toHtml(applicationPrefs.get("TITLE")) %> Blogs">Blogs</a></li>
                <li ${isFirst}><a href="${ctx}/page/wiki/all" title="All <%= toHtml(applicationPrefs.get("TITLE")) %> Wiki">Wiki</a></li>
                <li ${isFirst}><a href="${ctx}/page/photos/all" title="All <%= toHtml(applicationPrefs.get("TITLE")) %> Photos">Photos</a></li>
                <li ${isFirst}><a href="${ctx}/page/calendar/all" title="All <%= toHtml(applicationPrefs.get("TITLE")) %> Calendars">Calendar</a></li>
                <li ${isFirst}><a href="${ctx}/page/discussion/all" title="All <%= toHtml(applicationPrefs.get("TITLE")) %> Discussion">Discussion</a></li>
                <li ${isFirst}><a href="${ctx}/page/documents/all" title="All <%= toHtml(applicationPrefs.get("TITLE")) %> Documents">Documents</a></li>
                <li ${isFirst}><a href="${ctx}/page/issues/all" title="All <%= toHtml(applicationPrefs.get("TITLE")) %> Issues">Issues</a></li>
  --%>
                <c:set var="isFirst" value=""/>
              </ul>
            </div>
          </ccp:evaluate>
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
                <ccp:evaluate if="<%= User.isConnectCRMAdmin() || User.isConnectCRMManager() %>">
                  <li>
                    <a href="<%= ctx %>/management-crm<ccp:evaluate if='<%= request.getAttribute("requestedURL") != null %>'>?returnURL=<%= URLEncoder.encode((String)request.getAttribute("requestedURL"), "UTF-8") %></ccp:evaluate>">Manage Community</a>
                  </li>
                </ccp:evaluate>
                <ccp:evaluate if="<%= User.getAccessAdmin() %>">
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
                <li>
                  <a href="${ctx}/logout">Sign Out</a>
                </li>
              </ccp:evaluate>
              <%-- Display Login and register actions if user is not logged in --%>
              <ccp:evaluate if="<%= !isUserLoggedIn %>">
                <c:choose>
                  <c:when test='<%= "true".equals(applicationPrefs.get("SENSITIVE_INFORMATION")) %>'>
                    <c:set var="isFirst">class="last"</c:set>
                  </c:when>
                  <c:otherwise>
                    <c:set var="isFirst">class="first"</c:set>
                  </c:otherwise>
                </c:choose>
                <li ${isFirst}>
                  <a href="http<ccp:evaluate if="<%= sslEnabled %>">s</ccp:evaluate>://<%= getServerUrl(request) %>/login<ccp:evaluate if='<%= request.getAttribute("requestedURL") != null %>'>?redirectTo=<%= URLEncoder.encode((String)request.getAttribute("requestedURL"), "UTF-8") %></ccp:evaluate>"
                     title="Login to <%= toHtml(applicationPrefs.get("TITLE")) %>" accesskey="s" rel="nofollow">Sign In</a>
                </li>
                <ccp:evaluate if='<%= "true".equals(applicationPrefs.get("REGISTER")) %>'>
                  <li>
                    <a href="http<ccp:evaluate if="<%= sslEnabled %>">s</ccp:evaluate>://<%= getServerUrl(request) %>/register"
                       title="Register with <%= toHtml(applicationPrefs.get("TITLE")) %>" accesskey="r"
                       rel="nofollow">Register</a>
                  </li>
                </ccp:evaluate>
                <c:set var="isFirst" value=""/>
              </ccp:evaluate>
              <ccp:permission object="requestMainProfile" name="project-wiki-view">
                <li ${isFirst}>
                  <a href="${ctx}/show/main-profile" title="Get information about <%= toHtml(applicationPrefs.get("TITLE")) %>">About Us</a>
                </li>
                <c:set var="isFirst" value=""/>
              </ccp:permission>
              <li class="last"><a href="${ctx}/contact-us" title="Contact <%= toHtml(applicationPrefs.get("TITLE")) %>">Contact Us</a></li>
              <c:set var="isFirst" value=""/>
            </ul>
          </div>
          <ccp:evaluate if='<%= isUserLoggedIn && ((Integer.parseInt((String)request.getAttribute("requestPrivateMessageCount"))) > 0 || (Integer.parseInt((String)request.getAttribute("requestInvitationCount"))) > 0) %>'>
            <div class="ccp-alert">
              <ccp:evaluate if='<%= (Integer.parseInt((String)request.getAttribute("requestPrivateMessageCount"))) > 0 %>'>
                <a href="${ctx}/show/<%= User.getProfileProject().getUniqueId() %>/messages">You have <%= (String) request.getAttribute("requestPrivateMessageCount") %> new message<ccp:evaluate
                  if='<%= (Integer.parseInt((String)request.getAttribute("requestPrivateMessageCount"))) > 1 %>'>s</ccp:evaluate></a>
              </ccp:evaluate>
              <ccp:evaluate if='<%= (Integer.parseInt((String)request.getAttribute("requestInvitationCount"))) > 0 %>'>
                <a href="${ctx}/show/<%= User.getProfileProject().getUniqueId() %>">You have <%= (String) request.getAttribute("requestInvitationCount") %> new invitation<ccp:evaluate
                    if='<%= (Integer.parseInt((String)request.getAttribute("requestInvitationCount"))) > 1 %>'>s</ccp:evaluate></a>
              </ccp:evaluate>
            </div>
          </ccp:evaluate>
          <ccp:evaluate if="<%= menuCategoryList.size() > 1 %>">
            <div class="ccp-search-form">
              <form action="${ctx}/search" method="get">
                <fieldset>
                  <legend>Search <%= toHtml(pageTitle) %></legend>
                  <%--
                  <ccp:evaluate if="<%= menuCategoryList.size() > 1 %>">
                    <label for="categoryId">Search</label>
                    <%= menuCategoryList.getHtml("categoryId", searchBean.getCategoryId()) %>
                  </ccp:evaluate>
                  --%>
                  <label for="query">Search for</label>
                  <input type="text" size="20" name="query" value="<%= toHtmlValue(searchBean.getQuery()) %>"/>
                  <ccp:evaluate if="<%= useLocations %>">
                    <label for="location">near</label>
                    <input type="text" size="20" name="location" value="<%= toHtmlValue(searchBean.getLocation()) %>" />
                  </ccp:evaluate>
                  <input type="submit" alt="Search" value="Go" />
                  <input type="hidden" name="categoryId" value="-1"/>
                  <input type="hidden" name="type" value="all"/>
                  <input type="hidden" name="scope" value="<%= searchBean.getScopeText() %>"/>
                  <input type="hidden" name="filter" value="<%= searchBean.getFilter() %>"/>
                  <input type="hidden" name="projectId" value="<%= searchProjectId %>"/>
                  <input type="hidden" name="openProjectsOnly" value="true"/>
                  <input type="hidden" name="auto-populate" value="true"/>
                </fieldset>
              </form>
            </div>
          </ccp:evaluate>
        </div>
        <div class="ccp-navigation">
          <ul>
            <ccp:tabbedMenu text='<%= "Home" %>' key="home" value="${tabbedMenuValue}" url="${ctx}/" type="li" object="requestMainProfile"/>
            <ccp:evaluate if="<%= isUserLoggedIn && User.getProfileProjectId() > -1 %>">
              <ccp:tabbedMenu text='<%= "My Page" %>' key="me" value="${tabbedMenuValue}" url="${ctx}/show/${userProfile.uniqueId}" type="li" object="requestMainProfile"/>
            </ccp:evaluate>
            <c:forEach items="${tabCategoryList}" var="tabCategory" varStatus="status">
              <ccp:tabbedMenu text="${tabCategory.label}"
                                 key="${fn:toLowerCase(fn:replace(tabCategory.description,' ','_'))}"
                                 value="${tabbedMenuValue}"
                                 url='${ctx}/${fn:toLowerCase(fn:replace(tabCategory.description," ","_"))}.shtml'
                                 type="li"
                                 object="requestMainProfile"/>
            </c:forEach>
          </ul>
        </div>
      </div><%-- End ccp-header --%>
      <div class="ccp-body">
        <p class="access"><a name="content">Main Content</a></p>
        <%-- Any system alerts? --%>
        <c:if test="${!empty requestGlobalAlerts}">
          <div class="portlet-message-error">
            <ul>
              <c:forEach items="${requestGlobalAlerts}" var="alert" varStatus="status">
                <li>${alert}</li>
              </c:forEach>
            </ul>
          </div>
        </c:if>
        <%-- Render the page --%>
        <jsp:include page="<%= PageBody %>" flush="true"/>
      </div>
      <div class="ccp-footer">
        <ul>
          <c:if test="${fn:length(tabCategoryList) > 0}">
            <ccp:tabbedMenu text='<%= "Home" %>' key="nokey" value="novalue" url="${ctx}/" type="li" object="requestMainProfile"/>
          </c:if>
          <ccp:evaluate if="<%= isUserLoggedIn && User.getProfileProjectId() > -1 %>">
            <ccp:tabbedMenu text='<%= "My Page" %>' key="nokey" value="novalue" url="${ctx}/show/${userProfile.uniqueId}" type="li" object="requestMainProfile"/>
          </ccp:evaluate>
          <c:forEach items="${tabCategoryList}" var="tabCategory" varStatus="status">
            <ccp:tabbedMenu text="${tabCategory.label}"
                            key="nokey"
                            value="novalue"
                            url='${ctx}/${fn:toLowerCase(fn:replace(tabCategory.description," ","_"))}.shtml'
                            type="li"
                            object="requestMainProfile"/>
          </c:forEach>
          <ccp:permission object="requestMainProfile" name="project-wiki-view">
            <li>
              <a href="${ctx}/rss" title="RSS Feeds"><em>RSS</em></a>
            </li>
            <li class="last">
              <a href="${ctx}/show/main-profile/wiki/Site+Guidelines" rel="nofollow" title="Site Guildlines"><em>Guidelines</em></a>
            </li>
          </ccp:permission>
        </ul>
        <ul class="site-menu">
          <c:set var="isFirst">class="first"</c:set>
          <ccp:permission object="requestMainProfile" name="project-wiki-view">
            <li ${isFirst}><a href="${ctx}/show/main-profile" title="Get information about <%= toHtml(applicationPrefs.get("TITLE")) %>">About Us</a></li>
            <c:set var="isFirst" value=""/>
          </ccp:permission>
          <li ${isFirst}><a href="${ctx}/contact-us" title="Contact <%= toHtml(applicationPrefs.get("TITLE")) %>">Contact Us</a></li>
          <c:set var="isFirst" value=""/>
          <ccp:evaluate if='<%= isUserLoggedIn || !"true".equals(applicationPrefs.get("SENSITIVE_INFORMATION"))%>'>
            <li ${isFirst}><a href="${ctx}/page/tags/all" title="<%= toHtml(applicationPrefs.get("TITLE")) %> Tags">Tags</a></li>
            <li ${isFirst}><a href="${ctx}/page/classifieds/all" title="<%= toHtml(applicationPrefs.get("TITLE")) %> Classifieds">Classifieds</a></li>
            <c:set var="isFirst">class="last"</c:set>
            <li ${isFirst}><a href="${ctx}/page/promotions/all" title="<%= toHtml(applicationPrefs.get("TITLE")) %> Promotions">Promotions</a></li>
            <c:set var="isFirst" value=""/>
          </ccp:evaluate>
        </ul>
          <p>Powered by <a title="The Open Source Community and Collaboration Solution" href="http://www.concursive.com/show/concourseconnect" target="_blank">Concursive ConcourseConnect</a>,
            <a title="The Open Source Business Social Software Platform" href="http://www.concursive.com" target="_blank">The Open Source Community and Collaboration Solution</a></p>
      </div>
    </div>
    <%-- Allow pages to have a scrollTo... must be at end of html --%>
    <ccp:evaluate if="<%= doScrollTop %>">
      <script type="text/javascript">
        if (window.scrollTo) window.scrollTo(<%= StringUtils.jsEscape(request.getParameter("scrollLeft")) %>, <%= StringUtils.jsEscape(request.getParameter("scrollTop")) %>);
      </script>
    </ccp:evaluate>
    <div class="yui-skin-sam">
      <div id="popupCalendar"></div>
    </div>
    <div class="yui-skin-sam">
      <div id="popupLayer"></div>
    </div>
    <%-- Google Analytics Tracker --%>
    <c:if test='<%= applicationPrefs.has("GOOGLE_ANALYTICS.ID") %>'>
      <script type="text/javascript">
        var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
        document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
      </script>
      <script type="text/javascript">
        try {
        var pageTracker = _gat._getTracker("<%= applicationPrefs.get("GOOGLE_ANALYTICS.ID") %>");
        pageTracker._trackPageview();
        } catch(err) {}
      </script>
    </c:if>
  </body>
</html>
