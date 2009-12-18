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
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.communications.utils.EmailUpdatesUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.LookupList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;

/**
 * Represents a member of a project
 *
 * @author matt rajkowski
 * @version $Id$
 * @created July 23, 2001
 */
public class TeamMember extends GenericBean {
  // NOTE: Constants that control permissions within a project are driven by
  // the database, however some point-level solutions have been defined...
  // PROJECT_ADMIN = Total access to the project
  public final static int PROJECT_ADMIN = 10;
  // MANAGER = Assumed to have a lot of control similar to admin
  public final static int MANAGER = 14;
  // CHAMPION = Granted permission to create project structures
  public final static int CHAMPION = 17;
  // VIP = Acknowledged by project owners
  public final static int VIP = 20;
  // MEMBER = Status when a user self joins a project, when allowed
  public final static int MEMBER = 25;
  // PARTICIPANT = Logged in and auto-promoted by project
  public final static int PARTICIPANT = 30;
  // Permission used when user is either not logged in or not promoted by project
  public final static int GUEST = 100;
  //Constants that control invitations
  public final static int STATUS_ADDED = -1;
  public final static int STATUS_INVITING = 1;
  public final static int STATUS_MAILERROR = 2;
  public final static int STATUS_PENDING = 3;
  public final static int STATUS_REFUSED = 4;
  public final static int STATUS_JOINED_NEEDS_APPROVAL = 6;
  //Scheduled Email Updates
  public final static int EMAIL_NEVER = 0;
  public final static int EMAIL_OFTEN = 1;
  public final static int EMAIL_DAILY = 2;
  public final static int EMAIL_WEEKLY = 3;
  public final static int EMAIL_MONTHLY = 4;

  private Object contact = null;
  private User user = null;

  // Team Member Properties
  private int id = -1;
  private int projectId = -1;
  private int userId = -1;
  private int userLevel = -1;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  private int roleId = -1;
  private int status = STATUS_ADDED;
  private java.sql.Timestamp lastAccessed = null;
  private boolean tools = false;

  // Other factors
  private boolean temporaryAdmin = false;
  private boolean notification = false;
  private String customInvitationMessage = null;
  private int emailUpdatesSchedule = EMAIL_NEVER;

  /**
   * Constructor for the Assignment object
   */
  public TeamMember() {
  }


  /**
   * Constructor for the TeamMember object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public TeamMember(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the TeamMember object
   *
   * @param db           Description of the Parameter
   * @param teamMemberId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public TeamMember(Connection db, int teamMemberId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT t.team_id, t.project_id, t.user_id, t.userlevel, t.entered, t.enteredby, " +
            "t.modified, t.modifiedby, t.status, t.last_accessed, " +
            "t.tools, t.notification, t.email_updates_schedule, " +
            "r.level " +
            "FROM project_team t, lookup_project_role r " +
            "WHERE t.userlevel = r.code " +
            "AND t.team_id = ?");
    pst.setInt(1, teamMemberId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    } else {
      rs.close();
      pst.close();
      throw new SQLException("Member record not found.");
    }
    rs.close();
    pst.close();
  }


  /**
   * This method is typically used to see if a user is member of a specific
   * project.
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @param userId    Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public TeamMember(Connection db, int projectId, int userId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT t.team_id, t.project_id, t.user_id, t.userlevel, t.entered, t.enteredby, " +
            "t.modified, t.modifiedby, t.status, t.last_accessed, " +
            "t.tools, t.notification, t.email_updates_schedule, " +
            "r.level " +
            "FROM project_team t, lookup_project_role r " +
            "WHERE t.userlevel = r.code " +
            "AND t.project_id = ? " +
            "AND t.user_id = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, userId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    } else {
      rs.close();
      pst.close();
      throw new SQLException("Member record not found.");
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    // project_team
    id = rs.getInt("team_id");
    projectId = rs.getInt("project_id");
    userId = rs.getInt("user_id");
    userLevel = DatabaseUtils.getInt(rs, "userlevel");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    status = DatabaseUtils.getInt(rs, "status");
    lastAccessed = rs.getTimestamp("last_accessed");
    tools = rs.getBoolean("tools");
    notification = rs.getBoolean("notification");
    emailUpdatesSchedule = DatabaseUtils.getInt(rs, "email_updates_schedule");
    // lookup_project_role
    roleId = rs.getInt("level");
  }


  /**
   * Sets the contact attribute of the TeamMember object
   *
   * @param tmp The new contact value
   */
  public void setContact(Object tmp) {
    this.contact = tmp;
  }


