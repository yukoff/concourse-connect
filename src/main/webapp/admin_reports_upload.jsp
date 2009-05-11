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
<body onLoad="document.inputForm.title.focus();">
<script language="JavaScript" type="text/javascript">
  function checkFileForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";
    if (form.title.value == "") {
      messageText += "- Title is required\r\n";
      formTest = false;
    }
    if (formTest == false) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
      alert(messageText);
      return false;
    } else {
      if (form.upload.value != 'Please Wait...') {
        form.upload.value='Please Wait...';
        form.upload.disabled = true;
        return true;
      } else {
        return false;
      }
    }
  }
</script>
<%= showError(request, "actionError", false) %>
<form method="POST" name="inputForm" action="<%= ctx %>/AdminReports.do?command=Upload" enctype="multipart/form-data" onSubmit="return checkFileForm(this);">
<a href="<%= ctx %>/admin">System Administration</a> >
<a href="<%= ctx %>/AdminApplication.do">Manage Application Settings</a> >
<a href="<%= ctx %>/AdminReports.do?command=List">Reports</a> >
Upload a report<br />
<br />
<table class="pagedList">
  <thead>
    <tr>
      <th colspan="2">
        Parameters
      </th>
    </tr>
  </thead>
  <tbody>
    <tr class="containerBody">
      <td>
        Title
      </td>
      <td>
        <input type="text" name="title" value="" size="30" />
      </td>
    </tr>
    <tr class="containerBody">
      <td>
        Type
      </td>
      <td>
        <select name="type">
          <option value="user">User Report</option>
          <option value="admin">Admin Report</option>
        </select>
      </td>
    </tr>
    <tr class="containerBody">
      <td>
        Status
      </td>
      <td>
        Report will default to inactive
      </td>
    </tr>
    <tr class="containerBody">
      <td>
        File
      </td>
      <td>
        <input type="file" name="file" />
      </td>
    </tr>
  </tbody>
</table>
<input type="submit" value="Upload" name="upload">
<input type="button" value="Cancel" onclick="window.location.href='<%= ctx %>/AdminReports.do?command=List';">
<input type="hidden" name="dosubmit" value="true">
</form>
</body>