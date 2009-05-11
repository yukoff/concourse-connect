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
<jsp:useBean id="messageTemplate" class="com.concursive.connect.web.modules.communications.dao.MessageTemplate" scope="request" />
<jsp:useBean id="projectCategoryList" class="com.concursive.connect.web.modules.profile.dao.ProjectCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminApplication.do">Manage Application Settings</a> >
<a href="<%= ctx %>/AdminProjectMessageTemplates.do?command=List">Project Message Templates</a> >
Project Message Template Details
<br /><br />
<script type="text/javascript" language="JavaScript">
</script>
  <table cellpadding="4" cellspacing="0" width="100%" class="pagedList">
    <thead>
      <tr>
        <th colspan="2">
         Project Message Template Information
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel">Site Category</td>
      <td>
        <%= projectCategoryList.getValueFromId(messageTemplate.getProjectCategoryId()) %>
      </td>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="messageTemplate.title">Title</ccp:label></td>
        <td>
          <%= toHtmlValue(messageTemplate.getTitle()) %>
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="messageTemplate.subject">Subject</ccp:label></td>
        <td>
          <%=toHtmlValue(messageTemplate.getSubject()) %>"
        </td>
      </tr>
      <tr class="containerBody">
        <td nowrap class="formLabel" valign="top"><ccp:label name="messageTemplate.content">Content</ccp:label></td>
        <td>
          <%= toString(messageTemplate.getContent()) %>
        </td>
      </tr>
    </tbody>
  </table>
