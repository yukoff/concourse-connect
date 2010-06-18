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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<c:set var="user" value="<%= User %>" scope="request" />
<%@ include file="initPage.jsp" %>
All RSS (Really Simple Syndication) feeds from <c:out value="${requestMainProfile.title}"/> are based on the RSS 2.0 specification.
RSS is a standard for syndicating frequently updated content from a site via a newsreader.<br />
<br />
<table class="pagedList">
  <thead>
    <tr>
      <th>
        Available RSS Feeds
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="row1">
      <td>
        <img src="<%= ctx %>/images/xml.gif" align="absMiddle" />
        <a href="http://<%= getServerUrl(request) %>/feed/rss.xml">Main Site RSS Feed</a>
      </td>
    </tr>
    <c:forEach items="${tabCategoryList}" var="tabCategory" varStatus="status">
      <c:if test="${!tabCategory.sensitive || (tabCategory.sensitive && user.loggedIn)}">
        <tr class="row1">
          <td>
            <img src="<%= ctx %>/images/xml.gif" align="absMiddle" />
            <a href="http://<%= getServerUrl(request) %>/feed/${fn:toLowerCase(fn:replace(tabCategory.description," ","_"))}/rss.xml"><c:out value="${tabCategory.description}"/> Feed</a>
          </td>
        </tr>
      </c:if>
    </c:forEach>
    <tr class="row1">
      <td>
        <img src="<%= ctx %>/images/xml.gif" align="absMiddle" />
        <a href="http://<%= getServerUrl(request) %>/feed/blog.xml">Recent Blog Posts</a> (Personalized - Asks for user login)
      </td>
    </tr>
    <tr class="row1">
      <td>
        <img src="<%= ctx %>/images/xml.gif" align="absMiddle" />
        <a href="http://<%= getServerUrl(request) %>/feed/discussion.xml">Recent Discussion Posts</a> (Personalized - Asks for user login)
      </td>
    </tr>
    <tr class="row1">
      <td>
        <img src="<%= ctx %>/images/xml.gif" align="absMiddle" />
        <a href="http://<%= getServerUrl(request) %>/feed/documents.xml">Recent Documents</a> (Personalized - Asks for user login)
      </td>
    </tr>
    <tr class="row1">
      <td>
        <img src="<%= ctx %>/images/xml.gif" align="absMiddle" />
        <a href="http://<%= getServerUrl(request) %>/feed/wiki.xml">Recent Wiki Entries</a> (Personalized - Asks for user login)
      </td>
    </tr>
  </tbody>
</table>
