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
<%@ include file="initPage.jsp" %>
<div class="portletWrapper">
  <%-- Trails --%>
  <h1>
    <%--<img src="<%= ctx %>/images/icons/stock_macro-organizer-16.gif" border="0" align="absmiddle">--%>
    <ccp:label name="projectsCenterTickets.config.configuration">Configuration</ccp:label>
    <span><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/issues"><ccp:label name="projectsCenterTickets.config.tickets">Back to Tickets</ccp:label></a></span>
  </h1>
  <%-- End Trails --%>
  <div class="g_menuContainer configuration">
    <ul class="g_menuList configuration">
      <li>
        <a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=state"><ccp:label name="projectsCenterTickets.config.ticketStates">Ticket States</ccp:label></a>
      </li>
      <li><a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=ConfigureCategories&pid=<%= project.getId() %>&return=ProjectCenter"><ccp:label name="projectsCenterTickets.config.ticketCategories">Ticket Categories</ccp:label></a>
      </li>
      <li><a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=defect"><ccp:label name="projectsCenterTickets.config.ticketDefects">Ticket Defects</ccp:label></a></li>
      <li><a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=cause"><ccp:label name="projectsCenterTickets.config.ticketCauses">Ticket Causes</ccp:label></a></.li>
      <li><a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=escalation"><ccp:label name="projectsCenterTickets.config.ticketEscalationLevels">Ticket Escalation Levels</ccp:label></a></li>
      <li><a href="<%= ctx %>/ProjectManagementTicketsConfig.do?command=ConfigureItemList&pid=<%= project.getId() %>&list=resolution"><ccp:label name="projectsCenterTickets.config.ticketResolutionCategories">Ticket Resolution Categories</ccp:label></a></li>
    </ul>
  </div>
</div>
