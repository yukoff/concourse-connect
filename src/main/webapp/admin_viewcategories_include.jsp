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
<%@ page import="java.util.Iterator"%>
<%@ page import="com.concursive.connect.web.modules.issues.dao.TicketCategory"%>
<jsp:useBean id="categoryEditor" class="com.concursive.connect.web.modules.issues.beans.CategoryEditor" scope="request"/>
<jsp:useBean id="editList" class="com.concursive.connect.web.modules.issues.dao.TicketCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript">
  function loadCategories(currentLevel, nextLevel) {
    var url = "";
    var categoryList = document.getElementById('level' + currentLevel);
    var selectedId = categoryList.selectedIndex;
    if (selectedId > -1) {
      var categoryId = categoryList.options[selectedId].value;
      if (categoryId != "-1") {
        var projectId =  document.getElementById('projectId').value;
        url = '<%= ctx %>/ProjectManagementTicketsConfig.do?command=CategoryJSList&categoryId=' + categoryId + '&nextLevel=' + nextLevel + '&projectId=' + projectId + '&style=true';
        window.frames['server_commands'].location.href=url;
      }
    }
    processButtons(currentLevel);
  }
  function processButtons(level) {
    if (document.getElementById('level' + level).selectedIndex != -1) {
      for (i = (parseInt(level) + 1); i < <%= categoryEditor.getMaxLevels() %>; i++) {
        document.getElementById('edit' + i).disabled = true;
      }
      document.getElementById('edit' + (parseInt(level) + 1)).disabled = false;
    } else {
      for (i = (parseInt(level) + 1); i < <%= categoryEditor.getMaxLevels() %>; i++) {
        document.getElementById('edit' + i).disabled = true;
        resetList(document.getElementById('level' + i));
      }
    }
  }
  function editList(currentLevel, parentLevel) {
    var parentId = "0";
    if ("0" != currentLevel) {
      parentId =  document.getElementById('level' + parentLevel).options[document.getElementById('level' + parentLevel).selectedIndex].value;
    }
    var projectId =  document.getElementById('projectId').value;
    popURL('<%= ctx %>/ProjectManagementTicketsConfig.do?command=EditTicketList&form=categoryEditor&field=level' + currentLevel + '&parentId=' + parentId + '&catLevel=' + currentLevel + '&projectId=' + projectId + '&popup=true','600','300','yes','yes');
  }
  function resetList(list) {
    list.options.length = 0;
    list.options[list.length] = newOpt("-------- None --------", "-1");
  }
  function newOpt(param, value) {
    var newOption = document.createElement("OPTION");
    newOption.text=param;
    newOption.value=value;
    return newOption;
  }
</script>
<table border="0">
  <tr>
    <td align="center" class="row1">
      Primary Category
    </td>
<%
  for (int k = 1; k < categoryEditor.getMaxLevels(); k++) {
%>
    <td align="center" class="row1">
      Subcategory <%= k %>
    </td>
<%
  }
%>
  </tr>
  <tr>
    <td align="center">
      <select size="10" style="width: 150px;" name="level0" id="level0" onChange="javascript:loadCategories('0','1')">
        <ccp:evaluate if="<%= editList.size() == 0 %>"></ccp:evaluate>
<%
  Iterator i = editList.iterator();
  while (i.hasNext()) {
    TicketCategory thisCategory = (TicketCategory) i.next();
%>
        <option value="<%= thisCategory.getId() %>"><%= toHtml(thisCategory.getDescription()) %></option>
<%
  }
%>
      </select>
      <br />
      <input type="button" id="edit0" name="edit0" value="Edit List" onclick="editList('0','0');" />
    </td>
<%
   for (int k = 1; k < categoryEditor.getMaxLevels(); k++) {
%>
    <td align="center">
      <select size="10" style="width: 150px;" name="level<%= k %>" id="level<%= k %>" <ccp:evaluate if="<%= (k + 1) < categoryEditor.getMaxLevels() %>">onChange="javascript:loadCategories('<%= k %>', '<%= k + 1 %>')"</ccp:evaluate>>
        <option value="-1">-------- None --------</option>
      </select>
      <br />
      <input type="button" id="edit<%= k %>" name="edit<%= k %>" value="Edit List" onclick="editList('<%= k %>','<%= k - 1 %>');" disabled="true" />
    </td>
<%
   }
%>
  </tr>
</table>
<br />
