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

import com.concursive.connect.web.modules.wiki.dao.CustomForm;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiUtils;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created May 30, 2008
 */
public class WikiParserFormTest extends TestCase {

  protected final static String crmFoldersHtml =
      "<form name=\"wikiForm\" action=\"ProjectManagementWikiForms.do?command=Save\" method=\"post\">\n" +
          "  <table cellpadding=\"4\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"details\">\n" +
          "    <tr>\n" +
          "      <th colspan=\"2\">\n" +
          "        <strong>New Group</strong>\n" +
          "      </th>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        This is a text field\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf10\" maxlength=\"30\" size=\"30\" value=\"\"> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        This is the text to display after\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        Lookup List\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <select size='1' name='cf11' id='cf11'  ><option selected value='-1' >-- None --</option><option value='16' >Value 1</option><option value='17' >Value 2</option><option value='18' >Value 3</option><option value='19' >Value 4</option><option value='20' >Value 5</option></select> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        Some text to display after\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        Text Area\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <textarea cols=\"50\" rows=\"4\" name=\"cf12\"></textarea> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        text after\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        A date\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf13\" size=\"10\" value=\"\" > <a href=\"javascript:popCalendar('details', 'cf13','en','US');\"><img src=\"images/icons/stock_form-date-field-16.gif\" border=\"0\" align=\"absmiddle\" height=\"16\" width=\"16\"/></a> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        &nbsp;\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      \n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        A number\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf14\" maxlength=\"5\" size=\"5\" value=\"\"> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        &nbsp;\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      \n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        Decimal number\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf15\" maxlength=\"5\" size=\"5\" value=\"\"> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        More text\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      \n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        A percent\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf16\" size=\"8\" value=\"\"> % <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        &nbsp;\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      \n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        Currency here\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf17\" maxlength=\"5\" size=\"5\" value=\"\"> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        &nbsp;\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        A checkbox\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"checkbox\" name=\"cf18\" value=\"ON\" > <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        more text\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        an email address\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf19\" value=\"\"> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        &nbsp;\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "    <tr class=\"containerBody\">\n" +
          "      <td valign=\"top\" class=\"formLabel\">\n" +
          "        a phone\n" +
          "      </td>\n" +
          "      <td valign=\"top\">\n" +
          "        <input type=\"text\" name=\"cf20\" value=\"\"> <font color=\"red\"></font>\n" +
          "        <font color=\"#006699\">&nbsp;</font>\n" +
          "        &nbsp;\n" +
          "      </td>\n" +
          "    </tr>\n" +
          "  </table>  \n" +
          "    <br>\n" +
          "    <input type=\"submit\" value=\"Save\" onClick=\"javascript:this.form.action='Accounts.do?command=InsertFields&orgId=3&catId=2&popup=false&actionplan=false'\">\n" +
          "    <input type=\"submit\" value=\"Cancel\" onClick=\"javascript:this.form.action='Accounts.do?command=Fields&orgId=3&catId=2&popup=false&actionplan=false'\">\n" +
          "</form>";

  protected final static String wikiForm =
      "[{form name=\"wikiForm\"}]" +
          "---" +
          "[{group value=\"New Group\"}]" +
          "---" +
          "[{label value=\"This is a text field\"}]" +
          "[{field type=\"text\" name=\"cf10\" maxlength=\"30\" size=\"30\" value=\"\" required=\"false\"}]" +
          "[{description value=\"This is the text to display after\"}]" +
          //"[{entry value=\"This is the field's value during entry\"}]" +
          "---" +
          "[{label value=\"Lookup List\"}]" +
          "[{field type=\"select\" name=\"cf11\" options=\"-- None --|Value 1|Value 2|Value 3|Value 4|Value 5\" value=\"\" required=\"false\"}]" +
          "[{description value=\"Some text to display after\"}]" +
          "---" +
          "[{label value=\"Text Area\"}]" +
          "[{field type=\"textarea\" name=\"cf12\" cols=\"50\" rows=\"4\" value=\"\" required=\"false\"}]" +
          "[{description value=\"text after\"}]" +
          "---" +
          "[{label value=\"A date\"}]" +
          "[{field type=\"calendar\" name=\"cf13\" size=\"10\" value=\"\" required=\"false\"}]" +
          "[{description value=\"\"}]" +
          "---" +
          "[{label value=\"A number\"}]" +
          "[{field type=\"number\" name=\"cf14\" maxlength=\"5\" size=\"5\" value=\"\" required=\"false\"}]" +
          "[{description value=\"\"}]" +
          "---" +
          "[{label value=\"Decimal number\"}]" +
          "[{field type=\"decimal\" name=\"cf15\" maxlength=\"5\" size=\"5\" value=\"\" required=\"false\"}]" +
          "[{description value=\"More text\"}]" +
          "---" +
          "[{label value=\"A percent\"}]" +
          "[{field type=\"percent\" name=\"cf16\" size=\"8\" value=\"\" required=\"false\"}]" +
          "[{description value=\"\"}]" +
          "---" +
          "[{label value=\"Currency here\"}]" +
          "[{field type=\"currency\" name=\"cf17\" maxlength=\"5\" size=\"5\" value=\"\" required=\"false\"}]" +
          "[{description value=\"\"}]" +
          "---" +
          "[{label value=\"A checkbox\"}]" +
          "[{field type=\"checkbox\" name=\"cf18\" value=\"true\" required=\"false\"}]" +
          "[{description value=\"more text\"}]" +
          "---" +
          "[{label value=\"an email address\"}]" +
          "[{field type=\"email\" name=\"cf19\" value=\"\" required=\"false\"}]" +
          "[{description value=\"\"}]" +
          "---" +
          "[{label value=\"a phone\"}]" +
          "[{field type=\"phone\" name=\"cf20\" value=\"\" required=\"false\"}]" +
          "[{description value=\"\"}]" +
          "+++" +
          "\n";

