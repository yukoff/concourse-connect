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
<jsp:useBean id="user" class="com.concursive.connect.web.modules.login.dao.User" scope="request"/>
<%@ include file="initPage.jsp" %>
<form name="register" method="post" action="<%= ctx %>/register">
<%= showError(request, "actionError", false) %>
<table cellpadding="0" cellspacing="4" width="100%">
  <tr>
<%-- Left Side --%>
    <td width="100%" valign="top">
<table class="pagedList">
  <thead>
    <tr>
      <th>
        <img src="<%= ctx %>/images/error.gif" border="0" align="absmiddle" /> Invitation Error
      </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        Hello <ccp:username id="<%= user.getId() %>"/>,<br />
        <br />
        You have chosen to accept an invitation to the community, however the profile sent either
        does not exist anymore, or the invitation was retracted by the owner.
        Contact the sender of the invitation for more information.<br />
        <br />
        Even though the original invitation no longer exists, you can still register
        for an account and participate in the community.<br />
        <br />
        Registration is simple.  Click "Continue" and fill out the information that appears
        on the following page.<br />
        <br />
        <input type="hidden" name="data" value="<%= request.getParameter("data") %>"/>
        <input type="image" src="<%= ctx %>/images/buttons/continue-green.gif" systran="yes" border="0" alt="Continue" name="Continue" value="Continue" />
      </td>
    </tr>
  </tbody>
</table>
</td>
  </tr>
</table>
</form>
