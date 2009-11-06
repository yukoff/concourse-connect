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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="fileSize" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  fileItemList.setSelectSize(10);
  fileItemList.setSelectStyle("width: 300px");
  String selectorMode = request.getParameter("selectorMode");
  String added = request.getParameter("added");
%>
<%= showError(request, "actionError", false) %>
<div class="formContainer">
  <form method="POST" name="inputForm" action="<%= ctx %>/FileAttachments.do?command=Attach<ccp:evaluate if='<%= request.getParameter("pid") != null %>'>&pid=<%= StringUtils.encodeUrl(request.getParameter("pid")) %></ccp:evaluate>&lmid=<%= StringUtils.encodeUrl(request.getParameter("lmid")) %>&liid=<%= StringUtils.encodeUrl(request.getParameter("liid")) %>&selectorId=<%= StringUtils.encodeUrl(request.getParameter("selectorId")) %>&selectorMode=<%= selectorMode %>&added=true&out=text" enctype="multipart/form-data">
    <fieldset id="fileListing">
      <legend><ccp:label name="fileAttach.title">File Attachments</ccp:label></legend>
      <c:choose>
        <c:when test="${!empty param.added}">
          <div class="portlet-message-success">
            File added.
            <ccp:evaluate if='<%= !"single".equals(selectorMode)  %>'>
              Add another?<br />
              <input type="file" name="id<%= toHtmlValue(request.getParameter("pid")) %>" id="id<%= toHtmlValue(request.getParameter("pid")) %>" size="45" onChange="panel.submit();"/>
            </ccp:evaluate>
          </div>
        </c:when>
        <c:otherwise>
          <p>
            <label>
              <ccp:label name="fileAttach.selectFile">Choose the file you want to attach...</ccp:label>
              <ccp:evaluate if='<%= !"single".equals(selectorMode)  %>'>
                You can add more files after this one.
              </ccp:evaluate>
            </label>
              <input type="file" name="id<%= toHtmlValue(request.getParameter("pid")) %>" id="id<%= toHtmlValue(request.getParameter("pid")) %>" size="45" <c:if test="${param.allowCaption != 'true'}">onChange="panel.submit();"</c:if>/>
          </p>
        </c:otherwise>
      </c:choose>
      <ccp:evaluate if='<%= request.getParameter("pid") != null %>'>
        <input type="hidden" name="pid" value="<%= toHtmlValue(request.getParameter("pid")) %>" />
      </ccp:evaluate>
      <input type="hidden" name="lmid" value="<%= toHtmlValue(request.getParameter("lmid")) %>" />
      <input type="hidden" name="liid" value="<%= toHtmlValue(request.getParameter("liid")) %>" />
      <input type="hidden" name="selectorId" value="<%= toHtmlValue(request.getParameter("selectorId")) %>" />
      <input type="hidden" name="selectorMode" value="<%= toHtmlValue(selectorMode) %>" />
      <input type="hidden" name="added" value="true" />
      <input type="hidden" name="popup" value="true" />
      <c:if test="${param.allowCaption == 'true'}">
        <p>
          <label><ccp:label name="fileAttach.setCaption">Enter a caption to be displayed</ccp:label></label>
          <input type="text" name="comment" id="comment" value="<%= toHtmlValue(request.getParameter("caption")) %>" maxlength="500" />
          <span class="characterCounter">500 characters max</span>
        </p>
      </c:if>
      <ccp:evaluate if='<%= "single".equals(selectorMode)  %>'>
        <c:if test="${'true' eq param.popup || 'true' eq popup}">
          <p>
            <c:if test="${param.allowCaption == 'true'}">
              <input type="button" value="Submit" class="submit" id="panelSubmitButton" onClick="panel.submit();"/>
            </c:if>
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton" />
          </p>
        </c:if>
      </ccp:evaluate>
      <fieldset id="progressBar" style="display:none" class="submitSpinner">
        <div class="portlet-message-info">
          <legend>Please Wait</legend>
          <img src="<%= ctx %>/images/loading16.gif" alt="loading..." />
          <span><ccp:label name="fileAttach.largeFileWarning">Large files may take awhile to upload.</ccp:label></span>
          <span><ccp:label name="fileAttach.pleaseWaitForConfirmation">Please wait for a confirmation message before continuing.</ccp:label></span>
        </div>
      </fieldset>
      <ccp:evaluate if='<%= !"single".equals(selectorMode)  %>'>
<%
  long newSize = 0;
  if (fileItemList.size() > 0) {
    newSize = (Long.parseLong(fileSize) / 1000);
    if (newSize == 0) {
      newSize = 1;
    }
  }
%>
        <c:if test="<%= fileItemList.size() > 0 %>">
          <label>Added Attachments: (<%= newSize %> KB)</label><br />
          <%= fileItemList.getHtml("selFileItemList", 0) %><br />
        </c:if>
        <p>
          <c:if test="<%= fileItemList.size() > 0 %>">
            <input type="button" value="Finish" class="submit" id="panelCloseButton" onclick="setAttachmentList('<%= fileItemList.getValueListing() %>');setAttachmentText('<%= fileItemList.getTextListing() %>');panel.cancel();"/>
          </c:if>
          <input type="button" value="Cancel" class="submit" id="panelCloseButton" onclick="panel.cancel()"/>
        </p>
      </ccp:evaluate>
    </fieldset>
  </form>
</div>
