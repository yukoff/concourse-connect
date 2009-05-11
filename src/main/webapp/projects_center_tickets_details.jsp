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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.web.modules.issues.dao.TicketLog" %>
<%@ page import="com.concursive.connect.web.modules.issues.dao.TicketContact" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="projectTicketsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="ticket" class="com.concursive.connect.web.modules.issues.dao.Ticket" scope="request"/>
<jsp:useBean id="ticketList" class="com.concursive.connect.web.modules.issues.dao.TicketList" scope="request"/>
<jsp:useBean id="distributionList" class="com.concursive.connect.web.modules.issues.dao.TicketContactList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_tickets_files_menu.jspf" %>
<%-- Preload image rollovers for drop-down menu --%>
<script type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<script language="JavaScript" type="text/javascript">
  function changeWatch(ticketId, value) {
    // send ticketId and true/false
    window.frames['server_commands'].location.href=
      "<%= ctx %>/ProjectManagementTickets.do?command=ChangeWatch&ticketId=" + ticketId + "&value=" + value;
  }
  function watchAddOK(ticketId) {
    hideSpan("watchSpan");
    hideSpan("watchSpan2");
    showSpan("cancelSpan");
    showSpan("cancelSpan2");
  }
  function watchCancelOK(ticketId) {
    hideSpan("cancelSpan");
    hideSpan("cancelSpan2");
    showSpan("watchSpan");
    showSpan("watchSpan2");
  }
