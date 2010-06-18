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
package com.concursive.connect.web.modules.tools.actions;

import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.crm.api.client.CRMConnection;
import com.concursive.crm.api.client.DataRecord;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

/**
 * @author jfielek
 * @created Jul 19, 2008 11:53:58 PM
 */
public class ProjectManagementTools extends GenericAction {

  public String executeCommandDefault(ActionContext context) {
    // Base Visible Format is: /show/<project unique id>/tools[/xyz]
    String projectId = context.getRequest().getParameter("pid");
    String linkTo = context.getRequest().getParameter("linkTo");
    // Validate and redirect to the tools url
    try {
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      // Make sure the project has a tools link
      if (!StringUtils.hasText(thisProject.getConcursiveCRMUrl())) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("ProjectManagementTools-> concursive crm url is empty for project: " + thisProject.getId() + " - " + thisProject.getUniqueId());
        }
        return "PermissionError";
      }
      // Make sure this user is on the team
      TeamMember thisMember = thisProject.getTeam().getTeamMember(getUserId(context));
      if (thisMember == null || !thisMember.getTools()) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("ProjectManagementTools-> user doesn't have access to tools");
        }
        return "PermissionError";
      }
      // Retrieve owner's email address to create a relationship in tools
      String ownerEmail = null;
      Connection db = null;
      try {
        db = getConnection(context);
        // Get owner's email address in case the user needs to be added
        int ownerId = thisProject.getOwner();
        if (ownerId > -1) {
          User owner = UserUtils.loadUser(ownerId);
          ownerEmail = owner.getEmail();
        }
      } catch (Exception e) {
        e.printStackTrace();
        return "SystemError";
      } finally {
        freeConnection(context, db);
      }

      // Determine the user's current role
      LookupList roleList = CacheUtils.getLookupList("lookup_project_role");
      String role = roleList.getValueFromId(thisMember.getUserLevel());

      // Then generate token and use CRM API to pass token, and then redirect to CRM
      if (System.getProperty("DEBUG") != null) {
        System.out.println("ProjectManagementTools-> generating token");
      }
      String token = generateRandomToken();
      if (sendToken(thisProject, getUser(context), token, ownerEmail, role)) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("ProjectManagementTools-> sending redirect");
        }
        // if successful, send the redirect...
        String redirect = "MyCFS.do?command=Home";
        if ("campaign".equals(linkTo)) {
          redirect = "CampaignManager.do?command=Default";
        }
        // Link back to this page
        String returnURL = context.getRequest().getParameter("returnURL");
        if (returnURL != null) {
          try {
            returnURL = RequestUtils.getAbsoluteServerUrl(context.getRequest()) + URLEncoder.encode(returnURL, "UTF-8");
          } catch (Exception e) {
          }
        }
        LOG.debug("Return URL: " + returnURL);
        context.getRequest().setAttribute("redirectTo", thisProject.getConcursiveCRMUrl() + "/" + redirect + "&token=" + token + (returnURL != null ? "&returnURL=" + returnURL : ""));
        return ("Redirect301");
      } else {
        return "ToolsError";
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (System.getProperty("DEBUG") != null) {
      System.out.println("ProjectManagementTools-> End reached");
    }
    return "SystemError";
  }

  private boolean sendToken(Project project, User user, String token, String ownerEmail, String role) {
    // Connection details
    CRMConnection conn = new CRMConnection();
    conn.setUrl(project.getConcursiveCRMUrl());
    conn.setId(project.getConcursiveCRMDomain());
    conn.setCode(project.getConcursiveCRMCode());
    conn.setClientId(project.getConcursiveCRMClient());
    // Request info
    DataRecord record = new DataRecord();
    record.setName("map");
    record.setAction("importSessionAuthenticationId");
    record.addField("userName", user.getUsername());
    record.addField("userEmail", user.getEmail());
    record.addField("sessionToken", token);
    record.addField("userFirstName", user.getFirstName());
    record.addField("userLastName", user.getLastName());
    record.addField("userCompany", user.getCompany());
    record.addField("userPassword", user.getPassword());
    if (StringUtils.hasText(ownerEmail)) {
      record.addField("userReportsTo", ownerEmail);
    }
    record.addField("userTimeZone", user.getTimeZone());
    record.addField("role", role);
    boolean success = conn.save(record);
    if (!success) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("ProjectManagementTools-> sendToken error: " + conn.getLastResponse());
      }
    }
    return success;
  }

  static Random random = new Random();
  static String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxy0123456789";

  private String generateRandomToken() {
    String token = new String("");
    for (int i = 0; i < 128; i++) {
      // Generate a random character..
      int charNum = random.nextInt(validCharacters.length());
      token += validCharacters.charAt(charNum);
      //token += validCharacters.substring(charNum, charNum + 1);
    }
    return token;
  }
}
