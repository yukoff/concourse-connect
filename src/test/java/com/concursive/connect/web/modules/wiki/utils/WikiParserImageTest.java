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

/**
 * Tests common project database access
 *
 * @author matt
 * @created January 24, 2008
 */
public class WikiParserImageTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample =
      "<p>Image follows in next paragraph...</p>\n" +
          "<p>" +
          "<img " +
          "title=\"this is the caption for the external image\" " +
          "src=\"http://i.l.cnn.net/cnn/2008/LIVING/wayoflife/04/11/economy.food.shopping.ap/t1home.grocery.store.gi.jpg\" " +
          "alt=\"this text is discarded\" " +
          "width=\"265\" " +
          "height=\"239\" />" +
          "</p>\n" +
          "<p>credit: <a class=\"wikiLink external\" href=\"http://www.cnn.com\" target=\"_blank\">http://www.cnn.com</a></p>" +
          "<p><img src=\"/show/some-project/wiki-image/Workflow+-+Ticket+Example.png\" alt=\"Workflow - Ticket Example.png\" width=\"315\" height=\"362\" /></p>";

  public void testHTMLImageToWiki() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, "9999999", project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", new Integer(9999999));
    
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "", project.getId());
    assertEquals("" +
        "Image follows in next paragraph...\n" +
        "\n" +
        "[[Image:http://i.l.cnn.net/cnn/2008/LIVING/wayoflife/04/11/economy.food.shopping.ap/t1home.grocery.store.gi.jpg|frame|this is the caption for the external image]]\n" +
        "\n" +
        "credit: [[http://www.cnn.com]]\n" +
        "\n" +
        "[[Image:Workflow - Ticket Example.png]]\n", wiki);
  }
}