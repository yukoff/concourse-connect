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

package com.concursive.connect.web.modules.login.actions;

import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.members.dao.TeamMember;

import java.sql.Connection;

public final class ResetPassword extends GenericAction {

  public String executeCommandDefault(ActionContext context) {
    // If SSL, then redirect to SSL
    ApplicationPrefs prefs = getApplicationPrefs(context);
    boolean sslEnabled = "true".equals(getPref(context, "SSL"));
    if (sslEnabled && !"https".equals(context.getRequest().getScheme())) {
      String url = ("https://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), context.getRequest())) + "/ResetPassword.do";
      context.getRequest().setAttribute("redirectTo", url);
      return "Redirect301";
    }
    //Show the need password form
    return "FormOK";
  }


  public String executeCommandReset(ActionContext context) {
    // If SSL, then redirect to SSL
    ApplicationPrefs prefs = getApplicationPrefs(context);
    boolean sslEnabled = "true".equals(getPref(context, "SSL"));
    if (sslEnabled && !"https".equals(context.getRequest().getScheme())) {
      String url = ("https://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), context.getRequest())) + "/ResetPassword.do";
      context.getRequest().setAttribute("redirectTo", url);
      return "Redirect301";
    }
    //Check parameters
    String email = context.getRequest().getParameter("email");
    if (email == null || email.equals("")) {
      return "ResetError";
    }
    Connection db = null;
    int id = -1;
    User thisUser = null;
    try {
      db = getConnection(context);
      //Save the form and email a confirmation and password
      id = User.getIdByEmailAddress(db, email);
      if (id > -1) {
        thisUser = new User(db, 1, id);
        if (thisUser.getRegistered()) {
          thisUser.resetPassword(context, db);
        } else {
          TeamMemberList teamMemberList = new TeamMemberList();
          teamMemberList.setUserId(thisUser.getId());
          teamMemberList.buildList(db);
          if (teamMemberList.size() > 0) {
            // Resend the last invitation
            TeamMember teamMember = teamMemberList.get(teamMemberList.size() - 1);
            teamMember.setStatus(TeamMember.STATUS_INVITING);
            processInsertHook(context, teamMember, "concursive.teamMember.sendInvitationToUser");
          } else {
            id = -1;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    if (id > -1) {
      return "ResetOK";
    } else {
      context.getRequest().setAttribute("actionError", "Email could not be sent to specified address" + (thisUser != null ? ": " + (String) thisUser.getErrors().get("emailError") : ""));
      context.getRequest().setAttribute("emailError", "Check email address");
      return "ResetError";
    }
  }

  public String executeCommandCloseForm(ActionContext context) {
    return "CloseFormOK";
  }
}

