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

package com.concursive.connect.web.modules.communications.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Represents a Project Message Template. The following place holders will be processed
 * at runtime and replaced with appropriate content:
 * <p/>
 * ${invite.name}
 * ${project.name}
 * ${project.description}
 * ${project.ownerName}
 * ${project.profileLink}
 * ${project.unsubscribeLink}
 * ${project.customText}
 * ${link.info}
 *
 * @author Ananth
 * @version MessageTemplate.java Jul 29, 2008 1:31:54 PM Ananth $
 * @created Jul 29, 2008
 */
public class MessageTemplate extends GenericBean {
  private int id = -1;
  private int projectCategoryId = -1;
  private String title = null;
  private String subject = null;
  private String content = null;
  private int level = 0;
  private boolean enabled = true;
  private Timestamp entered = null;

  /**
   * Gets the 'content' attribute of the MessageTemplate object
   *
   * @return The 'content' value
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the 'content' attribute of the MessageTemplate
   *
   * @param content The new 'content' value
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Gets the 'enabled' attribute of the MessageTemplate object
   *
   * @return The 'enabled' value
   */
  public boolean getEnabled() {
    return enabled;
  }

  /**
   * Sets the 'enabled' attribute of the MessageTemplate
   *
   * @param enabled The new 'enabled' value
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = DatabaseUtils.parseBoolean(enabled);
  }

  /**
   * Gets the 'entered' attribute of the MessageTemplate object
   *
   * @return The 'entered' value
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * Sets the 'entered' attribute of the MessageTemplate
   *
   * @param entered The new 'entered' value
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }

  /**
   * Gets the 'id' attribute of the MessageTemplate object
   *
   * @return The 'id' value
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the 'id' attribute of the MessageTemplate
   *
   * @param id The new 'id' value
   */
  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }

  /**
   * Gets the 'level' attribute of the MessageTemplate object
   *
   * @return The 'level' value
   */
  public int getLevel() {
    return level;
  }

  /**
   * Sets the 'level' attribute of the MessageTemplate
   *
   * @param level The new 'level' value
   */
  public void setLevel(int level) {
    this.level = level;
  }

  public void setLevel(String level) {
    this.level = Integer.parseInt(level);
  }

  /**
   * Gets the 'projectCategoryId' attribute of the MessageTemplate object
   *
   * @return The 'projectCategoryId' value
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }

  /**
   * Sets the 'projectCategoryId' attribute of the MessageTemplate
   *
   * @param projectCategoryId The new 'projectCategoryId' value
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
  }

  /**
   * Gets the 'subject' attribute of the MessageTemplate object
   *
   * @return The 'subject' value
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the 'subject' attribute of the MessageTemplate
   *
   * @param subject The new 'subject' value
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Gets the 'title' attribute of the MessageTemplate object
   *
   * @return The 'title' value
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the 'title' attribute of the MessageTemplate
   *
   * @param title The new 'title' value
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public MessageTemplate() {
  }

  public MessageTemplate(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public MessageTemplate(Connection db, String id) throws SQLException {
    queryRecord(db, Integer.parseInt(id));
  }

  public MessageTemplate(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Id not specified..");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT pmt.* " +
            "FROM project_message_template pmt " +
            "WHERE pmt.template_id = ?");
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
    id = rs.getInt("template_id");
    projectCategoryId = DatabaseUtils.getInt(rs, "project_category_id");
    title = rs.getString("title");
    subject = rs.getString("subject");
    content = rs.getString("content");
    level = rs.getInt("level");
    enabled = rs.getBoolean("enabled");
    entered = rs.getTimestamp("entered");
  }

  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO project_message_template (project_category_id, title, subject, content, ");
    if (id > -1) {
      sql.append("template_id, ");
    }
    sql.append("level, enabled, entered) VALUES (?, ?, ?, ?, ");
    if (id > -1) {
      sql.append("?, ");
    }
    sql.append("?, ?, ?) ");

    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    DatabaseUtils.setInt(pst, ++i, projectCategoryId);
    pst.setString(++i, title);
    pst.setString(++i, subject);
    pst.setString(++i, content);
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setInt(++i, level);
    pst.setBoolean(++i, enabled);
    pst.setTimestamp(++i, entered);
    pst.execute();
    pst.close();

    id = DatabaseUtils.getCurrVal(db, "project_message_template_template_id_seq", id);

    return true;
  }

  public int update(Connection db) throws SQLException {
    int resultCount = 0;
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_message_template " +
            "SET project_category_id = ?, title = ?, subject = ?, content = ?, level = ?, " +
            "enabled = ? " +
            "WHERE template_id = ? ");
    DatabaseUtils.setInt(pst, ++i, projectCategoryId);
    pst.setString(++i, title);
    pst.setString(++i, subject);
    pst.setString(++i, content);
    pst.setInt(++i, level);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();

    return resultCount;
  }

  public void delete(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_message_template " +
            "WHERE template_id = ? ");
    pst.setInt(1, id);
    pst.execute();
    pst.close();
  }

  public boolean isValid() {
    if (title == null || title.trim().equals("")) {
      errors.put("titleError", "Required field");
    }
    if (subject == null || subject.trim().equals("")) {
      errors.put("subjectError", "Required field");
    }
    if (content == null || content.trim().equals("")) {
      errors.put("contentError", "Required field");
    }
    return !hasErrors();
  }
}