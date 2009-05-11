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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="category" class="com.concursive.connect.web.modules.lists.dao.TaskCategory" scope="request"/>
<jsp:useBean id="Task" class="com.concursive.connect.web.modules.lists.dao.Task" scope="request"/>
<%@ include file="initPage.jsp" %>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        <ccp:label name="projectsCenterLists.details.listItem">List Item</ccp:label>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.details.description">Description</ccp:label></td>
      <td>
        <img border="0" src="<%= ctx %>/images/box<%= Task.getComplete()?"-checked":"" %>.gif" alt="" align="absmiddle">
        <%= toHtml(Task.getDescription()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="projectsCenterLists.details.priority">Priority</ccp:label></td>
      <td>
        <%= Task.getPriority() %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel" valign="top"><ccp:label name="projectsCenterLists.details.notes">Notes</ccp:label></td>
      <td>
        <%= toHtml(Task.getNotes()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel" valign="top"><ccp:label name="projectsCenterLists.details.entered">Entered</ccp:label></td>
      <td>
        <ccp:username id="<%= Task.getEnteredBy() %>"/>
        <ccp:tz timestamp="<%= Task.getEntered() %>"/>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel" valign="top"><ccp:label name="projectsCenterLists.details.modified">Modified</ccp:label></td>
      <td>
        <ccp:username id="<%= Task.getModifiedBy() %>"/>
        <ccp:tz timestamp="<%= Task.getModified() %>"/>
      </td>
    </tr>
  </tbody>
</table>
<%-- Temp. fix for Weblogic --%>
<%
boolean isPopup = "true".equals(request.getParameter("popup"));
%>
<ccp:evaluate if="<%= isPopup %>">
<input type="button" value="<ccp:label name="button.close">Close</ccp:label>" onclick="window.close()">
</ccp:evaluate>
