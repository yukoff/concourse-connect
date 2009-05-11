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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<jsp:useBean id="task" class="com.concursive.connect.web.modules.lists.dao.Task" scope="request"/>
<jsp:useBean id="functionalAreaList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="complexityList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="businessValueList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="targetSprintList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="targetReleaseList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="statusList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<jsp:useBean id="loeRemainingList" class="com.concursive.connect.cms.portal.dao.ProjectItemList" scope="request"/>
<%@ include file="initPage.jsp" %>
<table class="pagedList" width="300">
  <thead>
    <tr>
      <td class="formLabel">Unique Id</td>
      <td align="left"><%= task.getId() %></td>
    </tr>
  </thead>
  <tbody>
    <c:if test="${!empty task.notes}">
      <tr>
        <td class="formLabel">Notes</td>
        <td align="left"><%= toHtml(task.getNotes()) %></td>
      </tr>
    </c:if>
    <tr>
      <td class="formLabel">Owner</td>
      <td align="left"><ccp:username id="<%= task.getOwner() %>"/></td>
    </tr>
    <c:if test="${!empty functionalAreaList}">
      <tr>
        <td class="formLabel">Functional Area</td>
        <td align="left"><%= toHtml(functionalAreaList.getValueFromId(task.getFunctionalArea())) %></td>
      </tr>
    </c:if>
    <c:if test="${!empty statusList}">
      <tr>
        <td class="formLabel">Status</td>
        <td align="left"><%= toHtml(statusList.getValueFromId(task.getStatus())) %></td>
      </tr>
    </c:if>
    <c:if test="${!empty businessValueList}">
      <tr>
        <td class="formLabel">Business Value</td>
        <td align="left"><%= toHtml(businessValueList.getValueFromId(task.getBusinessValue())) %></td>
      </tr>
    </c:if>
    <c:if test="${!empty complexityList}">
      <tr>
        <td class="formLabel">Complexity</td>
        <td align="left"><%= toHtml(complexityList.getValueFromId(task.getComplexity())) %></td>
      </tr>
    </c:if>
    <c:if test="${!empty targetReleaseList}">
      <tr>
        <td class="formLabel">Target Release</td>
        <td align="left"><%= toHtml(targetReleaseList.getValueFromId(task.getTargetRelease())) %></td>
      </tr>
    </c:if>
    <c:if test="${!empty targetSprintList}">
      <tr>
        <td class="formLabel">Target Sprint</td>
        <td align="left"><%= toHtml(targetSprintList.getValueFromId(task.getTargetSprint())) %></td>
      </tr>
    </c:if>
    <c:if test="${!empty loeRemainingList}">
      <tr>
        <td class="formLabel">Remaining</td>
        <td align="left"><%= toHtml(loeRemainingList.getValueFromId(task.getLoeRemaining())) %></td>
      </tr>
    </c:if>
  </tbody>
</table>
