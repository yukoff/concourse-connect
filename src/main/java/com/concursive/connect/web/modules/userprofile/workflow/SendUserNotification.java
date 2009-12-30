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

package com.concursive.connect.web.modules.userprofile.workflow;

import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.issues.dao.TicketContact;
import com.concursive.connect.web.modules.issues.dao.TicketContactList;
import com.concursive.connect.web.modules.issues.workflow.LoadTicketDetails;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.util.*;
import java.io.StringWriter;

import freemarker.template.Template;
import freemarker.template.Configuration;

import javax.servlet.ServletContext;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @created January 14, 2003
 */
public class SendUserNotification extends ObjectHookComponent implements ComponentInterface {

  private static Log LOG = LogFactory.getLog(SendUserNotification.class);

  public final static String HOST = "notification.host";
  public final static String USER_TO_NOTIFY = "notification.userToNotify";
  public final static String NOTIFY_PROJECT_LEADS = "notification.addProjectLeads";
  public final static String NOTIFY_TICKET_CONTACT_LIST = "notification.addTicketContactList";
  public final static String NOTIFY_FORUM_CONTACTS = "notification.addForumContacts";
  public final static String NOTIFY_ADMINS = "notification.addAdmins";
  public final static String SUBJECT = "notification.subject";
  public final static String FROM = "notification.from";
  public final static String USERS_FROM = "notification.users.from";
  public final static String REPLY_TO = "notification.replyTo";
  public final static String BODY = "notification.body";
  // Comma-separated email addresses
  public final static String EMAIL_TO = "notification.to";
  // Comma-separated user ids
  public final static String NOTIFICATION_USERS_TO = "notification.users.to";
  // Comma-separated user ids
  public final static String EXCLUDE_LIST = "notification.excludeUsers";
  // Comma-separated user ids
  public final static String USERS_TO_IDS = "userIds";
  // Message attributes
  public final static String ALERT_FONT_COLOR = "notification.alert.font.color";


  /**
   * Gets the description attribute of the SendUserNotification object
   *
   * @return The description value
   */
  public String getDescription() {
    return "Send an email notification to a user";
  }