</script>
<div class="portletWrapper">
  <div class="profile-portlet-header">
    <h1>
      <%--<img src="<%= ctx %>/images/icons/stock_macro-organizer-16.gif" border="0" align="absmiddle" />--%>
      <%-- Ticket Header --%>
      <%--<ccp:pagedListStatus object="projectTicketsInfo">--%>
      <ccp:label name="projectsCenterTickets.details.ticket">Ticket</ccp:label> # <%= ticket.getProjectTicketCount() %>
      <ccp:label name="projectsCenterTickets.details.details">Details</ccp:label>
    </h1>
  </div>
  <div class="profile-portlet-menu">
    <ul>
      <li>
        <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/issues">
          <ccp:label name="projectsCenterTickets.config.tickets">Back to All Tickets</ccp:label>
        </a>
      </li>
      <ccp:evaluate if="<%= ticket.isClosed() %>">
        <li>
          <img src="${ctx}/images/icons/exclamation.png" alt="Alert icon" />
          <ccp:label name="projectsCenterTickets.detals.ticketClosedOn">This ticket was closed on</ccp:label> <ccp:tz timestamp="<%= ticket.getClosed() %>"/>
        </li>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !ticket.isClosed() %>">
        <ccp:evaluate if="<%= !ticket.getReadyForClose() %>">
          <li>
            <img src="${ctx}/images/icons/exclamation.png" alt="Alert icon" />
            <ccp:label name="projectsCenterTickets.details.open">Open</ccp:label>
          </li>
        </ccp:evaluate>
        <ccp:evaluate if="<%= ticket.getReadyForClose() %>">
          <li>
            <img src="${ctx}/images/icons/exclamation.png" alt="Alert icon" />
            <ccp:label name="projectsCenterTickets.details.readyForReview">Ready for review</ccp:label>
          </li>
        </ccp:evaluate>
      </ccp:evaluate>
      <ccp:evaluate if="<%= User.getId() > -1 %>">
        <li class="last">
          <span id="watchSpan" <ccp:evaluate if="<%= distributionList.hasUserId(User.getId()) %>">style="display:none"</ccp:evaluate>>
            <img border="0" src="<%= ctx %>/images/icons/stock_macro-watch-variable-16.gif" align="absmiddle" />
            <a href="javascript:changeWatch(<%= ticket.getId() %>,'true');"><ccp:label name="projectsCenterTickets.details.watchByEmail">Watch this ticket by email</ccp:label></a>
          </span>
          <span id="cancelSpan" <ccp:evaluate if="<%= !distributionList.hasUserId(User.getId()) %>">style="display:none"</ccp:evaluate>>
            <img border="0" src="<%= ctx %>/images/icons/stock_macro-stop-watching-16.gif" align="absmiddle" />
            <a href="javascript:changeWatch(<%= ticket.getId() %>,'false');"><ccp:label name="projectsCenterTickets.detals.cancelWatchByEmail">Cancel watching this ticket by email</ccp:label></a>
          </span>
        </li>
      </ccp:evaluate>
    </ul>
    <%--</ccp:pagedListStatus>--%>

  </div>
  <div class="profile-portlet-body">
    <form name="details" action="<%= ctx %>/ProjectManagementTickets.do?command=Modify&pid=<%= project.getId() %>&id=<%= ticket.getId() %>" method="post">
      <%--<% if (ticket.getClosed() != null) { %>
        <ccp:permission name="project-tickets-close">
          <input type="button" class="submit" value="<ccp:label name="button.reopen">Re-open</ccp:label>" onClick="confirmForward('<%= ctx %>/ProjectManagementTickets.do?command=Reopen&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>');">
        </ccp:permission>
        <%} else {%>--%>
        <%-- allow user to edit own assigned ticket, and allow anyone to add comments --%>
        <%--<ccp:permission name="project-tickets-edit">
          <input type="button" class="submit" value="<ccp:label name="button.edit">Edit</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=Modify&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
          <input type="button" class="submit" value="<ccp:label name="button.addComments">Add Comments</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=AddComments&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
        </ccp:permission>
        <ccp:permission name="project-tickets-edit" if="none">
          <ccp:evaluate if="<%= ticket.getAssignedTo() == User.getId() %>">
            <input type="button" class="submit" value="<ccp:label name="button.edit">Edit</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=Modify&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
            <input type="button" class="submit" value="<ccp:label name="button.addComments">Add Comments</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=AddComments&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
          </ccp:evaluate>
          <ccp:evaluate if="<%= ticket.getAssignedTo() != User.getId() %>">
            <ccp:evaluate if="<%= ticket.getEnteredBy() == User.getId() %>">
              <input type="button" class="submit" value="<ccp:label name="button.addComments">Add Comments</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=AddComments&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
            </ccp:evaluate>
          </ccp:evaluate>
        </ccp:permission>
        <ccp:permission name="project-tickets-delete">
          <input type="button" class="submit" value="<ccp:label name="button.delete">Delete</ccp:label>" onClick="confirmDelete('<%= ctx %>/ProjectManagementTickets.do?command=Delete&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>');">
        </ccp:permission>
      <%}%>
      --%>

	  
    <%-- Watch Information (2)
   
    <ccp:permission name="project-tickets-edit,project-tickets-delete" if="any">
    <br />
    <br />
    </ccp:permission>

     --%>
    
    <%-- Record information --%>
    <table class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
          <ccp:label name="projectsCenterTickets.details.ticketInformation">Ticket Information</ccp:label>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr class="containerBody">
          <td class="formLabel">
            <ccp:label name="projectsCenterTickets.details.entered">Entered</ccp:label>
          </td>
          <td>
            <ccp:username id="<%= ticket.getEnteredBy() %>"/>
            -
            <ccp:tz timestamp="<%= ticket.getEntered() %>"/>
          </td>
        </tr>
        <tr class="containerBody">
          <td nowrap class="formLabel">
            <ccp:label name="projectsCenterTickets.details.lastModified">Last Modified</ccp:label>
          </td>
          <td>
            <ccp:username id="<%= ticket.getModifiedBy() %>"/>
            -
            <ccp:tz timestamp="<%= ticket.getModified() %>" />
          </td>
        </tr>
        <ccp:evaluate if="<%= ticket.isClosed() %>">
          <tr class="containerBody">
            <td class="formLabel">
              <ccp:label name="projectsCenterTickets.details.closedDate">Closed Date</ccp:label>
            </td>
            <td>
              <ccp:tz timestamp="<%= ticket.getClosed() %>" default="&nbsp;"/>
            </td>
          </tr>
        </ccp:evaluate>
        <ccp:evaluate if="<%= ticket.getFiles().size() > 0 %>">
          <tr class="containerBody">
            <td class="formLabel">
              <ccp:label name="projectsCenterTickets.details.attachments">Attachments</ccp:label>
            </td>
            <td>
              <a href="#attachments"><%= ticket.getFiles().size() %> file(s)</a>
            </td>
          </tr>
        </ccp:evaluate>
      </tbody>
    </table>
    <%-- Details --%>
    <table class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
            <ccp:label name="projectsCenterTickets.details.classification">Classification</ccp:label>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr class="containerBody">
          <td class="formLabel" valign="top">
            <ccp:label name="projectsCenterTickets.details.issue">Issue</ccp:label>
          </td>
          <td valign="top">
            <%= toHtml(ticket.getProblem()) %>
          </td>
        </tr>
        <ccp:evaluate if="<%= ticket.getLinkItemId() != -1 && ticket.getLinkProjectId() != -1 && ticket.getLinkModuleId() != -1%>">
        <tr class="containerBody">
          <td class="formLabel" valign="top">
            <ccp:label name="projectsCenterTickets.details.aboutThis">About this</ccp:label>
          </td>
          <td valign="top">
           <c:choose>
             <c:when test="${ticket.foundTicketLinkObject eq true}">
                <a href="${ctx}/show/${ticket.linkProject.uniqueId}/${ticket.itemLink}">${ticket.itemLabel}</a>
              </c:when>
              <c:otherwise>
                ${ticket.itemLabel}
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        </ccp:evaluate>
        <tr class="containerBody">
          <td class="formLabel">
            <ccp:label name="projectsCenterTickets.details.category">Category</ccp:label>
          </td>
          <td>
            <%= toHtml(ticket.getCategoryName()) %>
            <ccp:evaluate if="<%= hasText(ticket.getSubCategoryName1()) %>"> -&gt; <%= toHtml(ticket.getSubCategoryName1()) %></ccp:evaluate>
            <ccp:evaluate if="<%= hasText(ticket.getSubCategoryName2()) %>"> -&gt; <%= toHtml(ticket.getSubCategoryName2()) %></ccp:evaluate>
            <ccp:evaluate if="<%= hasText(ticket.getSubCategoryName3()) %>"> -&gt; <%= toHtml(ticket.getSubCategoryName3()) %></ccp:evaluate>
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            <ccp:label name="projectsCenterTickets.details.severity">Severity</ccp:label>
          </td>
          <td>
            <%= toHtml(ticket.getSeverityName()) %>
          </td>
        </tr>
      </tbody>
    </table>
    <table class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
            <ccp:label name="projectsCenterTickets.details.assignment">Assignment</ccp:label>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr class="containerBody">
          <td class="formLabel">
            <ccp:label name="projectsCenterTickets.details.priority">Priority</ccp:label>
          </td>
          <td>
            <%= toHtml(ticket.getPriorityName()) %>
          </td>
        </tr>
      <%--
        <tr class="containerBody">
          <td class="formLabel">
            Department
          </td>
          <td>
            <%= toHtml(ticket.getDepartmentName()) %>
          </td>
        </tr>
      --%>
        <tr class="containerBody">
          <td class="formLabel">
            <ccp:label name="projectsCenterTickets.details.assignedTo">Assigned To</ccp:label>
          </td>
          <td>
            <ccp:username id="<%= ticket.getAssignedTo() %>"/>
            <ccp:evaluate if="<%= !(ticket.getHasEnabledOwnerAccount()) %>"><font color="red">*</font></ccp:evaluate>
          </td>
        </tr>
        <ccp:evaluate if="<%= ticket.getEstimatedResolutionDate() != null %>">
          <tr class="containerBody">
            <td class="formLabel">
              <ccp:label name="projectsCenterTickets.details.estimatedResolutionDate">Estimated Resolution Date</ccp:label>
            </td>
            <td>
              <ccp:tz timestamp="<%= ticket.getEstimatedResolutionDate() %>" default="&nbsp;"/>
            </td>
          </tr>
        </ccp:evaluate>
      </tbody>
    </table>
    <table class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
            <ccp:label name="projectsCenterTickets.details.resolution">Resolution</ccp:label>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr class="containerBody">
          <td class="formLabel" valign="top">
            <ccp:label name="projectsCenterTickets.details.solution">Solution</ccp:label>
          </td>
          <td>
            <%= toHtml(ticket.getSolution()) %>
          </td>
        </tr>
      </tbody>
    </table>
    <%-- File listing --%>
    <a name="attachments"> </a>
    <table class="pagedList">
      <thead>
        <tr>
          <th><ccp:label name="projectsCenterTickets.details.fileAttachments">File Attachments</ccp:label></th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td class="file-list">
            <ccp:evaluate if="<%= ticket.getFiles().size() == 0 %>">
                <ccp:label name="projectsCenterTickets.details.noFilesAttached">No files attached.</ccp:label>
            </ccp:evaluate>
            <ccp:evaluate if="<%= ticket.getFiles().size() > 0 %>">
            <div class="portlet-section">
              <ol>
                <%
                  int rowid = 0;
                  Iterator i = ticket.getFiles().iterator();
                  while (i.hasNext()) {
                    rowid = (rowid != 1?1:2);
                    FileItem thisFile = (FileItem)i.next();
                %>
                  <li>
                    <div class="portlet-section-body">
                      <%= thisFile.getImageTag("-23", ctx) %>
                      <h3>
                        <a href="<%= ctx %>/ProjectManagementTickets.do?command=FileDownload&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&fid=<%= thisFile.getId() %><%= thisFile.isImageFormat() ? "&view=true&ext=" + thisFile.getExtension() : "" %>"
                          <ccp:evaluate if="<%= thisFile.isImageFormat() %>"> rel="shadowbox[Images]"</ccp:evaluate>><%= toHtml(thisFile.getSubject()) %>
                        </a>
                      </h3>
                      <dl>
                        <dt>
                          Type:
                        </dt>
                        <dd>
                          <%= toHtml(thisFile.getExtension()) %>
                        </dd>
                        <dt>
                          Size:
                        </dt>
                        <dd>
                          <%= thisFile.getRelativeSize() %>KB
                        </dd>
                        <dt>
                          Date Added:
                        </dt>
                        <dd>
                          <ccp:tz timestamp="<%= thisFile.getModified() %>"/>
                        </dd>
                        <dt>
                          Added by:
                        </dt>
                        <dd>
                          <ccp:username id="<%= thisFile.getModifiedBy() %>"/>
                        </dd>
                      </dl>
                    </div>
                    <div class="portlet-section-menu">
                      <div class="portlet-section-body-menu">
                        <p>
                          <%-- Download --%>
                          <a href="${downloadUrl}">
                            <img src="${ctx}/images/icons/get_32x32.png" alt="Download icon"/>
                            <a href="<%= ctx %>/ProjectManagementTickets.do?command=FileDownload&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&fid=<%= thisFile.getId()%>">
                              download
                              <span><%= thisFile.getRelativeSize() %>KB&nbsp;</span>
                          </a>
                        </p>
                      </div>
                      <ul>
                        <ccp:evaluate if="<%= thisFile.isImageFormat() %>">
                          <li>
                            <a href="<%= ctx %>/ProjectManagementTickets.do?command=FileDownload&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&fid=<%= thisFile.getId() %><%= thisFile.isImageFormat() ? "&view=true&ext=" + thisFile.getExtension() : "" %>"
                               rel="shadowbox[Images]">
                              <img src="<%= ctx %>/images/icons/magnifier_search.png" alt="Zoom icon"/>
                              preview
                            </a>
                          </li>
                        </ccp:evaluate>
                        <li>
                          <a href="<%= ctx %>/ProjectManagementTickets.do?command=FileDelete&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&fid=<%= thisFile.getId()%>">
                            <img src="${ctx}/images/icons/minus_circle.png" alt="Delete document icon"/>
                            delete File
                          </a>
                        </li>
                      </ul>
                    </div>
                  </li>
                <%
                   }
                %>
              </ol>
            </ccp:evaluate>
          </td>
        </tr>
      </tbody>
    </table>
    <br />

    <%-- Ticket history --%>
    <table class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
            <ccp:label name="projectsCenterTickets.details.ticketLogHistory">Ticket Log History</ccp:label>
          </th>
        </tr>
      </thead>
      <tbody>
        <%
            Iterator hist = ticket.getHistory().iterator();
            if (hist.hasNext()) {
              while (hist.hasNext()) {
                TicketLog thisEntry = (TicketLog) hist.next();
        %>
          <% if (thisEntry.getSystemMessage()) {%>
            <tr class="containerBody">
              <% } else { %>
            <tr class="containerBody">
              <%}%>
              <td nowrap valign="top" align="right">
                <ccp:username id="<%= thisEntry.getEnteredBy() %>"/>
                <ccp:tz timestamp="<%= thisEntry.getEntered() %>"/>
              </td>
              <td valign="top" width="100%">
                <%= toHtml(thisEntry.getEntryText()) %>
              </td>
            </tr>
          <%
              }
            } else {
          %>
            <tr class="containerBody">
              <td>
                <font color="#9E9E9E" colspan="2">No Log Entries.</font>
              </td>
            </tr>
          <%}%>
      </tbody>
    </table>
    <%-- Distribution List --%>
    <table class="pagedList">
      <thead>
        <tr>
          <th width="100%"><ccp:label name="projectsCenterTickets.details.distributionList">Distribution List</ccp:label></th>
        </tr>
      </thead>
      <tbody>
        <tr class="row2">
          <td>
            <ccp:evaluate if="<%= distributionList.size() == 0 %>">
               <ccp:label name="projectsCenterTickets.details.noRecipients">No recipients.</ccp:label>
            </ccp:evaluate>
            <%
              int dCount = 30;
              if (distributionList.size() > 30) {
                dCount = 25;
              }
              Iterator iD = distributionList.iterator();
              while (iD.hasNext() && dCount > 0) {
                TicketContact ticketContact = (TicketContact) iD.next();
                --dCount;
            %>
              <%= toHtml(ticketContact.getContactName()) %><ccp:evaluate if="<%= iD.hasNext() %>">, </ccp:evaluate>
            <%
              }
            %>
            <ccp:evaluate if="<%= distributionList.size() > 30 %>"> and <%= distributionList.size() - 25 %> others...</ccp:evaluate>
          </td>
        </tr>
      </tbody>
    </table>
    <% if (ticket.getClosed() != null) { %>
    <ccp:permission name="project-tickets-close">
      <input type="button" class="submit" value="<ccp:label name="button.reopen">Re-open</ccp:label>" onClick="confirmForward('<%= ctx %>/ProjectManagementTickets.do?command=Reopen&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>');">
    </ccp:permission>
    <%} else {%>
    <%-- allow user to edit own assigned ticket, and allow anyone to add comments --%>
    <ccp:permission name="project-tickets-edit">
      <input type="button" class="submit" value="<ccp:label name="button.edit">Edit</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=Modify&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
      <input type="button" class="submit" value="<ccp:label name="button.addComments">Add Comments</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=AddComments&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
    </ccp:permission>
    <ccp:permission name="project-tickets-edit" if="none">
      <ccp:evaluate if="<%= ticket.getAssignedTo() == User.getId() %>">
        <input type="button" class="submit" value="<ccp:label name="button.edit">Edit</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=Modify&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
        <input type="button" class="submit" value="<ccp:label name="button.addComments">Add Comments</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=AddComments&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
      </ccp:evaluate>
      <ccp:evaluate if="<%= ticket.getAssignedTo() != User.getId() %>">
        <ccp:evaluate if="<%= ticket.getEnteredBy() == User.getId() %>">
          <input type="button" class="submit" value="<ccp:label name="button.addComments">Add Comments</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagementTickets.do?command=AddComments&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>'">
        </ccp:evaluate>
      </ccp:evaluate>
    </ccp:permission>
    <ccp:permission name="project-tickets-delete">
      <input type="button" class="cancel" value="<ccp:label name="button.delete">Delete</ccp:label>" onClick="confirmDelete('<%= ctx %>/ProjectManagementTickets.do?command=Delete&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= request.getParameter("return") %>');">
    </ccp:permission>
    <%}%>
    <%-- Watch Information (2) --%>
    <ccp:evaluate if="<%= User.getId() > -1 %>">
      <span id="watchSpan2" <ccp:evaluate if="<%= distributionList.hasUserId(User.getId()) %>">style="display:none"</ccp:evaluate>>
        <img border="0" src="<%= ctx %>/images/icons/stock_macro-watch-variable-16.gif" align="absmiddle" />
        <a href="javascript:changeWatch(<%= ticket.getId() %>,'true');"><ccp:label name="projectsCenterTickets.details.watchByEmail">Watch this ticket by email</ccp:label></a>
      </span>
      <span id="cancelSpan2" <ccp:evaluate if="<%= !distributionList.hasUserId(User.getId()) %>">style="display:none"</ccp:evaluate>>
        <img border="0" src="<%= ctx %>/images/icons/stock_macro-stop-watching-16.gif" align="absmiddle" />
        <a href="javascript:changeWatch(<%= ticket.getId() %>,'false');"><ccp:label name="projectsCenterTickets.detals.cancelWatchByEmail">Cancel watching this ticket by email</ccp:label></a>
      </span>
    </ccp:evaluate>
    </form>
  </div>
  <iframe src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/empty.html" name="server_commands" id="server_commands" style="visibility:hidden" height="0"></iframe>
</div>