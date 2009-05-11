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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="dashboardList" class="com.concursive.connect.cms.portal.dao.DashboardList" scope="request"/>
<jsp:useBean id="templates" class="com.concursive.connect.cms.portal.dao.DashboardTemplateList" scope="request"/>
<%@ include file="initPage.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<br />
<%-- Show the dashboard tabs --%>
<ccp:evaluate if="<%= dashboardList.size() > 0 %>">
  <div class="yui-skin-sam">
    <div class="yui-navset" id="projects-center-tabs">
      <ul class="yui-nav">
        <c:forEach items="${dashboardList}" var="thisDashboard">
          <ccp:tabbedMenu text="${thisDashboard.name}" key="${thisDashboard.id}" value="${dashboard.id}" url="${ctx}/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=${project.id}&dash=${thisDashboard.id}" type="li"/>
        </c:forEach>
      </ul>
    </div>
  </div>
  <br />
</ccp:evaluate>
Available templates with portlets to import...<br />
<br />
<c:forEach items="${templates}" var="thisTemplate">
  <a href="<%= ctx %>/ProjectManagementDashboard.do?command=ImportTemplate&pid=<%= project.getId() %>&template=<c:out value="${thisTemplate.id}" />"><c:out value="${thisTemplate.name}" /></a>
  <br />
</c:forEach>
