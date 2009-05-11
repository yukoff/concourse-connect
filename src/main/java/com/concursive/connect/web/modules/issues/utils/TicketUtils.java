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

package com.concursive.connect.web.modules.issues.utils;

import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to work with ticket objects
 *
 * @author matt rajkowski
 * @created July 7, 2008
 */
public class TicketUtils {

  public static int retrieveTicketIdFromProjectTicketId(int projectId, int projectTicketId) {
    Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_PROJECT_TICKET_ID_CACHE);
    Element element = cache.get(projectId + "-" + projectTicketId);
    if (element.getObjectValue() != null) {
      return (Integer) element.getObjectValue();
    }
    return -1;
  }

  public static int queryTicketIdFromTicketCount(Connection db, int projectId, int projectTicketId) throws SQLException {
    int ticketId = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT ticketid " +
            "FROM ticket t " +
            "LEFT JOIN ticketlink_project tlp ON (t.ticketid = tlp.ticket_id) " +
            "WHERE tlp.project_id = ? AND t.key_count = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, projectTicketId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      ticketId = rs.getInt("ticketid");
    }
    rs.close();
    pst.close();
    return ticketId;
  }

  public static int queryProjectTicketIdFromTicketId(Connection db, int ticketId) throws SQLException {
    int projectTicketId = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT key_count " +
            "FROM ticket " +
            "WHERE ticket_id = ? ");
    pst.setInt(1, ticketId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      projectTicketId = rs.getInt("key_count");
    }
    rs.close();
    pst.close();
    return projectTicketId;
  }


}