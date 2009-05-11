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
<%@ page import="java.util.Iterator" %>
<%@ page import="com.concursive.connect.cms.portal.dao.ProjectItem" %>
<%@ page import="com.concursive.connect.web.modules.lists.dao.TaskList" %>
<%@ page import="com.concursive.connect.web.modules.lists.dao.Task" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="releaseStatusMap" class="java.util.LinkedHashMap" scope="request"/>
<jsp:useBean id="statusList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="categoryId" class="java.lang.String" scope="request"/>
<jsp:useBean id="category" class="com.concursive.connect.web.modules.lists.dao.TaskCategory" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="3">
        <%= toHtml(category.getDescription()) %>
      </th>
    </tr>
  </thead>
  <tbody>
<%
for (Object o : releaseStatusMap.keySet()) {
  ProjectItem thisRelease = (ProjectItem) o;
  LinkedHashMap statusMap = (LinkedHashMap) releaseStatusMap.get(thisRelease);
%>
  <tr class="row2">
    <td width="100%">
      <a href="${ctx}/ProjectManagementListsBuckets.do?command=List&pid=<%= thisRelease.getProjectId() %>&cid=<%= categoryId %>&trail=&value=targetRelease|<%= thisRelease.getId() %>&table=status"><%= toHtml(thisRelease.getName()) %></a>
    </td>
    <td nowrap>
      <table cellpadding="1" cellspacing="1" class="empty" style="border: 1px solid #666; margin: 2px" width="100">
        <tr>
<%
int total = 0;
for (Object p : statusMap.keySet()) {
  Integer thisStatusId = (Integer) p;
  Integer count = (Integer) statusMap.get(thisStatusId);
  total += count;
}
%>
          <ccp:evaluate if="<%= total == 0 %>">
            <td nowrap width="100">&nbsp;</td>
          </ccp:evaluate>
<%
for (Object p : statusMap.keySet()) {
  Integer thisStatusId = (Integer) p;
  Integer count = (Integer) statusMap.get(thisStatusId);
%>
          <ccp:evaluate if="<%= count > 0 %>">
            <td height="15" width="<%= (count * 100/ total) %>"
                <ccp:evaluate if="<%= (statusList.getFromId(thisStatusId)).getLevel() < 1 %>">
                  bgColor="#CCCCCC"
                </ccp:evaluate>
                <ccp:evaluate if="<%= (statusList.getFromId(thisStatusId)).getLevel() == 1 %>">
                  bgColor="#00F"
                </ccp:evaluate>
                <ccp:evaluate if="<%= (statusList.getFromId(thisStatusId)).getLevel() == 2 %>">
                  bgColor="#FF0"
                </ccp:evaluate>
                <ccp:evaluate if="<%= (statusList.getFromId(thisStatusId)).getLevel() == 3 %>">
                  bgColor="#0F0"
                </ccp:evaluate>
                <ccp:evaluate if="<%= (statusList.getFromId(thisStatusId)).getLevel() == 4 %>">
                  bgColor="#F00"
                </ccp:evaluate>
                <ccp:evaluate if="<%= (statusList.getFromId(thisStatusId)).getLevel() >= 5 %>">
                  bgColor="#000"
                </ccp:evaluate>
                nowrap class="progressCell"></td>
          </ccp:evaluate>
<%
  }
%>
        </tr>
      </table>
    </td>
    <td nowrap width="30" style="padding-left: 5px; text-align: center;">(<%= total %>)</td>
  </tr>
<%
  }
%>
  </tbody>
</table>
<%
  Iterator i = statusList.iterator();
  while (i.hasNext()) {
    ProjectItem thisStatus = (ProjectItem) i.next();
%>
<font
<ccp:evaluate if="<%= thisStatus.getLevel() < 1 %>">
  color="#CCCCCC"
</ccp:evaluate>
<ccp:evaluate if="<%= thisStatus.getLevel() == 1 %>">
  color="#00F"
</ccp:evaluate>
<ccp:evaluate if="<%= thisStatus.getLevel() == 2 %>">
  color="#FF0"
</ccp:evaluate>
<ccp:evaluate if="<%= thisStatus.getLevel() == 3 %>">
  color="#0F0"
</ccp:evaluate>
<ccp:evaluate if="<%= thisStatus.getLevel() == 4 %>">
  color="#F00"
</ccp:evaluate>
<ccp:evaluate if="<%= thisStatus.getLevel() >= 5 %>">
  color="#000"
</ccp:evaluate>
><%= thisStatus.getName() %></font>
<%  
  }
%>
