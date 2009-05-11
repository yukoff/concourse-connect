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

package com.concursive.connect.web.modules.discussion.workflow;

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.Forum;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.Connection;

/**
 * Loads the topic record and associated data
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 8, 2007
 */
public class LoadTopicDetails extends ObjectHookComponent implements ComponentInterface {

  public final static String ENTERED_BY_CONTACT = "issueEnteredByContact";
  public final static String MODIFIED_BY_CONTACT = "issueModifiedByContact";
  public final static String PROJECT = "project";
  public final static String ISSUE_CATEGORY = "issueCategory";
  //public final static String ISSUE = "this";

  public String getDescription() {
    return "Load all issue information for use in other steps";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    Topic thisTopic = (Topic) context.getThisObject();
    Connection db = null;
    try {
      db = getConnection(context);
      // Set project
      if (thisTopic.getProjectId() > 0) {
        Project project = ProjectUtils.loadProject(thisTopic.getProjectId());
        context.setAttribute(PROJECT, project);
      } else {
        context.setAttribute(PROJECT, new Project());
      }
      // Set issueCategory
      if (thisTopic.getCategoryId() > 0 && thisTopic.getProjectId() > 0) {
        Forum forum = new Forum(db, thisTopic.getCategoryId(), thisTopic.getProjectId());
        context.setAttribute(ISSUE_CATEGORY, forum);
      } else {
        context.setAttribute(ISSUE_CATEGORY, new Forum());
      }
      // Set enteredby
      if (thisTopic.getEnteredBy() > 0) {
        context.setAttribute(ENTERED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, thisTopic.getEnteredBy())).getNameFirstLastInitial());
      } else {
        context.setAttribute(ENTERED_BY_CONTACT, "");
      }
      // Set modifiedby
      if (thisTopic.getModifiedBy() > 0) {
        context.setAttribute(MODIFIED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, thisTopic.getModifiedBy())).getNameFirstLastInitial());
      } else {
        context.setAttribute(MODIFIED_BY_CONTACT, "");
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
