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

package com.concursive.connect.web.modules.profile.jobs;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.processes.dao.Process;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;

import java.sql.Connection;
import java.sql.Timestamp;

/**
 * Responsible for querying twitter Api
 *
 * @author Nanda Kumar
 * @created November 10, 2009
 */

public class TwitterQueryJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(TwitterQueryJob.class);
  public static final String TWITTER_BASE_URL = "http://search.twitter.com/";

  public static void init(SchedulerContext schedulerContext) {
    LOG.info("Twitter search query is initialized");
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {
    long startTime = System.currentTimeMillis();
    LOG.debug("Starting job...");
    SchedulerContext schedulerContext = null;
    Connection db = null;

    try {
      schedulerContext = context.getScheduler().getContext();

      // Determine if the twitter hash is enabled
      ApplicationPrefs prefs = (ApplicationPrefs) schedulerContext.get("ApplicationPrefs");
      String twitterHash = prefs.get(ApplicationPrefs.TWITTER_HASH);
      if (!StringUtils.hasText(twitterHash)) {
        LOG.debug("Hash is not defined exiting from Twitter query job...");
        return;
      }

      db = SchedulerUtils.getConnection(schedulerContext);

      // Determine the previous retrieved twitter id to use for query
      Process process = new Process(db, "TwitterQueryJob");
      long sinceId = process.getLongValue();
      LOG.debug("Last saved twitter id is : " + sinceId);

      // Create Query Object for searching twitter
      Query query = new Query("#" + twitterHash);
      query.setRpp(99);
      if (sinceId > 0) {
        // Set since_id in the query
        query.setSinceId(sinceId);
      }

      // Get the Twitter search results
      Twitter twitter = new Twitter(TWITTER_BASE_URL);
      QueryResult result = twitter.search(query);
      LOG.debug("Found and retrieved " + result.getTweets().size() + " tweet(s).");

      // Iterate through the tweets and store in project history
      int count = 0;
      for (Tweet tweet : result.getTweets()) {
        count++;
        LOG.debug("Got tweet from " + tweet.getFromUser() + " as " + tweet.getText());

        // See if this matches any profiles in the system
        // @note it's possible that more than one project can have the same twitter id
        ProjectList projectList = new ProjectList();
        projectList.setTwitterId(tweet.getFromUser());
        projectList.setApprovedOnly(true);
        projectList.buildList(db);

        // Clean up the tweet output
        String message = tweet.getText();

        // Turn links into wiki links
        message = WikiUtils.addWikiLinks(message);

        // Remove the hash tag - beginning or middle
        message = StringUtils.replace(message, "#" + twitterHash + " ", "");
        // Remove the hash tag - middle or end
        message = StringUtils.replace(message, " #" + twitterHash, "");
        // Remove the hash tag - untokenized
        message = StringUtils.replace(message, "#" + twitterHash, "");

        // Update the activity stream for the matching profiles
        for (Project project : projectList) {
          ProjectHistory projectHistory = new ProjectHistory();
          projectHistory.setProjectId(project.getId());
          projectHistory.setEnabled(true);
          // If there is a user profile, use the user's id, else use the businesses id? or use a different event
          if (project.getProfile()) {
            projectHistory.setEnteredBy(project.getOwner());
          } else {
            projectHistory.setEnteredBy(project.getOwner());
          }
          projectHistory.setLinkStartDate(new Timestamp(System.currentTimeMillis()));
          String desc = WikiLink.generateLink(project) + " [[http://twitter.com/" + tweet.getFromUser() + "/statuses/" + tweet.getId() + " tweeted]] " + message;
          projectHistory.setDescription(desc);
          projectHistory.setLinkItemId(project.getId());
          projectHistory.setLinkObject(ProjectHistoryList.TWITTER_OBJECT);
          projectHistory.setEventType(ProjectHistoryList.TWITTER_EVENT);
          // Store the tweets in project history
          projectHistory.insert(db);
        }
        // Set the tweet id as since_Id
        if (sinceId < tweet.getId()) {
          sinceId = tweet.getId();
        }
      }
      //update the recent sinceId and process timestamp
      process.setLongValue(sinceId);
      process.setProcessed(new Timestamp(new java.util.Date().getTime()));
      process.update(db);

      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      LOG.debug("Finished: " + count + " took " + totalTime + " ms");
    } catch (Exception e) {
      LOG.error("TwitterQueryJob Exception", e);
      throw new JobExecutionException(e.getMessage());
    } finally {
      SchedulerUtils.freeConnection(schedulerContext, db);
    }
  }
}