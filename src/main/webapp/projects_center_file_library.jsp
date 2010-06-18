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
<%@ page import="com.concursive.commons.files.FileUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.web.modules.ModuleUtils" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="folderList" class="com.concursive.connect.web.modules.documents.dao.FileFolderList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<script type="text/javascript">
  function goToFolder<portlet:namespace/>(folderId) {
    if (folderId == -1) {
      <portlet:renderURL var="jsDocumentsUrl">
        <portlet:param name="portlet-action" value="show"/>
        <portlet:param name="portlet-object" value="documents"/>
      </portlet:renderURL>
      window.location.href="${jsDocumentsUrl}";
    } else {
      <%-- Navigate to the folder --%>
      <portlet:renderURL var="jsFolderUrl">
        <portlet:param name="portlet-action" value="show"/>
        <portlet:param name="portlet-object" value="folder"/>
        <portlet:param name="portlet-value" value="{folderId}"/>
      </portlet:renderURL>
      var url = "${jsFolderUrl}".replace("{folderId}", folderId);
      window.location.href=url;
    }
  }
  function goToFolderDate<portlet:namespace/>(date) {
    <portlet:renderURL var="filterUrl">
      <portlet:param name="portlet-action" value="show"/>
      <portlet:param name="portlet-object" value="documents"/>
      <portlet:param name="portlet-value" value="date"/>
      <portlet:param name="portlet-params" value="{date}"/>
      <c:if test="${!empty param.view}">
        <portlet:param name="view" value="${param.view}"/>
      </c:if>
    </portlet:renderURL>
    var url = "${filterUrl}".replace("{date}", date);
    window.location.href=url;
  }
</script>
<div id="message" class="portlet-message-info menu">
  <p>Thank you for your valuable feedback.</p>
</div>
<div class="profile-portlet-header">
  <%-- The module name or folder name --%>
  <c:choose>
    <c:when test="${empty currentFolder || currentFolder.id == -1}">
      <c:if test="${!empty title}">
        <h2><c:out value="${title}"/></h2>
      </c:if>
      <%--<h2><ccp:tabLabel name="Documents" object="project"/></h2>--%>
    </c:when>
    <c:otherwise>
      <h2>
        <c:out value="${currentFolder.subject}"/>
        <portlet:renderURL var="documentsUrl">
          <portlet:param name="portlet-action" value="show"/>
          <portlet:param name="portlet-object" value="documents"/>
        </portlet:renderURL>
      </h2>
    </c:otherwise>
  </c:choose>
