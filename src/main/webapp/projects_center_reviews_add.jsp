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
<%@page import="com.concursive.connect.web.modules.ModuleUtils"%>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="projectRating" class="com.concursive.connect.web.modules.reviews.dao.ProjectRating" scope="request"/>
<jsp:useBean id="popularTags" class="com.concursive.connect.web.modules.common.social.tagging.dao.TagList" scope="request"/>
<jsp:useBean id="userTags" class="com.concursive.connect.web.modules.common.social.tagging.dao.TagLogList" scope="request"/>
<jsp:useBean id="userRecentTags" class="com.concursive.connect.web.modules.common.social.tagging.dao.TagList" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="clientType" class="com.concursive.connect.web.utils.ClientType" scope="session"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<script language="JavaScript" type="text/javascript">
  <%-- Onload --%>
  YAHOO.util.Event.onDOMReady(function() { document.inputForm.title.focus(); });
  <%-- Validations --%>
  function checkForm(form) {
    var formTest = true;
    var messageText = "";
    //Check required fields
    if (document.inputForm.title.value == "") {
        messageText += "- Title is required\r\n";
        formTest = false;
    }
    if (document.inputForm.rating.value == "-1") {
        messageText += "- Rating is required\r\n";
        formTest = false;
    }
    if (document.inputForm.comment.value == "" ||
        document.inputForm.comment.value == "<p>&nbsp;</p>") {
        messageText += "- Review is required\r\n";
        formTest = false;
    }
    if (!(document.inputForm.tags.value == "" ||
        document.inputForm.tags.value == "<p>&nbsp;</p>")) {
        var tagString = document.inputForm.tags.value;
        var tags = tagString.split(",");
		for (var i=0; i < tags.length; i++) {
			temp = tags[i];
			if (temp.length > 128){
		        messageText += "- One of the tags is longer than 128 characters\r\n";
	      		formTest = false;
	      	}
	  	 }
    }
    if (!formTest) {
        messageText = "The review could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
        alert(messageText);
        return false;
    } else {
      if (form.save.value != 'Please Wait...') {
        // Tell the user to wait
        form.save.value='Please Wait...';
        form.save.disabled = true;
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
  <div class="formContainer">
    <portlet:actionURL var="saveFormUrl">
      <portlet:param name="portlet-command" value="saveForm"/>
    </portlet:actionURL>
    <form method="POST" name="inputForm" action="${saveFormUrl}" onSubmit="try {return checkForm(this);}catch(e){return true;}">
      <fieldset id="<%= projectRating.getId() == -1 ? "Add" : "Update" %>">
        <legend>
          <ccp:evaluate if="<%= projectRating.getId() == -1 %>">
            <ccp:label name="projectsCenterReviews.add.add">Add a Review</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= projectRating.getId() != -1 %>">
            <ccp:label name="projectsCenterReviews.add.update">Update an Existing Review</ccp:label>
          </ccp:evaluate>
        </legend>
        <%= showError(request, "actionError") %>
        <label for="title"><ccp:label name="projectsCenterReviews.add.titleForReview">Please enter a title for the review <span class="required">*</span></ccp:label></label>
        <%= showAttribute(request, "titleError") %>
        <input type="text" id="title" name="title" value="<%= toHtmlValue(projectRating.getTitle()) %>" maxlength="100" />
        <span class="characterCounter">100 characters max</span>
        <label for="comment"><ccp:label name="projectsCenterReviews.add.typeInYourreview">Type in your review <span class="required">*</span></ccp:label></label>
        <%= showAttribute(request, "commentError") %>
        <textarea id="comment" name="comment"><%= toString(projectRating.getComment()) %></textarea>
        <div class="outline">
          <label for="rating"><ccp:label name="projectsCenterReviews.add.howDoYouRate">How do you rate this item? <span class="required">*</span></ccp:label></label>
          <%= showAttribute(request, "ratingError") %>
          <span>
            <portlet:renderURL var="ratingUrl" windowState="maximized">
              <portlet:param name="portlet-command" value="setProjectRating"/>
              <portlet:param name="v" value='<%= "${vote}" %>'/>
              <portlet:param name="out" value="text"/>
              <c:if test="${'true' eq param.popup || 'true' eq popup}">
                <portlet:param name="popup" value="true"/>
              </c:if>
            </portlet:renderURL>
            <ccp:rating id='<%= project.getId() %>'
                           showText='false'
                           count='1'
                           value='<%= projectRating.getRating() %>'
                           url='${ratingUrl}'
                           field='rating' />
          </span>
        </div>
        <ccp:evaluate if="<%= userTags.size() == 0 %>">
          <label for="tags"><ccp:label name="projectsCenterReviews.add.addTags">Add Tags</ccp:label></label>
          <c:set var="displayTags"><%= toHtmlValue(request.getParameter("tags"))%></c:set>
        </ccp:evaluate>
        <ccp:evaluate if="<%= userTags.size() > 0 %>">
          <label for="tags"><ccp:label name="projectsCenterReviews.add.updateYourTags">Update your Tags</ccp:label></label>
          <c:choose>
            <c:when test="${!empty param.tags}">
              <c:set var="displayTags"><c:out value="${param.tags}"/></c:set>
            </c:when>
            <c:otherwise>
              <c:set var="displayTags"><%= toHtmlValue(userTags.getTagsAsString())%></c:set>
            </c:otherwise>
          </c:choose>
        </ccp:evaluate>
        <em><ccp:label name="separateTagsWithCommas">separate multiple tags with commas</ccp:label></em>
        <input type="text" name="tags" id="tags" value="${displayTags}" />
        <span class="characterCounter">128 characters max per tag</span>
        <c:if test="${!empty userRecentTags || !empty popularTags}">
          <em><ccp:label name="clickToAddToYourTags">click a tag to add or remove it from your tags</ccp:label></em>
        </c:if>
        <c:if test="${!empty userRecentTags}">
          <span><ccp:label name="userRecentTags">Recently Used Tags</ccp:label><br/>
	          <c:forEach items="${userRecentTags}" var="userRecentTag">
  	          <a href="javascript:updateTag('${userRecentTag.tag}','tags');"><c:out value="${userRecentTag.tag}"/></a>&nbsp;
    	      </c:forEach></span>
        </c:if>
        <c:if test="${!empty popularTags}">
          <span><ccp:label name="popularTags">How others tagged this item</ccp:label><br/>
	          <c:forEach items="${popularTags}" var="popularTag">
  	          <a href="javascript:updateTag('${popularTag.tag}','tags');"><c:out value="${popularTag.tag}"/></a> (<c:out value="${popularTag.tagCount}"/>)&nbsp;
    	      </c:forEach></span>
        </c:if>
        <input type="hidden" name="id" value="<%= projectRating.getId() %>" />
        <input type="hidden" name="modified" value="<%= projectRating.getModified() %>" />
        <c:if test="${'true' eq param.popup || 'true' eq popup}">
          <input type="hidden" name="popup" value="true" />
        </c:if>
        <c:if test="${!empty param.redirectTo}">
          <input type="hidden" name="redirectTo" value="<%= StringUtils.toHtmlValue(request.getParameter("redirectTo")) %>"/>
        </c:if>
      </fieldset>
      <input type="submit" name="save" class="submit" value="<ccp:label name="button.save">Save</ccp:label>" />
      <c:choose>
        <c:when test="${'true' eq param.popup || 'true' eq popup}">
          <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
        </c:when>
        <c:otherwise>
          <portlet:renderURL var="cancelUrl">
            <portlet:param name="portlet-action" value="show"/>
            <portlet:param name="portlet-object" value="reviews"/>
          </portlet:renderURL>
          <a href="${cancelUrl}" class="cancel">Cancel</a>
        </c:otherwise>
      </c:choose>
      <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
    </form>
  </div>
