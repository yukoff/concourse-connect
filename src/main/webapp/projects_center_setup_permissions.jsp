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
<%@ page
    import="com.concursive.connect.web.modules.profile.dao.PermissionLookup" %>
<%@ page
    import="com.concursive.connect.web.modules.profile.dao.PermissionCategoryLookup" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="categories" class="com.concursive.connect.web.modules.profile.dao.PermissionCategoryLookupList" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function updateRole() {}
</script>
<div class="portletWrapper">
  <h1>
    <ccp:label name="projectsCenterSetup.permissions.configurePermissions">Configure Permissions</ccp:label>
    <span><a href="${ctx}/show/${project.uniqueId}/setup"><ccp:label name="projectsCenterSetup.customize.setup">Back to Setup</ccp:label></a></span>
  </h1>
  <div class="formContainer">
    <form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagement.do?command=UpdatePermissions">
    <%--
    <input type="submit" value="<ccp:label name="button.update">Update</ccp:label>">
    <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.location.href='<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Setup&pid=<%= project.getId() %>'"><br>
    --%>
    <table class="pagedList">
      <input type="hidden" name="pid" value="<%= project.getId() %>">
      <input type="hidden" name="id" value="<%= project.getId() %>">
      <input type="hidden" name="modified" value="<%= project.getModified() %>">
      <thead>
        <tr>
          <th colspan="4" valign="center">
            <ccp:label name="projectsCenterSetup.permissions.permissions">Permissions</ccp:label>
          </th>
        </tr>
      </thead>
      <tbody>
          <%
             int permissionCount = 0;
             Iterator i = categories.iterator();
             while (i.hasNext()) {
               PermissionCategoryLookup thisCategory = (PermissionCategoryLookup) i.next();
          %>
          <%-- For each category --%>
            <tr class="row1">
              <td width="100%" nowrap><%= toHtml(thisCategory.getDescription()) %></td>
              <td><ccp:label name="projectsCenterSetup.permissions.lowestRole">Lowest Role</ccp:label></td>
            </tr>
          <%
              Iterator j = thisCategory.getPermissions().iterator();
              while (j.hasNext()) {
                ++permissionCount;
                PermissionLookup thisPermission = (PermissionLookup) j.next();
                // Temp. fix for Weblogic
                String permName = "perm" + permissionCount + "level";
                String permValue = String.valueOf(project.getAccessUserLevel(thisPermission.getPermission()));
          %>
          <%-- For each permission --%>
            <tr class="row2" onmouseover="swapClass(this,'rowHighlight')" onmouseout="swapClass(this,'row2')">
              <td width="100%" nowrap>&nbsp; &nbsp;<%= toHtml(thisPermission.getDescription()) %></td>
              <td align="center">
                <input type="hidden" name="perm<%= permissionCount %>" value="<%= thisPermission.getId() %>">
                <ccp:roleSelect
                    name="<%= permName %>"
                    value="<%= permValue %>"/>
              </td>
            </tr>
          <%-- End Content --%>
          <%
               }
             }
          %>
        </tbody>
      </table>
      <input type="hidden" name="token" value="${clientType.token}" />
      <input type="submit" value="<ccp:label name="button.update">Update</ccp:label>" class="submit">
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='${ctx}/show/${project.uniqueId}/setup'">
    </form>
  </div>
</div>
    
