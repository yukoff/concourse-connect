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
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.dao.BadgeList;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created August 9, 2002
 */
public class ProjectList extends ArrayList<Project> {
  // main project filters
  private PagedListInfo pagedListInfo = null;
  private String emptyHtmlSelectRecord = null;
  private int groupId = -1;
  private int instanceId = -1;
  private int projectId = -1;
  private int projectsForUser = -1;
  private int enteredByUser = -1;
  private String enteredByUserRange = null;
  private String userRange = null;
  private boolean openProjectsOnly = false;
  private boolean closedProjectsOnly = false;
  private int withProjectDaysComplete = -1;
  private boolean projectsWithAssignmentsOnly = false;
  private boolean invitationPendingOnly = false;
  private boolean invitationAcceptedOnly = false;
  private int daysLastAccessed = -1;
  private int daysLastApproved = -1;
  private boolean includeGuestProjects = false;
  private int requiresMembership = Constants.UNDEFINED;
  private int categoryId = -1;
  private int subCategory1Id = -1;
  private int subCategory2Id = -1;
  private int subCategory3Id = -1;
  private String title = null;
  private String twitterId = null;
  private String keywords = null;
  private double latitude = -1;
  private double longitude = -1;
  private int owner = -1;
  private int profile = Constants.UNDEFINED;
  private int profileEnabled = Constants.UNDEFINED;
  private String projectIdsString = null;
  private String excludeProjectIdsString = null;
  private double minimumAverageRating = -1;
  private int forParticipant = Constants.UNDEFINED;


  // filters that go into sub-objects
  private boolean buildPermissions = false;
  private boolean buildLink = false;
  private int portalState = Constants.UNDEFINED;
  private boolean portalDefaultOnly = false;
  private String portalKey = null;
  private boolean publicOnly = false;
  private boolean approvedOnly = false;
  private int languageId = -1;
  private int userRating = -1;
  private boolean buildImages = false;

  // calendar filters
  protected Timestamp alertRangeStart = null;
  protected Timestamp alertRangeEnd = null;

  protected Timestamp approvalRangeStart = null;
  protected Timestamp approvalRangeEnd = null;

  private boolean buildOverallIssues = false;
  private int includeTemplates = Constants.FALSE;

  // Helper Attributes
  private String[] projectIdString = null;
  private String[] excludeProjectIdString = null;
  private String[] categoryIdString = null;
  private String[] badgeIdString = null;

  // Twitter filters
  private boolean hasTwitterId = false;


  /**
   * Constructor for the ProjectList object
   */
  public ProjectList() {
  }


  /**
   * Sets the pagedListInfo attribute of the ProjectList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Gets the pagedListInfo attribute of the ProjectList object
   *
   * @return pagedListInfo
   */
  public PagedListInfo getPagedListInfo() {
    return this.pagedListInfo;
  }


  /**
   * Sets the emptyHtmlSelectRecord attribute of the ProjectList object
   *
   * @param tmp The new emptyHtmlSelectRecord value
   */
  public void setEmptyHtmlSelectRecord(String tmp) {
    this.emptyHtmlSelectRecord = tmp;
  }


