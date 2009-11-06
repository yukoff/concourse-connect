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
<jsp:useBean id="imageList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="uploadedImage" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  String source = "style";
  String projectAction = "";
  String projectCommand = "UploadStyleImage";

%>
<script type="text/javascript" language="JavaScript">
  function checkFileForm(form) {
    var formTest = true;
    var messageText = "";
    if (form.id<%= project.getId() %>.value.length < 5) {
      messageText += "- File is required\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The file could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      if (form.Upload.value != 'Please Wait...') {
        form.Upload.value='Please Wait...';
        form.Upload.disabled = true;
        return true;
      } else {
        return false;
      }
    }
  }

  function checkManageForm(form) {
    var imageList = document.getElementById('imageList');
    var selectedId = imageList.selectedIndex;
    var formTest = true;
    var messageText = "";    
    if (selectedId < 0) {
      messageText += "- File is required\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The file could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }

  function displayTag() {
    var imageList = document.getElementById('imageList');
    var selectedId = imageList.selectedIndex;
    if (selectedId > -1) {
      // Generate a url
      var tagName = imageList.options[selectedId].value;
      var thumbnail = "";
      document.getElementById('preview').src =
        "<%= ctx %>/show/<%= project.getUniqueId() %>/<%= source %>-image/" + escape(tagName) + thumbnail;
    } else {
      document.getElementById('preview').src = "<%= ctx %>/images/Empty.png";
    }
  }

  function downloadWebImage() {
    var imageList = document.getElementById('imageList');
    var selectedId = imageList.selectedIndex;
    if (selectedId > -1) {
      var tagName = imageList.options[selectedId].value;
      window.open( "<%= ctx %>/show/<%= project.getUniqueId() %>/<%= source %>-image/" + escape(tagName)+"?download=true", "_self");
    } else {
      alert("An image must be selected to download")
    }
  }

  function displayWebImage(value) {
    document.getElementById('webimagepreview').src = value;
  }

  function updateSelectedImageFilepath(filePath, imageFile) {
      document.getElementById("selectedImageFilepath").innerHTML = filePath+imageFile;
  }
</script>
<h2>Manage Images for CSS</h2>
<p><em>NOTE:</em> all files will need to prepend a file path of <strong>"<%= ctx %>/show/<%= project.getUniqueId()%>/style-image/"</strong> prepended to the filename. For instance: "<%= ctx %>/show/<%= project.getUniqueId()%>/style-image/mypic.jpg"</p>
<%-- Wiki Image
<div  class="panel"> --%>
<form action="<%= ctx %>/ProjectManagement<%= projectAction %>.do?command=DeleteStyleImage&pid=<%= project.getId() %>" name="manageForm" method="POST" onSubmit="return checkManageForm(this);">
  <fieldset>
    <legend>Image Library</legend>
    <p>Selected image's relative location: <strong><span id="selectedImageFilepath">&nbsp;</span></strong></p>
    <div class="leftColumn">
      <select id="imageList" name="imageList" size="15" onClick="displayTag();updateSelectedImageFilepath('<%= ctx %>/show/<%= project.getUniqueId()%>/style-image/',document.manageForm.imageList.value);" style="width:300px">
        <%
          for (FileItem thisImage : imageList) {
        %>
                <option value="<%= thisImage.getClientFilename() %>"><%= thisImage.getClientFilename() %></option>
        <%
          }
        %>
     </select>
    </div>
    <div class="rightColumn panel" id="wikiimage_panel">
     <img name="preview" id="preview" src="<%= ctx %>/images/Empty.png" border="0" align="absmiddle" alt="" />
    </div>
  </fieldset>
  <input type="button" name="Download" value="Download" onclick="downloadWebImage();" class="submit" />
  <input type="submit" name="Delete" value="Delete" class="cancel" />
</form>
<form action="<%= ctx %>/ProjectManagement<%= projectAction %>.do?command=<%= projectCommand %>&pid=<%= project.getId() %>&added=true" name="inputForm" method="post" enctype="multipart/form-data" onSubmit="return checkFileForm(this);" >
  <fieldset>
    <legend>Upload an image</legend>
    <input type="file" name="id<%= project.getId() %>" size="45" onchange="YAHOO.util.Dom.setStyle(document.getElementById('progressBar'), 'display', 'inline');this.form.submit();" />
    <img id="progressBar" src="<%= ctx %>/images/loading16.gif" alt="loading..." style="display:none"/>
    <input type="hidden" name="pid" id="pid" value="<%= project.getId() %>" />         
  </fieldset>
</form> 
