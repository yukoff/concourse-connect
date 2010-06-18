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
package com.concursive.connect.web.modules.calendar.workflow;

import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.i18n.LocalizationUtils;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendee;
import com.concursive.connect.web.modules.calendar.utils.DimDimUtils;
import com.concursive.connect.web.modules.calendar.utils.MeetingInviteesBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Sends mails for meeting to attendees
 *
 * @author Nanda Kumar
 * @created June 11, 2009
 */
public class SendEventMeetingInvitation extends ObjectHookComponent implements ComponentInterface {

  private static Log LOG = LogFactory.getLog(SendEventMeetingInvitation.class);
  private Configuration freeMarkerConfiguration = null;
  private MeetingInviteesBean meetingInviteesBean = null;
  private freemarker.template.Template templateSubject = null;
  private freemarker.template.Template templateBody = null;
  private User hostUser = null;
  private SMTPMessage message = null;
  private Map<String, String> subjectMap = null;
  private Map<String, Object> bodyMap = null;

  public boolean execute(ComponentContext context) {
    try {
      //set freemarker configuration
      freeMarkerConfiguration = (Configuration) context.getAttribute(ComponentContext.FREEMARKER_CONFIGURATION);
      if (freeMarkerConfiguration == null) {
        LOG.error("freeMarkerConfiguration is null");
        return false;
      }

      //get the invitees object
      meetingInviteesBean = (MeetingInviteesBean) context.getThisObject();
      if (meetingInviteesBean == null) {
        LOG.error("Cannot find meeting details.");
        return false;
      }

      //check mail is to be send
      if (meetingInviteesBean.getAction() != DimDimUtils.ACTION_MEETING_STATUS_CHANGE &&
          meetingInviteesBean.getAction() != DimDimUtils.ACTION_MEETING_USER_JOIN &&
          meetingInviteesBean.getAction() != DimDimUtils.ACTION_MEETING_APPROVE_JOIN &&
          meetingInviteesBean.getMeetingChangeUsers().isEmpty() &&
          meetingInviteesBean.getMembersFoundList().isEmpty() &&
          meetingInviteesBean.getRejectedUsers().isEmpty() &&
          meetingInviteesBean.getCancelledUsers().isEmpty()) {
        return false;
      }

      //get meeting host
      hostUser = UserUtils.loadUser(meetingInviteesBean.getMeeting().getOwner());
      if (hostUser == null) {
        LOG.error("Cannot find meeting host details.");
        return false;
      }

      //mail settings
      Map<String, String> prefs = context.getApplicationPrefs();
      message = SMTPMessageFactory.createSMTPMessageInstance(prefs);
      message.setFrom(prefs.get("EMAILADDRESS"));
      message.setType("text/html");

      //message map parameters
      subjectMap = new HashMap<String, String>();
      bodyMap = new HashMap<String, Object>();

      //set mail parameter maps
      bodyMap.put("meeting", meetingInviteesBean.getMeeting());
      bodyMap.put("host", hostUser);

      String url = context.getParameter("url");
      bodyMap.put("url", url);

      // This uses the host's startDate, but a user can override this when sent
      bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), hostUser));

      Project project = meetingInviteesBean.getProject();
      bodyMap.put("eventUrl", url + "/show/" + project.getUniqueId() + "/calendar/" + generateCalendarURL(meetingInviteesBean.getMeeting().getStartDate(), LocalizationUtils.getLocale(prefs.get(ApplicationPrefs.LANGUAGE))));

      switch (meetingInviteesBean.getAction()) {
        case DimDimUtils.ACTION_MEETING_DIMDIM_EDIT:
          if (!meetingInviteesBean.getMeetingChangeUsers().isEmpty() && meetingInviteesBean.getIsModifiedMeeting()) {
            sendMeetingChangeMail();
          }
          if (!meetingInviteesBean.getMembersFoundList().isEmpty()) {
            sendMeetingInvitationMail();
          }
          if (!meetingInviteesBean.getRejectedUsers().isEmpty()) {
            sendMeetingInvitationRejectMail();
          }
          if (!meetingInviteesBean.getCancelledUsers().isEmpty()) {
            sendMeetingCancellationMail();
          }
          break;

        case DimDimUtils.ACTION_MEETING_DIMDIM_SCHEDULE:
          sendMeetingInvitationMail();
          break;

        case DimDimUtils.ACTION_MEETING_STATUS_CHANGE:
          sendInvitationStatusMail();
          break;

        case DimDimUtils.ACTION_MEETING_DIMDIM_CANCEL:
          sendMeetingCancellationMail();
          break;

        case DimDimUtils.ACTION_MEETING_USER_JOIN:
          sendUserJoinedMail();
          break;

        case DimDimUtils.ACTION_MEETING_APPROVE_JOIN:
          sendUserJoinStatusMail();
          break;

        default:
          LOG.error("Meeting action not known - " + meetingInviteesBean.getAction());
          return false;
      }
      return true;
    } catch (Exception e) {
      LOG.error("Exception when trying to send mail");
      e.printStackTrace(System.out);
      return false;
    }
  }

  /*
   * Sends the meeting invitation rejected mail to attendees
   */
  private boolean sendMeetingInvitationRejectMail() throws Exception {
    LOG.debug("Trying to send invitation rejected mail");

    //set mail templates
    templateSubject = freeMarkerConfiguration.getTemplate("event_meeting_invitation_reject_email_subject-text.ftl");
    templateBody = freeMarkerConfiguration.getTemplate("event_meeting_invitation_reject_email_body-html.ftl");

    //set replyto mailid
    message.setReplyTo(hostUser.getEmail(), hostUser.getNameFirstLast());

    //send mails to all meeting invitees
    for (User thisInvitee : meetingInviteesBean.getRejectedUsers()) {
      //set additional mail parameter maps
      bodyMap.put("invitee", thisInvitee);

      // override the date formatting
      if (thisInvitee.getLocale() != null) {
        bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), thisInvitee));
      }

      //set to mailid
      message.setTo(thisInvitee.getEmail());

      //build the templates
      StringWriter subjectTextWriter = new StringWriter();
      templateSubject.process(subjectMap, subjectTextWriter);
      StringWriter bodyTextWriter = new StringWriter();
      templateBody.process(bodyMap, bodyTextWriter);
      message.setSubject(subjectTextWriter.toString());
      message.setBody(bodyTextWriter.toString());
      LOG.debug(bodyTextWriter.toString());

      //send mail
      if (message.send() == 0) {
        LOG.debug("invitation rejected email sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
      } else {
        LOG.debug("invitation rejected email not sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
      }
    }
    return true;
  }

  /*
   * Sends the meeting cancelled mail to attendees
   */
  private boolean sendMeetingCancellationMail() throws Exception {
    LOG.debug("Trying to send meeting cancellation mail");

    //set mail templates
    templateSubject = freeMarkerConfiguration.getTemplate("event_meeting_cancellation_email_subject-text.ftl");
    templateBody = freeMarkerConfiguration.getTemplate("event_meeting_cancellation_email_body-html.ftl");

    //set replyto mailid
    message.setReplyTo(hostUser.getEmail(), hostUser.getNameFirstLast());

    //send mails to all meeting invitees
    for (User thisInvitee : meetingInviteesBean.getCancelledUsers()) {
      //set additional mail parameter maps
      bodyMap.put("invitee", thisInvitee);

      // override the date formatting
      if (thisInvitee.getLocale() != null) {
        bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), thisInvitee));
      }

      //set to mailid
      message.setTo(thisInvitee.getEmail());

      //build the templates
      StringWriter subjectTextWriter = new StringWriter();
      templateSubject.process(subjectMap, subjectTextWriter);
      StringWriter bodyTextWriter = new StringWriter();
      templateBody.process(bodyMap, bodyTextWriter);
      message.setSubject(subjectTextWriter.toString());
      message.setBody(bodyTextWriter.toString());
      LOG.debug(bodyTextWriter.toString());

      //send mail
      if (message.send() == 0) {
        LOG.debug("meeting cancelled email sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
      } else {
        LOG.debug("meeting cancelled email not sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
      }
    }
    return true;
  }

  /*
   * Sends the meeting invitation status change mail of attendees to meeting host
   */
  private boolean sendInvitationStatusMail() throws Exception {
    LOG.debug("Trying to send meeting invitation status change mail");

    //set mail templates
    templateSubject = freeMarkerConfiguration.getTemplate("event_meeting_attendeestatus_email_subject-text.ftl");
    templateBody = freeMarkerConfiguration.getTemplate("event_meeting_attendeestatus_email_body-html.ftl");

    //set additional mail parameter maps
    User inviteeUser = UserUtils.loadUser(meetingInviteesBean.getMeetingAttendee().getUserId());
    bodyMap.put("invitee", inviteeUser);

    // override the date formatting
    if (inviteeUser.getLocale() != null) {
      bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), inviteeUser));
    }

    bodyMap.put("status", "is tentative about");
    if (meetingInviteesBean.getMeetingAttendee().getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_ACCEPTED) {
      bodyMap.put("status", "has accepted");
    }
    if (meetingInviteesBean.getMeetingAttendee().getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_DECLINED) {
      bodyMap.put("status", "has declined");
    }

    //build the templates
    StringWriter subjectTextWriter = new StringWriter();
    templateSubject.process(subjectMap, subjectTextWriter);
    StringWriter bodyTextWriter = new StringWriter();
    templateBody.process(bodyMap, bodyTextWriter);
    message.setSubject(subjectTextWriter.toString());
    message.setBody(bodyTextWriter.toString());
    LOG.debug(bodyTextWriter.toString());

    //set replyto and to mailids
    message.setReplyTo(inviteeUser.getEmail(), inviteeUser.getNameFirstLast());
    message.setTo(hostUser.getEmail());

    //send mail
    if (message.send() == 0) {
      LOG.debug("invitiation status change email sent to " + hostUser.getNameFirstLast() + " - " + hostUser.getEmail());
      return true;
    }

    LOG.debug("invitiation status change email not sent to " + hostUser.getNameFirstLast() + " - " + hostUser.getEmail());
    return false;
  }

  /*
   * Sends meeting change mail to attendees
   */
  private boolean sendMeetingChangeMail() throws Exception {
    LOG.debug("Trying to send meeting change mail");

    //send edit mail only if the meeting record value was changed.
    if (meetingInviteesBean.getIsModifiedMeeting()) {
      //set mail templates
      templateSubject = freeMarkerConfiguration.getTemplate("event_meeting_change_email_subject-text.ftl");
      templateBody = freeMarkerConfiguration.getTemplate("event_meeting_change_email_body-html.ftl");

      //set additional mail parameter maps
//		  	bodyMap.put("previousStartDate", longDateToString(meetingInviteesBean.getPreviousMeetingStartDate()));
//		  	bodyMap.put("previousTitle", meetingInviteesBean.getPreviousMeetingTitle());

      for (User thisInvitee : meetingInviteesBean.getMeetingChangeUsers()) {
        //set additional mail parameter maps
        bodyMap.put("invitee", thisInvitee);

        // override the date formatting
        if (thisInvitee.getLocale() != null) {
          bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), thisInvitee));
        }

        //set to mailid
        message.setTo(thisInvitee.getEmail());

        //build the templates
        StringWriter subjectTextWriter = new StringWriter();
        templateSubject.process(subjectMap, subjectTextWriter);
        StringWriter bodyTextWriter = new StringWriter();
        templateBody.process(bodyMap, bodyTextWriter);
        message.setSubject(subjectTextWriter.toString());
        message.setBody(bodyTextWriter.toString());
        LOG.debug(bodyTextWriter.toString());

        //send mail
        if (message.send() == 0) {
          LOG.debug("meeting change email sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
        } else {
          LOG.debug("meeting change email not sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
        }
      }
      return true;
    }
    return false;
  }

  /*
   * Sends meeting invitation mails
   */
  private boolean sendMeetingInvitationMail() throws Exception {
    LOG.debug("Trying to send meeting invitation mail");

    //set mail templates
    templateSubject = freeMarkerConfiguration.getTemplate("event_meeting_invitation_email_subject-text.ftl");
    templateBody = freeMarkerConfiguration.getTemplate("event_meeting_invitation_email_body-html.ftl");

    //set replyto mailid
    message.setReplyTo(hostUser.getEmail(), hostUser.getNameFirstLast());

    //send mails to all meeting invitees
    for (User thisInvitee : meetingInviteesBean.getMembersFoundList().keySet()) {

      //set additional mail parameter maps
      bodyMap.put("invitee", thisInvitee);

      // override the date formatting
      if (thisInvitee.getLocale() != null) {
        bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), thisInvitee));
      }

      //set to mailid
      message.setTo(thisInvitee.getEmail());

      //build the templates
      StringWriter subjectTextWriter = new StringWriter();
      templateSubject.process(subjectMap, subjectTextWriter);
      StringWriter bodyTextWriter = new StringWriter();
      templateBody.process(bodyMap, bodyTextWriter);
      message.setSubject(subjectTextWriter.toString());
      message.setBody(bodyTextWriter.toString());
      LOG.debug(bodyTextWriter.toString());

      //send mail
      if (message.send() == 0) {
        LOG.debug("meeting invitation email sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
      } else {
        LOG.debug("meeting invitation email not sent to " + thisInvitee.getNameFirstLast() + " - " + thisInvitee.getEmail());
      }
    }
    return true;
  }

  /*
   * Sends mail to meeting host regarding user adding himself to meeting
   */
  private boolean sendUserJoinedMail() throws Exception {
    LOG.debug("Trying to send user joined to meeting mail");

    //set mail templates
    templateSubject = freeMarkerConfiguration.getTemplate("event_meeting_userjoined_email_subject-text.ftl");
    templateBody = freeMarkerConfiguration.getTemplate("event_meeting_userjoined_email_body-html.ftl");

    //set additional mail parameter maps
    User inviteeUser = UserUtils.loadUser(meetingInviteesBean.getMeetingAttendee().getUserId());
    bodyMap.put("invitee", inviteeUser);

    // override the date formatting
    if (inviteeUser.getLocale() != null) {
      bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), inviteeUser));
    }

    bodyMap.put("status", "but is tentative about");
    if (meetingInviteesBean.getMeetingAttendee().getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_ACCEPTED ||
        meetingInviteesBean.getMeetingAttendee().getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_APPROVE_YES) {
      bodyMap.put("status", "and will be");
    }

    //build the templates
    StringWriter subjectTextWriter = new StringWriter();
    templateSubject.process(subjectMap, subjectTextWriter);
    StringWriter bodyTextWriter = new StringWriter();
    templateBody.process(bodyMap, bodyTextWriter);
    message.setSubject(subjectTextWriter.toString());
    message.setBody(bodyTextWriter.toString());
    LOG.debug(bodyTextWriter.toString());

    //set replyto and to mailids
    message.setReplyTo(inviteeUser.getEmail(), inviteeUser.getNameFirstLast());
    message.setTo(hostUser.getEmail());

    //send mail
    if (message.send() == 0) {
      LOG.debug("user joined to meeting email sent to " + hostUser.getNameFirstLast() + " - " + hostUser.getEmail());
      return true;
    }

    LOG.debug("user joined to meeting email not sent to " + hostUser.getNameFirstLast() + " - " + hostUser.getEmail());
    return false;
  }

  /*
   * Sends approval or rejection mail to user on adding himself to meeting
   */
  private boolean sendUserJoinStatusMail() throws Exception {
    LOG.debug("Trying to send approve or reject mail for user join");

    //set mail templates
    templateSubject = freeMarkerConfiguration.getTemplate("event_meeting_joinstatus_email_subject-text.ftl");
    templateBody = freeMarkerConfiguration.getTemplate("event_meeting_joinstatus_email_body-html.ftl");

    //set additional mail parameter maps
    User inviteeUser = UserUtils.loadUser(meetingInviteesBean.getMeetingAttendee().getUserId());
    bodyMap.put("invitee", inviteeUser);

    // override the date formatting
    if (inviteeUser.getLocale() != null) {
      bodyMap.put("startDate", formatUserTimestamp(meetingInviteesBean.getMeeting().getStartDate(), inviteeUser));
    }

    bodyMap.put("status", "rejected");
    if (meetingInviteesBean.getMeetingAttendee().getDimdimStatus() == MeetingAttendee.STATUS_DIMDIM_ACCEPTED) {
      bodyMap.put("status", "approved");
    }

    //build the templates
    StringWriter subjectTextWriter = new StringWriter();
    templateSubject.process(subjectMap, subjectTextWriter);
    StringWriter bodyTextWriter = new StringWriter();
    templateBody.process(bodyMap, bodyTextWriter);
    message.setSubject(subjectTextWriter.toString());
    message.setBody(bodyTextWriter.toString());
    LOG.debug(bodyTextWriter.toString());

    //set replyto and to mailids
    message.setReplyTo(hostUser.getEmail(), hostUser.getNameFirstLast());
    message.setTo(inviteeUser.getEmail());

    //send mail
    if (message.send() == 0) {
      LOG.debug("user joined status to meeting email sent to " + inviteeUser.getNameFirstLast() + " - " + inviteeUser.getEmail());
      return true;
    }

    LOG.debug("user joined status to meeting email not sent to " + inviteeUser.getNameFirstLast() + " - " + inviteeUser.getEmail());
    return false;
  }

  private String formatUserTimestamp(Timestamp timestamp, User user) {
    // Use the user's locale
    SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(
        DateFormat.LONG, DateFormat.SHORT, user.getLocale());
    // Adjust the date/time based on any timezone
    if (user.getTimeZone() != null) {
      TimeZone tz = TimeZone.getTimeZone(user.getTimeZone());
      formatter.setTimeZone(tz);
    }
    return formatter.format(timestamp);
  }

  /**
   * The calendar module has a specific format for the url and must match the application's locale
   *
   * @param date
   * @param locale
   * @return
   */
  private String generateCalendarURL(Timestamp date, Locale locale) {
    SimpleDateFormat dtFormater = new SimpleDateFormat("yyyy-MM-dd", locale);
    return dtFormater.format(date);
  }

  public String getDescription() {
    return "Sends event meeting mails to meeting attendees";
  }
}
