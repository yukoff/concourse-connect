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
<%@ page import="java.util.*,
                 com.concursive.commons.date.DateUtils" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page
    import="com.concursive.connect.web.modules.plans.dao.AssignmentUserAllocation" %>
<%@ page
    import="com.concursive.connect.web.modules.plans.dao.AssignmentProjectAllocation" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="allocationList" class="com.concursive.connect.web.modules.plans.dao.AssignmentAllocationList" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/iframe.js"></script>
<script type="text/javascript">
  function resizeIframe() {
    parent.document.getElementById('viewId').height = getHeight("viewTable") + 25;
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
  movingCal.setTimeInMillis(allocationList.getStartDate().getTime());
  while (movingCal.getTimeInMillis() < allocationList.getEndDate().getTime()) {
    int previousMonth = -1;
    int thisMonth = -1;
%>
    <th colspan="7">
<%
      for (int i = 0; i < 7; i++) {
        thisMonth = movingCal.get(Calendar.MONTH);
        if (thisMonth != previousMonth) {
%><ccp:evaluate if="<%= previousMonth != -1 %>">/</ccp:evaluate><ccp:tz timestamp="<%= new Timestamp(movingCal.getTimeInMillis()) %>" pattern="MMM"/><%
        }
        previousMonth = thisMonth;
        movingCal.add(Calendar.DATE, 1);
      }
%>
    </th>
<%
  }
%>
  </tr>
<%-- Days --%>
  <tr>
<%
  movingCal.setTimeInMillis(allocationList.getStartDate().getTime());
  while (movingCal.getTimeInMillis() < allocationList.getEndDate().getTime()) {
    boolean today = formatter.format(movingCal.getTime()).equals(formatter.format(todayDate));
%>
    <th<ccp:evaluate if="<%= today %>"> class="resourceToday"</ccp:evaluate>>
      <ccp:evaluate if="<%= today %>">
        <font color="#000"><b>
      </ccp:evaluate>
      <ccp:tz timestamp="<%= new Timestamp(movingCal.getTimeInMillis()) %>" pattern="dd"/>
      <ccp:evaluate if="<%= today %>">
        </b></font>
      </ccp:evaluate>
    </th>
<%
    movingCal.add(Calendar.DATE, 1);
  }
%>
  </tr>
<%-- Users --%>
<%
  Iterator users = allocationList.getUserList().keySet().iterator();
  while (users.hasNext()) {
    Integer userId = (Integer) users.next();
    AssignmentUserAllocation userMap = (AssignmentUserAllocation) allocationList.getUser(userId);
%>
  <tr class="resourceGroup">
<%
    movingCal.setTimeInMillis(allocationList.getStartDate().getTime());
    while (movingCal.getTimeInMillis() < allocationList.getEndDate().getTime()) {
      boolean today = formatter.format(movingCal.getTime()).equals(formatter.format(todayDate));
      // Rollup the hours
      Double userHoursValue = (Double) userMap.getEstimatedDailyHours().get(formatter.format(movingCal.getTime()));
      String userHours = null;
      if (userHoursValue != null) {
        double hoursDouble = userHoursValue.doubleValue();
        if (hoursDouble == 0) {
          userHours = "";
        } else {
          hoursDouble = (double) Math.round(hoursDouble);
          userHours = String.valueOf(hoursDouble);
          if (userHours.endsWith(".0")) {
            userHours = userHours.substring(0, userHours.length() - 2);
          }
        }
      }
      String highlightClass = null;
      if (userMap.getTimesheet().getTimesheet(movingCal).getVacation()) {
        highlightClass = "resourceUnavailable";
        if (userHours == null) {
          userHours = "X";
        }
      }
%>
    <td align="center" <ccp:evaluate if="<%= hasText(highlightClass) %>"> class="<%= highlightClass %>"</ccp:evaluate>>
        <%= toHtml(userHours) %>
    </td>
<%
      movingCal.add(Calendar.DATE, 1);
    }
%>
  </tr>
<%-- Projects --%>
<%
    Iterator projects = userMap.keySet().iterator();
    while (projects.hasNext()) {
      Integer projectId = (Integer) projects.next();
      AssignmentProjectAllocation projectAllocation = (AssignmentProjectAllocation) userMap.get(projectId);
%>
  <tr class="resourceItem">
<%
      movingCal.setTimeInMillis(allocationList.getStartDate().getTime());
      while (movingCal.getTimeInMillis() < allocationList.getEndDate().getTime()) {
        boolean today = formatter.format(movingCal.getTime()).equals(formatter.format(todayDate));
        boolean afterToday = movingCal.getTimeInMillis() > todayDate.getTime();
        Double hoursValue = (Double) projectAllocation.getEstimatedDailyHours().get(formatter.format(movingCal.getTime()));
        String hours = null;
        if (hoursValue != null) {
          double hoursDouble = hoursValue.doubleValue();
          if (hoursDouble == 0) {
            hours = "";
          } else {
            if (hoursDouble > 8) {
              hoursDouble = 9;
            }
            hoursDouble = (double) Math.round(hoursDouble);
            hours = String.valueOf(hoursDouble);
            if (hours.endsWith(".0")) {
              hours = hours.substring(0, hours.length() - 2);
            }
            if (hours.equals("9")) {
              hours = "8+";
            }
          }
        }
        String highlightClass = null;
        if (userMap.getTimesheet().getTimesheet(movingCal).getVacation()) {
          highlightClass = "resourceUnavailable";
          if (hours == null) {
            hours = "X";
          }
        }
%>
    <ccp:evaluate if="<%= hasText(hours) %>">
      <ccp:evaluate if="<%= \"resourceUnavailable\".equals(highlightClass) %>">
        <th class="resourceUnavailable">
          <%= toHtml(hours) %>
        </th>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !\"resourceUnavailable\".equals(highlightClass) %>">
        <ccp:evaluate if="<%= today %>">
          <th class="resourceToday">
            <%= toHtml(hours) %>
          </th>
        </ccp:evaluate>
        <ccp:evaluate if="<%= afterToday %>">
          <th class="resourceFuture">
            <%= toHtml(hours) %>
          </th>
        </ccp:evaluate>
        <ccp:evaluate if="<%= !today && !afterToday %>">
          <th class="resourcePrevious">
            <%= toHtml(hours) %>
          </th>
        </ccp:evaluate>
      </ccp:evaluate>
    </ccp:evaluate>
    <ccp:evaluate if="<%= !hasText(hours) %>">
      <ccp:evaluate if="<%= today %>">
        <th class="resourceToday">
          <%= toHtml(hours) %>
        </th>
      </ccp:evaluate>
      <ccp:evaluate if="<%= afterToday || DateUtils.isWeekend(movingCal) %>">
        <td>
          <%= toHtml(hours) %>
        </td>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !today && !afterToday && !DateUtils.isWeekend(movingCal) %>">
        <th class="resourcePrevious">
          <%= toHtml(hours) %>
        </th>
      </ccp:evaluate>
    </ccp:evaluate>
<%
          movingCal.add(Calendar.DATE, 1);
        }
%>
  </tr>
<%
    }
  }
%>
</table>
<script type="text/javascript">
  resizeIframe();
</script>