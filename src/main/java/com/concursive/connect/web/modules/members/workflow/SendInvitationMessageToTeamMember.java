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

package com.concursive.connect.web.modules.members.workflow;

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.security.Key;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads the details of the specified user.
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created December 15, 2008
 */
public class SendInvitationMessageToTeamMember extends ObjectHookComponent implements ComponentInterface {

  private static Log LOG = LogFactory.getLog(SendInvitationMessageToTeamMember.class);

  public final static String URL = "url";

  public String getDescription() {
    return "Determines if the user is registered and sends the appropriate invitation message";
  }

  public boolean execute(ComponentContext context) {
    TeamMember thisTeamMember = (TeamMember) context.getThisObject();
    User user = UserUtils.loadUser(thisTeamMember.getEnteredBy());
    User teamMemberUser = null;
    Project project = null;
    Connection db = null;
    try {
      db = getConnection(context);
      if (thisTeamMember.getUserId() > 0 && user.getId() > 0) {
        teamMemberUser = UserUtils.loadUser(thisTeamMember.getUserId());
        project = ProjectUtils.loadProject(thisTeamMember.getProjectId());

        String url = context.getParameter(URL);
        if (teamMemberUser != null) {
          Key key = (Key) context.getAttribute("TEAM.KEY");
          // Initialize the message template
          freemarker.template.Template inviteSubject = null;
          freemarker.template.Template inviteBody = null;
          // Set the data model
          Map subjectMappings = new HashMap();
          Map bodyMappings = new HashMap();
          bodyMappings.put("project", project);
          bodyMappings.put("user", user);
          bodyMappings.put("link", new HashMap());
          bodyMappings.put("invite", new HashMap());
          bodyMappings.put("optional", new HashMap());
          ((Map) bodyMappings.get("invite")).put("firstName", teamMemberUser.getFirstName());
          ((Map) bodyMappings.get("invite")).put("lastName", teamMemberUser.getLastName());
          ((Map) bodyMappings.get("invite")).put("name", teamMemberUser.getNameFirstLast());
          ((Map) bodyMappings.get("optional")).put("message", thisTeamMember.getCustomInvitationMessage() != null ? StringUtils.toHtmlValue(thisTeamMember.getCustomInvitationMessage(), false, true) : "");
          Configuration freeMarkerConfiguration = (Configuration) context.getAttribute(ComponentContext.FREEMARKER_CONFIGURATION);
          if (freeMarkerConfiguration == null) {
            LOG.error("freeMarkerConfiguration is null");
          }
          if (teamMemberUser.getRegistered()) {
            // User IS registered with site
            inviteSubject = freeMarkerConfiguration.getTemplate("project_invitation_email_subject-text.ftl");
            inviteBody = freeMarkerConfiguration.getTemplate("project_invitation_email_body-html.ftl");
            ((Map) bodyMappings.get("link")).put("info", url);
            ((Map) bodyMappings.get("link")).put("invitations", url + "/invites");
          } else {
            // User IS NOT registered
            inviteSubject = freeMarkerConfiguration.getTemplate("project_invitation_for_new_user_email_subject-text.ftl");
            inviteBody = freeMarkerConfiguration.getTemplate("project_invitation_for_new_user_email_body-html.ftl");
            String data = URLEncoder.encode(PrivateString.encrypt(key, "id=" + teamMemberUser.getId() + ",pid=" + project.getId()), "UTF-8");
            ((Map) bodyMappings.get("link")).put("accept", url + "/LoginAccept.do?data=" + data);
            ((Map) bodyMappings.get("link")).put("reject", url + "/LoginReject.do?data=" + data);
            ((Map) bodyMappings.get("link")).put("info", url);
          }

          // Send the message
          Map<String, String> prefs = context.getApplicationPrefs();
          SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(prefs);
          message.setFrom(prefs.get("EMAILADDRESS"));
          message.addReplyTo(user.getEmail(), user.getNameFirstLast());
          message.addTo(teamMemberUser.getEmail());
          // Set the subject from the template
          StringWriter inviteSubjectTextWriter = new StringWriter();
          inviteSubject.process(subjectMappings, inviteSubjectTextWriter);
          message.setSubject(inviteSubjectTextWriter.toString());
          // Set the body from the template
          StringWriter inviteBodyTextWriter = new StringWriter();
          inviteBody.process(bodyMappings, inviteBodyTextWriter);
          message.setBody(inviteBodyTextWriter.toString());
          //Send the invitations
          message.setType("text/html");
          int result = message.send();
          if (result == 0) {
            //Record that message was delivered
            LOG.debug("email sent successfully to " + teamMemberUser.getNameFirstLast());
            thisTeamMember.setStatus(TeamMember.STATUS_PENDING);
          } else {
            LOG.debug("email not sent to " + teamMemberUser.getNameFirstLast());
            //Record that message was not delivered
            thisTeamMember.setStatus(TeamMember.STATUS_MAILERROR);
          }
          LOG.debug("updating team member status ");
          thisTeamMember.updateStatus(db);
        } else {
          LOG.error("could not find user profile for or team member " + thisTeamMember.getId());
        }
      } else {
        LOG.error("user or team member is null: teammember user Id " + thisTeamMember.getId() + " userId: " + user.getId());
      }
    } catch (Exception e) {
      LOG.error("Exception when sending invitation message to " + teamMemberUser.getNameFirstLast() + " for " + project.getTitle());
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return false;
  }
}
