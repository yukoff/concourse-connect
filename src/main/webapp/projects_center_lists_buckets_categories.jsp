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
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.connect.web.utils.LookupElement" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.lists.dao.TaskCategoryList" scope="request"/>
<jsp:useBean id="category" class="com.concursive.connect.web.modules.lists.dao.TaskCategory" scope="request"/>
<jsp:useBean id="outlineList" class="com.concursive.connect.web.modules.lists.dao.TaskList" scope="request"/>
<jsp:useBean id="taskUrlMap" class="java.util.HashMap" scope="request"/>
<%@ include file="initPage.jsp" %>
<ccp:permission name="project-lists-modify">
  <script language="JavaScript" type="text/javascript" src="<%= ctx %>/javascript/bucketlist.js?2"></script>
  <script language="JavaScript" type="text/javascript">
    function bucketMove(itemId, columnId) {
      callURL("<%= ctx %>/ProjectManagementListsBuckets.do?command=MoveCategory&pid=<%= project.getId() %>&id=" + itemId + "&cid=" + columnId + "&out=text");
    }
  </script>
  <c:set var="movableClass">movable</c:set>
</ccp:permission>
<div class="portletWrapper">
  <div class="profile-portlet-header">
    <h2>Lists</h2>
  </div>
  <div class="profile-portlet-menu">
    <ul>
      <ccp:permission name="project-lists-add">
        <li class="last">
          <a href="<%= ctx %>/ProjectManagementListsCategory.do?command=AddCategory&pid=<%= project.getId() %>"><ccp:label name="projectsCenterLists.categories.newList">New List</ccp:label></a>
          <%-- | <a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Lists_Categories&pid=<%= project.getId() %>"><ccp:label name="list.listView">List View</ccp:label></a> --%>
        </li>
      </ccp:permission>
    </ul>
  </div>
  <div class="profile-portlet-body">
    <c:forEach items="${categoryList}" var="thisCategory" varStatus="categoryStatus">
    <div class="workarea">
      <h3><ccp:permission name="project-lists-modify"><a href="<%= ctx %>/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${thisCategory.id}"></ccp:permission><c:out value="${thisCategory.description}"/><ccp:permission name="project-lists-modify"></a></ccp:permission></h3>
      <ul id="column_${thisCategory.id}" class="draglist">
        <c:forEach items="${outlineList}" var="thisTask">
          <c:if test="${thisTask.categoryId == thisCategory.id}">
            <li id="item_${thisTask.id}" class="list${categoryStatus.count} ${movableClass}">
              <c:choose>
                <c:when test="${!empty taskUrlMap[thisTask.id]}">
                  <a href="${taskUrlMap[thisTask.id]}" title="<c:out value="${thisTask.description}"/>"><c:out value="${thisTask.description}"/></a>
                </c:when>
                <c:otherwise>
                  <c:out value="${thisTask.description}"/>
                </c:otherwise>
              </c:choose>
            </li>
          </c:if>
        </c:forEach>
      </ul>
    </div>
  </c:forEach>
  </div>
</div>
<div id="overlay1" class="worktooltip"></div>
