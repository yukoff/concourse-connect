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
package com.concursive.connect.web.rss.portlets;

import com.concursive.connect.web.portal.IPortletViewer;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.net.URL;

/**
 * RSS feed viewer
 *
 * @author matt rajkowski
 * @created July 2, 2009
 */
public class RssListViewer implements IPortletViewer {

  private static Log LOG = LogFactory.getLog(RssListViewer.class);

  // Pages
  private static final String VIEW_PAGE = "/portlets/rss/rss_feed-view.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_FEED = "feed";
  private static final String PREF_SHOW_DESCRIPTION = "showDescription";
  private static final String PREF_LIMIT = "limit";

  // Object Results
  private static final String TITLE = "title";
  private static final String RSS_FEED = "rssFeed";
  private static final String SHOW_DESCRIPTION = "showDescription";
  private static final String LIMIT = "limit";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // General display preferences
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Review"));
    request.setAttribute(SHOW_DESCRIPTION, request.getPreferences().getValue(PREF_SHOW_DESCRIPTION, "true"));
    request.setAttribute(LIMIT, String.valueOf(Integer.parseInt(request.getPreferences().getValue(PREF_LIMIT, "10"))-1));

    // Determine the feed
    String feedValue = request.getPreferences().getValue(PREF_FEED, null);
    if (feedValue == null) {
      return null;
    }

    // Process the feed
    URL feedUrl = new URL(feedValue);
    SyndFeedInput input = new SyndFeedInput();
    SyndFeed feed = input.build(new XmlReader(feedUrl));
    request.setAttribute(RSS_FEED, feed);

    if (LOG.isDebugEnabled()) {
      LOG.debug(feed);
    }

    // Skip displaying if there are no entries
    if (feed.getEntries().size() == 0) {
      return null;
    }

    // JSP view
    return defaultView;
  }
}