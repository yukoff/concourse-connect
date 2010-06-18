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
    if (form.billing_nameFirst.value == ""){
      message += "- First name is required\r\n";
      formTest = false;
    }
    if (form.billing_nameLast.value == ""){
      message += "- Last name is required\r\n";
      formTest = false;
    }
    if (form.billing_addressLine1.value == ""){
      message += "- Address Line 1 is required\r\n";
      formTest = false ;
    }
    if (form.billing_city.value == ""){
      message += "- City is required\r\n";
      formTest = false ;
    }
    if (form.billing_state.value == ""){
      message += "- State/Province is required\r\n";
      formTest = false ;
    }
    if (form.billing_postalCode.value == ""){
      message += "- Zip/Postal Code is required\r\n";
      formTest = false ;
    }
    if (form.billing_country.value == ""){
      message += "- Country is required\r\n";
      formTest = false ;
    }
    if (form.billing_email.value == ""){
      message += "- Email is required\r\n";
      formTest = false ;
    } else if (!checkEmail(form.billing_email.value)) {
      message += "- Email address is invalid. Make sure there are no invalid characters\r\n";
      formTest = false ;
    }
    if (formTest == false) {
      alert("Form could not be saved, please check the following:\r\n\r\n" + message);
    }
    return formTest;
  }
</script>
<body onLoad="document.inputForm.billing_nameFirst.focus();">
<%--
<form name="inputForm" action="https://<%= getServerUrl(request) %>/Order.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);" method="post">
--%>
<div class="spacerContainer">
  <div class="portletWindowBackground">
    <div class="portletWrapper">
      <form name="inputForm" action="<%= ctx %>/Order.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);" method="post">
        <table class="pagedList">
          <thead>
            <tr>
              <th colspan="2">Billing Information</th>
            </tr>
          </thead>
          <tbody>
            <tr class="containerBody">
              <td class="empty" colspan="2">Note: Your billing address must match the address on your credit card statement.</td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">First Name</td>
              <td><input type="text" size="25" name="billing_nameFirst" value="<%= toHtmlValue(orderBean.getBilling().getNameFirst()) %>"><font color="red">*</font><%= showAttribute(request, "nameFirstError") %></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Last Name</td>
              <td><input type="text" size="25" name="billing_nameLast" value="<%= toHtmlValue(orderBean.getBilling().getNameLast()) %>"><font color="red">*</font><%= showAttribute(request, "nameLastError") %></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Address Line 1</td>
              <td><input type="text" size="25" name="billing_addressLine1" value="<%= toHtmlValue(orderBean.getBilling().getAddressLine1()) %>"><font color="red">*</font><%= showAttribute(request, "addressLine1Error") %></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Address Line 2</td>
              <td><input type="text" size="25" name="billing_addressLine2" value="<%= toHtmlValue(orderBean.getBilling().getAddressLine2()) %>"></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Address Line 3</td>
              <td><input type="text" size="25" name="billing_addressLine3" value="<%= toHtmlValue(orderBean.getBilling().getAddressLine3()) %>"></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">City</td>
              <td><input type="text" size="25" name="billing_city" value="<%= toHtmlValue(orderBean.getBilling().getCity()) %>"><font color="red">*</font><%= showAttribute(request, "cityError") %></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">State/Province</td>
              <td><input type="text" size="15" name="billing_state" value="<%= toHtmlValue(orderBean.getBilling().getState()) %>"><font color="red">*</font><%= showAttribute(request, "stateError") %></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Zip/Postal code</td>
              <td><input type="text" size="15" name="billing_postalCode" value="<%= toHtmlValue(orderBean.getBilling().getPostalCode()) %>"><font color="red">*</font><%= showAttribute(request, "postalCodeError") %></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Country</td>
              <td><input type="text" size="25" name="billing_country" value="<%= toHtmlValue(orderBean.getBilling().getCountry()) %>"><font color="red">*</font><%= showAttribute(request, "countryError") %></td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Email</td>
              <td><input type="text" size="25" name="billing_email" value="<%= toHtmlValue(orderBean.getBilling().getEmail()) %>"><font color="red">*</font><%= showAttribute(request, "emailError") %></td>
            </tr>
          </tbody>
        </table>
        <br />
        <ccp:evaluate if="<%= orderBean.requiresShippingAddress() %>">
          <input type="checkbox" name="billing_useForShipping" value="on" <%= orderBean.getBilling().getUseForShipping() ? "checked" : "" %>>Billing address is the same as shipping address.
          <br />
        </ccp:evaluate>
        <input type="hidden" name="billing_attempt" value="true" />
        <input type="submit" value="Continue" />
        <%--

        <table cellpadding="3" cellspacing="0" border="0" width="100%">
          <tr>
             <td>
               <table cellpadding="3" cellspacing="0" border="0" width="100%" class="order">
            <tr>
                <th align="left"><strong>Payment Type</strong></th>
            </tr>
            <tr>
               <td>
                  <input type="radio" name="paymentType" value="Visa" <%= ((paymentBean.getPaymentType() != null) ? (paymentBean.getPaymentType().equals("Visa") ? "checked":"") : "") %>>Visa
                  <input type="radio" name="paymentType" value="Master Card" <%= ((paymentBean.getPaymentType() != null) ? (paymentBean.getPaymentType().equals("Master Card") ? "checked":"") : "") %>>Master Card
               </td>
            </tr>
             </td>
          </tr>
        </table>
        <br />
        <table cellpadding="4" cellspacing="0" width="100%" border="0">
          <tr>
            <td class="smallFont">
              <sup>*</sup> Purchase possible electronically through Visa and Mastercard.
              <ccp:evaluate if="<%= orderBean.getProductId() != 5 %>">
                Price includes shipping cost. Sales tax of 4.5% will be applied on orders shipped to Virginia.
              </ccp:evaluate>
            </td>
          </tr>
          <tr>
              <td>
                <input type="submit" value="Continue" />
                <input type="button" value="Cancel Order" onclick="window.location.href='http://<%= getServerUrl(request) %>/WebsiteCancelOrder.do?command=CancelOrder'">
              </td>
          </tr>
        </table>
        --%>
      </form>
    </div>
  </div>
</div>
</body>

