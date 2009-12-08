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

package com.concursive.connect.web.modules.members.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Represents a list of members of a project
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 23, 2001
 */
public class TeamMemberList extends ArrayList<TeamMember> {

  private PagedListInfo pagedListInfo = null;
  private String emptyHtmlSelectRecord = null;
  private int projectId = -1;
  private int categoryId = -1;
  private int userLevel = -1;
  private int roleLevel = -1;
  private int minimumRoleLevel = -1;
  private String insertMembers = null;
  private String deleteMembers = null;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private int forProjectUser = -1;
  private int userId = -1;
  private int status = -2;
  private boolean tools = false;
  private boolean buildProject = false;
  private int withNotificationsSet = Constants.UNDEFINED;


  /**
   * Constructor for the TeamMemberList object
   */
  public TeamMemberList() {
  }


  /**
   * Sets the pagedListInfo attribute of the TeamMemberList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the emptyHtmlSelectRecord attribute of the TeamMemberList object
   *
   * @param tmp The new emptyHtmlSelectRecord value
   */
  public void setEmptyHtmlSelectRecord(String tmp) {
    this.emptyHtmlSelectRecord = tmp;
  }


  /**
   * Sets the projectId attribute of the TeamMemberList object
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
   * @return the categoryId
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }


  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
  }


  /**
   * Sets the userLevel attribute of the TeamMemberList object
   *
   * @param tmp The new userLevel value
   */
  public void setUserLevel(int tmp) {
    this.userLevel = tmp;
  }


  /**
   * Sets the userLevel attribute of the TeamMemberList object
   *
   * @param tmp The new userLevel value
   */
  public void setUserLevel(String tmp) {
    this.userLevel = Integer.parseInt(tmp);
  }


  /**
   * Sets the roleLevel attribute of the TeamMemberList object
   *
   * @param tmp The new roleLevel value
   */
  public void setRoleLevel(int tmp) {
    this.roleLevel = tmp;
  }


  public void setMinimumRoleLevel(int minimumRoleLevel) {
    this.minimumRoleLevel = minimumRoleLevel;
  }


  public void setRoleLevel(String tmp) {
    this.roleLevel = Integer.parseInt(tmp);
  }


  /**
   * Sets the enteredBy attribute of the TeamMemberList object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }

  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the TeamMemberList object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the insertMembers attribute of the TeamMemberList object
   *
   * @param tmp The new insertMembers value
   */
  public void setInsertMembers(String tmp) {
    insertMembers = tmp;
  }


  /**
   * Sets the deleteMembers attribute of the TeamMemberList object
   *
   * @param tmp The new deleteMembers value
   */
  public void setDeleteMembers(String tmp) {
    deleteMembers = tmp;
  }


  /**
   * Sets the forProjectUser attribute of the TeamMemberList object
   *
   * @param tmp The new forProjectUser value
   */
  public void setForProjectUser(int tmp) {
    this.forProjectUser = tmp;
  }


