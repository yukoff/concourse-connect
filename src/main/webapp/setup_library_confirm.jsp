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
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function configureLibrary() {
    document.forms['setupForm'].action = '${ctx}/Setup.do?command=ConfigureLibrary';
    document.forms['setupForm'].submit();
  }
</script>
<form action="${ctx}/Setup.do?command=SaveLibrary&confirm=yes" name="setupForm" method="post" style="margin:0; width:100%; height:100%; background:#efefef; ">
  <input type="hidden" name="directory" value="<%= request.getParameter("directory") %>" />
  <div style="padding:15px 0 0 0; margin:0">
    <div style="width:600px; position:relative; margin:0 auto">
      <img src="${ctx}/images/setup/step-1-header.jpg" alt="Step One" style="margin:0 auto; display:block">
      <br style="clear:both" />
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Configure the file library</h1>
      <h2 style="color:#5d9fbf; font-size:large; font-weight:normal; text-align:center">Continue and use this directory for the application?</h2>
      <div style="font-size:small; width:500px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:0 15px 15px">
        <img src="${ctx}/images/icons/exclamation.png" alt="Important" style="margin:0 5px 0 0">
        The following directory does not exist and will be created...<br />
        <br />
        <strong><%= toHtml(request.getParameter("directory")) %></strong><br />
      </div>
      <input type="submit" value="Continue" style="float:right; margin-top:10px" />
      <input type="button" onclick="configureLibrary()" value="Back" style="float:right; margin-top:10px" />      
    </div>
  </div>
</form>
