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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="editList" class="com.concursive.connect.web.utils.HtmlSelect" scope="request"/>
<jsp:useBean id="subTitleKey" class="java.lang.String" scope="request" />
<jsp:useBean id="subTitle" class="java.lang.String" scope="request"/>
<jsp:useBean id="returnUrl" class="java.lang.String" scope="request"/>
<%@ include file="initPage.jsp" %>
<script language="JavaScript" type="text/javascript" src="<%= RequestUtils.getAbsoluteServerUrl(request) %>/javascript/editListForm.js"></script>
<script language="JavaScript" type="text/javascript">
  function doCheck() {
    var test = document.modifyList.selectedList;
    if (test != null) {
      return selectAllOptions(document.modifyList.selectedList);
    }
  }
</script>
<body onLoad="document.forms['modifyList'].newValue.focus();">
<div class="formContainer">
  <form name="modifyList" method="post" action="<%= returnUrl %>" onSubmit="return doCheck();">
    <fieldset id="modifyList">
      <legend><ccp:label name="<%= subTitleKey %>"><%= toHtml(subTitle) %></ccp:label></legend>
      <label for="newValue"><ccp:label name="utilEditList.description">Description</ccp:label>
      <input type="text" id="newValue" name="newValue" value="" size="25" maxlength="125">
      <input type="button" class="submit" name="addButton" value="<ccp:label name="button.addgt">Add ></ccp:label>" onclick="addValues()">
    </fieldset>
    <fieldset id="sortOrder">
      <legend>Sort Lists</legend>
      <fieldset>
			 <%
					editList.setSelectSize(8);
					editList.setMultiple(true);
					editList.setSelectStyle("width: 90%;");
        %>
				<%= editList.getHtml("selectedList",0) %>
      </fieldset>
        <input type="button" class="submit" value="<ccp:label name="button.up">Up</ccp:label>" onclick="moveOptionUp(document.modifyList.selectedList)">
        <input type="button" class="submit" value="<ccp:label name="button.down">Down</ccp:label>" onclick="moveOptionDown(document.modifyList.selectedList)">
        <input type="button" class="submit" value="<ccp:label name="button.sort">Sort</ccp:label>" onclick="sortSelect(document.modifyList.selectedList)">
        <input type="button" class="submit" value="<ccp:label name="button.rename">Rename</ccp:label>" onclick="switchToRename()">
        <input type="button" class="submit" value="<ccp:label name="button.remove">Remove</ccp:label>" onclick="removeValues()">
        
        <%--
        <input type="button" value="Enable" onclick="enable()">
        --%>
         
    </fieldset>
    <input type="hidden" name="selectNames" value="" />
    <ccp:evaluate if="<%= request.getParameter(\"popup\") != null %>">
      <input type="hidden" name="popup" value="true" />
    </ccp:evaluate>
    <ccp:evaluate if="<%= request.getParameter(\"form\") != null %>">
      <input type="hidden" name="form" value="<%= request.getParameter("form") %>" />
    </ccp:evaluate>
    <ccp:evaluate if="<%= request.getParameter(\"field\") != null %>">
      <input type="hidden" name="field" value="<%= request.getParameter("field") %>" />
    </ccp:evaluate>
    <ccp:evaluate if="<%= request.getParameter(\"projectId\") != null %>">
      <input type="hidden" name="projectId" value="<%= request.getParameter("projectId") %>" />
    </ccp:evaluate>
    <ccp:evaluate if="<%= request.getParameter(\"pid\") != null %>">
      <input type="hidden" name="pid" value="<%= request.getParameter("pid") %>" />
    </ccp:evaluate>
    <input type="submit" class="submit" value="<ccp:label name="button.saveChanges">Save Changes</ccp:label>" />
    <ccp:evaluate if="<%= request.getParameter(\"popup\") != null %>">
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.close()" />
    </ccp:evaluate>
	</form>
</div>
</body>
