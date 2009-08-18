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

package com.concursive.connect.web.modules.tools.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;

import java.sql.*;

/**
 * Represents the features of a project
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created June 17, 2008
 */
public class ProjectCRM extends GenericBean {

  private int id = -1;
  private int modifiedBy = -1;
  private Timestamp modified = null;

  private String concursiveCRMUrl = null;
  private String concursiveCRMDomain = null;
  private String concursiveCRMCode = null;
  private String concursiveCRMClient = null;


  /**
   * Constructor for the ProjectCRM object
   */
  public ProjectCRM() {
  }


  /**
   * Constructor for the ProjectCRM object
   *
   * @param rs Description of Parameter
   * @throws java.sql.SQLException Description of Exception
   */
  public ProjectCRM(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public ProjectCRM(Connection db, int thisProjectId) throws SQLException {
    queryRecord(db, thisProjectId);
  }


  /**
   * Description of the Method
   *
   * @param db            Description of the Parameter
   * @param thisProjectId Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  private void queryRecord(Connection db, int thisProjectId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM projects p " +
            "WHERE p.project_id = ? ");
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, thisProjectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }


  public void setId(int tmp) {
    this.id = tmp;
  }


  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public int getId() {
    return id;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
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

  /**
   * @return the concursiveCRMUrl
   */
  public String getConcursiveCRMUrl() {
    return concursiveCRMUrl;
  }


  /**
   * @param concursiveCRMUrl the concursiveCRMUrl to set
   */
  public void setConcursiveCRMUrl(String concursiveCRMUrl) {
    this.concursiveCRMUrl = concursiveCRMUrl;
  }


  /**
   * @return the concursiveCRMDomain
   */
  public String getConcursiveCRMDomain() {
    return concursiveCRMDomain;
  }


  /**
   * @param concursiveCRMDomain the concursiveCRMDomain to set
   */
  public void setConcursiveCRMDomain(String concursiveCRMDomain) {
    this.concursiveCRMDomain = concursiveCRMDomain;
  }


  /**
   * @return the concursiveCRMCode
   */
  public String getConcursiveCRMCode() {
    return concursiveCRMCode;
  }


  /**
   * @param concursiveCRMCode the concursiveCRMCode to set
   */
  public void setConcursiveCRMCode(String concursiveCRMCode) {
    this.concursiveCRMCode = concursiveCRMCode;
  }


  /**
   * @return the concursiveCRMClient
   */
  public String getConcursiveCRMClient() {
    return concursiveCRMClient;
  }


  /**
   * @param concursiveCRMClient the concursiveCRMClient to set
   */
  public void setConcursiveCRMClient(String concursiveCRMClient) {
    this.concursiveCRMClient = concursiveCRMClient;
  }


  /**
   * Updates the features of a projectCRM
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ProjectId was not specified");
    }
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = db.prepareStatement(
          "UPDATE projects " +
              "SET " +
              "concursive_crm_url = ?, concursive_crm_domain = ?, concursive_crm_code = ?, concursive_crm_client = ?, " +
              "modifiedby = ?, modified = CURRENT_TIMESTAMP " +
              "WHERE project_id = ? ");
      int i = 0;
      pst.setString(++i, concursiveCRMUrl);
      pst.setString(++i, concursiveCRMDomain);
      pst.setString(++i, concursiveCRMCode);
      pst.setString(++i, concursiveCRMClient);
      pst.setInt(++i, modifiedBy);
      pst.setInt(++i, id);
      resultCount = pst.executeUpdate();
      pst.close();
      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, id);
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
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws java.sql.SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("project_id");
    concursiveCRMUrl = rs.getString("concursive_crm_url");
    concursiveCRMDomain = rs.getString("concursive_crm_domain");
    concursiveCRMCode = rs.getString("concursive_crm_code");
    concursiveCRMClient = rs.getString("concursive_crm_client");
  }

}