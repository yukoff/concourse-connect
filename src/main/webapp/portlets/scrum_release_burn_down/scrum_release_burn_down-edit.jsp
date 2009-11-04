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
<portlet:actionURL var="processAction" portletMode="view" />
<form method="post" name="inputForm" action="<%= pageContext.getAttribute("processAction") %>">
  Start Date:
  <input type="text" name="startDate" id="startDate" size="10" value="<ccp:tz timestamp="${startDate}" dateOnly="true"/>">
  <a href="javascript:popCalendar('inputForm', 'startDate', '${user.locale.language}', '${user.locale.country}');"><img src="${ctx}/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a><br />
  <input type="text" name="daysToShow" value="<c:out value="${daysToShow}" />" size="5" />days to work on release<br />
  <input type="text" name="estimatedHours" value="<c:out value="${estimatedHours}" />" size="5" />hours this release will take<br />
  <input type="text" name="workHours" value="<c:out value="${workHours}" />" size="5" />hours worked on per day<br />
  <br />
  Choose a list for data...<br />
  <input type="radio" name="categoryId" value="-1" <c:if test="${categoryId == -1}">checked</c:if> /> Any list<br />
  <c:forEach items="${categoryList}" var="thisCategory">
    <input type="radio" name="categoryId" value="${thisCategory.id}" <c:if test="${categoryId == thisCategory.id}">checked</c:if> /> <c:out value="${thisCategory.description}" /><br />
  </c:forEach>
  <br />
  Choose a release for charting...<br />
  <c:forEach items="${releaseList}" var="thisRelease">
    <input type="radio" name="releaseId" value="${thisRelease.id}" <c:if test="${releaseId == thisRelease.id}">checked</c:if>/> <c:out value="${thisRelease.name}" /><br />
  </c:forEach>
  <br />
  Chart properties...<br />
  <input type="text" name="chartHeight" value="<c:out value="${chartHeight}" />" size="5" />pixels<br />
  <br />
  <input type="submit" value="Save" />
</form>
