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
<%@ page import="com.concursive.commons.files.FileUtils" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="thisUser" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<jsp:useBean id="userProjectCount" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Temp. fix for Weblogic --%>
<% 
boolean allowInvite = "true".equals(applicationPrefs.get("INVITE"));
String detailsUrl = ctx + "/AdminUserDetails.do?command=Details&id=" + thisUser.getId();
String projectsUrl = ctx + "/AdminUserDetails.do?command=Projects&id=" + thisUser.getId();
String loginsUrl = ctx + "/AdminUserDetails.do?command=Logins&id=" + thisUser.getId();
String languagesUrl = ctx + "/AdminUserDetails.do?command=Languages&id=" + thisUser.getId();
String webSitesUrl = ctx + "/AdminUserDetails.do?command=WebSites&id=" + thisUser.getId();
%>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminUsers.do">Manage Users</a> >
<a href="<%= ctx %>/AdminUsers.do?command=Search">Search Results</a> >
User Details<br />
<br />
<table border="0" width="100%">
  <ccp:evaluate if="<%= !thisUser.getEnabled() %>">
  <tr>
    <td>
      <img src="<%= ctx %>/images/error.gif" border="0" align="absMiddle"/>
    </td>
    <td width="100%" colspan="2">
      This user is not allowed to login
    </td>
  </tr>
  </ccp:evaluate>
  <tr>
    <td>
      <img src="<%= ctx %>/images/icons/stock_new-bcard-16.gif" border="0" align="absMiddle"/>
    </td>
    <td width="100%">
      <strong><ccp:username id="${thisUser.id}" showProfile="true" showPresence="true" showCityState="true" /></strong>
    </td>
    <td align="right" nowrap>
      (Type: <ccp:evaluate if="<%= thisUser.getAccessAdmin() %>">Administrator</ccp:evaluate><ccp:evaluate if="<%= !thisUser.getAccessAdmin() %>">User</ccp:evaluate>)
      &nbsp;
    </td>
  </tr>
</table>
<div class="tabs-te" id="toptabs">
<table width="100%">
  <tr>
    <ccp:tabbedMenu text="Details" key="details" value="details" url="<%= detailsUrl %>"/>
    <ccp:tabbedMenu text="Projects" key="projects" value="details" url="<%= projectsUrl %>"/>
    <ccp:tabbedMenu text="Logins" key="logins" value="details" url="<%= loginsUrl %>"/>
    <ccp:tabbedMenu text="Languages" key="languages" value="details" url="<%= languagesUrl %>"/>
    <ccp:tabbedMenu text="Web Sites" key="web-sites" value="details" url="<%= webSitesUrl %>"/>
    <td width="100%" style="background-image: none; background-color: transparent; border: 0px; border-bottom: 1px solid #666; cursor: default;">&nbsp;</td>
    </tr>
