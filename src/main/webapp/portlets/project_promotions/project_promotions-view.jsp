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
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<jsp:useBean id="adList" class="com.concursive.connect.web.modules.promotions.dao.AdList" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<%--@elvariable id="title" type="java.lang.String"--%>
<%--@elvariable id="ad" type="com.concursive.connect.web.modules.promotions.dao.Ad"--%>
  <h3><c:out value="${title}"/></h3>
  <c:if test="${!empty adList}">
    <ul>
      <c:forEach items="${adList}" var="ad">
        <jsp:useBean id="ad" type="com.concursive.connect.web.modules.promotions.dao.Ad" />
<%
  Project project = ProjectUtils.loadProject(ad.getProjectId());
  request.setAttribute("thisProject", project);
%>
        <li>
          <dl>
            <dt><a href="${ctx}/show/${thisProject.uniqueId}/promotion/${ad.id}" title="<c:out value="${thisProject.title}"/> promotion details"><c:out value="${ad.heading}"/></a></dt>
            <c:if test="${!empty ad.briefDescription1}"><dd><c:out value="${ad.briefDescription1}"/></dd></c:if>
            <c:if test="${!empty ad.briefDescription2}"><dd><c:out value="${ad.briefDescription2}"/></dd></c:if>
            <c:if test="<%= adList.getProjectId() == -1 %>">
              <cite><c:out value="${thisProject.title}"/></cite>
            </c:if>
            <c:if test="<%= adList.getProjectId() > -1 %>">
              <c:if test="${!empty ad.expirationDate}">
                <c:set var="expirationDate"><ccp:tz timestamp="<%= ad.getExpirationDate() %>" dateOnly="true"/></c:set>
                <dd>expires ${expirationDate}</dd>
              </c:if>
              <c:if test="${!empty ad.webPage && !empty ad.destinationUrl && fn:startsWith(ad.destinationUrl, 'http')}">
                <cite><a href="<c:out value="${ad.destinationUrl}"/>"><c:out value="${ad.webPage}"/></a></cite>
              </c:if>
            </c:if>
          </dl>
        </li>
      </c:forEach>
    </ul>
  </c:if>
  <c:if test="${empty adList}">
    <p>There are no promotions at this time, please check back later.</p>
  </c:if>
