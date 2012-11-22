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
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.commons.text.StringUtils" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="classified" class="com.concursive.connect.web.modules.classifieds.dao.Classified" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.classifieds.dao.ClassifiedCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<body onLoad="document.inputForm.title.focus();">
  <script language="JavaScript" type="text/javascript">
    <%-- Validations --%>
    function checkForm(form) {
      var formTest = true;
      var messageText = "";
      //Check required fields
      if (document.inputForm.title.value == "") {
        messageText += "- Title is a required field\r\n";
        formTest = false;
      }
      if (document.inputForm.description.value == "" ||
        document.inputForm.description.value == "<p>&nbsp;</p>") {
        messageText += "- Description is a required field\r\n";
        formTest = false;
      }
      //Check date fields
      if (document.inputForm.publishDate && (document.inputForm.publishDate.value != "") && (!checkDate(document.inputForm.publishDate.value))) {
        messageText += "- Publish date was not properly entered\r\n";
        formTest = false;
      }
      if (document.inputForm.expirationDate && (document.inputForm.expirationDate.value != "") && (!checkDate(document.inputForm.expirationDate.value))) {
        messageText += "- Expiration date was not properly entered\r\n";
        formTest = false;
      }

      if (!formTest) {
        messageText = "The message could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
        alert(messageText);
        return false;
      } else {
        if (form.save.value != 'Please Wait...') {
          form.save.value='Please Wait...';
          form.save.disabled = true;
          return true;
        } else {
          return false;
        }
      }
    }
    function setAttachmentList(newVal) {
      document.getElementById("attachmentList").value = newVal;
    }
    function setAttachmentText(newVal) {
      document.getElementById("attachmentText").value = newVal;
    }
  </script>
  <div class="portletWrapper">
    <div class="formContainer">
      <portlet:actionURL var="saveFormUrl">
        <portlet:param name="portlet-command" value="saveForm"/>
      </portlet:actionURL>
      <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
        <fieldset id="<%= classified.getId() == -1 ? "Add" : "Update" %>">
          <legend>
            <%= classified.getId() == -1 ? "New Post" : "Update an Existing Post" %>
          </legend>
          <%= showError(request, "actionError") %>
          <div class="portlet-message-info"><p>Ads auto-expire in 45 days</p></div>

          <label for="title"><ccp:label name="projectsCenterClassifieds.add.title">Title</ccp:label> <span class="required">*</span></label>
          <%= showAttribute(request, "titleError") %>
          <input type="text" id="title" name="title" size="60" maxlength="255" value="<%= toHtmlValue(classified.getTitle()) %>" />
          <span class="characterCounter">255 characters max</span>

          <label for="content"><ccp:label name="projectsCenterClassifieds.add.description">Description <span class="required">*</span></ccp:label></label>
          <%= showAttribute(request, "descriptionError") %>
          <textarea rows="5" cols="60" id="content" name="description"><c:out value="<%= classified.getDescription() %>" /></textarea>
          <%--
          <ccp:permission name="project-classifieds-admin">
            <fieldset>
              <legend>
                <ccp:label name="projectsCenterClassifieds.add.startDate">Publish Date/Time</ccp:label>
              </legend>
              <%= showAttribute(request, "publishDateError") %>
              <input type="text" name="publishDate" id="publishDate" size="10" value="<ccp:tz timestamp="<%= classified.getPublishDate() %>" dateOnly="true"/>">
              <a href="javascript:popCalendar('inputForm', 'publishDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
              <ccp:label name="projectsCenterClassifieds.add.at">at</ccp:label>
              <ccp:timeSelect baseName="publishDate" value="<%= classified.getPublishDate() %>" timeZone="<%= User.getTimeZone() %>"/>
              <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
            </fieldset>
            <fieldset>
              <legend>
                <ccp:label name="projectsCenterClassifieds.add.archiveTime">Expiration Date/Time</ccp:label>
              </legend>
              <%= showAttribute(request, "expirationDateError") %>
              <input type="text" name="expirationDate" id="expirationDate" size="10" value="<ccp:tz timestamp="<%= classified.getExpirationDate() %>" dateOnly="true"/>">
              <a href="javascript:popCalendar('inputForm', 'expirationDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
              <ccp:label name="projectsCenterClassifieds.add.at">at</ccp:label>
              <ccp:timeSelect baseName="expirationDate" value="<%= classified.getExpirationDate() %>" timeZone="<%= User.getTimeZone() %>"/>
              <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
            </fieldset>
          </ccp:permission>
          --%>
          <c:if test="<%= categoryList.size() > 0 %>">
            <label for="categoryId"><ccp:label name="projectsCenterClassifieds.add.category">Category</ccp:label></label>
            <%= showAttribute(request, "categoryIdError") %>
            <%= categoryList.getHtmlSelect("categoryId", classified.getCategoryId()) %>
          </c:if>
          <label>File Attachments</label>
          <%
            if (classified.getFiles() != null) {
              Iterator files = classified.getFiles().iterator();
              while (files.hasNext()) {
                FileItem thisFile = (FileItem)files.next();
          %>
                <%= toHtml(thisFile.getClientFilename()) %><ccp:evaluate if="<%= files.hasNext() %>">;</ccp:evaluate>
          <%
              }
            }
          %>
          <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
          <a href="${ctx}/FileAttachments.do?command=ShowForm&pid=<%= project.getId() %>&lmid=<%= Constants.PROJECT_CLASSIFIEDS_FILES %>&liid=<%= classified.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&popup=true"
             rel="shadowbox" title="Share an attachment">Attach Files</a>
          <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
          <input type="hidden" id="attachmentList" name="attachmentList" value="" />
        </fieldset>
        <input type="submit" class="submit" name="save" value="<ccp:label name="button.save">Save</ccp:label>" />
        <c:choose>
          <c:when test="${'true' eq param.popup || 'true' eq popup}">
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:when>
          <c:otherwise>
            <portlet:renderURL var="cancelUrl">
              <portlet:param name="portlet-action" value="show"/>
              <portlet:param name="portlet-object" value="classifieds"/>
            </portlet:renderURL>
            <a href="${cancelUrl}" class="cancel">Cancel</a>
          </c:otherwise>
        </c:choose>
        <c:if test="${classified.id ne -1}">
		<ccp:permission name="project-classifieds-admin">
			<ccp:evaluate if="<%= !classified.isExpired() %>">
	           <portlet:actionURL var="expireUrl" portletMode="view">
	             <portlet:param name="portlet-value" value="${classified.id}"/>
	             <portlet:param name="portlet-command" value="expire"/>
	           </portlet:actionURL>
	           <a href="javascript:confirmDelete('${expireUrl}');" class="cancel">Expire Now</a>
			</ccp:evaluate>
		</ccp:permission>
		</c:if>
        <input type="hidden" name="id" value="<%= classified.getId() %>" />
        <input type="hidden" name="modified" value="<%= classified.getModified() %>" />
        <c:if test="${'true' eq param.popup || 'true' eq popup}">
          <input type="hidden" name="popup" value="true" />
        </c:if>
      </form>
    </div>
  </div>
</body>

