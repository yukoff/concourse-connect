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

package com.concursive.connect.web.modules.services.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;

import java.sql.*;

/**
 * Maps a project to a service lookup
 *
 * @author matt rajkowski
 * @created Sep 8, 2008
 */
public class Service extends GenericBean {
  // base properties
  private int id = -1;
  private int projectId = -1;
  private int serviceId = -1;
  private Timestamp entered = null;
  // joined helpers
  private String serviceDescription = null;

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

  public int getServiceId() {
    return serviceId;
  }

  public void setServiceId(int serviceId) {
    this.serviceId = serviceId;
  }

  public void setServiceId(String tmp) {
    this.serviceId = Integer.parseInt(tmp);
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

  public String getServiceDescription() {
    return serviceDescription;
  }

  public void setServiceDescription(String serviceDescription) {
    this.serviceDescription = serviceDescription;
  }

  public Service() {
  }

  public Service(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public Service(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT s.*, ls.description " +
            "FROM project_service s " +
            "LEFT JOIN lookup_service ls ON (s.service_id = ls.code) " +
            "WHERE id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException(String.format("Record not found for id %s.", id));
    }
  }

  protected void buildRecord(ResultSet rs) throws SQLException {
    // project_service
    id = rs.getInt("id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    serviceId = DatabaseUtils.getInt(rs, "service_id");
    entered = rs.getTimestamp("entered");
    // lookup_service
    serviceDescription = rs.getString("description");
  }

  public void insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "INSERT INTO project_service " +
            "(" +
            (id > -1 ? "id, " : "") +
            "project_id, " +
            "service_id ");
    if (entered != null) {
      sql.append(", entered ");
    }
    sql.append(") ");
    sql.append("VALUES (");
    if (id > -1) {
      sql.append("?, ");
    }
    sql.append("?, ?");
    if (entered != null) {
      sql.append(", ?");
    }
    sql.append(")");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    if (id > -1) {
      pst.setInt(++i, id);
    }
    DatabaseUtils.setInt(pst, ++i, projectId);
    DatabaseUtils.setInt(pst, ++i, serviceId);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "project_service_id_seq", id);
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
  }

  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_service SET " +
            "project_id = ?, " +
            "service_id = ? " +
            "WHERE id = ? ");
    DatabaseUtils.setInt(pst, ++i, projectId);
    DatabaseUtils.setInt(pst, ++i, serviceId);
    resultCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    return resultCount;
  }

  public boolean delete(Connection db) throws SQLException {
    if (this.getId() < 0) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_service " +
            "WHERE id = ? ");
    pst.setInt(1, this.getId());
    resultCount = pst.executeUpdate();
    pst.close();
    CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
    return resultCount > 0;
  }
}
