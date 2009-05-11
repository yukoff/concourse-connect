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

package com.concursive.connect.web.modules.discussion.utils;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.Reply;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class to manipulate discussion objects
 *
 * @author matt rajkowski
 * @created November 13, 2008
 */
public class DiscussionUtils {

  public static void updateTopicResponse(Connection db, Reply reply) throws SQLException {
    // The topic will be updated to reflect any solution
    Topic topic = new Topic(db, reply.getIssueId());
    // Set values based on how a reply has been rated
    if (topic.getQuestion() && reply.getReplyToId() > -1) {
      // Determine changes to the original
      Reply replyBeingRated = new Reply(db, reply.getReplyToId());
      replyBeingRated.setProjectId(reply.getProjectId());
      if (reply.getAnswered() == Reply.ANSWERED) {
        // The user states that the referenced reply answered the question
        topic.setSolutionReplyId(reply.getReplyToId());
        replyBeingRated.setHelpful(false);
        replyBeingRated.setSolution(true);
      } else if (reply.getAnswered() == Reply.HELPFUL) {
        // The user states that the referenced reply was helpful
        if (topic.getSolutionReplyId() == reply.getReplyToId()) {
          topic.setSolutionReplyId(-1);
        }
        replyBeingRated.setHelpful(true);
        replyBeingRated.setSolution(false);
      } else if (reply.getAnswered() == Reply.NOT_ANSWERED) {
        // The user states that the referenced reply did not answer the question
        // and was not helpful
        if (topic.getSolutionReplyId() == replyBeingRated.getId()) {
          topic.setSolutionReplyId(-1);
        }
        replyBeingRated.setHelpful(false);
        replyBeingRated.setSolution(false);
      } else if (reply.getAnswered() == Reply.ANSWER_NOT_REQUIRED) {
        // The user states that the question is no longer a question
        topic.setSolutionReplyId(-1);
        topic.setQuestion(false);
        replyBeingRated.setHelpful(false);
        replyBeingRated.setSolution(false);
      }
      // Update the original issue reply record
      replyBeingRated.updateSolutionForTopicAndReply(db, topic.getQuestion());
      // Update the rating tables
      if (replyBeingRated.getHelpful() || replyBeingRated.getSolution()) {
        Rating.save(db, reply.getEnteredBy(), topic.getProjectId(), replyBeingRated.getId(), "1", Reply.TABLE, Reply.PRIMARY_KEY, Constants.FALSE);
      } else {
        Rating.save(db, reply.getEnteredBy(), topic.getProjectId(), replyBeingRated.getId(), "0", Reply.TABLE, Reply.PRIMARY_KEY, Constants.FALSE);
      }
    }
  }
}
