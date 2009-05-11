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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="wiki" class="com.concursive.connect.web.modules.wiki.dao.Wiki" scope="request"/>
<jsp:useBean id="versionList" class="com.concursive.connect.web.modules.wiki.dao.WikiVersionList" scope="request"/>
<%@ include file="initPage.jsp" %>
<div class="portletWrapper">
  <h2><ccp:label name="projectsCenterWiki.versionList.versions">Versions</ccp:label></h2>
  <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
    <p class="back"><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/wiki<ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">/<%= wiki.getSubjectLink() %></ccp:evaluate>"><%= toHtml(wiki.getSubject()) %></a></p>
  </ccp:evaluate>
  <p class="back"><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/wiki"><ccp:label name="projectsCenterWiki.versionList.home">Back to Wiki</ccp:label></a></p>
  <div class="wikiBodyContainer">
    <div class="wikiHeader">
      <ccp:evaluate if="<%= hasText(wiki.getSubject()) %>">
        <%= toHtml(wiki.getSubject()) %>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !hasText(wiki.getSubject()) %>">
        <%= toHtml(project.getTitle()) %>
      </ccp:evaluate>
    </div>
    <div class="wikiBody">
    <table class="pagedList">
      <thead>
        <tr>
          <th><ccp:label name="projectsCenterWiki.versionList.date">Date</ccp:label></th>
          <th><ccp:label name="projectsCenterWiki.versionList.modifiedBy">Modified By</ccp:label></th>
          <th><ccp:label name="projectsCenterWiki.versionList.totalLines">Total Lines</ccp:label></th>
          <th><ccp:label name="projectsCenterWiki.versionList.linesAdded">Lines Added</ccp:label></th>
          <th><ccp:label name="projectsCenterWiki.versionList.linesChanged">Lines Changed</ccp:label></th>
          <th><ccp:label name="projectsCenterWiki.versionList.linesDeleted">Lines Deleted</ccp:label></th>
          <th><ccp:label name="projectsCenterWiki.versionList.contentSize">Content Size</ccp:label></th>
        </tr>
      </thead>
      <tbody>
        <%
          int rowid = 0;
          Iterator i = versionList.iterator();
          while (i.hasNext()) {
            rowid = (rowid != 1?1:2);
            WikiVersion thisVersion = (WikiVersion) i.next();
        %>
              <tr class="row<%= rowid %>">
                <td align="center" valign="top" nowrap>
                  <ccp:tz timestamp="<%= thisVersion.getEntered() %>"/>
                </td>
                <td align="center" valign="top" nowrap>
                  <ccp:username id="<%= thisVersion.getEnteredBy() %>"/>
                </td>
                <td align="center" valign="top" nowrap>
                  <%= thisVersion.getLinesTotal() %>
                </td>
                <td align="center" valign="top" nowrap>
                  <%= thisVersion.getLinesAdded() %>
                </td>
                <td align="center" valign="top" nowrap>
                  <%= thisVersion.getLinesChanged() %>
                </td>
                <td align="center" valign="top" nowrap>
                  <%= thisVersion.getLinesDeleted() %>
                </td>
                <td align="center" valign="top" nowrap>
                  <%= thisVersion.getSize() %>
                </td>
              </tr>
        <%
          }
        %>
      </tbody>
    </table>
  </div>
</div>

