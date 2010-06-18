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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.connect.web.utils.LookupElement" %>
<%@ page import="com.concursive.commons.objects.ObjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.lists.dao.Task" %>
<%@ page import="com.concursive.connect.cms.portal.dao.ProjectItem" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="category" class="com.concursive.connect.web.modules.lists.dao.TaskCategory" scope="request"/>
<jsp:useBean id="outlineList" class="com.concursive.connect.web.modules.lists.dao.TaskList" scope="request"/>
<jsp:useBean id="itemList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="trailMap" class="com.concursive.connect.web.utils.TrailMap" scope="request"/>
<jsp:useBean id="functionalAreaList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="complexityList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="businessValueList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="targetSprintList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="targetReleaseList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="statusList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="loeRemainingList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="assignedPriorityList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="taskUrlMap" class="java.util.HashMap" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<%@ include file="initPage.jsp" %>
<%-- Bucket View --%>
<ccp:permission name="project-lists-modify">
  <script language="JavaScript" type="text/javascript" src="${ctx}/javascript/bucketlist.js?v=20080117"></script>
</ccp:permission>
<%-- Animation --%>
<script language="JavaScript" type="text/javascript">
  function bucketMove(itemId, columnId) {
    callBucket("${ctx}/ProjectManagementListsBuckets.do?command=Move&pid=<%= project.getId() %>&id=" + itemId + "&columnId=" + columnId + "&key=<%= itemList.getObjectKeyProperty() %>&out=text");
  }
  function handleEditListSaved() {
    window.location.href = '${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=<%= StringUtils.encodeUrl(request.getParameter("table")) %>';
  }
  function addItem(formFieldValue) {
    var formField = document.getElementById(formFieldValue);
    if (formField.value != '') {
      var columnId;
      // Create a new LI with a new id and blank value
      var eLI = document.createElement("li");
      // Create a RANDOM ID (based on ms for the attribute name)
      var d = new Date();
      var idValue = d.getHours() + "" + d.getMinutes() + "" + d.getSeconds() + "" + d.getMilliseconds();
      eLI.setAttribute("id", "item_" + idValue);
      eLI.setAttribute("class", "list1");
      eLI.appendChild(document.createTextNode("(saving)"));
      // Attach it to the first (unset) list
      var uItems = document.getElementsByTagName("ul");
      for(var j=0; j<uItems.length; j++){
        if (uItems[j].id.indexOf("column_")> -1) {
          var masterUL = uItems[j];
          masterUL.appendChild(eLI);
          columnId = uItems[j].id;
          break;
        }
      }
      // Take the contents, the table, the filters, and submit
      // The saving part will change the contents of the LI
      callBucket("${ctx}/ProjectManagementListsBuckets.do?command=Add&pid=<%= project.getId() %>&cid=${category.id}&id=" + eLI.getAttribute("id") + "&description=" + encodeURIComponent(formField.value) + "&columnId=" + columnId + "&key=<%= itemList.getObjectKeyProperty() %>&trail=<%= trailMap.getTrailParameters() %>&out=text");
    }

    // Reset the form
    formField.value = "";
    formField.focus();
    return false;
  }
  function deleteBucketItem(taskId) {
    if (confirm("Delete?")) {
      // The saving part will delete the LI
      callDeleteBucketItem("${ctx}/ProjectManagementListsBuckets.do?command=Delete&pid=<%= project.getId() %>&cid=${category.id}&id=" + taskId + "&out=text");
    }
  }
