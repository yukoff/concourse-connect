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
<%@ page import="com.concursive.connect.web.portal.PortalUtils,javax.portlet.*" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<script language="JavaScript" type="text/javascript">
  function checkForm<portlet:namespace/>(form) {
    var message = "";
    var formTest = true;
    if (form.email.value == "") {
      message += "- Email address is required\r\n";
      formTest = false;
    } else {
      if (!checkEmail(form.sentFromEmail.value)) {
        message += "- Your email address is invalid.  Make sure there are no invalid characters\r\n";
        formTest = false;
      }
      if (!checkEmail(form.sendToEmail.value)) {
        message += "- Your friend's email address is invalid.  Make sure there are no invalid characters\r\n";
        formTest = false;
      }
    }
    if (formTest) {
      return true;
    } else {
      alert("The form could not be submitted:\r\n" + message);
      return false;
    }
  }
</script>
<div class="sendProjectToFriendEdit">
  <c:if test="${!empty actionError}">
    <p><font color="red"><c:out value="${actionError}"/></font></p>
  </c:if>
  <c:if test="${!empty projectNotFoundError}">
    <p><font color="red"><c:out value="${projectNotFoundError}"/></font></p>
  </c:if>
  <div class="formContainer">
	<portlet:actionURL var="submitContentUrl" portletMode="view" />
	<form method="POST" id="sendToFriendForm" name="sendToFriendForm" action="${submitContentUrl}" onSubmit="try {return checkForm<portlet:namespace/>(this);}catch(e){return true;}">
    <input id="projectId" name="projectId" type="hidden" value="<c:out value='${sendProjectToFriendFormBean.projectId}'/>">
    <fieldset id="sendtofriend">
      <legend><c:out value="${title}"/></legend>
      <label for="sentFromEmail">Your Email Address <span class="required">* &nbsp;<c:out value="${sentFromEmailError}"/></span></label>
      <c:choose>
        <c:when test="${user.id >= 1 && !empty user.email}"><c:set var="fromEmailValue">${user.email}</c:set></c:when>
        <c:otherwise><c:set var="fromEmailValue">${sendProjectToFriendFormBean.sentFromEmail}</c:set></c:otherwise>
      </c:choose>
      <input id="sentFromEmail" name="sentFromEmail" type="text" size="35" maxlength="255" value="<c:out value='${fromEmailValue}'/>">
      <label for="sentFromName">Your Name <span class="required">* &nbsp;<c:out value="${sentFromNameError}"/></span></label>
      <c:choose>
        <c:when test="${user.id >= 1}"><c:set var="fromNameValue">${user.nameFirstLast}</c:set></c:when>
        <c:otherwise><c:set var="fromNameValue">${sendProjectToFriendFormBean.sentFromName}</c:set></c:otherwise>
      </c:choose>
      <input id="sentFromName" name="sentFromName" type="text" size="35" maxlength="255" value="<c:out value='${fromNameValue}'/>">
      <label for="sendToEmails">Email Address of Recipient(s) <span class="required">* &nbsp;<c:out value="${sendToEmailsError}"/></span></label>
      <input id="sendToEmails" name="sendToEmails" type="text" size="35" maxlength="255" value="<c:out value='${sendProjectToFriendFormBean.sendToEmails}'/>">
      <p>(separate multiple email addresses with commas)</p>
      <label for="note">Note</label>
      <textarea id="note" name="note" rows="5" cols="35"><c:out value='${sendProjectToFriendFormBean.note}'/></textarea>
      <c:if test="${user.id < 1 && empty captchaPassed}">
        <label for="captcha">Please input the disguised word to help us validate this form</label>
        <%= showAttribute(request, "captchaError") %>
        <img width="200" height="50" src="${ctx}/Captcha.png?<%= Math.random() %>" id="captimg" name="captimg">
        <input type="text" name="captcha" id="captcha" class="twoHundredPixels">
        <p>Trouble reading? <a href="javascript:newCaptcha('captimg');" class="lightBlue noUnderline">Try another word</a></p>
      </c:if>
    </fieldset>
    <input type="submit" value="<c:out value="${title}"/>" class="submit">
    <c:choose>
      <c:when test="${'true' eq popup}">
        <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
      </c:when>
      <c:otherwise>
        <span><a href="${ctx}/show/${project.uniqueId}" class="cancel">Cancel</a></span>
      </c:otherwise>
    </c:choose>
    <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
  </form>
</div>