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
package com.concursive.connect.web.modules.communications.utils;

import com.concursive.commons.web.URLFactory;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.communications.dao.EmailUpdatesQueue;
import com.concursive.connect.web.modules.communications.dao.EmailUpdatesQueueList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.PagedListInfo;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility methods used by the EmailUpdatesJob
 *
 * @author Ananth
 * @created Dec 9, 2009
 */
public class EmailUpdatesUtils {

  private static Log LOG = LogFactory.getLog(EmailUpdatesUtils.class);

  public static String getEmailHTMLMessage(Connection db, ApplicationPrefs prefs, Configuration configuration, EmailUpdatesQueue queue,
                                           Timestamp min, Timestamp max) throws Exception {
    // User who needs to be sent an email
    User user = UserUtils.loadUser(queue.getEnteredBy());

    Project website = ProjectUtils.loadProject("main-profile");
    int emailUpdatesSchedule = -1;
    String title = "";
    if (queue.getScheduleOften()) {
      emailUpdatesSchedule = TeamMember.EMAIL_OFTEN;
      title = website.getTitle() + " recent updates";
    } else if (queue.getScheduleDaily()) {
      emailUpdatesSchedule = TeamMember.EMAIL_DAILY;
      title = website.getTitle() + " daily update";
    } else if (queue.getScheduleWeekly()) {
      emailUpdatesSchedule = TeamMember.EMAIL_WEEKLY;
      title = website.getTitle() + " weekly update";
    } else if (queue.getScheduleMonthly()) {
      emailUpdatesSchedule = TeamMember.EMAIL_MONTHLY;
      title = website.getTitle() + " monthly update";
    }
    if (emailUpdatesSchedule == -1) {
      //unexpected case; throw exception
      throw new Exception("The queue does not have a valid schedule type!");
    }
    if (URLFactory.createURL(prefs.getPrefs()) == null) {
      throw new Exception("The server URL is not specified. Please contact the system administrator to configure the remote server's URL!");
    }

    // Populate the message template
    freemarker.template.Template template = configuration.getTemplate("scheduled_activity_updates_email_body-html.ftl");
    Map bodyMappings = new HashMap();
    bodyMappings.put("title", title);
    bodyMappings.put("link", new HashMap());
    ((Map) bodyMappings.get("link")).put("settings", URLFactory.createURL(prefs.getPrefs()) + "/show/" + user.getProfileUniqueId());

    // Load the RSS config file to determine the objects for display
    String fileName = "scheduled_emails_en_US.xml";
    URL resource = EmailUpdatesUtils.class.getResource("/" + fileName);
    LOG.debug("Schedule emails config file: " + resource.toString());
    XMLUtils library = new XMLUtils(resource);

    String purpose = prefs.get(ApplicationPrefs.PURPOSE);
    LOG.debug("Purpose: " + purpose);
    Element emailElement = XMLUtils.getElement(library.getDocumentElement(), "email", "events", "site," + purpose);
    if (emailElement == null) {
      emailElement = XMLUtils.getElement(library.getDocumentElement(), "email", "events", "site");
    }
    if (emailElement != null) {
      LinkedHashMap categories = new LinkedHashMap();

      PagedListInfo info = new PagedListInfo();
      String limit = emailElement.getAttribute("limit");
      info.setItemsPerPage(limit);
      int activityCount = 0;

      //Determine the website's site-chatter data to email for this user (if any)
      ProjectHistoryList chatter = new ProjectHistoryList();
      chatter.setProjectId(website.getId());
      chatter.setLinkObject(ProjectHistoryList.SITE_CHATTER_OBJECT);
      chatter.setRangeStart(min);
      chatter.setRangeEnd(max);
      chatter.setPagedListInfo(info);
      chatter.forMemberEmailUpdates(user.getId(), emailUpdatesSchedule);
      LinkedHashMap map = chatter.getList(db, user.getId(), URLFactory.createURL(prefs.getPrefs()));
      activityCount += map.size();
      if (map.size() != 0) {
        categories.put("Chatter", map);
      }

      // Determine the types of events to display based on the config file
      ArrayList<Element> eventElements = new ArrayList<Element>();
      XMLUtils.getAllChildren(emailElement, "event", eventElements);

      // set all the requested types based on the types we allow for the query..
      ArrayList<String> types = new ArrayList<String>();
      for (Element eventElement : eventElements) {
        String type = XMLUtils.getNodeText(eventElement);
        types.add(type);
      }
      LOG.debug("Event Types: " + types);
      // Load the categories
      ProjectCategoryList categoryList = new ProjectCategoryList();
      categoryList.setTopLevelOnly(true);
      categoryList.setEnabled(Constants.TRUE);
      categoryList.buildList(db);

      for (ProjectCategory category : categoryList) {
        ProjectHistoryList activities = new ProjectHistoryList();
        activities.setProjectCategoryId(category.getId());
        activities.setRangeStart(min);
        activities.setRangeEnd(max);
        activities.setObjectPreferences(types);
        activities.setPagedListInfo(info);
        activities.forMemberEmailUpdates(user.getId(), emailUpdatesSchedule);
        LinkedHashMap activityMap = activities.getList(db, user.getId(), URLFactory.createURL(prefs.getPrefs()));
        activityCount += activityMap.size();
        if (activityMap.size() != 0) {
          categories.put(category.getDescription(), activityMap);
        }
      }

      if (activityCount == 0) {
        //Don't send an email update
        return null;
      }
      bodyMappings.put("categories", categories);
    }

    // Parse and return
    StringWriter emailBodyTextWriter = new StringWriter();
    template.process(bodyMappings, emailBodyTextWriter);

    return emailBodyTextWriter.toString();
  }

