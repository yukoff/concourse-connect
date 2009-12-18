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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.config.ApplicationVersion" %>
<%@ page import="com.concursive.connect.config.ApplicationPrefs" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://packtag.sf.net" prefix="pack" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="templateTheme" class="java.lang.String" scope="application"/>
<jsp:useBean id="templateColorScheme" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="initPage.jsp" %>
<%
  if (clientType.getType() == -1) {
    clientType.setParameters(request);
  }
  Locale locale = User.getLocale();
  if (locale == null) {
    locale = new Locale(clientType.getLanguage());
  }
  String pattern = ((SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT, locale)).toLocalizedPattern();
  String patternDel = "/";
  if (pattern.contains(".")) { patternDel = "."; }
  if (pattern.contains("-")) { patternDel = "-"; }
%>
<pack:style>
  <src>/css/carousel.css</src>
  <src>/css/ccp-typography.css</src>
  <src>/css/ccp-layout.css</src>
  <src>/css/ccp-portlets.css</src>
  <src>/css/ccp-profile.css</src>
  <src>/css/star_rating.css</src>
  <src>/css/bucketlist.css</src>
  <src>/javascript/yui-2.8r4/build/calendar/assets/skins/sam/calendar.css</src>
  <src>/javascript/yui-2.8r4/build/button/assets/skins/sam/button.css</src>
  <src>/javascript/yui-2.8r4/build/menu/assets/skins/sam/menu.css</src>
  <src>/javascript/yui-2.8r4/build/container/assets/skins/sam/container.css</src>
  <src>/javascript/yui-2.8r4/build/container/assets/container.css</src>
</pack:style>
<c:if test="${!empty templateColorScheme}">
  <link rel="stylesheet" href="${ctx}/themes/${templateTheme}/color-schemes/${templateColorScheme}/css/ccp-color.css?3" />
</c:if>
<%-- RSS Feeds --%>
<ccp:evaluate if='<%= !"intranet".equals(applicationPrefs.get("PURPOSE")) || User.isLoggedIn() || !"true".equals(applicationPrefs.get(ApplicationPrefs.INFORMATION_IS_SENSITIVE)) %>'>
  <link rel="alternate" type="application/rss+xml" title="<c:out value="${requestMainProfile.title}"/>" href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/feed/rss.xml"/>
</ccp:evaluate>
<c:forEach items="${tabCategoryList}" var="tabCategory" varStatus="status">
  <link rel="alternate" type="application/rss+xml" title="<c:out value="${requestMainProfile.title}"/> - <c:out value="${tabCategory.description}"/>" href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/feed/${fn:toLowerCase(fn:replace(tabCategory.description," ","_"))}/rss.xml"/>
</c:forEach>
<ccp:evaluate if="<%= User.isLoggedIn() %>">
  <link rel="alternate" type="application/rss+xml"
    title="<c:out value="${requestMainProfile.title}"/> - Blog Rollup (Login Required)"
    href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/feed/blog.xml" />
  <link rel="alternate" type="application/rss+xml"
    title="<c:out value="${requestMainProfile.title}"/> - Discussion Rollup (Login Required)"
    href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/feed/discussion.xml" />
  <link rel="alternate" type="application/rss+xml"
    title="<c:out value="${requestMainProfile.title}"/> - Documents Rollup (Login Required)"
    href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/feed/documents.xml" />
  <link rel="alternate" type="application/rss+xml"
    title="<c:out value="${requestMainProfile.title}"/> - Wiki Rollup (Login Required)"
    href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/feed/wiki.xml" />
</ccp:evaluate>
<%-- Global Javascript --%>
<script type="text/javascript">
  var teamelements_ctx = '<%= ctx %>';
  var connect_startDayOfWeek = <%= Calendar.getInstance(locale).getFirstDayOfWeek() - 1 %>;
  var connect_datePattern = '<%= pattern %>';
  var connect_dateFieldDelimiter = '<%= patternDel %>';
  var connect_dateDayPosition = '<%= pattern.indexOf("d") < pattern.indexOf("M") ? 1 : 2 %>';
  var connect_dateMonthPosition = '<%= pattern.indexOf("d") < pattern.indexOf("M") ? 2 : 1 %>';
  var connect_dateYearPosition = '3';
