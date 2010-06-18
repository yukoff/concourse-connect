/*
 * ConcourseConnect
 * Copyright 2010 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect and is licensed under a commercial
 * license, not an open source license.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.web.modules.activity.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;

/**
 * class description goes here..
 *
 * @author Ananth
 * @version ProjectHistory.java Feb 11, 2009 4:22:28 PM Ananth $
 * @created Feb 11, 2009
 */
public class ProjectHistory extends GenericBean {
  private int id = -1;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private int projectId = -1;
  private String linkObject = null;
  private int linkItemId = -1;
  private Timestamp linkStartDate = null;
  private String description = null;
  private boolean enabled = true;
  private int eventType = -1;
  private int parentId = -1;
  private int topId = -1;
  private int position = 0;
  private int threadPosition = 0;
  private int indent = 0;
  private int childCount = 0;
  private Timestamp relativeDate = null;
  private int relativeEnteredby = -1;
  private String lineage = "/";

  private String htmlLink = null;

  /**
   * @param relativeEnteredby the relativeEnteredby to set
   */
  public void setRelativeEnteredby(int relativeEnteredby) {
    this.relativeEnteredby = relativeEnteredby;
  }

  public void setRelativeEnteredby(String relativeEnteredby) {
    this.relativeEnteredby = Integer.parseInt(relativeEnteredby);
  }

  /**
   * @return the relativeEnteredby
   */
  public int getRelativeEnteredby() {
    return relativeEnteredby;
  }

  /**
   * @param relativeDate the relativeDate to set
   */
  public void setRelativeDate(Timestamp relativeDate) {
    this.relativeDate = relativeDate;
  }

  public void setRelativeDate(String relativeDate) {
    this.relativeDate = DatabaseUtils.parseTimestamp(relativeDate);
  }

  /**
   * @return the relativeDate
   */
  public Timestamp getRelativeDate() {
    return relativeDate;
  }

  /**
   * @param indent the indent to set
   */
  public void setIndent(int indent) {
    this.indent = indent;
  }

  public void setIndent(String indent) {
    this.indent = Integer.parseInt(indent);
  }

  /**
   * @return the indent
   */
  public int getIndent() {
    return indent;
  }

  public int getChildCount() {
    return childCount;
  }

  public void setChildCount(int childCount) {
    this.childCount = childCount;
  }

  public void setChildCount(String childCount) {
    this.childCount = Integer.parseInt(childCount);
  }

  /**
   * @param position the position to set
   */
  public void setPosition(int position) {
    this.position = position;
  }

  public void setPosition(String position) {
    this.position = Integer.parseInt(position);
  }

  public void setThreadPosition(int threadPosition) {
    this.threadPosition = threadPosition;
  }

  public void setThreadPosition(String threadPosition) {
    this.threadPosition = Integer.parseInt(threadPosition);
  }

  /**
   * @return the position
   */
  public int getPosition() {
    return position;
  }

  public int getThreadPosition() {
    return threadPosition;
  }

  /**
   * @param topId the topId to set
   */
  public void setTopId(int topId) {
    this.topId = topId;
  }

  public void setTopId(String topId) {
    this.topId = Integer.parseInt(topId);
  }

  /**
   * @return the topId
   */
  public int getTopId() {
    return topId;
  }

