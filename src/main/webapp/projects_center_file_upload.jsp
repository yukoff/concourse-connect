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
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItem" class="com.concursive.connect.web.modules.documents.dao.FileItem" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<script type="text/javascript" language="JavaScript">
  <%-- Onload --%>
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.subject.focus(); });
  <%-- Validations --%>
  function checkFileForm(form) {
    var formTest = true;
    var messageText = "";
    if (form.subject.value == "") {
      messageText += "- Subject is required\r\n";
      formTest = false;
    }
    if (form.id<%= project.getId() %>.value.length < 5) {
      messageText += "- File is required\r\n";
      formTest = false;
    }
    if (!formTest) {
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
<div class="portletWrapper">
  <c:choose>
    <c:when test="${empty currentFolder || currentFolder.id == -1}">
      <h1><ccp:tabLabel name="Documents" object="project"/></h1>
    </c:when>
    <c:otherwise>
      <h1><c:out value="${currentFolder.subject}"/></h1>
    </c:otherwise>
  </c:choose>
  <div class="formContainer">
    <form method="POST" name="inputForm" action="<%= ctx %>/ProjectManagementFiles.do?command=Upload&pid=<%= project.getId() %>&folderId=${currentFolderId}" enctype="multipart/form-data" onSubmit="return checkFileForm(this);">
      <%= showError(request, "actionError") %>
      <ccp:evaluate if="<%= User.getAccountSize() > -1 %>">
        <%--<img src="<%= ctx %>/images/icons/stock_about-16.gif" border="0" align="absmiddle" />--%>
        <ccp:label name="projectsCenterFile.limit.message" param="<%= \"accountLimit=\" + User.getAccountSize() + \"|accountUsage=\" + User.getCurrentAccountSizeInMB() %>">
          <p>Maintain your files by deleting older versions of the same file, and by deleting
          outdated or unused files.</p>
          <p>This user account is limited to <%= User.getAccountSize() %> MB.</p>
          <p>This account is currently using <%= User.getCurrentAccountSizeInMB() %> MB.</p>
        </ccp:label>
      </ccp:evaluate>
      <fieldset id="">
        <legend>
          <ccp:evaluate if="<%= fileItem.getId() == -1 %>">
            <ccp:label name="projectsCenterFile.upload.uploadFile">Upload File</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= fileItem.getId() > 0 %>">
            <ccp:label name="projectsCenterFile.upload.addFileRevision">Add Revision to</ccp:label>
            &ldquo;<c:out value="${fileItem.subject}"/>&rdquo;
          </ccp:evaluate>
        </legend>
        <%-- subject --%>
        <label for="subject"><ccp:label name="projectsCenterFile.upload.subject">Subject</ccp:label> <span class="required">*</span></label>
        <%= showAttribute(request, "subjectError") %>
        <input type="text" name="subject" id="subject" size="59" maxlength="255" value="<%= toHtmlValue(fileItem.getSubject()) %>" />
        <span class="characterCounter">255 characters max</span>
        <%-- versions --%>
        <ccp:evaluate if="<%= fileItem.getId() > 0 %>">
          <fieldset>
            <legend><ccp:label name="projectsCenterFile.upload.currentVersion">Current Version:</ccp:label> <strong><%= fileItem.getVersion() %></strong></legend>
            <input type="radio" value="<%= fileItem.getVersionNextMajor() %>" checked name="versionId"><ccp:label name="projectsCenterFile.upload.majorUpdate">Major Update</ccp:label> <%= fileItem.getVersionNextMajor() %>
            <input type="radio" value="<%= fileItem.getVersionNextMinor() %>" name="versionId"><ccp:label name="projectsCenterFile.upload.minorUpdate">Minor Update</ccp:label> <%= fileItem.getVersionNextMinor() %>
            <input type="radio" value="<%= fileItem.getVersionNextChanges() %>" name="versionId"><ccp:label name="projectsCenterFile.upload.changes">Changes</ccp:label> <%= fileItem.getVersionNextChanges() %>
          </fieldset>
        </ccp:evaluate>
        <%-- comment --%>
        <label for="comment"><ccp:label name="projectsCenterFile.upload.summaryOfChanges">Summary of changes</ccp:label></label>
        <input type="text" name="comment" id="comment" size="59" maxlength="500">
        <span class="characterCounter">500 characters max</span>
        <%-- featured file  --%>
        <fieldset>
          <input type="checkbox" name="featuredFile" id="featuredFile" value="true" />
          <label for="featuredFile"><ccp:label name="projectsCenterFile.upload.featuredFile">Featured File</ccp:label></label>
        </fieldset>
        <%-- file upload --%>
        <label for="id<%= project.getId() %>"><ccp:label name="projectsCenterFile.upload.file">File</ccp:label> <span class="required">*</span></label>
        <input type="file" name="id<%= project.getId() %>" size="45">
      </fieldset>
      <p align="center">
        <ccp:label name="projectsCenterFile.upload.bigFileHint">
         Large files may take a while to upload.<br /> Please wait for confirmation message before continuing.
        </ccp:label>
      </p>
      <input type="submit" class="submit" value="<ccp:label name="button.upload">Upload</ccp:label>" name="upload">
      <c:choose>
        <c:when test="${'true' eq param.popup || 'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
        </c:when>
        <c:otherwise>
          <portlet:renderURL var="cancelUrl">
            <portlet:param name="portlet-action" value="show"/>
            <c:choose>
              <c:when test="${fileItem.folderId == -1}">
                <portlet:param name="portlet-object" value="documents"/>
              </c:when>
              <c:otherwise>
                <portlet:param name="portlet-object" value="folder"/>
                <portlet:param name="portlet-value" value="${fileItem.folderId}"/>
              </c:otherwise>
            </c:choose>
          </portlet:renderURL>
          <a href="${cancelUrl}" class="cancel">Cancel</a>
        </c:otherwise>
      </c:choose>
      <input type="hidden" name="folderId" value="${currentFolderId}">
      <input type="hidden" name="pid" value="<%= project.getId() %>">
      <input type="hidden" name="fid" value="<%= fileItem.getId() %>">
    </form>
  </div>
</div>

