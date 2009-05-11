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

package com.concursive.connect.web.modules.documents.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a file system folder in which files can be organized within
 *
 * @author matt rajkowski
 * @version $Id$
 * @created April 9, 2003
 */
public class FileFolder extends GenericBean {

  private static Log LOG = LogFactory.getLog(FileFolder.class);

  public static final int VIEW_LIBRARY = -1;
  public static final int VIEW_GALLERY = 1;
  public static final int VIEW_SLIDESHOW = 2;

  // Object Properties
  private int id = -1;
  private int linkModuleId = -1;
  private int linkItemId = -1;
  private int parentId = -1;
  private String subject = null;
  private String description = null;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  // Helper Properties
  private int itemCount = -1;
  private FileFolderList subFolders = null;
  // Display Properties
  private int display = VIEW_LIBRARY;
  private int level = -1;


  /**
   * Constructor for the FileFolder object
   */
  public FileFolder() {
  }


  /**
   * Constructor for the FileFolder object
   *
   * @param db       Description of the Parameter
   * @param folderId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileFolder(Connection db, int folderId) throws SQLException {
    queryRecord(db, folderId);
  }


  /**
   * Constructor for the FileFolder object
   *
   * @param db           Description of the Parameter
   * @param folderId     Description of the Parameter
   * @param moduleItemId Description of the Parameter
   * @param moduleId     Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileFolder(Connection db, int folderId, int moduleItemId, int moduleId) throws SQLException {
    this.linkModuleId = moduleId;
    this.linkItemId = moduleItemId;
    queryRecord(db, folderId);
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param folderId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int folderId) throws SQLException {
    if (folderId == -1) {
      throw new SQLException("Invalid folder id");
    }
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM project_folders " +
            "WHERE folder_id > -1 ");
    if (folderId > -1) {
      sql.append("AND folder_id = ? ");
    }
    if (linkModuleId > -1) {
      sql.append("AND link_module_id = ? ");
    }
    if (linkItemId > -1) {
      sql.append("AND link_item_id = ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    if (folderId > -1) {
      pst.setInt(++i, folderId);
    }
    if (linkModuleId > -1) {
      pst.setInt(++i, linkModuleId);
    }
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
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
   * Constructor for the FileFolder object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public FileFolder(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Sets the linkModuleId attribute of the FileFolder object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int tmp) {
    linkModuleId = tmp;
  }


  /**
   * Sets the linkModuleId attribute of the FileFolder object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(String tmp) {
    linkModuleId = Integer.parseInt(tmp);
  }


  /**
   * Sets the linkItemId attribute of the FileFolder object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(int tmp) {
    linkItemId = tmp;
  }


  /**
   * Sets the linkItemId attribute of the FileFolder object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(String tmp) {
    linkItemId = Integer.parseInt(tmp);
  }


  /**
   * Sets the id attribute of the FileFolder object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the FileFolder object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the parentId attribute of the FileFolder object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(int tmp) {
    this.parentId = tmp;
  }


  /**
   * Sets the parentId attribute of the FileFolder object
   *
   * @param tmp The new parentId value
   */
  public void setParentId(String tmp) {
    this.parentId = Integer.parseInt(tmp);
  }


  /**
   * Sets the subject attribute of the FileFolder object
   *
   * @param tmp The new subject value
   */
  public void setSubject(String tmp) {
    this.subject = tmp;
  }


  /**
   * Sets the description attribute of the FileFolder object
   *
   * @param tmp The new description value
   */
  public void setDescription(String tmp) {
    this.description = tmp;
  }


  /**
   * Sets the entered attribute of the FileFolder object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the FileFolder object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the FileFolder object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the FileFolder object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the FileFolder object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the FileFolder object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the FileFolder object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the FileFolder object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the itemCount attribute of the FileFolder object
   *
   * @param tmp The new itemCount value
   */
  public void setItemCount(int tmp) {
    this.itemCount = tmp;
  }


  /**
   * Sets the subFolders attribute of the FileFolder object
   *
   * @param tmp The new subFolders value
   */
  public void setSubFolders(FileFolderList tmp) {
    this.subFolders = tmp;
  }


  /**
   * Sets the display attribute of the FileFolder object
   *
   * @param tmp The new display value
   */
  public void setDisplay(int tmp) {
    this.display = tmp;
  }


