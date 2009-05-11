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

  private String htmlLink = null;

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
  }

  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO project_history " +
        "(enteredby, project_id, link_object, link_item_id ");
    if (linkStartDate != null) {
      sql.append(", link_start_date");
    }
    if (entered != null) {
      sql.append(", entered");//entered
    }
    sql.append(", description, enabled, event_type) VALUES " +
        "(?, ?, ?, ?");
    if (linkStartDate != null) {
      sql.append(", ?");
    }
    if (entered != null) {
      sql.append(", ?");//entered
    }
    sql.append(", ?, ?, ?) ");
    PreparedStatement pst = db.prepareStatement(sql.toString());

    int i = 0;
    pst.setInt(++i, enteredBy);
    pst.setInt(++i, projectId);
    pst.setString(++i, linkObject);
    pst.setInt(++i, linkItemId);
    if (linkStartDate != null) {
      pst.setTimestamp(++i, linkStartDate);
    }
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    pst.setString(++i, description);
    pst.setBoolean(++i, enabled);
    pst.setInt(++i, eventType);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_history_history_id_seq", -1);

    return true;
  }


  public boolean delete(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ID not specified");
    }
    int deleteCount = 0;

    PreparedStatement pst = db.prepareStatement("DELETE FROM project_history WHERE history_id = ? ");
    pst.setInt(1, id);
    deleteCount = pst.executeUpdate();
    pst.close();

    return (deleteCount > 0);
  }
}