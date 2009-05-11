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
package com.concursive.connect.web.rss.cache;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.cache.CacheContext;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.activity.dao.ProjectHistory;
import com.concursive.connect.web.modules.activity.dao.ProjectHistoryList;
import com.concursive.connect.web.modules.blog.feed.BlogPostFeedEntry;
import com.concursive.connect.web.modules.discussion.feed.DiscussionTopicFeedEntry;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.feed.ProjectFeedEntry;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.reviews.feed.ProjectRatingFeedEntry;
import com.concursive.connect.web.rss.servlets.FeedServlet;
import com.concursive.connect.web.utils.PagedListInfo;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Provides public feeds
 *
 * @author Kailash Bhoopalam
 * @created October 3, 2008
 */
public class FeedCacheEntryFactory implements CacheEntryFactory {

  private static final Log LOG = LogFactory.getLog(FeedCacheEntryFactory.class);

  private CacheContext context = null;

  public FeedCacheEntryFactory(CacheContext context) {
    this.context = context;
  }

  public Object createEntry(Object key) throws Exception {
    if (key == null) {
      throw new Exception("FeedCacheEntryFactory-> Invalid key specified: null");
    }

    // Determine the feed, expect an optional category and a site purpose for lookup
    String[] items = key.toString().split("[|]");
    Connection db = null;
    String requestedFeedUrl = items[0];
    String serverUrl = items[1];
    String[] requestedFeedUrlPart = requestedFeedUrl.split("/rss.xml");
    String purpose = "";
    if (requestedFeedUrlPart.length > 1) {
      purpose = requestedFeedUrlPart[1].substring(1);
    }
    String category = requestedFeedUrlPart[0];

    // Load the RSS config file to determine the objects for display
    String fileName = "rss_en_US.xml";
    URL resource = FeedServlet.class.getResource("/" + fileName);
    LOG.debug("RSS config file: " + resource.toString());
    XMLUtils library = new XMLUtils(resource);

    try {
      // Determine if a site feed or a specific category feed
      Element feedElement;
      if (StringUtils.hasText(category)) {
        // Find the site specific category configuration
        feedElement = XMLUtils.getElement(library.getDocumentElement(), "feed", "url", "site," + purpose + ",category," + category);
        if (feedElement == null) {
          // Find the specific category configuration
          feedElement = XMLUtils.getElement(library.getDocumentElement(), "feed", "url", "category," + category);
          if (feedElement == null) {
            // Find the site specific generic category
            feedElement = XMLUtils.getElement(library.getDocumentElement(), "feed", "url", "site," + purpose + ",category");
            // If not found, use the generic category configuration
            if (feedElement == null) {
              feedElement = XMLUtils.getElement(library.getDocumentElement(), "feed", "url", "category");
            }
          }
        }
      } else {
        // Use the site specific configuration
        feedElement = XMLUtils.getElement(library.getDocumentElement(), "feed", "url", "site," + purpose);
        if (feedElement == null) {
          feedElement = XMLUtils.getElement(library.getDocumentElement(), "feed", "url", "site");
        }
      }

      // Process the feedElement
      if (feedElement != null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Feed found: " + feedElement.getAttribute("url"));
        }
        // Determine the maximum number of feed entries
        String limit = feedElement.getAttribute("limit");

        String useDefaultProfile = feedElement.getAttribute("useDefaultProfile");

        // Determine the types of entries to display in the feed based on the config file
        ArrayList<Element> entryElements = new ArrayList<Element>();
        XMLUtils.getAllChildren(feedElement, "entry", entryElements);

        // Create the Feed object to be returned
        SyndFeed feed = new SyndFeedImpl();
        ArrayList<SyndEntry> entries = new ArrayList<SyndEntry>();

        // Find feed items by querying the ProjectHistoryList
        ProjectHistoryList historyList = new ProjectHistoryList();
        PagedListInfo info = new PagedListInfo();
        info.setItemsPerPage(limit);
        historyList.setPagedListInfo(info);

        // Retrieve the database connection to use
        db = CacheUtils.getConnection(context);

        // Check to see if the main profile is to be used
        if (StringUtils.hasText(useDefaultProfile) && "true".equals(useDefaultProfile)) {
          Project project = ProjectUtils.loadProject(context.getApplicationPrefs().get("MAIN_PROFILE"));
          historyList.setProjectId(project.getId());
        }

        // set all the requested types based on the types we allow for the query..
        ArrayList<String> types = new ArrayList<String>();
        for (Element entryElement : entryElements) {
          String type = XMLUtils.getNodeText(entryElement);
          types.add(type);
        }
        historyList.setObjectPreferences(types);


        // If a specific category is requested, search for it's id
        if (StringUtils.hasText(category)) {
          int projectCategoryId = -1;
          ProjectCategoryList projectCategoryList = new ProjectCategoryList();
          projectCategoryList.setTopLevelOnly(true);
          projectCategoryList.setEnabled(true);
          projectCategoryList.setCategoryNameLowerCase(category.toLowerCase());
          projectCategoryList.buildList(db);
          if (projectCategoryList.size() >= 1) {
            projectCategoryId = projectCategoryList.get(0).getId();
          }
          historyList.setProjectCategoryId(projectCategoryId);
        }

        // Since this is a public feed, base the feed off of a guest user
        User guestUser = UserUtils.createGuestUser();
        historyList.setForUser(guestUser.getId());
        historyList.buildList(db);

        // For each history item, determine the feed content
        for (ProjectHistory item : historyList) {
          SyndEntry syndEntry = null;
          if (ProjectHistoryList.BLOG_OBJECT.equals(item.getLinkObject())) {
            syndEntry = BlogPostFeedEntry.getSyndEntry(db, item.getLinkItemId(), serverUrl);
            if (syndEntry != null) {
              entries.add(syndEntry);
            }
          } else if (ProjectHistoryList.RATING_OBJECT.equals(item.getLinkObject())) {
            syndEntry = ProjectRatingFeedEntry.getSyndEntry(db, item.getLinkItemId(), serverUrl);
            if (syndEntry != null) {
              entries.add(syndEntry);
            }
          } else if (ProjectHistoryList.TOPIC_OBJECT.equals(item.getLinkObject())) {
            syndEntry = DiscussionTopicFeedEntry.getSyndEntry(db, item.getLinkItemId(), serverUrl);
            if (syndEntry != null) {
              entries.add(syndEntry);
            }
          } else if (ProjectHistoryList.PROFILE_OBJECT.equals(item.getLinkObject())) {
            syndEntry = ProjectFeedEntry.getSyndEntry(db, item.getLinkItemId(), serverUrl);
            if (syndEntry != null) {
              entries.add(syndEntry);
            }
          }
        }
        feed.setEntries(entries);
        return feed;
      }
    } catch (Exception e) {
      throw new Exception(e);
    } finally {
      CacheUtils.freeConnection(context, db);
    }
    return null;
  }
}
