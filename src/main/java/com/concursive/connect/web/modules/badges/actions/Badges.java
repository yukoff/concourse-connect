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
package com.concursive.connect.web.modules.badges.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.dao.BadgeList;
import com.concursive.connect.web.modules.badges.utils.BadgeUtils;

import java.sql.Connection;
import java.util.Map;

/**
 * Actions for working with global badges
 *
 * @author matt rajkowski
 * @created December 30, 2008
 */
public final class Badges extends GenericAction {

  private static final String BADGE = "badge";
  private static final String BADGE_MEMBER_COUNT_MAP = "badgeMemberCountMap";
  private static final String BADGE_MEMBER_COUNT = "memberCount";

  /**
   * Method for viewing the details of a badge
   *
   * @param context ActionContext
   * @return String the mvc result
   */
  public String executeCommandDetails(ActionContext context) {
    Connection db = null;
    try {
      // Get parameters
      int badgeId = Integer.parseInt(context.getRequest().getParameter("id"));

      // Load the record
      Badge badge = BadgeUtils.loadBadge(badgeId);
      if (badge == null) {
        return "PermissionError";
      }
      context.getRequest().setAttribute(BADGE, badge);

      // Verify the record can be seen
      if (!badge.getEnabled()) {
        return "PermissionError";
      }

      // Determine the database connection
      db = getConnection(context);

      // Count the number of members for this badge
      BadgeList badgeList = new BadgeList();
      badgeList.add(badge);
      Map<Integer, Integer> memberCountMap = badgeList.findBadgeMemberCount(db);
      context.getRequest().setAttribute(BADGE_MEMBER_COUNT_MAP, memberCountMap);
      // Let the display know the value
      String memberCountValue = "0";
      if (memberCountMap.containsKey(badge.getId())) {
        memberCountValue = String.valueOf(memberCountMap.get(badge.getId()));
      }
      context.getRequest().setAttribute(BADGE_MEMBER_COUNT, memberCountValue);

      // Show a successful result
      return "BadgeOK";
    } catch (Exception e) {
      // Report an error
      e.printStackTrace();
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      // Always free the connection
      this.freeConnection(context, db);
    }
  }
}