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
<%@ page import="com.concursive.connect.web.utils.CounterPair" %>
<%@ page import="com.concursive.connect.web.modules.classifieds.dao.Classified" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItemList" %>
<%@ page import="com.concursive.connect.web.modules.ModuleUtils" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="classifiedList" class="com.concursive.connect.web.modules.classifieds.dao.ClassifiedList" scope="request"/>
<jsp:useBean id="projectClassifiedsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="session"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
  <h1><ccp:tabLabel name="Classifieds" object="project"/></h1>
  <c:if test="${!empty actionError}"><%= showError(request, "actionError") %></c:if>
  <c:if test="${empty classifiedList}">
    <ccp:label name="projectsCenterAds.noAds"><p>There are currently no classifieds.</p></ccp:label>
  </c:if>
<%
  Iterator i = classifiedList.iterator();
  while (i.hasNext()) {
    Classified thisClassified = (Classified) i.next();
    request.setAttribute("thisClassified", thisClassified);
%>
    <div class="listingContainer">
      <div class="listingHeaderContainer">
        <h3><%= toHtml(thisClassified.getTitle()) %></h3>
        <div class="version">
          <span class="red">
            <ccp:evaluate if="<%= thisClassified.getPublishDate() == null %>">
              <ccp:label name="classified.unpublished">Unpublished</ccp:label>
            </ccp:evaluate>
            <ccp:evaluate if="<%= thisClassified.isExpired() %>">
              <ccp:label name="classified.expired">Expired</ccp:label>
            </ccp:evaluate>
            <ccp:evaluate if="<%= thisClassified.isScheduledInFuture() %>">
              <ccp:label name="classified.expired">Scheduled in future</ccp:label>
            </ccp:evaluate>
          </span>
        </div>
        <div class="details">
          <ccp:evaluate if="<%= thisClassified.getPublishDate() != null %>">
            <div>
              <ccp:label name="classified.starts">Starts</ccp:label>
              <ccp:tz timestamp="<%= thisClassified.getPublishDate() %>" dateFormat="<%= DateFormat.LONG %>" />
            </div>
          </ccp:evaluate>
          <ccp:evaluate if="<%= thisClassified.getExpirationDate() != null %>">
            <div>
              <ccp:label name="classified.expires">Expires</ccp:label>
              <ccp:tz timestamp="<%= thisClassified.getExpirationDate() %>" dateFormat="<%= DateFormat.LONG %>" />
              <ccp:permission name="project-classifieds-admin">
                <ccp:evaluate if="<%= !thisClassified.isExpired() %>">
                  <portlet:actionURL var="expireUrl" portletMode="view">
                    <portlet:param name="portlet-value" value="${thisClassified.id}"/>
                    <portlet:param name="portlet-command" value="expire"/>
                  </portlet:actionURL>
                  [<a href="javascript:confirmDelete('${expireUrl}');">Expire Now</a>]
                </ccp:evaluate>
              </ccp:permission>
            </div>
          </ccp:evaluate>
          <ccp:permission name="project-classifieds-admin">
            <div>
              by <ccp:username id="<%= thisClassified.getEnteredBy() %>" />
            </div>
          </ccp:permission>
          <ccp:evaluate if="<%= !thisClassified.isExpired() %>">
            <ccp:evaluate if="<%= thisClassified.getId() > -1 && User.isLoggedIn() %>">
              <div>
              [<a href="javascript:showPanel('Ask seller a question','${ctx}/show/${project.uniqueId}/app/compose_message?module=classifieds&id=${thisClassified.id}',700)">Ask seller a question</a>]
              </div>
            </ccp:evaluate>
          </ccp:evaluate>
        </div>
        <ccp:permission name="project-classifieds-add,project-classifieds-add" if="any">
          <div class="permissions">
            <%-- edit --%>
            <ccp:evaluate if="<%= !thisClassified.isExpired() %>">
              <portlet:renderURL var="updateUrl" portletMode="view">
                <portlet:param name="portlet-action" value="modify"/>
                <portlet:param name="portlet-object" value="classified-ad"/>
                <portlet:param name="portlet-value" value="${thisClassified.id}"/>
              </portlet:renderURL>
              <ccp:evaluate if="<%= thisClassified.getPublishDate() == null%>">
                <ccp:permission name="project-classifieds-add">
                 <a href="${updateUrl}"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.editThisItem">Edit this item</ccp:label>"></a>
                </ccp:permission>
              </ccp:evaluate>
              <ccp:evaluate if="<%= thisClassified.getPublishDate() != null%>">
                <ccp:permission name="project-classifieds-admin">
                  <a href="${updateUrl}"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.editThisItem">Edit this item</ccp:label>"></a>
                </ccp:permission>
              </ccp:evaluate>
            </ccp:evaluate>
            <ccp:permission name="project-classifieds-add">
              <%-- clone --%>
              <portlet:actionURL var="cloneUrl" portletMode="view">
                <portlet:param name="portlet-value" value="${thisClassified.id}"/>
                <portlet:param name="portlet-command" value="clone"/>
              </portlet:actionURL>
              <a href="javascript:confirmForward('${cloneUrl}');"><img src="<%= ctx %>/images/icons/stock_copy-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.makeACopyOfThisItem">Make a copy of this item</ccp:label>"></a>
            </ccp:permission>
            <%-- delete --%>
            <portlet:actionURL var="deleteUrl" portletMode="view">
              <portlet:param name="portlet-value" value="${thisClassified.id}"/>
              <portlet:param name="portlet-command" value="delete"/>
            </portlet:actionURL>
            <ccp:evaluate if="<%= thisClassified.getPublishDate() == null%>">
              <ccp:permission name="project-classifieds-add">
                <a href="javascript:confirmDelete('${deleteUrl}');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.deleteThisItem">Delete this item</ccp:label>"></a>
              </ccp:permission>
            </ccp:evaluate>
            <ccp:evaluate if="<%= thisClassified.getPublishDate() != null%>">
              <ccp:permission name="project-classifieds-admin">
                <a href="javascript:confirmDelete('${deleteUrl}');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.deleteThisItem">Delete this item</ccp:label>"></a>
              </ccp:permission>
            </ccp:evaluate>
          </div>
        </ccp:permission>
        <div class="ratings">
          <portlet:renderURL var="ratingUrl" windowState="maximized">
            <portlet:param name="portlet-action" value="show"/>
            <portlet:param name="portlet-object" value="classifieds"/>
            <portlet:param name="portlet-value" value="${thisClassified.id}"/>
            <portlet:param name="portlet-command" value="setRating"/>
            <portlet:param name="v" value="\${vote}"/>
            <portlet:param name="out" value="text"/>
          </portlet:renderURL>
          <ccp:rating id='${thisClassified.id}'
             showText='false'
             count='${thisClassified.ratingCount}'
             value='${thisClassified.ratingValue}'
             url='${ratingUrl}'/>
        </div>
      </div>
      <div class="listingBodyContainer">
        <p><%= toHtml(thisClassified.getDescription()) %></p>
        <div class="attachments">
          <c:forEach var="fileItem" items="${thisClassified.files}">
            <% FileItem thisFile = (FileItem)pageContext.getAttribute("fileItem"); %>
            <c:choose>
             <c:when test="<%= thisFile.isImageFormat() %>">
              <div<%-- MOVED TO CSS style="float:left; text-align:center;"--%>>
                <img <%--style="margin: 5px;"  --%>
                     src="<%= ctx %>/ProjectManagementClassifieds.do?command=Download&pid=<%= project.getId() %>&cid=${thisClassified.id}&fid=${fileItem.id}&view=true&ext=${fileItem.extension}&size=<%=Classified.DEFAULT_IMAGE_WIDTH%>x<%=Classified.DEFAULT_IMAGE_HEIGHT%>"
                     alt="<c:out value='${fileItem.subject} - ${project.title}'/> image" title="<c:out value='${fileItem.subject} - ${project.title}'/> image"/>
                <ccp:permission name="project-classifieds-add">
                  <br/><a href="javascript:confirmDelete('<%= ctx %>/ProjectManagementClassifieds.do?command=FileDelete&pid=<%= project.getId()%>&fid=${fileItem.id}&cid=${thisClassified.id}')"><em>(Delete?)</em></a>
                </ccp:permission>
              </div>
             </c:when>
             <c:otherwise>
              <% //show all non image links beneath images
                FileItemList niList = (FileItemList)pageContext.getAttribute("nonImageList");
                if(niList == null) niList = new FileItemList();
                niList.add(thisFile);
                pageContext.setAttribute("nonImageList", niList);
                %>
             </c:otherwise>
            </c:choose>
          </c:forEach>
          <c:if test="${!empty nonImageList}">
          <h6><ccp:label name="classifieds.attachments">Attachments</ccp:label></h6>
          <ul>
            <c:forEach var="fileItem" items="${nonImageList}">
              <li>
                <%-- todo
                <portlet:renderURL var="downloadUrl">
                  <portlet:param name="portlet-action" value="download"/>
                  <portlet:param name="portlet-object" value="classified-ad-file"/>
                  <portlet:param name="portlet-params" value="${fileItem.id}"/>
                </portlet:renderURL>
                --%>
               <a href="<%= ctx %>/ProjectManagementClassifieds.do?command=Download&pid=<%= project.getId() %>&cid=${thisClassified.id}&fid=${fileItem.id}"><c:out value="${fileItem.clientFilename}"/></a>
               <ccp:permission name="project-classifieds-add">
                 <a href="javascript:confirmDelete('<%= ctx %>/ProjectManagementClassifieds.do?command=FileDelete&pid=<%= project.getId()%>&fid=${fileItem.id}&cid=${thisClassified.id}')"><em>(Delete?)</em></a>
               </ccp:permission>
              </li>
            </c:forEach>
          </ul>
        </c:if>
        <span class="tagList">
      		<portlet:renderURL var="setTagsUrl" windowState="maximized">
      			<portlet:param name="portlet-action" value="modify"/>
        		<portlet:param name="portlet-command" value="setTags" />
        		<portlet:param name="portlet-object" value="<%= ModuleUtils.MODULENAME_CLASSIFIEDS %>"/>
        		<portlet:param name="portlet-value" value="${thisClassified.id}"/>
        		<portlet:param name="popup" value="true" />
	      	</portlet:renderURL>
  	    	<ccp:tags url="${setTagsUrl}" />
    		</span>
        </div>
      </div>
      <% pageContext.removeAttribute("nonImageList"); %>
    </div>
<%
  }
%>
  <c:if test="${projectClassifiedsInfo.numberOfPages > 1}">
    <div class="pagination">
      <ccp:paginationControl object="projectClassifiedsInfo"/>
    </div>
  </c:if>