  /**
   * Sets the user attribute of the TeamMember object
   *
   * @param tmp The new user value
   */
  public void setUser(User tmp) {
    this.user = tmp;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }


  /**
   * Sets the projectId attribute of the TeamMember object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the TeamMember object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the userId attribute of the TeamMember object
   *
   * @param tmp The new userId value
   */
  public void setUserId(int tmp) {
    this.userId = tmp;
  }


  /**
   * Sets the userId attribute of the TeamMember object
   *
   * @param tmp The new userId value
   */
  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Sets the userLevel attribute of the TeamMember object
   *
   * @param tmp The new userLevel value
   */
  public void setUserLevel(int tmp) {
    this.userLevel = tmp;
  }


  /**
   * Sets the userLevel attribute of the TeamMember object
   *
   * @param tmp The new userLevel value
   */
  public void setUserLevel(String tmp) {
    this.userLevel = Integer.parseInt(tmp);
  }


  /**
   * Sets the entered attribute of the TeamMember object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the TeamMember object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the TeamMember object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the TeamMember object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the TeamMember object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the TeamMember object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the TeamMember object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the TeamMember object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the roleId attribute of the TeamMember object
   *
   * @param tmp The new roleId value
   */
  public void setRoleId(int tmp) {
    this.roleId = tmp;
  }


  /**
   * Sets the roleId attribute of the TeamMember object
   *
   * @param tmp The new roleId value
   */
  public void setRoleId(String tmp) {
    this.roleId = Integer.parseInt(tmp);
  }


  /**
   * Sets the status attribute of the TeamMember object
   *
   * @param tmp The new status value
   */
  public void setStatus(int tmp) {
    this.status = tmp;
  }


  /**
   * Sets the status attribute of the TeamMember object
   *
   * @param tmp The new status value
   */
  public void setStatus(String tmp) {
    this.status = Integer.parseInt(tmp);
  }

  public int getEmailUpdatesSchedule() {
    return emailUpdatesSchedule;
  }

  public void setEmailUpdatesSchedule(int emailUpdatesSchedule) {
    this.emailUpdatesSchedule = emailUpdatesSchedule;
  }

  public void setEmailUpdatesSchedule(String emailUpdatesSchedule) {
    this.emailUpdatesSchedule = Integer.parseInt(emailUpdatesSchedule);
  }

  /**
   * Sets the lastAccessed attribute of the TeamMember object
   *
   * @param tmp The new lastAccessed value
   */
  public void setLastAccessed(java.sql.Timestamp tmp) {
    this.lastAccessed = tmp;
  }

