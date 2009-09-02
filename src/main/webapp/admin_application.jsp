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
<%@ include file="initPage.jsp" %>
<div class="admin-portlet">
  <div class="portlet-section-header">
    <h1>Manage Application Settings</h1>
    <p>Back to <a href="<%= ctx %>/admin" title ="System Administration">System Administration</a></p>
  </div>
  <div class="portlet-section-body">
      <h2>Choose an item to configure</h2>
      <ul>
        <li><a href="<%= ctx %>/AdminProjectCategories.do?command=List&resetList=true">Site Categories</a></li>
        <%--
        <li><a href="<%= ctx %>/AdminApplication.do?command=EditTicketCategoryList">Ticket Categories</a></li>
        --%>
        <li><a href="<%= ctx %>/AdminApplication.do?command=EditAssignmentRoleList">Assignment Role List</a></li>
        <li><a href="<%= ctx %>/AdminReports.do?command=List">Reports</a></li>
        <li><a href="<%= ctx %>/AdminBadgeCategories.do?command=List&resetList=true">Badge Categories</a></li>
        <li><a href="<%= ctx %>/AdminBadges.do?command=List&resetList=true">Badges</a></li>
        <li><a href="<%= ctx %>/AdminAdCategories.do?command=List">Ad Categories</a></li>
        <li><a href="<%= ctx %>/AdminClassifiedCategories.do?command=List">Classified Categories</a></li>
        <li><a href="<%= ctx %>/AdminWikiTemplates.do?command=List">Wiki Templates</a></li>
        <li><a href="<%= ctx %>/AdminDiscussionForumTemplates.do?command=List">Discussion Forum Templates</a></li>
        <li><a href="<%= ctx %>/AdminDocumentFolderTemplates.do?command=List">Document Folder Templates</a></li>
        <li><a href="<%= ctx %>/AdminListsTemplates.do?command=List">Project Lists Templates</a></li>
        <li><a href="<%= ctx %>/AdminProjectMessageTemplates.do?command=List">Project Message Templates</a></li>
        <li><a href="<%= ctx %>/AdminTicketCategoryTemplates.do?command=List">Project Ticket Category Templates</a></li>
        <li><a href="<%= ctx %>/AdminContributionPointsSystem.do">Contribution Points System</a></li>
      </ul>
    </dl>
  </div>
</div>