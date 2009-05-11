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
<jsp:useBean id="fileFolder" class="com.concursive.connect.web.modules.documents.dao.FileFolder" scope="request"/>
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
      messageText += "- Name is required\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      if (form.save.value != 'Please Wait...') {
        form.save.value='Please Wait...';
        form.save.disabled = true;
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
    <portlet:actionURL var="saveFolderUrl">
      <portlet:param name="portlet-command" value="saveFolder"/>
    </portlet:actionURL>
    <form method="POST" name="inputForm" action="${saveFolderUrl}" onSubmit="try {return checkForm(this);}catch(e){return true;}">
      <%= showError(request, "actionError") %>
      <fieldset id="editFolder">
        <legend><%= (fileFolder.getId() > -1 ? "Rename" : "New" ) %> <ccp:label name="projcetsCenterFiles.folder.folder">Folder</ccp:label></legend>
        <%= showAttribute(request, "subjectError") %>
        <label for="subject"><%= (fileFolder.getId() > -1 ? "Rename" : "New" ) %> <ccp:label name="projcetsCenterFiles.folder.folder">Folder <span class="required">*</span></ccp:label></label>
        <input type="text" name="subject" id="subject" size="59" maxlength="255" value="<%= toHtmlValue(fileFolder.getSubject()) %>" />
        <span class="characterCounter">255 characters max</span>
        <%--
        <label for="display"><ccp:label name="projectsCenterFile.folder.type.iconLayout">Icon Layout</ccp:label></label>
        <select size="1" name="display" id="display">
          <option value="-1" <%= fileFolder.getDisplay() == -1 ? "selected" : "" %>><ccp:label name="projectsCenterFile.folder.type.listView">List View</ccp:label></option>
          <option value="1" <%= fileFolder.getDisplay() == 1 ? "selected" : "" %>><ccp:label name="projectsCenterFile.folder.type.imageView">Image View</ccp:label></option>
          <option value="2" <%= fileFolder.getDisplay() == 2 ? "selected" : "" %>><ccp:label name="projectsCenterFile.folder.type.slideshow">Slideshow View</ccp:label></option>
        </select>
        --%>
      </fieldset>
      <input type="hidden" name="modified" value="<%= fileFolder.getModified() %>">
      <input type="hidden" name="id" value="<%= fileFolder.getId() %>">
      <input type="hidden" name="parentId" value="<%= fileFolder.getParentId() %>">
      <input type="submit" class="submit" value="<ccp:label name="button.save">Save</ccp:label>" name="save">
      <c:choose>
        <%-- close the popup --%>
        <c:when test="${'true' eq param.popup || 'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
        </c:when>
        <c:otherwise>
          <portlet:renderURL var="cancelUrl">
            <portlet:param name="portlet-action" value="show"/>
            <c:choose>
              <%-- the folder is being added --%>
              <c:when test="${fileFolder.id == -1}">
                <c:choose>
                  <c:when test="${fileFolder.parentId == -1}">
                    <portlet:param name="portlet-object" value="documents"/>
                  </c:when>
                  <c:otherwise>
                    <portlet:param name="portlet-object" value="folder"/>
                    <portlet:param name="portlet-value" value="${fileFolder.parentId}"/>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <%-- the folder is being modified --%>
              <c:otherwise>
                <portlet:param name="portlet-object" value="folder"/>
                <portlet:param name="portlet-value" value="${fileFolder.id}"/>
              </c:otherwise>
            </c:choose>
          </portlet:renderURL>
          <a href="${cancelUrl}" class="cancel">Cancel</a>
        </c:otherwise>
      </c:choose>
    </form>
  </div>
</div>
