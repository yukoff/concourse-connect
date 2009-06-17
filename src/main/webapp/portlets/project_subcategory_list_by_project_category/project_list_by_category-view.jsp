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
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%--@elvariable id="hits" type="com.concursive.connect.indexer.IndexerQueryResultList"--%>
<jsp:useBean id="hits" class="com.concursive.connect.indexer.IndexerQueryResultList" scope="request"/>
<jsp:useBean id="projectCategory" class="com.concursive.connect.web.modules.profile.dao.ProjectCategory" scope="request"/>
<jsp:useBean id="projectSubCategory" class="com.concursive.connect.web.modules.profile.dao.ProjectCategory" scope="request"/>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<c:if test="${empty projectSubCategory.description}">
  <h3><c:out value="${title}"/></h3>
</c:if>
<c:if test="${!empty projectSubCategory.description}">
  <h3><c:out value="${projectSubCategory.description}"/></h3>
</c:if>
<c:if test="${empty hits}">
  No <c:out value="${projectCategory.description}"/> found.
</c:if>
<c:if test="${!empty hits}">
  <ul>
<%
  request.setAttribute("projectListInfo", hits.getPagedListInfo());
%>
    <c:set var="recordCount">${fn:length(hits)}</c:set>
    <c:if test="${hasPaging eq 'true'}">
      <c:choose>
       <c:when test="${empty hasMoreURL && !empty projectListInfo && projectListInfo.numberOfPages > 1}">
         <c:set var="startIndex">${projectListInfo.currentOffset}</c:set>
         <c:set var="endIndex">
           ${projectListInfo.itemsPerPage + projectListInfo.currentOffset < recordCount
                  ? projectListInfo.currentOffset + projectListInfo.itemsPerPage - 1 : recordCount - 1}
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
    </c:if>
    <c:if test="${showCategoryLandingPageLink}">
      <li><a href='${ctx}/${fn:toLowerCase(fn:replace(projectCategory.description," ","_"))}.shtml'>All <c:out value="${projectCategory.description}"/></a></li>
    </c:if>
    <c:forEach items="${hits}" var="document">
      <c:set var="projectId">${document.projectId}</c:set>
      <c:set var="title">${document.title}</c:set>
      <% pageContext.setAttribute("project", ProjectUtils.loadProject((Integer.parseInt((String)pageContext.getAttribute("projectId"))))); %>
      <li><a href="${ctx}/show/${project.uniqueId}" title="<c:out value="${project.title}"/>"><c:out value="${project.title}"/></a>
      <c:if test="${!empty project.location}"><address><c:out value="${project.location}"/></address></c:if></li>
    </c:forEach>
  </ul>
  <c:if test="${hasPaging eq 'true'}">
    <c:if test="${!empty projectListInfo && projectListInfo.numberOfPages > 1}">
      <jsp:useBean id="hasMoreURL" class="java.lang.String" scope="request"/>
      <c:if test="${!empty projectSubCategory.description}">
        <ccp:paginationControl object="projectListInfo" url='<%= hasMoreURL + "/" + StringUtils.toHtmlValue(StringUtils.replace(projectSubCategory.getDescription().toLowerCase()," ", "_")) %>' />
      </c:if>
      <c:if test="${empty projectSubCategory.description}">
        <ccp:paginationControl object="projectListInfo" url='<%= hasMoreURL %>' />
      </c:if>
    </c:if>
  </c:if>
</c:if>