  /**
   * @param parentId the parentId to set
   */
  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = Integer.parseInt(parentId);
  }

  /**
   * @return the parentId
   */
  public int getParentId() {
    return parentId;
  }

  public int getEventType() {
    return eventType;
  }

  public void setEventType(int eventType) {
    this.eventType = eventType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getLinkItemId() {
    return linkItemId;
  }

  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public String getLinkObject() {
    return linkObject;
  }

  public void setLinkObject(String linkObject) {
    this.linkObject = linkObject;
  }

  public Timestamp getLinkStartDate() {
    return linkStartDate;
  }

  public void setLinkStartDate(Timestamp linkStartDate) {
    this.linkStartDate = linkStartDate;
  }

  public void setLinkStartDate(String linkStartDate) {
    this.linkStartDate = DatabaseUtils.parseTimestamp(linkStartDate);
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLineage() {
    return lineage;
  }

  public void setLineage(String lineage) {
    this.lineage = lineage;
  }

  /**
   * @return the htmlLink
   */
  public String getHtmlLink() {
    return htmlLink;
  }

  /**
   * @param htmlLink the htmlLink to set
   */
  public void setHtmlLink(String htmlLink) {
    this.htmlLink = htmlLink;
  }

  public Project getProject() {
    if (projectId != -1) {
      return ProjectUtils.loadProject(projectId);
    }
    return null;
  }

  public User getUser() {
    if (enteredBy != -1) {
      return UserUtils.loadUser(enteredBy);
    }
    return null;
  }

  public ProjectHistory() {
  }

  public ProjectHistory(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public ProjectHistory(Connection db, String id) throws SQLException {
    queryRecord(db, Integer.parseInt(id));
  }

  public ProjectHistory(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Id not specified..");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT * " +
            "FROM project_history " +
            "WHERE history_id = ?");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();

    if (this.id == -1) {
      throw new SQLException("Record not found");
    }
  }

  public void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("history_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    projectId = rs.getInt("project_id");
    linkObject = rs.getString("link_object");
    linkItemId = rs.getInt("link_item_id");
    linkStartDate = rs.getTimestamp("link_start_date");
    description = rs.getString("description");
    enabled = rs.getBoolean("enabled");
    eventType = rs.getInt("event_type");
    parentId = DatabaseUtils.getInt(rs, ("parent_id"));
    topId = DatabaseUtils.getInt(rs, "top_id");
    position = rs.getInt("position");
    threadPosition = rs.getInt("thread_position");
    indent = rs.getInt("indent");
    childCount = rs.getInt("child_count");
    relativeDate = rs.getTimestamp("relative_date");
    relativeEnteredby = rs.getInt("relative_enteredby");
    lineage = rs.getString("lineage");
  }

  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO project_history " +
        "(enteredby, project_id, link_object, link_item_id");
    if (parentId != -1) {
      sql.append(", parent_id");
    }
    if (topId != -1) {
      sql.append(", top_id");
    }
    if (position != -1) {
      sql.append(", position");
    }
    if (threadPosition != -1) {
      sql.append(", thread_position");
    }
    if (indent != -1) {
      sql.append(", indent");
    }
    if (childCount != -1) {
      sql.append(", child_count");
    }
    sql.append(", relative_enteredby");
    if (linkStartDate != null) {
      sql.append(", link_start_date");
    }
    if (entered != null) {
      sql.append(", entered");
    }
    sql.append(", description, enabled, event_type, lineage) VALUES " +
        "(?, ?, ?, ?");
    if (parentId != -1) {
      sql.append(", ?");
    }
    if (topId != -1) {
      sql.append(", ?");
    }
    if (position != -1) {
      sql.append(", ?");
    }
    if (threadPosition != -1) {
      sql.append(", ?");
    }
    if (indent != -1) {
      sql.append(", ?");
    }
    if (childCount != -1) {
      sql.append(", ?");
    }
    sql.append(", ?");
    /*if (relativeDate != null) {
      sql.append(", ?");
    }*/
    if (linkStartDate != null) {
      sql.append(", ?");
    }
    if (entered != null) {
      sql.append(", ?");
    }
    sql.append(", ?, ?, ?, ?) ");
    PreparedStatement pst = db.prepareStatement(sql.toString());

    int i = 0;
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, projectId);
    pst.setString(++i, linkObject);
    pst.setInt(++i, linkItemId);
    if (parentId != -1) {
      DatabaseUtils.setInt(pst, ++i, parentId);
    }
    if (topId != -1) {
      DatabaseUtils.setInt(pst, ++i, topId);
    }
    if (position != -1) {
      pst.setInt(++i, position);
    }
    if (threadPosition != -1) {
      pst.setInt(++i, threadPosition);
    }
    if (indent != -1) {
      pst.setInt(++i, indent);
    }
    if (childCount != -1) {
      pst.setInt(++i, childCount);
    }
    if (relativeEnteredby != -1) {
      pst.setInt(++i, relativeEnteredby);
    } else {
      pst.setInt(++i, enteredBy);
    }
    /*  if (relativeDate != null) {
      pst.setTimestamp(++i, relativeDate);
    }*/
    if (linkStartDate != null) {
      pst.setTimestamp(++i, linkStartDate);
    }
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    pst.setString(++i, description);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, eventType);
    pst.setString(++i, lineage);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_history_history_id_seq", -1);
    return true;
  }

  public int updateThreadPosition(Connection db) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int i = 0;
      PreparedStatement pst;
      pst = db.prepareStatement(
          "UPDATE project_history " +
              "SET thread_position = thread_position + 1 " +
              "WHERE thread_position >= ? AND top_id = ? ");
      pst.setInt(++i, threadPosition);
      pst.setInt(++i, topId);
      int updateCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      return updateCount;
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
  }

  public int updateRelativeDate(Connection db) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int i = 0;
      PreparedStatement pst;
      pst = db.prepareStatement(
          "UPDATE project_history " +
              "SET relative_date = CURRENT_TIMESTAMP, top_id = ? " +
              "WHERE history_id = ? OR top_id = ?");
      pst.setInt(++i, topId);
      pst.setInt(++i, topId);
      pst.setInt(++i, topId);
      int updateCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      return updateCount;
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
  }

  public int updateChildCount(Connection db) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int i = 0;
      PreparedStatement pst;
      pst = db.prepareStatement(
          "UPDATE project_history " +
              "SET child_count = child_count + 1 " +
              "WHERE history_id = ? ");
      pst.setInt(++i, parentId);
      int updateCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      return updateCount;
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
  }

  public boolean delete(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ID not specified");
    }
    int deleteCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Remove the entry and any replies
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_history " +
              "WHERE history_id = ? OR top_id = ? ");
      pst.setInt(1, id);
      pst.setInt(2, id);
      deleteCount = pst.executeUpdate();
      pst.close();
      if (topId > -1) {
        // Update the position values
        pst = db.prepareStatement(
            "UPDATE project_history " +
                "SET position = position - 1 " +
                "WHERE top_id = ? " +
                "AND position > ? ");
        pst.setInt(1, topId);
        pst.setInt(2, position);
        pst.executeUpdate();
        pst.close();
        // Update the thread position values
        pst = db.prepareStatement(
            "UPDATE project_history " +
                "SET thread_position = thread_position - 1 " +
                "WHERE top_id = ? " +
                "AND thread_position > ? ");
        pst.setInt(1, topId);
        pst.setInt(2, threadPosition);
        pst.executeUpdate();
        pst.close();
      }
      // Update the parent value
      if (parentId > -1) {
        pst = db.prepareStatement(
            "UPDATE project_history " +
                "SET child_count = child_count - 1 " +
                "WHERE history_id = ? ");
        pst.setInt(1, parentId);
        pst.executeUpdate();
        pst.close();
      }
      if (commit) {
        db.commit();
      }
      return (deleteCount > 0);
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
  }
}
