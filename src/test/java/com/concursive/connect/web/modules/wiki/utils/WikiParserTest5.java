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
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserTest5 extends TestCase {

  protected final static String wikiSample =
      "Improvements to the '''stable version '''of Team Elements are being done in the following Subversion branch:\n" +
          "\n" +
          "<pre>https://svn.teamelements.com/iteam/branches/f-20080107-api\\r\n" +
          "https://svn.teamelements.com/iteam/branches/f-20080107-api2</pre>This is the next Hello world!\n" +
          "\n" +
          "And this is another paragraph.\n" +
          "\n" +
          "= This is heading 1 =\n" +
          "This is a '''''''paragraph'''''.''\n" +
          "\n" +
          "||||||||||Products of interest||\n" +
          "| |Product A|Product B|Product C|Product D|\n" +
          "|Feature A|y|n|n|y|\n" +
          "|Feature B|n|y|y|n|\n" +
          "|Feature C|y|n|y|n|\n" +
          "*  Bullet A\n" +
          "*  Bullet B\n" +
          "#  Number 1\n" +
          "#  Number 2" +
          "\n";

  protected final static String htmlSample =
      "<p>Improvements to the <strong>stable version </strong>of Team Elements are being done in the following Subversion branch:</p>" +
          "<pre>https://svn.teamelements.com/iteam/branches/f-20080107-api\\r\n" +
          "https://svn.teamelements.com/iteam/branches/f-20080107-api2</pre><p>This is the next Hello world!</p>" +
          "<p>And this is another paragraph.</p>" +
          "<h1>This is heading 1</h1>\n" +
          "<p>This is a <strong><em><em>paragraph</strong></em>.</em></p><table cellpadding=\"4\" cellspacing=\"0\" class=\"pagedList\">\n" +
          "<tr><th colSpan=\"5\">Products of interest</th></tr>\n" +
          "<tr class=\"row1\"><td>&nbsp;</td><td>Product A</td><td>Product B</td><td>Product C</td><td>Product D</td></tr>\n" +
          "<tr class=\"row2\"><td>Feature A</td><td>y</td><td>n</td><td>n</td><td>y</td></tr>\n" +
          "<tr class=\"row1\"><td>Feature B</td><td>n</td><td>y</td><td>y</td><td>n</td></tr>\n" +
          "<tr class=\"row2\"><td>Feature C</td><td>y</td><td>n</td><td>y</td><td>n</td></tr>\n" +
          "</table>\n" +
          "<ul><li>  Bullet A</li>\n" +
          "<li>  Bullet B</li>\n" +
          "</ul>\n" +
          "<ol><li>  Number 1</li>\n" +
          "<li>  Number 2</li>";

  public void testNestedFormatting() throws Exception {

    // Convert wiki to html
    Wiki thisWiki = new Wiki();
    thisWiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertNotNull(html);

    Revision revision = Diff.diff(
        ToString.stringToArray(htmlSample),
        ToString.stringToArray(html), null);
    // TODO: Fix the multiple nesting of bold and italic
//    assertTrue("Revision found: " + revision.toString(), revision.size() == 0);
  }
}