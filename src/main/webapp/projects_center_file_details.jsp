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
<%@ page
    import="com.concursive.connect.web.modules.documents.dao.FileItemVersion" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItem" class="com.concursive.connect.web.modules.documents.dao.FileItem" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
  <div class="profile-portlet-header">
    <h1>
      <ccp:label name="projectsCenterFile.details.fileVersions">File Versions</ccp:label> for <c:out value="${fileItem.subject}"/>
    </h1>
  </div>

  <ccp:permission name="project-documents-files-upload">
    <portlet:renderURL var="uploadUrl">
      <portlet:param name="portlet-action" value="create"/>
      <portlet:param name="portlet-object" value="file-version"/>
      <portlet:param name="portlet-value" value="${fileItem.id}"/>
      <portlet:param name="folder" value="${fileItem.folderId}"/>
    </portlet:renderURL>
    <div class="profile-portlet-menu">
      <ul>
         <li class="first">
           <c:choose>
            <c:when test="${empty currentFolder || currentFolder.id == -1}">
              <portlet:renderURL var="documentsUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="documents"/>
              </portlet:renderURL>
              <span><a href="${documentsUrl}">Back to <ccp:tabLabel name="Documents" object="project"/></a></span>
            </c:when>
            <c:otherwise>
              <portlet:renderURL var="documentsUrl">
                <portlet:param name="portlet-action" value="show"/>
                <portlet:param name="portlet-object" value="folder"/>
                <portlet:param name="portlet-value" value="${currentFolder.id}"/>
              </portlet:renderURL>
              <span><a href="${documentsUrl}">Back to <c:out value="${currentFolder.subject}"/></a></span>
            </c:otherwise>
          </c:choose>
         </li>
        <li class="last">
          <img src="<%= ctx %>/images/icons/documents_plus.png"/>
          <a href="${uploadUrl}"><ccp:label name="projectsCenterFile.details.addVersion">Add Version</ccp:label></a>
        </li>
      </ul>
    </div>
  </ccp:permission>

  <ccp:permission name="project-documents-files-download" if="none">
    <ccp:evaluate if="<%= !User.isLoggedIn() && project.getFeatures().getAllowGuests() %>">
      <div class="portlet-message-alert">
        <p><ccp:label name="projectsCenterfiles.details.needLogin">You need to be logged in to download files.</ccp:label></p>
      </div>
    </ccp:evaluate>
  </ccp:permission>

  <%--
    <ccp:label name="projectsCenterFile.details.actions">Action</ccp:label>
    <ccp:label name="projectsCenterFile.details.file">File</ccp:label>
    <ccp:label name="projcetsCenterFiles.details.size">Size</ccp:label>
    <ccp:label name="projcetsCenterFiles.details.version">Version</ccp:label>
    <ccp:label name="projcetsCenterFiles.details.submitted">Submitted</ccp:label>
    <ccp:label name="projectsCenterFile.details.sendBy">Sent By</ccp:label>
    <ccp:label name="projectsCenterFile.details.dl">D/L</ccp:label>
  --%>
  <div class="portlet-section">
    <ol>
      <%
        int rowid = 0;
        Iterator versionList = fileItem.getVersionList().iterator();
        while (versionList.hasNext()) {
          rowid = (rowid != 1?1:2);
          FileItemVersion thisVersion = (FileItemVersion)versionList.next();
          request.setAttribute("thisVersion", thisVersion);
      %>
        <li>
          <div class="portlet-section-body">
            <%= thisVersion.getImageTag("-23", ctx) %>
            <h2>
              <c:out value="${thisVersion.clientFilename}"/>
              <ccp:permission name="project-documents-files-download">
                <span>
                  <portlet:renderURL var="downloadUrl">
                    <portlet:param name="portlet-action" value="download"/>
                    <portlet:param name="portlet-object" value="file"/>
                    <portlet:param name="portlet-value" value="${thisVersion.id}"/>
                    <portlet:param name="portlet-params" value="${thisVersion.version}"/>
                  </portlet:renderURL>
                  <img src="${ctx}/images/icons/get_16x16.png" alt="download icon"/>
                  <a href="${downloadUrl}">download</a>
                </span>
              </ccp:permission>
            </h2>

            <dl>
              <dt>File Size:</dt>
              <dd><%= thisVersion.getRelativeSize() %>k&nbsp;</dd>
              <dt>File Version:</dt>
              <dd><%= thisVersion.getVersion() %>&nbsp;</dd>
              <dt>Created: </dt>
              <dd><ccp:tz timestamp="<%= thisVersion.getEntered() %>"/> by <ccp:username id="<%= thisVersion.getEnteredBy() %>"/></dd>
              <dt>Downloads:</dt>
              <dd><%= thisVersion.getDownloads() %></dd>
              <dt>Subject:</dt>
              <dd><%= toHtml(thisVersion.getSubject()) %></dd>
              <ccp:evaluate if="<%= hasText(thisVersion.getComment()) %>">
                <dt>
                  <ccp:label name="projectsCenterFile.details.changes">Changes:</ccp:label>
                </dt>
                <dd>
                  <%= toHtml(thisVersion.getComment()) %>
                </dd>
              </ccp:evaluate>
            </dl>
          </div>
          <ccp:permission name="project-documents-files-download,project-documents-files-delete,project-documents-files-download">
            <div class="portlet-section-menu">
              <ccp:permission name="project-documents-files-download">
                <div class="portlet-section-body-menu">
                  <p>
                    <%-- Download --%>
                    <portlet:renderURL var="downloadUrl">
                      <portlet:param name="portlet-action" value="download"/>
                      <portlet:param name="portlet-object" value="file"/>
                      <portlet:param name="portlet-value" value="${thisVersion.id}"/>
                      <portlet:param name="portlet-params" value="${thisVersion.version}"/>
                    </portlet:renderURL>
                    <a href="${downloadUrl}">
                      <img src="${ctx}/images/icons/get_32x32.png" alt="Download icon"/>
                      <ccp:label name="projectsCenterFile.details.download">download</ccp:label>
                      <span><%= thisVersion.getRelativeSize() %>k&nbsp;</span>
                    </a>
                  </p>
                </div>
              </ccp:permission>
              <ul>
                <ccp:permission name="project-documents-files-download">
                  <li>
                    <%-- View --%>
                    <portlet:renderURL var="streamUrl">
                      <portlet:param name="portlet-action" value="stream"/>
                      <portlet:param name="portlet-object" value="file"/>
                      <portlet:param name="portlet-value" value="${thisVersion.id}"/>
                      <portlet:param name="portlet-params" value="${thisVersion.version}"/>
                    </portlet:renderURL>
                    <img src="<%= ctx %>/images/icons/magnifier_search.png" alt="Zoom icon"/>
                    <a href="javascript:popURL('${streamUrl}','Content_<%= fileItem.getId() %>-<%= thisVersion.getVersion() %>',700,580,1,1);">
                      <ccp:label name="projectsCenterFile.details.openInNewWindow">open in new window</ccp:label>
                    </a>
                  </li>
                </ccp:permission>
                <ccp:permission name="project-documents-files-delete">
                  <li>
                    <%-- Delete --%>
                    <portlet:actionURL var="deleteUrl">
                      <portlet:param name="portlet-value" value="${fileItem.id}"/>
                      <portlet:param name="portlet-params" value="<%= String.valueOf(thisVersion.getVersion()) %>"/>
                      <portlet:param name="portlet-command" value="deleteFile"/>
                    </portlet:actionURL>
                    <img src="${ctx}/images/icons/minus_circle.png" alt="Delete document icon"/>
                    <a href="javascript:confirmDelete('${deleteUrl}');">
                      delete
                    </a>
                  </li>
                </ccp:permission>
              </ul>
            </div>
          </ccp:permission>
        </li>
      <%
        }
      %>
    </ol>
