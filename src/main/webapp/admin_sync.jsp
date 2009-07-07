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
<%@ page import="com.concursive.commons.text.StringUtils"%>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="syncStatus" class="java.util.Vector" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    if (form.dosubmit.value == "false") {
      return true;
    }
    var formTest = true;
    var messageText = "";
    
    <ccp:evaluate if="<%= !StringUtils.hasText(applicationPrefs.get("CONCURSIVE_CRM.SERVER")) %>">
    //Check required field
	    if (form.serverURL.value == "") {
	      messageText += "- Server URL  is a required field.\r\n";
	      formTest = false;
	    }
	    if (form.apiClientId.value == "") {
	      messageText += "- API Client Id  is a required field.\r\n";
	      formTest = false;
	    }
	    if (form.apiCode.value == "") {
	      messageText += "- API Code  is a required field.\r\n";
	      formTest = false;
	    }
    </ccp:evaluate>
    if (formTest == false) {
      messageText = "The settings could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      form.dosubmit.value = "true";
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
  
  function setField(fieldId){
  	document.getElementById(fieldId).value = "true";
  }
  
</script>
<div class="admin-portlet">
  <div class="portlet-section-header">
    <h1>Synchronize with ConcourseSuite CRM</h1>
    <p>Back to <a href="<%= ctx %>/admin">System Administration</a></p>
  </div>
  <form name="editForm" action="<%= ctx %>/AdminSync.do?command=StartSync" method="post"  onSubmit="return checkForm(this);" >
    <div class="portlet-section-body">
    <ccp:evaluate if="<%= syncStatus.size() == 0 %>">
      <p>Click the Sync button to begin the process.</p>
      <ol>
        <li>The ConcourseConnect CRM Plug-in must be installed</li>
        <li>ConcourseSuite CRM must be separately installed</li>
        <li>ConcourseSuite CRM must have an API client configured</li>
        <li>ConcourseConnect must have the CRM credentials configured</li>
      </ol>
      <p>For more information see the <a target="_blank" href="http://www.concursive.com/show/concourseconnect/wiki">ConcourseConnect Wiki</a></p>
    	<ccp:evaluate if="<%= StringUtils.hasText(applicationPrefs.get("CONCURSIVE_CRM.SERVER")) %>">
      		<input type="submit" name="Sync" value="<ccp:label name="button.sync">Sync</ccp:label>" class="submit" onClick="javascript:setField('startSync')"/>
			<input type="hidden" name="startSync" id="startSync" value="false">
         </ccp:evaluate>
    	<ccp:evaluate if="<%= !StringUtils.hasText(applicationPrefs.get("CONCURSIVE_CRM.SERVER")) %>">
			  <div class="portlet-section-body">
			    <div class="formContainer">
			        <fieldset id="site-information">
			          <legend>CRM Connection Information</legend>
			          <label for="serverURL">Server URL<span class="required">*</span></label>
			          <input type="text" name="serverURL" id="serverURL" value="<%= toHtmlValue((String)request.getAttribute("serverURL")) %>" />
			          <label for="apiClientId">API Client Id<span class="required">*</span></label>
			          <input type="text" name="apiClientId" id="apiClientId" value="<%= toHtmlValue((String)request.getAttribute("apiClientId")) %>" />
			          <label for="apiCode">API Code<span class="required">*</span></label>
			          <input type="text" name="apiCode" id="apiCode" value="<%= toHtmlValue((String)request.getAttribute("apiCode")) %>" />
			        </fieldset>
			        <input type="submit" name="Save" value="<ccp:label name="button.saveAndSync">Save and Sync</ccp:label>" class="submit" onClick="javascript:setField('saveConnectionDetails')"/>
					<input type="hidden" name="saveConnectionDetails" id="saveConnectionDetails" value="false">
			    </div>
			   </div> 
		</ccp:evaluate>         
    </ccp:evaluate>
    <ccp:evaluate if="<%= syncStatus.size() > 0 %>">
      <p>
      <%
        int size = syncStatus.size();
        int count = size;
        while (count > 0 ){
          String syncStatusRecord = (String)syncStatus.get(--count);
      %>
        <%= syncStatusRecord %><br />
      <%
        }
      %>
      </p>
    </ccp:evaluate>
    </div>
		<input type="hidden" name="dosubmit" value="true">
  </form>
</div>