  /**
   * Sets the display attribute of the FileFolder object
   *
   * @param tmp The new display value
   */
  public void setDisplay(String tmp) {
    this.display = Integer.parseInt(tmp);
  }


  /**
   * Sets the level attribute of the FileFolder object
   *
   * @param tmp The new level value
   */
  public void setLevel(int tmp) {
    this.level = tmp;
  }


  /**
   * Sets the level attribute of the FileFolder object
   *
   * @param tmp The new level value
   */
  public void setLevel(String tmp) {
    this.level = Integer.parseInt(tmp);
  }


  /**
   * Gets the level attribute of the FileFolder object
   *
   * @return The level value
   */
  public int getLevel() {
    return level;
  }


  /**
   * Gets the linkModuleId attribute of the FileFolder object
   *
   * @return The linkModuleId value
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }


  /**
   * Gets the linkItemId attribute of the FileFolder object
   *
   * @return The linkItemId value
   */
  public int getLinkItemId() {
    return linkItemId;
  }


  /**
   * Gets the id attribute of the FileFolder object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the parentId attribute of the FileFolder object
   *
   * @return The parentId value
   */
  public int getParentId() {
    return parentId;
  }


  /**
   * Gets the subject attribute of the FileFolder object
   *
   * @return The subject value
   */
  public String getSubject() {
    return subject;
  }


  /**
   * Gets the description attribute of the FileFolder object
   *
   * @return The description value
   */
  public String getDescription() {
    return description;
  }


  /**
   * Gets the entered attribute of the FileFolder object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredString attribute of the FileFolder object
   *
   * @return The enteredString value
   */
  public String getEnteredString() {
    try {
      return DateFormat.getDateInstance(3).format(entered);
    } catch (NullPointerException e) {
    }
    return "";
  }


  /**
   * Gets the enteredDateTimeString attribute of the FileFolder object
   *
   * @return The enteredDateTimeString value
   */
  public String getEnteredDateTimeString() {
    try {
      return DateFormat.getDateTimeInstance(3, 3).format(entered);
    } catch (NullPointerException e) {
    }
    return "";
  }


  /**
   * Gets the enteredBy attribute of the FileFolder object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the FileFolder object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedBy attribute of the FileFolder object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the itemCount attribute of the FileFolder object
   *
   * @return The itemCount value
   */
  public int getItemCount() {
    return itemCount;
  }


  /**
   * Gets the subFolders attribute of the FileFolder object
   *
   * @return The subFolders value
   */
  public FileFolderList getSubFolders() {
    return subFolders;
  }


  /**
   * Gets the display attribute of the FileFolder object
   *
   * @return The display value
   */
  public int getDisplay() {
    return display;
  }


