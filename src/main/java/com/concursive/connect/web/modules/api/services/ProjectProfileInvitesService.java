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
package com.concursive.connect.web.modules.api.services;

import com.concursive.commons.api.Record;
import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.Template;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.api.beans.TransactionItem;
import com.concursive.connect.web.modules.communications.dao.ProjectMessage;
import com.concursive.connect.web.modules.communications.dao.ProjectMessageRecipient;
import com.concursive.connect.web.modules.contacts.dao.Contact;
import com.concursive.connect.web.modules.contacts.dao.ContactList;
import com.concursive.connect.web.modules.contacts.utils.ContactUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URLEncoder;
import java.security.Key;
import java.sql.Connection;
import java.util.Map;
import java.util.HashMap;
import java.io.StringWriter;

import freemarker.template.Configuration;

/**
 * Handler invoked on a projectMessage bean using the API...
 * <p/>
 * This service is responsible for processing incoming project message and recipient emails and
 * sending out project profile invite emails to contacts matching the email IDs. If
 * an email ID does not have a corresponding contact record, a new contact is created
 * and an email is sent.
 * <p/>
 * <projectMessage action="projectInvites"/>
 *
 * @author Ananth
 * @created Jul 24, 2008
 */
public class ProjectProfileInvitesService implements CustomActionHandler {

  private static Log LOG = LogFactory.getLog(ProjectProfileInvitesService.class);

