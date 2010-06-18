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
<%@ page import="com.concursive.connect.web.modules.activity.dao.ProjectHistoryList" %>
<%@ page import="com.concursive.connect.web.modules.profile.utils.ProjectUtils" %>
<%@ page import="com.concursive.commons.date.DateUtils" %>
<%@ page import="com.concursive.connect.web.modules.profile.dao.Project" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.concursive.commons.html.HTMLUtils" %>
<jsp:useBean id="applicationPrefs" class="com.concursive.connect.config.ApplicationPrefs" scope="application"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<c:set var="user" value="<%= User %>" scope="request" />
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="projectHistoryList" class="com.concursive.connect.web.modules.activity.dao.ProjectHistoryList" scope="request"/>
<jsp:useBean id="projectHistoryArrayList" class="java.util.ArrayList" scope="request"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="add_a_comment_constant">Add a comment...</c:set>
<script type="text/javascript">

  <c:if test="${ajax eq 'false' or param.ajax eq 'false'}">

    // Internal array used to track all running intervals
    var intervalActive<portlet:namespace/> = false;
    var interval<portlet:namespace/>;

    function startPolling<portlet:namespace/>() {
      if (!intervalActive<portlet:namespace/>) {
        intervalActive<portlet:namespace/> = true;
        interval<portlet:namespace/> = window.setInterval("refreshActivityList<portlet:namespace/>()",10000);
      }
    }

    function pausePolling<portlet:namespace/>() {
      window.clearInterval(interval<portlet:namespace/>);
      intervalActive<portlet:namespace/> = false;
    }

    function stopPolling<portlet:namespace/>() {
      window.clearInterval(interval<portlet:namespace/>);
      intervalActive<portlet:namespace/> = true;
    }

    // Keep refreshing the activity stream
    var interval<portlet:namespace/> = window.setInterval("refreshActivityList<portlet:namespace/>()",10000);

    function replyForm<portlet:namespace/>(spanId, historyId, parentId, replyNameEscaped) {
      pausePolling<portlet:namespace/>();
      if (isSpanVisible("reply" +spanId) && !historyId) {
        hideSpan("reply" +spanId);
      } else {
        document.getElementById("parentId" + spanId).value = historyId;
        if (parentId > -1) {
          changeText("replyName" + spanId,'Replying to ' + unescape(replyNameEscaped));
        } else {
          changeText("replyName" + spanId,'${add_a_comment_constant}');
        }
        showSpan("reply" +spanId);
        document.getElementById("description" + spanId).focus();
      }
      return false;
    }

    function checkActivityReplyInputForm<portlet:namespace/>(form,id) {
      var formTest = true;
      var messageText = "";
      if (form.description.value.trim() == "") {
          messageText += "- A message is required\r\n";
          formTest = false;
      }
      if (!formTest) {
          messageText = "The message could not be sent.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
          alert(messageText);
          return false;
      } else {
        if (form.save.value != 'Saving...') {
          // Tell the user to wait
          form.save.value='Saving...';
          form.save.disabled = true;
          // find one or more spinners
          var uItems = YAHOO.util.Dom.getElementsByClassName("submitSpinner_"+id);
          for (var j = 0; j < uItems.length; j++) {
           YAHOO.util.Dom.setStyle(uItems[j], "display", "inline");
          }
          return true;
        } else {
          return false;
        }
      }
    }

    function saveActivityReply<portlet:namespace/>(form,id) {
      <%-- Validate the form --%>
      if (form.description.value.trim() == "") {
          messageText = "Please enter some text and try again.";
          alert(messageText);
          return false;
      }
      <%-- Submit the form --%>
      if (form.save.value != 'Please Wait...') {
        form.save.value='Please Wait...';
        form.save.disabled = true;
        // find one or more spinners
        var uItems = YAHOO.util.Dom.getElementsByClassName("submitSpinner_" + id);
        for (var j = 0; j < uItems.length; j++) {
          YAHOO.util.Dom.setStyle(uItems[j], "display", "inline");
        }
        // Post the data
        <portlet:actionURL var="jsSaveFormUrl">
          <portlet:param name="portlet-command" value="reply-saveForm" />
        </portlet:actionURL>
        // Use YUI to properly encode the POST
        var handleSuccess = function(o) {
          refreshActivityList<portlet:namespace/>();
        };
        var callback = {
          success: handleSuccess
        };
        var postData =
            "out=text" +
            "&description=" + encodeURIComponent(form.description.value.trim()) +
            "&parentId=" + form.parentId.value;
        YAHOO.util.Connect.asyncRequest('POST', "${jsSaveFormUrl}", callback, postData);
      }
      return false;
    }

    function deleteActivity<portlet:namespace/>(url) {
      if (confirmAction()) {
              sendRequest(url, 'activityList<portlet:namespace/>');
      }
    }

    function getResponseAppend<portlet:namespace/>(xmlHttpReq,id) {
      if(xmlHttpReq.readyState == 4){
        if (xmlHttpReq.status == 200){
          changeText(id,xmlHttpReq.responseText);
          startPolling<portlet:namespace/>();
        }
      }
    }
  </c:if>

  <%-- the following functions are displayed each request because they have new parameters --%>

  function refreshActivityList<portlet:namespace/>() {
    <%-- Refresh the activity stream --%>
    <portlet:renderURL var="notificationURL" portletMode="view" windowState="maximized">
      <portlet:param name="out" value="text"/>
      <portlet:param name="offset" value="${offset}"/>
      <portlet:param name="streamType" value="${streamType}"/>
      <portlet:param name="limit" value="${limit}"/>
      <portlet:param name="ajax" value="true"/>
    </portlet:renderURL>
    pausePolling<portlet:namespace/>();
    xmlhttp.open('get', '${notificationURL}&rnd=' + new Date().valueOf().toString());
    xmlhttp.onreadystatechange = function(){getResponseAppend<portlet:namespace/>(xmlhttp,'activityList<portlet:namespace/>');};
    xmlhttp.send(null);
  }

  <%-- More button / Refresh --%>
  var limit<portlet:namespace/> = ${limit};
  function showMore<portlet:namespace/>() {
    stopPolling<portlet:namespace/>();
    limit<portlet:namespace/> += ${prefLimit};
	  var moreButton_link = document.getElementById("moreButton_link<portlet:namespace/>");
    YAHOO.util.Dom.setStyle(moreButton_link, "display", "none");
    <portlet:renderURL var="notificationURL" portletMode="view" windowState="maximized">
      <portlet:param name="out" value="text"/>
      <portlet:param name="offset" value="${offset}"/>
      <portlet:param name="streamType" value="${streamType}"/>
      <portlet:param name="ajax" value="true"/>
    </portlet:renderURL>
    xmlhttp.open('get', '${notificationURL}&limit=' + limit<portlet:namespace/> + '&rnd=' + new Date().valueOf().toString());
    xmlhttp.onreadystatechange = function(){getResponseAppend<portlet:namespace/>(xmlhttp,'activityList<portlet:namespace/>');};
    xmlhttp.send(null);
  }

