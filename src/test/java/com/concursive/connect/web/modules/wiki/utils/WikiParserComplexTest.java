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

import com.concursive.connect.web.modules.wiki.utils.HTMLToWikiUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.commons.db.AbstractConnectionPoolTest;

/**
 * Tests common project database access
 *
 * @author matt rajkowski
 * @created April 15, 2008
 */
public class WikiParserComplexTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample1 =
      "<h1>Here's some logo.</h1>\n" +
          "<p><img title=\"This is our logo\" src=\"/show/some-project/wiki-image/concoursesuite.png\" alt=\"concoursesuite.png\" width=\"564\" height=\"92\" /></p>\n" +
          "<p>And some <strong>more</strong> content:</p>\n" +
          "<p>&nbsp;</p>\n" +
          "<ol>\n" +
          "<li>Line 1</li>\n" +
          "<li>Line 2</li>\n" +
          "<li>Line 3</li>\n" +
          "</ol>\n" +
          "<div><img src=\"/show/some-project/wiki-image/concursivelogo.png?th=true\" alt=\"concursivelogo.png\" width=\"200\" height=\"94\" /></div>\n" +
          "<div></div>\n" +
          "<div>\n" +
          "<table border=\"0\">\n" +
          "<tbody>\n" +
          "<tr>\n" +
          "<td colspan=\"2\">Here's some stuff</td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td>Left Side</td>\n" +
          "<td>Right Side</td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td>LS 2</td>\n" +
          "<td>RS 2</td>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>\n" +
          "</div>\n" +
          "<p>&nbsp;</p>\n" +
          "<p><a href=\"http://www.cnn.com\">Anything else?</a></p>\n" +
          "<p>&nbsp;</p>";

  public void testHTMLPreToWiki() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, "9999999", project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", new Integer(9999999));
    
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample1, "");
    assertEquals("" +
        "= Here's some logo. =\n" +
        "\n" +
        "[[Image:concoursesuite.png|frame|This is our logo]]\n" +
        "\n" +
        "And some '''more''' content:\n" +
        "\n" +
        "# Line 1\n" +
        "# Line 2\n" +
        "# Line 3\n" +
        "[[Image:concursivelogo.png|thumb]]\n" +
        "\n" +
        "||||Here's some stuff||\n" +
        "|Left Side|Right Side|\n" +
        "|LS 2|RS 2|\n" +
        "\n" +
        "[[http://www.cnn.com Anything else?]]" +
        "", wiki);
  }
}