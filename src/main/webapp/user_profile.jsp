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
<%@ page import="java.util.*" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="timeZone" class="com.concursive.connect.web.utils.HtmlSelectTimeZone" scope="request"/>
<jsp:useBean id="currency" class="com.concursive.connect.web.utils.HtmlSelectCurrency" scope="request"/>
<jsp:useBean id="language" class="com.concursive.connect.web.utils.HtmlSelectLanguage" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Temp. fix for Weblogic --%>
<%
boolean hasPrefsRegister = "true".equals(applicationPrefs.get("REGISTER"));
%>
<div class="portletWindowBackground">
  <div class="portletWrapper accountInfo tableContainer">
    <h1><ccp:label name="userProfile.accountInformation">Your Account Information</ccp:label></h1>
    <ccp:evaluate if="<%= hasPrefsRegister %>">
      <p></p>
    </ccp:evaluate>
    <ul>
      <li><span class="status"><ccp:label name="userProfile.accountActive">Your account is active</ccp:label></span></li>
      <li><ccp:label name="userProfile.memberSince">You have been a member since</ccp:label> <ccp:tz timestamp="<%= User.getEntered() %>" pattern="MMMM yyyy" default="&nbsp;"/></li>
      <li>Your link profile (to share with others) is <ccp:username id="<%= User.getId() %>" showPresence="false"/></li>
    </ul>
    <h3>Contact Information</h3>
    <dl>
      <dt><ccp:label name="userProfile.firstName">First Name</ccp:label></dt>
      <dd><%= toHtml(User.getFirstName()) %></dd>
      <dt><ccp:label name="userProfile.lastName">Last Name</ccp:label></dt>
      <dd><%= toHtml(User.getLastName()) %></dd>
      <dt><ccp:label name="userProfile.organization">Organization</ccp:label></dt>
      <dd><%= toHtml(User.getCompany()) %></dd>
    </dl>
    <h3>Your Current Location Information  <span class="edit"><a href="<%= ctx %>/Profile.do?command=ModifyLocation"><ccp:label name="userProfile.modify">modify</ccp:label></a></span></h3>
    <dl>
      <dt><ccp:label name="userProfile.currentTime">Current Time</ccp:label></dt>
      <dd><ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" timeFormat="<%= DateFormat.LONG %>" /></dd>
      <dt><ccp:label name="userProfile.defaultLanguage">Default Language</ccp:label></dt>
      <dd>
        <ccp:evaluate if="<%= hasText(User.getLanguage()) %>">
          <%= toHtml(language.getSelect("a1", User.getLanguage()).getValueFromId(User.getLanguage())) %>
        </ccp:evaluate>
        <ccp:evaluate if="<%= !hasText(User.getLanguage()) %>">
          &lt;default setting&gt;
        </ccp:evaluate>
      </dd>
    </dl>
    <a class="submit" href="<%= ctx %>/Password.do?command=ChangePassword"><ccp:label name="userProfile.changeMyPassword">Change my password</ccp:label></a>
  </div>
</div>