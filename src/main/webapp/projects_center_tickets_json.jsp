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
<%@ page import="java.util.Iterator" %>
<%@ page import="com.concursive.connect.web.modules.issues.dao.Ticket" %>
<%@ page import="com.concursive.commons.text.StringUtils" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="projectTicketsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="ticketList" class="com.concursive.connect.web.modules.issues.dao.TicketList" scope="request"/>
<ccp:debug value='<%= "Records:" + ticketList.size() %>'/>
{
  "recordsReturned":<%= ticketList.size() %>,
  "startIndex":<%= projectTicketsInfo.getCurrentOffset() %>,
  "totalRecords":<%= projectTicketsInfo.getMaxRecords() %>,
  "sort":"<%= projectTicketsInfo.getColumnToSortBy() %>",
  "dir":<%= projectTicketsInfo.getSortOrder() == null ? "null" : "\"asc\"" %>,
  "records":[
<%
  Iterator i = ticketList.iterator();
  while (i.hasNext()) {
    Ticket thisTicket = (Ticket) i.next();
%>
     {"action":<%= thisTicket.getId() %>,
      "id":<%= thisTicket.getId() %>,
      "ticketCount":<%= thisTicket.getProjectTicketCount() %>,
      "category":"<%= StringUtils.jsStringEscape(thisTicket.getCategoryName()) %>",
      "issue":"<%= StringUtils.jsStringEscape(thisTicket.getProblemHeader()) %>",
      "ticpri":"<%= StringUtils.jsStringEscape(thisTicket.getPriorityName()) %>",
      "assignedTo":"<ccp:username id="<%= thisTicket.getAssignedTo() %>" showPresence="false"/>",
      "age":"<%= thisTicket.getAgeOf() %>",
      "closed":
<% if (thisTicket.getClosed() == null) { %>
      <ccp:evaluate if="<%= !thisTicket.getReadyForClose() %>">
      "<ccp:label name="projectsCenterTickets.open">open</ccp:label>"
    </ccp:evaluate>
    <ccp:evaluate if="<%= thisTicket.getReadyForClose() %>">
      "<ccp:label name="projectsCenterTickets.forReview">for review</ccp:label>"
    </ccp:evaluate>
<%} else {%>
      "<ccp:label name="projectsCenterTickets.closed">closed</ccp:label>"
<%}%>
}
<ccp:evaluate if="<%= i.hasNext() %>">,</ccp:evaluate>
<%
  }
%>
  ]
}
