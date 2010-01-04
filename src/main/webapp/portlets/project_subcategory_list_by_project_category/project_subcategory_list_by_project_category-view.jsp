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
<%@ page import="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.ProjectCategory" %> 
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="hasMore" class="java.lang.String" scope="request"/>
<jsp:useBean id="hasMoreURL" class="java.lang.String" scope="request"/>
<jsp:useBean id="projectSubCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<%// divide the list by column count into sub lists
  int columnCount = (Integer)request.getAttribute("columns");
  int columnLength = (int)Math.ceil(projectSubCategoryList.size() / (double)columnCount);

  Collection<ProjectCategoryList> lists = new ArrayList<ProjectCategoryList>();
  for(int i=0; i!=columnCount; i++){
    ProjectCategoryList pcList = new ProjectCategoryList();
    for(int j=0; j!=columnLength; j++){
      if(projectSubCategoryList.isEmpty()) {
        break;
      } else {
        pcList.add(projectSubCategoryList.remove(0));
      }
    }
    lists.add(pcList);
  }
  request.setAttribute("dividedLists", lists);

%>
<h3><c:out value="${title}"/></h3>
  <c:forEach items="${dividedLists}" var="pcList">
  <ul<c:if test="<%= lists.size() > 1 %>"> style="width:<%= 100 / lists.size() %>%"</c:if>>
    <c:forEach items="${pcList}" var="subProjectCategory">
      <c:set var="subProjectCategoryDescription" value="${subProjectCategory.description}"/>
      <jsp:useBean id="subProjectCategoryDescription" type="java.lang.String" />
      <c:set var="subProjectCategory" value="${subProjectCategory}"/>
      <jsp:useBean id="subProjectCategory" class="com.concursive.connect.web.modules.profile.dao.ProjectCategory" scope="request"/>
        <li class="listing"><a href='${ctx}${hasMoreURL}/<%= ProjectCategory.getNormalizedCategoryName(subProjectCategoryDescription) %>' title='<c:out value="${subProjectCategory.description}" />'><c:out value="${subProjectCategory.description}" /></a></li>
    </c:forEach>
  </ul>
  <ccp:evaluate if='<%= "true".equals(hasMore) %>'>
    <p class="more"><a href="${ctx}<%= hasMoreURL %>" title="<c:out value="${hasMoreTitle}"/>">more</a></p>
  </ccp:evaluate>
</c:forEach>
