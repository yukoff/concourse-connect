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

package com.concursive.connect.web.modules.productcatalog.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * After an order has been placed, the end result is a record
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 9, 2007
 */
public class OrderRecord extends GenericBean {

  // Order properties
  private int id = -1;
  private Timestamp entered;
  private String ipAddress;
  private String browser;
  private double totalPrice;
  private Timestamp processed;
  private int userId = -1;

  public OrderRecord() {
  }

  public OrderRecord(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public OrderRecord(Connection db, int orderId) throws SQLException {
    queryRecord(db, orderId);
  }

  public OrderRecord(Connection db, int orderId, long orderDate) throws SQLException {
    queryRecord(db, orderId, orderDate);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Timestamp getProcessed() {
    return processed;
  }

  public void setProcessed(Timestamp processed) {
    this.processed = processed;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public void queryRecord(Connection db, int orderId) throws SQLException {
    if (orderId == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT o.* " +
            "FROM customer_order o " +
            "WHERE order_id = ? ");
    pst.setInt(1, orderId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("ID not found");
    }
  }

  public void queryRecord(Connection db, int orderId, long orderDate) throws SQLException {
    if (orderId == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT o.* " +
            "FROM customer_order o " +
            "WHERE order_id = ? " +
            "AND entered = ? ");
    pst.setInt(1, orderId);
    pst.setTimestamp(2, new Timestamp(orderDate));
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("ID not found");
    }
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    // customer_order table
    id = rs.getInt("order_id");
    entered = rs.getTimestamp("entered");
    ipAddress = rs.getString("ipaddress");
    browser = rs.getString("browser");
    totalPrice = rs.getDouble("total_price");
    processed = rs.getTimestamp("processed");
    userId = DatabaseUtils.getInt(rs, "order_by");
  }

}
