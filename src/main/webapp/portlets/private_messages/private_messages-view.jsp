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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.text.DateFormat" %>
<jsp:useBean id="privateMessageList" class="com.concursive.connect.web.modules.messages.dao.PrivateMessageList" scope="request"/>
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
  <div class="privateMessagesContainer">
    <c:if test="${!empty privateMessageList}">
      <%
        request.setAttribute("privateMessagesListInfo", privateMessageList.getPagedListInfo());
      %>
      <c:forEach items="${privateMessageList}" var="privateMessage">
        <c:choose>
          <c:when test="${privateMessage.readBy eq -1}">
            <dl class="new">
          </c:when>
          <c:otherwise>
            <dl>
          </c:otherwise>
        </c:choose>
        <c:set var="privateMessage" value="${privateMessage}"/>
        <jsp:useBean id="privateMessage" type="com.concursive.connect.web.modules.messages.dao.PrivateMessage"/>
          <dt>
            <div class="status">
              <c:choose>
                <c:when test="${privateMessage.readBy eq -1}">
                  <a href="${ctx}/show/${project.uniqueId}/message/${folder}/${privateMessage.id}"><img alt="New Message Icon" src="${ctx}/images/icons/mail_blue.png"/></a>
                </c:when>
                <c:otherwise>
                  <a href="${ctx}/show/${project.uniqueId}/message/${folder}/${privateMessage.id}"><img alt="Old Message Icons" src="${ctx}/images/icons/mail_open.png"/></a>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="status">
              <c:choose>
                <c:when test="${!empty privateMessage.lastReplyDate}">
                  <img alt="Replied Icon" src="${ctx}/images/external.gif" alt="replied" />
                </c:when>
                <c:otherwise>
                	&nbsp;
                </c:otherwise>
			        </c:choose>
            </div>
            <div class="projectCenterProfileImage">
              <div class="imageContainer">
              <c:choose>
                <c:when test="${folder eq 'inbox'}">
	                <c:choose>
	                  <c:when test="${!empty privateMessage.user.profileProject.logo}">
	                    <img alt="<c:out value="${privateMessage.user.profileProject.title}"/> photo"
	                         src="${ctx}/image/<%= privateMessage.getUser().getProfileProject().getLogo().getUrlName(45,45) %>"/>
	                  </c:when>
	                  <c:when test="${!empty privateMessage.user.profileProject.category.logo}">
	                    <img alt="Default profile photo"
	                         src="${ctx}/image/<%= privateMessage.getUser().getProfileProject().getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
	                  </c:when>
	                </c:choose>
                </c:when>
                <c:when test="${folder eq 'sent'}">
	                <c:choose>
	                  <c:when test="${!empty privateMessage.project.logo}">
	                    <img alt="<c:out value="${privateMessage.project.title}"/> photo"
	                         src="${ctx}/image/<%= privateMessage.getProject().getLogo().getUrlName(45,45) %>"/>
	                  </c:when>
	                  <c:when test="${!empty privateMessage.project.category.logo}">
	                    <img alt="Default profile photo"
	                         src="${ctx}/image/<%= privateMessage.getProject().getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
	                  </c:when>
	                </c:choose>
                </c:when>
              </c:choose>  
              </div>
            </div>
            <ul>
              <c:choose>
                <c:when test="${folder eq 'inbox'}">
					<li>From: <ccp:username id="${privateMessage.enteredBy}" showPresence="${true}"/></li>
                </c:when>
                <c:when test="${folder eq 'sent'}">
                  <li>To: <ccp:username id="${privateMessage.project.owner}" showPresence="${true}"/></li>
                </c:when>
              </c:choose>
              <li class="date"><ccp:tz timestamp="${privateMessage.entered}" dateFormat="<%= DateFormat.SHORT %>"
                                          default="&nbsp;"/></li>
            </ul>
          </dt>
          <dd>
            <div class="details">
              <ul>
                <c:if test="${!empty privateMessage.itemLink}">
                  <li>
                  	<c:choose>
                  		<c:when test="${empty privateMessage.linkProject}">
		                  	About this <a href="${ctx}/show/${privateMessage.project.uniqueId}/${privateMessage.itemLink}"><c:out
		                    value="${privateMessage.itemLabel}"/></a>
	                    </c:when>
	                    <c:otherwise>
		                  	About this <a href="${ctx}/show/${privateMessage.linkProject.uniqueId}/${privateMessage.itemLink}"><c:out
		                    value="${privateMessage.itemLabel}"/></a>
	                    </c:otherwise>
	                </c:choose>
	                </li>
                </c:if>
                <li>
                  <span class="messageBody">
                    <c:out value="${fn:substring(privateMessage.body,0,100)}"/>
                    <c:if test="${fn:length(privateMessage.body) > 100}" > ... [<a href="${ctx}/show/${project.uniqueId}/message/${folder}/${privateMessage.id}">read more</a>]</c:if>
                    <c:if test="${fn:length(privateMessage.body) <= 100}" > [<a href="${ctx}/show/${project.uniqueId}/message/${folder}/${privateMessage.id}">show message</a>]</c:if>
                    <c:if test="${folder ne 'sent'}">
                      <ccp:permission name="project-private-messages-reply">
                        &nbsp;[<a href="javascript:showPanel('ContactUs','${ctx}/show/${privateMessage.user.profileProject.uniqueId}/app/compose_message?module=inbox&id=${privateMessage.id}',700)">Reply</a>]
                      </ccp:permission>
                    </c:if>
                  </span>
                </li>
              </ul>
            </div>
          </dd>
        </dl>
      </c:forEach>
      <c:if test="${!empty privateMessagesListInfo && privateMessagesListInfo.numberOfPages > 1}">
        <ccp:paginationControl object="privateMessagesListInfo" url="${hasMoreURL}"/>
      </c:if>
    </c:if>
    <c:if test="${empty privateMessageList}">
      <p>There are currently no messages.</p>
    </c:if>
  </div>
</div>
