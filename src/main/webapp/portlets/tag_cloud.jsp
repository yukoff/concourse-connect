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
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<%@ include file="../initPage.jsp" %>
<%-- Temp. fix for Weblogic --%>
<% 
boolean sslEnabled = "true".equals(applicationPrefs.get("SSL"));
%>
<table border="0" cellspacing="0" cellpadding="4" width="100%">
  <tr class="shadow">
    <td align="center" style="border-top: 1px solid #000; border-left: 1px solid #000; border-right: 1px solid #000" nowrap width="100%">
      <b>Tags</b>
    </td>
  </tr>
  <tr bgColor="#FFFFFF">
    <td style="border-left: 1px solid #000; border-right: 1px solid #000; border-bottom: 1px solid #000;" width="100%">
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <tr>
          <td align="center" style="border-bottom: 1px #333 solid;">
            <a href="#">My tags</a> | <a href="#">Popular tags</a><br />
            <a href="#">name</a> | <a href="#">frequency</a>
          </td>
        </tr>
        <tr>
          <td align="center">
            <a href="#" class="rollover cloud1">advertising</a>
            <a href="#" class="rollover cloud4">ajax</a>
            <a href="#" class="rollover cloud3">apple</a>
            <a href="#" class="rollover cloud5">art</a>
            <a href="#" class="rollover cloud3">article</a>
            <a href="#" class="rollover cloud1">articles</a>
            <a href="#" class="rollover cloud2">asp.net</a>
            <a href="#" class="rollover cloud2">audio</a>
            <a href="#" class="rollover cloud5">blog</a>
            <a href="#" class="rollover cloud3">blogging</a>
            <a href="#" class="rollover cloud5">blogs</a>
            <a href="#" class="rollover cloud1">book</a>
            <a href="#" class="rollover cloud4">books</a>
            <a href="#" class="rollover cloud4">business</a>
            <a href="#" class="rollover cloud1">cms</a>
            <a href="#" class="rollover cloud1">code</a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
