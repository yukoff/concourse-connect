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

package com.concursive.connect.web.modules.lists.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Encapsulates the fields in which Task updates are logged
 *
 * @author matt
 * @version $Id$
 * @created Februrary 19, 2008
 */
public class TaskLog extends GenericBean {

  // db variables
  private int id = -1;
  private int taskId = -1;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private int priority = -1;
  private double estimatedLOE = -1;
  private int estimatedLOEType = -1;
  private int owner = -1;
  private int categoryId = -1;
  private boolean complete = false;
  private Timestamp dueDate = null;
  private Timestamp entered = null;
  private Timestamp modified = null;
  private Timestamp completeDate = null;
  private int functionalArea = -1;
  private int status = -1;
  private int businessValue = -1;
  private int complexity = -1;
  private int targetRelease = -1;
  private int targetSprint = -1;
  private int loeRemaining = -1;
  private int assignedPriority = -1;
  // helper
  private int age = -1;
  private boolean insertCreationDates = false;


  public TaskLog() {
  }


  /**
   * Constructor for the TaskLog object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public TaskLog(Connection db, int thisId) throws SQLException {
    if (thisId == -1) {
      throw new SQLException("TaskLog ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT t.id, t.task_id, t.entered, t.enteredby, t.priority, " +
            "t.duedate, t.complete, t.estimatedloe, " +
            "t.estimatedloetype, t.owner, t.completedate, " +
            "t.category_id, " +
            "functional_area, status, business_value, complexity, target_release, target_sprint, loe_remaining, " +
            "assigned_priority " +
            "FROM tasklog t " +
            "WHERE id = ? ");
    int i = 0;
    pst.setInt(++i, thisId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("TaskLog ID not found");
    }
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public TaskLog(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public TaskLog(Task task) {
    taskId = task.getId();
    enteredBy = task.getEnteredBy();
    modifiedBy = task.getModifiedBy();
    priority = task.getPriority();
    estimatedLOE = task.getEstimatedLOE();
    estimatedLOEType = task.getEstimatedLOEType();
    owner = task.getOwner();
    categoryId = task.getCategoryId();
    complete = task.getComplete();
    dueDate = task.getDueDate();
    entered = task.getEntered();
    modified = task.getModified();
    completeDate = task.getCompleteDate();
    functionalArea = task.getFunctionalArea();
    status = task.getStatus();
    businessValue = task.getBusinessValue();
    complexity = task.getComplexity();
    targetRelease = task.getTargetRelease();
    targetSprint = task.getTargetSprint();
    loeRemaining = task.getLoeRemaining();
    assignedPriority = task.getAssignedPriority();
    // adjustments
    if (enteredBy == -1) {
      enteredBy = modifiedBy;
    }
    if (modifiedBy == -1) {
      modifiedBy = enteredBy;
    }
  }

  public int getTaskId() {
    return taskId;
  }

  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }

  public void setTaskId(String tmp) {
    this.taskId = Integer.parseInt(tmp);
  }

  /**
   * Sets the entered attribute of the TaskLog object
   *
   * @param entered The new entered value
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }


  /**
   * Sets the entered attribute of the TaskLog object
   *
   * @param entered The new entered value
   */
  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String modified) {
    this.modified = DatabaseUtils.parseTimestamp(modified);
  }

  /**
   * Sets the enteredBy attribute of the TaskLog object
   *
   * @param enteredBy The new enteredBy value
   */
  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }


  /**
   * Sets the enteredBy attribute of the TaskLog object
   *
   * @param enteredBy The new enteredBy value
   */
  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = Integer.parseInt(modifiedBy);
  }

  /**
   * Sets the complete attribute of the TaskLog object
   *
   * @param complete The new complete value
   */
  public void setComplete(boolean complete) {
    this.complete = complete;
  }


  /**
   * Sets the complete attribute of the TaskLog object
   *
   * @param complete The new complete value
   */
  public void setComplete(String complete) {
    this.complete = DatabaseUtils.parseBoolean(complete);
  }

  /**
   * Sets the priority attribute of the TaskLog object
   *
   * @param priority The new priority value
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }


  /**
   * Sets the priority attribute of the TaskLog object
   *
   * @param priority The new priority value
   */
  public void setPriority(String priority) {
    this.priority = Integer.parseInt(priority);
  }


  /**
   * Sets the dueDate attribute of the TaskLog object
   *
   * @param dueDate The new dueDate value
   */
  public void setDueDate(Timestamp dueDate) {
    this.dueDate = dueDate;
  }


  /**
   * Sets the dueDate attribute of the TaskLog object
   *
   * @param tmp The new dueDate value
   */
  public void setDueDate(String tmp) {
    this.dueDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the estimatedLOE attribute of the TaskLog object
   *
   * @param estimatedLOE The new estimatedLOE value
   */
  public void setEstimatedLOE(double estimatedLOE) {
    this.estimatedLOE = estimatedLOE;
  }


  /**
   * Sets the estimatedLOE attribute of the TaskLog object
   *
   * @param estimatedLOE The new estimatedLOE value
   */
  public void setEstimatedLOE(String estimatedLOE) {
    this.estimatedLOE = Double.parseDouble(estimatedLOE);
  }


  /**
   * Sets the owner attribute of the TaskLog object
   *
   * @param owner The new owner value
   */
  public void setOwner(int owner) {
    this.owner = owner;
  }


  /**
   * Sets the categoryId attribute of the TaskLog object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the categoryId attribute of the TaskLog object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    this.setCategoryId(Integer.parseInt(tmp));
  }


  /**
   * Sets the owner attribute of the TaskLog object
   *
   * @param owner The new owner value
   */
  public void setOwner(String owner) {
    this.owner = Integer.parseInt(owner);
  }


  /**
   * Sets the id attribute of the TaskLog object
   *
   * @param id The new id value
   */
  public void setId(int id) {
    this.id = id;
  }


  /**
   * Sets the id attribute of the TaskLog object
   *
   * @param id The new id value
   */
  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }


  /**
   * Sets the estimatedLOEType attribute of the TaskLog object
   *
   * @param estimatedLOEType The new estimatedLOEType value
   */
  public void setEstimatedLOEType(int estimatedLOEType) {
    this.estimatedLOEType = estimatedLOEType;
  }


  /**
   * Sets the estimatedLOEType attribute of the TaskLog object
   *
   * @param estimatedLOEType The new estimatedLOEType value
   */
  public void setEstimatedLOEType(String estimatedLOEType) {
    this.estimatedLOEType = Integer.parseInt(estimatedLOEType);
  }


  /**
   * Gets the estimatedLOEType attribute of the TaskLog object
   *
   * @return The estimatedLOEType value
   */
  public int getEstimatedLOEType() {
    return estimatedLOEType;
  }


  /**
   * Sets the completeDate attribute of the TaskLog object
   *
   * @param completeDate The new completeDate value
   */
  public void setCompleteDate(Timestamp completeDate) {
    this.completeDate = completeDate;
  }


  /**
   * Gets the completeDate attribute of the TaskLog object
   *
   * @return The completeDate value
   */
  public Timestamp getCompleteDate() {
    return completeDate;
  }


  public int getFunctionalArea() {
    return functionalArea;
  }

  public void setFunctionalArea(int functionalArea) {
    this.functionalArea = functionalArea;
  }

  public void setFunctionalArea(String tmp) {
    functionalArea = Integer.parseInt(tmp);
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setStatus(String tmp) {
    status = Integer.parseInt(tmp);
  }

  public int getBusinessValue() {
    return businessValue;
  }

  public void setBusinessValue(int businessValue) {
    this.businessValue = businessValue;
  }

  public void setBusinessValue(String tmp) {
    businessValue = Integer.parseInt(tmp);
  }

  public int getComplexity() {
    return complexity;
  }

  public void setComplexity(int complexity) {
    this.complexity = complexity;
  }

  public void setComplexity(String tmp) {
    complexity = Integer.parseInt(tmp);
  }

  public int getTargetRelease() {
    return targetRelease;
  }

  public void setTargetRelease(int targetRelease) {
    this.targetRelease = targetRelease;
  }

  public void setTargetRelease(String tmp) {
    targetRelease = Integer.parseInt(tmp);
  }

  public int getTargetSprint() {
    return targetSprint;
  }

  public void setTargetSprint(int targetSprint) {
    this.targetSprint = targetSprint;
  }

  public void setTargetSprint(String tmp) {
    targetSprint = Integer.parseInt(tmp);
  }

  public int getLoeRemaining() {
    return loeRemaining;
  }

  public void setLoeRemaining(int loeRemaining) {
    this.loeRemaining = loeRemaining;
  }

  public void setLoeRemaining(String tmp) {
    this.loeRemaining = Integer.parseInt(tmp);
  }

  public int getAssignedPriority() {
    return assignedPriority;
  }

  public void setAssignedPriority(int assignedPriority) {
    this.assignedPriority = assignedPriority;
  }

  public void setAssignedPriority(String tmp) {
    this.assignedPriority = Integer.parseInt(tmp);
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  /**
   * Gets the estimatedLOE attribute of the TaskLog object
   *
   * @return The estimatedLOE value
   */
  public double getEstimatedLOE() {
    return estimatedLOE;
  }


  /**
   * Gets the owner attribute of the TaskLog object
   *
   * @return The owner value
   */
  public int getOwner() {
    return owner;
  }


  /**
   * Gets the categoryId attribute of the TaskLog object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }

  public boolean getComplete() {
    return complete;
  }


  /**
   * Gets the dueDate attribute of the TaskLog object
   *
   * @return The dueDate value
   */
  public Timestamp getDueDate() {
    return dueDate;
  }


  /**
   * Gets the priority attribute of the TaskLog object
   *
   * @return The priority value
   */
  public int getPriority() {
    return priority;
  }


  /**
   * Gets the enteredBy attribute of the TaskLog object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  /**
   * Gets the entered attribute of the TaskLog object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }

  public Timestamp getModified() {
    return modified;
  }

  /**
   * Gets the id attribute of the TaskLog object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }

  public boolean getInsertCreationDates() {
    return insertCreationDates;
  }

  public void setInsertCreationDates(boolean insertCreationDates) {
    this.insertCreationDates = insertCreationDates;
  }

  public void setInsertCreationDates(String tmp) {
    this.insertCreationDates = DatabaseUtils.parseBoolean(tmp);
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int i = 0;
      PreparedStatement pst = db.prepareStatement(
          "INSERT INTO tasklog " +
              "(task_id, enteredby, modifiedby, priority, owner, duedate, estimatedloe, " +
              (estimatedLOEType == -1 ? "" : "estimatedloetype, ") +
              (insertCreationDates ? "entered, modified, " : "") +
              "complete, completedate, category_id, " +
              "functional_area, status, business_value, complexity, target_release, target_sprint, loe_remaining, " +
              "assigned_priority) " +
              "VALUES (?, ?, ?, ?, ?, ?, ?, " +
              (estimatedLOEType == -1 ? "" : "?, ") +
              (insertCreationDates ? "?, ?, " : "") +
              "?, ?, ?, ?, ?, ?, " +
              "?, ?, ?, ?, ? ) "
      );
      pst.setInt(++i, taskId);
      pst.setInt(++i, this.getEnteredBy());
      pst.setInt(++i, this.getModifiedBy());
      pst.setInt(++i, this.getPriority());
      DatabaseUtils.setInt(pst, ++i, this.getOwner());
      pst.setTimestamp(++i, this.getDueDate());
      pst.setDouble(++i, this.getEstimatedLOE());
      if (this.getEstimatedLOEType() != -1) {
        pst.setInt(++i, this.getEstimatedLOEType());
      }
      if (insertCreationDates) {
        pst.setTimestamp(++i, entered);
        pst.setTimestamp(++i, modified);
      }
      pst.setBoolean(++i, this.getComplete());
      DatabaseUtils.setTimestamp(pst, ++i, this.getCompleteDate());
      DatabaseUtils.setInt(pst, ++i, categoryId);
      DatabaseUtils.setInt(pst, ++i, functionalArea);
      DatabaseUtils.setInt(pst, ++i, status);
      DatabaseUtils.setInt(pst, ++i, businessValue);
      DatabaseUtils.setInt(pst, ++i, complexity);
      DatabaseUtils.setInt(pst, ++i, targetRelease);
      DatabaseUtils.setInt(pst, ++i, targetSprint);
      DatabaseUtils.setInt(pst, ++i, loeRemaining);
      DatabaseUtils.setInt(pst, ++i, assignedPriority);
      pst.execute();
      this.id = DatabaseUtils.getCurrVal(db, "tasklog_id_seq", -1);
      pst.close();
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  public boolean delete(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("TaskLog ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "DELETE from tasklog " +
            "WHERE id = ? ");
    pst.setInt(1, this.getId());
    pst.execute();
    pst.close();
    return true;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    taskId = rs.getInt("task_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    priority = rs.getInt("priority");
    dueDate = rs.getTimestamp("duedate");
    complete = rs.getBoolean("complete");
    estimatedLOE = rs.getDouble("estimatedloe");
    estimatedLOEType = DatabaseUtils.getInt(rs, "estimatedloetype");
    owner = DatabaseUtils.getInt(rs, "owner");
    completeDate = rs.getTimestamp("completedate");
    categoryId = DatabaseUtils.getInt(rs, "category_id");
    functionalArea = DatabaseUtils.getInt(rs, "functional_area");
    status = DatabaseUtils.getInt(rs, "status");
    businessValue = DatabaseUtils.getInt(rs, "business_value");
    complexity = DatabaseUtils.getInt(rs, "complexity");
    targetRelease = DatabaseUtils.getInt(rs, "target_release");
    targetSprint = DatabaseUtils.getInt(rs, "target_sprint");
    loeRemaining = DatabaseUtils.getInt(rs, "loe_remaining");
    assignedPriority = DatabaseUtils.getInt(rs, "assigned_priority");
    // Other items
    if (entered != null) {
      float ageCheck = ((System.currentTimeMillis() - entered.getTime()) / 86400000);
      age = Math.round(ageCheck);
    }
  }

}