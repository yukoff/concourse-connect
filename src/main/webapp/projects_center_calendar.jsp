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
<%@ page import="com.concursive.connect.web.modules.calendar.utils.CalendarBean" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="calendarView" class="com.concursive.connect.web.modules.calendar.utils.CalendarView" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<%-- Initialize the drop-down menus --%>
<%@ include file="projects_center_calendar_menu.jspf" %>
<%
  String returnPage = request.getParameter("return");

  CalendarBean calendarInfo = (CalendarBean) request.getAttribute("calendarInfoBean" + project.getId());
  request.setAttribute("calendarInfo", calendarInfo);

  calendarView.setCellPadding(4);
  calendarView.setCellSpacing(0);
  calendarView.setSortEvents(true);
  calendarView.setMonthArrows(true);
  calendarView.setFrontPageView(true);
  calendarView.setShowSubject(false);
%>
<script type="text/javascript">
  function calendarChange(){
    <portlet:renderURL var="calendarUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
    </portlet:renderURL>
    var year = document.monthBean.primaryYear.value;
    var month = document.monthBean.primaryMonth.value;
    if (month.length == 1) {
      month = "0" + month;
    }
    window.location.href="${calendarUrl}/" + year + "-" + month;
  }
  function eventFilter(){
    //&filter='+document.getElementById('filter').value
    <portlet:renderURL var="calendarUrl" windowState="maximized">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
      <portlet:param name="portlet-command" value="agenda-events"/>
      <portlet:param name="out" value="text"/>
    </portlet:renderURL>
    sendRequest('${calendarUrl}&source=calendardetails<%=returnPage!=null?"&return="+returnPage:""%>','calendarDetails');
  }
  function showDayEvents(year,month,day){
    //&filter='+document.getElementById('filter').value
    <portlet:renderURL var="calendarUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
    </portlet:renderURL>
    if (month.length == 1) {
      month = "0" + month;
    }
    if (day.length == 1) {
      day = "0" + day;
    }
    //sendRequest('${calendarUrl}&year='+year+'&month='+month+'&day='+day+'<%=returnPage!=null?"&return="+returnPage:""%>','calendarDetails');
    window.location.href="${calendarUrl}/" + year + "-" + month + "-" + day;
  }
  function showToDaysEvents(month,day,year){
    //&filter='+document.getElementById('filter').value
    <portlet:renderURL var="calendarUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
    </portlet:renderURL>
    if (month.length == 1) {
      month = "0" + month;
    }
    if (day.length == 1) {
      day = "0" + day;
    }
    //sendRequest('${calendarUrl}&month='+month +'&day='+day+'&year='+year+'&reloadCalendar=true<%= returnPage!=null ? "&return="+returnPage : "" %>','calendarDetails');
    window.location.href="${calendarUrl}/" + year + "-" + month + "-" + day;
  }
  function goToMonth(month) {
    <portlet:renderURL var="calendarUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
    </portlet:renderURL>
    window.location.href="${calendarUrl}/" + month;
  }
  function showWeekEvents(year,month,day){
    //&filter='+document.getElementById('filter').value
    <portlet:renderURL var="calendarUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
    </portlet:renderURL>
    if (month.length == 1) {
      month = "0" + month;
    }
    if (day.length == 1) {
      day = "0" + day;
    }
    //sendRequest('${calendarUrl}&year='+year+'&month='+month+'&startMonthOfWeek='+month+'&startDayOfWeek='+day+'<%=returnPage!=null?"&return="+returnPage:""%>','calendarDetails');
    window.location.href="${calendarUrl}/" + year + "-" + month + "-" + day + "?view=week";
  }
  function reloadCalendarDetails(){
    <portlet:renderURL var="calendarUrl" windowState="maximized">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
      <portlet:param name="portlet-command" value="agenda-events"/>
      <portlet:param name="out" value="text"/>
      <portlet:param name="userId" value="${calendarInfo.selectedUserId}"/>
      <c:choose>
        <c:when test="${calendarInfo.calendarView eq 'day'}">
          <portlet:param name="view" value="day"/>
          <portlet:param name="month" value="${calendarInfo.monthSelected}"/>
          <portlet:param name="day" value="${calendarInfo.daySelected}"/>
          <portlet:param name="year" value="${calendarInfo.yearSelected}"/>
        </c:when>
        <c:when test="${calendarInfo.calendarView eq 'week'}">
          <portlet:param name="view" value="week"/>
          <portlet:param name="startMonthOfWeek" value="${calendarInfo.startMonthOfWeek}"/>
          <portlet:param name="startDayOfWeek" value="${calendarInfo.startDayOfWeek}"/>
          <portlet:param name="year" value="${calendarInfo.yearSelected}"/>
        </c:when>
        <c:when test="${calendarInfo.calendarView eq 'today'}">
          <portlet:param name="view" value="today"/>
          <portlet:param name="month" value="${calendarInfo.monthSelected}"/>
          <portlet:param name="day" value="${calendarInfo.daySelected}"/>
          <portlet:param name="year" value="${calendarInfo.yearSelected}"/>
        </c:when>
        <c:otherwise>
          <portlet:param name="view" value="agenda"/>
        </c:otherwise>
      </c:choose>
    </portlet:renderURL>
    //&filter='+document.getElementById('filter').value
    sendRequest('${calendarUrl}','calendarDetails');
  }
  function switchTableClass(E,className,rowOrCell){
    if(rowOrCell == "cell"){
      tdToChange = E;
    }
    while (E.tagName!="TR") {
      E=E.parentNode;
    }
    rowToChange = E;
    resetCalendar();
    if (rowOrCell == "cell") {
      tdToChange.className = className;
      return;
    }
    for (i=0;i<rowToChange.childNodes.length;i++) {
      if (rowToChange.childNodes.item(i).tagName == "TD") {
        rowToChange.childNodes.item(i).className = className;
      }
    }
  }
  function resetCalendar() {
    tableElement = document.getElementById('calendarTable');
    E = tableElement.childNodes.item(0);
    for (i=0;i<E.childNodes.length;i++) {
      if (E.childNodes.item(i).tagName == "TR" && ! (E.childNodes.item(i).getAttribute('name') == "staticrow")) {
        tmpTR = E.childNodes.item(i);
        for (j=0;j<tmpTR.childNodes.length;j++) {
          if (tmpTR.childNodes.item(j).tagName == "TD" && tmpTR.childNodes.item(j).className.indexOf("weekName") == -1) {
            tmpTR.childNodes.item(j).className = tmpTR.childNodes.item(j).getAttribute('name');
          }
        }
      }
    }
  }
  function agendaView(){
    resetCalendar();
    <portlet:renderURL var="calendarUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="calendar"/>
    </portlet:renderURL>
    window.location.href="${calendarUrl}";      
  }

</script>
<div class="portletWrapper">
  <form name="monthBean"  method="get">
    <%= calendarView.getHtml(ctx) %>
  </form>
</div>
