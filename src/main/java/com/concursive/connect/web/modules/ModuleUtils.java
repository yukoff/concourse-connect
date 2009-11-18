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
package com.concursive.connect.web.modules;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.blog.dao.BlogPostComment;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.classifieds.dao.Classified;
import com.concursive.connect.web.modules.common.social.comments.dao.Comment;
import com.concursive.connect.web.modules.discussion.dao.Reply;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.promotions.dao.Ad;
import com.concursive.connect.web.modules.reviews.dao.ProjectRating;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiComment;

/**
 * Utilties for working with modules
 *
 * @author matt rajkowski
 * @created February 27, 2009
 */
public class ModuleUtils {

  // Module Names
  public static final String MODULENAME_PROFILE = "profile";
  public static final String MODULENAME_PROFILE_IMAGE = "profileimage";
  public static final String MODULENAME_REVIEWS = "reviews";
  public static final String MODULENAME_BLOG = "blog";
  public static final String MODULENAME_NEWS = "news"; //another alias for blogs
  public static final String MODULENAME_BLOG_POST = "post";
  public static final String MODULENAME_BLOG_COMMENT = "blogcomment";
  public static final String MODULENAME_CALENDAR = "calendar";
  public static final String MODULENAME_CALENDAR_EVENT = "calendarevent";
  public static final String MODULENAME_WIKI = "wiki";
  public static final String MODULENAME_WIKI_COMMENT = "wikicomment";
  public static final String MODULENAME_DISCUSSION_TOPIC = "topic";
  public static final String MODULENAME_DISCUSSION_REPLY = "reply";
  public static final String MODULENAME_PROMOTIONS = "promotions";
  public static final String MODULENAME_ADS = "ads"; //another alias for promotions
  public static final String MODULENAME_CLASSIFIEDS = "classifieds";
  public static final String MODULENAME_LISTS = "lists";
  public static final String MODULENAME_TICKETS = "tickets";
  public static final String MODULENAME_BADGES = "badges";
  public static final String MODULENAME_MEMBERS = "members";
  public static final String MODULENAME_INBOX = "inbox";
  public static final String MODULENAME_DOCUMENTS = "documents";

  public static String getTableFromModuleName(String moduleName) {
    if (!StringUtils.hasText(moduleName)) {
      return null;
    } else if (MODULENAME_REVIEWS.equals(moduleName)) {
      return ProjectRating.TABLE;
    } else if (MODULENAME_ADS.equals(moduleName) || MODULENAME_PROMOTIONS.equals(moduleName)) {
      return Ad.TABLE;
    } else if (MODULENAME_CLASSIFIEDS.equals(moduleName)) {
      return Classified.TABLE;
    } else if (MODULENAME_WIKI.equals(moduleName)) {
      return Wiki.TABLE;
    } else if (MODULENAME_DISCUSSION_TOPIC.equals(moduleName)) {
      return Topic.TABLE;
    } else if (MODULENAME_NEWS.equals(moduleName) || MODULENAME_BLOG.equals(moduleName) || MODULENAME_BLOG_POST.equals(moduleName)) {
      return BlogPost.TABLE;
    } else if (MODULENAME_WIKI_COMMENT.equals(moduleName)) {
      return WikiComment.TABLE;
    } else if (MODULENAME_BLOG_COMMENT.equals(moduleName)) {
      return BlogPostComment.TABLE;
    } else if (MODULENAME_DOCUMENTS.equals(moduleName)) {
      return FileItem.TABLE;
    } else if (MODULENAME_CALENDAR_EVENT.equals(moduleName) || MODULENAME_CALENDAR.equals(moduleName)) {
      return Meeting.TABLE;
    } else if (MODULENAME_PROFILE_IMAGE.equals(moduleName)) {
      return FileItem.TABLE;
    } if (MODULENAME_DISCUSSION_REPLY.equals(moduleName)) {
      return Reply.TABLE;
    }
    return null;
  }

  public static String getPrimaryKeyFromModuleName(String moduleName) {
    if (!StringUtils.hasText(moduleName)) {
      return null;
    } else if (MODULENAME_REVIEWS.equals(moduleName)) {
      return ProjectRating.PRIMARY_KEY;
    } else if (MODULENAME_ADS.equals(moduleName) || MODULENAME_PROMOTIONS.equals(moduleName)) {
      return Ad.PRIMARY_KEY;
    } else if (MODULENAME_CLASSIFIEDS.equals(moduleName)) {
      return Classified.PRIMARY_KEY;
    } else if (MODULENAME_WIKI.equals(moduleName)) {
      return Wiki.PRIMARY_KEY;
    } else if (MODULENAME_DISCUSSION_TOPIC.equals(moduleName)) {
      return Topic.PRIMARY_KEY;
    } else if (MODULENAME_NEWS.equals(moduleName) || MODULENAME_BLOG.equals(moduleName) || MODULENAME_BLOG_POST.equals(moduleName)) {
      return BlogPost.PRIMARY_KEY;
    } else if (MODULENAME_WIKI_COMMENT.equals(moduleName)) {
      return Comment.PRIMARY_KEY;
    } else if (MODULENAME_BLOG_COMMENT.equals(moduleName)) {
      return Comment.PRIMARY_KEY;
    } else if (MODULENAME_DOCUMENTS.equals(moduleName)) {
      return FileItem.PRIMARY_KEY;
    } else if (MODULENAME_CALENDAR_EVENT.equals(moduleName) || MODULENAME_CALENDAR.equals(moduleName)) {
      return Meeting.PRIMARY_KEY;
    } else if (MODULENAME_PROFILE_IMAGE.equals(moduleName)) {
      return FileItem.PRIMARY_KEY;
    } else if (MODULENAME_DISCUSSION_REPLY.equals(moduleName)) {
      return Reply.PRIMARY_KEY;
    }

    return null;
  }

