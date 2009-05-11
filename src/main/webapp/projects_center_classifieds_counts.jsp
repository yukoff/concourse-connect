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
<%@ page import="com.concursive.connect.web.modules.login.utils.UserUtils" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="counter" class="com.concursive.connect.web.modules.classifieds.utils.ClassifiedsCounter" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategoryList" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
  <h3>Summary</h3>
  <%-- Categories --%>
  <ccp:evaluate if="<%= counter.getCategories().size() > 0 %>">
    <div class="box140top">
      <div class="box140bottom">
        <div class="boxHeader">
          Categories
        </div>
        <div class="boxContent">
          <ul>
            <ccp:evaluate if="<%= counter.getCategories().size() > 1 %>">
              <portlet:renderURL var="filterUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="classifieds"/>
              </portlet:renderURL>
              <li><a href="${filterUrl}">All categories</a> (<%=counter.getCategories().getTotal()%>)</li>
            </ccp:evaluate>
            <%
              for (CounterPair thisItem : counter.getCategories().getSortedPairs()) {
                request.setAttribute("thisItem", thisItem);
            %>
              <portlet:renderURL var="filterUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="classifieds"/>
                <portlet:param name="portlet-value" value="category"/>
                <portlet:param name="portlet-params" value="${thisItem.name}"/>
              </portlet:renderURL>
              <li><a href="${filterUrl}"><%=toHtml(categoryList.getValueFromId(thisItem.getName())) %></a> (<%=thisItem.getValue()%>)</li>
            <%
              }
            %>
          </ul>
        </div>
      </div>
    </div>
  </ccp:evaluate>

  <%-- Dates --%>
  <ccp:evaluate if="<%= counter.getDates().size() > 0 %>">
    <div class="box140top">
      <div class="box140bottom">
        <div class="boxHeader">
          Dates Published
        </div>
        <div class="boxContent">
          <ul>
            <ccp:evaluate if="<%= counter.getDates().size() > 1 %>">
              <portlet:renderURL var="filterUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="classifieds"/>
              </portlet:renderURL>
              <li><a href="${filterUrl}">All dates</a> (<%=counter.getDates().getTotal()%>)</li>
            </ccp:evaluate>
            <%
              for (CounterPair thisItem : counter.getDates().getSortedPairs()) {
                request.setAttribute("thisItem", thisItem);
            %>
              <portlet:renderURL var="filterUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="classifieds"/>
                <portlet:param name="portlet-value" value="date"/>
                <portlet:param name="portlet-params" value="${thisItem.name}"/>
              </portlet:renderURL>
              <li><a href="${filterUrl}"><ccp:tz timestamp="<%= DatabaseUtils.parseTimestamp(thisItem.getName()) %>" pattern="MMMM yyyy" default="Unpublished" /></a> (<%=thisItem.getValue()%>)</li>
            <%
              }
            %>
            <%--
            <li><a href="#">Past 6 Months (7)</a></li>
            <li><a href="#">April 2008 (1)</a></li>
            <li><a href="#">March 2008 (3)</a></li>
            <li><a href="#">January 2008 (3)</a></li>
            <li><a href="#">July 2007 (7)</a></li>
            <li><a href="#">June 2007</a></li>
            <li><a href="#">May 2007</a></li>
            <li><a href="#">April 2007 (3)</a></li>
            <li><a href="#">Archive</a> (3)</li>
            --%>
          </ul>
        </div>
      </div>
    </div>
  </ccp:evaluate>

  <%-- Authors --%>
  <ccp:evaluate if="<%= counter.getAuthors().size() > 0 %>">
    <div class="box140top">
      <div class="box140bottom">
        <div class="boxHeader">
          Authors
        </div>
        <div class="boxContent">
          <ul>
            <ccp:evaluate if="<%= counter.getAuthors().size() > 1 %>">
              <portlet:renderURL var="filterUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="classifieds"/>
              </portlet:renderURL>
              <li><a href="${filterUrl}">All authors</a> (<%=counter.getAuthors().getTotal()%>)</li>
            </ccp:evaluate>
<%
            for (CounterPair thisItem : counter.getAuthors().getSortedPairs()) {
              request.setAttribute("thisItem", thisItem);
          %>
            <portlet:renderURL var="filterUrl">
              <portlet:param name="portlet-action" value="show"/>
              <portlet:param name="portlet-object" value="classifieds"/>
              <portlet:param name="portlet-value" value="author"/>
              <portlet:param name="portlet-params" value="${thisItem.name}"/>
            </portlet:renderURL>
            <li><a href="${filterUrl}"><%= toHtml(UserUtils.getUserName(Integer.parseInt(thisItem.getName()))) %></a> (<%= thisItem.getValue()%>)</li>
<%
}
%>
          </ul>
        </div>
      </div>
    </div>
  </ccp:evaluate>
