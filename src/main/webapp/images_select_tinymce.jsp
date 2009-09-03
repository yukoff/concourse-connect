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
<%@ page import="com.concursive.connect.Constants" %>
<jsp:useBean id="imageList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="newsArticle" class="com.concursive.connect.web.modules.blog.dao.BlogPost" scope="request"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="javascript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.6/tiny_mce_popup.js?1"></script>
<script type="text/javascript">
var FileBrowserDialogue = {
  init : function () {
    <ccp:evaluate if="<%= imageList.getLinkModuleId() == -1 %>">
      document.getElementById('link').focus();
    </ccp:evaluate>
  },
  mySubmit : function () {
    
  }
}

tinyMCEPopup.onInit.add(FileBrowserDialogue.init, FileBrowserDialogue);

function onCancel() {
  tinyMCEPopup.close();
};

function setImage(imageUrl) {
  var win = tinyMCEPopup.getWindowArg("window");

  // insert information now
  win.document.getElementById(tinyMCEPopup.getWindowArg("input")).value = imageUrl;

  // for image browsers: update image dimensions
  if (win.getImageData) win.getImageData();

  // show preview
  //win.ImageDialog.showPreviewImage(inurl);

  // close popup window
  tinyMCEPopup.close();
}
</script>
<%-- Title --%>
<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
<tr>
<td>
<table border="0" width="100%">
  <tr>
    <td>
      <img src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/images/icons/stock_insert_image-16.gif" border="0" align="absmiddle" />
    </td>
    <td width="100%">
      <strong>Image Libraries</strong>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      Choose an image library in which you would like to insert an image from.
      You can use images from the following sources...
    </td>
  </tr>
</table>
</td>
</tr>
<%-- Menu --%>
<tr>
<td>
<div class="tabs-te" id="toptabs">
<table cellpadding="4" cellspacing="0" border="0" width="100%">
  <tr>
    <ccp:evaluate if="<%= newsArticle.getId() > -1 %>">
    <ccp:tabbedMenu text="News Article" key="<%= String.valueOf(Constants.BLOG_POST_FILES) %>" value="<%= String.valueOf(imageList.getLinkModuleId()) %>" url='<%= ctx + "/" + "Portal.do?command=ImageSelect&popup=true&editor=tinymce&constant=" + Constants.BLOG_POST_FILES + "&id=" + newsArticle.getId() %>' />
    </ccp:evaluate>
    <ccp:tabbedMenu text="This Project" key="<%= String.valueOf(Constants.PROJECTS_FILES) %>" value="<%= String.valueOf(imageList.getLinkModuleId()) %>" url='<%= ctx + "/" + "Portal.do?command=ImageSelect&popup=true&editor=tinymce&constant=" + Constants.PROJECTS_FILES + "&id=" + project.getId() + "&nid=" + newsArticle.getId() %>' />
    <%--
    <ccp:tabbedMenu text="Shared" key="global" value="project" url="#" />
    --%>
    <ccp:tabbedMenu text="External" key="-1" value="<%= String.valueOf(imageList.getLinkModuleId()) %>" url='<%= ctx + "/" + "Portal.do?command=ImageSelect&popup=true&editor=tinymce&constant=-1&id=-1&pid=" + project.getId() + "&nid=" + newsArticle.getId() %>' />
    <td width="100%" style="background-image: none; background-color: transparent; border: 0px; border-bottom: 1px solid #666; cursor: default;">&nbsp;</td>
  </tr>
</table>
</div>
</td>
</tr>
<tr>
<td height="100%">
<table cellpadding="4" cellspacing="0" border="0" width="100%" height="100%">
  <tr>
    <td class="containerBack" height="100%" valign="top">
      <%-- Begin images --%>
      <table cellpadding="10" cellspacing="0" border="0" width="100%">
        <ccp:evaluate if="<%= imageList.size() == 0 && imageList.getLinkModuleId() != -1 %>">
        <tr>
          <td class="ImageList" valign="center">
            No images to display in this library.
          </td>
        </tr>
        </ccp:evaluate>
<%
  int rowcount = 0;
  int count = 0;
  int multiplier = 3;
  if (imageList.size() == 4) {
    multiplier = 2;
  }
  Iterator i = imageList.iterator();
  while (i.hasNext()) {
    FileItem thisItem = (FileItem) i.next();
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
        <img src="<%= ctx + "/Portal.do?command=Img&constant=" + imageList.getLinkModuleId() + "&id=" + imageList.getLinkItemId() + "&fid=" + thisItem.getId() %>&th=true" onClick="setImage('<%= "Portal.do?command=Img&constant=" + imageList.getLinkModuleId() + "&id=" + imageList.getLinkItemId() + "&fid=" + thisItem.getId() %>')" />
      </span>
    </td>
<ccp:evaluate exp="<%= count % multiplier == 0 %>">
  </tr>
</ccp:evaluate>
<%}%>

      </table>
      <%-- End images --%>
      <br />
      <%-- Upload Form --%>
      <ccp:evaluate if="<%= Constants.BLOG_POST_FILES == imageList.getLinkModuleId() %>">
      <table border="0" cellpadding="1" cellspacing="0" width="100%">
        <tr class="subtab">
          <td>
            &nbsp;
          </td>
        </tr>
      </table>
      <br />
      Attach image directly to this News Article:
      <form method="POST" name="inputForm" action='<%= ctx + "/Portal.do?command=UploadImage" %>' enctype="multipart/form-data">
        <input type="file" name="id<%= imageList.getLinkItemId() %>" size="45" /><br />
        <br />
        <input type="submit" name="Upload" value="Upload" />
        <input type="hidden" name="popup" value="<%= request.getParameter("popup") %>" />
        <input type="hidden" name="editor" value="tinymce" />
        <input type="hidden" name="constant" id="constant" value="<%= request.getParameter("constant") %>" />
        <input type="hidden" name="nid" id="nid" value="<%= newsArticle.getId() %>" />
      </form>
      <br />
      </ccp:evaluate>
      <%-- Upload Form --%>
      <ccp:evaluate if="<%= imageList.getLinkModuleId() == -1 %>">
      <table border="0" cellpadding="1" cellspacing="0" width="100%">
        <tr class="subtab">
          <td>
            &nbsp;
          </td>
        </tr>
      </table>
      <br />
      Use an image from another web site:<br />
      <br />
      URL: <input type="text" name="link" id="link" size="50" /><br />
      <br />
      <input type="button" name="Submit" value="Submit" onclick="setImage(document.getElementById('link').value);" />
      <br />
      </ccp:evaluate>
      <%-- Cancel --%>
      <table border="0" cellpadding="1" cellspacing="0" width="100%">
        <tr class="subtab">
          <td>
            &nbsp;
          </td>
        </tr>
      </table>
      <br />
      <input type="button" name="cancel" value="Cancel" onclick="return onCancel();" /><br />
      <br />
    </td>
  </tr>
</table>
</td>
</tr>
</table>
<input type="hidden" name="popup" value="<%= request.getParameter("popup") %>" />
<input type="hidden" name="constant" id="constant" value="<%= request.getParameter("constant") %>" />
<input type="hidden" name="id" id="id" value="<%= request.getParameter("id") %>" />
<input type="hidden" name="f_url" id="f_url" value="" />
<input type="hidden" name="f_horiz" id="f_horiz" value="" />
<input type="hidden" name="f_vert" id="f_vert" value="" />
<input type="hidden" name="f_align" id="f_align" value="absMiddle" />
<input type="hidden" name="f_border" id="f_border" value="0" />
<input type="hidden" name="f_alt" id="f_alt" value="" />
