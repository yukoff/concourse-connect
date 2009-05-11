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
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.commons.files.FileUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="folderList" class="com.concursive.connect.web.modules.documents.dao.FileFolderList" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
  <c:if test="${!empty title}">
    <h3><c:out value="${title}"/></h3>
  </c:if>
  <%-- The module name or folder name --%>
    <c:choose>
      <c:when test="${empty fileItemList}">
        <div>
          There are currently no files to display, please check back soon.
        </div>
      </c:when>
      <c:otherwise>
  <%-- The file listing --%>
	    <ol>
      <%
        int rowid = 0;
        for (FileItem thisFile : fileItemList) {
          request.setAttribute("thisFile", thisFile);
          rowid = (rowid != 1?1:2);
      %>
      	<li>
           <%= thisFile.getImageTag("", ctx) %>
            <%-- download --%>
	        <ccp:evaluate if="<%= !User.isLoggedIn() %>">
              <h4><c:out value="${thisFile.subject}"/></h4>
	        </ccp:evaluate>
	        <ccp:evaluate if="<%= User.isLoggedIn() %>">
              <h4><a href="${ctx}/download/${thisFile.project.uniqueId}/file/${thisFile.id}/${thisFile.version}/${thisFile.clientFilename}"><c:out value="${thisFile.subject}"/></a></h4>
	        </ccp:evaluate>
          <%-- show the comment if there is one --%>
          <c:if test="${!empty thisFile.comment}">
            <p><c:out value="${thisFile.comment}"/></p>
          </c:if>
          <%-- show some info about the file --%>
          <p>by <ccp:username id="${thisFile.modifiedBy}"/>
          <c:if test="${thisFile.folderId > -1 && !empty folderList}">
            in <a href="${ctx}/show/${thisFile.project.uniqueId}/folder/${thisFile.folderId}"><c:out value="<%= folderList.getFolder(thisFile.getFolderId()).getSubject() %>"/></a>
          </c:if>
          ${thisFile.relativeSize}k</p>
      <%
        }
      %>
      </ol>
      <c:if test="${showAllDownloadsLink eq 'true'}">
       <a href="${ctx}/show/${project.uniqueId}/documents" class="more">see all downloads</a>
      </c:if>
  </c:otherwise>
  </c:choose>
