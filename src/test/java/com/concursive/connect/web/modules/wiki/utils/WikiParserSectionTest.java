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
import org.suigeneris.jrcs.util.ToString;

import java.util.HashMap;

/**
 * Tests wiki parser section editing functions
 *
 * @author matt rajkowski
 * @created July 9, 2008
 */
public class WikiParserSectionTest extends TestCase {

  protected final static String wikiSample =
      "Paragraph 1 and some text\n" +
          "\n" +
          "== Section 1.0 (1) ==\n" +
          "\n" +
          "some section text and lots of it 1.0.1\n" +
          "some section text and lots of it 1.0.2\n" +
          "\n" +
          "This is '''bold''' and this is ''itals'' and this is '''''both''''' and this is __underline__ and this is <s>strikethrough</s>.\n" +
          "\n" +
          "* Unord 1 in bold\n" +
          "* Unord 2\n" +
          "\n" +
          "# Ord 1\n" +
          "# Ord 2\n" +
          "## Indent 1\n" +
          "## Indent 2\n" +
          "== Section 2.0 (2) ==\n" +
          "\n" +
          "some section text and lots of it 2.0.1\n" +
          "some section text and lots of it 2.0.2\n" +
          "\n" +
          "=== Section 2.1 (3) ===\n" +
          "\n" +
          "some section text and lots of it 2.1.1\n" +
          "some section text and lots of it 2.1.2\n" +
          "\n" +
          "=== Section 2.2 (4) ===\n" +
          "\n" +
          "some section text and lots of it 2.2.1\n" +
          "some section text and lots of it 2.2.2\n" +
          "some section text and lots of it 2.2.3\n" +
          "\n" +
          "== Section 3.0 (5) ==\n" +
          "\n" +
          "some section text and lots of it 3.0.1\n" +
          "some section text and lots of it 3.0.2\n" +
          "some section text and lots of it 3.0.3\n" +
          "some section text and lots of it 3.0.4\n" +
          "some section text and lots of it 3.0.5\n" +
          "\n" +
          "=== Section 3.1 (6) ===\n" +
          "\n" +
          "some section text and lots of it 3.1.0\n" +
          "some section text and lots of it 3.1.1\n" +
          "\n" +
          "=== Section 3.2 (7) ===\n" +
          "\n" +
          "some section text and lots of it 3.2.1\n" +
          "some section text and lots of it 3.2.2\n" +
          "some section text and lots of it 3.2.3\n" +
          "some section text and lots of it 3.2.4\n" +
          "some section text and lots of it 3.2.5\n" +
          "some section text and lots of it 3.2.6\n" +
          "\n" +
          "== Section 4.0 (8) ==\n" +
          "\n" +
          "some section text and lots of it 4.0.1\n" +
          "some section text and lots of it 4.0.2\n" +
          "some section text and lots of it 4.0.3\n" +
          "\n" +
          "the end text";

  public void testEditWikiSectionHtml() throws Exception {
    // Convert wiki to html, this will optimize the html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, true, "");
    wikiContext.setEditSectionId(5);
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);
    assertFalse("Unrequested section was incorrectly found", html.contains("Section 2.2"));
    assertTrue("Requested section not found", html.contains("Section 3.0"));
    assertEquals(
        "<h2 id=\"Section_3.0_(5)\"><span>Section 3.0 (5)</span></h2>\n" +
            "<p>some section text and lots of it 3.0.1<br />some section text and lots of it 3.0.2<br />some section text and lots of it 3.0.3<br />some section text and lots of it 3.0.4<br />some section text and lots of it 3.0.5</p>\n" +
            "<h3><span>Section 3.1 (6)</span></h3>\n" +
            "<p>some section text and lots of it 3.1.0<br />some section text and lots of it 3.1.1</p>\n" +
            "<h3><span>Section 3.2 (7)</span></h3>\n" +
            "<p>some section text and lots of it 3.2.1<br />some section text and lots of it 3.2.2<br />some section text and lots of it 3.2.3<br />some section text and lots of it 3.2.4<br />some section text and lots of it 3.2.5<br />some section text and lots of it 3.2.6</p>\n", html);
    assertFalse("Unrequested section was incorrectly found", html.contains("Section 4.0"));
    assertFalse("Unrequested text was incorrectly found", html.contains("the end text"));
  }

  public void testMergeWikiSectionFromHtml() throws Exception {
    int section = 5;
    // Convert wiki to html, this will optimize the html
    Wiki wiki = new Wiki();
    wiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, new HashMap<String, ImageInfo>(), -1, true, "");
    wikiContext.setEditSectionId(section);
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    // Initial tests
    assertNotNull(html);
    assertFalse("Unrequested section was incorrectly found", html.contains("Section 2.2"));
    assertTrue("Requested section not found", html.contains("Section 3.0"));
    assertFalse("Unrequested section was incorrectly found", html.contains("Section 4.0"));
    assertFalse("Unrequested text was incorrectly found", html.contains("the end text"));
    // Integrate the same content back in, assuming no changes have been made...
    String sectionMarkup = HTMLToWikiUtils.htmlToWiki(html, "", -1);
    String mergedContent = WikiUtils.merge(wiki, sectionMarkup, section);
    assertEquals(wikiSample, mergedContent);

    Revision revision = Diff.diff(
        ToString.stringToArray(wikiSample),
        ToString.stringToArray(wiki.getContent()), null);
    assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
  }
}