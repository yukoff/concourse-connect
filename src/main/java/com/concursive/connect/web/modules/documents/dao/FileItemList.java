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
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.badges.dao.BadgeLogoFile;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryLogoFile;
import com.concursive.connect.web.modules.wiki.dao.WikiFile;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * A list of files stored in the filesystem and referenced in a database
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 8, 2002
 */
public class FileItemList extends ArrayList<FileItem> {

  private static Log LOG = LogFactory.getLog(FileItemList.class);

  // filters
  protected PagedListInfo pagedListInfo = null;
  protected int linkModuleId = -1;
  protected int linkItemId = -1;
  protected int folderId = -1;
  protected int owner = -1;
  protected String ownerIdRange = null;
  protected String fileLibraryPath = null;
  protected boolean topLevelOnly = false;
  protected boolean webImageFormatOnly = false;
  protected int defaultFile = Constants.UNDEFINED;
  protected int enabled = Constants.UNDEFINED;
  protected String filename = null;
  protected int ignoreId = -1;
  protected int featuredFilesOnly = Constants.UNDEFINED;
  // these filters can only be used when the linkModuleId maps the linkItemId to a project
  protected int publicProjectFiles = Constants.UNDEFINED;
  protected int projectCategoryId = -1;
  protected int forProjectUser = -1;

  // calendar
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;
  protected String modifiedYearMonth = null;
  protected Timestamp startOfCurrentMonth = null;
  protected Timestamp startOfNextMonth = null;

  // html select
  protected String htmlJsEvent = "";


  /**
   * Gets the enabled attribute of the FileItemList object
   *
   * @return The enabled value
   */
  public int getEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled attribute of the FileItemList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(int tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the FileItemList object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = Integer.parseInt(tmp);
  }


  /**
   * Gets the htmlJsEvent attribute of the FileItemList object
   *
   * @return The htmlJsEvent value
   */
  public String getHtmlJsEvent() {
    return htmlJsEvent;
  }


  /**
   * Sets the htmlJsEvent attribute of the FileItemList object
   *
   * @param tmp The new htmlJsEvent value
   */
  public void setHtmlJsEvent(String tmp) {
    this.htmlJsEvent = tmp;
  }


  /**
   * Constructor for the FileItemList object
   */
  public FileItemList() {
  }


  /**
   * Gets the defaultFile attribute of the FileItemList object
   *
   * @return The defaultFile value
   */
  public int getDefaultFile() {
    return defaultFile;
  }


  /**
   * Sets the defaultFile attribute of the FileItemList object
   *
   * @param tmp The new defaultFile value
   */
  public void setDefaultFile(int tmp) {
    this.defaultFile = tmp;
  }


