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
package com.concursive.connect.web.modules.profile.portlets;

import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.text.Template;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.*;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.TemplateException;
import freemarker.template.Configuration;

/**
 * Send Project To Friend Portlet
 *
 * @author Kailash Bhoopalam
 * @created August 11, 2008
 */
public class SendProjectToFriendPortlet extends GenericPortlet {

  // Pages
  private static final String EDIT_PAGE = "/portlets/send_project_to_friend/send_project_to_friend-edit.jsp";
  private static final String VIEW_PAGE = "/portlets/send_project_to_friend/send_project_to_friend-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_FAILURE_MESSAGE = "failureMessage";
  private static final String PREF_MESSAGE_BODY = "messageBody";
  private static final String PREF_MESSAGE_SUBJECT = "messageSubject";

  // Attribute names for objects available in the view
  private static final String TITLE = "title";
  private static final String ERROR_MESSAGE = "errorMessage";
  private static final String FAILURE_MESSAGE = "failureMessage";
  private static final String USER = "user";
  private static final String SEND_PROJECT_TO_FRIEND_FORM_BEAN = "sendProjectToFriendFormBean";
  private static final String PROJECT = "project";
  private static final String CAPTCHA_PASSED = "captchaPassed";

  private static final String VIEW_TYPE = "viewType";
  private static final String SEND_FAILURE = "sendFailure";
  private static final String SEND_SUCCESS = "sendSuccess";
  private static final String MESSAGE_SEND_STATUS = "messageSendStatus";
  private static final String CLOSE = "close";


