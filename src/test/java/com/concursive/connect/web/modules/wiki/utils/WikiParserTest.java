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
import junit.framework.TestCase;
import org.suigeneris.jrcs.diff.Diff;
import org.suigeneris.jrcs.diff.Revision;
import org.suigeneris.jrcs.util.ToString;

import java.util.HashMap;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserTest extends TestCase {

  protected final static String htmlSample = "<h1><span style=\"font-weight: normal;\">Heading 1</span></h1>\n" +
      "<p>Paragraph 1</p>\n" +
      "<h2>Heading 2</h2>\n" +
      "<p>Paragraph 2</p>\n" +
      "<p>This is <strong>bold</strong> and this is <em>itals</em> and this is <strong><em>both</em></strong> and this is <span style=\"text-decoration: underline;\">underline</span> and this is <span style=\"text-decoration: line-through;\">strikethrough</span>.</p>\n" +
      "<ul>\n" +
      "<li>Unord 1 in <strong>bold</strong></li>\n" +
      "<li>Unord 2</li>\n" +
      "</ul>\n" +
      "<ol>\n" +
      "<li>Ord 1</li>\n" +
      "<li>Ord 2<ol>\n" +
      "<li>Indent 1</li>\n" +
      "<li>Indent 2</li>\n" +
      "</ol></li>\n" +
      "</ol>";

  protected final static String wikiSample =
      "= Heading 1 =\n" +
          "\n" +
          "Paragraph 1\n" +
          "\n" +
          "== Heading 2 ==\n" +
          "\n" +
          "Paragraph 2\n" +
          "\n" +
          "This is '''bold''' and this is ''itals'' and this is '''''both''''' and this is __underline__ and this is <s>strikethrough</s>.\n" +
          "\n" +
          "* Unord 1 in bold\n" +
          "* Unord 2\n" +
          "\n" +
          "# Ord 1\n" +
          "# Ord 2\n" +
          "## Indent 1\n" +
          "## Indent 2\n";

  public void testBasicWikiParser() throws Exception {
    // Convert html to wiki
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "");
    assertEquals(wikiSample, wiki);
    /*
    Revision revision = Diff.diff(
        ToString.stringToArray(wikiSample),
        ToString.stringToArray(wiki), null);
    assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
    */
  }

  public void testBasicHTMLParser() throws Exception {
    // Convert wiki to html, this will optimize the html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap(), null, -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext);
    assertNotNull(html);
  }

  public void testRoundtripParser() throws Exception {

    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap(), null, -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext);
    String wiki = HTMLToWikiUtils.htmlToWiki(html, "");

    assertNotNull(html);
    assertNotNull(wiki);

    Revision revision = Diff.diff(
        ToString.stringToArray(wikiSample),
        ToString.stringToArray(wiki), null);

    //assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
  }
}