  public void setLastAccessed(String tmp) {
    this.lastAccessed = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the contact attribute of the TeamMember object
   *
   * @return The contact value
   */
  public Object getContact() {
    return contact;
  }


  /**
   * Gets the user attribute of the TeamMember object
   *
   * @return The user value
   */
  public User getUser() {
    if (user == null && userId > -1) {
      return UserUtils.loadUser(userId);
    }
    return user;
  }


  /**
   * Gets the projectId attribute of the TeamMember object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Gets the userId attribute of the TeamMember object
   *
   * @return The userId value
   */
  public int getUserId() {
    return userId;
  }


  /**
   * Gets the userLevel attribute of the TeamMember object
   *
   * @return The userLevel value
   */
  public int getUserLevel() {
    return userLevel;
  }


  /**
   * Gets the entered attribute of the TeamMember object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredString attribute of the TeamMember object
   *
   * @return The enteredString value
   */
  public String getEnteredString() {
    try {
      return DateFormat.getDateInstance(3).format(entered);
    } catch (NullPointerException e) {
    }
    return ("--");
  }


  /**
   * Gets the enteredBy attribute of the TeamMember object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the TeamMember object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedString attribute of the TeamMember object
   *
   * @return The modifiedString value
   */
  public String getModifiedString() {
    try {
      return DateFormat.getDateInstance(3).format(modified);
    } catch (NullPointerException e) {
    }
    return ("--");
  }


  /**
   * Gets the modifiedBy attribute of the TeamMember object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the roleId attribute of the TeamMember object
   *
   * @return The roleId value
   */
  public int getRoleId() {
    return roleId;
  }


  /**
   * Gets the status attribute of the TeamMember object
   *
   * @return The status value
   */
  public int getStatus() {
    return status;
  }


  /**
   * Gets the lastAccessed attribute of the TeamMember object
   *
   * @return The lastAccessed value
   */
  public java.sql.Timestamp getLastAccessed() {
    return lastAccessed;
  }


  /**
   * Gets the lastAccessedString attribute of the TeamMember object
   *
   * @return The lastAccessedString value
   */
  public String getLastAccessedString() {
    try {
      return DateFormat.getDateInstance(3).format(lastAccessed);
    } catch (NullPointerException e) {
    }
    return ("--");
  }

  public boolean isTemporaryAdmin() {
    return temporaryAdmin;
  }

  public void setTemporaryAdmin(boolean temporaryAdmin) {
    this.temporaryAdmin = temporaryAdmin;
  }

  public void setTemporaryAdmin(String tmp) {
    this.temporaryAdmin = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getNotification() {
    return notification;
  }

  public void setNotification(String tmp) {
    this.notification = DatabaseUtils.parseBoolean(tmp);
  }

  public void setNotification(boolean notification) {
    this.notification = notification;
  }

  public boolean getTools() {
    return tools;
  }

  public void setTools(boolean tools) {
    this.tools = tools;
  }

  public void setTools(String tmp) {
    this.tools = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * @return the customInvitationMessage
   */
  public String getCustomInvitationMessage() {
    return customInvitationMessage;
  }


  /**
   * @param customInvitationMessage the customInvitationMessage to set
   */
  public void setCustomInvitationMessage(String customInvitationMessage) {
    this.customInvitationMessage = customInvitationMessage;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO project_team ");
      sql.append("(" + (id > -1 ? "team_id, " : "") + "project_id, user_id, userlevel, tools, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      if (lastAccessed != null) {
        sql.append("last_accessed, ");
      }
      sql.append("enteredby, modifiedby, status, notification, email_updates_schedule) ");
      sql.append("VALUES (?, ?, ?, ?, ");
      if (id > -1) {
        sql.append("?, ");
      }
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      if (lastAccessed != null) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?, ?, ?) ");
      PreparedStatement pst = db.prepareStatement(sql.toString());
      int i = 0;
      if (id > -1) {
        pst.setInt(++i, id);
      }
      pst.setInt(++i, projectId);
      pst.setInt(++i, userId);
      DatabaseUtils.setInt(pst, ++i, userLevel);
      pst.setBoolean(++i, tools);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      if (lastAccessed != null) {
        pst.setTimestamp(++i, lastAccessed);
      }
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      DatabaseUtils.setInt(pst, ++i, status);
      pst.setBoolean(++i, notification);
      DatabaseUtils.setInt(pst, ++i, emailUpdatesSchedule);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_team_team_id_seq", id);

      //Update the scheduled emails queue
      EmailUpdatesUtils.saveQueue(db, this);

      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      db.setAutoCommit(true);
    }
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    return true;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE project_team ");
    sql.append("SET modified = CURRENT_TIMESTAMP, ");
    if (lastAccessed != null) {
      sql.append("last_accessed = ?, ");
    }
    sql.append("modifiedby = ?, status = ?, userlevel = ?, tools = ?, notification = ?, email_updates_schedule = ? ");
    sql.append("WHERE team_id = ? AND modified = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    if (lastAccessed != null) {
      pst.setTimestamp(++i, lastAccessed);
    }
    pst.setInt(++i, modifiedBy);
    DatabaseUtils.setInt(pst, ++i, status);
    DatabaseUtils.setInt(pst, ++i, userLevel);
    pst.setBoolean(++i, tools);
    pst.setBoolean(++i, notification);
    DatabaseUtils.setInt(pst, ++i, emailUpdatesSchedule);
    pst.setInt(++i, id);
    pst.setTimestamp(++i, modified);
    int resultCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    return resultCount;
  }


  /**
   * Deletes a user from a team
   *
   * @param db Database connection
   * @return true if any records were deleted, otherwise false
   * @throws SQLException Any database connection or statement error
   */
  public boolean delete(Connection db) throws SQLException {
    int resultCount = 0;
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }
      int projectId = TeamMemberList.queryProjectIdOfTeamMemberId(db, id);
      PreparedStatement pst = db.prepareStatement(
              "DELETE FROM project_team " +
                      "WHERE team_id = ? ");
      pst.setInt(1, id);
      resultCount = pst.executeUpdate();
      pst.close();

      //Manage the scheduled emails queue
      EmailUpdatesUtils.manageQueue(db, this);

      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      db.setAutoCommit(true);
    }
    return (resultCount > 0);
  }


  /**
   * Description of the Method
   *
   * @param db                 The database connection used
   * @param project            The project in which the change is being made
   * @param userIdMakingChange The user making the change
   * @param userId             The target user being changed
   * @param newUserLevel       The id of the role the target user is being changed to
   * @return True if the change occurred
   * @throws SQLException Description of the Exception
   */
  public static boolean changeRole(Connection db, Project project, int userIdMakingChange, int userId, int newUserLevel) throws SQLException {
    // Prepare to enforce a few rules
    TeamMember memberMakingChange = project.getTeam().getTeamMember(userIdMakingChange);
    TeamMember memberBeingChanged = project.getTeam().getTeamMember(userId);
    LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
    int newRowLevel = roleList.getLevelFromId(newUserLevel);

    // Leverage the user's record whether on the team or not
    User thisUser = UserUtils.loadUser(userIdMakingChange);

    // Determine if the role can be changed
    if ((memberMakingChange != null && memberMakingChange.getUser() != null && memberMakingChange.getUser().getAccessAdmin()) ||
        (thisUser != null && thisUser.getAccessAdmin())) {
      // This is an administrator and can make the change
    } else {
      // This is not an administrator so make sure the user is on the team and has
      // access to make the change
      if (memberMakingChange == null) {
        return false;
      }

      // A team member cannot make themself worse or better
      if (memberMakingChange.getUserId() == memberBeingChanged.getUserId()) {
        return false;
      }

      // The team member making the change cannot change the role of another member to something better than themself
      if (newRowLevel < memberMakingChange.getRoleId()) {
        return false;
      }

      // The team member cannot make a better team member, worse
      if (memberBeingChanged.getRoleId() < memberMakingChange.getRoleId()) {
        return false;
      }
    }

    // Now update the team
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_team " +
            "SET userlevel = ? " +
            "WHERE project_id = ? " +
            "AND user_id = ? ");
    pst.setInt(1, newUserLevel);
    pst.setInt(2, project.getId());
    pst.setInt(3, userId);
    pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, project.getId());
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void updateStatus(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_team " +
            "SET status = ?, modified = " + DatabaseUtils.getCurrentTimestamp(db) + " " +
            "WHERE project_id = ? " +
            "AND user_id = ? ");
    DatabaseUtils.setInt(pst, 1, status);
    pst.setInt(2, projectId);
    pst.setInt(3, userId);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
  }


  public void updateTools(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_team " +
            "SET tools = ?, modified = " + DatabaseUtils.getCurrentTimestamp(db) + " " +
            "WHERE project_id = ? " +
            "AND user_id = ? ");
    pst.setBoolean(1, tools);
    pst.setInt(2, projectId);
    pst.setInt(3, userId);
    pst.execute();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @param user      Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void updateLastAccessed(Connection db, int projectId, User user) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_team " +
            "SET last_accessed = " + DatabaseUtils.getCurrentTimestamp(db) + " " +
            "WHERE project_id = ? " +
            "AND user_id = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, user.getId());
    pst.execute();
    pst.close();
    user.addRecentProject(projectId);
  }

  public static boolean handleMembershipRequest(Connection db, TeamMember teamMember, boolean approval, int userId) throws SQLException {
    boolean success = false;
    if (approval) {
      LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
      teamMember.setUserLevel(roleList.getIdFromLevel(TeamMember.MEMBER));
      teamMember.setStatus(TeamMember.STATUS_ADDED);
      teamMember.setModifiedBy(userId);
      success = (teamMember.update(db) == 1);
    } else {
      teamMember.setStatus(TeamMember.STATUS_REFUSED);
      success = teamMember.delete(db);
    }
    return success;
  }


  public Project getProject() {
    if (projectId > -1) {
      return ProjectUtils.loadProject(projectId);
    } else {
      return null;
    }
  }


  public Project getTeamMemberProfile() {
    return UserUtils.loadUser(userId).getProfileProject();
  }
}


