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
import junit.framework.TestCase;
import org.suigeneris.jrcs.diff.Revision;
import org.suigeneris.jrcs.diff.Diff;
import org.suigeneris.jrcs.util.ToString;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserTest4 extends TestCase {

  protected String htmlSample =
      "<p>Improvements to the stable version of Team Elements are being done in the following Subversion branch:</p>\n" +
          "<pre>https://svn.teamelements.com/iteam/branches/f-20080107-api\n" +
          "https://svn.teamelements.com/iteam/branches/f-20080107-api2</pre>\n" +
          "<p>This is the next&nbsp;Hello world!</p>\n" +
          "<p>And this is another paragraph.</p>\n" +
          "<h1>This is heading 1</h1>\n" +
          "<p>This is a paragraph.</p>\n" +
          "<table class=\"pagedList\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\">\n" +
          "<tbody>\n" +
          "<tr>\n" +
          "<th colspan=\"5\">Products of interest</th>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>&nbsp;</td>\n" +
          "<td>Product A</td>\n" +
          "<td>Product B</td>\n" +
          "<td>Product C</td>\n" +
          "<td>Product D</td>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>Feature A</td>\n" +
          "<td>y</td>\n" +
          "<td>n</td>\n" +
          "<td>n</td>\n" +
          "<td>y</td>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>Feature B</td>\n" +
          "<td>n</td>\n" +
          "<td>y</td>\n" +
          "<td>y</td>\n" +
          "<td>n</td>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>Feature C</td>\n" +
          "<td>y</td>\n" +
          "<td>n</td>\n" +
          "<td>y</td>\n" +
          "<td>n</td>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>\n" +
          "<p>" +
            "<strong>" +
              "<strong>\n" +
                "<p><strong> </strong></p>\n" +
                "<strong>\n" +
                  "<p>" +
                    "<strong>\n" +
                      "<ul>\n" +
                        "<li>Bullet A<br /></li>\n" +
                        "<li> Bullet B</li>\n" +
                      "</ul>\n" +
                      "<ol>\n" +
                        "<li> Number 1</li>\n" +
                        "<li> Number 2</li>\n" +
                      "</ol>" +
                    "</strong>" +
          "       </p>\n" +
                  "<strong></strong>" +
                "</strong>" +
              "</strong>" +
          " </strong>" +
          "</p>";

  protected String targetWikiContent =
      "Improvements to the stable version of Team Elements are being done in the following Subversion branch:\n" +
          "\n" +
          "<pre>https://svn.teamelements.com/iteam/branches/f-20080107-api\n" +
          "https://svn.teamelements.com/iteam/branches/f-20080107-api2</pre>\n" +
          "\n" +
          "This is the next Hello world!\n" +
          "\n" +
          "And this is another paragraph.\n" +
          "\n" +
          "= This is heading 1 =\n" +
          "\n" +
          "This is a paragraph.\n" +
          "\n" +
          "||||||||||Products of interest||\n" +
          "| |Product A|Product B|Product C|Product D|\n" +
          "|Feature A|y|n|n|y|\n" +
          "|Feature B|n|y|y|n|\n" +
          "|Feature C|y|n|y|n|\n" +
          "\n" +
          "\n" +
          "* Bullet A\n" +
          "*  Bullet B\n" +
          "\n" +
          "#  Number 1\n" +
          "#  Number 2" +
          "\n";

  public void testNestedParagraphs() throws Exception {
    // Convert html to wiki
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "");

    assertEquals(targetWikiContent, wiki);

    Revision revision = Diff.diff(
        ToString.stringToArray(targetWikiContent),
        ToString.stringToArray(wiki), null);

    assertTrue("A difference was found: \n" + revision.toString(), revision.size() == 0);
    System.out.println(wiki);
  }
}