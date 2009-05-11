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
<%@ include file="initPage.jsp" %>
<jsp:useBean id="contact" class="com.concursive.connect.web.modules.contacts.dao.Contact" scope="request" />
<a href="<%= ctx %>/ContactsSearch.do?command=Form">Search</a> >
<a href="<%= ctx %>/ContactsSearch.do?command=Search">Results</a> >
Details<br />
<br />
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Contact Information
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td class="formLabel">
        First Name
      </td>
      <td>
        <%= toHtml(contact.getFirstName()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Middle Name
      </td>
      <td>
        <%= toHtml(contact.getMiddleName()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Last Name
      </td>
      <td>
        <%= toHtml(contact.getLastName()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Organization
      </td>
      <td>
        <%= toHtml(contact.getOrganization()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Job Title
      </td>
      <td>
        <%= toHtml(contact.getJobTitle()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Role
      </td>
      <td>
        <%= toHtml(contact.getRole()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Email
      </td>
      <td>
        <%= toHtml(contact.getEmailAsText()) %>
      </td>
    </tr>
    <tr class="containerBody">
      <td class="formLabel">
        Phone
      </td>
      <td>
        <%= toHtml(contact.getPhoneAsText()) %>
      </td>
    </tr>
  </tbody>
</table>
<input type="hidden" name="id" value="<%= contact.getId() %>" />
<input type="button" value="Modify" onClick="window.location.href='<%= ctx %>/Contacts.do?command=Modify&contactId=<%= contact.getId() %>'" />
<input type="button" value="Delete" onClick="confirmDelete('<%= ctx %>/Contacts.do?command=Delete&contactId=<%= contact.getId() %>');" />
