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

package com.concursive.connect.web.modules.reviews.contribution;

import com.concursive.connect.web.modules.common.social.contribution.ContributionCalculation;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.common.social.rating.dao.RatingList;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.reviews.dao.ProjectRating;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for calculating user contributions based on the number of reviews that people liked
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created February 14, 2009
 */

public class ContributionCalculationForReviewsLiked extends ContributionCalculation {

  public void process(Connection db, LookupContribution lookupContribution) throws SQLException {

    lastRun = lookupContribution.getRunDate();
    currentRun = new Timestamp(System.currentTimeMillis());
    userPoints = new HashMap<String, Integer>();

    // Page through the updates just in case there are lots of users do 100 at a time...
    PagedListInfo pagedListInfo = new PagedListInfo();
    pagedListInfo.setItemsPerPage(100);
    pagedListInfo.setDefaultSort("entered", "DESC");

    // Use the paged list to paged through all users
    RatingList ratingList = new RatingList();
    ratingList.setPagedListInfo(pagedListInfo);
    ratingList.setTable(ProjectRating.TABLE);
    ratingList.setUniqueField(ProjectRating.PRIMARY_KEY);
    ratingList.setPrimaryKeyField(Rating.PRIMARY_KEY_RECORD_ID);
    ratingList.setHasInappropriate(true);
    ratingList.setEnteredRangeStart(lastRun);
    ratingList.setEnteredRangeEnd(currentRun);
    ratingList.buildList(db);

    ArrayList<Integer> projectRatingIdList = new ArrayList<Integer>();

    while (ratingList.size() > 0) {
      for (Rating rating : ratingList) {
        int ratingId = rating.getObjectId();
        if (!projectRatingIdList.contains(ratingId)) {
          int givenRating = rating.getRating();
          //rating of 1 means the review was helpful
          //currently does not consider how many people marked it as inappropriate
          if (givenRating == 1) {
            //Insure that a project rating is counted only once
            ProjectRating projectRating = new ProjectRating(db, ratingId);
            projectRatingIdList.add(ratingId);
            int userId = projectRating.getEnteredBy();
            int projectId = projectRating.getProjectId();

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
      }

      // Always reset the list or else the records are included on buildList again
      ratingList.clear();
      if (pagedListInfo.getPage() < pagedListInfo.getNumberOfPages()) {
        pagedListInfo.setCurrentPage(pagedListInfo.getPage() + 1);
        ratingList.buildList(db);
      }
    }

    updatePoints(db, lookupContribution);
  }

}
