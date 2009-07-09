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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.5/tiny_mce_popup.js?1"></script>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.5/utils/mctabs.js"></script>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.5/utils/validate.js"></script>
<script type="text/javascript">
var VideoSelect = {
  init : function (ed) {
    var dom = ed.dom, n = ed.selection.getNode();
    if (n.nodeName == 'OBJECT') {
      // web video
      if (n.hasChildNodes()) {
        for (i = 0; i < n.childNodes.length; i++) {
          if (n.childNodes[i].tagName == 'PARAM' && dom.getAttrib(n.childNodes[i], 'name') == 'movie') {
            document.getElementById('webvideo').value = dom.getAttrib(n.childNodes[i], 'value');
          }
        }
      }
      // title
      //document.getElementById('webcaption').value = dom.getAttrib(n, 'title');
    }
    document.getElementById('webvideo_tab').className = 'current';
    document.getElementById('webvideo_panel').className = 'panel current';
  },

  insertAndClose : function() {
    var ed = tinyMCEPopup.editor, args = {}, el;
    tinyMCEPopup.restoreSelection();
		// Fixes crash in Safari
    if (tinymce.isWebKit) {
      ed.getWin().focus();
    }
    var videoUrl = document.getElementById('webvideo').value;
    var width = '425';
    var height = '350';
    var name = '';
    var link = videoUrl;

    var foundLink = false;
    if (videoUrl.match(/watch\?v=(.+)(.*)/)) {
      foundLink = true;
      width = '425';
      height = '350';
      link = 'http://www.youtube.com/v/' + videoUrl.match(/v=(.*)(.*)/)[0].split('=')[1];
    } else if (videoUrl.indexOf('http://video.google.com/videoplay?docid=') == 0) {
      foundLink = true;
      width = '425';
      height = '326';
      link = 'http://video.google.com/googleplayer.swf?docId=' + videoUrl.substring('http://video.google.com/videoplay?docid='.length) + '&hl=en';
    } else {
      if (videoUrl.indexOf("<param") == -1) {
        foundLink = true;
      }
    }

    var g1 = '<p>';
    var g2 = '';
    var h = '';
    if (foundLink) {
      // Avoid annoying warning about insecure items
      if (!tinymce.isIE || document.location.protocol != 'https:') {
        g1 += '<object width="' + width + '" height="' + height + '" name="' + name + '">';
        h+= '<param name="movie" value="' + link + '"></param>';
        h+= '<param name="allowFullScreen" value="true"></param>';
        h+= '<param name="allowscriptaccess" value="always"></param>';
      }
      h += '<embed type="application/x-shockwave-flash" ';
      h += 'src="' + link + '" allowscriptaccess="always" allowfullscreen="true" width="' + width + '" height="' + height + '"';
      h += '></embed>';

      // Avoid annoying warning about insecure items
      if (!tinymce.isIE || document.location.protocol != 'https:') {
        g2 += '</object>';
      }
    } else {
      h = videoUrl;
    }
    g2 += "&nbsp;</p>";

    // Update the editor
    args.onmouseover = args.onmouseout = '';
    el = ed.selection.getNode();
    if (el && el.nodeName == 'OBJECT') {
      ed.execCommand('mceBeginUndoLevel');

      el.setAttribute("width", width);
      el.setAttribute("height", height);
      el.innerHTML = h;

      ed.addVisual();
      ed.nodeChanged();
      ed.execCommand('mceEndUndoLevel');
    } else {
      ed.execCommand('mceInsertContent', false, g1 + h + g2);
    }
    tinyMCEPopup.close();
  }
};
tinyMCEPopup.onInit.add(VideoSelect.init, VideoSelect);
</script>
<div class="portletWrapper">
  <h1>Insert a Video</h1>
  <br />
  <div class="tabs">
    <ul>
      <li id="webvideo_tab"><span><a href="javascript:mcTabs.displayTab('webvideo_tab','webvideo_panel');" onmousedown="return false;">Video</a></span></li>
    </ul>
  </div>
  <div class="panel_wrapper" style="height:300px">

    <%-- Web Video --%>
    <div id="webvideo_panel" class="panel">
      <form onsubmit="VideoSelect.insertAndClose();return false;" action="#">
        <fieldset>
          <legend>General</legend>
          <label id="webvideoLabel" for="webvideo">Embed a video using YouTube's video URL:</label><br />
          <input name="webvideo" type="text" id="webvideo" size="80" value="" class="mceFocus url" /><br />
          (use the complete url or embed content, example: http://www.youtube.com/watch?v=d13hPzZqFJI)
        </fieldset>
        <%--
        <fieldset>
          <legend>Caption</legend>
          <label id="webcaptionlabel" for="webcaption">Add an optional title:</label><br />
          <input type="text" id="webcaption" name="webcaption" value="" size="50" /><br />
          <br />
        </fieldset>
        --%>
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