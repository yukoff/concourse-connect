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
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.commons.text.StringUtils" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<script language="JavaScript" type="text/javascript">
  <%-- Onload --%>
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.body.focus(); });

  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    
    if (!formTest) {
        messageText = "The message could not be sent.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
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
</script>
  <div class="formContainer">
    <portlet:actionURL var="saveFormUrl">
      <portlet:param name="portlet-command" value="saveForm"/>
    </portlet:actionURL>
    <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="try {return checkForm(this);}catch(e){return true;}">
      <fieldset id="<portlet:namespace/>Add">
        <legend><c:out value="${title}" /></legend>
        <%= showError(request, "actionError") %>
        <label>Please provide us with any additional details regarding this request.</label><br />
        <label for="<portlet:namespace/>comment">Additional Comments <span class="required">*</span></label>
        <%= showAttribute(request, "commentError") %>
        <textarea id="<portlet:namespace/>comment" name="comment"><c:out value="${reportAsInappropriateBean.comment}" /></textarea>
        <span class="characterCounter">1000 characters max</span>
        <input type="hidden" id="<portlet:namespace/>linkProjectId" name="linkProjectId" value="${reportAsInappropriateBean.linkProjectId}" />
        <input type="hidden" id="<portlet:namespace/>linkItemId" name="linkItemId" value="${reportAsInappropriateBean.linkItemId}" />
        <input type="hidden" id="<portlet:namespace/>linkModule" name="linkModule" value="${reportAsInappropriateBean.linkModule}" />
        <c:if test="${'true' eq param.popup || 'true' eq popup}">
          <input type="hidden" name="popup" value="true"/>
        </c:if>
      </fieldset>
      <input type="submit" name="save" class="submit" value="<ccp:label name="button.submit">Submit</ccp:label>" />
      <c:choose>
        <c:when test="${'true' eq param.popup || 'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
        </c:when>
        <c:otherwise>
             <a href="${ctx}/show/${project.uniqueId}/${linkModule}" class="cancel">Cancel</a>
        </c:otherwise>
      </c:choose>
      <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
    </form>
  </div>
