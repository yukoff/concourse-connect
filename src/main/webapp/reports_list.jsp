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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.reports.dao.ReportQueue" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="queueList" class="com.concursive.connect.web.modules.reports.dao.ReportQueueList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<script language="JavaScript" type="text/javascript" src="${ctx}/javascript/preloadImages.js"></script>
<script language="JavaScript" type="text/javascript">
  var base = "${ctx}/images/";
  loadImages('select_<%= SKIN %>');
</script>
<%@ include file="reports_list_menu.jspf" %>
<script language="JavaScript" type="text/javascript">
  function view(thisQueueId) {
    popURL('Reports.do?command=View&queueId=' + thisQueueId,'750','575','yes','yes');
  }
</script>
<%-- Temp. fix for Weblogic --%>
<%= showError(request, "actionError", false) %>
<img src="<%= ctx %>/images/icons/stock_form-16.gif" align="absmiddle" alt="" border="0"/>
Reports<br />
<br />
<a href="<%= ctx %>/Reports.do?command=Setup">Setup a Report</a><br />
<br />
<ccp:evaluate if="<%= queueList.size() == 0 %>">
  <table class="pagedList">
    <thead>
      <tr>
        <th>
          Report Queue
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="row2">
        <td>Setup a report to see it in your list</td>
      </tr>
    </tbody>
  </table>
</ccp:evaluate>
<%
  boolean start = true;
  int lastProjectId = -1;
  int row = 0;
  int count = 0;
  Iterator i = queueList.iterator();
  while (i.hasNext()) {
    ReportQueue thisReport = (ReportQueue) i.next();
    row = row != 1 ? 1 : 2;
    ++count;
  if (start) {
    lastProjectId = thisReport.getProjectId();
    start = false;
  }
%>
<ccp:evaluate if="<%= lastProjectId != thisReport.getProjectId() %>">
  </table>
  <br />
<%
  count = 1;
  lastProjectId = thisReport.getProjectId();
%>
</ccp:evaluate>
<ccp:evaluate if="<%= count == 1 && thisReport.getProjectId() != -1 %>">
  <ccp:project id="<%= thisReport.getProjectId() %>"/>
  (<a href="javascript:confirmDelete('<%= ctx %>/Reports.do?command=DeleteAll&projectId=<%= thisReport.getProjectId() %>');">delete all</a>)
</ccp:evaluate>
<ccp:evaluate if="<%= count == 1 %>">
<table class="pagedList">
    <tr>
      <th width="8" align="center" nowrap>Action</th>
      <th width="100%">
        Title
      </th>
      <th width="75" nowrap colspan="8">
        Schedule
      </th>
      <th width="100" nowrap>
        Status
      </th>
      <th width="50" align="center" nowrap>
        Size
      </th>
      <th nowrap>
        Format
      </th>
      <th width="130" nowrap>
        Last Run
      </th>
    </tr>
  </ccp:evaluate>
    <tr class="row<%= row %>">
      <td valign="top" align="center" nowrap>
        <a href="javascript:displayMenu('select_<%= SKIN %><%= thisReport.getId() %>','menuItem',<%= thisReport.getId() %>,'<%= thisReport.getFilename() != null %>');"
           onMouseOver="over(0, <%= thisReport.getId() %>)"
           onmouseout="out(0, <%= thisReport.getId() %>);"><img
           src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= thisReport.getId() %>" id="select_<%= SKIN %><%= thisReport.getId() %>" align="absmiddle" border="0"></a>
      </td>
      <td valign="top" nowrap>
        <a href="javascript:view(<%= thisReport.getId() %>);"><%= toHtml(thisReport.getReport().getTitle()) %></a>
      </td>
      <td valign="top">
        <ccp:evaluate if="<%= thisReport.getScheduleMonday() %>">M</ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.getScheduleMonday() %>">&nbsp;</ccp:evaluate>
      </td>
      <td valign="top">
        <ccp:evaluate if="<%= thisReport.getScheduleTuesday() %>">T</ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.getScheduleTuesday() %>">&nbsp;</ccp:evaluate>
      </td>
      <td valign="top">
        <ccp:evaluate if="<%= thisReport.getScheduleWednesday() %>">W</ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.getScheduleWednesday() %>">&nbsp;</ccp:evaluate>
      </td>
      <td valign="top">
        <ccp:evaluate if="<%= thisReport.getScheduleThursday() %>">T</ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.getScheduleThursday() %>">&nbsp;</ccp:evaluate>
      </td>
      <td valign="top">
        <ccp:evaluate if="<%= thisReport.getScheduleFriday() %>">F</ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.getScheduleFriday() %>">&nbsp;</ccp:evaluate>
      </td>
      <td valign="top">
        <ccp:evaluate if="<%= thisReport.getScheduleSaturday() %>">S</ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.getScheduleSaturday() %>">&nbsp;</ccp:evaluate>
      </td>
      <td valign="top">
        <ccp:evaluate if="<%= thisReport.getScheduleSunday() %>">S</ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.getScheduleSunday() %>">&nbsp;</ccp:evaluate>
      </td>
      <td valign="top" nowrap>
        <ccp:evaluate if="<%= thisReport.hasSchedule() %>">
          <ccp:tz timestamp="<%= thisReport.getScheduleTime() %>" timeOnly="true" default="&nbsp;" />
        </ccp:evaluate>
        <ccp:evaluate if="<%= !thisReport.hasSchedule() %>">
          &nbsp;
        </ccp:evaluate>
      </td>
      <td valign="top" width="30%" nowrap>
        <ccp:evaluate if="<%= thisReport.getStatus() == ReportQueue.STATUS_PROCESSED %>">Ready</ccp:evaluate>
        <ccp:evaluate if="<%= thisReport.getStatus() == ReportQueue.STATUS_PROCESSING %>"><img src="<%= ctx %>/images/icons/gear.gif" align="absMiddle" border="0" /> Processing</ccp:evaluate>
        <ccp:evaluate if="<%= thisReport.getStatus() == ReportQueue.STATUS_QUEUED %>">Queued</ccp:evaluate>
        <ccp:evaluate if="<%= thisReport.getStatus() == ReportQueue.STATUS_ERROR %>">Error</ccp:evaluate>
        <ccp:evaluate if="<%= thisReport.getStatus() == ReportQueue.STATUS_DISABLED %>">Report Disabled</ccp:evaluate>
        <ccp:evaluate if="<%= thisReport.getStatus() == ReportQueue.STATUS_SCHEDULED %>">Scheduled
          <ccp:tz timestamp="<%= thisReport.getScheduleTime() %>" default="&nbsp;" />
        </ccp:evaluate>
      </td>
      <td valign="top" align="right" nowrap>
        <ccp:evaluate if="<%= thisReport.getSize() == -1 %>">
          --
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisReport.getSize() != -1 %>">
          <%= thisReport.getRelativeSize() %>k&nbsp;
        </ccp:evaluate>
      </td>
      <td align="right">
        <%= thisReport.getOutput() %>
      </td>
      <td valign="top" nowrap>
        <ccp:tz timestamp="<%= thisReport.getProcessed() %>" default="&nbsp;" />
      </td>
    </tr>
  <%
    }
  %>
  </tbody>
<ccp:evaluate if="<%= queueList.size() != 0 %>">
</table>
<br />
<input type="button" name="Refresh" value="Refresh" onclick="window.location.href='<%= ctx %>/reports';" />
</ccp:evaluate>