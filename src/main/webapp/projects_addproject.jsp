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
<jsp:useBean id="ProjectList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<jsp:useBean id="currencyCodeList" class="com.concursive.connect.web.utils.HtmlSelectCurrencyCode" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.title.value == "") {
      messageText += "- Title is a required field\r\n";
      formTest = false;
    }
    if (form.uniqueId.value == "") {
      //messageText += "- Unique Id is a required field\r\n";
      //formTest = false;
    } else {
      var regx = new RegExp("^[a-zA-Z0-9-]+$", "g");
      if (!form.uniqueId.value.match(regx)) {
        messageText += "- Unique Id contains symbols that are not allowed\r\n";
        formTest = false;
      }
    }
    if (form.shortDescription.value == "") {
      messageText += "- Description is a required field\r\n";
      formTest = false;
    }
    if (form.requestDate.value == "") {
      messageText += "- Start Date is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<body onLoad="document.inputForm.title.focus()">
<form method="post" name="inputForm" action="<%= ctx %>/ProjectManagement.do?command=InsertProject&auto-populate=true" onSubmit="return checkForm(this);">
<%= showError(request, "actionError", false) %>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        <ccp:label name="projectsAddProject.header">New Project Information</ccp:label>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel">
        <ccp:label name="projectsAddProject.title">Title</ccp:label>
      </td>
      <td>
        <input type="text" name="title" size="57" maxlength="100" value="<%= toHtmlValue(project.getTitle()) %>">
        <font color="red">*</font> <%= showAttribute(request, "titleError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Unique Id</td>
      <td>
        <input type="text" name="uniqueId" size="57" maxlength="100" value="<%= toHtmlValue(project.getUniqueId()) %>">
        <font color="navy">(Case insensitive. Symbols allowed: a-z, &quot;-&quot;, 0-9)</font>
        <%= showAttribute(request, "uniqueIdError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsAddProject.shortDescription">Short Description</ccp:label></td>
      <td>
        <input type="text" name="shortDescription" size="57" maxlength="200" value="<%= toHtmlValue(project.getShortDescription()) %>"><font color="red">*</font> <%= showAttribute(request, "shortDescriptionError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsCenterModifyProject.keywords">Keywords</ccp:label></td>
      <td>
        <input type="text" name="keywords" size="57" maxlength="255" value="<%= toHtmlValue(project.getKeywords()) %>">
        <%= showAttribute(request, "keywordsError") %>
      </td>
    </tr>
    <ccp:evaluate if="<%= categoryList.size() > 0 %>">
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsAddProject.category">Category</ccp:label></td>
      <td>
        <%= categoryList.getHtmlSelect("categoryId", project.getCategoryId()) %>
      </td>
    </tr>
    </ccp:evaluate>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsAddProject.startDate">Start Date</ccp:label></td>
      <td>
        <input type="text" name="requestDate" id="requestDate" size="10" value="<ccp:tz timestamp="<%= project.getRequestDate() %>" dateOnly="true"/>">
        <a href="javascript:popCalendar('inputForm', 'requestDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        <ccp:label name="projectsAddProject.at">at</ccp:label>
        <ccp:timeSelect baseName="requestDate" value="<%= project.getRequestDate() %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
        <font color="red">*</font>
        <%= showAttribute(request, "requestDateError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsAddProject.estimatedCloseDate">Estimated Close Date</ccp:label></td>
      <td>
        <input type="text" name="estimatedCloseDate" id="estimatedCloseDate" size="10" value="<ccp:tz timestamp="<%= project.getEstimatedCloseDate() %>" dateOnly="true"/>">
        <a href="javascript:popCalendar('inputForm', 'estimatedCloseDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        <ccp:label name="projectsAddProject.at">at</ccp:label>
        <ccp:timeSelect baseName="estimatedCloseDate" value="<%= project.getEstimatedCloseDate() %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
        <%= showAttribute(request, "estimatedCloseDateError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsAddProject.requestedBy">Requested By</ccp:label></td>
      <td>
        <input type="text" name="requestedBy" size="24" maxlength="50" value="<%= toHtmlValue(project.getRequestedBy()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsAddProject.organisation">Organization</ccp:label></td>
      <td>
        <input type="text" name="requestedByDept" size="24" maxlength="50" value="<%= toHtmlValue(project.getRequestedByDept()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsAddProject.budget">Budget</ccp:label></td>
      <td>
        <%= currencyCodeList.getSelect("budgetCurrency", project.getBudgetCurrency()) %>
        <input type="text" name="budget" size="15" value="<ccp:number value="<%= project.getBudget() %>" locale="<%= User.getLocale() %>" />">
        <%=showAttribute(request,"budgetError")%>
      </td>
    </tr>
    <tr class="containerBody">
  <%
    String projectApprovedCheck = "";
    String projectClosedCheck = "";
    String projectAllowGuestsCheck = "";
    String projectAllowParticipantsCheck = "";
    String projectMembershipRequiredCheck = "";
  %>
      <td class="formLabel" valign="top" nowrap><ccp:label name="projectsAddProject.status">Status</ccp:label></td>
      <td>
        <input type="checkbox" name="approved" value="ON"<%= projectApprovedCheck %>>
        <ccp:label name="projectsAddProject.approved">Approved</ccp:label> <ccp:tz timestamp="<%= project.getApprovalDate() %>"/>
        <input type="checkbox" name="closed" value="ON"<%= projectClosedCheck %>>
        <ccp:label name="projectsAddProject.closed">Closed</ccp:label> <ccp:tz timestamp="<%= project.getCloseDate() %>"/>
        <ccp:evaluate if="<%= User.getAccessGuestProjects() || User.getAccessAdmin() %>">
          <input type="checkbox" name="features_allowGuests" value="ON"<%= projectAllowGuestsCheck %>>
          <ccp:label name="projectsAddProject.allowGuests">Allow Guests</ccp:label>
          <input type="checkbox" name="features_allowParticipants" value="ON"<%= projectAllowParticipantsCheck %>>
          <ccp:label name="projectsAddProject.allowParticipants">Allow Participants</ccp:label>
          <input type="checkbox" name="features_membershipRequired" value="ON"<%= projectMembershipRequiredCheck %>>
          <ccp:label name="projectsAddProject.membershipRequired">Membership Required</ccp:label>
        </ccp:evaluate>
      </td>
    </tr>
    <%--
    <ccp:evaluate if="<%= ProjectList.size() > 0 %>">
      <tr class="containerBody">
        <td class="formLabel" valign="top" nowrap >Import Data</td>
        <td>
          Copy News, Discussions, Lists, Outlines, Assignments, Documents and Team from an existing project:<br />
          <%= ProjectList.getHtmlSelect("templateId", 0) %>
        </td>
      </tr>
    </ccp:evaluate>
    --%>
  </tbody>
</table>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        <ccp:label name="projectsCenterModifyProject.Location">Location</ccp:label>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td class="formLabel">
        Address To
      </td>
      <td>
        <input type="text" name="addressTo" size="35" maxlength="80"
               value="<%= toHtmlValue(project.getAddressTo()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Address Line 1
      </td>
      <td>
        <input type="text" name="addressLine1" size="35" maxlength="80"
               value="<%= toHtmlValue(project.getAddressLine1()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Address Line 2
      </td>
      <td>
        <input type="text" name="addressLine2" size="35" maxlength="80"
               value="<%= toHtmlValue(project.getAddressLine2()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Address Line 3
      </td>
      <td>
        <input type="text" name="addressLine3" size="35" maxlength="80"
               value="<%= toHtmlValue(project.getAddressLine3()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        City
      </td>
      <td>
        <input type="text" name="city" size="35" maxlength="80"
               value="<%= toHtmlValue(project.getCity()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        State
      </td>
      <td>
        <input type="text" name="state" size="35" maxlength="80"
               value="<%= toHtmlValue(project.getState()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Country
      </td>
      <td>
        <input type="text" name="country" size="35" maxlength="80"
               value="<%= toHtmlValue(project.getCountry()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Postal Code
      </td>
      <td>
        <input type="text" name="postalCode" size="35" maxlength="12"
               value="<%= toHtmlValue(project.getPostalCode()) %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Latitude
      </td>
      <td>
        <input type="text" name="latitude" size="35" maxlength="12"
               value="<%= project.isGeoCoded() ? String.valueOf(project.getLatitude()) : "" %>">
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Longitude
      </td>
      <td>
        <input type="text" name="longitude" size="35" maxlength="12"
               value="<%= project.isGeoCoded() ? String.valueOf(project.getLongitude()) : "" %>">
      </td>
    </tr>
  </tbody>
</table>
<input type="hidden" name="features_showDashboard" value="false">
<input type="hidden" name="features_showCalendar" value="false">
<input type="hidden" name="features_showNews" value="true">
<input type="hidden" name="features_showWiki" value="true">
<input type="hidden" name="features_showDetails" value="true">
<input type="hidden" name="features_showTeam" value="true">
<input type="hidden" name="features_showPlan" value="true">
<input type="hidden" name="features_showLists" value="true">
<input type="hidden" name="features_showDiscussion" value="true">
<input type="hidden" name="features_showTickets" value="true">
<input type="hidden" name="features_showDocuments" value="true">
<input type="hidden" name="features_showAds" value="false">
<input type="hidden" name="features_showClassifieds" value="false">
<input type="hidden" name="features_showReviews" value="false">
<input type="hidden" name="features_showBadges" value="false">
<input type="submit" value="<ccp:label name="button.save">Save</ccp:label>">
<input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagement.do?command=ProjectList'">
</form>
</body>
