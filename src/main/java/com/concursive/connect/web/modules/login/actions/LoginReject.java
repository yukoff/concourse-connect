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

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.members.dao.TeamMember;

import java.security.Key;
import java.sql.Connection;
import java.util.StringTokenizer;


/**
 * Actions for users replying to an invitation
 *
 * @author matt rajkowski
 * @version $Id$
 * @created November 7, 2003
 */
public final class LoginReject extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    Connection db = null;
    try {
      // Decode the user id
      String codedData = context.getRequest().getParameter("data");
      Key key = (Key) context.getServletContext().getAttribute("TEAM.KEY");
      String data = PrivateString.decrypt(key, codedData);
      //Process it into properties
      int userId = -1;
      int projectId = -1;
      StringTokenizer st = new StringTokenizer(data, ",");
      while (st.hasMoreTokens()) {
        String pair = (st.nextToken());
        StringTokenizer stPair = new StringTokenizer(pair, "=");
        String param = stPair.nextToken();
        String value = stPair.nextToken();
        if ("id".equals(param)) {
          userId = Integer.parseInt(value);
        } else if ("pid".equals(param)) {
          projectId = Integer.parseInt(value);
        }
      }
      // Verify the user's info by loading it
      db = getConnection(context);
      User thisUser = new User(db, getUser(context).getGroupId(), userId);
      context.getRequest().setAttribute("user", thisUser);
      // Get the team member to display the inviter
      TeamMember thisMember = new TeamMember(db, projectId, userId);
      context.getRequest().setAttribute("teamMember", thisMember);
      return ("DefaultOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("InvitationError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandConfirm(ActionContext context) {
    Connection db = null;
    try {
      // Decode the user id
      String codedData = context.getRequest().getParameter("data");
      Key key = (Key) context.getServletContext().getAttribute("TEAM.KEY");
      String data = PrivateString.decrypt(key, codedData);
      //Process it into properties
      int userId = -1;
      int projectId = -1;
      StringTokenizer st = new StringTokenizer(data, ",");
      while (st.hasMoreTokens()) {
        String pair = (st.nextToken());
        StringTokenizer stPair = new StringTokenizer(pair, "=");
        String param = stPair.nextToken();
        String value = stPair.nextToken();
        if ("id".equals(param)) {
          userId = Integer.parseInt(value);
        } else if ("pid".equals(param)) {
          projectId = Integer.parseInt(value);
        }
      }
      db = getConnection(context);
      // Verify the user's info by loading it
      User thisUser = new User(db, getUser(context).getGroupId(), userId);
      context.getRequest().setAttribute("user", thisUser);
      // Get the team member to display the invitee
      TeamMember previousMemberStatus = new TeamMember(db, projectId, userId);
      TeamMember thisMember = new TeamMember(db, projectId, userId);
      context.getRequest().setAttribute("teamMember", thisMember);
      // Update the project to indicate a rejection
      thisMember.setStatus(TeamMember.STATUS_REFUSED);
      thisMember.updateStatus(db);
      // Send an email to the invitee
      processUpdateHook(context, previousMemberStatus, thisMember);
      return ("RejectOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("InvitationError");
    } finally {
      this.freeConnection(context, db);
    }
  }
}

