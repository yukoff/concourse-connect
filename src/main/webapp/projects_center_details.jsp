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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="projectCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<div class="portletWrapper">
  <h1><ccp:label name="projectsCenter.details.overview">Overview</ccp:label></h1>
  <div class="g_tabsContainer">
    <ul class="g_tabsList">
      <ccp:permission name="project-details-edit">
  			<li><a href="<%= ctx %>/ProjectManagement.do?command=ModifyProject&pid=<%= project.getId() %>&return=ProjectCenter"><ccp:label name="projectsCenter.details.modifyProject">Modify Profile</ccp:label></a></li>
        <ccp:permission name="project-details-delete">
    			<li><a href="javascript:confirmDelete('<%= ctx %>/ProjectManagement.do?command=DeleteProject&pid=<%= project.getId() %>');"><ccp:label name="projectsCenter.details.deleteProject">Delete Profile</ccp:label></a></li>
  			</ccp:permission>
      </ccp:permission>
    </ul>
  </div>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:label name="projectsCenter.details.generalInformation">General Information</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.status">Status</ccp:label></td>
        <td>
          <ccp:evaluate if="<%= !project.getClosed() && project.getApprovalDate() == null %>">
            <img border="0" src="<%= ctx %>/images/box-hold.gif" alt="On Hold" align="absmiddle" />
          </ccp:evaluate>
          <ccp:evaluate if="<%= project.getClosed() %>">
            <font color="blue"><ccp:label name="projectsCenter.details.projectClosedOn">This project was closed on</ccp:label>
            <ccp:tz timestamp="<%= project.getCloseDate() %>" default="&nbsp;"/>
            </font>
          </ccp:evaluate>
          <ccp:evaluate if="<%= !project.getClosed() %>">
            <ccp:evaluate if="<%= project.getApprovalDate() == null %>">
              <font color="red"><ccp:label name="projectsCenter.details.projectUnderReview">This project is currently under review and has not been approved</ccp:label></font>
            </ccp:evaluate>
            <ccp:evaluate if="<%= project.getApprovalDate() != null %>">
              <font color="darkgreen"><ccp:label name="projectsCenter.details.projectApprovedOn">This project was approved on</ccp:label> <ccp:tz timestamp="<%= project.getApprovalDate() %>" default="&nbsp;"/></font>
            </ccp:evaluate>
          </ccp:evaluate>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.title">Title</ccp:label></td>
        <td>
          <%= toHtml(project.getTitle()) %>
        </td>
      </tr>
       <tr class="containerBody">
        <td nowrap class="formLabel">Unique Id</td>
        <td>
          <%= toHtml(project.getUniqueId()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.shortDescription">Short Description</ccp:label></td>
        <td>
          <%= toHtml(project.getShortDescription()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.keywords">Keywords</ccp:label></td>
        <td>
          <%= toHtml(project.getKeywords()) %>
        </td>
      </tr>
      <ccp:evaluate if="<%= projectCategoryList.size() > 1 %>">
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.category">Category</ccp:label></td>
        <td>
          <%= toHtml(projectCategoryList.getValueFromId(project.getCategoryId())) %>
        </td>
      </tr>
      </ccp:evaluate>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.startDate">Start Date</ccp:label></td>
        <td>
          <ccp:tz timestamp="<%= project.getRequestDate() %>" default="&nbsp;"/>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.estimatedCloseDate">Estimated Close Date</ccp:label></td>
        <td>
          <ccp:tz timestamp="<%= project.getEstimatedCloseDate() %>" default="&nbsp;"/>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.requestedBy">Requested By</ccp:label></td>
        <td>
          <%= toHtml(project.getRequestedBy()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.organization">Organization</ccp:label></td>
        <td>
          <%= toHtml(project.getRequestedByDept()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.budget">Budget</ccp:label></td>
        <td>
          <ccp:currency value="<%= project.getBudget() %>" code="<%= project.getBudgetCurrency() %>" locale="<%= User.getLocale() %>" default="&nbsp;"/>
        </td>
      </tr>
      <%--
      <tr class="containerBody">
        <td nowrap class="formLabel">Category</td>
        <td>
          ---
        </td>
      </tr>
      --%>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.entered">Entered</ccp:label></td>
        <td>
          <ccp:username id="<%= project.getEnteredBy() %>"/>
          <ccp:tz timestamp="<%= project.getEntered() %>"/>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenter.details.modified">Modified</ccp:label></td>
        <td>
          <ccp:username id="<%= project.getModifiedBy() %>"/>
          <ccp:tz timestamp="<%= project.getModified() %>"/>
        </td>
      </tr>
    </tbody>
  </table>
</div>
