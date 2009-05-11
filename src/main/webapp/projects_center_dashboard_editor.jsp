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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%@ include file="initPage.jsp" %>
<jsp:include page="css_include.jsp" flush="true"/>
<script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/portal.js"></script>
<script type="text/javascript">
  function checkValues(form) {
    return true;
  }
  function initEditor(e) {
    initPortletHandle("portletHtmlPortlet");
    initPortletHandle("portletPollPortlet");
    initPortletHandle("portletCalendarPortlet");
    initPortletHandle("portletMyRecentTicketsPortlet");
    initPortletHandle("portletMyRecentAssignmentsPortlet");
    initPortletHandle("portletRecentlyUpdatedTicketsPortlet");
    initPortletHandle("portletRecentlyUpdatedAssignmentsPortlet");

    initPortletWindow("r0c1d1");
    initPortletWindow("r1c1d1");
    initPortletWindow("r1c2d1");
    initPortletWindow("r3c1d1");

    // Instantiate a Panel from markup
    YAHOO.namespace("example.container");
    YAHOO.example.container.panel1 = new YAHOO.widget.Panel("panel1", { width:"300px", visible:true, constraintoviewport:true, close:false } );
    YAHOO.example.container.panel1.render();
  }
  YAHOO.util.Event.addListener(window, "load", initEditor);
</script>

<style type="text/css">
  .indexed {
    width: 10px;
    background: #CCCCCC;
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
<table border="0" cellpadding="1" cellspacing="0" width="100%">
  <tr class="subtab">
    <td width="100%" valign="top">
      <img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle" />
      <ccp:tabLabel name="Dashboard" object="project"/>
    </td>
  </tr>
</table>
<br />
<%-- the editor --%>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" width="100%">
      <form action="" onSubmit="checkValues(this)">
        <p>
          <input type="button" value="append row" onclick="addRow('master')" />
          <input type="button" value="append column" onclick="addColumn('master')" />
          <input type="button" value="delete last row" onclick="deleteRow('master')" />
          <input type="button" value="delete last column" onclick="deleteColumn('master')" />
        </p>
        <table id="master" class="pagedList">
          <thead>
            <caption>
              Dashboard Title
            </caption>
            <tr class="row1">
              <th>&nbsp;</th>
              <th>A</th>
              <th>B</th>
            </tr>
          </thead>
          <tbody>
            <tr id="r0">
              <td class="indexed">1</td>
              <td id="r0c1">
                <div id="r0c1d1" class="basket">
                  <a href='javascript:expandRight("r0c1");'>&gt;</a>
                </div>
              </td>
            </tr>
            
          </tbody>
        </table>
        <br />
        <input type="submit" name="Save" value="Save all changes" />
      </form>
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
  </div>
  <div class="ft"></div>
</div>
