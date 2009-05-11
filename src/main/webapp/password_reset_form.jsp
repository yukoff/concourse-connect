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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/><%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    var message = "";
    var formTest = true;
    if (form.email.value == "") {
      message += "- Email address is required\r\n";
      formTest = false;
    } else {
      if (!checkEmail(form.email.value)) {
        message += "- The entered email address is invalid.  Make sure there are no invalid characters\r\n";
        formTest = false;
      }
    }
    if (formTest) {
      return true;
    } else {
      alert("The form could not be submitted:\r\n" + message);
      return false;
    }
  }
</script>
<body onLoad="document.register.email.focus()">
<div class="portletWrapper passwordResetForm">
  <div class="formContainer">
    <div class="leftColumn">
      <form name="register" method="post" action="<%= ctx %>/ResetPassword.do?command=Reset" onSubmit="return checkForm(this);">
      <c:if test="${'true' eq param.popup || 'true' eq popup}">
        <input type="hidden" name="popup" value="true" />
      </c:if>
      <%= showError(request, "actionError", false) %>
      <fieldset id="Password Request Form">
      <legend>Password Request Form</legend>
      <label for="email">Email Address<span class="required">*</span></label>
			<span class="error"><%= showAttribute(request, "emailError") %></span>
      <input type="text" name="email" id="email" value="<%= toHtmlValue(request.getParameter("email")) %>" />
      </fieldset>
      <input type="submit" class="submit" systran="yes" border="0" alt="Continue" name="Continue" />
      </form>
    </div>
    <div class="rightColumn">
      <h3>Reset your password</h3>
      <p>Enter the email address that you use to log in. You will receive an email with a temporary password. Once you log
      in, you'll create a new password for your account.</p>
    </div>
  </div>
</div>
</body>
