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
<%@ page import="javax.portlet.PortletSession" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<div class="yui-skin-sam">
<div id="container<portlet:namespace/>"><h2>My Projects</h2></div>
<script type="text/javascript">
  var tabView<portlet:namespace/> = new YAHOO.widget.TabView();
<%
  String activeTab = null;
  PortletSession ps = renderRequest.getPortletSession(false);
  if (ps != null) {
    activeTab = (String) renderRequest.getPortletSession().getAttribute("projectListSession");
  }
  if (activeTab == null) {
    activeTab = "top_rated";
  }
%>
  <%-- Favorites --%>
  <portlet:renderURL var="urlTopRated" portletMode="view" windowState="maximized">
    <portlet:param name="tab" value="top_rated"/>
  </portlet:renderURL>
  tabView<portlet:namespace/>.addTab( new YAHOO.widget.Tab({
	    label: 'Top Rated',
	    dataSrc: '<%= pageContext.getAttribute("urlTopRated") %>&out=text',
	    cacheData: false
    <ccp:evaluate if="<%= \"top_rated\".equals(activeTab) %>">, active: true</ccp:evaluate>
  }));

  <%-- Recent --%>
  <portlet:renderURL var="urlRecent" portletMode="view" windowState="maximized">
    <portlet:param name="tab" value="recent"/>
  </portlet:renderURL>
  tabView<portlet:namespace/>.addTab( new YAHOO.widget.Tab({
	    label: 'Recent',
	    dataSrc: '<%= pageContext.getAttribute("urlRecent") %>&out=text',
	    cacheData: false
	    <ccp:evaluate if="<%= \"recent\".equals(activeTab) %>">, active: true</ccp:evaluate>
	}));

  <%-- New --%>
  <portlet:renderURL var="urlNew" portletMode="view" windowState="maximized">
    <portlet:param name="tab" value="new"/>
  </portlet:renderURL>
  tabView<portlet:namespace/>.addTab( new YAHOO.widget.Tab({
	    label: 'New',
	    dataSrc: '<%= pageContext.getAttribute("urlNew") %>&out=text',
	    cacheData: false
      <ccp:evaluate if="<%= \"new\".equals(activeTab) %>">, active: true</ccp:evaluate>
	}));

  <%-- Closed --%>
  <portlet:renderURL var="urlClosed" portletMode="view" windowState="maximized">
    <portlet:param name="tab" value="closed"/>
  </portlet:renderURL>
  tabView<portlet:namespace/>.addTab( new YAHOO.widget.Tab({
	    label: 'Closed',
	    dataSrc: '<%= pageContext.getAttribute("urlClosed") %>&out=text',
	    cacheData: true
	    <ccp:evaluate if="<%= \"closed\".equals(activeTab) %>">, active: true</ccp:evaluate>
	}));

  <%-- All --%>
  <portlet:renderURL var="urlAll" portletMode="view" windowState="maximized">
    <portlet:param name="tab" value="all"/>
  </portlet:renderURL>
  tabView<portlet:namespace/>.addTab( new YAHOO.widget.Tab({
	    label: 'All',
	    dataSrc: '<%= pageContext.getAttribute("urlAll") %>&out=text',
	    cacheData: true
      <ccp:evaluate if="<%= \"all\".equals(activeTab) %>">, active: true</ccp:evaluate>
  }));
  
  tabView<portlet:namespace/>.appendTo('container<portlet:namespace/>');
</script>
</div>
<a href="${ctx}/ProjectManagement.do?command=ProjectList">All Projects</a>
