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
package com.concursive.connect.web.modules.discussion.portlets.main;

import com.concursive.connect.web.portal.AbstractPortletModule;

/**
 * Project Discussion mvc portlet
 *
 * @author matt rajkowski
 * @created November 7, 2008
 */
public class DiscussionPortlet extends AbstractPortletModule {

  // Viewers
  public static final String DEFAULT_VIEW = "forums-list";

  public static final String FORUMS_LIST_VIEW = "forum-list";
  public static final String FORUMS_FORM_VIEW = "forum-form";

  public static final String TOPICS_LIST_VIEW = "topic-list";
  public static final String TOPICS_FORM_VIEW = "topic-form";

  public static final String REPLIES_LIST_VIEW = "reply-list";
  public static final String REPLIES_FORM_VIEW = "reply-form";
  public static final String REPLIES_SET_INAPPROPRIATE_AJAX_VIEW = "reply-setInappropriate";
  public static final String SET_RATING_AJAX_VIEW = "topic-setRating";

  // Actions
  public static final String SAVE_FORUM_FORM_ACTION = "forum-saveForm";
  public static final String DELETE_FORUM_ACTION = "forum-delete";

  public static final String SAVE_TOPIC_FORM_ACTION = "topic-saveForm";
  public static final String DELETE_TOPIC_ACTION = "topic-delete";

  public static final String SAVE_REPLY_FORM_ACTION = "reply-saveForm";
  public static final String DELETE_REPLY_ACTION = "reply-delete";

  public DiscussionPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }

  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(FORUMS_LIST_VIEW, new ForumListViewer());
    viewers.put(FORUMS_FORM_VIEW, new ForumFormViewer());
    viewers.put(TOPICS_LIST_VIEW, new TopicListViewer());
    viewers.put(TOPICS_FORM_VIEW, new TopicFormViewer());
    viewers.put(REPLIES_LIST_VIEW, new ReplyListViewer());
    viewers.put(REPLIES_FORM_VIEW, new ReplyFormViewer());
    viewers.put(REPLIES_SET_INAPPROPRIATE_AJAX_VIEW, new ReplySetInappropriateViewer());
    viewers.put(SET_RATING_AJAX_VIEW, new TopicSetInappropriateViewer());

    // Actions
    actions.put(SAVE_FORUM_FORM_ACTION, new SaveForumAction());
    actions.put(DELETE_FORUM_ACTION, new DeleteForumAction());
    actions.put(SAVE_TOPIC_FORM_ACTION, new SaveTopicAction());
    actions.put(DELETE_TOPIC_ACTION, new DeleteTopicAction());
    actions.put(SAVE_REPLY_FORM_ACTION, new SaveReplyAction());
    actions.put(DELETE_REPLY_ACTION, new DeleteReplyAction());
  }
}