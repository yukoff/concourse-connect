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
<%@ page
    import="com.concursive.connect.web.modules.timesheet.dao.DailyTimesheet" %>
<%@ page
    import="com.concursive.connect.web.modules.timesheet.dao.ProjectTimesheet" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="timesheet" class="com.concursive.connect.web.modules.timesheet.dao.DailyTimesheetList" scope="request"/>
<jsp:useBean id="projectList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="projectTimesheetList" class="com.concursive.connect.web.modules.timesheet.dao.ProjectTimesheetList" scope="request"/>
<%@ include file="initPage.jsp" %>
<body onLoad="resizeIframe();totalAll();" bgcolor="#FFFFFF" LEFTMARGIN="0" MARGINWIDTH="0" TOPMARGIN="0" MARGINHEIGHT="0">
<script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/iframe.js"></script>
<script type="text/javascript">
  function resizeIframe() {
    parent.document.getElementById('weekView').height = getHeight("viewTable") + 25;
  }
  function totalAll() {
    for (lineId = 0; lineId < (<%= projectTimesheetList.size() %> + 5); lineId++) {
      addLine(lineId);
    }
    for (dayId = 0; dayId < 7; dayId++) {
      addColumn(dayId);
    }
    grandTotal();
  }
  function total(lineId, dayId) {
    addLine(lineId);
    addColumn(dayId);
    grandTotal();
  }
  function grandTotal() {
    var sum = 0;
    for (i = 0; i < 7; i++) {
      elm = document.getElementById("totalDay" + i);
      if (elm.value != "") {
        value = parseInt(elm.value);
        sum += value;
      }
    }
    document.getElementById("totalAll").value = sum;
  }
  function addLine(lineId) {
    var sum = 0;
    for (dayId = 0; dayId < 7; dayId++) {
      elm = document.getElementById("line" + lineId + "day" + dayId);
      if (elm.value != "") {
        value = parseInt(elm.value);
        sum += value;
      }
    }
    document.getElementById("total" + lineId).value = sum;
  }
  function addColumn(dayId) {
    var sum = 0;
    for (lineId = 0; lineId < (<%= projectTimesheetList.size() %> + 5); lineId++) {
      elm = document.getElementById("line" + lineId + "day" + dayId);
      if (elm.value != "") {
        value = parseInt(elm.value);
        sum += value;
      }
    }
    document.getElementById("totalDay" + dayId).value = sum;
  }
  function selectAll(varName) {
    var count = 0;
    for (dayId = 0; dayId < 7; dayId++) {
      elm = document.getElementById(varName + dayId);
      if (elm != null) {
        if (elm.checked == false) {
          ++count;
          elm.checked = true;
        }
      }
    }
    if (count == 0) {
      selectNone(varName);
    }
  }
  function selectNone(varName) {
    for (dayId = 0; dayId < 7; dayId++) {
      elm = document.getElementById(varName + dayId);
      if (elm != null) {
        elm.checked = false;
      }
    }
  }
</script>
<form name="timesheetForm" action="<%= ctx %>/Timesheet.do?command=Save&start=<%= timesheet.getStartDate().getTime() %>&popup=true" method="post">
<%-- Week --%>
  <table id="viewTable" cellpadding="4" cellspacing="0" border="0">
    <tr>
      <td valign="bottom">&nbsp;</td>
