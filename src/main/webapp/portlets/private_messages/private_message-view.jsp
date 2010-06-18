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
<%@ page import="java.text.DateFormat" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<div class="privateMessages">
	<div class="privateMessagesNavigation">
    <ul>
      <c:choose>
        <c:when test="${folder eq 'inbox'}">
          <li class="active"><a href="${ctx}/show/${project.uniqueId}/messages/inbox"><em>Inbox</em></a></li>
        </c:when>
        <c:otherwise>
          <li><a href="${ctx}/show/${project.uniqueId}/messages/inbox"><em>Inbox</em></a></li>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${folder eq 'sent'}">
          <li class="active"><a href="${ctx}/show/${project.uniqueId}/messages/sent"><em>Sent Messages</em></a></li>
        </c:when>
        <c:otherwise>
          <li><a href="${ctx}/show/${project.uniqueId}/messages/sent"><em>Sent Messages</em></a></li>
        </c:otherwise>
      </c:choose>
    </ul>
  </div>
	<div class="privateMessagesHeader">
		<ul>
			<li class="userName"><span>From:</span> <ccp:username id="${privateMessage.enteredBy}" showPresence="${false}" /></li>
			<li class="userName"><span>To:</span> <a href="${ctx}/show/${privateMessage.project.uniqueId}"><c:out value="${privateMessage.project.title}"/></a></li>
			<li class="date"><span>Sent:</span> <ccp:tz timestamp="${privateMessage.entered}" dateFormat="<%= DateFormat.SHORT %>" default="&nbsp;" /></li>
      <c:if test="${!empty privateMessage.itemLink}">
        <li>
          <c:choose>
            <c:when test="${empty privateMessage.linkProject}">
              &raquo; About this <a href="${ctx}/show/${privateMessage.project.uniqueId}/${privateMessage.itemLink}"><c:out value="${privateMessage.itemLabel}"/></a>
            </c:when>
            <c:otherwise>
              &raquo; About this <a href="${ctx}/show/${privateMessage.linkProject.uniqueId}/${privateMessage.itemLink}"><c:out value="${privateMessage.itemLabel}"/></a>
            </c:otherwise>
          </c:choose>
        </li>
      </c:if>
		</ul>
	</div>
  <div class="privateMessagesBody">
    <p>${privateMessage.htmlBody}</p>
  </div>
   <c:if test="${folder ne 'sent'}">
	<ccp:permission name="project-private-messages-reply">
    <div class="actions">
      <a href="javascript:showPanel('ContactUs','${ctx}/show/${privateMessage.user.profileProject.uniqueId}/app/compose_message?module=inbox&id=${privateMessage.id}',700)" name="reply" class="reply">
        <img src="${ctx}/images/icons/mail_pencil.png" alt="Reply Icons">
        Reply</a>
    </div>
	</ccp:permission>
   </c:if>	
</div>
