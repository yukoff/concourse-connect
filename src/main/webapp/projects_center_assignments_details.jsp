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
<%@ page import="com.concursive.connect.web.modules.plans.dao.AssignedUser" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="Assignment" class="com.concursive.connect.web.modules.plans.dao.Assignment" scope="request"/>
<jsp:useBean id="PriorityList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="StatusList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<jsp:useBean id="StatusPercentList" class="com.concursive.connect.web.utils.HtmlPercentList" scope="request"/>
<%@ include file="initPage.jsp" %>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:label name="projectsCenterActivities.details.activityDetails">Activity Details</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td valign="top" nowrap class="formLabel"><ccp:label name="projectsCenterActivities.details.description">Description</ccp:label></td>
        <td valign="top" nowrap><%= toHtml(Assignment.getRole()) %></td>
      </tr>
      <tr class="containerBody">
        <td class="formLabel"><ccp:label name="projectsCenterActivities.details.priority">Priority</ccp:label></td>
        <td valign="top"><%= toHtml(PriorityList.getValueFromId(Assignment.getPriorityId())) %></td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterActivities.details.keywords">Keywords</ccp:label></td>
        <td valign="top">
          <%= toHtml(Assignment.getTechnology()) %>
        </td>
      </tr>
    </tbody>
  </table>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:label name="projectsCenterActivities.details.assignments">Assignment</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterActivites.details.assignedTo">Assigned To</ccp:label></td>
        <td valign="top">
  <%
          Iterator assignedUsers = Assignment.getAssignedUserList().iterator();
          while (assignedUsers.hasNext()) {
            AssignedUser thisAssignedUser = (AssignedUser) assignedUsers.next();
  %>
          <ccp:username id="<%= thisAssignedUser.getUserId() %>"/><ccp:evaluate if="<%= assignedUsers.hasNext() %>">, </ccp:evaluate>
  <%
          }
  %>
        </td>
      </tr>
      <tr class="containerBody">
        <td class="formLabel" valign="top" nowrap><ccp:label name="projectsCenterActivities.details.levelOfEffort">Level of Effort</ccp:label></td>
        <td>
          <table border="0" cellspacing="0" cellpadding="0" class="empty">
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterActivities.details.estimated">Estimated:</ccp:label>
              </td>
              <td>
                <%= Assignment.getEstimatedLoeString() %>
              </td>
            </tr>
            <tr>
              <td align="right">
                <ccp:label name="projectsCenterActivites.details.actual">Actual:</ccp:label>
              </td>
              <td>
                <%= Assignment.getActualLoeString() %>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterActivites.details.startDate">Start Date</ccp:label></td>
        <td valign="top"><ccp:tz timestamp="<%= Assignment.getEstStartDate() %>" dateOnly="true" default="&nbsp;"/></td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterActivities.details.dueDate">Due Date</ccp:label></td>
        <td valign="top"><ccp:tz timestamp="<%= Assignment.getDueDate() %>" dateOnly="true"/></td>
      </tr>
    </tbody>
  </table>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:label name="projectsCenterActivities.details.progress">Progress</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterActivites.details.status">Status</ccp:label></td>
        <td>
          <%= toHtml(StatusList.getValueFromId(Assignment.getStatusId())) %>
          (<%= toHtml(StatusPercentList.getValueFromId(Assignment.getPercentComplete())) %>)
        </td>
      </tr>
    </tbody>
  </table>
<ccp:evaluate if="<%= isPopup(request) %>">
  <input type="button" value="<ccp:label name="button.close">Close</ccp:label>" onclick="window.close()"/>
</ccp:evaluate>
