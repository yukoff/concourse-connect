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

package com.concursive.connect.web.modules.members.dao;

import com.concursive.commons.db.DatabaseUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represents a list of members of a profile
 *
 * @author matt rajkowski
 * @version $Id$
 * @created October 27, 2003
 */
public class InvitationList extends ArrayList<Invitation> {

  private int projectId = -1;


  /**
   * Constructor for the InvitationList object
   */
  public InvitationList() {
  }


  /**
   * Constructor for the InvitationList object
   *
   * @param request Description of the Parameter
   */
  public InvitationList(HttpServletRequest request) {
    int count = 1;
    String data = null;
    while ((data = request.getParameter("count" + count)) != null) {
      if (DatabaseUtils.parseBoolean(request.getParameter("check" + count))) {
        Invitation thisInvitation = new Invitation(request, count);
        this.add(thisInvitation);
      }
      ++count;
    }
  }


  /**
   * Sets the projectId attribute of the InvitationList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the InvitationList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Gets the projectId attribute of the InvitationList object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param userId Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int queryCount(Connection db, int userId) throws SQLException {
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT count(user_id) AS nocols " +
            "FROM project_team " +
            "WHERE user_id = ? " +
            "AND status = ? ");
    pst.setInt(1, userId);
    pst.setInt(2, TeamMember.STATUS_PENDING);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("nocols");
    }
    rs.close();
    pst.close();
    return count;
  }
}

