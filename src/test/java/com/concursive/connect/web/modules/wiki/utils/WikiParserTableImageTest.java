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
public class WikiParserTableImageTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample1 =
      "<table border=\"0\">\n" +
          "<tbody>\n" +
          "<tr>\n" +
          "<th>Site</th>\n" +
          "<th>Description</th>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td><img src=\"/show/some-project/wiki-image/tzvids.arizona.stimulus.gi.jpg\" alt=\"tzvids.arizona.stimulus.gi.jpg\" width=\"120\" height=\"68\" /></td>\n" +
          "<td>" +
          "<p>This is the first picture</p>" +
          "<p>(nice)</p>" +
          "</td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td><img src=\"/show/some-project/wiki-image/tzvids.haiti.aid.afp.gi.jpg\" alt=\"tzvids.haiti.aid.afp.gi.jpg\" width=\"120\" height=\"68\" /></td>\n" +
          "<td>This is the second picture</td>\n" +
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
        "||Site||Description||\n" +
        "|[[Image:tzvids.arizona.stimulus.gi.jpg]]|This is the first picture\n" +
        "!\n" +
        "!(nice)|\n" +
        "|[[Image:tzvids.haiti.aid.afp.gi.jpg]]|This is the second picture|\n" +
        "", wiki);

    // Read it, re-write it, and compare
    Wiki thisWiki = new Wiki();
    thisWiki.setProjectId(project.getId());
    thisWiki.setContent(wiki);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertEquals(wiki, HTMLToWikiUtils.htmlToWiki(html, "", project.getId()));
  }
}
