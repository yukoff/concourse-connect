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
import com.concursive.connect.web.modules.discussion.dao.Reply;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;

/**
 * Loads the reply record and associated data
 *
 * @author matt rajkowski
 * @created June 8, 2007
 */
public class LoadReplyDetails extends ObjectHookComponent implements ComponentInterface {

  private static Log LOG = LogFactory.getLog(LoadReplyDetails.class);

  public final static String ENTERED_BY_CONTACT = "issueReplyEnteredByContact";
  public final static String MODIFIED_BY_CONTACT = "issueReplyModifiedByContact";
  public final static String PROJECT = "project";
  public final static String ISSUE_CATEGORY = "issueCategory";
  public final static String ISSUE = "issue";
  public final static String ISSUE_ENTERED_BY_CONTACT = "issueEnteredByContact";
  public final static String ISSUE_MODIFIED_BY_CONTACT = "issueModifiedByContact";
  //public final static String ISSUE_REPLIED_TO = "issueRepliedTo";
  //public final static String ISSUE_REPLY = "this";

  public String getDescription() {
    return "Load all issue reply information for use in other steps";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    Reply thisReply = (Reply) context.getThisObject();
    Connection db = null;
    try {
      db = getConnection(context);
      if (thisReply.getIssueId() > 0) {
        // Set issue
        Topic topic = new Topic(db, thisReply.getIssueId(), thisReply.getProjectId());
        context.setAttribute(ISSUE, topic);
        // Set issue enteredby
        if (topic.getEnteredBy() > 0) {
          context.setAttribute(ISSUE_ENTERED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, topic.getEnteredBy())).getNameFirstLastInitial());
        } else {
          context.setAttribute(ISSUE_ENTERED_BY_CONTACT, "");
        }
        // Set issue modifiedby
        if (topic.getModifiedBy() > 0) {
          context.setAttribute(ISSUE_MODIFIED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, topic.getModifiedBy())).getNameFirstLastInitial());
        } else {
          context.setAttribute(ISSUE_MODIFIED_BY_CONTACT, "");
        }
        // Set project
        Project project = new Project(db, topic.getProjectId());
        context.setAttribute(PROJECT, project);
        // Set issueCategory
        Forum forum = new Forum(db, topic.getCategoryId(), topic.getProjectId());
        context.setAttribute(ISSUE_CATEGORY, forum);
      } else {
        context.setAttribute(ISSUE, new Topic());
        LOG.error("Did not load issue record");
      }

      // Set enteredby
      if (thisReply.getEnteredBy() > 0) {
        context.setAttribute(ENTERED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, thisReply.getEnteredBy())).getNameFirstLastInitial());
      } else {
        context.setAttribute(ENTERED_BY_CONTACT, "");
      }
      // Set modifiedby
      if (thisReply.getModifiedBy() > 0) {
        context.setAttribute(MODIFIED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, thisReply.getModifiedBy())).getNameFirstLastInitial());
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