</table>
</div>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Contact Information
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel">Name</td>
      <td>
        <%= toHtml(thisUser.getNameFirstLast()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Organization</td>
      <td>
        <%= toHtml(thisUser.getCompany()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Email Address</td>
      <td>
        <%= toHtml(thisUser.getEmail()) %>
      </td>
    </tr>
  </tbody>
</table>

<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Login Information
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel">Username</td>
      <td>
        <%= toHtml(thisUser.getUsername()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Login Enabled</td>
      <td>
        <%= thisUser.getEnabled() %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Access Expiration</td>
      <td>
        <ccp:tz timestamp="<%= thisUser.getExpiration() %>" default="Account does not expire"/>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Last Login</td>
      <td>
        <ccp:tz timestamp="<%= thisUser.getLastLogin() %>" default="&nbsp;"/>
      </td>
    </tr>
  </tbody>
</table>

<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Access Summary
      </th>
    </tr>
  </thead>
  <tbody>
    <%-- Show enabled information --%>
    <%-- Show expiration information --%>

    <%-- Admin access --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Admin</td>
      <td>
        <ccp:evaluate if="<%= !thisUser.getAccessAdmin() %>">
          <img border="0" src="<%= ctx %>/images/box.gif" alt="" align="absmiddle" />
          <i>User does not have administrator access</i>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisUser.getAccessAdmin() %>">
          <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="" align="absmiddle" />
          <i>User has administrator access</i>
        </ccp:evaluate>
      </td>
    </tr>

    <%-- Content Editor access --%>
    <ccp:evaluate if="<%= thisUser.getWebSiteLanguageList().size() > 0 %>">
    <tr class="containerBody">
      <td nowrap class="formLabel">Web Content</td>
      <td>
        <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="" align="absmiddle" />
        <i>User is enabled for content editor... see list of web sites.</i>
      </td>
    </tr>
    </ccp:evaluate>

    <%-- Invitation access --%>
    <ccp:evaluate if="<%= !allowInvite %>">
    <tr class="containerBody">
      <td nowrap class="formLabel">Invitations</td>
      <td>
        <ccp:evaluate if="<%= !thisUser.getAccessInvite() %>">
          <img border="0" src="<%= ctx %>/images/box.gif" alt="" align="absmiddle" />
          <i>User cannot invite others by email address</i>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisUser.getAccessInvite() %>">
          <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="" align="absmiddle" />
          <i>User can invite others by email address</i>
        </ccp:evaluate>
      </td>
    </tr>
    </ccp:evaluate>

    <%-- Create project access --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Projects</td>
      <td>
        <ccp:evaluate if="<%= !thisUser.getAccessAddProjects() %>">
          <img border="0" src="<%= ctx %>/images/box.gif" alt="" align="absmiddle" />
          <i>User cannot create new projects and reports</i>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisUser.getAccessAddProjects() %>">
          <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="" align="absmiddle" />
          <i>User can create new projects and reports</i>
        </ccp:evaluate>
      </td>
    </tr>

    <%-- Access to Contacts --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Contacts</td>
      <td>
        <ccp:evaluate if="<%= thisUser.getAccessViewAllContacts() %>">
          <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="" align="absmiddle" />
          <i>User can view the shared contact directory</i>
        </ccp:evaluate>
        <ccp:evaluate if="<%= !thisUser.getAccessViewAllContacts() %>">
          <img border="0" src="<%= ctx %>/images/box.gif" alt="" align="absmiddle" />
          <i>User cannot view the shared contact directory</i>
        </ccp:evaluate>
        <br />
        <ccp:evaluate if="<%= thisUser.getAccessEditAllContacts() %>">
          <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="" align="absmiddle" />
          <i>User can edit the shared contact directory</i>
        </ccp:evaluate>
        <ccp:evaluate if="<%= !thisUser.getAccessEditAllContacts() %>">
          <img border="0" src="<%= ctx %>/images/box.gif" alt="" align="absmiddle" />
          <i>User cannot edit the shared contact directory</i>
        </ccp:evaluate>
      </td>
    </tr>

    <%-- Document library access --%>
    <tr class="containerBody">
      <td nowrap class="formLabel">Document Library</td>
      <td>
        <ccp:evaluate if="<%= thisUser.getAccountSize() > -1 %>">
          <img border="0" src="<%= ctx %>/images/box.gif" alt="" align="absmiddle" />
          <i>User is limited to <%= thisUser.getAccountSize() %> MB of document storage space</i>
        </ccp:evaluate>
        <ccp:evaluate if="<%= thisUser.getAccountSize() == -1 %>">
          <img border="0" src="<%= ctx %>/images/box-checked.gif" alt="" align="absmiddle" />
          <i>User can store an unlimited number of documents</i>
        </ccp:evaluate>
      </td>
    </tr>
  </tbody>
</table>
<br />

<table cellpadding="4" cellspacing="0" width="100%" class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Usage Information
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel">Projects Created</td>
      <td>
        <%= userProjectCount %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Document Library</td>
      <td>
        <%= FileUtils.getRelativeSize(thisUser.getCurrentAccountSize(), null) %>
      </td>
    </tr>
  </tbody>
</table>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Record Information
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel">Entered</td>
      <td>
        <ccp:username id="<%= thisUser.getEnteredBy() %>"/>
        <ccp:tz timestamp="<%= thisUser.getEntered() %>"/>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel">Modified</td>
      <td>
        <ccp:username id="<%= thisUser.getModifiedBy() %>"/>
        <ccp:tz timestamp="<%= thisUser.getModified() %>" default="&nbsp;"/>
      </td>
    </tr>
  </tbody>
</table>
<input type="button" value="Modify" onClick="window.location.href='<%= ctx %>/AdminUserDetails.do?command=Modify&id=<%= thisUser.getId() %>'" />
