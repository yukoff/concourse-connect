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
<%@ include file="initPage.jsp" %>
<form name="user" method="post" action="<%= ctx %>/Profile.do?command=SaveContactInformation&auto-populate=true">
<%= showError(request, "actionError", false) %>
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        <ccp:label name="userProfile.contactInformation.contactInformation">Contact Information</ccp:label>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="userProfile.contactInformation.firstName">First Name</ccp:label></td>
      <td>
        <input type="text" name="firstName" value="<%= toHtmlValue(User.getFirstName()) %>"/>
        <font color="red">*</font>
        <%= showAttribute(request, "firstNameError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="userProfile.contactInformation.lastName">Last Name</ccp:label></td>
      <td>
        <input type="text" name="lastName" value="<%= toHtmlValue(User.getLastName()) %>"/>
        <font color="red">*</font>
        <%= showAttribute(request, "lastNameError") %>
      </td>
    </tr>
    <tr class="containerBody">
      <td nowrap class="formLabel"><ccp:label name="userProfile.contactInformation.organisation">Organization</ccp:label></td>
      <td>
        <input type="text" name="company" value="<%= toHtmlValue(User.getCompany()) %>"/>
      </td>
    </tr>
  </tbody>
</table>
<input type="submit" value="<ccp:label name="button.update">Update</ccp:label>"/>
<input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onclick="window.location.href='<%= ctx %>/Profile.do'"/>
</form>
