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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<jsp:useBean id="projectView" class="java.lang.String" scope="session"/>
<%@ include file="initPage.jsp" %>
<%
  if (project.getId() == -1) {
%>
<br /><font color="red">This project does not belong to you, or does not exist!
<%
  } else {
    String section = (String) request.getAttribute("IncludeSection");
    String includeSection = "projects_center_" + section + ".jsp";

String wikiLabel = project.getLabel("Wiki");
String wikiUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Wiki&pid=" + project.getId();
String dashboardLabel = project.getLabel("Dashboard");
String dashboardUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=" + project.getId();
String calendarLabel = project.getLabel("Calendar");
String calendarUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Calendar&source=Calendar&pid=" + project.getId() + "&reloadCalendarDetails=true";
String newsLabel = project.getLabel("Blog");
String newsUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=News&pid=" + project.getId();
String discussionLabel = project.getLabel("Discussion");
String discussionUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Issues_Categories&pid=" + project.getId();
String documentsLabel = project.getLabel("Documents");
String documentsUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=File_Library&pid=" + project.getId() + "&folderId=-1";
String listsLabel = project.getLabel("Lists");
String listsUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Lists_Categories&pid=" + project.getId();
String planLabel = project.getLabel("Plan");
String planUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Requirements&pid=" + project.getId();
String ticketsLabel = project.getLabel("Tickets");
String ticketsUrl = RequestUtils.getAbsoluteServerUrl(request) + "/show/" + project.getUniqueId() + "/issues";
String teamLabel = project.getLabel("Team");
String teamUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Team&pid=" + project.getId();
String detailsLabel = project.getLabel("Details");
String detailsUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Details&pid=" + project.getId();
String setupUrl = RequestUtils.getAbsoluteServerUrl(request) + "/ProjectManagement.do?command=ProjectCenter&section=Setup&pid=" + project.getId();

%>
<ccp:evaluate if='<%= !isPopup(request) && request.getParameter("out") == null %>'>
<style>
#yui-history-iframe {
  position:absolute;
  top:0; left:0;
  width:1px; height:1px;
  visibility:hidden;
}
</style>
<iframe id="yui-history-iframe" src="assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden" />
<% boolean isMaximized = "MAXIMIZED".equals(projectView); %>
<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
  <tr>
  <td valign="top">
    <table border="0" width="100%">
      <tr>
        <td width="100%">
          <strong><%= toHtml(project.getTitle()) %></strong>
          <ccp:evaluate if="<%= project.getPortal() %>">
            <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/portal.gif" border="0" alt="" align="absmiddle" />
          </ccp:evaluate>
          <ccp:evaluate if="<%= project.getFeatures().getAllowGuests() %>">
            <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/public.gif" border="0" alt="" align="absmiddle" />
          </ccp:evaluate>
          <ccp:evaluate if="<%= project.getFeatures().getMembershipRequired() %>">
            <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/members_only.gif" border="0" alt="" align="absmiddle" />
          </ccp:evaluate>
          <ccp:evaluate if="<%= project.getApprovalDate() == null %>">
            <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/unapproved.gif" border="0" alt="" align="absmiddle" />
          </ccp:evaluate>
          (<ccp:label name="projectsCenter.role">Role</ccp:label>: <ccp:role id="<%= currentMember.getUserLevel() %>"/>)
          <ccp:evaluate if="<%= currentMember.isTemporaryAdmin() %>">
            <font color="red">(<ccp:label name="projectsCenter.admin">Admin</ccp:label>)</font>
          </ccp:evaluate>
        </td>
        <td align="right" nowrap>
          &nbsp;
          <ccp:permission name="project-details-view">
            <ccp:evaluate if="<%= project.getFeatures().getShowDetails() || currentMember.getRoleId() <= TeamMember.PROJECT_ADMIN %>">
              <a href="<%= detailsUrl %>"><%= detailsLabel %></a>
            </ccp:evaluate>
          </ccp:permission>
          <ccp:evaluate if="<%= currentMember.getRoleId() <= TeamMember.PROJECT_ADMIN %>">
            | <a href="<%= setupUrl %>">Setup</a>
          </ccp:evaluate>
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <%= toHtml(project.getShortDescription()) %>
        </td>
      </tr>
    </table>
  </td>
  </tr>
  <tr>
    <td class="yui-skin-sam">
      <div id="project-center-tabs">
        <%--
        <ccp:debug value="<%= includeSection %>" />
        <jsp:include page="<%= includeSection %>" flush="true"/>
        --%>
      </div>
    </td>
  </tr>
</table>

