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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.concursive.connect.web.modules.classifieds.dao.Classified" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItemList" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="classified" class="com.concursive.connect.web.modules.classifieds.dao.Classified" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<div class="portletWrapper">
  <ccp:evaluate if="<%= !isPopup(request) %>">
    <h1>
      <%--<img src="<%= ctx %>/images/icons/stock_announcement-16.gif" border="0" align="absmiddle">--%>
      <ccp:label name="projectsCenterClassifieds.details.details">Details</ccp:label>
      <a href="${ctx}/show/${classified.project.uniqueId}/classifieds">Back to <ccp:tabLabel name="Classifieds" object="project"/></a>
    </h1>
  </ccp:evaluate>
  <div class="listingContainer">
    <div class="listingHeaderContainer">
      <h3><%= toHtml(classified.getTitle()) %></h3>
      <div class="version">
        <ccp:evaluate if="<%= classified.getPublishDate() == null %>">
        	<ccp:label name="projectsCenterClassifieds.byClassified.draft">(Draft)</ccp:label>
        </ccp:evaluate>
            <ccp:evaluate if="<%= classified.isExpired() %>">
              <ccp:label name="classified.expired">Expired</ccp:label>
            </ccp:evaluate>
            <ccp:evaluate if="<%= classified.isScheduledInFuture() %>">
              <ccp:label name="classified.expired">Scheduled in future</ccp:label>
            </ccp:evaluate>
      </div>
      <div class="details">
          <ccp:evaluate if="<%= classified.getPublishDate() != null %>">
            <div>
              <ccp:label name="classified.starts">Starts</ccp:label>
              <ccp:tz timestamp="<%= classified.getPublishDate() %>" dateFormat="<%= DateFormat.LONG %>" />
            </div>
          </ccp:evaluate>
          <ccp:evaluate if="<%= classified.getExpirationDate() != null %>">
            <div>
              <ccp:label name="classified.expires">Expires</ccp:label>
              <ccp:tz timestamp="<%= classified.getExpirationDate() %>" dateFormat="<%= DateFormat.LONG %>" />
            </div>
          </ccp:evaluate>
           <div>
    	    	by <ccp:username id="<%= classified.getEnteredBy() %>"/>
	        </div>
      </div>
   	  <ccp:evaluate if="<%= classified.getId() > -1 && User.isLoggedIn() %>">
	   	  <div>
        [<a href="javascript:showPanel('Ask seller a question','${ctx}/show/${classified.project.uniqueId}/app/compose_message?module=classifieds&id=${classified.id}',700)">Ask seller a question</a>]
        </div>
      </ccp:evaluate>
      <ccp:evaluate if="<%= !isPopup(request) %>">
        <div class="permissions">
            <%-- edit --%>
            <ccp:evaluate if="<%= !classified.isExpired() %>">
              <portlet:renderURL var="updateUrl" portletMode="view">
                <portlet:param name="portlet-action" value="modify"/>
                <portlet:param name="portlet-object" value="classified-ad"/>
                <portlet:param name="portlet-value" value="${classified.id}"/>
              </portlet:renderURL>
              <ccp:evaluate if="<%= classified.getPublishDate() == null%>">
                <ccp:permission name="project-classifieds-add">
                 <a href="${updateUrl}"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.editThisItem">Edit this item</ccp:label>"></a>
                </ccp:permission>
              </ccp:evaluate>
              <ccp:evaluate if="<%= classified.getPublishDate() != null%>">
                <ccp:permission name="project-classifieds-admin">
                  <a href="${updateUrl}"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.editThisItem">Edit this item</ccp:label>"></a>
                </ccp:permission>
              </ccp:evaluate>
            </ccp:evaluate>
              <%-- clone --%>
            <ccp:permission name="project-classifieds-add">
              <portlet:actionURL var="cloneUrl" portletMode="view">
                <portlet:param name="portlet-value" value="${classified.id}"/>
                <portlet:param name="portlet-command" value="clone"/>
              </portlet:actionURL>
              <a href="javascript:confirmForward('${cloneUrl}');"><img src="<%= ctx %>/images/icons/stock_copy-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.makeACopyOfThisItem">Make a copy of this item</ccp:label>"></a>
            </ccp:permission>
            <%-- delete --%>
            <portlet:actionURL var="deleteUrl" portletMode="view">
              <portlet:param name="portlet-value" value="${classified.id}"/>
              <portlet:param name="portlet-command" value="delete"/>
            </portlet:actionURL>
            <ccp:evaluate if="<%= classified.getPublishDate() == null%>">
              <ccp:permission name="project-classifieds-add">
                <a href="javascript:confirmDelete('${deleteUrl}');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.deleteThisItem">Delete this item</ccp:label>"></a>
              </ccp:permission>
            </ccp:evaluate>
            <ccp:evaluate if="<%= classified.getPublishDate() != null%>">
              <ccp:permission name="project-classifieds-admin">
                <a href="javascript:confirmDelete('${deleteUrl}');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterClassifieds.byClassified.deleteThisItem">Delete this item</ccp:label>"></a>
              </ccp:permission>
            </ccp:evaluate>
        </div>
      </ccp:evaluate>
      <div class="ratings">
        <ccp:rating id='<%= classified.getId() %>'
                       showText='false'
                       count='<%= classified.getRatingCount() %>'
                       value='<%= classified.getRatingValue() %>'
                       url='<%= ctx + "/ProjectManagementClassifieds.do?command=SetRating&pid=" + classified.getProjectId() + "&id=" + classified.getId() + "&v=${vote}&out=text" %>'/>
       </div>
     </div>
     <div class="listingBodyContainer">
       <h4><%= toHtml(classified.getTitle()) %></h4>
       <ccp:evaluate if="<%= hasText(classified.getDescription()) %>">
          <p><%= toHtml(classified.getDescription()) %></p>
       </ccp:evaluate>
       <ccp:evaluate if="<%= !isPopup(request) %>">
         <%--<img src="<%= ctx %>/images/icons/stock_left-16.gif" border="0" align="absmiddle">--%>
         <a href="${ctx}/show/${classified.project.uniqueId}/classifieds"><ccp:label name="projectsCenterClassifieds.details.back">Back to list</ccp:label></a>
       </ccp:evaluate>
       <c:forEach var="fileItem" items="${classified.files}">
         <% FileItem thisFile = (FileItem)pageContext.getAttribute("fileItem"); %>
       <c:choose>
         <c:when test="<%= thisFile.isImageFormat() %>">
           <div<%-- Never put inline styles -- they ruin the cors CSS and make styling harder -- style="float:left; text-align:center;"--%>>
             <img <%--style="margin: 5px;"--%> src="<%= ctx %>/ProjectManagementClassifieds.do?command=Download&pid=${classified.project.id}&cid=${classified.id}&fid=${fileItem.id}&view=true&ext=${fileItem.extension}&size=<%=Classified.DEFAULT_IMAGE_WIDTH%>x<%=Classified.DEFAULT_IMAGE_HEIGHT%>"
                 alt="<c:out value='${fileItem.subject} - ${project.title}'/> image"
                 title="<c:out value='${fileItem.subject} - ${project.title}'/> image"/>
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
     <div class="attachments">
        <c:if test="${!empty nonImageList}">
        <h6><ccp:label name="classifieds.attachments">Attachments</ccp:label></h6>
        <ul>
        <c:forEach var="fileItem" items="${nonImageList}">
          <li>
           <a href="<%= ctx %>/ProjectManagementClassifieds.do?command=Download&pid=${classified.project.id}&cid=${classified.id}&fid=${fileItem.id}"><c:out value="${fileItem.clientFilename}"/></a>
          </li>
        </c:forEach>
        </ul>
        </c:if>
      </div>
    </div>
    <% pageContext.removeAttribute("nonImageList"); %>
  </div>
  <ccp:evaluate if="<%= isPopup(request) %>">
    <a name="button.close" onclick="window.close()" class="cancel"><ccp:label name="button.close">Close</ccp:label></a>
    <%--<input type="button" class="cancel" value="<ccp:label name="button.close">Close</ccp:label>" onclick="window.close()"/>--%>
  </ccp:evaluate>
</div>

