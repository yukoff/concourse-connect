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
<%@ page import="com.concursive.commons.files.FileUtils" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page
    import="com.concursive.connect.web.modules.timesheet.dao.DailyTimesheet" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="timesheet" class="com.concursive.connect.web.modules.timesheet.dao.DailyTimesheetList" scope="request"/>
<%@ include file="initPage.jsp" %>
<body onLoad="resizeIframe();" bgcolor="#FFFFFF" LEFTMARGIN="0" MARGINWIDTH="0" TOPMARGIN="0" MARGINHEIGHT="0">
<script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/iframe.js"></script>
<script type="text/javascript">
  function resizeIframe() {
    parent.document.getElementById('sliderView').height = getHeight("viewTable") + 25;
  }
</script>
<table id="viewTable" cellpadding="2" cellspacing="0" border="0" width="100%" class="resource">
  <tr>
<%-- Months --%>
<%
  // Global
  // Determine the dates for display
  Calendar cal = Calendar.getInstance();
  cal.set(Calendar.MILLISECOND, 0);
  cal.set(Calendar.SECOND, 0);
  cal.set(Calendar.MINUTE, 0);
  cal.set(Calendar.HOUR_OF_DAY, 0);
  java.util.Date todayDate = new java.util.Date(cal.getTimeInMillis());
  SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  formatter.applyPattern("M/d/yyyy");
  // Go through months
  Calendar movingCal = Calendar.getInstance();
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  while (movingCal.getTimeInMillis() < timesheet.getEndDate().getTime()) {
    int previousMonth = -1;
    int thisMonth = -1;
%>
    <th colspan="7"><a href="javascript:parent.adjustDates(<%= movingCal.getTimeInMillis() %>, '<ccp:tz timestamp="<%= new Timestamp(movingCal.getTimeInMillis()) %>" dateOnly="true" />')"><%
      for (int i = 0; i < 7; i++) {
        thisMonth = movingCal.get(Calendar.MONTH);
        if (thisMonth != previousMonth) {
%><ccp:evaluate if="<%= previousMonth != -1 %>">/</ccp:evaluate><ccp:tz timestamp="<%= new Timestamp(movingCal.getTimeInMillis()) %>" pattern="MMM"/><%
        }
        previousMonth = thisMonth;
        movingCal.add(Calendar.DATE, 1);
      }
%></a>
    </th>
<%
  }
%>
  </tr>
<%-- Days --%>
  <tr>
<%
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  while (movingCal.getTimeInMillis() < timesheet.getEndDate().getTime()) {
    boolean today = formatter.format(movingCal.getTime()).equals(formatter.format(todayDate));
%>
    <th<ccp:evaluate if="<%= today %>"> class="resourceToday"</ccp:evaluate>>
      <ccp:tz timestamp="<%= new Timestamp(movingCal.getTimeInMillis()) %>" pattern="dd"/>
    </th>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
  </tr>
<%-- Hours --%>
  <tr class="resourceItem">
<%
  movingCal.setTimeInMillis(timesheet.getStartDate().getTime());
  while (movingCal.getTimeInMillis() < timesheet.getEndDate().getTime()) {
    boolean today = formatter.format(movingCal.getTime()).equals(formatter.format(todayDate));
    boolean afterToday = movingCal.getTimeInMillis() > todayDate.getTime();
    boolean isWeekend =
        (movingCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
        movingCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    DailyTimesheet thisTimesheet = timesheet.getTimesheet(movingCal);
    String display = String.valueOf(thisTimesheet.getTotalHours());
    if (display.endsWith(".0")) {
      display = display.substring(0, display.length() - 2);
    }
    if (display.equals("0")) {
      if (thisTimesheet.getVacation()) {
        display = "X";
      } else {
        display = null;
      }
    }
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
    }
%>
    <th<ccp:evaluate if="<%= hasText(highlightClass) %>"> class="<%= highlightClass %>"</ccp:evaluate>>
      <%= toHtml(display) %>
    </th>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
  </tr>
</table>
</body>