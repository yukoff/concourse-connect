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
<jsp:useBean id="projectList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
  <c:if test="${!empty title}">
    <h3><c:out value="${title}"/></h3>
  </c:if>
  <c:if test="${empty projectList}">
    No listings found.
  </c:if>
  <c:if test="${!empty projectList}">
    <ol>
      <c:forEach items="${projectList}" var="project">
        <c:set var="project" value="${project}" scope="request"/>
        <jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request" />
        <li>
          <c:if test="${!empty project.category.logo}">
            <c:choose>
              <c:when test="${!empty project.logo}">
                <img alt="<c:out value="${project.title}"/> photo" src="${ctx}/image/<%= project.getLogo().getUrlName(45,45) %>" />
              </c:when>
              <c:when test="${!empty project.category.logo}">
                <img alt="Default category photo" src="${ctx}/image/<%= project.getCategory().getLogo().getUrlName(45,45) %>"  class="default-photo" />
              </c:when>
            </c:choose>
          </c:if>
          <c:choose>
            <c:when test="${showRating eq 'true'}">
              <ccp:rating id='${project.id}'
                          showText='false'
                          count='${project.ratingCount}'
                          value='${project.ratingValue}'
                          url=''/>
            </c:when>
            <c:when test="${showPoints eq 'true'}">
              <div class="points-rating">
                <div class="points-ratingValue">
                  ${project.ratingValue} POINT<c:if test="${project.ratingValue != 1}">S</c:if>
                </div>
                <div class="points-ratingCount">
                  <c:set var="ratingLinkShown" value="false"/>
                  <c:if test="${project.features.showReviews}">
                    <c:if test="${project.owner ne user.id}">
                      <ccp:permission name="project-reviews-add" object="project">
                        <a href="${ctx}/create/${project.uniqueId}/review?redirectTo=${ctx}/ideas.shtml" rel="shadowbox;width=600">${project.ratingCount} vote<c:if test="${project.ratingCount != 1}">s</c:if></a>
                        <c:set var="ratingLinkShown" value="true"/>
                      </ccp:permission>
                    </c:if>
                  </c:if>
                  <c:if test="${ratingLinkShown eq 'false'}">
                    ${project.ratingCount} vote<c:if test="${project.ratingCount != 1}">s</c:if>
                  </c:if>
                </div>
              </div>
            </c:when>
          </c:choose>
          <h4><a href="${ctx}/show/${project.uniqueId}"><c:out value="${project.title}"/></a></h4>
          <c:if test="${!empty project.location}">
            <address>
              <c:if test="${!empty project.city}"><span class="city"><c:out value="${project.city}"/></span>,</c:if>
              <c:if test="${!empty project.state}"><span class="state"><c:out value="${project.state}"/></span></c:if>
              <%--
              <c:if test="${!empty project.postalCode}"><span class="zip"><c:out value="${project.postalCode}"/></span></c:if>
              --%>
              <c:if test="${!empty project.country && project.country ne 'UNITED STATES'}">
                <span class="country"><c:out value="${project.country}"/></span>
              </c:if>
            </address>
          </c:if>
          <c:if test="${project.subCategory1Id > -1}">
            <p><c:out value="${project.subCategory1.label}"/></p>
          </c:if>
        </li>
      </c:forEach>
    </ol>
  </c:if>
