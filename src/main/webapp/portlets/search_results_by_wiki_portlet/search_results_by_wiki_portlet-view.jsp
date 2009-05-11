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
<%@ page import="com.concursive.commons.text.StringUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.indexer.IndexerQueryResult" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<portlet:defineObjects/>
<%--@elvariable id="hits" type="com.concursive.connect.indexer.IndexerQueryResultList"--%>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<c:set var="hits" value="${hits}"/>
<jsp:useBean id="hits" type="com.concursive.connect.indexer.IndexerQueryResultList"/>
<c:set var="limit" value="${limit}"/>
<jsp:useBean id="limit" type="java.lang.String"/>
<h3><c:out value="${title}"/></h3>
<div>
<ul>
<%
  for (int i = 0; i < hits.size() && i < Integer.parseInt(limit); i++) {
    IndexerQueryResult document = hits.get(i);
    int projectId = Integer.parseInt(document.get("projectId"));
    Project project = ProjectUtils.loadProject(projectId);
    String wikiSubject = "";
    if (StringUtils.hasText(document.get("subjectLink"))) {
      wikiSubject = "/" + document.get("subjectLink");
    }
%>
    <li>
      <a href="${ctx}/show/<%= project.getUniqueId() %>/wiki<%= wikiSubject %>"><ccp:evaluate if="<%= StringUtils.hasText(document.get(\"title\")) %>"><%= StringUtils.toHtml(document.get("title")) %></ccp:evaluate><ccp:evaluate if="<%= !StringUtils.hasText(document.get(\"title\")) %>"><ccp:project id="<%= document.get(\"projectId\")%>"/></ccp:evaluate></a><br />
      Source: <ccp:project id="<%= document.get(\"projectId\")%>"/> wiki
    </li>
  
<%
  }
%>
</ul>
</div>
