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

import com.concursive.commons.text.Template;
import com.concursive.commons.text.StringUtils;
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
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Utility methods used by the EmailUpdatesJob
 *
 * @author Ananth
 * @created Dec 9, 2009
 */
public class EmailUpdatesUtils {

  private static Log LOG = LogFactory.getLog(EmailUpdatesUtils.class);

  public static String getEmailHTMLMessage(Connection db, EmailUpdatesQueue queue, ApplicationPrefs prefs,
                                           Timestamp min, Timestamp max) throws Exception {
    // User who needs to be sent an email
    User user = UserUtils.loadUser(queue.getEnteredBy());

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
    if (URLFactory.createURL(prefs.getPrefs()) == null) {
      throw new Exception("The server URL is not specified. Please contact the system administrator to configure the remote server's URL!");
    }

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
      PagedListInfo info = new PagedListInfo();
      String limit = emailElement.getAttribute("limit");
      info.setItemsPerPage(limit);

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
      chatter.setPagedListInfo(info);
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
          WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(user.getId(), URLFactory.createURL(prefs.getPrefs()));
          String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, description);
          message.append("<li>" + wikiLinkString + "</li>");
        }
        message.append("</ol>");
      }
      message.append("</ol>");

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
        activities.setForMember(user.getId());
        activities.setEmailUpdatesSchedule(emailUpdatesSchedule);
        activities.setRangeStart(min);
        activities.setRangeEnd(max);
        activities.setObjectPreferences(types);
        activities.setPagedListInfo(info);
        HashMap activityMap = activities.getList(db);

        message.append("<h2>" + category.getDescription() + "</h2>");
        message.append("<ol>");

        Iterator k = activityMap.keySet().iterator();
        while (k.hasNext()) {
          String date = (String) k.next();
          message.append("<li>" + date + "</li>");
          message.append("<ol>");
          ArrayList descriptions = (ArrayList) activityMap.get(date);
          Iterator l = descriptions.iterator();
          while (l.hasNext()) {
            String description = (String) l.next();
            WikiToHTMLContext wikiToHTMLContext = new WikiToHTMLContext(user.getId(), URLFactory.createURL(prefs.getPrefs()));
            String wikiLinkString = WikiToHTMLUtils.getHTML(wikiToHTMLContext, db, description);
            message.append("<li>" + wikiLinkString + "</li>");
          }
          message.append("</ol>");
        }
        if (activityMap.size() == 0) {
          message.append("No activities under " + category.getDescription());
        }
        message.append("</ol>");
      }

      message.append("<p>");
      message.append("This information is sent based on your notification settings.<br/>");
      message.append("<a href=\"${secureUrl}/show/${this.project.uniqueId:html}\" target=\"_blank\">" +
              "Manage your notifications.</a>");
      message.append("</p>");

    }
    Template template = new Template(message.toString());
    template.addParseElement("${secureUrl}", URLFactory.createURL(prefs.getPrefs()));
    template.addParseElement("${this.project.uniqueId:html}", website.getUniqueId());

    return template.getParsedText();
  }

  public static void saveQueue(Connection db, TeamMember teamMember) throws SQLException {
    if (teamMember.getEmailUpdatesSchedule() != TeamMember.EMAIL_NEVER) {
      EmailUpdatesQueueList queues = new EmailUpdatesQueueList();
      queues.setEnteredBy(teamMember.getUserId());
      queues.setType(teamMember.getEmailUpdatesSchedule());
      queues.buildList(db);
      if (queues.size() == 0) {
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
}
