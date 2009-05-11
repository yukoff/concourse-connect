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
<%@ page import="com.concursive.connect.web.modules.productcatalog.dao.Product" %>
<jsp:useBean id="orderBean" class="com.concursive.connect.web.modules.productcatalog.beans.OrderBean" scope="session"/>
<%@ include file="initPage.jsp" %>
<div class="spacerContainer">
  <div class="portletWindowBackground">
    <div class="portletWrapper">
      <%= showError(request, "actionError", false) %>
      <table cellpadding="4" cellspacing="0" width="100%">
        <tr>
          <td>
            Items in cart: <%= orderBean.getProductList().size() %>
          </td>
        </tr>
      </table>
      <br />
      <table class="pagedList">
        <thead>
          <tr>
            <th>
              Action
            </th>
            <th>
              Product<%= orderBean.getProductList().size() > 1 ? "s" : "" %>
            </th>
            <th>
              Details
            </th>
            <%--
            <th width="20">
              Qty.
            </th>
            --%>
            <th width="50">
              Price
            </th>
          </tr>
        </thead>
        <tbody>
          <%
            Iterator i = orderBean.getProductList().iterator();
            while (i.hasNext()) {
              Product thisProduct = (Product) i.next();
          %>
          <tr class="containerBody">
            <td valign="top">
              [<a href="<%= ctx %>/Order.do?command=Remove&uid=<%= thisProduct.getUniqueId() %>">remove</a>]
            </td>
            <td valign="top">
              <%= toHtml(thisProduct.getName()) %>
            </td>
            <td valign="top">
              <%= toHtml(thisProduct.getPriceDescription()) %><br />
              <%= toHtml(thisProduct.getConfiguredSummary()) %>
            </td>
            <%--
              <td valign="top" align="right">
            <%
              Iterator o = thisProduct.getOptionList().iterator();
              while (o.hasNext()) {
                Option thisOption = (Option) o.next();
            %>
                <ccp:evaluate if="<%= thisOption.getType() == Option.TYPE_QUANTITY %>">
                    <%= toHtml(thisOption.getDefaultValue()) %>
                </ccp:evaluate>
            <%
              }
            %>
              </td>
            --%>
            <td align="right" valign="top">
              <ccp:currency value="<%= thisProduct.getTotalPrice() %>" code="USD" locale="<%= Locale.getDefault() %>" default="&nbsp;"/>
            </td>
          </tr>
        <%
          }
        %>
        </tbody>
        <tfoot>
          <tr class="containerBody">
            <td colspan="4" align="right" style="background-color: #EEE;">
              Sub-Total: <b><ccp:currency value="<%= orderBean.getProductList().getTotalPrice() %>" code="USD" locale="<%= Locale.getDefault() %>" default="&nbsp;"/></b>
            </td>
          </tr>
        </tfoot>
      </table>
      <br />
      <table border="0">
        <tr>
          <td>
            <input type="button" value="Continue Shopping" onClick="window.location.href='<%= ctx %>/Order.do'" />
          </td>
        </tr>
      </table>
      <ccp:evaluate if="<%= orderBean.getProductList().size() > 0 %>">
        <br />
        <table border="0">
          <tr>
            <td>
              <input type="button" value="Check Out" onClick="window.location.href='https://<%= getServerUrl(request) %>/Order.do?command=Save'" />
            </td>
          </tr>
        </table>
      </ccp:evaluate>
    </div>
  </div>
</div>

