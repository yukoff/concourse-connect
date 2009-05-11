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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="thisWeekStartDate" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function adjustDates(start, val) {
    frames['weekView'].location.href='<%= ctx %>/Timesheet.do?command=Week&start=' + start + '&popup=true';
    document.inputForm.goToDate.value = val;
  }
  function refreshPage(start) {
    frames['sliderView'].location.href='<%= ctx %>/Timesheet.do?command=Slider&start=' + start + '&popup=true';
    frames['weekView'].location.href='<%= ctx %>/Timesheet.do?command=Week&start=' + start + '&popup=true';
  }
  function calendarTrigger(formName, fieldName, fieldValue) {
    window.location.href='<%= ctx %>/Timesheet.do?goToDate=' + fieldValue;
  }
</script>
<form name="inputForm" method="post" action="<%= ctx %>/Timesheet.do">
<img src="<%= ctx %>/images/icons/stock_form-time-field-16.gif" align="absmiddle" alt="" border="0" />
<ccp:label name="projectsTimesheets.timesheet">Timesheet:</ccp:label>
<ccp:username id="<%= User.getId() %>"/>
<br />
<ccp:label name="projectsTimesheets.weekOf">Week of:</ccp:label>
<input type="text" disabled size="10" name="goToDate" value="<ccp:tz timestamp="<%= new Timestamp(Long.parseLong(thisWeekStartDate)) %>" dateOnly="true" default="&nbsp;"/>" />
<a href="javascript:popCalendar('inputForm', 'goToDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
<br />
</form>
<table cellpadding="4" cellspacing="0" border="0" width="100%">
  <tr>
    <td>
      <%-- slider --%>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <tr>
      <td valign="top" width="100%">
      <iframe id="sliderView" name="sliderView"
              style="overflow: hidden;" border="0" frameborder="0"
              width="100%" height="75" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Timesheet.do?command=Slider&start=<%= thisWeekStartDate %>&popup=true">
      <ccp:label name="projectsTimesheets.viewNotSupported">View not supported in this browser.</ccp:label>
      </iframe>
      </td>
      </tr>
      </table>
      <br />
      <%-- Selected Week --%>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <tr>
      <td valign="top" width="100%">
      <iframe id="weekView" name="weekView"
              style="overflow: hidden;" border="0" frameborder="0"
              width="100%"  src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Timesheet.do?command=Week&start=<%= thisWeekStartDate %>&popup=true">
      <ccp:label name="projectsTimesheets.viewNotSupported">View not supported in this browser.</ccp:label>
      </iframe>
      </td>
      </tr>
      </table>
    </td>
  </tr>
</table>
