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

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import nl.captcha.Captcha;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletSession;

/**
 * Represents a Project Claim Form
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created August 11, 2008
 */
public class SendProjectToFriendFormBean extends GenericBean {
  private String source = null;
  private int projectId = -1;
  private int userId = -1;
  private String sentFromName = null;
  private String sentFromEmail = null;
  private String sendToName = null;
  private String sendToEmails = null;
  private String note = null;
  private String captcha = null;

  public SendProjectToFriendFormBean() {
  }

  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * @param source the source to set
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * @return the userId
   */
  public int getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(String userId) {
    this.userId = Integer.parseInt(userId);
  }


  /**
   * @return the sentFromName
   */
  public String getSentFromName() {
    return sentFromName;
  }

  /**
   * @param sentFromName the sentFromName to set
   */
  public void setSentFromName(String sentFromName) {
    this.sentFromName = sentFromName;
  }

  /**
   * @return the sentFromEmail
   */
  public String getSentFromEmail() {
    return sentFromEmail;
  }

  /**
   * @param sentFromEmail the sentFromEmail to set
   */
  public void setSentFromEmail(String sentFromEmail) {
    this.sentFromEmail = sentFromEmail;
  }

  /**
   * @return the sendToName
   */
  public String getSendToName() {
    return sendToName;
  }

  /**
   * @param sendToName the sendToName to set
   */
  public void setSendToName(String sendToName) {
    this.sendToName = sendToName;
  }

  /**
   * @return the sendToEmails
   */
  public String getSendToEmails() {
    return sendToEmails;
  }

  /**
   * @param sendToEmails the sendToEmails to set
   */
  public void setSendToEmails(String sendToEmails) {
    this.sendToEmails = sendToEmails;
  }

  /**
   * @return the note
   */
  public String getNote() {
    return note;
  }

  /**
   * @param note the note to set
   */
  public void setNote(String note) {
    this.note = note;
  }

  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public boolean isValid(PortletSession session) {
    // Validate the captcha if the user is not logged in
    if (userId < 1) {
      String captchaPassed = (String) session.getAttribute("TE-REGISTER-CAPTCHA-PASSED");
      if (!"passed".equals(captchaPassed)) {
        Captcha captchaValue = (Captcha) session.getAttribute(Captcha.NAME);
        session.removeAttribute(Captcha.NAME);
        if (captchaValue == null) {
          System.out.println("SendProjectToFriendFormBean-> Could not find captcha session variable for comparison to user input");
        }
        if (captchaValue == null || captcha == null ||
            !captchaValue.isCorrect(captcha)) {
          errors.put("captchaError", "Text did not match image");
        } else {
          session.setAttribute("TE-REGISTER-CAPTCHA-PASSED", "passed");
        }
      }
    }
    // Validate the input
    try {
      new InternetAddress(this.getSentFromEmail());
    } catch (AddressException e) {
      errors.put("sentFromEmailError", "Email is invalid");
    }
    if (!StringUtils.hasText(this.getSentFromEmail())) {
      errors.put("sentFromEmailError", "Email is required");
    }
    if (this.getSentFromEmail().indexOf("@") < 1) {
      errors.put("sentFromEmailError", "Email is invalid");
    }
    if (!StringUtils.hasText(this.getSentFromName())) {
      errors.put("sentFromNameError", "Name is required");
    }
    if (!StringUtils.hasText(this.getSendToEmails())) {
      errors.put("sendToEmailsError", "Email is required");
    } else {
      String[] sendToEmail = this.getSendToEmails().split(",");
      for (String aSendToEmail : sendToEmail) {
        if (aSendToEmail.trim().indexOf("@") < 1) {
          errors.put("sendToEmailsError", "Invalid email address");
        }
        try {
          new InternetAddress(aSendToEmail.trim());
        } catch (AddressException e) {
          errors.put("sendToEmailsError", "Invalid email address");
        }
      }
    }
    return (!hasErrors());
  }
}
