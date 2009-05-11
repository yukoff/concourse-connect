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
<%@ page import="com.concursive.connect.web.modules.login.dao.User" %>
<%@ page import="com.concursive.connect.web.modules.login.utils.UserUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.blog.dao.BlogPost" %>
<%@ page import="com.concursive.connect.web.modules.blog.dao.BlogPostComment" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="blog" class="com.concursive.connect.web.modules.blog.dao.BlogPost" scope="request"/>
<jsp:useBean id="commentList" class="com.concursive.connect.web.modules.blog.dao.BlogPostCommentList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<%
  User thisUser = UserUtils.loadUser(blog.getEnteredBy());
  request.setAttribute("thisUser", thisUser);
%>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<div class="portletWrapper">
  <div id="message" class="menu">
    <p>Thank you for your valuable feedback.</p>
  </div>
  <div id="deleteMessage" class="menu">
    <p>Comment deleted.</p>
  </div>
  <div class="articleContainer">
    <c:if test="${hideBlogDetails eq 'false'}">
      <div class="articleHeader <ccp:permission if="any" name="project-news-edit,project-news-delete">admin</ccp:permission>">
        <div class="header">
          <h2><%= toHtml(blog.getSubject()) %></h2>
        <span class="red">
        <ccp:evaluate if="<%= blog.getStatus() == BlogPost.DRAFT %>">
          <span class="red"><ccp:label name="projectsCenterNews.byArticle.draft">(Draft)</ccp:label></span>
        </ccp:evaluate>
        <ccp:evaluate if="<%= blog.getStatus() == BlogPost.UNAPPROVED %>">
          <span class="red"><ccp:label name="projectsCenterNews.byArticle.unapproved">(Unapproved)</ccp:label></span>
        </ccp:evaluate>
        </span>
        </div>
        <ccp:permission name="project-news-edit,project-news-delete" if="any">
          <div class="permissions">
            <ccp:permission name="project-news-edit">
              <%-- edit message --%>
              <portlet:renderURL var="updateUrl">
                <portlet:param name="portlet-action" value="modify"/>
                <portlet:param name="portlet-object" value="post"/>
                <portlet:param name="portlet-value" value="${blog.id}"/>
              </portlet:renderURL>

	            <a href="${updateUrl}"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.editThisItem">Edit this item</ccp:label>"></a>
              <%-- clone message --%>
              <portlet:actionURL var="cloneUrl">
                <portlet:param name="portlet-value" value="${blog.id}"/>
                <portlet:param name="portlet-command" value="clone"/>
              </portlet:actionURL>
	            <a href="javascript:confirmForward('${cloneUrl}');"><img src="<%= ctx %>/images/icons/stock_copy-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.makeACopyOfThisItem">Make a copy of this item</ccp:label>"></a>
              <%-- archive message --%>
              <%--
              <ccp:evaluate if="<%= blog.getEndDate() == null %>">
	          <div class="permissions">
	                  <a href="javascript:confirmForward('<%= ctx %>/BlogActions.do?command=Archive&pid=<%= project.getId() %>&id=<%= blog.getId() %>');"><img src="<%= ctx %>/images/icons/stock_archive-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.archiveThisItem">Archive this item</ccp:label>"></a>
	          </div>
              </ccp:evaluate>
              --%>
            </ccp:permission>
            <ccp:permission name="project-news-delete">
              <%-- delete message --%>
              <portlet:actionURL var="deleteUrl">
                <portlet:param name="portlet-value" value="${blog.id}"/>
                <portlet:param name="portlet-command" value="delete"/>
              </portlet:actionURL>
	            <a href="javascript:confirmDelete('${deleteUrl}');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterNews.byArticle.deleteThisItem">Delete this item</ccp:label>"></a>
            </ccp:permission>
          </div>
        </ccp:permission>
        <div class="details">
          <div class="projectCenterProfileImage">
            <c:if test="${!empty project.category.logo}">
              <div class="imageContainer">
                <c:choose>
                  <c:when test="${!empty thisUser.profileProject.logo}">
                    <img alt="<c:out value="${thisUser.profileProject.title}"/> photo" src="${ctx}/image/<%= thisUser.getProfileProject().getLogo().getUrlName(50,50) %>" />
                  </c:when>
                  <c:when test="${!empty thisUser.profileProject.category.logo}">
                    <img alt="Default user photo" src="${ctx}/image/<%= thisUser.getProfileProject().getCategory().getLogo().getUrlName(50,50) %>" class="default-photo" />
                  </c:when>
                </c:choose>
              </div>
            </c:if>
          </div>
          <ul>
            <li><span>Posted On:</span> <ccp:tz timestamp="<%= blog.getStartDate() %>" dateFormat="<%= DateFormat.LONG %>" /></li>
            <li><span>Posted By:</span> <ccp:username id="<%= blog.getEnteredBy() %>"/></li>
            <li><ccp:evaluate if="<%= blog.getId() > -1 && User.isLoggedIn() %>">
                <a href="javascript:showPanel('Send author a question','${ctx}/show/${thisUser.profileProject.uniqueId}/app/compose_message?module=blog&id=${blog.id}',700)" title="Send ${thisUser.profileProject.title} a message"><img src="${ctx}/images/icons/mail_pencil.png" alt="Message icon" align="top"> Send ${thisUser.profileProject.title} a message</a>
              </ccp:evaluate>
            </li>
          </ul>
        </div>
      </div>
      <div class="articleBody">
        <%= blog.getIntro() %>
        <ccp:evaluate if="<%= StringUtils.hasText(blog.getMessage()) %>">
          <%= blog.getMessage() %>
        </ccp:evaluate>
      </div>
      <div class="userInputFooter">
        <ccp:evaluate if="<%= blog.getRatingCount() > 0 %>">
          <p>(<%= blog.getRatingValue() %> out of <%= blog.getRatingCount() %> <%= blog.getRatingCount() == 1 ? " person" : " people"%> found this blog post useful.)</p>
        </ccp:evaluate>
        <ccp:evaluate if="<%= blog.getInappropriateCount() > 0 && ProjectUtils.hasAccess(blog.getProjectId(), User, \"project-news-edit\")%>">
          <p>(<%= blog.getInappropriateCount() %><%= blog.getInappropriateCount() == 1? " person" : " people"%> found this blog post inappropriate.)</p>
        </ccp:evaluate>
        <%-- any user who is not the author of the blog can mark the blog as useful  --%>
        <ccp:permission name="project-news-view">
          <ccp:evaluate if="<%= (blog.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
            <p>Is this blog post useful?
              <portlet:renderURL var="ratingUrl" windowState="maximized">
                <portlet:param name="portlet-command" value="setRating"/>
                <portlet:param name="id" value="${blog.id}"/>
                <portlet:param name="v" value="1"/>
                <portlet:param name="out" value="text"/>
              </portlet:renderURL>
              <a href="javascript:copyRequest('${ratingUrl}','<%= "blog_" + blog.getId() %>','message');">Yes</a>&nbsp;
              <portlet:renderURL var="ratingUrl" windowState="maximized">
                <portlet:param name="portlet-command" value="setRating"/>
                <portlet:param name="id" value="${blog.id}"/>
                <portlet:param name="v" value="0"/>
                <portlet:param name="out" value="text"/>
              </portlet:renderURL>
              <a href="javascript:copyRequest('${ratingUrl}','<%= "blog_" + blog.getId() %>','message');">No</a>&nbsp;
              <ccp:evaluate if="<%= blog.getId() > -1 && User.isLoggedIn() %>">
                <a href="javascript:showPanel('Mark this blog post as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=blog&pid=${project.id}&id=${blog.id}',700)">Report this as inappropriate</a>
              </ccp:evaluate>
            </p>
          <div id="blog_<%= blog.getId() %>"></div>
        </ccp:evaluate>
        </ccp:permission>
      </div>
      <div class="portlet-menu">
        <ccp:evaluate if="<%= blog.getId() > -1 && User.isLoggedIn() %>">
          <div class="actions">
            <div class="addComment">
              <ul>
                <li>
                  <%-- AJAX Display Add Comment Panel
                  <a href="javascript:showSpan('thisCommentWindow${blog.id}');window.scrollBy(0,1000);document.getElementById('comment').focus();">Add a comment</a>
                  --%>
                  <a href="#addComment" title="Add a comment to this blog post"><img src="${ctx}/images/icons/balloon_plus.png" alt="Add a comment icon" align="top"> Add a Comment</a>
                </li>
              </ul>
            </div>
          </div>
        </ccp:evaluate>
      </div>
      <a name="comments"></a>
      <div class="userInputContainer">
        <ccp:evaluate if="<%= commentList.size() > 0 %>">
              <h3>Comments</h3> <span class="count">(<%= commentList.size() %>) </span>
              &nbsp;
              <c:if test="${hideBlogDetails eq 'true'}">
                <a href="${ctx}/show/${project.uniqueId}/post/${blog.id}">Show Blog Post</a>
              </c:if>
            <%
              Iterator cl = commentList.iterator();
              int count = 0;
              while (cl.hasNext()) {
                BlogPostComment blogPostComment = (BlogPostComment) cl.next();
                ++count;
            %>
			<c:set var="newsArticleComment" value="<%= blogPostComment %>" />
            <div id="commentContainer${newsArticleComment.id}" class="userInputContainer">
		  	  <a name="${newsArticleComment.id}"></a>
          <div class="userInputBody">
            <p><%= StringUtils.toHtml(blogPostComment.getComment()) %></p>
          </div>
          <div class="userInputDetails">
            <div class="details">
              <h5>Written by <ccp:username id="<%= blogPostComment.getEnteredBy() %>" /></h5>
              <p><ccp:tz timestamp="<%= blogPostComment.getEntered() %>" default="&nbsp;"/></p>
            </div>
          </div>
			    <div class="userInputFooter">
			      <ccp:evaluate if="<%= blogPostComment.getRatingCount() > 0 %>">
			        <p>(<%= blogPostComment.getRatingValue() %> out of <%= blogPostComment.getRatingCount() %> <%= blogPostComment.getRatingCount() == 1 ? " person" : " people"%> found this comment useful.)</p>
			      </ccp:evaluate>
			      <ccp:evaluate if="<%= blogPostComment.getInappropriateCount() > 0 && ProjectUtils.hasAccess(blog.getProjectId(), User, \"project-news-edit\")%>">
			        <p>(<%= blogPostComment.getInappropriateCount() %><%= blogPostComment.getInappropriateCount() == 1? " person" : " people"%> found this comment inappropriate.)</p>
			      </ccp:evaluate>
			        <%-- any user who is not the author of the comment can mark the comment as useful  --%>
			        <p>
			       <ccp:permission name="project-news-view">
			        <ccp:evaluate if="<%= (blogPostComment.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
			          Is this comment useful?
			          <portlet:renderURL var="ratingUrl" windowState="maximized">
			            <portlet:param name="portlet-command" value="comment-setRating"/>
			            <portlet:param name="id" value="${newsArticleComment.id}"/>
			            <portlet:param name="v" value="1"/>
			            <portlet:param name="out" value="text"/>
			          </portlet:renderURL>
			          <a href="javascript:copyRequest('${ratingUrl}','<%= "newsArticleComment_" + blogPostComment.getId() %>','message');">Yes</a>&nbsp;
			          <portlet:renderURL var="ratingUrl" windowState="maximized">
			            <portlet:param name="portlet-command" value="comment-setRating"/>
			            <portlet:param name="id" value="${newsArticleComment.id}"/>
			            <portlet:param name="v" value="0"/>
			            <portlet:param name="out" value="text"/>
			          </portlet:renderURL>
			          <a href="javascript:copyRequest('${ratingUrl}','<%= "newsArticleComment_" + blogPostComment.getId() %>','message');">No</a>&nbsp;
			        </ccp:evaluate>
			        </ccp:permission>
			        <%-- a user with admin access can delete the comment  --%>
			       <c:if test="${User.accessAdmin}">
			          <portlet:renderURL var="deleteCommentUrl" windowState="maximized">
			            <portlet:param name="portlet-command" value="comment-delete"/>
			            <portlet:param name="id" value="${newsArticleComment.id}"/>
			          </portlet:renderURL>
			          <a href="javascript:copyRequest('${deleteCommentUrl}','commentContainer${newsArticleComment.id}','deleteMessage');">Delete</a>&nbsp;
			        </c:if>
 			        <ccp:permission name="project-news-view">
				        <ccp:evaluate if="<%= (blogPostComment.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
			   			  <a href="javascript:showPanel('Mark this comment as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=blogcomment&pid=${project.id}&id=${newsArticleComment.id}',700)">Report this as inappropriate</a>
				        </ccp:evaluate>
			        </ccp:permission>
			 		  </p>
			          <div id="newsArticleComment_<%= blogPostComment.getId() %>"></div>
			    </div>
        </div>
      <%
        }
      %>
    </ccp:evaluate>
  </div>
	<c:choose>
	<c:when test="${(blog.id > -1)  && User.loggedIn }">
      <a name="addComment"></a>
      <div class="formContainer" id="thisCommentWindow${blog.id}">
        <portlet:actionURL var="saveCommentUrl" portletMode="view">
          <portlet:param name="portlet-action" value="show"/>
          <portlet:param name="portlet-object" value="post"/>
          <portlet:param name="portlet-value" value="${blog.id}"/>
          <portlet:param name="portlet-command" value="saveComments"/>
        </portlet:actionURL>
        <form action="${saveCommentUrl}" method="post">
          <fieldset id="thisCommentWindow${blog.id}">
            <legend>Add a suggestion or comment to this blog post  <span class="required">*</span></legend>
            <div class="commentBox">
              <%= showAttribute(request, "commentError") %>
              <textarea id="comment${blog.id}" name="comment" class="height200"></textarea>
              <p class="characterCounter">Comments are reset by an administrator when they no longer apply.</p>
            </div>
            <input type="hidden" name="newsId" value="${blog.id}" />
          </fieldset>
          <input type="submit" name="Save" value="Save" class="submit" />
          <input type="button" value="Cancel" onclick="hideSpan('thisCommentWindow${blog.id}')" class="cancel" />
        </form>
      </div>
      </c:when>
	  <c:otherwise>
	      <div class="formContainer" id="thisCommentWindow${blog.id}">
              <p class="characterCounter">Sign in to add your comment.</p>
		  </div>	
	  </c:otherwise>
	  </c:choose>      
    </c:if>
  </div>
  <ccp:evaluate if="<%= isPopup(request) %>">
  <input type="button" class="cancel" value="<ccp:label name="button.close">Close</ccp:label>" onclick="window.close()"/>
  </ccp:evaluate>
</div>
