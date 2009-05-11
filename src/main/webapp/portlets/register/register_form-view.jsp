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
<jsp:useBean id="countries" class="com.concursive.connect.web.utils.CountrySelect" scope="request"/>
<jsp:useBean id="registerBean" class="com.concursive.connect.web.modules.register.beans.RegisterBean" scope="request"/>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<%--@elvariable id="registerBean" type="com.concursive.connect.web.modules.register.beans.RegisterBean"--%>
<div class="registerFormContainer">
  <div class="formContainer">
    <div class="leftColumn">
    <portlet:actionURL var="submitContentUrl" portletMode="view" />
    <form name="register" method="post" action="<%= pageContext.getAttribute("submitContentUrl") %>">
      <%= showError(request, "actionError", false) %>
      <fieldset id="Create a New Account">
        <legend>Create a New Account</legend>
        <label for="nameFirst">First Name <span class="required">*</span></label>
				<%= showAttribute(request, "nameFirstError") %>
        <input type="text" name="nameFirst" id="nameFirst" value="<c:out value="${registerBean.nameFirst}"/>">
        <label for="nameLast">Last Name <span class="required">*</span></label>
        <%= showAttribute(request, "nameLastError") %>
        <input type="text" name="nameLast" id="nameLast" value="<c:out value="${registerBean.nameLast}"/>">
        <label for="email">Email Address <span class="required">*</span></label>
				<%= showAttribute(request, "emailError") %>
        <input type="text" name="email" id="email" value="<c:out value="${registerBean.email}"/>">
        <label for="organization">Organization Name</label>
        <%=  showAttribute(request, "organizationError") %>
        <input type="text" name="organization" id="organization" value="<c:out value="${registerBean.organization}"/>">
        <label for="country">Country<span class="required">*</span></label>
        <%= countries.getHtml("country",registerBean.getCountry()) %>
        <label for="city">City</label>
        <%=  showAttribute(request, "cityError") %>
        <input type="text" name="city" id="city" value="<c:out value="${registerBean.city}"/>">
        <label for="state">State</label>
        <%=  showAttribute(request, "stateError") %>
        <input type="text" name="state" id="state" value="<c:out value="${registerBean.state}"/>">
        <label for="postalCode">Postal Code <span class="required">*</span></label>
        <%= showAttribute(request, "postalCodeError") %>
        <input type="text" name="postalCode" id="postalCode" value="<c:out value="${registerBean.postalCode}"/>">
        <%-- hidden variables --%>
        <c:if test="${!empty registerBean.data}">
          <input type="hidden" name="data" value="<c:out value="${registerBean.data}"/>" />
        </c:if>
        <input type="hidden" name="currentPage" value="form" />
        <c:if test="${'true' eq showTermsAndConditions}">
        <div>
          <span>
            <%= showAttribute(request, "termsError") %>
            <label for="terms">
              <input name="terms" id="terms" type="checkbox" class="checkbox" value="accept" <c:if test="${registerBean.terms == true}">checked</c:if> />
              I have read &amp; agree to the
              <a href="javascript:popURL('<%= ctx %>/show/${requestMainProfile.uniqueId}/wiki/Terms+and+Conditions?popup=true','Terms_of_use','650','375','yes','yes');">terms of use</a>
            </label>
          </span>
        </div>
        </c:if>
        <c:if test="${empty captchaPassed}">
          <label for="captcha">Please input the disguised word to help us validate this form</label>
          <%= showAttribute(request, "captchaError") %>
          <img width="200" height="50" src="${ctx}/Captcha.png?<%= Math.random() %>" id="captimg" name="captimg">
          <input type="text" name="captcha" id="captcha" class="twoHundredPixels">
          <p>Trouble reading? <a href="javascript:newCaptcha('captimg');" class="lightBlue noUnderline">Try another word</a></p>
        </c:if>
      </fieldset>
      <input type="submit" class="submit" value="Submit" />
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
    <h3>Why is this information required?</h3>
    <ul>
      <li>A valid email address is required so that we can send you your
        login account information</li>
      <li>A valid postal code is required so that we can tailor information to your account</li>
      <li>You will receive a confirmation by email with your login information</li>
      <%--
      <ccp:evaluate if="<%= \"true\".equals(applicationPrefs.get(\"START_PROJECTS\")) %>">
        <li>Enter your name and organization so that you can invite others to
          your projects and they will know who you are</li>
      </ccp:evaluate>
      --%>
    </ul>
  </div>
</div>
</div>
