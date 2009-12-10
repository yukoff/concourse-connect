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

import org.quartz.StatefulJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.communications.dao.EmailUpdatesQueue;
import com.concursive.connect.web.modules.communications.dao.EmailUpdatesQueueList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.Template;

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
      System.out.println("EmailUpdatesJob triggered...");
      while (true) {
        System.out.println("Inside the while loop...");
        //Retrieve retrieve the next item from the email_updates_queue that is scheduled for now.
        EmailUpdatesQueueList queueList = new EmailUpdatesQueueList();
        queueList.setScheduledOnly(true);
        queueList.setMax(1);
        queueList.buildList(db);
        System.out.println("queue size: " + queueList.size());
        if (queueList.size() == 0) {
          LOG.debug("No more scheduled emails to be processed...");
          break;
        } else {
          EmailUpdatesQueue queue = (EmailUpdatesQueue) queueList.get(0);
          //Lock that record by issuing a successful update which updates the status = 1 to status = 2.
          System.out.println("Attempting to lock a queue...");
          if (EmailUpdatesQueue.lockQueue(queue, db)) {
            LOG.debug("Processing scheduled email queue...");
            // User who needs to be sent an email
            User user = UserUtils.loadUser(queue.getEnteredBy());

            //Determine the date range to query the activity stream data
            Calendar now = Calendar.getInstance();
            if (user.getTimeZone() != null) {
              now = Calendar.getInstance(TimeZone.getTimeZone(user.getTimeZone()));
            }
            Timestamp min = queue.getProcessed();
            Timestamp max = new Timestamp(now.getTimeInMillis());

            StringBuffer message = new StringBuffer();
            Project website = ProjectUtils.loadProject("main-profile");
            int emailUpdatesSchedule = -1;
            if (queue.getScheduleOften()) {
              emailUpdatesSchedule = TeamMember.EMAIL_OFTEN;
              message.append("<h1>" + website.getTitle() + " recent updates" + "</h1>");
            } else if (queue.getScheduleDaily()) {
              emailUpdatesSchedule = TeamMember.EMAIL_DAILY;
              message.append("<h1>" + website.getTitle() + " daily update" + "</h1>");
            } else if (queue.getScheduleWeekly()) {
              emailUpdatesSchedule = TeamMember.EMAIL_WEEKLY;
              message.append("<h1>" + website.getTitle() + " weekly update" + "</h1>");
            } else if (queue.getScheduleMonthly()) {
              emailUpdatesSchedule = TeamMember.EMAIL_MONTHLY;
              message.append("<h1>" + website.getTitle() + " monthly update" + "</h1>");
            }
            if (emailUpdatesSchedule == -1) {
              //unexpected case; throw exception
              throw new Exception("The queue does not have a valid schedule type!");
            }


            //Determine the website's site-chatter data to email for this user (if any)
            message.append("<h2>Chatter</h2>");
            message.append("<ol>");
            ProjectHistoryList chatter = new ProjectHistoryList();
            chatter.setProjectId(website.getId());
            chatter.setForMember(user.getId());
            chatter.setLinkObject(ProjectHistoryList.SITE_CHATTER_OBJECT);
            chatter.setEmailUpdatesSchedule(emailUpdatesSchedule);
            chatter.setRangeStart(min);
            chatter.setRangeEnd(max);
            HashMap map = chatter.getList(db);

            Iterator i = map.keySet().iterator();
            while (i.hasNext()) {
              String date = (String) i.next();
              message.append("<li>" + date + "</li>");
              message.append("<ol>");
              ArrayList descriptions = (ArrayList) map.get(date);
              Iterator j = descriptions.iterator();
              while (j.hasNext()) {
                String description = (String) j.next();
                WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(user.getId(), prefs.get(ApplicationPrefs.WEB_DOMAIN_NAME));
                String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, description);
                message.append("<li>" + wikiLinkString + "</li>");
              }
              message.append("</ol>");
            }
            message.append("</ol>");

            // Load the categories
            ProjectCategoryList categoryList = new ProjectCategoryList();
            categoryList.setTopLevelOnly(true);
            categoryList.buildList(db);

            if (categoryList.getIdFromValue("Businesses") != -1) {
              ProjectHistoryList businesses = new ProjectHistoryList();
              businesses.setProjectCategoryId(categoryList.getIdFromValue("Businesses"));
              businesses.setForMember(user.getId());
              businesses.setEmailUpdatesSchedule(emailUpdatesSchedule);
              businesses.setRangeStart(min);
              businesses.setRangeEnd(max);
              HashMap businessMap = businesses.getList(db);

              message.append("<h2>Businesses</h2>");
              message.append("<ol>");

              Iterator k = businessMap.keySet().iterator();
              while (k.hasNext()) {
                String date = (String) k.next();
                message.append("<li>" + date + "</li>");
                message.append("<ol>");
                ArrayList descriptions = (ArrayList) businessMap.get(date);
                Iterator l = descriptions.iterator();
                while (l.hasNext()) {
                  String description = (String) l.next();
                  WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(user.getId(), prefs.get(ApplicationPrefs.WEB_DOMAIN_NAME));
                  String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, description);
                  message.append("<li>" + wikiLinkString + "</li>");
                }
                message.append("</ol>");
              }
              message.append("</ol>");
            }

            if (categoryList.getIdFromValue("Groups") != -1) {
              ProjectHistoryList groups = new ProjectHistoryList();
              groups.setProjectCategoryId(categoryList.getIdFromValue("Groups"));
              groups.setForMember(user.getId());
              groups.setEmailUpdatesSchedule(emailUpdatesSchedule);
              groups.setRangeStart(min);
              groups.setRangeEnd(max);
              HashMap groupMap = groups.getList(db);

              message.append("<h2>Groups</h2>");
              message.append("<ol>");

              Iterator m = groupMap.keySet().iterator();
              while (m.hasNext()) {
                String date = (String) m.next();
                message.append("<li>" + date + "</li>");
                message.append("<ol>");
                ArrayList descriptions = (ArrayList) groupMap.get(date);
                Iterator n = descriptions.iterator();
                while (n.hasNext()) {
                  String description = (String) n.next();
                  WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(user.getId(), prefs.get(ApplicationPrefs.WEB_DOMAIN_NAME));
                  String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, description);
                  message.append("<li>" + wikiLinkString + "</li>");
                }
                message.append("</ol>");
              }
              message.append("</ol>");
            }

            message.append("<p>");
            message.append("This information is sent based on your notification settings.<br/>");
            message.append("<a href=\"${secureUrl}/show/${this.project.uniqueId:html}\" target=\"_blank\">" +
                    "Manage your notifications.</a>");
            message.append("</p>");

            Template template = new Template(message.toString());
            template.addParseElement("${secureUrl}", prefs.get(ApplicationPrefs.WEB_DOMAIN_NAME));
            template.addParseElement("${this.project.uniqueId:html}", website.getUniqueId());
            //Try to send the email
            LOG.debug("Sending email...");
            SMTPMessage email = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
            email.setFrom(prefs.get("EMAILADDRESS"));
            email.addReplyTo(prefs.get("EMAILADDRESS"));
            email.addTo(user.getEmail());
            email.setSubject("Activity Updates");
            email.setType("text/html");
            email.setBody(template.getParsedText());
            email.send();

            //Determine the next schedule date and save the schedule date and status=1
            System.out.println("Calculating next run date...");
            queue.calculateNextRunDate(db);

            //Set the max date to be the queue's processed date
            System.out.println("Updating process date...");
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
