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
<%@ page import="com.concursive.connect.web.modules.blog.dao.BlogPost" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="newsList" class="com.concursive.connect.web.modules.blog.dao.BlogPostList" scope="request"/>
<jsp:useBean id="projectNewsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="newsArticleCategoryList" class="com.concursive.connect.web.modules.blog.dao.BlogPostCategoryList" scope="request"/>
<jsp:useBean id="taskCategoryList" class="com.concursive.connect.web.modules.lists.dao.TaskCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_news_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<%-- begin list of article categories and their subjects --%>
<%
  int count = 0;
  int previousCategoryId = -2;
  int rowid = 0;
  Iterator i = newsList.iterator();
  while (i.hasNext()) {
    ++count;
    rowid = (rowid != 1?1:2);
    BlogPost thisArticle = (BlogPost) i.next();
%>
  <ccp:evaluate if="<%= previousCategoryId != thisArticle.getCategoryId() %>">
  <ccp:evaluate if="<%= count > 1 %>">
  </table>
  </ccp:evaluate>
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
          <ccp:evaluate if="<%= thisArticle.getCategoryId() > -1 %>">
            <%= toHtml(newsArticleCategoryList.getValueFromId(thisArticle.getCategoryId())) %>
          </ccp:evaluate>
          <ccp:evaluate if="<%= thisArticle.getCategoryId() == -1 %>">
            <%= toHtml(project.getTitle()) %>
          </ccp:evaluate>
        </th>
      </tr>
    </thead>
  </ccp:evaluate>
    <tbody>
      <tr class="row<%= rowid %>">
        <td width="8" valign="top" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuItem', <%= thisArticle.getId() %>);"
             onMouseOver="over(0, <%= count %>)"
             onmouseout="out(0, <%= count %>); hideMenu('menuItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0"></a>
        </td>
        <td valign="top">
          <a href="javascript:popURL('<%= ctx %>/show/<%= project.getUniqueId() %>/post/<%= thisArticle.getId() %>?popup=true','Article','600','400','yes','yes');"><%= toHtml(thisArticle.getSubject()) %></a>
          <ccp:evaluate if="<%= hasText(thisArticle.getPortalKey()) %>"><em><%= toHtml(thisArticle.getPortalKey()) %></em></ccp:evaluate>
          <div class="portlet-message-alert">
            <ccp:evaluate if="<%= thisArticle.getStatus() == BlogPost.DRAFT %>">DRAFT</ccp:evaluate>
            <ccp:evaluate if="<%= thisArticle.getStatus() == BlogPost.UNAPPROVED %>">FOR REVIEW</ccp:evaluate>
          </div>
        </td>
      </tr>
    <%
        if (previousCategoryId != thisArticle.getCategoryId()) {
          previousCategoryId = thisArticle.getCategoryId();
        }
      }
    %>
  </tbody>
<ccp:evaluate if="<%= newsList.size() > 0 %>">
  </table>
</ccp:evaluate>
