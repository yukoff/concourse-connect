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
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.connect.web.modules.issues.dao.TicketCategory" %>
<%@ include file="initPage.jsp" %>
<jsp:useBean id="categoryEditor" class="com.concursive.connect.web.modules.issues.beans.CategoryEditor" scope="request"/>
<jsp:useBean id="editList" class="com.concursive.connect.web.modules.issues.dao.TicketCategoryList" scope="request"/>
<html>
<head>
</head>
<body onload="page_init();">
<script language="JavaScript">
function newOpt(param, value) {
  var newOption = parent.document.createElement("OPTION");
	newOption.text=param;
	newOption.value=value;
  return newOption;
}
function page_init() {
  var list = parent.document.categoryEditor.elements['level<%= editList.getCatLevel() %>'];
  list.options.length = 0;
  <ccp:evaluate if="<%= editList.size() == 0 %>">
    list.options[list.length] = newOpt("-------- None --------", "-1");
  </ccp:evaluate>
<%
  Iterator list = editList.iterator();
  while (list.hasNext()) {
    TicketCategory thisCategory = (TicketCategory) list.next();
    if (thisCategory.getEnabled()) {
%>
      list.options[list.length] = newOpt("<%= toHtml(thisCategory.getDescription()) %>", "<%= thisCategory.getId() %>");
<%
    }
  }
  for (int k = editList.getCatLevel() + 1; k < categoryEditor.getMaxLevels() ; k++) {
%>
    resetList(parent.document.categoryEditor.elements['level<%= k %>']);
<%}%>
}
function resetList(list) {
  list.options.length = 0;
  list.options[list.length] = newOpt("-------- None --------", "-1");
}
</script>
</body>
</html>
