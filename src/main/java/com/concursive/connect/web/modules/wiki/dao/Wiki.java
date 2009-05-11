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

package com.concursive.connect.web.modules.wiki.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.common.social.viewing.utils.Viewing;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;

import java.sql.*;

/**
 * Represents a wiki page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 7, 2006
 */
public class Wiki extends GenericBean {
  public static final String TABLE = "project_wiki";
  public static final String PRIMARY_KEY = "wiki_id";

  private int id = -1;
  private int projectId = -1;
  private String subject = "";
  private String content = null;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private int modifiedBy = -1;
  private int readCount = 0;
  private boolean enabled = true;
  private boolean readOnly = false;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAvg = 0.0;
  private int stateId = -1;
  private int templateId = -1;
  private Timestamp readDate = null;
  private int inappropriateCount = 0;

  private boolean apiRestore = false;

  public Wiki() {
  }

  public Wiki(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public Wiki(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public Wiki(Connection db, int id, int projectId) throws SQLException {
    this.projectId = projectId;
    queryRecord(db, id);
  }

  public void queryRecord(Connection db, int wikiId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT w.* " +
            "FROM project_wiki w " +
            "WHERE wiki_id = ? ");
    if (projectId > -1) {
      sql.append("AND project_id = ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, wikiId);
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Wiki record not found.");
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

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public String getSubject() {
    return subject;
  }

  public String getSubjectLink() {
    return StringUtils.replace(StringUtils.jsEscape(subject), "%20", "+");
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = Integer.parseInt(modifiedBy);
  }

  public int getReadCount() {
    return readCount;
  }

  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }

  public void setReadCount(String readCount) {
    this.readCount = Integer.parseInt(readCount);
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

  public boolean getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public void setReadOnly(String readOnly) {
    this.readOnly = DatabaseUtils.parseBoolean(readOnly);
  }


  public int getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public void setRatingCount(String ratingCount) {
    this.ratingCount = Integer.parseInt(ratingCount);
  }

  public int getRatingValue() {
    return ratingValue;
  }

  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public void setRatingValue(String ratingValue) {
    this.ratingValue = Integer.parseInt(ratingValue);
  }

  /**
   * @return the ratingAvg
   */
  public double getRatingAvg() {
    return ratingAvg;
  }

  /**
   * @param ratingAvg the ratingAvg to set
   */
  public void setRatingAvg(double ratingAvg) {
    this.ratingAvg = ratingAvg;
  }

  public void setRatingAvg(String ratingAvg) {
    this.ratingAvg = Integer.parseInt(ratingAvg);
  }

  public int getStateId() {
    return stateId;
  }

  public void setStateId(int stateId) {
    this.stateId = stateId;
  }

  public void setStateId(String stateId) {
    this.stateId = Integer.parseInt(stateId);
  }

  public int getTemplateId() {
    return templateId;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public void setTemplateId(String tmp) {
    templateId = Integer.parseInt(tmp);
  }

  /**
   * @return the readDate
   */
  public Timestamp getReadDate() {
    return readDate;
  }

  /**
   * @param readDate the readDate to set
   */
  public void setReadDate(Timestamp readDate) {
    this.readDate = readDate;
  }

  public void setReadDate(String readDate) {
    this.readDate = DatabaseUtils.parseTimestamp(readDate);
  }

  /**
   * @return the inappropriateCount
   */
  public int getInappropriateCount() {
    return inappropriateCount;
  }

  /**
   * @param inappropriateCount the inappropriateCount to set
   */
  public void setInappropriateCount(int inappropriateCount) {
    this.inappropriateCount = inappropriateCount;
  }

  public void setInappropriateCount(String inappropriateCount) {
    this.inappropriateCount = Integer.parseInt(inappropriateCount);
  }

  public boolean isApiRestore() {
    return apiRestore;
  }

  public void setApiRestore(boolean apiRestore) {
    this.apiRestore = apiRestore;
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("wiki_id");
    projectId = rs.getInt("project_id");
    subject = rs.getString("subject");
    content = rs.getString("content");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    readCount = rs.getInt("read_count");
    enabled = rs.getBoolean("enabled");
    readOnly = rs.getBoolean("read_only");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    stateId = DatabaseUtils.getInt(rs, "state_id");
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    readDate = rs.getTimestamp("read_date");
    templateId = DatabaseUtils.getInt(rs, "template_id");
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
  }

  public boolean isValid() {
    if (projectId == -1) {
      errors.put("actionError", "Project ID not specified");
    }
    if (subject == null) {
      subject = "";
    }
    if (!StringUtils.hasText(content)) {
      errors.put("contentError", "Required field");
    }
    return !hasErrors();
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
          "INSERT INTO project_wiki " +
              "(" + (id > -1 ? "wiki_id, " : "") + "project_id, subject, content, read_count, enabled, read_only, template_id, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      sql.append(
          "enteredby, modifiedby) ");
      sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ");
      if (id > -1) {
        sql.append("?, ");
      }
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      sql.append("?, ?) ");
      int i = 0;
      //Insert the page
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      pst.setInt(++i, projectId);
      pst.setString(++i, subject);
      pst.setString(++i, content);
      pst.setInt(++i, readCount);
      pst.setBoolean(++i, enabled);
      pst.setBoolean(++i, readOnly);
      DatabaseUtils.setInt(pst, ++i, templateId);
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
      id = DatabaseUtils.getCurrVal(db, "project_wiki_wiki_id_seq", id);

      // TODO: Skip this if a restore is underway
      if (!isApiRestore()) {
        // Insert the version
        WikiVersion.insert(db, this);
        // Update the page links used
        WikiUtils.updatePageLinks(db, this);
      }
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
    Wiki originalWiki = new Wiki(db, id);
    return update(db, originalWiki);
  }

  public int update(Connection db, Wiki originalWiki) throws SQLException {
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
          "UPDATE project_wiki " +
              "SET content = ?, " +
              "modifiedby = ?, modified = CURRENT_TIMESTAMP " +
              "WHERE wiki_id = ? " +
              "AND modified = ? ");
      pst.setString(++i, content);
      pst.setInt(++i, this.getModifiedBy());
      pst.setInt(++i, id);
      pst.setTimestamp(++i, modified);
      resultCount = pst.executeUpdate();
      pst.close();
      if (resultCount == 1) {
        WikiVersion.insert(db, originalWiki, this);
        WikiUtils.updatePageLinks(db, this);
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

  public void lock(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_wiki " +
            "SET read_only = ? " +
            "WHERE wiki_id = ? ");
    int i = 0;
    pst.setBoolean(++i, true);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }

  public void unlock(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_wiki " +
            "SET read_only = ? " +
            "WHERE wiki_id = ? ");
    int i = 0;
    pst.setBoolean(++i, false);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }

  public void delete(Connection db) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      // Delete the collaboration items
      Rating.delete(db, id, TABLE, PRIMARY_KEY);
      Viewing.delete(db, id, TABLE, PRIMARY_KEY);
      WikiCommentList wikiCommentList = new WikiCommentList();
      wikiCommentList.setWikiId(id);
      wikiCommentList.buildList(db);
      wikiCommentList.delete(db);

      // Delete the version data
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_wiki_version " +
              "WHERE wiki_id = ? ");
      pst.setInt(1, id);
      pst.execute();
      pst.close();

      // Delete the wiki
      pst = db.prepareStatement(
          "DELETE FROM project_wiki " +
              "WHERE wiki_id = ? ");
      int i = 0;
      pst.setInt(++i, id);
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
