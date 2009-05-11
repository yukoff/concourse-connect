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
<%@ page import="com.concursive.connect.web.modules.contacts.dao.Contact" %>
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="contactListInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="contactList" class="com.concursive.connect.web.modules.contacts.dao.ContactList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="contacts_list_menu.jspf" %>
<%-- Preload image rollovers --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
<%-- dynamic checkboxes --%>
<a href="<%= ctx %>/ContactsSearch.do?command=Form">Search</a> > Results<br />
<br />
<img src="<%= ctx %>/images/icons/stock_bcard-16.gif" align="absmiddle" alt="" border="0" />
<a href="<%= ctx %>/Contacts.do?command=Add">Add a Contact</a><br />
<br />
<table class="pagedList">
  <thead>
    <tr>
      <th width="8">Action</th>
      <th align="left" width="100%">File As</th>
      <th align="left" nowrap>Email</th>
      <th align="left" nowrap>Phone</th>
    </tr>
  </thead>
  <tbody>
    <%
      if (contactList.size() == 0) {
    %>
      <tr class="row2">
        <td colspan="4">No contacts to display.</td>
      </tr>
    <%
      }
      int count = 0;
      int rowid = 0;
      Iterator i = contactList.iterator();
      while (i.hasNext()) {
        ++count;
        rowid = (rowid != 1 ? 1 : 2);
        Contact thisContact = (Contact) i.next();
    %>
      <tr class="row<%= rowid %>">
        <td valign="top" nowrap>
          <a href="javascript:displayMenu('select_<%= SKIN %><%= count %>', 'menuItem', <%= thisContact.getId() %>, '<%= thisContact.hasReadAccess(User) %>', '<%= thisContact.hasWriteAccess(User) %>');"
             onMouseOver="over(0, <%= count %>)"
             onmouseout="out(0, <%= count %>); hideMenu('menuItem');"><img
             src="<%= ctx %>/images/select_<%= SKIN %>.gif" name="select_<%= SKIN %><%= count %>" id="select_<%= SKIN %><%= count %>" align="absmiddle" border="0" /></a>
        </td>
        <td valign="top" nowrap>
          <a href="<%= ctx %>/Contacts.do?command=Details&contactId=<%= thisContact.getId() %>"><%= toHtml(thisContact.getFileAs()) %></a>
        </td>
        <td valign="top" nowrap>
          <%= toHtml(thisContact.getEmailAsText()) %>
        </td>
        <td valign="top" nowrap>
          <%= toHtml(thisContact.getPhoneAsText()) %>
        </td>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
<ccp:pagedListControl object="contactListInfo"/>
