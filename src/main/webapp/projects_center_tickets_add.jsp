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
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.connect.web.utils.HtmlSelect" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="teamMemberList" class="com.concursive.connect.web.modules.members.dao.TeamMemberList" scope="request"/>
<jsp:useBean id="ticket" class="com.concursive.connect.web.modules.issues.dao.Ticket" scope="request"/>
<jsp:useBean id="DepartmentList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="SeverityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="SourceList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="PriorityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="CategoryList" class="com.concursive.connect.web.modules.issues.dao.TicketCategoryList" scope="request"/>
<jsp:useBean id="SubList1" class="com.concursive.connect.web.modules.issues.dao.TicketCategoryList" scope="request"/>
<jsp:useBean id="SubList2" class="com.concursive.connect.web.modules.issues.dao.TicketCategoryList" scope="request"/>
<jsp:useBean id="SubList3" class="com.concursive.connect.web.modules.issues.dao.TicketCategoryList" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="TicketCauseList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="TicketResolutionList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="TicketDefectList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="TicketEscalationList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="TicketStateList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="distributionList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="vectorUserId" class="java.lang.String" scope="request"/>
<jsp:useBean id="vectorState" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  distributionList.setSelectSize(10);
  distributionList.setSelectStyle("width: 160px");
  distributionList.setJsEvent("onClick=\"removeList(this.form)\"");
%>
<script language="JavaScript" type="text/javascript">
  var items = "";<%-- Maintains users in the selected category --%>
  var vectorUserId = "<%= vectorUserId %>".split("|");<%-- User ID --%>
  var vectorState = "<%= vectorState %>".split("|");<%-- State --%>
