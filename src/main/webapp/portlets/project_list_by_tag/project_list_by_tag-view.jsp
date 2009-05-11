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
<%@ page import="com.concursive.commons.text.StringUtils" %>
<jsp:useBean id="projectListByTag" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<jsp:useBean id="tag" class="java.lang.String" scope="request"/>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
  <c:if test="${empty title}">
    <h2><c:out value="${title}"/></h2>
  </c:if>
  <c:if test="${empty projectListByTag}">
    No listings found for this tag.
  </c:if>
  <c:if test="${!empty projectListByTag}">
<%
    request.setAttribute("projectTagListInfo", projectListByTag.getPagedListInfo());
%>
      <c:set var="recordCount">${fn:length(projectListByTag)}</c:set>
      <c:choose>
       <c:when test="${empty hasMoreURL && !empty projectTagListInfo && projectTagListInfo.numberOfPages > 1}">
         <c:set var="startIndex">
           ${projectTagListInfo.currentOffset}
         </c:set>
         <c:set var="endIndex">
           ${projectTagListInfo.itemsPerPage + projectTagListInfo.currentOffset < recordCount
                  ? projectTagListInfo.currentOffset + projectTagListInfo.itemsPerPage - 1 : recordCount - 1}
         </c:set>
       </c:when>
       <c:otherwise>
         <c:set var="limit">
            ${(!empty recordLimit && recordLimit < recordCount)
               ? recordLimit - 1 : recordCount - 1}
         </c:set>
         <c:set var="startIndex">0</c:set>
         <c:set var="endIndex">${limit}</c:set>
       </c:otherwise>
      </c:choose>
      <ol>
      <c:if test="${!empty projectListByTag}">
        <c:forEach items="${projectListByTag}" var="project">
        	<c:set var="project" value="${project}" />
        	<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" />
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
            <ccp:rating id='${projectId}'
	                   showText='false'
	                      count='${project.ratingCount}'
	                      value='${project.ratingValue}'
	                        url=''/>
            <h3><a href="${ctx}/show/${project.uniqueId}"><c:out value="${project.title}"/></a></h3>
            <c:if test="${!empty project.location}">
              <address><c:out value="${project.location}"/></address>
            </c:if>
          </li>
        </c:forEach>
      </c:if>
    </ol>
    <c:if test="${!empty projectTagListInfo && projectTagListInfo.numberOfPages > 1}">
		<ccp:paginationControl object="projectTagListInfo" url="${hasMoreURL}/${categoryName}/${normalizedTag}" />
    </c:if>
  </c:if>
