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
<jsp:useBean id="errorMessage" class="java.lang.String" scope="request" />
<jsp:useBean id="installLog" class="java.util.ArrayList" scope="request" />
<jsp:useBean id="installedVersion" class="java.lang.String" scope="request"/>
<jsp:useBean id="newVersion" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>

<form style="background:#efefef" name="configure" method="POST" action="<%= ctx %>/Login.do?command=Login&auto-populate=true" onSubmit="return checkForm(this)">
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:600px; height:600px; position:relative; margin:0 auto">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">ConcourseConnect Upgrade</h1>
      <h2 style="color:#5d9fbf; font-size:large; font-weight:normal; text-align:center">
        <img src="${ctx}/images/icons/exclamation.png" alt="Important" style="margin:0 5px 0 0">
        An error occurred during the upgrade!
      </h2>
      <div style="font-size:small; width:700px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <div style="float:left; width:50%; border-right:2px solid #ddd">
          <h3>ConcourseConnect Upgrade Failed</h3>
          <p>
            Your database could be in an inconsistent state. You might want to seek assistance
            with what could have gone wrong. This page might contain some useful information
            during that analysis.
          </p>
          <h3>Suggestions</h3>
          <p>
            If you are in a hurry to get the system working, you could restore the database
            to your last backup and then
            put the previous version back online.
          </p>
        </div>
        <div style="width:45%; float:left; margin-left:10px">
          <h3>Error Log</h3>
          <p>While trying to upgrade from</p>
          '<%= installedVersion %>'
          <p>to</p>
          '<%= newVersion %>'
          <p>an error occurred.</p>
          <p>The following error message was provided:</p>
          <pre><%= toHtml(errorMessage) %></pre>
          <h3>Upgrade Log</h3>
          <ccp:evaluate if="<%= installLog.size() == 0 %>">
            No scripts completed successfully
          </ccp:evaluate>
          <%
          Iterator installs = installLog.iterator();
          while (installs.hasNext()) {
          String step = (String) installs.next();
          %>
            <%= toHtml(step) %>
          <%}%>
        </div>
      </div>
    </div>
  </div>
  <br />
</form>


