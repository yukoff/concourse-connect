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
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<jsp:useBean id="publicProjects" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<%@ include file="../initPage.jsp" %>
<table border="0" cellspacing="0" cellpadding="4" width="100%">
  <tr class="shadow">
    <td align="center" style="border-top: 1px solid #000; border-left: 1px solid #000; border-right: 1px solid #000" nowrap width="100%">
      <b>Sponsored Projects</b>
    </td>
  </tr>
<%
  int count = 0;
  Iterator i = publicProjects.iterator();
  while (i.hasNext() && (count < 6)) {
    Project thisProject = (Project) i.next();
    if (!thisProject.getPortal()) {
      ++count;
%>
  <tr bgColor="#FFFFFF">
    <td width="100%" style="border-left: 1px solid #000; border-right: 1px solid #000;" width="100%">
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <tr>
          <td valign="top">
            <img src="<%= ctx %>/images/bullet.gif" align="absmiddle" alt=""/>
          </td>
          <td valign="top" width="100%">
            <a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&pid=<%= thisProject.getId() %>"><%= toHtml(thisProject.getTitle()) %></a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
<%  }
  } %>
  <tr bgColor="#FFFFFF">
    <td width="100%" style="border-left: 1px solid #000; border-right: 1px solid #000; border-bottom: 1px solid #000;">
      <table border="0" cellpadding="2" cellspacing="0" width="100%">
        <tr>
          <td valign="top">
            <img src="<%= ctx %>/images/bullet.gif" align="absmiddle" alt=""/>
          </td>
          <td valign="top" width="100%">
            If you are interested in having your project displayed here for anyone to see, please <a href="<%= ctx %>/ContactUs.do?auto-populate=true&description=%5BSponsored%20Project%5D%20%20">contact us</a> for more information
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
