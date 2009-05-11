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

package com.concursive.connect.web.modules.api.dao;

import com.concursive.commons.xml.XMLUtils;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Used for building and storing a list of SyncTable objects
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June, 2002
 */
public class SyncTableList extends ArrayList<SyncTable> {

  private int systemId = -1;
  private boolean buildTextFields = true;
  private boolean buildSyncElementsOnly = false;
  private boolean buildCreateStatementsOnly = false;


  /**
   * Constructor for the SyncTableList object
   */
  public SyncTableList() {
  }


  /**
   * Sets the systemId attribute of the SyncTableList object
   *
   * @param tmp The new systemId value
   */
  public void setSystemId(int tmp) {
    this.systemId = tmp;
  }


  /**
   * Sets the systemId attribute of the SyncTableList object
   *
   * @param tmp The new systemId value
   */
  public void setSystemId(String tmp) {
    this.systemId = Integer.parseInt(tmp);
  }


  /**
   * Sets the buildTextFields attribute of the SyncTableList object
   *
   * @param tmp The new buildTextFields value
   */
  public void setBuildTextFields(boolean tmp) {
    this.buildTextFields = tmp;
  }


  /**
   * Sets the buildSyncElementsOnly attribute of the SyncTableList object
   *
   * @param tmp The new buildSyncElementsOnly value
   */
  public void setBuildSyncElementsOnly(boolean tmp) {
    this.buildSyncElementsOnly = tmp;
  }


  /**
   * Sets the buildSyncElementsOnly attribute of the SyncTableList object
   *
   * @param tmp The new buildSyncElementsOnly value
   */
  public void setBuildSyncElementsOnly(String tmp) {
    this.buildSyncElementsOnly =
        (tmp.equalsIgnoreCase("true") ||
            tmp.equalsIgnoreCase("on"));
  }


  /**
   * Sets the buildCreateStatementsOnly attribute of the SyncTableList object
   *
   * @param tmp The new buildCreateStatementsOnly value
   */
  public void setBuildCreateStatementsOnly(boolean tmp) {
    this.buildCreateStatementsOnly = tmp;
  }


  /**
   * Sets the buildCreateStatementsOnly attribute of the SyncTableList object
   *
   * @param tmp The new buildCreateStatementsOnly value
   */
  public void setBuildCreateStatementsOnly(String tmp) {
    this.buildCreateStatementsOnly =
        (tmp.equalsIgnoreCase("true") ||
            tmp.equalsIgnoreCase("on"));
  }


  /**
   * Gets the systemId attribute of the SyncTableList object
   *
   * @return The systemId value
   */
  public int getSystemId() {
    return systemId;
  }


  /**
   * Gets the buildTextFields attribute of the SyncTableList object
   *
   * @return The buildTextFields value
   */
  public boolean getBuildTextFields() {
    return buildTextFields;
  }


  /**
   * Gets the buildSyncElementsOnly attribute of the SyncTableList object
   *
   * @return The buildSyncElementsOnly value
   */
  public boolean getBuildSyncElementsOnly() {
    return buildSyncElementsOnly;
  }


  /**
   * Gets the buildCreateStatementsOnly attribute of the SyncTableList object
   *
   * @return The buildCreateStatementsOnly value
   */
  public boolean getBuildCreateStatementsOnly() {
    return buildCreateStatementsOnly;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void select(Connection db) throws SQLException {
    buildList(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;

    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT table_id, system_id, element_name, mapped_class_name, entered, modified ");
    if (buildTextFields) {
      sql.append(", create_statement ");
    }
    sql.append(", order_id, sync_item, object_key ");
    sql.append("FROM sync_table ");
    sql.append("WHERE table_id > -1 ");
    createFilter(sql);
    sql.append("ORDER BY order_id ");
    pst = db.prepareStatement(sql.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      SyncTable thisTable = new SyncTable();
      thisTable.setBuildTextFields(buildTextFields);
      thisTable.buildRecord(rs);
      this.add(thisTable);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("SyncTableList-> Added: " + thisTable.getName());
      }
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }

    if (systemId != -1) {
      sqlFilter.append("AND system_id = ? ");
    }

    if (buildSyncElementsOnly) {
      sqlFilter.append("AND sync_item = ? ");
    }

    if (buildCreateStatementsOnly) {
      sqlFilter.append("AND create_statement IS NOT NULL ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (systemId != -1) {
      pst.setInt(++i, systemId);
    }
    if (buildSyncElementsOnly) {
      pst.setBoolean(++i, true);
    }
    return i;
  }


  /**
   * Gets the objectMapping attribute of the SyncTableList object
   *
   * @param thisSystemId Description of the Parameter
   * @return The objectMapping value
   */
  public HashMap<String, SyncTable> getObjectMapping(int thisSystemId) {
    HashMap<String, SyncTable> objectMap = new HashMap<String, SyncTable>();
    Iterator iList = this.iterator();
    while (iList.hasNext()) {
      SyncTable thisTable = (SyncTable) iList.next();
      if (thisTable.getSystemId() == thisSystemId && thisTable.getMappedClassName() != null) {
        objectMap.put(thisTable.getName(), thisTable);
      }
    }
    return objectMap;
  }


  /**
   * Removes a specific systemId from the cached list. TODO: Test this
   *
   * @param thisSystemId Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean clearObjectMapping(int thisSystemId) {
    Iterator iList = this.iterator();
    while (iList.hasNext()) {
      SyncTable thisTable = (SyncTable) iList.next();
      if (thisTable.getSystemId() == thisSystemId) {
        iList.remove();
        return true;
      }
    }
    return false;
  }

  public void loadObjectMap(InputStream resourceAsStream) throws Exception {
    XMLUtils objectMap = new XMLUtils(resourceAsStream);
    ArrayList<Element> items = new ArrayList<Element>();
    XMLUtils.getAllChildren(objectMap.getFirstChild("mappings"), "map", items);
    Iterator<Element> i = items.iterator();
    while (i.hasNext()) {
      SyncTable thisTable = new SyncTable(i.next());
      thisTable.setSystemId(systemId);
      this.add(thisTable);
    }
  }
}
