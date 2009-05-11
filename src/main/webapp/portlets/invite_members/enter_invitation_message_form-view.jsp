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
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<script language="JavaScript" type="text/javascript">
  function checkForm<portlet:namespace/>(form) {
    var formTest = true;
    var messageText = "";
    // verify any form field validations and requirements

    if (!formTest) {
      messageText = "The message could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      if (form.save.value != 'Please Wait...') {
        // Tell the user to wait
        form.save.value='Please Wait...';
        form.save.disabled = true;
        // find one or more spinners
        var uItems = YAHOO.util.Dom.getElementsByClassName("submitSpinner");
        for (var j = 0; j < uItems.length; j++) {
          YAHOO.util.Dom.setStyle(uItems[j], "display", "inline");
        }
        return true;
      } else {
        return false;
      }      
    }
  }
  function updateTargetAction(targetValue){
    var action = document.getElementById("<portlet:namespace/>actionType");
	  action.value = targetValue;
  }
</script>
<div class="inviteMembersPortletEdit">
  <h2>Send Invites</h2>
  <c:if test="${hasProjectAccess eq 'false'}">
  	<c:out value="You do not have permissions to invite members"/>
  </c:if>
  <c:if test="${hasProjectAccess ne 'false'}">
  <div class="formContainer">
	<portlet:actionURL var="submitContentUrl" portletMode="view" />
	 <form method="POST" name="<portlet:namespace/>inputForm" action="${submitContentUrl}" onSubmit="try {return checkForm<portlet:namespace/>(this);}catch(e){return true;}">
 		<input type="hidden" name="actionType" id="<portlet:namespace/>actionType" value="sendInvitation" />
    	<fieldset id="InviteMembers">
      		<legend>
		        <c:out value="Prepare Invitation Message" />
	      	</legend>
	      	Please enter a personal message to include with your email invitation(optional).
	      	<br /><br />
			<textarea name="optionalMessage" id="<portlet:namespace/>optionalMessage" cols="80" rows="10"><c:out value="${optionalMessage}" /></textarea>
			<br /><br />
  		</fieldset>
     <input type="hidden" name="sourcePage" value="getInvitationMessage" />
	    <c:if test="${'true' eq param.popup || 'true' eq popup}">
	      <input type="hidden" name="popup" value="true" />
	      <input type="hidden" name="close" value="true" />
	    </c:if>
     <input type="submit" class="submit" name="save" value="Send Invitation" />
     <%--
     <input type="submit" class="submit" name="Back" value="Back" onClick="javascript:updateTargetAction('getMatches')"/>
      --%>
     <c:choose>
	      <c:when test="${'true' eq param.popup || 'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
	      </c:when>
	      <c:otherwise>
          <a href="${ctx}/show/${project.uniqueId}" class="cancel">Cancel</a>
	      </c:otherwise>
     </c:choose>
     <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
	</form>
	</div>
  </c:if>
</div>