</script>
<script language="JavaScript" type="text/javascript" src="<%= ctx %>/javascript/projects_center_tickets_add.js?1"></script>
<script language="JavaScript" type="text/javascript">
function updateSubList1() {
  var sel = document.forms['ticketForm'].elements['catCode'];
  var value = sel.options[sel.selectedIndex].value;
  var url = "<%= ctx %>/ProjectManagementTickets.do?command=CategoryJSList&projectId=<%= project.getId() %>&categoryId=" + encodeURIComponent(value) + "&nextLevel=1";
  window.frames['server_commands'].location.href=url;
	hideSubCats(1, value);
}
function updateSubList2() {
  var sel = document.forms['ticketForm'].elements['subCat1'];
  var value = sel.options[sel.selectedIndex].value;
  var url = "<%= ctx %>/ProjectManagementTickets.do?command=CategoryJSList&projectId=<%= project.getId() %>&categoryId=" + encodeURIComponent(value) + "&nextLevel=2";
  window.frames['server_commands'].location.href=url;
	hideSubCats(2, value);
}
function updateSubList3() {
  var sel = document.forms['ticketForm'].elements['subCat2'];
  var value = sel.options[sel.selectedIndex].value;
  var url = "<%= ctx %>/ProjectManagementTickets.do?command=CategoryJSList&projectId=<%= project.getId() %>&categoryId=" + encodeURIComponent(value) + "&nextLevel=3";
  window.frames['server_commands'].location.href=url;
	hideSubCats(3, value);
}
function setAttachmentList(newVal) {
  document.getElementById("attachmentList").value = newVal;
}
function setAttachmentText(newVal) {
  document.getElementById("attachmentText").value = newVal;
}
//Hide SubCategories
function hideSubCats(subCatID, value) {
  if (value >= 1) {
	  document.getElementById("cat" + subCatID).style.display="block";
		for (c = subCatID+1; c < 4; c++) {
      if (document.getElementById("cat" + c)) {
        document.getElementById("cat" + c).style.display="none";
      }
    }
	} else {
    for(c = subCatID; c < 4; c++) {
      if (document.getElementById("cat" + c)) {
        document.getElementById("cat" + c).style.display="none";
      }
    }
  }
}
</script>
<body onLoad="document.ticketForm.problem.focus(); hideSubCats(1, 0);">
<form name="ticketForm" action="<%= ctx %>/ProjectManagementTickets.do?command=Save&auto-populate=true" method="post" onSubmit="return checkForm(this)">
<div class="portletWrapper">
  <h1>
    <%--<img src="<%= ctx %>/images/icons/stock_macro-organizer-16.gif" border="0" align="absmiddle" />--%>
    <%= ticket.getId() == -1 ? "Add" : "Update" %> 
    <ccp:evaluate if="<%= ticket.getId() > -1 %>">
    <ccp:label name="projectsCenterTickets.add.ticket">Ticket</ccp:label> #<%= ticket.getProjectTicketCount() %>
      <ccp:evaluate if="<%= ticket.getClosed() != null %>">
        (<span class="closed"><ccp:label name="projectsCenterTickets.add.ticketClosedOn">This ticket was closed on</ccp:label> <ccp:tz timestamp="<%= ticket.getClosed() %>"/></span>)
      </ccp:evaluate>
      <ccp:evaluate if="<%= ticket.getClosed() == null %>">
        <ccp:evaluate if="<%= !ticket.getReadyForClose() %>">
          (<span class="open"><ccp:label name="projectsCenterTickets.add.open">Open</ccp:label></span>)
        </ccp:evaluate>
        <ccp:evaluate if="<%= ticket.getReadyForClose() %>">
          (<span class="open"><ccp:label name="projectsCenterTickets.add.forReview">For review</ccp:label></span>)
        </ccp:evaluate>
      </ccp:evaluate>
    </ccp:evaluate>
    <span><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/issues"><ccp:label name="projectsCenterTickets.add.tickets">Back to Tickets</ccp:label></a></span>
  </h1>
  <%--
	<% if (ticket.getClosed() != null) { %>
  <ccp:permission name="project-tickets-close">
    <input type="button" class="submit" value="<ccp:label name="button.reopen">Re-open</ccp:label>" onClick="confirmForward('<%= ctx %>/ProjectManagementTickets.do?command=Reopen&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= StringUtils.encodeUrl(request.getParameter("return")) %>')">
  </ccp:permission>
  <%} else {%>
    <input type="submit" class="submit" value="<ccp:label name="button.save">Save</ccp:label>">
  <%}%>
  <% if ("list".equals(request.getParameter("return")) || ticket.getId() == -1) {%>
    <input type="button" class="submit" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/issues'">
  <%} else {%>
    <input type="button" class="submit" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/issue/<%= ticket.getProjectTicketCount() %>?return=<%= StringUtils.encodeUrl(request.getParameter("return")) %>'">
  <%}%>
  --%>
  <%= showError(request, "actionError") %>
  <div class="formContainer">
    <%-- Classification --%>
    <fieldset id="classification">
      <legend><ccp:label name="projectsCenterTickets.add.classification">Classification</ccp:label></legend>
      <label for="issue"><ccp:label name="projectsCenterTickets.add.issue">Issue <span class="required">*</span></ccp:label></label>
      <%= showAttribute(request, "problemError") %></span>
      <textarea name="problem" cols="55" rows="8"><%= toString(ticket.getProblem()) %></textarea>
      <ccp:evaluate if="<%= CategoryList.size() > 0 %>">
        <%-- Categories --%>
        <div class="multiColumnContainer">
          <div id="cat0">
            <label for="catCode"><ccp:label name="projectsCenterTickets.add.category">Category</ccp:label></label>
            <%= CategoryList.getHtmlSelect("catCode", ticket.getCatCode()) %>
          </div>
          <div id="cat1">
            <label for="subCat1"><ccp:label name="projectsCenterTickets.add.subcat1">Subcategory 1</ccp:label></label>
            <%= SubList1.getHtmlSelect("subCat1", ticket.getSubCat1()) %>
          </div>
          <div id="cat2">
            <label for="subCat2"><ccp:label name="projectsCenterTickets.add.subcat2">Subcategory 2</ccp:label></label>
            <%= SubList2.getHtmlSelect("subCat2", ticket.getSubCat2()) %>
          </div>
          <div id="cat3">
            <label for="subCat3"><ccp:label name="projectsCenterTickets.add.subcat3">Subcategory 3</ccp:label></label>
            <%= SubList3.getHtmlSelect("subCat3", ticket.getSubCat3()) %>
          </div>
        </div>
      </ccp:evaluate>
      <label for="severityCode"><ccp:label name="projectsCenterTickets.add.severity">Severity</ccp:label></label>
      <%= SeverityList.getHtmlSelect("severityCode", ticket.getSeverityCode()) %>
      <label>Supporting File Attachments</label>
      <%
        Iterator files = ticket.getFiles().iterator();
        while (files.hasNext()) {
          FileItem thisFile = (FileItem)files.next();
      %>
            <%= toHtml(thisFile.getClientFilename()) %><ccp:evaluate if="<%= files.hasNext() %>">;</ccp:evaluate>
      <%
        }
      %>
      <ccp:evaluate if="<%= ticket.getFiles().size() > 0 %>"></ccp:evaluate>
      <img alt="reminder" src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
      <a href="${ctx}/FileAttachments.do?command=ShowForm&pid=<%= project.getId() %>&lmid=<%= Constants.PROJECT_TICKET_FILES %>&liid=<%= ticket.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&popup=true"
         rel="shadowbox" title="Share an attachment">Attach Files</a>
      <input type="hidden" id="attachmentList" name="attachmentList" value="" />
      <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
    </fieldset>
    <%-- Evaluation --%>
    <ccp:permission name="project-tickets-assign,project-tickets-edit" if="any">
      <fieldset id="evaluations">
        <legend><ccp:label name="projectsCenterTickets.add.evaluations">Evaluation</ccp:label></legend>
          <ccp:evaluate if="<%= TicketStateList.size() > 0 %>">
            <label for="stateId"><ccp:label name="projectsCenterTickets.add.state">State</ccp:label></label>
						<%= TicketStateList.getHtmlSelect("stateId", ticket.getStateId(), false) %>
          </ccp:evaluate>
          <ccp:evaluate if="<%= TicketCauseList.size() > 0 %>">
            <label for="causeId"><ccp:label name="projectsCenterTickets.add.cause">Cause</ccp:label></label>
            <%= TicketCauseList.getHtmlSelect("causeId", ticket.getCauseId()) %>
            <%-- edit --%>
          </ccp:evaluate>
          <ccp:evaluate if="<%= TicketDefectList.size() > 0 %>">
            <label for="defectId"><ccp:label name="projectsCenterTickets.add.knownIssue">Known Issue</ccp:label></label>
            <%= TicketDefectList.getHtmlSelect("defectId", ticket.getDefectId()) %>
            <%-- edit --%>
          </ccp:evaluate>
          <label for="relatedId"><ccp:label name="projectsCenterTickets.add.relatedTo">Related To</ccp:label></label>
          <span class="error"><%= showAttribute(request, "relatedIdError") %></span>
          # <input type="text" name="relatedId" size="10"
            <ccp:evaluate if="<%= ticket.getRelatedId() > 0 %>">
               value="<%= ticket.getRelatedId() %>"
            </ccp:evaluate>
          />
      <%--
            <a href="<%= ctx %>/ProjectManagementTickets.do?command=Details&pid=<%= project.getId() %>&num=&popup=true">show related ticket</a>
            <a href="<%= ctx %>/ProjectManagementTickets.do?command=Details&pid=<%= project.getId() %>&num=">go to related ticket</a>
      --%>
      </fieldset>
    </ccp:permission>
    <%-- Assignment --%>
    <ccp:permission name="project-tickets-assign">
      <fieldset id="assigned">
        <legend><ccp:label name="projectsCenterTickets.add.assigned">Assignment</ccp:label></legend>
        <ccp:evaluate if="<%= TicketEscalationList.size() > 0 %>">
          <label for="escalationId"><ccp:label name="projectsCenterTickets.add.escalationLevel">Escalation Level</ccp:label></label>
          <%= TicketEscalationList.getHtmlSelect("escalationId", ticket.getEscalationId()) %>
        </ccp:evaluate>
        <label for="priorityCode"><ccp:label name="projectsCenterTickets.add.priority">Priority</ccp:label></label>
        <%= PriorityList.getHtmlSelect("priorityCode", ticket.getPriorityCode()) %>
        <label for="projectsCenterTickets">
          <ccp:evaluate if="<%= ticket.getId() == -1 %>">
              <ccp:label name="projectsCenterTickets.add.assignedTo">Assign To</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= ticket.getId() > -1 %>">
              <ccp:label name="projectsCenterTickets.add.reassignedTo">Reassign To</ccp:label>
          </ccp:evaluate>
        </label>
				<%
            HtmlSelect team = new HtmlSelect();
            team.addItem(-1, "-- None Selected --");
      
            for (TeamMember thisMember: teamMemberList) {
              team.addItem(thisMember.getUserId(),
                   ((User) thisMember.getUser()).getNameLastFirst());
            }
        %>
        <%= team.getHtml("assignedTo", ticket.getAssignedTo()) %>
        <fieldset>
          <legend><ccp:label name="projectsCenterTickets.add.estimatedResolutionDate">Estimated Resolution Date</ccp:label></legend>
          <input type="text" name="estimatedResolutionDate" id="estimatedResolutionDate" size="10" value="<ccp:tz timestamp="<%= ticket.getEstimatedResolutionDate() %>" dateOnly="true"/>">
          <a href="javascript:popCalendar('ticketForm', 'estimatedResolutionDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle" /></a>
          <ccp:label name="projectsCenterTickets.add.at">at</ccp:label>
          <ccp:timeSelect baseName="estimatedResolutionDate" value="<%= ticket.getEstimatedResolutionDate() %>" timeZone="<%= User.getTimeZone() %>"/>
          <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
        </fieldset>
      </fieldset>
    </ccp:permission>
    <%-- Communication --%>
    <ccp:permission name="project-tickets-assign,project-tickets-edit" if="any">
      <fieldset id="<ccp:label name="projectsCenterTickets.add.communication">Communication</ccp:label>">
        <legend><ccp:label name="projectsCenterTickets.add.communication">Communication</ccp:label></legend>
        <label for="comment"><ccp:label name="projectsCenterTickets.add.addComments">Add Comments</ccp:label></label>
        <textarea name="comment" cols="55" rows="5"><%= toString(ticket.getComment()) %></textarea>
      </fieldset>
    </ccp:permission>
    <%-- Resolution --%>
    <ccp:permission name="project-tickets-edit,project-tickets-assign" if="any">
      <fieldset id="resolution">
        <legend><ccp:label name="projectsCenterTickets.add.resolution">Resolution</ccp:label></legend>
        <ccp:evaluate if="<%= TicketResolutionList.size() > 0 %>">
          <label for="resolutionId"><ccp:label name="projectsCenterTickets.add.category">Category</ccp:label></label>
          <%= TicketResolutionList.getHtmlSelect("resolutionId", ticket.getResolutionId()) %>
          <%-- edit --%>
        </ccp:evaluate>
        <label for="solution"><ccp:label name="projectsCenterTickets.add.solution">Solution</ccp:label></label>
        <textarea name="solution" cols="55" rows="8"><%= toString(ticket.getSolution()) %></textarea>
        <%= showAttribute(request, "readyForClose") %>
        <label for="ticketReadyToClose">
          <input type="checkbox" name="readyForClose" id="ticketReadyToClose" value="ON"<ccp:evaluate if="<%= ticket.getReadyForClose() %>"> checked</ccp:evaluate>>
          <ccp:label name="projectsCenterTicket.add.ticketReadyToClose">Ticket is ready to be closed</ccp:label>
        </label>
        <ccp:permission name="project-tickets-close">
          <label for="closeNow">
            <input type="checkbox" name="closeNow" id="closeNow"> Close ticket <%= showAttribute(request, "closedError") %>
          </label>
        </ccp:permission>
      </fieldset>
    </ccp:permission>
    <%-- Distribution List --%>
    <ccp:permission name="project-tickets-assign,project-tickets-edit" if="any">
      <fieldset id="distributionListContainer">
        <legend><ccp:label name="projectsCenterTickets.add.distributionList">Distribution List</ccp:label></legend>
        <label for="selDirectory"><ccp:label name="projectsCenterTickets.add.addContactFrom">Add a contact from:</ccp:label></label>
        <select name='selDirectory' id='selDirectory' onChange="updateCategory();">
          <option value="">Select An Option</option>
          <ccp:permission name="project-team-view">
            <ccp:evaluate if="<%= project.getFeatures().getShowTeam() %>">
              <option value="this|this|<%= project.getId() %>"><ccp:label name="projectsCenterTickets.add.thisProject">This project</ccp:label></option>
            </ccp:evaluate>
          </ccp:permission>
          <option value="my|open"><ccp:label name="projectsCenterTickets.add.openProjects">Open projects</ccp:label></option>
          <option value="my|closed"><ccp:label name="projectsCenterTickets.add.closedProjects">Closed projects</ccp:label></option>
          <option value="contacts|search"><ccp:label name="projectsCenterTickets.add.contacts">Contacts</ccp:label></option>
          <ccp:permission name="project-tickets-assign,project-tickets-edit" if="any">
            <option value="email|one"><ccp:label name="projectsCenterTickets.add.email">Email address</ccp:label></option>
          </ccp:permission>
        </select>
        <fieldset id="listSpan" style="display:none">
          <legend id="select1SpanDepartment" style="display:none"><ccp:label name="projectsCenterTickets.add.selectDepartment">Select a department:</ccp:label></legend>
          <legend id="select1SpanProject" style="display:none"><ccp:label name="projectsCenterTickets.add.selectProject">Select a project:</ccp:label></legend>
          <select name='selDepartment' id='selDepartment' onChange="updateItemList();">
          </select>
        </fieldset>
        <%-- Only show if permission to --%>
        <fieldset id="emailSpan" style="display:none">
          <legend><ccp:label name="projectsCenterTickets.add.emailOfContactToAdd">Email Address of contact to add:</ccp:label></legend>
          <input type="text" name="email" value="" />
          <input type="button" class="submit" name="<ccp:label name="button.addgt">Add ></ccp:label>" value ="Add >" onClick="addEmail(this.form);" />
        </fieldset>
        <fieldset id="contactSpan" style="display:none" class="leftColumn">
          <legend id="select1SpanContacts"><ccp:label name="projectsCenterTickets.add.searchContacts">Search Contacts:</ccp:label></legend>
          <label for="searchValue">Text to search for:</label>
          <span>(name, org or email)</span>
          <input type="text" name="searchValue" value="" />
          <input type="button" class="submit" name="<ccp:label name="button.search">Search</ccp:label>" value="Search" onClick="searchName(this.form);" />
        </fieldset>
        <fieldset id="emailSpan2" style="display:none">
          &nbsp;
        </fieldset>
        <fieldset id="listSpan2" style="display:none">
          <legend id="select2Span" style="display:none"><ccp:label name="projectsCenterTickets.ass.selectContact">Select a contact:</ccp:label></legend>
          <legend id="select1SpanDepartment" style="display:none"><ccp:label name="projectsCenterTickets.add.selectDepartment">Select a department:</ccp:label></legend>
          <legend id="select2SpanContacts" style="display:none">Search results:</legend>
          <select name='selTotalList' id='selTotalList' onClick="addList(this.form)">
          </select>
        </fieldset>
        <fieldset id="distributionList">
          <legend><ccp:label name="projectsCenterTickets.add.distributionList">Distribution List</ccp:label></legend>
          <%= distributionList.getHtml("selProjectList", 0) %>
          <span><label for="selProjectList"><ccp:label name="projectsCenterTickets.add.clickToRemove">(click contact to remove)</ccp:label></label></span>
          <input type="hidden" name="insertMembers" id="insertMembers">
          <input type="hidden" name="deleteMembers">
        </fieldset>
      </fieldset>
    </ccp:permission>
    <ccp:evaluate if="<%= ticket.getId() == -1 || !distributionList.hasKey(String.valueOf(User.getId())) %>">
      <label for="emailUpdates"><input type="checkbox" name="emailUpdates" value="ON" /> <ccp:label name="projectsCenterTickets.add.emailOnChange">Email me every time this ticket is updated</ccp:label></label>
    </ccp:evaluate>
    <ccp:evaluate if="<%= ticket.getId() > -1 && distributionList.hasKey(String.valueOf(User.getId())) %>">
      <label for="doNotEmailUpdates"><input type="checkbox" name="doNotEmailUpdates" value="ON" /> <ccp:label name="projectsCenterTickets.add.noEmailOnChange">Do not email me when this ticket is updated (currently subscribed)</ccp:label></label>
    </ccp:evaluate>
    <%--
    Next action, after saving:<br />
    <input type="radio" name="nextAction" value="returnToList" checked /> Return to ticket list<br />
    <input type="radio" name="nextAction" value="viewDetails" /> Review saved ticket<br />
    <input type="radio" name="nextAction" value="attachFiles" /> Attach files<br />
    <input type="radio" name="nextAction" value="newTicket" /> Create a new ticket<br />
    <br />
    --%>
    <% if (ticket.getClosed() != null) { %>
      <input type="button" class="submit" value="<ccp:label name="button.reopen">Re-open</ccp:label>" onClick="confirmForward('<%= ctx %>/ProjectManagementTickets.do?command=Reopen&pid=<%= project.getId() %>&id=<%= ticket.getId() %>&return=<%= StringUtils.encodeUrl(request.getParameter("return")) %>')">
    <%} else {%>
      <input type="submit" class="submit" value="<ccp:label name="button.save">Save</ccp:label>">
    <%}%>
    <% if ("list".equals(request.getParameter("return")) || ticket.getId() == -1) {%>
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/issues'">
    <%} else {%>
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/issue/<%= ticket.getProjectTicketCount() %>?return=<%= StringUtils.encodeUrl(request.getParameter("return")) %>'">
    <%}%>
    <input type="hidden" name="modified" value="<%= ticket.getModified() %>">
    <input type="hidden" name="pid" value="<%= project.getId() %>">
    <input type="hidden" name="id" value="<%= ticket.getId() %>">
    <input type="hidden" name="orgId" value="<%= ticket.getOrgId() %>">
    <input type="hidden" name="contactId" value="<%= ticket.getContactId() %>">
    <input type="hidden" name="companyName" value="<%= toHtmlValue(ticket.getCompanyName()) %>">
    <input type="hidden" name="close" value="">
    <input type="hidden" name="return" value="<%= toHtmlValue(request.getParameter("return")) %>">
  </div>
</div>
</form>
<iframe src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/empty.html" name="server_commands" id="server_commands" style="visibility:hidden" height="0"></iframe>
</body>
