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
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.connect.web.modules.login.utils.UserUtils" %>
<%@ page import="com.concursive.connect.web.modules.blog.dao.BlogPost" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="newsList" class="com.concursive.connect.web.modules.blog.dao.BlogPostList" scope="request"/>
<jsp:useBean id="projectNewsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="newsArticleCategoryList" class="com.concursive.connect.web.modules.blog.dao.BlogPostCategoryList" scope="request"/>
<jsp:useBean id="taskCategoryList" class="com.concursive.connect.web.modules.lists.dao.TaskCategoryList" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_news_email_menu.jspf" %>
<%-- begin articles --%>
<%
  Iterator i = newsList.iterator();
  while (i.hasNext()) {
    BlogPost thisArticle = (BlogPost) i.next();
    User thisUser = UserUtils.loadUser(thisArticle.getEnteredBy());
    request.setAttribute("thisUser", thisUser);
%>
<div class="portletWrapper">
<div class="articleContainer">
  <div class="articleHeader">
    <div class="header <ccp:permission if="any" name="project-news-edit,project-news-delete">admin</ccp:permission>">
      <h3><a href="javascript:popURL('<%= ctx %>/show/<%= project.getUniqueId() %>/post/<%= thisArticle.getId() %>?popup=true','Article_<%= thisArticle.getId() %>','600','500','yes','yes');"><%= toHtml(thisArticle.getSubject()) %></a></h3>
      <ccp:evaluate if="<%= thisArticle.getStatus() == BlogPost.DRAFT %>">
        <span class="red">
          <ccp:label name="projectsCenterNews.byArticle.draft">(Draft)</ccp:label>
        </span>
      </ccp:evaluate>
      <ccp:evaluate if="<%= thisArticle.getStatus() == BlogPost.UNAPPROVED %>">
        <span class="red">
          <ccp:label name="projectsCenterNews.byArticle.unapproved">(Unapproved)</ccp:label>
        </span>
      </ccp:evaluate>
    </div>
    <ccp:permission name="project-news-edit,project-news-delete" if="any">
      <div class="permissions">
        <ccp:permission name="project-news-edit">
          <%-- edit message --%>
          <a href="<%= ctx %>/BlogActions.do?command=Edit&pid=<%= project.getId() %>&id=<%= thisArticle.getId() %>"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.editThisItem">Edit this item</ccp:label>"></a>
          <%-- clone message --%>
            <a href="javascript:confirmForward('<%= ctx %>/BlogActions.do?command=Clone&pid=<%= project.getId() %>&id=<%= thisArticle.getId() %>');"><img src="<%= ctx %>/images/icons/stock_copy-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.makeACopyOfThisItem">Make a copy of this item</ccp:label>"></a>
          <%-- archive message --%>
          <%--
          <ccp:evaluate if="<%= thisArticle.getEndDate() == null %>">
          <div class="permissions">
              <a href="javascript:confirmForward('<%= ctx %>/BlogActions.do?command=Archive&pid=<%= project.getId() %>&id=<%= thisArticle.getId() %>');"><img src="<%= ctx %>/images/icons/stock_archive-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.archiveThisItem">Archive this item</ccp:label>"></a>
          </div>
          </ccp:evaluate>
          --%>
        </ccp:permission>
        <ccp:permission name="project-news-delete">
          <%-- delete message --%>
          <a href="javascript:confirmDelete('<%= ctx %>/BlogActions.do?command=Delete&pid=<%= project.getId() %>&id=<%= thisArticle.getId() %>');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.deleteThisItem">Delete this item</ccp:label>"></a>
        </ccp:permission>
      </div>
    </ccp:permission>
    <div class="rating">
      <ccp:rating id='<%= thisArticle.getId() %>'
                     showText='false'
                     count='<%= thisArticle.getRatingCount() %>'
                     value='<%= thisArticle.getRatingValue() %>'
                     url='<%= ctx + "/BlogActions.do?command=SetRating&pid=" + thisArticle.getProjectId() + "&id=" + thisArticle.getId() + "&v=${vote}&out=text" %>'/>
    </div>
    <div class="details">
      <ul>
        <li><ccp:tz timestamp="<%= thisArticle.getStartDate() %>" dateFormat="<%= DateFormat.LONG %>" /></li>
        <li>Posted By: <ccp:username id="<%= thisArticle.getEnteredBy() %>"/></li>
      </ul>
    </div>
  </div>
  <div class="articleBody">
    <%= thisArticle.getIntro() %>
    <ccp:evaluate if="<%= StringUtils.hasText(thisArticle.getMessage()) %>">
      <%= thisArticle.getMessage() %>
    </ccp:evaluate>
  </div>
</div>
<%
  }
%>
