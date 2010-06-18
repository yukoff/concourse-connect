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
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.Wiki;

import java.util.HashMap;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserTest7 extends AbstractConnectionPoolTest {

  protected final static String wikiSample =
      "[[Subversion Details]]\n" +
          "[[API examples]]\n" +
          "[[Backup and Restore]]\n" +
          "[[UI Configurability]]\n" +
          "Look at the [[|:calendar|]]\n" +
          "Look at the [[|9999999:calendar|]]\n" +
          "[[|9999999:calendar||See our calendar]]\n" +
          "\n";

  public void testWikiToHtmlLinks() throws Exception {
    // Stage a project and ticket for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_TICKET_ID_CACHE, "9999999-1", 200);
    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertEquals(
        "<p><a class=\"wikiLink newWiki\" href=\"/show//wiki/Subversion+Details\">Subversion Details</a><br />" +
            "<a class=\"wikiLink newWiki\" href=\"/show//wiki/API+examples\">API examples</a><br />" +
            "<a class=\"wikiLink newWiki\" href=\"/show//wiki/Backup+and+Restore\">Backup and Restore</a><br />" +
            "<a class=\"wikiLink newWiki\" href=\"/show//wiki/UI+Configurability\">UI Configurability</a><br />" +
            "Look at the <a class=\"wikiLink external\" href=\"/show//calendar\">calendar</a><br />" +
            "Look at the <a class=\"wikiLink denied\" href=\"#\" onmouseover=\"window.status='\\/show\\/some-project\\/calendar;'\">calendar</a><br />" +
            "<a class=\"wikiLink denied\" href=\"#\" onmouseover=\"window.status='\\/show\\/some-project\\/calendar;'\">See our calendar</a></p>" +
            "\n", html);
  }

  protected final static String htmlSample =
      "<p><a class=\"wikiLink\" title=\"Subversion Details\" href=\"/show/some-project/wiki/Subversion+Details\">Subversion Details</a><br />" +
          "<a class=\"wikiLink\" title=\"API examples\" href=\"/show/some-project/wiki/API+examples\">API examples</a><br />" +
          "<a class=\"wikiLink\" title=\"UI Configurability\" href=\"/show/some-project/wiki/UI+Configurability\">UI Configurability</a></p>\n" +
          "<p><a href=\"/modify/some-project/wiki/New+Page\">New Page</a></p>\n" +
          "<p><a href=\"/show/some-project/issue/1\">Some ticket</a></p>";

  public void testHtmlToWikiLinks() throws Exception {
    // Stage a project and ticket for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_TICKET_ID_CACHE, "9999999-1", 200);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "", project.getId());
    assertEquals("" +
        "[[Subversion Details]]\n" +
        "[[API examples]]\n" +
        "[[UI Configurability]]\n" +
        "\n" +
        "[[New Page]]\n" +
        "\n" +
        "[[|9999999:issue|1|Some ticket]]\n", wiki);
  }
}