<%
  // Global
  int tabIndex = -1;
  // Determine the dates for display
  Calendar cal = Calendar.getInstance();
  cal.set(Calendar.MILLISECOND, 0);
  cal.set(Calendar.SECOND, 0);
  cal.set(Calendar.MINUTE, 0);
  cal.set(Calendar.HOUR_OF_DAY, 0);
  java.util.Date todayDate = new java.util.Date(cal.getTimeInMillis());
  SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  formatter.applyPattern("M/d/yyyy");
  // Go through days
  Calendar movingCal = Calendar.getInstance();
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  for (int i = 0; i < 7; i++) {
    boolean today = formatter.format(movingCal.getTime()).equals(formatter.format(todayDate));
    boolean afterToday = movingCal.getTimeInMillis() > todayDate.getTime();
    boolean isWeekend =
        (movingCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
        movingCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    DailyTimesheet thisTimesheet = timesheet.getTimesheet(movingCal);
    // Color coding
    String highlightClass = null;
    if (today) {
      highlightClass = "resourceToday";
    } else if (thisTimesheet.getVacation()) {
      highlightClass = "resourceUnavailable";
    } else if (thisTimesheet.getUnavailable()) {
      highlightClass = "resourceConditional";
    } else if (isWeekend) {
      highlightClass = "resourceOffDay";
    } else if (afterToday) {
      highlightClass = "resourceFuture";
    } else {
      highlightClass = "resourcePrevious";
    }
%>
      <td class="<%= highlightClass %>" align="center">
        <ccp:tz timestamp="<%= new Timestamp(movingCal.getTimeInMillis()) %>" pattern="EEE"/><br />
        <ccp:tz timestamp="<%= new Timestamp(movingCal.getTimeInMillis()) %>" pattern="MMM d"/>
      </td>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
      <td align="center" valign="bottom">&nbsp;</td>
    </tr>
    <%-- Vacation --%>
    <tr>
      <td align="right">Vacation/Holiday/Time Off</td>
<%
      movingCal = Calendar.getInstance();
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  for (int k = 0; k < 7; k++) {
    DailyTimesheet thisTimesheet = timesheet.getTimesheet(movingCal);
%>
      <td align="center" style="border-right: 1px dotted #888887;"><input type="checkbox" id="vacation<%= k %>" name="vacation<%= k %>" value="ON" <ccp:evaluate if="<%= thisTimesheet.getVacation() %>">checked</ccp:evaluate> /></td>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
      <td><a href="javascript:selectAll('vacation');">all</a></td>
    </tr>
    <%-- Unavailable --%>
    <tr>
      <td align="right">Conditional/Unavailable</td>
<%
  movingCal = Calendar.getInstance();
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  for (int k = 0; k < 7; k++) {
    DailyTimesheet thisTimesheet = timesheet.getTimesheet(movingCal);
%>
      <td align="center" style="border-right: 1px dotted #888887;"><input type="checkbox" id="unavailable<%= k %>" name="unavailable<%= k %>" value="ON" <ccp:evaluate if="<%= thisTimesheet.getUnavailable() %>">checked</ccp:evaluate> /></td>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
      <td><a href="javascript:selectAll('unavailable');">all</a></td>
    </tr>
<%
  // List the projects, the input fields, and the grand total
  int count = -1;
  int maxCount = projectTimesheetList.size() + 5;
  Iterator projectIterator = projectTimesheetList.values().iterator();
  for (int i = 0; i < maxCount; i++) {
    ProjectTimesheet projectTimesheet = null;
    if (projectIterator.hasNext()) {
      projectTimesheet = (ProjectTimesheet) projectIterator.next();
    } else {
      projectTimesheet = new ProjectTimesheet();
    }
    ++count;
    tabIndex = count;
%>
    <tr>
      <td><%= projectList.getHtmlSelect("project" + i, projectTimesheet.getProjectId()) %></td>
<%
    movingCal = Calendar.getInstance();
    movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
    for (int j = 0; j < 7; j++) {
      tabIndex = tabIndex + maxCount;
      double hours = 0;
      hours = projectTimesheet.getHours(movingCal.getTimeInMillis());
%>
      <td align="center" style="border-right: 1px dotted #888887;">
        <input type="hidden" name="line<%= i %>day<%= j %>date" value="<%= movingCal.getTimeInMillis() %>" />
        <input type="text" size="3" name="line<%= i %>day<%= j %>"
               id="line<%= i %>day<%= j %>"
               <ccp:evaluate if="<%= hours <= 0 %>">
                 value=""
               </ccp:evaluate>
               <ccp:evaluate if="<%= hours > 0 %>">
                 value="<%= hours %>"
               </ccp:evaluate>
               onChange="total(<%= i %>,<%= j %>)" TABINDEX="<%= tabIndex %>" />
      </td>
<%
      movingCal.add(Calendar.DATE, 1);
    }
%>
      <td align="center"><input type="text" size="3" name="total<%= i %>" id="total<%= i %>" value="0" disabled /></td>
    </tr>
<%
  }
%>
    <tr>
      <td align="right">Total</td>
<%
  // List the total fields
  for (int k = 0; k < 7; k++) {
%>
      <td align="center"><input type="text" size="3" name="totalDay<%= k %>" id="totalDay<%= k %>" value="0" disabled /></td>
<%
  }
%>
      <td align="center"><input type="text" size="3" name="totalAll" id="totalAll" value="0" disabled /></td>
    </tr>
    <%-- Certify --%>
<%--
    <tr>
      <td align="right">User Verified/Submitted</td>
<%
  movingCal = Calendar.getInstance();
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  for (int k = 0; k < 7; k++) {
    DailyTimesheet thisTimesheet = timesheet.getTimesheet(movingCal);
%>
      <td align="center"><input type="checkbox" id="verified<%= k %>" name="verified<%= k %>" value="ON" TABINDEX="<%= ++tabIndex %>" <ccp:evaluate if="<%= thisTimesheet.getVerified() %>">checked</ccp:evaluate> /></td>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
      <td><a href="javascript:selectAll('verified');">all</a></td>
    </tr>
--%>
    <%-- Approve --%>
<%--
    <tr>
      <td align="right">Administrator Recorded</td>
<%
  movingCal = Calendar.getInstance();
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  for (int k = 0; k < 7; k++) {
    DailyTimesheet thisTimesheet = timesheet.getTimesheet(movingCal);
%>
      <td align="center"><input type="checkbox" id="approve<%= k %>" name="approve<%= k %>" value="ON" TABINDEX="<%= ++tabIndex %>" <ccp:evaluate if="<%= thisTimesheet.getApproved() %>">checked</ccp:evaluate> /></td>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
      <td><a href="javascript:selectAll('approve');">all</a></td>
    </tr>
--%>

    <%-- Submit --%>
    <tr>
      <td>&nbsp;</td>
      <td colspan="7"><input type="hidden" name="start" value="<%= timesheet.getStartDate().getTime() %>" /><input type="submit" name="Save" value="<ccp:label name="button.save">Save</ccp:label>" /></td>
      <td>&nbsp;</td>
    </tr>
  </table>
</form>
</body>
