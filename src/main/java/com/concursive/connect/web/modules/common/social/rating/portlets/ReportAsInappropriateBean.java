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
package com.concursive.connect.web.modules.common.social.rating.portlets;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.issues.dao.Ticket;

/**
 * Represents an Inappropriate content/report issue form
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created February 18, 2009
 */
public class ReportAsInappropriateBean extends GenericBean {

  private int linkProjectId = -1;
  private int linkModuleId = -1;
  private String linkModule = null;
  private int linkItemId = -1;
  private int userId = -1;
  private String comment = null;


  /**
   * @return the linkProjectId
   */
  public int getLinkProjectId() {
    return linkProjectId;
  }

  /**
   * @param linkProjectId the linkProjectId to set
   */
  public void setLinkProjectId(int linkProjectId) {
    this.linkProjectId = linkProjectId;
  }

  public void setLinkProjectId(String linkProjectId) {
    this.linkProjectId = Integer.parseInt(linkProjectId);
  }

  /**
   * @return the linkModuleId
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }

  /**
   * @param linkModuleId the linkModuleId to set
   */
  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  public void setLinkModuleId(String linkModuleId) {
    this.linkModuleId = Integer.parseInt(linkModuleId);
  }

  /**
   * @return the linkModule
   */
  public String getLinkModule() {
    return linkModule;
  }

  /**
   * @param linkModule the linkModule to set
   */
  public void setLinkModule(String linkModule) {
    this.linkModule = linkModule;
  }

  /**
   * @return the linkItemId
   */
  public int getLinkItemId() {
    return linkItemId;
  }

  /**
   * @param linkItemId the linkItemId to set
   */
  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public void setLinkItemId(String linkItemId) {
    this.linkItemId = Integer.parseInt(linkItemId);
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

  public void setUserId(String userId) {
    this.userId = Integer.parseInt(userId);
  }

  /**
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * @param comment the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean populateTicketFromBean(Ticket ticket) {
    if (!isValid()) {
      return false;
    }
    ticket.setProblem(comment);
    ticket.setLinkProjectId(linkProjectId);
    ticket.setLinkModuleId(linkModuleId);
    ticket.setLinkItemId(linkItemId);

    return true;
  }

  /**
   * @return
   */
  private boolean isValid() {
    if (!StringUtils.hasText(comment)) {
      errors.put("commentError", "Comment is required");
    }
    return !this.hasErrors();
  }
}
