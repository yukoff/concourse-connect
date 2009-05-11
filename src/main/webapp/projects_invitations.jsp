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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="invitedProjectList" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<%@ include file="initPage.jsp" %>
<ccp:evaluate if="<%= invitedProjectList.size() > 0 %>">
<table class="note" cellspacing="0">
<tr>
  <th>
    <img src="<%= ctx %>/images/icons/stock_form-open-in-design-mode-16.gif" border="0" align="absmiddle" />
  </th>
  <td>
    <ccp:label name="projectsInvitatins.description">
    You have been invited to participate in the following items and have been requested to
    either <b>ACCEPT</b> or <b>REJECT</b> each one.<br />
    Accepting will simply allow you to access the item with additional capabilities.
    </ccp:label>
  </td>
</tr>
</table>
<table border="0" width="100%" cellspacing="0" cellpadding="4">
<%
  Iterator vi = invitedProjectList.iterator();
  String viHighlight1 = "#000000";
  String viHighlight2 = "#0000FF";
  String viHighlight = viHighlight2;
  while (vi.hasNext()) {
    if (viHighlight.equals(viHighlight1)) {
      viHighlight = viHighlight2;
    } else {
      viHighlight = viHighlight1;
    }
    Project thisProject = (Project) vi.next();
%>
<tr class="newline">
  <td nowrap valign="top">
    <a href="<%= ctx %>/accept/<%= thisProject.getUniqueId() %>"><img alt="accept item" src="<%= ctx %>/images/buttons/accept.gif" border="0" align="absmiddle" /></a>
    <a href="<%= ctx %>/reject/<%= thisProject.getUniqueId() %>"><img alt="reject item" src="<%= ctx %>/images/buttons/reject.gif" border="0" align="absmiddle" /></a>
  </td>
  <td valign="top" width="100%">
    <b><%= toHtml(thisProject.getTitle()) %></b>,
    created by <ccp:username id="<%= thisProject.getEnteredBy() %>"/>
    on <ccp:tz timestamp="<%= thisProject.getEntered() %>" default="&nbsp;"/><br />
    <%= toHtml(thisProject.getShortDescription()) %>
  </td>
</tr>
<%
  }
%>
</table>
</ccp:evaluate>
<ccp:evaluate if="<%= invitedProjectList.size() == 0 %>">
<p><ccp:label name="projectsInvitations.noInvitations">There are currently no invitations awaiting your reply.</ccp:label></p>
</ccp:evaluate>
<%--
  <p><a href="<%= ctx %>/browse">Browse items</a> that you participate in.</p>
--%>
