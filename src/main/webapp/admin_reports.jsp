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
<%@ page import="com.concursive.connect.web.modules.reports.dao.Report" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="activeReportList" class="com.concursive.connect.web.modules.reports.dao.ReportList" scope="request"/>
<jsp:useBean id="disabledReportList" class="com.concursive.connect.web.modules.reports.dao.ReportList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="admin_reports_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminApplication.do">Manage Application Settings</a> >
Reports<br />
<br />
<table class="note" cellspacing="0">
<tr>
  <th>
    <img src="<%= ctx %>/images/icons/stock_form-open-in-design-mode-16.gif" border="0" align="absmiddle" />
  </th>
  <td>
    Available reports can be executed by users that have access to run reports.<br />
    Inactive reports are either uploaded reports that have not been verified for use, or may be
    subreports that are used by other reports.  Inactive reports cannot be executed directly by users.
  </td>
</tr>
</table>
<a href="<%= ctx %>/AdminReports.do?command=Add">Upload Report</a><br />
<br />
<br />
<table class="pagedList">
  <caption><b>Available Master Reports</b></caption>
  <thead>
    <tr>
      <th>
        Action
      </th>
      <th>
        Title
      </th>
      <th>
        Filename
      </th>
      <th>
        Custom?
      </th>
      <th>
        User?
      </th>
      <th>
        Admin?
      </th>
    </tr>
  </thead>
  <tbody>
    <ccp:evaluate if="<%= activeReportList.size() == 0 %>">
      <tr class="row2">
        <td colspan="6">
          There are no available reports.
        </td>
      </tr>
    </ccp:evaluate>
    <%
      int row = 0;
      Iterator i = activeReportList.iterator();
      while (i.hasNext()) {
        Report thisReport = (Report) i.next();
        row = row != 1 ? 1 : 2;
    %>
      <tr class="row<%= row %>">
        <td valign="top" align="center" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= thisReport.getId() %>','menuItem',<%= thisReport.getId() %>,'<%= thisReport.getEnabled() ? "true" : "false" %>','<%= thisReport.getCustom() ? "true" : "false" %>');"
             onMouseOver="over(0, <%= thisReport.getId() %>)"
             onmouseout="out(0, <%= thisReport.getId() %>); hideMenu('menuItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= thisReport.getId() %>" id="select_<%= SKIN %><%= thisReport.getId() %>" align="absmiddle" border="0"></a>
        </td>
        <td width="45%">
          <a href="<%= ctx %>/AdminReports.do?command=Details&reportId=<%= thisReport.getId() %>"><%= toHtml(thisReport.getTitle()) %></a>
        </td>
        <td width="40%">
          <%= toHtml(thisReport.getFilename()) %>
        </td>
        <td width="5%">
          <%= thisReport.getCustom() ? "Yes" : "&nbsp;" %>
        </td>
        <td width="5%">
          <%= thisReport.getUserReport() ? "Yes" : "&nbsp;" %>
        </td>
        <td width="5%">
          <%= thisReport.getAdminReport() ? "Yes" : "&nbsp;" %>
        </td>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
<table class="pagedList">
  <caption><b>Subreports and Inactive Reports</b></caption>
  <thead>
    <tr>
      <th>
        Action
      </th>
      <th>
        Title
      </th>
      <th>
        Filename
      </th>
      <th>
        Custom?
      </th>
      <th>
        User?
      </th>
      <th>
        Admin?
      </th>
    </tr>
  </thead>
  <tbody>
    <ccp:evaluate if="<%= disabledReportList.size() == 0 %>">
      <tr class="row2">
        <td colspan="6">
          There are no inactive or subreports.
        </td>
      </tr>
    </ccp:evaluate>
    <%
      row = 0;
      Iterator i2 = disabledReportList.iterator();
      while (i2.hasNext()) {
        Report thisReport = (Report) i2.next();
        row = row != 1 ? 1 : 2;
    %>
      <tr class="row<%= row %>">
        <td valign="top" align="center" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= thisReport.getId() %>','menuItem',<%= thisReport.getId() %>,'<%= thisReport.getEnabled() ? "true" : "false" %>','<%= thisReport.getCustom() ? "true" : "false" %>');"
             onMouseOver="over(0, <%= thisReport.getId() %>)"
             onmouseout="out(0, <%= thisReport.getId() %>); hideMenu('menuItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= thisReport.getId() %>" id="select_<%= SKIN %><%= thisReport.getId() %>" align="absmiddle" border="0"></a>
        </td>
        <td width="45%">
          <a href="<%= ctx %>/AdminReports.do?command=Details&reportId=<%= thisReport.getId() %>"><%= toHtml(thisReport.getTitle()) %></a>
        </td>
        <td width="40%">
          <%= toHtml(thisReport.getFilename()) %>
        </td>
        <td width="5%">
          <%= thisReport.getCustom() ? "Yes" : "&nbsp;" %>
        </td>
        <td width="5%">
          <%= thisReport.getUserReport() ? "Yes" : "&nbsp;" %>
        </td>
        <td width="5%">
          <%= thisReport.getAdminReport() ? "Yes" : "&nbsp;" %>
        </td>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
