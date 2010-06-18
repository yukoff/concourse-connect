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
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;

import java.util.HashMap;

/**
 * Tests common project database access
 *
 * @author matt rajkowski
 * @created February 7, 2010
 */
public class WikiParserInternalLinksTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample1 =
      "<h2 id=\"Products_Overview\">Products Overview</h2>\n" +
          "<p><a href=\"/show/another-profile\">Project 2</a>\n" +
          "<p>Text link to <a class=\"wikiLink external\" href=\"http://www.cnn.com\" target=\"_blank\">cnn</a> and internal <a class=\"wikiLink newWiki\" href=\"/modify/main-profile/wiki/products\">products</a> wiki.</p>\n" +
          "<table class=\"wikiTable\">\n" +
          "<tbody>\n" +
          "<tr>\n" +
          "<th style=\"width: 50%;\">\n" +
          "<p><a href=\"http://www.cnn.com\" target=\"_blank\"><img src=\"/show/main-profile/wiki-image/Concourse-connect-logo200-by-27.png\" alt=\"Concourse-connect-logo200-by-27.png\" width=\"200\" height=\"27\" /></a></p>\n" +
          "</th><th style=\"width: 50%;\">\n" +
          "<p><a href=\"/modify/main-profile/wiki/products\"><img src=\"/show/main-profile/wiki-image/Concourse-suite-logos-sized-for-products-page.png\" alt=\"Concourse-suite-logos-sized-for-products-page.png\" width=\"200\" height=\"27\" /></a></p>\n" +
          "</th>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>";

  protected final static String wikiSample1 =
      "" +
        "== Products Overview ==\n" +
        "\n" +
        "[[|9999998:profile|another-profile|Project 2]]\n" +
          "\n" +
        "Text link to [[http://www.cnn.com cnn]] and internal [[products]] wiki.\n" +
        "\n" +
        "||{width: 50%}||{width: 50%}||\n" +
        "||[[Image:Concourse-connect-logo200-by-27.png|link=http://www.cnn.com]]||[[Image:Concourse-suite-logos-sized-for-products-page.png|link=products]]||\n";

  public void testHTMLLinksToWikiLinks() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("main-profile");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, project.getId(), project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "main-profile", project.getId());
    // Stage a 2nd project for the cache
    Project project2 = new Project();
    project2.setId(9999998);
    project2.setTitle("Another Project");
    project2.setUniqueId("another-profile");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, project2.getId(), project2);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "another-profile", project2.getId());
    // Parse it
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample1, "", project.getId());
    assertEquals(wikiSample1, wiki);
  }

  public void testWikiLinksToHtmlLinks() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("main-profile");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, project.getId(), project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "main-profile", project.getId());
    // Stage a 2nd project for the cache
    Project project2 = new Project();
    project2.setId(9999998);
    project2.setTitle("Another Project");
    project2.setUniqueId("another-profile");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, project2.getId(), project2);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "another-profile", project2.getId());

    assertTrue("Cached project was not found", (ProjectUtils.loadProject(project.getId())).getId() == project.getId());

    Wiki wiki = new Wiki();
    wiki.setProjectId(project.getId());
    wiki.setContent(wikiSample1);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertEquals("<h2 id=\"Products_Overview\">Products Overview</h2>\n" +
        "<p><a class=\"wikiLink denied\" href=\"#\" onmouseover=\"window.status='\\/show\\/another-profile;'\">Project 2</a></p>\n" +
        "<p>Text link to <a class=\"wikiLink external\" target=\"_blank\" href=\"http://www.cnn.com\">cnn</a> and internal <a class=\"wikiLink newWiki\" href=\"/show/main-profile/wiki/products\">products</a> wiki.</p>\n" +
        "<table class=\"wikiTable\">\n" +
        "<tr><th style=\"width: 50%;\"><p><a href=\"http://www.cnn.com\" target=\"_blank\" alt=\"http://www.cnn.com\"><img src=\"/show/main-profile/wiki-image/Concourse-connect-logo200-by-27.png\"  alt=\"Concourse-connect-logo200-by-27.png\" /></a></p></th><th style=\"width: 50%;\"><p><a href=\"/show/main-profile/wiki/products\" alt=\"products\"><img src=\"/show/main-profile/wiki-image/Concourse-suite-logos-sized-for-products-page.png\"  alt=\"Concourse-suite-logos-sized-for-products-page.png\" /></a></p></th></tr>\n" +
        "</table>\n", html);
  }
}