  public boolean process(TransactionItem transactionItem, Connection db) throws Exception {
    try {
      // Request parameters and objects
      Object object = transactionItem.getObject();
      ApplicationPrefs prefs = transactionItem.getPacketContext().getApplicationPrefs();
      ActionContext actionContext = transactionItem.getPacketContext().getActionContext();
      Key key = (Key) actionContext.getServletContext().getAttribute(ApplicationPrefs.TEAM_KEY);

      // Prepare the response, a list of contactEmailAddress with a status will be returned
      transactionItem.getRecordList().setName("contactEmailAddress");

      // Process the incoming projectMessage object
      if (object != null && object instanceof ProjectMessage) {
        // Extract the ProjectMessage Bean and save it..
        ProjectMessage projectMessage = (ProjectMessage) transactionItem.getObject();

        // Retrieve the specified profile
        Project project = ProjectUtils.loadProject(projectMessage.getProfileUniqueId());
        projectMessage.setProjectId(project.getId());
        projectMessage.setEnteredBy(project.getOwner());
        projectMessage.insert(db);

        // Insert ProjectMessageRecipient records
        ContactList contacts = projectMessage.getContacts();
        for (Contact contact : contacts) {
          int contactId = ContactList.getIdByEmailAddress(db, contact.getEmail1());
          if (contactId == -1) {
            // contact not found. save the contact record as owned by the project's owner
            contact.setOwner(project.getOwner());
            contact.setEnteredBy(project.getOwner());
            contact.setModifiedBy(project.getOwner());
            contact.insert(db);
          } else {
            contact = new Contact(db, contactId);
            if (contact.getOwner() != project.getOwner()) {
              // The contact is not owned by the project owner. Add the contact to a contact share pool
              contact.addToShare(db, contact.getOwner(), project.getOwner(), true);
            }
          }
          ProjectMessageRecipient recipient = new ProjectMessageRecipient();
          recipient.setMessageId(projectMessage.getId());
          recipient.setContactId(contact.getId());
          recipient.setStatusId(ProjectMessageRecipient.STATUS_INVITING);
          recipient.setStatus(ProjectMessageRecipient.INVITING);
          recipient.setEnteredBy(project.getOwner());
          recipient.insert(db);

          String data = URLEncoder.encode(PrivateString.encrypt(key, "id=" + recipient.getId() + ",pid=" + project.getId()), "UTF-8");

          /*
          ${invite.name}
          ${project.name}
          ${project.description}
          ${project.ownerName}
          ${project.profileLink}
          ${project.unsubscribeLink}
          ${project.customText}
          ${link.info}
           */
          Template inviteBody = new Template(projectMessage.getBody());
          inviteBody.addParseElement("${invite.name}", ContactUtils.getNameFirstLast(contact.getFirstName(), contact.getLastName()));
          inviteBody.addParseElement("${project.name}", project.getTitle());
          inviteBody.addParseElement("${project.description}", project.getShortDescription());
          inviteBody.addParseElement("${project.ownerName}", ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, project.getOwner())).getNameFirstLastInitial());
          inviteBody.addParseElement("${project.profileLink}",
              RequestUtils.getServerUrl(actionContext.getRequest()) + "/show/" + project.getUniqueId());
          //TODO: Define the following link..
          //inviteBody.addParseElement("${project.unsubscribeLink}", YYY);
          inviteBody.addParseElement("\r\n", "");
          inviteBody.addParseElement("\r", "");
          inviteBody.addParseElement("\n", "");
          //inviteBody.addParseElement("${link.info}", RequestUtils.getServerUrl(applicationPrefs, actionContext.getRequest()));
          //inviteBody.addParseElement("${link.accept}", RequestUtils.getServerUrl(applicationPrefs, actionContext.getRequest()) + "/LoginAccept.do?data=" + data);
          //inviteBody.addParseElement("${link.reject}", RequestUtils.getServerUrl(applicationPrefs, actionContext.getRequest()) + "/LoginReject.do?data=" + data);

          //Prepare the invitation
          SMTPMessage message = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
          message.setFrom(prefs.get(ApplicationPrefs.EMAILADDRESS));
          //message.addReplyTo(applicationPrefs.get()contact.getEmail(), getUser(context).getNameFirstLast());
          message.setTo(contact.getEmail1());
          message.setSubject(projectMessage.getSubject());
          // Populate the message template
          Configuration configuration = ApplicationPrefs.getFreemarkerConfiguration(actionContext.getServletContext());
          freemarker.template.Template template = configuration.getTemplate("project_profile_invites_email-html.ftl");
          Map bodyMappings = new HashMap();
          bodyMappings.put("body", inviteBody.getParsedText());
          // Parse and send
          StringWriter inviteBodyTextWriter = new StringWriter();
          template.process(bodyMappings, inviteBodyTextWriter);
          message.setBody(inviteBodyTextWriter.toString());
          message.setType("text/html");
          //Send the invitations
          int result = message.send();
          if (result == 0) {
            //Record that message was delivered
            recipient.setStatusId(ProjectMessageRecipient.STATUS_PENDING);
            recipient.setStatus(ProjectMessageRecipient.PENDING);
            //Flag that the recipient received the message
            Record record = new Record("processed");
            record.put("email", contact.getEmail1());
            record.put("type", 1);
            transactionItem.getRecordList().add(record);
          } else {
            // Record that message was not delivered
            recipient.setStatusId(ProjectMessageRecipient.STATUS_MAILERROR);
            recipient.setStatus(ProjectMessageRecipient.MAILERROR);
            // Flag that the recipient DID NOT receive the message
            Record record = new Record("processed");
            record.put("email", contact.getEmail1());
            record.put("type", -1);
            transactionItem.getRecordList().add(record);
            LOG.warn("MAIL ERROR: " + message.getErrorMsg());
          }
          recipient.update(db);
        }
        return true;
      }
    } catch (Exception e) {
      LOG.error("process error", e);
      throw new Exception(e.getMessage());
    }
    return false;
  }

  /*
  private String getPath(ActionContext context, ApplicationPrefs applicationPrefs, String folder) {
  String fs = System.getProperty("file.separator");
  String fileLibrary = applicationPrefs.get("FILELIBRARY");
  User user = (User) context.getSession().getAttribute(Constants.SESSION_USER);

  if (user != null) {
  return fileLibrary + user.getGroupId() + fs + folder + fs;
  }
  return fileLibrary + "1" + fs + folder + fs;
  } */
}
