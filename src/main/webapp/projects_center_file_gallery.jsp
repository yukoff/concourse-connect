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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileFolder" %>
<link rel="stylesheet" href="<%= RequestUtils.getAbsoluteServerUrl(request) %>/css/iteam-images.css" type="text/css">
<jsp:useBean id="SKIN" class="java.lang.String" scope="application"/>
<jsp:useBean id="projectDocumentsGalleryInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="currentFolder" class="com.concursive.connect.web.modules.documents.dao.FileFolder" scope="request"/>
<jsp:useBean id="fileFolderList" class="com.concursive.connect.web.modules.documents.dao.FileFolderList" scope="request"/>
<%@ include file="initPage.jsp" %>
<%-- Initialize the drop-down menus --%>
<%@ include file="initPopupMenu.jsp" %>
<%@ include file="projects_center_files_menu.jspf" %>
<%-- Preload image rollovers for drop-down menu --%>
<script language="JavaScript" type="text/javascript">
  loadImages('select_<%= SKIN %>');
</script>
  <p>
    <%
      String actionError = showError(request, "actionError");
      boolean showDetails = ("true".equals(request.getParameter("details")));
    %>
  </p>
  <div class="g_menuContainer">
    <ul class="g_menuList">
      <ccp:permission name="project-documents-folders-add">
        <li><a href="<%= ctx %>/ProjectManagementFileFolders.do?command=Add&pid=<%= project.getId() %>&parentId=<%= fileItemList.getFolderId() %>&folderId=<%= fileItemList.getFolderId() %>"><ccp:label name="projectsCenterFile.gallery.newFolder">New Folder</ccp:label></a></li>
      </ccp:permission>
      <ccp:permission name="project-documents-files-upload">
        <li><a href="<%= ctx %>/ProjectManagementFiles.do?command=Add&pid=<%= project.getId() %>&folderId=<%= fileItemList.getFolderId() %>"><ccp:label name="projectsCenterFile.gallery.submitFile">Submit File</ccp:label></a></li>
      </ccp:permission>
      <ccp:evaluate if="<%= fileItemList.getFolderId() != -1 %>">
        <ccp:permission name="project-documents-folders-edit">
          <li><a href="<%= ctx %>/ProjectManagementFileFolders.do?command=Modify&pid=<%= project.getId() %>&folderId=<%= fileItemList.getFolderId() %>&id=<%= fileItemList.getFolderId() %>&parentId=<%= fileItemList.getFolderId() %>"><ccp:label name="projectsCenterFile.gallery.renameFolder">Rename Folder</ccp:label></a></li>
        </ccp:permission>
        <ccp:permission name="project-documents-folders-delete">
          <li><a href="javascript:confirmDelete('<%= ctx %>/ProjectManagementFileFolders.do?command=Delete&pid=<%= project.getId() %>&folderId=<%= currentFolder.getParentId() %>&id=<%= fileItemList.getFolderId() %>');"><ccp:label name="projectsCenterFile.gallery.deleteFolder">Delete Folder</ccp:label></a></li>
        </ccp:permission>
      </ccp:evaluate>
    </ul>
  </div>
  <%-- Temp. fix for Weblogic --%>
  <table class="pagedList">
    <thead>
      <tr>
        <th>
            <ccp:paginationControl object="projectDocumentsInfo"/>
        </th>
      </tr>
    </thead>
    <tbody>
    <%
      if (fileFolderList.size() == 0 && fileItemList.size() == 0) {
    %>
        <tr>
          <td class="ImageList" valign="center">
            <ccp:label name="projectsCenterFile.gallery.noFiles">No files to display.</ccp:label>
          </td>
        </tr>
    <%
      }
      int rowcount = 0;
      int count = 0;
      int multiplier = 3;
      if (fileFolderList.size() + fileItemList.size() == 4) {
        multiplier = 2;
      }
    %>
    <%-- Show the folders --%>
    <ccp:evaluate if="<%= !showDetails %>">
    <%
      Iterator i = fileFolderList.iterator();
      while (i.hasNext()) {
        FileFolder thisFolder = (FileFolder) i.next();
        ++count;
        if ((count + (multiplier - 1)) % multiplier == 0) {
          ++rowcount;
        }
    %>
    <ccp:evaluate exp="<%= (count + (multiplier - 1)) % multiplier == 0 %>">
      <tr>
    </ccp:evaluate>
        <td class="ImageList<%= (rowcount == 1?"":"AdditionalRow") %>">
          <span>
            <a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=File_<%= thisFolder.getDisplay() == -1?"Library":"Gallery" %>&pid=<%= project.getId() %>&folderId=<%= thisFolder.getId() %>"><img src="<%= ctx %>/images/stock_folder.gif" border="0" align="absmiddle"></a><br />
            <%= toHtml(thisFolder.getSubject()) %>
          </span>
        </td>
    <ccp:evaluate if="<%= count % multiplier == 0 %>">
      </tr>
    </ccp:evaluate>
    <%
        }
    %>
    </ccp:evaluate>
    <%-- Show the image(s) --%>
    <%
      int pagedItemCount = projectDocumentsGalleryInfo.getCurrentOffset() - 1;
      Iterator j = fileItemList.iterator();
      while (j.hasNext()) {
        FileItem thisItem = (FileItem) j.next();
        ++count;
        ++pagedItemCount;
        if ((count + (multiplier - 1)) % multiplier == 0) {
          ++rowcount;
        }
    %>
    <ccp:evaluate if="<%= (count + (multiplier - 1)) % multiplier == 0 %>">
      <tr>
    </ccp:evaluate>
        <td class="ImageList<%= (rowcount == 1?"":"AdditionalRow") %>">
          <span>
            <ccp:evaluate if="<%= !showDetails %>">
              <a href="<%= ctx %>/ProjectManagement.do?command=ProjectCenter&section=File_Gallery&pid=<%= project.getId() %>&folderId=<%= fileItemList.getFolderId() %>&details=true&offset=<%= pagedItemCount %>"><%= thisItem.getThumbnail(ctx) %></a><br />
            </ccp:evaluate>
            <ccp:evaluate if="<%= showDetails %>">
              <%= thisItem.getFullImage(ctx) %><br>
            </ccp:evaluate>
            <%= toHtml(thisItem.getSubject()) %>
          </span>
        </td>
    <ccp:evaluate if="<%= count % multiplier == 0 %>">
      </tr>
    </ccp:evaluate>
    <%
      }
    %>
    <ccp:evaluate if="<%= count % multiplier != 0 %>">
      </tr>
    </ccp:evaluate>
  </tbody>
</table>
