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
<%@ page import="com.concursive.connect.web.modules.reports.dao.Report" %>
<jsp:useBean id="reportList" class="com.concursive.connect.web.modules.reports.dao.ReportList" scope="request"/>
<%@ include file="initPage.jsp" %>
<img src="<%= ctx %>/images/icons/stock_form-16.gif" align="absmiddle" alt="" border="0"/>
<a href="<%= ctx %>/reports">Reports</a> >
Setup<br />
<br />
Select a report to configure...<br />
<br />
<table class="pagedList">
  <thead>
    <tr>
      <th>
        Reports
      </th>
    </tr>
  </thead>
  <tbody>
    <ccp:evaluate if="<%= reportList.size() == 0 %>">
      <tr class="row2">
        <td>
          There are no available reports installed on this system.
        </td>
      </tr>
    </ccp:evaluate>
    <%
      int row = 0;
      Iterator i = reportList.iterator();
      while (i.hasNext()) {
        Report thisReport = (Report) i.next();
        row = row != 1 ? 1 : 2;
    %>
      <tr class="row<%= row %>">
        <td>
          <a href="<%= ctx %>/Reports.do?command=Criteria&reportId=<%= thisReport.getId() %>"><%= toHtml(thisReport.getTitle()) %></a>
        </td>
      </tr>
    <%
      }
    %>
  </tbody>
</table>