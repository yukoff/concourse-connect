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
<%@ page import="java.util.*" %>
<jsp:useBean id="password" class="com.concursive.connect.web.modules.login.beans.Password" scope="request"/>
<%@ include file="initPage.jsp" %>
<body onLoad="document.inputForm.password.focus();">
<script language="JavaScript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.password.value == "") {
      messageText += "- Password field is required\r\n";
      formTest = false;
    }
    if (form.newPassword1.value == "") {
      messageText += "- New Password field is required\r\n";
      formTest = false;
    }
    if (form.newPassword2.value == "") {
      messageText += "- Verify New Password field is required\r\n";
      formTest = false;
    }
    if (form.newPassword1.value != form.newPassword2.value) {
      messageText += "- New password fields must match\r\n";
      formTest = false;
    }
    if (formTest == false) {
      messageText = "The password form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form name="inputForm" method="post" action="<%= ctx %>/Password.do?command=SavePassword&auto-populate=true" onSubmit="return checkForm(this);">
<%= showError(request, "actionError", false) %>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        <ccp:label name="userProfile.password.changePassword">Change Password</ccp:label>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="userProfile.password.currentPassword">Current Password</ccp:label></td>
      <td>
        <input type="password" name="password" value=""/>
        <font color="red">*</font>
        <%= showAttribute(request, "passwordError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="userProfile.password.newPassword">New Password</ccp:label></td>
      <td>
        <input type="password" name="newPassword1" value=""/>
        <font color="red">*</font>
        <%= showAttribute(request, "newPasswordError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="userProfile.password.verifyNewPassword">Verify New Password</ccp:label></td>
      <td>
        <input type="password" name="newPassword2" value=""/>
        <font color="red">*</font>
      </td>
    </tr>
  </tbody>
</table>
<input type="submit" value="<ccp:label name="button.update">Update</ccp:label>"/>
<input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.location.href='<%= ctx %>/Profile.do'"/>
</form>
</body>
