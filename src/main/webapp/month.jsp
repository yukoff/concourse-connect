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
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.concursive.connect.web.modules.calendar.utils.CalendarView" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.calendar.utils.CalendarEventList" %>
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<html>
<head>
<title>Calendar</title>
<%@ include file="initPage.jsp" %>
<jsp:include page="css_include.jsp" flush="true"/>
<%
   String formName = StringUtils.encodeUrl(request.getParameter("form"));
   String element = StringUtils.encodeUrl(request.getParameter("element"));
   String language = StringUtils.encodeUrl(request.getParameter("language"));
   String country = StringUtils.encodeUrl(request.getParameter("country"));
   if (language == null) {
     language = "en";
     country = "US";
   }
%>
<script LANGUAGE="javascript" TYPE="text/javascript">
  function openWindow(month, day, year) {
    width = 600;
    height = 400;
    url='<%= ctx %>/schedule.jsp?month=' + month +
          '&&year=' + year + '&&day=' + day;
    Win = open(url, 'as_events', 'toolbar=0,location=0,directories=0,status=0,menubar=0,resizable=1,width=' + width + ',height=' + height + ',scrollbars=yes');
    Win.focus();
  }
  function formatDate(val) {
    opener.document.<%= formName %>.<%= element %>.value = val;
    try {
      opener.calendarTrigger('<%= formName %>','<%= element %>',val);
    } catch(ex) {
    }
    window.close();
  }
  function returnDate(dayVal, monthVal, yearVal) {
    window.frames['server_commands'].location.href='<%= ctx %>/month_format.jsp?month=' + monthVal + '&day=' + dayVal + '&year=' + yearVal + '&language=<%= language %>&country=<%= country %>';
  }
</script>
</head>
<body>
<form name="monthBean" action="<%= ctx %>/month.jsp">
<%
  //Retrieve the parameters
  String year = StringUtils.encodeUrl(request.getParameter("year"));
  String month = StringUtils.encodeUrl(request.getParameter("month"));
  String day = StringUtils.encodeUrl(request.getParameter("day"));
  String origDay = StringUtils.encodeUrl(request.getParameter("origDay"));
  String origYear = StringUtils.encodeUrl(request.getParameter("origYear"));
  String origMonth = StringUtils.encodeUrl(request.getParameter("origMonth"));
  String dateString = request.getParameter("date");
  String timeZone = StringUtils.encodeUrl(request.getParameter("timeZone"));

  //If the user clicks the next/previous arrow, increment/decrement the month
  //Range checking is not necessary on the month.  The calendar object automatically
  //increments the year when necessary
  if (month != null) {
    try {
      int monthTmp = Integer.parseInt(month);
      if (request.getParameter("next.x") != null) { ++monthTmp; }
      if (request.getParameter("prev.x") != null) { monthTmp += -1; }
      month = String.valueOf(monthTmp);
      day = "1";
    } catch(NumberFormatException e) {
    }
  }

  // Create the calendar with the selected locale
  Locale locale = new Locale(language, country);
  CalendarView calendarView = new CalendarView(locale);

  //Check to see if this should be a popup window
  String action = request.getParameter("action");
  if ((action != null) && (action.equals("popup"))) {
    out.println("<input type=\"hidden\" name=\"action\" value=\"popup\">");
    calendarView.setPopup(true);
    calendarView.setBorderSize(0);
  } else {
    calendarView.setHeaderSpace(true);
    calendarView.setMonthArrows(true);
    calendarView.setBorderSize(1);
  }

  // Break apart String into fields, using locale (from input field)
  if (dateString != null) {
    try {
      java.util.Date tmpDate = DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(dateString);
      Calendar parseCal = Calendar.getInstance();
      parseCal.setTime(tmpDate);
      month = String.valueOf(parseCal.get(Calendar.MONTH) + 1);
      day = String.valueOf(parseCal.get(Calendar.DAY_OF_MONTH));
      year = String.valueOf(parseCal.get(Calendar.YEAR));
    } catch (Exception e) {
      System.out.println("*** Could not parse date: " + dateString);
    }
  }

  // Set the calendar with appropriate values
  calendarView.setYear(year);
  calendarView.setMonth(month);
  calendarView.setDay(day);

  //set the timezone if the user is logged in
  User thisUser = (User) request.getSession().getAttribute("User");
  if (thisUser != null && thisUser.getId() != -1) {
    calendarView.setTimeZone(TimeZone.getTimeZone(thisUser.getTimeZone()));
  } else if (timeZone != null) {
    calendarView.setTimeZone(TimeZone.getTimeZone(timeZone));
  }

  //Configure the month to highlight a date that was passed in
  String origStatus = StringUtils.toHtmlValue(request.getParameter("origStatus"));
  if ((origStatus == null) && ((day == null) || (day.startsWith("undefined")))) {
    origStatus = "blank";
  } else if (origStatus == null) {
    origStatus = "highlight";
  }
  //Restore the original request date from the calling app,
  //this allows the calling date to be highlighted differently from
  //other dates
  if ((origYear != null) && (origMonth != null) && (origDay != null)) {
  } else {
    origYear = calendarView.getYear();
    origMonth = calendarView.getMonth();
    origDay = calendarView.getDay();
  }
  CalendarEventList highlightEvent = new CalendarEventList();
  highlightEvent.put(origStatus, null);
  calendarView.getEventList().put(origMonth + "/" + origDay + "/" + origYear, highlightEvent);
%>
<input type="hidden" name="origYear" value="<%= origYear %>">
<input type="hidden" name="origMonth" value="<%= origMonth %>">
<input type="hidden" name="origDay" value="<%= origDay %>">
<input type="hidden" name="origStatus" value="<%= origStatus %>">
<input type="hidden" name="form" value="<%= formName %>">
<input type="hidden" name="element" value="<%= element %>">
<input type="hidden" name="language" value="<%= language %>">
<input type="hidden" name="country" value="<%= country %>">
<ccp:evaluate if="<%= timeZone != null %>">
  <input type="hidden" name="timeZone" value="<%=timeZone%>">
</ccp:evaluate>
<%= calendarView.getHtml(ctx) %>
</form>
<iframe src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/empty.html" name="server_commands" id="server_commands" style="visibility:hidden" height="0"></iframe>
</body>
</html>
