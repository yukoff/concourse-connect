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
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.commons.video.services.JustinTV" %>
<%@ page import="com.concursive.commons.video.services.Livestream" %>
<%@ page import="com.concursive.commons.video.services.Qik" %>
<%@ page import="com.concursive.commons.video.services.Ustream" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="webcast" class="com.concursive.connect.web.modules.webcast.dao.Webcast" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<div id="message" class="menu">
  <p>Thank you for your valuable feedback.</p>
</div>
<c:choose>
    <c:when test="<%= !project.getWebcastInfoExists() %>">
        <ccp:label name="projectsCenterWebcasts.noStreams">This profile does not have any webcasts.</ccp:label>
    </c:when>
    <c:otherwise>
        <ccp:evaluate if="<%= StringUtils.hasText(project.getUstreamId()) %>">
          <%= Ustream.embed(project.getUstreamId(), false, false) %>
        </ccp:evaluate>
        <ccp:evaluate if="<%= StringUtils.hasText(project.getJustintvId()) %>">
          <%= JustinTV.embed(project.getJustintvId()) %>
        </ccp:evaluate>
        <ccp:evaluate if="<%= StringUtils.hasText(project.getLivestreamId()) %>">
          <%= Livestream.embed(project.getLivestreamId()) %>
        </ccp:evaluate>
        <ccp:evaluate if="<%= StringUtils.hasText(project.getQikId()) %>">
          <%= Qik.embed(project.getQikId()) %>
        </ccp:evaluate>
        <ccp:permission name="project-webcasts-view">
            <ccp:evaluate if="<%= (webcast.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
                <p>Do you like this webcast?
                    <portlet:renderURL var="ratingUrl" windowState="maximized">
                        <portlet:param name="portlet-command" value="setRating"/>
                        <portlet:param name="id" value="${webcast.id}"/>
                        <portlet:param name="v" value="1"/>
                        <portlet:param name="out" value="text"/>
                    </portlet:renderURL>
                    <a href="javascript:copyRequest('${ratingUrl}','<%= "webcast_" + webcast.getId() %>','message');">Yes</a>&nbsp;
                    <portlet:renderURL var="ratingUrl" windowState="maximized">
                        <portlet:param name="portlet-command" value="setRating"/>
                        <portlet:param name="id" value="${webcast.id}"/>
                        <portlet:param name="v" value="0"/>
                        <portlet:param name="out" value="text"/>
                    </portlet:renderURL>
                    <a href="javascript:copyRequest('${ratingUrl}','<%= "webcast_" + webcast.getId() %>','message');">No</a>&nbsp;
                    <ccp:evaluate if="<%= webcast.getId() > -1 && User.isLoggedIn() %>">
                        <a href="javascript:showPanel('Mark this webcast as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=webcast&pid=${project.id}&id=${webcast.id}',700)">Report this as inappropriate</a>
                    </ccp:evaluate>
                </p>
                <div id="webcast_<%= webcast.getId() %>"></div>
            </ccp:evaluate>
        </ccp:permission>
    </c:otherwise>
</c:choose>