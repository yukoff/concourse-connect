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
 * Tests wiki parser
 *
 * @author matt rajkowski
 * @created January 29, 2010
 */
public class WikiParserTableListTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample1 =
      "<p><img src=\"/show/main-profile/wiki-image/Concursive+Front+Page.png\" alt=\"Concursive Front Page.png\" width=\"607\" height=\"241\" /></p>\n" +
          "<table class=\"wikiTable\">\n" +
          "<tbody>\n" +
          "<tr class=\"row1\">\n" +
          "<td><strong>What are your Pain Points?</strong><br />\n" +
          "<ul>\n" +
          "<li> Finding new ways to engage customers</li>\n" +
          "<li> Enabling internal communication &amp; collaboration</li>\n" +
          "<li> Providing better customer support at lower cost</li>\n" +
          "<li> Creating a themed online community</li>\n" +
          "</ul>\n" +
          "<a class=\"wikiLink external\" href=\"https://www.concursive.com/show/concourseconnect/wiki/Pain+Points\" target=\"_blank\">more</a>\n" +
          "</td>\n" +
          "<td><strong>What do you Want to Create?</strong><br />\n" +
          "<ul>\n" +
          "<li> A corporate social networking site</li>\n" +
          "<li> An internal collaboration network</li>\n" +
          "<li> A public customer support forum</li>\n" +
          "<li> A full-featured online community</li>\n" +
          "</ul>\n" +
          "<a class=\"wikiLink external\" href=\"https://www.concursive.com/show/concourseconnect/wiki/Sample+Sites\" target=\"_blank\">more</a>\n" +
          "</td>\n" +
          "<td><strong>How is Connect Different?</strong><br />\n" +
          "<ul>\n" +
          "<li> Open source version</li>\n" +
          "<li> Integrated CRM functionality</li>\n" +
          "<li> On-premise or on-demand deployment</li>\n" +
          "<li> Cloud enabled services and plugins</li>\n" +
          "</ul>\n" +
          "<a class=\"wikiLink external\" href=\"https://www.concursive.com/show/concourseconnect/wiki/How+is+Connect+Different\" target=\"_blank\">more</a>\n" +
          "</td>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>";

  protected final static String htmlSample2 = "<table class=\"wikiTable\">\n" +
      "<tbody>\n" +
      "<tr class=\"row1\">\n" +
      "<td>\n" +
      "<ul>\n" +
      "<li>Line 1</li>\n" +
      "<li>Line 2</li>\n" +
      "</ul>\n" +
      "</td>\n" +
      "<td>\n" +
      "<p>1</p><p>2</p>\n" +
      "</td>\n" +
      "</tr>\n" +
      "</tbody>\n" +
      "</table>";

  protected final static String htmlSample3 = "<table class=\"wikiTable\">\n" +
      "<tbody>\n" +
      "<tr class=\"row1\">\n" +
      "<td>\n" +
      "<ul>\n" +
      "<li> Line 1</li>\n" +
      "<li> Line 2</li>\n" +
      "<li> Line 3</li>\n" +
      "</ul>\n" +
      "</td>\n" +
      "</tr>\n" +
      "</tbody>\n" +
      "</table>\n" +
      "<table class=\"wikiTable\">\n" +
      "<tbody>\n" +
      "<tr class=\"row1\">\n" +
      "<td>\n" +
      "<p><strong>Some title</strong></p>\n" +
      "<ul>\n" +
      "<li> Line 1</li>\n" +
      "<li> Line 2</li>\n" +
      "</ul>\n" +
      "</td>\n" +
      "<td>\n" +
      "<p><strong>Some other title</strong></p>\n" +
      "<ul>\n" +
      "<li> A list here</li>\n" +
      "<li> And some more content</li>\n" +
      "</ul>\n" +
      "</td>\n" +
      "<td>\n" +
      "<p><strong>And one more</strong><br />And line 2</p>\n" +
      "</td>\n" +
      "</tr>\n" +
      "</tbody>\n" +
      "</table>";

  public void testHTMLTableImageToWiki() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample1, "", project.getId());
    assertEquals("" +
        "[[Image:Concursive Front Page.png]]\n" +
        "\n" +
        "|'''What are your Pain Points?'''\n" +
        "!* Finding new ways to engage customers\n" +
        "!* Enabling internal communication & collaboration\n" +
        "!* Providing better customer support at lower cost\n" +
        "!* Creating a themed online community\n" +
        "![[https://www.concursive.com/show/concourseconnect/wiki/Pain+Points more]]|'''What do you Want to Create?'''\n" +
        "!* A corporate social networking site\n" +
        "!* An internal collaboration network\n" +
        "!* A public customer support forum\n" +
        "!* A full-featured online community\n" +
        "![[https://www.concursive.com/show/concourseconnect/wiki/Sample+Sites more]]|'''How is Connect Different?'''\n" +
        "!* Open source version\n" +
        "!* Integrated CRM functionality\n" +
        "!* On-premise or on-demand deployment\n" +
        "!* Cloud enabled services and plugins\n" +
        "![[https://www.concursive.com/show/concourseconnect/wiki/How+is+Connect+Different more]]|\n" +
        "", wiki);

    String wikiSample2 =
        "[[Image:Concursive Front Page.png]]\n" +
            "\n" +
            "|'''What are your Pain Points?'''\n" +
            "!* Finding new ways to engage customers\n" +
            "!* Enabling internal communication & collaboration\n" +
            "!* Providing better customer support at lower cost\n" +
            "!* Creating a themed online community\n" +
            "!\n" +
            "![[https://www.concursive.com/show/concourseconnect/wiki/Pain+Points more]]|'''What do you Want to Create?'''\n" +
            "!* A corporate social networking site\n" +
            "!* An internal collaboration network\n" +
            "!* A public customer support forum\n" +
            "!* A full-featured online community\n" +
            "!\n" +
            "![[https://www.concursive.com/show/concourseconnect/wiki/Sample+Sites more]]|'''How is Connect Different?'''\n" +
            "!* Open source version\n" +
            "!* Integrated CRM functionality\n" +
            "!* On-premise or on-demand deployment\n" +
            "!* Cloud enabled services and plugins\n" +
            "!\n" +
            "![[https://www.concursive.com/show/concourseconnect/wiki/How+is+Connect+Different more]]|\n" +
            "";

    // Read it, re-write it, and compare
    Wiki thisWiki = new Wiki();
    thisWiki.setProjectId(project.getId());
    thisWiki.setContent(wiki);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    System.out.println(html);
    // When the parser reads and re-writes, the html is subtly different, but better
    assertEquals(wikiSample2, HTMLToWikiUtils.htmlToWiki(html, "", project.getId()));
  }
}
