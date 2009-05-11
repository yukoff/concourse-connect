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

package com.concursive.connect.web.modules.reports.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a set of report parameters. Each parameter has specific display
 * capabilities and requirements according to a JasperReport.
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 15, 2003
 */
public class Criteria extends GenericBean {
  //properties
  private int id = -1;
  private int queueId = -1;
  private String parameter = null;
  private String value = null;


  /**
   * Constructor for the Criteria object
   */
  public Criteria() {
  }


  /**
   * Constructor for the Criteria object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Criteria(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the Criteria object
   *
   * @param db         Description of the Parameter
   * @param criteriaId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Criteria(Connection db, int criteriaId) throws SQLException {
    queryRecord(db, criteriaId);
  }


  public Criteria(Parameter param) {
    parameter = param.getName();
    value = param.getValue();
  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getValue() {
    return value;
  }

  public int getValueAsInt() {
    return Integer.parseInt(value);
  }

  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Populates the report criteria information for the specified criteria id
   *
   * @param db         Description of the Parameter
   * @param criteriaId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int criteriaId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT c.* " +
            "FROM report_criteria c " +
            "WHERE criteria_id = ? ");
    pst.setInt(1, criteriaId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Criteria record not found.");
    }
  }


  /**
   * Populates this object from a given result set
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("criteria_id");
    queueId = rs.getInt("queue_id");
    parameter = rs.getString("parameter");
    value = rs.getString("value");
  }


  /**
   * Sets the id attribute of the Criteria object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Criteria object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Inserts this object into the database as a new record.
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    id = DatabaseUtils.getNextSeq(db, "report_criteria_criteria_id_seq", id);
    sql.append(
        "INSERT INTO report_criteria " +
            "(");
    if (id > -1) {
      sql.append("criteria_id, ");
    }
    sql.append("queue_id, parameter, value) ");
    sql.append("VALUES (");
    if (id > -1) {
      sql.append("?, ");
    }
    sql.append("?, ?, ?) ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setInt(++i, queueId);
    pst.setString(++i, parameter);
    pst.setString(++i, value);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "report_criteria_criteria_id_seq", id);
    return true;
  }


  /**
   * Updates the database with this object for an existing set of criteria.
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean update(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE report_criteria " +
            "SET parameter = ?, " +
            "value = ? " +
            "WHERE criteria_id = ? ");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setString(++i, parameter);
    pst.setString(++i, value);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();

    return true;
  }
}

