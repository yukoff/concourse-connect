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
package com.concursive.connect.web.modules.messages.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.controller.servlets.LinkGenerator;
import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;

/**
 * Represents a private message to a profile
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created December 21, 2008
 */
public class PrivateMessage extends GenericBean {

  private int id = -1;
  private int projectId = -1;
  private int parentId = -1;
  private String module = null;
  private int linkModuleId = -1;
  private int linkItemId = -1;
  private String body = null;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp readDate = null;
  private int readBy = -1;
  private boolean deletedByEnteredBy = false;
  private boolean deletedByUserId = false;
  private Timestamp lastReplyDate = null;
  private int linkProjectId = -1;
  //helper constants
  public static final String FOLDER_SENT = "sent";
  public static final String FOLDER_INBOX = "inbox";

  /**
   * Constructor for the PrivateMessage
   */
  public PrivateMessage() {
  }

  /**
   * Constructor for the PrivateMessage object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public PrivateMessage(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * Constructor for the PrivateMessage object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public PrivateMessage(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }

  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int thisId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
        "FROM project_private_message ppm " +
        "WHERE ppm.message_id = ? ");

    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, thisId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    Exception errorMessage = null;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO project_private_message " +
          "(" + (id > -1 ? "message_id," : "") +
          "project_id, " +
          "parent_id, " +
          "link_module_id, " +
          "link_item_id, " +
          "body, " +
          (entered != null ? "entered, " : "") +
          "enteredby, " +
          (readDate != null ? "read_date, " : "") +
          "read_by, " +
          (lastReplyDate != null ? "last_reply_date, " : "") +
          "link_project_id, " +
          "deleted_by_entered_by, " +
          "deleted_by_user_id )");
      sql.append("VALUES (");
      if (id > -1) {
        sql.append("?, ");
      }
      if (entered != null) {
        sql.append("?, ");
      }
      if (readDate != null) {
        sql.append("?, ");
      }
      if (lastReplyDate != null) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, projectId);
      DatabaseUtils.setInt(pst, ++i, parentId);
      DatabaseUtils.setInt(pst, ++i, linkModuleId);
      DatabaseUtils.setInt(pst, ++i, linkItemId);
      pst.setString(++i, body);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      DatabaseUtils.setInt(pst, ++i, enteredBy);
      if (readDate != null) {
        pst.setTimestamp(++i, readDate);
      }
      DatabaseUtils.setInt(pst, ++i, readBy);
      if (lastReplyDate != null) {
        pst.setTimestamp(++i, lastReplyDate);
      }
      DatabaseUtils.setInt(pst, ++i, linkProjectId);
      pst.setBoolean(++i, deletedByEnteredBy);
      pst.setBoolean(++i, deletedByUserId);

      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_private_message_message_id_seq", id);

      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
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

  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    // Update the project
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE  project_private_message SET " +
        (readDate != null ? "read_date = ? , " : "") +
        (readBy > -1 ? "read_by = ? , " : "") +
        (lastReplyDate != null ? "last_reply_date = ? , " : "") +
        " deleted_by_entered_by = ? , " +
        " deleted_by_user_id = ? " +
        "WHERE message_id = ? ");
    int i = 0;
    if (readDate != null) {
      pst.setTimestamp(++i, readDate);
    }
    if (readBy > -1) {
      DatabaseUtils.setInt(pst, ++i, readBy);
    }
    if (lastReplyDate != null) {
      pst.setTimestamp(++i, lastReplyDate);
    }
    pst.setBoolean(++i, deletedByEnteredBy);
    pst.setBoolean(++i, deletedByUserId);
    DatabaseUtils.setInt(pst, ++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }

  public boolean delete(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int recordCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      //Delete the private message
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_private_message " +
          "WHERE message_id = ? ");
      pst.setInt(1, id);
      recordCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
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
      errors.put("actionError", "Private message could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }

  /**
   * Sets the Id attribute of the PrivateMessage object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }

  /**
   * Sets the id attribute of the PrivateMessage object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  /**
   * Sets the enteredBy attribute of the PrivateMessage object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }

  /**
   * Sets the entered attribute of the PrivateMessage object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  /**
   * Sets the entered attribute of the PrivateMessage object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    entered = tmp;
  }

  /**
   * Sets the enteredBy attribute of the PrivateMessage object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * Gets the Id attribute of the PrivateMessage object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the entered attribute of the PrivateMessage object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * Gets the enteredBy attribute of the PrivateMessage object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * @return the parentId
   */
  public int getParentId() {
    return parentId;
  }

  /**
   * Sets the parentId attribute of the PrivateMessage object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  /**
   * Sets the parentId attribute of the PrivateMessage object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(String parentId) {
    this.parentId = Integer.parseInt(parentId);
  }

  public String getModule() {
    return module;
  }

  /**
   * Sets the module attribute of the PrivateMessage object
   *
   * @param tmp The new module value
   */
  public void setModule(String module) {
    this.module = module;
  }

  /**
   * @return the linkModuleId
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }

  /**
   * Sets the linkModuleId attribute of the PrivateMessage object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  /**
   * Sets the linkModuleId attribute of the PrivateMessage object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(String linkModuleId) {
    this.linkModuleId = Integer.parseInt(linkModuleId);
  }

  /**
   * @return the linkItemId
   */
  public int getLinkItemId() {
    return linkItemId;
  }

  /**
   * Sets the linkItemId attribute of the PrivateMessage object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public void setLinkItemId(String linkItemId) {
    if (StringUtils.hasText(linkItemId)) {
      this.linkItemId = Integer.parseInt(linkItemId);
    }
  }

  /**
   * @return the body
   */
  public String getBody() {
    return body;
  }

