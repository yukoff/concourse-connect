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
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<h3><c:out value="${title}"/></h3>
<c:if test="${!empty classifiedList}">
  <ul>
    <c:forEach items="${classifiedList}" var="classified">
      <jsp:useBean id="classified" type="com.concursive.connect.web.modules.classifieds.dao.Classified" />
      <li>
        <dl>
          <dt>
            <a href="${ctx}/show/${classified.project.uniqueId}/classified-ad/${classified.id}" title="<c:out value="${classified.project.title}"/> classified details"><c:out value="${classified.title}"/></a>
          </dt>
<%--
          <c:if test="${!empty classified.description}"><dd><c:out value="${classified.description}"/></dd></c:if>
--%>
          <cite>
            <c:out value="${classified.project.title}"/>
            <c:if test="${!empty classified.project.location}">- (<c:out value="${classified.project.location}"/>)</c:if>
          </cite>
        </dl>
      </li>
    </c:forEach>
  </ul>
</c:if>
<c:if test="${empty classifiedList}">
  <p>There are no classifieds at this time, please check back later.</p>
</c:if>
<c:if test="${hasMore eq 'true'}">
  <p class="more"><a href="${ctx}${hasMoreURL}" title="<c:out value="${hasMoreTitle}"/>"><c:out value="${hasMoreTitle}"/></a></p>
</c:if>
