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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<div class="panelMessage">
  <c:if test="${!empty actionError}">
    <p><font color="red"><c:out value="${actionError}"/></font></p>
  </c:if>
  <c:if test="${!empty projectNotFoundError}">
    <p><font color="red"><c:out value="${projectNotFoundError}"/></font></p>
  </c:if>
  <div class="formContainer">
    <portlet:actionURL var="submitUrl">
      <portlet:param name="portlet-command" value="save"/>
    </portlet:actionURL>
    <form method="POST" id="sendToFriendForm" name="sendToFriendForm" action="${submitUrl}">
      <fieldset id="formFieldset">
        <legend>Approve this listing?</legend>
        <h2><c:out value="${project.title}"/></h2>
        <p>Submitted by <ccp:username id="${project.enteredBy}" /></p>
      </fieldset>
      <input type="submit" value="Approve" class="submit">
      <c:choose>
        <c:when test="${'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
        </c:when>
        <c:otherwise>
          <span><a href="${ctx}/show/${project.uniqueId}" class="cancel">Cancel</a></span>
        </c:otherwise>
      </c:choose>
      <img src="${ctx}/images/loading16.gif" alt="updating please wait" class="submitSpinner" style="display:none"/>
    </form>
  </div>
</div>