</script>

<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<c:set var="add_activity_constant">
  <%= ProjectHistoryList.ADD_ACTIVITY_ENTRY_EVENT %>
</c:set>
<c:set var="twitter_constant">
  <%= ProjectHistoryList.TWITTER_EVENT %>
</c:set>

<%-- Set All | Just Mine --%>
<portlet:renderURL var="allUrl">
	<portlet:param name="streamType" value="1"/>
</portlet:renderURL>
<portlet:renderURL var="justMineUrl" >
	<portlet:param name="streamType" value="0"/>
</portlet:renderURL>

<c:set var="isProfile" value="<%= project.getId() > -1 && project.getProfile() %>"/>

<%-- Show / hide All | Just Mine depending on the request type (ajax or normal) --%>
<c:if test="${ajax eq 'false' or param.ajax eq 'false'}">
	<h3>
    <c:out value="${title}"/>&nbsp;
    <c:if test="${showControls eq 'true'}">
      <c:choose>
        <c:when test="${!empty streamType}">
          <c:if test="${streamType eq 1}">
            All | <a href="${justMineUrl }">Just Mine</a>
          </c:if>
          <c:if test="${streamType eq 0}">
            <a href="${allUrl }">All</a> | Just Mine
          </c:if>
        </c:when>
        <c:otherwise>
          <a href="${allUrl }">All</a> | Just Mine
          <c:set var="streamType" value="1"/>
        </c:otherwise>
      </c:choose>
    </c:if>
	</h3>
  <c:if test="${!empty content}">
    <h4><c:out value="${content}"/></h4>
  </c:if>
