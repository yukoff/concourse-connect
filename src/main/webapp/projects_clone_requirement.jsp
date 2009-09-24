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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="requirement" class="com.concursive.connect.web.modules.plans.dao.Requirement" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="cloneBean" class="com.concursive.connect.web.modules.profile.beans.CloneBean" scope="request" />
<%@ include file="initPage.jsp" %>
<form name="inputForm" action="<%= ctx %>/CloneProjectRequirement.do?command=Save&auto-populate=true" method="post">
  <ccp:label name="projectsClone.requirement.outlineToClone">Outline to clone:</ccp:label> <%= toHtml(requirement.getShortDescription()) %><br />
  <br />
  <table class="pagedList">
    <thead>
      <tr>
        <th>
          <ccp:label name="projectsClone.requirement.cloneData">Clone the following data:</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <ccp:permission name="project-plan-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneActivities" value="ON" /> <ccp:label name="projectsClone.requirement.activitiesAndFolders">Activities and Folders</ccp:label> <br />
            <%--
            <input type="checkbox" name="resetActivityDates" value="ON" /> Adjust outline dates based on new start date <br />
            --%>
            <input type="checkbox" name="resetActivityStatus" value="ON" /> <ccp:label name="projectsClone.requirements.resetOutline">Reset outline and activity status to incomplete</ccp:label>
          </td>
        </tr>
      </ccp:permission>
    </tbody>
  </table>
  <input type="hidden" name="popup" value="<%= toHtmlValue(request.getParameter("popup")) %>" />
  <input type="hidden" name="projectId" value="<%= project.getId() %>" />
  <input type="hidden" name="pid" value="<%= project.getId() %>" />
  <input type="hidden" name="rid" value="<%= requirement.getId() %>" />
  <input type="submit" name="<ccp:label name="button.save">Save</ccp:label>" value="<ccp:label name="button.save">Save</ccp:label>" />
  <input type="button" name="<ccp:label name="button.cancel">Cancel</ccp:label>" value="Cancel" onClick="window.close();" />
</form>
