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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<jsp:useBean id="CountrySelect" class="com.concursive.connect.web.utils.CountrySelect" scope="request"/>
<jsp:useBean id="CompanySizeSelect" class="com.concursive.connect.web.utils.CompanySizeSelect" scope="request"/>
<jsp:useBean id="CompanyRevenueSelect" class="com.concursive.connect.web.utils.CompanyRevenueSelect" scope="request"/>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="contactUs" class="com.concursive.connect.web.modules.contactus.dao.ContactUsBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" TYPE="text/javascript">
  function checkForm(form) {
    var message = "";
    var formTest = true;
    if (form.nameFirst.value == "") {
      message += "- First Name is required\r\n";
      formTest = false;
    }
    if (form.nameLast.value == "") {
      message += "- Last Name is required\r\n";
      formTest = false;
    }
    if (form.email.value == "") {
      message += "- Email address is required\r\n";
      formTest = false;
    } else {
      if (!checkEmail(form.email.value)) {
        message += "- The entered email address is invalid.  Make sure there are no invalid characters\r\n";
        formTest = false;
      }
    }
    if (form.description.value == "") {
      message += "- A question or comment is required\r\n";
      formTest = false;
    }
    if (form.captcha.value == "") {
      message += "- Please don't forget to input the validation image\r\n";
      formTest = false;
    }
    if (formTest) {
      return true;
    } else {
      alert("The form could not be submitted:\r\n" + message);
      return false;
    }
  }
  function newCaptcha() {
    document.contactUs.captimg.src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Captcha.png?" + Math.random();
  }
</script>
<form action="<%= ctx %>/contact-us?command=Send&auto-populate=true" method="post" name="contactUs" onSubmit="return checkForm(this);">
<div class="spacerContainer">
  <div class="formContainer portletWindowBackground">
    <div class="leftColumn">
      <fieldset>
      <legend>Contact Us</legend>
        <h3>Who Would You Like to Contact?</h3>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: Sales"/>
            Sales
          </label>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: Support"/>
            Support
          </label>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: Services"/>
            Services
          </label>
          <label>
            <input type="radio" class="checkbox" name="formData" value="Contact reason: General"/>
            General Info
          </label>
        <h3>Tell Us About Yourself</h3>
          <label>First Name <span class="required">*</span></label>
          <%= showAttribute(request, "nameFirstError") %>
          <input type="text" name="nameFirst" value="<%= toHtmlValue(contactUs.getNameFirst()) %>"/>
          <label>Last Name <span class="required">*</span></label>
          <%= showAttribute(request, "nameLastError") %>
          <input type="text" name="nameLast" value="<%= toHtmlValue(contactUs.getNameLast()) %>"/>
          <label>Email Address <span class="required">*</span></label>
          <%= showAttribute(request, "emailError") %>
          <input type="text" name="email" value="<%= toHtmlValue(contactUs.getEmail()) %>"/>
          <label>Organization</label>
          <%= showAttribute(request, "organizationError") %>
          <input type="text" name="organization" value="<%= toHtmlValue(contactUs.getOrganization()) %>"/>
          <label>Phone</label>
          <%= showAttribute(request, "businessPhoneError") %>
          <input type="text" name="businessPhone" value="<%= toHtmlValue(contactUs.getBusinessPhone()) %>"/>
          <label>Extension</label>
          <input type="text" name="businessPhoneExt" value="<%= toHtmlValue(contactUs.getBusinessPhoneExt()) %>"/>
          <label>Address</label>
          <%= showAttribute(request, "addressLine1Error") %>
          <input type="text" name="addressLine1" value="<%= toHtmlValue(contactUs.getAddressLine1()) %>"/>
          <label>Address 2</label>
          <input type="text" name="addressLine2" value="<%= toHtmlValue(contactUs.getAddressLine2()) %>"/>
          <label>City</label>
          <%= showAttribute(request, "cityError") %>
          <input type="text" name="city" value="<%= toHtmlValue(contactUs.getCity()) %>"/>
          <label>State</label>
          <%= showAttribute(request, "stateError") %>
          <input type="text" name="state" value="<%= toHtmlValue(contactUs.getState()) %>"/>
          <label>Postal Code</label>
          <%= showAttribute(request, "postalCodeError") %>
          <input type="text" name="postalCode" value="<%= toHtmlValue(contactUs.getPostalCode()) %>"/>
          <label>Country or area <span class="required">*</span></label>
          <%= showAttribute(request, "countryError") %>
          <%
          String selected_country = contactUs.getCountry();
          if(selected_country == null) {
              selected_country = "UNITED STATES";
          }
          %>
          <%= CountrySelect.getHtml("country", selected_country) %>
         <label>Please input the disguised word<span class="required">*</span></label>
        <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Captcha.png" id="captimg" name="captimg">
        <span class="characterCounter">Trouble reading this? <a style="padding-left:10px;" href="javascript:newCaptcha();">Try another word...</a></span>
        <input type="text" name="captcha">
        <label>Your Comments <span class="required">*</span> </label>
        <%= showAttribute(request, "descriptionError") %>
        <textarea name="description" class="height100"><%= toString(contactUs.getDescription()) %></textarea>
      </fieldset>
      <input type="hidden" name="after_submit" value="after_submit_url"/>
      <input type="submit" value="submit" name="submit" class="submit"/>
    </div>
    <%-- TODO: Use same format as profile portlet and detect if data is present before displaying --%>
    <div class="rightColumn">
      <h2><c:out value="${requestMainProfile.title}"/></h2>
      <address>
        <span><c:out value="${requestMainProfile.addressToAndLocation}"/></span>
        <span><c:out value="${requestMainProfile.businessPhone} ${requestMainProfile.businessPhoneExt}"/></span>
      </address>
    </div>
  </div>
</div>
</form>