  public String getHtmlBody() {
    if (StringUtils.hasText(body)) {
      return StringUtils.toHtmlValue(body);
    }

    return null;
  }

  /**
   * Sets the body attribute of the PrivateMessage object
   *
   * @param tmp The new body value
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return the readDate
   */
  public Timestamp getReadDate() {
    return readDate;
  }

  /**
   * Sets the readDate attribute of the PrivateMessage object
   *
   * @param tmp The new readDate value
   */
  public void setReadDate(Timestamp readDate) {
    this.readDate = readDate;
  }

  /**
   * Sets the readDate attribute of the PrivateMessage object
   *
   * @param tmp The new readDate value
   */
  public void setReadDate(String readDate) {
    this.readDate = DatabaseUtils.parseTimestamp(readDate);
  }

  /**
   * @return the readBy
   */
  public int getReadBy() {
    return readBy;
  }

  /**
   * Sets the readBy attribute of the PrivateMessage object
   *
   * @param tmp The new readBy value
   */
  public void setReadBy(int readBy) {
    this.readBy = readBy;
  }

  /**
   * Sets the readBy attribute of the PrivateMessage object
   *
   * @param tmp The new readBy value
   */
  public void setReadBy(String readBy) {
    this.readBy = Integer.parseInt(readBy);
  }

  /**
   * @return the deletedByEnteredBy
   */
  public boolean getDeletedByEnteredBy() {
    return deletedByEnteredBy;
  }

  /**
   * Sets the deletedByEnteredBy attribute of the PrivateMessage object
   *
   * @param tmp The new deletedByEnteredBy value
   */
  public void setDeletedByEnteredBy(boolean deletedByEnteredBy) {
    this.deletedByEnteredBy = deletedByEnteredBy;
  }

  /**
   * Sets the deletedByEnteredBy attribute of the PrivateMessage object
   *
   * @param tmp The new deletedByEnteredBy value
   */
  public void setDeletedByEnteredBy(String deletedByEnteredBy) {
    this.deletedByEnteredBy = DatabaseUtils.parseBoolean(deletedByEnteredBy);
  }

  /**
   * @return the deletedByUserId
   */
  public boolean getDeletedByUserId() {
    return deletedByUserId;
  }

  /**
   * Sets the deletedByUserId attribute of the PrivateMessage object
   *
   * @param tmp The new deletedByUserId value
   */
  public void setDeletedByUserId(boolean deletedByUserId) {
    this.deletedByUserId = deletedByUserId;
  }

  /**
   * Sets the deletedByUserId attribute of the PrivateMessage object
   *
   * @param tmp The new deletedByUserId value
   */
  public void setDeletedByUserId(String deletedByUserId) {
    this.deletedByUserId = DatabaseUtils.parseBoolean(deletedByUserId);
  }

  /**
   * Sets the lastReplyDate attribute of the PrivateMessage object
   *
   * @param tmp The new lastReplyDate value
   */
  public Timestamp getLastReplyDate() {
    return lastReplyDate;
  }

  /**
   * Sets the lastReplyDate attribute of the PrivateMessage object
   *
   * @param tmp The new lastReplyDate value
   */
  public void setLastReplyDate(Timestamp lastReplyDate) {
    this.lastReplyDate = lastReplyDate;
  }

  public void setLastReplyDate(String lastReplyDate) {
    this.lastReplyDate = DatabaseUtils.parseTimestamp(lastReplyDate);
  }

  /**
   * @return the linkProjectId
   */
  public int getLinkProjectId() {
    return linkProjectId;
  }

  /**
   * Sets the linkProjectId attribute of the PrivateMessage object
   *
   * @param tmp The new linkProjectId value
   */
  public void setLinkProjectId(int linkProjectId) {
    this.linkProjectId = linkProjectId;
  }

  /**
   * Sets the linkProjectId attribute of the PrivateMessage object
   *
   * @param tmp The new linkProjectId value
   */
  public void setLinkProjectId(String linkProjectId) {
    this.linkProjectId = Integer.parseInt(linkProjectId);
  }

  /**
   * Gets the valid attribute of the PrivateMessage object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (!StringUtils.hasText(getBody())) {
      errors.put("bodyError", "Body is required");
    }
    return !this.hasErrors();
  }

  public User getUser() {
    return UserUtils.loadUser(enteredBy);
  }

  public Project getProject() {
    return ProjectUtils.loadProject(projectId);
  }

  public Project getLinkProject() {
    if (linkProjectId != -1) {
      return ProjectUtils.loadProject(linkProjectId);
    }
    return null;
  }

  public String getItemLink() {
    return LinkGenerator.getItemLink(linkModuleId, linkItemId);
  }

  public String getItemLabel() {
    return ModuleUtils.getItemLabel(linkModuleId);
  }

  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("message_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    parentId = DatabaseUtils.getInt(rs, "parent_id");
    linkModuleId = DatabaseUtils.getInt(rs, "link_module_id");
    linkItemId = DatabaseUtils.getInt(rs, "link_item_id");
    body = rs.getString("body");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    readDate = rs.getTimestamp("read_date");
    readBy = DatabaseUtils.getInt(rs, "read_by");
    deletedByEnteredBy = rs.getBoolean("deleted_by_entered_by");
    deletedByUserId = rs.getBoolean("deleted_by_user_id");
    lastReplyDate = rs.getTimestamp("last_reply_date");
    linkProjectId = DatabaseUtils.getInt(rs, "link_project_id");
  }
}
