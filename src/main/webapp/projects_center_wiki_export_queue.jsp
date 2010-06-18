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
<%@ page import="com.concursive.connect.web.modules.wiki.beans.WikiExportBean" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.concursive.connect.cache.utils.CacheUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="queueValue" class="java.lang.String" scope="request" />
<jsp:useBean id="queueTotal" class="java.lang.String" scope="request" />
<jsp:useBean id="exportsAvailableToUser" class="java.util.ArrayList" scope="request" />
<portlet:defineObjects/>
<%@ include file="initPage.jsp" %>
<div class="profile-portlet-header">
  <h1>Export Queue</h1>
</div>
<div class="profile-portlet-body">
  <ccp:evaluate if="<%= !\"0\".equals(queueValue) %>">
    <div class="wikiHeader">
      <p>The requested wiki is being exported... please refresh this window until complete.</p>
      <p>Your request is <%= queueValue %> of <%= queueTotal %>.</p>
    </div>
  </ccp:evaluate>
  <ccp:evaluate if="<%= exportsAvailableToUser.size() > 0 %>">
    <div class="wikiHeader">
      <p>The following requested Wiki documents are available for download...</p>
    </div>
    <div class="portlet-section">
      <ol>
<%
int count = 0;
for (Object anAvailableToUser : exportsAvailableToUser) {
  ++count;
  WikiExportBean thisBean = (WikiExportBean) anAvailableToUser;
  request.setAttribute("thisBean", thisBean);
%>
        <li>
          <div class="portlet-section-body">
            <portlet:renderURL var="streamUrl">
              <portlet:param name="portlet-action" value="stream"/>
              <portlet:param name="portlet-value" value="${thisBean.exportedFile.name}"/>
            </portlet:renderURL>
            <portlet:renderURL var="downloadUrl">
              <portlet:param name="portlet-action" value="download"/>
              <portlet:param name="portlet-value" value="${thisBean.exportedFile.name}"/>
            </portlet:renderURL>
            <h2><ccp:project id="<%= thisBean.getProjectId()%>" /></h2>
            <p><%= toHtml(thisBean.getDisplaySubject()) %></p>
            <p>Title Page: <%= thisBean.getIncludeTitle() %></p>
            <p>All Linked Pages: <%= thisBean.getFollowLinks() %></p>
          </div>
          <div class="portlet-section-menu">
            <div class="portlet-section-body-menu">
              <p>
                <img alt="Download icon" src="${ctx}/images/icons/get_32x32.png"/>
                <a href="${downloadUrl}">download</a>
                <%= thisBean.getExportedFile().length() %> bytes</p>
              <ul>
                <li><img alt="Zoom icon" src="${ctx}/images/icons/magnifier_search.png"/><a href="javascript:popURL('${streamUrl}',700,580,1,1);">View</a></li>
              </ul>
            </div>
          </div>
        </li>
<%
      }
%>
      </ol>
    </div>
  </ccp:evaluate>
  <ccp:evaluate if="<%= \"0\".equals(queueValue) && exportsAvailableToUser.size() == 0 %>">
    <div class="wikiHeader">
      <p>You currently do not have any exports pending.</p>
    </div>
  </ccp:evaluate>
  <portlet:renderURL var="refreshUrl">
    <portlet:param name="portlet-action" value="show"/>
    <portlet:param name="portlet-object" value="wiki-exports"/>
    <c:if test="${'true' eq param.popup || 'true' eq popup}">
      <portlet:param name="popup" value="true"/>
    </c:if>
  </portlet:renderURL>
  <input type="button" class="submit" name="Refresh" value="Refresh" onClick="window.location.href='${refreshUrl}'" />
  <c:if test="${'true' eq param.popup || 'true' eq popup}">
    <input class="cancel" type="button" value="<ccp:label name="button.close">Close</ccp:label>" onclick="window.close()"/>
  </c:if>
</div>
