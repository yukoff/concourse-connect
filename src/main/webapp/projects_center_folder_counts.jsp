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
<%@ page import="com.concursive.commons.db.DatabaseUtils" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItemCounter" class="com.concursive.connect.web.modules.documents.utils.FileItemCounter" scope="request"/>
<jsp:useBean id="fileFolderList" class="com.concursive.connect.web.modules.documents.dao.FileFolderList" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
  <h3>Folders</h3>
  <%-- Folders --%>
  <div class="box140top">
    <div class="box140bottom">
      <div class="boxContent">
        <ul>
          <c:choose>
            <c:when test="${!empty namespace}">
              <c:set var="homeUrl" value="javascript:goToFolder${namespace}(-1);"/>
            </c:when>
            <c:otherwise>
              <portlet:renderURL var="homeUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="documents"/>
                <c:if test="${!empty param.view}">
                  <portlet:param name="view" value="${param.view}"/>
                </c:if>
              </portlet:renderURL>
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${empty currentFolder || currentFolder.id == -1}">
              <c:set var="active"> class="selected"</c:set>
            </c:when>
            <c:otherwise>
              <c:set var="active" value=""/>
            </c:otherwise>
          </c:choose>
          <li ${active}><a href="${homeUrl}">All</a>
            <c:if test="${fileItemCounter.total > 0}">
              (${fileItemCounter.total})
            </c:if>
          </li>
          <c:forEach items="${fileFolderList}" var="thisFolder">
            <c:set var="thisFolder" value="${thisFolder}" scope="request"/>
            <c:choose>
              <c:when test="${!empty currentFolder && currentFolder.id == thisFolder.id}">
                <c:set var="active"> class="selected"</c:set>
              </c:when>
              <c:otherwise>
                <c:set var="active" value=""/>
              </c:otherwise>
            </c:choose>
            <jsp:useBean id="thisFolder" class="com.concursive.connect.web.modules.documents.dao.FileFolder" scope="request"/>
            <c:choose>
              <c:when test="${!empty namespace}">
                <c:set var="filterUrl" value="javascript:goToFolder${namespace}(${thisFolder.id});"/>
              </c:when>
              <c:otherwise>
                <portlet:renderURL var="filterUrl">
                  <portlet:param name="portlet-action" value="show"/>
                  <portlet:param name="portlet-object" value="folder"/>
                  <portlet:param name="portlet-value" value="${thisFolder.id}"/>
                  <c:if test="${!empty param.view}">
                    <portlet:param name="view" value="${param.view}"/>
                  </c:if>
                </portlet:renderURL>
              </c:otherwise>
            </c:choose>
            <li ${active}><a href="${filterUrl}"><%= toHtml(thisFolder.getSubject()) %></a>
            <c:set var="thisFolderCount">
              <%= fileItemCounter.getFolders().get(String.valueOf(thisFolder.getId())) %>
            </c:set>
            <c:if test="${!empty thisFolderCount && thisFolderCount ne 'null'}">
              (<c:out value="${thisFolderCount}"/>)
            </c:if>
            </li>
          </c:forEach>
        </ul>
      </div>
    </div>
  </div>
  <%-- Dates --%>
<c:if test="${empty namespace}">
  <ccp:evaluate if="<%= fileItemCounter.getDates().size() > 0 %>">
    <div class="box140top">
      <div class="box140bottom">
        <div class="boxHeader">
          Dates
        </div>
        <div class="boxContent">
          <ul>
            <ccp:evaluate if="<%= fileItemCounter.getDates().size() > 1 %>">
              <portlet:renderURL var="filterUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="documents"/>
                <c:if test="${!empty param.view}">
                  <portlet:param name="view" value="${param.view}"/>
                </c:if>
              </portlet:renderURL>
              <li><a href="${filterUrl}">All</a> (<%=fileItemCounter.getDates().getTotal()%>)</li>
            </ccp:evaluate>
            <%
              for (CounterPair thisItem : fileItemCounter.getDates().getSortedPairs()) {
                request.setAttribute("thisItem", thisItem);
            %>
              <c:choose>
                <c:when test="${!empty namespace}">
                  <c:set var="filterUrl" value="javascript:goToFolderDate${namespace}('${thisItem.name}');"/>
                </c:when>
                <c:otherwise>
                  <portlet:renderURL var="filterUrl">
                    <portlet:param name="portlet-action" value="show"/>
                    <portlet:param name="portlet-object" value="documents"/>
                    <portlet:param name="portlet-value" value="date"/>
                    <portlet:param name="portlet-params" value="${thisItem.name}"/>
                    <c:if test="${!empty param.view}">
                      <portlet:param name="view" value="${param.view}"/>
                    </c:if>
                  </portlet:renderURL>
                </c:otherwise>
              </c:choose>
              <li><a href="${filterUrl}"><ccp:tz timestamp="<%= DatabaseUtils.parseTimestamp(thisItem.getName()) %>" pattern="MMMM yyyy" default="Draft" /></a> (<%=thisItem.getValue()%>)</li>
            <%
              }
            %>
          </ul>
        </div>
      </div>
    </div>
  </ccp:evaluate>
</c:if>
