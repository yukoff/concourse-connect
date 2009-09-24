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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="dashboardList" class="com.concursive.connect.cms.portal.dao.DashboardList" scope="request"/>
<jsp:useBean id="dashboard" class="com.concursive.connect.cms.portal.dao.Dashboard" scope="request"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.name.value == "") {
      messageText += "- Name is a required field\r\n";
      formTest = false;
    }
    if (form.level.value == "" || form.level.value == "-1" || form.level.value == "0") {
      messageText += "- Level must be greater than 0\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<body onLoad="document.inputForm.name.focus()">
<form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementDashboard.do?command=Save&pid=<%= project.getId() %>&auto-populate=true" onSubmit="return checkForm(this);">
  <%-- Required state fields --%>
  <input type="hidden" name="id" value="<%= dashboard.getId() %>" />
  <input type="hidden" name="modified" value="<%= dashboard.getModified() %>" />
  <input type="hidden" name="return" value="<%= StringUtils.toHtmlValue(request.getParameter("return")) %>"/>
  <%-- Trails --%>
  <table border="0" cellpadding="1" cellspacing="0" width="100%">
    <tr class="subtab">
      <td>
        <img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle" />
        <a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=<%= project.getId() %>"><ccp:tabLabel name="Dashboard" object="project"/></a> >
        <%= dashboard.getId() == -1 ? "Add" : "Update" %>
      </td>
    </tr>
  </table>
  <br />
  <%-- Show the dashboards --%>
  <ccp:evaluate if="<%= dashboardList.size() > 0 %>">
    <c:forEach items="${dashboardList}" var="thisDashboard">
      <c:choose>
        <c:when test="${thisDashboard.id == dashboard.id}">
          *<a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=<%= project.getId() %>&dash=<c:out value="${thisDashboard.id}" />"><c:out value="${thisDashboard.name}" /></a>*
        </c:when>
        <c:otherwise>
          <a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=<%= project.getId() %>&dash=<c:out value="${thisDashboard.id}" />"><c:out value="${thisDashboard.name}" /></a>
        </c:otherwise>
      </c:choose>
    </c:forEach>
    <br />
  </ccp:evaluate>
  <%= showError(request, "actionError") %>
  <%-- Portal editor --%>
  <script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/portal.js?1"></script>
  <script type="text/javascript" language="JavaScript">
    function initEditor(e) {
      initPortletHandle("portletSpreadsheet");
      initPortletHandle("portletGraph");
      initPortletHandle("portletGauges");
      initPortletHandle("portletHtmlPortlet");
      initPortletHandle("portletPollPortlet");
      initPortletHandle("portletCalendarPortlet");
      initPortletHandle("portletMyRecentTicketsPortlet");
      initPortletHandle("portletMyRecentAssignmentsPortlet");
      initPortletHandle("portletRecentlyUpdatedTicketsPortlet");
      initPortletHandle("portletRecentlyUpdatedAssignmentsPortlet");

      initPortletWindow("r0c0d1");

      // Instantiate a Panel from markup
      YAHOO.namespace("example.container");
      YAHOO.example.container.panel1 = new YAHOO.widget.Panel("panel1", { width:"300px", visible:true, constraintoviewport:true, close:false } );
      YAHOO.example.container.panel1.render();
    }
    YAHOO.util.Event.addListener(window, "load", initEditor);
  </script>
  <style type="text/css">
    .spreadsheet th {
      background: #EDEDED;
      border-top: 1px solid gray;
    }
    .spreadsheet th, .spreadsheet td {
      border-bottom: 1px solid gray;
      border-right: 1px solid gray;
    }
    .indexed {
      background: #EDEDED;
      border-left: 1px solid gray;
    }
    .basket {
      background: white;
      height: 50px;
    }
    .primed {
      background: #DEDEDE !important;
      height: 50px;
    }
    .hovered {
      background: #FFFF66 !important;
      height: 50px;
    }
    .portletLibrary {
      cursor:pointer;
      border-bottom:1px solid #333333;
    }
  </style>
  <br />
  <table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
      <td valign="top" width="100%">
        <p>
          <input type="button" value="append row" onclick="addRow('master')" />
          <input type="button" value="append column" onclick="addColumn('master')" />
          <input type="button" value="delete last row" onclick="deleteRow('master')" />
          <input type="button" value="delete last column" onclick="deleteColumn('master')" />
        </p>
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <th>
              <input type="text" name="name" size="30" maxlength="255" value="<%= toHtmlValue(dashboard.getName()) %>" />
              <input type="checkbox" name="enabled" value="ON" <%= dashboard.getEnabled() ? "checked" : "" %>>
              <input type="text" name="level" size="6" maxlength="3" value="<%= dashboard.getLevel() %>" />
            </th>
          </tr>
        </table>
        <table id="master" border="0" cellpadding="0" cellspacing="0" width="100%" class="spreadsheet">
          <thead>
            <tr>
              <th width="10" style="border-left: 1px solid gray;">&nbsp;</th>
              <th width="100%">A</th>
            </tr>
          </thead>
          <tbody>
            <tr id="r0">
              <td class="indexed">1</td>
              <td id="r0c0">
                <div id="r0c0d1" class="basket">
                  <a href='javascript:expandRight("r0c0");'>&gt;</a>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <br />
        <input type="submit" name="Save" value="Save all changes" />
        <input type="button" value="Cancel" onclick="window.location.href='<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=Dashboard&pid=<%= project.getId() %>&dash=<%= dashboard.getId() %>'" />
      </td>
    </tr>
  </table>
  <div id="panel1">
    <div class="hd">
      Portlet Library
    </div>
    <div class="bd">
      <div id="portletHtmlPortlet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Text and HTML Content</div>

      <div id="portletSpreadsheet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Spreadsheet</div>

      <div id="portletGraph" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Graph</div>

      <div id="portletGauges" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Gauges</div>
<%--

      <div id="portletPollPortlet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Poll</div>
      <div id="portletCalendarPortlet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Calendar</div>
      <div id="portletMyRecentTicketsPortlet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        My Recent Tickets</div>
      <div id="portletMyRecentAssignmentsPortlet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        My Recent Assignments</div>
      <div id="portletRecentlyUpdatedTicketsPortlet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Recently Updated Tickets</div>
      <div id="portletRecentlyUpdatedAssignmentsPortlet" class="portletLibrary">
        <img src="http://www.teamelements.com/images/teamelements/join-16.gif" />
        Recently Updated Assignments</div>
--%>
    </div>
    <div class="ft"></div>
  </div>
</form>
</body>