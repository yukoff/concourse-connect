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
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="LoginBean" class="com.concursive.connect.web.modules.login.beans.LoginBean" scope="request"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="initPage.jsp" %>
<%-- Temp. fix for Weblogic --%>
<%
boolean sslEnabled = "true".equals(applicationPrefs.get("SSL"));
%>
<script type="text/javascript">
  function checkForm(form) {
    if ((form.username.value == "") || (form.password.value == "")) {
      alert("Please fill out all the fields in the form, then try submitting your information again.");
      form.username.focus();
      return false;
    } else {
      return true;
    }
  }
  function focusForm(form) {
    form.username.focus();
    return false;
  }
</script>
<body onLoad="document.loginForm.username.focus()">
  <div class="portletWrapper">
    <div class="formContainer">
      <div class="leftColumn">
        <form name="loginForm" method="post" action="<%= ctx %>/Login.do?command=Login&auto-populate=true" onSubmit="return checkForm(this);">
          <%-- Temp. fix for Weblogic --%>
          <% String loginBeanActionError = LoginBean.getError("actionError"); %>
          <ccp:evaluate if="<%= loginBeanActionError != null %>"> <img src="<%= ctx %>/images/error.gif" border="0" align="absmiddle"/> <font color="red"><%= toHtml(LoginBean.getError("actionError")) %></font><br>
          </ccp:evaluate>
          <fieldset id="sign-in-form">
          <legend>
          <ccp:label name="login.signIn">Sign in</ccp:label>
          </legend>
          <label for="username">
          <ccp:label name="login.userName">Email Address</ccp:label>
          </label>
          <input type="text" name="username" id="username" value="<%= toHtmlValue(LoginBean.getUsername()) %>">
          <label for="password">
          <ccp:label name="login.password">Password</ccp:label>
          </label>
          <input type="password" name="password" id="password">
          <label for="addCookie"><input type="checkbox" class="checkbox" name="addCookie" id="addCookie" value="true" />Keep me logged in for two weeks</label>
          <c:if test="${!empty param.redirectTo}">
            <input type="hidden" name="redirectTo" value="${param.redirectTo}" />
            <span><ccp:label name="login.redirectAfterLogin">After login, you will be returned to the previous page</ccp:label></span>
          </c:if>
          </fieldset>
          <input type="submit" class="submit" alt="Login" name="Login" value="Login" />
          <c:if test="${'true' eq param.popup || 'true' eq popup}">
            <input type="hidden" name="popup" value="true" />
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:if>
          <span><a href="ResetPassword.do">
            <ccp:label name="login.forgotPassword">Forgot your password?</ccp:label>
          </a></span>
        </form>
      </div>
      <div class="rightColumn">
        <% boolean appPrefsRegister = "true".equals(applicationPrefs.get("REGISTER")); %>
        <ccp:evaluate if="<%= appPrefsRegister && !clientType.getMobile() %>">
          <form name="register-redirect" method="post" action="<ccp:evaluate if="<%= sslEnabled %>">https://<%= getServerUrl(request) %>/</ccp:evaluate>register" >
            <h3>Register now</h3>
            <p>Not a member? Join now!</p>
            <input type="submit" class="submit" value="Register" />
            <%-- TODO: Allow for pop-up registration, so once the user registers from the sign-in page, they can login quickly

              <a href="<ccp:evaluate if="<%= sslEnabled %>">https://<%= getServerUrl(request) %>/</ccp:evaluate>register" class="submit" rel="shadowbox;wdith=500">Set Up An Account</a>

            --%>
          </form>
        </ccp:evaluate>
      <ccp:evaluate if="<%= !appPrefsRegister %>"> </ccp:evaluate>
      </div>
    </div>
  </div>
</body>
