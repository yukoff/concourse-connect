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
package com.concursive.connect.web.modules.communications.jobs;

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.communications.dao.EmailUpdatesQueue;
import com.concursive.connect.web.modules.communications.dao.EmailUpdatesQueueList;
import com.concursive.connect.web.modules.communications.utils.EmailUpdatesUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Job to process the scheduled email udpates queue and send emails
 *
 * @author Ananth
 * @created Dec 2, 2009
 */
public class EmailUpdatesJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(EmailUpdatesJob.class);

  /**
   * Process email updates queue
   *
   * @param context
   * @throws JobExecutionException
   */
  public void execute(JobExecutionContext context) throws JobExecutionException {
    SchedulerContext schedulerContext = null;
    Connection db = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      db = SchedulerUtils.getConnection(schedulerContext);
      ApplicationPrefs prefs = (ApplicationPrefs) schedulerContext.get(
          "ApplicationPrefs");
      ServletContext servletContext = (ServletContext) schedulerContext.get("ServletContext");
      LOG.debug("EmailUpdatesJob triggered...");
      while (true) {
        //Retrieve retrieve the next item from the email_updates_queue that is scheduled for now.
        EmailUpdatesQueueList queueList = new EmailUpdatesQueueList();
        queueList.setScheduledOnly(true);
        queueList.setMax(1);
        queueList.buildList(db);
        if (queueList.size() == 0) {
          LOG.debug("No more scheduled emails to be processed...");
          break;
        } else {
          EmailUpdatesQueue queue = (EmailUpdatesQueue) queueList.get(0);
          //Lock that record by issuing a successful update which updates the status = 1 to status = 2.
          if (EmailUpdatesQueue.lockQueue(queue, db)) {
            LOG.debug("Processing scheduled email queue...");

            // User who needs to be sent an email
            User user = UserUtils.loadUser(queue.getEnteredBy());

            //Determine the date range to query the activity stream data
            Timestamp min = queue.getProcessed();
            Timestamp max = new Timestamp(System.currentTimeMillis());

            if (min == null) {
              //set the min value to be queue's entered date to restrict picking up all the records in the system
              min = queue.getEntered();
            }

            Configuration configuration = ApplicationPrefs.getFreemarkerConfiguration(servletContext);
            // Determine the message to be sent
            String message = EmailUpdatesUtils.getEmailHTMLMessage(db, prefs, configuration, queue, min, max);

            if (message != null) {
              // Use the user's locale to format the date
              SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.SHORT, user.getLocale());
              formatter.applyPattern(DateUtils.get4DigitYearDateFormat(formatter.toLocalizedPattern()));
              String date = formatter.format(max);

              String subject = "";
              if (queue.getScheduleOften()) {
                subject = "Activity Updates for " + date + " - Recent updates";
              } else if (queue.getScheduleDaily()) {
                subject = "Activity Updates for " + date + " - Daily update";
              } else if (queue.getScheduleWeekly()) {
                subject = "Activity Updates for " + date + " - Weekly update";
              } else if (queue.getScheduleMonthly()) {
                subject = "Activity Updates for " + date + " - Monthly update";
              }
              //Try to send the email
              LOG.debug("Sending email...");
              SMTPMessage email = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
                email.setFrom(prefs.get(ApplicationPrefs.EMAILADDRESS));
                email.addReplyTo(prefs.get(ApplicationPrefs.EMAILADDRESS));
              email.addTo(user.getEmail());
              email.setSubject(subject);
              email.setType("text/html");
              email.setBody(message);
              email.send();
            } else {
              LOG.debug("No activities to report. Skipping email.");
            }

            //Determine the next schedule date and save the schedule date and status=1
            LOG.debug("Calculating next run date...");
            queue.calculateNextRunDate(db);

            //Set the max date to be the queue's processed date
            LOG.debug("Updating process date...");
            queue.updateProcessedDate(db, max);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new JobExecutionException(e.getMessage());
    } finally {
      SchedulerUtils.freeConnection(schedulerContext, db);
    }
  }
}
