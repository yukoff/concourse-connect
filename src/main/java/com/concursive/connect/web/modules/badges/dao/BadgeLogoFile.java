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

package com.concursive.connect.web.modules.badges.dao;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents the configuration of a fileItem
 *
 * @author matt rajkowski
 * @created March 13, 2009
 */
public class BadgeLogoFile extends FileItem {

  private static Log LOG = LogFactory.getLog(BadgeLogoFile.class);

  public BadgeLogoFile() {
  }


  public BadgeLogoFile(ResultSet rs) throws SQLException {
    buildRecord(rs, false);
  }

  public BadgeLogoFile(Connection db, int itemId) throws SQLException {
    queryRecord(db, itemId);
  }

  public boolean insert(Connection db) throws SQLException {
    boolean result = false;
    // The required linkModuleId
    linkModuleId = Constants.BADGE_FILES;
    // Determine if the database is in auto-commit mode
    boolean doCommit = false;
    try {
      if (doCommit = db.getAutoCommit()) {
        db.setAutoCommit(false);
      }
      // Insert the record
      result = super.insert(db);
      // Update the referenced pointer
      if (result) {
        int i = 0;
        PreparedStatement pst = db.prepareStatement(
            "UPDATE badge " +
                "SET logo_id = ? " +
                "WHERE badge_id = ? ");
        pst.setInt(++i, id);
        pst.setInt(++i, linkItemId);
        int count = pst.executeUpdate();
        result = (count == 1);
      }
      if (doCommit) {
        db.commit();
      }
    } catch (Exception e) {
      if (doCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (doCommit) {
        db.setAutoCommit(true);
      }
    }
    return result;
  }

}