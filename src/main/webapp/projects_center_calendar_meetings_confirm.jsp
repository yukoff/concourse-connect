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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="meetingInviteesBean" class="com.concursive.connect.web.modules.calendar.utils.MeetingInviteesBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<portlet:actionURL var="saveFormUrl">
  <portlet:param name="portlet-command" value="saveInvitees"/>
</portlet:actionURL>
<div class="formContainer">
  <form name="inputForm" method="post" action="${saveFormUrl}">
    <fieldset>
      <h3>The meeting has been saved, but requires additional information</h3>
      <c:if test="${!empty meetingInviteesBean.membersFoundList}">
        <fieldset>
          <p>The following participants were added:</p>
          <p><c:forEach var="membersFoundList" items="${meetingInviteesBean.membersFoundList}" varStatus="loopStatus">
            <strong><ccp:username id="${membersFoundList.key.id}" showProfile="false" showPresence="true" showCityState="false" /></strong><c:if test="${!loopStatus.last}">,</c:if>
            <c:set var="membersInvited" value="${membersInvited},${membersFoundList.key.id}"/>
          </c:forEach></p>
        </fieldset>
        <input type="hidden" name="membersInvited" value="${membersInvited}">
      </c:if>
      <c:if test="${!empty meetingInviteesBean.cancelledUsers}">
        <c:forEach var="thisUser" items="${meetingInviteesBean.cancelledUsers}" varStatus="loopStatus">
          <c:set var="cancelledUsers" value="${cancelledUsers},${thisUser.id}"/>
        </c:forEach>
        <input type="hidden" name="cancelledUsers" value="${cancelledUsers}">
      </c:if>
      <c:if test="${!empty meetingInviteesBean.meetingChangeUsers}">
        <c:forEach var="thisUser" items="${meetingInviteesBean.meetingChangeUsers}" varStatus="loopStatus">
          <c:set var="meetingChangeUsers" value="${meetingChangeUsers},${thisUser.id}"/>
        </c:forEach>
        <input type="hidden" name="meetingChangeUsers" value="${meetingChangeUsers}">
      </c:if>
      <c:if test="${!empty meetingInviteesBean.rejectedUsers}">
        <c:forEach var="thisUser" items="${meetingInviteesBean.rejectedUsers}" varStatus="loopStatus">
          <c:set var="rejectedUsers" value="${rejectedUsers},${thisUser.id}"/>
        </c:forEach>
        <input type="hidden" name="rejectedUsers" value="${rejectedUsers}">
      </c:if>

      <c:if test="${!empty meetingInviteesBean.membersMultipleList}">
        <fieldset>
          <p>The following names could match more than one person, please choose the specific participant:</p>
          <table class="pagedList">
            <thead>
            <tr>
              <th>You entered</th>
              <th>Matching results</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="membersMultipleList" items="${meetingInviteesBean.membersMultipleList}" varStatus="loopStatus">
              <tr>
                <td><c:out value="${membersMultipleList.key}"/></td>
                <td>
                  <select name="multipleInvitees">
                    <option value="0">----- Choose one -----</option>
                    <c:forEach var="userInfo" items="${membersMultipleList.value}" varStatus="loopStatus">
                      <option value="(${userInfo.profileProject.uniqueId})"><ccp:username id="${userInfo.id}" showProfile="true" showPresence="false" showCityState="true" /></option>
                    </c:forEach>
                  </select>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </fieldset>
      </c:if>

      <c:if test="${!empty meetingInviteesBean.membersNotFoundList}">
        <fieldset>
          <p>The following invites are not members of this site, you can invite them by completing their information which gives them access to the web meeting and supporting materials:</p>
          <table class="pagedList">
            <thead>
            <tr>
              <th>You&nbsp;entered</th>
              <th>First name</th>
              <th>Last name</th>
              <th>Email address</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="membersNotFoundList" items="${meetingInviteesBean.membersNotFoundList}" varStatus="loopStatus">
              <tr>
                <td><c:out value="${membersNotFoundList.key}"/></td>
                <td><input type="text" name="firstName"></td>
                <td><input type="text" name="lastName"></td>
                <c:choose>
                  <c:when test="${fn:contains(membersNotFoundList.key, '@')}">
                    <td><input type="text" name="emailAddress" value="<c:out value="${membersNotFoundList.key}"/>"></td>
                  </c:when>
                  <c:otherwise>
                    <td><input type="text" name="emailAddress"></td>
                  </c:otherwise>
                </c:choose>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </fieldset>
      </c:if>

      <c:if test="${!empty meetingInviteesBean.membersMultipleList || !empty meetingInviteesBean.membersNotFoundList}">
        <div class="portlet-message-alert">
          <p>Invites without a name and email address will not be sent an invitation.</p>
        </div>
      </c:if>
      
      <c:if test="${meetingInviteesBean.meeting.isDimdim}">
	      <p>To schedule the Dimdim meeting, you must have an account with the Dimdim server.
	        If you do not have one please <a href="http://www.dimdim.com" target="_blank">setup an account</a> with Dimdim.
	      Once you have an account, provide your Dimdim information.</p>
	      <%--
	      <label for="dimdimUrl">Dimdim URL <span class="required">*</span></label>
	      <input type="text" name="dimdimUrl" id="dimdimUrl" size="20" maxlength="255" value="<c:out value="${meetingInviteesBean.meeting.dimdimUrl}"/>">
	      --%>
	      <label for="dimdimUsername">Dimdim Username <span class="required">*</span></label>
	      <input type="text" name="dimdimUsername" id="dimdimUsername" size="20" maxlength="255" value="<c:out value="${meetingInviteesBean.meeting.dimdimUsername}"/>">
	      <label for="dimdimPassword">Dimdim Password <span class="required">*</span></label>
	      <input type="password" name="dimdimPassword" id="dimdimPassword" size="20" maxlength="255" value="<c:out value="${meetingInviteesBean.meeting.dimdimPassword}"/>" autocomplete="off">
	      <c:if test="${meetingInviteesBean.meeting.byInvitationOnly}">
	      	Meeting key to be used by the participants: ${meetingInviteesBean.meeting.dimdimMeetingKey}
	      </c:if>
      </c:if>
    </fieldset>
    <input type="submit" class="submit" name="submitAction" value="<ccp:label name="button.save">Save</ccp:label>" onclick="return checkFormEventMeetingConfirm(this.form);" />
    <input type="submit" class="cancel" name="submitAction" value="Cancel" />
    <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
    <input type="hidden" name="meetingId" value="${meetingInviteesBean.meeting.id}" />
    <input type="hidden" name="meetingAction" value="${meetingInviteesBean.action}" />
    <input type="hidden" name="isModifiedMeeting" value="${meetingInviteesBean.isModifiedMeeting}" />
    <c:if test="${'true' eq param.popup || 'true' eq popup}">
      <input type="hidden" name="popup" value="true" />
    </c:if>
    <c:if test="${!empty param.redirectTo}">
      <input type="hidden" name="redirectTo" value="<%= StringUtils.toHtmlValue(request.getParameter("redirectTo")) %>"/>
    </c:if>
  </form>
</div>
