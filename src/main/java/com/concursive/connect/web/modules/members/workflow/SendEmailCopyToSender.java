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

import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.beans.TeamMemberEmailBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Send email copy to sender 
 *
 * @author Kailash Bhoopalam
 * @version $Id: SendEmailCopyToSender.java $
 * @created December 28, 2009
 */
public class SendEmailCopyToSender extends ObjectHookComponent implements ComponentInterface {

  private static Log LOG = LogFactory.getLog(SendEmailCopyToSender.class);

  public final static String URL = "url";

  public String getDescription() {
    return "Sends a copy of the email to the sender";
  }

  public boolean execute(ComponentContext context) {
  	
    TeamMemberEmailBean thisEmailBean = (TeamMemberEmailBean) context.getThisObject();
    User user = UserUtils.loadUser(thisEmailBean.getEnteredBy());
    Project project = ProjectUtils.loadProject(thisEmailBean.getProjectId());
    Connection db = null;
    String url = context.getParameter(URL);
    
    Map<String, String> prefs = context.getApplicationPrefs();
    try {
      db = getConnection(context);
      // Set the data model
      Map subjectMappings = new HashMap();
      subjectMappings.put("user",  new HashMap());
      ((Map) subjectMappings.get("user")).put("nameFirstLast", user.getNameFirstLast());
      subjectMappings.put("project",  new HashMap());
      ((Map) subjectMappings.get("project")).put("title", project.getTitle());

      Map bodyMappings = new HashMap();
      bodyMappings.put("site", new HashMap());
      ((Map) bodyMappings.get("site")).put("title", prefs.get("TITLE"));
      
      bodyMappings.put("user",  new HashMap());
      ((Map) bodyMappings.get("user")).put("nameFirstLast", user.getNameFirstLast());
      ((Map) bodyMappings.get("user")).put("profileUrl", url + "/show/" + user.getProfileProject().getUniqueId());
      
      bodyMappings.put("project",  new HashMap());
      ((Map) bodyMappings.get("project")).put("title", project.getTitle());
      ((Map) bodyMappings.get("project")).put("profileUrl", url + "/show/" + project.getUniqueId());

      bodyMappings.put("emailMessage", thisEmailBean.getBody());

      Configuration freeMarkerConfiguration = (Configuration) context.getAttribute(ComponentContext.FREEMARKER_CONFIGURATION);
      if (freeMarkerConfiguration == null) {
        LOG.error("freeMarkerConfiguration is null");
      }

      // Initialize the message template
      freemarker.template.Template emailSubject = null;
      freemarker.template.Template emailBody = null;
      emailSubject = freeMarkerConfiguration.getTemplate("project_members_email_subject_copy-text.ftl");
      emailBody = freeMarkerConfiguration.getTemplate("project_members_email_body_copy-html.ftl");

      // Send the message
      SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(prefs);
      message.setFrom(prefs.get("EMAILADDRESS"));
      message.addTo(user.getEmail());
      // Set the subject from the template
      StringWriter inviteSubjectTextWriter = new StringWriter();
      emailSubject.process(subjectMappings, inviteSubjectTextWriter);
      message.setSubject(inviteSubjectTextWriter.toString());
      // Set the body from the template
      StringWriter inviteBodyTextWriter = new StringWriter();
      emailBody.process(bodyMappings, inviteBodyTextWriter);
      message.setBody(inviteBodyTextWriter.toString());
      
      //Send the email
      message.setType("text/html");
      int result = message.send();
      if (result == 0) {
        LOG.debug("email sent successfully to " + user.getNameFirstLast());
      } else {
        LOG.debug("email not sent to " + user.getNameFirstLast());
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return true;
  }
}
