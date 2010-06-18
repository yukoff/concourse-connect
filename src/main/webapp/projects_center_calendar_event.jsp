<%--
  ~ ConcourseConnect
  ~ Copyright 2010 Concursive Corporation
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
<%@ taglib uri="http://packtag.sf.net" prefix="pack" %>
<%@ page import="java.util.*,java.text.DateFormat,java.lang.reflect.*, com.concursive.connect.web.modules.calendar.utils.CalendarBean,com.concursive.commons.text.StringUtils" %>
<%@ page import="com.concursive.connect.web.modules.issues.dao.Ticket " %>
<%@ page import="com.concursive.connect.web.modules.calendar.utils.CalendarEventList,com.concursive.connect.web.modules.calendar.utils.CalendarEvent" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="meeting" class="com.concursive.connect.web.modules.calendar.dao.Meeting" scope="request"/>
<jsp:useBean id="thisDay" class="com.concursive.connect.web.modules.calendar.utils.CalendarEventList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<%-- Initialize the drop-down menus --%>
<%@ include file="projects_center_calendar_menu.jspf" %>

<%-- Display the date --%>
<%
  // required for included files
  boolean firstEvent = true;
  int menuCount = 0;
  boolean showToday = false;

  TimeZone timeZone = Calendar.getInstance().getTimeZone();
  if (User.getTimeZone() != null) {
    timeZone = TimeZone.getTimeZone(User.getTimeZone());
  }

  Calendar today = Calendar.getInstance();
  today.setTimeZone(timeZone);

  Calendar thisCal = Calendar.getInstance();
  thisCal.setTimeZone(timeZone);
  thisCal.setTime(meeting.getStartDate());
%>
<div class="calendarListContainer">
  <div class="dateContainer">
    <div class="dateWrapper">
        <%-- The dates are already using the user's timezone, so do not use again --%>
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
  <%-- draw the events for the day --%>
  <%@ include file="projects_center_calendar_days_events_include.jspf" %>
</div>
