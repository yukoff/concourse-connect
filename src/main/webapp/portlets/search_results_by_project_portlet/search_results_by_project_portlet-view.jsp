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
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.indexer.IndexerQueryResult" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%--@elvariable id="hits" type="com.concursive.connect.indexer.IndexerQueryResultList"--%>
<%--@elvariable id="title" type="java.lang.String"--%>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<jsp:useBean id="hits" class="com.concursive.connect.indexer.IndexerQueryResultList" scope="request"/>
<jsp:useBean id="searchBean" class="com.concursive.connect.web.modules.search.beans.SearchBean" scope="request"/>
<%
    request.setAttribute("searchInfo", hits.getPagedListInfo());
%>
<h2><c:out value="${title}"/></h2>
<c:if test="${!empty hits}">
    <ol>
      <c:forEach var="document" items="${hits}">
        <c:set var="projectId">${document.projectId}</c:set>
        <c:set var="title">${document.title}</c:set>
        <%
          Project project = ProjectUtils.loadProject((Integer.parseInt((String)pageContext.getAttribute("projectId"))));
          request.setAttribute("project",project);
        %>
        <li>
          <c:choose>
            <c:when test="${!empty project.logo}">
              <img alt="<c:out value="${project.title}"/> photo" width="45" height="45" src="${ctx}/image/<%= project.getLogo().getUrlName(45,45) %>" />
            </c:when>
            <c:when test="${!empty project.category.logo}">
              <img alt="Default photo" width="45" height="45" src="${ctx}/image/<%= project.getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
            </c:when>
          </c:choose>
          <ccp:rating id='${projectId}'
                   showText='false'
                      count='${project.ratingCount}'
                      value='${project.ratingValue}'
                        url=''/>
          <h3><a href="${ctx}/show/${project.uniqueId}"><c:out value="${title}"/></a></h3>
          <c:if test="${!empty project.location}">
            <address><c:out value="${project.location}"/></address>
          </c:if>
        </li>
      </c:forEach>
      <c:if test='${!empty hasMoreRecords && !empty hasMoreURL}'>
          <li class="more"><a href="${ctx}${hasMoreURL}" title="<c:out value="${ctx}${hasMoreTitle}"/>">more</a> &#62;</li>
      </c:if>
    </ol>
    <c:if test="${!empty hasPaging && !empty searchInfo && searchInfo.numberOfPages > 1}">
      <ccp:paginationControl object="searchInfo"/>
    </c:if>
</c:if>
