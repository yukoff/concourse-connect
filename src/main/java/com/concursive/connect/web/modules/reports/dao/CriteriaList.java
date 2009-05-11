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

import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A collection of Criteria objects, can be used for retrieving from the
 * database.
 *
 * @author matt rajkowski
 * @created September 15, 2003
 */
public class CriteriaList extends HashMap {

  protected PagedListInfo pagedListInfo = null;
  protected int queueId = -1;


  /**
   * Constructor for the CategoryList object
   */
  public CriteriaList() {
  }

  public CriteriaList(ParameterList params) {
    Iterator i = params.iterator();
    while (i.hasNext()) {
      Parameter param = (Parameter) i.next();
      this.put(param.getName(), new Criteria(param));
    }

  }


  /**
   * Sets the pagedListInfo attribute of the ReportList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Gets the pagedListInfo attribute of the ReportList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  /**
   * Builds a list of criteria objects based on the properties that have been
   * set for this object.
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) as recordcount " +
            "FROM report_criteria c " +
            "WHERE criteria_id > -1 ");
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
      //Determine column to sort by
      pagedListInfo.setDefaultSort("c.criteria_id", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY c.criteria_id ");
    }

    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "c.* " +
            "FROM report_criteria c " +
            "WHERE criteria_id > -1 ");
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    while (rs.next()) {
      Criteria thisCriteria = new Criteria(rs);
      this.put(thisCriteria.getParameter(), thisCriteria);
    }
    rs.close();
    pst.close();
  }


  /**
   * Defines additional filters for the query
   *
   * @param sqlFilter Description of the Parameter
   */
  protected void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (queueId != -1) {
      sqlFilter.append("AND c.queue_id = ? ");
    }
  }


  /**
   * Sets the parameters for the additional parameters specified for this query
   *
   * @param pst Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (queueId != -1) {
      pst.setInt(++i, queueId);
    }
    return i;
  }


  /**
   * Deletes all of the related data for a specific criteria id
   *
   * @param db         Description of the Parameter
   * @param criteriaId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void delete(Connection db, int criteriaId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM report_criteria " +
            "WHERE criteria_id = ? ");
    pst.setInt(1, criteriaId);
    pst.execute();
    pst.close();
  }

  public Map<String, Object> getParameters() {
    Map<String, Object> parameters = new HashMap<String, Object>();
    Iterator i = this.values().iterator();
    while (i.hasNext()) {
      Criteria thisCriteria = (Criteria) i.next();
      // TODO: Add a value_class field to criteria
      /*
      if (thisCriteria.getValueClass().equals("java.lang.Integer")) {

      }
      */
      if (thisCriteria.getParameter().endsWith("Id")) {
        parameters.put(thisCriteria.getParameter(), Integer.parseInt(thisCriteria.getValue()));
      } else {
        parameters.put(thisCriteria.getParameter(), thisCriteria.getValue());
      }
    }
    return parameters;
  }

  public int getValueAsInt(String param) {
    Iterator i = this.values().iterator();
    while (i.hasNext()) {
      Criteria thisCriteria = (Criteria) i.next();
      if (thisCriteria.getParameter().equals(param)) {
        return thisCriteria.getValueAsInt();
      }
    }
    return -1;
  }
}
