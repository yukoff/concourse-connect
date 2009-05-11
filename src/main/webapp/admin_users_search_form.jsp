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
<%@ page import="com.concursive.connect.web.utils.HtmlSelectChoice" %>
<jsp:useBean id="adminSearchUserForm" class="com.concursive.connect.web.modules.admin.beans.UserSearchBean" scope="session"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript">
  function clearForm() {
    document.forms['searchForm'].email.value="";
    document.forms['searchForm'].firstName.value="";
    document.forms['searchForm'].lastName.value="";
    document.forms['searchForm'].company.value="";
    document.forms['searchForm'].enabled.options.selectedIndex = 0;
    document.forms['searchForm'].registered.options.selectedIndex = 0;
    document.forms['searchForm'].expired.options.selectedIndex = 0;
    document.forms['searchForm'].admin.options.selectedIndex = 0;
    document.forms['searchForm'].contentEditor.options.selectedIndex = 0;
    document.forms['searchForm'].email.focus();
  }
</script>
<body onLoad="javascript:document.searchForm.email.focus()">
<div class="admin-portlet">
  <div class="portlet-section-header">
    <h1>Manage Users</h1>
    <p>Back to <a href="<%= ctx %>/admin">System Administration</a></p>
  </div>
  <div class="portlet-section-body">
    <div class="formContainer">
      <form name="searchForm" action="<%= ctx %>/AdminUsers.do?command=Search&auto-populate=true&resetList=true" method="post">
        <fieldset id="search-users">
          <legend>Search Users</legend>
          <label for="email">Email Address</label>
          <input type="text" name="email" id="email" value="<%= toHtmlValue(adminSearchUserForm.getEmail()) %>" />
          <label for="firstName">First Name</label>
          <input type="text" name="firstName" id="firstName" value="<%= toHtmlValue(adminSearchUserForm.getFirstName()) %>" />
          <label for="lastName">Last Name</label>
          <input type="text" name="lastName" id="lastName" value="<%= toHtmlValue(adminSearchUserForm.getLastName()) %>" />
          <label for="company">Company</label>
          <input type="text" name="company" id="company" value="<%= toHtmlValue(adminSearchUserForm.getCompany()) %>" />
        </fieldset>
        <fieldset id="member-attributes">
          <legend>Member Attributes</legend>
          <label for="">Account Enabled</label>
          <%= HtmlSelectChoice.getSelect("enabled", adminSearchUserForm.getEnabled()) %>
          <label for="">Registered</label>
          <%= HtmlSelectChoice.getSelect("registered", adminSearchUserForm.getRegistered()) %>
          <label for="">Expired</label>
          <%= HtmlSelectChoice.getSelect("expired", adminSearchUserForm.getExpired()) %>
          <label for="">Admin Access</label>
          <%= HtmlSelectChoice.getSelect("admin", adminSearchUserForm.getAdmin()) %>
          <label for="">Web Content Author</label>
          <%= HtmlSelectChoice.getSelect("contentEditor", adminSearchUserForm.getContentEditor()) %>
        </fieldset>
        <input type="submit" name="Search" value="Search" class="submit" />
        <input type="button" name="Clear" value="Clear" onClick="clearForm()" class="cancel" />
      </form>
    </div>
  </div>
</div>
</body>

