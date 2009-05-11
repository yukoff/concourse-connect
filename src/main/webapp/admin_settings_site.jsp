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
<jsp:useBean id="siteSettings" class="com.concursive.connect.web.modules.setup.beans.SiteSettingsBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminSettings.do">Configure System Settings</a> >
Site Defaults<br />
<br />
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Site Defaults
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td class="formLabel">File Size Limit</td>
      <td>
        Set the default storage capacity for new users to
        <input type="text" name="accountSize" size="5" value="<%= siteSettings.getAccountSize() %>" /> MB
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">Time Zone</td>
      <td>
        Set the default time zone for new users to
        <input type="text" name="timeZone" />
      </td>
    </tr>
  <%--
    <tr class="containerBody">
      <td class="formLabel">Registration</td>
      <td>
        <input type="checkbox" name="allowRegistration" <ccp:evaluate if="<%= siteSettings.getAllowRegistration() %>">checked</ccp:evaluate>/>
        Allow non-users to register and create an account on this system
      </td>
    </tr>
  --%>
    <tr class="containerBody">
      <td class="formLabel">Projects</td>
      <td>
        <input type="checkbox" name="allowAddProjects" <ccp:evaluate if="<%= siteSettings.getAllowAddProjects() %>">checked</ccp:evaluate>/>
        Allow new users to start projects
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">Invitations</td>
      <td>
        <input type="checkbox" name="allowInvitations" <ccp:evaluate if="<%= siteSettings.getAllowInvitations() %>">checked</ccp:evaluate>/>
        Allow existing users to invite non-users to the system
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">Invitation Subject</td>
      <td>
        <input type="text" name="invitationSubject" size="40" value="<%= toHtmlValue(siteSettings.getInvitationSubject()) %>" />
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel" valign="top">Invitation Message</td>
      <td>
        <textarea rows="8" name="invitationMessage" cols="80"><%= toString(siteSettings.getInvitationMessage()) %></textarea>
      </td>
    </tr>
  </tbody>
</table>
<input type="hidden" name="isLoaded" value="true" />

