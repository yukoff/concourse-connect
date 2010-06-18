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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.config.ApplicationVersion" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ include file="initPage.jsp" %>
<script language="javascript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/tiny_mce-3.2.7/tiny_mce.js?1"></script>
<script language="javascript" type="text/javascript">
  function initEditor(ta) {

    // Create a blog image plugin, adapted from wiki
    tinymce.create('teamelements.WikiImagePlugin', {
      WikiImagePlugin : function(ed, url) {
        // Register commands
        ed.addCommand('wikiImage', function() {

          // Internal image object like a flash placeholder
          if (ed.dom.getAttrib(ed.selection.getNode(), 'class').indexOf('mceItem') != -1) {
            alert('called, but not expected');
            return;
          }

          ed.windowManager.open({
            file : teamelements_ctx + "/BlogActions.do?command=ImageSelect&popup=true&pid=" + ilId,
            width : 640 + parseInt(ed.getLang('advimage.delta_width', 0)),
            height : 510 + parseInt(ed.getLang('advimage.delta_height', 0)),
            inline : 1
          }, {
            plugin_url : url
          });
        });

        // Register buttons
        ed.addButton('image', {
          title : 'advimage.image_desc',
          cmd : 'wikiImage'
        });
      }
    });

    // Register plugin using the add method
    tinymce.PluginManager.add('wikiImage', teamelements.WikiImagePlugin);

    // Create a blog video plugin, adapted from wiki
    tinymce.create('teamelements.WikiVideoPlugin', {
      WikiVideoPlugin : function(ed, url) {
        // Register commands
        ed.addCommand('wikiVideo', function() {

          // Internal image object like a flash placeholder
          if (ed.dom.getAttrib(ed.selection.getNode(), 'class').indexOf('mceItem') != -1) {
            alert('called, but not expected');
            return;
          }

          ed.windowManager.open({
            file : teamelements_ctx + "/BlogActions.do?command=VideoSelect&popup=true&pid=" + ilId,
            width : 640 + parseInt(ed.getLang('media.delta_width', 0)),
            height : 400 + parseInt(ed.getLang('media.delta_height', 0)),
            inline : 1
          }, {
            plugin_url : url
          });
        });

        // Register buttons
        ed.addButton('media', {
          title : 'media.desc',
          cmd : 'wikiVideo'
        });
      }
    });

    // Register plugin using the add method
    tinymce.PluginManager.add('wikiVideo', teamelements.WikiVideoPlugin);


    // Start the editor
    tinyMCE.init({

      strict_loading_mode : true,
      width : "100%",
      mode : "exact",
	    elements : ta,
	    theme : "advanced",
	    relative_urls : false,

      theme_advanced_buttons1 : "formatselect,link,unlink,image,media,|,bold,italic,underline,strikethrough,|,bullist,numlist,outdent,indent,removeformat,|,undo,redo,|,code",
      theme_advanced_buttons2 : "tablecontrols",
      theme_advanced_buttons3 : "",
      theme_advanced_toolbar_location : "top",
      theme_advanced_toolbar_align : "left",
      theme_advanced_statusbar_location : "none",
      theme_advanced_blockformats : "p,h2,h3,h4,h5,h6,pre",
      plugins : "safari,table,-wikiImage,-wikiVideo,inlinepopups",
      content_css : "${ctx}/css/editor.css?v=<%= ApplicationVersion.APP_VERSION %>,${ctx}/css/ccp-typography.css?v=<%= ApplicationVersion.APP_VERSION %>"
    });
  }
</script>
