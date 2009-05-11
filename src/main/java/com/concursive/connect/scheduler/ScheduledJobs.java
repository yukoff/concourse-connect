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

package com.concursive.connect.scheduler;

import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.jobs.DirectoryIndexerJob;
import com.concursive.connect.indexer.jobs.IndexerJob;
import com.concursive.connect.web.modules.common.social.contribution.jobs.UserContributionJob;
import com.concursive.connect.web.modules.common.social.images.jobs.ImageResizerJob;
import com.concursive.connect.web.modules.fileattachments.jobs.CleanupTempFilesJob;
import com.concursive.connect.web.modules.login.jobs.DeleteDisabledUsersJob;
import com.concursive.connect.web.modules.reports.jobs.CleanupReportsJob;
import com.concursive.connect.web.modules.reports.jobs.ReportsJob;
import com.concursive.connect.web.modules.translation.jobs.UpdateTranslationPercentageJob;
import com.concursive.connect.web.modules.wiki.jobs.WikiExporterJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import java.util.Date;

/**
 * These jobs are background tasks that get initialized during webapp startup
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Jun 20, 2005
 */

public class ScheduledJobs {

  public static String CONTEXT_SCHEDULER_GROUP = "SCHEDULER_GROUP";

  public static void addJobs(Scheduler scheduler) throws SchedulerException {
    // Use the default group for this instance
    String uniqueGroup = Scheduler.DEFAULT_GROUP;
    // Share the group context by using the scheduler
    scheduler.getContext().put(CONTEXT_SCHEDULER_GROUP, uniqueGroup);
    // Get the Application prefs...
    ApplicationPrefs prefs = (ApplicationPrefs) scheduler.getContext().get("ApplicationPrefs");

    // Update Translating Percentages
    boolean doTranslations = false;
    if (prefs.has("BACKGROUND.TRANSLATION_UPDATE")) {
      doTranslations = "true".equals(prefs.get("BACKGROUND.TRANSLATION_UPDATE"));
    }
    if (doTranslations) {
      JobDetail job = new JobDetail(
          "updateTranslationPercentage",
          uniqueGroup,
          UpdateTranslationPercentageJob.class);
      // Update every 30 minutes, starting in 5 minutes
      long startTime = System.currentTimeMillis() + (5L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "updateTranslationPercentage",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          30L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }

    // Indexer (uses factory)
    boolean doIndexer = true;
    if (prefs.has("BACKGROUND.INDEXER")) {
      doIndexer = "true".equals(prefs.get("BACKGROUND.INDEXER"));
    }
    if (doIndexer) {
      {
        // Main Indexer
        JobDetail job = new JobDetail(
            "indexer",
            uniqueGroup,
            IndexerJob.class);
        // Update every 24 hours, starting in 5 minutes
        long startTime = System.currentTimeMillis() + (5L * 60L * 1000L);
        SimpleTrigger trigger = new SimpleTrigger(
            "indexer",
            uniqueGroup,
            new Date(startTime),
            null,
            SimpleTrigger.REPEAT_INDEFINITELY,
            24L * 60L * 60L * 1000L);
        scheduler.scheduleJob(job, trigger);
      }

      {
        // Directory Indexer
        JobDetail job = new JobDetail(
            "directoryIndexer",
            uniqueGroup,
            DirectoryIndexerJob.class);
        // Start in 10 seconds of startup and don't repeat
        long startTime = System.currentTimeMillis() + (10L * 1000L);
        SimpleTrigger trigger = new SimpleTrigger(
            "directoryIndexer",
            uniqueGroup,
            new Date(startTime));
        scheduler.scheduleJob(job, trigger);
      }
    }

    // Wiki Exporter (uses local queue)
    boolean doWikiExports = true;
    if (prefs.has("BACKGROUND.WIKI_EXPORTER")) {
      doWikiExports = "true".equals(prefs.get("BACKGROUND.WIKI_EXPORTER"));
    }
    if (doWikiExports) {
      JobDetail job = new JobDetail(
          "wikiExporter",
          uniqueGroup,
          WikiExporterJob.class);
      // Update every 24 hours, starting in 6 minutes
      long startTime = System.currentTimeMillis() + (6L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "wikiExporter",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          24L * 60L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }

    // Report runner
    boolean doReports = true;
    if (prefs.has("BACKGROUND.REPORTS")) {
      doReports = "true".equals(prefs.get("BACKGROUND.REPORTS"));
    }
    if (doReports) {
      // Execute every 2 minutes, starting in 5 minutes
      // This job is also executed immediately when a new report is added
      JobDetail job = new JobDetail(
          "reports", uniqueGroup, ReportsJob.class);
      long startTime = System.currentTimeMillis() + (5L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "reports",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          2L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }

    // Report cleanup
    boolean doReportsCleanUp = true;
    if (prefs.has("BACKGROUND.REPORTS_CLEANUP")) {
      doReportsCleanUp = "true".equals(prefs.get("BACKGROUND.REPORTS_CLEANUP"));
    }
    if (doReportsCleanUp) {
      // Execute every 30 minutes, starting in 6 minutes
      // This job is also executed immediately when a new report is added
      JobDetail job = new JobDetail(
          "cleanupReports", uniqueGroup, CleanupReportsJob.class);
      long startTime = System.currentTimeMillis() + (6L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "cleanupReports",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          30L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }

    // Temporary file uploads cleanup
    boolean doCleanupTempFiles = true;
    if (prefs.has("BACKGROUND.TEMP_FILES_CLEANUP")) {
      doCleanupTempFiles = "true".equals(prefs.get("BACKGROUND.TEMP_FILES_CLEANUP"));
    }
    if (doCleanupTempFiles) {
      // Execute every 30 minutes, starting in 3 minutes
      JobDetail job = new JobDetail(
          "cleanupTempFiles", uniqueGroup, CleanupTempFilesJob.class);
      long startTime = System.currentTimeMillis() + (3L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "cleanupTempFiles",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          30L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }

    // Delete disabled users when they are no longer referenced
    boolean doDeleteDisabledUsers = false;
    if (prefs.has("BACKGROUND.DELETE_DISABLED_USERS")) {
      doDeleteDisabledUsers = "true".equals(prefs.get("BACKGROUND.DELETE_DISABLED_USERS"));
    }
    if (doDeleteDisabledUsers) {
      // Execute every 6 hours, starting in 20 minutes
      JobDetail job = new JobDetail(
          "deleteDisabledUsers", uniqueGroup, DeleteDisabledUsersJob.class);
      long startTime = System.currentTimeMillis() + (20L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "deleteDisabledUsers",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          6L * 60L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }

    // Image resizer (uses local queue)
    boolean doImageResizing = true;
    if (prefs.has("BACKGROUND.IMAGE_RESIZER")) {
      doImageResizing = "true".equals(prefs.get("BACKGROUND.IMAGE_RESIZER"));
    }
    if (doImageResizing) {
      JobDetail job = new JobDetail(
          "imageResizer",
          uniqueGroup,
          ImageResizerJob.class);
      // Update every 24 hours, starting in 7 minutes
      long startTime = System.currentTimeMillis() + (7L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "imageResizer",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          24L * 60L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }

    // Top Contributions
    boolean doCalculateTopContributors = true;
    if (prefs.has("BACKGROUND.CALCULATE_TOP_CONTRIBUTORS")) {
      doCalculateTopContributors = "true".equals(prefs.get("BACKGROUND.CALCULATE_TOP_CONTRIBUTORS"));
    }
    if (doCalculateTopContributors) {
      JobDetail job = new JobDetail(
          "userContribution",
          uniqueGroup,
          UserContributionJob.class);
      // Update every 24 hours, starting in 5 minutes
      long startTime = System.currentTimeMillis() + (5L * 60L * 1000L);
      SimpleTrigger trigger = new SimpleTrigger(
          "userContribution",
          uniqueGroup,
          new Date(startTime),
          null,
          SimpleTrigger.REPEAT_INDEFINITELY,
          24L * 60L * 60L * 1000L);
      scheduler.scheduleJob(job, trigger);
    }
  }
}
