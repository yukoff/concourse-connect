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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="invitationCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="whatsAssignedCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="whatsNewCount" class="java.lang.String" scope="request"/>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<%@ include file="../initPage.jsp" %>
<%-- Temp. fix for Weblogic --%>
<%
boolean isUserLoggedIn = User.isLoggedIn();
boolean hasInvitations = hasText(invitationCount) && !"0".equals(invitationCount);
boolean hasWhatsAssignedCount = hasText(whatsAssignedCount) && !"0".equals(whatsAssignedCount);
boolean hasWhatsNewCount = hasText(whatsNewCount) && !"0".equals(whatsNewCount);
boolean allowsRegister = "true".equals((String) getServletConfig().getServletContext().getAttribute("portlet.register"));
boolean sslEnabled = "true".equals(applicationPrefs.get("SSL"));
%>
<table border="0" cellspacing="0" cellpadding="4" width="100%">
  <tr class="shadow">
    <td align="center" style="border-top: 1px solid #000; border-left: 1px solid #000; border-right: 1px solid #000" nowrap width="100%">
      <b><ccp:label name="siteNavigation.title">Site Navigation</ccp:label></b>
    </td>
  </tr>
  <tr bgColor="#FFFFFF">
    <td style="border-left: 1px solid #000; border-right: 1px solid #000; border-bottom: 1px solid #000;" width="100%">
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <%-- Menu item --%>
        <ccp:evaluate if="<%= !isUserLoggedIn %>">
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_home-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="http://<%= getServerUrl(request) %>/">Home</a>
          </td>
        </tr>
        </ccp:evaluate>
        <%-- Menu item --%>
        <ccp:evaluate if="<%= isUserLoggedIn %>">
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_3d-light-on-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/ProjectManagement.do?command=Overview"><ccp:label name="siteNavigation.whatsNew">What's New?</ccp:label></a>
            <ccp:evaluate if="<%= hasWhatsNewCount %>">
              (<font color="red"><%= toHtml(whatsNewCount) %></font>)
            </ccp:evaluate>
          </td>
        </tr>
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_3d-light-on-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/ProjectManagement.do?command=Assignments"><ccp:label name="siteNavigation.whatsAssigned">What's Assigned?</ccp:label></a>
            <ccp:evaluate if="<%= hasWhatsAssignedCount %>">
              (<font color="red"><%= toHtml(whatsAssignedCount) %></font>)
            </ccp:evaluate>
          </td>
        </tr>
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_data-table-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/ProjectManagement.do?command=ProjectList"><ccp:label name="siteNavigation.projectList">Project List</ccp:label></a>
          </td>
        </tr>
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_data-explorer-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/Discussion.do"><ccp:label name="siteNavigation.discussionForums">Discussion Forums</ccp:label></a>
          </td>
        </tr>

        <ccp:evaluate if="<%= hasInvitations %>">
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_about-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/ProjectManagement.do?command=RSVP"><ccp:label name="siteNavigation.invitations">Invitations</ccp:label></a>
            <ccp:evaluate if="<%= hasInvitations %>">
              (<font color="red"><%= toHtml(invitationCount) %></font>)
            </ccp:evaluate>
          </td>
        </tr>
        </ccp:evaluate>

        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_form-time-field-16.gif" align="absmiddle" alt="" border="0" />
            <a class="rollover" href="Timesheet.do"><ccp:label name="siteNavigation.timesheet">Timesheet</ccp:label></a>
          </td>
        </tr>
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_resources-16.gif" align="absmiddle" alt="" border="0" />
            <a href="<%= ctx %>/Resources.do?command=Users"><ccp:label name="siteNavigation.userGraph">User Graph</ccp:label></a>
            <a href="<%= ctx %>/Resources.do?command=Projects"><ccp:label name="siteNavigation.projectGraph">Project Graph</ccp:label></a>
          </td>
        </tr>
        <ccp:evaluate if="<%= User.getAccessRunReports() %>">
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_form-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/Reports.do?command=List"><ccp:label name="siteNavigation.reports">Reports</ccp:label></a>
          </td>
        </tr>
        </ccp:evaluate>
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_bcard-16.gif" align="absmiddle" alt="" border="0" />
            <a class="rollover" href="<%= ctx %>/ContactsSearch.do?command=Form"><ccp:label name="siteNavigation.contacts">Contacts</ccp:label></a>
          </td>
        </tr>
        <%--
        <ccp:evaluate if="<%= User.getAccessUserSettings() %>">
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_preferences-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/Profile.do"><ccp:label name="siteNavigation.personalSettings">Personal Settings</ccp:label></a>
          </td>
        </tr>
        </ccp:evaluate>
        --%>
        <%--
        <ccp:evaluate if="<%= User.getAccessAdmin() %>">
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_form-properties-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<%= ctx %>/ProjectPortal.do?command=Builder">Portal Builder</a>
          </td>
        </tr>
        </ccp:evaluate>
        --%>
        <ccp:evaluate if="<%= User.getAccessAdmin() %>">
          <tr>
            <td>
              <img src="<%= ctx %>/images/icons/stock_form-properties-16.gif" align="absmiddle" alt="" border="0"/>
              <a class="rollover" href="<%= ctx %>/admin"><ccp:label name="siteNavigation.administration">Administration</ccp:label></a>
            </td>
          </tr>
          <ccp:evaluate if="<%= \"true\".equals(applicationPrefs.get(\"PORTAL\")) %>">
            <tr>
              <td>
                <img src="<%= ctx %>/images/icons/stock_preview-three-pages-16.gif" align="absmiddle" alt="" border="0"/>
                <a class="rollover" href="<%= ctx %>/ProjectPortal.do?command=Builder">Site Editor</a>
              </td>
            </tr>
          </ccp:evaluate>
        </ccp:evaluate>
        </ccp:evaluate>
        <ccp:evaluate if="<%= !isUserLoggedIn %>">
        <tr>
          <td>
            <img src="<%= ctx %>/images/icons/stock_jump-to-16.gif" align="absmiddle" alt="" border="0"/>
            <a class="rollover" href="<ccp:evaluate if="<%= sslEnabled %>">https://<%= getServerUrl(request) %>/</ccp:evaluate>Login.do"><ccp:label name="siteNavigation.login">Login</ccp:label></a>
          </td>
        </tr>
        </ccp:evaluate>
      </table>
    </td>
  </tr>
</table>
