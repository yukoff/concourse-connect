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
package com.concursive.connect.web.modules.login.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.admin.beans.UserSearchBean;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A collection of User objects
 *
 * @author matt rajkowski
 * @created April 30, 2003
 */
public class UserList extends ArrayList<User> {

  private int userId = -1;
  private int groupId = -1;
  private int departmentId = -1;
  private int validUser = Constants.UNDEFINED;
  private int watchForums = Constants.UNDEFINED;
  private PagedListInfo pagedListInfo = null;
  private UserSearchBean searchCriteria = null;
  private String username = null;
  private String guid = null;
  private boolean firstCriteria = true;
  private int isAdmin = Constants.UNDEFINED;
  private String userIds = null;
  private String[] userIdArray = null;

  public UserList() {
  }

  public int getUserId() {
    return userId;
  }

  public void setId(int userId) {
    this.userId = userId;
  }

  public void setId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void setUserId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }

  public void setEmail(String email) {
    getSearchCriteria().setEmail(email);
  }

  public void setFirstName(String firstName) {
    getSearchCriteria().setFirstName(firstName);
  }

  public void setLastName(String lastName) {
    getSearchCriteria().setLastName(lastName);
  }

  public void setCompany(String company) {
    getSearchCriteria().setCompany(company);
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Sets the groupId attribute of the UserList object
   *
   * @param tmp The new groupId value
   */
  public void setGroupId(int tmp) {
    this.groupId = tmp;
  }

  /**
   * Sets the departmentId attribute of the UserList object
   *
   * @param tmp The new departmentId value
   */
  public void setDepartmentId(int tmp) {
    this.departmentId = tmp;
  }

  /**
   * Sets the pagedListInfo attribute of the UserList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }

  public UserSearchBean getSearchCriteria() {
    if (searchCriteria == null) {
      searchCriteria = new UserSearchBean();
    }
    return searchCriteria;
  }

  /**
   * Sets the searchCriteria attribute of the UserList object
   *
   * @param tmp The new searchCriteria value
   */
  public void setSearchCriteria(UserSearchBean tmp) {
    this.searchCriteria = tmp;
  }

  /**
   * Gets the groupId attribute of the UserList object
   *
   * @return The groupId value
   */
  public int getGroupId() {
    return groupId;
  }

  /**
   * Gets the departmentId attribute of the UserList object
   *
   * @return The departmentId value
   */
  public int getDepartmentId() {
    return departmentId;
  }

  public void setWatchForums(int watchForums) {
    this.watchForums = watchForums;
  }

  public void setValidUser(int validUser) {
    this.validUser = validUser;
  }

  public void setValidUser(String tmp) {
    validUser = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public int getAdmin() {
    return isAdmin;
  }

  public void setAdmin(int admin) {
    isAdmin = admin;
  }

  /**
   * Gets the user attribute of the UserList object
   *
   * @param userId Description of the Parameter
   * @return The user value
   */
  public User getUser(int userId) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      User thisUser = (User) i.next();
      if (thisUser.getId() == userId) {
        return thisUser;
      }
    }
    return null;
  }

  /**
   * @return the userIds
   */
  public String getUserIds() {
    return userIds;
  }

  /**
   * @param userIds the userIds to set
   */
  public void setUserIds(String userIds) {
    this.userIds = userIds;

    userIdArray = userIds.split(",");
  }

  public int getSize() {
    return this.size();
  }

  /**
   * builds a list of user objects based on configured properties
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
    // Build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
        "FROM users u " +
        "WHERE u.user_id > -1 ");
    createFilter(sqlFilter, db);
    if (pagedListInfo != null) {
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
            "AND u.last_name > ? ");
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
      pagedListInfo.setDefaultSort("u.last_name, u.first_name", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY u.last_name, u.first_name ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "u.*, d.description as department " +
        "FROM users u LEFT JOIN departments d ON (u.department_id = d.code) " +
        "WHERE u.user_id > -1 ");
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
      User thisUser = new User(rs);
      this.add(thisUser);
    }
    rs.close();
    pst.close();
  }

  /**
   * adds conditions to the sql query
   *
   * @param sqlFilter Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter, Connection db) throws SQLException {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (userId > -1) {
      sqlFilter.append("AND u.user_id = ? ");
    }
    if (groupId > -1) {
      sqlFilter.append("AND u.group_id = ? ");
    }
    if (departmentId > -1) {
      sqlFilter.append("AND u.department_id = ? ");
    }
    if (watchForums != Constants.UNDEFINED) {
      sqlFilter.append("AND u.watch_forums = ? ");
    }
    if (validUser == Constants.TRUE) {
      sqlFilter.append("AND u.enabled = ? AND (u.expiration > ? OR u.expiration IS NULL) ");
    }
    if (validUser == Constants.FALSE) {
      sqlFilter.append("AND (u.enabled = ? OR (u.expiration <= ? AND u.expiration IS NOT NULL)) ");
    }
    if (username != null) {
      sqlFilter.append("AND lower(u.username) LIKE ? ");
    }
    if (userIdArray != null && userIdArray.length > 0) {
      sqlFilter.append("AND u.user_id IN ( ");
      int count = 0;
      while (count < userIdArray.length) {
        sqlFilter.append("?");
        count++;
        if (count < userIdArray.length) {
          sqlFilter.append(", ");
        }
      }
      sqlFilter.append(") ");
    }
    // Check all valid search criteria
    if (searchCriteria != null) {
      if (StringUtils.hasText(searchCriteria.getEmail())) {
        sqlFilter.append("AND lower(u.email) LIKE ? ");
      }
      if (StringUtils.hasText(searchCriteria.getFirstName())) {
        sqlFilter.append("AND lower(u.first_name) LIKE ? ");
      }
      if (StringUtils.hasText(searchCriteria.getLastName())) {
        sqlFilter.append("AND lower(u.last_name) LIKE ? ");
      }
      if (StringUtils.hasText(searchCriteria.getCompany())) {
        sqlFilter.append("AND lower(u.company) LIKE ? ");
      }
      if (StringUtils.hasText(searchCriteria.getName())) {
        sqlFilter.append("AND (lower(u.first_name) LIKE ? OR lower(u.last_name) LIKE ?) ");
      }
      if (searchCriteria.getEnabled() != Constants.UNDEFINED) {
        sqlFilter.append("AND u.enabled = ? ");
      }
      if (searchCriteria.getRegistered() != Constants.UNDEFINED) {
        sqlFilter.append("AND u.registered = ? ");
      }
      if (searchCriteria.getExpired() != Constants.UNDEFINED) {
        if (searchCriteria.getExpired() == Constants.TRUE) {
          sqlFilter.append("AND u.expiration <= ? ");
          sqlFilter.append("AND u.expiration IS NOT NULL ");
        } else {
          sqlFilter.append("AND (u.expiration > ? OR u.expiration IS NULL) ");
        }
      }
      if (searchCriteria.getAdmin() != Constants.UNDEFINED) {
        sqlFilter.append("AND u.access_admin = ? ");
      }
      if (searchCriteria.getContentEditor() != Constants.UNDEFINED) {
        sqlFilter.append("AND u.access_content_editor = ? ");
      }
      //Process multiple criteria
      if (StringUtils.hasText(searchCriteria.getActiveProject())) {
        Project activeProject = ProjectUtils.loadProject(searchCriteria.getActiveProject());
        boolean newTerm = true;
        int termsProcessed = 0;
        //Determine TE users that are members of the project under consideration who have specified roles
        if (searchCriteria.getRoleIds() != null && searchCriteria.getRoleIds().size() > 0) {
          HashMap roleIds = searchCriteria.getRoleIds();
          Iterator operators = roleIds.keySet().iterator();

          termsProcessed = 0;
          String previousOp = null;

          while (operators.hasNext()) {
            String operator = (String) operators.next();
            ArrayList values = (ArrayList) roleIds.get(operator);

            Iterator iter = values.iterator();
            while (iter.hasNext()) {
              String value = (String) iter.next();
              if (operator.equals("=") || operator.equals("!=")) {
                if (termsProcessed > 0 && !(previousOp.equals(operator))) {
                  newTerm = processElementHeader(sqlFilter, newTerm, 0);
                } else {
                  if (termsProcessed > 0 && previousOp.equals(operator) && operator.equals("!=")) {
                    //if you're doing multiple != terms in a row, what you really want is an AND not an OR
                    newTerm = processElementHeader(sqlFilter, newTerm, 0);
                  } else {
                    newTerm = processElementHeader(sqlFilter, newTerm, termsProcessed);
                  }
                }
              }

              if (termsProcessed == 0) {
                sqlFilter.append("(");
              }

              sqlFilter.append(" (u.user_id in (SELECT pt.user_id FROM project_team pt WHERE pt.project_id = " + activeProject.getId() + " AND pt.userlevel " + operator + " " + value + " )) ");
              previousOp = operator;
              termsProcessed++;
            }
          }

          if (termsProcessed > 0) {
            sqlFilter.append(") ");
          }
        }
        //Determine users (both members and non-members of the project under consideration) who have rated the project
        if (searchCriteria.getRatings() != null && searchCriteria.getRatings().size() > 0) {
          HashMap ratings = searchCriteria.getRatings();
          Iterator operators = ratings.keySet().iterator();

          termsProcessed = 0;
          String previousOp = null;

          while (operators.hasNext()) {
            String operator = (String) operators.next();
            ArrayList values = (ArrayList) ratings.get(operator);

            Iterator iter = values.iterator();
            while (iter.hasNext()) {
              String value = (String) iter.next();
              if (operator.equals("=") || operator.equals("!=")) {
                if (termsProcessed > 0 && !(previousOp.equals(operator))) {
                  newTerm = processElementHeader(sqlFilter, newTerm, 0);
                } else {
                  if (termsProcessed > 0 && previousOp.equals(operator) && operator.equals("!=")) {
                    //if you're doing multiple != terms in a row, what you really want is an AND not an OR
                    newTerm = processElementHeader(sqlFilter, newTerm, 0);
                  } else {
                    newTerm = processElementHeader(sqlFilter, newTerm, termsProcessed);
                  }
                }
              }

              if (termsProcessed == 0) {
                sqlFilter.append("(");
              }

              sqlFilter.append("(u.user_id in (SELECT pr.enteredby FROM projects_rating pr WHERE pr.project_id = " + activeProject.getId() + " AND pr.rating " + operator + " " + value + " )) ");
              if (searchCriteria.getIsTeamMember() == Constants.TRUE){
                sqlFilter.append(" AND (u.user_id in (SELECT pt.user_id FROM project_team pt WHERE pt.project_id = " + activeProject.getId()  + " )) ");
              }
              
              previousOp = operator;
              termsProcessed++;
            }
          }

          if (termsProcessed > 0) {
            sqlFilter.append(") ");
          }
        }
        //Last Viewed
        if (searchCriteria.getLastViewed() != null && searchCriteria.getLastViewed().size() > 0) {
          HashMap lastViewed = searchCriteria.getLastViewed();

          Iterator operators = lastViewed.keySet().iterator();

          termsProcessed = 0;
          String previousOp = null;

          while (operators.hasNext()) {
            String operator = (String) operators.next();
            ArrayList values = (ArrayList) lastViewed.get(operator);

            Iterator iter = values.iterator();
            while (iter.hasNext()) {
              String value = (String) iter.next();
              if (operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")) {

                if (termsProcessed > 0 && !(previousOp.equals(operator))) {
                  //we want to get an 'AND' term if we have switched between operators here
                  //for example, enteredDate less than x AND enteredDate greater than y
                  newTerm = processElementHeader(sqlFilter, newTerm, 0);
                } else {
                  newTerm = processElementHeader(sqlFilter, newTerm, termsProcessed);
                }

                if (termsProcessed == 0) {
                  sqlFilter.append("(");
                }

                sqlFilter.append("(u.user_id IN (SELECT pv.user_id FROM projects_view pv WHERE pv.project_id = " + activeProject.getId() + " AND pv.view_date " + operator + " '" + DatabaseUtils.parseDateToTimestamp(value) + "') )");
                if (searchCriteria.getIsTeamMember() == Constants.TRUE){
                  sqlFilter.append(" AND (u.user_id in (SELECT pt.user_id FROM project_team pt WHERE pt.project_id = " + activeProject.getId()  + " )) ");
                }
                previousOp = operator;
                termsProcessed++;
              }
            }
          }

          if (termsProcessed > 0) {
            sqlFilter.append(")");
          }
        }
        //Connect Members who have been active in this project
        if (searchCriteria.getActive() != null && searchCriteria.getActive().size() > 0) {
          HashMap active = searchCriteria.getActive();

          Iterator operators = active.keySet().iterator();

          termsProcessed = 0;
          String previousOp = null;

          while (operators.hasNext()) {
            String operator = (String) operators.next();
            ArrayList values = (ArrayList) active.get(operator);

            Iterator iter = values.iterator();
            while (iter.hasNext()) {
              String value = (String) iter.next();
              if (operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")) {

                if (termsProcessed > 0 && !(previousOp.equals(operator))) {
                  //we want to get an 'AND' term if we have switched between operators here
                  //for example, enteredDate less than x AND enteredDate greater than y
                  newTerm = processElementHeader(sqlFilter, newTerm, 0);
                } else {
                  newTerm = processElementHeader(sqlFilter, newTerm, termsProcessed);
                }

                if (termsProcessed == 0) {
                  sqlFilter.append("(");
                }

                //has rated
                sqlFilter.append(" (u.user_id in (SELECT pr.enteredby FROM projects_rating pr WHERE pr.project_id = " + activeProject.getId() + " AND pr.entered " + operator + " '" + DatabaseUtils.parseDateToTimestamp(value) + "') ");
                //has downloaded project files
                sqlFilter.append(" OR u.user_id IN (" +
                    "SELECT pfd.user_download_id " +
                    "FROM project_files_download pfd " +
                    "LEFT JOIN project_files pf ON (pfd.item_id = pf.item_id) " +
                    "WHERE pf.link_module_id = " + Constants.PROJECTS_FILES + " AND pf.link_item_id = " + activeProject.getId() + " " +
                    "AND pfd.download_date " + operator + " '" + DatabaseUtils.parseDateToTimestamp(value) + "') ");
                //has posted discussions
                sqlFilter.append(" OR u.user_id IN (" +
                    "SELECT pi.enteredby FROM project_issues pi " +
                    "WHERE pi.project_id = " + activeProject.getId() + " " +
                    "AND pi.entered " + operator + " '" + DatabaseUtils.parseDateToTimestamp(value) + "') ");
                //has posted replies to existing discussions
                sqlFilter.append(" OR u.user_id IN (" +
                    "SELECT pir.enteredby FROM project_issue_replies pir " +
                    "LEFT JOIN project_issues iss ON (pir.issue_id = iss.issue_id) " +
                    "WHERE iss.project_id = " + activeProject.getId() + " " +
                    "AND pir.entered " + operator + "'" + DatabaseUtils.parseDateToTimestamp(value) + "') ");
                sqlFilter.append(") ");
                if (searchCriteria.getIsTeamMember() == Constants.TRUE){
                  sqlFilter.append(" AND (u.user_id in (SELECT pt.user_id FROM project_team pt WHERE pt.project_id = " + activeProject.getId()  + " )) ");
                }
                previousOp = operator;
                termsProcessed++;
              }
            }
          }

          if (termsProcessed > 0) {
            sqlFilter.append(")");
          }
        }
        if (!newTerm) {
          sqlFilter.append(") ");
        }
      }
    }
    if (guid != null) {
      // From UserUtils.generateGuid
      sqlFilter.append("AND " + DatabaseUtils.getSubString(db, "password", 3, 13) + " = ? ");
      sqlFilter.append("AND entered = ? ");
      sqlFilter.append("AND user_id = ? ");
    }
    if (isAdmin != Constants.UNDEFINED) {
      sqlFilter.append("AND u.access_admin = ? ");
    }
  }

  public boolean processElementHeader(StringBuffer sqlFilter, boolean newTerm, int termsProcessed) {
    if (firstCriteria && newTerm) {
      sqlFilter.append(" AND (");
      firstCriteria = false;
      newTerm = false;
    } else if (newTerm && !(firstCriteria)) {
      sqlFilter.append(" OR (");
      newTerm = false;
    } else if (termsProcessed > 0) {
      sqlFilter.append(" OR ");
    } else {
      sqlFilter.append(" AND ");
    }
    return newTerm;
  }

  /**
   * sets sql values for any specified conditions
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (userId > -1) {
      pst.setInt(++i, userId);
    }
    if (groupId > -1) {
      pst.setInt(++i, groupId);
    }
    if (departmentId > -1) {
      pst.setInt(++i, departmentId);
    }
    if (watchForums != Constants.UNDEFINED) {
      pst.setBoolean(++i, watchForums == Constants.TRUE);
    }
    if (validUser == Constants.TRUE) {
      pst.setBoolean(++i, true);
      pst.setTimestamp(++i, new java.sql.Timestamp(System.currentTimeMillis()));
    }
    if (validUser == Constants.FALSE) {
      pst.setBoolean(++i, false);
      pst.setTimestamp(++i, new java.sql.Timestamp(System.currentTimeMillis()));
    }
    if (username != null) {
      pst.setString(++i, username.toLowerCase());
    }
    if (userIdArray != null && userIdArray.length > 0) {
      int count = 0;
      while (count < userIdArray.length) {
        pst.setInt(++i, Integer.parseInt(userIdArray[count].trim()));
        count++;
      }
    }
    if (searchCriteria != null) {
      if (StringUtils.hasText(searchCriteria.getEmail())) {
        pst.setString(++i, "%" + searchCriteria.getEmail().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getFirstName())) {
        pst.setString(++i, "%" + searchCriteria.getFirstName().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getLastName())) {
        pst.setString(++i, "%" + searchCriteria.getLastName().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getCompany())) {
        pst.setString(++i, "%" + searchCriteria.getCompany().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getName())) {
        pst.setString(++i, "%" + searchCriteria.getName().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getName())) {
        pst.setString(++i, "%" + searchCriteria.getName().toLowerCase() + "%");
      }
      if (searchCriteria.getEnabled() != Constants.UNDEFINED) {
        pst.setBoolean(++i, searchCriteria.getEnabled() == Constants.TRUE);
      }
      if (searchCriteria.getRegistered() != Constants.UNDEFINED) {
        pst.setBoolean(++i, searchCriteria.getRegistered() == Constants.TRUE);
      }
      if (searchCriteria.getExpired() != Constants.UNDEFINED) {
        pst.setTimestamp(++i, new java.sql.Timestamp(System.currentTimeMillis()));
      }
      if (searchCriteria.getAdmin() != Constants.UNDEFINED) {
        pst.setBoolean(++i, searchCriteria.getAdmin() == Constants.TRUE);
      }
      if (searchCriteria.getContentEditor() != Constants.UNDEFINED) {
        pst.setBoolean(++i, searchCriteria.getContentEditor() == Constants.TRUE);
      }
    }
    if (guid != null) {
      // From UserUtils.generateGuid
      pst.setString(++i, UserUtils.getPasswordSubStringFromGuid(guid));
      pst.setTimestamp(++i, UserUtils.getEnteredTimestampFromGuid(guid));
      pst.setInt(++i, UserUtils.getUserIdFromGuid(guid));
    }
    if (isAdmin != Constants.UNDEFINED) {
      pst.setBoolean(++i, isAdmin == Constants.TRUE);
    }
    return i;
  }

  /**
   * returns a count of all users in database, without any conditions applied
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int buildUserCount(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) AS recordcount " +
        "FROM users u " +
        "WHERE user_id > -1 ");
    ResultSet rs = pst.executeQuery();
    rs.next();
    int count = rs.getInt("recordcount");
    rs.close();
    pst.close();
    return count;
  }
}

