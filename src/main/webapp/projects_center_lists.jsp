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
<%@ page import="com.concursive.connect.web.utils.LookupElement" %>
<%@ page import="com.concursive.connect.web.utils.HtmlSelect" %>
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ page import="com.concursive.connect.web.modules.lists.dao.Task" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="category" class="com.concursive.connect.web.utils.LookupElement" scope="request"/>
<jsp:useBean id="outlineList" class="com.concursive.connect.web.modules.lists.dao.TaskList" scope="request"/>
<jsp:useBean id="projectListsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="teamMemberList" class="com.concursive.connect.web.modules.members.dao.TeamMemberList" scope="request"/>
<jsp:useBean id="functionalAreaList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="complexityList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="businessValueList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="targetSprintList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="targetReleaseList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="statusList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="loeRemainingList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="assignedPriorityList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="taskUrlMap" class="java.util.HashMap" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_lists_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<%-- dynamic checkboxes --%>
<table border="0" cellpadding="1" cellspacing="0" width="100%">
  <tr class="subtab">
    <td>
      <img border="0" src="<%= ctx %>/images/icons/stock_list_enum2-16.gif" align="absmiddle">
      <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/lists"><ccp:label name="projectsCenterLists.lists">Lists</ccp:label></a> >
      <%= toHtml(category.getDescription()) %>
    </td>
  </tr>
</table>
<br />
<ccp:permission name="project-lists-modify">
  <a href="<%= ctx %>/ProjectManagementLists.do?command=Add&pid=<%= project.getId() %>&cid=<%= category.getId() %>"><ccp:label name="projectsCenterLists.addItem">Add an Item to this List</ccp:label></a>
  | <a href="<%= ctx %>/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=<%= category.getId() %>"><ccp:label name="projectsCenterLists.bucketView">Bucket View</ccp:label></a>
  <ccp:permission name="project-setup-customize">
    |
    <a href="<%= ctx %>/ProjectManagementListsConfig.do?command=Options&pid=<%= project.getId() %>"><ccp:label name="projectsCenter.configuration">Configuration</ccp:label></a>
  </ccp:permission>
  <br />
  <br />
</ccp:permission>
<%-- Temp. fix for Weblogic --%>
<%
  String actionError = showError(request, "actionError");
  HtmlSelect team = new HtmlSelect();
  team.addItem(-1, "Any");
  team.addItem(0, "Unset");
  Iterator iTeam = teamMemberList.iterator();
  while (iTeam.hasNext()) {
    TeamMember thisMember = (TeamMember)iTeam.next();
    team.addItem(thisMember.getUserId(),
         ((User)thisMember.getUser()).getNameFirstLastInitial());
  }
%>
<form name="pagedListView" method="post" action="<%= ctx %>/show/<%= project.getUniqueId() %>/list/<%= category.getId() %>">
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr>
  <td colspan="2">
    <ccp:evaluate if="<%= functionalAreaList.size() > 2 %>">
      <div style="display: block; float:left; padding-right: 5px;">
        Functional Area:
        <% functionalAreaList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
        <%= functionalAreaList.getHtml("listFilter1", projectListsInfo.getFilterValue("listFilter1")) %>
      </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= complexityList.size() > 2 %>">
      <div style="display: block; float:left; padding-right: 5px;">
        Complexity:
        <% complexityList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
        <%= complexityList.getHtml("listFilter2", projectListsInfo.getFilterValue("listFilter2")) %>
      </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= businessValueList.size() > 2 %>">
    <div style="display: block; float:left; padding-right: 5px;">
      Business Value:
      <% businessValueList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
      <%= businessValueList.getHtml("listFilter3", projectListsInfo.getFilterValue("listFilter3")) %>
    </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= targetSprintList.size() > 2 %>">
    <div style="display: block; float:left; padding-right: 5px;">
      Target Sprint:
      <% targetSprintList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
      <%= targetSprintList.getHtml("listFilter4", projectListsInfo.getFilterValue("listFilter4")) %>
    </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= targetReleaseList.size() > 2 %>">
    <div style="display: block; float:left; padding-right: 5px;">
      Target Release:
      <% targetReleaseList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
      <%= targetReleaseList.getHtml("listFilter5", projectListsInfo.getFilterValue("listFilter5")) %>
    </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= statusList.size() > 2 %>">
    <div style="display: block; float:left; padding-right: 5px;">
      Status:
      <% statusList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
      <%= statusList.getHtml("listFilter6", projectListsInfo.getFilterValue("listFilter6")) %>
    </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= loeRemainingList.size() > 2 %>">
    <div style="display: block; float:left; padding-right: 5px;">
      Remaining:
      <% loeRemainingList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
      <%= loeRemainingList.getHtml("listFilter7", projectListsInfo.getFilterValue("listFilter7")) %>
    </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= teamMemberList.size() >= 2 %>">
      <div style="display: block; float:left; padding-right: 5px;">
        Owner:
        <% team.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
        <%= team.getHtml("listFilter8", projectListsInfo.getFilterValue("listFilter8")) %>
      </div>
    </ccp:evaluate>
    <ccp:evaluate if="<%= assignedPriorityList.size() > 2 %>">
    <div style="display: block; float:left; padding-right: 5px;">
      Assigned Priority:
      <% assignedPriorityList.setJsEvent("onChange=\"javascript:document.forms['pagedListView'].submit();\""); %>
      <%= assignedPriorityList.getHtml("listFilter9", projectListsInfo.getFilterValue("listFilter9")) %>
    </div>
    </ccp:evaluate>
  </td>
