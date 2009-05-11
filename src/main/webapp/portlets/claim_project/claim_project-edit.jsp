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
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<script language="JavaScript" type="text/javascript">
  function checkForm<portlet:namespace/>(form) {
    var message = "";
    var formTest = true;
    if (form.email.value == "") {
      message += "- Email address is required\r\n";
      formTest = false;
    } else {
      if (!checkEmail(form.email.value)) {
        message += "- The entered email address is invalid.  Make sure there are no invalid characters\r\n";
        formTest = false;
      }
    }
    if (form.owner.value == "") {
      message += "- Verification of ownership is required\r\n";
      formTest = false;
    }
    if (formTest) {
      return true;
    } else {
      alert("The form could not be submitted:\r\n" + message);
      return false;
    }
  }
</script>

<div class="claimProject">
  <div class="formContainer">
    <h2>Claim <c:out value="${claim.projectTitle}"/></h2>
    <c:if test="${!empty actionError}">
      <p><span class="error"><c:out value="${actionError}"/></span></p>
    </c:if>
    <div class="leftColumn">
      <portlet:actionURL var="submitContentUrl" />
      <form method="POST" id="claimForm" name="claimForm" action="${submitContentUrl}" onSubmit="try {return checkForm<portlet:namespace/>(this);}catch(e){return true;}">
        <fieldset id="Claim Project<portlet:namespace/>">
          <legend>Your Details</legend>
          <%-- name --%>
          <ccp:label name="userProfile.name">Your Name</ccp:label>:
          <c:out value='${claim.firstName}'/> <c:out value='${claim.lastName}'/>
          <%-- email --%>
          <label for="email<portlet:namespace/>"><ccp:label name="userProfile.email">Email<span class="required">*</span></ccp:label></label>
          <input type="text" id="email<portlet:namespace/>" name="email" value="<c:out value='${claim.email}'/>">
          <%= showAttribute(request, "emailError") %>
          <%-- notes
          <label for="notes"><ccp:label name="user.notes">Notes</ccp:label></label><br/>
          <textarea name="notes" id="notes<portlet:namespace/>"></textarea>
          <%= showAttribute(request, "notesError") %>
          --%>
          <%-- confirmation --%>
          <span>
            <%= showAttribute(request, "verifyError") %>
            <label for="owner<portlet:namespace/>"> <input type="checkbox" class="checkbox" id="owner<portlet:namespace/>" name="owner" value="true" <c:if test="${claim.isOwner}">checked</c:if> />
            <ccp:label name="project.claim.verify">I am the owner or an employee<span class="required">*</span></ccp:label></label>
          </span>
        </fieldset>
        <input type="submit" value="Submit" class="submit" />
        <c:choose>
          <c:when test="${'true' eq popup}">
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:when>
          <c:otherwise>
            <a href="${ctx}/show/${project.uniqueId}" class="cancel">Cancel</a>
          </c:otherwise>
        </c:choose>
        <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
      </form>
    </div>
    <div class="rightColumn">
      ${introductionMessage}
    </div>
  </div>
</div>
