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
<jsp:useBean id="actionError" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<div class="spacerContainer">
  <div class="portletWindowBackground">
    <div class="formContainer">
      <table cellpadding="4" cellspacing="0" width="100%" border="0">
        <tr>
          <td>
            <img align="absMiddle" src="<%= ctx %>/images/error.gif" border="0" />
            An error occurred when processing your payment...<br />
            <br />
            <b><%= toHtml(actionError) %></b><br />
            <br />
            You can choose to modify the information that caused the problem and try again.
          </td>
        </tr>
      </table>
      <br />
      <%@ include file="order_form_items_include.jspf" %>
      <ccp:evaluate if="<%= orderBean.showBillingAddress() || orderBean.showShippingAddress() %>">
        <%@ include file="order_form_address_include.jspf" %>
      </ccp:evaluate>
      <ccp:evaluate if="<%= orderBean.showPayment() %>">
        <%@ include file="order_form_payment_include.jspf" %>
      </ccp:evaluate>
      <br />
      <input type="button" value="Make Changes" onClick="window.location.href='<%= ctx %>/Order.do?command=Save'" />
    </div>
  </div>
</div>

