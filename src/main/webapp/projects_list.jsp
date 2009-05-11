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
<%@ page import="com.concursive.connect.web.modules.plans.dao.RequirementList" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="projectList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="projectListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="projectCategoryList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="projectRequirementsMap" class="java.util.HashMap" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_list_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<%-- Temp. fix for Weblogic --%>
<%
String actionError = showError(request, "actionError");
%>
<div class="pagedListContainer">
<div class="pagedListForm">
  <form name="listView" method="post" action="<%= ctx %>/ProjectManagement.do?command=ProjectList">
    <div>
      <img src="<%= ctx %>/images/icons/stock_filter-data-by-criteria-16.gif" border="0" align="absmiddle" />
    </div>
    <div>
      <select size="1" name="listView" onchange="document.forms['listView'].submit();">
        <option <%= projectListInfo.getOptionValue("open") %>><ccp:label name="projectsList.filter.allOpenProjects">All Open Projects</ccp:label></option>
        <option <%= projectListInfo.getOptionValue("closed") %>><ccp:label name="projectsList.filter.allClosedProjects">All Closed Projects</ccp:label></option>
        <option <%= projectListInfo.getOptionValue("recent") %>><ccp:label name="projectsList.filter.recentlyAccessedProjects">Recently Accessed Projects</ccp:label></option>
      </select>
    </div>
    <ccp:evaluate if="<%= projectCategoryList.size() > 1 %>">
      <div>
        <% projectCategoryList.setJsEvent("onChange=\"javascript:document.forms['listView'].submit();\""); %>
        <%= projectCategoryList.getHtml("listFilter1", projectListInfo.getFilterValue("listFilter1")) %>
      </div>
    </ccp:evaluate>
  </form>
</div>
<div class="pagedListStatus">
  <ccp:pagedListStatus label="Projects" title="<%= actionError %>" object="projectListInfo"/>
