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
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="imageList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript" language="JavaScript">
  function checkFileForm(form) {
    if (form.dosubmit.value == "false") {
      return false;
    }
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
        form.dosubmit.value = "false";
        return true;
      } else {
        return false;
      }
    }
  }

  var imageId = "";
  function displayTag() {
    var imageList = document.forms['imageForm'].imageList;
    var selectedId = imageList.selectedIndex;
    if (selectedId > -1) {
      var tagName = imageList.options[selectedId].value;
      var frame = "";
      //alert(getSelectedCheckbox(document.forms['imageForm'].frameType).length);
      if (getSelectedCheckboxValue(document.forms['imageForm'].frameType) == "frame") {
        frame = "|frame";
      }
      var thumbnail = "";
      if (getSelectedCheckboxValue(document.forms['imageForm'].frameType) == "thumbnail") {
        thumbnail = "&th=true";
        frame = "|thumb";
      }
      var label = "";
      if (document.forms['imageForm'].label.value != '') {
        label = "|" + document.forms['imageForm'].label.value;
      }
      document.forms['imageForm'].tag.value = "[[Image:" + tagName + frame + label + "]]";
      if (tagName + frame != imageId) {
        document.forms['imageForm'].preview.src = "<%= ctx %>/ProjectManagementWiki.do?command=Img&pid=<%= project.getId() %>&subject=" + escape(tagName) + thumbnail;
      }
      imageId = tagName + frame;
    } else {
      document.forms['imageForm'].tag.value = "";
      imageId = "";
      document.forms['imageForm'].preview.src = "<%= ctx %>/images/Empty.png";
    }
  }
</script>
<form name="imageForm">
Build a link to an existing image:<br />
<br />
<table cellpadding="1" cellspacing="0" width="100%">
  <tr>
    <td rowspan="2" valign="top">
      <select name="imageList" size="15" onClick="displayTag();" style="width:200px">
<%
  Iterator i = imageList.iterator();
  while (i.hasNext()) {
    FileItem thisImage = (FileItem) i.next();
%>
        <option value="<%= thisImage.getClientFilename() %>"><%= thisImage.getClientFilename() %></option>
<%
  }
%>
      </select>
    </td>
    <td valign="top" align="left" style="margin-top: 1px;" nowrap>
      <input type="radio" name="frameType" value="none" onclick="displayTag();" checked /> No frame<br />
      <input type="radio" name="frameType" value="frame" onclick="displayTag();" /> Add frame around image<br />
      <input type="radio" name="frameType" value="thumbnail" onclick="displayTag();" /> Add frame; Show thumbnail and link to larger image<br />
      Caption:<br />
      <input type="text" name="label" value="" size="45" onkeyup="displayTag();" /><br />
      Image tag:<br />
      <input type="text" name="tag" value="" size="45" />
    </td>
  </tr>
  <tr>
    <td valign="top" align="left">
      <div name="frame" id="frame" style="border: 1px solid #999999;">
        <div name="mat" id="mat" style="border: 1px solid #999999; margin: 5px;">
          <img name="preview" id="preview" src="<%= ctx %>/images/Empty.png" border="0" align="absmiddle" />
        </div>
        <div name="caption" id="caption" style="margin-bottom: 5px; margin-left: 5px; margin-right: 5px; text-align: left;">
          <div style="float:right"><a href="#" title="Enlarge"><img src="<%= ctx %>/images/magnify-clip.png" width="15" height="11" alt="Enlarge" /></a></div>
          This is a sample caption.
        </div>
      </div>
    </td>
  </tr>
</table>
</form>
<hr>
<form action="<%= ctx %>/ProjectManagementWiki.do?command=UploadImage&popup=true&pid=<%= project.getId() %>" name="inputForm" method="post" enctype="multipart/form-data" onSubmit="return checkFileForm(this);">
  Send a new image:<br />
  <br />
  <input type="file" name="id<%= project.getId() %>" size="45" /><br />
  <br />
  <input type="submit" name="Upload" value="Upload">
  <input type="hidden" name="pid" id="pid" value="<%= project.getId() %>">
  <input type="hidden" name="popup" value="<%= StringUtils.toHtmlValue(request.getParameter("popup")) %>" />
  <input type="hidden" name="dosubmit" value="true">
</form>
