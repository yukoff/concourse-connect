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
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page
  import="java.util.*,java.text.DateFormat,java.lang.reflect.*, com.concursive.connect.web.modules.calendar.utils.CalendarBean,com.concursive.commons.text.StringUtils" %>
<%@ page import="com.concursive.connect.web.modules.issues.dao.Ticket " %>
<%@ page import="com.concursive.connect.web.modules.calendar.utils.CalendarEventList,com.concursive.connect.web.modules.calendar.utils.CalendarEvent" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="calendarView" class="com.concursive.connect.web.modules.calendar.utils.CalendarView" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<%
  CalendarBean calendarInfo = (CalendarBean) request.getAttribute("calendarInfoBean" + project.getId());
  request.setAttribute("calendarInfo", calendarInfo);

  String returnPage = request.getParameter("return");
  TimeZone timeZone = Calendar.getInstance().getTimeZone();
  if (User.getTimeZone() != null) {
    timeZone = TimeZone.getTimeZone(User.getTimeZone());
  }
%>

<%-- Display header label --%>

<ccp:evaluate if="<%= calendarInfo.isAgendaView() %>">
  <h1>Upcoming Events</h1>
</ccp:evaluate>
<ccp:evaluate if="<%= !calendarInfo.isAgendaView() %>">
  <ccp:evaluate if="<%= \"week\".equalsIgnoreCase(calendarInfo.getCalendarView()) %>">
    <h1>Events for the week:
      <ccp:tz timestamp="<%= new Timestamp(calendarView.getCalendarInfo().getStartOfWeekDate().getTime()) %>"
                 dateOnly="true" dateFormat="<%= DateFormat.MEDIUM %>" default="&nbsp;"/> - <ccp:tz
        timestamp="<%= new Timestamp(calendarView.getCalendarInfo().getEndOfWeekDate().getTime()) %>" dateOnly="true"
        dateFormat="<%= DateFormat.MEDIUM %>" default="&nbsp;"/>
    </h1>
  </ccp:evaluate>
  <ccp:evaluate if="<%= \"day\".equalsIgnoreCase(calendarInfo.getCalendarView()) %>">
    <h1>Day View</h1>
  </ccp:evaluate>
</ccp:evaluate>

<%-- Display back link --%>
<ccp:evaluate if="<%= !calendarInfo.isAgendaView() %>">

  <a href="javascript:agendaView()">Back To Upcoming Events</a>

</ccp:evaluate>
<%-- Display the days, always starting with today in Agenda View --%>


<ccp:evaluate if="<%= calendarInfo.isAgendaView() %>">
  <div class="calendarListContainer">
  <div class="dateContainer">
    <div class="dateWrapper">
      <%
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTimeZone(timeZone);
      %>
        <span class="year">
        <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="y" dateOnly="true"
                   default="&nbsp;"/>
        </span>
        <span class="month">
        <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="MMM" dateOnly="true"
                   default="&nbsp;"/>
        </span>
        <span class="date">
        <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="d" dateOnly="true"
                   default="&nbsp;"/>
        </span>
        <span class="day">
        <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="EE" dateOnly="true"
                   default="&nbsp;"/>
        </span>
    </div>

    <div class="dateCurrent">
      <span class="today">(Today)</span>
    </div>
  </div>
</ccp:evaluate>
<%
  Calendar today = Calendar.getInstance();
  today.setTimeZone(timeZone);
  Iterator days = (calendarView.getEvents(100)).iterator();
  if (days.hasNext()) {
    boolean showToday = false;
    boolean firstEvent = true;
    int count = 0;
    int menuCount = 0;
    while (days.hasNext()) {
      CalendarEventList thisDay = (CalendarEventList) days.next();
      Calendar thisCal = Calendar.getInstance();
      thisCal.setTime(thisDay.getDate());
      boolean isToday =
        ((thisCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) &&
          (thisCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)));
%>
<ccp:evaluate if="<%= (calendarInfo.isAgendaView() && !isToday && count == 0) %>">
  <div class="eventsContainer">
    <div class="eventsWrapper">
      <p>There are no events scheduled for today.</p>
    </div>
  </div>
</div> <%-- Close calendarListContainer --%>
</ccp:evaluate>
<ccp:evaluate if="<%= (!isToday && calendarInfo.isAgendaView()) || !calendarInfo.isAgendaView() %>">
  <div class="calendarListContainer">
  <div class="dateContainer">
    <div class="dateWrapper">
        <%-- The dates are already using the user's timezone, so do not use again --%>
          <span class="year">
            <ccp:tz timestamp="<%= new Timestamp(thisCal.getTimeInMillis()) %>" pattern="y" dateOnly="true"
                       default="&nbsp;"/>
          </span>
          <span class="month">
            <ccp:tz timestamp="<%= new Timestamp(thisCal.getTimeInMillis()) %>" pattern="MMM" dateOnly="true"
                       default="&nbsp;"/>
          </span>
          <span class="date">
              <ccp:tz timestamp="<%= new Timestamp(thisCal.getTimeInMillis()) %>" pattern="d" dateOnly="true"
                         default="&nbsp;"/>
          </span>
          <span class="day">
             <ccp:tz timestamp="<%= new Timestamp(thisCal.getTimeInMillis()) %>" pattern="EE" dateOnly="true"
                        default="&nbsp;"/>
          </span>
    </div>
  </div>
</ccp:evaluate>

<%-- draw the events for the day --%>
<%@ include file="projects_center_calendar_days_events_include.jspf" %>
<%--</div> Close calendarListContainer --%>

<%--<ccp:evaluate if="<%= days.hasNext() %>">
  </div>
</ccp:evaluate>--%>

</div>

<%
    count++;
  }
} else {
%>

<%--<img src="<%= ctx %>/images/select-arrow-trans.gif" border="0"/>--%>
  <div class="calendarListContainer">
	<ccp:evaluate if="<%= (\"day\".equalsIgnoreCase(calendarView.getCalendarInfo().getCalendarView())) %>">
	  <div class="dateContainer">
	    <div class="dateWrapper">
	      <%
	        Calendar tmpCal = Calendar.getInstance();
	        tmpCal.setTimeZone(timeZone);
	        tmpCal.set(Calendar.YEAR,calendarView.getCalendarInfo().getYearSelected());
	        tmpCal.set(Calendar.MONTH,calendarView.getCalendarInfo().getMonthSelected() - 1);
	        tmpCal.set(Calendar.DAY_OF_MONTH,calendarView.getCalendarInfo().getDaySelected());
	        
	      %>
	        <%-- The dates are already using the user's timezone, so do not use again --%>
	          <span class="year">
	            <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="y" dateOnly="true"
	                       default="&nbsp;"/>
	          </span>
	          <span class="month">
	            <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="MMM" dateOnly="true"
	                       default="&nbsp;"/>
	          </span>
	          <span class="date">
	              <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="d" dateOnly="true"
	                         default="&nbsp;"/>
	          </span>
	          <span class="day">
	             <ccp:tz timestamp="<%= new Timestamp(tmpCal.getTimeInMillis()) %>" pattern="EE" dateOnly="true"
	                        default="&nbsp;"/>
	          </span>
	    </div>
	  </div>
  </ccp:evaluate>
	<div class="eventsContainer">
	  <div class="eventsWrapper">
	    <h3 class="eventName">There are no events at this time.</h3>
	    <p>Please keep checking back for the latest events.</p>
	  </div>
	</div>
</div>


<%
  }
%>
