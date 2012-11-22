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
<%@ page import="com.concursive.connect.web.portal.PortalUtils,javax.portlet.*" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="subCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<jsp:useBean id="allowedCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<jsp:useBean id="categoryName" class="java.lang.String" scope="request"/>
<jsp:useBean id="isSubCategoryModifiable" class="java.lang.String" scope="request"/>
<jsp:useBean id="showAllowGuestsOption" class="java.lang.String" scope="request"/>
<jsp:useBean id="showAllowParticipantsOption" class="java.lang.String" scope="request"/>
<jsp:useBean id="showRequiresMembershipOption" class="java.lang.String" scope="request"/>
<jsp:useBean id="states" class="com.concursive.connect.web.utils.StateSelect" scope="request"/>
<jsp:useBean id="countries" class="com.concursive.connect.web.utils.CountrySelect" scope="request"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<%--@elvariable id="title" type="java.lang.String"--%>
<%--@elvariable id="introductionMessage" type="java.lang.String"--%>
<%--@elvariable id="allowedCategoryList" type="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList"--%>
<%--@elvariable id="labelMap" type="java.util.HashMap"--%>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<%-- Use the minimal TinyMCE Editor for blog posts --%>
<jsp:include page="../../tinymce_comments_include.jsp" flush="true"/>
<script language="javascript" type="text/javascript">
  // editor variable
  var ilId = <%= project.getId() %>;
  initEditor('description');
  
  //focus
  YAHOO.util.Event.onDOMReady(function() {
    document.inputForm.title.focus();
  } );

  // form validation
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    <%-- Validations --%>
    <c:if test="${requiresStartEndDate eq 'true'}">
      if (!document.getElementById("requestDate").value) {
        messageText += "- Start Date is a required field\r\n";
        formTest = false;
      }
      if (!document.getElementById("estimatedCloseDate").value) {
        messageText += "- End Date is a required field\r\n";
        formTest = false;
      }
    </c:if>
    <%-- End Validations --%>
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      if (form.save.value != 'Please Wait...') {
        form.save.value='Please Wait...';
        form.save.disabled = true;
        return true;
      } else {
        return false;
      }
    }
  }
  <c:choose>
	  <c:when test="<%= allowedCategoryList.size() <= 3 %>">
	  	var eventName = 'click';
	  </c:when>
    <c:otherwise>
	  	var eventName = 'change';
    </c:otherwise>
  </c:choose>
  YAHOO.util.Event.on('<portlet:namespace/>categoryId',eventName,function (e) {
    // Get the div element in which to report messages from the server
    var msg_section = YAHOO.util.Dom.get('<portlet:namespace/>subCategory1Id');
    msg_section.innerHTML = '<p>&nbsp;</p>';
    // Define the callbacks for the asyncRequest
    var callbacks = {
        success : function (o) {
          var messages = [];
          try {
              messages = YAHOO.lang.JSON.parse(o.responseText);
          }
          catch (x) {
              alert("Connection to server failed.");
              return;
          }
          if (messages.length > 0) {
            msg_section.innerHTML = '&nbsp;';
            var sel = document.createElement('select');
            sel.name = 'subCategory1Id';
            msg_section.appendChild(sel);
            for (var i = 0, len = messages.length; i < len; ++i) {
                var m = messages[i];
                var optionNew = document.createElement('option');
                optionNew.text = m.text;
                optionNew.value = m.value;
                sel.options[i] = optionNew;
            }
          } else {
            msg_section.innerHTML = '<p>None to choose from</p>';
          }
        },

        failure : function (o) {
            if (!YAHOO.util.Connect.isCallInProgress(o)) {
                alert("Call failed!");
            }
        },
        timeout : 3000
    };
    <portlet:renderURL var="urlSubCategories" portletMode="view"  windowState="maximized">
      <portlet:param name="viewType" value="getSubCategories"/>
    </portlet:renderURL>
    <c:choose>
      <c:when test="<%= allowedCategoryList.size() <= 3 %>">
        var val = 0;
        for( i = 0; i < document.inputForm.categoryId.length ; i++ ){
          if(document.inputForm.categoryId[i].checked)
            val = document.inputForm.categoryId[i].value;
        }
        YAHOO.util.Connect.asyncRequest('GET',"<%= pageContext.getAttribute("urlSubCategories") %>&__rp<%=PortalUtils.getDashboardPortlet((PortletRequest)request).getWindowConfigId()%>_category=" + val + "&out=text", callbacks);
      </c:when>
      <c:otherwise>
        YAHOO.util.Connect.asyncRequest('GET','<%= pageContext.getAttribute("urlSubCategories") %>&__rp<%=PortalUtils.getDashboardPortlet((PortletRequest)request).getWindowConfigId()%>_category=' + document.inputForm.categoryId.value + '&out=text', callbacks);
      </c:otherwise>
    </c:choose>
  });
  function calendarTrigger(fieldName) {
    var startDateEl = document.getElementById('requestDate');
    var endDateEl = document.getElementById('estimatedCloseDate');
    if (fieldName == 'requestDate') {
      if (dateIsLaterThan(startDateEl, endDateEl)) {
        endDateEl.value = startDateEl.value;
      }
    } else if (fieldName == 'estimatedCloseDate') {
      if (dateIsLaterThan(startDateEl, endDateEl)) {
        startDateEl.value = endDateEl.value;
      }
    }
  }
  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
  }
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }
</script>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<h2><c:out value="${title}"/></h2>
<c:if test="${!empty introductionMessage}">
  <p><c:out value="${introductionMessage}"/></p>
