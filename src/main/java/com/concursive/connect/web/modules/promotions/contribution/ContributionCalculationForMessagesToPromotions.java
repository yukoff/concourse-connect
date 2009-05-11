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

package com.concursive.connect.web.modules.promotions.contribution;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.contribution.ContributionCalculation;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.messages.dao.PrivateMessage;
import com.concursive.connect.web.modules.messages.dao.PrivateMessageList;
import com.concursive.connect.web.modules.promotions.dao.Ad;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for calculating user contributions based on the number promotions that have received a message
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created February 12, 2009
 */

public class ContributionCalculationForMessagesToPromotions extends ContributionCalculation {

  public void process(Connection db, LookupContribution lookupContribution) throws SQLException {

    lastRun = lookupContribution.getRunDate();
    currentRun = new Timestamp(System.currentTimeMillis());
    userPoints = new HashMap<String, Integer>();

    // Page through the updates just in case there are lots of users do 100 at a time...
    PagedListInfo pagedListInfo = new PagedListInfo();
    pagedListInfo.setItemsPerPage(100);
    pagedListInfo.setDefaultSort("entered", "DESC");

    // Use the paged list to paged through all users
    PrivateMessageList privateMessageList = new PrivateMessageList();
    privateMessageList.setPagedListInfo(pagedListInfo);
    privateMessageList.setLinkModuleId(Constants.PROJECT_AD_FILES);
    privateMessageList.setEnteredRangeStart(lastRun);
    privateMessageList.setEnteredRangeEnd(currentRun);
    privateMessageList.buildList(db);

    ArrayList<Integer> adList = new ArrayList<Integer>();

    while (privateMessageList.size() > 0) {
      for (PrivateMessage privateMessage : privateMessageList) {
        int adId = privateMessage.getLinkItemId();
        //Insure that an ad is counted only once
        if (!adList.contains(adId)) {
          adList.add(adId);
          Ad ad = new Ad(db, adId);
          int userId = ad.getEnteredBy();
          int projectId = ad.getProjectId();

          String userIdAndProjectId = userId + "-" + projectId;
          if (userPoints.containsKey(userIdAndProjectId)) {
            int currentPoints = userPoints.get(userIdAndProjectId);
            currentPoints = currentPoints + lookupContribution.getPointsAwarded();
            userPoints.put(userIdAndProjectId, currentPoints);
          } else {
            userPoints.put(userIdAndProjectId, lookupContribution.getPointsAwarded());
          }
        }
      }

      // Always reset the list or else the records are included on buildList again
      privateMessageList.clear();
      if (pagedListInfo.getPage() < pagedListInfo.getNumberOfPages()) {
        pagedListInfo.setCurrentPage(pagedListInfo.getPage() + 1);
        privateMessageList.buildList(db);
      }
    }

    updatePoints(db, lookupContribution);
  }

}
