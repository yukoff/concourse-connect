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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="imageList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="uploadedImage" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  int sourceLMID = imageList.getLinkModuleId();
  String source = null;
  String sourceText = null;
  String projectAction = null;
  switch (sourceLMID) {
    case Constants.PROJECT_BLOG_FILES:
      source = "blog";
      sourceText = "Blog";
      projectAction = "BlogActions.do";
      break;
    default:
      source = "wiki";
      sourceText = "Wiki";
      projectAction = "ProjectManagementWiki.do";
      break;
  }
%>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.6/tiny_mce_popup.js?1"></script>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.6/utils/mctabs.js"></script>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.6/utils/validate.js"></script>
<script type="text/javascript">
var ImageSelect = {
  init : function (ed) {
    var dom = ed.dom, n = ed.selection.getNode();
    if (n.nodeName == 'IMG') {
      // caption
      document.getElementById('caption').value = dom.getAttrib(n, 'title');
      document.getElementById('webcaption').value = dom.getAttrib(n, 'title');

      // see if a thumbnail image
      if (dom.getAttrib(n, 'src').indexOf('th=true') > -1) {
        document.getElementById('thumbnail').checked = true;
      }

      // set the current image selection
      var imageFilename;
    <ccp:evaluate if="<%= !hasText(uploadedImage) %>">
      imageFilename = dom.getAttrib(n, 'alt');
    </ccp:evaluate>
    <ccp:evaluate if="<%= hasText(uploadedImage) %>">
      imageFilename = '<%= StringUtils.jsEscape(uploadedImage) %>';
    </ccp:evaluate>
      var imageList = document.getElementById('imageList');
      for (var i = 0; i < imageList.options.length; i++) {
        if (imageList.options[i].value == imageFilename) {
          imageList.selectedIndex = i;
        }
      }
      if (imageList.selectedIndex > -1) {
        // wiki image
        displayTag();
        document.getElementById('wikiimage_tab').className = 'current';
        document.getElementById('wikiimage_panel').className = 'panel current';
      } else {
        // web image
        document.getElementById('webimagepreview').src = dom.getAttrib(n, 'src');
        document.getElementById('webimage').value = dom.getAttrib(n, 'src');
        document.getElementById('webimage_tab').className = 'current';
        document.getElementById('webimage_panel').className = 'panel current';
      }
    } else {
      // default to wiki image
      document.getElementById('wikiimage_tab').className = 'current';
      document.getElementById('wikiimage_panel').className = 'panel current';
    }
  },

  insertAndClose : function() {
    var ed = tinyMCEPopup.editor, args = {}, el;
    tinyMCEPopup.restoreSelection();
		// Fixes crash in Safari
    if (tinymce.isWebKit)
      ed.getWin().focus();

    var wikiImagePanelElm = document.getElementById('wikiimage_tab');
    if (wikiImagePanelElm.className == 'current') {
      var imageList = document.getElementById('imageList');
      tinymce.extend(args, {
        width : document.getElementById('preview').width,
        height : document.getElementById('preview').height,
        alt : imageList.options[imageList.selectedIndex].value,
        title : document.getElementById('caption').value,
        src : document.getElementById('preview').src
      });
    }

    var webImagePanelElm = document.getElementById('webimage_tab');
    if (webImagePanelElm.className == 'current') {
      var imageUrl = document.getElementById('webimage');
      tinymce.extend(args, {
        width : document.getElementById('webimagepreview').width,
        height : document.getElementById('webimagepreview').height,
        alt : imageUrl.value,
        title : document.getElementById('webcaption').value,
        src : document.getElementById('webimagepreview').src
      });
    }

    // Update the editor
    args.onmouseover = args.onmouseout = '';
    el = ed.selection.getNode();
    if (el && el.nodeName == 'IMG') {
      ed.dom.setAttribs(el, args);
    } else {
      ed.execCommand('mceInsertContent', false, '<img id="__mce_tmp" src="<%= ctx %>/javascript:;" />', {skip_undo : 1});
      ed.dom.setAttribs('__mce_tmp', args);
      ed.dom.setAttrib('__mce_tmp', 'id', '');
      ed.undoManager.add();
    }
    tinyMCEPopup.close();
  }
}
tinyMCEPopup.onInit.add(ImageSelect.init, ImageSelect);
</script>
<script type="text/javascript" src="<%= ctx %>/javascript/checkCheckbox.js"></script>
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

  function displayTag() {
    var imageList = document.getElementById('imageList');
    var selectedId = imageList.selectedIndex;
    if (selectedId > -1) {
      // Generate a url
      var tagName = imageList.options[selectedId].value;
      var thumbnail = "";
      if (getSelectedCheckboxValue(document.getElementById('thumbnail')) == 'true') {
        thumbnail = "?th=true";
      }
      document.getElementById('preview').src =
        "<%= ctx %>/show/<%= project.getUniqueId() %>/<%= source %>-image/" + escape(tagName) + thumbnail;
    } else {
      document.getElementById('preview').src = "<%= ctx %>/images/Empty.png";
    }
  }

  function displayWebImage(value) {
    document.getElementById('webimagepreview').src = value;
  }
	
