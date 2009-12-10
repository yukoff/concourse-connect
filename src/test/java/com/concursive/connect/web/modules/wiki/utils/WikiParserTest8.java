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
package com.concursive.connect.web.modules.wiki.utils;

import com.concursive.commons.db.AbstractConnectionPoolTest;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserTest8 extends AbstractConnectionPoolTest {

  protected final static String history =
      "[[|-1:profile||John Smith]] granted the [[|-1:badge|-1|Awesome]] badge to [[|-1:profile||A new group]]";

  public void testWikiToHtmlHistoryItem() throws Exception {
    // The user id
    int userId = -1;
    // Create a context based on the current user, and the application web context
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(userId, "http://127.0.0.1:8080/community");
    // Convert the wiki to html for this user
    String html = WikiToHTMLUtils.getHTML(wikiContext, db, history);
    // Test the output
    assertEquals("Wiki text mismatch",
        "<p>" +
            "<a class=\"wikiLink external\" href=\"http://127.0.0.1:8080/community/show/\">John Smith</a> " +
            "granted the <a class=\"wikiLink external\" href=\"http://127.0.0.1:8080/community/badge/-1\">Awesome</a> " +
            "badge to <a class=\"wikiLink external\" " +
            "href=\"http://127.0.0.1:8080/community/show/\">A new group</a></p>\n", html);
  }
}
