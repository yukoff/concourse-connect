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
<%@ include file="initPage.jsp" %>
<%-- Trails --%>
<table border="0" cellpadding="1" cellspacing="0" width="100%">
  <tr class="subtab">
    <td>
      <img border="0" src="<%= ctx %>/images/icons/stock_list_enum2-16.gif" align="absmiddle" />
      <a href="<%= ctx %>/show/<%= project.getUniqueId() %>/lists"><ccp:label name="projectsCenterLists.categories.lists">Lists</ccp:label></a> >
      <ccp:label name="projectsCenter.configuration">Configuration</ccp:label>
    </td>
  </tr>
</table>
<br />
<%-- End Trails --%>
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=functional_area"><ccp:label name="projectsCenterLists.config.functionalArea">Functional Area</ccp:label></a><br />
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=complexity"><ccp:label name="projectsCenterLists.config.complexity">Complexity</ccp:label></a><br />
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=value"><ccp:label name="projectsCenterLists.config.businessValue">Business Value</ccp:label></a><br />
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=sprint"><ccp:label name="projectsCenterLists.config.targetSprint">Target Sprint</ccp:label></a><br />
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=release"><ccp:label name="projectsCenterLists.config.targetRelease">Target Release</ccp:label></a><br />
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=status"><ccp:label name="projectsCenterLists.config.status">Status</ccp:label></a><br />
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=loe_remaining"><ccp:label name="projectsCenterLists.config.remaining">Remaining</ccp:label></a><br />
<a href="<%= ctx %>/ProjectManagementListsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=assigned_priority"><ccp:label name="projectsCenterLists.config.assignedPriority">Assigned Priority</ccp:label></a><br />
