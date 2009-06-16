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

package com.concursive.connect.web.modules.profile.utils;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.phone.PhoneNumberBean;
import com.concursive.commons.phone.PhoneNumberUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationBean;
import com.concursive.connect.web.modules.common.social.geotagging.utils.LocationUtils;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagLogList;
import com.concursive.connect.web.modules.discussion.dao.TopicList;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.issues.dao.TicketList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.plans.dao.AssignmentList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.PagedListInfo;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Class to manipulate project objects
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 7, 2006
 */
public class ProjectUtils {

  private static Log LOG = LogFactory.getLog(ProjectUtils.class);

  public static int queryWhatsNewCount(Connection db, int userId) throws SQLException {
    int count = 0;
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -2);
    Timestamp alertRangeStart = new Timestamp(cal.getTimeInMillis());
    // Latest News
    BlogPostList newsList = new BlogPostList();
    newsList.setForUser(userId);
    newsList.setAlertRangeStart(alertRangeStart);
    newsList.setCurrentNews(Constants.TRUE);
    count += newsList.queryCount(db);
    // Latest Issues
    TopicList topicList = new TopicList();
    topicList.setForUser(userId);
    topicList.setAlertRangeStart(alertRangeStart);
    count += topicList.queryCount(db);
    // Latest Files
    FileItemList fileItemList = new FileItemList();
    fileItemList.setLinkModuleId(Constants.PROJECTS_FILES);
    fileItemList.setForProjectUser(userId);
    fileItemList.setAlertRangeStart(alertRangeStart);
    count += fileItemList.queryCount(db);
    // Latest Wikis
    WikiList wikiList = new WikiList();
    wikiList.setForUser(userId);
    wikiList.setAlertRangeStart(alertRangeStart);
    count += wikiList.queryCount(db);
    return count;
  }

  public static int queryWhatsAssignedCount(Connection db, int userId) throws SQLException {
    int count = 0;
    // Assignments
    AssignmentList assignmentList = new AssignmentList();
    assignmentList.setForProjectUser(userId);
    assignmentList.setAssignmentsForUser(userId);
    assignmentList.setIncompleteOnly(true);
    assignmentList.setOnlyIfRequirementOpen(true);
    assignmentList.setOnlyIfProjectOpen(true);
    count += assignmentList.queryCount(db);
    // Tickets
    TicketList ticketList = new TicketList();
    ticketList.setForProjectUser(userId);
    ticketList.setAssignedTo(userId);
    ticketList.setOnlyOpen(true);
    ticketList.setOnlyIfProjectOpen(true);
    count += ticketList.queryCount(db);
    return count;
  }

  public static int queryMyProjectCount(Connection db, int userId) throws SQLException {
    int count = 0;
    // How many projects does this user belong to?
    TeamMemberList teamMemberList = new TeamMemberList();
    teamMemberList.setUserId(userId);
    count += teamMemberList.queryCount(db);
    return count;
  }

  public static Project loadProject(int projectId) {
    if (projectId > -1) {
      Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_PROJECT_CACHE);
      Element element = cache.get(projectId);
      return (Project) element.getObjectValue();
    } else {
      return null;
    }
  }

  public static Project loadProject(String uniqueId) {
    if (StringUtils.hasText(uniqueId)) {
      int projectId = retrieveProjectIdFromUniqueId(uniqueId);
      if (projectId > -1) {
        return loadProject(projectId);
      }
    }
    return null;
  }

  public static int retrieveProjectIdFromUniqueId(String uniqueId) {
    Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE);
    Element element = cache.get(uniqueId);
    if (element.getObjectValue() != null) {
      return (Integer) element.getObjectValue();
    }
    return -1;
  }

  public static boolean hasAccess(int projectId, User thisUser, String permission) {
    // Get the project from cache
    Project thisProject = loadProject(projectId);
    // Check access to the system first
    if (thisUser == null || UserUtils.isUserDisabled(thisUser)) {
      if (thisUser == null) {
        LOG.debug("hasAccess: failed - user is null");
      } else {
        LOG.debug("hasAccess: failed - user is disabled");
      }
      return false;
    }
    // Allow admins in
    if (thisUser.getAccessAdmin()) {
      return true;
    }
    // Allow content editors in
    if (thisUser.hasContentEditorAccess(thisProject.getLanguageId()) && thisProject.getPortal()) {
      return true;
    }
    // Compare the project permissions
    int accessLevel = thisProject.getPermissions().getAccessLevel(permission);
    // The following returns a number... typically 10-100
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    int projectRoleId = roleList.getLevelFromId(accessLevel);
    if (accessLevel == -1 || projectRoleId == -1) {
      LOG.debug("hasAccess: failed - invalid role");
      return false;
    }
    // If the user is on the team, check their permission
    TeamMember teamMember = thisProject.getTeam().getTeamMember(thisUser.getId());
    if (teamMember != null && teamMember.getRoleId() <= projectRoleId) {
      return true;
    }
    if (!thisProject.getApproved()) {
      // If the project isn't approved, and you're not a team member
      LOG.trace("hasAccess: failed - project is not approved");
      return false;
    }
    /*
    if (thisProject.getFeatures().getMembershipRequired()) {
      // If not a member, and membership is required
      LOG.debug("hasAccess: failed - project requires membership");
      return false;
    }
    */
    // If the permissions allow for participants, return true
    if (thisProject.getFeatures().getAllowParticipants() && thisUser.getId() > 0) {
      // This user is logged in, and the project allows participants
      if (TeamMember.PARTICIPANT <= projectRoleId) {
        return true;
      }
    }
    if (thisProject.getFeatures().getAllowGuests()) {
      // If the permissions allow for guests, return true
      if (TeamMember.GUEST <= projectRoleId) {
        return true;
      }
    }
    LOG.trace("hasAccess: failed - all access conditions failed: " + permission);
    return false;
  }

  public static boolean hasPermissionAsTeamMember(int projectId, User thisUser, String permission) {
    if (thisUser == null || UserUtils.isUserDisabled(thisUser)) {
      if (thisUser == null) {
        LOG.debug("hasAccess: failed - user is null");
      } else {
        LOG.debug("hasAccess: failed - user is disabled");
      }
      return false;
    }

    // Get the project from cache
    Project thisProject = loadProject(projectId);

    int accessLevel = thisProject.getPermissions().getAccessLevel(permission);
    // The following returns a number... typically 10-100
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    int projectRoleId = roleList.getLevelFromId(accessLevel);
    if (accessLevel == -1 || projectRoleId == -1) {
      LOG.debug("hasAccess: failed - invalid role");
      return false;
    }
    // If the user is on the team, check their permission
    TeamMember teamMember = thisProject.getTeam().getTeamMember(thisUser.getId());
    if (teamMember != null && teamMember.getRoleId() <= projectRoleId) {
      return true;
    }

    return false;
  }


  public static TeamMember retrieveTeamMember(int projectId, User thisUser) {
    // Get the project from cache
    Project thisProject = loadProject(projectId);
    // Try retrieving the team member
    TeamMember teamMember = thisProject.getTeam().getTeamMember(thisUser.getId());
    if (teamMember == null) {
      // Generate a temporary team member
      if (thisUser.getAccessAdmin()) {
        // If this is an administrator of the system, give them access
        teamMember = new TeamMember();
        teamMember.setProjectId(thisProject.getId());
        teamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.PROJECT_ADMIN));
        teamMember.setRoleId(TeamMember.PROJECT_ADMIN);
        teamMember.setTemporaryAdmin(true);
      } else if (thisUser.hasContentEditorAccess(thisProject.getLanguageId()) && thisProject.getPortal()) {
        teamMember = new TeamMember();
        teamMember.setProjectId(thisProject.getId());
        teamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.PROJECT_ADMIN));
        teamMember.setRoleId(TeamMember.PROJECT_ADMIN);
        teamMember.setTemporaryAdmin(true);
      } else if (!thisProject.getFeatures().getAllowGuests() && !thisProject.getPortal()) {
        // If the project is not open to guests, then no access
        return null;
      } else if (!thisProject.getApproved()) {
        // If the project isn't approved, and you're not a team member
        return null;
        /*
      } else if (thisProject.getFeatures().getMembershipRequired()) {
        // If not a member, and membership is required
        return false;
        */
      } else if (thisProject.getApproved() && thisProject.getFeatures().getAllowParticipants() && thisUser.getId() > 0) {
        // Create a participant because the project promotes registered users to Partcipant
        teamMember = new TeamMember();
        teamMember.setProjectId(thisProject.getId());
        teamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.PARTICIPANT));
        teamMember.setRoleId(TeamMember.PARTICIPANT);
      } else if (thisProject.getApproved() && thisProject.getFeatures().getAllowGuests()) {
        // Create a guest by default because the project allows guests
        teamMember = new TeamMember();
        teamMember.setProjectId(thisProject.getId());
        teamMember.setUserLevel(UserUtils.getUserLevel(TeamMember.GUEST));
        teamMember.setRoleId(TeamMember.GUEST);
      }
    }
    return teamMember;
  }


  /**
   * Accepts a project for the given user
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @param userId    Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void accept(Connection db, int projectId, int userId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_team " +
            "SET status = ? " +
            "WHERE project_id = ? " +
            "AND user_id = ? " +
            "AND status = ? ");
    DatabaseUtils.setInt(pst, 1, TeamMember.STATUS_ACCEPTED);
    pst.setInt(2, projectId);
    pst.setInt(3, userId);
    pst.setInt(4, TeamMember.STATUS_PENDING);
    pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
  }


  /**
   * Rejects a project for the given user
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @param userId    Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void reject(Connection db, int projectId, int userId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_team " +
            "SET status = ? " +
            "WHERE project_id = ? " +
            "AND user_id = ? " +
            "AND status = ? ");
    pst.setInt(1, TeamMember.STATUS_REFUSED);
    pst.setInt(2, projectId);
    pst.setInt(3, userId);
    pst.setInt(4, TeamMember.STATUS_PENDING);
    pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
  }

  public static void formatAddress(Project thisProject) {
    // format US addresses only
    if (thisProject.getPostalCode() == null ||
        !StringUtils.hasAllowedOnly("0123456789-", thisProject.getPostalCode()) ||
        (thisProject.getPostalCode().length() != 5 &&
            thisProject.getPostalCode().length() != 10)) {
      // not a us zip so do nothing
    } else {
      if (StringUtils.hasText(thisProject.getPostalCode()) &&
          (!StringUtils.hasText(thisProject.getCity()) ||
              !StringUtils.hasText(thisProject.getState()))) {
        LocationBean location = LocationUtils.findLocationByZipCode(thisProject.getPostalCode());
        if (location != null) {
          if (!StringUtils.hasText(thisProject.getCity())) {
            thisProject.setCity(location.getCity());
          }
          if (!StringUtils.hasText(thisProject.getState())) {
            thisProject.setState(location.getState());
          }
          if (!StringUtils.hasText(thisProject.getCountry())) {
            thisProject.setCountry("UNITED STATES");
          }
          if (!StringUtils.hasText(thisProject.getAddress()) && !thisProject.isGeoCoded()) {
            // Use the zip code default
            thisProject.setLatitude(location.getLatitude());
            thisProject.setLongitude(location.getLongitude());
          }
        }
      }
    }
  }

  public static void formatPhoneNumbers(Project thisProject) {
    // business
    if (thisProject.getBusinessPhone() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getBusinessPhone(), thisProject.getBusinessPhoneExt());
      thisProject.setBusinessPhone(phone.getNumber());
      thisProject.setBusinessPhoneExt(phone.getExtension());
    }
    // business2
    if (thisProject.getBusiness2Phone() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getBusiness2Phone(), thisProject.getBusiness2PhoneExt());
      thisProject.setBusiness2Phone(phone.getNumber());
      thisProject.setBusiness2PhoneExt(phone.getExtension());
    }
    // businessFax
    if (thisProject.getBusinessFax() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getBusinessFax(), null);
      thisProject.setBusinessFax(phone.getNumber());
    }
    // home
    if (thisProject.getHomePhone() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getHomePhone(), thisProject.getHomePhoneExt());
      thisProject.setHomePhone(phone.getNumber());
      thisProject.setHomePhoneExt(phone.getExtension());
    }
    // home2
    if (thisProject.getHome2Phone() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getHome2Phone(), thisProject.getHome2PhoneExt());
      thisProject.setHome2Phone(phone.getNumber());
      thisProject.setHome2PhoneExt(phone.getExtension());
    }
    // homeFax
    if (thisProject.getHomeFax() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getHomeFax(), null);
      thisProject.setHomeFax(phone.getNumber());
    }
    // mobilePhone
    if (thisProject.getMobilePhone() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getMobilePhone(), null);
      thisProject.setMobilePhone(phone.getNumber());
    }
    // pagerNumber
    if (thisProject.getPagerNumber() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getPagerNumber(), null);
      thisProject.setPagerNumber(phone.getNumber());
    }
    // carPhone
    if (thisProject.getCarPhone() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getCarPhone(), null);
      thisProject.setCarPhone(phone.getNumber());
    }
    // radioPhone
    if (thisProject.getRadioPhone() != null) {
      PhoneNumberBean phone = PhoneNumberUtils.format(thisProject.getRadioPhone(), null);
      thisProject.setRadioPhone(phone.getNumber());
    }
  }

  public static void formatWebAddress(Project thisProject) {
    if (thisProject.getWebPage() != null) {
      String page = thisProject.getWebPage().trim().toLowerCase();
      if (page.length() == 0) {
        // turn blanks into null
        page = null;
      } else if (page.indexOf("://") == -1) {
        // prefix with a scheme
        page = "http://" + page;
      }
      thisProject.setWebPage(page);
    }
  }

  // TODO: the following really only work in the Western Hemisphere. Need to update to handle whole world coordinates.
  public static String formatLatitude(String latitude) {
    if (latitude == null || latitude.contains(".")) {
      return latitude;
    }
    if (latitude.startsWith("0")) {
      return (latitude.substring(1, 3) + "." + latitude.substring(3));
    }
    String thisLatitude;
    if (latitude.startsWith("-")) {
      // If it starts with a '-', put a decimal after the second character...
      // -36775005
      thisLatitude = "-" + latitude.substring(1, 3) + "." + latitude.substring(3);
    } else {
      // place the decimal after the second character...
      // 36775005
      thisLatitude = latitude.substring(0, 2) + "." + latitude.substring(2);
    }
    return thisLatitude;
  }

  public static String formatLongitude(String longitude) {
    if (longitude == null || longitude.contains(".")) {
      return longitude;
    }
    // Default for many importers...
    if (longitude.startsWith("0")) {
      return ("-" + longitude.substring(1, 3) + "." + longitude.substring(3));
    }

    // Assumptions:
    // The following fits standard DDMMSSAA formatting:
    // The following assume that a leading '-' is ignored.
    // If the string length is even, the first two characters are the degrees,
    // if the string length is odd, the first three characters are the degrees.
    // TODO: Do an actiua
    String tmpLongitude = longitude;
    if (tmpLongitude.startsWith("-")) {
      // Strip the minus...
      tmpLongitude = tmpLongitude.substring(1);
    }
    // Now process the value...
    String thisLongitude;
    if ((tmpLongitude.length() % 2) > 0) {
      // Odd string length, take the first three characters as degrees...
      thisLongitude = tmpLongitude.substring(0, 3) + "." + tmpLongitude.substring(3);
    } else {
      // Even string length, take the first two characters as degrees...
      thisLongitude = tmpLongitude.substring(0, 2) + "." + tmpLongitude.substring(2);
    }
    // Add the '-' back in..."
    if (longitude.startsWith("-")) {
      thisLongitude = "-" + thisLongitude;
    }
    return thisLongitude;
  }

  public static synchronized String updateUniqueId(Connection db, int projectId, String title) throws SQLException {
    if (projectId == -1) {
      throw new SQLException("ID was not specified");
    }
    if (title == null) {
      throw new SQLException("Title was not specified");
    }
    // reserve a unique text id for the project
    String uniqueId = ProjectUtils.generateUniqueId(title, projectId, db);
    PreparedStatement pst = db.prepareStatement(
        "UPDATE projects " +
            "SET projecttextid = ? " +
            "WHERE project_id = ?");
    pst.setString(1, uniqueId);
    pst.setInt(2, projectId);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, uniqueId);
    return uniqueId;
  }

  private static String generateUniqueId(String title, int projectId, Connection db) throws SQLException {
    // Title can look like...
    // Some Project Name
    // some-project-name
    // some-project-name-2

    // Format to allowed characters to get extension (some will be treated later)
    String allowed = "abcdefghijklmnopqrstuvwxyz1234567890-/& ";
    String nameToSearch = StringUtils.toAllowedOnly(allowed, title.trim().toLowerCase());
    if (!StringUtils.hasText(nameToSearch)) {
      nameToSearch = "listing";
    }

    // Break out any numbered extension: ex. name-5
    String originalExtension = null;
    int dotIndex = nameToSearch.lastIndexOf("-");
    if (dotIndex > -1 && dotIndex + 1 < nameToSearch.length()) {
      if (StringUtils.isNumber(nameToSearch.substring(dotIndex + 1))) {
        originalExtension = nameToSearch.substring(dotIndex);
        nameToSearch = nameToSearch.substring(0, dotIndex);
      }
    }

    // Convert spaces to - for url compliance and search engine readability
    nameToSearch = StringUtils.replace(nameToSearch, " ", "-");
    nameToSearch = StringUtils.replace(nameToSearch, "&", "and");
    nameToSearch = StringUtils.replace(nameToSearch, "/", "-");

    // See if there is a dupe in the database, and retrieve the latest value
    boolean originalExtensionExists = false;
    PreparedStatement pst = db.prepareStatement(
        "SELECT project_id, projecttextid " +
            "FROM projects " +
            "WHERE projecttextid LIKE ? ");
    pst.setString(1, nameToSearch + "%");
    ResultSet rs = pst.executeQuery();
    long value = 0;
    while (rs.next()) {
      long thisProjectId = rs.getLong("project_id");
      String thisTextId = rs.getString("projecttextid");
      // If it already owns this id, then keep it
      if (projectId > -1 && projectId == thisProjectId && nameToSearch.equals(thisTextId)) {
        return nameToSearch;
      }
      if (originalExtension != null) {
        if (thisTextId.equals(nameToSearch + originalExtension)) {
          originalExtensionExists = true;
        }
      }
      // Only compare to this name exactly, or this named iteration
      if (thisTextId.equals(nameToSearch)) {
        if (1 > value) {
          value = 1;
        }
      }

      if (thisTextId.startsWith(nameToSearch + "-")) {
        String foundExtensionValue = thisTextId.substring(thisTextId.lastIndexOf("-") + 1);
        if (StringUtils.isNumber(foundExtensionValue)) {
          try {
            long thisValue = Long.parseLong(foundExtensionValue);
            if (thisValue > value) {
              value = thisValue;
            }
          } catch (Exception e) {
            // The extension is big... so add another extension
            rs.close();
            pst.close();
            return generateUniqueId(nameToSearch + "-2", projectId, db);
          }
        }
      }
    }
    if (originalExtension != null && !originalExtensionExists) {
      return (nameToSearch + originalExtension);
    }
    // Set this one accordingly
    if (value == 0) {
      return nameToSearch;
    } else {
      ++value;
      return (nameToSearch + "-" + value);
    }
  }

  public static String decodeLabel(Project project, String section) {
    if (section == null) {
      return null;
    }
    // Add items that do not match their section name
    HashMap<String, String> sectionMap = new HashMap<String, String>();
    sectionMap.put("issues", "Discussion");
    sectionMap.put("file", "Documents");
    sectionMap.put("requirements", "Plan");
    sectionMap.put("assignments", "Plan");
    int sep = section.indexOf("_");
    if (sep > -1) {
      section = section.substring(0, sep);
    }
    String label = sectionMap.get(section);
    if (label != null) {
      return project.getLabel(label);
    }
    return project.getLabel(String.valueOf(section.charAt(0)).toUpperCase() + section.substring(1));
    // TODO: use translation
    //String newText = prefs.getLabel("tabbedMenu.tab." + text, language);
  }

  public static ProjectCategory loadProjectCategory(int categoryId) {
    Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_PROJECT_CATEGORY_LIST_CACHE);
    Element element = cache.get(categoryId);
    return (ProjectCategory) element.getObjectValue();
  }

  public static TagList loadProjectTags(Connection db, int projectId) throws SQLException {
    // Get popular tags for this project
    TagList popularTagList = new TagList();
    popularTagList.setTableName(Project.TABLE);
    popularTagList.setUniqueField(Project.PRIMARY_KEY);
    popularTagList.setLinkItemId(projectId);
    PagedListInfo tagListInfo = new PagedListInfo();
    tagListInfo.setColumnToSortBy("tag_count DESC, tag");
    popularTagList.setPagedListInfo(tagListInfo);
    popularTagList.buildList(db);
    return popularTagList;
  }

  public static TagLogList loadProjectTagsForUser(Connection db, int projectId, int userId) throws SQLException {
    // Get the user's tags for this project
    TagLogList tagLogList = new TagLogList();
    tagLogList.setTableName(Project.TABLE);
    tagLogList.setUniqueField(Project.PRIMARY_KEY);
    tagLogList.setUserId(userId);
    tagLogList.setLinkItemId(projectId);
    tagLogList.buildList(db);
    return tagLogList;
  }

  public static boolean datesDiffer(Timestamp a, Timestamp b) {
    if (a == null && b == null) {
      return false;
    }
    if (a != null && b == null) {
      return true;
    }
    if (a == null) {
      return true;
    }
    return !a.equals(b);
  }
}