</script>
<div class="portletWrapper">
  <div class="profile-portlet-header">
    <h2><c:out value="${category.description}"/></h2>
  </div>
  <div class="profile-portlet-menu">
    <ul>
      <li class="last">
        <a href="${ctx}/show/${project.uniqueId}/lists">back to lists</a>
      </li>
    </ul>
  </div>
  <div class="profile-portlet-body">
    <%-- Group by --%>
      <div class="workoptions">
        <ul>
          <c:if test="${fn:length(functionalAreaList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=functional_area">Functional Area</a></li>
          </c:if>
          <c:if test="${fn:length(complexityList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=complexity">Complexity</a></li>
          </c:if>
          <c:if test="${fn:length(businessValueList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=value">Business Value</a></li>
          </c:if>
          <c:if test="${fn:length(targetSprintList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=sprint">Target Sprint</a></li>
          </c:if>
          <c:if test="${fn:length(targetReleaseList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=release">Target Release</a></li>
          </c:if>
          <c:if test="${fn:length(statusList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=status">Status</a></li>
          </c:if>
          <c:if test="${fn:length(loeRemainingList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=loe_remaining">Remaining</a></li>
          </c:if>
          <c:if test="${fn:length(assignedPriorityList) > 1}">
            <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=assigned_priority">Assigned Priority</a></li>
          </c:if>
          <li><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters() %>&table=owner">Owner</a></li>
        </ul>
      </div>
    <%-- Filter by --%>
      <ccp:evaluate if="<%= trailMap.size() > 0 %>">
        <div class="workfilters">
          Filters:
      <%
        for (int iCount = 0; iCount < trailMap.size(); iCount++) {
          if (iCount % 2 == 0) {
            String thisSubject = (String) trailMap.get(iCount);
            String thisValue = (String) trailMap.get(iCount + 1);
            String displayValue = null;
            if ("status".equals(thisSubject)) {
              displayValue = statusList.getValueFromId(Integer.parseInt(thisValue));
            } else if ("loeRemaining".equals(thisSubject)) {
              displayValue = loeRemainingList.getValueFromId(Integer.parseInt(thisValue));
            } else if ("targetRelease".equals(thisSubject)) {
              displayValue = targetReleaseList.getValueFromId(Integer.parseInt(thisValue));
            } else if ("targetSprint".equals(thisSubject)) {
              displayValue = targetSprintList.getValueFromId(Integer.parseInt(thisValue));
            } else if ("businessValue".equals(thisSubject)) {
              displayValue = businessValueList.getValueFromId(Integer.parseInt(thisValue));
            } else if ("complexity".equals(thisSubject)) {
              displayValue = complexityList.getValueFromId(Integer.parseInt(thisValue));
            } else if ("functionalArea".equals(thisSubject)) {
              displayValue = functionalAreaList.getValueFromId(Integer.parseInt(thisValue));
            } else if ("assignedPriority".equals(thisSubject)) {
              displayValue = assignedPriorityList.getValueFromId(Integer.parseInt(thisValue));
            }
      %>
          <ccp:evaluate if="<%= iCount > 0 %>">/</ccp:evaluate>
          <a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters(thisSubject) %>&table=<%= StringUtils.encodeUrl(request.getParameter("table")) %>"><%= toHtml(thisSubject) %></a>
          <ccp:evaluate if="<%= displayValue != null %>">(<%= displayValue %>)</ccp:evaluate>
          <ccp:evaluate if="<%= displayValue == null && thisValue != null %>">(<ccp:username id="<%= thisValue %>"/>)</ccp:evaluate>
      <%
          }
        }
      %>
        </div>
      </ccp:evaluate>
    <%-- Add Form --%>
      <ccp:permission name="project-lists-modify">
        <div id="workcenter">
        <ccp:evaluate if="<%= category.getId() > -1 %>">
          <div id="workform">
            <form method="get" name="addItemForm" action="#" onSubmit="return addItem('description');">
              Add Item
              <div style="display:inline">
                <input type="text" id="description" name="description" style="width:400px" />
                <input type="submit" name="addItemButton" value="Save" />
              </div>
            </form>
          </div>
        </ccp:evaluate>
        <ccp:evaluate if='<%= request.getParameter("table") != null && !"owner".equals(request.getParameter("table")) %>'>
          <div class="workoptionseditor">
            [<a href="javascript:popURL('${ctx}/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=<%= StringUtils.encodeUrl(request.getParameter("table")) %>&popup=true','650','400','yes','yes');">configure list</a>]
          </div>
        </ccp:evaluate>
        </div>
      </ccp:permission>
    <%-- Buckets --%>
      <%
      int columnCount = 0;
      int itemCount = 0;
      Iterator ic = itemList.iterator();
      while (ic.hasNext()) {
        ++columnCount;
        ProjectItem thisItem = (ProjectItem) ic.next();
    %>
      <div class="workarea<%= (columnCount < 3 ? String.valueOf(columnCount) : "") %>">
        <div>
          <div class="columnheader">
            <h3><a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= project.getId() %>&cid=${category.id}&trail=<%= trailMap.getTrailParameters(itemList.getObjectKeyProperty()) %>&value=<%= itemList.getObjectKeyProperty() %>|<%= thisItem.getId() == -1 ? "0" : String.valueOf(thisItem.getId()) %>&table=<%= StringUtils.encodeUrl(request.getParameter("table")) %>">
            <ccp:evaluate if="<%= !\"owner\".equals(request.getParameter(\"table\")) %>"><%= thisItem.getName() %></ccp:evaluate>
            <ccp:evaluate if="<%= \"owner\".equals(request.getParameter(\"table\")) %>"><ccp:username id="<%= thisItem.getId() %>"/></ccp:evaluate>
            </a></h3>
          </div>
          <%--
          <ccp:evaluate if="<%= columnCount > 1 %>">
          <div class="columntoggle">
            [<a href="javascript:bucketToggle('<%= thisItem.getId() %>');">+</a>]
          </div>
          </ccp:evaluate>
          --%>
        </div>
        <ul id="column_<%= thisItem.getId() %>" class="draglist<%= (columnCount == 1 ? "1" : "") %>">
      <%
          Iterator i = outlineList.iterator();
          while (i.hasNext()) {
            Task thisTask = (Task)i.next();
            if (ObjectUtils.getParamAsInt(thisTask, itemList.getObjectKeyProperty()) == thisItem.getId()) {
              ++itemCount;
      %>
            <li id="item_<%= thisTask.getId() %>" class="list<%= columnCount == 1 ? "1" : "2" %>">
              <ccp:evaluate if="<%= taskUrlMap.get(thisTask.getId()) != null %>">
               <a href="<%= taskUrlMap.get(thisTask.getId())%>" title="<%= toHtml(thisTask.getDescription()) %>">
                <%= toHtml(thisTask.getDescription()) %>
               </a>
              </ccp:evaluate>
              <ccp:evaluate if="<%= taskUrlMap.get(thisTask.getId()) == null %>">
                <%= toHtml(thisTask.getDescription()) %>
              </ccp:evaluate>
              <div class="bucketItemActionContainer">
                (<div id="tooltip_<%= thisTask.getId() %>" class="bucketItemTooltip"><%= thisTask.getId() %></div>)
                <ccp:permission name="project-lists-modify">
                  <a href="#Delete" onclick="deleteBucketItem(<%= thisTask.getId() %>)" class="bucketItemDeleteAction">X</a>
                </ccp:permission>
              </div>
            </li>
      <%
            }
          }
      %>
        </ul>
      </div>
      <%
        }
      %>
  </div>
</div>
<div id="overlay1" style="position:absolute; background-color:#FFFFFF; height:auto; z-index:1000 !important; visibility:hidden;"></div>
