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
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.connect.web.utils.CounterPair" %>
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.connect.web.modules.blog.dao.BlogPost" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.login.utils.UserUtils" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="newsList" class="com.concursive.connect.web.modules.blog.dao.BlogPostList" scope="request"/>
<jsp:useBean id="newsCounter" class="com.concursive.connect.web.modules.blog.utils.BlogPostCounter" scope="request"/>
<jsp:useBean id="newsArticleCategoryList" class="com.concursive.connect.web.modules.blog.dao.BlogPostCategoryList" scope="request"/>
<jsp:useBean id="taskCategoryList" class="com.concursive.connect.web.modules.lists.dao.TaskCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
  <h1><ccp:tabLabel name="News" object="project"/></h1>
  <%= showError(request, "actionError") %>
  <ccp:evaluate if="<%= newsList.isEmpty() %>">
    <ccp:label name="projectsCenterNews.noNews">No posts to display.</ccp:label>
  </ccp:evaluate>
  <div id="message" class="menu">
    <p>Thank you for your valuable feedback.</p>
  </div>
  <%-- begin articles --%>
  <%
    Iterator i = newsList.iterator();
    while (i.hasNext()) {
      BlogPost thisArticle = (BlogPost) i.next();
      request.setAttribute("thisArticle", thisArticle);
      User thisUser = UserUtils.loadUser(thisArticle.getEnteredBy());
      request.setAttribute("thisUser", thisUser);
  %>
  <div class="articleContainer">
    <div class="articleHeader <ccp:permission if="any" name="project-news-edit,project-news-delete">admin</ccp:permission>">
      <div class="header">
        <portlet:renderURL var="detailsUrl">
          <portlet:param name="portlet-action" value="show"/>
          <portlet:param name="portlet-object" value="post"/>
          <portlet:param name="portlet-value" value="${thisArticle.id}"/>
        </portlet:renderURL>
        <h3>
          <a href="${detailsUrl}">
            <%= toHtml(thisArticle.getSubject()) %>
          </a>
        </h3>
        <ccp:evaluate if="<%= thisArticle.getStatus() == BlogPost.DRAFT %>">
          <span class="red">
            <ccp:label
              name="projectsCenterNews.byArticle.draft">(Draft)</ccp:label>
          </span>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisArticle.getStatus() == BlogPost.UNAPPROVED %>">
          <span class="red">
            <ccp:label
            name="projectsCenterNews.byArticle.unapproved">(Unapproved)</ccp:label>
          </span>
        </ccp:evaluate>
      </div>
      <ccp:permission name="any">
        <div class="permissions">
          <ccp:permission name="project-news-edit,project-news-delete" if="any">
            <portlet:renderURL var="updateUrl">
              <portlet:param name="portlet-action" value="modify"/>
              <portlet:param name="portlet-object" value="post"/>
              <portlet:param name="portlet-value" value="${thisArticle.id}"/>
            </portlet:renderURL>
            <a href="${updateUrl}">
              <img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle"
                   title="<ccp:label name="projectsCenterNews.byArticle.editThisItem">Edit this item</ccp:label>">
            </a>
            <%-- clone message --%>
            <portlet:actionURL var="cloneUrl">
              <portlet:param name="portlet-value" value="${thisArticle.id}"/>
              <portlet:param name="portlet-command" value="clone"/>
            </portlet:actionURL>
            <a href="javascript:confirmForward('${cloneUrl}')">
              <img src="<%= ctx %>/images/icons/stock_copy-16.gif"
                   border="0" align="absmiddle"
                   title="<ccp:label name="projectsCenterNews.byArticle.makeACopyOfThisItem">Make a copy of this item</ccp:label>">
            </a>
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
            <portlet:actionURL var="deleteUrl">
              <portlet:param name="portlet-value" value="${thisArticle.id}"/>
              <portlet:param name="portlet-command" value="delete"/>
            </portlet:actionURL>
            <a href="javascript:confirmDelete('${deleteUrl}');">
              <img src="<%= ctx %>/images/icons/stock_delete-16.gif"
                   border="0" align="absmiddle"
                   title="<ccp:label name="projectsCenterNews.byArticle.deleteThisItem">Delete this item</ccp:label>">
            </a>
          </ccp:permission>
        </div>
      </ccp:permission>
      <div class="details">
        <div class="projectCenterProfileImage">
          <c:if test="${!empty project.category.logo}">
            <div class="imageContainer">
              <c:choose>
                <c:when test="${!empty thisUser.profileProject.logo}">
                  <img alt="<c:out value="${thisUser.profileProject.title}"/> photo"
                       src="${ctx}/image/<%= thisUser.getProfileProject().getLogo().getUrlName(50,50) %>"/>
                </c:when>
                <c:when test="${!empty thisUser.profileProject.category.logo}">
                  <img alt="Default user photo"
                       src="${ctx}/image/<%= thisUser.getProfileProject().getCategory().getLogo().getUrlName(50,50) %>" class="default-photo" />
                </c:when>
              </c:choose>
            </div>
          </c:if>
        </div>
        <ul>
          <li><span>Posted On:</span> <ccp:tz timestamp="<%= thisArticle.getStartDate() %>"
                                                 dateFormat="<%= DateFormat.LONG %>" pattern="MM/dd/yy' at 'h:mm a"/>
          </li>
          <li><span>Posted By:</span> <ccp:username id="<%= thisArticle.getEnteredBy() %>"/>
          </li>
          <li>
            <ccp:evaluate if="<%= thisArticle.getId() > -1 && User.isLoggedIn() %>">
            <a href="javascript:showPanel('Send author a question','${ctx}/show/${thisUser.profileProject.uniqueId}/app/compose_message?module=blog&id=${thisArticle.id}',700)"><img src="${ctx}/images/icons/mail_pencil.png" alt="Message icon" align="top">  Send ${thisUser.profileProject.title} a message</a>
            </ccp:evaluate>
          </li>
        </ul>
      </div>
    </div>
    <div class="articleBody">
      <%= thisArticle.getIntro() %>
    </div>
    <div class="articleFooter">
      <span class="comments">
        <c:set var="commentText">
          <c:choose>
            <c:when test="${thisArticle.numberOfComments == 0}">
              <em>No comments yet</em>
            </c:when>
            <c:when test="${thisArticle.numberOfComments == 1}">
              <em>1 comment</em>
            </c:when>
            <c:otherwise>
              <em><c:out value="${thisArticle.numberOfComments} comments"/></em>
            </c:otherwise>
          </c:choose>
        </c:set>
        <c:choose>
          <c:when test="${thisArticle.numberOfComments > 0}">
            <%-- TODO: Fix Link
            <a href="${detailsUrl}/comments">${commentText}</a>
            --%>
            <img src="${ctx}/images/icons/balloons.png" alt="comments icon" align="absmiddle"> <a href="${detailsUrl}#comments">${commentText}</a>
          </c:when>
          <c:otherwise>
            ${commentText}
          </c:otherwise>
        </c:choose>
      </span>
    </div>
    <div class="portlet-menu">
      <ul>
        <li><a href="${detailsUrl}">Read full post</a></li>
        <li>
          <ccp:evaluate if="<%= thisArticle.getId() > -1 && User.isLoggedIn() %>">
          <a href="javascript:showSpan('thisCommentWindow<%= thisArticle.getId() %>');window.scrollBy(0,1000);document.getElementById('comment').focus();">Add
            a comment</a>
         </ccp:evaluate>
        </li>
      </ul>
    </div>
    <div class="userInputFooter">
      <ccp:evaluate if="<%= thisArticle.getRatingCount() > 0 %>">
        <p>(<%= thisArticle.getRatingValue() %> out of <%= thisArticle.getRatingCount() %> <%= thisArticle.getRatingCount() == 1 ? " person" : " people"%> found this blog post useful.)</p>
      </ccp:evaluate>
      <ccp:evaluate if="<%= thisArticle.getInappropriateCount() > 0 && ProjectUtils.hasAccess(thisArticle.getProjectId(), User, \"project-news-edit\")%>">
        <p>(<%= thisArticle.getInappropriateCount() %><%= thisArticle.getInappropriateCount() == 1? " person" : " people"%> found this blog post inappropriate.)</p>
      </ccp:evaluate>
        <%-- any user who is not the author of the blog can mark the blog as useful  --%>
       <ccp:permission name="project-news-view">
        <ccp:evaluate if="<%= (thisArticle.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
          <p>Is this blog post useful?
          <portlet:renderURL var="ratingUrl" windowState="maximized">
            <portlet:param name="portlet-command" value="setRating"/>
            <portlet:param name="id" value="${thisArticle.id}"/>
            <portlet:param name="v" value="1"/>
            <portlet:param name="out" value="text"/>
          </portlet:renderURL>
          <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + thisArticle.getId() %>','message');">Yes</a>&nbsp;
          <portlet:renderURL var="ratingUrl" windowState="maximized">
            <portlet:param name="portlet-command" value="setRating"/>
            <portlet:param name="id" value="${thisArticle.id}"/>
            <portlet:param name="v" value="0"/>
            <portlet:param name="out" value="text"/>
          </portlet:renderURL>
          <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + thisArticle.getId() %>','message');">No</a>&nbsp;
     	  <ccp:evaluate if="<%= thisArticle.getId() > -1 && User.isLoggedIn() %>">
 			<a href="javascript:showPanel('Mark this blog post as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=blog&pid=${project.id}&id=${thisArticle.id}',700)">Report this as inappropriate</a>
 		  </ccp:evaluate>
 		  </p>
          <div id="message_<%= thisArticle.getId() %>"></div>
        </ccp:evaluate>
        </ccp:permission>
    </div>
   <ccp:evaluate if="<%= thisArticle.getId() > -1 && User.isLoggedIn() %>">
    <div class="formContainer" id="thisCommentWindow<%= thisArticle.getId() %>"
         style="display:none; padding-top: 10px;">
      <portlet:actionURL var="saveCommentUrl" portletMode="view">
        <portlet:param name="portlet-action" value="show"/>
        <portlet:param name="portlet-object" value="post"/>
        <portlet:param name="portlet-value" value="${thisArticle.id}"/>
        <portlet:param name="portlet-command" value="saveComments"/>
      </portlet:actionURL>
      <form action="${saveCommentUrl}" method="post">
        <fieldset id="thisCommentWindow<%= thisArticle.getId() %>">
          <legend>Add a suggestion or comment to this blog post <span class="required">*</span></legend>
          <div class="commentBox">
            <%= showAttribute(request, "commentError") %>
            <textarea id="comment${thisArticle.id}" name="comment" style="width:100%" rows="4"></textarea>
            <p>Comments are reset by an administrator when they no longer apply.</p>
          </div>
          <input type="hidden" name="newsId" value="${thisArticle.id}"/>
        </fieldset>
        <input type="submit" name="Save" value="Save" class="submit"/>
        <input type="button" value="Cancel" onclick="hideSpan('thisCommentWindow<%= thisArticle.getId() %>')"
               class="cancel"/>
      </form>
    </div>
    </ccp:evaluate>
  </div>
<%
  }
%>
<c:if test="${projectNewsInfo.numberOfPages > 1}">
  <div class="pagination">
    <portlet:renderURL var="pagingUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="blog"/>
    </portlet:renderURL>
    <ccp:paginationControl object="projectNewsInfo" url="${pagingUrl}"/>
  </div>
</c:if>
