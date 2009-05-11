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
<%@ page import="com.concursive.connect.web.modules.plans.dao.Assignment" %>
<%@ page import="com.concursive.connect.web.modules.issues.dao.Ticket" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="assignmentsListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="assignmentList" class="com.concursive.connect.web.modules.plans.dao.AssignmentList" scope="request"/>
<jsp:useBean id="requirementList" class="com.concursive.connect.web.modules.plans.dao.RequirementList" scope="request"/>
<jsp:useBean id="assignmentsAssignmentListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="ticketList" class="com.concursive.connect.web.modules.issues.dao.TicketList" scope="request"/>
<jsp:useBean id="assignmentsTicketListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="initPage.jsp" %>
<ccp:evaluate if="<%= !clientType.getMobile() %>">
<table class="note" cellspacing="0">
<tr>
  <th>
    <img src="<%= ctx %>/images/icons/stock_form-open-in-design-mode-16.gif" border="0" align="absmiddle" />
  </th>
  <td>
    <ccp:label name="projectsAssignments.title">The following items are assigned from each of your projects.</ccp:label>
  </td>
</tr>
</table>
</ccp:evaluate>
<% int rowid = 0; %>

<table cellpadding="0" cellspacing="0" width="100%" border="0">
<tr>
<td valign="top">

<%-- Start assignments --%>
<ccp:evaluate if="<%= !assignmentsTicketListInfo.getExpandedSelection() %>">
<ccp:pagedListStatus tableClass="pagedListTab" showExpandLink="true" title="Activities" object="assignmentsAssignmentListInfo"/>
<table cellpadding="4" cellspacing="0" width="100%">
  <ccp:evaluate if="<%= assignmentList.isEmpty() %>">
    <tr>
      <td>
        <ccp:label name="projectsAssignments.noAssignmentsFound">No Assignments found.</ccp:label>
      </td>
    </tr>
  </ccp:evaluate>
<%
    rowid = 0;
    Iterator assignmentIterator = assignmentList.iterator();
    while (assignmentIterator.hasNext()) {
      rowid = (rowid != 1?1:2);
      Assignment thisAssignment = (Assignment) assignmentIterator.next();
%>
    <tr class="overviewrow<%= rowid %>">
      <td width="90%">
        <table border="0" cellpadding="2" cellspacing="0">
          <tr>
            <td valign="top">
              <%= thisAssignment.getStatusGraphicTag(ctx) %>
            </td>
            <td>
              <a href="javascript:popURL('<%= ctx %>/ProjectManagementAssignments.do?command=Modify&pid=<%= thisAssignment.getProjectId() %>&aid=<%= thisAssignment.getId() %>&popup=true','ITEAM_Activity','650','600','yes','yes');"><%= toHtml(thisAssignment.getRole()) %></a>
              (<a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Assignments&rid=<%= thisAssignment.getRequirementId() %>&pid=<%= thisAssignment.getProjectId() %>"><%= toHtml(requirementList.getRequirement(thisAssignment.getRequirementId())) %></a>)
              <ccp:evaluate if="<%= thisAssignment.hasNotes() %>">
                <a href="javascript:popURL('<%= ctx %>/ProjectManagementAssignments.do?command=ShowNotes&pid=<%= thisAssignment.getProjectId() %>&aid=<%= thisAssignment.getId() %>&popup=true','ITEAM_Assignment_Notes','400','500','yes','yes');"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absmiddle" alt="Review all notes"/></a>
                <%= thisAssignment.getNoteCount() %>
              </ccp:evaluate>
              <br />
              <a class="searchLink" href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&pid=<%= thisAssignment.getProjectId() %>&section=Requirements"><ccp:project id="<%= thisAssignment.getProjectId() %>"/></a>
            </td>
          </tr>
        </table>
      </td>
      <td colspan="2" width="10%" nowrap>
        <%= thisAssignment.getRelativeDueDateString(User.getTimeZone(), User.getLocale()) %>
      </td>
    </tr>
<%
    }
%>
</table>
<br />
</ccp:evaluate>

<ccp:evaluate if="<%= !clientType.getMobile() %>">
</td>
<%-- gutter --%>
<td width="8" nowrap>&nbsp;</td>
<%-- 2nd column --%>
<td valign="top">
</ccp:evaluate>

<%-- Start tickets --%>
<ccp:evaluate if="<%= !assignmentsAssignmentListInfo.getExpandedSelection() %>">
<ccp:pagedListStatus tableClass="pagedListTab" showExpandLink="true" title="Tickets" object="assignmentsTicketListInfo"/>
<table cellpadding="4" cellspacing="0" width="100%">
  <ccp:evaluate if="<%= ticketList.isEmpty() %>">
    <tr>
      <td>
        <ccp:label name="projectsAssignments.noTicketsFound">No Tickets found.</ccp:label>
      </td>
    </tr>
  </ccp:evaluate>
<%
    rowid = 0;
    Iterator ticketIterator = ticketList.iterator();
    while (ticketIterator.hasNext()) {
      rowid = (rowid != 1?1:2);
      Ticket thisTicket = (Ticket) ticketIterator.next();
%>
    <tr class="overviewrow<%= rowid %>">
      <td width="90%">
        <table border="0" cellpadding="2" cellspacing="0">
          <tr>
            <td valign="top">
              <img src="<%= ctx %>/images/icons/stock_macro-organizer-16.gif" border="0" align="absmiddle" />
            </td>
            <td>
              <a href="<%= ctx %>/ProjectManagementTickets.do?command=Details&pid=<%= thisTicket.getProjectId() %>&id=<%= thisTicket.getId() %>&return=details"><%= toHtml(thisTicket.getProblemHeader()) %></a><br />
              <a class="searchLink" href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&pid=<%= thisTicket.getProjectId() %>&section=Tickets"><ccp:project id="<%= thisTicket.getProjectId() %>"/></a>
            </td>
          </tr>
        </table>
      </td>
      <td colspan="2" width="10%" nowrap><%= toHtml(thisTicket.getAgeOf()) %></td>
    </tr>
<%
    }
%>
</table>
<br />
</ccp:evaluate>

</td>
</tr>
</table>