  /**
   * This component sends email based on various notification parameters
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean execute(ComponentContext context) {
    boolean result = false;
    Connection db = null;
    try {
      db = getConnection(context);
      ArrayList<Integer> users = new ArrayList<Integer>();
      ArrayList<String> emails = new ArrayList<String>();

      // Add admins
      boolean addAdmins = "true".equals(context.getParameter(NOTIFY_ADMINS));
      if (addAdmins) {
        UserList userList = new UserList();
        userList.setAdmin(Constants.TRUE);
        userList.setValidUser(Constants.TRUE);
        userList.buildList(db);
        for (User user : userList) {
          if (StringUtils.hasText(user.getEmail())) {
            if (user.getAccessAdmin()) {
              users.add(user.getId());
            } else {
              LOG.error("A non-admin user was almost added to SendUserNotification");
            }
          }
        }
        if (LOG.isDebugEnabled() && userList.size() == 0) {
          LOG.warn("No admins found");
        }
      }

      // Add project leads
      Project thisProject = (Project) context.getAttribute("project");
      boolean addProjectLeads = "true".equals(context.getParameter(NOTIFY_PROJECT_LEADS));
      if (addProjectLeads && thisProject != null) {
        TeamMemberList members = new TeamMemberList();
        members.setProjectId(thisProject.getId());
        members.setMinimumRoleLevel(TeamMember.MANAGER);
        members.buildList(db);
        for (TeamMember member : members) {
          if (!users.contains(member.getUserId())) {
            users.add(member.getUserId());
          }
        }
      }

      // Add forum contacts
      boolean addForumContacts = "true".equals(context.getParameter(NOTIFY_FORUM_CONTACTS));
      if (addForumContacts && thisProject != null) {
        // Go through the users of a project and see if they have access to discussions
        TeamMemberList teamMembers = new TeamMemberList();
        teamMembers.setProjectId(thisProject.getId());
        teamMembers.buildList(db);
        for (TeamMember member : teamMembers) {
          User thisUser = UserUtils.loadUser(member.getUserId());
          if (thisUser.getWatchForums() &&
              ProjectUtils.hasAccess(thisProject.getId(), thisUser, "project-discussion-topics-view")) {
            users.add(thisUser.getId());
          }
        }
      }

      // Add ticket contact list
      TicketContactList ticketContactList = (TicketContactList) context.getAttribute(LoadTicketDetails.CONTACT_LIST);
      boolean addTicketContactList = "true".equals(context.getParameter(NOTIFY_TICKET_CONTACT_LIST));
      if (addTicketContactList && ticketContactList != null) {
        Iterator i = ticketContactList.iterator();
        while (i.hasNext()) {
          TicketContact contact = (TicketContact) i.next();
          contact.buildEmailAddress(db);
          if (contact.getUserId() > -1) {
            if (!users.contains(contact.getUserId())) {
              users.add(contact.getUserId());
            }
          } else if (StringUtils.hasText(contact.getContactEmail())) {
            emails.add(contact.getContactEmail());
          }
        }
      }

      // Add user to notify
      int userToNotify = context.getParameterAsInt(USER_TO_NOTIFY);
      if (userToNotify > -1) {
        if (!users.contains(userToNotify)) {
          users.add(userToNotify);
        }
      }

      // Go through users to
      String includeList = context.getParameter(NOTIFICATION_USERS_TO);
      if (includeList != null) {
        StringTokenizer st = new StringTokenizer(includeList, ",");
        while (st.hasMoreTokens()) {
          Integer id = Integer.parseInt(st.nextToken().trim());
          if (!users.contains(id)) {
            users.add(id);
          }
        }
      }

      // Go through userIds set as attributes
      includeList = (String) context.getAttribute(USERS_TO_IDS);
      if (includeList != null) {
        StringTokenizer st = new StringTokenizer(includeList, ",");
        while (st.hasMoreTokens()) {
          Integer id = Integer.parseInt(st.nextToken().trim());
          if (!users.contains(id)) {
            users.add(id);
          }
        }
      }
      // Go through email to
      String emailValues = context.getParameter(EMAIL_TO);
      if (emailValues != null) {
        StringTokenizer st = new StringTokenizer(emailValues, ",");
        while (st.hasMoreTokens()) {
          String emailAddress = st.nextToken().trim();
          if (!emails.contains(emailAddress)) {
            emails.add(emailAddress);
          }
        }
      }
      // Go through exclude list
      String excludeList = context.getParameter(EXCLUDE_LIST);
      if (excludeList != null) {
        StringTokenizer st = new StringTokenizer(excludeList, ",");
        while (st.hasMoreTokens()) {
          String id = st.nextToken().trim();
          users.remove(new Integer(Integer.parseInt(id)));
        }
      }
      // Send the message(s)
      if (users.size() > 0 || emails.size() > 0) {
        LOG.debug("Constructing mail object");
        SMTPMessage mail = SMTPMessageFactory.createSMTPMessageInstance(context.getApplicationPrefs());
        String from = StringUtils.toHtmlValue(context.getParameter(FROM));
        String fromId = StringUtils.toHtmlValue(context.getParameter(USERS_FROM));
        if (from != null && !"".equals(from)) {
          mail.setFrom(from);
        } else if (fromId != null && !"".equals(fromId)) {
          mail.setFrom(User.getEmailAddressById(db, Integer.parseInt(fromId)));
        } else {
          mail.setFrom(context.getParameter("EMAILADDRESS"));
        }
        mail.setType("text/html");
        mail.setSubject(context.getParameter(SUBJECT));
        /*
        //TODO: Populate the message using a freemarker template
        Configuration configuration = (Configuration) context.getAttribute(ComponentContext.FREEMARKER_CONFIGURATION);
        Template template = configuration.getTemplate("send_user_notification_email-html.ftl");
        Map bodyMappings = new HashMap();
        bodyMappings.put("body", context.getParameter(BODY));
        // Parse and send
        StringWriter inviteBodyTextWriter = new StringWriter();
        template.process(bodyMappings, inviteBodyTextWriter);
        mail.setBody(inviteBodyTextWriter.toString());
        */
        mail.setBody(context.getParameter(BODY));
        // Send to each user
        Iterator userList = users.iterator();
        while (userList.hasNext()) {
          Integer id = (Integer) userList.next();
          String email = User.getEmailAddressById(db, id);
          if (email != null) {
            LOG.debug("Sending to user: " + email);
            mail.setTo(email);
            int status = mail.send();
            LOG.debug("Send status: " + status);
          }
        }

        // Send to each contact
        Iterator emailList = emails.iterator();
        while (emailList.hasNext()) {
          String email = (String) emailList.next();
          if (email != null) {
            LOG.debug("Sending to contact: " + email);
            mail.setTo(email);
            int status = mail.send();
            LOG.debug("Send status: " + status);
          }
        }
      } else {
        LOG.warn("No users or emails to send notification to");
      }
      result = true;
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return result;
  }
}

