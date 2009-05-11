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

import com.concursive.connect.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Represents the information needed for HTPasswd
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Jan 29, 2006
 */

public class HTPasswdList extends ArrayList {

  private int hasPassword = Constants.UNDEFINED;
  private int pastDays = -1;
  private int pastMinutes = -1;

  public int getHasPassword() {
    return hasPassword;
  }

  public void setHasPassword(int hasPassword) {
    this.hasPassword = hasPassword;
  }

  public void setHasPassword(String tmp) {
    if ("true".equals(tmp)) {
      hasPassword = Constants.TRUE;
    } else if ("false".equals(tmp)) {
      hasPassword = Constants.FALSE;
    } else {
      hasPassword = Integer.parseInt(tmp);
    }
  }

  public int getPastDays() {
    return pastDays;
  }

  public void setPastDays(int pastDays) {
    this.pastDays = pastDays;
  }

  public void setPastDays(String tmp) {
    pastDays = Integer.parseInt(tmp);
  }

  public int getPastMinutes() {
    return pastMinutes;
  }

  public void setPastMinutes(int pastMinutes) {
    this.pastMinutes = pastMinutes;
  }

  public void setPastMinutes(String tmp) {
    pastMinutes = Integer.parseInt(tmp);
  }

  public void select(Connection db) throws SQLException {
    buildList(db);
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    createFilter(sqlFilter);
    sqlOrder.append("ORDER BY entered ");
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "u.username, u.htpasswd, u.htpasswd_date " +
            "FROM users u " +
            "WHERE u.user_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    int count = 0;
    while (rs.next()) {
      HTPasswd thisRecord = new HTPasswd(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }

  private void createFilter(StringBuffer sqlFilter) {
    if (sqlFilter == null) {
      sqlFilter = new StringBuffer();
    }
    if (hasPassword == Constants.TRUE) {
      sqlFilter.append("AND htpasswd IS NOT NULL ");
    }
    if (hasPassword == Constants.FALSE) {
      sqlFilter.append("AND htpasswd IS NULL ");
    }
    if (pastDays != -1) {
      sqlFilter.append("AND htpasswd_date >= ? ");
    }
    if (pastMinutes != -1) {
      sqlFilter.append("AND htpasswd_date >= ? ");
    }
  }

  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (pastDays != -1) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -pastDays);
      pst.setTimestamp(++i, new java.sql.Timestamp(cal.getTimeInMillis()));
    }
    if (pastMinutes != -1) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MINUTE, -pastMinutes);
      pst.setTimestamp(++i, new java.sql.Timestamp(cal.getTimeInMillis()));
    }
    return i;
  }
}