</div>
<%-- The status of the contents --%>
<div class="profile-portlet-menu">
  <ul>
    <c:if test="${!empty currentFolder && currentFolder.id != -1}">
      <li class="first <c:if test="${empty fileItemList}">last</c:if>">
        <span><a href="${documentsUrl}">Back to all documents</a></span>
      </li>
      <ccp:permission name="project-documents-folders-edit">
        <%-- Modify Folder --%>
        <portlet:renderURL var="modifyUrl">
          <portlet:param name="portlet-command" value="folderForm"/>
          <portlet:param name="portlet-action" value="modify"/>
          <portlet:param name="portlet-object" value="folder"/>
          <portlet:param name="portlet-value" value="${currentFolder.id}"/>
        </portlet:renderURL>
        <li><a href="${modifyUrl}"><ccp:label name="projectsCenterFile.library.modifyFolder">Modify Folder</ccp:label></a></li>
        <%-- Move Folder --%>
        <portlet:renderURL var="moveFolderUrl">
          <portlet:param name="portlet-command" value="moveFolder"/>
          <portlet:param name="portlet-action" value="set"/>
          <portlet:param name="portlet-object" value="folder"/>
          <portlet:param name="portlet-value" value="${currentFolder.id}"/>
        </portlet:renderURL>
        <li><a href="${moveFolderUrl}">Move Folder</a></li>
      </ccp:permission>
      <ccp:permission name="project-documents-folders-delete">
        <%-- Delete Folder --%>
        <portlet:actionURL var="deleteUrl">
          <portlet:param name="portlet-value" value="${currentFolder.id}"/>
          <portlet:param name="portlet-command" value="deleteFolder"/>
        </portlet:actionURL>
        <li <c:if test="${empty fileItemList}"> class="last" </c:if>> <a href="javascript:confirmDelete('${deleteUrl}');"><ccp:label name="projectsCenterFile.library.deleteFolder">Delete Folder</ccp:label></a></li>
      </ccp:permission>
    </c:if>
    <c:if test="${!empty fileItemList}">
      <portlet:renderURL var="sortByRecentUrl">
        <portlet:param name="portlet-action" value="show"/>
        <c:if test="${!empty currentFolder}">
          <portlet:param name="portlet-value" value="${currentFolder.id}"/>
        </c:if>
        <c:if test="${!empty currentDate}">
          <portlet:param name="portlet-value" value="date"/>
          <portlet:param name="portlet-params" value="${currentDate}"/>
        </c:if>
      </portlet:renderURL>
      <portlet:renderURL var="sortByAlphabeticalUrl">
        <portlet:param name="portlet-action" value="show"/>
        <c:if test="${!empty currentFolder}">
          <portlet:param name="portlet-value" value="${currentFolder.id}"/>
        </c:if>
        <c:if test="${!empty currentDate}">
          <portlet:param name="portlet-value" value="date"/>
          <portlet:param name="portlet-params" value="${currentDate}"/>
        </c:if>
        <portlet:param name="view" value="a-z"/>
      </portlet:renderURL>
      <li class="last">
        Sort by
        <a href="${sortByRecentUrl}">most recent</a> or
        <a href="${sortByAlphabeticalUrl}">alphabetical</a>
      </li>
    </c:if>
  </ul>
  <c:choose>
    <c:when test="${empty fileItemList}">
      <c:choose>
        <c:when test="${empty currentFolder}">
          <div class="portlet-message-info">
            <p>There are currently no files to display.</p>
          </div>
        </c:when>
        <c:otherwise>
          <div class="portlet-message-info">
            <p>This folder is empty.</p>
          </div>
        </c:otherwise>
      </c:choose>
    </c:when>
  </c:choose>
</div>
<ccp:permission name="project-documents-files-download" if="none">
  <ccp:evaluate if="<%= !User.isLoggedIn() && project.getFeatures().getAllowGuests() %>">
    <div class="portlet-message-info"><ccp:label name="projectsCenterFile.library.needLogin">You need to be logged in to download files.</ccp:label></div>
  </ccp:evaluate>
