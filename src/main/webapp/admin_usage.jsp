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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.commons.db.ConnectionPool,org.quartz.Scheduler" %>
<jsp:useBean id="ConnectionPool" class="com.concursive.commons.db.ConnectionPool" scope="application"/>
<jsp:useBean id="ConnectionPoolRSS" class="com.concursive.commons.db.ConnectionPool" scope="application"/>
<jsp:useBean id="ConnectionPoolAPI" class="com.concursive.commons.db.ConnectionPool" scope="application"/>
<jsp:useBean id="ObjectHookManager" class="com.concursive.commons.workflow.ObjectHookManager" scope="application"/>
<jsp:useBean id="applicationVersion" class="java.lang.String" scope="request"/>
<jsp:useBean id="diskFree" class="java.lang.String" scope="request"/>
<jsp:useBean id="diskPath" class="java.lang.String" scope="request"/>
<jsp:useBean id="fileSize" class="java.lang.String" scope="request"/>
<jsp:useBean id="projectCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="userCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="projectIndex" class="java.lang.String" scope="request"/>
<jsp:useBean id="servicesId" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  ConnectionPool connectionPoolScheduler = null;
  try {
    Scheduler scheduler = (Scheduler) pageContext.getServletContext().getAttribute("Scheduler");
    connectionPoolScheduler = ((ConnectionPool) scheduler.getContext().get("ConnectionPool"));
  } catch (Exception e) {

  }
%>
<div class="adminContainer">
  <div class="adminHeader">
    <h1>Review Application Usage</h1>
    <p>Back to <a href="<%= ctx %>/admin" title="Back to System Administration">System Administration</a></p>
  </div>
  <div class="adminBodyContainer">
    <div class="adminUsageContainer">
      <div class="installationInformationContainer">
        <table class="pagedList">
          <thead>
            <tr>
              <th colspan="2">
                Installation Information
              </th>
            </tr>
          </thead>
          <tbody>
            <tr class="containerBody">
              <td class="formLabel">Version</td>
              <td>
                <%= applicationVersion %>
              </td>
            </tr>
            <c:if test="${!empty serviceId}">
              <tr class="containerBody">
                <td nowrap class="formLabel">Services Id</td>
                <td>
                  <c:out value="CONNECT-${servicesId}"/>
                </td>
              </tr>
            </c:if>
          </tbody>
        </table>
      </div>
      <div class="currentSystemStatisticsContainer">
        <table class="pagedList">
          <thead>
            <tr>
              <th colspan="2">
                Current System Statistics
              </th>
            </tr>
          </thead>
          <tbody>
            <tr class="containerBody">
              <td nowrap class="formLabel">Up Since</td>
              <td>
                <%= ConnectionPool.getStartDate() %>
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Database Connection Pools</td>
              <td>
                Web-Tier:
                <%= ConnectionPool.toString() %>
                <%= (ConnectionPool.getMaxStatus() ? "<font color=\"red\">All connections are full!</font>":"") %><br />
                <%-- TODO: Cache ConnectionPool --%>
                <%-- Scheduler ConnectionPool --%>
                <ccp:evaluate if="<%= connectionPoolScheduler != null %>">
                  Scheduler:
                  <%= connectionPoolScheduler.toString() %>
                  <%= connectionPoolScheduler.getMaxStatus() ? "<font color=\"red\">All connections are full!</font>":"" %><br />
                </ccp:evaluate>
                <%-- Workflow ConnectionPool --%>
                Workflow:
                <%= ObjectHookManager.getConnectionPool().toString() %>
                <%= (ObjectHookManager.getConnectionPool().getMaxStatus() ? "<font color=\"red\">All connections are full!</font>":"") %><br />
                <%-- RSS ConnectionPool --%>
                RSS Feeds:
                <%= ConnectionPoolRSS.toString() %>
                <%= (ConnectionPoolRSS.getMaxStatus() ? "<font color=\"red\">All connections are full!</font>":"") %><br />
                API:
                <%= ConnectionPoolAPI.toString() %>
                <%= (ConnectionPoolAPI.getMaxStatus() ? "<font color=\"red\">All connections are full!</font>":"") %><br />
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Lucene RAM Index</td>
              <td>
                Ram Index: <%= toHtml(projectIndex) %>
                [<a href='javascript:showPanel("Project%20Reindex","<%= ctx %>/Search.do?command=IndexProjects",400)'>Reindex</a>]
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Lucene Disk Index</td>
              <td>
                Disk Index
                [<a href="javascript:popURL('<%= ctx %>/reindex.html','Indexing','500','325','yes','yes');">Reindex</a>]
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Rules Engine</td>
              <td>
                Rules: <%= ObjectHookManager.getProcessList().size() %>; Hooks: <%= ObjectHookManager.getHookList().size() %>
                [<a href="<%= ctx %>/AdminUsage.do?command=ReloadWorkflows">Reload</a>]
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Storage Path</td>
              <td>
                <%= toHtml(diskPath) %>
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Free Disk Space</td>
              <td>
                <%= toHtml(diskFree) %>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="applicationStatisticsContainer">
        <table class="pagedList">
          <thead>
            <tr>
              <th colspan="2">
                Application Statistics
              </th>
            </tr>
          </thead>
          <tbody>
            <tr class="containerBody">
              <td nowrap class="formLabel">Total Profiles</td>
              <td>
                <%= toHtml(projectCount) %>
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Total Users</td>
              <td>
                <%= toHtml(userCount) %>
              </td>
            </tr>
            <tr class="containerBody">
              <td nowrap class="formLabel">Document Size</td>
              <td>
                <%= toHtml(fileSize) %>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
