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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="meeting" class="com.concursive.connect.web.modules.calendar.dao.Meeting" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<script language="JavaScript" type="text/javascript">
  <%-- Onload --%>
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.title.focus(); });
  <%-- Validations --%>
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.title.value == "") {
      messageText += "- Title is a required field\r\n";
      formTest = false;
    }
     if (form.startDate.value == "") {
      messageText += "- Start Date is a required field\r\n";
      formTest = false;
    }
    if (form.endDate.value == "") {
      messageText += "- End Date is a required field\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      calendarTrigger("inputForm", "startDate", "");
      return true;
    }
  }
  function calendarTrigger(formName, fieldName, fieldValue) {
    // If the user is adjusting the startDate, and the endDate is earlier,
    // set the endDate to startDate
    if (fieldName == "startDate") {
      // TODO: i18n
      <ccp:evaluate if='<%= User.getLocale().getLanguage().equals("en") %>'>
      if (Date.parse(document.inputForm.startDate.value) > Date.parse(document.inputForm.endDate.value)) {
        document.inputForm.endDate.value = document.inputForm.startDate.value;
      }
      </ccp:evaluate>
    }
  }

</script>
<div class="formContainer">
  <portlet:actionURL var="saveFormUrl">
    <portlet:param name="portlet-command" value="saveForm"/>
  </portlet:actionURL>
  <form method="post" name="inputForm" action="${saveFormUrl}" onSubmit="try {return checkForm(this);}catch(e){return true;}" >
    <fieldset>
      <legend>
        <ccp:evaluate if="<%= meeting.getId() == -1 %>">
            <ccp:label name="projectsCenterCalendar.add.newMeeting">New Event</ccp:label>
        </ccp:evaluate>
        <ccp:evaluate if="<%= meeting.getId() > -1 %>">
            <ccp:label name="projectsCenterCalendar.add.modifyMeeting">Modify Event</ccp:label>
        </ccp:evaluate>
      </legend>
      <label for="title"><ccp:label name="projectsCenterCalendar.add.title">Title <span class="required">*</span></ccp:label></label>
      <%= showAttribute(request, "titleError") %>
      <input type="text" name="title" id="title" size="40" maxlength="100" value="<%= toHtmlValue(meeting.getTitle()) %>" />
      <span class="characterCounter">100 characters max</span>
      <label for="description"><ccp:label name="projectsCenterCalendar.add.description">Description</ccp:label></label>
      <%= showAttribute(request, "descriptionError") %>
      <textarea name="description" id="description"><%= toString(meeting.getDescription()) %></textarea>
      <label for="location"><ccp:label name="projectsCenterCalendar.add.location">Location</ccp:label></label>
      <%= showAttribute(request, "locationError") %>
      <input type="text" name="location" id="location" size="20" maxlength="200" value="<%= toHtmlValue(meeting.getLocation()) %>" />
      <span class="characterCounter">200 characters max</span>
      <fieldset>
        <legend>
          <ccp:label name="projectsCenterCalendar.add.startTime">Start Time <span class="required">*</span></ccp:label>
          <%= showAttribute(request, "startDateError") %>
        </legend>
        <input type="text" name="startDate" size="10" value="<ccp:tz timestamp="<%= meeting.getStartDate() %>" dateOnly="true"/>" onBlur="calendarTrigger('', 'startDate', '');">
        <a href="javascript:popCalendar('inputForm', 'startDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        at
        <ccp:timeSelect baseName="startDate" value="<%= meeting.getStartDate() %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
      </fieldset>
      <fieldset>
        <legend>
          <ccp:label name="projectsCenterCalendar.add.endTime">End Time <span class="required">*</span></ccp:label>
          <%= showAttribute(request, "endDateError") %>
        </legend>
        <input type="text" name="endDate" size="10" value="<ccp:tz timestamp="<%= meeting.getEndDate() %>" dateOnly="true"/>">
        <a href="javascript:popCalendar('inputForm', 'endDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
        at
        <ccp:timeSelect baseName="endDate" value="<%= meeting.getEndDate() %>" timeZone="<%= User.getTimeZone() %>"/>
        <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
      </fieldset>
      <fieldset>
        <legend> 
          <ccp:label name="projectsCenterCalendar.add.details">Details</ccp:label>
        </legend>
        <label for="isTentative">
          <input type="checkbox" name="isTentative" id="isTentative" value="ON"<%= meeting.getIsTentative() ? " checked" : "" %> />
          <ccp:label name="projectsCenterCalendar.thisItemIsTentative">This item is tentative</ccp:label>
        </label>
      </fieldset>
    </fieldset>
    <input type="submit" value="Submit" class="submit">
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
    <input type="hidden" name="return" value="<%= request.getAttribute("return") %>">
    <input type="hidden" name="param" value="<%= project.getId() %>">
  </form>
</div>
