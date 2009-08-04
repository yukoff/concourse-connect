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

package com.concursive.connect.web.modules.login.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Collection of instance objects
 *
 * @author matt rajkowski
 * @created July 6, 2009
 */
public class InstanceList extends ArrayList<Instance> {

  private int instanceId = -1;
  private String domainName = null;
  private String context = null;
  private PagedListInfo pagedListInfo = null;
  private int enabled = Constants.UNDEFINED;


  /**
   * Constructor for the InstanceList object
   */
  public InstanceList() {
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setInstanceId(String tmp) {
    this.instanceId = Integer.parseInt(tmp);
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  /**
   * Sets the pagedListInfo attribute of the UserLogList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * @return the enabled
   */
  public int getEnabled() {
  	return enabled;
  }

	/**
   * @param enabled the enabled to set
   */
  public void setEnabled(int enabled) {
  	this.enabled = enabled;
  }

  public void setEnabled(String enabled) {
  	this.enabled = Integer.parseInt(enabled);
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws java.sql.SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    // Build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM instances " +
            "WHERE instance_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo != null) {
      // Get the total number of records matching filter
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
      pagedListInfo.setDefaultSort("instance_id", null);
      pagedListInfo.appendSqlTail(db, sqlOrder);
    } else {
      sqlOrder.append("ORDER BY instance_id ");
    }
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "* " +
            "FROM instances " +
            "WHERE instance_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      Instance thisEntry = new Instance(rs);
      this.add(thisEntry);
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
    if (instanceId > -1) {
      sqlFilter.append("AND instance_id = ? ");
    }
    if (domainName != null) {
      sqlFilter.append("AND domain_name = ? ");
    }
    if (context != null) {
      sqlFilter.append("AND context = ? ");
    }
    if (enabled != Constants.UNDEFINED) {
      sqlFilter.append("AND enabled = ? ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (instanceId > -1) {
      pst.setInt(++i, instanceId);
    }
    if (domainName != null) {
      pst.setString(++i, domainName);
    }
    if (context != null) {
      pst.setString(++i, context);
    }
    if (enabled != Constants.UNDEFINED) {
      pst.setBoolean(++i, (enabled == Constants.TRUE));
    }
    return i;
  }

  
  public boolean contains(Instance instance){
  	int id = -1;
  	id = getIdFromInfo(instance.getDomainName(), instance.getContext(), instance.getTitle());
  	if (id != -1){
  		return true;
  	}
  	return false;
  }
  	
  	
  public int getIdFromInfo(String domainName, String context, String title){
  	int id = -1;
  	
  	for(Instance instance : this){
  		if (domainName.equals(instance.getDomainName()) && 
  				context.equals(instance.getContext()) &&
  				title.equals(instance.getTitle())){
  			id = instance.getId();
  		}
  	}
  	
  	return id;
  }

  public Instance getInstanceFromId(int id){
  	Instance thisInstance = null;
  	
  	for(Instance instance : this){
  		if (instance.getId() == id){
  			thisInstance = instance;
  			break;
  		}
  	}
  	
  	return thisInstance;
  }

}
