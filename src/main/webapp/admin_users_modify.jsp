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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="thisUser" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Temp. fix for Weblogic --%>
<% 
boolean allowInvite = "true".equals(applicationPrefs.get("INVITE"));
String detailsUrl = ctx + "/AdminUserDetails.do?command=Details&id=" + thisUser.getId();
String projectsUrl = ctx + "/AdminUserDetails.do?command=Projects&id=" + thisUser.getId();
String loginsUrl = ctx + "/AdminUserDetails.do?command=Logins&id=" + thisUser.getId();
String languagesUrl = ctx + "/AdminUserDetails.do?command=Languages&id=" + thisUser.getId();
String webSitesUrl = ctx + "/AdminUserDetails.do?command=WebSites&id=" + thisUser.getId();
%>
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (document.detailForm.firstName.value == "" && document.detailForm.lastName.value == "") {
      messageText += "- Name is a required field\r\n";
      formTest = false;
    }
    if (document.detailForm.email.value == "") {
      messageText += "- Email Address is a required field\r\n";
      formTest = false;
    }
    //Check date field
    if ((document.detailForm.expiration.value != "") && (!checkDate(document.detailForm.expiration.value))) {
      messageText += "- Expiration date was not properly entered\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<form name="detailForm" action="<%= ctx %>/AdminUserDetails.do?command=Save&id=<%= thisUser.getId() %>&auto-populate=true" method="post" onSubmit="return checkForm(this);">
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminUsers.do">Manage Users</a> >
<a href="<%= ctx %>/AdminUsers.do?command=Search">Search Results</a> >
User Details<br />
<br />
<table border="0" width="100%">
  <tr>
    <td>
      <img src="<%= ctx %>/images/icons/stock_new-bcard-16.gif" border="0" align="absMiddle" />
    </td>
    <td width="100%">
      <strong><%= toHtml(thisUser.getNameFirstLast()) %></strong>
    </td>
    <td align="right" nowrap>
      (Type: <ccp:evaluate if="<%= thisUser.getAccessAdmin() %>">Administrator</ccp:evaluate><ccp:evaluate if="<%= !thisUser.getAccessAdmin() %>">User</ccp:evaluate>)
      &nbsp;
    </td>
  </tr>
</table>
<div class="tabs-te" id="toptabs">
<table cellpadding="4" cellspacing="0" border="0" width="100%">
  <tr>
    <ccp:tabbedMenu text="Details" key="details" value="details" url="<%= detailsUrl %>"/>
    <ccp:tabbedMenu text="Projects" key="projects" value="details" url="<%= projectsUrl %>"/>
    <ccp:tabbedMenu text="Logins" key="logins" value="details" url="<%= loginsUrl %>"/>
    <ccp:tabbedMenu text="Languages" key="languages" value="details" url="<%= languagesUrl %>"/>
    <ccp:tabbedMenu text="Web Sites" key="web-sites" value="details" url="<%= webSitesUrl %>"/>
    <td width="100%" style="background-image: none; background-color: transparent; border: 0; border-bottom: 1px solid #666; cursor: default;">&nbsp;</td>
    </tr>
</table>
</div>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Contact Information
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel">First Name</td>
      <td>
        <input type="text" name="firstName" value="<%= toHtmlValue(thisUser.getFirstName()) %>" />
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Last Name</td>
      <td>
        <input type="text" name="lastName" value="<%= toHtmlValue(thisUser.getLastName()) %>" />
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Organization</td>
      <td>
        <input type="text" name="company" value="<%= toHtmlValue(thisUser.getCompany()) %>" />
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel" valign="top">Email Address</td>
      <td>
        <input type="text" name="email" value="<%= toHtmlValue(thisUser.getEmail()) %>" /><br />
        Note: The user's email address is used for logging in;
        All email for the user is sent to this address
      </td>
    </tr>
  </tbody>
</table>

<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        System Access
      </th>
    </tr>
  </thead>
  <tbody>
    <%-- Show enabled information --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Login</td>
      <td>
        <input type="checkbox" name="enabled" value="on"
        <ccp:evaluate if="<%= thisUser.getEnabled() %>">
          checked
        </ccp:evaluate>
        />
        Account is enabled and user can login
      </td>
    </tr>

    <%-- Show expiration information --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Access Expiration</td>
      <td>
        <input type="text" name="expiration" size="10" value="<ccp:tz timestamp="<%= thisUser.getExpiration() %>" dateOnly="true"/>">
        <a href="javascript:popCalendar('detailForm', 'expiration', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        at
        <ccp:timeSelect baseName="expiration" value="<%= thisUser.getExpiration() %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
      </td>
    </tr>

    <%-- Admin access --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Admin</td>
      <td>
        <input type="checkbox" name="accessAdmin" value="on"
        <ccp:evaluate if="<%= thisUser.getAccessAdmin() %>">
          checked
        </ccp:evaluate>
        />
        User has administrative access to the system
      </td>
    </tr>

    <%-- Invitation access --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Invitations</td>
      <td>
        <input type="checkbox" name="accessInvite" value="on"
        <ccp:evaluate if="<%= thisUser.getAccessInvite() %>">
          checked
        </ccp:evaluate>
        />
        User can invite non-users to the system
      </td>
    </tr>
    <%-- Create project access --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Projects</td>
      <td>
        <input type="checkbox" name="accessAddProjects" value="on"
        <ccp:evaluate if="<%= thisUser.getAccessAddProjects() %>">
          checked
        </ccp:evaluate>
        />
        User can create projects and reports in the system
      </td>
    </tr>

    <%-- Access to Contacts --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Contacts</td>
      <td>
        <input type="checkbox" name="accessViewAllContacts" value="on"
        <ccp:evaluate if="<%= thisUser.getAccessViewAllContacts() %>">
          checked
        </ccp:evaluate>
        />
        User can view the shared contact directory<br />
        <input type="checkbox" name="accessEditAllContacts" value="on"
        <ccp:evaluate if="<%= thisUser.getAccessEditAllContacts() %>">
          checked
        </ccp:evaluate>
        />
        User can edit the shared contact directory
      </td>
    </tr>

    <%-- Subscription Settings --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Subscriptions</td>
      <td>
        <input type="checkbox" name="watchForums" value="on"
        <ccp:evaluate if="<%= thisUser.getWatchForums() %>">
          checked
        </ccp:evaluate>
        />
        User receives emails of forum posts<br />
      </td>
    </tr>

    <%-- Document library access --%>
    <tr class="containerBody">
      <td class="formLabel" valign="top">Document Library Limit</td>
      <td>
        <input type="type" name="accountSize" value="<%= thisUser.getAccountSize() %>" size="5"/> MB<br />
        Note: &quot;-1&quot; indicates the user's total document storage is not limited
      </td>
    </tr>
  </tbody>
</table>

<input type="submit" value="Save" />
<input type="button" value="Cancel" onClick="window.location.href='<%= ctx %>/AdminUserDetails.do?command=Details&id=<%= thisUser.getId() %>'" />

