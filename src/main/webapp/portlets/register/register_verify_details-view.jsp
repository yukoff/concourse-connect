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
    <div class="leftColumn">
      <portlet:actionURL var="submitContentUrl" portletMode="view" />
      <form name="register" method="post" action="<%= pageContext.getAttribute("submitContentUrl") %>">
        <%= showError(request, "actionError", false) %>
        <fieldset id="Confirm your information">
          <legend>Verify Your Information</legend>
          <span>
            <c:out value="${registerBean.nameFirst}"/>
            <c:out value="${registerBean.nameLast}"/></span>
          <span><c:out value="${registerBean.email}"/></span>
          <c:if test="${!empty registerBean.organization}">
            <span><c:out value="${registerBean.organization}"/></span>
          </c:if>
          <span>
          	<c:out value="${registerBean.city}"/>
          </span>
          <span>
          	<c:out value="${registerBean.state}"/>
          	<c:out value="${registerBean.postalCode}"/>
          </span>
          <span>
          	<c:out value="${registerBean.country}"/>
          </span>
        </fieldset>
        <input type="hidden" name="nameFirst" id="namefirst" value="<c:out value="${registerBean.nameFirst}"/>">
        <input type="hidden" name="nameLast" value="<c:out value="${registerBean.nameLast}"/>">
        <input type="hidden" name="email" id="email" value="<c:out value="${registerBean.email}"/>">
        <input type="hidden" name="organization" id="organization" value="<c:out value="${registerBean.organization}"/>">
        <input type="hidden" name="postalCode" id="postalCode" value="<c:out value="${registerBean.postalCode}"/>">
        <input type="hidden" name="city" id="city" value="<c:out value="${registerBean.city}"/>">
        <input type="hidden" name="state" id="state" value="<c:out value="${registerBean.state}"/>">
        <input type="hidden" name="country" id="country" value="<c:out value="${registerBean.country}"/>">
        <c:if test="${!empty registerBean.data}">
          <input type="hidden" name="data" value="<c:out value="${registerBean.data}"/>" />
        </c:if>
        <c:if test="${registerBean.terms == true}">
          <input type="hidden" name="terms" value="${registerBean.terms}" />
        </c:if>
        <input type="hidden" name="currentPage" value="verify" />
        <input type="submit" class="submit" name="submitAction" value="Submit" />
        <input type="submit" class="cancel" name="submitAction" value="Back" />
        <c:choose>
          <c:when test="${'true' eq param.popup || 'true' eq popup}">
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:when>
          <c:otherwise>
            <a href="${ctx}/" class="cancel">Cancel</a>
          </c:otherwise>
        </c:choose>
      </form>
    </div>
    <div class="rightColumn">
      <h3>You're Almost Done...</h3>
      <p>Review the information you entered.  If this is correct then
        choose <strong>Submit</strong> otherwise choose <strong>Back</strong> and make changes.</p>
      <p>You will receive a confirmation by email with your login information.</p>
    </div>
  </div>
</div>
