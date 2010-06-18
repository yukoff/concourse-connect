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
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.7/tiny_mce_popup.js?1"></script>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.7/utils/mctabs.js"></script>
<script type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.7/utils/validate.js"></script>
<script type="text/javascript">
var VideoSelect = {
  init : function (ed) {
    var dom = ed.dom, n = ed.selection.getNode();
    if (n.nodeName == 'OBJECT') {
      // see if a thumbnail video
      if (dom.getAttrib(n, 'width') == '120') {
        document.getElementById('thumbnail').checked = true;
      }
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
    var thumbnail = "";
    if (getSelectedCheckboxValue(document.getElementById('thumbnail')) == 'true') {
      thumbnail = "true";
    }
    var width = '425';
    var height = '344';
    if (thumbnail == "true") {
      width = '120';
      height = '90';
    }
    var name = '';
    var link = videoUrl;

    var foundLink = false;
    var foundQikLink = false;
    var foundJustinTvLink = false;
    var foundLivestreamLink = false;
    var foundVimeoLink = false;
    var foundUstreamLink = false;

    if (videoUrl.match(/watch\?v=(.+)(.*)/)) {
      foundLink = true;
      width = '425';
      height = '344';
      if (thumbnail == "true") {
        width = '120';
        height = '90';
      }
      link = 'http://www.youtube.com/v/' + videoUrl.match(/v=(.*)(.*)/)[0].split('=')[1];
    } else if (videoUrl.indexOf('http://video.google.com/videoplay?docid=') == 0) {
      foundLink = true;
      width = '425';
      height = '326';
      link = 'http://video.google.com/googleplayer.swf?docId=' + videoUrl.substring('http://video.google.com/videoplay?docid='.length) + '&hl=en';
    } else if (videoUrl.indexOf('http://qik.com/') == 0) {
      foundQikLink = true;
      width = '425';
      height = '319';
    } else if (videoUrl.indexOf('http://www.justin.tv/') == 0) {
      foundJustinTvLink = true;
      width = '400';
      height = '300';
    } else if (videoUrl.indexOf('http://www.livestream.com/') == 0) {
      foundLivestreamLink = true;
      width = '400';
      height = '300';
    } else if (videoUrl.indexOf('http://vimeo.com/') == 0 || videoUrl.indexOf('http://www.vimeo.com/') == 0) {
      foundVimeoLink = true;
      width = '400';
      height = '300';
    } else if (videoUrl.indexOf("<object") == 0 && videoUrl.indexOf("http://www.ustream.tv/flash") > -1) {
      foundUstreamLink = true;
      //width = '400';
      //height = '300';
    } else {
      if (videoUrl.indexOf("<param") == -1) {
        foundLink = true;
      }
    }

    var g1 = '<p>';
    var g2 = '';
    var h = '';
    if (foundQikLink) {
      var channel = videoUrl.substring(15);
      g1 += '<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" ' +
                'codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,115,0" ' +
                'width="' + width + '" ' +
                'height="' + height + '" ' +
                'id="qikPlayer" align="middle">';
      h +=
                '<param name="allowScriptAccess" value="sameDomain" />' +
                '<param name="allowFullScreen" value="true" />' +
                '<param name="movie" value="http://assets0.qik.com/swfs/qikPlayer5.swf?1271067225" />' +
                '<param name="quality" value="high" />' +
                '<param name="bgcolor" value="#000000" />' +
                '<param name="FlashVars" value="username=' + channel + '" />' +
                '<embed src="http://assets0.qik.com/swfs/qikPlayer5.swf?1271067225" quality="high" bgcolor="#000000" ' +
                'width="' + width + '" ' +
                'height="' + height + '" name="qikPlayer" align="middle" allowScriptAccess="sameDomain" ' +
                'allowFullScreen="true" type="application/x-shockwave-flash" ' +
                'pluginspage="http://www.macromedia.com/go/getflashplayer" ' +
                'FlashVars="username=' + channel + '"></embed>';
      g2 +=
                '</object>';
    } else if (foundJustinTvLink) {
      var channel = videoUrl.substring(21);
      if (channel.indexOf("#") > -1) {
        channel = channel.substring(0, channel.indexOf("#"));
      }
      g1 += '<object type="application/x-shockwave-flash" ' +
                'height="' + height + '" ' +
                'width="' + width + '" ' +
                'id="live_embed_player_flash" ' +
                'data="http://www.justin.tv/widgets/live_embed_player.swf?channel=' + channel + '" ' +
                'bgcolor="#000000">';
      h +=
                '<param name="allowFullScreen" value="true" />' +
                '<param name="allowScriptAccess" value="always" />' +
                '<param name="allowNetworking" value="all" />' +
                '<param name="movie" value="http://www.justin.tv/widgets/live_embed_player.swf" />' +
                '<param name="flashvars" value="channel=' + channel + '&auto_play=false&start_volume=25" />';
      g2 +=
                '</object>';
    } else if (foundLivestreamLink) {
      var channel = videoUrl.substring(26);
      if (channel.indexOf("#") > -1) {
        channel = channel.substring(0, channel.indexOf("#"));
      }
      g1 += '<object ' +
                'width="' + width + '" ' +
                'height="' + height + '" ' +
                'id="lsplayer" ' +
                'classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">';
      h +=
                '<param name="movie" value="http://cdn.livestream.com/grid/LSPlayer.swf?channel=' + channel + '&amp;autoPlay=false"></param>' +
                '<param name="allowScriptAccess" value="always"></param>' +
                '<param name="allowFullScreen" value="true"></param>' +
                '<embed name="lsplayer" wmode="transparent" ' +
                'src="http://cdn.livestream.com/grid/LSPlayer.swf?channel=' + channel + '&amp;autoPlay=true" ' +
                'width="' + width + '" ' +
                'height="' + height + '" ' +
                'allowScriptAccess="always" allowFullScreen="true" type="application/x-shockwave-flash"></embed>';
      g2 +=
                '</object>';
    } else if (foundVimeoLink) {
      var channel = videoUrl.substring(videoUrl.indexOf("/", videoUrl.indexOf("http://") + 7) + 1);
      if (channel.indexOf("#") > -1) {
        channel = channel.substring(0, channel.indexOf("#"));
      }
      g1 += '<object ' +
                'width="' + width + '" ' +
                'height="' + height + '>';
      h +=
                '<param name="movie" value="http://vimeo.com/moogaloop.swf?clip_id=' + channel + '&amp;server=vimeo.com&amp;show_title=1&amp;show_byline=1&amp;show_portrait=0&amp;color=&amp;fullscreen=1" />' +
                '<param name="allowScriptAccess" value="always" />' +
                '<param name="allowFullScreen" value="true" />' +
                '<embed src="http://vimeo.com/moogaloop.swf?clip_id=' + channel + '&amp;server=vimeo.com&amp;show_title=1&amp;show_byline=1&amp;show_portrait=0&amp;color=&amp;fullscreen=1" ' +
                'width="' + width + '" ' +
                'height="' + height + '" ' +
                'allowScriptAccess="always" allowFullScreen="true" type="application/x-shockwave-flash"></embed>';
      g2 +=
                '</object>';
    } else if (foundUstreamLink) {
      g1 += videoUrl.substring(0, videoUrl.indexOf('">') + 2);
      h  += videoUrl.substring(videoUrl.indexOf('<param'), videoUrl.indexOf('</object>'));
      g2 += '</object>';
    } else if (foundLink) {
      g1 += '<object type="application/x-shockwave-flash" data="' + link + '" width="' + width + '" height="' + height + '">';
      h+= '<param name="movie" value="' + link + '" />';
      h+= '<param name="allowFullScreen" value="true" />';
      h+= '<param name="allowscriptaccess" value="always" />';
      g2 += '</object>';
    } else {
      h = videoUrl;
    }
    g2 += "&nbsp;</p>";

    // Update the editor
    args.onmouseover = args.onmouseout = '';
    el = ed.selection.getNode();
    if (el && el.nodeName == 'OBJECT') {
      ed.execCommand('mceBeginUndoLevel');
      el.setAttribute("type", "application/x-shockwave-flash");
      el.setAttribute("data", link);
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
          <label id="webvideoLabel" for="webvideo">Video Link:</label><br />
          <input name="webvideo" type="text" id="webvideo" size="80" value="" class="mceFocus url" /><br />
          <ul>
            <li>
              <strong>YouTube.com:</strong> use the complete url or embed content, <br />
              example: <em>http://www.youtube.com/watch?v=d13hPzZqFJI</em>
            </li>
            <li>
              <strong>Ustream.tv:</strong> use the complete embed content, example: <em>&lt;object&gt;...&lt;/object&gt;</em>
            </li>
            <li>
              <strong>Justin.tv:</strong> use the channel's URL, example: <em>http://www.justin.tv/zeroio</em>
            </li>
            <li>
              <strong>Qik.com:</strong> use the user's URL, example: <em>http://qik.com/zeroio</em>
            </li>
            <li>
              <strong>Livestream.com:</strong> use the video's link, example: <em>http://www.livestream.com/news</em>
            </li>
            <li>
              <strong>Google Video:</strong> use the complete url, <br />
              example: <em>http://video.google.com/videoplay?docid=-489885651925767878#</em>
            </li>
            <li>
              <strong>Vimeo:</strong> use the complete url, example: <em>http://www.vimeo.com/2696386</em>
            </li>
          </ul>
        </fieldset>
        <fieldset>
          <legend>Formatting</legend>
          <input type="checkbox" id="thumbnail" name="thumbnail" value="true" /> Insert as thumbnail<br />
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