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
<%@ page import="com.concursive.commons.text.StringUtils" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.concursive.connect.web.modules.promotions.dao.AdCategoryList" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="hasMore" class="java.lang.String" scope="request"/>
<jsp:useBean id="hasMoreURL" class="java.lang.String" scope="request"/>
<jsp:useBean id="promotionCategoryList" class="com.concursive.connect.web.modules.promotions.dao.AdCategoryList" scope="request"/>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<%// divide the list by column count into sub lists
  int columnCount = (Integer)request.getAttribute("columns");
  int columnLength = (int)Math.ceil(promotionCategoryList.size() / (double)columnCount);

  Collection<AdCategoryList> lists = new ArrayList<AdCategoryList>();
  for(int i=0; i!=columnCount; i++){
    AdCategoryList cList = new AdCategoryList();
    for(int j=0; j!=columnLength; j++){
      if(promotionCategoryList.isEmpty()) {
        break;
      } else {
        cList.add(promotionCategoryList.remove(0));
      }
    }
    lists.add(cList);
  }
  request.setAttribute("dividedLists", lists);

%>
<h3><c:out value="${title}"/></h3>
  <div class="box140top">
  <% int lastProjectCategoryId = -1; %>
  <c:forEach items="${dividedLists}" var="cList">
  <ul<c:if test="<%= lists.size() > 1 %>"> style="width:<%= 100 / lists.size() %>%"</c:if>>
    <c:forEach items="${cList}" var="thisPromotionCategory">
      <c:set var="promotionCategoryDescription" value="${thisPromotionCategory.itemName}"/>
      <jsp:useBean id="promotionCategoryDescription" type="java.lang.String" />
      <c:set var="thisPromotionCategory" value="${thisPromotionCategory}"/>
      <jsp:useBean id="thisPromotionCategory" class="com.concursive.connect.web.modules.promotions.dao.AdCategory" />
      	<c:if test="${showProjectCategoryNameInCategoryList eq 'true'}">
	      	<c:if test="<%= (lastProjectCategoryId != thisPromotionCategory.getProjectCategoryId()) %>" >
				<div class="boxHeader">
		        	<li class="listing"><h4><c:out value="${thisPromotionCategory.projectCategoryDescription}" /></h4></li>
				</div>
			</c:if>
		</c:if>
		<div class="boxContent">
         <c:choose>
	        <c:when test="${!empty promotionCategory and promotionCategory.id eq thisPromotionCategory.id}">
  	          <c:set var="active"> class="selected"</c:set>
      	    </c:when>
          	<c:otherwise>
            		<c:set var="active" value=""/>
          	</c:otherwise>
        </c:choose>
		<c:set var="paramExists" />
        <c:if test="${!empty sortOrder}">
   	        <c:set var="paramExists">?</c:set>
         	<c:set var="sortOrderString">sort=${sortOrder}</c:set>
  	    </c:if>
       <c:if test="${!empty query}">
       <c:choose>
        <c:when test="${empty paramExists}">
 	    	<c:set var="queryString">query=${query}&location=${location}</c:set>
	     </c:when>
	     <c:otherwise>
 	       <c:set var="queryString">&query=${query}&location=${location}</c:set>
	     </c:otherwise>
	     </c:choose>
  	     <c:set var="paramExists">?</c:set>
    	</c:if>
		<li  ${active}>
			<a href='${ctx}${hasMoreURL}/${thisPromotionCategory.normalizedCategoryName}/${thisPromotionCategory.id}${paramExists}${sortOrderString}${queryString}' title='<c:out value="${thisPromotionCategory.itemName}" />'><c:out value="${thisPromotionCategory.itemName}" /></a>
			<c:if test="${!empty promotionCategoryCountMap[thisPromotionCategory]}">
				(<c:out value="${promotionCategoryCountMap[thisPromotionCategory]}" />)
			</c:if>
		</li>
		<% lastProjectCategoryId = thisPromotionCategory.getProjectCategoryId(); %>
    	</div>
    </c:forEach>
  </ul>
</c:forEach>
</div>
<c:if test="${hasMore eq 'true'}">
  <p class="more"><a href="${ctx}${hasMoreURL}" title="<c:out value="${hasMoreTitle}"/>"><c:out value="${hasMoreTitle}"/></a></p>
</c:if>
