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

package com.concursive.connect.web.modules.plans.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * An AssignmentFolder represents a folder in which assignments can go in
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 24, 2003
 */
public class AssignmentFolder extends GenericBean {
  private int id = -1;
  private int projectId = -1;
  private int requirementId = -1;
  private int parentId = -1;
  private String name = null;
  private String description = null;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  //Used for displaying a tree of folders
  private boolean treeOpen = false;
  private int displayLevel = 0;
  private boolean levelOpen = false;
  private AssignmentFolderList folders = new AssignmentFolderList();
  private AssignmentList assignments = new AssignmentList();
  private int indent = -1;
  private int prevIndent = -1;
  private int prevMapId = -1;


  /**
   * Description of the Method
   */
  public AssignmentFolder() {
  }


  /**
   * Constructor for the AssignmentFolder object
   *
   * @param db        Description of the Parameter
   * @param folderId  Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public AssignmentFolder(Connection db, int folderId, int projectId) throws SQLException {
    this.setProjectId(projectId);
    queryRecord(db, folderId);
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public AssignmentFolder(ResultSet rs) throws SQLException {
    buildRecord(rs);
    folders.setRequirementId(requirementId);
    folders.setParentId(id);
    assignments.setRequirementId(requirementId);
    assignments.setFolderId(id);
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param folderId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int folderId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM project_assignments_folder " +
            "WHERE folder_id = ? ");
    if (projectId > -1) {
      sql.append("AND requirement_id IN (SELECT requirement_id FROM project_requirements WHERE project_id = ?) ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, folderId);
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Folder record not found.");
    }
  }


  /**
   * Sets the id attribute of the AssignmentFolder object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the AssignmentFolder object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the requirementId attribute of the AssignmentFolder object
   *
   * @param tmp The new requirementId value
   */
  public void setRequirementId(int tmp) {
    this.requirementId = tmp;
  }


  /**
   * Sets the requirementId attribute of the AssignmentFolder object
   *
   * @param tmp The new requirementId value
   */
  public void setRequirementId(String tmp) {
    this.requirementId = Integer.parseInt(tmp);
  }


  /**
   * Sets the projectId attribute of the AssignmentFolder object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the AssignmentFolder object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the parentId attribute of the AssignmentFolder object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(int tmp) {
    this.parentId = tmp;
  }


  /**
   * Sets the parentId attribute of the AssignmentFolder object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(String tmp) {
    this.parentId = Integer.parseInt(tmp);
  }


  /**
   * Sets the name attribute of the AssignmentFolder object
   *
   * @param tmp The new name value
   */
  public void setName(String tmp) {
    this.name = tmp;
  }


  /**
   * Sets the description attribute of the AssignmentFolder object
   *
   * @param tmp The new description value
   */
  public void setDescription(String tmp) {
    this.description = tmp;
  }


  /**
   * Sets the entered attribute of the AssignmentFolder object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the AssignmentFolder object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the AssignmentFolder object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the AssignmentFolder object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the AssignmentFolder object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the AssignmentFolder object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the AssignmentFolder object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the AssignmentFolder object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.setModifiedBy(Integer.parseInt(tmp));
  }


  /**
   * Sets the treeOpen attribute of the AssignmentFolder object
   *
   * @param tmp The new treeOpen value
   */
  public void setTreeOpen(boolean tmp) {
    this.treeOpen = tmp;
  }


  /**
   * Sets the displayLevel attribute of the AssignmentFolder object
   *
   * @param tmp The new displayLevel value
   */
  public void setDisplayLevel(int tmp) {
    this.displayLevel = tmp;
  }


  /**
   * Sets the levelOpen attribute of the AssignmentFolder object
   *
   * @param tmp The new levelOpen value
   */
  public void setLevelOpen(boolean tmp) {
    this.levelOpen = tmp;
  }


  /**
   * Sets the indent attribute of the AssignmentFolder object
   *
   * @param tmp The new indent value
   */
  public void setIndent(int tmp) {
    this.indent = tmp;
  }


  /**
   * Sets the indent attribute of the AssignmentFolder object
   *
   * @param tmp The new indent value
   */
  public void setIndent(String tmp) {
    this.indent = Integer.parseInt(tmp);
  }


  /**
   * Sets the prevIndent attribute of the AssignmentFolder object
   *
   * @param tmp The new prevIndent value
   */
  public void setPrevIndent(int tmp) {
    this.prevIndent = tmp;
  }


  /**
   * Sets the prevIndent attribute of the AssignmentFolder object
   *
   * @param tmp The new prevIndent value
   */
  public void setPrevIndent(String tmp) {
    this.prevIndent = Integer.parseInt(tmp);
  }