  /*
  // NOT IMPLEMENTED
  public void testCRMFoldersFormToWiki() throws Exception {
    String wiki = HTMLToWikiUtils.htmlToWiki(crmFoldersHtml);
    assertEquals(wikiForm, wiki);
  }
  */

  protected static final String completedWikiForm =
      "[{form name=\"wikiForm\"}]\n" +
          "---\n" +
          "[{group value=\"New Group\"}]\n" +
          "---\n" +
          "[{label value=\"This is a text field\"}]\n" +
          "[{field type=\"text\" name=\"cf10\" maxlength=\"50\" size=\"30\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"This is the text to display after\"}]\n" +
          "[{entry value=\"This is the field's value during entry\"}]\n" +
          "---\n" +
          "[{label value=\"Lookup List\"}]\n" +
          "[{field type=\"select\" name=\"cf11\" options=\"-- None --|Value 1|Value 2|Value 3|Value 4|Value 5\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"Some text to display after\"}]\n" +
          "[{entry value=\"Value 1\"}]\n" +
          "---\n" +
          "[{label value=\"Text Area\"}]\n" +
          "[{field type=\"textarea\" name=\"cf12\" cols=\"50\" rows=\"4\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"text after\"}]\n" +
          "[{entry value=\"This is the field's text area value during entry\"}]\n" +
          "---\n" +
          "[{label value=\"A date\"}]\n" +
          "[{field type=\"calendar\" name=\"cf13\" size=\"10\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"\"}]\n" +
          "[{entry value=\"2008-03-04 15:12:41.11\"}]\n" +
          "---\n" +
          "[{label value=\"A number\"}]\n" +
          "[{field type=\"number\" name=\"cf14\" maxlength=\"5\" size=\"5\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"\"}]\n" +
          "[{entry value=\"12345\"}]\n" +
          "---\n" +
          "[{label value=\"Decimal number\"}]\n" +
          "[{field type=\"decimal\" name=\"cf15\" maxlength=\"8\" size=\"8\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"More text\"}]\n" +
          "[{entry value=\"12345.27\"}]\n" +
          "---\n" +
          "[{label value=\"A percent\"}]\n" +
          "[{field type=\"percent\" name=\"cf16\" size=\"3\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"\"}]\n" +
          "[{entry value=\"50\"}]\n" +
          "---\n" +
          "[{label value=\"Currency here\"}]\n" +
          "[{field type=\"currency\" name=\"cf17\" maxlength=\"10\" size=\"7\" value=\"\" required=\"false\" currency=\"USD\"}]\n" +
          "[{description value=\"\"}]\n" +
          "[{entry value=\"2085.10\"}]\n" +
          "---\n" +
          "[{label value=\"A checkbox\"}]\n" +
          "[{field type=\"checkbox\" name=\"cf18\" value=\"true\" required=\"false\"}]\n" +
          "[{description value=\"more text\"}]\n" +
          "[{entry value=\"true\"}]\n" +
          "---\n" +
          "[{label value=\"an email address\"}]\n" +
          "[{field type=\"email\" name=\"cf19\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"\"}]\n" +
          "[{entry value=\"mrajkowski@example.com\"}]\n" +
          "---\n" +
          "[{label value=\"a phone\"}]\n" +
          "[{field type=\"phone\" name=\"cf20\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"\"}]\n" +
          "[{entry value=\"800-555-1212\"}]\n" +
          "---\n" +
          "[{label value=\"a website\"}]\n" +
          "[{field type=\"url\" name=\"cf21\" value=\"\" required=\"false\"}]\n" +
          "[{description value=\"\"}]\n" +
          "[{entry value=\"www.concursive.com\"}]\n" +
          "+++\n";

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
          "\n";

  public void testWikiFormEntry() throws Exception {
    BufferedReader in = new BufferedReader(new StringReader(completedWikiForm));
    CustomForm form = WikiToHTMLUtils.retrieveForm(in, "---");
    assertTrue("Groups found didn't match", form.size() == 1);
    assertTrue("Fields found didn't match", form.get(0).size() == 12);
  }

  public void testWikiWithFormMerge() throws Exception {
    // Combine the form and markup for the complete wiki
    Wiki originalWiki = new Wiki();
    originalWiki.setContent(completedWikiForm + wikiSample);
    // Assume that the user used WYSIWYG editor and modified just the wikiSample
    // Combine the two back together and see if they match
    Wiki finalWiki = new Wiki();
    finalWiki.setContent(WikiUtils.merge(originalWiki, wikiSample));
    assertEquals(originalWiki.getContent(), finalWiki.getContent());
  }

}