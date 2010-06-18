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
<%@ page import="com.concursive.connect.web.modules.reports.dao.Parameter" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="reportList" class="com.concursive.connect.web.modules.reports.dao.ReportList" scope="request"/>
<jsp:useBean id="report" class="com.concursive.connect.web.modules.reports.dao.Report" scope="request"/>
<jsp:useBean id="parameterList" class="com.concursive.connect.web.modules.reports.dao.ParameterList" scope="request"/>
<jsp:useBean id="projectList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<%@ include file="initPage.jsp" %>
<form method="post" name="inputForm" action="<%= ctx %>/Reports.do?command=Generate&auto-populate=true">
<img src="<%= ctx %>/images/icons/stock_form-16.gif" align="absmiddle" alt="" border="0"/>
<a href="<%= ctx %>/reports">Reports</a> >
<a href="<%= ctx %>/Reports.do?command=Setup">Setup</a> >
Configuration<br />
<br />
Configure the parameters for this report...<br />
<br />
<b><%= toHtml(report.getTitle()) %></b><br />
<br />
<ccp:evaluate if="<%= parameterList.size() > 0 %>">
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Parameters
      </th>
    </tr>
  </thead>
  <tbody>
    <%
      Iterator i = parameterList.iterator();
      while (i.hasNext()) {
        Parameter thisParameter = (Parameter) i.next();
        if (thisParameter.getIsForPrompting()) {
    %>
      <tr class="containerBody">
        <td class="formLabel">
          <%= toHtml(thisParameter.getDisplayName()) %>
        </td>
        <td>
          <%= thisParameter.getHtml(request) %>
          <%= showAttribute(request, thisParameter.getName() + "Error") %>
        </td>
      </tr>
    <%
        }
      }
    %>
  </tbody>
</table>
</ccp:evaluate>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Options
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td class="formLabel">
        Format?
      </td>
      <td>
        <select name="output" size="1">
          <option value="pdf">PDF</option>
          <option value="html">HTML</option>
          <option value="excel">Excel</option>
          <option value="csv">CSV</option>
        </select>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Email?
      </td>
      <td>
        <input type="checkbox" name="sendEmail" value="true" />
        Send a copy by email
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel" valign="top">
        Schedule?
      </td>
      <td>
        <input type="checkbox" name="scheduleMonday" value="true" />Mon
        <input type="checkbox" name="scheduleTuesday" value="true" />Tue
        <input type="checkbox" name="scheduleWednesday" value="true" />Wed
        <input type="checkbox" name="scheduleThursday" value="true" />Thur
        <input type="checkbox" name="scheduleFriday" value="true" />Fri
        <input type="checkbox" name="scheduleSaturday" value="true" />Sat
        <input type="checkbox" name="scheduleSunday" value="true" />Sun<br />
        at
        <ccp:timeSelect baseName="scheduleTime" value="<%= new Timestamp(System.currentTimeMillis()) %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/><br />
        Starting <input type="text" name="scheduleTime" id="scheduleTime" size="10" value="<ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" dateOnly="true"/>">
        <a href="javascript:popCalendar('inputForm', 'scheduleTime', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Cleanup?
      </td>
      <td>
        Delete after <input type="text" name="cleanup" value="1" size="3" maxlength="2" /> day(s).
      </td>
    </tr>
  </tbody>
</table>
<input type="submit" name="Generate" value="Generate" />
<input type="hidden" name="reportId" value="<%= report.getId() %>" />
</form>
