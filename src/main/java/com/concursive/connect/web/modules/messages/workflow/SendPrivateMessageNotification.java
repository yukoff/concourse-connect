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

package com.concursive.connect.web.modules.messages.workflow;

import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.messages.dao.PrivateMessage;
import com.concursive.connect.web.modules.profile.dao.Project;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringWriter;
import java.security.Key;
import java.util.*;

/**
 * Description of the Class
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created December 29, 2008
 */
public class SendPrivateMessageNotification extends ObjectHookComponent implements ComponentInterface {

  private static Log LOG = LogFactory.getLog(SendPrivateMessageNotification.class);

  public final static String HOST = "notification.host";
  public final static String PROJECT = "project";

  public final static String URL = "url";

  // Comma-separated user ids
  public final static String USERS_TO_IDS = "userIds";


  /**
   * Gets the description attribute of the SendUserNotification object
   *
   * @return The description value
   */
  public String getDescription() {
    return "Send an private message notification to a team member of the project";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean execute(ComponentContext context) {
    boolean result = false;
    try {
      ArrayList<Integer> users = new ArrayList<Integer>();

      // Add project leads
      Project thisProject = (Project) context.getAttribute(PROJECT);
      PrivateMessage thisPrivateMessage = (PrivateMessage) context.getThisObject();
      User senderUser = UserUtils.loadUser(thisPrivateMessage.getEnteredBy());
      String url = context.getParameter(URL);
      Key key = (Key) context.getAttribute("TEAM.KEY");
      Configuration freeMarkerConfiguration = (Configuration) context.getAttribute(ComponentContext.FREEMARKER_CONFIGURATION);

      // Go through userIds set as attributes
      String includeList = (String) context.getAttribute(USERS_TO_IDS);
      if (includeList != null) {
        StringTokenizer st = new StringTokenizer(includeList, ",");
        while (st.hasMoreTokens()) {
          Integer id = Integer.parseInt(st.nextToken().trim());
          if (!users.contains(id)) {
            users.add(id);
          }
        }
      }

      // Send the message(s)
      if (users.size() > 0) {

        SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(context.getApplicationPrefs());
      message.setFrom(context.getParameter(ComponentContext.APPLICATION_EMAIL_ADDRESS));
        message.setType("text/html");

        // Send to each user
        Iterator userList = users.iterator();
        while (userList.hasNext()) {
          Integer id = (Integer) userList.next();

          User teamMemberUser = UserUtils.loadUser(id.intValue());
          String email = teamMemberUser.getEmail();

          // Initialize the message template
          Template inviteSubject = null;
          Template inviteBody = null;

          // Set the data model
          Map subjectMappings = new HashMap();
          subjectMappings.put("user", senderUser);

          Map bodyMappings = new HashMap();
          bodyMappings.put("site", new HashMap());
        ((Map) bodyMappings.get("site")).put("title", context.getApplicationPrefs().get(ApplicationPrefs.WEB_PAGE_TITLE));
          bodyMappings.put("project", thisProject);
          bodyMappings.put("user", senderUser);
          bodyMappings.put("teamMember", teamMemberUser);

          bodyMappings.put("private", new HashMap());
          ((Map) bodyMappings.get("private")).put("message", StringUtils.toHtmlValue(thisPrivateMessage.getBody()));

          bodyMappings.put("link", new HashMap());
          ((Map) bodyMappings.get("link")).put("info", url);
          ((Map) bodyMappings.get("link")).put("projectMessages", url + "/show/" + thisProject.getUniqueId() + "/message/inbox/" + thisPrivateMessage.getId());

          inviteSubject = freeMarkerConfiguration.getTemplate("project_private_message_subject-text.ftl");
          inviteBody = freeMarkerConfiguration.getTemplate("project_private_message_body-html.ftl");

          // Set the subject from the template
          StringWriter inviteSubjectTextWriter = new StringWriter();
          inviteSubject.process(subjectMappings, inviteSubjectTextWriter);
          message.setSubject(inviteSubjectTextWriter.toString());
          // Set the body from the template
          StringWriter inviteBodyTextWriter = new StringWriter();
          inviteBody.process(bodyMappings, inviteBodyTextWriter);
          message.setBody(inviteBodyTextWriter.toString());
          message.setTo(email);
          message.setType("text/html");

          int emailResult = message.send();
          if (emailResult == 0) {
            LOG.debug("email sent successfully to " + teamMemberUser.getNameFirstLast());
          } else {
            LOG.debug("email not sent to " + teamMemberUser.getNameFirstLast());
          }
        }
      }
      result = true;
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
    return result;
  }
}

