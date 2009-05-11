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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<jsp:useBean id="DepartmentList" class="com.concursive.connect.web.utils.LookupList" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    if (form.firstName.value == "" || 
        form.lastName.value == "" || 
        form.company.value == "" || 
        form.email.value == "" ||
        form.password.value == "") {
      alert("Please fill out all the fields in the form, then try submitting your information again.");
      form.firstName.focus();
      return false;
    } else {
      return true;
    }
  }
</script>
<body onLoad="document.editUser.username.focus();">
<form name="editUser" method="post" action="<%= ctx %>/Admin.do?command=InsertUser" onSubmit="return checkForm(this);">
<table border="0" width="100%" cellspacing="0" cellpadding="0">
  <tr bgcolor="#E4EBFD">
    <td colspan="2" align="center" valign="top">
      <font color="#000000"><b>Add a new user.</b></font>
    </td>
  </tr>
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">&nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066">&nbsp;</font>
    </td>
  </tr>
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">Username: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="text" name="username"></font>
    </td>
  </tr>
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">User Password: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="password" name="password"></font>
    </td>
  </tr>   
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">First Name: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="text" name="firstName" value="<%= toHtmlValue(User.getFirstName()) %>"></font>
    </td>
  </tr>       
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">Last Name: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="text" name="lastName" value="<%= toHtmlValue(User.getLastName()) %>"></font>
    </td>
  </tr>    
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">Company: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="text" name="company" value="<%= toHtmlValue(User.getCompany()) %>"></font>
    </td>
  </tr>
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">Department: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><%= DepartmentList.getHtmlSelect("departmentId", User.getDepartmentId()) %></font>
    </td>
  </tr>    
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">Email: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="text" name="email" value="<%= toHtmlValue(User.getEmail()) %>"></font>
    </td>
  </tr>  
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">Permissions: &nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="checkbox" name="personalView" value="ON">Personal View</font>
    </td>
  </tr>  
  <tr>
    <td align="right" bgcolor="#F4F7FF">
      <font color="#000066">&nbsp;</font>
    </td>
    <td align="left" bgcolor="#F4F7FF">
      <font color="#000066"><input type="checkbox" name="enterpriseView" value="ON">Enterprise View</font>
    </td>
  </tr>
</table>    
<table border="0" width="100%" cellspacing="0" cellpadding="0">
  <tr>
    <td align="center" bgcolor="#F4F7FF">
      <br><input type="submit" value="Add User">
      <input type="button" value="Cancel" onClick="window.location.href='<%= ctx %>/Admin.do?command=Overview'">
    </td>
  </tr>
</table>
</form>
</body>
