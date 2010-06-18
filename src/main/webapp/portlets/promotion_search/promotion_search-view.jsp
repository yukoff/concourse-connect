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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<script language="JavaScript" type="text/javascript">
  function checkSearchInputForm<portlet:namespace/>(form) {
    var formTest = true;
    var messageText = "";
    if (document.<portlet:namespace/>inputForm.query.value.trim() == "") {
        messageText += "- A search text is required\r\n";
        formTest = false;
    }
    if (!formTest) {
        messageText = "The message could not be sent.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
        alert(messageText);
        return false;
    } else {
      if (form.go.value != 'Please Wait...') {
        // Tell the user to wait
        form.go.value='Please Wait...';
        form.go.disabled = true;

		    document.<portlet:namespace/>clearForm.clearSearch.disabled = true;
        // find one or more spinners
        var uItems = YAHOO.util.Dom.getElementsByClassName("submitSpinner");
        for (var j = 0; j < uItems.length; j++) {
          YAHOO.util.Dom.setStyle(uItems[j], "display", "inline");
        }
        return true;
      } else {
        return false;
      }
    }
  }
</script>
<h3>Search Promotions</h3>
<div class="formContainer">
<%
  boolean useLocations = "true".equals(applicationPrefs.get("USE_LOCATIONS"));
%>
  <form method="GET" name="<portlet:namespace/>inputForm" action="${ctx}${pageUrl}" onSubmit="try {return checkSearchInputForm<portlet:namespace/>(this);}catch(e){return true;}">
    <c:if test="${!empty sort}">
      <input type="hidden" name="sort" value="<c:out value="${sort}"/>" />
    </c:if>
    <fieldset id="PromotionSearchFields">
      <label for="query">Search for</label>
      <input type="text" size="20" name="query" value="<c:out value="${query}"/>" />
          <ccp:evaluate if="<%= useLocations %>">
            <label for="location">Near</label>
            <input type="text" size="20" name="location" value="<c:out value="${location}"/>" />
          </ccp:evaluate>
      <input alt="Search" name="go" value="Go" type="submit" />
      <a href="${ctx}/page/promotions/all">reset</a>
      <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
    </fieldset>
  </form>
</div>
