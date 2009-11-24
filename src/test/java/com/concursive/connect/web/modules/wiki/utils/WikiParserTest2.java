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

import java.util.HashMap;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserTest2 extends TestCase {

  protected final static String htmlSample = "<p>This is a new example.</p>\n" +
      "<p>Second paragraph</p>\n" +
      "<h2 id=\"Beginning_a_header\"><span>Beginning a header</span></h2>\n" +
      "<p>some text</p>\n" +
      "<h2 id=\"Beginning_a_new_header\"><span>Beginning a new header</span></h2>\n" +
      "<p>some other text</p>\n" +
      "<pre>pre1</pre>\n" +
      "<p>and something else</p>\n" +
      "<pre>pre2</pre>\n" +
      "<pre>Another pre3\n" +
      "line 2 of pre3</pre>\n" +
      "<p>Woah.  Round-trip!</p>\n";

  protected final static String wikiSample = "This is a new example.\n" +
      "\n" +
      "Second paragraph\n" +
      "\n" +
      "== Beginning a header ==\n\n" +
      "some text\n" +
      "\n" +
      "== Beginning a new header ==\n\n" +
      "some other text\n" +
      "\n" +
      "<pre>pre1</pre>\n" +
      "\n" +
      "and something else\n" +
      "\n" +
      "<pre>pre2</pre>\n" +
      "\n" +
      "<pre>Another pre3\n" +
      "line 2 of pre3</pre>\n" +
      "\n" +
      "Woah.  Round-trip!\n";

  public void testHtmlToWikiParser() throws Exception {
    // Convert html to wiki
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "", -1);
    assertEquals(wikiSample, wiki);
    /*
    Revision revision = Diff.diff(
        ToString.stringToArray(wikiSample),
        ToString.stringToArray(wiki), null);
    assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
    */
  }

  public void testRoundtrip() throws Exception {

    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap(), 1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertEquals(htmlSample, html);
    String wiki = HTMLToWikiUtils.htmlToWiki(html, "", -1);

    assertNotNull(html);
    assertNotNull(wiki);

    assertEquals(wikiSample, wiki);
    /*
    Revision revision = Diff.diff(
        ToString.stringToArray(wikiSample),
        ToString.stringToArray(wiki), null);
    assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
    */
  }
}