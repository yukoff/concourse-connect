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

import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
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
          "<table class=\"wikiTable\">\n" +
          "<tbody>\n" +
          "<tr>\n" +
          "<th colspan=\"5\">This is the header of the table</th>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>&nbsp;</td>\n" +
          "<td>\n" +
          "<p>Feature A</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Feature B</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Feature C</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Feature D</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>\n" +
          "<p>Item 1</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Yes</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>No</p>\n" +
          "<p>* Maybe</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>No</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>No</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>\n" +
          "<p>Item 2</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Yes</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Yes</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>No</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Yes</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>\n" +
          "<p>Item 3</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>No</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Yes</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Yes</p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>Yes</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>\n" +
          "<p>That is a tough decision.</p>";

  protected final static String wikiSample =
      "This is a table...\n" +
          "\n" +
          "||||||||||This is the header of the table||\n" +
          "| |Feature A|Feature B|Feature C|Feature D|\n" +
          "|Item 1|Yes|No\n" +
          "!\n" +
          "!\\* Maybe|No|No|\n" +
          "|Item 2|Yes|Yes|No|Yes|\n" +
          "|Item 3|No|Yes|Yes|Yes|\n" +
          "\n" +
          "That is a tough decision.\n";

  protected final static String htmlOut =
      "<p>This is a table...</p>\n" +
          "<table class=\"wikiTable\">\n" +
          "<tr><th colspan=\"5\"><p>This is the header of the table</p></th></tr>\n" +
          "<tr class=\"row1\"><td>&nbsp;</td><td><p>Feature A</p></td><td><p>Feature B</p></td><td><p>Feature C</p></td><td><p>Feature D</p></td></tr>\n" +
          "<tr class=\"row2\"><td><p>Item 1</p></td><td><p>Yes</p></td><td><p>No</p>\n<p>* Maybe</p></td><td><p>No</p></td><td><p>No</p></td></tr>\n" +
          "<tr class=\"row1\"><td><p>Item 2</p></td><td><p>Yes</p></td><td><p>Yes</p></td><td><p>No</p></td><td><p>Yes</p></td></tr>\n" +
          "<tr class=\"row2\"><td><p>Item 3</p></td><td><p>No</p></td><td><p>Yes</p></td><td><p>Yes</p></td><td><p>Yes</p></td></tr>\n" +
          "</table>\n" +
          "<p>That is a tough decision.</p>\n";

  public void testHtmlTablesToWiki() throws Exception {
    // Convert html to wiki
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "", -1);
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
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertEquals(htmlOut, html);

    // Verify no changes
    Revision revision = Diff.diff(
        ToString.stringToArray(html),
        ToString.stringToArray(htmlOut), null);

    assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
  }
}