  public static int getLinkModuleIdFromModuleName(String moduleName) {
    int linkModuleId = -1;
    if (MODULENAME_CLASSIFIEDS.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_CLASSIFIEDS_FILES;
    } else if (MODULENAME_BLOG.equals(moduleName) || MODULENAME_NEWS.equals(moduleName) || MODULENAME_BLOG_POST.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_BLOG_FILES;
    } else if (MODULENAME_PROMOTIONS.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_AD_FILES;
    } else if (MODULENAME_PROFILE.equals(moduleName)) {
      linkModuleId = -1;
    } else if (MODULENAME_INBOX.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_MESSAGES_FILES;
    } else if (MODULENAME_WIKI.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_WIKI_FILES;
    } else if (MODULENAME_BADGES.equals(moduleName)) {
      linkModuleId = Constants.BADGE_FILES;
    } else if (MODULENAME_TICKETS.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_TICKET_FILES;
    } else if (MODULENAME_REVIEWS.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_REVIEW_FILES;
    } else if (MODULENAME_DISCUSSION_TOPIC.equals(moduleName)) {
      linkModuleId = Constants.DISCUSSION_FILES_TOPIC;
    } else if (MODULENAME_WIKI_COMMENT.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_WIKI_COMMENT_FILES;
    } else if (MODULENAME_BLOG_COMMENT.equals(moduleName)) {
      linkModuleId = Constants.BLOG_POST_COMMENT_FILES;
    } else if (MODULENAME_DOCUMENTS.equals(moduleName)) {
      linkModuleId = Constants.PROJECTS_FILES;
    } else if (MODULENAME_CALENDAR_EVENT.equals(moduleName) || MODULENAME_CALENDAR.equals(moduleName)) {
      linkModuleId = Constants.PROJECTS_CALENDAR_EVENT_FILES;
    } else if (MODULENAME_PROFILE_IMAGE.equals(moduleName)) {
      linkModuleId = Constants.PROJECT_IMAGE_FILES;
    } else if (MODULENAME_DISCUSSION_REPLY.equals(moduleName)) {
      linkModuleId = Constants.DISCUSSION_FILES_REPLY;
    }
    return linkModuleId;
  }

  /**
   * Return the label for the module (an promotion, classified or blog)
   * TODO: may be there is a better way to do this as this information is already in project-portal-config.xml. Also this is configurable in the settings
   *
   * @param linkModuleId
   * @return
   */
  public static String getItemLabel(int linkModuleId) {
    String itemLabel = null;
    if (linkModuleId != -1) {
      if (linkModuleId == Constants.PROJECT_CLASSIFIEDS_FILES) {
        itemLabel = "Classified Ad";
      } else if (linkModuleId == Constants.PROJECT_BLOG_FILES) {
        itemLabel = "Blog Post";
      } else if (linkModuleId == Constants.PROJECT_AD_FILES) {
        itemLabel = "Promotion";
      } else if (linkModuleId == Constants.PROJECT_REVIEW_FILES) {
        itemLabel = "Review";
      } else if (linkModuleId == Constants.PROJECT_WIKI_FILES) {
        itemLabel = "Wiki";
      } else if (linkModuleId == Constants.DISCUSSION_FILES_TOPIC) {
        itemLabel = "Topic";
      } else if (linkModuleId == Constants.PROJECT_WIKI_COMMENT_FILES) {
        itemLabel = "Wiki Comment";
      } else if (linkModuleId == Constants.BLOG_POST_COMMENT_FILES) {
        itemLabel = "Blog Comment";
      } else if (linkModuleId == Constants.PROJECTS_FILES) {
        itemLabel = "Document";
      } else if (linkModuleId == Constants.PROJECTS_CALENDAR_EVENT_FILES) {
        itemLabel = "Event";
      } else if (linkModuleId == Constants.PROJECT_IMAGE_FILES) {
        itemLabel = "Profile Images";
      } else if (linkModuleId == Constants.PROJECT_MESSAGES_FILES) {
        itemLabel = "Message";
      }
    }
    return itemLabel;
  }
}
