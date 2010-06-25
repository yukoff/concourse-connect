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
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="registrationBean" class="com.concursive.connect.web.modules.setup.beans.SetupRegistrationBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" TYPE="text/javascript" SRC="javascript/checkEmail.js"></script>
<script language="JavaScript" TYPE="text/javascript" SRC="javascript/checkCheckbox.js"></script>
<script language="JavaScript" TYPE="text/javascript" SRC="javascript/checkNumber.js"></script>
<script type="text/javascript" language="JavaScript">
  function checkForm(form) {
    valid = true;
    message = "";
    if (form.nameFirst.value.length == 0) {
      message += label("check.firstname","- First name is a required field\r\n");
      valid = false;
    }
    if (form.nameLast.value.length == 0) {
      message += label("check.lastname","- Last name is a required field\r\n");
      valid = false;
    }
    if (form.organization.value.length == 0) {
      message += label("check.organization","- Organization is a required field\r\n");
      valid = false;
    }
    if (form.email.value.length == 0) {
      message += label("check.emailaddress","- Email address is a required field\r\n");
      valid = false;
    }
    if (!checkEmail(form.email.value)) {
      message += label("check.emailaddress.invalid","- Email address is invalid.  Make sure there are no invalid characters\r\n");
      valid = false;
    }
    if (getSelectedCheckbox(form.proxy).length > 0) {
      if (form.proxyHost.value.length == 0) {
        message += label("check.proxyhost","- Proxy host is required field when proxy is checked\r\n");
        valid = false;
      }
      if (form.proxyPort.value.length == 0) {
        message += label("check.proxyhost","- Proxy host is required field when proxy is checked\r\n");
        valid = false;
      }
      if (form.proxyPort.value.length > 0 && !checkNumber(form.proxyPort.value)) {
        message += label("check.proxyport.number","- Proxy port must be a number\r\n");
        valid = false;
      }
    }
    if (!valid) {
      alert(label("check.form","Form could not be submitted, please check the following:\r\n\r\n") + message);
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
</script>
<form action="${ctx}/SetupRegistration.do?command=SaveRegistration" name="setupForm" method="post" onSubmit="return checkForm(this)" style="margin:0; width:100%; background:#efefef; ">
  <input type="hidden" name="auto-populate" value="true" />
  <div style="padding:15px 0 0 0; margin:0">
    <div style="width:700px; position:relative; margin:0 auto">
      <img src="${ctx}/images/setup/step-2-header.jpg" alt="Step Two" style="margin:0 auto; display:block">
      <br style="clear:both" />
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Registration Information</h1>
      <center style="padding-bottom:10px"><%= showError(request, "actionError", false) %></center>
      <div style="font-size:small; width:700px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <div style="float:right; width:45%">
          <h2>Read Me...</h2>
          <p>
            This application uses remote services and connects securely using HTTPS directly to Concursive Corporation.
            Remote services enhance the features of the application.  In order to use Concursive's
            services, each instance of the application needs to retrieve a license.  This license can also be used
            for developing, ordering and turning on additional services.
          </p>
          <p>To install a services license, please fill out the requested information.</p>
          <ul>
            <li>The information indicated will be sent to Concursive Corporation and processed</li>
            <li>A license file will be sent by email to the email address specified</li>
            <li>Anonymous email addresses will not be accepted when processing licenses</li>
            <li>In good faith, Concursive Corporation provides this software and entitles you to use it according to the license agreement</li>
            <li>Your email address and contact information will not be provided to others without your consent</li>
          </ul>
          <cite>The Concursive Team</cite>
        </div>
        <div class="formContainer" style="margin-right:50%; width:50%;font-size:small; clear:none; padding-top:10px">
          <fieldset style="border-style:none solid none none">
            <legend style="padding:0;">Registration</legend>
            <label for="nameFirst"><font color="red">*</font>First Name:</label>
            <b><%= showAttribute(request, "nameFirstError") %></b>
            <input type="text" size="20" name="nameFirst" value="<%= toHtmlValue(registrationBean.getNameFirst()) %>" />
            <label for="nameLast"><font color="red">*</font>Last Name:</label>
            <b><%= showAttribute(request, "nameLastError") %> </b>
            <input type="text" size="20" name="nameLast" value="<%= toHtmlValue(registrationBean.getNameLast()) %>" />
            <label for="organizaion"><font color="red">*</font>Organization Name:</label>
            <b><%= showAttribute(request, "organizationError") %></b>
            <input type="text" size="30" name="organization" value="<%= toHtmlValue(registrationBean.getOrganization()) %>" />
            <label><font color="red">*</font>Email Address:</label>
            <b><%= showAttribute(request, "emailError") %></b>
            <input type="text" size="40" maxlength="255" name="email" value="<%= toHtmlValue(registrationBean.getEmail()) %>" />
            <label>O/S:
            <%= toHtml(registrationBean.getOs()) %></label>
            <label>JVM:
            <%= toHtml(registrationBean.getJava()) %></label>
            <label>Web Server:
            <%= toHtml(registrationBean.getWebserver()) %></label>
            <label><font color="red">*</font>Profile for Reference:</label>
            <b><%= showAttribute(request, "profileError") %></b>
            <input type="text" size="40" maxlength="255" name="profile" value="<%= toHtmlValue(registrationBean.getProfile()) %>" />
            <fieldset style="margin:10px 0">
              <input type="checkbox" name="ssl" id="ssl" value="ON" <ccp:evaluate if="<%= registrationBean.getSsl() %>" >checked</ccp:evaluate> />
              <label>Use SSL (port 443) for sending information</label>
              <br />
              <input type="checkbox" name="proxy" id="proxy" value="ON" <ccp:evaluate if="<%= registrationBean.getProxy() %>" >checked</ccp:evaluate> />
              <label style="clear:right">Use proxy server to make internet connection</label>
            </fieldset>
            <label>Proxy Host:</label>
            <input type="text" size="30" name="proxyHost" value="<%= toHtmlValue(registrationBean.getProxyHost()) %>" />
            <label>Proxy Port:</label>
            <input type="text" size="5" name="proxyPort" value="<%= toHtmlValue(registrationBean.getProxyPort()) %>" />
            <label>Proxy Username:</label>
            <input type="text" size="30" name="proxyUsername" value="<%= toHtmlValue(registrationBean.getProxyUsername()) %>" />
            <label>Proxy Password:</label>
            <input type="password" size="30" name="proxyPassword" value="<%= toHtmlValue(registrationBean.getProxyPassword()) %>" />
          </fieldset>
        </div>
        <br style="clear:both">
      </div>
      <input type="submit"  value="Continue" style="float:right; margin-top:10px" />
      <br style="clear:both">
    </div>
    <br style="clear:both">
  </div>
  <br style="clear:both">
</form>
