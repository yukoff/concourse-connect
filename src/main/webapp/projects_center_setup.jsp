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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project"
             scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember"
             scope="request"/>
<%@ include file="initPage.jsp" %>
<div class="portletWrapper">
  <h1><ccp:label name="projectsCenterSetup.setup">Setup</ccp:label></h1>
  <ccp:permission name="project-setup-customize,project-setup-permissions"
                     if="any">
    <div class="g_tabsContainer">
      <ul class="g_tabsList">
        <ccp:permission name="project-setup-customize">
          <li><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/customize?return=ProjectCenter"><ccp:label
              name="projectsCenterSetup.setupTabs">Setup Tabs</ccp:label></a></li>
        </ccp:permission>
        <ccp:permission name="project-setup-permissions">
          <li><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/permissions?return=ProjectCenter"><ccp:label
              name="projectsCenterSetup.configurePermissions">Configure Permissions</ccp:label></a></li>
        </ccp:permission>
        <ccp:permission name="project-setup-style">
          <li><a href="<%= ctx %>/show/<%= project.getUniqueId() %>/customize-style?return=ProjectCenter"><ccp:label
                  name="projectsCenterSetup.configureLookFeel">Configure Look and Feel</ccp:label></a></li>
        </ccp:permission>
      </ul>
    </div>
  </ccp:permission>
  <ccp:label name="projectsCenterSetup.message">
    <div class="pagedList">
      <p>To adjust properties choose Setup Tabs. Tabs can be enabled/disabled
      as well as renamed.</p>
      <p>To adjust permissions choose Configure Permissions. Each profile has a set
      of permissions that indicate which user roles are allowed to perform a
      function. By default, the Admin role is capable of all functions,
      while a Guest has very limited functionality.</p>
      <p>There are six major roles:</p>
      <dl>
        <dt><b>Project Lead:</b></dt>
        <dd>Usually the administrator(s) of the project and having the most control over the project data</dd>
        <dt><b>Manager:</b></dt>
        <dd>Usually the owner of the project and has a lot of control over the project data</dd>
        <dt><b>VIP:</b></dt>
        <dd>Those that are invited or elected by managers and admins</dd>
        <dt><b>Member:</b></dt>
        <dd>Those that join the project themselves</dd>
        <dt><b>Participant:</b></dt>
        <dd>Those that are typically interested in reviewing information, they have mostly read access but are possibly allowed to add or update some related information</dd>
        <dt><b>Guest:</b></dt>
        <dd>Typically a read-only role... those that are not direct members but have access to review some of the information</dd>
      </dl>
      <p>To adjust styles choose Configure Look and Feel. CSS can be added or modified, and images for style can be managed.</p>
    </div>
  </ccp:label>
</div>