  /**
   * Retruns 'true' if the member has any email updates queues configured
   *
   * @param db
   * @param memberId
   * @return
   * @throws SQLException
   */
  public static boolean hasMemberQueue(Connection db, int memberId) throws SQLException {
    return EmailUpdatesUtils.hasMemberQueue(db, memberId, -1);
  }

  /**
   * Returns 'true' if the member has any particular type of email updates queues configured
   * 
   * @param db
   * @param memberId
   * @param queueType
   * @return
   * @throws SQLException
   */
  public static boolean hasMemberQueue(Connection db, int memberId, int queueType) throws SQLException {
    EmailUpdatesQueueList queues = new EmailUpdatesQueueList();
    queues.setEnteredBy(memberId);
    queues.setType(queueType);
    queues.buildList(db);

    return (queues.size() > 0);
  }

  public static void saveQueue(Connection db, TeamMember teamMember) throws SQLException {
    if (teamMember.getEmailUpdatesSchedule() != TeamMember.EMAIL_NEVER) {
      if (!EmailUpdatesUtils.hasMemberQueue(db, teamMember.getUserId(), teamMember.getEmailUpdatesSchedule())) {
        //Populate specified email update queue if it does not exist for this user. If the queue type specified
        //already exists then no need to take any action
        EmailUpdatesQueue queue = new EmailUpdatesQueue();
        queue.setEnteredBy(teamMember.getUserId());
        queue.setModifiedBy(teamMember.getUserId());
        queue.setEnabled(true);
        queue.setType(teamMember.getEmailUpdatesSchedule());
        queue.insert(db);
      }
    }
  }

  public static void manageQueue(Connection db, TeamMember teamMember) throws SQLException {
    //Determine if the member is part of any other projects and has a matching email updates preference
    PreparedStatement pst = db.prepareStatement(
        "SELECT count(*) AS record_count " +
            "FROM project_team pt " +
            "WHERE pt.user_id = ? " +
            "AND pt.email_updates_schedule = ? ");
    int i = 0;
    pst.setInt(++i, teamMember.getUserId());
    pst.setInt(++i, teamMember.getEmailUpdatesSchedule());
    ResultSet rs = pst.executeQuery();
    int records = 0;
    if (rs.next()) {
      records = rs.getInt("record_count");
    }
    rs.close();
    pst.close();

    if (records == 0) {
      //Delete the queue since it is no longer needed.
      String field = "";
      int emailUpdatesSchedule = teamMember.getEmailUpdatesSchedule();
      if (emailUpdatesSchedule > 0) {
        if (emailUpdatesSchedule == TeamMember.EMAIL_OFTEN) {
          field = "schedule_often";
        } else if (emailUpdatesSchedule == TeamMember.EMAIL_DAILY) {
          field = "schedule_daily";
        } else if (emailUpdatesSchedule == TeamMember.EMAIL_WEEKLY) {
          field = "schedule_weekly";
        } else if (emailUpdatesSchedule == TeamMember.EMAIL_MONTHLY) {
          field = "schedule_monthly";
        }
        i = 0;
        pst = db.prepareStatement(
            "DELETE FROM email_updates_queue " +
                "WHERE enteredby = ? AND " + field + " = ? ");
        pst.setInt(++i, teamMember.getUserId());
        pst.setBoolean(++i, true);
        pst.executeUpdate();
        pst.close();
      }
    }
  }
}
