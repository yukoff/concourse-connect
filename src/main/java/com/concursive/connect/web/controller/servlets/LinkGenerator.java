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

package com.concursive.connect.web.controller.servlets;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.beans.URLControllerBean;
import com.concursive.connect.web.modules.issues.utils.TicketUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Static methods for building and reusing specific urls
 *
 * @author matt rajkowski
 * @created Apr 8, 2008
 */
public class LinkGenerator {

  private static Log LOG = LogFactory.getLog(LinkGenerator.class);

  public static String getProfileImageLink(int projectId, String imageUrl) {
    return "Portal.do?command=Img&pid=" + projectId + "&url=" + imageUrl;
  }

  public static String getRemoveProfileImageLink(int projectId, String imageUrl) {
    return "ProjectManagementProfile.do?command=DeleteImg&pid=" + projectId + "&url=" + imageUrl;
  }

  public static String getSetProfileImageLink(int projectId, String imageUrl) {
    return "ProjectManagementProfile.do?command=SetImg&pid=" + projectId + "&url=" + imageUrl;
  }

  public static String getDashboardLink(int projectId, String dashboard) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Dashboard&pid=" + projectId + (dashboard != null ? "&dash=" + dashboard : "");
  }

  public static String getProjectPortalLink(URLControllerBean bean) {
    if (LOG.isTraceEnabled()) {
      LOG.trace(bean.toString());
    }
    return "ProjectManagement.do?command=ProjectCenter" +
        "&portlet-action=" + StringUtils.toString(bean.getAction()) +
        "&portlet-pid=" + bean.getProjectId() +
        "&portlet-object=" + StringUtils.toString(bean.getDomainObject()) +
        "&portlet-value=" + StringUtils.toString(bean.getObjectValue()) +
        "&portlet-params=" + StringUtils.toString(bean.getParams());
  }

  public static String getAdminPortalLink(URLControllerBean bean) {
    if (LOG.isTraceEnabled()) {
      LOG.trace(bean.toString());
    }
    return "Admin.do?command=Portal" +
        "&portlet-action=" + StringUtils.toString(bean.getAction()) +
        "&portlet-object=" + StringUtils.toString(bean.getDomainObject()) +
        "&portlet-value=" + StringUtils.toString(bean.getObjectValue()) +
        "&portlet-params=" + StringUtils.toString(bean.getParams());
  }

  /**
   * Method for
   *
   * @param bean
   * @return
   */
  public static String getProjectActionLink(URLControllerBean bean) {
    if (LOG.isTraceEnabled()) {
      LOG.trace(bean.toString());
    }
    // Generate a servlet URL class action
    String[] newAction = bean.getDomainObject().split("-");
    StringBuffer actionBuffer = new StringBuffer();
    for (String thisAction : newAction) {
      actionBuffer.append(thisAction.substring(0, 1).toUpperCase()).append(thisAction.substring(1));
    }
    // Generate a servlet URL method command
    return actionBuffer.toString() + ".do?command=" + bean.getAction().substring(0, 1).toUpperCase() + bean.getAction().substring(1) +
        "&pid=" + bean.getProjectId() +
        "&portlet-value=" + StringUtils.toString(bean.getObjectValue()) +
        "&portlet-params=" + StringUtils.toString(bean.getParams());
  }

  public static String getBlogImageLink(int projectId, String image) {
    return "BlogActions.do?command=Img&pid=" + projectId + "&subject=" + image;
  }

  public static String getWikiImageLink(int projectId, String image) {
    return "ProjectManagementWiki.do?command=Img&pid=" + projectId + "&subject=" + image;
  }

  public static String getListsLink(int projectId) {
    return "ProjectManagementListsBuckets.do?command=Categories&pid=" + projectId;
  }

  public static String getListDetailsLink(int projectId, int listId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Lists&pid=" + projectId + "&cid=" + listId;
  }

  public static String getPlanLink(int projectId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Requirements&pid=" + projectId;
  }

  public static String getPlanLink(int projectId, int planId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Assignments&pid=" + projectId + "&rid=" + planId;
  }

  public static String getTicketsLink(int projectId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Tickets&pid=" + projectId;
  }

  public static String getTicketDetailsLink(int projectId, int projectTicketId) {
    int ticketId = TicketUtils.retrieveTicketIdFromProjectTicketId(projectId, projectTicketId);
    return "ProjectManagementTickets.do?command=Details&pid=" + projectId + "&id=" + ticketId;
  }

  public static String getTeamLink(int projectId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Team&pid=" + projectId;
  }

  public static String getAdsLink(int projectId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Ads&pid=" + projectId;
  }

  public static String getDetailsLink(int projectId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Details&pid=" + projectId;
  }

  public static String getSetupLink(int projectId) {
    return "ProjectManagement.do?command=ProjectCenter&portlet-section=Setup&pid=" + projectId;
  }

  public static String getCustomizeLink(int projectId) {
    return "ProjectManagement.do?command=CustomizeProject&pid=" + projectId;
  }

  public static String getPermissionsLink(int projectId) {
    return "ProjectManagement.do?command=ConfigurePermissions&pid=" + projectId;
  }

  public static String getCustomizeStyleLink(int projectId) {
    return "ProjectManagement.do?command=CustomizeStyle&pid=" + projectId;
  }

  public static String getStyleImageLink(int projectId, String image) {
    return "ProjectManagement.do?command=StyleImg&pid=" + projectId + "&subject=" + image;
  }

  public static String getPageLink(int projectId, String page) {
    return "ProjectManagementPage.do?command=ShowPortalPage&pid=" + projectId + "&name=" + page;
  }

  public static String getToolsLink(int projectId, String url) {
    return "ProjectManagementTools.do?command=Default&pid=" + projectId + "&linkTo=" + url;
  }

  public static String getCRMLink() {
    return "ProjectManagementCRM.do?command=Default";
  }

  public static String getCRMAccountLink(int projectId, String objectValue) {
    return "ProjectManagementCRM.do?command=ShowAccount&pid=" + projectId;
  }

  public static String getItemLink(int linkModuleId, int linkItemId, int linkAnchor) {
    return (getItemLink(linkModuleId, linkItemId) + "#" + linkAnchor);
  }

  public static String getItemLink(int linkModuleId, int linkItemId) {
    return getItemLink(linkModuleId, String.valueOf(linkItemId));
  }

  /**
   * Return the link to the item (an promotion, classified or blog)
   * TODO: may be there is a better way to do this as this information is already in project-portal-config.xml
   *
   * @param linkModuleId the module's id
   * @param linkItemId   the object's id within the module
   * @return the object's url part
   */
  public static String getItemLink(int linkModuleId, String linkItemId) {
    String itemLink = null;
    if (linkModuleId != -1) {
      if (linkModuleId == Constants.PROJECT_CLASSIFIEDS_FILES) {
        itemLink = "classified-ad/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECT_BLOG_FILES) {
        itemLink = "post/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECT_AD_FILES) {
        itemLink = "promotion/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECT_REVIEW_FILES) {
        itemLink = "review/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECT_WIKI_FILES) {
        itemLink = "wiki"; //TODO: needs to consider wiki subject
      } else if (linkModuleId == Constants.DISCUSSION_FILES_TOPIC) {
        itemLink = "topic/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECT_WIKI_COMMENT_FILES) {
        itemLink = "wiki"; //TODO: needs to consider wiki subject
      } else if (linkModuleId == Constants.BLOG_POST_COMMENT_FILES) {
        itemLink = "post/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECTS_FILES) {
        itemLink = "file/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECTS_CALENDAR_EVENT_FILES) {
        itemLink = "calendar/" + linkItemId;
      } else if (linkModuleId == Constants.PROJECT_IMAGE_FILES) {
        itemLink = "";
      } else if (linkModuleId == Constants.PROJECT_MESSAGES_FILES) {
        itemLink = "message/inbox/" + linkItemId;
      }
    }
    return itemLink;
  }
}
