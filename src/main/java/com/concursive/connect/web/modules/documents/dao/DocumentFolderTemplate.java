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

import java.sql.*;
import java.util.ArrayList;

/**
 * Can be used for creating document folders from a template
 *
 * @author Kailash Bhoopalam
 * @created August 12, 2008
 */
public class DocumentFolderTemplate extends GenericBean {

  private int id = -1;
  private int projectCategoryId = -1;
  private String folderNames = "";
  private boolean enabled = true;
  private Timestamp entered = null;

  public DocumentFolderTemplate() {
  }

  public DocumentFolderTemplate(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public DocumentFolderTemplate(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public void queryRecord(Connection db, int templateId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT dft.* " +
            "FROM project_document_folder_template dft " +
            "WHERE template_id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, templateId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Template record not found.");
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public int getProjectCategoryId() {
    return projectCategoryId;
  }

  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String tmp) {
    this.projectCategoryId = Integer.parseInt(tmp);
  }

  /**
   * @return the folderNames
   */
  public String getFolderNames() {
    return folderNames;
  }

  /**
   * @param folderNames the folderNames to set
   */
  public void setFolderNames(String folderNames) {
    this.folderNames = folderNames;
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

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBoolean(enabled);
  }


  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("template_id");
    projectCategoryId = DatabaseUtils.getInt(rs, "project_category_id");
    folderNames = rs.getString("folder_names");
    enabled = rs.getBoolean("enabled");
    entered = rs.getTimestamp("entered");
  }

  public boolean isValid() {
    if (folderNames == null || folderNames.trim().equals("")) {
      errors.put("folderNamesError", "Required field");
    }
    return !hasErrors();
  }

  public ArrayList<String> getFolderNamesArrayList() {
    ArrayList<String> folderNames = new ArrayList<String>();
    String[] tempNames = StringUtils.replaceReturns(this.getFolderNames(), "|").split("[|]");
    for (String name : tempNames) {
      if (name != null && !"".equals(name.trim())) {
        folderNames.add(name);
      }
    }
    return folderNames;
  }

  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_document_folder_template " +
              "(" + (id > -1 ? "template_id, " : "") + "project_category_id, folder_names, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      sql.append(
          "enabled) ");
      sql.append("VALUES (?, ?, ");
      if (id > -1) {
        sql.append("?, ");
      }
      if (entered != null) {
        sql.append("?, ");
      }
      sql.append("?) ");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, projectCategoryId);
      pst.setString(++i, folderNames);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.setBoolean(++i, enabled);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_document_folder_template_template_id_seq", id);
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

  public int update(Connection db) throws SQLException {
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      if (this.getId() == -1) {
        throw new SQLException("ID was not specified");
      }
      if (!isValid()) {
        return -1;
      }
      int i = 0;
      PreparedStatement pst = db.prepareStatement(
          "UPDATE project_document_folder_template " +
              "SET project_category_id = ?, folder_names = ?, " +
              "enabled = ? " +
              "WHERE template_id = ? ");
      DatabaseUtils.setInt(pst, ++i, projectCategoryId);
      pst.setString(++i, folderNames);
      pst.setBoolean(++i, enabled);
      pst.setInt(++i, id);
      resultCount = pst.executeUpdate();
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
    return resultCount;
  }

  public void delete(Connection db) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_document_folder_template " +
              "WHERE template_id = ? ");
      pst.setInt(1, id);
      pst.execute();
      pst.close();
      if (autoCommit) {
        db.commit();
      }
    } catch (Exception e) {
      if (autoCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
  }
}