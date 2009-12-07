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

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;
import com.concursive.connect.web.utils.LookupList;

import java.sql.Connection;

/**
 * Records team member update events
 *
 * @author Ananth
 * @created Feb 20, 2009
 */
public class SaveTeamMemberUpdateEvents extends ObjectHookComponent implements ComponentInterface {
  public final static String HISTORY_INVITE_TEXT = "history.invite.text";
  public final static String HISTORY_FRIEND_INVITE_TEXT = "history.friend.invite.text";
  public final static String HISTORY_PROMOTE_TEXT = "history.promote.text";
  public final static String HISTORY_GRANT_TEXT = "history.grant.text";

  public String getDescription() {
    return "Records team member update events..";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    Connection db = null;

    try {
      db = getConnection(context);

      TeamMember thisMember = (TeamMember) context.getThisObject();
      TeamMember prevMember = (TeamMember) context.getPreviousObject();

      // load the user that invited the team member
      User user = UserUtils.loadUser(thisMember.getModifiedBy());
      Project userProfile = ProjectUtils.loadProject(user.getProfileProjectId());

      // load the user that is being updated
      User member = UserUtils.loadUser(thisMember.getUserId());
      Project memberProfile = ProjectUtils.loadProject(member.getProfileProjectId());

      // load the project profile
      Project projectProfile = ProjectUtils.loadProject(thisMember.getProjectId());

      if (prevMember.getStatus() != TeamMember.STATUS_ADDED &&
          thisMember.getStatus() == TeamMember.STATUS_ADDED) {
        // An existing team member accepted an invitation

        // Prepare the wiki links
        context.setParameter("user", WikiLink.generateLink(userProfile));
        context.setParameter("member", WikiLink.generateLink(memberProfile));
        context.setParameter("profile", WikiLink.generateLink(projectProfile));

        // Insert the history
        ProjectHistory history = new ProjectHistory();
        history.setEnteredBy(thisMember.getUserId());
        history.setProjectId(thisMember.getProjectId());
        history.setLinkObject(ProjectHistoryList.INVITES_OBJECT);
        history.setEventType(ProjectHistoryList.ACCEPT_INVITATION_EVENT);
        history.setLinkItemId(thisMember.getId());
        if (projectProfile.getProfile()) {
          // Became a friend
          history.setDescription(context.getParameter(HISTORY_FRIEND_INVITE_TEXT));
        } else {
          // Became a member
          history.setDescription(context.getParameter(HISTORY_INVITE_TEXT));
        }
        history.insert(db);
      }

      if (prevMember.getRoleId() != thisMember.getRoleId() &&
          thisMember.getRoleId() == TeamMember.VIP) {

        LookupList roleList = CacheUtils.getLookupList("lookup_project_role");

        // team member was upgraded to a VIP role

        // Prepare the wiki links
        context.setParameter("user", WikiLink.generateLink(userProfile));
        context.setParameter("member", WikiLink.generateLink(memberProfile));
        context.setParameter("profile", WikiLink.generateLink(projectProfile));
        context.setParameter("role", roleList.getValueFromId(thisMember.getUserLevel()));

        // Insert the history
        ProjectHistory history = new ProjectHistory();
        history.setEnteredBy(user.getId());
        history.setProjectId(thisMember.getProjectId());
        history.setLinkObject(ProjectHistoryList.ROLE_OBJECT);
        history.setEventType(ProjectHistoryList.PROMOTE_MEMBER_EVENT);
        history.setLinkItemId(thisMember.getId());
        history.setDescription(context.getParameter(HISTORY_PROMOTE_TEXT));
        history.insert(db);
      }

      if (!prevMember.getTools() && thisMember.getTools()) {
        // team member just received tools access

        // Prepare the wiki links
        context.setParameter("user", WikiLink.generateLink(userProfile));
        context.setParameter("member", WikiLink.generateLink(memberProfile));

        // Insert the history
        ProjectHistory history = new ProjectHistory();
        history.setEnteredBy(user.getId());
        history.setProjectId(thisMember.getProjectId());
        history.setLinkObject(ProjectHistoryList.TOOLS_OBJECT);
        history.setEventType(ProjectHistoryList.GRANT_MEMBER_TOOLS_EVENT);
        history.setLinkItemId(thisMember.getId());
        history.setDescription(context.getParameter(HISTORY_GRANT_TEXT));
        history.insert(db);
      }

      result = true;
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return result;
  }
}
