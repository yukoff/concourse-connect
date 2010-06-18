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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<c:set var="user" value="<%= User %>" scope="request" />
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="profile" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<script language="JavaScript" type="text/javascript">
  function saveActivity<portlet:namespace/>(form) {
    <%-- Validate the form --%>
    if (form.body.value.trim() == "") {
        messageText = "Please enter some text and try again.";
        alert(messageText);
        return false;
    }
    <%-- Submit the form --%>
    if (form.save.value != 'Please Wait...') {
      // Tell the user to wait
      form.save.value='Please Wait...';
      form.save.disabled = true;
      // find one or more spinners
      var uItems = YAHOO.util.Dom.getElementsByClassName("submitSpinner");
      for (var j = 0; j < uItems.length; j++) {
        YAHOO.util.Dom.setStyle(uItems[j], "display", "inline");
      }
      <c:choose>
        <c:when test="${!empty namespace}">
          // Post the data
          <portlet:actionURL var="saveFormUrl">
            <portlet:param name="portlet-command" value="saveForm"/>
          </portlet:actionURL>
          // Use YUI to properly encode the POST
          var handleSuccess = function(o) {
            <%-- Reset the form --%>
            var form = document.inputForm<portlet:namespace/>;
            form.body.value='';
            form.save.value='Share';
            form.save.disabled = false;
            // find one or more spinners
            var uItems = YAHOO.util.Dom.getElementsByClassName("submitSpinner");
            for (var j = 0; j < uItems.length; j++) {
              YAHOO.util.Dom.setStyle(uItems[j], "display", "none");
            }
            <%-- Refresh the activity stream if there is one associated --%>
            refreshActivityList${namespace}();
          };
          var callback = {
            success: handleSuccess
          };
          var postData =
              "out=text" +
              "&body=" + encodeURIComponent(form.body.value.trim());
          YAHOO.util.Connect.asyncRequest('POST', "${saveFormUrl}", callback, postData);
          return false;
        </c:when>
        <c:otherwise>
          return true;
        </c:otherwise>
      </c:choose>
    } else {
      return false;
    }
  }
</script>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<div class="formContainer">
  <portlet:actionURL var="saveFormUrl">
    <portlet:param name="portlet-command" value="saveForm"/>
  </portlet:actionURL>
  <form name="inputForm<portlet:namespace/>" onSubmit="try {return saveActivity<portlet:namespace/>(this);}catch(e){alert(e);return true;}" method="POST" action="${saveFormUrl}">
    <ol>
      <li>
        <c:choose>
          <c:when test="${!empty user.profileProject.logo}">
            <img alt="<c:out value="${user.profileProject.title}"/> photo" width="45" height="45"
                 src="${ctx}/image/<%= User.getProfileProject().getLogo().getUrlName(45,45) %>"/>
          </c:when>
          <c:when test="${!empty user.profileProject.category.logo}">
            <img alt="Default user photo" width="45" height="45"
                 src="${ctx}/image/<%= User.getProfileProject().getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
          </c:when>
        </c:choose>
        <div class="portlet-section-body">
          <%= showError(request, "actionError") %>
          <label for="<portlet:namespace/>body"><c:out value="${title}" /></label>
          <%= showAttribute(request, "bodyError") %>
          <input id="<portlet:namespace/>body" name="body" type="text" class="input longInput" maxlength="512" value='<c:out value="${body}" />' />
          <span class="characterCounter">512 characters max</span>
        </div>
      </li>
    </ol>
    <c:if test="${!empty message}">
      <p><c:out value="${message}" /></p>
    </c:if>
    <input type="submit" name="save" class="submit" value="Share" />
    <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
  </form>
</div>
