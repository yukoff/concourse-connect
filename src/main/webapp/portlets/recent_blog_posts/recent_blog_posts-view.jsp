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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="blogPostList" class="com.concursive.connect.web.modules.blog.dao.BlogPostList" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
  <h3><c:out value="${title}"/></h3>
  <c:if test="${!empty blogPostList}">
  <ol>
    <c:forEach items="${blogPostList}" var="blogPost">
      <c:set var="blogPost" value="${blogPost}" />
      <jsp:useBean id="blogPost" type="com.concursive.connect.web.modules.blog.dao.BlogPost" />
      <li>
        <c:choose>
          <c:when test="${!empty blogPost.user.profileProject.logo}">
            <img alt="<c:out value="${blogPost.project.title}"/> photo" src="${ctx}/image/<%= blogPost.getUser().getProfileProject().getLogo().getUrlName(45,45) %>" />
          </c:when>
          <c:otherwise>
            <c:if test="${!empty blogPost.user.profileProject.category.logo}">
              <img alt="Default user photo" src="${ctx}/image/<%= blogPost.getUser().getProfileProject().getCategory().getLogo().getUrlName(45,45) %>" class="default-photo" />
            </c:if>
          </c:otherwise>
        </c:choose>
        <%-- Taken out 4/6/09
             TODO: Impliment mouseover on user profile image
        <cite><ccp:username id="${blogPost.enteredBy}" /></cite>
         --%>
        <c:choose>
          <c:when test="${showBlogLink eq 'true'}">
            <h4><a href="${ctx}/show/${blogPost.project.uniqueId}/post/${blogPost.id}" title="<c:out value="${blogPost.subject}" />"><c:out value="${blogPost.subject}"/></a></h4>
          </c:when>
          <c:otherwise>
            <h4><c:out value="${blogPost.subject}"/></h4>
          </c:otherwise>
        </c:choose>
        <c:if test="${showProjectTitle eq 'true'}">
            <p><c:out value="${blogPost.project.title}"/></p>
        </c:if>
      </li>
    </c:forEach>
  </ol>
</c:if>
<c:if test="${empty blogPostList}">
  <p>There are no blog posts</p>
</c:if>

