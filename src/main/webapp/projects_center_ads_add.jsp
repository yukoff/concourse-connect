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
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="promotion" class="com.concursive.connect.web.modules.promotions.dao.Ad" scope="request"/>
<jsp:useBean id="categoryList" class="com.concursive.connect.web.modules.promotions.dao.AdCategoryList" scope="request"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<body onLoad="document.inputForm.heading.focus();">
	<script language="JavaScript" type="text/javascript">
      <%-- Validations --%>
      function checkForm(form) {
        var formTest = true;
        var messageText = "";
        //Check required fields
        if (document.inputForm.heading.value == "") {
            messageText += "- Heading is a required field\r\n";
            formTest = false;
        }
        if (document.inputForm.content.value == "" ||
            document.inputForm.content.value == "<p>&nbsp;</p>") {
            messageText += "- Content is a required field\r\n";
            formTest = false;
        }
        //Check date fields
        if ((document.inputForm.publishDate.value != "") && (!checkDate(document.inputForm.publishDate.value))) {
            messageText += "- Publish date was not properly entered\r\n";
            formTest = false;
        }
        if ((document.inputForm.expirationDate.value != "") && (!checkDate(document.inputForm.expirationDate.value))) {
            messageText += "- Expiration date was not properly entered\r\n";
            formTest = false;
        }
        if (!formTest) {
            messageText = "The Ad. could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
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
  </script>
  <div class="portletWrapper">
    <div class="formContainer">
      <portlet:actionURL var="saveFormUrl">
        <portlet:param name="portlet-command" value="saveForm"/>
      </portlet:actionURL>
      <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="return checkForm(this);">
        <fieldset id="<%= promotion.getId() == -1 ? "Add" : "Update" %>">
          <legend>
            <%= promotion.getId() == -1 ? "New Ad" : "Update an Existing Ad" %>
          </legend>
          <%= showError(request, "actionError") %>

          <label for="heading"><ccp:label name="projectsCenterAds.add.heading">Heading <span class="required">*</span></ccp:label></label>
          <%= showAttribute(request, "headingError") %>
          <input type="text" name="heading" id="heading" value="<%= toHtmlValue(promotion.getHeading()) %>" maxlength="25" />
          <span class="characterCounter">25 characters max</span>

          <label for="briefDescription1">Brief Description Line One</label>
          <%= showAttribute(request, "briefDescription1Error") %>
          <input type="text" name="briefDescription1" id="briefDescription1" value="<%= toHtmlValue(promotion.getBriefDescription1()) %>" maxlength="35" />
          <span class="characterCounter">35 characters max</span>

          <label for="briefDescription2">Brief Description Line Two</label>
          <%= showAttribute(request, "briefDescription2Error") %>
          <input type="text" name="briefDescription2" id="briefDescription2" value="<%= toHtmlValue(promotion.getBriefDescription2()) %>" maxlength="35" />
          <span class="characterCounter">35 characters max</span>

          <ccp:permission name="project-ads-admin">
            <%-- Start Date --%>
            <fieldset>
              <legend>
              <ccp:label name="projectsCenterAds.add.startDate">Publish Date/Time</ccp:label>
              <%= showAttribute(request, "publishDateError") %>
              </legend>
              <input type="text" name="publishDate" id="publishDate" value="<ccp:tz timestamp="<%= promotion.getPublishDate() %>" dateOnly="true"/>">
              <a href="javascript:popCalendar('inputForm', 'publishDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
              <ccp:label name="projectsCenterAds.add.at">at</ccp:label>
              <ccp:timeSelect baseName="publishDate" value="${promotion.publishDate}" timeZone="<%= User.getTimeZone() %>"/>
              <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
            </fieldset>

            <%-- End Date --%>
            <fieldset>
              <legend>
                <ccp:label name="projectsCenterAds.add.archiveTime">Expiration Date/Time (Promotions
are limited to 45 days)</ccp:label>
              <%= showAttribute(request, "expirationDateError") %>
              </legend>
              <input type="text" name="expirationDate" id="expirationDate" value="<ccp:tz timestamp="<%= promotion.getExpirationDate() %>" dateOnly="true"/>">
              <a href="javascript:popCalendar('inputForm', 'expirationDate', '<%= User.getLocale().getLanguage() %>', '<%= User.getLocale().getCountry() %>');"><img src="<%= ctx %>/images/icons/stock_form-date-field-16.gif" border="0" align="absmiddle"></a>
              <ccp:label name="projectsCenterAds.add.at">at</ccp:label>
              <ccp:timeSelect baseName="expirationDate" value="${promotion.expirationDate}" timeZone="<%= User.getTimeZone() %>"/>
              <ccp:tz timestamp="<%= new Timestamp(System.currentTimeMillis()) %>" pattern="z"/>
            </fieldset>
          </ccp:permission>

          <c:if test="<%= categoryList.size() > 1 %>">
            <label for="categoryId"><ccp:label name="projectsCenterAds.add.category">Category</ccp:label></label>
            <%= showAttribute(request, "categoryIdError") %>
            <%= categoryList.getHtmlSelect("categoryId", promotion.getCategoryId()) %>
          </c:if>

          <label for="content"><ccp:label name="projectsCenterAds.add.contents">Contents <span class="required">*</span></ccp:label></label>
          <%= showAttribute(request, "contentError") %>
          <textarea rows="5" id="content" name="content"><c:out value="<%= promotion.getContent() %>" /></textarea>
          <span class="characterCounter">2048 characters max</span>

          <label for="webPage">Display URL</label>
          <%= showAttribute(request, "webPageError") %>
          <input type="text" name="webPage" id="webPage" value="<%= toHtmlValue(promotion.getWebPage()) %>" maxlength="255" />
          <span class="characterCounter">255 characters max</span>

          <label for="destinationUrl">Destination URL (include http:// or https://)</label>
          <%= showAttribute(request, "destinationUrlError") %>
          <input type="text" name="destinationUrl" id="destinationUrl" value="<%= toHtmlValue(promotion.getDestinationUrl()) %>" maxlength="1024" />
          <span class="characterCounter">1024 characters max</span>

        </fieldset>
        <input type="submit" class="submit" name="save" value="<ccp:label name="button.save">Save</ccp:label>" />
        <c:choose>
          <c:when test="${'true' eq param.popup || 'true' eq popup}">
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:when>
          <c:otherwise>
            <portlet:renderURL var="cancelUrl">
              <portlet:param name="portlet-action" value="show"/>
              <portlet:param name="portlet-object" value="promotions"/>
            </portlet:renderURL>
            <a href="${cancelUrl}" class="cancel">Cancel</a>
          </c:otherwise>
        </c:choose>
        <input type="hidden" name="id" value="${promotion.id}" />
        <input type="hidden" name="modified" value="<%= promotion.getModified() %>" />
        <c:if test="${'true' eq param.popup || 'true' eq popup}">
          <input type="hidden" name="popup" value="true" />
        </c:if>
      </form>
    </div>
  </div>
</body>
