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
<%@ page import="com.concursive.connect.web.portal.PortalUtils,javax.portlet.*" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<portlet:actionURL var="submitUrl" portletMode="view" />
<div class="formContainer">
  <form method="POST" name="<portlet:namespace/>inputForm" action="${submitUrl}">
    <c:choose>
      <c:when test="${'true' eq isUserProfile}">
        <h3>Friends?</h3>
      </c:when>
      <c:otherwise>
        <h3>Join</h3>
      </c:otherwise>
    </c:choose>
    <p>
      <c:choose>
	    <c:when test="${'true' eq isUserProfile}">
          Become a friend of <strong><c:out value="${project.title}"/></strong> to share information and stay up-to-date.
	    </c:when>
	    <c:otherwise>
          Become a member of <strong><c:out value="${project.title}"/></strong> so that you can participate
          and receive the latest news.
	    </c:otherwise>
      </c:choose>
    </p>
    <c:if test="${canJoin eq 'true'}">
      <input type="submit" class="submit" name="save" value="Join Now" />
    </c:if>
    <c:if test="${canRequestToJoin eq 'true' && !isUserProfile}">
      <input type="submit" class="submit" name="save" value="Ask to Become a Member" />
    </c:if>
    <c:if test="${isUserProfile eq 'true'}">
      <input type="submit" class="submit" name="save" value="Ask to Become a Friend" />
    </c:if>
  </form>
</div>