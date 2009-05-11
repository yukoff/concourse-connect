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
<%@ page
    import="com.concursive.connect.web.modules.productcatalog.dao.Attachment" %>
<jsp:useBean id="orderBean" class="com.concursive.connect.web.modules.productcatalog.beans.OrderBean" scope="request"/>
<jsp:useBean id="attachmentList" class="com.concursive.connect.web.modules.productcatalog.dao.AttachmentList" scope="request"/>
<jsp:useBean id="orderRecord" class="com.concursive.connect.web.modules.productcatalog.dao.OrderRecord" scope="request"/>
<%@ include file="initPage.jsp" %>
<div class="spacerContainer">
  <div class="portletWindowBackground">
    <div class="portletWrapper">
      <ccp:evaluate if="<%= orderBean.getPayment().isValid() %>">
        <table cellpadding="4" cellspacing="0" width="100%" border="0">
          <tr>
            <td>
              <b>Thank you for your order.<br />
                Please print this page for future reference.<br />
                A copy of this order has been emailed to the specified address.<br />
                Order #<%= orderBean.getId() %></b>
            </td>
          </tr>
        </table>
        <br />
      </ccp:evaluate>
      <ccp:evaluate if="<%= !orderBean.getPayment().isValid() %>">
        <table cellpadding="4" cellspacing="0" width="100%" border="0">
          <tr>
            <td>
              <b>Thank you, your request is being processed...<br />
                Please print this page for future reference.<br />
                A copy has been emailed to the specified address.<br />
                Order #<%= orderBean.getId() %></b>
            </td>
          </tr>
        </table>
        <br />
      </ccp:evaluate>
      <%@ include file="order_form_items_include.jspf" %>
      <ccp:evaluate if="<%= orderBean.getBilling().isValid() || orderBean.getShipping().isValid() %>">
        <%@ include file="order_form_address_include.jspf" %>
      </ccp:evaluate>
      <ccp:evaluate if="<%= orderBean.getPayment().isValid() %>">
        <%@ include file="order_form_payment_include.jspf" %>
      </ccp:evaluate>
      <ccp:evaluate if="<%= attachmentList.size() > 0 %>">
        The following file<%= attachmentList.size() == 1 ? " is" : "s are" %> available for immediate download...<br />
        <%
          Iterator iB = attachmentList.iterator();
          while (iB.hasNext()) {
            Attachment thisAttachment = (Attachment) iB.next();
        %>
        <img src="<%= ctx %>/images/file.gif" alt="file" align="absMiddle" />
        <a href="<%= ctx %>/Order.do?command=DownloadAttachment&id=<%= thisAttachment.getId() %>&hash=<%= orderRecord.getId() + "-" + orderRecord.getEntered().getTime() %>"><%= toHtml(thisAttachment.getAttachmentClientFilename()) %></a>
        (<%= thisAttachment.getRelativeAttachmentSize() %>k)
        <br />
        <%
          }
        %>
        <br />
      </ccp:evaluate>
      <br />
      <input type="button" value="Return to Website" onClick="window.location.href='http://<%= getServerUrl(request) %>/'" />
    </div>
  </div>
</div>


