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
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<h3>Filter By</h3>
  <ul>
  <%--
    <li>
      <a href="${ctx}${pageURL}/${categoryName}/${normalizedTag}/popular" title="Filter search results by most polular"<c:if test="${!empty sortCriteria and sortCriteria eq 'popular'}"> class="active"</c:if>>
        <em>Most Popular</em>
      </a>
    </li>
     --%>
    <li><a href="${ctx}${pageURL}/${categoryName}/${normalizedTag}/highest-rated" title="Filter search results by highest rated"<c:if test="${!empty sortCriteria and sortCriteria eq 'highest-rated'}"> class="active"</c:if>>
      <em>Highest Rated</em>
      </a></li>
    <li><a href="${ctx}${pageURL}/${categoryName}/${normalizedTag}/most-reviewed" title="Filter search results by most reviewed"<c:if test="${!empty sortCriteria and sortCriteria eq 'most-reviewed'}"> class="active"</c:if>>
      <em>Most Reviewed</em>
      </a></li>
    <li><a href="${ctx}${pageURL}/${categoryName}/${normalizedTag}/newly-added" title="Filter search results by newly added"<c:if test="${!empty sortCriteria and sortCriteria eq 'newly-added'}"> class="active"</c:if>>
      <em>Newly Added</em>
      </a></li>
  </ul>
