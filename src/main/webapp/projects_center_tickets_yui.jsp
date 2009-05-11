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
<div class="portletWrapper">
  <h1>
    <%--<img src="<%= ctx %>/images/icons/stock_macro-organizer-16.gif" border="0" align="absmiddle">--%>
    <ccp:label name="projectsCenterTickets.tickets">Tickets</ccp:label>
  </h1>
  <div class="g_menuContainer">
    <form name="ticketView" method="get" action="<%= ctx %>/ProjectManagement.do">
      <input type="hidden" name="command" value="ProjectCenter" />
      <input type="hidden" name="section" value="Tickets" />
      <input type="hidden" name="pid" value="<%= project.getId() %>" />
      <ul class="g_menuList">
        <ccp:permission name="project-tickets-add">
          <li><a href="<%= ctx %>/ProjectManagementTickets.do?command=Add&pid=<%= project.getId() %>"><ccp:label name="projectsCenterTickets.newTicket">New Ticket</ccp:label></a><li>
          <ccp:permission name="project-setup-customize">
            <li><a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=Options&pid=<%= project.getId() %>"><ccp:label name="projectsCenterTickets.configuration">Configuration</ccp:label></a></li>
          </ccp:permission>
        </ccp:permission>
        <%-- Temp. fix for Weblogic --%>
        <%
        String actionError = showError(request, "actionError");
        %>
        <li>
          <%--<img alt="" src="<%= ctx %>/images/icons/stock_filter-data-by-criteria-16.gif" align="absmiddle">--%>
          <select name="listView" onChange="document.forms['ticketView'].submit();">
            <option <%= projectTicketsInfo.getOptionValue("open") %>><ccp:label name="projectsCenterTickets.openTickets">Open Tickets</ccp:label></option>
            <option <%= projectTicketsInfo.getOptionValue("review") %>><ccp:label name="projectsCenterTickets.ticketsForReview">Tickets for review</ccp:label></option>
            <option <%= projectTicketsInfo.getOptionValue("closed") %>><ccp:label name="projectsCenterTickets.closedTickets">Closed Tickets</ccp:label></option>
            <option <%= projectTicketsInfo.getOptionValue("all") %>><ccp:label name="projectsCenterTickets.allTickets">All Tickets</ccp:label></option>
          </select>
        </li>
        <li>
          <ccp:evaluate if="<%= ticketCategoryList.size() > 1 %>">
            <% ticketCategoryList.setJsEvent("onChange=\"javascript:document.forms['ticketView'].submit();\""); %>
            <%= ticketCategoryList.getHtml("listFilter1", projectTicketsInfo.getFilterValue("listFilter1")) %>
          </ccp:evaluate>
        </li>
        <li><ccp:pagedListStatus label="Tickets" title="<%= actionError %>" object="projectTicketsInfo"/></li>
      </ul>
    </form>
  </div>
  
  <%-- Preload image rollovers --%>
  <script language="JavaScript" type="text/javascript">
    loadImages('select_<%= SKIN %>');
  </script>
  <div id="paging"></div>
  <div id="projectTeam"></div>
  <div id="paging2"></div>
  <script type="text/javascript">
  
  //var myLogReader = new YAHOO.widget.LogReader("loggerDiv");
  
  YAHOO.util.Event.onDOMReady(function () {
    var DataSource = YAHOO.util.DataSource,
        DataTable  = YAHOO.widget.DataTable,
        Paginator  = YAHOO.widget.Paginator;
  
    var mySource = new DataSource('<%= RequestUtils.getAbsoluteServerUrl(request) %>/show/<%= project.getUniqueId() %>/issues?format=json&out=text&popup=true&');
    mySource.responseType   = DataSource.TYPE_JSON;
    mySource.responseSchema = {
      totalRecords: 'totalRecords',
      resultsList : 'records',
      fields : ['id','action','ticketCount','closed','category','count','status','issue','ticpri','assignedTo','age']
    };
  
    var generateStateString = function (start,items,key,dir) {
        return "offset="+start+"&items="+items;
        //return "items="+items+"&offset="+start+"&sort="+key+"&dir="+dir;
      //state.sorting.key
      //((state.sorting.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "desc" : "asc")
    };
  
    var buildQueryString = function (state,dt) {
      return generateStateString(state.pagination.recordOffset, state.pagination.rowsPerPage, null, 'asc');
    };
  
    var myPaginator = new Paginator({
        containers         : ['paging','paging2'],
        pageLinks          : 6,
        rowsPerPage        : <%= projectTicketsInfo.getItemsPerPage() %>,
        recordOffset       : <%= projectTicketsInfo.getCurrentOffset() %>,
        totalRecords       : <%= projectTicketsInfo.getMaxRecords() %>,
        rowsPerPageOptions : [5,10,20,50,100],
        template           : "{FirstPageLink} {PreviousPageLink} {NextPageLink} {LastPageLink} <br /> {PageLinks} Show {RowsPerPageDropdown} per page"
    });
  
    var myTableConfig = {
      initialRequest: generateStateString(<%= projectTicketsInfo.getCurrentOffset() %>,<%= projectTicketsInfo.getItemsPerPage() %>),
      generateRequest: buildQueryString,
      sortedBy: {key:"<%= projectTicketsInfo.getColumnToSortBy() %>", dir:<%= (!"desc".equals(projectTicketsInfo.getSortOrder()) ? "YAHOO.widget.DataTable.CLASS_ASC" : "YAHOO.widget.DataTable.CLASS_DESC") %>},
      paginator: myPaginator,
      paginationEventHandler: DataTable.handleDataSourcePagination,
      scrollable: true,
      width: "100%",
      draggableColumns:true
    };
  
    var myColumnDefs = [
        {key:"action", label:"Action", editor:YAHOO.widget.DataTable.editDropdown, editorOptions:{dropdownOptions:["one","two","three"]}},
        {key:"ticketCount", sortable:true, label:"<ccp:label name="projectsCenterTickets.id">Id</ccp:label>", width:35},
        {key:"closed", sortable:true, label:"<ccp:label name="projectsCenterTickets.status">Status</ccp:label>", width:50},
        {key:"category", sortable:true, label:"<ccp:label name="projectsCenterTickets.category">Category</ccp:label>", width:150},
        {key:"issue", label:"<ccp:label name="projectsCenterTickets.issue">Issue</ccp:label>", width:350},
        {key:"ticpri", sortable:true, label:"<ccp:label name="projectsCenterTickets.priority">Priority</ccp:label>", width:70},
        {key:"assignedTo", label:"<ccp:label name="projectsCenterTickets.assignedTo">Assigned To</ccp:label>", width:75},
        {key:"age", label:"<ccp:label name="projectsCenterTickets.age">Age</ccp:label>", width:60}
    ];
  
    var doBeforeLoadData = function (oRequest, oResponse, oPayload) {
        oPayload = oPayload || {};
        if (!YAHOO.lang.isNumber(oPayload.startIndex)) {
            oPayload.startIndex = this.get('paginator').getStartIndex();
        }
        return true;
    };
  
    var handleSorting = function(oColumn) {
        var sDir = "asc"
        if (oColumn.key === this.get("sortedBy").key) {
            sDir = (this.get("sortedBy").dir === YAHOO.widget.DataTable.CLASS_ASC) ? "desc" : "asc";
        }
        var newRequest = "column=" + oColumn.key + "&dir=" + sDir;
        var oCallback = {
            success: this.onDataReturnInitializeTable,
            failure: this.onDataReturnInitializeTable,
            scope: this,
            argument: {
                sorting: {
                    key: oColumn.key,
                    dir: (sDir === "asc") ? YAHOO.widget.DataTable.CLASS_ASC : YAHOO.widget.DataTable.CLASS_DESC
                }
            }
        }
        this.getDataSource().sendRequest(newRequest, oCallback);
    };
  
  
    var myTable = new DataTable('projectTeam', myColumnDefs, mySource, myTableConfig);
    myTable.doBeforeLoadData = doBeforeLoadData;
  
  
    //myTable.subscribe('theadCellClickEvent', myTable.onEventSortColumn);
    //myTable.sortColumn = handleSorting;
  
  
    // Enables row highlighting
    myTable.subscribe("rowMouseoverEvent", myTable.onEventHighlightRow);
    myTable.subscribe("rowMouseoutEvent", myTable.onEventUnhighlightRow);
  
    // Enable clicking a row and going to detail page
    myTable.set("selectionMode","single");
    myTable.subscribe("rowClickEvent", myTable.onEventSelectRow);
    myTable.subscribe("rowClickEvent", function(ev) {
      var target = YAHOO.util.Event.getTarget(ev);
      var record = this.getRecord(target);
      var id = record.getData("id");
      window.location.href='<%= RequestUtils.getAbsoluteServerUrl(request) %>/ProjectManagementTickets.do?command=Details&pid=<%= project.getId() %>&id=' + id;
    });
    myTable.subscribe("cellClickEvent", myTable.onEventShowCellEditor);
  
  });
  </script>
</div>