</c:if>
<c:if test="${!empty introductionMessage2}">
  <p><c:out value="${introductionMessage2}"/></p>
</c:if>
<c:if test="${!empty actionError}">
  <p><font color="red"><c:out value="${actionError}"/></font></p>
</c:if>
<div class="formContainer">
  <portlet:actionURL var="submitContentUrl" portletMode="view" />
    <form method="POST" id="inputForm" name="inputForm" action="<%= pageContext.getAttribute("submitContentUrl") %>" onSubmit="return checkForm(this);">
      <fieldset id="Basic Information">
        <legend>Basic Information</legend>
          <input type="hidden" name="id" value="<%= project.getId() %>" />
          <input type="hidden" name="modified" value="<%= project.getModified() %>" />
          <label for="<portlet:namespace/>title">
            <c:choose>
              <c:when test='${empty labelMap["title"]}'>
                Name
              </c:when>
              <c:otherwise>
                <c:out value='${labelMap["title"]}'/>
              </c:otherwise>
            </c:choose>
            <span class="required">*</span>
          </label>
          <div class="error">
            <%= showAttribute(request, "titleError") %>
          </div>
          <input type="text" id="<portlet:namespace/>title" name="title" class="input longInput" maxlength="100" value="<%= toHtmlValue(project.getTitle()) %>" />
          <span class="characterCounter">100 characters max</span>
          <%-- Location Moved --%>
          <c:if test='${showLocationName == "true" || showAddress == "true"}'>
            <c:if test='${showLocationName == "true"}'>
              <label for="addressTo">Location Name</label>
              <input type="text" name="addressTo" id="addressTo" class="input longInput"  maxlength="80" value="<%= toHtmlValue(project.getAddressTo()) %>" />
              <span class="characterCounter">80 characters max</span>
            </c:if>
            <c:if test='${showAddress == "true"}'>
              <label for="addressline1">Street Address</label>
              <input type="text" name="addressLine1" id="addressLine1" class="input longInput" maxlength="80" value="<%= toHtmlValue(project.getAddressLine1()) %>" />
              <span class="characterCounter">80 characters max</span>
              <%-- Additional Address feilds
              <label for="addressLine2">Address Line 2 (optional)</label>
              <input type="text" name="addressLine2" id="addressLine2" class="input shortInput"
               value="<%= toHtmlValue(project.getAddressLine2()) %>" />
              <label for="addressLine3">Address Line 3 (optional)</label>
              <input type="text" name="addressLine3" id="addressLine3" class="input shortInput"
               value="<%= toHtmlValue(project.getAddressLine3()) %>" />
               --%>
              <label for="city">City</label>
              <input type="text" name="city" id="city" class="input city" maxlength="80" value="<%= toHtmlValue(project.getCity()) %>" />
              <span class="characterCounter">80 characters max</span>
              <label for="state">State/Province</label>
              <input type="text" name="state" id="state" class="input state" maxlength="80" value="<%= toHtmlValue(project.getState()) %>" />
              <span class="characterCounter">80 characters max</span>
              <label for="postalCode">Zip/Postal Code</label>
              <input type="text" name="postalCode" id="postalCode" class="input zipInput" maxlength="12" value="<%= toHtmlValue(project.getPostalCode()) %>" />
               <label for="country">Country</label>
              <div class="displayCountries">
                <%= countries.getHtml("country",project.getCountry()) %>
              </div>
            </c:if>
          </c:if>
          <%-- End Location --%>
          <%-- Contact Information --%>
          <c:if test='${showContactInformation == "true"}'>
            <label for="phone">Phone</label>
            <input type="text" name="businessPhone" id="phone" class="input shortInput" maxlength="30" value="<%= toHtmlValue(project.getBusinessPhone()) %>" />
            <%-- Fax
            <label for="businessFax">Fax</label>
            <input type="text" name="businessFax" id="businessFax" class="input shortInput"
            value="<%= toHtmlValue(project.getBusinessFax()) %>" />
            --%>
            <%-- Email
            <label for="email1">Email<span class="required">*</span></label>
            <input type="text" id="email1" name="email1" class="input longInput"
             value="<%= toHtmlValue(project.getEmail1()) %>" />
               --%>
          </c:if>
          <%-- Project Type --%>
          <c:if test="${!empty allowedCategoryList}">
            <c:choose>
              <c:when test="<%= allowedCategoryList.size() <= 3 %>">
                <label for="<portlet:namespace/>categoryId">Type<span class="required">*</span></label>
                <div id="<portlet:namespace/>categoryId">
                  <c:forEach items="${allowedCategoryList}" var="allowedCategory">
                    <label>
                        <%--@elvariable id="allowedCategory" type="com.concursive.connect.web.modules.profile.dao.ProjectCategory"--%>
                      <input type="radio" name="categoryId" value="${allowedCategory.id}"<c:if test="${allowedCategory.id == project.categoryId}"> checked</c:if>/>
                      <c:out value="${allowedCategory.label}"/>
                    </label>
                  </c:forEach>
                </div>
              </c:when>
              <c:otherwise>
                <label for="<portlet:namespace/>categoryId">Type<span class="required">*</span>
                  <select name="categoryId" id="<portlet:namespace/>categoryId" class="input selectInput">
                      <%--@elvariable id="allowedCategory" type="com.concursive.connect.web.modules.profile.dao.ProjectCategory"--%>
                    <c:forEach items="${allowedCategoryList}" var="allowedCategory">
                      <option value="${allowedCategory.id}"<c:if test="${allowedCategory.id == project.categoryId}"> selected</c:if>><c:out value="${allowedCategory.label}"/></option>
                    </c:forEach>
                  </select>
                </label>
              </c:otherwise>
            </c:choose>
          </c:if>
          <%-- End Project Type --%>
        </fieldset>
        <%-- End Basic Project Information --%>
        <%--  Additional Project Details --%>
        <fieldset id="Additional Details">
          <legend>Additional Details</legend>
          <%-- Start And Stop Time--%>
          <c:if test="${showStartEndDateOption}">
            <c:if test="${!empty project.requestDate}">
              <c:set var="requestDate">
                <ccp:tz timestamp="${project.requestDate}" dateOnly="true"/>
              </c:set>
            </c:if>
            <c:if test="${!empty project.estimatedCloseDate}">
              <c:set var="estimatedCloseDate">
                <ccp:tz timestamp="${project.estimatedCloseDate}" dateOnly="true"/>
              </c:set>
            </c:if>
            <fieldset>
              <legend>Start Date <c:if test="requiresStartEndDate"><span class="required">*</span></c:if></legend>
              <%= showAttribute(request, "requestDateError") %>
              <input type="text" name="requestDate" id="requestDate" class="inputDate" onchange="calendarTrigger('requestDate');" value="${requestDate}" />
              <a href="javascript:popCalendar('inputForm','requestDate','${user.locale.language}','${user.locale.country}');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
              <ccp:label name="projectsAddProject.at">at</ccp:label>
              <ccp:timeSelect baseName="requestDate" value="${project.requestDate}" timeZone="${user.timeZone}"/>
              <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
            </fieldset>
            <fieldset class="projectEndTime">
              <legend>Ends at <c:if test="requiresStartEndDate"><span class="required">*</span></c:if></legend>
              <%= showAttribute(request, "estimatedCloseDateError") %>
              <input type="text" name="estimatedCloseDate" id="estimatedCloseDate" class="inputDate" onchange="calendarTrigger('estimatedCloseDate');" value="${estimatedCloseDate}" />
              <a href="javascript:popCalendar('inputForm', 'estimatedCloseDate', '${user.locale.language}', '${user.locale.country}');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
              <ccp:label name="projectsAddProject.at">at</ccp:label>
              <ccp:timeSelect baseName="estimatedCloseDate" value="${project.estimatedCloseDate}" timeZone="${user.timeZone}"/>
              <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
            </fieldset>
          </c:if>
          <%-- End Start And Stop Time--%>
          <%-- Site Categories --%>
          <ccp:evaluate if='<%= "true".equals(isSubCategoryModifiable) %>'>
            <ccp:evaluate if="<%= allowedCategoryList.size() <= 1 %>">
              <ccp:evaluate if="<%= subCategoryList.size() > 0 %>">
                <label for="subCategory1Id">
                  <ccp:label name="portlet.addprojectByCategory.category">Category</ccp:label>
                </label>
                <div id="<portlet:namespace/>subCategory1Id">
                  <%= subCategoryList.getHtmlSelect("subCategory1Id", project.getSubCategory1Id()) %>
                </div>
              </ccp:evaluate>
            </ccp:evaluate>
            <ccp:evaluate if="<%= allowedCategoryList.size() > 1 %>">
              <label for="subCategory1Id">
                <ccp:label name="portlet.addprojectByCategory.category">Category</ccp:label>
              </label>
              <div id="<portlet:namespace/>subCategory1Id">
                <%= subCategoryList.getHtmlSelect("subCategory1Id", project.getSubCategory1Id()) %>
              </div>
            </ccp:evaluate>
          </ccp:evaluate>
          <%-- End Site Categories --%>
          <%-- Project Description --%>
          <c:if test='${showShortDescription == "true"}'>
            <label for="shortDescription">Short description<span class="required">*</span></label>
            <%= showAttribute(request, "shortDescriptionError") %>
            <input type="text" name="shortDescription" id="shortDescription" class="input longInput" maxlength="1000" value="<%= toHtmlValue(project.getShortDescription()) %>" />
            <span class="characterCounter">1000 characters max</span>
          </c:if>
          <c:if test='${showLongDescription == "true"}'>
            <label for="description">Description<span class="required">*</span></label>
            <%= showAttribute(request, "descriptionError") %>
            <textarea id="description" name="description" class="height200"><c:out value="<%= project.getDescription() %>" /></textarea>
          </c:if>
          <c:if test='${showKeywords == "true"}'>
            <label for="keywords">Keywords<span class="required">*</span> (comma-separated)</label>
            <input type="text" name="keywords" id="keywords" class="input longInput" maxlength="255" value="<%= toHtmlValue(project.getKeywords()) %>" />
            <span class="characterCounter">255 characters max</span>
          </c:if>
          <c:if test='${showWebsite == "true"}'>
            <label for="webPage">Website</label>
            <input type="text" id="webPage" name="webPage" class="input longInput" maxlength="200" value="<%= toHtmlValue(project.getWebPage()) %>" />
            <span class="characterCounter">200 characters max</span>
          </c:if>
          <c:if test='${showAllowGuestsOption == "true"}'>
            <label for="features_allowGuests">
              <input type="checkbox" class="checkbox" name="features_allowGuests" id="features_allowGuests" value="ON" checked />
              <ccp:label name="projectsAddProject.allowGuests">Allow others to search for and view this listing information in the directory, otherwise the listing is private and by invitation only</ccp:label>
            </label>
          </c:if>
          <c:if test='${showAllowParticipantsOption == "true"}'>
            <label for="features_allowParticipants">
              <input type="checkbox" class="checkbox" name="features_allowParticipants" id="features_allowParticipants" value="ON" checked />
              <ccp:label name="projectsAddProject.allowParticipants">Allow others to search for and view this listing information in the directory, otherwise the listing is private and by invitation only</ccp:label>
            </label>
          </c:if>
          <c:if test='${showRequiresMembershipOption == "true"}'>
            <label for="features_membershipRequired">
              <input type="checkbox" class="checkbox" name="features_membershipRequired" id="features_membershipRequired" value="ON" />
              <ccp:label name="projectsAddProject.membershipRequired">Require authorization by you for other users to join and participate, otherwise anyone can join and immediately collaborate</ccp:label>
            </label>
          </c:if>
          <c:if test='${showSingleImageAttachment == "true"}'>
            <label for="singleAttachment">Image
          <%
            Iterator files = fileItemList.iterator();
            while (files.hasNext()) {
              FileItem thisFile = (FileItem)files.next();
              if (thisFile.getId() == project.getLogoId()){
          %>
          <%= thisFile.getFullImageFromAdmin(ctx) %>&nbsp;
          <%
              }
            }
          %>
          <ccp:evaluate if="<%= fileItemList.size() > 0 %>"><br /></ccp:evaluate>
          <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
          <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.PROJECT_IMAGE_FILES %>&liid=<%= project.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&popup=true"
             rel="shadowbox" title="Share an attachment">
            <% if (project.getLogoId() != -1) { %>
              Replace Image
            <%} else {%>
              Attach Image
            <%}%>
          </a>
          </label>
          <input type="hidden" id="attachmentList" name="attachmentList" value="" />
          <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
          <% if (project.getLogoId() != -1) { %>
            <input type="hidden" name="logoId" value="<%= project.getLogoId() %>" />
          <%}%>
     </c:if>
    <%-- End Project Description --%>
    </fieldset>
    <%-- End Additional Information --%>
    <c:if test='${showIsOwner == "true"}'>
      <fieldset id="Claim Project">
        <legend>
          <c:choose>
            <c:when test='${empty labelMap["claimListing"]}'>
              Claim this Listing
            </c:when>
            <c:otherwise>
              <c:out value='${labelMap["claimListing"]}'/>
            </c:otherwise>
          </c:choose>
        </legend>
        <label for="owner">
          <input type="checkbox"class="checkbox" name="owner" id="owner" value="${user.id}"<ccp:evaluate if="<%= project.getOwner() > -1 %>"> checked</ccp:evaluate> />
          <c:choose>
            <c:when test='${empty labelMap["areYouTheOwner"]}'>
              I represent this listing and want more information on getting full access to its profile
            </c:when>
            <c:otherwise>
              <c:out value='${labelMap["areYouTheOwner"]}'/>
            </c:otherwise>
          </c:choose>
        </label>
      </fieldset>
    </c:if>
    <input type="hidden" name="token" value="${clientType.token}" />
    <input type="submit" class="submit" name="save" value="<ccp:label name="button.save">Save</ccp:label>" />
  </form>
</div>
