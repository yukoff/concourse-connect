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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="contactUs" class="com.concursive.connect.web.modules.contactus.dao.ContactUsBean" scope="request"/>
<%@ include file="../initPage.jsp" %>
<script language="JavaScript">
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
<body onLoad="document.contactUs.nameFirst.focus()">
<form name="contactUs" method="post" action="<%= ctx %>/TranslationApplication.do?command=SubmitApplication&auto-populate=true" onSubmit="return checkForm(this);">
<a href="<%= ctx %>/Translation.do">Languages</a> >
Application<br />
<%= showError(request, "actionError") %>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Language Translator Application
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td class="formLabel">
        First Name
      </td>
      <td nowrap>
        <input type="text" size="30" maxlength="50" name="nameFirst" value="<%= toHtmlValue(contactUs.getNameFirst()) %>">
        <font color="red">*</font>
        <%= showAttribute(request, "nameFirstError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Last Name
      </td>
      <td nowrap>
        <input type="text" size="30" maxlength="50" name="nameLast" value="<%= toHtmlValue(contactUs.getNameLast()) %>">
        <font color="red">*</font>
        <%= showAttribute(request, "nameLastError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Email Address
      </td>
      <td nowrap>
        <input type="text" size="30" maxlength="255" name="email" value="<%= toHtmlValue(contactUs.getEmail()) %>">
        <font color="red">*</font>
        <%= showAttribute(request, "emailError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Organization Name
      </td>
      <td nowrap>
        <input type="text" size="30" maxlength="100" name="organization" value="<%= toHtmlValue(contactUs.getOrganization()) %>">
        <%= showAttribute(request, "organizationError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Language
      </td>
      <td nowrap>
        <input type="text" size="20" maxlength="100" name="language" value="<%= toHtmlValue(contactUs.getLanguage()) %>">
        <%= showAttribute(request, "languageError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel" valign="top">
        Tell us a little bit about yourself
      </td>
      <td nowrap>
        <table border="0" cellpadding="0" cellspacing="0" border="0" class="empty">
        <tr>
          <td>
            <textarea rows="8" name="description" cols="50" wrap="soft"><%= toString(contactUs.getDescription()) %></textarea>
          </td>
          <td valign="top">
            <font color="red">*</font>
            <%= showAttribute(request, "descriptionError") %>
          </td>
        </tr>
        </table>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel" valign="top">
        Validation
      </td>
      <td nowrap>
        <table border="0" cellpadding="0" cellspacing="0" border="0" class="empty">
        <tr>
          <td>
            <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/Captcha.png" id="captimg" name="captimg"><br />
            Please input the disguised word to help us process your
            request sooner:<br />
            <input type="text" name="captcha"><br />
            Trouble reading? <a href="javascript:newCaptcha();">Try another word</a>...
          </td>
          <td valign="top">
            <font color="red">*</font>
            <%= showAttribute(request, "captchaError") %>
          </td>
        </tr>
        </table>
      </td>
    </tr>
  </tbody>
</table>
<input type="submit" value="Submit" />
</form>
</body>