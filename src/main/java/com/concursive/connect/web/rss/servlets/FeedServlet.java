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
package com.concursive.connect.web.rss.servlets;

import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.discussion.dao.TopicList;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.rss.utils.FeedUtils;
import com.concursive.connect.web.utils.PagedListInfo;
import com.concursive.connect.web.webdav.WebdavManager;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Sep 9, 2005
 */
public class FeedServlet extends HttpServlet {

  private static final Log LOG = LogFactory.getLog(FeedServlet.class);
  private static final String MIME_TYPE = "application/xml; charset=UTF-8";
  private static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";

  public void init() {
  }

  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Connection db = null;
    try {

      response.setContentType(MIME_TYPE);

      ApplicationPrefs prefs = (ApplicationPrefs) getServletContext().getAttribute("applicationPrefs");
      String url = "http://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), request);
      List entries = new ArrayList();
      SyndEntry entry;
      SyndContent description;

      // Everything is ready, now check the username/password
      db = getConnection(getServletContext());
      java.util.Date publishDate = null;

      // Can pull from a cache or rebuild the news list...

      String path = request.getRequestURI();

      // Generate the RSS and replace the cache for all public projects
      SyndFeed feed = new SyndFeedImpl();
      if (path.endsWith("/rss.xml")) {
        // Use the purpose of the site for determining what is included in the feed
        String purpose = prefs.get(ApplicationPrefs.PURPOSE);
        // Check to see which public feed is requested
        if (path.endsWith("/feed/rss.xml")) {
          // This is the main website feed
          feed = FeedUtils.loadFeed("/rss.xml," + purpose, url);
          feed.setTitle(prefs.get("TITLE"));
          feed.setDescription(prefs.get("TITLE") + " feed");
        } else {
          // Check to see which website category feed is requested
          ProjectCategoryList projectCategoryList = new ProjectCategoryList();
          projectCategoryList.setTopLevelOnly(true);
          projectCategoryList.setEnabled(true);
          projectCategoryList.setSensitive(Constants.FALSE);
          projectCategoryList.buildList(db);
          for (ProjectCategory projectCategory : projectCategoryList) {
            String normalizedCategoryName = projectCategory.getNormalizedCategoryName();
            if (path.indexOf(normalizedCategoryName) != -1) {
              String requestedFeed = path.substring(path.indexOf("/feed/") + "/feed/".length(), path.indexOf(".xml")) + ".xml";
              LOG.debug("Requested feed: " + requestedFeed);
              feed = FeedUtils.loadFeed(requestedFeed + "," + purpose, url);
              feed.setTitle(prefs.get("TITLE") + " - " + projectCategory.getDescription());
              feed.setDescription(prefs.get("TITLE") + " - " + projectCategory.getDescription() + " feed");
              break;
            }
          }
        }
        // No feed was found
        if (feed == null) {
          return;
        }
      } else {
        boolean authenticated = WebdavManager.checkAuthentication(request);
        if (!authenticated) {
          WebdavManager.askForAuthentication(response);
          return;
        }
        int userId = WebdavManager.validateUser(db, request);
        if (userId == -1) {
          WebdavManager.askForAuthentication(response);
          return;
        }

        if (path.endsWith("/news.xml") || path.endsWith("/blog.xml")) {
          feed.setTitle(prefs.get("TITLE") + " - Blog Rollup");
          feed.setDescription(prefs.get("TITLE") + " personalized blog rollup");
          BlogPostList newsList = new BlogPostList();
          newsList.setLastNews(5);
          newsList.setForUser(userId);
          //Calendar cal = Calendar.getInstance();
          // 14 Days
          //cal.add(Calendar.DAY_OF_MONTH, -30);
          //Timestamp alertRangeStart = new Timestamp(cal.getTimeInMillis());
          //newsList.setAlertRangeStart(alertRangeStart);
          newsList.setCurrentNews(Constants.TRUE);
          newsList.buildList(db);
          Iterator i = newsList.iterator();
          while (i.hasNext()) {
            BlogPost thisArticle = (BlogPost) i.next();
            Project thisProject = ProjectUtils.loadProject(thisArticle.getProjectId());
            entry = new SyndEntryImpl();
            entry.setTitle(thisArticle.getSubject());
            entry.setPublishedDate(thisArticle.getStartDate());
            if (publishDate == null || thisArticle.getStartDate().after(publishDate)) {
              publishDate = thisArticle.getStartDate();
            }
            entry.setAuthor(UserUtils.getUserName(thisArticle.getEnteredBy()));
            description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(thisArticle.getIntro());
            entry.setLink(url + "/show/" + thisProject.getUniqueId() + "/post/" + thisArticle.getId());
            entry.setDescription(description);
            entries.add(entry);
          }
        }

        if (path.endsWith("/discussion.xml")) {
          feed.setTitle(prefs.get("TITLE") + " - Discussion Rollup");
          feed.setDescription(prefs.get("TITLE") + " personalized discussion rollup");
          TopicList topicList = new TopicList();
          topicList.setForUser(userId);
          PagedListInfo pagedList = new PagedListInfo();
          pagedList.setColumnToSortBy("i.last_reply_date");
          pagedList.setSortOrder("desc");
          pagedList.setItemsPerPage(10);
          topicList.setPagedListInfo(pagedList);
          topicList.buildList(db);
          Iterator i = topicList.iterator();
          while (i.hasNext()) {
            Topic thisTopic = (Topic) i.next();
            Project thisProject = ProjectUtils.loadProject(thisTopic.getProjectId());
            entry = new SyndEntryImpl();
            entry.setTitle(thisTopic.getSubject());
            entry.setPublishedDate(thisTopic.getReplyDate());
            if (publishDate == null || thisTopic.getReplyDate().after(publishDate)) {
              publishDate = thisTopic.getReplyDate();
            }
            if (thisTopic.getReplyBy() > -1) {
              entry.setAuthor(UserUtils.getUserName(thisTopic.getReplyBy()));
            } else if (thisTopic.getReplyBy() == -1) {
              entry.setAuthor(UserUtils.getUserName(thisTopic.getEnteredBy()));
            }
            description = new SyndContentImpl();
            description.setType("text/html");
            if (thisTopic.getReplyBy() == -1) {
              // This is the first posting
              description.setValue(StringUtils.toHtml(thisTopic.getBody()));
            } else {
              // This is a reply
              // TODO: Load the reply
              description.setValue(StringUtils.toHtml("A reply has been posted."));
            }
            entry.setLink(url + "/show/" + thisProject.getUniqueId() + "/topic/" + thisTopic.getId() + "?resetList=true&modified=" + thisTopic.getReplyDate().getTime());
            entry.setDescription(description);
            entries.add(entry);
          }
        }

        if (path.endsWith("/documents.xml")) {
          feed.setTitle(prefs.get("TITLE") + " - Documents Rollup");
          feed.setDescription(prefs.get("TITLE") + " personalized document rollup");
          FileItemList fileItemList = new FileItemList();
          fileItemList.setLinkModuleId(Constants.PROJECTS_FILES);
          fileItemList.setForProjectUser(userId);
          PagedListInfo pagedList = new PagedListInfo();
          pagedList.setColumnToSortBy("f.modified");
          pagedList.setSortOrder("desc");
          pagedList.setItemsPerPage(10);
          fileItemList.setPagedListInfo(pagedList);
          fileItemList.buildList(db);
          Iterator i = fileItemList.iterator();
          while (i.hasNext()) {
            FileItem fileItem = (FileItem) i.next();
            entry = new SyndEntryImpl();
            entry.setTitle(fileItem.getSubject());
            entry.setPublishedDate(fileItem.getModified());
            if (publishDate == null || fileItem.getModified().after(publishDate)) {
              publishDate = fileItem.getModified();
            }
            entry.setAuthor(UserUtils.getUserName(fileItem.getModifiedBy()));
            description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue(
                "<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\">\n" +
                "          <tr>\n" +
                "            <td valign=\"top\">\n" +
                "              " + fileItem.getImageTag("-23", url) + "\n" +
                "            </td>\n" +
                "            <td>\n" +
                "              " + StringUtils.toHtml(fileItem.getClientFilename()) + " " + fileItem.getVersion() + ", " + fileItem.getRelativeSize() + "k\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>");
            entry.setLink(url + "/ProjectManagementFiles.do?command=Details&pid=" + fileItem.getLinkItemId() + "&fid=" + fileItem.getId() + "&folderId=" + fileItem.getFolderId());
            entry.setDescription(description);
            entries.add(entry);
          }
        }

        if (path.endsWith("/wiki.xml")) {
          feed.setTitle(prefs.get("TITLE") + " - Wiki Rollup");
          feed.setDescription(prefs.get("TITLE") + " personalized wiki entry rollup");
          WikiList wikiList = new WikiList();
          wikiList.setForUser(userId);
          PagedListInfo pagedList = new PagedListInfo();
          pagedList.setColumnToSortBy("w.modified");
          pagedList.setSortOrder("desc");
          pagedList.setItemsPerPage(10);
          wikiList.setPagedListInfo(pagedList);
          wikiList.buildList(db);
          Iterator i = wikiList.iterator();
          while (i.hasNext()) {
            Wiki thisWiki = (Wiki) i.next();
            entry = new SyndEntryImpl();
            Project thisProject = ProjectUtils.loadProject(thisWiki.getProjectId());
            if (thisWiki.getSubjectLink() == null || "".equals(thisWiki.getSubjectLink().trim())) {
              entry.setTitle(thisProject.getTitle() + ": Home");
            } else {
              entry.setTitle(thisWiki.getSubject());
            }
            entry.setPublishedDate(thisWiki.getModified());
            if (publishDate == null || thisWiki.getModified().after(publishDate)) {
              publishDate = thisWiki.getModified();
            }
            entry.setAuthor(UserUtils.getUserName(thisWiki.getModifiedBy()));
            description = new SyndContentImpl();
            description.setType("text/html");
            entry.setLink(url + "/show/" + thisProject.getUniqueId() + "/wiki" + (StringUtils.hasText(thisWiki.getSubjectLink()) ? "/" + thisWiki.getSubjectLink() : "") + "?modified=" + thisWiki.getModified().getTime());
            entry.setDescription(description);
            entries.add(entry);
          }
        }

        // Prepare the feed classes before using the database connection...
        //rss_0.90, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0 rss_2.0 or atom_0.3
        if (publishDate == null) {
          publishDate = new java.util.Date();
        }
        feed.setEntries(entries);
      }
      // Output the feed
      SyndFeedOutput output = new SyndFeedOutput();
      feed.setFeedType("rss_2.0");
      feed.setPublishedDate(publishDate);
      feed.setLink(url);
      output.output(feed, response.getWriter());

    } catch (Exception ex) {
      String msg = COULD_NOT_GENERATE_FEED_ERROR;
      log(msg, ex);
      ex.printStackTrace(System.out);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    } finally {
      freeConnection(db, getServletContext());
    }
  }

  protected static ConnectionElement getConnectionElement(ServletContext context) {
    ApplicationPrefs prefs = (ApplicationPrefs) context.getAttribute("applicationPrefs");
    ConnectionElement ce = new ConnectionElement();
    ce.setDriver(prefs.get("SITE.DRIVER"));
    ce.setUrl(prefs.get("SITE.URL"));
    ce.setUsername(prefs.get("SITE.USER"));
    ce.setPassword(prefs.get("SITE.PASSWORD"));
    return ce;
  }

  protected static Connection getConnection(ServletContext context) throws SQLException {
    ConnectionElement ce = getConnectionElement(context);
    ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute("ConnectionPoolRSS");
    return sqlDriver.getConnection(ce, false);
  }

  protected static void freeConnection(Connection db, ServletContext context) {
    if (db != null) {
      ConnectionPool sqlDriver = (ConnectionPool) context.getAttribute("ConnectionPoolRSS");
      sqlDriver.free(db);
    }
    db = null;
  }
}
