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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="lookupContributionList" class="com.concursive.connect.web.modules.contribution.dao.LookupContributionList" scope="request" />
<%@ include file="initPage.jsp" %>
<script type="text/javascript" language="JavaScript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    
    //validate fields
    for (i=0;i<form.elements.length;i++) {
    	//if the type of control is textbox
      if (form.elements[i].type == "text") {
          //if empty replace with 0
          if (form.elements[i].value.trim().length < 1) {
        	  form.elements[i].value = 0;
          }

          //check if valid positive integer
          if ((isNaN(form.elements[i].value) || form.elements[i].value < 0 || 
              parseInt(form.elements[i].value) != Number(form.elements[i].value)) && formTest) {
        	  messageText += "- Points must be a positive integer value\r\n";
        	  formTest = false;
        	  form.elements[i].focus();
        	  form.elements[i].select();
          }
      }
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
</script>
<div class="admin-portlet">
  <div class="portlet-section-header">
    <h1>Contribution Points System</h1>
    <p>
      <a href="${ctx}/admin">System Administration</a> &gt;
      <a href="${ctx}/AdminApplication.do">Manage Application Settings</a> &gt; Contribution Points System
    </p>
  </div>
  <div class="portlet-section-body">
    <div class="formContainer">
    <form name="inputForm" method="post" action="<%= ctx %>/AdminContributionPointsSystem.do?command=Save" onSubmit="return checkForm(this);">
      <fieldset>
        <legend>Award points to users based on participation in the following areas...</legend>
        <c:if test="${!empty lookupContributionList}">
          <table>
            <thead>
              <tr>
                <th>Description</th>
                <th>Points</th>
              </tr>
            </thead>
            <tbody>
            <c:forEach var="lookupContribution" items="${lookupContributionList}">
            <jsp:useBean id="lookupContribution" class="com.concursive.connect.web.modules.contribution.dao.LookupContribution" />
              <tr>
                <td><label for="${lookupContribution.id}"><c:out value="${lookupContribution.description}"/></label></td>
                <td>
                  <c:set var="pointsAwarded" value='<%= request.getAttribute(""+lookupContribution.getId()) %>'/>
                  <c:if test="${empty pointsAwarded}">
                    <c:set var="pointsAwarded" value="${lookupContribution.pointsAwarded}"/>
                  </c:if>
                  <input type="text" name="${lookupContribution.id}" id="${lookupContribution.id}" size="3" maxlength="5" value="<c:out value="${pointsAwarded}"/>" />
                  <%= showAttribute(request, "Error"+lookupContribution.getId()) %>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
          <input type="hidden" name="token" value="${clientType.token}" />
          <input type="submit" value="<ccp:label name="button.save">Save</ccp:label>">
          <input type="button" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='${ctx}/AdminApplication.do'">
        </c:if>
        <c:if test="${empty lookupContributionList}">
          The contribution points system has not been installed.
        </c:if>
      </fieldset>
    </form>
    </div>
  </div>
</div>