  /**
   * Sets the prevMapId attribute of the AssignmentFolder object
   *
   * @param tmp The new prevMapId value
   */
  public void setPrevMapId(int tmp) {
    this.prevMapId = tmp;
  }


  /**
   * Sets the prevMapId attribute of the AssignmentFolder object
   *
   * @param tmp The new prevMapId value
   */
  public void setPrevMapId(String tmp) {
    this.prevMapId = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the AssignmentFolder object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the requirementId attribute of the AssignmentFolder object
   *
   * @return The requirementId value
   */
  public int getRequirementId() {
    return requirementId;
  }


  /**
   * Gets the projectId attribute of the AssignmentFolder object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Gets the parentId attribute of the AssignmentFolder object
   *
   * @return The parentId value
   */
  public int getParentId() {
    return parentId;
  }


  /**
   * Gets the name attribute of the AssignmentFolder object
   *
   * @return The name value
   */
  public String getName() {
    return name;
  }


  /**
   * Gets the description attribute of the AssignmentFolder object
   *
   * @return The description value
   */
  public String getDescription() {
    return description;
  }


  /**
   * Gets the entered attribute of the AssignmentFolder object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredString attribute of the AssignmentFolder object
   *
   * @return The enteredString value
   */
  public String getEnteredString() {
    try {
      return DateFormat.getDateInstance(DateFormat.SHORT).format(entered);
    } catch (NullPointerException e) {
    }
    return ("");
  }


  /**
   * Gets the enteredDateTimeString attribute of the AssignmentFolder object
   *
   * @return The enteredDateTimeString value
   */
  public String getEnteredDateTimeString() {
    try {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(entered);
    } catch (NullPointerException e) {
    }
    return ("");
  }


  /**
   * Gets the enteredBy attribute of the AssignmentFolder object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the AssignmentFolder object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedString attribute of the AssignmentFolder object
   *
   * @return The modifiedString value
   */
  public String getModifiedString() {
    if (modified != null) {
      return modified.toString();
    } else {
      return "";
    }
  }


  /**
   * Gets the modifiedBy attribute of the AssignmentFolder object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the treeOpen attribute of the AssignmentFolder object
   *
   * @return The treeOpen value
   */
  public boolean getTreeOpen() {
    return treeOpen;
  }


  /**
   * Gets the displayLevel attribute of the AssignmentFolder object
   *
   * @return The displayLevel value
   */
  public int getDisplayLevel() {
    return displayLevel;
  }


  /**
   * Gets the levelOpen attribute of the AssignmentFolder object
   *
   * @return The levelOpen value
   */
  public boolean getLevelOpen() {
    return levelOpen;
  }


  /**
   * Gets the folders attribute of the AssignmentFolder object
   *
   * @return The folders value
   */
  public AssignmentFolderList getFolders() {
    return folders;
  }


  /**
   * Gets the assignments attribute of the AssignmentFolder object
   *
   * @return The assignments value
   */
  public AssignmentList getAssignments() {
    return assignments;
  }


  /**
   * Gets the indent attribute of the AssignmentFolder object
   *
   * @return The indent value
   */
  public int getIndent() {
    return indent;
  }


  /**
   * Gets the prevIndent attribute of the AssignmentFolder object
   *
   * @return The prevIndent value
   */
  public int getPrevIndent() {
    return prevIndent;
  }


  /**
   * Gets the prevMapId attribute of the AssignmentFolder object
   *
   * @return The prevMapId value
   */
  public int getPrevMapId() {
    return prevMapId;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("folder_id");
    parentId = DatabaseUtils.getInt(rs, "parent_id");
    requirementId = rs.getInt("requirement_id");
    name = rs.getString("name");
    description = rs.getString("description");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
  }


  /**
   * Gets the valid attribute of the AssignmentFolder object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (!StringUtils.hasText(name)) {
      errors.put("nameError", "Required field");
    }
    return (!hasErrors());
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
    StringBuffer sql = new StringBuffer();
    sql.append(
        "INSERT INTO project_assignments_folder " +
            "(requirement_id, parent_id, name, description, ");
    if (entered != null) {
      sql.append("entered, ");
    }
    if (modified != null) {
      sql.append("modified, ");
    }
    sql.append("enteredBy, modifiedBy) ");
    sql.append("VALUES (?, ?, ?, ?, ");
    if (entered != null) {
      sql.append("?, ");
    }
    if (modified != null) {
      sql.append("?, ");
    }
    sql.append("?, ?) ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    DatabaseUtils.setInt(pst, ++i, requirementId);
    DatabaseUtils.setInt(pst, ++i, parentId);
    pst.setString(++i, name);
    pst.setString(++i, description);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, modifiedBy);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_assignmen_folder_id_seq", -1);
    //Record the position of this entry
    RequirementMapItem mapItem = new RequirementMapItem();
    mapItem.setProjectId(projectId);
    mapItem.setRequirementId(requirementId);
    mapItem.setFolderId(id);
    mapItem.setIndent(indent);
    mapItem.setPrevIndent(prevIndent);
    mapItem.setPrevMapId(prevMapId);
    mapItem.append(db);
    indent = mapItem.getIndent();
    prevIndent = mapItem.getIndent();
    prevMapId = mapItem.getId();
    return true;
  }


  /**
   * Gets the assignmentIterator attribute of the AssignmentFolder object
   *
   * @return The assignmentIterator value
   */
  public Iterator getAssignmentIterator() {
    ArrayList assignmentList = new ArrayList();
    //TODO: add all the assignments and all the assignments in the folders
    return assignmentList.iterator();
  }


  /**
   * Gets the planIterator attribute of the AssignmentFolder object
   *
   * @return The planIterator value
   */
  public Iterator getPlanIterator() {
    ArrayList itemList = new ArrayList();
    addPlanItems(itemList, this, 1);
    return itemList.iterator();
  }


  /**
   * Adds a feature to the PlanItems attribute of the AssignmentFolder object
   *
   * @param itemList     The feature to be added to the PlanItems attribute
   * @param folderObject The feature to be added to the PlanItems attribute
   * @param level        The feature to be added to the PlanItems attribute
   */
  private void addPlanItems(ArrayList itemList, AssignmentFolder folderObject, int level) {
    Iterator assignments = folderObject.getAssignments().iterator();
    while (assignments.hasNext()) {
      Assignment thisAssignment = (Assignment) assignments.next();
      if (assignments.hasNext() ||
          (!assignments.hasNext() && folderObject.getFolders().size() > 0)) {
        thisAssignment.setLevelOpen(true);
      }
      thisAssignment.setDisplayLevel(level);
      itemList.add(thisAssignment);
    }
    Iterator folders = folderObject.getFolders().iterator();
    while (folders.hasNext()) {
      AssignmentFolder thisFolder = (AssignmentFolder) folders.next();
      thisFolder.setLevelOpen(folders.hasNext());
      thisFolder.setDisplayLevel(level);
      itemList.add(thisFolder);
      addPlanItems(itemList, thisFolder, level + 1);
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db) throws SQLException {
    if (this.getId() == -1 || this.requirementId == -1) {
      throw new SQLException("Assignment Folder ID was not specified");
    }
    int recordCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      //Remove the mapped item
      RequirementMapItem mapItem = new RequirementMapItem();
      mapItem.setProjectId(projectId);
      mapItem.setRequirementId(requirementId);
      mapItem.setFolderId(id);
      mapItem.remove(db);
      //Move assignments to the left
      PreparedStatement pst = db.prepareStatement(
          "UPDATE project_assignments " +
              "SET folder_id = ? " +
              "WHERE folder_id = ? ");
      DatabaseUtils.setInt(pst, 1, parentId);
      pst.setInt(2, id);
      pst.executeUpdate();
      pst.close();
      //Move other folders to the left
      pst = db.prepareStatement(
          "UPDATE project_assignments_folder " +
              "SET parent_id = ? " +
              "WHERE parent_id = ? ");
      DatabaseUtils.setInt(pst, 1, parentId);
      pst.setInt(2, id);
      pst.executeUpdate();
      pst.close();
      //Delete this folder (which should have nothing in it now)
      pst = db.prepareStatement(
          "DELETE FROM project_assignments_folder " +
              "WHERE folder_id = ? ");
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
      System.out.println(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (recordCount == 0) {
      errors.put("actionError", "Folder could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }


  /**
   * Description of the Method
   *
   * @param db      Description of the Parameter
   * @param context Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db, ActionContext context) throws SQLException {
    return this.update(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (this.getId() == -1 || this.projectId == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_assignments_folder " +
            "SET name = ?, description = ?, " +
            "modifiedBy = ?, modified = CURRENT_TIMESTAMP, parent_id = ? " +
            "WHERE folder_id = ? " +
            "AND modified = ? ");
    int i = 0;
    pst.setString(++i, name);
    pst.setString(++i, description);
    pst.setInt(++i, this.getModifiedBy());
    DatabaseUtils.setInt(pst, ++i, parentId);
    pst.setInt(++i, this.getId());
    pst.setTimestamp(++i, modified);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }
}