</tr>
  <tr>
      <td align="left">
        <img alt="" src="<%= ctx %>/images/icons/stock_filter-data-by-criteria-16.gif" align="absmiddle">
        <select name="listView" onChange="document.forms['pagedListView'].submit();">
          <option <%= projectListsInfo.getOptionValue("all") %>><ccp:label name="projectsCenterLists.item.allItems">All Items</ccp:label></option>
          <option <%= projectListsInfo.getOptionValue("open") %>><ccp:label name="projectsCenterLists.item.incompleteItems">Incomplete Items</ccp:label></option>
          <option <%= projectListsInfo.getOptionValue("closed") %>><ccp:label name="projectsCenterLists.item.completeItems">Completed Items</ccp:label></option>
        </select>
      </td>
      <td>
        <ccp:pagedListStatus label="Items" title="<%= actionError %>" object="projectListsInfo"/>
      </td>
  </tr>
</table>
</form>
<table id="itemList" class="pagedList">
  <thead>
    <tr>
      <th width="8"><ccp:label name="projectsCenterLists.action">Action</ccp:label></th>
      <th align="center"><ccp:label name="projectsCenterLists.count">#</ccp:label></th>
      <th align="center" nowrap>
        <a href="<%= projectListsInfo.addParameter(projectListsInfo.getLink(), "column", "t.priority,description") %>"><ccp:label name="projectsCenterLists.priority">Priority</ccp:label></a>
        <%= projectListsInfo.getSortIcon("t.priority,description") %>
      </th>
      <th width="100%">
        <a href="<%= projectListsInfo.addParameter(projectListsInfo.getLink(), "column", "t.description") %>"><ccp:label name="projectsCenterLists.item">Item</ccp:label></a>
        <%= projectListsInfo.getSortIcon("t.description") %>
      </th>
      <th align="center" nowrap width="100">
        Rating
      </th>
      <th align="center" nowrap>
        <ccp:label name="projectsCenterLists.modifiedBy">Modified By</ccp:label>
      </th>
      <th align="center" nowrap>
        <a href="<%= projectListsInfo.addParameter(projectListsInfo.getLink(), "column", "t.modified") %>"><ccp:label name="projectsCenterLists.modified">Modified</ccp:label></a>
        <%= projectListsInfo.getSortIcon("t.modified") %>
      </th>
    </tr>
  </thead>
  <tbody>
    <%
      if (outlineList.size() == 0) {
    %>
      <tr class="row2">
        <td colspan="7"><ccp:label name="projectsCenterLists.noItemsToDisplay">No items to display.</ccp:label></td>
      </tr>
    <%
      }
      int count = 0;
      int rowid = 0;
      Iterator i = outlineList.iterator();
      while (i.hasNext()) {
        ++count;
        rowid = (rowid != 1?1:2);
        Task thisTask = (Task)i.next();
    %>
      <tr id="item_<%= thisTask.getId() %>" class="row<%= rowid %>" onmouseover="swapClass(this,'rowHighlight')" onmouseout="swapClass(this,'row<%= rowid %>')">
        <td valign="top" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuListItem', <%= thisTask.getId() %>);"
             onMouseOver="over(0, <%= count %>)"
             onmouseout="out(0, <%= count %>); hideMenu('menuListItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0"></a>
        </td>
        <td align="center" valign="top" nowrap>
          <table border="0" cellpadding="0" cellspacing="0" width="100%" class="empty">
            <tr>
              <td valign="middle" align="right">
                <%= count %>.&nbsp;
              </td>
            </tr>
          </table>
        </td>
        <td align="center" valign="top" nowrap>
          <%= thisTask.getPriority() %>
        </td>
        <td width="100%" valign="top" align="left"<%= thisTask.getComplete()?" class=\"ghost\"":"" %>>
          <table border="0" cellspacing="0" cellpadding="0" width="100%" class="empty">
            <tr>
              <td valign="top" nowrap>
                <ccp:permission name="project-lists-modify"><a href="javascript:changeImages('task<%= count %>', '<%= ctx %>/ProjectManagementLists.do?command=MarkItem&pid=<%= project.getId() %>&id=<%= thisTask.getId() %>&check=off', '<%= ctx %>/ProjectManagementLists.do?command=MarkItem&pid=<%= project.getId() %>&id=<%= thisTask.getId() %>&check=on')"></ccp:permission><img name="task<%= count %>" border="0" src="<%= ctx %>/images/box<%= thisTask.getComplete()?"-checked":"" %>.gif" alt="" align="absmiddle" id="<%= thisTask.getComplete()?"1":"0" %>" /><ccp:permission name="project-lists-modify"></a></ccp:permission>&nbsp;
              </td>
              <td valign="top" width="100%">
                <ccp:evaluate if="<%= taskUrlMap.get(thisTask.getId()) != null %>">
                    <a href="<%= taskUrlMap.get(thisTask.getId())%>" title="<%= toHtml(thisTask.getDescription()) %>">
                        <%= toHtml(thisTask.getDescription()) %>
                    </a>
                </ccp:evaluate>
                <ccp:evaluate if="<%= taskUrlMap.get(thisTask.getId()) == null %>">
                    <%= toHtml(thisTask.getDescription()) %>
                </ccp:evaluate>
                <ccp:evaluate if="<%= hasText(thisTask.getNotes()) %>">
                <a href="javascript:popURL('<%= ctx %>/ProjectManagementLists.do?command=Details&pid=<%= project.getId() %>&cid=<%= category.getId() %>&id=<%= thisTask.getId() %>&popup=true','650','375','yes','yes');"><img src="<%= ctx %>/images/icons/stock_insert-note-16.gif" border="0" align="absmiddle"/></a>
                </ccp:evaluate>
              </td>
            </tr>
          </table>
        </td>
        <td align="center" valign="top" nowrap>
          <%-- if this task is linked to anything show the linkedItemRating instead of the task rating --%>
          <ccp:evaluate if="<%= thisTask.getLinkModuleId() == -1 || thisTask.getLinkItemId() == -1%>">
            <ccp:rating id='<%= String.valueOf(thisTask.getId()) %>'
                         showText='false'
                         count='<%= thisTask.getRatingCount() %>'
                         value='<%= thisTask.getRatingValue() %>'
                         url='<%= ctx + "/ProjectManagementLists.do?command=SetRating&pid=" + thisTask.getProjectId() + "&id=" + thisTask.getId() + "&v={vote}&out=text" %>'/>
          </ccp:evaluate>
          <ccp:evaluate if="<%= thisTask.getLinkModuleId() != -1 && thisTask.getLinkItemId() != -1%>">
            <ccp:rating id='<%= String.valueOf(thisTask.getId()) %>'
                         showText='false'
                         count='1'
                         value='<%= Math.round((float)thisTask.getLinkItemRating()) %>'
                         url=''/>
          </ccp:evaluate>
        </td>
        <td align="center" valign="top" nowrap>
          <ccp:username id="<%= thisTask.getModifiedBy() %>"/>
        </td>
        <td align="center" valign="top" nowrap>
          <ccp:tz timestamp="<%= thisTask.getModified() %>"/>
        </td>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