  /**
   * Sets the defaultFile attribute of the FileItemList object
   *
   * @param tmp The new defaultFile value
   */
  public void setDefaultFile(String tmp) {
    this.defaultFile = Integer.parseInt(tmp);
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  /**
   * Sets the linkModuleId attribute of the FileItemList object
   *
   * @param tmp The new linkModuleId value
   */
  public void setLinkModuleId(int tmp) {
    this.linkModuleId = tmp;
  }

  public void setLinkModuleId(String tmp) {
    this.linkModuleId = Integer.parseInt(tmp);
  }


  /**
   * Gets the linkModuleId attribute of the FileItemList object
   *
   * @return The linkModuleId value
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }


  /**
   * Sets the linkItemId attribute of the FileItemList object
   *
   * @param tmp The new linkItemId value
   */
  public void setLinkItemId(int tmp) {
    this.linkItemId = tmp;
  }

  public void setLinkItemId(String tmp) {
    this.linkItemId = Integer.parseInt(tmp);
  }


  /**
   * Gets the linkItemId attribute of the FileItemList object
   *
   * @return The linkItemId value
   */
  public int getLinkItemId() {
    return linkItemId;
  }


  /**
   * Sets the pagedListInfo attribute of the FileItemList object
   *
   * @param pagedListInfo The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }


  /**
   * Sets the owner attribute of the FileItemList object
   *
   * @param tmp The new owner value
   */
  public void setOwner(int tmp) {
    this.owner = tmp;
  }

  public void setEnteredBy(int tmp) {
    this.owner = tmp;
  }

  public void setEnteredBy(String tmp) {
    this.owner = Integer.parseInt(tmp);
  }


  /**
   * Sets the ownerIdRange attribute of the FileItemList object
   *
   * @param tmp The new ownerIdRange value
   */
  public void setOwnerIdRange(String tmp) {
    this.ownerIdRange = tmp;
  }


  /**
   * Sets the folderId attribute of the FileItemList object
   *
   * @param tmp The new folderId value
   */
  public void setFolderId(int tmp) {
    this.folderId = tmp;
  }

  public void setFolderId(String tmp) {
    this.folderId = Integer.parseInt(tmp);
  }


  /**
   * Sets the fileLibraryPath attribute of the FileItemList object
   *
   * @param tmp The new fileLibraryPath value
   */
  public void setFileLibraryPath(String tmp) {
    this.fileLibraryPath = tmp;
  }


  /**
   * Sets the topLevelOnly attribute of the FileItemList object
   *
   * @param tmp The new topLevelOnly value
   */
  public void setTopLevelOnly(boolean tmp) {
    this.topLevelOnly = tmp;
  }

  public void setTopLevelOnly(String tmp) {
    this.topLevelOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the webImageFormatOnly attribute of the FileItemList object
   *
   * @return The webImageFormatOnly value
   */
  public boolean getWebImageFormatOnly() {
    return webImageFormatOnly;
  }


  /**
   * Sets the webImageFormatOnly attribute of the FileItemList object
   *
   * @param tmp The new webImageFormatOnly value
   */
  public void setWebImageFormatOnly(boolean tmp) {
    this.webImageFormatOnly = tmp;
  }


  /**
   * Sets the webImageFormatOnly attribute of the FileItemList object
   *
   * @param tmp The new webImageFormatOnly value
   */
  public void setWebImageFormatOnly(String tmp) {
    this.webImageFormatOnly = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the alertRangeStart attribute of the FileItemList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(java.sql.Timestamp tmp) {
    this.alertRangeStart = tmp;
  }


  /**
   * Sets the alertRangeStart attribute of the FileItemList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(String tmp) {
    this.alertRangeStart = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the alertRangeEnd attribute of the FileItemList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(java.sql.Timestamp tmp) {
    this.alertRangeEnd = tmp;
  }


  /**
   * Sets the alertRangeEnd attribute of the FileItemList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(String tmp) {
    this.alertRangeEnd = DatabaseUtils.parseTimestamp(tmp);
  }

  public void setModifiedYearMonth(String modifiedYearMonth) {
    this.modifiedYearMonth = modifiedYearMonth;
    int year = Integer.parseInt(modifiedYearMonth.substring(0, modifiedYearMonth.indexOf("-")));
    int month = Integer.parseInt(modifiedYearMonth.substring(modifiedYearMonth.indexOf("-") + 1)) - 1; //0 based month

    // Calculate the start of the month
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, 1, 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    startOfCurrentMonth = new Timestamp(calendar.getTimeInMillis());

    // Calculate the end of the month
    calendar.set(year, month, 1, 23, 59, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    calendar.add(Calendar.MONTH, 1);
    startOfNextMonth = new Timestamp(calendar.getTimeInMillis());
  }


  /**
   * Sets the forProjectUser attribute of the FileItemList object
   *
   * @param tmp The new forProjectUser value
   */
  public void setForProjectUser(int tmp) {
    this.forProjectUser = tmp;
  }


  /**
   * Sets the forProjectUser attribute of the FileItemList object
   *
   * @param tmp The new forProjectUser value
   */
  public void setForProjectUser(String tmp) {
    this.forProjectUser = Integer.parseInt(tmp);
  }


  /**
   * Gets the pagedListInfo attribute of the FileItemList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Gets the owner attribute of the FileItemList object
   *
   * @return The owner value
   */
  public int getOwner() {
    return owner;
  }


  /**
   * Gets the ownerIdRange attribute of the FileItemList object
   *
   * @return The ownerIdRange value
   */
  public String getOwnerIdRange() {
    return ownerIdRange;
  }


  /**
   * Gets the folderId attribute of the FileItemList object
   *
   * @return The folderId value
   */
  public int getFolderId() {
    return folderId;
  }


  /**
   * Gets the fileLibraryPath attribute of the FileItemList object
   *
   * @return The fileLibraryPath value
   */
  public String getFileLibraryPath() {
    return fileLibraryPath;
  }


  /**
   * Gets the fileSize of all of the FileItem objects within this collection
   *
   * @return The fileSize value
   */
  public long getFileSize() {
    long fileSize = 0;
    for (FileItem thisItem : this) {
      fileSize += thisItem.getSize();
    }
    return fileSize;
  }


  /**
   * Gets the alertRangeStart attribute of the FileItemList object
   *
   * @return The alertRangeStart value
   */
  public java.sql.Timestamp getAlertRangeStart() {
    return alertRangeStart;
  }


  /**
   * Gets the alertRangeEnd attribute of the FileItemList object
   *
   * @return The alertRangeEnd value
   */
  public java.sql.Timestamp getAlertRangeEnd() {
    return alertRangeEnd;
  }


  /**
   * Gets the forProjectUser attribute of the FileItemList object
   *
   * @return The forProjectUser value
   */
  public int getForProjectUser() {
    return forProjectUser;
  }

  public int getIgnoreId() {
    return ignoreId;
  }

  public void setIgnoreId(int ignoreId) {
    this.ignoreId = ignoreId;
  }

  public void setIgnoreId(String tmp) {
    this.ignoreId = Integer.parseInt(tmp);
  }

  /**
   * @return the featuredFilesOnly
   */
  public int getFeaturedFilesOnly() {
    return featuredFilesOnly;
  }


  /**
   * @param featuredFilesOnly the featuredFilesOnly to set
   */
  public void setFeaturedFilesOnly(int featuredFilesOnly) {
    this.featuredFilesOnly = featuredFilesOnly;
  }

  public void setFeaturedFilesOnly(String featuredFilesOnly) {
    this.featuredFilesOnly = Integer.parseInt(featuredFilesOnly);
  }

  /**
   * @return the publicProjectFiles
   */
  public int getPublicProjectFiles() {
    return publicProjectFiles;
  }


  /**
   * @param publicProjectFiles the publicProjectFiles to set
   */
  public void setPublicProjectFiles(int publicProjectFiles) {
    this.publicProjectFiles = publicProjectFiles;
  }

  public void setPublicProjectFiles(String publicProjectFiles) {
    this.publicProjectFiles = Integer.parseInt(publicProjectFiles);
  }

  /**
   * @return the projectCategoryId
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }


  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
  }

  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_files f " +
            "WHERE f.item_id > -1 ");
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

  public void buildList(Connection db) throws SQLException {
    queryList(db, FileItem.class);
  }

  public void queryList(Connection db, Class objectClass) throws SQLException {
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
            "FROM project_files f " +
            "WHERE f.item_id > -1 ");
    createFilter(sqlFilter);
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
            "AND lower(f.subject) < ? ");
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
      pagedListInfo.setDefaultSort("f.subject, f.client_filename", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY f.default_file desc, f.item_id ");
    }
    //Build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    // TODO: Make the following work with SQL Server
    sqlSelect.append(
        "f.*, t.filename AS thumbnail " +
            "FROM project_files f " +
            "LEFT JOIN project_files_thumbnail t ON (f.item_id = t.item_id AND f.version = t.version AND t.filename = f.filename || 'TH') " +
            "WHERE f.item_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    // Process the result set
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      // Cast the item to a file item for storing
      Object thisItem = ObjectUtils.constructObject(objectClass, rs, "java.sql.ResultSet");
      ((FileItem) thisItem).setDirectory(fileLibraryPath);
      if (thisItem instanceof ProjectCategoryLogoFile) {
        this.add((ProjectCategoryLogoFile) thisItem);
      } else if (thisItem instanceof WikiFile) {
        this.add((WikiFile) thisItem);
      } else if (thisItem instanceof BadgeLogoFile) {
        this.add((BadgeLogoFile) thisItem);
      } else {
        this.add((FileItem) thisItem);
      }
    }
    rs.close();
    pst.close();
  }


  /**
   * Deletes all files in this list
   *
   * @param db           Description of Parameter
   * @param baseFilePath Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void delete(Connection db, String baseFilePath) throws SQLException {
    for (FileItem thisFile : this) {
      thisFile.delete(db, baseFilePath);
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
    if (linkModuleId > -1) {
      sqlFilter.append("AND f.link_module_id = ? ");
    }
    if (linkItemId > -1) {
      sqlFilter.append("AND f.link_item_id = ? ");
    }
    if (folderId > -1) {
      sqlFilter.append("AND f.folder_id = ? ");
    }
    if (owner != -1) {
      sqlFilter.append("AND f.enteredby = ? ");
    }
    if (ownerIdRange != null) {
      sqlFilter.append("AND f.enteredby IN (").append(ownerIdRange).append(") ");
    }
    if (topLevelOnly) {
      sqlFilter.append("AND f.folder_id IS NULL ");
    }
    if (webImageFormatOnly) {
      sqlFilter.append("AND (lower(f.client_filename) LIKE '%.gif' OR lower(f.client_filename) LIKE '%.jpg' OR lower(f.client_filename) LIKE '%.png') ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND f.modified >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND f.modified < ? ");
    }
    if (forProjectUser > -1) {
      sqlFilter.append(
          "AND (f.link_item_id in (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? AND status IS NULL) " +
              "OR f.link_item_id IN (SELECT project_id FROM projects WHERE (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL)) ");
    }
    if (defaultFile != Constants.UNDEFINED) {
      sqlFilter.append("AND f.default_file = ? ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND f.enabled = ? ");
    }
    if (filename != null) {
      sqlFilter.append("AND client_filename = ? ");
    }
    if (ignoreId > -1) {
      sqlFilter.append("AND f.item_id <> ? ");
    }
    if (featuredFilesOnly != Constants.UNDEFINED) {
      sqlFilter.append("AND f.featured_file = ? ");
    }
    if (publicProjectFiles != Constants.UNDEFINED) {
      sqlFilter.append("AND f.link_item_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL) ");
    }
    if (projectCategoryId != -1) {
      sqlFilter.append("AND f.link_item_id IN (SELECT project_id FROM projects WHERE category_id = ?) ");
    }
    if (modifiedYearMonth != null) {
      sqlFilter.append("AND ( f.modified >= ? AND f.modified < ? )");
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
    if (linkModuleId > -1) {
      pst.setInt(++i, linkModuleId);
    }
    if (linkItemId > -1) {
      pst.setInt(++i, linkItemId);
    }
    if (folderId > -1) {
      pst.setInt(++i, folderId);
    }
    if (owner != -1) {
      pst.setInt(++i, owner);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    if (forProjectUser > -1) {
      pst.setInt(++i, forProjectUser);
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, true);
    }
    if (defaultFile != Constants.UNDEFINED) {
      pst.setBoolean(++i, defaultFile == Constants.TRUE);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, enabled == Constants.TRUE);
    }
    if (filename != null) {
      pst.setString(++i, filename);
    }
    if (ignoreId > -1) {
      pst.setInt(++i, ignoreId);
    }
    if (featuredFilesOnly != Constants.UNDEFINED) {
      pst.setBoolean(++i, featuredFilesOnly == Constants.TRUE);
    }
    if (publicProjectFiles != Constants.UNDEFINED) {
      pst.setBoolean(++i, true);
    }
    if (projectCategoryId != -1) {
      pst.setInt(++i, projectCategoryId);
    }
    if (modifiedYearMonth != null) {
      pst.setTimestamp(++i, startOfCurrentMonth);
      pst.setTimestamp(++i, startOfNextMonth);
    }
    return i;
  }


  /**
   * Returns the number of fileItems that match the module and itemid
   *
   * @param db           Description of the Parameter
   * @param linkModuleId Description of the Parameter
   * @param linkItemId   Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int retrieveRecordCount(Connection db, int linkModuleId, int linkItemId) throws SQLException {
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT COUNT(*) as filecount " +
            "FROM project_files pf " +
            "WHERE pf.link_module_id = ? and pf.link_item_id = ? ");
    pst.setInt(1, linkModuleId);
    pst.setInt(2, linkItemId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("filecount");
    }
    rs.close();
    pst.close();
    return count;
  }


  /**
   * Checks to see if any of the files has the specified extension
   *
   * @param extension Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean hasFileType(String extension) {
    for (FileItem thisItem : this) {
      if (extension.equalsIgnoreCase(thisItem.getExtension())) {
        return true;
      }
    }
    return false;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public long queryFileSize(Connection db) throws SQLException {
    long recordSize = 0;
    StringBuffer sqlFilter = new StringBuffer();
    String sqlCount =
        "SELECT SUM(size) AS recordsize " +
            "FROM project_files f " +
            "WHERE f.item_id > -1 ";
    createFilter(sqlFilter);
    PreparedStatement pst = db.prepareStatement(
        sqlCount + sqlFilter.toString());
    int items = prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      recordSize = DatabaseUtils.getLong(rs, "recordsize", 0);
    }
    rs.close();
    pst.close();
    return recordSize;
  }


  /**
   * Gets the htmlSelectDefaultNone attribute of the FileItemList object
   *
   * @param selectName Description of the Parameter
   * @param currentKey Description of the Parameter
   * @param useDefault Description of the Parameter
   * @return The htmlSelectDefaultNone value
   */
  public String getHtmlSelectDefaultNone(String selectName, int currentKey, boolean useDefault) {
    HtmlSelect fileSelect = new HtmlSelect();
    fileSelect.addItem(-1, "-- None --");
    Iterator i = this.iterator();
    int defaultKey = -1;
    while (i.hasNext()) {
      FileItem thisItem = (FileItem) i.next();
      if (thisItem.getEnabled()) {
        // Only add the item if enabled
        if (useDefault && thisItem.getDefaultFile()) {
          defaultKey = thisItem.getId();
        }
        fileSelect.addItem(thisItem.getId(), StringUtils.toHtml(thisItem.getSubject()));
      } else {
        // Allow disabled items that match the current key
        if (thisItem.getId() == currentKey) {
          fileSelect.addItem(thisItem.getId(), StringUtils.toHtml(thisItem.getSubject()) + " (X)");
        }
      }
    }
    if (!(this.getHtmlJsEvent().equals(""))) {
      fileSelect.setJsEvent(this.getHtmlJsEvent());
    }
    if (currentKey > 0) {
      return fileSelect.getHtml(selectName, currentKey);
    } else {
      return fileSelect.getHtml(selectName, defaultKey);
    }
  }

  /**
   * The file attachment selector allows users to attach files to non-existent
   * records, for example when they are new.  This method converts the temp
   * files to the newly created or existing object.  Files that are not
   * converted will be deleted by the scheduler.
   *
   * @param db             Connection
   * @param linkModuleId   The module for the associated object
   * @param modifiedBy     The user who uploaded the temp files
   * @param id             The object id that is associated
   * @param attachmentList a comma separated list of ids to associate
   * @throws SQLException sql exception
   */
  public static void convertTempFiles(Connection db, int linkModuleId, int modifiedBy, int id, String attachmentList) throws SQLException {
    convertTempFiles(db, linkModuleId, modifiedBy, id, attachmentList, false);
  }

  public static void convertTempFiles(Connection db, int linkModuleId, int modifiedBy, int id, String attachmentList, boolean setDefault) throws SQLException {
    StringTokenizer items = new StringTokenizer(attachmentList, ",");
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_files " +
            "SET link_module_id = ?, link_item_id = ? " +
            (setDefault ? ", default_file = ? " : "") +
            "WHERE link_module_id = ? AND item_id = ? AND enteredby = ? ");
    while (items.hasMoreTokens()) {
      int itemId = Integer.parseInt(items.nextToken().trim());
      int i = 0;
      pst.setInt(++i, linkModuleId);
      pst.setInt(++i, id);
      if (setDefault) {
        pst.setBoolean(++i, true);
      }
      pst.setInt(++i, Constants.TEMP_FILES);
      pst.setInt(++i, itemId);
      pst.setInt(++i, modifiedBy);
      int count = pst.executeUpdate();
    }
    pst.close();
  }

  public FileItem getById(int id) {
    for (FileItem thisItem : this) {
      if (thisItem.getId() == id) {
        return thisItem;
      }
    }
    return null;
  }
}

