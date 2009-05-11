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
<jsp:useBean id="databaseBean" class="com.concursive.connect.web.modules.setup.beans.SetupDatabaseBean" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  var installing = "false";
  function configureDatabase() {
    document.forms['setupForm'].action = '${ctx}/SetupDatabase.do?command=ConfigureDatabase';
    document.forms['setupForm'].submit()
  }
  function checkForm(form) {
    if (installing == "false") {
      installing = "true";
      if (document.forms['setupForm'].action.indexOf('InstallDatabase') > -1) {
        showSpan('pleaseWait');
        hideSpan('information');
        form.save.value='Please Wait...';
        form.save.disabled = true;
      }
      return true;
    }
    return false;
  }
</script>
<form action="${ctx}/SetupDatabase.do?command=InstallDatabase" name="setupForm" method="post" onsubmit="return checkForm(this)">
  <input type="hidden" name="auto-populate" value="true" />
  <input type="hidden" name="type" value="<%= databaseBean.getType() %>" />
  <input type="hidden" name="address" value="<%= databaseBean.getAddress() %>" />
  <input type="hidden" name="port" value="<%= databaseBean.getPort() %>" />
  <input type="hidden" name="database" value="<%= databaseBean.getDatabase() %>" />
  <input type="hidden" name="user" value="<%= databaseBean.getUser() %>" />
  <input type="hidden" name="password" value="<%= databaseBean.getPassword() %>" />
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:800px; height:600px; position:relative; margin:0 auto">
      <img src="${ctx}/images/setup/step-3-header.jpg" alt="Step Three" style="margin:0 auto; display:block">
      <br style="clear:both">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Configure the database connection</h1>
      <div  style="font-size:small; width:700px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <div style="float:left; width:50%; border-right:2px solid #ddd">
          <span name="pleaseWait" ID="pleaseWait" style="display:none; text-align:center">
            <img src="${ctx}/images/progressbar220x19.gif" alt="Please Wait" style="padding:15px 0; margin:0 auto" />
            <br />
            <font color="red">Please wait... this could take up to 5 minutes.</font></span>
          <span id="information">
            <p style="font-size:small">A connection was made using the following URL:<br />
            <b><%= toHtml(databaseBean.getUrl()) %></b><br />
             <b style="margin-top:15px; display:block">Continue with testing and installing the schema?</b>
             This could take several minutes, please be patient.</p>
          </span>
        </div>
        <div style="width:45%; float:left; margin-left:10px">
          <h2 style="margin-top:0">Read Me...</h2>
          The next step is to test the database, if it is new, then the schema
          will be installed. If the schema already exists, then no changes will
          be made to the database.
        </div>
        <br style="clear:both" />
      </div>
      <input type="submit" value="Continue" style="float:right; margin-top:10px" name="save" />
      <input type="button" onclick="configureDatabase()" value="Back" style="float:right; margin-top:10px" />
    </div>
  </div>
</form>
