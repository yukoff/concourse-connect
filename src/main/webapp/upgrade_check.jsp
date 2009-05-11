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
<jsp:useBean id="status" class="java.lang.String" scope="request"/>
<jsp:useBean id="installedVersion" class="java.lang.String" scope="request"/>
<jsp:useBean id="newVersion" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    if (confirm("Are you sure you want to upgrade the system now?")) {
      showProgress();
      return true;
    } else {
      return false;
    }
  }
  function showProgress() {
    hideSpan("buttons");
    showSpan("progress");
    return true;
  }
</script>
<form style="background:#efefef" name="configure" method="POST" action="<%= ctx %>/Upgrade.do?command=PerformUpgrade&style=true" onSubmit="return checkForm(this)" style="margin:0; width:100%; background:#efefef; ">
  <div style="width:100%; height:100%; background:#efefef; padding:15px 0 0 0; margin:0">
    <div style="width:600px; height:600px; position:relative; margin:0 auto; background:#efefef">
      <h1 style="color:#425c61; font-size:x-large; margin:35px 0">Site Upgrade</h1>
      <div style="font-size:small; width:700px; margin:0 auto; background:#fff; -moz-border-radius:10px; -webkit-border-radius:10px; border-color:#acacac #cacaca #d4d4d4 #aeaeae; border-width:2px; border-style:solid; padding:15px">
        <p><em>You are now logged in as administrator.</em></p>
        <p style="color:#ff0000">Make sure you have backed up your database and file library.</p>
        <p>Please review the following information;</p>
        <dl>
          <dt>Previous version:</dt>
          <dd><%= toHtml(installedVersion) %></dd>
          <dt>Upgrade to:</dt>
          <dd><%= toHtml(newVersion) %></dd>
        </dl>
        <ccp:evaluate if="<%= \"0\".equals(status) %>">
          <p style="color:#ff0000; font-weight:900">
            This system appears to already have been upgraded. Continuing will
                mark this system as upgraded, allowing users to login.
          </p>
        </ccp:evaluate>
        <span id="buttons" name="buttons">
          <input type="submit" value="Upgrade >"/>
        </span>
        <span id="progress" name="progress" style="display:none">
          <font color="blue"><b>Please Wait... upgrading!</b></font>
        </span>
      </div>
    </div>
  </div>
  <br />
</form>
