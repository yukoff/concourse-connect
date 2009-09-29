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
package com.concursive.connect.web.modules.blog.portlets.main;

import com.concursive.connect.web.modules.reviews.portlets.main.SaveTagsAction;
import com.concursive.connect.web.modules.reviews.portlets.main.TagsFormViewer;
import com.concursive.connect.web.portal.AbstractPortletModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Project blog mvc portlet
 *
 * @author matt rajkowski
 * @created October 28, 2008
 */
public class BlogPortlet extends AbstractPortletModule {

  private static Log LOG = LogFactory.getLog(BlogPortlet.class);

  // Viewers
  public static final String DEFAULT_VIEW = "list";
  public static final String LIST_VIEW = "list";
  public static final String DETAILS_VIEW = "details";
  public static final String FORM_VIEW = "form";
  public static final String SET_RATING_AJAX_VIEW = "setRating";
  public static final String SET_COMMENT_RATING_AJAX_VIEW = "comment-setRating";
  public static final String COMMENT_DELETE_VIEW = "comment-delete";
  public static final String TAGS_FORM_VIEW = "setTags";

  // Actions
  public static final String SAVE_FORM_ACTION = "saveForm";
  public static final String SAVE_COMMENTS_ACTION = "saveComments";
  public static final String DELETE_ACTION = "delete";
  public static final String CLONE_ACTION = "clone";
  public static final String SAVE_TAGS_ACTION = "saveTags";

  public BlogPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }

  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(LIST_VIEW, new BlogListViewer());
    viewers.put(DETAILS_VIEW, new BlogDetailsViewer());
    viewers.put(FORM_VIEW, new BlogFormViewer());
    viewers.put(SET_RATING_AJAX_VIEW, new BlogSetRatingViewer());
    viewers.put(SET_COMMENT_RATING_AJAX_VIEW, new BlogCommentSetRatingViewer());
    viewers.put(COMMENT_DELETE_VIEW, new BlogDeleteCommentViewer());
    viewers.put(TAGS_FORM_VIEW, new TagsFormViewer());

    // Actions
    actions.put(SAVE_FORM_ACTION, new SaveBlogAction());
    actions.put(DELETE_ACTION, new DeleteBlogAction());
    actions.put(CLONE_ACTION, new CloneBlogAction());
    actions.put(SAVE_COMMENTS_ACTION, new SaveBlogCommentsAction());
    actions.put(SAVE_TAGS_ACTION, new SaveTagsAction());
  }
}