  /**
   * Sets the groupId attribute of the ProjectList object
   *
   * @param tmp The new groupId value
   */
  public void setGroupId(int tmp) {
    this.groupId = tmp;
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
   * Sets the projectId attribute of the ProjectList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  /**
   * Sets the openProjectsOnly attribute of the ProjectList object
   *
   * @param tmp The new openProjectsOnly value
   */
  public void setOpenProjectsOnly(boolean tmp) {
    this.openProjectsOnly = tmp;
  }

  public void setOpenProjectsOnly(String tmp) {
    this.openProjectsOnly = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getOpenProjectsOnly() {
    return openProjectsOnly;
  }

  public int getForParticipant() {
    return forParticipant;
  }

  public void setForParticipant(int forParticipant) {
    this.forParticipant = forParticipant;
  }

  public void setForParticipant(String tmp) {
    forParticipant = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  /**
   * Sets the closedProjectsOnly attribute of the ProjectList object
   *
   * @param tmp The new closedProjectsOnly value
   */
  public void setClosedProjectsOnly(boolean tmp) {
    this.closedProjectsOnly = tmp;
  }


  /**
   * Sets the closedProjectsOnly attribute of the ProjectList object
   *
   * @param tmp The new closedProjectsOnly value
   */
  public void setClosedProjectsOnly(String tmp) {
    this.closedProjectsOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the withProjectDaysComplete attribute of the ProjectList object
   *
   * @param tmp The new withProjectDaysComplete value
   */
  public void setWithProjectDaysComplete(int tmp) {
    this.withProjectDaysComplete = tmp;
  }


  /**
   * Sets the projectsWithAssignmentsOnly attribute of the ProjectList object
   *
   * @param tmp The new projectsWithAssignmentsOnly value
   */
  public void setProjectsWithAssignmentsOnly(boolean tmp) {
    this.projectsWithAssignmentsOnly = tmp;
  }


  /**
   * Sets the invitationPendingOnly attribute of the ProjectList object
   *
   * @param tmp The new invitationPendingOnly value
   */
  public void setInvitationPendingOnly(boolean tmp) {
    this.invitationPendingOnly = tmp;
  }


  /**
   * Sets the invitationAcceptedOnly attribute of the ProjectList object
   *
   * @param tmp The new invitationAcceptedOnly value
   */
  public void setInvitationAcceptedOnly(boolean tmp) {
    this.invitationAcceptedOnly = tmp;
  }


  /**
   * Gets the invitationPendingOnly attribute of the ProjectList object
   *
   * @return The invitationPendingOnly value
   */
  public boolean getInvitationPendingOnly() {
    return invitationPendingOnly;
  }


  /**
   * Gets the invitationAcceptedOnly attribute of the ProjectList object
   *
   * @return The invitationAcceptedOnly value
   */
  public boolean getInvitationAcceptedOnly() {
    return invitationAcceptedOnly;
  }


  /**
   * Gets the daysLastAccessed attribute of the ProjectList object
   *
   * @return The daysLastAccessed value
   */
  public int getDaysLastAccessed() {
    return daysLastAccessed;
  }


  /**
   * Sets the daysLastAccessed attribute of the ProjectList object
   *
   * @param tmp The new daysLastAccessed value
   */
  public void setDaysLastAccessed(int tmp) {
    this.daysLastAccessed = tmp;
  }


  /**
   * Sets the daysLastAccessed attribute of the ProjectList object
   *
   * @param tmp The new daysLastAccessed value
   */
  public void setDaysLastAccessed(String tmp) {
    this.daysLastAccessed = Integer.parseInt(tmp);
  }

  public int getDaysLastApproved() {
    return daysLastApproved;
  }

  public void setDaysLastApproved(int daysLastApproved) {
    this.daysLastApproved = daysLastApproved;
  }

  /**
   * Gets the includeGuestProjects attribute of the ProjectList object
   *
   * @return The includeGuestProjects value
   */
  public boolean getIncludeGuestProjects() {
    return includeGuestProjects;
  }


  /**
   * Sets the includeGuestProjects attribute of the ProjectList object
   *
   * @param tmp The new includeGuestProjects value
   */
  public void setIncludeGuestProjects(boolean tmp) {
    this.includeGuestProjects = tmp;
  }


  /**
   * Sets the includeGuestProjects attribute of the ProjectList object
   *
   * @param tmp The new includeGuestProjects value
   */
  public void setIncludeGuestProjects(String tmp) {
    this.includeGuestProjects = DatabaseUtils.parseBoolean(tmp);
  }

  public int getRequiresMembership() {
    return requiresMembership;
  }

  public void setRequiresMembership(int requiresMembership) {
    this.requiresMembership = requiresMembership;
  }

  public void setRequiresMembership(String requiresMembership) {
    this.requiresMembership = Integer.parseInt(requiresMembership);
  }

  public void setRequiresMembership(boolean requiresMembership) {
    if (requiresMembership) {
      this.requiresMembership = Constants.TRUE;
    } else {
      this.requiresMembership = Constants.FALSE;
    }
  }


  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryId(String tmp) {
    categoryId = Integer.parseInt(tmp);
  }

  /**
   * @return the subCategory1Id
   */
  public int getSubCategory1Id() {
    return subCategory1Id;
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


  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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


  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLatitude(String tmp) {
    this.latitude = Double.parseDouble(tmp);
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setLongitude(String tmp) {
    this.longitude = Double.parseDouble(tmp);
  }

  /**
   * Sets the projectsForUser attribute of the ProjectList object
   *
   * @param tmp The new projectsForUser value
   */
  public void setProjectsForUser(int tmp) {
    this.projectsForUser = tmp;
  }


  /**
   * Sets the enteredByUser attribute of the ProjectList object
   *
   * @param tmp The new enteredByUser value
   */
  public void setEnteredByUser(int tmp) {
    this.enteredByUser = tmp;
  }


  /**
   * Sets the enteredByUser attribute of the ProjectList object
   *
   * @param tmp The new enteredByUser value
   */
  public void setEnteredByUser(String tmp) {
    this.enteredByUser = Integer.parseInt(tmp);
  }


  /**
   * Sets the userRange attribute of the ProjectList object
   *
   * @param tmp The new userRange value
   */
  public void setUserRange(String tmp) {
    this.userRange = tmp;
  }


  /**
   * Sets the enteredByUserRange attribute of the ProjectList object
   *
   * @param tmp The new enteredByUserRange value
   */
  public void setEnteredByUserRange(String tmp) {
    this.enteredByUserRange = tmp;
  }


  /**
   * Gets the portalState attribute of the ProjectList object
   *
   * @return The portalState value
   */
  public int getPortalState() {
    return portalState;
  }


  /**
   * Sets the portalState attribute of the ProjectList object
   *
   * @param tmp The new portalState value
   */
  public void setPortalState(int tmp) {
    this.portalState = tmp;
  }


  /**
   * Sets the portalState attribute of the ProjectList object
   *
   * @param tmp The new portalState value
   */
  public void setPortalState(String tmp) {
    this.portalState = DatabaseUtils.parseBooleanToConstant(tmp);
  }


  /**
   * Gets the portalDefaultOnly attribute of the ProjectList object
   *
   * @return The portalDefaultOnly value
   */
  public boolean getPortalDefaultOnly() {
    return portalDefaultOnly;
  }


  /**
   * Sets the portalDefaultOnly attribute of the ProjectList object
   *
   * @param tmp The new portalDefaultOnly value
   */
  public void setPortalDefaultOnly(boolean tmp) {
    this.portalDefaultOnly = tmp;
  }


  /**
   * Sets the portalDefaultOnly attribute of the ProjectList object
   *
   * @param tmp The new portalDefaultOnly value
   */
  public void setPortalDefaultOnly(String tmp) {
    this.portalDefaultOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the portalKey attribute of the ProjectList object
   *
   * @return The portalKey value
   */
  public String getPortalKey() {
    return portalKey;
  }


  /**
   * Sets the portalKey attribute of the ProjectList object
   *
   * @param tmp The new portalKey value
   */
  public void setPortalKey(String tmp) {
    this.portalKey = tmp;
  }


  /**
   * Sets the publicOnly attribute of the ProjectList object
   *
   * @param tmp The new publicOnly value
   */
  public void setPublicOnly(boolean tmp) {
    this.publicOnly = tmp;
  }


  /**
   * Sets the publicOnly attribute of the ProjectList object
   *
   * @param tmp The new publicOnly value
   */
  public void setPublicOnly(String tmp) {
    this.publicOnly = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getPublicOnly() {
    return publicOnly;
  }

  /**
   * Gets the approvedOnly attribute of the ProjectList object
   *
   * @return The approvedOnly value
   */
  public boolean getApprovedOnly() {
    return approvedOnly;
  }


  /**
   * Sets the approvedOnly attribute of the ProjectList object
   *
   * @param tmp The new approvedOnly value
   */
  public void setApprovedOnly(boolean tmp) {
    this.approvedOnly = tmp;
  }


  /**
   * Sets the approvedOnly attribute of the ProjectList object
   *
   * @param tmp The new approvedOnly value
   */
  public void setApprovedOnly(String tmp) {
    this.approvedOnly = DatabaseUtils.parseBoolean(tmp);
  }

  public int getLanguageId() {
    return languageId;
  }

  public void setLanguageId(int languageId) {
    this.languageId = languageId;
  }

  public int getUserRating() {
    return userRating;
  }

  public void setUserRating(int userRating) {
    this.userRating = userRating;
  }

  /**
   * @return the buildImages
   */
  public boolean getBuildImages() {
    return buildImages;
  }


  /**
   * @param buildImages the buildImages to set
   */
  public void setBuildImages(boolean buildImages) {
    this.buildImages = buildImages;
  }


  public void setBuildImages(String buildImages) {
    this.buildImages = DatabaseUtils.parseBoolean(buildImages);
  }


  /**
   * Sets the alertRangeStart attribute of the ProjectList object
   *
   * @param alertRangeStart The new alertRangeStart value
   */
  public void setAlertRangeStart(Timestamp alertRangeStart) {
    this.alertRangeStart = alertRangeStart;
  }

  public void setAlertRangeStart(String alertRangeStart) {
    this.alertRangeStart = DatabaseUtils.parseTimestamp(alertRangeStart);
  }

  /**
   * Sets the alertRangeEnd attribute of the ProjectList object
   *
   * @param alertRangeEnd The new alertRangeEnd value
   */
  public void setAlertRangeEnd(Timestamp alertRangeEnd) {
    this.alertRangeEnd = alertRangeEnd;
  }

  public void setAlertRangeEnd(String alertRangeEnd) {
    this.alertRangeEnd = DatabaseUtils.parseTimestamp(alertRangeEnd);
  }

  /**
   * @return the approvalRangeStart
   */
  public Timestamp getApprovalRangeStart() {
    return approvalRangeStart;
  }


  /**
   * @param approvalRangeStart the approvalRangeStart to set
   */
  public void setApprovalRangeStart(Timestamp approvalRangeStart) {
    this.approvalRangeStart = approvalRangeStart;
  }

  public void setApprovalRangeStart(String approvalRangeStart) {
    this.approvalRangeStart = DatabaseUtils.parseTimestamp(approvalRangeStart);
  }

  /**
   * @return the approvalRangeEnd
   */
  public Timestamp getApprovalRangeEnd() {
    return approvalRangeEnd;
  }


  /**
   * @param approvalRangeEnd the approvalRangeEnd to set
   */
  public void setApprovalRangeEnd(Timestamp approvalRangeEnd) {
    this.approvalRangeEnd = approvalRangeEnd;
  }

  public void setApprovalRangeEnd(String approvalRangeEnd) {
    this.approvalRangeEnd = DatabaseUtils.parseTimestamp(approvalRangeEnd);
  }

  public void setBuildPermissions(boolean buildPermissions) {
    this.buildPermissions = buildPermissions;
  }

  public void setBuildLink(boolean buildLink) {
    this.buildLink = buildLink;
  }

  /**
   * Gets the ownerId attribute of the ProjectList object
   *
   * @return The ownerId value
   */
  public int getOwner() {
    return owner;
  }


  /**
   * Sets the ownerId attribute of the ProjectList object
   *
   * @param tmp The new ownerId value
   */
  public void setOwner(int tmp) {
    this.owner = tmp;
  }


  /**
   * Sets the hasProfile attribute of the ProjectList object
   *
   * @param tmp The new hasProfile value
   */
  public void setOwner(String tmp) {
    this.owner = Integer.parseInt(tmp);
  }

  /**
   * Gets the hasProfile attribute of the ProjectList object
   *
   * @return The hasProfile value
   */
  public int getProfile() {
    return profile;
  }


  /**
   * Sets the hasProfile attribute of the ProjectList object
   *
   * @param tmp The new hasProfile value
   */
  public void setProfile(int tmp) {
    this.profile = tmp;
  }


  /**
   * Sets the hasProfile attribute of the ProjectList object
   *
   * @param tmp The new hasProfile value
   */
  public void setProfile(String tmp) {
    this.profile = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  /**
   * Gets the profileEnabled attribute of the ProjectList object
   *
   * @return The profileEnabled value
   */
  public int getProfileEnabled() {
    return profileEnabled;
  }


  /**
   * Sets the hasProfile attribute of the ProjectList object
   *
   * @param tmp The new hasProfile value
   */
  public void setProfileEnabled(int tmp) {
    this.profileEnabled = tmp;
  }


  /**
   * Sets the hasProfile attribute of the ProjectList object
   *
   * @param tmp The new hasProfile value
   */
  public void setProfileEnabled(String tmp) {
    this.profileEnabled = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  /**
   * @return the projectIdsString
   */
  public String getProjectIdsString() {
    return projectIdsString;
  }


  /**
   * @param projectIdsString the projectIdsString to set
   */
  public void setProjectIdsString(String projectIdsString) {
    this.projectIdsString = projectIdsString;
    if (StringUtils.hasText(this.projectIdsString)) {
      projectIdString = this.projectIdsString.split(",");
    }
  }

  public void setProjectIdString(String[] projectIdString) {
    this.projectIdString = projectIdString;
  }

  /**
   * @return the excludeProjectIdsString
   */
  public String getExcludeProjectIdsString() {
    return excludeProjectIdsString;
  }


  /**
   * @param excludeProjectIdsString the excludeProjectIdsString to set
   */
  public void setExcludeProjectIdsString(String excludeProjectIdsString) {
    this.excludeProjectIdsString = excludeProjectIdsString;
    if (StringUtils.hasText(this.excludeProjectIdsString)) {
      excludeProjectIdString = this.excludeProjectIdsString.split(",");
    }
  }

  public void setExcludeProjectIdString(String[] excludeProjectIdString) {
    this.excludeProjectIdString = excludeProjectIdString;
  }

  public void setCategoryList(ProjectCategoryList categoryList) {
    if (categoryList != null && categoryList.size() > 0) {
      categoryIdString = new String[categoryList.size()];
      int count = -1;
      for (ProjectCategory category : categoryList) {
        ++count;
        categoryIdString[count] = String.valueOf(category.getId());
      }
    } else {
      categoryIdString = null;
    }
  }

  public void setCategoryIdString(String[] categoryIdString) {
    this.categoryIdString = categoryIdString;
  }

  public String[] getCategoryIdString() {
    return categoryIdString;
  }

  public void setBadgeList(BadgeList badgeList) {
    if (badgeList != null && badgeList.size() > 0) {
      badgeIdString = new String[badgeList.size()];
      int count = -1;
      for (Badge thisBadge : badgeList) {
        ++count;
        badgeIdString[count] = String.valueOf(thisBadge.getId());
      }
    } else {
      badgeIdString = null;
    }
  }

  public void setBadgeIdString(String[] badgeIdString) {
    this.badgeIdString = badgeIdString;
  }

  public String[] getBadgeIdString() {
    return badgeIdString;
  }

  /**
   * @return the minimumAverageRating
   */
  public double getMinimumAverageRating() {
    return minimumAverageRating;
  }


  /**
   * @param minimumAverageRating the minimumAverageRating to set
   */
  public void setMinimumAverageRating(double minimumAverageRating) {
    this.minimumAverageRating = minimumAverageRating;
  }


  public void setMinimumAverageRating(String minimumAverageRating) {
    this.minimumAverageRating = Double.parseDouble(minimumAverageRating);
  }

  /**
   * @param hasTwitterId the hasTwitterId to set
   */
  public void setHasTwitterId(boolean hasTwitterId) {
    this.hasTwitterId = hasTwitterId;
  }

  /**
   * @param hasTwitterId the hasTwitterId to set
   */
  public void setHasTwitterId(String hasTwitterId) {
    this.hasTwitterId = DatabaseUtils.parseBoolean(hasTwitterId);
  }

  /**
   * @return the hasTwitterId
   */
  public boolean getHasTwitterIdy() {
    return hasTwitterId;
  }

  /**
   * Gets the htmlSelect attribute of the ProjectList object
   *
   * @param selectName Description of Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName) {
    return getHtmlSelect(selectName, -1);
  }


  /**
   * Gets the htmlSelect attribute of the ProjectList object
   *
   * @param selectName Description of Parameter
   * @param defaultKey Description of Parameter
   * @return The htmlSelect value
   */
  public String getHtmlSelect(String selectName, int defaultKey) {
    HtmlSelect listSelect = this.getHtmlSelect();
    return listSelect.getHtml(selectName, defaultKey);
  }


  /**
   * Gets the htmlSelect attribute of the ProjectList object
   *
   * @return The htmlSelect value
   */
  public HtmlSelect getHtmlSelect() {
    HtmlSelect listSelect = new HtmlSelect();
    if (emptyHtmlSelectRecord != null) {
      listSelect.addItem(-1, emptyHtmlSelectRecord);
    }
    for (Project thisProject : this) {
      listSelect.addItem(
          thisProject.getId(),
          thisProject.getTitle());
    }
    return listSelect;
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;

    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();

    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM projects p " +
            "WHERE project_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(0);
    }

    //Get the total number of records matching filter
    pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (rs.next()) {
      int maxRecords = rs.getInt("recordcount");
      pagedListInfo.setMaxRecords(maxRecords);
    }
    rs.close();
    pst.close();

    //Determine the offset, based on the filter, for the first record to show
    if (!pagedListInfo.getCurrentLetter().equals("")) {
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString() +
          "AND lower(title) < ? ");
      items = prepareFilter(pst);
      pst.setString(++items, pagedListInfo.getCurrentLetter().toLowerCase());
      rs = pst.executeQuery();
      if (rs.next()) {
        int offsetCount = rs.getInt("recordcount");
        pagedListInfo.setCurrentOffset(offsetCount);
      }
      rs.close();
      pst.close();
    }

    //Determine column to sort by
    pagedListInfo.setDefaultSort("title", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);

    //Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "* " +
            "FROM projects p " +
            "WHERE project_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      Project thisProject = new Project(rs);
      this.add(thisProject);
    }
    rs.close();
    pst.close();
    for (Project thisProject : this) {
      if (buildOverallIssues) {
        thisProject.buildTicketCounts(db);
      }
      if (buildPermissions) {
        thisProject.buildPermissionList(db);
      }
      if (buildLink) {
        thisProject.buildPortalLink(db);
      }
      if (buildImages) {
        thisProject.buildImages(db);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (hasTwitterId) {
      sqlFilter.append("AND (twitter_id IS NOT NULL AND twitter_id <> '') ");
    }
    if (groupId > -1) {
      sqlFilter.append("AND group_id = ? ");
    }
    if (instanceId > -1) {
      sqlFilter.append("AND instance_id = ? ");
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (projectsWithAssignmentsOnly) {
      sqlFilter.append("AND p.project_id IN (SELECT DISTINCT project_id FROM project_assignments) ");
    }
    if (openProjectsOnly && withProjectDaysComplete > -1) {
      sqlFilter.append("AND (closedate IS NULL OR closedate > ?) ");
    } else {
      if (openProjectsOnly) {
        sqlFilter.append("AND closedate IS NULL ");
      }
      if (withProjectDaysComplete > -1) {
        sqlFilter.append("AND closeDate > ? ");
      }
    }
    if (closedProjectsOnly) {
      sqlFilter.append("AND closedate IS NOT NULL ");
    }
    if (projectsForUser > -1) {
      sqlFilter.append("AND (p.project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          (invitationAcceptedOnly ? "AND status IS NULL " : "") +
          (invitationPendingOnly ? "AND status = ? " : "") +
          (daysLastAccessed > -1 ? "AND last_accessed > ? " : "") + ") " +
          (includeGuestProjects ? "OR (allow_guests = ? AND approvaldate IS NOT NULL) " : "") +
          ") ");
    }
    if (requiresMembership != Constants.UNDEFINED) {
      sqlFilter.append("AND p.membership_required = ? ");
    }
    if (daysLastApproved > -1) {
      sqlFilter.append("AND approvaldate IS NOT NULL AND approvaldate > ? ");
    }
    if (userRange != null) {
      sqlFilter.append("AND (p.project_id in (SELECT DISTINCT project_id FROM project_team WHERE user_id IN (" + userRange + ")) " +
          "OR p.enteredBy IN (" + userRange + ")) ");
    }
    if (enteredByUser > -1) {
      sqlFilter.append("AND p.enteredby = ? ");
    }
    if (enteredByUserRange != null) {
      sqlFilter.append("AND p.enteredby IN (" + enteredByUserRange + ") ");
    }
    if (portalState != Constants.UNDEFINED) {
      sqlFilter.append("AND portal = ? ");
    }
    if (portalDefaultOnly) {
      sqlFilter.append("AND portal_default = ? ");
    }
    if (portalKey != null) {
      sqlFilter.append("AND portal_key = ? ");
    }
    if (publicOnly) {
      sqlFilter.append("AND allow_guests = ? ");
    }
    if (forParticipant == Constants.TRUE) {
      sqlFilter.append("AND (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL ");
    }
    if (approvedOnly) {
      sqlFilter.append("AND approvaldate IS NOT NULL ");
    }
    if (categoryId > -1) {
      sqlFilter.append("AND p.category_id = ? ");
    }
    if (subCategory1Id > -1) {
      sqlFilter.append("AND p.subcategory1_id = ? ");
    }
    if (subCategory2Id > -1) {
      sqlFilter.append("AND p.subcategory2_id = ? ");
    }
    if (subCategory2Id > -1) {
      sqlFilter.append("AND p.subcategory3_id = ? ");
    }
    if (includeTemplates != Constants.UNDEFINED) {
      sqlFilter.append("AND template = ? ");
    }
    if (languageId > -1) {
      sqlFilter.append("AND p.language_id = ? ");
    }
    if (userRating > -1 && projectsForUser > -1) {
      sqlFilter.append(
          "AND p.project_id IN " +
              "(SELECT project_id FROM projects_rating WHERE rating = ? AND enteredby = ?) ");
    }
    if (title != null) {
      sqlFilter.append("AND lower(title) = ? ");
    }
    if (twitterId != null) {
      sqlFilter.append("AND lower(twitter_id) = ? ");
    }
    if (keywords != null) {
      sqlFilter.append("AND lower(keywords) = ? ");
    }
    if (latitude != -1) {
      sqlFilter.append("AND latitude = ? ");
    }
    if (longitude != -1) {
      sqlFilter.append("AND longitude = ? ");
    }
    if (owner > -1) {
      sqlFilter.append("AND owner = ? ");
    }
    if (profile != Constants.UNDEFINED) {
      sqlFilter.append("AND profile = ? ");
    }
    if (profileEnabled != Constants.UNDEFINED) {
      sqlFilter.append("AND profile_enabled = ? ");
    }

    if (projectIdString != null && projectIdString.length > 0) {
      sqlFilter.append("AND project_id IN (");
      int count = 0;
      boolean isNumber = false;
      while (count < projectIdString.length) {
        if (StringUtils.isNumber(projectIdString[count])) {
          sqlFilter.append("?");
          isNumber = true;
        }
        count++;
        if (count < projectIdString.length && isNumber) {
          sqlFilter.append(",");
        }
        isNumber = false;
      }
      sqlFilter.append(") ");
    }

    if (excludeProjectIdString != null && excludeProjectIdString.length > 0) {
      sqlFilter.append("AND project_id NOT IN (");
      int count = 0;
      boolean isNumber = false;
      while (count < excludeProjectIdString.length) {
        if (StringUtils.isNumber(excludeProjectIdString[count])) {
          sqlFilter.append("?");
          isNumber = true;
        }
        count++;
        if (count < excludeProjectIdString.length && isNumber) {
          sqlFilter.append(",");
        }
        isNumber = false;
      }
      sqlFilter.append(") ");
    }

    if (categoryIdString != null && categoryIdString.length > 0) {
      sqlFilter.append("AND p.category_id IN (");
      int count = 0;
      for (String thisCategoryId : categoryIdString) {
        ++count;
        sqlFilter.append(thisCategoryId);
        if (count < categoryIdString.length) {
          sqlFilter.append(", ");
        }
      }
      sqlFilter.append(") ");
    }

    if (badgeIdString != null && badgeIdString.length > 0) {
      sqlFilter.append("AND p.project_id IN (SELECT project_id FROM badgelink_project WHERE badge_id IN (");
      int count = 0;
      for (String thisBadgeId : badgeIdString) {
        ++count;
        sqlFilter.append(thisBadgeId);
        if (count < badgeIdString.length) {
          sqlFilter.append(", ");
        }
      }
      sqlFilter.append(")) ");
    }

    if (minimumAverageRating != -1) {
      sqlFilter.append("AND rating_avg > ? ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND p.entered >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND p.entered < ? ");
    }
    if (approvalRangeStart != null) {
      sqlFilter.append("AND p.approvaldate >= ? ");
    }
    if (approvalRangeEnd != null) {
      sqlFilter.append("AND p.approvaldate < ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (groupId > -1) {
      pst.setInt(++i, groupId);
    }
    if (instanceId > -1) {
      pst.setInt(++i, instanceId);
    }
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (openProjectsOnly && withProjectDaysComplete > -1) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -withProjectDaysComplete);
      pst.setTimestamp(++i, new Timestamp(cal.getTimeInMillis()));
    } else {
      if (withProjectDaysComplete > -1) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -withProjectDaysComplete);
        pst.setTimestamp(++i, new Timestamp(cal.getTimeInMillis()));
      }
    }
    if (projectsForUser > -1) {
      pst.setInt(++i, projectsForUser);
      if (invitationPendingOnly) {
        pst.setInt(++i, TeamMember.STATUS_PENDING);
      }
      if (daysLastAccessed > -1) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysLastAccessed);
        pst.setTimestamp(++i, new Timestamp(cal.getTimeInMillis()));
      }
      if (includeGuestProjects) {
        pst.setBoolean(++i, true);
      }
    }
    if (requiresMembership != Constants.UNDEFINED) {
      pst.setBoolean(++i, requiresMembership == Constants.TRUE);
    }
    if (daysLastApproved > -1) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -daysLastApproved);
      pst.setTimestamp(++i, new Timestamp(cal.getTimeInMillis()));
    }
    if (enteredByUser > -1) {
      pst.setInt(++i, enteredByUser);
    }
    if (portalState != Constants.UNDEFINED) {
      pst.setBoolean(++i, (portalState == Constants.TRUE));
    }
    if (portalDefaultOnly) {
      pst.setBoolean(++i, true);
    }
    if (portalKey != null) {
      pst.setString(++i, portalKey);
    }
    if (publicOnly) {
      pst.setBoolean(++i, true);
    }
    if (forParticipant == Constants.TRUE) {
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, true);
    }
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (subCategory1Id > -1) {
      pst.setInt(++i, subCategory1Id);
    }
    if (subCategory2Id > -1) {
      pst.setInt(++i, subCategory2Id);
    }
    if (subCategory3Id > -1) {
      pst.setInt(++i, subCategory3Id);
    }
    if (includeTemplates != Constants.UNDEFINED) {
      pst.setBoolean(++i, (includeTemplates == Constants.TRUE));
    }
    if (languageId > -1) {
      pst.setInt(++i, languageId);
    }
    if (userRating > -1 && projectsForUser > -1) {
      pst.setInt(++i, userRating);
      pst.setInt(++i, projectsForUser);
    }
    if (title != null) {
      pst.setString(++i, title.toLowerCase());
    }
    if (twitterId != null) {
      pst.setString(++i, twitterId.toLowerCase());
    }
    if (keywords != null) {
      pst.setString(++i, keywords.toLowerCase());
    }
    if (latitude != -1) {
      pst.setDouble(++i, latitude);
    }
    if (longitude != -1) {
      pst.setDouble(++i, longitude);
    }
    if (owner > -1) {
      pst.setInt(++i, owner);
    }
    if (profile != Constants.UNDEFINED) {
      pst.setBoolean(++i, (profile == Constants.TRUE));
    }
    if (profileEnabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (profileEnabled == Constants.TRUE));
    }
    if (projectIdString != null && projectIdString.length > 0) {
      int count = 0;
      while (count < projectIdString.length) {
        if (StringUtils.isNumber(projectIdString[count])) {
          pst.setInt(++i, Integer.parseInt(projectIdString[count]));
        }
        count++;
      }
    }
    if (excludeProjectIdString != null && excludeProjectIdString.length > 0) {
      int count = 0;
      while (count < excludeProjectIdString.length) {
        if (StringUtils.isNumber(excludeProjectIdString[count])) {
          pst.setInt(++i, Integer.parseInt(excludeProjectIdString[count]));
        }
        count++;
      }
    }
    if (minimumAverageRating != -1) {
      pst.setDouble(++i, minimumAverageRating);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    if (approvalRangeStart != null) {
      pst.setTimestamp(++i, approvalRangeStart);
    }
    if (approvalRangeEnd != null) {
      pst.setTimestamp(++i, approvalRangeEnd);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static HashMap buildNameList(Connection db) throws SQLException {
    HashMap nameList = new HashMap();
    PreparedStatement pst = db.prepareStatement(
        "SELECT project_id, title " +
            "FROM projects");
    ResultSet rs = pst.executeQuery();
    while (rs.next()) {
      nameList.put(new Integer(rs.getInt("project_id")), rs.getString("title"));
    }
    rs.close();
    pst.close();
    return nameList;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int buildProjectCount(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) AS recordcount " +
            "FROM projects p " +
            "WHERE project_id > -1 ");
    ResultSet rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt("recordcount");
    rs.close();
    pst.close();
    return count;
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param userId Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int buildProjectCount(Connection db, int userId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) AS recordcount " +
            "FROM projects p " +
            "WHERE project_id > -1 " +
            "AND enteredby = ? ");
    pst.setInt(1, userId);
    ResultSet rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt("recordcount");
    rs.close();
    pst.close();
    return count;
  }


  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM projects p " +
            "WHERE project_id > -1 ");
    createFilter(sqlFilter);
    PreparedStatement pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("recordcount");
    }
    rs.close();
    pst.close();
    return count;
  }


  /**
   * Checks to see if any of the projects in the list are user only projects
   *
   * @return Description of the Return Value
   */
  public boolean hasUserProjects() {
    for (Project thisProject : this) {
      if (!thisProject.getFeatures().getAllowGuests()) {
        return true;
      }
    }
    return false;
  }

  public void buildTeam(Connection db) throws SQLException {
    for (Project thisProject : this) {
      thisProject.buildTeamMemberList(db);
    }
  }

  public void setBuildOverallIssues(boolean buildOverallIssues) {
    this.buildOverallIssues = buildOverallIssues;
  }

  public void setIncludeTemplates(int tmp) {
    includeTemplates = tmp;
  }

  public void setIncludesTemplates(String tmp) {
    includeTemplates = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  public int getIncludeTemplates() {
    return includeTemplates;
  }

  public Project retrieveProjectByTitle(String projectName) {
    for (Project thisProject : this) {
      if (thisProject.getTitle().equals(projectName)) {
        return thisProject;
      }
    }
    return null;
  }

  public Project retrieveProjectByKey(String portalKey) {
    for (Project thisProject : this) {
      if (thisProject.getPortalKey().equals(portalKey)) {
        return thisProject;
      }
    }
    return null;
  }

  /**
   * Description of the Method
   *
   * @param db  Database connection
   * @param key portal unique string id
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int queryIdFromKey(Connection db, String key) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT project_id " +
            "FROM projects " +
            "WHERE portal_key = ? " +
            "AND portal = ? " +
            "AND approvaldate IS NOT NULL ");
    pst.setString(1, key);
    pst.setBoolean(2, true);
    ResultSet rs = pst.executeQuery();
    int id = -1;
    if (rs.next()) {
      id = rs.getInt("project_id");
    }
    rs.close();
    pst.close();
    return id;
  }

  public static int querySystemDefault(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT project_id " +
            "FROM projects " +
            "WHERE system_default = ? ");
    pst.setBoolean(1, true);
    ResultSet rs = pst.executeQuery();
    int id = -1;
    if (rs.next()) {
      id = rs.getInt("project_id");
    }
    rs.close();
    pst.close();
    return id;
  }
}