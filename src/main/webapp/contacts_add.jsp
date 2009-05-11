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
<%@ include file="initPage.jsp" %>
<jsp:useBean id="contact" class="com.concursive.connect.web.modules.contacts.dao.Contact" scope="request" />
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.firstName.value == "" &&
        form.lastName.value == "" &&
        form.organization.value == "") {
      messageText += "- Name and/or Organization is required\r\n";
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
<body onLoad="document.inputForm.firstName.focus()">
  <form method="post" name="inputForm" action="<%= ctx %>/Contacts.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);">
    <%= showError(request, "actionError", false) %>
    <table class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
            <%= contact.getId() == -1 ? "Add" : "Update"%> Contact Information
          </th>
        </tr>
      </thead>
      <tbody>
        <tr class="containerBody">
          <td class="formLabel">
            First Name
          </td>
          <td>
            <input type="text" name="firstName" size="30" maxlength="80" value="<%= toHtmlValue(contact.getFirstName()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Middle Name
          </td>
          <td>
            <input type="text" name="middleName" size="30" maxlength="80" value="<%= toHtmlValue(contact.getMiddleName()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Last Name
          </td>
          <td>
            <input type="text" name="lastName" size="35" maxlength="80" value="<%= toHtmlValue(contact.getLastName()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Organization
          </td>
          <td>
            <input type="text" name="organization" size="40" maxlength="255" value="<%= toHtmlValue(contact.getOrganization()) %>"><br />
            <input type="checkbox" name="isOrganization" value="ON" <%= contact.getIsOrganization() ? "checked" : "" %> /> File Record as Organization
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Job Title
          </td>
          <td>
            <input type="text" name="jobTitle" size="35" maxlength="255" value="<%= toHtmlValue(contact.getJobTitle()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Role
          </td>
          <td>
            <input type="text" name="role" size="35" maxlength="255" value="<%= toHtmlValue(contact.getRole()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Email 1
          </td>
          <td>
            <input type="text" name="email1" size="40" maxlength="255" value="<%= toHtmlValue(contact.getEmail1()) %>">
            (primary)
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Email 2
          </td>
          <td>
            <input type="text" name="email2" size="40" maxlength="255" value="<%= toHtmlValue(contact.getEmail2()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Email 3
          </td>
          <td>
            <input type="text" name="email3" size="40" maxlength="255" value="<%= toHtmlValue(contact.getEmail3()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Business Phone
          </td>
          <td>
            <input type="text" name="businessPhone" size="20" maxlength="30" value="<%= toHtmlValue(contact.getBusinessPhone()) %>">
            ext <input type="text" size="5" name="businessPhoneExt" maxlength="30" value="<%= toHtmlValue(contact.getBusinessPhoneExt()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel" nowrap>
            Business Phone 2
          </td>
          <td>
            <input type="text" name="business2Phone" size="20" maxlength="30" value="<%= toHtmlValue(contact.getBusiness2Phone()) %>">
            ext <input type="text" size="5" name="business2PhoneExt" maxlength="30" value="<%= toHtmlValue(contact.getBusiness2PhoneExt()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Mobile Phone
          </td>
          <td>
            <input type="text" name="mobilePhone" size="20" maxlength="30" value="<%= toHtmlValue(contact.getMobilePhone()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Home Phone
          </td>
          <td>
            <input type="text" name="homePhone" size="20" maxlength="30" value="<%= toHtmlValue(contact.getHomePhone()) %>">
            ext <input type="text" size="5" name="homePhoneExt" maxlength="30" value="<%= toHtmlValue(contact.getHomePhoneExt()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel" nowrap>
            Home Phone 2
          </td>
          <td>
            <input type="text" name="home2Phone" size="20" maxlength="30" value="<%= toHtmlValue(contact.getHome2Phone()) %>">
            ext <input type="text" size="5" name="home2PhoneExt" maxlength="30" value="<%= toHtmlValue(contact.getHome2PhoneExt()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Address Line 1
          </td>
          <td>
            <input type="text" name="addrline1" size="35" maxlength="80" value="<%= toHtmlValue(contact.getAddressLine1()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Address Line 2
          </td>
          <td>
            <input type="text" name="addrline2" size="35" maxlength="80" value="<%= toHtmlValue(contact.getAddressLine2()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Address Line 3
          </td>
          <td>
            <input type="text" name="addrline3" size="35" maxlength="80" value="<%= toHtmlValue(contact.getAddressLine3()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            City
          </td>
          <td>
            <input type="text" name="city" size="35" maxlength="80" value="<%= toHtmlValue(contact.getCity()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            State
          </td>
          <td>
            <input type="text" name="state" size="35" maxlength="80" value="<%= toHtmlValue(contact.getState()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Country
          </td>
          <td>
            <input type="text" name="country" size="35" maxlength="80" value="<%= toHtmlValue(contact.getCountry()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Postal Code
          </td>
          <td>
            <input type="text" name="postalCode" size="35" maxlength="12" value="<%= toHtmlValue(contact.getPostalCode()) %>">
          </td>
        </tr>
      </tbody>
    </table>
    <input type="hidden" name="id" value="<%= contact.getId() %>" />
    <input type="submit" value="Save" />
    <input type="button" value="Cancel" onClick="window.location.href='<%= ctx %>/ContactsSearch.do?command=Form'" />
  </form>
</body>
