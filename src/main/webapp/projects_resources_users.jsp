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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page
    import="com.concursive.connect.web.modules.plans.dao.AssignmentUserAllocation" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="allocationList" class="com.concursive.connect.web.modules.plans.dao.AssignmentAllocationList" scope="request"/>
<%@ include file="initPage.jsp" %>
<img src="<%= ctx %>/images/icons/stock_resources-16.gif" align="absmiddle" alt="" border="0" />
<ccp:label name="projectsResources.users.users">Users</ccp:label><br />
<ccp:tz timestamp="<%= allocationList.getStartDate() %>" dateOnly="true"/> -
<ccp:tz timestamp="<%= allocationList.getEndDate() %>" dateOnly="true"/><br />
<br />
<ccp:evaluate if="<%= allocationList.getUserList().size() == 0 %>">
  <ccp:label name="projectsResources.users.noUsers">No users to display for the selected date range.</ccp:label>
</ccp:evaluate>
<ccp:evaluate if="<%= allocationList.getUserList().size() > 0 %>">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td nowrap valign="top">
<%-- names --%>
<table cellpadding="2" cellspacing="0" border="0" width="100%" class="resource">
  <%-- months --%>
  <tr>
    <th>&nbsp;</th>
  </tr>
  <%-- days --%>
  <tr>
    <th>&nbsp;</th>
  </tr>
  <%-- users --%>
<%
    Iterator users = allocationList.getUserList().keySet().iterator();
    while (users.hasNext()) {
      Integer userId = (Integer) users.next();
%>
  <tr class="resourceGroup">
    <td nowrap><b><ccp:username id="<%= userId.intValue() %>"/></b></td>
  </tr>
  <%-- Projects --%>
<%
    AssignmentUserAllocation userMap = (AssignmentUserAllocation) allocationList.getUser(userId);
    Iterator projects = userMap.keySet().iterator();
    while (projects.hasNext()) {
      Integer projectId = (Integer) projects.next();
%>
  <tr class="resourceItem">
    <td nowrap>&nbsp;<a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Requirements&pid=<%= projectId.intValue() %>"><ccp:project id="<%= projectId.intValue() %>"/></a></td>
  </tr>
<%
    }
  }
%>
</table>
<%-- end names --%>
</td>
<td valign="top" width="100%">
<iframe id="viewId" name="viewId"
        style="overflow: hidden;" border="0" frameborder="0"
        marginheight="0" marginwidth="0"
        width="100%" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Resources.do?command=UsersProjects&popup=true">
<ccp:label name="projectsResources.users.noViewInBrowser">View not supported in this browser.</ccp:label>(
</iframe>
</td>
</tr>
</table>
</ccp:evaluate>
