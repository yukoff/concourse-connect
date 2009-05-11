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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<%--@elvariable id="registerBean" type="com.concursive.connect.web.modules.register.beans.RegisterBean"--%>
<%--@elvariable id="showTermsAndConditions" type="java.lang.String"--%>
<div class="registerTermsContainer">
  <div class="formContainer">
    <h3>Please review our terms and conditions</h3>
    <p>Review the information you entered.  If this is correct then
      choose <strong>Continue</strong> otherwise choose <strong>Back</strong> and make changes.</p>
    <div class="leftColumn">
      <portlet:actionURL var="submitContentUrl" portletMode="view" />
      <form name="register" method="post" action="<%= pageContext.getAttribute("submitContentUrl") %>">
        <%= showError(request, "actionError", false) %>
        <fieldset id="Terms and Conditions">
          <legend>Review Terms &amp; Conditions</legend>
          <span><img src="<%= ctx %>/images/icons/stock_print-16.gif" align="absmiddle" title="Print friendly page" border="0"/> <a href="javascript:popURL('<%= ctx %>/PortalTerms.do?popup=true&printButton=true','Terms_and_Conditions','650','375','yes','yes');">Printer Friendly Page</a></span>
          <p>Please indicate that you accept the following terms and
            conditions by checking "Accept" and clicking the "Submit" button.</p>
          <textarea WRAP="VIRTUAL" READONLY><%@ include file="../../terms.jsp" %></textarea>
          <ul>
            <li><input type="radio" class="radio" name="terms" value="accept" /> Accept</li>
            <%-- <li><input type="radio" class="radio" name="terms" value="decline" /> Decline</li>
            <li><input type="radio" class="radio" name="terms" value="changes" /> Make changes</li> --%>
            <li><input type="radio" class="radio" name="terms" value="changes" /> Back</li>
          </ul>
        </fieldset>
        <%-- end terms --%>
        <c:if test="${!empty registerBean.data}">
          <input type="hidden" name="data" value="<c:out value="${registerBean.data}"/>" />
        </c:if>
        <input type="hidden" name="currentPage" value="terms" />
        <input type="submit" class="submit" systran="yes" border="0" alt="Continue" name="Continue" value="Submit" />
      </form>
    </div>
    <%-- TODO: Add containing layer for Guideline verbage --%>
    <div class="rightColumn">
      <h4>Connect321 Guidelines</h4>
      <p>Connect321 is a community among local businesses&sbquo; organizations&sbquo; groups&sbquo; and individuals. Meaningful contributions make the site better for everyone. Our guidelines are simple:</p>
      <p><b>Be respectful</b>. Be polite in your interactions with others. Remember that everyone has different opinions and beliefs.</p>
      <p><b>Be honest</b>. Do review businesses and submit events and content&sbquo; but use good judgement. An honest review can be negative without slanderous. The point is for others to know about your experience. Hopefully&sbquo; a business owner will take steps to improve. Don&rsquo;t upload anything that isn&rsquo;t yours or already considered public. Spamming and vandalizing isn&rsquo;t an honest use of the Community and its tools.</p>
      <p><b>Be legal</b>. We all have to follow federal&sbquo; state&sbquo; and local laws. Don&rsquo;t post copyrighted work&sbquo; pornography&sbquo; or harmful material.</p>
      <p><b>Be helpful</b>. People come to the Community to find out about what is happening right here in your Community. Contribute as much as you can that is meaningful.</p>
      <p>For additional information&sbquo; please contact us at <a href="mailto:Support@Concursive.com">Support@Concursive.com</a></p>
    </div>
  </div>
</div>