<script type="text/javascript">


  (function () {

    // The initially selected tab will be chosen in the following order:
    //
    // URL fragment identifier (it will be there if the user previously
    // bookmarked the application in a specific state)
    //
    //         or
    //
    // "tab0" (default)

    var bookmarkedTabViewState = YAHOO.util.History.getBookmarkedState("tabview");
    var initialTabViewState = bookmarkedTabViewState || "tab0";

    var tabView;

    // Register our TabView module. Module registration MUST
    // take place before calling YAHOO.util.History.initialize.
    YAHOO.util.History.register("tabview", initialTabViewState, function (state) {
        // This is called after calling YAHOO.util.History.navigate, or after the user
        // has trigerred the back/forward button. We cannot discrminate between
        // these two situations.

        // "state" can be "tab0", "tab1" or "tab2".
        // Select the right tab:
        tabView.set("activeIndex", state.substr(3));
    });

    function handleTabViewActiveTabChange (e) {
        var newState, currentState;

        newState = "tab" + this.getTabIndex(e.newValue);

        try {
            currentState = YAHOO.util.History.getCurrentState("tabview");
            // The following test is crucial. Otherwise, we end up circling forever.
            // Indeed, YAHOO.util.History.navigate will call the module onStateChange
            // callback, which will call tabView.set, which will call this handler
            // and it keeps going from here...
            if (newState != currentState) {
                YAHOO.util.History.navigate("tabview", newState);
            }
        } catch (e) {
            tabView.set("activeIndex", newState.substr(3));
        }
    }

    function initTabView () {
        // Instantiate the TabView control...
        tabView = new YAHOO.widget.TabView();
  <ccp:permission name="project-dashboard-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowDashboard() %>">
      <ccp:tabbedMenu text="<%= dashboardLabel %>" key="dashboard" value="<%= section %>" url="<%= dashboardUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-calendar-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowCalendar() %>">
      <ccp:tabbedMenu text="<%= calendarLabel %>" key="calendar" value="<%= section %>" url="<%= calendarUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-news-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowNews() %>">
      <ccp:tabbedMenu text="<%= newsLabel %>" key="home,news" value="<%= section %>" url="<%= newsUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-wiki-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowWiki() %>">
      <ccp:tabbedMenu text="<%= wikiLabel %>" key="wiki" value="<%= section %>" url="<%= wikiUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-discussion-forums-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowDiscussion() %>">
      <ccp:tabbedMenu text="<%= discussionLabel %>" key="issues" value="<%= section %>" url="<%= discussionUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-documents-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowDocuments() %>">
      <ccp:tabbedMenu text="<%= documentsLabel %>" key="file" value="<%= section %>" url="<%= documentsUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-lists-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowLists() %>">
      <ccp:tabbedMenu text="<%= listsLabel %>" key="lists" value="<%= section %>" url="<%= listsUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-plan-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowPlan() %>">
      <ccp:tabbedMenu text="<%= planLabel %>" key="requirements,assignments" value="<%= section %>" url="<%= planUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-tickets-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowTickets() %>">
      <ccp:tabbedMenu text="<%= ticketsLabel %>" key="tickets" value="<%= section %>" url="<%= ticketsUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
  <ccp:permission name="project-team-view">
    <ccp:evaluate if="<%= project.getFeatures().getShowTeam() %>">
      <ccp:tabbedMenu text="<%= teamLabel %>" key="team" value="<%= section %>" url="<%= teamUrl %>" type="yui" />
    </ccp:evaluate>
  </ccp:permission>
        tabView.appendTo('project-center-tabs');
        tabView.addListener("activeTabChange", handleTabViewActiveTabChange);
    }

    // Use the Browser History Manager onReady method to instantiate the TabView widget.
    YAHOO.util.History.onReady(function () {
        var currentState;

        initTabView();

        // This is the tricky part... The onLoad event is fired when the user
        // comes back to the page using the back button. In this case, the
        // actual tab that needs to be selected corresponds to the last tab
        // selected before leaving the page, and not the initially selected tab.
        // This can be retrieved using getCurrentState:
        currentState = YAHOO.util.History.getCurrentState("tabview");
        tabView.set("activeIndex", currentState.substr(3));
    });

    // Initialize the browser history management library.
    try {
        YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
    } catch (e) {
        // The only exception that gets thrown here is when the browser is
        // not supported (Opera, or not A-grade) Degrade gracefully.
        initTabView();
    }

})();

</script>
</ccp:evaluate>
<ccp:evaluate if='<%= isPopup(request) || "text".equals(request.getParameter("out")) %>'>
  <ccp:debug value="<%= includeSection %>" />
  <jsp:include page="<%= includeSection %>" flush="true"/>
</ccp:evaluate>
<%
  }
%>
