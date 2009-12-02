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
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" />
<%--@elvariable id="title" type="java.lang.String"--%>
<%--@elvariable id="showDescription" type="java.lang.String"--%>
<%--@elvariable id="limit" type="java.lang.String"--%>
<%--@elvariable id="rssFeed" type="com.sun.syndication.feed.synd.SyndFeed"--%>
<h3 class="portletHeader"><c:out value="${title}"/></h3>
<ol>
  <c:forEach var="feed" items="${rssFeed.entries}" varStatus="status" end="${limit}">
    <li>
      <c:if test="${!empty feed.title}">
        <h4>
          <c:choose>
            <c:when test="${!empty feed.link}">
              <a target="_blank" href="${feed.link}"><c:out value="${feed.title}"/></a>
            </c:when>
            <c:otherwise>
              <c:out value="${feed.title}"/>
            </c:otherwise>
          </c:choose>
        </h4>
        <c:if test="${!empty feed.publishedDate}">
          <i><ccp:tz date="${feed.publishedDate}" pattern="relative"/></i>
        </c:if>
      </c:if>
      <c:if test="${showDescription eq 'true' && !empty feed.description.value}">
        <p>
          ${feed.description.value}
        </p>
      </c:if>
    </li>
  </c:forEach>
</ol>
<c:if test="${!empty rssFeed.title && !empty rssFeed.link}">
  <p>Provided by <a target="_blank" href="${rssFeed.link}"><c:out value="${rssFeed.title}"/></a></p>
</c:if>
