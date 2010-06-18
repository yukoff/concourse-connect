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
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember"%>
<%@ page import="com.concursive.connect.web.modules.issues.dao.Ticket" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="ticketList" class="com.concursive.connect.web.modules.issues.dao.TicketList" scope="request"/>
<jsp:useBean id="ticketCategoryList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="projectTicketsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_tickets_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<div class="portletWrapper">
  <%--<div class="profile-portlet-header">--%>
    <%--<h1><ccp:label name="projectsCenterTickets.tickets">Tickets</ccp:label></h1>--%>
  <%--</div>--%>
  <%
  String actionError = showError(request, "actionError");
  %>
  <div class="profile-portlet-menu">
    <ul>
      <li class="first">
        <form name="ticketView" method="get" action="<%= ctx %>/show/<%= project.getUniqueId() %>/issues">
          <select name="listView" onChange="document.forms['ticketView'].submit();">
            <option <%= projectTicketsInfo.getOptionValue("open") %>><ccp:label name="projectsCenterTickets.openTickets">Open Tickets</ccp:label></option>
            <option <%= projectTicketsInfo.getOptionValue("review") %>><ccp:label name="projectsCenterTickets.ticketsForReview">Tickets for review</ccp:label></option>
            <option <%= projectTicketsInfo.getOptionValue("closed") %>><ccp:label name="projectsCenterTickets.closedTickets">Closed Tickets</ccp:label></option>
            <option <%= projectTicketsInfo.getOptionValue("all") %>><ccp:label name="projectsCenterTickets.allTickets">All Tickets</ccp:label></option>
          </select>
          <ccp:evaluate if="<%= ticketCategoryList.size() > 1 %>">
            <% ticketCategoryList.setJsEvent("onChange=\"javascript:document.forms['ticketView'].submit();\""); %>
            <%= ticketCategoryList.getHtml("listFilter1", projectTicketsInfo.getFilterValue("listFilter1")) %>
          </ccp:evaluate>
        </form>
      </li>
      <ccp:permission name="project-tickets-add">
        <ccp:permission name="project-setup-customize">
          <li>
            <a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=Options&pid=<%= project.getId() %>">
              <ccp:label name="projectsCenterTickets.configuration">Configuration</ccp:label>
            </a>
          </li>
        </ccp:permission>
        <li class="last">
          <a href="<%= ctx %>/ProjectManagementTickets.do?command=Add&pid=<%= project.getId() %>">
            <img src="${ctx}/images/icons/ticket_plus.png" alt="New ticket icon" />
            <ccp:label name="projectsCenterTickets.newTicket">New Ticket</ccp:label>
          </a>
        </li>
      </ccp:permission>

    </ul>
  </div>
  <div class="profile-portlet-body">
    <table class="pagedList">
      <thead>
      <tr>
        <th>
          <a href="<%= projectTicketsInfo.addParameter(projectTicketsInfo.getLink(), "column", "t.entered") %>"><ccp:label name="projectsCenterTickets.id">Id</ccp:label></a>
          <%= projectTicketsInfo.getSortIcon("t.entered") %>
        </th>
        <th>
          <a href="<%= projectTicketsInfo.addParameter(projectTicketsInfo.getLink(), "column", "closed") %>"><ccp:label name="projectsCenterTickets.status">Status</ccp:label></a>
          <%= projectTicketsInfo.getSortIcon("closed") %>
        </th>
        <th>
          <a href="<%= projectTicketsInfo.addParameter(projectTicketsInfo.getLink(), "column", "catname,subcatname1,subcatname2") %>"><ccp:label name="projectsCenterTickets.category">Category</ccp:label></a>
          <%= projectTicketsInfo.getSortIcon("catname,subcatname1,subcatname2") %>
        </th>
        <th>
          <ccp:label name="projectsCenterTickets.issue">Issue</ccp:label>
          <%= projectTicketsInfo.getSortIcon("problem") %>
        </th>
        <th>
          <a href="<%= projectTicketsInfo.addParameter(projectTicketsInfo.getLink(), "column", "ticpri") %>"><ccp:label name="projectsCenterTickets.priority">Priority</ccp:label></a>
          <%= projectTicketsInfo.getSortIcon("ticpri") %>
        </th>
        <th>
          <ccp:label name="projectsCenterTickets.assignedTo">Assigned To</ccp:label>
        </th>
        <th>
          Age
        </th>
        <th>
          <a href="<%= projectTicketsInfo.addParameter(projectTicketsInfo.getLink(), "column", "t.modified") %>">Last Modified</a>
          <%= projectTicketsInfo.getSortIcon("t.modified") %>
        </th>
      </tr>
    </thead>
  <tfoot />
    <tbody>
    <ccp:evaluate if="<%= ticketList.size() == 0 %>">
      <tr class="row2">
        <td colspan="9"><ccp:label name="projectsCenterTickets.noTickets">No tickets to display.</ccp:label></td>
      </tr>
    </ccp:evaluate>
    <%
      Iterator i = ticketList.iterator();
      int count = 0;
      int rowid = 0;
      int offset = (projectTicketsInfo.getCurrentOffset() - 1);
      while (i.hasNext()) {
        ++count;
        ++offset;
        rowid = (rowid != 1?1:2);
        Ticket thisTicket = (Ticket) i.next();
    %>
      <tr class="row<%= rowid %>">
        <th>
          <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/issue/<%= thisTicket.getProjectTicketCount() %>"><%= thisTicket.getProjectTicketCount() %></a>
        </th>
        <td>
    <% if (thisTicket.getClosed() == null) { %>
          <ccp:evaluate if="<%= !thisTicket.getReadyForClose() %>">
          <span class="open"><ccp:label name="projectsCenterTickets.open">open</ccp:label></span>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisTicket.getReadyForClose() %>">
          <span class="review"><ccp:label name="projectsCenterTickets.forReview">for review</ccp:label></span>
        </ccp:evaluate>
    <%} else {%>
          <span class="closed"><ccp:label name="projectsCenterTickets.closed">closed</ccp:label></span>
    <%}%>
        </td>

        <td>
          <%= toHtml(thisTicket.getCategoryName()) %>
        </td>
        <td>
          <%= toHtml(thisTicket.getProblemHeader()) %>
        </td>
        <td>
          <%= toHtml(thisTicket.getPriorityName()) %>
        </td>
        <td>
          <ccp:username id="<%= thisTicket.getAssignedTo() %>" />&nbsp;
        </td>
        <td align="right" nowrap>
          <%= thisTicket.getAgeOf() %>
        </td>
        <td>
    <% if (thisTicket.getClosed() == null) { %>
          <ccp:tz timestamp="<%= thisTicket.getModified() %>"/>
    <%} else {%>
          <ccp:tz timestamp="<%= thisTicket.getClosed() %>"/>
    <%}%>
        </td>
      </tr>
    <%}%>
    </tbody>
  </table>
  </div>
  <c:if test="${projectClassifiedsInfo.numberOfPages > 1}">
    <ccp:paginationControl object="projectTicketsInfo"/>
  </c:if>
</div>