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
<jsp:useBean id="orderBean" class="com.concursive.connect.web.modules.productcatalog.beans.OrderBean" scope="session"/>
<%@ include file="initPage.jsp" %>
<%= showError(request, "actionError", false) %>
<%@ include file="order_form_items_include.jspf" %>
<%-- form --%>
<script language="JavaScript">
  function checkForm(form) {
    formTest = true ;
    message = "";
    if (form.contactInformation_nameFirst.value == ""){
      message += "- First name is required\r\n";
      formTest = false;
    }
    if (form.contactInformation_nameLast.value == ""){
      message += "- Last name is required\r\n";
      formTest = false;
    }
    if (form.contactInformation_organization.value == ""){
      message += "- Organization is required\r\n";
      formTest = false ;
    }
    if (form.contactInformation_email.value == ""){
      message += "- Email is required\r\n";
      formTest = false ;
    } else if (!checkEmail(form.contactInformation_email.value)) {
      message += "- Email address is invalid. Make sure there are no invalid characters\r\n";
      formTest = false ;
    }
    if (formTest == false) {
      alert("Form could not be saved, please check the following:\r\n\r\n" + message);
    }
    return formTest;
  }
</script>
<body onLoad="document.inputForm.contactInformation_nameFirst.focus();">
<div class="spacerContainer">
  <div class="portletWindowBackground">
    <div class="portletWrapper">
      <form name="inputForm" action="<%= ctx %>/Order.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);" method="post">
        <table class="pagedList">
          <thead>
            <tr>
              <th colspan="2">Contact Information</th>
            </tr>
          </thead>
          <tbody>
            <tr class="containerBody">
              <td class="empty" colspan="2">Note: This is the information we will use to contact you.</td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">First Name</td>
              <td>
                <input type="text" size="25" name="contactInformation_nameFirst" value="<%= toHtmlValue(orderBean.getContactInformation().getNameFirst()) %>"><font color="red">*</font>
                <%= showAttribute(request, "nameFirstError") %>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Last Name</td>
              <td>
                <input type="text" size="25" name="contactInformation_nameLast" value="<%= toHtmlValue(orderBean.getContactInformation().getNameLast()) %>"><font color="red">*</font>
                <%= showAttribute(request, "nameLastError") %>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Organization</td>
              <td>
                <input type="text" size="25" name="contactInformation_organization" value="<%= toHtmlValue(orderBean.getContactInformation().getOrganization()) %>"><font color="red">*</font>
                <%= showAttribute(request, "organizationError") %>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Title</td>
              <td>
                <input type="text" size="25" name="contactInformation_title" value="<%= toHtmlValue(orderBean.getContactInformation().getTitle()) %>">
                <%= showAttribute(request, "titleError") %>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Email</td>
              <td>
                <input type="text" size="25" name="contactInformation_email" value="<%= toHtmlValue(orderBean.getContactInformation().getEmail()) %>"><font color="red">*</font>
                <%= showAttribute(request, "emailError") %>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Phone</td>
              <td>
                <input type="text" size="25" name="contactInformation_phoneNumber" value="<%= toHtmlValue(orderBean.getContactInformation().getPhoneNumber()) %>">
                Ext <input type="text" size="5" name="contactInformation_phoneNumberExt" value="<%= toHtmlValue(orderBean.getContactInformation().getPhoneNumberExt()) %>">
                <%= showAttribute(request, "phoneNumberError") %>
              </td>
            </tr>
          </tbody>
        </table>
        <input type="hidden" name="contactInformation_attempt" value="true" />
        <input type="submit" value="Continue" />
      </form>
    </div>
  </div>
</div>
</body>

