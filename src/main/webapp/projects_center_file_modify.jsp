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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItem" class="com.concursive.connect.web.modules.documents.dao.FileItem" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<script type="text/javascript" language="JavaScript">
  <%-- Onload --%>
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.subject.focus(); });
  <%-- Validations --%>
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    if (form.subject.value == "") {
      messageText += "- Subject is required\r\n";
      formTest = false;
    }
    if ((form.clientFilename.value) == "") {
      messageText += "- Filename is required\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The file information could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
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
  <h1><%= fileItem.getSubject() %></h1>
  <div class="formContainer">
    <portlet:actionURL var="saveFormUrl">
      <portlet:param name="portlet-command" value="saveFile"/>
    </portlet:actionURL>
    <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="try {return checkForm(this);}catch(e){return true;}">
      <%= showError(request, "actionError") %>
      <fieldset>
        <legend><ccp:label name="projectsCenterFile.modify.modifyFileInformation">Modify File Information</ccp:label></legend>
        <%-- subject --%>
        <label for="subject"><ccp:label name="projectsCenterFile.modify.subject">Subject</ccp:label> <span class="required">*</span></label>
        <%= showAttribute(request, "subjectError") %>
        <input type="text" name="subject" id="subject" size="59" maxlength="255" value="<%= fileItem.getSubject() %>" />
        <span class="characterCounter">255 characters max</span>
        <%-- client filename --%>
        <label for="clientFilename"><ccp:label name="projectsCenterFile.modify.fileName">File Name</ccp:label> <span class="required">*</span></label>
        <%= showAttribute(request, "clientFilenameError") %>
        <input type="text" name="clientFilename" id="clientFilename" size="59" maxlength="255" value="<%= fileItem.getClientFilename() %>" />
        <span class="characterCounter">255 characters max</span>
        <%-- versions --%>
        <label><ccp:label name="projectsCenterFile.modify.currentVersion">Current Version</ccp:label></label>
        <%= fileItem.getVersion() %><br />
        <br />
        <%-- comment --%>
        <label for="comment"><ccp:label name="projectsCenterFile.modify.summaryOfChanges">Summary of changes</ccp:label></label>
        <input type="text" name="comment" id="comment" size="59" maxlength="500" value="<%= toHtmlValue(fileItem.getComment()) %>" />
        <span class="characterCounter">500 characters max</span>
        <%-- featured file  --%>
        <label for="featuredFile"><ccp:label name="projectsCenterFile.upload.featuredFile">Featured File</ccp:label></label>
        <input type="checkbox" name="featuredFile" id="featuredFile" <%= fileItem.getFeaturedFile()? " checked":"" %>/>
      </fieldset>
      <input type="submit" class="submit" value="<ccp:label name="button.update">Update</ccp:label>" name="update">
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
      <input type="hidden" name="modified" value="<%= fileItem.getModified() %>">
      <input type="hidden" name="defaultFile" value="<%= fileItem.getDefaultFile() %>">
      <input type="hidden" name="folderId" value="<%= fileItem.getFolderId() %>">
      <input type="hidden" name="id" value="<%= fileItem.getId() %>">
    </form>
  </div>
</div>
