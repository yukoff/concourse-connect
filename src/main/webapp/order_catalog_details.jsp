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
<%@ page import="com.concursive.connect.web.modules.productcatalog.dao.Option" %>
<%@ page import="com.concursive.connect.web.modules.productcatalog.dao.Product" %>
<%@ page
    import="com.concursive.connect.web.modules.productcatalog.dao.Attachment" %>
<jsp:useBean id="productList" class="com.concursive.connect.web.modules.productcatalog.dao.ProductList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  boolean showCatalogPresence = true;
  for (Product product : productList) {
    if (!product.getShowInCatalog()) {
      showCatalogPresence = false;
    }
  }
%>
<ccp:evaluate if="<%= showCatalogPresence %>">
  <div class="spacerContainer">
  <div class="portletWindowBackground">
  <div class="portletWrapper">
  <table cellpadding="4" cellspacing="0" width="100%" border="0">
    <tr>
      <td>
        <a href="<%= ctx %>/Order.do">&lt;&lt;Back to product catalog</a>
      </td>
    </tr>
  </table>
  <br />
</ccp:evaluate>
<%= showError(request, "actionError", false) %>
<%= showError(request, "configError", false) %>
<%
  for (Product product : productList) {
%>
<table class="pagedList">
  <thead>
    <tr>
      <th><%= toHtml(product.getName()) %></th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td>
        <table cellpadding="4" cellspacing="0" width="100%" class="empty">
          <tr>
            <td valign="top" rowspan="2">
              <img src="<%= product.getSmallImage() %>" border="0" />
            </td>
            <td valign="top" width="100%">
              <%= product.getDetails() %>
            </td>
          </tr>
          <tr>
            <td>
              <form action="<%= ctx %>/Order.do?command=Add" method="post">
                <table cellpadding="4" cellspacing="0" width="100%" border="0" class="empty">
                  <tr>
                    <td>
                      <%-- Display any product attachments for this product --%>
                      <ccp:evaluate if="<%= product.getAttachmentList().size() > 0 %>">
                        <ccp:evaluate
                            if="<%= product.getAttachmentList().getAllowBeforeCheckoutCount() > 0 %>">
                          The following file<%= product.getAttachmentList().getAllowBeforeCheckoutCount() == 1 ? " is" : "s are" %> available now...<br />
                          <%
                            Iterator iB = product.getAttachmentList().getAllowBeforeCheckoutList().iterator();
                            while (iB.hasNext()) {
                              Attachment thisAttachment = (Attachment) iB.next();
                          %>
                          <img src="<%= ctx %>/images/file.gif" alt="file" align="absMiddle"/>
                          <a href="<%= ctx %>/Order.do?command=DownloadAttachment&id=<%= thisAttachment.getId() %>"><%= toHtml(thisAttachment.getAttachmentClientFilename()) %>
                          </a>
                          (<%= thisAttachment.getRelativeAttachmentSize() %>k)
                          <br />
                          <%
                            }
                          %>
                          <br />
                        </ccp:evaluate>
                        <ccp:evaluate
                            if="<%= product.getAttachmentList().getAllowAfterCheckoutCount() > 0 %>">
                          The following file<%= product.getAttachmentList().getAllowAfterCheckoutCount() == 1 ? " is" : "s are" %> available for immediate download after registering...<br />
                          <%
                            Iterator iB = product.getAttachmentList().getAllowAfterCheckoutList().iterator();
                            while (iB.hasNext()) {
                              Attachment thisAttachment = (Attachment) iB.next();
                          %>
                          <img src="<%= ctx %>/images/file.gif" alt="file" align="absMiddle"/>
                          <%= toHtml(thisAttachment.getAttachmentClientFilename()) %>
                          (<%= thisAttachment.getRelativeAttachmentSize() %>k)
                          <br />
                          <%
                            }
                          %>
                          <br />
                        </ccp:evaluate>
                        <ccp:evaluate
                            if="<%= product.getAttachmentList().getSendAsEmailCount() > 0 %>">
                          The following file<%= product.getAttachmentList().getSendAsEmailCount() == 1 ? "" : "s" %> will be emailed to you after registering...<br />
                          <%
                            Iterator iB = product.getAttachmentList().getSendAsEmailList().iterator();
                            while (iB.hasNext()) {
                              Attachment thisAttachment = (Attachment) iB.next();
                          %>
                          <img src="<%= ctx %>/images/file.gif" alt="file" align="absMiddle"/>
                          <%= toHtml(thisAttachment.getAttachmentClientFilename()) %>
                          (<%= thisAttachment.getRelativeAttachmentSize() %>k)
                          <br />
                          <%
                            }
                          %>
                          <br />
                        </ccp:evaluate>
                      </ccp:evaluate>
                      <%-- Display the options for this product --%>
                      <ccp:evaluate if="<%= product.getOptionList().size() > 0 %>">
                        <%
                          int optionCount = 0;
                    for (Option thisOption : product.getOptionList()) {
                            ++optionCount;
                        %>
                        <ccp:evaluate if="<%= thisOption.getType() == Option.TYPE_INTEGER %>">
                          <input type="hidden" name="optionCount<%= optionCount %>"
                                 value="<%= thisOption.getId() %>"/>
                          <%= toHtml(thisOption.getName()) %>:
                          <input type="text" name="option<%= thisOption.getId() %>"
                                 size="5"
                                 value="<%= toHtmlValue(thisOption.getDefaultValue()) %>"/>
                        </ccp:evaluate>

                        <ccp:evaluate if="<%= thisOption.getType() == Option.TYPE_LOOKUPLIST %>">
                          <input type="hidden" name="optionCount<%= optionCount %>"
                                 value="<%= thisOption.getId() %>"/>
                          <%= toHtml(thisOption.getName()) %>:
                          <%= thisOption.getValueList().getHtmlSelect("option" + thisOption.getId(), thisOption.getDefaultValue(), thisOption.getPriceList()) %>
                        </ccp:evaluate>

                        <ccp:evaluate if="<%= thisOption.getType() == Option.TYPE_STRING %>">
                          <input type="hidden" name="optionCount<%= optionCount %>"
                                 value="<%= thisOption.getId() %>"/>
                          <%= toHtml(thisOption.getName()) %>:
                          <input type="text" name="option<%= thisOption.getId() %>"
                                 size="20"
                                 value="<%= toHtmlValue(thisOption.getDefaultValue()) %>"/>
                        </ccp:evaluate>

                        <ccp:evaluate if="<%= thisOption.getType() == Option.TYPE_TERMS_AND_CONDITIONS %>">
                          <input type="hidden" name="optionCount<%= optionCount %>"
                                 value="<%= thisOption.getId() %>"/>
                          <%= toHtml(thisOption.getName()) %>:<br />
                          <div style="overflow-y:scroll;width:550px;height:250px"><%= toHtml(thisOption.getText()) %></div>
                          <input type="checkbox" name="option<%= thisOption.getId() %>"
                                 value="yes"<%= "yes".equals(thisOption.getDefaultValue()) ? " checked" : "" %> />
                          Accept terms and conditions
                        </ccp:evaluate>

                        <%= toHtml(thisOption.getAdditionalText()) %>
                        <ccp:evaluate if="<%= hasText(thisOption.getLastError()) %>">
                          [<%= thisOption.getLastError() %>]
                        </ccp:evaluate>
                        <br />
                        <%
                          }
                        %>
                        <br />
                      </ccp:evaluate>
                      <input type="hidden" name="pid" value="<%= product.getId() %>"/>
                      <ccp:evaluate if="<%= product.getCartEnabled() %>">
                        <input type="submit" value="Add to cart"/>
                      </ccp:evaluate>
                      <ccp:evaluate if="<%= !product.getCartEnabled() %>">
                        <input type="submit" value="<%= toHtmlValue(product.getActionText()) %>"/>
                      </ccp:evaluate>
                    </td>
                  </tr>
                </table>
              </form>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </tbody>
</table>



</div>
</div>
</div>
<%}%>