</script>
<pack:script>
  <src>/javascript/ajax.js</src>
  <src>/javascript/popURL.js</src>
  <src>/javascript/confirmDelete.js</src>
  <src>/javascript/spanDisplay.js</src>
  <src>/javascript/div.js</src>
  <src>/javascript/swapClass.js</src>
  <src>/javascript/checkDate.js</src>
  <src>/javascript/checkEmail.js</src>
  <src>/javascript/checkPhone.js</src>
  <src>/javascript/checkCreditCardNumber.js</src>
  <src>/javascript/popCalendar.js</src>
  <src>/javascript/images.js</src>
  <src>/javascript/popWiki.js</src>
  <src>/javascript/scrollReload.js</src>
  <src>/javascript/checkCheckbox.js</src>
  <src>/javascript/captcha.js</src>
  <src>/javascript/AC_RunActiveContent.js</src>
  <src>/javascript/yui-2.8r4/build/yahoo/yahoo.js</src>
  <src>/javascript/yui-2.8r4/build/event/event.js</src>
  <src>/javascript/yui-2.8r4/build/dom/dom.js</src>
  <src>/javascript/yui-2.8r4/build/element/element.js</src>
  <src>/javascript/yui-2.8r4/build/connection/connection.js</src>
  <src>/javascript/yui-2.8r4/build/animation/animation.js</src>
  <src>/javascript/yui-2.8r4/build/dragdrop/dragdrop.js</src>
  <src>/javascript/yui-2.8r4/build/calendar/calendar-min.js</src>
  <src>/javascript/yui-2.8r4/build/container/container-min.js</src>
  <src>/javascript/yui-2.8r4/build/datasource/datasource-min.js</src>
  <src>/javascript/yui-2.8r4/build/json/json-min.js</src>
  <src>/javascript/yui-2.8r4/build/menu/menu-min.js</src>
  <src>/javascript/yui-2.8r4/build/button/button-min.js</src>
  <src>/javascript/panel.js</src>
  <src>/javascript/carousel_min.js</src>
  <src>/javascript/trackMouse.js</src>
  <src>/javascript/tags_form_add.js</src>
</pack:script>
<%--
<script src="<%= yuiURL %>/datatable/datatable-beta-min.js" type="text/javascript"></script>
<script src="<%= yuiURL %>/charts/charts-experimental-min.js" type="text/javascript"></script>
<script type="text/javascript">
  YAHOO.widget.Chart.SWFURL = "<%= yuiURL %>/charts/assets/charts.swf";
</script>
<script type="text/javascript" src="<%= yuiURL %>/tabview/tabview-min.js"></script>
<script type="text/javascript" src="<%= yuiURL %>/history/history-min.js"></script>
<script type="text/javascript" src="<%= yuiURL %>/selector/selector-beta-min.js"></script>
<script type="text/javascript" src="<%= yuiURL %>/autocomplete/autocomplete-min.js"></script>
<script type="text/javascript" src="<%= yuiURL %>/logger/logger-min.js"></script>
--%>
<!--[if lt IE 7]>
  <script type="text/javascript">var clear="${ctx}/images/clear.gif"</script>
  <script type="text/javascript" src="${ctx}/javascript/unitpngfix.js"></script>
<![endif]-->
<%
  if(request.getAttribute("project") == null) {
    pageContext.setAttribute("project",request.getAttribute("Project"));
  }
%>
<ccp:permission name="project-setup-style">
  <c:set var="canOverrideStyle" value="true"/>
</ccp:permission>
<c:if test="${project.id > -1 && !project.portal}">
 <c:choose>
  <c:when test="${canOverrideStyle == 'true' && !empty param.style}">
   <style type="text/css">
    ${param.style}
   </style>
  </c:when>
  <c:when test="${project.styleEnabled && !empty project.style && param.popup ne 'true'}">
   <style type="text/css">
    ${project.style}
   </style>
  </c:when> 
  <c:when test="${!project.styleEnabled && project.category.styleEnabled && !empty project.category.style && param.popup ne 'true'}">
   <style type="text/css">
    ${project.category.style}
   </style>
  </c:when>
 </c:choose>
</c:if>
