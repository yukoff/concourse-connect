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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.*" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<%@ page import="com.concursive.connect.config.ApplicationPrefs" %>
<%@ page import="com.concursive.connect.Constants" %>
<jsp:useBean id="mainProfile" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" TYPE="text/javascript" SRC="javascript/scriptUtils.js"></script>
<script language="JavaScript" type="text/javascript">
  function clearForm() {
    document.forms['editForm'].title.value="";
    document.forms['editForm'].shortDescription.value="";
    document.forms['editForm'].attachmentText.value="";
    document.forms['editForm'].attachmentList.value="";
    document.forms['editForm'].theme.value="";
    document.forms['editForm'].colorScheme.value="";
    
    document.forms['editForm'].title.focus();
  }

  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
    document.getElementById("isTempImage").value = "true";
    setWebLogo(newVal);
  }  
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }
  function setTheme(val){
    document.forms['editForm'].theme.value=val;
    document.forms['editForm'].colorScheme.value="";
  }
  function setColorScheme(colorValue){
  	  var theme = document.forms['editForm'].theme.value;
  	  var newStyle = teamelements_ctx + "/themes/" + theme + "/color-schemes/" + colorValue + "/css/ccp-color.css";
  	  replacejscssfile("ccp-color.css", newStyle, "css");
      document.forms['editForm'].colorScheme.value=colorValue;
  }

  function setWebPageTitle(titleFieldId){
  	var titleValue = document.forms['editForm'].title.value;
	var contentNode = document.createTextNode(titleValue)
  	replaceChildNode("ccp-header-title-item-id",contentNode);
  }
  
  function setWebLogo(imageFileId){
    var isTempImage = document.forms['editForm'].isTempImage.value;

    var imgNode = document.createElement('img');
    imgNode.setAttribute('id','ccp-header-title-image-id');
    if (isTempImage == 'true'){
      imgNode.setAttribute('src', teamelements_ctx + '/image/' + '<%= Constants.TEMP_FILES %>' + '-0-' + imageFileId + '-300x100/logo.png');
    } else {
      imgNode.setAttribute('src', teamelements_ctx + '/image/' + '<%= Constants.SITE_LOGO_FILES %>' + '-0-' + imageFileId + '-300x100/logo.png');
    }
  	replaceChildNode("ccp-header-title-link-id",imgNode);
  }
  
  function resetWebsiteLogo(){
    var isChecked = document.forms['editForm'].removeLogo.checked;
    if (isChecked){
	  	var titleValue = document.forms['editForm'].title.value;
	  	var siteTitleNode = document.createElement("h1");
	  	siteTitleNode.setAttribute('id','ccp-header-title-item-id');
	  	
      var contentNode = document.createTextNode(titleValue);
      siteTitleNode.appendChild(contentNode);
      replaceChildNode("ccp-header-title-link-id",siteTitleNode);
    } else {
      setWebLogo(document.forms['editForm'].attachmentList.value);
    }
  }

  function checkForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";
    
    //Check required field
    if (form.title.value == "") {
      messageText += "- Title is a required field.\r\n";
      formTest = false;
    }
    if (form.colorScheme.value == "") {
      messageText += "- Color scheme is a required field.\r\n";
      formTest = false;
    }
    if (formTest == false) {
      messageText = "The settings could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<body onLoad="javascript:document.editForm.title.focus()">
<div class="admin-portlet">
  <div class="portlet-section-header">
    <h1>Customize Settings</h1>
    <p>Back to <a href="<%= ctx %>/admin">System Administration</a></p>
  </div>
  <div class="portlet-section-body">
    <div class="formContainer">
      <form name="editForm" action="<%= ctx %>/AdminCustomize.do?command=Save&auto-populate=true&resetList=true" method="post"  onSubmit="return checkForm(this);" >
        <fieldset id="site-information">
          <legend>Site Information</legend>
          <label for="title">Title<span class="required">*</span></label>
          <input type="text" name="title" id="title" value="<%= toHtmlValue(mainProfile.getTitle()) %>" onkeyup="javascript:setWebPageTitle()" />
          <label for="shortDescription">Description</label>
          <input type="text" name="shortDescription" id="shortDescription" value="<%= toHtmlValue(mainProfile.getShortDescription()) %>" />
          <label for="attachmentText">Logo</label>
          <%
            Iterator files = fileItemList.iterator();
            while (files.hasNext()) {
              FileItem thisFile = (FileItem)files.next();
          %>
			  <%= thisFile.getFullImageFromAdmin(ctx) %>&nbsp;
          <%
            }
          %>
          <ccp:evaluate if="<%= fileItemList.size() > 0 %>"><br /></ccp:evaluate>
          <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
          <% if (fileItemList.size() > 0) { %>
            <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.SITE_LOGO_FILES %>&liid=<%= mainProfile.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&popup=true"
               rel="shadowbox" title="Share an attachment">Replace Logo</a>&nbsp;|&nbsp;Remove Logo <input type="checkbox" name="removeLogo" value="true" onClick="javascript:resetWebsiteLogo()"/>
	          <input type="hidden" id="attachmentList" name="attachmentList" value="<%= fileItemList.get(0).getId() %>" />
	          <input type="text" id="attachmentText" name="attachmentText" value="<%= fileItemList.get(0).getClientFilename() %>" size="45" disabled="true" />
          <%} else {%>
	          <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.SITE_LOGO_FILES %>&liid=<%= mainProfile.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single&popup=true"
               rel="shadowbox" title="Share an attachment">Attach Logo</a>
	          <input type="hidden" id="attachmentList" name="attachmentList" value="" />
	          <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
          <%}%>
	        <span class="characterCounter">Must be a .png or .gif with transparency, or .jpg; 300 x 100 pixels</span>
          <input type="hidden" id="isTempImage" name="isTempImage" value="false" />
        </fieldset>
        <fieldset id="Themes">
          <legend>Themes</legend>
          <c:forEach items="${themes}" var="theme">
          	<c:if test="${not fn:contains(theme,'.')}">
				<c:set var="thisTheme" value="${fn:substringBefore(fn:substringAfter(theme,\"/themes/\"),\"/\")}" />
            	<jsp:useBean id="thisTheme" type="java.lang.String" />
				<ccp:evaluate if="<%= applicationPrefs.get(ApplicationPrefs.THEME).equals(thisTheme) %>">
	          		<a class="selected" href="javascript:setTheme('${fn:substringBefore(fn:substringAfter(theme,"/themes/"),"/")}');sendRequest('<%= ctx %>/AdminCustomize.do?command=ListColorSchemes&theme=<%= thisTheme %>&inline=true','schemeList');"><img src="${ctx}${theme}thumbnail.png" alt="thumbnail" /></a>
          		</ccp:evaluate>
				<ccp:evaluate if="<%= !applicationPrefs.get(ApplicationPrefs.THEME).equals(thisTheme) %>">
	          		<a href="javascript:setTheme('${fn:substringBefore(fn:substringAfter(theme,"/themes/"),"/")}');sendRequest('<%= ctx %>/AdminCustomize.do?command=ListColorSchemes&theme=<%= thisTheme %>&inline=true','schemeList');"><img src="${ctx}${theme}thumbnail.png" alt="thumbnail" /></a>
          		</ccp:evaluate>
          	</c:if>
          </c:forEach>
          <input type="hidden" name="theme" value="<%= applicationPrefs.get(ApplicationPrefs.THEME) %>" />
        </fieldset>
        <fieldset id="Schemes">
          <legend>Color Schemes</legend>
          <div id="schemeList">
          <c:forEach items="${colorSchemes}" var="scheme">
          	<c:if test="${not fn:contains(scheme,'.')}">
	          	<a href="javascript:setColorScheme('${fn:substringBefore(fn:substringAfter(scheme,"/color-schemes/"),"/")}')"><img src="${ctx}${scheme}thumbnail.png" alt="thumbnail" /></a>
          	</c:if>
          </c:forEach>
          <input type="hidden" name="colorScheme" value="<%= applicationPrefs.get(ApplicationPrefs.COLOR_SCHEME) %>"/>
         </div> 
        </fieldset>
        <input type="submit" name="Save" value="<ccp:label name="button.save">Save</ccp:label>" class="submit" />
		<input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="javascript:this.form.dosubmit.value='false';window.location.href='<%= ctx %>/Admin.do?command=Default'">
		<input type="hidden" name="dosubmit" value="true">
      </form>
    </div>
  </div>
</div>
</body>