</script>
<div class="portletWrapper">
  <h1>Insert an Image</h1>
  <br />
  <div class="tabs">
    <ul>
      <li id="wikiimage_tab"><span><a href="javascript:mcTabs.displayTab('wikiimage_tab','wikiimage_panel');" onmousedown="return false;"><%= sourceText %> Image</a></span></li>
      <li id="webimage_tab"><span><a href="javascript:mcTabs.displayTab('webimage_tab','webimage_panel');" onmousedown="return false;">Web Image</a></span></li>
    </ul>
  </div>
<%-- TODO: Impliment formContainer 
<div class="formContainer">--%>
  <div class="panel_wrapper" style="height:380px">

    <%-- Wiki Image --%>
    <div id="wikiimage_panel" class="panel">
      <fieldset>
        <legend>Image Library</legend>
        <table cellpadding="1" cellspacing="0" width="100%">
          <tr>
            <td valign="top" nowrap>
              <select id="imageList" size="15" onClick="displayTag();" style="width:300px">
        <%
          for (FileItem thisImage : imageList) {
        %>
                <option value="<%= thisImage.getClientFilename() %>"><%= thisImage.getClientFilename() %></option>
        <%
          }
        %>
              </select>
            </td>
            <td valign="top">
              <div style="border: 1px solid #999999; overflow:scroll; width:275px; height:200px">
                <img name="preview" id="preview" src="<%= ctx %>/images/Empty.png" border="0" align="absmiddle" alt="" />
              </div>
            </td>
          </tr>
        </table>
      </fieldset>

      <fieldset>
        <legend>Upload an image</legend>
        <form action="${ctx}/<%= projectAction %>?command=UploadImage&popup=true&pid=<%= project.getId() %>" name="inputForm" method="post" enctype="multipart/form-data" onSubmit="return checkFileForm(this);">
        <input type="file" name="id<%= project.getId() %>" size="45" />
        <input type="submit" name="Upload" value="Upload" >
        <span class="characterCounter">Must be a .png, .gif, or .jpg</span>
        <input type="hidden" name="pid" id="pid" value="<%= project.getId() %>">
        <input type="hidden" name="popup" value="<%= StringUtils.toHtmlValue(request.getParameter("popup")) %>" />
        </form>
      </fieldset>

      <form onsubmit="ImageSelect.insertAndClose();return false;" action="#">
        <fieldset>
          <legend>Selected Image</legend>
          <input type="checkbox" id="thumbnail" name="thumbnail" value="true" onclick="displayTag();" /> Insert as thumbnail<br />
          Set an optional caption:<br />
          <input type="text" id="caption" name="caption" value="" size="50" /><br />
        </fieldset>
        <div class="mceActionPanel">
          <div>
            <input type="submit" id="insert" name="insert" value="{#insert}" />
            <input type="button" id="cancel" name="cancel" value="{#cancel}" onclick="tinyMCEPopup.close();" />
          </div>
        </div>
      </form>
    </div>

    <%-- Web Image --%>
    <div id="webimage_panel" class="panel">
      <form onsubmit="ImageSelect.insertAndClose();return false;" action="#">
        <fieldset>
          <legend>General</legend>
          <label id="webimagelabel" for="webimage">Display the image at the following URL:</label><br />
          <input name="webimage" type="text" id="webimage" size="80" value="" class="mceFocus url" onchange="displayWebImage(this.value);" /><br />
          (use the complete url, example: http://www.example.com/images/logo.png)
        </fieldset>
        <fieldset>
          <legend>Preview</legend>
          <div style="border: 1px solid #999999; overflow:scroll; width:275px; height:200px">
            <img name="webimagepreview" id="webimagepreview" src="<%= ctx %>/images/Empty.png" border="0" align="absmiddle" alt="" />
          </div>
        </fieldset>
        <fieldset>
          <legend>Image Title</legend>
          <label id="webcaptionlabel" for="webcaption">Add an optional title:</label><br />
          <input type="text" id="webcaption" name="webcaption" value="" size="50" /><br />
          <br />
        </fieldset>
        <div class="mceActionPanel">
          <div>
            <input type="submit" id="insert" name="insert" value="{#insert}" />
            <input type="button" id="cancel" name="cancel" value="{#cancel}" onclick="tinyMCEPopup.close();" />
          </div>
        </div>
      </form>
    </div>
  </div>
</div>