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
<%@ page import="com.concursive.commons.http.RequestUtils" %>
<%@ page import="com.concursive.connect.web.modules.members.dao.TeamMember" %>
<jsp:useBean id="User" class="com.concursive.connect.web.modules.login.dao.User" scope="session"/>
<jsp:useBean id="project" class="com.concursive.connect.web.modules.profile.dao.Project" scope="request"/>
<jsp:useBean id="currentMember" class="com.concursive.connect.web.modules.members.dao.TeamMember" scope="request"/>
<%@ include file="initPage.jsp" %>
<%
  if (project.getId() == -1) {
%>

<span class="red">This listing does not belong to you, or does not exist!</span>
<%
} else {
  String section = (String) request.getAttribute("IncludeSection");
  String includeSection = "projects_center_" + section + ".jsp";
  // Determine if showing the portal
  String includePortal = (String) request.getAttribute("includePortal");
  if (includePortal != null) {
    includeSection = "projects_center_" + includePortal + ".jsp";
  }
  String profileLabel = project.getLabel("Profile");
  String profileUrl = ctx + "/show/" + project.getUniqueId();
  String wikiLabel = project.getLabel("Wiki");
  String wikiUrl = ctx + "/show/" + project.getUniqueId() + "/wiki";
  String dashboardLabel = project.getLabel("Dashboard");
  String dashboardUrl = ctx + "/show/" + project.getUniqueId() + "/dashboard";
  String calendarLabel = project.getLabel("Calendar");
  String calendarUrl = ctx + "/show/" + project.getUniqueId() + "/calendar";
  String newsLabel = project.getLabel("News");
  String newsUrl = ctx + "/show/" + project.getUniqueId() + "/blog";
  String discussionLabel = project.getLabel("Discussion");
  String discussionUrl = ctx + "/show/" + project.getUniqueId() + "/discussion";
  String documentsLabel = project.getLabel("Documents");
  String documentsUrl = ctx + "/show/" + project.getUniqueId() + "/documents";
  String listsLabel = project.getLabel("Lists");
  String listsUrl = ctx + "/show/" + project.getUniqueId() + "/lists";
  String planLabel = project.getLabel("Plan");
  String planUrl = ctx + "/show/" + project.getUniqueId() + "/plans";
  String ticketsLabel = project.getLabel("Tickets");
  String ticketsUrl = ctx + "/show/" + project.getUniqueId() + "/issues";
  String teamLabel = project.getLabel("Team");
  String teamUrl = ctx + "/show/" + project.getUniqueId() + "/members";
  String adsLabel = project.getLabel("Ads");
  String adsUrl = ctx + "/show/" + project.getUniqueId() + "/promotions";
  String classifiedsLabel = project.getLabel("Classifieds");
  String classifiedsUrl = ctx + "/show/" + project.getUniqueId() + "/classifieds";
  String detailsLabel = project.getLabel("Details");
  String detailsUrl = ctx + "/show/" + project.getUniqueId() + "/details";
  String setupUrl = ctx + "/show/" + project.getUniqueId() + "/setup";
  String badgeLabel = project.getLabel("Badges");
  String badgeUrl = ctx + "/show/" + project.getUniqueId() + "/badges";
  String reviewsLabel = project.getLabel("Reviews");
  String reviewsUrl = ctx + "/show/" + project.getUniqueId() + "/reviews";
  String messagesLabel = project.getLabel("Messages");
  String messagesUrl = ctx + "/show/" + project.getUniqueId() + "/messages";
%>
<ccp:evaluate if="<%= !isPopup(request) %>">
<%-- TODO: Remove portletWindow layer once portlets have been created --%>
<ccp:include name="projects-projectcenter-header">
<div class="portletWindow">
  <div class="profile-header-portletWindowBackground">
    <div class="profile-header portlet1">
      <div class="profile-header portlet2">
        <div class="profile-header portlet3">
          <div class="profile-header portlet4">
            <div class="portletWrapper">
              <ccp:include name="projects-projectcenter-header-project-badge">
                <%--@elvariable id="requestProjectBadgeList" type="com.concursive.connect.web.modules.badges.dao.ProjectBadgeList"--%>
                <%--@elvariable id="projectBadge" type="com.concursive.connect.web.modules.badges.dao.ProjectBadge"--%>
                <%--@elvariable id="badge" type="com.concursive.connect.web.modules.badges.dao.Badge"--%>
                <c:if test="${!empty requestProjectBadgeList}">
                  <div class="portlet-section-header">
                    <ul>
                      <c:forEach items="${requestProjectBadgeList}" var="projectBadge">
                        <c:set var="projectBadge" value="${projectBadge}" scope="request"/>
                        <jsp:useBean id="projectBadge" class="com.concursive.connect.web.modules.badges.dao.ProjectBadge" scope="request"/>
                        <ccp:evaluate if="<%= projectBadge.getBadge().getLogoId() != -1 %>">
                          <li><a href="${ctx}/badge/${projectBadge.badge.id}" rel="shadowbox"><img alt="<c:out value="${projectBadge.badge.title}"/>" title="<c:out value="${projectBadge.badge.title}"/>" src="${ctx}/image/<%= projectBadge.getBadge().getLogo().getUrlName(45,45) %>" class="badgeImage" /></a></li>
                        </ccp:evaluate>
                      </c:forEach>
                    </ul>
                  </div>
                </c:if>
              </ccp:include>
              <ccp:include name="projects-projectcenter-header-project-photo">
                <ccp:evaluate if="<%= project.hasCategoryId() && project.getCategory().hasLogoId() %>">
                  <div class="portlet-section-footer">
                    <div class="icon">
                      <img alt="Default photo" src="${ctx}/image/<%= project.getCategory().getLogo().getUrlName(50,50) %>" class="default-photo" />
                    </div>
                  </div>
                </ccp:evaluate>
                <ccp:evaluate if="<%= !project.hasCategoryId() || !project.getCategory().hasLogoId() %>">
                  <div class="portlet-section-footer">
                    <div class="no-photo">
                      <p><span>no image</span></p>
                    </div>
                  </div>
                </ccp:evaluate>
              </ccp:include>
              <div class="portlet-section-body">
                <ccp:include name="projects-projectcenter-header-project">
                  <%-- Microformat Spec for vcard information http://microformats.org/wiki/hcard-cheatsheet --%>
                  <div class="vcard">
                    <ccp:include name="projects-projectcenter-header-project-title">
                      <h2 class="fn">
                        <c:out value="${project.title}"/>
                        <ccp:permission name="project-profile-admin">
                        <c:choose>
                          <c:when test="${empty project.description}">
                            <span><a href="javascript:showPanel('','${ctx}/show/${project.uniqueId}/app/edit_profile','600')">Edit Profile</a></span>
                          </c:when>
                          <c:otherwise>
                            <span><a href="${ctx}/modify/${project.uniqueId}/profile">Edit Profile</a></span>
                          </c:otherwise>
                        </c:choose>
                      </ccp:permission>
                      </h2>
                      <ccp:include name="projects-projectcenter-header-project-address">
                        <ccp:evaluate if="${!empty project.location || !empty project.addressTo}">
                          <address class="adr">
                            <c:if test="${!empty project.businessPhone}">
                              <span class="tel value"><c:out value="${project.businessPhone}"/></span>
                            </c:if>
                            <c:if test="${!empty project.businessFax}">
                              <span class="tel fax"><c:out value="${project.businessFax}"/> fax</span>
                            </c:if>
                            <c:if test="${!empty project.email1}">
                              <span class="email value"><c:out value="${project.email1}"/></span>
                            </c:if>
                            <c:if test="${!empty project.addressTo}"><span class="street-address"><c:out value="${project.addressTo}"/></span></c:if>
                            <c:if test="${!empty project.addressLine1}"><span class="address1"><c:out value="${project.addressLine1}"/></span></c:if>
                            <c:if test="${!empty project.addressLine2}"><span class="address2"><c:out value="${project.addressLine2}"/></span></c:if>
                            <c:if test="${!empty project.addressLine3}"><span class="address3"><c:out value="${project.addressLine3}"/></span></c:if>
                            <c:if test="${!empty project.location}">
                                <span class="street-address">
                                  <c:if test="${!empty project.city}"><span class="locality">${project.city}</span>,</c:if>
                                  <c:if test="${!empty project.state}"><span class="region">${project.state}</span></c:if>
                                  <c:if test="${!empty project.postalCode}"><span class="postal-code">${project.postalCode}</span></c:if>
                                <c:if test="${!empty project.country && project.country ne 'UNITED STATES'}">
                                  <span class="country-name"><c:out value="${project.country}"/></span>
                                </c:if>
                              </span>
                            </c:if>
                          </address>
                          <c:if test="${!empty project.webPage}">
                          	<span class="url"><a href="${project.webPage}" target="_blank"><c:out value="${project.webPage}"/></a></span>
                          </c:if>
                        </ccp:evaluate>
                      </ccp:include>
                    </ccp:include>
                  </div>
                  <%-- End vcard --%>
                  <ccp:include name="projects-projectcenter-header-project-status">
                    <%--
                    <ccp:evaluate if="<%= project.getFeatures().getAllowGuests() %>">
                      <img src="${ctx}/images/public.gif" border="0" alt="" align="absmiddle" title="Publicly accessible to all users"/>
                    </ccp:evaluate>
                    --%>
                    <ccp:evaluate if="<%= project.getFeatures().getMembershipRequired() %>"> <img src="${ctx}/images/members_only.gif" border="0" alt="" align="absmiddle" title="Requires membership to participate"/> </ccp:evaluate>
                    <ccp:evaluate if="<%= project.getApprovalDate() == null %>"> <img src="${ctx}/images/unapproved.gif" border="0" alt="" align="absmiddle" title="Not yet visible or ready for participation"/> </ccp:evaluate>
                  </ccp:include>
                </ccp:include>
              </div>
              <c:if test="${project.features.showReviews && project.ratingCount > 0 && !empty project.category && project.category.description ne 'People'}">
                <div class="portlet-table">
                  <ccp:rating id='${project.id}'
                                 showText='true'
                                 count='${project.ratingCount}'
                                 value='${project.ratingValue}'
                                 url=''/>
                </div>
              </c:if>
              <ccp:include name="projects-projectcenter-header-project-setup">
                <div class="portlet-menu">
                  <ccp:permission if="any" name="project-profile-admin, project-details-view">
                    <ul>
                      <ccp:permission name="project-details-view">
                        <ccp:evaluate if="<%= User.getAccessAdmin() || project.getFeatures().getShowDetails() || currentMember.getRoleId() <= TeamMember.PROJECT_ADMIN %>">
                          <li><a href="<%= detailsUrl %>"><%= toHtml(detailsLabel) %></a></li>
                        </ccp:evaluate>
                      </ccp:permission>
                      <ccp:evaluate if="<%= User.getAccessAdmin() || currentMember.getRoleId() <= TeamMember.PROJECT_ADMIN %>">
                        <li class="last"><a href="<%= setupUrl %>">Setup</a></li>
                      </ccp:evaluate>
                    </ul>
                  </ccp:permission>
                  <ccp:include name="projects-projectcenter-header-role">
                    <ccp:evaluate if="<%= User.getAccessAdmin() || currentMember.isTemporaryAdmin() %>"> <p>(<ccp:label name="projectsCenter.admin">Admin</ccp:label>)</p></ccp:evaluate>
                  </ccp:include>
                </div>
              </ccp:include>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<%-- End Profile header Region --%>

<%-- Profile Navigation Region --%>
<div class="portletWindow">
  <div class="profile-menu-portletWindowBackground">
    <div class="profile-menu portlet1">
      <div class="profile-menu portlet2">
        <div class="profile-menu portlet3">
          <div class="profile-menu portlet4">
            <div class="portlet-menu">
              <ul>
                <ccp:permission name="project-profile-view">
                  <c:if test="${project.features.showProfile}">
                    <ccp:tabbedMenu text="<%= profileLabel %>" key="profile" value="<%= section %>" url="<%= profileUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-dashboard-view">
                  <c:if test="${project.features.showDashboard}">
                    <ccp:tabbedMenu text="<%= dashboardLabel %>" key="dashboard" value="<%= section %>" url="<%= dashboardUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-reviews-view">
                  <c:if test="${project.features.showReviews}">
                    <ccp:tabbedMenu text="<%= reviewsLabel %>" key="reviews" value="<%= section %>" url="<%= reviewsUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-news-view">
                  <c:if test="${project.features.showNews}">
                    <ccp:tabbedMenu text="<%= newsLabel %>" key="home,news,blog" value="<%= section %>" url="<%= newsUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-calendar-view">
                  <c:if test="${project.features.showCalendar}">
                    <ccp:tabbedMenu text="<%= calendarLabel %>" key="calendar" value="<%= section %>" url="<%= calendarUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-wiki-view">
                  <c:if test="${project.features.showWiki}">
                    <ccp:tabbedMenu text="<%= wikiLabel %>" key="wiki" value="<%= section %>" url="<%= wikiUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-discussion-forums-view">
                  <c:if test="${project.features.showDiscussion}">
                    <ccp:tabbedMenu text="<%= discussionLabel %>" key="discussion" value="<%= section %>" url="<%= discussionUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-ads-view">
                  <c:if test="${project.features.showAds}">
                    <ccp:tabbedMenu text="<%= adsLabel %>" key="ads,promotions" value="<%= section %>" url="<%= adsUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-classifieds-view">
                  <c:if test="${project.features.showClassifieds}">
                    <ccp:tabbedMenu text="<%= classifiedsLabel %>" key="classifieds" value="<%= section %>" url="<%= classifiedsUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-documents-view">
                  <c:if test="${project.features.showDocuments}">
                    <ccp:tabbedMenu text="<%= documentsLabel %>" key="documents,folder,file" value="<%= section %>" url="<%= documentsUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-lists-view">
                  <c:if test="${project.features.showLists}">
                    <ccp:tabbedMenu text="<%= listsLabel %>" key="lists" value="<%= section %>" url="<%= listsUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-plan-view">
                  <c:if test="${project.features.showPlan}">
                    <ccp:tabbedMenu text="<%= planLabel %>" key="requirements,assignments" value="<%= section %>" url="<%= planUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-badges-view">
                  <c:if test="${project.features.showBadges}">
                    <ccp:tabbedMenu text="<%= badgeLabel %>" key="badges" value="<%= section %>" url="<%= badgeUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-tickets-view">
                  <c:if test="${project.features.showTickets}">
                    <ccp:tabbedMenu text="<%= ticketsLabel %>" key="tickets,issues" value="<%= section %>" url="<%= ticketsUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-team-view">
                  <c:if test="${project.features.showTeam}">
                    <ccp:tabbedMenu text="<%= teamLabel %>" key="members" value="<%= section %>" url="<%= teamUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
                <ccp:permission name="project-private-messages-view">
                  <c:if test="${project.features.showMessages}">
                    <ccp:tabbedMenu text="<%= messagesLabel %>" key="messages" value="<%= section %>" url="<%= messagesUrl %>" type="li"/>
                  </c:if>
                </ccp:permission>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</ccp:include>

<div class="yui-content">
  <div class="projectCenterPortalContainer">
    <ccp:debug value="<%= includeSection %>" />
    <jsp:include page="<%= includeSection %>" flush="true"/>
  </div>
</div>
</ccp:evaluate>
<ccp:evaluate if="<%= isPopup(request) %>">
  <ccp:debug value="<%= includeSection %>" />
  <jsp:include page="<%= includeSection %>" flush="true"/>
</ccp:evaluate>
<%
  }
%>
