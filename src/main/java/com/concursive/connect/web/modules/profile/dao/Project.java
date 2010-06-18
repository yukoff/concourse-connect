/*
 * ConcourseConnect
 * Copyright 2009 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect, an open source social business
 * software and community platform.
 *
 * Concursive ConcourseConnect is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * Under the terms of the GNU Affero General Public License you must release the
 * complete source code for any application that uses any part of ConcourseConnect
 * (system header files and libraries used by the operating system are excluded).
 * These terms must be included in any work that has ConcourseConnect components.
 * If you are developing and distributing open source applications under the
 * GNU Affero General Public License, then you are free to use ConcourseConnect
 * under the GNU Affero General Public License.
 *
 * If you are deploying a web site in which users interact with any portion of
 * ConcourseConnect over a network, the complete source code changes must be made
 * available.  For example, include a link to the source archive directly from
 * your web site.
 *
 * For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
 * products, and do not license and distribute their source code under the GNU
 * Affero General Public License, Concursive provides a flexible commercial
 * license.
 *
 * To anyone in doubt, we recommend the commercial license. Our commercial license
 * is competitively priced and will eliminate any confusion about how
 * ConcourseConnect can be used and distributed.
 *
 * ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.web.modules.profile.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.html.HTMLUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.cms.portal.dao.DashboardList;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.badges.dao.ProjectBadgeList;
import com.concursive.connect.web.modules.blog.dao.BlogPostCategoryList;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.calendar.dao.MeetingList;
import com.concursive.connect.web.modules.classifieds.dao.ClassifiedList;
import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLogList;
import com.concursive.connect.web.modules.common.social.viewing.utils.Viewing;
import com.concursive.connect.web.modules.discussion.dao.ForumList;
import com.concursive.connect.web.modules.documents.dao.FileFolderList;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.issues.dao.ProjectTicketCount;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryList;
import com.concursive.connect.web.modules.issues.dao.TicketList;
import com.concursive.connect.web.modules.lists.dao.TaskCategoryList;
import com.concursive.connect.web.modules.lists.utils.TaskUtils;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.messages.dao.PrivateMessageList;
import com.concursive.connect.web.modules.plans.dao.RequirementList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.promotions.dao.AdList;
import com.concursive.connect.web.modules.reports.dao.ReportQueueList;
import com.concursive.connect.web.modules.reviews.dao.ProjectRatingList;
import com.concursive.connect.web.modules.services.dao.ServiceList;
import com.concursive.connect.web.modules.timesheet.dao.DailyTimesheetList;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.modules.webcast.dao.Webcast;

import java.sql.*;
import java.util.ArrayList;

/**
 * Represents a Project in iTeam
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 23, 2001
 */
public class Project extends GenericBean {

  public static final String TABLE = "projects";
  public static final String PRIMARY_KEY = "project_id";
  public final static int PORTAL_TYPE_ARTICLE = -1;
  public final static int PORTAL_TYPE_HOMEPAGE = 1;
  public final static int PORTAL_TYPE_COMMUNITY = 2;
  public final static int PORTAL_TYPE_ORDER = 3;
  public final static int PORTAL_TYPE_CONTACT_US = 4;
  public final static int PORTAL_TYPE_NEWS = 5;
  public final static int PORTAL_TYPE_WIKI = 6;
  private int instanceId = -1;
  private int id = -1;
  private int groupId = -1;
  private int departmentId = -1;
  private int categoryId = -1;
  private int templateId = -1;
  private boolean template = false;
  private boolean clone = false;
  private String title = "";
  private String uniqueId = "";
  private String shortDescription = "";
  private String requestedBy = "";
  private String requestedByDept = "";
  private Timestamp requestDate = null;
  private boolean approved = false;
  private Timestamp approvalDate = null;
  private boolean closed = false;
  private Timestamp closeDate = null;
  private Timestamp estimatedCloseDate = null;
  private double budget = -1;
  private String budgetCurrency = null;
  private int level = -1;
  private int owner = -1;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private int modifiedBy = -1;
  private int readCount = 0;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAverage = 0.0;
  private int logoId = -1;
  private String keywords = null;
  private boolean profile = false;
  private String source = null;
  private String style = null;
  private boolean styleEnabled = false;
  private String description = null;
  private boolean systemDefault = false;
  // Portal capabilities
  private boolean portal = false;
  private String portalHeader = null;
  private String portalFormat = null;
  private String portalKey = null;
  private boolean portalBuildNewsBody = false;
  private boolean portalNewsMenu = false;
  private int portalPageType = PORTAL_TYPE_ARTICLE;
  private boolean portalDefault = false;
  private String portalLink = null;
  private int languageId = -1;
  // Integration capabilities
  private String concursiveCRMUrl = null;
  private String concursiveCRMDomain = null;
  private String concursiveCRMCode = null;
  private String concursiveCRMClient = null;
  // Personalization
  private String email1 = null;
  private String email2 = null;
  private String email3 = null;
  private String homePhone = null;
  private String homePhoneExt = null;
  private String home2Phone = null;
  private String home2PhoneExt = null;
  private String homeFax = null;
  private String businessPhone = null;
  private String businessPhoneExt = null;
  private String business2Phone = null;
  private String business2PhoneExt = null;
  private String businessFax = null;
  private String mobilePhone = null;
  private String pagerNumber = null;
  private String carPhone = null;
  private String radioPhone = null;
  private String webPage = null;
  private String twitterId = null;
  private String addressTo = null;
  private String addressLine1 = null;
  private String addressLine2 = null;
  private String addressLine3 = null;
  private String city = null;
  private String state = null;
  private String country = null;
  private String postalCode = null;
  private double latitude = 0.0;
  private double longitude = 0.0;
  private int subCategory1Id = -1;
  private int subCategory2Id = -1;
  private int subCategory3Id = -1;
  private String facebookPage = null;
  private String youtubeChannelId = null;
  // Live Video properties
  private String ustreamId = null;
  private String livestreamId = null;
  private String justintvId = null;
  private String qikId = null;
  // Helper properties
  private String userRange = null;
  private boolean apiRestore = false;
  private FileItemList siteLogos = new FileItemList();
  // Project cached items
  private ProjectFeatures features = new ProjectFeatures();
  private TeamMemberList team = new TeamMemberList();
  private PermissionList permissions = new PermissionList();
  private FileItemList images = new FileItemList();
  private ServiceList services = new ServiceList();
  // TODO: move these out of the project class
  private int ticketsClosed = -1;
  private int ticketsOpen = -1;
  private int ticketsOverdue = -1;

  /**
   * Constructor for the Project object
   */
  public Project() {
  }

  /**
   * Constructor for the Project object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public Project(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * Constructor for the Project object
   *
   * @param db        Description of Parameter
   * @param thisId    Description of Parameter
   * @param userRange Description of Parameter
   * @throws SQLException Description of Exception
   */
  public Project(Connection db, int thisId, String userRange) throws SQLException {
    this.userRange = userRange;
    queryRecord(db, thisId);
  }

  /**
   * Constructor for the Project object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Project(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }

  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void queryRecord(Connection db, int thisId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM projects p " +
            "WHERE p.project_id = ? ");
    if (userRange != null) {
      sql.append(
          "AND (project_id in (SELECT DISTINCT project_id FROM project_team WHERE user_id IN (" + userRange + ") " +
              "AND project_id = ?) " +
              "OR p.enteredby IN (" + userRange + ") " +
              "OR (p.allow_guests = ? AND p.membership_required = ?) " +
              "OR p.portal = ?) ");
    }

    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, thisId);
    if (userRange != null) {
      pst.setInt(++i, thisId);
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, false);
      pst.setBoolean(++i, true);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }

  public boolean getWebcastInfoExists() {
    return
        (StringUtils.hasText(livestreamId) ||
            StringUtils.hasText(justintvId) ||
            StringUtils.hasText(ustreamId) ||
            StringUtils.hasText(qikId)
        );
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setInstanceId(String tmp) {
    this.instanceId = Integer.parseInt(tmp);
  }

  /**
   * Sets the Id attribute of the Project object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }

  /**
   * Sets the id attribute of the Project object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  /**
   * Sets the GroupId attribute of the Project object
   *
   * @param tmp The new GroupId value
   */
  public void setGroupId(int tmp) {
    this.groupId = tmp;
  }

  /**
   * Sets the groupId attribute of the Project object
   *
   * @param tmp The new groupId value
   */
  public void setGroupId(String tmp) {
    this.groupId = Integer.parseInt(tmp);
  }

  /**
   * Sets the DepartmentId attribute of the Project object
   *
   * @param tmp The new DepartmentId value
   */
  public void setDepartmentId(int tmp) {
    this.departmentId = tmp;
  }

  /**
   * Sets the departmentId attribute of the Project object
   *
   * @param tmp The new departmentId value
   */
  public void setDepartmentId(String tmp) {
    this.departmentId = Integer.parseInt(tmp);
  }

  /**
   * Sets the categoryId attribute of the Project object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    categoryId = tmp;
  }

  /**
   * Sets the categoryId attribute of the Project object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    categoryId = Integer.parseInt(tmp);
  }

  /**
   * Sets the templateId attribute of the Project object
   *
   * @param tmp The new templateId value
   */
  public void setTemplateId(int tmp) {
    this.templateId = tmp;
  }

  /**
   * Sets the templateId attribute of the Project object
   *
   * @param tmp The new templateId value
   */
  public void setTemplateId(String tmp) {
    this.templateId = Integer.parseInt(tmp);
  }

  public boolean isTemplate() {
    return template;
  }

  public void setTemplate(boolean template) {
    this.template = template;
  }

