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
<%@ page import="com.concursive.connect.web.modules.badges.dao.Badge" %>
<jsp:useBean id="badgeCategoryList1" class="com.concursive.connect.web.modules.badges.dao.BadgeCategoryList" scope="request" />
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="badgeCategoryId" class="java.lang.String" scope="request"/>
<jsp:useBean id="badgeList" class="com.concursive.connect.web.modules.badges.dao.BadgeList" scope="request" />
<jsp:useBean id="projectCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="admin_badge_list_menu.jspf" %>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<a href="<%= ctx %>/AdminBadges.do?command=Modify&badgeCategoryId=<%= badgeCategoryId %>">Add Badge</a><br /><br />
<table class="pagedList">
  <thead>
    <tr>
      <th>
          Action
      </th>
      <th width="10%">
          Logo
      </th>
      <th width="60%">
          Name
      </th>
      <th width="20%" nowrap>
          Badge Category
      </th>
      <th width="10%" nowrap>
          Enabled
      </th>
    </tr>
  </thead>
  <tbody>
  <%
    if (badgeList.size() == 0) {
  %>
    <tr class="row2">
      <td colspan="5">No badges to display.</td>
    </tr>
      <%
      }
      int count1 = 0;
      int rowid1 = 0;
      Iterator<Badge> badgeItr = badgeList.iterator();
      while (badgeItr.hasNext()) {
        ++count1;
        rowid1 = (rowid1 != 1?1:2);
        Badge thisBadge = badgeItr.next();
    %>
    <tr class="row<%=rowid1%>">
      <td valign="top" align="center" nowrap>
        <a href="javascript:displayMenu('select_<%= SKIN %><%= thisBadge.getId() %>','menuItem',<%= thisBadge.getId() %>, <%= thisBadge.getCategoryId() %>);"
           onMouseOver="over(0, <%= thisBadge.getId() %>)"
           onmouseout="out(0, <%= thisBadge.getId() %>); hideMenu('menuItem');"><img
           src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= thisBadge.getId() %>" id="select_<%= SKIN %><%= thisBadge.getId() %>" align="absmiddle" border="0"></a>
      </td>
      <td>
        <ccp:evaluate if="<%= thisBadge.getLogoId() != -1 %>">
        <%= thisBadge.getLogo().getFullImageFromAdmin(ctx) %>
        </ccp:evaluate>&nbsp;
      </td>
      <td>
        <a href="<%= ctx %>/AdminBadges.do?command=Modify&badgeId=<%= thisBadge.getId() %>"><%= toHtml(thisBadge.getTitle()) %></a>
      </td>
      <td>
        <%= toHtml(badgeCategoryList1.getValueFromId(thisBadge.getCategoryId())) %>
      </td>
      <td>
      <%= thisBadge.getEnabled()?"Yes":"No" %>
      </td>
    </tr>
    <%
    }
  %>
  </tbody>
</table>