</div>
<table class="pagedList" summary="List of projects">
  <thead>
    <tr>
      <th width="8" nowrap><ccp:label name="projectsList.action">Action</ccp:label></th>
      <th width="100%" nowrap><a href="<%= projectListInfo.addParameter(projectListInfo.getLink(), "column", "title") %>"><ccp:label name="projectsList.projectTitle">Project Title</ccp:label></a><%= projectListInfo.getSortIcon("title") %></th>
      <th nowrap><ccp:label name="projectsList.overallProgress">Overall Progress</ccp:label></th>
      <th nowrap><ccp:label name="projectsList.issueSummary">Issue Summary</ccp:label></th>
      <th nowrap><a href="<%= projectListInfo.addParameter(projectListInfo.getLink(), "column", "p.entered") %>"><ccp:label name="projectsList.startDate">Start Date</ccp:label></a><%= projectListInfo.getSortIcon("p.entered") %></th>
      <ccp:evaluate if="<%= !\"closed\".equals(projectListInfo.getListView()) %>">
        <th nowrap><a href="<%= projectListInfo.addParameter(projectListInfo.getLink(), "column", "p.est_closedate") %>"><ccp:label name="projectsList.closeDate">Close Date</ccp:label></a><%= projectListInfo.getSortIcon("p.est_closedate") %></th>
      </ccp:evaluate>
      <ccp:evaluate if="<%= \"closed\".equals(projectListInfo.getListView()) %>">
        <th nowrap><a href="<%= projectListInfo.addParameter(projectListInfo.getLink(), "column", "p.closedate") %>"><ccp:label name="projectsList.closeDate">Close Date</ccp:label></a><%= projectListInfo.getSortIcon("p.closedate") %></th>
      </ccp:evaluate>
      <%--
      <th width="118">Category</th>
      --%>
    </tr>
  </thead>
  <tbody>
    <%
      if (projectList.size() == 0) {
    %>
      <tr class="row2">
        <td colspan="6"><ccp:label name="projectsList.noProjects">No projects to display.</ccp:label></td>
      </tr>
    <%
      }
      int rowid = 0;
      int count = 0;
      Iterator i = projectList.iterator();
      while (i.hasNext()) {
        rowid = (rowid != 1?1:2);
        ++count;
        Project thisProject = (Project) i.next();
        RequirementList requirements = (RequirementList) projectRequirementsMap.get(thisProject.getId());
    %>
      <tr class="row<%= rowid %>" onmouseover="swapClass(this,'rowHighlight');showSpan('bookmark<%= count %>')" onmouseout="swapClass(this,'row<%= rowid %>');hideSpan('bookmark<%= count %>')">
        <td valign="top" align="center" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuItem', <%= thisProject.getId() %>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowNews() %>">1</ccp:evaluate>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowDiscussion() %>">1</ccp:evaluate>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowDocuments() %>">1</ccp:evaluate>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowLists() %>">1</ccp:evaluate>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowPlan() %>">1</ccp:evaluate>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowTickets() %>">1</ccp:evaluate>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowTeam() %>">1</ccp:evaluate>, 0<ccp:evaluate if="<%= thisProject.getFeatures().getShowDetails() %>">1</ccp:evaluate>, 0);"
             onMouseOver="over(0, <%= count %>)"
             onmouseout="out(0, <%= count %>); hideMenu('menuItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0"></a>
        </td>
        <td valign="top">
          <a href="<%= ctx %>/show/<%= thisProject.getUniqueId() %>"><%= toHtml(thisProject.getTitle()) %></a>
          <%--
          <ccp:evaluate if="<%= thisProject.getAllowGuests() %>">
            <img src="<%= ctx %>/images/public.gif" border="0" alt="" align="absmiddle" />
          </ccp:evaluate>
          --%>
          <ccp:evaluate if="<%= thisProject.getApprovalDate() == null %>">
            <img src="<%= ctx %>/images/unapproved.gif" border="0" alt="" align="absmiddle" />
          </ccp:evaluate>
          <br />
          <ccp:evaluate if="<%= thisProject.getCategoryId() > -1 %>">
            <i><%= toHtml(projectCategoryList.getValueFromId(thisProject.getCategoryId())) %></i>
          </ccp:evaluate>
        </td>
        <td valign="top" align="right" nowrap>
          <table cellpadding="1" cellspacing="1" class="empty">
            <tr>
              <td>&nbsp;</td>
            <ccp:evaluate if="<%= requirements.getPlanActivityCount() == 0 %>">
              <td width="<%= requirements.getPercentClosed() %>" bgColor="#CCCCCC" nowrap class="progressCell"></td>
            </ccp:evaluate>
            <ccp:evaluate if="<%= requirements.getPlanActivityCount() > 0 %>">
              <ccp:evaluate if="<%= requirements.getPercentClosed() > 0 %>">
                <td width="<%= requirements.getPercentClosed()  %>" bgColor="green" nowrap class="progressCell"></td>
              </ccp:evaluate>
              <ccp:evaluate if="<%= requirements.getPercentUpcoming() > 0 %>">
                <td width="<%= requirements.getPercentUpcoming() %>" bgColor="#99CC66" nowrap class="progressCell"></td>
              </ccp:evaluate>
              <ccp:evaluate if="<%= requirements.getPercentOverdue() > 0 %>">
                <td width="<%= requirements.getPercentOverdue() %>" bgColor="red" nowrap class="progressCell"></td>
              </ccp:evaluate>
            </ccp:evaluate>
            </tr>
          </table>
          <ccp:evaluate if="<%= requirements.getPlanActivityCount() == 0 %>">
            (0 <ccp:label name="projectsList.activities">activities</ccp:label>)
          </ccp:evaluate>
          <ccp:evaluate if="<%= requirements.getPlanActivityCount() > 0 %>">
            (<%= requirements.getPlanClosedCount() %> of <%= requirements.getPlanActivityCount() %>
            activit<%= (requirements.getPlanActivityCount() == 1?"y":"ies") %>
            <%= (requirements.getPlanClosedCount() == 1?"is":"are") %> complete)
          </ccp:evaluate>
        </td>
        <td valign="top" align="right" nowrap>
          <table cellpadding="1" cellspacing="1" class="empty">
            <tr>
              <td>&nbsp;</td>
            <ccp:evaluate if="<%= thisProject.getTicketCount() == 0 %>">
              <td width="<%= thisProject.getPercentTicketsClosed() %>" bgColor="#CCCCCC" nowrap class="progressCell"></td>
            </ccp:evaluate>
            <ccp:evaluate if="<%= thisProject.getTicketCount() > 0 %>">
              <ccp:evaluate if="<%= thisProject.getPercentTicketsClosed() > 0 %>">
                <td width="<%= thisProject.getPercentTicketsClosed()  %>" bgColor="green" nowrap class="progressCell"></td>
              </ccp:evaluate>
              <ccp:evaluate if="<%= thisProject.getPercentTicketsUpcoming() > 0 %>">
                <td width="<%= thisProject.getPercentTicketsUpcoming() %>" bgColor="#99CC66" nowrap class="progressCell"></td>
              </ccp:evaluate>
              <ccp:evaluate if="<%= thisProject.getPercentTicketsOverdue() > 0 %>">
                <td width="<%= thisProject.getPercentTicketsOverdue() %>" bgColor="red" nowrap class="progressCell"></td>
              </ccp:evaluate>
            </ccp:evaluate>
            </tr>
          </table>
          <ccp:evaluate if="<%= thisProject.getTicketCount() == 0 %>">
            (0 issues)
          </ccp:evaluate>
          <ccp:evaluate if="<%= thisProject.getTicketCount() > 0 %>">
            (<%= thisProject.getTicketsClosed() %> of <%= thisProject.getTicketCount() %>
            issue<%= (thisProject.getTicketCount() == 1?"":"s") %>
            <%= (thisProject.getTicketsClosed() == 1?"is":"are") %> complete)
          </ccp:evaluate>
        </td>
        <td valign="top" align="center" nowrap>
          <ccp:tz timestamp="<%= thisProject.getRequestDate() %>" dateOnly="true" default="&nbsp;" />
        </td>
        <ccp:evaluate if="<%= !\"closed\".equals(projectListInfo.getListView()) %>">
          <td valign="top" align="center" nowrap>
            <ccp:tz timestamp="<%= thisProject.getEstimatedCloseDate() %>" dateOnly="true" default="&nbsp;" />
          </td>
        </ccp:evaluate>
        <ccp:evaluate if="<%= \"closed\".equals(projectListInfo.getListView()) %>">
          <td valign="top" align="center" nowrap>
            <ccp:tz timestamp="<%= thisProject.getCloseDate() %>" dateOnly="true" default="&nbsp;" />
          </td>
        </ccp:evaluate>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
</div>
<ccp:evaluate if="<%= User.getAccessAddProjects() %>">
<div>
  <a href="<%= ctx %>/ProjectManagement.do?command=AddProject"><img src="<%= ctx %>/images/buttons/start_project-green.gif" border="0" /></a>
</div>
</ccp:evaluate>

