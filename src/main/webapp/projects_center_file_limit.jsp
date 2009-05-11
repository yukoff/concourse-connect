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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="FileItem" class="com.concursive.connect.web.modules.documents.dao.FileItem" scope="request"/>
<%@ include file="initPage.jsp" %>
<img src="<%= ctx %>/images/error.gif" border="0" align="absmiddle" />
<font color="red"><ccp:label name="projectsCenterFile.limit.accountExeeded">This user account's file size limit has been exceeded.</ccp:label><br />
<br />
<ccp:evaluate if="<%= User.getAccountSize() > -1 %>">
<table class="note" cellspacing="0">
<tr>
  <th>
    <img src="<%= ctx %>/images/icons/stock_about-16.gif" border="0" align="absmiddle" />
  </th>
  <td>
    <ccp:label name="projectsCenterFile.limit.message" param="<%= \"accountLimit=\" + User.getAccountSize() + \"|accountUsage=\" + User.getCurrentAccountSizeInMB() %>">
    Maintain your files by deleting older versions of the same file, and by deleting
    outdated or unused files.<br />
    This user account is limited to <%= User.getAccountSize() %> MB.<br />
    This account is currently using <%= User.getCurrentAccountSizeInMB() %> MB.
    </ccp:label>
  </td>
</tr>
</table>
<br />
</ccp:evaluate>
<input type="button" value="<ccp:label name="button.ok">OK</ccp:label>" onClick="window.location.href='<%= ctx %>/show/<%= project.getUniqueId() %>/folder/<%= request.getParameter("folderId") %>';">
