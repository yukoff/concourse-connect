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

package com.concursive.connect.web.modules.common.social.contribution.jobs;

import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.contribution.dao.LookupContribution;
import com.concursive.connect.web.modules.contribution.dao.LookupContributionList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Responsible for recording user contribution
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created January 28, 2009
 */

public class UserContributionJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(UserContributionJob.class);

  public void execute(JobExecutionContext context) throws JobExecutionException {
    LOG.debug("Starting job...");
    SchedulerContext schedulerContext = null;
    Connection db = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      ApplicationPrefs prefs = (ApplicationPrefs) schedulerContext.get(
          "ApplicationPrefs");
      db = SchedulerUtils.getConnection(schedulerContext);

      // Get the list of contribution criteria
      LookupContributionList lookupContributionList = new LookupContributionList();
      lookupContributionList.setEnabled(Constants.TRUE);
      lookupContributionList.buildList(db);

      HashMap<String, HashMap<Integer, Integer>> completeUserMap = new HashMap<String, HashMap<Integer, Integer>>();
      for (LookupContribution lookupContribution : lookupContributionList) {
        Object classRef = null;
        classRef = Class.forName(lookupContribution.getConstant()).newInstance();

        //passing lookup contribution as that could have rules to determine the points
        Method method = classRef.getClass().getMethod("process", new Class[]{Connection.class, LookupContribution.class});
        HashMap<Integer, Integer> userMap = (HashMap<Integer, Integer>) method.invoke(classRef, new Object[]{db, lookupContribution});
        completeUserMap.put(lookupContribution.getConstant(), userMap);
      }

    } catch (Exception e) {
      LOG.error("UserContributionJob Exception", e);
      throw new JobExecutionException(e.getMessage());
    } finally {
      SchedulerUtils.freeConnection(schedulerContext, db);
    }
  }
}