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
<%@ include file="initPage.jsp" %>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem" %>
<jsp:useBean id="badge" class="com.concursive.connect.web.modules.badges.dao.Badge" scope="request" />
<jsp:useBean id="badgeCategoryId" class="java.lang.String" scope="request" />
<jsp:useBean id="fileItemList" class="com.concursive.connect.web.modules.documents.dao.FileItemList" scope="request"/>
<jsp:useBean id="badgeCategoryList" class="com.concursive.connect.web.modules.badges.dao.BadgeCategoryList" scope="request" />
<jsp:useBean id="projectCategory" class="com.concursive.connect.web.modules.profile.dao.ProjectCategory" scope="request"/>
<script language="JavaScript" type="text/javascript">
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (form.title.value == "") {
      messageText += "- Title is required\r\n";
      formTest = false;
    }
    if (form.categoryId.value == -1) {
      messageText += "- Badge category is required\r\n";
      formTest = false;
    }
    if (!formTest) {
      messageText = "The form could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
      alert(messageText);
      return false;
    } else {
      return true;
    }
  }
  function setAttachmentList(newVal) {
    document.getElementById("attachmentList").value = newVal;
  }
  function setAttachmentText(newVal) {
    document.getElementById("attachmentText").value = newVal;
  }
</script>
<body onLoad="document.inputForm.title.focus()">
  <form method="post" name="inputForm" action="<%= ctx %>/AdminBadges.do?command=Save&auto-populate=true" onSubmit="return checkForm(this);">
    <%= showError(request, "actionError", false) %>
    <input type="submit" value="Save" />
    <input type="button" value="Cancel" onClick="window.location.href='<%= ctx %>/AdminBadges.do?command=List&badgeCategoryId=<%=badgeCategoryId %>'" /><br /><br />
    <table cellpadding="4" cellspacing="0" width="100%" class="pagedList">
      <thead>
        <tr>
          <th colspan="2">
            <%= badge.getId() == -1 ? "Add" : "Update"%> Badge Information
          </th>
        </tr>
      </thead>
      <tbody>
        <tr class="containerBody">
          <td class="formLabel">
            Title
          </td>
          <td>
            <input type="text" name="title" size="30" maxlength="80" value="<%= toHtmlValue(badge.getTitle()) %>"><span class="required">*</span>
        &nbsp;<%= showAttribute(request,"titleError") %>
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Description
          </td>
          <td>
            <textarea rows="3" cols="50" name="description"><c:out value="<%= badge.getDescription() %>" /></textarea>
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Project Category
          </td>
          <td>
            <c:out value="${projectCategory.label}" />
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Badge Category
          </td>
          <td>
            <%= badgeCategoryList.getHtmlSelect("categoryId", (badge.getCategoryId() == -1?Integer.parseInt(badgeCategoryId): badge.getCategoryId())) %><span class="required">*</span>
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Logo
          </td>
          <td>
            <%
              Iterator files = fileItemList.iterator();
              while (files.hasNext()) {
                FileItem thisFile = (FileItem)files.next();
                if (thisFile.getId() == badge.getLogoId()){
            %>
          <%= thisFile.getFullImageFromAdmin(ctx) %>&nbsp;
            <%
                }
              }
            %>
            <ccp:evaluate if="<%= fileItemList.size() > 0 %>"><br /></ccp:evaluate>
            <img src="<%= ctx %>/images/icons/stock_navigator-reminder-16.gif" border="0" align="absmiddle" />
            <a href="${ctx}/FileAttachments.do?command=ShowForm&lmid=<%= Constants.BADGE_FILES %>&liid=<%= badge.getId() %>&selectorId=<%= FileItem.createUniqueValue() %>&selectorMode=single"
               rel="shadowbox" title="Share an attachment">
            <% if (badge.getLogoId() != -1) { %>
              Replace Image
            <%} else {%>
              Attach Image
            <%}%>
            </a>
            <input type="hidden" id="attachmentList" name="attachmentList" value="" />
            <input type="text" id="attachmentText" name="attachmentText" value="" size="45" disabled="true" />
            <% if (badge.getLogoId() != -1) { %>
              <input type="hidden" name="logoId" value="<%= badge.getLogoId() %>" />
            <%}%>
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Email 1
          </td>
          <td>
            <input type="text" name="email1" size="40" maxlength="255" value="<%= toHtmlValue(badge.getEmail1()) %>">
            (primary)
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Email 2
          </td>
          <td>
            <input type="text" name="email2" size="40" maxlength="255" value="<%= toHtmlValue(badge.getEmail2()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Email 3
          </td>
          <td>
            <input type="text" name="email3" size="40" maxlength="255" value="<%= toHtmlValue(badge.getEmail3()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Business Phone
          </td>
          <td>
            <input type="text" name="businessPhone" size="20" maxlength="30" value="<%= toHtmlValue(badge.getBusinessPhone()) %>">
            ext <input type="text" size="5" name="businessPhoneExt" maxlength="30" value="<%= toHtmlValue(badge.getBusinessPhoneExt()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Web Page
          </td>
          <td>
            <input type="text" name="webPage" size="35" maxlength="80" value="<%= toHtmlValue(badge.getWebPage()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Address Line 1
          </td>
          <td>
            <input type="text" name="addrline1" size="35" maxlength="80" value="<%= toHtmlValue(badge.getAddrline1()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Address Line 2
          </td>
          <td>
            <input type="text" name="addrline2" size="35" maxlength="80" value="<%= toHtmlValue(badge.getAddrline2()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Address Line 3
          </td>
          <td>
            <input type="text" name="addrline3" size="35" maxlength="80" value="<%= toHtmlValue(badge.getAddrline3()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            City
          </td>
          <td>
            <input type="text" name="city" size="35" maxlength="80" value="<%= toHtmlValue(badge.getCity()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            State/Province
          </td>
          <td>
            <input type="text" name="state" size="35" maxlength="80" value="<%= toHtmlValue(badge.getState()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Country
          </td>
          <td>
            <input type="text" name="country" size="35" maxlength="80" value="<%= toHtmlValue(badge.getCountry()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Zip/Postal Code
          </td>
          <td>
            <input type="text" name="postalCode" size="35" maxlength="12" value="<%= toHtmlValue(badge.getPostalCode()) %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Latitude
          </td>
          <td>
            <input type="text" name="latitude" size="35" maxlength="12" value="<%= badge.getLatitude() %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td class="formLabel">
            Longitude
          </td>
          <td>
            <input type="text" name="longitude" size="35" maxlength="12" value="<%= badge.getLongitude() %>">
          </td>
        </tr>
        <tr class="containerBody">
          <td nowrap class="formLabel">
            System Assigned
          </td>
          <td>
            <input type="checkbox" name="systemAssigned" value="true" <%= badge.getSystemAssigned()?" CHECKED":"" %> />
          </td>
        </tr>
      </tbody>
    </table>
    <input type="hidden" name="id" value="<%= badge.getId() %>" />
    <input type="hidden" name="enteredBy" value="<%= badge.getEnteredBy() %>" />
    <input type="hidden" name="modifiedBy" value="<%= badge.getModifiedBy() %>" />
    <input type="hidden" name="entered" value="<%= badge.getEntered() %>" />
    <input type="hidden" name="modified" value="<%= badge.getModified() %>" />
    <input type="submit" value="Save" />
    <input type="button" value="Cancel" onClick="window.location.href='<%= ctx %>/AdminBadges.do?command=List&badgeCategoryId=<%=badgeCategoryId %>'" />
  </form>
</body>
