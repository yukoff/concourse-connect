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
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.Reply;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiLink;

import java.sql.Connection;

/**
 * Records a project forum topic response event
 *
 * @author Ananth
 * @created Feb 19, 2009
 */
public class SaveForumTopicResponseEvent extends ObjectHookComponent implements ComponentInterface {
  public final static String HISTORY_POST_REPLY_TEXT = "history.postreply.text";
  public final static String HISTORY_POST_ANSWER_TEXT = "history.postanswer.text";

  public final static String HISTORY_MARK_ANSWER_TEXT = "history.markanswer.text";
  public final static String HISTORY_MARK_ANSWER_USER_TEXT = "history.markanswer.user-profile.text";

  public String getDescription() {
    return "Records a project forum topic entry event";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    Connection db = null;

    try {
      db = getConnection(context);

      Reply thisReply = (Reply) context.getThisObject();
      Reply prevReply = (Reply) context.getPreviousObject();

      //load the topic
      Topic topic = new Topic(db, thisReply.getIssueId(), thisReply.getProjectId());
      Project profileProject = ProjectUtils.loadProject(thisReply.getProjectId());

      //load the user that submitted the forum topic response
      User entered = UserUtils.loadUser(thisReply.getEnteredBy());
      Project enteredProfile = ProjectUtils.loadProject(entered.getProfileProjectId());

      // The user answered a question
      if (topic.getQuestion()) {

        // Check to see if the owner is marking a question as answered
        if (thisReply.getAnswered() == Reply.ANSWERED) {
          // The owner marked an answer as correct

          //load the user that posted the answer
          Reply replyBeingRated = new Reply(db, thisReply.getReplyToId());

          User answeredBy = UserUtils.loadUser(replyBeingRated.getEnteredBy());
          Project answeredByProfile = ProjectUtils.loadProject(answeredBy.getProfileProjectId());

          // Prepare the wiki links
          context.setParameter("user", WikiLink.generateLink(enteredProfile));
          context.setParameter("answeredby", WikiLink.generateLink(answeredByProfile));
          context.setParameter("topic", WikiLink.generateLink(topic));
          context.setParameter("profile", WikiLink.generateLink(profileProject));

          // Insert the history
          ProjectHistory history = new ProjectHistory();
          history.setEnteredBy(entered.getId());
          history.setProjectId(thisReply.getProjectId());
          history.setLinkObject(ProjectHistoryList.REPLY_OBJECT);
          history.setEventType(ProjectHistoryList.MARK_FORUM_TOPIC_RESPONSE_EVENT);
          history.setLinkItemId(thisReply.getId());
          if (entered.getId() == answeredBy.getId()) {
            // User answered own question
            history.setDescription(context.getParameter(HISTORY_MARK_ANSWER_USER_TEXT));
          } else {
            // Another user answered the question
            history.setDescription(context.getParameter(HISTORY_MARK_ANSWER_TEXT));
          }
          history.insert(db);
        } else {
          // Prepare the wiki links
          context.setParameter("user", WikiLink.generateLink(enteredProfile));
          context.setParameter("topic", WikiLink.generateLink(topic));
          context.setParameter("profile", WikiLink.generateLink(profileProject));

          // Insert the history
          ProjectHistory history = new ProjectHistory();
          history.setEnteredBy(entered.getId());
          history.setProjectId(thisReply.getProjectId());
          history.setLinkObject(ProjectHistoryList.REPLY_OBJECT);
          history.setEventType(ProjectHistoryList.POST_FORUM_TOPIC_RESPONSE_EVENT);
          history.setLinkItemId(thisReply.getId());
          history.setDescription(context.getParameter(HISTORY_POST_ANSWER_TEXT));
          history.insert(db);
        }
      } else {
        // This is just a reply to a topic that isn't a question

        // Prepare the wiki links
        context.setParameter("user", WikiLink.generateLink(enteredProfile));
        context.setParameter("topic", WikiLink.generateLink(topic));
        context.setParameter("profile", WikiLink.generateLink(profileProject));

        // Insert the history
        ProjectHistory history = new ProjectHistory();
        history.setEnteredBy(entered.getId());
        history.setProjectId(thisReply.getProjectId());
        history.setLinkObject(ProjectHistoryList.REPLY_OBJECT);
        history.setEventType(ProjectHistoryList.POST_FORUM_TOPIC_RESPONSE_EVENT);
        history.setLinkItemId(thisReply.getId());
        history.setDescription(context.getParameter(HISTORY_POST_REPLY_TEXT));
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
