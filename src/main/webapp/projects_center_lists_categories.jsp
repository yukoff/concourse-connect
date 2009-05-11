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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.connect.web.modules.lists.dao.TaskCategory" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.lists.dao.TaskCategoryList" scope="request"/>
<jsp:useBean id="projectListsCategoriesInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<%@ include file="initPage.jsp" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<%-- Initialize the drop-down menus --%>
<script language="JavaScript" type="text/javascript" src="${ctx}/javascript/preloadImages.js"></script>
<script language="JavaScript" type="text/javascript">
  var base = "${ctx}/images/";
  loadImages('select_<%= SKIN %>');
</script>
<%@ include file="projects_center_lists_categories_menu.jspf" %>
<ccp:permission name="project-lists-add">
  <a href="${ctx}/ProjectManagementListsCategory.do?command=AddCategory&pid=${project.id}"><ccp:label name="projectsCenterLists.categories.newList">New List</ccp:label></a>
  | <a href="${ctx}/ProjectManagementListsBuckets.do?command=Categories&pid=${project.id}"><ccp:label name="projectsCenterLists.bucketView">Bucket View</ccp:label></a>
  <ccp:permission name="project-setup-customize">
    |
    <a href="${ctx}/ProjectManagementListsConfig.do?command=Options&pid=${project.id}"><ccp:label name="projectsCenter.configuration">Configuration</ccp:label></a>
  </ccp:permission>
  <br />
</ccp:permission>
<%-- Temp. fix for Weblogic --%>
<%
String actionError = showError(request, "actionError");
%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
  <tr>
    <form name="pagedListView" method="post" action="${ctx}/show/${project.uniqueId}/lists">
    <td align="left">
      &nbsp;
    </td>
    <td>
      <ccp:pagedListStatus label="Lists" title="<%= actionError %>" object="projectListsCategoriesInfo"/>
    </td>
    </form>
  </tr>
</table>
<table class="pagedList">
  <thead>
    <tr>
      <th width="8" nowrap><ccp:label name="projectsCenterLists.categories.action">Action</ccp:label></th>
      <th width="100%" nowrap><ccp:label name="projectsCenterLists.categores.list">List</ccp:label></th>
      <th align="center" nowrap><ccp:label name="projectsCenterLists.categories.items">Items</ccp:label></th>
      <th align="center" nowrap><ccp:label name="projectsCenterLists.categories.lastPost">Last Post</ccp:label></th>
    </tr>
  </thead>
  <tbody>
  <%
    if (categoryList.size() == 0) {
  %>
    <tr class="row2">
      <td colspan="4"><ccp:label name="projectsCenterLists.categories.noLists">No lists to display.</ccp:label></td>
    </tr>
  <%
    }
  %>
    <c:forEach items="${categoryList}" var="thisCategory" varStatus="categoryStatus">
      <tr class="row1">
        <td valign="top" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %>${categoryStatus.count}',${thisCategory.id})"
           onMouseover="over(0,${categoryStatus.count})"
           onmouseout="out(0,${categoryStatus.count})"><img
             src="${ctx}/images/select_<%= SKIN %>.gif" id="select_<%= SKIN %>${categoryStatus.count}" align="absmiddle" border="0"></a>
        </td>
        <td width="100%" valign="top">
          <table border="0" cellpadding="0" cellspacing="0" width="100%" class="empty">
            <tr>
              <td valign="top" nowrap>
                <img border="0" src="${ctx}/images/icons/stock_list_enum-16.gif" align="absmiddle" />&nbsp;
              </td>
              <td valign="top" width="100%">
                <a href="${ctx}/show/${project.uniqueId}/list/${thisCategory.id}"><c:out value="${thisCategory.description}"/></a>
              </td>
            </tr>
          </table>
        </td>
        <td valign="top" align="center" nowrap>${thisCategory.taskCount}</td>
        <td valign="top" align="center" nowrap>
        <c:choose>
          <c:when test="${!empty thisCategory.lastTaskEntered}">
            <ccp:tz timestamp="${thisCategory.lastTaskEntered}" default="--"/>
          </c:when>
          <c:otherwise>
            --
          </c:otherwise>
        </c:choose>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>

