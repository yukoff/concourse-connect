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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<div class="portletWrapper">
  <h1>
    <ccp:label name="projectsCenterSetup.customize.customizeTabs">Customize Tabs</ccp:label>
    <span><a href="${ctx}/show/${project.uniqueId}/setup"><ccp:label name="projectsCenterSetup.customize.setup">Back to Setup</ccp:label></a></span>
  </h1>
  <div class="formContainer">
    <form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagement.do?command=UpdateFeatures&auto-populate=true">
      <input type="hidden" name="id" value="<%= project.getId() %>">
      <input type="hidden" name="modified" value="<%= project.getModified() %>">
      <fieldset id="<ccp:label name="projectsCenterSetup.customize.updateProjectFeatures">Update Project Features</ccp:label>">
        <legend><ccp:label name="projectsCenterSetup.customize.updateProjectFeatures">Update Project Features</ccp:label></legend>
        <%--
        <ccp:evaluate if="<%= User.getAccessGuestProjects() %>">
          <tr class="containerBody">
            <td class="formLabel" valign="top">Global Settings</td>
            <td>
              <input type="checkbox" name="portal" value="ON"<%= project.getPortal() ? " checked" : "" %>>
              Project is the default home page project for all users (including guests)<br>
              <input type="checkbox" name="allowGuests" value="ON"<%= project.getAllowGuests() ? " checked" : "" %>>
              Guests are allowed to view this project, without logging in and without being a member of the project
            </td>
          </tr>
        </ccp:evaluate>
        --%>
        <ccp:label name="projectsCenterSetup.customize.projectTabs">Project Tabs</ccp:label>
        <table width="100%" class="empty">
          <tr>
            <td><ccp:label name="projectsCenterSetup.customize.enabled">Enabled</ccp:label></td>
            <td><ccp:label name="projectsCenterSetup.customize.order">Order</ccp:label></td>
            <td><ccp:label name="projectsCenterSetup.customize.tab">Tab</ccp:label></td>
            <td width="100%"><ccp:label name="propjectsCenterSetup.customize.label">Label</ccp:label></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showProfile" value="ON"<%= project.getFeatures().getShowProfile() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderProfile" value="<%= project.getFeatures().getOrderProfile() %>" size="3" maxlength="10"/></td>
            <td>Profile</td>
            <td><input type="text" name="features_labelProfile" value="<%= toHtmlValue(project.getFeatures().getLabelProfile()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showDashboard" value="ON"<%= project.getFeatures().getShowDashboard() ? " checked" : "" %>></td>
            <td><input type="text" name="orderDashboard" value="<%= project.getOrderDashboard() %>" size="3" maxlength="10"/></td>
            <td>Dashboard</td>
            <td><input type="text" name="labelDashboard" value="<%= toHtmlValue(project.getFeatures().getLabelDashboard()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showCalendar" value="ON"<%= project.getFeatures().getShowCalendar() ? " checked" : "" %>></td>
            <td><input type="text" name="orderCalendar" value="<%= project.getOrderCalendar() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.calendar">Calendar</ccp:label></td>
            <td><input type="text" name="labelCalendar" value="<%= toHtmlValue(project.getFeatures().getLabelCalendar()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showNews" value="ON"<%= project.getFeatures().getShowNews() ? " checked" : "" %>></td>
            <td><input type="text" name="orderNews" value="<%= project.getOrderNews() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.news">News</ccp:label></td>
            <td><input type="text" name="labelNews" value="<%= toHtmlValue(project.getFeatures().getLabelNews()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showWiki" value="ON"<%= project.getFeatures().getShowWiki() ? " checked" : "" %>></td>
            <td><input type="text" name="orderWiki" value="<%= project.getOrderWiki() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.wiki">Wiki</ccp:label></td>
            <td><input type="text" name="labelWiki" value="<%= toHtmlValue(project.getFeatures().getLabelWiki()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showDiscussion" value="ON"<%= project.getFeatures().getShowDiscussion() ? " checked" : "" %>></td>
            <td><input type="text" name="orderDiscussion" value="<%= project.getOrderDiscussion() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.discussion">Discussion</ccp:label></td>
            <td><input type="text" name="labelDiscussion" value="<%= toHtmlValue(project.getFeatures().getLabelDiscussion()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showDocuments" value="ON"<%= project.getFeatures().getShowDocuments() ? " checked" : "" %>></td>
            <td><input type="text" name="orderDocuments" value="<%= project.getOrderDocuments() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.documents">Documents</ccp:label></td>
            <td><input type="text" name="labelDocuments" value="<%= toHtmlValue(project.getFeatures().getLabelDocuments()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showLists" value="ON"<%= project.getFeatures().getShowLists() ? " checked" : "" %>></td>
            <td><input type="text" name="orderLists" value="<%= project.getOrderLists() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.lists">Lists</ccp:label></td>
            <td><input type="text" name="labelLists" value="<%= toHtmlValue(project.getFeatures().getLabelLists()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showPlan" value="ON"<%= project.getFeatures().getShowPlan() ? " checked" : "" %>></td>
            <td><input type="text" name="orderPlan" value="<%= project.getOrderPlan() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.plan">Plan</ccp:label></td>
            <td><input type="text" name="labelPlan" value="<%= toHtmlValue(project.getFeatures().getLabelPlan()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showTickets" value="ON"<%= project.getFeatures().getShowTickets() ? " checked" : "" %>></td>
            <td><input type="text" name="orderTickets" value="<%= project.getOrderTickets() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.tickets">Tickets</ccp:label></td>
            <td><input type="text" name="labelTickets" value="<%= toHtmlValue(project.getFeatures().getLabelTickets()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="showTeam" value="ON"<%= project.getFeatures().getShowTeam() ? " checked" : "" %>></td>
            <td><input type="text" name="orderTeam" value="<%= project.getOrderTeam() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.team">Team</ccp:label></td>
            <td><input type="text" name="labelTeam" value="<%= toHtmlValue(project.getFeatures().getLabelTeam()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showDetails" value="ON"<%= project.getFeatures().getShowDetails() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderDetails" value="<%= project.getOrderDetails() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.details">Details</ccp:label></td>
            <td><input type="text" name="features_labelDetails" value="<%= toHtmlValue(project.getFeatures().getLabelDetails()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showBadges" value="ON"<%= project.getFeatures().getShowBadges() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderBadges" value="<%= project.getFeatures().getOrderBadges() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.badges">Badges</ccp:label></td>
            <td><input type="text" name="features_labelBadges" value="<%= toHtmlValue(project.getFeatures().getLabelBadges()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showReviews" value="ON"<%= project.getFeatures().getShowReviews() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderReviews" value="<%= project.getFeatures().getOrderReviews() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.reviews">Reviews</ccp:label></td>
            <td><input type="text" name="features_labelReviews" value="<%= toHtmlValue(project.getFeatures().getLabelReviews()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showClassifieds" value="ON"<%= project.getFeatures().getShowClassifieds() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderClassifieds" value="<%= project.getFeatures().getOrderClassifieds() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.classifieds">Classifieds</ccp:label></td>
            <td><input type="text" name="features_labelClassifieds" value="<%= toHtmlValue(project.getFeatures().getLabelClassifieds()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showAds" value="ON"<%= project.getFeatures().getShowAds() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderAds" value="<%= project.getFeatures().getOrderAds() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.ads">Ads</ccp:label></td>
            <td><input type="text" name="features_labelAds" value="<%= toHtmlValue(project.getFeatures().getLabelAds()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showMessages" value="ON"<%= project.getFeatures().getShowMessages() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderMessages" value="<%= project.getFeatures().getOrderMessages() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.messages">Messages</ccp:label></td>
            <td><input type="text" name="features_labelMessages" value="<%= toHtmlValue(project.getFeatures().getLabelMessages()) %>" maxlength="50"/></td>
          </tr>
          <tr>
            <td align="center"><input type="checkbox" name="features_showWebcasts" value="ON"<%= project.getFeatures().getShowWebcasts() ? " checked" : "" %>></td>
            <td><input type="text" name="features_orderWebcasts" value="<%= project.getFeatures().getOrderWebcasts() %>" size="3" maxlength="10"/></td>
            <td><ccp:label name="projectsCenterSetup.customize.webcasts">Webcasts</ccp:label></td>
            <td><input type="text" name="features_labelWebcasts" value="<%= toHtmlValue(project.getFeatures().getLabelWebcasts()) %>" maxlength="50"/></td>
          </tr>
        </table>
      </fieldset>
      <input type="submit" class="submit" value="<ccp:label name="button.update">Update</ccp:label>">
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='${ctx}/show/${project.uniqueId}/setup'">
    </form>
  </div>
</div>
