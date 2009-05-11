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
package com.concursive.commons.html;

import junit.framework.TestCase;
import com.concursive.commons.html.HTMLUtils;

/**
 * Tests html utils functions
 *
 * @author matt rajkowski
 * @created August 7, 2008
 */
public class HTMLUtilsTest extends TestCase {

  protected String htmlSample =
      "<HTML>\n" +
          "<HEAD>\n" +
          "<SCRIPT LANGUAGE = \"JavaScript\">\n" +
          "\n" +
          "//  change.src = new image(64, 54); \n" +
          "//  change.src = \"billthec.gif\";\n" +
          "//  normal.src = new image(64, 54); \n" +
          "//  normal.src = \"garfield.gif\";\n" +
          "\n" +
          "function change_image() {document.changing.src = \"billthec.gif\";}\n" +
          "function normal_image() {document.changing.src = \"garfield.gif\";}\n" +
          "</SCRIPT>\n" +
          "\n" +
          "</HEAD>\n" +
          "<BODY BACKGROUND=\"../images/bnd_wire.jpg\" TEXT=\"#0000A0\" LINK=\"#8B1CAC\" VLINK=\"#A336C2\" ALINK=\"#000000\">\n" +
          "<TITLE>Mouse sensitive buttons</TITLE>\n" +
          "\n" +
          "<!-- BEGIN BANNERS XXXXXXXXXXXXXXXXX --> \n" +
          "<TABLE CELLSPACING=0 CELLPADDING=0 ><tr><td>\n" +
          "<!--#exec cgi=\"/perl/adrotate.pl?PATH=/ads/pages/java/top\" -->\n" +
          "</td><td></td></tr><tr><td>\n" +
          "<!--#exec cgi=\"/perl/adrotate.pl?PATH=/ads/pages/java/mid\" -->\n" +
          "</td><td>\n" +
          "\t<A HREF=\"http://www.pages.org/javascript/index.html\" TARGET=\"_top\"><FONT SIZE=5><B>\n" +
          "\tClick HERE to return to<BR>\n" +
          "\tJavascript main page</B></FONT></A>\n" +
          "</td></tr></table>\n" +
          "<HR>\n" +
          "<!-- END BANNERS XXXXXXXXXXXXXXXXX --> \n" +
          "\n" +
          "<TABLE BORDER=0>\n" +
          "<TR><TD>\n" +
          "<FONT SIZE=3>\n" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n" +
          "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n" +
          "&nbsp;\n" +
          "</FONT></TD><TD>\n" +
          "<CENTER><H2>\n" +
          "Mouse sensitive images and buttons<BR><BR>\n" +
          "<a href=\"javaScript:doSomething()\">run me</a>" +
          "<A HREF=\"index.html\" \n" +
          "   onMouseOver=\"change_image(); \n" +
          "   window.status='image will change';  \n" +
          "   return true\" \n" +
          "\n" +
          "   onMouseOut=\"normal_image(); \n" +
          "   window.status='';  \n" +
          "   return true\">\n" +
          "\n" +
          "<IMG SRC=\"garfield.gif\" NAME=\"changing\" WIDTH=64 HEIGHT=54 BORDER=0 ALT=\"click here!\"></A>\n" +
          "<BR><FONT COLOR=\"RED\">\n" +
          "This ONLY works using Netscape 3, because Microsoft's Internet Explorer \n" +
          "doesn't yet support javascript arrays used in this example.</FONT><BR><BR>\n" +
          "<FONT COLOR=\"BLACK\" SIZE=3>hello</FONT></CENTER>\n" +
          "<span style=\"text-decoration: underline;\">Underline</span>\n" +
          "<span style=\"bg-color: red;\">A single invalid style was around this</span>\n" +
          "<span style=\"text-decoration: line-through; bg-color: red\">Strike-through</span>" +
          "It works quite simply:<br>\n" +
          "When the mouse moves over the image a function called \n" +
          "change_image is called. This function changes the image[1] which in this case is\n" +
          "the second image on the page (<I>The background image being the first</I>). When\n" +
          "the mouse moves out of the image area a function called normal_image is called \n" +
          "which changes the image back to Garfield.\n" +
          "</TD></TR></TABLE>\n" +
          "<!--#exec cgi=\"/perl/adrotate.pl?PATH=/ads/pages/java/bot\" -->\n" +
          "</BODY>\n" +
          "</HTML>";


  public void testHtmlcleanerWithPublicContent() throws Exception {
    String html = HTMLUtils.makePublicHtml(htmlSample).toLowerCase();
    assertFalse("script tag incorrectly found", html.contains("<script"));
    assertFalse("body tag incorrectly found", html.contains("<body"));
    assertFalse("head tag incorrectly found", html.contains("<head"));
    assertFalse("javascript incorrectly found", html.toLowerCase().contains("javascript:"));
    assertFalse("event incorrectly found: onmouseover", html.toLowerCase().contains("onmouseover"));
    assertFalse("event incorrectly found: onmouseout", html.toLowerCase().contains("onmouseoout"));
    assertFalse("comment incorrectly found", html.toLowerCase().contains("<!--"));
    assertFalse("font tag incorrectly found", html.contains("<font"));
    assertFalse("color attribute incorrectly found", html.contains("color"));
  }
}