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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileFolder" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItem" class="com.concursive.connect.web.modules.documents.dao.FileItem" scope="request"/>
<jsp:useBean id="folderHierarchy" class="com.concursive.connect.web.modules.documents.dao.FileFolderHierarchy" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<h1>
  <ccp:label name="projectsCenterFile.hierarchy.selectFolder">Move <%= toHtml(fileItem.getSubject()) %> to:</ccp:label>
</h1>
<div class="portlet-section-body">
  <div>
    <img alt="" src="<%= ctx %>/images/tree7o.gif" border="0" align="absmiddle" height="16" width="19"/>
    <img alt="" src="<%= ctx %>/images/icons/stock_open-16-19.gif" border="0" align="absmiddle" height="16" width="19"/>
    <portlet:actionURL var="moveUrl">
      <portlet:param name="portlet-command" value="moveFile"/>
      <portlet:param name="newFolderId" value="-1"/>
    </portlet:actionURL>
    <a href="${moveUrl}"><ccp:label name="projectsCenterFile.hierarchy.home">Top Folder</ccp:label></a>
    <ccp:evaluate if="<%= fileItem.getFolderId() == -1 %>">
    <ccp:label name="projectsCenterFile.hierarchy.currentFolder">(current folder)</ccp:label>
    </ccp:evaluate>
  </div>
<%
  int rowid = 0;
  Iterator i = folderHierarchy.getHierarchy().iterator();
  while (i.hasNext()) {
    rowid = (rowid != 1?1:2);
    FileFolder thisFolder = (FileFolder) i.next();
    request.setAttribute("thisFolder", thisFolder);
%>
  <div>
    <% for(int j=1;j<thisFolder.getLevel();j++){ %><img border="0" src="<%= ctx %>/images/treespace.gif" align="absmiddle" height="16" width="19">&nbsp;<%}%><img border="0" src="<%= ctx %>/images/treespace.gif" align="absmiddle" height="16" width="19">
    <img alt="" src="<%= ctx %>/images/tree7o.gif" border="0" align="absmiddle" height="16" width="19"/>
    <img border="0" src="<%= ctx %>/images/icons/stock_open-16-19.gif" align="absmiddle">
    <portlet:actionURL var="moveUrl">
      <portlet:param name="portlet-command" value="moveFile"/>
      <portlet:param name="newFolderId" value="${thisFolder.id}"/>
    </portlet:actionURL>
    <a href="${moveUrl}"><%= toHtml(thisFolder.getSubject()) %></a>
    <ccp:evaluate if="<%= fileItem.getFolderId() == thisFolder.getId() %>">
    <ccp:label name="projectsCenterFile.hierarchy.currentFolder">(current folder)</ccp:label>
    </ccp:evaluate>
  </div>
<%
  }
%>
</div>
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
