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
<jsp:useBean id="Requirement" class="com.concursive.connect.web.modules.plans.dao.Requirement" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript">
  function checkFileForm(form) {
    var formTest = true;
    var messageText = "";
    if (form.file.value.length < 5) {
      messageText += "- File is required\r\n";
      formTest = false;
    }
    if (formTest == false) {
      messageText = "The file could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
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
<form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementRequirements.do?command=Import&pid=<%= project.getId() %>&rid=<%= Requirement.getId() %>" enctype="multipart/form-data" onSubmit="return checkFileForm(this);">
  <ccp:label name="projectsCenterRequirements.import.message">
  A project plan can be imported from either a Microsoft Excel spreadsheet or
  from an Omni Outliner template.
  </ccp:label><br />
  <br />
  <table class="pagedList">
    <thead>
      <tr>
        <th colspan="2" align="left">
            <ccp:label name="projectsCenterRequirements.import.uploadFile">Upload File</ccp:label>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="containerBody">
        <td nowrap class="formLabel"><ccp:label name="projectsCenterRequirements.import.file">File</ccp:label></td>
        <td>
          <input type="file" name="file" size="45">
        </td>
      </tr>
    </tbody>
  </table>
  <p align="center">
    <ccp:label name="projectsCenterRequirements.import.largeFileMessage">
      Large files may take a while to upload.<br /> Please wait for confirmation message before continuing.
    </ccp:label>
  </p>
  <input type="submit" value="<ccp:label name="button.upload">Upload</ccp:label>" name="upload">
  <input type="hidden" name="pid" value="<%= project.getId() %>">
  <input type="hidden" name="rid" value="<%= Requirement.getId() %>">
</form>

