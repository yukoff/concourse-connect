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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Description of the AuthenticationClassesLookupList
 *
 * @author Artem.Zakolodkin
 * @created Jul 19, 2007
 */
public class AuthenticationClassesLookupList extends ArrayList<AuthenticationClassesLookup> {

  private static final long serialVersionUID = 1L;
  private String loginMode = null;

  public AuthenticationClassesLookupList() {

  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;

    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlSelect.append(" SELECT ");
    sqlSelect.append(
        "lac.* " +
            "FROM lookup_authentication_classes lac " +
            "WHERE lac.code > 0 ");
    createFilter(sqlFilter);
    pst = db.prepareStatement(
        sqlSelect.toString() + sqlFilter.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      AuthenticationClassesLookup thisItem = new AuthenticationClassesLookup(rs);
      this.add(thisItem);
    }
    rs.close();
    pst.close();
  }

  private void createFilter(StringBuffer sqlFilter) {
    if (loginMode != null) {
      sqlFilter.append("AND lac.login_mode = ? ");
    }
  }

  private int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (loginMode != null) {
      pst.setString(++i, this.loginMode);
    }
    return i;
  }

  /**
   * @return the loginMode
   */
  public String getLoginMode() {
    return loginMode;
  }

  /**
   * @param loginMode the loginMode to set
   */
  public void setLoginMode(String loginMode) {
    this.loginMode = loginMode;
  }
}
