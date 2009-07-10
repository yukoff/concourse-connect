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

package com.concursive.connect.web.modules.wiki.jobs;

import com.concursive.commons.date.DateUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.beans.WikiExportBean;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.utils.WikiPDFUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Responsible for exporting wiki pages
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 6, 2007
 */

public class WikiExporterJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(WikiExporterJob.class);

  public static final String WIKI_EXPORT_ARRAY = "WikiExportArray";
  public static final String WIKI_AVAILABLE_ARRAY = "WikiAvailableArray";

  public static void init(SchedulerContext schedulerContext) {
    schedulerContext.put(WIKI_EXPORT_ARRAY, new Vector());
    schedulerContext.put(WIKI_AVAILABLE_ARRAY, new Vector());
    LOG.info("Wiki export queues initialized");
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {
    LOG.debug("Starting job...");
    SchedulerContext schedulerContext = null;
    Connection db = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      ApplicationPrefs prefs = (ApplicationPrefs) schedulerContext.get(
          "ApplicationPrefs");
      String fs = System.getProperty("file.separator");
      db = SchedulerUtils.getConnection(schedulerContext);
      Vector exportList = (Vector) schedulerContext.get(WIKI_EXPORT_ARRAY);
      Vector availableList = (Vector) schedulerContext.get(WIKI_AVAILABLE_ARRAY);
      while (exportList.size() > 0) {
        WikiExportBean bean = (WikiExportBean) exportList.get(0);
        LOG.debug("Exporting a wiki (" + bean.getWikiId() + ")...");
        User user = UserUtils.loadUser(bean.getUserId());
        // Load the project
        Project thisProject = new Project(db, bean.getProjectId());
        // Load the wiki
        Wiki wiki = new Wiki(db, bean.getWikiId(), thisProject.getId());
        // See if a recent export is already available
        long currentDate = System.currentTimeMillis();
        String destDir = prefs.get("FILELIBRARY") + user.getGroupId() + fs + "wiki" + fs +
            DateUtils.getDatePath(new Date(currentDate));
        File destPath = new File(destDir);
        destPath.mkdirs();
        String filename = "wiki-" + bean.getWikiId() + "-" + bean.getIncludeTitle() + "-" + bean.getFollowLinks() + "-" + WikiUtils.getLatestModifiedDate(wiki, bean.getFollowLinks(), db).getTime();
        File exportFile = new File(destDir + filename);
        WikiExportBean existingBean = getExisting(exportFile, availableList);
        if (existingBean != null) {
          if (existingBean.getUserId() == bean.getUserId()) {
            // This user already has a valid file ready so a new record isn't needed
            LOG.debug("Exported file already exists (" + existingBean.getWikiId() + ") and was requested by this user");
          } else {
            // Tell the new bean the existing bean's details which another user ran
            LOG.debug("Exported file already exists (" + existingBean.getWikiId() + ") and will be reused for this user");
            bean.setExportedFile(existingBean.getExportedFile());
            availableList.add(bean);
          }
        } else {
          // No existing PDF exists so export to PDF
          if (exportFile.exists()) {
            LOG.debug("Found the requested file in the FileLibrary (" + wiki.getId() + ")...");
          } else {
            LOG.debug("Generating a new file for wiki (" + wiki.getId() + ")...");
            WikiPDFUtils.exportToFile(thisProject, wiki, exportFile, new HashMap(), db, prefs.get("FILELIBRARY"), bean);
          }
          bean.setExportedFile(exportFile);
          bean.setFileSize(exportFile.length());
          availableList.add(bean);
        }
        exportList.remove(0);
      }
    } catch (Exception e) {
      LOG.error("WikiExporterJob Exception", e);
      throw new JobExecutionException(e.getMessage());
    } finally {
      SchedulerUtils.freeConnection(schedulerContext, db);
    }
  }

  protected WikiExportBean getExisting(File checkFile, Vector collectionToCheck) throws Exception {
    Iterator i = collectionToCheck.iterator();
    while (i.hasNext()) {
      WikiExportBean bean = (WikiExportBean) i.next();
      if (bean.getExportedFile().toURL().equals(checkFile.toURL())) {
        return bean;
      }
    }
    return null;
  }

}