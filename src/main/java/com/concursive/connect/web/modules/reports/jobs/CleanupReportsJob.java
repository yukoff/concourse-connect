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

package com.concursive.connect.web.modules.reports.jobs;

import com.concursive.commons.date.DateUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.reports.dao.ReportQueue;
import com.concursive.connect.web.modules.reports.dao.ReportQueueList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.util.Iterator;

/**
 * Removes processed and expired reports
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Jun 20, 2005
 */

public class CleanupReportsJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(CleanupReportsJob.class);

  public void execute(JobExecutionContext context) throws JobExecutionException {
    SchedulerContext schedulerContext = null;
    Connection db = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      ServletContext servletContext = (ServletContext) schedulerContext.get(
          "ServletContext");
      ApplicationPrefs prefs = (ApplicationPrefs) schedulerContext.get(
          "ApplicationPrefs");
      String fs = System.getProperty("file.separator");
      db = SchedulerUtils.getConnection(schedulerContext);
      LOG.debug("Checking reports...");
      // Load the report queue for this site, processed only, whether successful or not
      ReportQueueList queue = new ReportQueueList();
      queue.setSortAscending(true);
      queue.setProcessedOnly(true);
      queue.setExpiredOnly(true);
      queue.buildList(db);
      // Iterate the list
      Iterator list = queue.iterator();
      while (list.hasNext()) {
        ReportQueue thisQueue = (ReportQueue) list.next();
        User user = UserUtils.loadUser(thisQueue.getEnteredBy());
        thisQueue.delete(
            db,
            prefs.get("FILELIBRARY") + user.getGroupId() + fs + "projects" + fs +
                DateUtils.getDatePath(thisQueue.getEntered()));
      }
    } catch (Exception e) {
      LOG.error("CleanupReportsJob Exception", e);
      throw new JobExecutionException(e.getMessage());
    } finally {
      SchedulerUtils.freeConnection(schedulerContext, db);
    }
  }
}