  public void setForProjectUser(String tmp) {
    this.forProjectUser = Integer.parseInt(tmp);
  }


  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }

  public int getSize() {
    return this.size();
  }

  /**
   * Description of the Method
   *
   * @param thisId Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean hasUserId(int thisId) {
    for (TeamMember thisMember : this) {
      if (thisMember.getUserId() == thisId) {
        return true;
      }
    }
    return false;
  }


  /**
   * @return the status
   */
  public int getStatus() {
    return status;
  }


  /**
   * @param status the status to set
   */
  public void setStatus(int status) {
    this.status = status;
  }

  public void setStatus(String status) {
    this.status = Integer.parseInt(status);
  }


  /**
   * @return the tools
   */
  public boolean getTools() {
    return tools;
  }


  /**
   * @param tools the tools to set
   */
  public void setTools(boolean tools) {
    this.tools = tools;
  }


  /**
   * @param tools the tools to set
   */
  public void setTools(String tools) {
    this.tools = DatabaseUtils.parseBoolean(tools);
  }


  /**
   * @return the buildProject
   */
  public boolean getBuildProject() {
    return buildProject;
  }


  /**
   * @param buildProject the buildProject to set
   */
  public void setBuildProject(boolean buildProject) {
    this.buildProject = buildProject;
  }


  public void setBuildProject(String buildProject) {
    this.buildProject = DatabaseUtils.parseBoolean(buildProject);
  }


  public int getWithNotificationsSet() {
    return withNotificationsSet;
  }

  public void setWithNotificationsSet(int withNotificationsSet) {
    this.withNotificationsSet = withNotificationsSet;
  }

  public void setWithNotificationsSet(String test) {
    this.withNotificationsSet = DatabaseUtils.parseBooleanToConstant(test);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    // Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_team t " +
            "WHERE t.project_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(-1);
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
    // Determine the offset, based on the filter, for the first record to show
    if (!pagedListInfo.getCurrentLetter().equals("")) {
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString() +
          "AND project_id < ? ");
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
    // Determine column to sort by
    pagedListInfo.setDefaultSort("t.project_id, r.level, last_name", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    // Need to build a base SQL statement for returning records
    pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    sqlSelect.append(
        "t.team_id, t.project_id, t.user_id, t.userlevel, t.entered, t.enteredby, " +
            "t.modified, t.modifiedby, t.status, t.last_accessed, " +
            "t.tools, t.notification, t.email_updates_schedule, " +
            "r.level " +
            "FROM project_team t, lookup_project_role r, users u " +
            "WHERE t.userlevel = r.code " +
            "AND t.user_id = u.user_id ");
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
      TeamMember thisTeamMember = new TeamMember(rs);
      if (buildProject) {
        thisTeamMember.setProject(ProjectUtils.loadProject(thisTeamMember.getProjectId()));
      }

      this.add(thisTeamMember);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (projectId > -1) {
      sqlFilter.append("AND project_id = ? ");
    }
    if (categoryId > -1) {
      sqlFilter.append("AND project_id IN (SELECT DISTINCT project_id FROM projects WHERE category_id = ?) ");
    }
    if (forProjectUser > -1) {
      sqlFilter.append("AND project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) ");
    }
    if (roleLevel > -1) {
      sqlFilter.append("AND t.userlevel IN (SELECT code FROM lookup_project_role WHERE level = ?) ");
    }
    if (minimumRoleLevel > -1) {
      sqlFilter.append("AND t.userlevel IN (SELECT code FROM lookup_project_role WHERE level <= ?) ");
    }
    if (userId > -1) {
      sqlFilter.append("AND t.user_id = ? ");
    }
    if (status > -2) {
      if (status == -1) {
        sqlFilter.append("AND t.status IS NULL ");
      } else {
        sqlFilter.append("AND t.status = ? ");
      }
    }
    if (withNotificationsSet != Constants.UNDEFINED) {
      sqlFilter.append("AND t.notification = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (forProjectUser > -1) {
      pst.setInt(++i, forProjectUser);
    }
    if (roleLevel > -1) {
      pst.setInt(++i, roleLevel);
    }
    if (minimumRoleLevel > -1) {
      pst.setInt(++i, minimumRoleLevel);
    }
    if (userId > -1) {
      pst.setInt(++i, userId);
    }
    if (status > -2) {
      if (status == -1) {
        //Do nothing
      } else {
        pst.setInt(++i, status);
      }
    }
    if (withNotificationsSet != Constants.UNDEFINED) {
      pst.setBoolean(++i, withNotificationsSet == Constants.TRUE);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db           Description of the Parameter
   * @param masterUserId Description of the Parameter
   * @param groupId      Description of the Parameter
   * @param addedUsers   Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean update(Connection db, int masterUserId, int groupId, ArrayList addedUsers) throws SQLException {
    try {
      db.setAutoCommit(false);
      //Add new members
      if (insertMembers != null && (!insertMembers.equals("")) && projectId > -1) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("TeamMemberList-> New: " + insertMembers);
        }
        StringTokenizer items = new StringTokenizer(insertMembers, "|");
        while (items.hasMoreTokens()) {
          int itemId = -1;
          String itemIdValue = items.nextToken();
          boolean byEmail = false;
          if (itemIdValue.indexOf("@") > 0) {
            itemId = User.getIdByEmailAddress(db, itemIdValue);
            if (itemId > -1) {
              byEmail = true;
            }
          } else {
            itemId = Integer.parseInt(itemIdValue);
          }
          if (itemId == -1) {
            //If not, add them to the TeamMemberList...
            //The lead will be asked whether to send an email and invite and to
            //enter their name.
            TeamMember member = new TeamMember();
            User user = new User();
            user.setEmail(itemIdValue);
            member.setUser(user);
            this.add(member);
          } else {
            // See if ID is already on the team
            if (!isOnTeam(db, projectId, itemId)) {
              // Insert the member
              PreparedStatement pst = db.prepareStatement(
                  "INSERT INTO project_team " +
                      "(project_id, user_id, userlevel, enteredby, modifiedby, status, tools) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?) ");
              int i = 0;
              pst.setInt(++i, projectId);
              pst.setInt(++i, itemId);
              DatabaseUtils.setInt(pst, ++i, userLevel);
              pst.setInt(++i, enteredBy);
              pst.setInt(++i, modifiedBy);
              pst.setInt(++i, TeamMember.STATUS_PENDING);
              pst.setBoolean(++i, tools);
              pst.execute();
              pst.close();
              // Existing user will be sent an email
              User user = UserUtils.loadUser(itemId);
              addedUsers.add(user);
            }
          }
        }
      }
      //Removed deleted members
      if ((deleteMembers != null) && (!deleteMembers.equals("")) && projectId > -1) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("TeamMemberList-> Del: " + deleteMembers);
        }
        //Delete everyone but self
        StringTokenizer items = new StringTokenizer(deleteMembers, "|");
        while (items.hasMoreTokens()) {
          String itemId = items.nextToken();
          if (Integer.parseInt(itemId) != modifiedBy) {
            PreparedStatement pst = db.prepareStatement(
                "DELETE FROM project_team " +
                    "WHERE project_id = ? " +
                    "AND user_id = ?");
            pst.setInt(1, projectId);
            pst.setInt(2, Integer.parseInt(itemId));
            pst.execute();
            pst.close();
          }
        }
      }
      db.commit();
      db.setAutoCommit(true);
    } catch (SQLException e) {
      db.rollback();
      db.setAutoCommit(true);
      throw new SQLException(e.getMessage());
    } finally {
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    for (TeamMember thisMember : this) {
      thisMember.setId(-1);
      thisMember.setProjectId(projectId);
      thisMember.setEnteredBy(enteredBy);
      thisMember.setModifiedBy(modifiedBy);
      thisMember.setEntered((Timestamp) null);
      thisMember.setModified((Timestamp) null);
      thisMember.setLastAccessed((Timestamp) null);
      thisMember.setStatus(TeamMember.STATUS_PENDING);
      thisMember.insert(db);
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void delete(Connection db) throws SQLException {
    for (TeamMember thisMember : this) {
      thisMember.delete(db);
    }
  }


  /**
   * Gets the onTeam attribute of the TeamMemberList object
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @param userId    Description of the Parameter
   * @return The onTeam value
   * @throws SQLException Description of the Exception
   */
  public static boolean isOnTeam(Connection db, int projectId, int userId) throws SQLException {
    boolean exists = false;
    PreparedStatement pst = db.prepareStatement(
        "SELECT userlevel " +
            "FROM project_team " +
            "WHERE project_id = ? " +
            "AND user_id = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, userId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      exists = true;
    }
    rs.close();
    pst.close();
    return exists;
  }


  /**
   * Gets the userRelated attribute of the TeamMemberList class
   *
   * @param db            Description of the Parameter
   * @param masterUserId  Description of the Parameter
   * @param userIdToCheck Description of the Parameter
   * @return The userRelated value
   * @throws SQLException Description of the Exception
   */
  public static boolean isUserRelated(Connection db, int masterUserId, int userIdToCheck) throws SQLException {
    boolean exists = false;
    PreparedStatement pst = db.prepareStatement(
        "SELECT p1.project_id " +
            "FROM project_team p1, project_team p2 " +
            "WHERE p1.project_id = p2.project_id " +
            "AND p1.user_id = ? " +
            "AND p2.user_id = ? ");
    pst.setInt(1, masterUserId);
    pst.setInt(2, userIdToCheck);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      exists = true;
    }
    rs.close();
    pst.close();
    return exists;
  }

  public TeamMember getTeamMember(int userId) {
    for (TeamMember thisMember : this) {
      if (thisMember.getUserId() == userId) {
        return thisMember;
      }
    }
    return null;
  }

  public void removeTeamMember(int userId) {
    Iterator team = this.iterator();
    while (team.hasNext()) {
      TeamMember thisMember = (TeamMember) team.next();
      if (thisMember.getUserId() == userId) {
        team.remove();
        break;
      }
    }
  }

  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_team t " +
            "WHERE t.user_id > -1 ");
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

  public static int queryProjectIdOfTeamMemberId(Connection db, int id) throws SQLException {
    int projectId = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT project_id " +
            "FROM project_team " +
            "WHERE team_id = ? "
    );
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      projectId = rs.getInt("project_id");
    }
    rs.close();
    pst.close();
    return projectId;
  }

}