</c:if>
<c:if test="${!empty projectHistoryArrayList}">
  <%-- Starts  activityList div only once, not for ajax requests --%>
  <c:if test="${(ajax eq 'false' or param.ajax eq 'false')}">
    <div id="activityList<portlet:namespace/>">
  </c:if>
  <c:forEach items="${projectHistoryArrayList}" var="dayList" varStatus="dayStatus">
    <c:set var="drawDay" value="true"/>
    <c:forEach items="${dayList}" var="activityList" varStatus="eventStatus">
      <c:forEach items="${activityList}" var="projectHistory" varStatus="activityStatus">
        <c:set var="projectHistory" value="${projectHistory}" scope="request"/>
        <jsp:useBean id="projectHistory" type="com.concursive.connect.web.modules.activity.dao.ProjectHistory" scope="request"/>
        <%-- Determine the target id for all replies --%>
        <c:if test="${activityStatus.first}">
          <c:set var="thisReplyId">${projectHistory.id}</c:set>
        </c:if>

        <%-- Draw the day --%>
        <c:if test="${!empty drawDay}">
          <c:remove var="drawDay"/>
          <div class="portlet-section-subheader">
            <h4><ccp:tz timestamp="${projectHistory.relativeDate}" pattern="MMMM dd" /></h4>
          </div>
        </c:if>

        <%-- Draw the user --%>
        <c:if test="${activityStatus.first}">
          <ol>
            <li>
        </c:if>

        <%-- Indent the replies --%>
        <c:if test="${activityStatus.index eq 1}">
          <ol>
        </c:if>
        <c:if test="${!activityStatus.first}">
          <c:choose>
            <c:when test="${activityStatus.last}">
              <li class="last">
            </c:when>
            <c:otherwise>
              <li>
            </c:otherwise>
          </c:choose>
        </c:if>

        <c:choose>
          <%-- draw the project history owners photo, or use the project it was put in --%>
          <c:when test="${activityStatus.first && !empty projectHistory.user.profileProject.logo}">
            <img alt="<c:out value="${projectHistory.user.profileProject.title}"/> photo"
                 src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getLogo().getUrlName(45,45) %>" width="45" height="45" />
          </c:when>
          <c:when test="${activityStatus.first && !empty projectHistory.user.profileProject.category.logo}">
            <img alt="Default user photo"
                 src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getCategory().getLogo().getUrlName(45,45) %>" width="45" height="45" class="default-photo" />
          </c:when>
          <c:when test="${activityStatus.first && !empty projectHistory.project.logo}">
            <img alt="<c:out value="${projectHistory.project.title}"/> photo"
                 src="${ctx}/image/<%= projectHistory.getProject().getLogo().getUrlName(45,45) %>" width="45" height="45" />
          </c:when>
          <c:when test="${activityStatus.first && !empty projectHistory.project.category.logo}">
            <img alt="Default user photo"
                 src="${ctx}/image/<%= projectHistory.getProject().getCategory().getLogo().getUrlName(45,45) %>" width="45" height="45" class="default-photo" />
          </c:when>
          <%-- draw the reply photo --%>
          <c:when test="${!activityStatus.first && !empty projectHistory.user.profileProject.logo}">
            <img alt="<c:out value="${projectHistory.user.profileProject.title}"/> photo"
                 src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getLogo().getUrlName(30,30) %>" width="30" height="30" />
          </c:when>
          <c:when test="${!activityStatus.first && !empty projectHistory.user.profileProject.category.logo}">
            <img alt="Default user photo"
                 src="${ctx}/image/<%= projectHistory.getUser().getProfileProject().getCategory().getLogo().getUrlName(30,30) %>" width="30" height="30" class="default-photo" />
          </c:when>
        </c:choose>

        <%-- Draw the activity --%>
        ${projectHistory.htmlLink}

        <%-- Reply delete url --%>
       	<portlet:actionURL var="activity_deleteUrl" >
	      	<portlet:param name="id" value="${projectHistory.id}"/>
           <portlet:param name="limit" value="${limit}"/>
			    <portlet:param name="streamType" value="${streamType}"/>
	      	<portlet:param name="portlet-command" value="delete"/>
	      	<portlet:param name="out" value="text"/>
	      	<portlet:param name="ajax" value="true"/>
	     	</portlet:actionURL>
	     	
        <div class="replyFieldsetContainer">
          <ccp:tz timestamp="${projectHistory.linkStartDate}" pattern="relative" />
          <c:if test="${user.loggedIn && allowReplies eq 'true'}">
            <%-- Reply? --%>
            <ccp:permission name="project-profile-activity-reply" object="projectHistory.project">
              &bull;
              <a href="" onclick="return replyForm<portlet:namespace/>('_${thisReplyId}<portlet:namespace/>',${projectHistory.id},${projectHistory.parentId},'<%= StringUtils.jsEscape(projectHistory.getUser().getNameFirstLastInitial()) %>');">reply</a>
            </ccp:permission>
            <%-- Delete? --%>
            <%-- @note MAINTAIN THESE IN THE PORTLET ACTION TOO... @todo create method --%>
            <c:set var="showDelete" value="false"/>
            <c:if test="${projectHistory.childCount eq 0 && (projectHistory.linkObject eq 'user-entry' || projectHistory.linkObject eq 'site-chatter' || projectHistory.linkObject eq 'webcasts-chatter' || projectHistory.linkObject eq 'tv-chatter')}">
              <c:choose>
                <%-- Allow the user to delete their own entries, if no one has replied --%>
                <c:when test="${projectHistory.enteredBy == user.id}">
                  <c:set var="showDelete" value="true"/>
                </c:when>
                <c:otherwise>
                  <ccp:permission name="project-profile-activity-delete" object="projectHistory.project">
                    <%-- Allow if the user has access to delete within the profile --%>
                    <c:set var="showDelete" value="true"/>
                  </ccp:permission>
                </c:otherwise>
              </c:choose>
            </c:if>
            <%-- Allow the admin to delete anything --%>
            <c:if test="${projectHistory.childCount eq 0 && user.accessAdmin}">
              <c:set var="showDelete" value="true"/>
            </c:if>
            <c:if test="${showDelete eq 'true'}">
              &bull;
              <a href="javascript:deleteActivity<portlet:namespace/>('${activity_deleteUrl}');">delete</a>
            </c:if>
          </c:if>
        </div>

        <%-- Let the user know when there are more replies --%>
        <c:if test="${dayStatus.last && eventStatus.last && activityStatus.last && !empty hasNext && !empty additionalComments}">
          <p>
            <c:choose>
              <c:when test="${additionalComments eq '1'}">
                <strong>There is 1 more reply to show...</strong>
              </c:when>
              <c:otherwise>
                <strong>There are ${additionalComments} more replies to show...</strong>
              </c:otherwise>
            </c:choose>
          </p>
        </c:if>

        <%-- Show a thumbnail of the entry --%>
        <%-- UPDATE against project_profile-view.jsp
        <c:if test="${projectHistory.linkObject eq 'image'}">
          <c:set var="startImageUrlName210" scope="request">
            <%= project.getImages().get(0).getUrlName(210,150) %>
          </c:set>
          <div id="<portlet:namespace/>profileImage">
            <a title="<c:out value='${imageCaption}'/>"
               href="javascript:showImage('<c:out value="${imageCaption}" />','${ctx}/show/${project.uniqueId}/image/${startImageUrlName0}',null,${startImage.imageHeight},${startImage.imageWidth}, '<portlet:namespace/>images');">
              <img alt="<c:out value='${startImage.subject} - ${project.title}'/> image"
                   src="<%= ctx %>/show/${project.uniqueId}/image/${startImageUrlName210}"/>
            </a>
          </div>
        </c:if>
        --%>

        <c:if test="${user.loggedIn}">
          <c:if test="${activityStatus.last}">
            <ccp:permission name="project-profile-activity-reply" object="projectHistory.project">
              <%-- Reply form start --%>
              <portlet:actionURL var="saveFormUrl">
                <portlet:param name="portlet-command" value="reply-saveForm" />
              </portlet:actionURL>
              <div class="replyFormContainer" id="reply_${thisReplyId}<portlet:namespace/>" <c:if test="${activityStatus.index eq 0}">style="display:none;"</c:if>>
                <div class="formContainer">
                  <form name="replyInputForm<portlet:namespace/>" onSubmit="try {return saveActivityReply<portlet:namespace/>(this,'${projectHistory.id}');}catch(e){alert(e); return true;}" method="POST" action="${saveFormUrl}">
                    <span id="replyName_${thisReplyId}<portlet:namespace/>">
                      ${add_a_comment_constant}
                    </span>
                    <img src="${ctx}/images/cross.png" class="cancel" onClick="return replyForm<portlet:namespace/>('_${thisReplyId}<portlet:namespace/>');" alt="Cancel reply"/>
                    <div class="reply">
                      <input type="hidden" name="limit" value="${limit + 1}" />
                      <input type="hidden" name="streamType" value="${streamType}" />
                      <input type="hidden" id="parentId_${thisReplyId}<portlet:namespace/>" name="parentId" value="${thisReplyId}" />
                      <input id="description_${thisReplyId}<portlet:namespace/>" type="text" name="description" size="57" class="input longInput" maxlength="512" value="" onclick="pausePolling<portlet:namespace/>();" onchange="pausePolling<portlet:namespace/>();" />
                      <input type="submit" name="save" class="submit" value="Reply" />
                      <img src="${ctx}/images/loading16.gif" alt="Saving..." class="submitSpinner_${projectHistory.id}" style="display:none;" />
                    </div>
                  </form>
                </div>
              </div>
            </ccp:permission>
				  </c:if>
				</c:if>

        <%--  --%>
        <c:if test="${activityStatus.first && !activityStatus.last}">
          <fieldset class="indentFieldset" id="fieldset_${projectHistory.id }">
        </c:if>

        <c:if test="${!activityStatus.first}">
          </li>
        </c:if>

        <c:if test="${activityStatus.last}">
            <c:if test="${activityStatus.index > 0}">
              </ol>
            </c:if>
            <c:if test="${!activityStatus.first && activityStatus.last}">
              </fieldset>
            </c:if>
            </li>
          </ol>
        </c:if>
      </c:forEach>
    </c:forEach>
  </c:forEach>

  <%-- Shows more button --%>  
  <c:if test="${!empty hasNext}">
		<div class="button-single" id="moreButton<portlet:namespace/>" >
			<a id="moreButton_link<portlet:namespace/>" title="Show more activities..." href="javascript:showMore<portlet:namespace/>()">
				<em>More</em>
			</a>
		</div>
  </c:if>

  <%-- Ends  activityList div, if the request is a non-ajax type--%>
  <c:if test="${(ajax eq 'false' or param.ajax eq 'false')}">
    </div>
  </c:if>
  
  <%-- Show info about twitter, if the capability is enabled --%>
  <c:if test="${ajax eq 'false' or param.ajax eq 'false'}">
    <c:if test='${!empty applicationPrefs.prefs["TWITTER_HASH"] && fn:contains(eventArrayList, "twitter")}'>
      <c:set var="projectId" value="<%= projectHistoryList.getProjectId() %>"/>
      <c:set var="projectCategoryId" value="<%= projectHistoryList.getProjectCategoryId() %>"/>
      <c:set var="isProfile" value="<%= project.getId() > -1 && project.getProfile() %>"/>
      <c:set var="userProfileProjectId" value="<%= User.getProfileProjectId() %>"/>
