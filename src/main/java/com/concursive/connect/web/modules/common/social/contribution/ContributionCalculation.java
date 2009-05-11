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

package com.concursive.connect.web.modules.common.social.contribution;

import com.concursive.connect.web.modules.common.social.contribution.dao.UserContributionLog;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.login.dao.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Abstract class for updating user points based on contribution
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created January 31, 2009
 */

public abstract class ContributionCalculation implements ContributionCalculationInterface {

  protected Timestamp lastRun = null;
  protected Timestamp currentRun = null;
  protected HashMap<String, Integer> userPoints = null;

  protected void updatePoints(Connection db, LookupContribution lookupContribution) throws SQLException {

    Iterator<String> keysItr = userPoints.keySet().iterator();
    while (keysItr.hasNext()) {
      String userIdandProjectId = keysItr.next();
      Integer points = userPoints.get(userIdandProjectId);

      String[] keyParts = userIdandProjectId.split("-");

      int userId = Integer.parseInt(keyParts[0]);
      int projectId = Integer.parseInt(keyParts[1]);

      //Increment user points
      User.incrementPoints(db, userId, points);

      //Insert a user contribution record
      UserContributionLog userContributionLog = new UserContributionLog();
      userContributionLog.setUserId(userId);
      userContributionLog.setProjectId(projectId);
      userContributionLog.setPoints(points);
      userContributionLog.setContributionDate(currentRun);
      userContributionLog.setContributionId(lookupContribution.getId());
      userContributionLog.insert(db);

    }
    // Set the run date for the contribution
    lookupContribution.setRunDate(currentRun);
    lookupContribution.update(db);
  }
}
