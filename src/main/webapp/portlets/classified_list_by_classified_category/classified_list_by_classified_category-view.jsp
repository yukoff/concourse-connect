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
<jsp:useBean id="classifiedList" class="com.concursive.connect.indexer.IndexerQueryResultList" scope="request"/>
<jsp:useBean id="classifiedCategory" class="com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategory" scope="request"/>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
  <c:if test="${empty classifiedCategory.itemName}">
    <h2><c:out value="${title}"/></h2>
  </c:if>
  <c:if test="${!empty classifiedCategory.itemName}">
    <h2>Classifieds (<c:out value="${classifiedCategory.itemName}"/>)</h2>
  </c:if>
  <c:if test="${empty classifiedList}">
    No classifieds found.
  </c:if>
  <c:if test="${!empty classifiedList}">
    <ul>
<%
    request.setAttribute("classifiedListInfo", classifiedList.getPagedListInfo());
%>
      <c:if test="${!empty classifiedList}">
        <c:forEach items="${classifiedList}" var="classified">
          <li>
	          <dl>
    	      	<dt><a href="${ctx}/show/${classified.projectUniqueId}/classified-ad/${classified.objectId}" title="<c:out value="${classified.title}"/>"><c:out value="${classified.title}"/></a></dt>
	            <c:if test="${!empty classified.contents}">
	            	<dd><c:out value="${classified.contents}"/></dd>
	            </c:if>
              <cite>
                <c:out value="${classified.projectTitle}"/>
                <c:if test="${!empty classified.projectLocation}">- (<c:out value="${classified.projectLocation}"/>)</c:if>
              </cite>
	          </dl>  
        </c:forEach>
      </c:if>
    </ul>
    <c:if test="${hasPaging eq 'true'}">
	    <c:if test="${!empty classifiedListInfo && classifiedListInfo.numberOfPages > 1}">
	      <jsp:useBean id="hasMoreURL" class="java.lang.String" scope="request"/>
		  	<c:choose>
		  		<c:when test="${empty sortOrder}">
		  			<c:set var="sortURL" />
		  		</c:when>
		  		<c:otherwise>
		  			<c:set var="sortURL">
		  				?sort=${sortOrder}
		  			</c:set>
		  		</c:otherwise>
		  	</c:choose>
		  	<c:choose>
		  		<c:when test="${empty query}">
		  			<c:set var="queryString" />
		  		</c:when>
		  		<c:otherwise>
		  			<c:set var="queryString">
		  				query=${query}
		  			</c:set>
		  		</c:otherwise>
		  	</c:choose>
		  	<c:choose>
		  		<c:when test="${empty location}">
		  			<c:set var="locationString" />
		  		</c:when>
		  		<c:otherwise>
		  			<c:set var="locationString">
		  				location=${location}
		  			</c:set>
		  		</c:otherwise>
		  	</c:choose>
	      <c:if test="${!empty classifiedCategory.itemName}">
	        <ccp:paginationControl object="classifiedListInfo" url='<%= hasMoreURL + "/" + StringUtils.toHtmlValue(classifiedCategory.getNormalizedCategoryName()) + "/" +classifiedCategory.getId() %>' urlParams='${sortURL},${queryString},${locationString}' />
	      </c:if>
	      <c:if test="${empty classifiedCategory.itemName}">
	        <ccp:paginationControl object="classifiedListInfo" url='<%= hasMoreURL %>' urlParams='${sortURL},${queryString},${locationString}' />
	      </c:if>
	    </c:if>
	  </c:if>
  </c:if>
