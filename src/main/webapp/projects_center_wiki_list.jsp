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
<%@ page import="com.concursive.commons.text.StringUtils"%>
<%@ page import="com.concursive.connect.web.modules.wiki.utils.WikiUtils"%>
<%@ page import="com.concursive.connect.web.modules.wiki.dao.WikiVersion"%>
<%@ page import="com.concursive.connect.web.modules.wiki.dao.Wiki"%>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="wikiList" class="com.concursive.connect.web.modules.wiki.dao.WikiList" scope="request"/>
<portlet:defineObjects/>
<%@ include file="initPage.jsp" %>
<div class="portletWrapper">
  <h2>Index</h2>
  <portlet:renderURL var="homeUrl" portletMode="view">
    <portlet:param name="portlet-action" value="show"/>
    <portlet:param name="portlet-object" value="wiki"/>
  </portlet:renderURL>
  <p class="back"><a href="${homeUrl}">Back to Home</a></p>
  <div class="wikiBodyContainer">
    <table class="pagedList">
      <thead>
        <tr>
          <th><ccp:label name="projectsCenterWiki.list.name">Name</ccp:label></th>
          <th>Views</th>
          <th><ccp:label name="projectsCenterWiki.list.modified">Modified</ccp:label></th>
          <th><ccp:label name="projectsCenterWiki.list.modifiedBy">Modified By</ccp:label></th>
        </tr>
      </thead>
      <tbody>
        <%
          int rowid = 0;
          Iterator i = wikiList.iterator();
          while (i.hasNext()) {
            rowid = (rowid != 1?1:2);
            Wiki wiki = (Wiki) i.next();
            request.setAttribute("wiki", wiki);
        %>
              <tr class="row<%= rowid %>">
                <td valign="top" width="100%">
                  <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
                    <portlet:renderURL var="wikiUrl" portletMode="view">
                      <portlet:param name="portlet-action" value="show"/>
                      <portlet:param name="portlet-object" value="wiki"/>
                      <portlet:param name="portlet-value" value="${wiki.subjectLink}"/>
                    </portlet:renderURL>
                    <a href="${wikiUrl}"><%= toHtml(wiki.getSubject()) %></a>
                  </ccp:evaluate>
                  <ccp:evaluate if="<%= !hasText(wiki.getSubject()) %>">
                    <portlet:renderURL var="wikiUrl" portletMode="view">
                      <portlet:param name="portlet-action" value="show"/>
                      <portlet:param name="portlet-object" value="wiki"/>
                    </portlet:renderURL>
                    <a href="${wikiUrl}"><%= toHtml(project.getTitle()) %></a>
                  </ccp:evaluate>
                </td>
                <td>
                  <%= ((wiki.getReadCount()==0)?"&nbsp;":""+wiki.getReadCount()) %>
                </td>
                <td align="center" valign="top" nowrap>
                  <ccp:tz timestamp="<%= wiki.getModified() %>"/>
                </td>
                <td align="center" valign="top" nowrap>
                  <ccp:username id="<%= wiki.getModifiedBy() %>"/>
                </td>
              </tr>
        <%
          }
        %>
      </tbody>
    </table>
  </div>
</div>
