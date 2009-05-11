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
<jsp:useBean id="projectList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<%--@elvariable id="title" type="java.lang.String"--%>
<%--@elvariable id="project" type="com.concursive.connect.web.modules.profile.dao.Project"--%>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
  <h3><c:out value="${title}"/></h3>
  <c:if test="${!empty projectList}">
  <ol>
    <c:forEach items="${projectList}" var="project">
      <c:set var="project" value="${project}" />
      <jsp:useBean id="project" type="com.concursive.connect.web.modules.profile.dao.Project" />
      <li>
        	<c:if test="${!empty project.category.logo}">
              <a href="${ctx}/show/<%= project.getUniqueId() %>" title="<c:out value="${project.title}"/> profile page"><c:choose><c:when test="${!empty project.logo}"><img alt="<c:out value="${project.title}"/> photo" src="${ctx}/image/<%= project.getLogo().getUrlName(45,45) %>" /></c:when><c:otherwise><img alt="Default photo" src="${ctx}/image/<%= project.getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" /></c:otherwise></c:choose></a>
          </c:if>
          <ccp:rating id='${project.id}'
                               showText='false'
                               count='${project.ratingCount}'
                               value='${project.ratingValue}'
                               url=''/>
          <h4><a href="${ctx}/show/${project.uniqueId}" title="<c:out value="${project.title}"/> profile page"><c:out value="${project.title}"/></a></h4>
          <c:if test="${!empty project.location}">
            <address>
              <c:if test="${!empty project.city}"><span class="city">${project.city}</span>,</c:if>
              <c:if test="${!empty project.state}"><span class="state">${project.state}</span></c:if>
              <%--
              <c:if test="${!empty project.postalCode}"><span class="zip">${project.postalCode}</span></c:if>
              --%>
              <c:if test="${!empty project.country && project.country ne 'UNITED STATES'}">
                <span class="country"><c:out value="${project.country}"/></span>
              </c:if>
            </address>
          </c:if>

          <%-- not implemented
            <div class="join">
              <p><a href="${ctx}/show/${project.uniqueId}" title="Join <c:out value="${project.title}"/>">Join</a></p>
            </div>
          --%>
      </li>
    </c:forEach>
  </ol>
</c:if>
<c:if test="${empty projectList}">
  <p>Popularity is based on many factors, and currently there are no items that meet the criteria.</p>
</c:if>
