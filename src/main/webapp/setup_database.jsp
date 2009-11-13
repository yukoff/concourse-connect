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
  function setPort() {
    if (document.forms['setupForm'].type.value == "postgresql" && 
      (document.forms['setupForm'].port.value == "1433" || document.forms['setupForm'].port.value == "")) {
      document.forms['setupForm'].port.value = "5432";
    }
    if (document.forms['setupForm'].type.value == "mssql" && 
      (document.forms['setupForm'].port.value == "5432" || document.forms['setupForm'].port.value == "")) {
      document.forms['setupForm'].port.value = "1433";
    }
    document.forms['setupForm'].address.focus();
  }
</script>
<form action="${ctx}/SetupDatabase.do?command=SaveDatabase" name="setupForm" method="post">
  <input type="hidden" name="auto-populate" value="true" />
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:600px; height:600px; position:relative; margin:0 auto">
      <img src="${ctx}/images/setup/step-3-header.jpg" alt="Step Three" style="margin:0 auto; display:block">
      <br style="clear:both">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Configure the database connection</h1>
      <center><b><%= showError(request, "actionError", false) %></b></center>
      <div class="formContainer" style="font-size:small; width:500px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <fieldset style="border:none">
          <legend style="padding:0">Database Details</legend>
          <label><font color="red">*</font>Database</label>
          <b><%= showAttribute(request, "typeError") %></b>
          <select name="type" onChange="setPort();">
            <option value="none"></option>
            <option value="postgresql" <%= "postgresql".equals(databaseBean.getType()) ? "selected" : "" %>>PostgreSQL</option>
            <%--
            <option value="mssql" <%= "mssql".equals(databaseBean.getType()) ? "selected" : "" %>>Microsoft SQL Server 2005/2000/MSDE</option>
            --%>
          </select>
          <label><font color="red">*</font>Address</label>
          <b><%= showAttribute(request, "addressError") %></b>
          <input type="text" name="address" size="30" value="<%= toHtmlValue(databaseBean.getAddress()) %>" />
          <label><font color="red">*</font>Port</label>
          <b><%= showAttribute(request, "portError") %></b>
          <input type="text" name="port" size="10" maxlength="5" value="<%= databaseBean.getPort() > 0 ? String.valueOf(databaseBean.getPort()) : "" %>"/>
          <label><font color="red">*</font>Database</label>
          <b><%= showAttribute(request, "databaseError") %></b>
          <input type="text" name="database" size="30" value="<%= toHtmlValue(databaseBean.getDatabase()) %>" />
          <label><font color="red">*</font>User Name</label>
          <b><%= showAttribute(request, "userError") %></b>
          <input type="text" name="user" size="30" value="<%= toHtmlValue(databaseBean.getUser()) %>"/>
          <label>Password</label>
          <input type="text" name="password" size="30" value="<%= toHtmlValue(databaseBean.getPassword()) %>"/>
        </fieldset>
      </div>
      <input type="submit" value="Continue" style="float:right; margin-top:10px" />
    </div>
  </div>
</form>
