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
<%@ page import="com.concursive.connect.web.modules.reports.dao.Parameter" %>
<jsp:useBean id="report" class="com.concursive.connect.web.modules.reports.dao.Report" scope="request"/>
<jsp:useBean id="parameterList" class="com.concursive.connect.web.modules.reports.dao.ParameterList" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="javascript" type="text/javascript">
  function activateReport() {
    window.location.href='<%= ctx %>/AdminReports.do?command=Activate&reportId=<%= report.getId() %>';
  }
  function disableReport() {
    window.location.href='<%= ctx %>/AdminReports.do?command=Disable&reportId=<%= report.getId() %>';
  }
  function deleteReport() {
    confirmDelete('<%= ctx %>/AdminReports.do?command=Delete&reportId=<%= report.getId() %>');
  }
</script>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminApplication.do">Manage Application Settings</a> >
<a href="<%= ctx %>/AdminReports.do?command=List">Reports</a> >
Details<br />
<br />
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Settings
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td class="formLabel">
        Type
      </td>
      <td>
        <ccp:evaluate if="<%= report.getAdminReport() %>">
          Report can be executed by Admin users only
          (<a href="<%= ctx %>/AdminReports.do?command=SetAsUser&reportId=<%= report.getId() %>">toggle</a>)
        </ccp:evaluate>
        <ccp:evaluate if="<%= report.getUserReport() %>">
          Report can be executed by any User
          (<a href="<%= ctx %>/AdminReports.do?command=SetAsAdmin&reportId=<%= report.getId() %>">toggle</a>)
        </ccp:evaluate>
      </td>
    </tr>
  </tbody>
</table>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Parameters
      </th>
    </tr>
  </thead>
  <tbody>
    <%
      Iterator i = parameterList.iterator();
      while (i.hasNext()) {
        Parameter thisParameter = (Parameter) i.next();
        if (thisParameter.getIsForPrompting()) {
    %>
      <tr class="containerBody">
        <td class="formLabel">
          <%= toHtml(thisParameter.getDisplayName()) %>
        </td>
        <td>
          <%= thisParameter.getHtml(request) %>
        </td>
      </tr>
    <%
        }
      }
    %>
  </tbody>
</table>
<br />
<ccp:evaluate if="<%= !report.getEnabled() %>">
  <input type="button" name="Activate" value="Activate" onclick="activateReport();" />
</ccp:evaluate>
<ccp:evaluate if="<%= report.getEnabled() %>">
  <input type="button" name="Disable" value="Disable" onclick="disableReport();" />
</ccp:evaluate>
<ccp:evaluate if="<%= report.getCustom() %>">
  <input type="button" name="Delete" value="Delete" onclick="deleteReport();" />
</ccp:evaluate>