  public void setTemplate(String tmp) {
    this.template = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean isClone() {
    return clone;
  }

  public void setClone(boolean clone) {
    this.clone = clone;
  }

  /**
   * Sets the Title attribute of the Project object
   *
   * @param tmp The new Title value
   */
  public void setTitle(String tmp) {
    this.title = tmp;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  /**
   * Sets the ShortDescription attribute of the Project object
   *
   * @param tmp The new ShortDescription value
   */
  public void setShortDescription(String tmp) {
    this.shortDescription = tmp;
  }

  /**
   * Sets the RequestedBy attribute of the Project object
   *
   * @param tmp The new RequestedBy value
   */
  public void setRequestedBy(String tmp) {
    this.requestedBy = tmp;
  }

  /**
   * Sets the RequestedByDept attribute of the Project object
   *
   * @param tmp The new RequestedByDept value
   */
  public void setRequestedByDept(String tmp) {
    this.requestedByDept = tmp;
  }

  /**
   * Sets the RequestDate attribute of the Project object
   *
   * @param tmp The new RequestDate value
   */
  public void setRequestDate(Timestamp tmp) {
    this.requestDate = tmp;
  }

  /**
   * Sets the requestDate attribute of the Project object
   *
   * @param tmp The new requestDate value
   */
  public void setRequestDate(String tmp) {
    requestDate = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the Approved attribute of the Project object
   *
   * @param tmp The new Approved value
   */
  public void setApproved(boolean tmp) {
    this.approved = tmp;
  }

  /**
   * Sets the approved attribute of the Project object
   *
   * @param tmp The new approved value
   */
  public void setApproved(String tmp) {
    approved = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Sets the ApprovalDate attribute of the Project object
   *
   * @param tmp The new ApprovalDate value
   */
  public void setApprovalDate(java.sql.Timestamp tmp) {
    this.approvalDate = tmp;
  }

  /**
   * Sets the approvalDate attribute of the Project object
   *
   * @param tmp The new approvalDate value
   */
  public void setApprovalDate(String tmp) {
    approvalDate = DatabaseUtils.parseDateToTimestamp(tmp);
  }

  /**
   * Sets the closed attribute of the Project object
   *
   * @param tmp The new closed value
   */
  public void setClosed(boolean tmp) {
    this.closed = tmp;
  }

  /**
   * Sets the closed attribute of the Project object
   *
   * @param tmp The new closed value
   */
  public void setClosed(String tmp) {
    closed = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Sets the closeDate attribute of the Project object
   *
   * @param tmp The new closeDate value
   */
  public void setCloseDate(java.sql.Timestamp tmp) {
    this.closeDate = tmp;
  }

  /**
   * Sets the closeDate attribute of the Project object
   *
   * @param tmp The new closeDate value
   */
  public void setCloseDate(String tmp) {
    this.closeDate = DatabaseUtils.parseDateToTimestamp(tmp);
  }

  /**
   * Sets the estimatedCloseDate attribute of the Project object
   *
   * @param tmp The new estimatedCloseDate value
   */
  public void setEstimatedCloseDate(Timestamp tmp) {
    this.estimatedCloseDate = tmp;
  }

  /**
   * Sets the estimatedCloseDate attribute of the Project object
   *
   * @param tmp The new estimatedCloseDate value
   */
  public void setEstimatedCloseDate(String tmp) {
    this.estimatedCloseDate = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the budget attribute of the Project object
   *
   * @param tmp The new budget value
   */
  public void setBudget(double tmp) {
    this.budget = tmp;
  }

  /**
   * Sets the budget attribute of the Project object
   *
   * @param tmp The new budget value
   */
  public void setBudget(String tmp) {
    this.budget = Double.parseDouble(tmp);
  }

  /**
   * Sets the budgetCurrency attribute of the Project object
   *
   * @param tmp The new budgetCurrency value
   */
  public void setBudgetCurrency(String tmp) {
    this.budgetCurrency = tmp;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  /**
   * Sets the owner attribute of the Project object
   *
   * @param tmp The new owner value
   */
  public void setOwner(int tmp) {
    this.owner = tmp;
  }

  /**
   * Sets the owner attribute of the Project object
   *
   * @param tmp The new owner value
   */
  public void setOwner(String tmp) {
    this.owner = Integer.parseInt(tmp);
  }

  /**
   * Sets the enteredBy attribute of the Project object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }

  /**
   * Sets the entered attribute of the Project object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the entered attribute of the Project object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    entered = tmp;
  }

  /**
   * Sets the enteredBy attribute of the Project object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the modified attribute of the Project object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the modified attribute of the Project object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    modified = tmp;
  }

  /**
   * Sets the modifiedBy attribute of the Project object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }

  /**
   * Sets the modifiedBy attribute of the Project object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the Team attribute of the Project object
   *
   * @param tmp The new Team value
   */
  public void setTeam(TeamMemberList tmp) {
    this.team = tmp;
  }

  /**
   * Sets the portal attribute of the Project object
   *
   * @param tmp The new portal value
   */
  public void setPortal(boolean tmp) {
    this.portal = tmp;
  }

  /**
   * Sets the portal attribute of the Project object
   *
   * @param tmp The new portal value
   */
  public void setPortal(String tmp) {
    this.portal = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Gets the portalHeader attribute of the Project object
   *
   * @return The portalHeader value
   */
  public String getPortalHeader() {
    return portalHeader;
  }

  /**
   * Sets the portalHeader attribute of the Project object
   *
   * @param tmp The new portalHeader value
   */
  public void setPortalHeader(String tmp) {
    this.portalHeader = tmp;
  }

  /**
   * Gets the portalFormat attribute of the Project object
   *
   * @return The portalFormat value
   */
  public String getPortalFormat() {
    return portalFormat;
  }

  /**
   * Sets the portalFormat attribute of the Project object
   *
   * @param tmp The new portalFormat value
   */
  public void setPortalFormat(String tmp) {
    this.portalFormat = tmp;
  }

  /**
   * Gets the portalKey attribute of the Project object
   *
   * @return The portalKey value
   */
  public String getPortalKey() {
    return portalKey;
  }

  public String getPortalKeyLink() {
    return StringUtils.replace(StringUtils.jsEscape(portalKey), "%20", "+");
  }

  /**
   * Sets the portalKey attribute of the Project object
   *
   * @param tmp The new portalKey value
   */
  public void setPortalKey(String tmp) {
    this.portalKey = tmp;
  }

  /**
   * Gets the portalBuildNewsBody attribute of the Project object
   *
   * @return The portalBuildNewsBody value
   */
  public boolean getPortalBuildNewsBody() {
    return portalBuildNewsBody;
  }

  /**
   * Sets the portalBuildNewsBody attribute of the Project object
   *
   * @param tmp The new portalBuildNewsBody value
   */
  public void setPortalBuildNewsBody(boolean tmp) {
    this.portalBuildNewsBody = tmp;
  }

  /**
   * Sets the portalBuildNewsBody attribute of the Project object
   *
   * @param tmp The new portalBuildNewsBody value
   */
  public void setPortalBuildNewsBody(String tmp) {
    this.portalBuildNewsBody = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Gets the portalNewsMenu attribute of the Project object
   *
   * @return The portalNewsMenu value
   */
  public boolean getPortalNewsMenu() {
    return portalNewsMenu;
  }

  /**
   * Sets the portalNewsMenu attribute of the Project object
   *
   * @param tmp The new portalNewsMenu value
   */
  public void setPortalNewsMenu(boolean tmp) {
    this.portalNewsMenu = tmp;
  }

  /**
   * Sets the portalNewsMenu attribute of the Project object
   *
   * @param tmp The new portalNewsMenu value
   */
  public void setPortalNewsMenu(String tmp) {
    this.portalNewsMenu = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Gets the portalPageType attribute of the Project object
   *
   * @return The portalPageType value
   */
  public int getPortalPageType() {
    return portalPageType;
  }

  /**
   * Sets the portalPageType attribute of the Project object
   *
   * @param tmp The new portalPageType value
   */
  public void setPortalPageType(int tmp) {
    this.portalPageType = tmp;
  }

  /**
   * Sets the portalPageType attribute of the Project object
   *
   * @param tmp The new portalPageType value
   */
  public void setPortalPageType(String tmp) {
    this.portalPageType = Integer.parseInt(tmp);
  }

  public boolean getPortalDefault() {
    return portalDefault;
  }

  public void setPortalDefault(boolean portalDefault) {
    this.portalDefault = portalDefault;
  }

  public String getPortalLink() {
    return portalLink;
  }

  public void setPortalLink(String portalLink) {
    this.portalLink = portalLink;
  }

  /**
   * Sets the allowGuests attribute of the Project object
   *
   * @param tmp The new allowGuests value
   */
  public void setAllowGuests(boolean tmp) {
    features.setAllowGuests(tmp);
  }

  /**
   * Sets the allowGuests attribute of the Project object
   *
   * @param tmp The new allowGuests value
   */
  public void setAllowGuests(String tmp) {
    features.setAllowGuests(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the updateAllowGuests attribute of the Project object
   *
   * @param tmp The new updateAllowGuests value
   */
  public void setUpdateAllowGuests(boolean tmp) {
    features.setUpdateAllowGuests(tmp);
  }

  public void setUpdateAllowGuests(String tmp) {
    features.setUpdateAllowGuests(DatabaseUtils.parseBoolean(tmp));
  }

  public void setAllowParticipants(boolean tmp) {
    features.setAllowParticipants(tmp);
  }

  public void setAllowParticipants(String tmp) {
    features.setAllowParticipants(DatabaseUtils.parseBoolean(tmp));
  }

  public void setUpdateAllowParticipants(boolean tmp) {
    features.setUpdateAllowParticipants(tmp);
  }

  public void setUpdateAllowParticipants(String tmp) {
    features.setUpdateAllowParticipants(DatabaseUtils.parseBoolean(tmp));
  }

  public void setUpdateMembershipRequired(boolean tmp) {
    features.setUpdateMembershipRequired(tmp);
  }

  public void setUpdateMembershipRequired(String tmp) {
    features.setUpdateMembershipRequired(DatabaseUtils.parseBoolean(tmp));
  }

  public void setMembershipRequired(boolean tmp) {
    features.setMembershipRequired(tmp);
  }

  public void setMembershipRequired(String tmp) {
    features.setMembershipRequired(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowDashboard(boolean showDashboard) {
    features.setShowDashboard(showDashboard);
  }

  public void setShowDashboard(String tmp) {
    features.setShowDashboard(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the showCalendar attribute of the Project object
   *
   * @param tmp The new showCalendar value
   */
  public void setShowCalendar(boolean tmp) {
    features.setShowCalendar(tmp);
  }

  /**
   * Sets the showCalendar attribute of the Project object
   *
   * @param tmp The new showCalendar value
   */
  public void setShowCalendar(String tmp) {
    features.setShowCalendar(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowWiki(boolean showWiki) {
    features.setShowWiki(showWiki);
  }

  public void setShowWiki(String tmp) {
    features.setShowWiki(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the showNews attribute of the Project object
   *
   * @param tmp The new showNews value
   */
  public void setShowNews(boolean tmp) {
    features.setShowNews(tmp);
  }

  public void setShowBlog(boolean tmp) {
    this.setShowNews(tmp);
  }

  /**
   * Sets the showNews attribute of the Project object
   *
   * @param tmp The new showNews value
   */
  public void setShowNews(String tmp) {
    features.setShowNews(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowBlog(String tmp) {
    this.setShowNews(tmp);
  }

  /**
   * Sets the showDetails attribute of the Project object
   *
   * @param tmp The new showDetails value
   */
  public void setShowDetails(boolean tmp) {
    features.setShowDetails(tmp);
  }

  /**
   * Sets the showDetails attribute of the Project object
   *
   * @param tmp The new showDetails value
   */
  public void setShowDetails(String tmp) {
    features.setShowDetails(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the showTeam attribute of the Project object
   *
   * @param tmp The new showTeam value
   */
  public void setShowTeam(boolean tmp) {
    features.setShowTeam(tmp);
  }

  /**
   * Sets the showTeam attribute of the Project object
   *
   * @param tmp The new showTeam value
   */
  public void setShowTeam(String tmp) {
    features.setShowTeam(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the showPlan attribute of the Project object
   *
   * @param tmp The new showPlan value
   */
  public void setShowPlan(boolean tmp) {
    features.setShowPlan(tmp);
  }

  /**
   * Sets the showPlan attribute of the Project object
   *
   * @param tmp The new showPlan value
   */
  public void setShowPlan(String tmp) {
    features.setShowPlan(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the showLists attribute of the Project object
   *
   * @param tmp The new showLists value
   */
  public void setShowLists(boolean tmp) {
    features.setShowLists(tmp);
  }

  /**
   * Sets the showLists attribute of the Project object
   *
   * @param tmp The new showLists value
   */
  public void setShowLists(String tmp) {
    features.setShowLists(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the showDiscussion attribute of the Project object
   *
   * @param tmp The new showDiscussion value
   */
  public void setShowDiscussion(boolean tmp) {
    features.setShowDiscussion(tmp);
  }

  /**
   * Sets the showDiscussion attribute of the Project object
   *
   * @param tmp The new showDiscussion value
   */
  public void setShowDiscussion(String tmp) {
    features.setShowDiscussion(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the showTickets attribute of the Project object
   *
   * @param tmp The new showTickets value
   */
  public void setShowTickets(boolean tmp) {
    features.setShowTickets(tmp);
  }

  public void setShowIssues(boolean tmp) {
    this.setShowTickets(tmp);
  }

  /**
   * Sets the showTickets attribute of the Project object
   *
   * @param tmp The new showTickets value
   */
  public void setShowTickets(String tmp) {
    features.setShowTickets(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowIssues(String tmp) {
    this.setShowTickets(tmp);
  }

  /**
   * Sets the showDocuments attribute of the Project object
   *
   * @param tmp The new showDocuments value
   */
  public void setShowDocuments(boolean tmp) {
    features.setShowDocuments(tmp);
  }

  /**
   * Sets the showDocuments attribute of the Project object
   *
   * @param tmp The new showDocuments value
   */
  public void setShowDocuments(String tmp) {
    features.setShowDocuments(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowBadges(boolean tmp) {
    features.setShowBadges(tmp);
  }

  public void setShowBadges(String tmp) {
    features.setShowBadges(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowReviews(boolean tmp) {
    features.setShowReviews(tmp);
  }

  public void setShowReviews(String tmp) {
    features.setShowReviews(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowClassifieds(boolean tmp) {
    features.setShowClassifieds(tmp);
  }

  public void setShowClassifieds(String tmp) {
    features.setShowClassifieds(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowAds(boolean tmp) {
    features.setShowAds(tmp);
  }

  public void setShowAds(String tmp) {
    features.setShowAds(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowProfile(boolean tmp) {
    features.setShowProfile(tmp);
  }

  public void setShowProfile(String tmp) {
    features.setShowProfile(DatabaseUtils.parseBoolean(tmp));
  }

  public void setShowMessages(boolean tmp) {
    features.setShowMessages(tmp);
  }

  public void setShowMessages(String tmp) {
    features.setShowMessages(DatabaseUtils.parseBoolean(tmp));
  }

  /**
   * Sets the labelCalendar attribute of the Project object
   *
   * @param tmp The new labelCalendar value
   */
  public void setLabelCalendar(String tmp) {
    features.setLabelCalendar(tmp);
  }

  public void setLabelDashboard(String labelDashboard) {
    features.setLabelDashboard(labelDashboard);
  }

  public void setLabelWiki(String labelWiki) {
    features.setLabelWiki(labelWiki);
  }

  /**
   * Sets the labelNews attribute of the Project object
   *
   * @param tmp The new labelNews value
   */
  public void setLabelNews(String tmp) {
    features.setLabelNews(tmp);
  }

  public void setLabelBlog(String tmp) {
    this.setLabelNews(tmp);
  }

  /**
   * Sets the labelDetails attribute of the Project object
   *
   * @param tmp The new labelDetails value
   */
  public void setLabelDetails(String tmp) {
    features.setLabelDetails(tmp);
  }

  /**
   * Sets the labelTeam attribute of the Project object
   *
   * @param tmp The new labelTeam value
   */
  public void setLabelTeam(String tmp) {
    features.setLabelTeam(tmp);
  }

  /**
   * Sets the labelPlan attribute of the Project object
   *
   * @param tmp The new labelPlan value
   */
  public void setLabelPlan(String tmp) {
    features.setLabelPlan(tmp);
  }

  /**
   * Sets the labelLists attribute of the Project object
   *
   * @param tmp The new labelLists value
   */
  public void setLabelLists(String tmp) {
    features.setLabelLists(tmp);
  }

  /**
   * Sets the labelDiscussion attribute of the Project object
   *
   * @param tmp The new labelDiscussion value
   */
  public void setLabelDiscussion(String tmp) {
    features.setLabelDiscussion(tmp);
  }

  /**
   * Sets the labelTickets attribute of the Project object
   *
   * @param tmp The new labelTickets value
   */
  public void setLabelTickets(String tmp) {
    features.setLabelTickets(tmp);
  }

  public void setLabelIssues(String tmp) {
    this.setLabelTickets(tmp);
  }

  /**
   * Sets the labelDocuments attribute of the Project object
   *
   * @param tmp The new labelDocuments value
   */
  public void setLabelDocuments(String tmp) {
    features.setLabelDocuments(tmp);
  }

  public void setLabelBadges(String tmp) {
    features.setLabelBadges(tmp);
  }

  public void setLabelReviews(String tmp) {
    features.setLabelReviews(tmp);
  }

  public void setLabelClassifieds(String tmp) {
    features.setLabelClassifieds(tmp);
  }

  public void setLabelAds(String tmp) {
    features.setLabelAds(tmp);
  }

  public void setLabelProfile(String tmp) {
    features.setLabelProfile(tmp);
  }

  public void setLabelMessages(String tmp) {
    features.setLabelMessages(tmp);
  }

  public int getOrderNews() {
    return features.getOrderNews();
  }

  public int getOrderBlog() {
    return this.getOrderNews();
  }

  public void setOrderNews(int orderNews) {
    features.setOrderNews(orderNews);
  }

  public void setOrderNews(String tmp) {
    features.setOrderNews(Integer.parseInt(tmp));
  }

  public void setOrderBlog(int orderNews) {
    this.setOrderNews(orderNews);
  }

  public void setOrderBlog(String tmp) {
    this.setOrderNews(tmp);
  }

  public int getOrderCalendar() {
    return features.getOrderCalendar();
  }

  public void setOrderCalendar(int orderCalendar) {
    features.setOrderCalendar(orderCalendar);
  }

  public void setOrderCalendar(String tmp) {
    features.setOrderCalendar(Integer.parseInt(tmp));
  }

  public int getOrderDashboard() {
    return features.getOrderDashboard();
  }

  public void setOrderDashboard(int orderDashboard) {
    features.setOrderDashboard(orderDashboard);
  }

  public void setOrderDashboard(String tmp) {
    features.setOrderDashboard(Integer.parseInt(tmp));
  }

  public int getOrderWiki() {
    return features.getOrderWiki();
  }

  public void setOrderWiki(int orderWiki) {
    features.setOrderWiki(orderWiki);
  }

  public void setOrderWiki(String tmp) {
    features.setOrderWiki(Integer.parseInt(tmp));
  }

  public int getOrderDiscussion() {
    return features.getOrderDiscussion();
  }

  public void setOrderDiscussion(int orderDiscussion) {
    features.setOrderDiscussion(orderDiscussion);
  }

  public void setOrderDiscussion(String tmp) {
    features.setOrderDiscussion(Integer.parseInt(tmp));
  }

  public int getOrderDocuments() {
    return features.getOrderDocuments();
  }

  public void setOrderDocuments(int orderDocuments) {
    features.setOrderDocuments(orderDocuments);
  }

  public void setOrderDocuments(String tmp) {
    features.setOrderDocuments(Integer.parseInt(tmp));
  }

  public void setOrderBadges(int orderBadges) {
    features.setOrderBadges(orderBadges);
  }

  public void setOrderBadges(String tmp) {
    features.setOrderBadges(Integer.parseInt(tmp));
  }

  public void setOrderReviews(int orderReviews) {
    features.setOrderReviews(orderReviews);
  }

  public void setOrderReviews(String tmp) {
    features.setOrderReviews(Integer.parseInt(tmp));
  }

  public void setOrderClassifieds(int orderClassifieds) {
    features.setOrderClassifieds(orderClassifieds);
  }

  public void setOrderClassifieds(String tmp) {
    features.setOrderClassifieds(Integer.parseInt(tmp));
  }

  public void setOrderAds(int orderAds) {
    features.setOrderAds(orderAds);
  }

  public void setOrderAds(String tmp) {
    features.setOrderAds(Integer.parseInt(tmp));
  }

  public void setOrderProfile(int orderProfile) {
    features.setOrderProfile(orderProfile);
  }

  public void setOrderProfile(String tmp) {
    features.setOrderProfile(Integer.parseInt(tmp));
  }

  public void setOrderMessages(int orderMessages) {
    features.setOrderMessages(orderMessages);
  }

  public void setOrderMessages(String tmp) {
    features.setOrderMessages(Integer.parseInt(tmp));
  }

  public int getOrderLists() {
    return features.getOrderLists();
  }

  public void setOrderLists(int orderLists) {
    features.setOrderLists(orderLists);
  }

  public void setOrderLists(String tmp) {
    features.setOrderLists(Integer.parseInt(tmp));
  }

  public int getOrderPlan() {
    return features.getOrderPlan();
  }

  public void setOrderPlan(int orderPlan) {
    features.setOrderPlan(orderPlan);
  }

  public void setOrderPlan(String tmp) {
    features.setOrderPlan(Integer.parseInt(tmp));
  }

  public int getOrderTickets() {
    return features.getOrderTickets();
  }

  public int getOrderIssues() {
    return getOrderTickets();
  }

  public void setOrderTickets(int orderTickets) {
    features.setOrderTickets(orderTickets);
  }

  public void setOrderIssues(int orderTickets) {
    this.setOrderTickets(orderTickets);
  }

  public void setOrderTickets(String tmp) {
    features.setOrderTickets(Integer.parseInt(tmp));
  }

  public void setOrderIssues(String tmp) {
    this.setOrderTickets(tmp);
  }

  public int getOrderTeam() {
    return features.getOrderTeam();
  }

  public void setOrderTeam(int orderTeam) {
    features.setOrderTeam(orderTeam);
  }

  public void setOrderTeam(String tmp) {
    features.setOrderTeam(Integer.parseInt(tmp));
  }

  public int getOrderDetails() {
    return features.getOrderDetails();
  }

  public void setOrderDetails(int orderDetails) {
    features.setOrderDetails(orderDetails);
  }

  public void setOrderDetails(String tmp) {
    features.setOrderDetails(Integer.parseInt(tmp));
  }

  /**
   * Gets the Id attribute of the Project object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the GroupId attribute of the Project object
   *
   * @return The GroupId value
   */
  public int getGroupId() {
    return groupId;
  }

  /**
   * Gets the DepartmentId attribute of the Project object
   *
   * @return The DepartmentId value
   */
  public int getDepartmentId() {
    return departmentId;
  }

  /**
   * Gets the categoryId attribute of the Project object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }

  public boolean hasCategoryId() {
    return categoryId > -1;
  }

  public ProjectCategory getCategory() {
    if (categoryId > -1) {
      return ProjectUtils.loadProjectCategory(categoryId);
    } else {
      return null;
    }
  }

  /**
   * Gets the templateId attribute of the Project object
   *
   * @return The templateId value
   */
  public int getTemplateId() {
    return templateId;
  }

  /**
   * Gets the Title attribute of the Project object
   *
   * @return The Title value
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the ShortDescription attribute of the Project object
   *
   * @return The ShortDescription value
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Gets the RequestedBy attribute of the Project object
   *
   * @return The RequestedBy value
   */
  public String getRequestedBy() {
    return requestedBy;
  }

  /**
   * Gets the RequestedByDept attribute of the Project object
   *
   * @return The RequestedByDept value
   */
  public String getRequestedByDept() {
    return requestedByDept;
  }

  /**
   * Gets the RequestDate attribute of the Project object
   *
   * @return The RequestDate value
   */
  public Timestamp getRequestDate() {
    return requestDate;
  }

  /**
   * Gets the estimatedCloseDate attribute of the Project object
   *
   * @return The estimatedCloseDate value
   */
  public Timestamp getEstimatedCloseDate() {
    return estimatedCloseDate;
  }

  /**
   * Gets the budget attribute of the Project object
   *
   * @return The budget value
   */
  public double getBudget() {
    return budget;
  }

  /**
   * Gets the budgetCurrency attribute of the Project object
   *
   * @return The budgetCurrency value
   */
  public String getBudgetCurrency() {
    return budgetCurrency;
  }

  public int getLevel() {
    return level;
  }

  /**
   * Gets the Approved attribute of the Project object
   *
   * @return The Approved value
   */
  public boolean getApproved() {
    return approved;
  }

  /**
   * Gets the ApprovalDate attribute of the Project object
   *
   * @return The ApprovalDate value
   */
  public java.sql.Timestamp getApprovalDate() {
    return approvalDate;
  }

  /**
   * Gets the closed attribute of the Project object
   *
   * @return The closed value
   */
  public boolean getClosed() {
    return closed;
  }

  /**
   * Gets the CloseDate attribute of the Project object
   *
   * @return The CloseDate value
   */
  public java.sql.Timestamp getCloseDate() {
    return closeDate;
  }

  /**
   * Gets the entered attribute of the Project object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * Gets the owner attribute of the Project object
   *
   * @return The owner value
   */
  public int getOwner() {
    return owner;
  }

  /**
   * Gets the enteredBy attribute of the Project object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  /**
   * Gets the modified attribute of the Project object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }

  /**
   * Gets the modifiedBy attribute of the Project object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }

  /**
   * Gets the Team attribute of the Project object
   *
   * @return The Team value
   */
  public TeamMemberList getTeam() {
    return team;
  }

  /**
   * Gets the paddedId attribute of the Project object
   *
   * @return The paddedId value
   */
  public String getPaddedId() {
    String padded = (String.valueOf(id));
    while (padded.length() < 6) {
      padded = "0" + padded;
    }
    return padded;
  }

  /**
   * Gets the portal attribute of the Project object
   *
   * @return The portal value
   */
  public boolean getPortal() {
    return portal;
  }

  public PermissionList getPermissions() {
    return permissions;
  }

  public FileItemList getImages() {
    return images;
  }

  public FileItemList getSiteLogos() {
    return siteLogos;
  }

  public ServiceList getServices() {
    return services;
  }

  public int getTicketsClosed() {
    return ticketsClosed;
  }

  public void setFeatures(ProjectFeatures tmp) {
    features = tmp;
  }

  public ProjectFeatures getFeatures() {
    return features;
  }

  public int getTicketsOpen() {
    return ticketsOpen;
  }

  public int getTicketsOverdue() {
    return ticketsOverdue;
  }

  public int getLanguageId() {
    return languageId;
  }

  public void setLanguageId(int languageId) {
    this.languageId = languageId;
  }

  public void setLanguageId(String tmp) {
    this.languageId = Integer.parseInt(tmp);
  }

  public boolean isApiRestore() {
    return apiRestore;
  }

  public void setApiRestore(boolean apiRestore) {
    this.apiRestore = apiRestore;
  }

  public int getReadCount() {
    return readCount;
  }

  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }

  public int getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public int getRatingValue() {
    return ratingValue;
  }

  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public double getRatingAverage() {
    return ratingAverage;
  }

  public void setRatingAverage(double ratingAverage) {
    this.ratingAverage = ratingAverage;
  }

  public void setRatingAverage(String ratingAverage) {
    this.ratingAverage = Double.parseDouble(ratingAverage);
  }

  public String getEmail1() {
    return email1;
  }

  public void setEmail1(String email1) {
    this.email1 = email1;
  }

  public String getEmail2() {
    return email2;
  }

  public void setEmail2(String email2) {
    this.email2 = email2;
  }

  public String getEmail3() {
    return email3;
  }

  public void setEmail3(String email3) {
    this.email3 = email3;
  }

  public String getHomePhone() {
    return homePhone;
  }

  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  public String getHomePhoneExt() {
    return homePhoneExt;
  }

  public void setHomePhoneExt(String homePhoneExt) {
    this.homePhoneExt = homePhoneExt;
  }

  public String getHome2Phone() {
    return home2Phone;
  }

  public void setHome2Phone(String home2Phone) {
    this.home2Phone = home2Phone;
  }

  public String getHome2PhoneExt() {
    return home2PhoneExt;
  }

  public void setHome2PhoneExt(String home2PhoneExt) {
    this.home2PhoneExt = home2PhoneExt;
  }

  public String getHomeFax() {
    return homeFax;
  }

  public void setHomeFax(String homeFax) {
    this.homeFax = homeFax;
  }

  public String getBusinessPhone() {
    return businessPhone;
  }

  public void setBusinessPhone(String businessPhone) {
    this.businessPhone = businessPhone;
  }

  public String getBusinessPhoneExt() {
    return businessPhoneExt;
  }

  public void setBusinessPhoneExt(String businessPhoneExt) {
    this.businessPhoneExt = businessPhoneExt;
  }

  public String getBusiness2Phone() {
    return business2Phone;
  }

  public void setBusiness2Phone(String business2Phone) {
    this.business2Phone = business2Phone;
  }

  public String getBusiness2PhoneExt() {
    return business2PhoneExt;
  }

  public void setBusiness2PhoneExt(String business2PhoneExt) {
    this.business2PhoneExt = business2PhoneExt;
  }

  public String getBusinessFax() {
    return businessFax;
  }

  public void setBusinessFax(String businessFax) {
    this.businessFax = businessFax;
  }

  public String getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public String getPagerNumber() {
    return pagerNumber;
  }

  public void setPagerNumber(String pagerNumber) {
    this.pagerNumber = pagerNumber;
  }

  public String getCarPhone() {
    return carPhone;
  }

  public void setCarPhone(String carPhone) {
    this.carPhone = carPhone;
  }

  public String getRadioPhone() {
    return radioPhone;
  }

  public void setRadioPhone(String radioPhone) {
    this.radioPhone = radioPhone;
  }

  public String getWebPage() {
    return webPage;
  }

  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

  /**
   * @param twitterId the twitterId to set
   */
  public void setTwitterId(String twitterId) {
    this.twitterId = twitterId;
  }

  /**
   * @return the twitterId
   */
  public String getTwitterId() {
    return twitterId;
  }

  public String getAddressTo() {
    return addressTo;
  }

  public void setAddressTo(String tmp) {
    this.addressTo = tmp;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public String getAddressLine3() {
    return addressLine3;
  }

  public void setAddressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    if (StringUtils.hasText(state) && !"-1".equals(state)) {
      this.state = state;
    } else {
      state = null;
    }
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(String latitude) {
    String newLat = ProjectUtils.formatLatitude(latitude);
    if (newLat != null) {
      this.latitude = Double.parseDouble(newLat);
    }
  }

  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(String longitude) {
    String newLong = ProjectUtils.formatLongitude(longitude);
    if (newLong != null) {
      this.longitude = Double.parseDouble(newLong);
    }
  }

  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /**
   * @return the subCategory1Id
   */
  public int getSubCategory1Id() {
    return subCategory1Id;
  }

  public ProjectCategory getSubCategory1() {
    if (subCategory1Id > -1) {
      return ProjectUtils.loadProjectCategory(subCategory1Id);
    } else {
      return null;
    }
  }

  /**
   * @param subCategory1Id the subCategory1Id to set
   */
  public void setSubCategory1Id(int subCategory1Id) {
    this.subCategory1Id = subCategory1Id;
  }

  /**
   * @param subCategory1Id the subCategory1Id to set
   */
  public void setSubCategory1Id(String subCategory1Id) {
    this.subCategory1Id = Integer.parseInt(subCategory1Id);
  }

  /**
   * @return the subCategory2Id
   */
  public int getSubCategory2Id() {
    return subCategory2Id;
  }

  /**
   * @param subCategory2Id the subCategory2Id to set
   */
  public void setSubCategory2Id(int subCategory2Id) {
    this.subCategory2Id = subCategory2Id;
  }

  /**
   * @param subCategory2Id the subCategory2Id to set
   */
  public void setSubCategory2Id(String subCategory2Id) {
    this.subCategory2Id = Integer.parseInt(subCategory2Id);
  }

  /**
   * @return the subCategory3Id
   */
  public int getSubCategory3Id() {
    return subCategory3Id;
  }

  /**
   * @param subCategory3Id the subCategory3Id to set
   */
  public void setSubCategory3Id(int subCategory3Id) {
    this.subCategory3Id = subCategory3Id;
  }

  /**
   * @param subCategory3Id the subCategory3Id to set
   */
  public void setSubCategory3Id(String subCategory3Id) {
    this.subCategory3Id = Integer.parseInt(subCategory3Id);
  }

  public String getFacebookPage() {
    return facebookPage;
  }

  public void setFacebookPage(String facebookPage) {
    this.facebookPage = facebookPage;
  }

  public String getYoutubeChannelId() {
    return youtubeChannelId;
  }

  public void setYoutubeChannelId(String youtubeChannelId) {
    this.youtubeChannelId = youtubeChannelId;
  }

  public String getUstreamId() {
    return ustreamId;
  }

  public void setUstreamId(String ustreamId) {
    this.ustreamId = ustreamId;
  }

  public String getLivestreamId() {
    return livestreamId;
  }

  public void setLivestreamId(String livestreamId) {
    this.livestreamId = livestreamId;
  }

  public String getJustintvId() {
    return justintvId;
  }

  public void setJustintvId(String justintvId) {
    this.justintvId = justintvId;
  }

  public String getQikId() {
    return qikId;
  }

  public void setQikId(String qikId) {
    this.qikId = qikId;
  }

  public int getLogoId() {
    return logoId;
  }

  public void setLogoId(int logoId) {
    this.logoId = logoId;
  }

  public void setLogoId(String tmp) {
    logoId = Integer.parseInt(tmp);
  }

  public FileItem getLogo() {
    if (logoId > -1 && images != null) {
      return images.getById(logoId);
    } else {
      return null;
    }
  }

  public boolean hasLogo() {
    if (logoId > -1 && images != null) {
      return images.getById(logoId) != null;
    } else {
      return false;
    }
  }

  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public boolean getProfile() {
    return profile;
  }

  public void setProfile(boolean profile) {
    this.profile = profile;
  }

  public void setProfile(String tmp) {
    this.profile = DatabaseUtils.parseBoolean(tmp);
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public boolean getStyleEnabled() {
    return styleEnabled;
  }

  public void setStyleEnabled(boolean styleEnabled) {
    this.styleEnabled = styleEnabled;
  }

  public void setStyleEnabled(String tmp) {
    this.styleEnabled = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the htmldescription to set, which is filtered
   */
  public void setDescription(String description) {
    this.description = HTMLUtils.makePublicHtml(description);
  }

  public String getConcursiveCRMUrl() {
    return concursiveCRMUrl;
  }

  public void setConcursiveCRMUrl(String concursiveCRMUrl) {
    this.concursiveCRMUrl = concursiveCRMUrl;
  }

  public String getConcursiveCRMDomain() {
    return concursiveCRMDomain;
  }

  public void setConcursiveCRMDomain(String concursiveCRMDomain) {
    this.concursiveCRMDomain = concursiveCRMDomain;
  }

  public String getConcursiveCRMCode() {
    return concursiveCRMCode;
  }

  public void setConcursiveCRMCode(String concursiveCRMCode) {
    this.concursiveCRMCode = concursiveCRMCode;
  }

  public String getConcursiveCRMClient() {
    return concursiveCRMClient;
  }

  public void setConcursiveCRMClient(String concursiveCRMClient) {
    this.concursiveCRMClient = concursiveCRMClient;
  }

  public boolean getSystemDefault() {
    return systemDefault;
  }

  public void setSystemDefault(boolean systemDefault) {
    this.systemDefault = systemDefault;
  }

  public void setSystemDefault(String tmp) {
    this.systemDefault = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int buildTeamMemberList(Connection db) throws SQLException {
    team.setProjectId(this.getId());
    team.buildList(db);
    return team.size();
  }

  public boolean hasPermissionList() {
    return (permissions != null && permissions.size() > 0);
  }

  public boolean hasImages() {
    return (images != null && images.size() > 0);
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int buildPermissionList(Connection db) throws SQLException {
    permissions.setProjectId(this.getId());
    permissions.buildList(db);
    return permissions.size();
  }

  public int buildImages(Connection db) throws SQLException {
    images.clear();
    images.setLinkModuleId(Constants.PROJECT_IMAGE_FILES);
    images.setLinkItemId(this.getId());
    images.buildList(db);
    return images.size();
  }

  public int buildSiteLogo(Connection db) throws SQLException {
    siteLogos.clear();
    siteLogos.setLinkModuleId(Constants.SITE_LOGO_FILES);
    siteLogos.setLinkItemId(this.getId());
    siteLogos.buildList(db);
    return siteLogos.size();
  }

  public int buildServices(Connection db) throws SQLException {
    services.clear();
    services.setProjectId(this.getId());
    services.buildList(db);
    return services.size();
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    if (!isApiRestore()) {
      // Make data consistent
      ProjectUtils.formatAddress(this);
      ProjectUtils.formatPhoneNumbers(this);
      ProjectUtils.formatWebAddress(this);
    }
    Exception errorMessage = null;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO projects " +
              "(" + (id > -1 ? "project_id, " : "") + "instance_id, group_id, department_id, category_id, owner, enteredby, modifiedby, template_id, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      sql.append(
          "title, projecttextid, shortdescription, requestedby, requesteddept, requestdate, " +
              (features.getUpdateAllowGuests() ? "allow_guests," : "") +
              (features.getUpdateAllowParticipants() ? "allows_user_observers," : "") +
              (features.getUpdateMembershipRequired() ? "membership_required," : "") +
              "calendar_enabled, dashboard_enabled, news_enabled, wiki_enabled, details_enabled, " +
              "team_enabled, plan_enabled, lists_enabled, discussion_enabled, " +
              "tickets_enabled, documents_enabled, " +
              "badges_enabled, reviews_enabled, classifieds_enabled, ads_enabled, profile_enabled, messages_enabled, webcasts_enabled, " +
              (level > -1 ? "level, " : "") +
              "approvaldate, closedate, est_closedate, budget, budget_currency, description, template, language_id," +
              "address_to, addrline1, addrline2, addrline3, city, state, country, postalcode, " +
              "latitude, longitude, " +
              "email1, email2, email3, " +
              "home_phone, home_phone_ext, home2_phone, home2_phone_ext, home_fax, " +
              "business_phone, business_phone_ext, business2_phone, business2_phone_ext, business_fax, " +
              "mobile_phone, pager_number, car_phone, radio_phone, web_page, twitter_id, " +
              "subcategory1_id, subcategory2_id, subcategory3_id, keywords, profile, source, " +
              "facebook_page, youtube_channel_id, ustream_id, livestream_id, justintv_id, qik_id) " +
              "VALUES (?, ?, ?, ?, ?, " +
              (features.getUpdateAllowGuests() ? "?," : "") +
              (features.getUpdateAllowParticipants() ? "?," : "") +
              (features.getUpdateMembershipRequired() ? "?," : "") +
              "?, ?, ");
      if (id > -1) {
        sql.append("?, ");
      }
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      if (level > -1) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
          "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
          "?, ?, ?, ?, ?, ?) ");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, instanceId);
      DatabaseUtils.setInt(pst, ++i, groupId);
      DatabaseUtils.setInt(pst, ++i, departmentId);
      DatabaseUtils.setInt(pst, ++i, categoryId);
      DatabaseUtils.setInt(pst, ++i, owner);
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      DatabaseUtils.setInt(pst, ++i, templateId);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.setString(++i, title);
      pst.setString(++i, uniqueId);
      pst.setString(++i, shortDescription);
      pst.setString(++i, requestedBy);
      pst.setString(++i, requestedByDept);
      if (requestDate == null) {
        requestDate = new java.sql.Timestamp(System.currentTimeMillis());
      }
      pst.setTimestamp(++i, requestDate);
      if (features.getUpdateAllowGuests()) {
        pst.setBoolean(++i, features.getAllowGuests());
      }
      if (features.getUpdateAllowParticipants()) {
        pst.setBoolean(++i, features.getAllowParticipants());
      }
      if (features.getUpdateMembershipRequired()) {
        pst.setBoolean(++i, features.getMembershipRequired());
      }
      pst.setBoolean(++i, features.getShowCalendar());
      pst.setBoolean(++i, features.getShowDashboard());
      pst.setBoolean(++i, features.getShowNews());
      pst.setBoolean(++i, features.getShowWiki());
      pst.setBoolean(++i, features.getShowDetails());
      pst.setBoolean(++i, features.getShowTeam());
      pst.setBoolean(++i, features.getShowPlan());
      pst.setBoolean(++i, features.getShowLists());
      pst.setBoolean(++i, features.getShowDiscussion());
      pst.setBoolean(++i, features.getShowTickets());
      pst.setBoolean(++i, features.getShowDocuments());
      pst.setBoolean(++i, features.getShowBadges());
      pst.setBoolean(++i, features.getShowReviews());
      pst.setBoolean(++i, features.getShowClassifieds());
      pst.setBoolean(++i, features.getShowAds());
      pst.setBoolean(++i, features.getShowProfile());
      pst.setBoolean(++i, features.getShowMessages());
      pst.setBoolean(++i, features.getShowWebcasts());
      if (level > -1) {
        pst.setInt(++i, level);
      }
      if (approved && approvalDate == null) {
        approvalDate = new Timestamp(System.currentTimeMillis());
        approvalDate.setNanos(0);
      }
      if (approvalDate == null) {
        pst.setNull(++i, java.sql.Types.DATE);
      } else {
        approvalDate.setNanos(0);
        pst.setTimestamp(++i, approvalDate);
      }
      if (closed) {
        closeDate = new Timestamp(System.currentTimeMillis());
        closeDate.setNanos(0);
      }
      DatabaseUtils.setTimestamp(pst, ++i, closeDate);
      DatabaseUtils.setTimestamp(pst, ++i, estimatedCloseDate);
      DatabaseUtils.setDouble(pst, ++i, budget);
      pst.setString(++i, budgetCurrency);
      pst.setString(++i, description);
      pst.setBoolean(++i, template);
      DatabaseUtils.setInt(pst, ++i, languageId);
      pst.setString(++i, addressTo);
      pst.setString(++i, addressLine1);
      pst.setString(++i, addressLine2);
      pst.setString(++i, addressLine3);
      pst.setString(++i, city);
      pst.setString(++i, state);
      pst.setString(++i, country);
      pst.setString(++i, postalCode);
      DatabaseUtils.setDouble(pst, ++i, latitude);
      DatabaseUtils.setDouble(pst, ++i, longitude);
      pst.setString(++i, email1);
      pst.setString(++i, email2);
      pst.setString(++i, email3);
      pst.setString(++i, homePhone);
      pst.setString(++i, homePhoneExt);
      pst.setString(++i, home2Phone);
      pst.setString(++i, home2PhoneExt);
      pst.setString(++i, homeFax);
      pst.setString(++i, businessPhone);
      pst.setString(++i, businessPhoneExt);
      pst.setString(++i, business2Phone);
      pst.setString(++i, business2PhoneExt);
      pst.setString(++i, businessFax);
      pst.setString(++i, mobilePhone);
      pst.setString(++i, pagerNumber);
      pst.setString(++i, carPhone);
      pst.setString(++i, radioPhone);
      pst.setString(++i, webPage);
      pst.setString(++i, twitterId);
      DatabaseUtils.setInt(pst, ++i, subCategory1Id);
      DatabaseUtils.setInt(pst, ++i, subCategory2Id);
      DatabaseUtils.setInt(pst, ++i, subCategory3Id);
      pst.setString(++i, keywords);
      pst.setBoolean(++i, profile);
      pst.setString(++i, source);
      pst.setString(++i, facebookPage);
      pst.setString(++i, youtubeChannelId);
      pst.setString(++i, ustreamId);
      pst.setString(++i, livestreamId);
      pst.setString(++i, justintvId);
      pst.setString(++i, qikId);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "projects_project_id_seq", id);

      // insert corresponding webcast info
      Webcast webcast = new Webcast();
      webcast.setProjectId(id);
      webcast.setEnteredBy(enteredBy);
      webcast.insert(db);

      // Turn off with a restore operation
      if (!isApiRestore()) {
        ProjectTicketCount.insertProjectTicketCount(db, id);
        if (!clone) {
          // Insert the default permissions
          PermissionList.insertDefaultPermissions(db, id);
        }
        if (portal) {
          updatePortal(db);
        }
        // set a unique text id for the project
        if (!StringUtils.hasText(uniqueId)) {
          uniqueId = ProjectUtils.updateUniqueId(db, id, title);
        }
      }
      if (commit) {
        db.commit();
      }
      features.setModifiedBy(enteredBy);
      features.setId(id);
    } catch (Exception e) {
      e.printStackTrace(System.out);
      errorMessage = e;
      if (commit) {
        db.rollback();
      }
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (errorMessage != null) {
      throw new SQLException(errorMessage.getMessage());
    }
    return true;
  }

  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param basePath Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db, String basePath) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int recordCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      if (profile) {
        UserUtils.detachProfile(db, id);
      }
      MeetingList.delete(db, id);
      WikiList.delete(db, id);

      DailyTimesheetList.delete(db, id);

      ForumList issueCategories = new ForumList();
      issueCategories.setProjectId(id);
      issueCategories.buildList(db);
      issueCategories.delete(db, basePath);

      TicketList tickets = new TicketList();
      tickets.setProjectId(id);
      tickets.buildList(db);
      tickets.delete(db, basePath);

      ProjectTicketCount.deleteProjectTicketCount(db, id);

      TicketCategoryList.delete(db, id);
      ProjectItemList.delete(db, id, ProjectItemList.TICKET_DEFECT);
      ProjectItemList.delete(db, id, ProjectItemList.TICKET_STATE);
      ProjectItemList.delete(db, id, ProjectItemList.TICKET_CAUSE);
      ProjectItemList.delete(db, id, ProjectItemList.TICKET_RESOLUTION);
      ProjectItemList.delete(db, id, ProjectItemList.TICKET_ESCALATION);

      // Delete the ticket that is linked to this project if any
      TicketList linkedTickets = new TicketList();
      linkedTickets.setLinkProjectId(id);
      linkedTickets.buildList(db);
      linkedTickets.delete(db, basePath);

      TaskCategoryList taskCategories = new TaskCategoryList();
      taskCategories.setProjectId(id);
      taskCategories.buildList(db);
      taskCategories.delete(db);
      TaskUtils.removeLinkedItemId(db, Constants.TASK_CATEGORY_PROJECTS, id);

      TeamMemberList team = new TeamMemberList();
      team.setProjectId(id);
      team.buildList(db);
      team.delete(db);

      RequirementList requirements = new RequirementList();
      requirements.setProjectId(id);
      requirements.buildList(db);
      requirements.delete(db, basePath);

      FileFolderList folders = new FileFolderList();
      folders.setLinkModuleId(Constants.PROJECTS_FILES);
      folders.setLinkItemId(id);
      folders.buildList(db);
      folders.delete(db, basePath);

      FileItemList fileItemList = new FileItemList();
      fileItemList.setLinkModuleId(Constants.PROJECTS_FILES);
      fileItemList.setLinkItemId(id);
      fileItemList.buildList(db);
      fileItemList.delete(db, basePath);

      FileItemList wikiImages = new FileItemList();
      wikiImages.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
      wikiImages.setLinkItemId(id);
      wikiImages.buildList(db);
      wikiImages.delete(db, basePath);

      BlogPostList.delete(db, id, basePath);
      // News article images?
      BlogPostCategoryList.delete(db, id);
      PermissionList.delete(db, id);

      ReportQueueList reportQueueList = new ReportQueueList();
      reportQueueList.setProjectId(id);
      reportQueueList.setEnabled(false);
      reportQueueList.buildList(db);
      reportQueueList.delete(db, basePath);

      DashboardList dashboardList = new DashboardList();
      dashboardList.setProjectId(id);
      dashboardList.buildList(db);
      dashboardList.delete(db);

      Viewing.delete(db, id, TABLE, PRIMARY_KEY);

      ProjectBadgeList projectBadgeList = new ProjectBadgeList();
      projectBadgeList.setProjectId(id);
      projectBadgeList.buildList(db);
      projectBadgeList.delete(db);

      AdList adList = new AdList();
      adList.setProjectId(id);
      adList.buildList(db);
      adList.delete(db);

      ClassifiedList classifiedList = new ClassifiedList();
      classifiedList.setProjectId(id);
      classifiedList.buildList(db);
      classifiedList.delete(db, basePath);

      ProjectRatingList projectRatingList = new ProjectRatingList();
      projectRatingList.setProjectId(id);
      projectRatingList.buildList(db);
      projectRatingList.delete(db);

      ServiceList.delete(db, id);

      PrivateMessageList privateMessageList1 = new PrivateMessageList();
      privateMessageList1.setProjectId(id);
      privateMessageList1.buildList(db);
      privateMessageList1.delete(db);

      PrivateMessageList privateMessageList2 = new PrivateMessageList();
      privateMessageList2.setLinkProjectId(id);
      privateMessageList2.buildList(db);
      privateMessageList2.delete(db);

      UserContributionLogList userContributionLogList = new UserContributionLogList();
      userContributionLogList.setProjectId(id);
      userContributionLogList.buildList(db);
      userContributionLogList.delete(db);

      ProjectFeaturedListingList projectFeaturedListingList = new ProjectFeaturedListingList();
      projectFeaturedListingList.setProjectId(id);
      projectFeaturedListingList.buildList(db);
      projectFeaturedListingList.delete(db);

      ProjectHistoryList.delete(db, id);

      Webcast webcast = new Webcast(db, ProjectUtils.retrieveWebcastIdFromProjectId(db, id));
      webcast.delete(db);

      // Delete the actual project
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM projects " +
              "WHERE project_id = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();

      // Now that the project reference is deleted, delete the images
      FileItemList logoImages = new FileItemList();
      logoImages.setLinkModuleId(Constants.PROJECT_IMAGE_FILES);
      logoImages.setLinkItemId(id);
      logoImages.buildList(db);
      logoImages.delete(db, basePath);

      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_NAME_CACHE, id);
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, uniqueId);
      if (profile && owner > -1) {
        CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, owner);
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      e.printStackTrace(System.out);
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (recordCount == 0) {
      errors.put("actionError", "Project could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }

    // Compare with the previous project
    Project previousProject = ProjectUtils.loadProject(id);

    // Make data consistent
    ProjectUtils.formatAddress(this);
    ProjectUtils.formatPhoneNumbers(this);
    ProjectUtils.formatWebAddress(this);

    // Check the uniqueId
    if (!StringUtils.hasText(uniqueId)) {
      uniqueId = previousProject.getUniqueId();
    }
    if (!StringUtils.hasText(uniqueId) ||
        !uniqueId.equals(previousProject.getUniqueId())) {
      // see if the specified uniqueId is valid, otherwise
      // generate from the title
      if (StringUtils.hasText(uniqueId)) {
        // Example: godfathers-pizza-2
        uniqueId = ProjectUtils.updateUniqueId(db, id, uniqueId);
      } else {
        // Example: Godfather's Pizza
        uniqueId = ProjectUtils.updateUniqueId(db, id, title);
      }
    }

    // See if the state is changing...
    Timestamp previousApprovalDate = previousProject.getApprovalDate();
    boolean previouslyApproved = (previousApprovalDate != null);
    Timestamp previousCloseDate = previousProject.getCloseDate();
    boolean previouslyClosed = (previousCloseDate != null);

    // Update the project
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
            "UPDATE projects " +
                    "SET department_id = ?, category_id = ?, title = ?, shortdescription = ?, requestedby = ?, " +
                    "requesteddept = ?, requestdate = ?, " +
                    "approvaldate = ?, closedate = ?, owner = ?, est_closedate = ?, budget = ?, " +
                    "budget_currency = ?, template = ?, " +
                    (features.getUpdateAllowGuests() ? "allow_guests = ?, " : "") +
                    (features.getUpdateAllowParticipants() ? "allows_user_observers = ?, " : "") +
                    (features.getUpdateMembershipRequired() ? "membership_required = ?, " : "") +
                    (level > -1 ? "level = ?, " : "") +
                    "description = ?, " +
                    "address_to = ?, addrline1 = ?, addrline2 = ?, addrline3 = ?, city = ?, state = ?, country = ?, postalcode = ?, " +
                    "latitude = ?, longitude = ?, " +
                    "email1 = ?, email2 = ?, email3 = ?, " +
                    "home_phone = ?, home_phone_ext = ?, home2_phone = ?, home2_phone_ext = ?, home_fax = ?, " +
                    "business_phone = ?, business_phone_ext = ?, business2_phone = ?, business2_phone_ext = ?, business_fax = ?, " +
                    "mobile_phone = ?, pager_number = ?, car_phone = ?, radio_phone = ?, web_page = ?, twitter_id = ?, " +
                    "subcategory1_id = ?, subcategory2_id = ?, subcategory3_id = ?, keywords = ?, " +
                    "facebook_page = ?, youtube_channel_id = ?, ustream_id = ?, livestream_id = ?, justintv_id = ?, qik_id = ?, " +
                    "modifiedby = ?, modified = CURRENT_TIMESTAMP " +
                    "WHERE project_id = ? " +
                    "AND modified = ? ");
    int i = 0;
    DatabaseUtils.setInt(pst, ++i, departmentId);
    DatabaseUtils.setInt(pst, ++i, categoryId);
    pst.setString(++i, title);
    pst.setString(++i, shortDescription);
    pst.setString(++i, requestedBy);
    pst.setString(++i, requestedByDept);
    DatabaseUtils.setTimestamp(pst, ++i, requestDate);
    if (previouslyApproved && approved) {
      pst.setTimestamp(++i, previousApprovalDate);
    } else if (!previouslyApproved && approved) {
      approvalDate = new Timestamp(System.currentTimeMillis());
      approvalDate.setNanos(0);
      pst.setTimestamp(++i, approvalDate);
    } else if (!approved) {
      pst.setNull(++i, java.sql.Types.DATE);
    }
    if (previouslyClosed && closed) {
      pst.setTimestamp(++i, previousCloseDate);
    } else if (!previouslyClosed && closed) {
      closeDate = new Timestamp(System.currentTimeMillis());
      closeDate.setNanos(0);
      pst.setTimestamp(++i, closeDate);
    } else if (!closed) {
      pst.setNull(++i, java.sql.Types.DATE);
    }
    DatabaseUtils.setInt(pst, ++i, owner);
    DatabaseUtils.setTimestamp(pst, ++i, estimatedCloseDate);
    DatabaseUtils.setDouble(pst, ++i, budget);
    pst.setString(++i, budgetCurrency);
    pst.setBoolean(++i, template);
    if (features.getUpdateAllowGuests()) {
      pst.setBoolean(++i, features.getAllowGuests());
    }
    if (features.getUpdateAllowParticipants()) {
      pst.setBoolean(++i, features.getAllowParticipants());
    }
    if (features.getUpdateMembershipRequired()) {
      pst.setBoolean(++i, features.getMembershipRequired());
    }
    if (level > -1) {
      pst.setInt(++i, level);
    }
    pst.setString(++i, description);
    pst.setString(++i, addressTo);
    pst.setString(++i, addressLine1);
    pst.setString(++i, addressLine2);
    pst.setString(++i, addressLine3);
    pst.setString(++i, city);
    pst.setString(++i, state);
    pst.setString(++i, country);
    pst.setString(++i, postalCode);
    DatabaseUtils.setDouble(pst, ++i, latitude);
    DatabaseUtils.setDouble(pst, ++i, longitude);
    pst.setString(++i, email1);
    pst.setString(++i, email2);
    pst.setString(++i, email3);
    pst.setString(++i, homePhone);
    pst.setString(++i, homePhoneExt);
    pst.setString(++i, home2Phone);
    pst.setString(++i, home2PhoneExt);
    pst.setString(++i, homeFax);
    pst.setString(++i, businessPhone);
    pst.setString(++i, businessPhoneExt);
    pst.setString(++i, business2Phone);
    pst.setString(++i, business2PhoneExt);
    pst.setString(++i, businessFax);
    pst.setString(++i, mobilePhone);
    pst.setString(++i, pagerNumber);
    pst.setString(++i, carPhone);
    pst.setString(++i, radioPhone);
    pst.setString(++i, webPage);
    pst.setString(++i, twitterId);
    DatabaseUtils.setInt(pst, ++i, subCategory1Id);
    DatabaseUtils.setInt(pst, ++i, subCategory2Id);
    DatabaseUtils.setInt(pst, ++i, subCategory3Id);
    pst.setString(++i, keywords);
    pst.setString(++i, facebookPage);
    pst.setString(++i, youtubeChannelId);
    pst.setString(++i, ustreamId);
    pst.setString(++i, livestreamId);
    pst.setString(++i, justintvId);
    pst.setString(++i, qikId);
    pst.setInt(++i, this.getModifiedBy());
    pst.setInt(++i, this.getId());
    pst.setTimestamp(++i, modified);
    resultCount = pst.executeUpdate();
    pst.close();

    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_NAME_CACHE, id);
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
    if (profile && owner > -1) {
      CacheUtils.invalidateValue(Constants.SYSTEM_USER_CACHE, owner);
    }
    return resultCount;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int updateFeatures(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      features.setId(id);
      features.setModifiedBy(modifiedBy);
      features.setModified(modified);
      resultCount = features.update(db);
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
    return resultCount;
  }

  public void updatePortal(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "UPDATE projects " +
            "SET portal = ?, portal_key = ?, portal_default = ?, portal_page_type = ? " +
            "WHERE project_id = ?");
    pst.setBoolean(1, portal);
    pst.setString(2, portalKey);
    pst.setBoolean(3, portalDefault);
    DatabaseUtils.setInt(pst, 4, portalPageType);
    pst.setInt(5, id);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
  }

  public synchronized void updateSystemDefault(Connection db) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      if (this.getId() == -1) {
        throw new SQLException("ID was not specified");
      }

      PreparedStatement pst = null;

      // Find the current default and disable it
      int currentDefault = ProjectList.querySystemDefault(db);
      if (currentDefault > -1 && currentDefault != id) {
        pst = db.prepareStatement(
            "UPDATE projects " +
                "SET system_default = ? " +
                "WHERE project_id = ?");
        pst.setBoolean(1, false);
        pst.setInt(2, currentDefault);
        pst.execute();
        pst.close();
      }

      // Turn on the new default
      pst = db.prepareStatement(
          "UPDATE projects " +
              "SET system_default = ? " +
              "WHERE project_id = ?");
      pst.setBoolean(1, true);
      pst.setInt(2, id);
      pst.execute();
      pst.close();

      // Commit the changes
      if (autoCommit) {
        db.commit();
      }

      // Update the caches
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
      if (currentDefault > -1 && currentDefault != id) {
        CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, currentDefault);
      }
    } catch (Exception e) {
      if (autoCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
  }

  /**
   * Gets the valid attribute of the Project object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (!StringUtils.hasText(title)) {
      errors.put("titleError", "Title is required");
    }
    if (!StringUtils.hasText(shortDescription)) {
      errors.put("shortDescriptionError", "Description is required");
    }
    return !hasErrors();
  }

  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("project_id");
    groupId = DatabaseUtils.getInt(rs, "group_id");
    departmentId = DatabaseUtils.getInt(rs, "department_id");
    //templateId
    title = rs.getString("title");
    requestedBy = rs.getString("requestedby");
    requestedByDept = rs.getString("requesteddept");
    requestDate = rs.getTimestamp("requestdate");
    approvalDate = rs.getTimestamp("approvaldate");
    approved = (approvalDate != null);
    closeDate = rs.getTimestamp("closedate");
    closed = (closeDate != null);
    owner = DatabaseUtils.getInt(rs, "owner");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    categoryId = DatabaseUtils.getInt(rs, "category_id");
    portal = rs.getBoolean("portal");
    features.setAllowGuests(rs.getBoolean("allow_guests"));
    features.setShowNews(rs.getBoolean("news_enabled"));
    features.setShowDetails(rs.getBoolean("details_enabled"));
    features.setShowTeam(rs.getBoolean("team_enabled"));
    features.setShowPlan(rs.getBoolean("plan_enabled"));
    features.setShowLists(rs.getBoolean("lists_enabled"));
    features.setShowDiscussion(rs.getBoolean("discussion_enabled"));
    features.setShowTickets(rs.getBoolean("tickets_enabled"));
    features.setShowDocuments(rs.getBoolean("documents_enabled"));
    features.setLabelNews(rs.getString("news_label"));
    features.setLabelDetails(rs.getString("details_label"));
    features.setLabelTeam(rs.getString("team_label"));
    features.setLabelPlan(rs.getString("plan_label"));
    features.setLabelLists(rs.getString("lists_label"));
    features.setLabelDiscussion(rs.getString("discussion_label"));
    features.setLabelTickets(rs.getString("tickets_label"));
    features.setLabelDocuments(rs.getString("documents_label"));
    estimatedCloseDate = rs.getTimestamp("est_closedate");
    budget = DatabaseUtils.getDouble(rs, "budget");
    budgetCurrency = rs.getString("budget_currency");
    portalHeader = rs.getString("portal_header");
    portalFormat = rs.getString("portal_format");
    portalKey = rs.getString("portal_key");
    portalBuildNewsBody = rs.getBoolean("portal_build_news_body");
    portalNewsMenu = rs.getBoolean("portal_news_menu");
    description = rs.getString("description");
    features.setAllowParticipants(rs.getBoolean("allows_user_observers"));
    level = rs.getInt("level");
    portalPageType = DatabaseUtils.getInt(rs, "portal_page_type");
    features.setShowCalendar(rs.getBoolean("calendar_enabled"));
    features.setLabelCalendar(rs.getString("calendar_label"));
    template = rs.getBoolean("template");
    features.setShowWiki(rs.getBoolean("wiki_enabled"));
    features.setLabelWiki(rs.getString("wiki_label"));
    features.setOrderDashboard(rs.getInt("dashboard_order"));
    features.setOrderNews(rs.getInt("news_order"));
    features.setOrderCalendar(rs.getInt("calendar_order"));
    features.setOrderWiki(rs.getInt("wiki_order"));
    features.setOrderDiscussion(rs.getInt("discussion_order"));
    features.setOrderDocuments(rs.getInt("documents_order"));
    features.setOrderLists(rs.getInt("lists_order"));
    features.setOrderPlan(rs.getInt("plan_order"));
    features.setOrderTickets(rs.getInt("tickets_order"));
    features.setOrderTeam(rs.getInt("team_order"));
    features.setOrderDetails(rs.getInt("details_order"));
    features.setShowDashboard(rs.getBoolean("dashboard_enabled"));
    features.setLabelDashboard(rs.getString("dashboard_label"));
    languageId = DatabaseUtils.getInt(rs, "language_id");
    uniqueId = rs.getString("projecttextid");
    readCount = rs.getInt("read_count");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAverage = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    logoId = DatabaseUtils.getInt(rs, "logo_id");
    features.setDescriptionDashboard(rs.getString("dashboard_description"));
    features.setDescriptionNews(rs.getString("news_description"));
    features.setDescriptionCalendar(rs.getString("calendar_description"));
    features.setDescriptionWiki(rs.getString("wiki_description"));
    features.setDescriptionDiscussion(rs.getString("discussion_description"));
    features.setDescriptionDocuments(rs.getString("documents_description"));
    features.setDescriptionLists(rs.getString("lists_description"));
    features.setDescriptionPlan(rs.getString("plan_description"));
    features.setDescriptionTickets(rs.getString("tickets_description"));
    features.setDescriptionTeam(rs.getString("team_description"));
    concursiveCRMUrl = rs.getString("concursive_crm_url");
    concursiveCRMDomain = rs.getString("concursive_crm_domain");
    concursiveCRMCode = rs.getString("concursive_crm_code");
    concursiveCRMClient = rs.getString("concursive_crm_client");
    email1 = rs.getString("email1");
    email2 = rs.getString("email2");
    email3 = rs.getString("email3");
    homePhone = rs.getString("home_phone");
    homePhoneExt = rs.getString("home_phone_ext");
    home2Phone = rs.getString("home2_phone");
    home2PhoneExt = rs.getString("home2_phone_ext");
    homeFax = rs.getString("home_fax");
    businessPhone = rs.getString("business_phone");
    businessPhoneExt = rs.getString("business_phone_ext");
    business2Phone = rs.getString("business2_phone");
    business2PhoneExt = rs.getString("business2_phone_ext");
    businessFax = rs.getString("business_fax");
    mobilePhone = rs.getString("mobile_phone");
    pagerNumber = rs.getString("pager_number");
    carPhone = rs.getString("car_phone");
    radioPhone = rs.getString("radio_phone");
    webPage = rs.getString("web_page");
    addressTo = rs.getString("address_to");
    addressLine1 = rs.getString("addrline1");
    addressLine2 = rs.getString("addrline2");
    addressLine3 = rs.getString("addrline3");
    city = rs.getString("city");
    state = rs.getString("state");
    country = rs.getString("country");
    postalCode = rs.getString("postalcode");
    latitude = DatabaseUtils.getDouble(rs, "latitude", 0.0);
    longitude = DatabaseUtils.getDouble(rs, "longitude", 0.0);
    features.setShowBadges(rs.getBoolean("badges_enabled"));
    features.setLabelBadges(rs.getString("badges_label"));
    features.setOrderBadges(rs.getInt("badges_order"));
    features.setDescriptionBadges(rs.getString("badges_description"));
    features.setShowReviews(rs.getBoolean("reviews_enabled"));
    features.setLabelReviews(rs.getString("reviews_label"));
    features.setOrderReviews(rs.getInt("reviews_order"));
    features.setDescriptionReviews(rs.getString("reviews_description"));
    features.setShowClassifieds(rs.getBoolean("classifieds_enabled"));
    features.setLabelClassifieds(rs.getString("classifieds_label"));
    features.setOrderClassifieds(rs.getInt("classifieds_order"));
    features.setDescriptionClassifieds(rs.getString("classifieds_description"));
    features.setShowAds(rs.getBoolean("ads_enabled"));
    features.setLabelAds(rs.getString("ads_label"));
    features.setOrderAds(rs.getInt("ads_order"));
    features.setDescriptionAds(rs.getString("ads_description"));
    features.setMembershipRequired(rs.getBoolean("membership_required"));
    subCategory1Id = DatabaseUtils.getInt(rs, "subcategory1_id");
    subCategory2Id = DatabaseUtils.getInt(rs, "subcategory2_id");
    subCategory3Id = DatabaseUtils.getInt(rs, "subcategory3_id");
    keywords = rs.getString("keywords");
    profile = rs.getBoolean("profile");
    features.setShowProfile(rs.getBoolean("profile_enabled"));
    features.setLabelProfile(rs.getString("profile_label"));
    features.setOrderProfile(rs.getInt("profile_order"));
    features.setDescriptionProfile(rs.getString("profile_description"));
    source = rs.getString("source");
    style = rs.getString("style");
    styleEnabled = rs.getBoolean("style_enabled");
    features.setShowMessages(rs.getBoolean("messages_enabled"));
    features.setLabelMessages(rs.getString("messages_label"));
    features.setOrderMessages(rs.getInt("messages_order"));
    features.setDescriptionMessages(rs.getString("messages_description"));
    systemDefault = rs.getBoolean("system_default");
    shortDescription = rs.getString("shortdescription");
    instanceId = DatabaseUtils.getInt(rs, "instance_id", -1);
    twitterId = rs.getString("twitter_id");
    facebookPage = rs.getString("facebook_page");
    youtubeChannelId = rs.getString("youtube_channel_id");
    ustreamId = rs.getString("ustream_id");
    livestreamId = rs.getString("livestream_id");
    justintvId = rs.getString("justintv_id");
    qikId = rs.getString("qik_id");
    features.setShowWebcasts(rs.getBoolean("webcasts_enabled"));
    features.setLabelWebcasts(rs.getString("webcasts_label"));
    features.setOrderWebcasts(rs.getInt("webcasts_order"));
    features.setDescriptionWebcasts(rs.getString("webcasts_description"));
    //Set the related objects
    team.setProjectId(this.getId());
  }

  /**
   * Gets the accessUserLevel attribute of the Project object
   *
   * @param permission Description of the Parameter
   * @return The accessUserLevel value
   */
  public int getAccessUserLevel(String permission) {
    return permissions.getAccessLevel(permission);
  }

  /**
   * Gets the label attribute of the Project object
   *
   * @param name Description of the Parameter
   * @return The label value
   */
  public String getLabel(String name) {
    String value = ObjectUtils.getParam(features, "Label" + name);
    if (value != null && !"".equals(value)) {
      return value;
    }
    return name;
  }

  /**
   * The following fields depend on a timezone preference
   *
   * @return The timeZoneParams value
   */
  public static ArrayList getTimeZoneParams() {
    ArrayList thisList = new ArrayList();
    thisList.add("requestDate");
    thisList.add("estimatedCloseDate");
    return thisList;
  }

  /**
   * Gets the numberParams attribute of the Project class
   *
   * @return The numberParams value
   */
  public static ArrayList getNumberParams() {
    ArrayList thisList = new ArrayList();
    thisList.add("budget");
    return thisList;
  }

  public void buildTicketCounts(Connection db) throws SQLException {
    ticketsClosed = TicketList.countClosedTickets(db, id);
    ticketsOpen = TicketList.countOpenTickets(db, id);
    ticketsOverdue = TicketList.countOverdueTickets(db, id);
  }

  public int getTicketCount() {
    if (ticketsOpen > -1 && ticketsClosed > -1) {
      return (ticketsClosed + ticketsOpen);
    }
    return -1;
  }

  public int getPercentTicketsClosed() {
    if (getTicketCount() == 0 || ticketsClosed == getTicketCount()) {
      return 100;
    }
    return (int) Math.round(((double) ticketsClosed / (double) getTicketCount()) * 100.0);
  }

  public int getPercentTicketsUpcoming() {
    if (getTicketCount() == 0 || (ticketsOpen - ticketsOverdue) == 0) {
      return 0;
    }
    return (int) Math.round(((double) (ticketsOpen - ticketsOverdue) / (double) getTicketCount()) * 100.0);
  }

  public int getPercentTicketsOverdue() {
    if (getTicketCount() == 0 || ticketsOverdue == 0) {
      return 0;
    }
    return (int) Math.round(((double) ticketsOverdue / (double) getTicketCount()) * 100.0);
  }

  public void buildPortalLink(Connection db) throws SQLException {
    portalLink = BlogPostList.queryPagePortalKey(db, id);
  }

  public boolean isGeoCoded() {
    return (latitude != 0.0 && longitude != 0.0);
  }

  public String getAddress() {
    StringBuffer sb = new StringBuffer();
    if (StringUtils.hasText(addressLine1)) {
      sb.append(addressLine1);
    }
    if (StringUtils.hasText(addressLine2)) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(addressLine2);
    }
    if (StringUtils.hasText(addressLine3)) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(addressLine3);
    }
    return sb.toString();
  }

  public String getLocation() {
    StringBuffer sb = new StringBuffer();
    if (StringUtils.hasText(city)) {
      sb.append(city);
    }
    if (StringUtils.hasText(state)) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(state);
    }
    if (StringUtils.hasText(postalCode)) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      if (postalCode.length() == 10) {
        sb.append(postalCode.substring(0, 5));
      } else {
        sb.append(postalCode);
      }
    }
    return sb.toString();
  }

  public String getAddressToAndLocation() {
    StringBuffer sb = new StringBuffer();
    if (StringUtils.hasText(addressTo)) {
      sb.append(addressTo);
    }
    String address = this.getAddress();
    if (StringUtils.hasText(address)) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(address);
    }
    String location = this.getLocation();
    if (StringUtils.hasText(location)) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(location);
    }
    return sb.toString();
  }

  public void updateGeocode(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "UPDATE projects " +
            "SET latitude = ?, longitude = ? " +
            "WHERE project_id = ?");
    pst.setDouble(1, latitude);
    pst.setDouble(2, longitude);
    pst.setInt(3, id);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
  }

  public void updateLocation(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "UPDATE projects " +
            "SET latitude = ?, longitude = ?, city = ?, state = ?, postalcode = ?, country = ? " +
            "WHERE project_id = ?");
    pst.setDouble(1, latitude);
    pst.setDouble(2, longitude);
    pst.setString(3, city);
    pst.setString(4, state);
    pst.setString(5, postalCode);
    pst.setString(6, country);
    pst.setInt(7, id);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
  }

  public void updateLogoId(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // set the project default
      PreparedStatement pst = db.prepareStatement(
          "UPDATE projects " +
              "SET logo_id = ? " +
              "WHERE project_id = ?");
      DatabaseUtils.setInt(pst, 1, logoId);
      pst.setInt(2, this.getId());
      pst.execute();
      pst.close();
      if (logoId > -1) {
        // set the file item list default
        pst = db.prepareStatement(
            "UPDATE project_files " +
                "SET default_file = ? " +
                "WHERE item_id = ?");
        pst.setBoolean(1, true);
        DatabaseUtils.setInt(pst, 2, logoId);
        pst.execute();
        pst.close();
      }
      // unset the others
      pst = db.prepareStatement(
          "UPDATE project_files " +
              "SET default_file = ? " +
              "WHERE link_module_id = ? AND link_item_id = ? AND default_file = ? AND item_id <> ?");
      pst.setBoolean(1, false);
      pst.setInt(2, Constants.PROJECT_IMAGE_FILES);
      pst.setInt(3, id);
      pst.setBoolean(4, true);
      pst.setInt(5, logoId);
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw e;
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
  }

  public void updateStyle(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "UPDATE projects " +
            "SET style = ?, style_enabled = ? " +
            "WHERE project_id = ?");
    int i = 1;
    pst.setString(i++, style);
    pst.setBoolean(i++, styleEnabled);
    pst.setInt(i++, id);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public boolean isImport() {
    // If the source is an import, then return true...
    return (source != null && source.toLowerCase().contains("import"));
  }

  public String getCityStateString() {
    StringBuffer cityStateStringBuffer = new StringBuffer();
    if (StringUtils.hasText(getCity()) || StringUtils.hasText(getState())) {
      cityStateStringBuffer.append("(");
      if (StringUtils.hasText(getCity())) {
        cityStateStringBuffer.append(getCity());
      }
      if (StringUtils.hasText(getState())) {
        if (cityStateStringBuffer.toString().length() > 1) {
          cityStateStringBuffer.append(", " + getState());
        } else {
          cityStateStringBuffer.append(getState());
        }
      }
      cityStateStringBuffer.append(")");
    }
    return StringUtils.hasText(cityStateStringBuffer.toString()) ? cityStateStringBuffer.toString() : "";
  }

  /**
   * @param db
   * @param userId
   * @param fileLibraryPath
   * @param attachment
   */
  public void insertLogo(Connection db, int userId, String fileLibraryPath, String attachment) throws SQLException {
    if (StringUtils.hasText(attachment)) {
      boolean isFirst = (buildImages(db) == 0);
      FileItemList.convertTempFiles(db, Constants.PROJECT_IMAGE_FILES, userId, this.getId(), attachment, isFirst);
      isFirst = (buildImages(db) == 1);
      if (isFirst) {
        this.setLogoId(images.get(0).getId());
        this.updateLogoId(db);
      }
    }
  }

  public void insertSiteLogo(Connection db, int userId, String fileLibraryPath, String attachment) throws SQLException {
    buildSiteLogo(db);
    siteLogos.delete(db, fileLibraryPath);
    if (StringUtils.hasText(attachment)) {
      FileItemList.convertTempFiles(db, Constants.SITE_LOGO_FILES, userId, this.getId(), attachment);
    }
  }
}
