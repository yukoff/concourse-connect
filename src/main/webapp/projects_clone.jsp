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
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="cloneBean" class="com.concursive.connect.web.modules.profile.beans.CloneBean" scope="request" />
<%@ include file="initPage.jsp" %>
<script type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    if (form.requestDate.value == "") {
      messageText += "- Start Date is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      if (form.Save.value != 'Please Wait...') {
        form.Save.value='Please Wait...';
        form.Save.disabled = true;
        return true;
      } else {
        return false;
      }
    }
  }
</script>
<form name="inputForm" action="<%= ctx %>/CloneProject.do?command=Save&auto-populate=true" method="post" onSubmit="return checkForm(this);">
  <ccp:label name="projectsClone.projectToClone">Project to clone:</ccp:label> <%= toHtml(project.getTitle()) %><br />
  <br />
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:label name="projectsClone.newProjectDetails">New Project Details</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsClone.title">Title</ccp:label></td>
        <td nowrap>
          <input type="text" name="title" size="40" value="<%= toHtmlValue(project.getTitle()) %>">
          <%= showAttribute(request, "titleError") %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsClone.startDate">Start Date</ccp:label></td>
        <td nowrap>
          <input type="text" name="requestDate" size="10" value="<ccp:tz timestamp="<%= project.getRequestDate() %>" dateOnly="true"/>">
          <a href="javascript:popCalendar('inputForm', 'requestDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
          <ccp:label name="projectsClone.at">at</ccp:label>
          <ccp:timeSelect baseName="requestDate" value="<%= project.getRequestDate() %>" timeZone="<%= User.getTimeZone() %>"/>
          <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
          <font color="red">*</font>
          <%= showAttribute(request, "requestDateError") %>
        </td>
      </tr>
    </tbody>
  </table>
  <table class="pagedList">
    <thead>
      <tr>
        <th>
          <ccp:label name="projectsClone.cloneData">Clone the following data:</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <ccp:permission name="project-news-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneNewsCategories" value="ON" /> <ccp:label name="projectsClone.newCategories">News Categories</ccp:label> <br />
            <input type="checkbox" name="cloneNews" value="ON" /> <ccp:label name="projectsClone.newArticles">News Articles</ccp:label>
          </td>
        </tr>
      </ccp:permission>
      <ccp:permission name="project-wiki-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneWiki" value="ON" /> <ccp:label name="projectsClone.wiki">Wiki</ccp:label>
          </td>
        </tr>
      </ccp:permission>
      <ccp:permission name="project-discussion-forums-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneForums" value="ON" /> <ccp:label name="projectsClone.discussionForums">Discussion Forums</ccp:label> <%-- <br />
            <input type="checkbox" name="cloneTopics" value="ON" /> Forum Topics --%>
          </td>
        </tr>
      </ccp:permission>
      <ccp:permission name="project-documents-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneDocumentFolders" value="ON" /> <ccp:label name="projectsClone.documentFolders">Document Folders</ccp:label>
          </td>
        </tr>
      </ccp:permission>
      <ccp:permission name="project-lists-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneLists" value="ON" /> <ccp:label name="projectsClone.lists">Lists</ccp:label> <br />
            <input type="checkbox" name="cloneListItems" value="ON" /> <ccp:label name="projectsClone.listItems">List Items</ccp:label>
          </td>
        </tr>
      </ccp:permission>
      <ccp:permission name="project-plan-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneOutlines" value="ON" /> <ccp:label name="projectsClone.planOutlines">Plan Outlines</ccp:label> <br />
            <input type="checkbox" name="cloneActivities" value="ON" /> <ccp:label name="projectsClone.outlineActivities">Outline Activities and Folders</ccp:label> <br />
            <input type="checkbox" name="resetActivityDates" value="ON" /> <ccp:label name="projectsClone.adjustOutlineDates">Adjust outline dates based on new project start date</ccp:label> <br />
            <input type="checkbox" name="resetActivityStatus" value="ON" /> <ccp:label name="projectsClone.resetOutlineToIncomplete">Reset outline and activity status to incomplete</ccp:label>
          </td>
        </tr>
      </ccp:permission>
      <ccp:permission name="project-tickets-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneTicketConfig" value="ON" /> <ccp:label name="projectsClone.ticketConfiguration">Ticket Configuration</ccp:label>
          </td>
        </tr>
      </ccp:permission>
      <ccp:permission name="project-team-view">
        <tr class="containerBody">
          <td>
            <input type="checkbox" name="cloneTeam" value="ON" /> <ccp:label name="projectsClone.treamMembers">Team Members</ccp:label>
          </td>
        </tr>
      </ccp:permission>
    </tbody>
  </table>
  <input type="hidden" name="popup" value="<%= toHtmlValue(request.getParameter("popup")) %>" />
  <input type="hidden" name="projectId" value="<%= project.getId() %>" />
  <input type="submit" name="<ccp:label name="button.save">Save</ccp:label>" value="<ccp:label name="button.save">Save</ccp:label>" />
  <input type="button" name="<ccp:label name="button.cancel">Cancel</ccp:label>" value="Cancel" onClick="window.close();" />
</form>
