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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="LoginBean" class="com.concursive.connect.web.modules.login.beans.LoginBean" scope="request"/>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    valid = true;
    message = "";
    if ((form.username.value == "")) { 
      message += "- Username is a required field\r\n";
      valid = false;
    }
    if ((form.password.value == "")) { 
      message += "- Password is a required field\r\n";
      valid = false;
    }
    if (!valid) {
      alert("Form could not be submitted, please check the following:\r\n\r\n" + message);
      return false;
    }
    return true;
  }
</script>

<form name="configure" method="POST" action="<%= ctx %>/Login.do?command=Login&auto-populate=true" onSubmit="return checkForm(this)">
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:600px; height:600px; position:relative; margin:0 auto; background:#efefef">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Site Upgrade</h1>
      <h2 style="color:#5d9fbf; font-size:large; font-weight:normal; text-align:center">
        <img src="${ctx}/images/icons/exclamation.png" alt="Important" style="margin:0 5px 0 0">
        This site is currently being upgraded.
      </h2>
      <div style="float:left; font-size:small; width:700px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <div style="float:left; width:45%; border-right:2px solid #ddd; padding:0 10px 0 0">
          <h3>
            <c:choose>
              <c:when test="${!empty requestMainProfile}">
                <c:out value="${requestMainProfile.title}"/> Upgrade
              </c:when>
              <c:otherwise>
                Site Upgrade
              </c:otherwise>
            </c:choose>
          </h3>
          <p>You are receiving this message for one of several reasons:</p>
          <ul>
            <li>You are an administrator in the process of upgrading and you are ready
                to proceed with the upgrade.</li>
            <li>You are a user expecting to be able to login, however, at this time
                it appears the software is being upgraded by an administrator. If
                this screen persists, you might review any emails that you may have
                received from your administrator
                or you might contact your administrator for more information.</li>
            <li>If an upgrade was not planned, then there might be a configuration
                issue that needs to be resolved. If this is the case, then you might
                try proceeding with the upgrade process to verify your installation.</li>
          </ul>
          <p>If you have administrative access, then you are required to login to
          proceed with the upgrade process. To protect your data, you should perform
          the following steps before continuing:</p>
          <ul>
            <li>Backup the database</li>
            <li>Backup the file library</li>
          </ul>
        </div>
        <div class="formContainer" style="width:45%; float:left; margin-left:10px; clear:none">
          <h3>Login with an administrator account</h3>
          <i>If you have backed up your system and you are an administrator, continue with the upgrade process.</i>
          <ccp:evaluate if="<%= hasText(LoginBean.getMessage()) %>">
              <p style="color:#ff0000"><%= LoginBean.getMessage() %></p>
            </ccp:evaluate>
          <label>
            User Name <font color="red">*</font>:
          </label>
          <input type="text" size="20" maxlength="80" name="username" />
          <label>Password <font color="red">*</font>:</label>
          <input type="password" size="20" maxlength="20" name="password" />
          <input type="submit" value="Continue >"/>
        </div>
        <br />
      </div>
    </div>
  </div>
</form>
