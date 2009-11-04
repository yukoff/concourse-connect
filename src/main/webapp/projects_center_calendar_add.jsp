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
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="meeting" class="com.concursive.connect.web.modules.calendar.dao.Meeting" scope="request"/>
<jsp:useBean id="teamMembersList" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="meetingInviteeList" class="java.util.LinkedHashMap" scope="request"/>
<%--@elvariable id="showDimDim" type="java.lang.String"--%>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<script language="JavaScript" type="text/javascript">
  <%-- Onload --%>
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.title.focus(); });
</script>
<div class="formContainer">
  <portlet:actionURL var="saveFormUrl">
    <portlet:param name="portlet-command" value="saveForm"/>
  </portlet:actionURL>
  <form method="post" name="inputForm" action="${saveFormUrl}" >
    <fieldset>
      <legend>
        <ccp:evaluate if="<%= meeting.getId() == -1 %>">
            <ccp:label name="projectsCenterCalendar.add.newMeeting">New Event</ccp:label>
        </ccp:evaluate>
        <ccp:evaluate if="<%= meeting.getId() > -1 %>">
            <ccp:label name="projectsCenterCalendar.add.modifyMeeting">Modify Event</ccp:label>
        </ccp:evaluate>
      </legend>
      <label for="title"><ccp:label name="projectsCenterCalendar.add.title">Title</ccp:label> <span class="required">*</span></label>
      <%= showAttribute(request, "titleError") %>
      <input type="text" name="title" id="title" size="40" maxlength="100" value="<%= toHtmlValue(meeting.getTitle()) %>" />
      <span class="characterCounter">100 characters max</span>
      <label for="description"><ccp:label name="projectsCenterCalendar.add.description">Description</ccp:label></label>
      <%= showAttribute(request, "descriptionError") %>
      <textarea name="description" id="description" class="height100"><%= toString(meeting.getDescription()) %></textarea>
      <label for="location"><ccp:label name="projectsCenterCalendar.add.location">Location</ccp:label></label>
      <%= showAttribute(request, "locationError") %>
      <input type="text" name="location" id="location" size="20" maxlength="200" value="<%= toHtmlValue(meeting.getLocation()) %>" />
      <span class="characterCounter">200 characters max</span>
      <fieldset>
        <legend>
          <ccp:label name="projectsCenterCalendar.add.startTime">Start Time</ccp:label> <span class="required">*</span>
          <%= showAttribute(request, "startDateError") %>
        </legend>
        <input type="text" name="startDate" id="startDate" size="10" onchange="calendarTrigger('startDate');" value="<ccp:tz timestamp="<%= meeting.getStartDate() %>" dateOnly="true"/>">
        <a href="javascript:popCalendar('inputForm', 'startDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        at
        <ccp:timeSelect baseName="startDate" value="<%= meeting.getStartDate() %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
      </fieldset>
      <fieldset>
        <legend>
          <ccp:label name="projectsCenterCalendar.add.endTime">End Time</ccp:label> <span class="required">*</span>
          <%= showAttribute(request, "endDateError") %>
        </legend>
        <input type="text" name="endDate" id="endDate" size="10" onchange="calendarTrigger('endDate');" value="<ccp:tz timestamp="<%= meeting.getEndDate() %>" dateOnly="true"/>">
        <a href="javascript:popCalendar('inputForm', 'endDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        at
        <ccp:timeSelect baseName="endDate" value="<%= meeting.getEndDate() %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
      </fieldset>
        <fieldset>
          <legend><ccp:label name="projectsCenterCalendar.add.details">Details</ccp:label></legend>
          <label for="allowUsersToJoin">
            <input type="checkbox" name="allowUsersToJoin" id="allowUsersToJoin" value="on" <%= meeting.getAllowUsersToJoin() ? "checked" : "" %>/>
            Allow any user to sign up for this meeting<br />
          </label>
          <c:if test="${showDimDim eq 'true'}">
            <label for="isDimdim">
              <input type="checkbox" name="isDimdim" id="isDimdim" value="on" <%= meeting.getIsDimdim() ? "checked" : "" %>/>
              Schedule a Dimdim web meeting and notify participants<br />
            </label>
          </c:if>
        </fieldset>
        <fieldset>
          <legend>Participants</legend>
          <label for="meetingInvitees">Please enter the names or email addresses of your invitees, separated by a comma.</label>
          <%= showAttribute(request, "inviteesError") %>
          <textarea id="meetingInvitees" name="meetingInvitees" class="height100"><%= meeting.getMeetingInvitees() %></textarea>
          <c:if test="${!empty teamMembersList}">
            <span class="characterCounter">You may also choose from the member list below.</span>
          </c:if>
          <c:forEach var="teamMember" items="${teamMembersList}" varStatus="loopStatus">
            <c:set var="teamMember" scope="request" value="${teamMember}"/>
            <a href="javascript:void(0);" onClick="addMember('${teamMember.profileProject.uniqueId}','<%= StringUtils.jsStringEscape(((User)request.getAttribute("teamMember")).getNameFirstLast()) %>'); void(0);"><u><c:out value="${teamMember.nameFirstLast}"/></u></a><c:if test="${!loopStatus.last}">,</c:if>
          </c:forEach>
        </fieldset>
    </fieldset>
    <input type="submit" name="save" class="submit" value="<ccp:label name="button.save">Save</ccp:label>" onclick="calendarTrigger('startDate'); return checkFormEventMeetingAdd(this.form);" />
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
    <input type="hidden" name="id" value="<%= meeting.getId()%>"/>
    <input type="hidden" name="modified" value="<%= meeting.getModified() %>" />
    <c:if test="${'true' eq param.popup || 'true' eq popup}">
      <input type="hidden" name="popup" value="true" />
    </c:if>
    <c:if test="${!empty param.redirectTo}">
      <input type="hidden" name="redirectTo" value="${param.redirectTo}"/>
    </c:if>
    <input type="hidden" name="return" value="<%= request.getAttribute("return") %>">
    <input type="hidden" name="param" value="<%= project.getId() %>">
  </form>
</div>
