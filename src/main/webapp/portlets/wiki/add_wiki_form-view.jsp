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
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" />
<%--@elvariable id="title" type="java.lang.String"--%>
<%--@elvariable id="templateList" type="com.concursive.connect.web.modules.wiki.dao.WikiTemplateList"--%>
<script language="JavaScript" type="text/javascript">
  function checkInputForm<portlet:namespace/>(form) {
    var formTest = true;
    var messageText = "";
    if (document.inputForm<portlet:namespace/>.title.value.trim() == "") {
        messageText += "- A title is required\r\n";
        formTest = false;
    }
    if (!formTest) {
        messageText = "The wiki could not be added.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
        alert(messageText);
        return false;
    } else {
      return true;
    }
  }
</script>
<h3 class="portletHeader"><c:out value="${title}"/></h3>
<div class="formContainer">
  <portlet:actionURL var="saveFormUrl">
    <portlet:param name="portlet-command" value="save"/>
  </portlet:actionURL>
  <form method="POST" name="inputForm<portlet:namespace/>" action="${saveFormUrl}" onSubmit="try {return checkInputForm<portlet:namespace/>(this);}catch(e){return true;}">
    <label for="title<portlet:namespace/>">Title</label>
    <input id="title<portlet:namespace/>" name="title" type="text" class="input longInput" maxlength="200" />
    <span class="characterCounter">200 characters max</span>
    <c:if test="${!empty templateList}">
    <label for="templateId">Template (optional)</label>
    <span>
      <select name="templateId" id="templateId">
        <option value="-1">----- Choose one -----</option>
        <c:forEach var="wikiTemplate" items="${templateList}">
          <option value="${wikiTemplate.id}"><c:out value="${wikiTemplate.title}" /></option>
        </c:forEach>
      </select>
    </span>
    </c:if>
    <input type="submit" name="save" class="submit" value="<ccp:label name="button.submit">Submit</ccp:label>" />
  </form>
</div>
