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
<%@ page import="com.concursive.connect.web.utils.HtmlSelectCurrencyCode" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
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
  <div class="portletWrapper">
    <h1><%--<img src="<%= ctx %>/images/icons/stock_macro-objects-16.gif" border="0" align="absmiddle">--%>
<ccp:label name="projectsCenterModifyProject.modifyProject">Modify Project</ccp:label> <span><a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Details&pid=<%= project.getId() %>"><ccp:label name="projectsCenterModifyProject.overview">Overview</ccp:label></a></span></h1>
    <%= showError(request, "actionError") %>
    <div class="formContainer">
      <form method="post" name="inputForm" action="<%= ctx %>/ProjectManagement.do?command=UpdateProject&auto-populate=true" onSubmit="return checkForm(this);">
        <input type="hidden" name="id" value="<%= project.getId() %>">
        <input type="hidden" name="modified" value="<%= project.getModified() %>">
        <fieldset id="Update_Project_Information">
          <legend><ccp:label name="projectsCenterModifyProject.updateProjectInformation">Update Project Information</ccp:label></legend>
          <label><ccp:label name="projectCenterModifyProject.title">Title<span class="required">*</span></ccp:label></label>
          <%= showAttribute(request, "titleError") %>
          <input type="text" name="title" size="57" maxlength="100" value="<%= toHtmlValue(project.getTitle()) %>">
          <label>Unique Id <span><font color="navy">(Case insensitive. Symbols allowed: a-z, &quot;-&quot;, 0-9)</font></span></label>
          <%= showAttribute(request, "uniqueIdError") %>
          <input type="text" name="uniqueId" size="57" maxlength="100" value="<%= toHtmlValue(project.getUniqueId()) %>">
          <label><ccp:label name="projectsCenterModifyProject.shortDescription">Short Description</ccp:label></label>
          <%= showAttribute(request, "shortDescriptionError") %>
          <input type="text" name="shortDescription" size="57" maxlength="1000" value="<%= toHtmlValue(project.getShortDescription()) %>"><font color="red">*</font>
          <label><ccp:label name="projectsCenterModifyProject.keywords">Keywords</ccp:label></label>
          <%= showAttribute(request, "keywordsError") %>
          <input type="text" name="keywords" size="57" maxlength="255" value="<%= toHtmlValue(project.getKeywords()) %>">
          <ccp:evaluate if="<%= categoryList.size() > 0 %>">
            <label for=""><ccp:label name="projectsCenterModifyProject.category">Category</ccp:label></label>
            <%= categoryList.getHtmlSelect("categoryId", project.getCategoryId()) %>
          </ccp:evaluate>
          <fieldset id="projectStartTime">
            <legend><ccp:label name="projectsCenterModifyProject.startDate">Start Date</ccp:label></legend>
              <input type="text" name="requestDate" id="requestDate" size="10" value="<ccp:tz timestamp="<%= project.getRequestDate() %>" dateOnly="true"/>">
              <a href="javascript:popCalendar('inputForm', 'requestDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
              <ccp:label name="projectsCenterModifyProject.at">at</ccp:label>
              <ccp:timeSelect baseName="requestDate" value="<%= project.getRequestDate() %>" timeZone="<%= User.getTimeZone() %>"/>
              <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
              <font color="red">*</font>
              <%= showAttribute(request, "requestDateError") %>
          </fieldset>
          <fieldset id="projectCloseDate">
            <legend><ccp:label name="projectsCenterModifyProject.estimatedCloseDate">Estimated Close Date</ccp:label></legend>
            <input type="text" name="estimatedCloseDate" id="estimatedCloseDate" size="10" value="<ccp:tz timestamp="<%= project.getEstimatedCloseDate() %>" dateOnly="true"/>">
            <a href="javascript:popCalendar('inputForm', 'estimatedCloseDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
            <ccp:label name="projectsCenterModifyProject.at">at</ccp:label>
            <ccp:timeSelect baseName="estimatedCloseDate" value="<%= project.getEstimatedCloseDate() %>" timeZone="<%= User.getTimeZone() %>"/>
            <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
            <%= showAttribute(request, "estimatedCloseDateError") %>
          </fieldset>
          <label for=""><ccp:label name="projectsCenterModifyProject.requestedBy">Requested By</ccp:label></label>
          <input type="text" name="requestedBy" size="24" maxlength="50" value="<%= toHtmlValue(project.getRequestedBy()) %>">
          <label for=""><ccp:label name="projectsCenterModifyProject.organization">Organization</ccp:label></label>
          <input type="text" name="requestedByDept" size="24" maxlength="50" value="<%= toHtmlValue(project.getRequestedByDept()) %>">
          <%= showAttribute(request,"budgetError") %><label for=""><ccp:label name="projectsCenterModifyProject.budget">Budget</ccp:label></label>
          <%= HtmlSelectCurrencyCode.getSelect("budgetCurrency", project.getBudgetCurrency()) %>
          <input type="text" name="budget" size="15" value="<ccp:number value="<%= project.getBudget() %>" locale="<%= User.getLocale() %>" />">
          <%
            String projectApprovedCheck = "";
            if (project.getApprovalDate() != null) {
              projectApprovedCheck = " checked";
            }
            String projectClosedCheck = "";
            if (project.getCloseDate() != null) {
              projectClosedCheck = " checked";
            }
            String projectAllowGuestsCheck = "";
            if (project.getFeatures().getAllowGuests()) {
              projectAllowGuestsCheck = " checked";
            }
            String projectAllowParticipantsCheck = "";
            if (project.getFeatures().getAllowParticipants()) {
              projectAllowParticipantsCheck = " checked";
            }
            String projectMembershipRequiredCheck = "";
            if (project.getFeatures().getMembershipRequired()) {
              projectMembershipRequiredCheck = " checked";
            }
            String projectSystemDefaultCheck = "";
            if (project.getSystemDefault()) {
              projectSystemDefaultCheck = " checked";
            }
          %>
          <label for="approved"><ccp:label name="projectCenterModifyProject.status">Status</ccp:label></label>
          <input type="checkbox" name="approved" id="approved" value="ON"<%= projectApprovedCheck %>>
          <ccp:label name="projectCenterModifyProject.approved">Approved</ccp:label> <ccp:tz timestamp="<%= project.getApprovalDate() %>"/>
          <input type="checkbox" name="closed" value="ON"<%= projectClosedCheck %>>
          <ccp:label name="projectCenterModifyProject.closed">Closed</ccp:label> <ccp:tz timestamp="<%= project.getCloseDate() %>"/>
          <ccp:evaluate if="<%= User.getAccessGuestProjects() || User.getAccessAdmin() %>">
            <input type="checkbox" name="features_allowGuests" value="ON"<%= projectAllowGuestsCheck %>>
            <ccp:label name="projectsAddProject.allowGuests">Allow Guests</ccp:label>
            <input type="checkbox" name="features_allowParticipants" value="ON"<%= projectAllowParticipantsCheck %>>
            <ccp:label name="projectsAddProject.allowParticipants">Allow Participants</ccp:label>
            <input type="checkbox" name="features_membershipRequired" value="ON"<%= projectMembershipRequiredCheck %>>
            <ccp:label name="projectsAddProject.membershipRequired">Membership Required</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= User.getAccessAdmin() %>">
            <input type="checkbox" name="systemDefault" value="ON"<%= projectSystemDefaultCheck %>>
            <ccp:label name="projectsAddProject.systemDefault">Default Profile for System</ccp:label>
          </ccp:evaluate>
        </fieldset>
        <fieldset>
          <legend><ccp:label name="projectsCenterModifyProject.ContactInformation">Contact Information</ccp:label></legend>
          <label for="businessPhone">Business Phone</label>
          <%-- @TODO: Descide whether it is better to keep inline styles for specific
                      inputs (such as business phone and extention number or ir individual
                      classes are to be created
          --%>
            <span>
              <input type="text" name="businessPhone" id="businessPhone" style="width:80%"
                       value="<%= toHtmlValue(project.getBusinessPhone()) %>">
              ext. <input type="text" name="businessPhoneExt" style="width:10%"
                       value="<%= toHtmlValue(project.getBusinessPhoneExt()) %>">
            </span>
          <label>Business Fax</label>
          <input type="text" name="businessFax" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getBusinessFax()) %>">
          <label>Email</label>
          <input type="text" name="email1" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getEmail1()) %>">
          <label>Web Site</label>
          <input type="text" name="webPage" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getWebPage()) %>">
        </fieldset>
        <fieldset>
          <legend><ccp:label name="projectsCenterModifyProject.Location">Location</ccp:label></legend>
          <label for="addressTo">Address To</label>
          <input type="text" name="addressTo" id="addressTo" size="35" maxlength="80"
                     value="<%= toHtmlValue(project.getAddressTo()) %>">
          <label for="addressLine1">Address Line 1</label>
          <input type="text" name="addressLine1" id="addressLine1" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getAddressLine1()) %>">
          <label for="addressLine2">Address Line 2</label>
          <input type="text" name="addressLine2" id="addressLine2" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getAddressLine2()) %>">
          <label for="addressLine3">Address Line 3</label>
          <input type="text" name="addressLine3" id="addressLine3" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getAddressLine3()) %>">
          <label for="city">City</label>
          <input type="text" name="city" id="city" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getCity()) %>">
          <label for="state">State</label>
          <input type="text" name="state" id="state" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getState()) %>">
          <label for="country">Country</label>
          <input type="text" name="country" id="country" size="35" maxlength="80"
                   value="<%= toHtmlValue(project.getCountry()) %>">
          <label for="postalCode">Postal Code</label>
          <input type="text" name="postalCode" id="postalCode" size="35" maxlength="12"
                   value="<%= toHtmlValue(project.getPostalCode()) %>">
          <label for="latitude">Latitude</label>
          <input type="text" name="latitude" id="latitude" size="35" maxlength="12"
                   value="<%= project.isGeoCoded() ? String.valueOf(project.getLatitude()) : "" %>">
          <label for="longitude">Longitude</label>
          <input type="text" name="longitude" id="longitude" size="35" maxlength="12"
                   value="<%= project.isGeoCoded() ? String.valueOf(project.getLongitude()) : "" %>">
        </fieldset>
      <input type="hidden" name="portal" value="<%= project.getPortal() %>">
      <input type="hidden" name="features_showDashboard" value="<%= project.getFeatures().getShowDashboard() %>">
      <input type="hidden" name="features_showCalendar" value="<%= project.getFeatures().getShowCalendar() %>">
      <input type="hidden" name="features_showNews" value="<%= project.getFeatures().getShowNews() %>">
      <input type="hidden" name="features_showWiki" value="<%= project.getFeatures().getShowWiki() %>">
      <input type="hidden" name="features_showDetails" value="<%= project.getFeatures().getShowDetails() %>">
      <input type="hidden" name="features_showTeam" value="<%= project.getFeatures().getShowTeam() %>">
      <input type="hidden" name="features_showPlan" value="<%= project.getFeatures().getShowPlan() %>">
      <input type="hidden" name="features_showLists" value="<%= project.getFeatures().getShowLists() %>">
      <input type="hidden" name="features_showDiscussion" value="<%= project.getFeatures().getShowDiscussion() %>">
      <input type="hidden" name="features_showTickets" value="<%= project.getFeatures().getShowTickets() %>">
      <input type="hidden" name="features_showDocuments" value="<%= project.getFeatures().getShowDocuments() %>">
      <input type="hidden" name="features_showAds" value="<%= project.getFeatures().getShowAds() %>">
      <input type="hidden" name="features_showClassifieds" value="<%= project.getFeatures().getShowClassifieds() %>">
      <input type="hidden" name="features_showReviews" value="<%= project.getFeatures().getShowReviews() %>">
      <input type="hidden" name="features_showBadges" value="<%= project.getFeatures().getShowBadges() %>">
      <input type="hidden" name="email2" value="<%= toHtmlValue(project.getEmail2()) %>">
      <input type="hidden" name="email3" value="<%= toHtmlValue(project.getEmail3()) %>">
      <input type="hidden" name="homePhone" value="<%= toHtmlValue(project.getHomePhone()) %>">
      <input type="hidden" name="homePhoneExt" value="<%= toHtmlValue(project.getHomePhoneExt()) %>">
      <input type="hidden" name="home2Phone" value="<%= toHtmlValue(project.getHome2Phone()) %>">
      <input type="hidden" name="home2PhoneExt" value="<%= toHtmlValue(project.getHome2PhoneExt()) %>">
      <input type="hidden" name="homeFax" value="<%= toHtmlValue(project.getHomeFax()) %>">
      <input type="hidden" name="busines2Phone" value="<%= toHtmlValue(project.getBusiness2Phone()) %>">
      <input type="hidden" name="business2PhoneExt" value="<%= toHtmlValue(project.getBusiness2PhoneExt()) %>">
      <input type="hidden" name="mobilePhone" value="<%= toHtmlValue(project.getMobilePhone()) %>">
      <input type="hidden" name="pagerNumber" value="<%= toHtmlValue(project.getPagerNumber()) %>">
      <input type="hidden" name="carPhone" value="<%= toHtmlValue(project.getCarPhone()) %>">
      <input type="hidden" name="radioPhone" value="<%= toHtmlValue(project.getRadioPhone()) %>">
      <input type="hidden" name="subCategory1Id" value="<%= project.getSubCategory1Id() %>">
      <input type="hidden" name="subCategory2Id" value="<%= project.getSubCategory2Id() %>">
      <input type="hidden" name="subCategory3Id" value="<%= project.getSubCategory3Id() %>">
      <input type="hidden" name="owner" value="<%= project.getOwner() %>">
      <input type="submit" value="<ccp:label name="button.update">Update</ccp:label>" class="submit">
      <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Details&pid=<%= project.getId() %>'" class="cancel">
      </form>
    </div>
  </div>
</body>
