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
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import junit.framework.TestCase;
import org.suigeneris.jrcs.diff.Diff;
import org.suigeneris.jrcs.diff.Revision;
import org.suigeneris.jrcs.diff.simple.SimpleDiff;
import org.suigeneris.jrcs.util.ToString;

import java.util.HashMap;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserTest3 extends TestCase {

  protected final static String htmlSample =
      "<p>This is a table...</p>\n" +
          "<table border=\"0\">\n" +
          "<tbody>\n" +
          "<tr>\n" +
          "<td id=\"_mc_tmp\" colspan=\"5\">This is the header of the table</td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td>&nbsp;</td>\n" +
          "<td>Feature A</td>\n" +
          "<td>Feature B</td>\n" +
          "<td>Feature C</td>\n" +
          "<td>Feature D</td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td>Item 1</td>\n" +
          //"<td><a href=\"http://www.concursive.com\">Item 1</a></td>\n" +
          "<td>Yes</td>\n" +
          "<td>No</td>\n" +
          "<td>No</td>\n" +
          "<td>No</td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td>Item 2</td>\n" +
          "<td>Yes</td>\n" +
          "<td>Yes</td>\n" +
          "<td>No</td>\n" +
          "<td>Yes</td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td>Item 3</td>\n" +
          "<td>\n" +
          "<p>No</p>\n" +
          "<p>* maybe</p>\n" +
          "</td>\n" +
          "<td>Yes</td>\n" +
          "<td>Yes</td>\n" +
          "<td>Yes</td>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>\n" +
          "<p>That is a tough decision.</p>";

  protected final static String wikiSample =
      "This is a table...\n" +
          "\n" +
          "||||||||||This is the header of the table||\n" +
          "| |Feature A|Feature B|Feature C|Feature D|\n" +
          "|Item 1|Yes|No|No|No|\n" +
          //"|[[http://www.concursive.com Item 1]]|Yes|No|No|No|\n" +
          "|Item 2|Yes|Yes|No|Yes|\n" +
          "|Item 3|No\n" +
          "!* maybe|Yes|Yes|Yes|\n" +
          "\n" +
          "That is a tough decision.";

  protected final static String htmlOut =
      "<p>This is a table...</p>\n" +
          "<table class=\"wikiTable\">\n" +
          "<tr><th colspan=\"5\">This is the header of the table</th></tr>\n" +
          "<tr class=\"row1\"><td>&nbsp;</td><td>Feature A</td><td>Feature B</td><td>Feature C</td><td>Feature D</td></tr>\n" +
          "<tr class=\"row2\"><td>Item 1</td><td>Yes</td><td>No</td><td>No</td><td>No</td></tr>\n" +
          //"<tr class=\"row2\"><td><a class=\"wikiLink external\" target=\"_blank\" href=\"http://www.concursive.com\">Item 1</a></td><td>Yes</td><td>No</td><td>No</td><td>No</td></tr>\n" +
          "<tr class=\"row1\"><td>Item 2</td><td>Yes</td><td>Yes</td><td>No</td><td>Yes</td></tr>\n" +
          "<tr class=\"row2\"><td>Item 3</td><td>No<br />* maybe</td><td>Yes</td><td>Yes</td><td>Yes</td></tr>\n" +
          "</table>\n" +
          "<p>That is a tough decision.</p>\n";

  public void testHtmlTablesToWiki() throws Exception {
    // Convert html to wiki
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "");
    assertEquals(wikiSample, wiki);

    // meyers
    Revision revision = Diff.diff(
        ToString.stringToArray(wikiSample),
        ToString.stringToArray(wiki), null);
    assertTrue("A revision was found: " + revision.toString(), revision.size() == 0);
    // simple
    revision = Diff.diff(
        ToString.stringToArray(wikiSample),
        ToString.stringToArray(wiki), new SimpleDiff());
    assertTrue("A revision was found: " + revision.toString(), revision.size() == 0);
    assertTrue("must not be equal", wikiSample.equals(wiki));
  }

  public void testWikiTablesToHtml() throws Exception {

    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap(), null, -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext);
    assertNotNull(html);
    assertEquals(htmlOut, html);

    // Verify no changes
    Revision revision = Diff.diff(
        ToString.stringToArray(html),
        ToString.stringToArray(htmlOut), null);

    assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
  }
}