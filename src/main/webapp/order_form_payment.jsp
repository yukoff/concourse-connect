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
<%@ page import="com.concursive.connect.web.modules.productcatalog.dao.Payment" %>
<jsp:useBean id="orderBean" class="com.concursive.connect.web.modules.productcatalog.beans.OrderBean" scope="session"/>
<%@ include file="initPage.jsp" %>
<%= showError(request, "actionError", false) %>
<%@ include file="order_form_items_include.jspf" %>
<%@ include file="order_form_address_include.jspf" %>
<%-- form --%>
<script type="text/javascript" language="JavaScript">
  function checkForm(form) {
    formTest = true;
    message = "";
    if (form.payment_creditCard_number.value == ""){
      message += "- Credit Card Number is required\r\n";
      formTest = false;
      alert("Form could not be saved, please check the following:\r\n\r\n" + message);
    } else {
      var sel = document.forms['inputForm'].elements['payment_creditCard_type'];
      formTest = checkCard(sel.options[sel.selectedIndex].value.substring(0,1),form.payment_creditCard_number.value);
    }
    if (formTest) {
      if (form.order.value != 'Please Wait...') {
        form.order.value='Please Wait...';
        form.order.disabled = true;
      } else {
        return false;
      }
    }
    return formTest;
  }
</script>
<div class="spacerContainer">
  <div class="portletWindowBackground">
    <div class="formContainer">
      <form name="inputForm" action="<%= ctx %>/Order.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);" method="post">
        <table cellpadding="4" cellspacing="0" width="100%" class="pagedList">
          <thead>
            <tr>
              <th colspan="2">Payment Information</th>
            </tr>
          </thead>
          <tbody>
            <tr class="containerBody">
              <td class="formLabel">Amount to be charged</td>
              <td>
                <ccp:currency value="<%= orderBean.getChargeAmount() %>" code="USD" locale="<%= Locale.getDefault() %>" default="&nbsp;"/>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Form of payment</td>
              <td>
                <select name="payment_creditCard_type">
                  <option value="Visa">Visa</option>
                  <option value="MasterCard">MasterCard</option>
                </select>
                <font color="red">*</font>
                <%= showAttribute(request, "typeError") %>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Number</td>
              <td>
                <input type="text" size="20" name="payment_creditCard_number" /><font color="red">*</font>
                <%= showAttribute(request, "numberError") %>
              </td>
            </tr>
            <tr class="containerBody">
              <td class="formLabel">Expiration</td>
              <td>
                <select name="payment_creditCard_expirationMonth">
                  <%
                    for(int monthL=1;monthL<=9;++monthL){
                      out.println("<option value=\"" + monthL + "\">0" + monthL + "</option>");
                    }
                    for(int monthH=10;monthH<=12;++monthH){
                      out.println("<option value=\"" + monthH + "\">" + monthH + "</option>");
                    }
                  %>
                </select>
                <select name="payment_creditCard_expirationYear">
                  <%
                    Calendar today = Calendar.getInstance();
                    int year = today.get(Calendar.YEAR);
                    for(int yearD=year;yearD<=year+10;++yearD){
                      out.println("<option value=\"" + yearD + "\">" + yearD + "</option>");
                    }
                  %>
                </select>
                <font color="red">*</font>
                <%= showAttribute(request, "expirationMonthError") %>
                <%= showAttribute(request, "expirationYearError") %>
              </td>
            </tr>
          </tbody>
        </table>
        <input type="hidden" name="payment_type" value="<%= Payment.TYPE_CREDITCARD %>" />
        <input type="hidden" name="payment_attempt" value="true" />
        <input type="submit" name="order" value="Place Order" />
      </form>
    </div>
  </div>
</div>

