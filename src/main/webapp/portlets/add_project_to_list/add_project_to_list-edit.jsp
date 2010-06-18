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
<jsp:useBean id="availableProjects" class="com.concursive.connect.web.modules.profile.dao.ProjectList" scope="request"/>
<portlet:renderURL var="url1" portletMode="view"/>
<%@ include file="../../initPage.jsp" %>
<portlet:defineObjects/>
<c:if test="${!canDeleteFromList}">
  <c:set var="disableInput">disabled</c:set>
</c:if>
<div class="addProjectToListContainer">
	<%= showError(request, "actionError") %>
  <div class="formContainer">
    <div class="leftColumn">
      <portlet:actionURL var="submitContentUrl" portletMode="view" />
      <form method="POST" name="<portlet:namespace/>form" id="<portlet:namespace/>form" action="${submitContentUrl}" >
        <c:choose>
          <c:when test="${fn:length(availableProjects) >= 2}">
            <fieldset id="<ccp:label name="list.bookmark.choose">Choose where this will be added:</ccp:label>">
              <legend><ccp:label name="list.bookmark.choose">Choose where this will be added:</ccp:label></legend>
              <select name="pidOfLists" id="<portlet:namespace/>pidOfLists"
                <c:choose>
                    <c:when test="${'true' eq popup || 'true' eq param.popup}">
                      class="submitPanelOnChange"
                    </c:when>
                    <c:otherwise>
                       onChange="document.<portlet:namespace/>form.submit();"
                    </c:otherwise>
                </c:choose>
                  >
                <c:forEach items="${availableProjects}" var="thisProject">
                  <c:choose>
                    <c:when test="${pidOfLists == thisProject.id}"><c:set var="isSelected" value="selected"/></c:when>
                    <c:otherwise><c:set var="isSelected" value=""/></c:otherwise>
                  </c:choose>
                  <option value="${thisProject.id}" ${isSelected}><c:out value="${thisProject.title}"/></option>
                </c:forEach>
              </select>
              <input type="hidden" name="pidToCompare" value="${pidOfLists}"/>
            </fieldset>
          </c:when>
          <c:otherwise>
            <input type="hidden" name="pidOfLists" value="${userProfile.id}"/>
            <input type="hidden" name="pidToCompare" value="${userProfile.id}"/>
          </c:otherwise>
        </c:choose>
        <fieldset id="<portlet:namespace/>MyLists">
          <legend><ccp:label name="list.saveToMyList">My Lists</ccp:label></legend>
          <c:choose>
            <c:when test="${!empty availableLists}">
              <c:forEach var="item" items="${availableLists}">
                <c:set var="usedId" value="${usedListMap[item.id]}"/>
                <c:choose>
                <c:when test="${!empty usedId}">
                    <c:set var="isSelected">checked</c:set>
                    <c:set var="isDisabled">${disableInput}</c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="isSelected"> </c:set>
                    <c:set var="isDisabled"> </c:set>
                </c:otherwise>
                </c:choose>
                <span>
                  <label for="<portlet:namespace/>list${item.id}">
                    <input name="list" id="<portlet:namespace/>list${item.id}" type="checkbox" class="checkbox" value="${item.id}" ${isSelected} ${isDisabled}/><c:out value="${item.description}"/>
                  </label>
                </span>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <p><ccp:label name="list.noListsAvailable">No lists available</ccp:label></p>
            </c:otherwise>
          </c:choose>
        </fieldset>
        <c:if test="${canAddList}">
          <fieldset id="<portlet:namespace/>newList">
            <legend><ccp:label name="list.addToNew">Add to New List</ccp:label></legend>
            <label for="<portlet:namespace/>newListName"><ccp:label name="list.newName">New List Name: </ccp:label></label>
            <input type="text" id="<portlet:namespace/>newListName" name="newListName" value="<c:out value='${param.newListName}'/>"/>
          </fieldset>
        </c:if>
        <input type="hidden" name="pidToBookmark" value="${project.id}" />
        <input type="submit" value="Submit" class="submit">
        <c:choose>
          <c:when test="${'true' eq popup}">
            <input type="button" value="Cancel" class="cancel" id="panelCloseButton">
          </c:when>
          <c:otherwise>
            <span><a href="${ctx}/show/${project.uniqueId}" class="cancel">Cancel</a></span>
          </c:otherwise>
        </c:choose>
        <img src="${ctx}/images/loading16.gif" alt="loading please wait" class="submitSpinner" style="display:none"/>
      </form>
    </div>
    <div class="rightColumn">
      ${introductionMessage}
    </div>
  </div>
</div>
