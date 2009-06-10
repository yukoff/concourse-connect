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
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<script type="text/javascript">
  // preview function that reads css input and opens url with css param  
  function preview(){
    document.previewForm.style.value = document.inputForm.style.value;
    document.previewForm.submit();
  }

</script>
<div class="portletWrapper">
  <h1>
    <ccp:label name="projectsCenterSetup.configureLookFeel">Configure Look and Feel</ccp:label>
    <span><a href="${ctx}/show/${project.uniqueId}/setup"><ccp:label name="projectsCenterSetup.customize.setup">Back to Setup</ccp:label></a></span>
  </h1>
  <div class="formContainer">
    <h2><ccp:label name="projectsCenterSetup.style.updateStyle">Update Style</ccp:label></h2>
    <form method="POST" action="<%= ctx %>/show/<%= project.getUniqueId() %>" name="previewForm" target="_blank">
     <input type="hidden" id="previewStyle" name="style" />
    </form>
    <form method="POST" name="inputCSSForm" action="<%= ctx %>/ProjectManagement.do?command=UpdateStyle&auto-populate=true">
      <fieldset>
        <legend><ccp:label name="projectsCenterSetup.style.css">CSS</ccp:label></legend>
        <c:if test="<%= project.getStyleEnabled()%>"><c:set var="styleEnabledChecked">checked</c:set></c:if>
        <input type="hidden" name="id" value="<%= project.getId() %>">
        <input type="hidden" name="modified" value="<%= project.getModified() %>">
        <textarea rows="30" cols="100" id="style" name="style"><c:if test="${!empty project.style}"><%= project.getStyle() %></c:if></textarea>
        <label><input type="checkbox" name="styleEnabled" value="true" ${styleEnabledChecked}/><ccp:label name="projectsCenterSetup.style.styleEnabled">Enable Style</ccp:label></label>
      </fieldset>
      
      <input type="button" value="<ccp:label name="button.preview">Preview</ccp:label>" onClick="document.previewForm.style.value=document.inputCSSForm.style.value;document.previewForm.submit();" class="submit"/>
      <input type="submit" value="<ccp:label name="button.update">Update</ccp:label>" class="submit"/>
      <input type="button" value="<ccp:label name="button.clear">Clear</ccp:label>" onClick="document.inputCSSForm.style.value=''"  class="cancel"/>
      <input type="button" class="cancel" value="<ccp:label name="button.cancel">Cancel</ccp:label>" onClick="window.location.href='${ctx}/show/${project.uniqueId}/setup'">
    </form>
    <jsp:include page="projects_center_style_images.jsp" flush="true"/>
  </div>
</div>

