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
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
  <h3><c:out value="${title}"/></h3>
  <c:if test="${!empty topContributorList}">
  <ol>
    <c:forEach items="${topContributorList}" var="topContributor">
      <c:set var="topContributor" value="${topContributor}" />
      <jsp:useBean id="topContributor" type="com.concursive.connect.web.modules.login.dao.User" />
      <li>
        <c:choose>
          <c:when test="${!empty topContributor.profileProject.logo}">
            <img alt="<c:out value="${topContributor.profileProject.title}"/> photo"
                 src="${ctx}/image/<%= topContributor.getProfileProject().getLogo().getUrlName(45,45) %>"/>
          </c:when>
          <c:when test="${!empty topContributor.profileProject.category.logo}">
            <img alt="Default profile photo"
                 src="${ctx}/image/<%= topContributor.getProfileProject().getCategory().getLogo().getUrlName(45,45) %>"  class="default-photo" />
          </c:when>
        </c:choose>
        <h4><ccp:username id="${topContributor.id}"/></h4>
        <p><c:out value="${topContributor.company}" /></p>
      </li>
    </c:forEach>
  </ol>
</c:if>
<c:if test="${empty topContributorList}">
  <p>Currently there are no users that meet the criteria</p>
</c:if>
