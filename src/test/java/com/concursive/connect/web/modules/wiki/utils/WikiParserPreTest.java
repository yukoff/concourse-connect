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
 * Tests common project database access
 *
 * @author matt rajkowski
 * @created April 15, 2008
 */
public class WikiParserPreTest extends TestCase {

  protected final static String wikiSample =
      "* In cfs-modules.xml copy the section \n" +
          "<pre>\n" +
          "  <menu>\n" +
          "    <action name=\"ExternalContacts\"/>\n" +
          "    <action name=\"ExternalContactsOpps\"/>\n" +
          "    <action name=\"ExternalContactsOppComponents\"/>\n" +
          "    <action name=\"ExternalContactsCalls\"/>\n" +
          "    <action name=\"ExternalContactsCallsForward\"/>\n" +
          "    <action name=\"ExternalContactsPrototype\"/>\n" +
          "    <action name=\"ExternalContactsImports\"/>\n" +
          "    <action name=\"ExternalContactsHistory\"/>\n" +
          "    <action name=\"ExternalContactsMessages\"/>\n" +
          "    <page title=\"Contacts\"/>\n" +
          "    ....\n" +
          "  </menu>\n" +
          "</pre>\n" +
          "\n" +
          "for the contacts module and paste it below the closing menu tag (or where ever else you want to position it.) Rename the title to \"Personnel\"";

  public void testWikiPreToHTML() throws Exception {
    System.setProperty("DEBUG", "2");
    Wiki wiki = new Wiki();
    wiki.setContent(wikiSample);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, new HashMap(), null, -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext);
    assertEquals("<ul><li> In cfs-modules.xml copy the section </li>\n" +
        "</ul>\n" +
        "<pre>  &lt;menu&gt;\n" +
        "    &lt;action name=&quot;ExternalContacts&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsOpps&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsOppComponents&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsCalls&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsCallsForward&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsPrototype&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsImports&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsHistory&quot;/&gt;\n" +
        "    &lt;action name=&quot;ExternalContactsMessages&quot;/&gt;\n" +
        "    &lt;page title=&quot;Contacts&quot;/&gt;\n" +
        "    ....\n" +
        "  &lt;/menu&gt;</pre>\n" +
        "<p>for the contacts module and paste it below the closing menu tag (or where ever else you want to position it.) Rename the title to &quot;Personnel&quot;</p>\n", html);
  }


  protected final static String wikiSample2 =
      "== Accessing from a JSP ==\n" +
          "\n" +
          "With the LookupList object in the request, the method '''getHtmlSelect(\"formFieldName\", defaultId)''' can be used to render an HTML Select field with the options.\n" +
          "\n" +
          "<pre>\n" +
          "<jsp:useBean id=\"stepActionsLookupList\" class=\"com.concursive.connect.web.utils.LookupList\" scope=\"request\"/>\n" +
          "<tr class=\"containerBody\">\n" +
          "  <td class=\"formLabel\">\n" +
          "    <dhv:label name=\"sales.step.action\">Step Action</dhv:label>\n" +
          "  </td>\n" +
          "  <td>\n" +
          "    <%= stepActionsLookupList.getHtmlSelect(\"stepAction\", otherBean.getStepActionId()) %>\n" +
          "  </td>\n" +
          "</tr>\n" +
          "</pre>\n" +
          "\n" +
          "The LookupList object contains both enabled and disabled items, however only the enabled items will be shown.  The exception is that if the form object is set to a disabled item, the disabled item will be included in the Lookup List with an '''(X)''' next to its value to alert the user that a disabled item is being used.  The user can then optionally change the value to an enabled value, or leave the existing disabled value.";

  public void testWikiPreToHTML2() throws Exception {
    System.setProperty("DEBUG", "2");
    Wiki wiki = new Wiki();
    wiki.setContent(wikiSample2);
    // Parse it
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, new HashMap(), null, -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext);
    assertEquals("<h2><span>Accessing from a JSP</span></h2>\n" +
        "<p>With the LookupList object in the request, the method " +
        "<strong>getHtmlSelect(&quot;formFieldName&quot;, defaultId)</strong> " +
        "can be used to render an HTML Select field with the options.</p>\n" +
        "<pre>&lt;jsp:useBean id=&quot;stepActionsLookupList&quot; class=&quot;com.concursive.connect.web.utils.LookupList&quot; scope=&quot;request&quot;/&gt;\n" +
        "&lt;tr class=&quot;containerBody&quot;&gt;\n  " +
        "&lt;td class=&quot;formLabel&quot;&gt;\n    " +
        "&lt;dhv:label name=&quot;sales.step.action&quot;&gt;Step Action&lt;/dhv:label&gt;\n" +
        "  &lt;/td&gt;\n" +
        "  &lt;td&gt;\n" +
        "    &lt;%= stepActionsLookupList.getHtmlSelect(&quot;stepAction&quot;, otherBean.getStepActionId()) %&gt;\n" +
        "  &lt;/td&gt;\n&lt;/tr&gt;</pre>\n" +
        "<p>The LookupList object contains both enabled and disabled items, however only the enabled items will be shown.  The exception is that if the form object is set to a disabled item, the disabled item will be included in the Lookup List with an <strong>(X)</strong> next to its value to alert the user that a disabled item is being used.  The user can then optionally change the value to an enabled value, or leave the existing disabled value.</p>\n", html);
  }


  protected final static String htmlSample =
      "<ul><li>In cfs-modules.xml copy the section </li>\n" +
          "</ul>\n" +
          "<pre>test\n" +
          "  &lt;menu&gt;\n" +
          "    &lt;action name=&quot;ExternalContacts&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsOpps&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsOppComponents&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsCalls&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsCallsForward&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsPrototype&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsImports&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsHistory&quot;/&gt;\n" +
          "    &lt;action name=&quot;ExternalContactsMessages&quot;/&gt;\n" +
          "    &lt;page title=&quot;Contacts&quot;/&gt;\n" +
          " ....\n" +
          "&lt;/menu&gt;\n" +
          "test</pre><p>for the contacts module and paste it below the closing menu tag (or where ever else you want to position it.) Rename the title to &quot;Personnel&quot;</p>";

  public void testHTMLPreToWiki() throws Exception {
    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample, "");
    assertEquals("" +
        "* In cfs-modules.xml copy the section\n" +
        "\n" +
        "<pre>test\n" +
        "  <menu>\n" +
        "    <action name=\"ExternalContacts\"/>\n" +
        "    <action name=\"ExternalContactsOpps\"/>\n" +
        "    <action name=\"ExternalContactsOppComponents\"/>\n" +
        "    <action name=\"ExternalContactsCalls\"/>\n" +
        "    <action name=\"ExternalContactsCallsForward\"/>\n" +
        "    <action name=\"ExternalContactsPrototype\"/>\n" +
        "    <action name=\"ExternalContactsImports\"/>\n" +
        "    <action name=\"ExternalContactsHistory\"/>\n" +
        "    <action name=\"ExternalContactsMessages\"/>\n" +
        "    <page title=\"Contacts\"/>\n" +
        " ....\n" +
        "</menu>\n" +
        "test</pre>\n" +
        "\n" +
        "for the contacts module and paste it below the closing menu tag (or where ever else you want to position it.) Rename the title to \"Personnel\"\n", wiki);
  }
}
