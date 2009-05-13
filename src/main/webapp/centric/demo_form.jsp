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
  ~ under the GNU Affero General Public License. Ê
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
<%@ page import="com.concursive.connect.web.utils.HtmlSelectLanguage"%>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="CountrySelect" class="com.concursive.connect.web.utils.CountrySelect" scope="request"/>
<jsp:useBean id="CompanySizeSelect" class="com.concursive.connect.web.utils.CompanySizeSelect" scope="request"/>
<jsp:useBean id="CompanyRevenueSelect" class="com.concursive.connect.web.utils.CompanyRevenueSelect" scope="request"/>
<jsp:useBean id="demoBean" class="com.concursive.connect.web.modules.demo.beans.DemoBean" scope="request"/>
<%@ include file="../initPage.jsp" %>
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
<div class="spacerContainer">
  <div class="portletWindowBackground">
      <div id="pagecontent">
        <div class="formContainer">
        <div class="leftColumn">
          <h4>ConcourseSuite account activation form.</h4>
          <h5>Just provide a few pieces of information, and you'll be using ConcourseSuite in no time.</h5>
          <%= showError(request, "actionError", false) %>
          <div>
            <form action="<%= ctx %>/Activation.do?command=Save&amp;auto-populate=true"
                  method="post" name="contactUs" onSubmit="return checkForm(this);">
              <%-- Name --%>
              <div class="half">
                <h6><em>*</em>First Name</h6>
                <input type="text" name="nameFirst" maxlength="50" class="bighalf"
                       value="<%= toHtmlValue(demoBean.getNameFirst()) %>"/>
                <%= showAttribute(request, "nameFirstError") %>
              </div>
              <div class="half">
                <h6><em>*</em>Last Name</h6>
                <input type="text" name="nameLast" maxlength="50" class="smallhalf"
                       value="<%= toHtmlValue(demoBean.getNameLast()) %>"/>
                <%= showAttribute(request, "nameLastError") %>
              </div>

              <div style="clear: both">&nbsp;</div>

              <h6><em>*</em>Address</h6>
              <input type="text" name="addressLine1" maxlength="80" class="fullsize"
                     value="<%= toHtmlValue(demoBean.getAddressLine1()) %>"/>
              <%= showAttribute(request, "addressLine1Error") %>
              <h6>Address 2</h6>
              <input type="text" name="addressLine2" maxlength="80" class="fullsize"
                     value="<%= toHtmlValue(demoBean.getAddressLine2()) %>"/>

              <div class="half">
                <h6><em>*</em>City</h6>
                <input type="text" name="city" maxlength="50" class="bighalf"
                       value="<%= toHtmlValue(demoBean.getCity()) %>"/>
                <%= showAttribute(request, "cityError") %>
              </div>
              <div class="half">
                <h6><em>*</em>State</h6>
                <input type="text" name="state" maxlength="50" class="smallhalf"
                       value="<%= toHtmlValue(demoBean.getState()) %>"/>
                <%= showAttribute(request, "stateError") %>
              </div>
              <div class="half">
                <h6><em>*</em>Postal Code</h6>
                <input type="text" name="postalCode" maxlength="50" class="bighalf"
                       value="<%= toHtmlValue(demoBean.getPostalCode()) %>"/>
                <%= showAttribute(request, "postalCodeError") %>
              </div>

              <h6><em>*</em>Country</h6>
              <%
                String selected_country = demoBean.getCountry();
                if (selected_country == null) {
                  selected_country = "UNITED STATES";
                }
              %>
              <%= CountrySelect.getHtml("country", selected_country) %>
              <%= showAttribute(request, "countryError") %>

              <div style="clear: both">&nbsp;</div>

              <div class="half">
                <h6><em>*</em>Phone</h6>
                <input type="text" name="phone" maxlength="40" class="smallhalf"
                       value="<%= toHtmlValue(demoBean.getPhone()) %>"/>
                <%= showAttribute(request, "phoneError") %>
              </div>

              <div class="half">
                <h6>Extension</h6>
                <input type="text" name="phoneExt" maxlength="50" class="bighalf"
                       value="<%= toHtmlValue(demoBean.getPhoneExt()) %>"/>
              </div>
              <div class="half">
                <h6><em>*</em>Email</h6>
                <input type="text" name="email" maxlength="100" class="smallhalf"
                       value="<%= toHtmlValue(demoBean.getEmail()) %>"/>
                <%= showAttribute(request, "emailError") %>
              </div>

              <div style="clear: both">&nbsp;</div>

              <div class="half">
                <h6><em>*</em>Organization</h6>
                <input type="text" name="companyName" maxlength="80" class="smallhalf"
                       value="<%= toHtmlValue(demoBean.getCompanyName()) %>"/>
                <%= showAttribute(request, "companyNameError") %>
              </div>
              <div class="half">
                <h6>Website</h6>
                <input type="text" name="website" maxlength="80" class="smallhalf"
                       value="<%= toHtmlValue(demoBean.getWebsite()) %>"/>
                <%= showAttribute(request, "websiteError") %>
              </div>

              <div style="clear: both">&nbsp;</div>

              <h6>Language:
                English - US (other language options coming soon)
              </h6>
              <%--
              <%= HtmlSelectLanguage.getSelect("language", demoBean.getLanguage()).getHtml() %>
              --%>
              <%= showAttribute(request, "languageError") %>

              <h6><em>*</em>Requested URL:
                http://
                <input type="text" name="requestedURL" maxlength="80" class="bighalf"
                       value="<%= toHtmlValue(demoBean.getRequestedURL()) %>"/>
                .ondemand.concursive.net
              </h6>
              <%= showAttribute(request, "requestedURLError") %>

              <div>
                <img style="margin:10px 0px;"
                     src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Captcha.png"
                     id="captimg" name="captimg" /><br />
                <h6><em>*</em>Please input the disguised word</h6>
                <input style="margin:6px 0px;" type="text" name="captcha"><br/>
                <h6>Trouble reading this? <a style="padding-left:10px;"
                                             href="javascript:newCaptcha();">Try another word...</a>
                </h6>
              </div>

              <h5 class="comments">Terms and conditions</h5>
              <a href="javascript:popURL('<%= ctx %>/PortalTerms.do?popup=true&printButton=true','Terms_and_Conditions','650','375','yes','yes');">Printer Friendly Page</a>
              <textarea rows="8" cols="80" WRAP="VIRTUAL" READONLY style="border-style: solid; border-width: 2;"><%@ include file="../terms.jsp" %></textarea>

              <div class="checkbox">
                <div>
                  <label>
                  <input type="checkbox" name="agreement" value="yes"<ccp:evaluate
                      if="<%= demoBean.isAgreement() %>"> checked</ccp:evaluate>/>
                  Indicate that you accept the terms and conditions by checking here</label>
                  <%= showAttribute(request, "agreementError") %>
                </div>
              </div>

              <p>
                Note: Free accounts are valid for one year for up to 100 users.
                Storage, bandwidth and other limitations apply.
              </p>
              <input type="submit" value="Create account" name="submit" class="submit" />
            </form>
          </div>
        </div>

        <div class="rightColumn">
          <br/><br/>
          <address>
            <h2>Concursive Corporation</h2><br/>
            223 East City Hall Avenue<br/>Suite 212<br/>
            Norfolk, VA 23510<br/><br/>
            Phone: 877.818.8108<br/>
            Fax: 757.627.8773
          </address>
        </div>
      </div>
    </div>
  </div>
</div>
