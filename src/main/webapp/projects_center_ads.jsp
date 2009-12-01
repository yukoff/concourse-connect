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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/concourseconnect-taglib.tld" prefix="ccp" %>
<%@ page import="com.concursive.connect.web.utils.CounterPair" %>
<%@ page import="com.concursive.connect.web.modules.promotions.dao.Ad" %>
<%@ page import="com.concursive.connect.web.modules.ModuleUtils" %>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="promotionsList" class="com.concursive.connect.web.modules.promotions.dao.AdList" scope="request"/>
<jsp:useBean id="projectPromotionsInfo" class="com.concursive.connect.web.utils.PagedListInfo" scope="request"/>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<%@ include file="initPage.jsp" %>
<portlet:defineObjects/>
<div class="portletWrapper">
  <h1><ccp:tabLabel name="Ads" object="project"/></h1>
  <c:if test="${!empty actionError}"><%= showError(request, "actionError") %></c:if>
  <%-- If there are no promotions --%>
  <c:if test="${empty promotionsList}">
    <ccp:label name="projectsCenterAds.noAds"><p>There are currently no promotions.</p></ccp:label>
  </c:if>
<%
  Iterator i = promotionsList.iterator();
  while (i.hasNext()) {
    Ad promotion = (Ad) i.next();
    request.setAttribute("promotion", promotion);
%>
  <div class="listingContainer">
    <div class="listingHeaderContainer">
      <h3><c:out value="${promotion.heading}"/></h3>
      <div class="version">
        <span class="red">
          <ccp:evaluate if="<%= promotion.getPublishDate() == null %>">
            <ccp:label name="promotion.unpublished">Unpublished</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= promotion.isExpired() %>">
            <ccp:label name="promotion.expired">Expired</ccp:label>
          </ccp:evaluate>
          <ccp:evaluate if="<%= promotion.isScheduledInFuture() %>">
            <ccp:label name="promotion.expired">Scheduled in future</ccp:label>
          </ccp:evaluate>
        </span>
      </div>
      <div class="details">
        <ccp:evaluate if="<%= promotion.getPublishDate() != null %>">
          <div>
            <ccp:label name="promotion.starts">Starts</ccp:label>
            <ccp:tz timestamp="<%= promotion.getPublishDate() %>" dateFormat="<%= DateFormat.LONG %>" />
          </div>
        </ccp:evaluate>
        <ccp:evaluate if="<%= promotion.getExpirationDate() != null %>">
          <div>
            <ccp:label name="promotion.expires">Expires</ccp:label>
            <ccp:tz timestamp="<%= promotion.getExpirationDate() %>" dateFormat="<%= DateFormat.LONG %>" />
          </div>
        </ccp:evaluate>
        <ccp:permission name="project-ads-admin">
          <div>
            by <ccp:username id="<%= promotion.getEnteredBy() %>" />
          </div>
        </ccp:permission>
        <ccp:evaluate if="<%= promotion.getId() > -1 && User.isLoggedIn() %>">
          <div>
            [<a href="javascript:showPanel('Ask advertiser a question','${ctx}/show/${project.uniqueId}/app/compose_message?module=promotions&id=${promotion.id}',700)">Ask advertiser a question</a>]
          </div>
        </ccp:evaluate>
      </div>
      <ccp:permission name="project-ads-admin,project-ads-add" if="any">
        <div class="permissions">
          <%-- edit --%>
          <portlet:renderURL var="updateUrl" portletMode="view">
            <portlet:param name="portlet-action" value="modify"/>
            <portlet:param name="portlet-object" value="promotion"/>
            <portlet:param name="portlet-value" value="${promotion.id}"/>
          </portlet:renderURL>
          <ccp:evaluate if="<%= promotion.getPublishDate() == null %>">
            <ccp:permission name="project-ads-add">
              <a href="${updateUrl}"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterAds.byAd.editThisItem">Edit this item</ccp:label>"></a>
            </ccp:permission>
          </ccp:evaluate>
          <ccp:evaluate if="<%= promotion.getPublishDate() != null %>">
            <ccp:permission name="project-ads-admin">
              <a href="${updateUrl}"><img src="<%= ctx %>/images/icons/stock_edit-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterAds.byAd.editThisItem">Edit this item</ccp:label>"></a>
            </ccp:permission>
          </ccp:evaluate>
          <ccp:permission name="project-ads-add">
            <%-- clone --%>
            <portlet:actionURL var="cloneUrl" portletMode="view">
              <portlet:param name="portlet-value" value="${promotion.id}"/>
              <portlet:param name="portlet-command" value="clone"/>
            </portlet:actionURL>
            <a href="javascript:confirmForward('${cloneUrl}');"><img src="<%= ctx %>/images/icons/stock_copy-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterAds.byAd.makeACopyOfThisItem">Make a copy of this item</ccp:label>"></a>
          </ccp:permission>
          <%-- delete --%>
          <portlet:actionURL var="deleteUrl" portletMode="view">
            <portlet:param name="portlet-value" value="${promotion.id}"/>
            <portlet:param name="portlet-command" value="delete"/>
          </portlet:actionURL>
          <ccp:evaluate if="<%= promotion.getPublishDate() == null %>">
            <ccp:permission name="project-ads-add">
              <a href="javascript:confirmDelete('${deleteUrl}');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterAds.byAd.deleteThisItem">Delete this item</ccp:label>"></a>
            </ccp:permission>
          </ccp:evaluate>
          <ccp:evaluate if="<%= promotion.getPublishDate() != null %>">
            <ccp:permission name="project-ads-admin">
              <a href="javascript:confirmDelete('${deleteUrl}');"><img src="<%= ctx %>/images/icons/stock_delete-16.gif" border="0" align="absmiddle" title="<ccp:label name="projectsCenterAds.byAd.deleteThisItem">Delete this item</ccp:label>"></a>
            </ccp:permission>
          </ccp:evaluate>
        </div>
      </ccp:permission>
      <div class="ratings">
        <portlet:renderURL var="ratingUrl" windowState="maximized">
          <portlet:param name="portlet-action" value="show"/>
          <portlet:param name="portlet-object" value="promotions"/>
          <portlet:param name="portlet-value" value="${promotion.id}"/>
          <portlet:param name="portlet-command" value="setRating"/>
          <portlet:param name="v" value="\${vote}"/>
          <portlet:param name="out" value="text"/>
        </portlet:renderURL>
        <ccp:rating id='${promotion.id}'
           showText='false'
           count='${promotion.ratingCount}'
           value='${promotion.ratingValue}'
           url='${ratingUrl}'/>
      </div>
    </div>
    <div class="listingBodyContainer">
      <c:if test="${!empty promotion.briefDescription1}">
        <p>
          <c:out value="${promotion.briefDescription1}"/>
          <c:if test="${!empty promotion.briefDescription2}">
            <br />
            <c:out value="${promotion.briefDescription2}"/>
          </c:if>
        </p>
      </c:if>
      <p><c:out value="${promotion.content}"/></p>
      <c:if test="${!empty promotion.webPage && !empty promotion.destinationUrl && fn:startsWith(promotion.destinationUrl, 'http')}">
        <cite><a target="_blank" rel="nofollow" href="<c:out value="${promotion.destinationUrl}"/>"><c:out value="${promotion.webPage}"/></a></cite>
      </c:if>
      <span class="tagListAd">
      	<portlet:renderURL var="setTagsUrl" windowState="maximized">
      	  <portlet:param name="portlet-action" value="modify" />
        	<portlet:param name="portlet-command" value="setTags" />
        	<portlet:param name="portlet-object" value="<%= ModuleUtils.MODULENAME_PROMOTIONS %>"/>
        	<portlet:param name="portlet-value" value="${promotion.id}"/>
        	<portlet:param name="popup" value="true" />
	      </portlet:renderURL>
      	<br/><ccp:tags url="${setTagsUrl}" />
      </span>
    </div>
  </div>
<%
  }
%>
  <c:if test="${projectPromotionsInfo.numberOfPages > 1}">
    <div class="pagination">
      <ccp:paginationControl object="projectPromotionsInfo"/>
    </div>
  </c:if>
</div>
