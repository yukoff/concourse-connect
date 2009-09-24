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
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<div class="formContainer">
	<portlet:actionURL var="saveFormUrl">
  	<portlet:param name="portlet-command" value="saveJoinEvent"/>
	</portlet:actionURL>
  <form name="inputForm" method="post" action="${saveFormUrl}">
    <fieldset>
      <legend>Join Event</legend>
      <b>Please choose one of the options below to indicate your likeliness in attending the event.</b>
   		<label for="yes"><input type="radio" name="join" id="yes" value="yes" checked />Yes, I will be attending the event.</label>
 			<label for="maybe"><input type="radio" name="join" id="maybe" value="maybe" />Maybe, I am interested but not sure if i can make it to the event.</label><br/>
    </fieldset>
    <input type="submit" name="save" class="submit" value="<ccp:label name="button.save">Save</ccp:label>" />
    <c:choose>
      <c:when test="${'true' eq param.popup || 'true' eq popup}">
        <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
      </c:when>
      <c:otherwise>
        <portlet:renderURL var="cancelUrl">
          <portlet:param name="portlet-action" value="show"/>
          <portlet:param name="portlet-object" value="calendar"/>
        </portlet:renderURL>
        <a href="${cancelUrl}" class="cancel">Cancel</a>
      </c:otherwise>
    </c:choose>
    <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
    <c:if test="${'true' eq param.popup || 'true' eq popup}">
      <input type="hidden" name="popup" value="true" />
    </c:if>
    <c:if test="${!empty param.redirectTo}">
      <input type="hidden" name="redirectTo" value="<%= StringUtils.toHtmlValue(request.getParameter("redirectTo")) %>"/>
    </c:if>
    <input type="hidden" name="meetingId" value="<%= request.getAttribute("meetingId") %>"/>
  </form>
</div>