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
package com.concursive.connect.web.modules.login.jobs;

import com.concursive.connect.Constants;
import com.concursive.connect.indexer.IndexEvent;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import java.sql.Connection;
import java.util.Vector;

/**
 * User: matt rajkowski
 * Date: Mar 6, 2008
 * Time: 4:50:06 PM
 * Attempts to delete disabled users -- will fail due to referential integrity
 */
public class DeleteDisabledUsersJob implements StatefulJob {

  public void execute(JobExecutionContext context) throws JobExecutionException {
    SchedulerContext schedulerContext = null;
    Connection db = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      db = SchedulerUtils.getConnection(schedulerContext);

      if (System.getProperty("DEBUG") != null) {
        System.out.println(
            "DeleteDisabledUsersJob-> Deleting disabled users...");
      }

      int count = 0;
      UserList userList = new UserList();
      userList.setValidUser(Constants.FALSE);
      userList.buildList(db);
      for (User user : userList) {
        try {
          // Double-check and delete the user
          if (UserUtils.isUserDisabled(user)) {
            user.delete(db);
            // Remove the user's profile from the index
            if (user.getProfileProjectId() > -1) {
              IndexEvent indexEvent = new IndexEvent(user.getProfileProject(), IndexEvent.DELETE);
              ((Vector) schedulerContext.get("IndexArray")).add(indexEvent);
              context.getScheduler().triggerJob("indexer", (String) schedulerContext.get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
            }
          }
          ++count;
        } catch (Exception e) {
          // Expected
          // User must have data references preventing delete
        }
      }
      if (count > 0) {
        System.out.println(
            "DeleteDisabledUsersJob-> Disabled users have been deleted: " + count);
      }
    } catch (Exception e) {
      throw new JobExecutionException(e.getMessage());
    } finally {
      SchedulerUtils.freeConnection(schedulerContext, db);
    }
  }
}
