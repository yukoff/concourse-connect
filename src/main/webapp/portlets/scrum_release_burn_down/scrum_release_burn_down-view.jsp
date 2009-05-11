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
<%@ page import="java.util.Iterator" %>
<%@ page import="com.concursive.connect.cms.portal.dao.ProjectItem" %>
<%@ page import="com.concursive.connect.web.modules.lists.dao.TaskList" %>
<%@ page import="com.concursive.connect.web.modules.lists.dao.Task" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<portlet:defineObjects/>
<jsp:useBean id="release" class="com.concursive.connect.cms.portal.dao.ProjectItem" scope="request"/>
<jsp:useBean id="graphMap" class="java.util.LinkedHashMap" scope="request"/>
<jsp:useBean id="graphMap2" class="java.util.LinkedHashMap" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<div id="<portlet:namespace/>chartContainer" style="width:100%; height:${chartHeight}px">Chart</div>
<script type="text/javascript">
var <portlet:namespace/>graphData = [
<%
  Iterator i = graphMap.keySet().iterator();
  while (i.hasNext()) {
    java.util.Date thisDate = (java.util.Date) i.next();
    Double thisValue = (Double) graphMap.get(thisDate);
    Double thisValue2 = (Double) graphMap2.get(thisDate);

%>{ month: "<ccp:tz timestamp="<%= new Timestamp(thisDate.getTime()) %>" dateOnly="true"/>",
    remaining: <%= thisValue %>,
    ideal: <%= thisValue2 %>}<%= i.hasNext() ? "," : "" %>
<%
  }
%>
];
var <portlet:namespace/>myDataSource = new YAHOO.util.DataSource(<portlet:namespace/>graphData);
<portlet:namespace/>myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
<portlet:namespace/>myDataSource.responseSchema =
	{
	    fields: [ "month", "remaining", "ideal" ]
	};

var <portlet:namespace/>seriesDef = [
  { displayName: "Where team is now", yField: "remaining" },
  { displayName: "Steady pace", yField: "ideal" }
];

var <portlet:namespace/>myChart = new YAHOO.widget.LineChart(
    "<portlet:namespace/>chartContainer",
    <portlet:namespace/>myDataSource,
{
    xField: "month",
    series: <portlet:namespace/>seriesDef
});

</script>
