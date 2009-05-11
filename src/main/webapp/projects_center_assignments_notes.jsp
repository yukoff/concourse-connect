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
<%@ page import="com.concursive.connect.web.modules.plans.dao.AssignmentNote" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="assignment" class="com.concursive.connect.web.modules.plans.dao.Assignment" scope="request"/>
<jsp:useBean id="assignmentNoteList" class="com.concursive.connect.web.modules.plans.dao.AssignmentNoteList" scope="request"/>
<%@ include file="initPage.jsp" %>
<table cellpadding="0" cellspacing="0" width="100%" border="0">
  <tr>
    <td>
      <ccp:label name="projectsCenterActivities.notes.message">The following notes have been entered for this activity:</ccp:label>
    </td>
  </tr>
</table>
<br />

<table class="pagedList">
  <thead>
    <tr>
      <th width="100%"><ccp:label name="projectsCenterActivities.notes.note">Note</ccp:label></th>
      <th><ccp:label name="projectsCenterActivities.notes.date">Date</ccp:label></th>
      <th><ccp:label name="projectsCenterActivities.notes.enteredBy">Entered By</ccp:label></th>
    </tr>
  </thead>
  <tbody>
    <%
      if (assignmentNoteList.size() == 0) {
    %>
      <tr class="row2">
        <td colspan="3"><ccp:label name="projectsCenterAssignments.notes.noNotesToDisplay">No notes to display.</ccp:label></td>
      </tr>
    <%
      }
      int rowid = 0;
      Iterator i = assignmentNoteList.iterator();
      while (i.hasNext()) {
        rowid = (rowid != 1?1:2);
        AssignmentNote thisNote = (AssignmentNote) i.next();
    %>
      <tr class="row<%= rowid %>">
        <td valign="top"><%= toHtml(thisNote.getDescription()) %></td>
        <td valign="top"><ccp:tz timestamp="<%= thisNote.getEntered() %>" default="&nbsp;" /></td>
        <td valign="top"><ccp:username id="<%= thisNote.getUserId() %>"/></td>
      </tr>
    <%
      }
    %>
  </tbody>
</table>
<ccp:evaluate if="<%= isPopup(request) %>">
<input type="button" value="<ccp:label name="button.close">Close</ccp:label>" onclick="window.close()">
</ccp:evaluate>