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
<script language="JavaScript" type="text/javascript">
  var pid="";
  <ccp:evaluate if="<%= request.getParameter(\"pid\") != null %>">
    pid = "&pid=<%= request.getParameter("pid") %>";
  </ccp:evaluate>
  var lmid="&lmid=<%= request.getParameter("lmid") %>";
  var liid="&liid=<%= request.getParameter("liid") %>";
  var selectorId="&selectorId=<%= request.getParameter("selectorId") %>";
  var selectorMode="&selectorMode=<%= selectorMode %>";

  <ccp:evaluate if="<%= fileItemList.size() > 0 %>">
  window.opener.setAttachmentList('<%= fileItemList.getValueListing() %>');
  window.opener.setAttachmentText('<%= fileItemList.getTextListing() %>');
  </ccp:evaluate>

  function checkFileForm(form) {
    if (form.id<%= request.getParameter("pid") %>.value.length < 5) {
      alert("A file must be selected before choosing Attach");
      return false;
    } else {
      if (form.attach.value != 'Please Wait...') {
        form.attach.value='Please Wait...';
        form.attach.disabled = true;
        hideSpan('fileListing');
        showSpan('progressBar');
        return true;
      } else {
        return false;
      }
    }
  }
      
  function removeItem() {
    var sel = document.forms['inputForm'].elements['selFileItemList'];
    if (sel.selectedIndex > -1) {
      var value = sel.options[sel.selectedIndex].value;
      var removeButton = document.forms['inputForm'].elements['remove'];
      if (removeButton.value != 'Please Wait...') {
          removeButton.value='Please Wait...';
          removeButton.disabled = true;
          hideSpan('fileListing');
          showSpan('progressBar');
          window.location.href=
            "FileAttachments.do?command=Remove" + pid + lmid + liid + selectorId + "&fid=" + value + selectorMode + '&popup=true';
      }
    } else {
      alert("An item must be selected before choosing Remove");
    }
  }

  function finish() {
    window.close();
  }

  <ccp:evaluate if="<%= \"single\".equals(selectorMode) && \"true\".equals(added) %>">
    finish();
  </ccp:evaluate>

</script>
<%= showError(request, "actionError", false) %>
<c:set var="counter" value="${1}" />
<div class="formContainer">
  <form method="POST" name="inputForm" action="<%= ctx %>/FileAttachments.do?command=Attach<ccp:evaluate if="<%= request.getParameter(\"pid\") != null %>">&pid=<%= request.getParameter("pid") %></ccp:evaluate>&lmid=<%= request.getParameter("lmid") %>&liid=<%= request.getParameter("liid") %>&selectorId=<%= request.getParameter("selectorId") %>&selectorMode=<%= selectorMode %>&added=true&popup=true" enctype="multipart/form-data" onSubmit="try {return checkFileForm(this);}catch(e){return true;}">
    <fieldset id="fileListing">
      <legend><ccp:label name="fileAttach.title">File Attachments</ccp:label></legend>
      <label>
        ${counter}. <ccp:label name="fileAttach.selectFile">Choose the file you want to attach...</ccp:label>
      </label>
      <c:set var="counter" value="${counter+1}"/>
      <input type="file" name="id<%= request.getParameter("pid") %>" size="45"/>
      <ccp:evaluate if="<%= request.getParameter(\"pid\") != null %>">
        <input type="hidden" name="pid" value="<%= request.getParameter("pid") %>" />
      </ccp:evaluate>
      <input type="hidden" name="lmid" value="<%= request.getParameter("lmid") %>" />
      <input type="hidden" name="liid" value="<%= request.getParameter("liid") %>" />
      <input type="hidden" name="selectorId" value="<%= request.getParameter("selectorId") %>" />
      <input type="hidden" name="selectorMode" value="<%= selectorMode %>" />
      <input type="hidden" name="added" value="true" />
      <input type="hidden" name="popup" value="true" />
      <c:if test="${param.allowCaption == 'true'}">
        <label>${counter}. <ccp:label name="fileAttach.setCaption">Enter a caption to be displayed</ccp:label></label>
        <c:set var="counter" value="${counter+1}"/>
        <input type="text" name="comment" id="comment" value="${param.caption}" maxlength="500" />
        <span class="characterCounter">500 characters max</span>
      </c:if>
      <p>
        <input type="submit" value="Attach..." name="attach" class="submit" />
        <ccp:evaluate if="<%= \"single\".equals(selectorMode)  %>">
          <c:if test="${'true' eq param.popup || 'true' eq popup}">
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:if>
        </ccp:evaluate>
      </p>
      <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
      <ccp:evaluate if="<%= !\"single\".equals(selectorMode)  %>">
          <p>${counter}. <ccp:label name="fileAttach.repeatForMoreAttachments">Repeat steps 1 and 2 to add more attachments.</ccp:label></p>
          <c:set var="counter" value="${counter+1}"/>
          <label><ccp:label name="fileAttach.howToRemoveAttachment">To remove an attachment, select it below and choose Remove.</ccp:label></label>
          <%= fileItemList.getHtml("selFileItemList", 0) %>
          <input type="button" name="remove" value="Remove" onClick="removeItem();" class="cancel" />
<%
  long newSize = 0;
  if (fileItemList.size() > 0) {
    newSize = (Long.parseLong(fileSize) / 1000);
    if (newSize == 0) {
      newSize = 1;
    }
  }
%>
          <label>Total:</label>
          <span><%= newSize %>KB</span>
          <label>${counter}. Choose Finish to attach the files.</label>
          <c:set var="counter" value="${counter+1}"/>
          <input type="button" value="Finish" onClick="finish();" class="submit" />
      </ccp:evaluate>
    </fieldset>
    <fieldset id="progressBar" style="display:none">
      <legend>Please Wait</legend>
      <img src="<%= ctx %>/images/loading32.gif" alt="loading..." />
      <span><ccp:label name="fileAttach.largeFileWarning">Large files may take a while to upload.</ccp:label></span>
      <span><ccp:label name="fileAttach.pleaseWaitForConfirmation">Please wait for confirmation message before continuing.</ccp:label></span>
    </fieldset>
  </form>
</div>