  /**
   * Inserts a new folder
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
        "INSERT INTO project_folders " +
            "(link_module_id, link_item_id, subject, description, parent_id, ");
    if (entered != null) {
      sql.append("entered, ");
    }
    if (modified != null) {
      sql.append("modified, ");
    }
    sql.append(
        "enteredby, modifiedby, display) " +
            "VALUES (?, ?, ?, ?, ?, ");
    if (entered != null) {
      sql.append("?, ");
    }
    if (modified != null) {
      sql.append("?, ");
    }
    sql.append("?, ?, ?) ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setInt(++i, linkModuleId);
    pst.setInt(++i, linkItemId);
    pst.setString(++i, subject);
    pst.setString(++i, description);
    DatabaseUtils.setInt(pst, ++i, parentId);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    if (modified != null) {
      pst.setTimestamp(++i, modified);
    }
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, modifiedBy);
    DatabaseUtils.setInt(pst, ++i, display);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_folders_folder_id_seq", -1);
    if (subFolders != null) {
      Iterator subI = subFolders.iterator();
      while (subI.hasNext()) {
        FileFolder thisFolder = (FileFolder) subI.next();
        thisFolder.setLinkItemId(linkItemId);
        thisFolder.setParentId(id);
        thisFolder.setId(-1);
      }
    }
    return true;
  }


  /**
   * Updates the folder meta data
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    int resultCount = 0;
    if (!isValid()) {
      return -1;
    }
    String sql =
        "UPDATE project_folders " +
            "SET subject = ?, description = ?, display = ? " +
            "WHERE folder_id = ? " +
            "AND modified = ?";
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setString(++i, subject);
    pst.setString(++i, description);
    DatabaseUtils.setInt(pst, ++i, display);
    pst.setInt(++i, this.getId());
    pst.setTimestamp(++i, this.getModified());
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }


  /**
   * Deletes the folder and any enclosed files
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db, String baseFilePath) throws SQLException {
    if (id == -1) {
      return false;
    }
    boolean result = false;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Build a list of files to delete
      FileItemList fileItemList = new FileItemList();
      fileItemList.setFolderId(id);
      fileItemList.buildList(db);
      fileItemList.delete(db, baseFilePath);
      // Build a list of folders to delete
      FileFolderList folderList = new FileFolderList();
      folderList.setParentId(id);
      folderList.buildList(db);
      folderList.delete(db, baseFilePath);
      // Delete this folder
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_folders " +
              "WHERE folder_id = ?");
      pst.setInt(1, id);
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
      result = true;
    } catch (Exception e) {
      LOG.error("Could not delete folder", e);
      if (commit) {
        db.rollback();
      }
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return result;
  }


  /**
   * Gets the valid attribute of the FileFolder object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (linkModuleId == -1 || linkItemId == -1) {
      errors.put("actionError", "Id not specified");
    }
    if (!StringUtils.hasText(subject)) {
      errors.put("subjectError", "Required field");
    }
    return !hasErrors();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("folder_id");
    linkModuleId = rs.getInt("link_module_id");
    linkItemId = rs.getInt("link_item_id");
    subject = rs.getString("subject");
    description = rs.getString("description");
    parentId = DatabaseUtils.getInt(rs, "parent_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    display = DatabaseUtils.getInt(rs, "display");
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildItemCount(Connection db) throws SQLException {
    itemCount = 0;
    //Get number of folders in this folder
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) AS record_count " +
            "FROM project_folders " +
            "WHERE parent_id = ? ");
    pst.setInt(1, this.getId());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      itemCount += rs.getInt("record_count");
    }
    rs.close();
    pst.close();
    //Get number of files in this folder
    pst = db.prepareStatement(
        "SELECT COUNT(*) AS record_count " +
            "FROM project_files " +
            "WHERE folder_id = ? ");
    pst.setInt(1, this.getId());
    rs = pst.executeQuery();
    if (rs.next()) {
      itemCount += rs.getInt("record_count");
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param hierarchy Description of the Parameter
   * @param currentId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void buildHierarchy(Connection db, Map hierarchy, int currentId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT parent_id, subject, display " +
            "FROM project_folders " +
            "WHERE folder_id = ? ");
    pst.setInt(1, currentId);
    ResultSet rs = pst.executeQuery();
    int parentId = 0;
    String subject = null;
    int display = -1;
    if (rs.next()) {
      parentId = DatabaseUtils.getInt(rs, "parent_id");
      subject = rs.getString("subject");
      display = DatabaseUtils.getInt(rs, "display");
    }
    rs.close();
    pst.close();
    hierarchy.put(new Integer(currentId), new String[]{subject, String.valueOf(display)});
    if (parentId > -1) {
      FileFolder.buildHierarchy(db, hierarchy, parentId);
    }
  }


  /**
   * Description of the Method
   *
   * @param db          Description of the Parameter
   * @param newParentId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void updateParentId(Connection db, int newParentId) throws SQLException {
    // Some validations
    if (id == -1) {
      throw new SQLException("ID not specified");
    }
    if (newParentId == id) {
      throw new SQLException("ID cannot be the same");
    }
    // If the new parent is a child...
    FileFolderHierarchy thisHierarchy = new FileFolderHierarchy();
    thisHierarchy.setLinkModuleId(linkModuleId);
    thisHierarchy.setLinkItemId(linkItemId);
    thisHierarchy.build(db, id);
    if (thisHierarchy.getHierarchy().hasFolder(newParentId)) {
      buildSubFolders(db);
      for (FileFolder childFolder : getSubFolders()) {
        childFolder.updateParentId(db, getParentId());
      }
    }
    // Update this folder
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_folders " +
            "SET parent_id = ? " +
            "WHERE folder_id = ? ");
    int i = 0;
    if (newParentId > 0) {
      pst.setInt(++i, newParentId);
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    pst.setInt(++i, id);
    pst.execute();
    pst.close();
    // Reflect the change in the property
    parentId = newParentId;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildSubFolders(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ID not specified");
    }
    subFolders = new FileFolderList();
    subFolders.setLinkModuleId(this.getLinkModuleId());
    subFolders.setParentId(this.getId());
    subFolders.buildList(db);
  }
}

