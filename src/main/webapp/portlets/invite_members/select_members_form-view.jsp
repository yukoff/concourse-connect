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
<%@ page import="com.concursive.connect.web.portal.PortalUtils,javax.portlet.*" %>
<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:set var="ctx" value="${renderRequest.contextPath}" scope="request"/>
<script language="JavaScript" type="text/javascript">
  function checkForm<portlet:namespace/>(form) {
    var formTest = true;
    var messageText = "";
    // verify any form field validations and requirements

    if (!formTest) {
      messageText = "The message could not be submitted.          \r\nPlease verify the following items:\r\n\r\n" + messageText;
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
  function updateTargetAction(targetValue){
    var action = document.getElementById("<portlet:namespace/>actionType");
    action.value = targetValue;
  }
</script>
<div class="inviteMembersPortletEdit">
  <h2>Send Invites</h2>
  <c:if test="${hasProjectAccess eq 'false'}">
    <c:out value="You do not have permissions to invite members"/>
  </c:if>
  <c:if test="${hasProjectAccess ne 'false'}">
  <div class="formContainer">
  <portlet:actionURL var="submitContentUrl" portletMode="view" />
   <form method="POST" name="<portlet:namespace/>inputForm" action="${submitContentUrl}" onSubmit="try {return checkForm<portlet:namespace/>(this);}catch(e){return true;}">
    <input type="hidden" name="actionType" id="<portlet:namespace/>actionType" value="getInvitationMessage" />
      <fieldset id="InviteMembers">
        <legend><c:out value="Verify Members" /></legend>
         <%-- using tables as a temporary measure --%>
         <%= showAttribute(request,"actionError") %>
         <table class="pagedList">
          <c:set var="foundToDisplay" value="false" />
          <c:forEach items="${members}" var="member" varStatus="status">
            <c:choose>
              <c:when test="${member.value ne 'noMatchFound'}">
                <c:if test="${foundToDisplay eq 'false'}">
                    <c:if test="${showAccessToTools eq 'true'}">
                      <caption>The following entries have one or more matches:</caption>
                    </c:if>
                    <c:if test="${showAccessToTools eq 'false'}">
                      <caption>The following entries have one or more matches:</caption>
                    </c:if>
                  <c:if test="${hasMultipleMatches eq 'true'}">
                      <c:if test="${showAccessToTools eq 'true'}">
                        <caption><img src="${ctx}/images/box-hold.gif" />&nbsp;More than one user found.</caption>
                      </c:if>
                      <c:if test="${showAccessToTools eq 'false'}">
                        <caption><img src="${ctx}/images/box-hold.gif" />&nbsp;More than one user found.</caption>
                      </c:if>
                  </c:if>
                <thead>
                  <tr>
                    <th>Invite</th>
                    <th>You Entered</th>
                    <th>Matching results</th>
                    <th>Role</th>
                    <c:if test="${showAccessToTools eq 'true'}">
                      <th>Access my tools</th>
                    </c:if>
                  </tr>
                </thead>
                </c:if>
                <c:set var="foundToDisplay" value="true" />
                <tbody>
                  <tr>
                    <td>
                    <input type="checkbox" name="matches" value="${member.key}" checked />&nbsp;
                  </td>
                  <td valign="top">
                    <c:out value="${member.key}" />&nbsp;
                  </td>
                  <td valign="top">
                    <c:choose>
                      <c:when test="${fn:length(fn:split(member.value,',')) > 1}">
                        <select name="matchUserId-${member.key}">
                          <c:forEach items="${member.value}" var="id" varStatus="matchUserIdStatus">
                            <option value="${id}" <c:if test="${!empty matchUserId[member.key] and matchUserId[member.key] eq id}"> selected</c:if>><ccp:username id="${id}" showProfile="${false}" showPresence="${false}" showCityState="${true}" /></option>
                          </c:forEach>
                        </select>
                        <img src="${ctx}/images/box-hold.gif" />
                      </c:when>
                      <c:otherwise>
                        <input type="hidden" name="matchUserId-${member.key}" value="${member.value}" />
                        <ccp:username id="${member.value}" showProfile="${true}" showPresence="${false}" showCityState="${true}" />
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td valign="top">
                    <select name="matchedRole-${member.key}" class="input selectInput">
                      <c:forEach items="${roleList}" var="role">
                        <option value="${role.level}" <c:if test="${(!empty matchRole[member.key] and matchRole[member.key] == role.level) or role.level == defaultRole}"> selected</c:if>><c:out value="${role.description}"/></option>
                      </c:forEach>
                    </select>
                  </td>
                  <c:if test="${showAccessToTools eq 'true'}">
                      <td valign="top">
                      <input type="checkbox" name="accessToTools" value="${member.key}" />&nbsp;
                    </td>
                  </c:if>
                </tr>
              </tbody>
              </c:when>
            </c:choose>
          </c:forEach>
        </table>
        <c:set var="foundToDisplay" value="false" />
          <table class="pagedList">
            <c:forEach items="${members}" var="member" varStatus="status">
              <c:set var="count" value="${status.count}" />
              <jsp:useBean id="count" type="java.lang.Integer" />
              <c:choose>
                <c:when test="${member.value eq 'noMatchFound'}">
                    <c:if test="${foundToDisplay eq 'false'}">
                      <c:if test="${showAccessToTools eq 'true'}">
                        <caption>No matches were found for the following entries:</caption>
                      </c:if>
                      <c:if test="${showAccessToTools eq 'false'}">
                        <caption>No matches were found for the following entries:</caption>
                      </c:if>
                      <thead>
                        <tr>
                          <th class="invite">Invite</th>
                          <th class="email">You Entered</th>
                          <th class="firstName">First Name <span class="required">*</span></th>
                          <th class="lastName">Last Name <span class="required">*</span></th>
                          <th class="email">Email <span class="required">*</span></th>
                          <th class="role">Role</th>
                          <c:if test="${showAccessToTools eq 'true'}">
                            <th class="tools">Access my tools</th>
                          </c:if>
                        </tr>
                      </thead>
                    </c:if>
                    <tbody>
                    <c:set var="foundToDisplay" value="true" />
                    <tr>
                      <td valign="top">
                        <input type="checkbox" name="mismatches" value="${member.key}" checked />&nbsp;
                      </td>
                      <td valign="top">
                        <c:out value="${member.key}" />&nbsp;
                      </td>
                      <td>
                        <input type="text" size="20" maxlength="50" name="firstName-${member.key}" value="<c:out value="${noMatchFirstName[member.key]}" />"/>&nbsp;
                      </td>
                      <td>
                        <input type="text" size="20" maxlength="50" name="lastName-${member.key}" value="<c:out value="${noMatchLastName[member.key]}" />" />&nbsp;
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${empty noMatchEmail[member.key] && fn:contains(member.key, '@')}">
                            <input type="text" size="20" maxlength="255" name="email-${member.key}" value="<c:out value="${member.key}" />" />&nbsp;
                          </c:when>
                          <c:otherwise>
                            <input type="text" size="20" maxlength="255" name="email-${member.key}" value="<c:out value="${noMatchEmail[member.key]}" />" />&nbsp;
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td valign="top">
                           <select name="notMatchedRole-${member.key}" class="input selectInput">
                              <c:forEach items="${roleList}" var="role">
                                <option value="${role.level}" <c:if test="${(!empty noMatchRole[member.key] and noMatchRole[member.key] == role.level) or role.level == defaultRole}"> selected</c:if>><c:out value="${role.description}"/></option>
                              </c:forEach>
                            </select>
                    </td>
                      <c:if test="${showAccessToTools eq 'true'}">
                        <td valign="top">
                    <input type="checkbox" name="notMatchedAccessToTools" value="${member.key}"/>&nbsp;
                        </td>
                      </c:if>
                  </tr>
                </tbody>
                </c:when>
              </c:choose>
            </c:forEach>
         </table>
       </fieldset>
       <input type="hidden" name="sourcePage" value="getMatches" />
        <c:if test="${'true' eq param.popup || 'true' eq popup}">
          <input type="hidden" name="popup" value="true" />
          <input type="hidden" name="close" value="true" />
        </c:if>
       <input type="submit" class="submit" name="save" value="Prepare Message" />
       <%--
        <input type="submit" class="submit" name="Back" class="cancel" value="Back" onClick="javascript:updateTargetAction('getMembers')"/>
      --%>
        <c:choose>
          <c:when test="${'true' eq popup}">
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:when>
          <c:otherwise>
            <a href="${ctx}/show/${project.uniqueId}" class="cancel">Cancel</a>
          </c:otherwise>
        </c:choose>
        <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
      </form>
    </div>
  </c:if>
</div>