<%--
      Proj:<c:out value="${projectId}"/>
      Cat:<c:out value="${projectCategoryId}"/>
      IsUser:<c:out value="${isProfile}"/>
      UsedId:<c:out value="${userProfileProjectId}"/>
--%>
      <c:choose>
        <c:when test="${dashboardPortlet.cached}">
          <%-- Show simple cacheable message --%>
          <p>
            Post to <a href="http://twitter.com" target="_blank">Twitter</a> using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.<br />
            Be sure to login and link your profile to Twitter.
          </p>
        </c:when>
        <c:otherwise>
          <%-- Show personalized message --%>
          <c:choose>
            <%-- Users profile page --%>
            <c:when test="${isProfile eq 'true'}">
              <c:if test="${userProfileProjectId eq project.id}">
                <p>
                  Post to Twitter using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.
                  <c:if test="${empty user.profileProject.twitterId}">
                    <br />
                    <a href="javascript:showPanel('','${ctx}/show/${user.profileProject.uniqueId}/app/edit_profile','600')">Link your Twitter id</a> |
                    <a href="http://twitter.com" target="_blank">Need a Twitter account?</a>
                  </c:if>
                </p>
              </c:if>
            </c:when>
            <%-- Non-Users profile page and access to post --%>
            <c:when test="${projectId ne -1}">
              <ccp:permission if="all" name="project-profile-activity-add">
                <p>
                  Post to <a href="http://twitter.com" target="_blank">Twitter</a> using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.
                  <c:if test="${empty project.twitterId}">
                    <br />
                    <a href="javascript:showPanel('','${ctx}/show/${project.uniqueId}/app/edit_profile','600')">Link your Twitter id</a> |
                    <a href="http://twitter.com" target="_blank">Need a Twitter account?</a>
                  </c:if>
                </p>
              </ccp:permission>
            </c:when>
            <%-- Category page --%>
            <c:when test="${projectCategoryId ne -1}">
              <%--Category tab... TBD--%>
            </c:when>
            <%-- Home page --%>
            <c:otherwise>
              <p>
                Post to <a href="http://twitter.com" target="_blank">Twitter</a> using <strong>#${applicationPrefs.prefs["TWITTER_HASH"]}</strong> so others will see your messages here.<br />
                <c:choose>
                  <c:when test="${user.profileProjectId eq -1}">
                    Login to link your Twitter id.
                  </c:when>
                  <c:when test="${empty user.profileProject.twitterId}">
                    <a href="javascript:showPanel('','${ctx}/show/${user.profileProject.uniqueId}/app/edit_profile','600')">Link your Twitter id</a>.
                  </c:when>
                </c:choose>
              </p>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </c:if>
  </c:if>
</c:if>
<c:if test="${empty projectHistoryArrayList}">
  <p>There are no activities to report at this time.</p>
</c:if>
