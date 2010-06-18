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
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<script type="text/javascript" language="javascript">
  <portlet:renderURL var="notificationURL" portletMode="view" windowState="maximized" />
  function <portlet:namespace/>changeNotifications(p_oValue) {
    <portlet:namespace/>sendRequest('<%= pageContext.getAttribute("notificationURL") %>&out=text&notifications=' + p_oValue);
  }

  function <portlet:namespace/>changeSchedule(p_oValue) {
    <portlet:namespace/>sendRequest('<%= pageContext.getAttribute("notificationURL") %>&out=text&schedule=' + p_oValue);
  }

  function <portlet:namespace/>sendRequest(url) {
    var xmlHttpReq = myXMLHttpRequest();
    url += ((url.indexOf('?') == -1)?'?':'&') + "rnd=" + new Date().valueOf().toString();
    xmlHttpReq.open('get', url);
    xmlHttpReq.send(null);
  }
</script>
<h3>Your Email Digest Settings</h3>
<p>
  <%--
  Stay informed about <strong><c:out value="${project.title}"/></strong>. You can
  receive emails from the owners of this profile and you can add this profile to
  an automated email digest with all the latest activity sent right to you.<br/>
  <br/>
  --%>
  Receive email regarding <strong><c:out value="${project.title}"/></strong> from the
  profile owners?<br/>
  <input type="radio" onclick="<portlet:namespace/>changeNotifications('true');" name="notification"
         <c:if test="${member.notification}">checked="true"</c:if> id="notification">Yes
  <input type="radio" onclick="<portlet:namespace/>changeNotifications('false');" name="notification"
         <c:if test="${!member.notification}">checked="true"</c:if> id="notification">No <br /><br />
  Receive automated email updates?<br />
  <input type="radio" onclick="<portlet:namespace/>changeSchedule(<%= TeamMember.EMAIL_OFTEN %>);" name="emailNotification"
         <c:if test="${member.emailUpdatesSchedule == 1}">checked="true"</c:if> id="emailNotification">Often
  <input type="radio" onclick="<portlet:namespace/>changeSchedule(<%= TeamMember.EMAIL_DAILY %>);" name="emailNotification"
         <c:if test="${member.emailUpdatesSchedule == 2}">checked="true"</c:if> id="emailNotification">Daily
  <input type="radio" onclick="<portlet:namespace/>changeSchedule(<%= TeamMember.EMAIL_WEEKLY %>);" name="emailNotification"
         <c:if test="${member.emailUpdatesSchedule == 3}">checked="true"</c:if> id="emailNotification">Weekly
  <input type="radio" onclick="<portlet:namespace/>changeSchedule(<%= TeamMember.EMAIL_MONTHLY %>);" name="emailNotification"
         <c:if test="${member.emailUpdatesSchedule == 4}">checked="true"</c:if> id="emailNotification">Monthly
  <input type="radio" onclick="<portlet:namespace/>changeSchedule(<%= TeamMember.EMAIL_NEVER %>);" name="emailNotification"
         <c:if test="${member.emailUpdatesSchedule == 0}">checked="true"</c:if> id="emailNotification">Never
</p>