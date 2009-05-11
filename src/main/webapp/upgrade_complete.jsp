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
<jsp:useBean id="installLog" class="java.util.ArrayList" scope="request" />
<%@ include file="initPage.jsp" %>
<div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
  <div style="width:600px; height:600px; position:relative; margin:0 auto; background:#efefef">
    <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Upgrade Complete!</h1>
    <div style="font-size:small; width:500px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:0 15px 15px">
      <h2 style="font-size:large; font-weight:normal; text-align:center; margin:25px 0">
        <img src="${ctx}/images/icons/check_button_green_16x16.png" alt="Success!">
        Success!
      </h2>
      <p>The system has been upgraded and users can now login.</p>
      <p>Upgrade Log:</p>
      <p>
        <ccp:evaluate if="<%= installLog.size() == 0 %>">
          No scripts executed
        </ccp:evaluate>
        <%
          Iterator installs = installLog.iterator();
          while (installs.hasNext()) {
          String step = (String) installs.next();
        %>
          <%= toHtml(step) %><br />
        <%}%>
      </p>
      <input type="button" value="Home Page >" onClick="window.location.href='<%= ctx %>/Login.do?command=Logout'" />
    </div>
  </div>
  <br />"
</div>