  private static final String PROJECT_ID = "pid";

  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    // populate a SendProjectToFriendFormBean
    // show the form with username
    try {
      // Add the captcha session variable to the request so that the bean can be populated
      String captchaPassed = (String) request.getPortletSession().getAttribute("TE-REGISTER-CAPTCHA-PASSED");
      if (captchaPassed != null) {
        request.setAttribute(CAPTCHA_PASSED, captchaPassed);
      }

      SendProjectToFriendFormBean formBean = new SendProjectToFriendFormBean();
      String defaultView = EDIT_PAGE;
      String viewType = request.getParameter("viewType");
      if (viewType == null) {
        viewType = (String) request.getPortletSession().getAttribute("viewType");
      }
      // Set global preferences
      request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, null));

      Project project = PortalUtils.getProject(request);
      request.setAttribute(PROJECT, project);

      User user = PortalUtils.getUser(request);
      int projectId = project == null ? -1 : project.getId();
      if (SEND_FAILURE.equals(viewType)) {
        // Show the form with any errors provided
        request.setAttribute(ERROR_MESSAGE, request.getPreferences().getValue(PREF_FAILURE_MESSAGE, null));

        formBean = (SendProjectToFriendFormBean) request.getPortletSession().getAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN);
        PortalUtils.processErrors(request, formBean.getErrors());

        request.setAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN, formBean);
        request.getPortletSession().removeAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN);

        // Cleanup other session attributes
        request.getPortletSession().removeAttribute(VIEW_TYPE);
        request.getPortletSession().removeAttribute(CAPTCHA_PASSED);

      } else if (SEND_SUCCESS.equals(viewType)) {

        // Use the project on the results page
        formBean = (SendProjectToFriendFormBean) request.getPortletSession().getAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN);
        request.setAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN, formBean);
        request.getPortletSession().removeAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN);

        request.setAttribute(FAILURE_MESSAGE, request.getPreferences().getValue(PREF_FAILURE_MESSAGE, null));

        // Clean up the session
        request.setAttribute(MESSAGE_SEND_STATUS, (HashMap<String, String>) request.getPortletSession().getAttribute(MESSAGE_SEND_STATUS));
        request.getPortletSession().removeAttribute(MESSAGE_SEND_STATUS);
        request.getPortletSession().removeAttribute(VIEW_TYPE);
        request.getPortletSession().removeAttribute(CAPTCHA_PASSED);

        defaultView = VIEW_PAGE;

      } else {
        if (projectId <= 0) {
          request.setAttribute(ERROR_MESSAGE, "No project was specified");
          defaultView = VIEW_PAGE;
        } else {
          // Show the send project to friend form, filled out
          // Fill out with properties from project and user
          formBean.setProjectId(projectId);
          if (user != null && user.getId() >= 1) {
            formBean.setSentFromName(user.getNameFirstLast());
            formBean.setSentFromEmail(user.getEmail());
            request.setAttribute(USER, user);
          }
          request.setAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN, formBean);
        }
      }
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);
    } catch (Exception e) {
      e.printStackTrace();
      throw new PortletException(e);
    }
  }

  /**
   * @return
   */
  private String getFormattedMessageToSend(PortletRequest request, String messageToFormat, Project project) {
    String formattedMessage = null;
    Template template = new Template();
    template.setText(messageToFormat);
    template.addParseElement("${bean.sentFromName}", request.getParameter("sentFromName"));
    template.addParseElement("${bean.project.title}", project.getTitle());
    template.addParseElement("${bean.project.description}", project.getShortDescription());
    template.addParseElement("${bean.project.location}", project.getLocation());
    template.addParseElement("${bean.message}", request.getParameter("note"));
    template.addParseElement("${bean.project.uniqueId}", project.getUniqueId());
    template.addParseElement("${secureUrl}", PortalUtils.getApplicationUrl(request));
    formattedMessage = template.getParsedText();
    //System.out.println("formatted text ==> " + formattedMessage);
    return formattedMessage;
  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {

    // Determine the page that submitted the data
    String currentPage = request.getParameter("currentPage");

    String ctx = request.getContextPath();
    if ("finalPage".equals(currentPage)) {
      response.sendRedirect(ctx + "/close_panel_refresh.jsp");
    } else {
      // Handle the send to friend form and show the status of each email
      try {
        HashMap<String, String> messageSendStatus = sendProjectToFriend(request);
        if (messageSendStatus.size() == 0) {
          request.getPortletSession().setAttribute(VIEW_TYPE, SEND_FAILURE);
        } else {
          // See if any of the sent messages had an error, to show status report
          boolean hasError = false;
          for (String result : messageSendStatus.values()) {
            if ("Failure".equals(result)) {
              hasError = true;
              break;
            }
          }
          if (hasError) {
            // Let the user know which addresses caused an error
            request.getPortletSession().setAttribute(VIEW_TYPE, SEND_SUCCESS);
            request.getPortletSession().setAttribute(MESSAGE_SEND_STATUS, messageSendStatus);
          } else {
            // Close the panel, everything went well
            response.sendRedirect(ctx + "/close_panel_refresh.jsp");
          }
        }
      } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }

  private HashMap<String, String> sendProjectToFriend(ActionRequest request) throws SQLException {
    Connection db = null;
    HashMap<String, String> messageSendStatus = new HashMap<String, String>();
    // Parameters
    String projectIdStr = request.getParameter(PROJECT_ID);
    SendProjectToFriendFormBean formBean = new SendProjectToFriendFormBean();
    PortalUtils.populateObject(formBean, request);
    db = PortalUtils.getConnection(request);
    if (formBean.getProjectId() <= 0) {
      formBean.addError("projectNotFoundError", "No existing project was specified");
    }
    Project project = new Project(db, formBean.getProjectId());

    // If the user is logged in, user
    User user = PortalUtils.getUser(request);
    if (user != null && user.getId() > 0) {
      formBean.setUserId(user.getId());
    }
    boolean isValid = formBean.isValid(request.getPortletSession());

    // Handle validation error
    if (!isValid) {
      request.getPortletSession().setAttribute("actionError", "The form could not be submitted as it has the following errors");
    } else {
      // Add the form values to the smtp message template
      String messageSubject = getFormattedMessageToSend(request, request.getPreferences().getValue(PREF_MESSAGE_SUBJECT, null), project);
      String messageBody = getFormattedMessageToSend(request, request.getPreferences().getValue(PREF_MESSAGE_BODY, null), project);
      // Process multiple email addresses
      String[] sendToEmail = formBean.getSendToEmails().split(",");
      for (String aSendToEmail : sendToEmail) {
        if (sendEmailMessage(PortalUtils.getFreemarkerConfiguration(request), messageSubject, messageBody, formBean.getSentFromEmail(), aSendToEmail.trim(), PortalUtils.getApplicationPrefs(request)) == 0) {
          messageSendStatus.put(aSendToEmail, "Success");
        } else {
          messageSendStatus.put(aSendToEmail, "Failure");
        }
      }
    }
    request.getPortletSession().setAttribute(SEND_PROJECT_TO_FRIEND_FORM_BEAN, formBean);
    return messageSendStatus;
  }

  /**
   *
   */
  private int sendEmailMessage(Configuration configuration, String subject, String body, String sentFromEmail, String sendToEmail, ApplicationPrefs prefs) {
    try {
      SMTPMessage messageToSend = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
      messageToSend.setFrom(prefs.get("EMAILADDRESS"));
      messageToSend.addReplyTo(sentFromEmail);
      messageToSend.setTo(sendToEmail);
      messageToSend.setType("text/html");
      messageToSend.setSubject(subject);
      // Populate the message template
      freemarker.template.Template template = configuration.getTemplate("send_project_to_friend_email-html.ftl");
      Map bodyMappings = new HashMap();
      bodyMappings.put("body", body);
      // Parse and send
      StringWriter inviteBodyTextWriter = new StringWriter();
      template.process(bodyMappings, inviteBodyTextWriter);
      messageToSend.setBody(inviteBodyTextWriter.toString());
      return messageToSend.send();
    } catch (IOException io) {
      return -1;
    } catch (TemplateException te) {
      return -1;
    }
  }
}