</ccp:permission>
<%-- The file listing --%>
<c:if test="${!empty fileItemList}">
  <div class="portlet-section">
    <ol>
      <%
        int rowid = 0;
        for (FileItem thisFile : fileItemList) {
          request.setAttribute("thisFile", thisFile);
          rowid = (rowid != 1?1:2);
      %>
        <li>
          <div class="portlet-section-body">
            <%-- view details --%>
            <portlet:renderURL var="detailsUrl">
              <portlet:param name="portlet-command" value="details"/>
              <portlet:param name="portlet-action" value="show"/>
              <portlet:param name="portlet-object" value="file"/>
              <portlet:param name="portlet-value" value="${thisFile.id}"/>
            </portlet:renderURL>
            <%-- download or stream --%>
            <ccp:permission name="project-documents-files-download">
              <c:set var="downloadUrl" value="${ctx}/download/${project.uniqueId}/file/${thisFile.id}/0" />
              <%--
              <portlet:renderURL var="downloadUrl">
                <portlet:param name="portlet-action" value="download"/>
                <portlet:param name="portlet-object" value="file"/>
                <portlet:param name="portlet-value" value="${thisFile.id}"/>
                <portlet:param name="portlet-params" value="0"/>
              </portlet:renderURL>
              --%>
              <c:set var="streamUrl" value="${ctx}/stream/${project.uniqueId}/file/${thisFile.id}" />
              <%--
              <portlet:renderURL var="streamUrl">
                <portlet:param name="portlet-action" value="stream"/>
                <portlet:param name="portlet-object" value="file"/>
                <portlet:param name="portlet-value" value="${thisFile.id}"/>
              </portlet:renderURL>
              --%>
            </ccp:permission>
            <%-- Show the file --%>
            <h3>
              <a href="${detailsUrl}"><c:out value="${thisFile.subject}"/></a>
              <c:if test="${thisFile.version > 1.0}">v${thisFile.version}</c:if>
            </h3>
            <p>
              <%-- show some info about the file --%>
              by <ccp:username id="${thisFile.modifiedBy}"/>
              <c:if test="${thisFile.folderId > -1}">
                <portlet:renderURL var="folderUrl">
                  <portlet:param name="portlet-action" value="show"/>
                  <portlet:param name="portlet-object" value="folder"/>
                  <portlet:param name="portlet-value" value="${thisFile.folderId}"/>
                </portlet:renderURL>
                in <a href="${folderUrl}"><c:out value="<%= folderList.getFolder(thisFile.getFolderId()).getSubject() %>"/></a>
              </c:if>
            </p>
            <%-- show the comment if there is one --%>
            <c:if test="${!empty thisFile.comment}">
              <p>&raquo; <c:out value="${thisFile.comment}"/></p>
            </c:if>
            <%-- Download --%>
            <ccp:permission name="project-documents-files-download">
              <div class="portlet-section-body-menu">
                <p>
                  <a href="${downloadUrl}/<c:out value="${thisFile.clientFilename}"/>"><img src="${ctx}/images/icons/get_32x32.png" alt="Download icon"/>
                    download
                    <span><%= toHtml(thisFile.getExtension()) %> (${thisFile.relativeSize}k)</span>
                  </a>
                </p>
                <c:if test="${!empty streamUrl && thisFile.imageFormat}">
                  <p><a href="${streamUrl}?ext=<c:out value="${thisFile.extension}"/>" rel="shadowbox[Images]"><img src="<%= ctx %>/images/icons/magnifier_search_32x32.png" alt="Zoom icon"/>
                    view</a></p>
                </c:if>
              </div>
            </ccp:permission>
            <%-- Show the tags --%>
            <span class="tagList">
              <portlet:renderURL var="setTagsUrl" windowState="maximized">
                <portlet:param name="portlet-command" value="setTags" />
                <portlet:param name="portlet-object" value="<%= ModuleUtils.MODULENAME_DOCUMENTS %>"/>
                <portlet:param name="portlet-value" value="${thisFile.id}"/>
                <portlet:param name="popup" value="true" />
              </portlet:renderURL>
              <ccp:tags url="${setTagsUrl}" />
            </span>
          </div>
          <div class="portlet-section-menu">
            <ul>
              <%-- add version --%>
              <ccp:permission name="project-documents-files-upload">
                <li>
                  <portlet:renderURL var="createUrl">
                    <portlet:param name="portlet-command" value="fileForm"/>
                    <portlet:param name="portlet-action" value="create"/>
                    <portlet:param name="portlet-object" value="file-version"/>
                    <portlet:param name="portlet-value" value="${thisFile.id}"/>
                  </portlet:renderURL>
                  <a href="${createUrl}"><img src="${ctx}/images/icons/document_plus.png" alt="Add document icon"/> add version</a>
                </li>
              </ccp:permission>
              <%-- move --%>
              <ccp:permission name="project-documents-files-rename">
                <li>
                  <portlet:renderURL var="moveUrl">
                    <portlet:param name="portlet-command" value="moveFile"/>
                    <portlet:param name="portlet-action" value="set"/>
                    <portlet:param name="portlet-object" value="file"/>
                    <portlet:param name="portlet-value" value="${thisFile.id}"/>
                  </portlet:renderURL>
                  <a href="${moveUrl}"><img src="${ctx}/images/icons/folder_arrow.png" alt="Move document icon"/> move</a>
                </li>
              </ccp:permission>
              <%-- edit --%>
              <ccp:permission name="project-documents-files-rename">
                <li>
                  <portlet:renderURL var="modifyUrl">
                    <portlet:param name="portlet-command" value="fileForm"/>
                    <portlet:param name="portlet-action" value="modify"/>
                    <portlet:param name="portlet-object" value="file"/>
                    <portlet:param name="portlet-value" value="${thisFile.id}"/>
                  </portlet:renderURL>
                  <a href="${modifyUrl}"><img src="${ctx}/images/icons/pencil.png" alt="Modify document icon"/>  modify</a>
                </li>
              </ccp:permission>
              <%-- delete --%>
              <ccp:permission name="project-documents-files-delete">
                <li>
                  <portlet:actionURL var="deleteUrl">
                    <portlet:param name="portlet-value" value="${thisFile.id}"/>
                    <portlet:param name="portlet-command" value="deleteFile"/>
                  </portlet:actionURL>
                  <a href="javascript:confirmDelete('${deleteUrl}');"><img src="${ctx}/images/icons/minus_circle.png" alt="Delete document icon"/> delete</a>
                </li>
              </ccp:permission>
            </ul>
          </div>
          <ccp:evaluate if="<%= thisFile.getRatingCount() > 0 %>">
            <div class="userInputFooter">
              <p>(<%= thisFile.getRatingValue() %> out of <%= thisFile.getRatingCount() %> <%= thisFile.getRatingCount() == 1 ? " person" : " people"%> found this document useful.)</p>
            </div>
          </ccp:evaluate>
          <ccp:evaluate if="<%= thisFile.getInappropriateCount() > 0 && ProjectUtils.hasAccess(project.getId(), User, \"project-documents-files-upload\")%>">
            <div class="userInputFooter">
              <p>(<%= thisFile.getInappropriateCount() %><%= thisFile.getInappropriateCount() == 1? " person" : " people"%> found this document inappropriate.)</p>
            </div>
          </ccp:evaluate>
          <%-- any user who is not the author of the document can mark the document as useful  --%>
          <ccp:permission name="project-documents-files-download">
            <ccp:evaluate if="<%= (thisFile.getEnteredBy() != User.getId())  && User.isLoggedIn() %>">
              <div class="userInputFooter">
                <p>Is this document useful?</p>
                <p>
                  <portlet:renderURL var="ratingUrl" windowState="maximized">
                    <portlet:param name="portlet-command" value="setRating"/>
                    <portlet:param name="id" value="${thisFile.id}"/>
                    <portlet:param name="v" value="1"/>
                    <portlet:param name="out" value="text"/>
                  </portlet:renderURL>
                  <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + thisFile.getId() %>','message');">Yes</a>&nbsp;
                  <portlet:renderURL var="ratingUrl" windowState="maximized">
                    <portlet:param name="portlet-command" value="setRating"/>
                    <portlet:param name="id" value="${thisFile.id}"/>
                    <portlet:param name="v" value="0"/>
                    <portlet:param name="out" value="text"/>
                  </portlet:renderURL>
                  <a href="javascript:copyRequest('${ratingUrl}','<%= "message_" + thisFile.getId() %>','message');">No</a>&nbsp;
                  <ccp:evaluate if="<%= thisFile.getId() > -1 && User.isLoggedIn() %>">
                    <a href="javascript:showPanel('Mark this document post as Inappropriate','${ctx}/show/${project.uniqueId}/app/report_inappropriate?module=documents&pid=${project.id}&id=${thisFile.id}',700)">Report this as inappropriate</a>
                  </ccp:evaluate>
                </p>
                <div id="message_<%= thisFile.getId() %>"></div>
              </div>
            </ccp:evaluate>
          </ccp:permission>
      </li>
      <%
        }
      %>
    </ol>
  </div>
</c:if>
