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

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.profile.dao.Project;

/**
 * Tests wiki parser functions
 *
 * @author matt rajkowski
 * @created March 27, 2008
 */
public class WikiParserExternalCopyTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample1 =
      "<p>\n" +
          "<span style=\"font-family: arial, sans-serif; font-size: 12px; line-height: 15px;\">\n" +
          "<h1 style=\"font-size: 22px; line-height: 24px; font-family: Helvetica, arial, sans-serif; padding-top: 4px; padding-right: 0px; padding-bottom: 2px; padding-left: 0px; font-weight: bold; margin: 0px;\">\n" +
          "<a style=\"text-decoration: none; color: #004276;\" href=\"http://money.cnn.com/2009/06/10/news/companies/chrysler_fiat/index.htm\">Chrysler and Fiat make it official</a>\n" +
          "</h1>\n" +
          "<div class=\"cnnT1Blurb\" style=\"line-height: 17px; padding-top: 2px; padding-right: 0px; padding-bottom: 12px; padding-left: 0px;\">Chrysler and Italian automaker Fiat today officially signed a strategic alliance brokered by the U.S. government, CNNMoney reports. The deal forms a new company, called Chrysler Group LLC. \"This is a very significant day ... for the global automotive industry as a whole,\" said Sergio Marchionne, who was named chief executive of Chrysler.&nbsp;<a style=\"text-decoration: none; color: #004276;\" href=\"http://money.cnn.com/2009/06/10/news/companies/chrysler_fiat/index.htm\">full story</a>\n" +
          "</div>\n" +
          "</span>\n" +
          "</p>";

  public void testHTMLPreToWiki1() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, "9999999", project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", new Integer(9999999));

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample1, "");
    assertEquals("" +
        "= Chrysler and Fiat make it official =\n" +
        "\n" +
        "Chrysler and Italian automaker Fiat today officially signed a strategic alliance brokered by the U.S. government, CNNMoney reports. The deal forms a new company, called Chrysler Group LLC. \"This is a very significant day ... for the global automotive industry as a whole,\" said Sergio Marchionne, who was named chief executive of Chrysler. [[http://money.cnn.com/2009/06/10/news/companies/chrysler_fiat/index.htm full story]]\n" +
        "", wiki);
  }

  protected final static String htmlSample2 =
      "<p>\n" +
          "<span style=\"font-family: Arial, Helvetica, sans-serif; font-size: small;\">\n" +
          "<div class=\"headline\" style=\"font-size: 16px; font-weight: bold; margin-top: 0px; margin-right: 0px; margin-bottom: 2px; margin-left: 0px;\">\n" +
          "<a style=\"text-decoration: none; color: #000000;\" href=\"http://www.usatoday.com/tech/news/2009-06-09-biking-maps-google_N.htm\">Google map cam heads for the trails</a>\n" +
          "</div>\n" +
          "<div class=\"chatter\" style=\"font-size: 12px; color: #666666; padding: 0px;\">Bikers and hikers will soon be able to scout locations.</div>\n" +
          "<div class=\"spike\" style=\"overflow-x: auto; overflow-y: auto;\">\n" +
          "<ul style=\"float: left; padding-top: 2px; padding-right: 0px; padding-bottom: 4px; padding-left: 0px; list-style-type: none; list-style-position: initial; list-style-image: initial; line-height: 13px; margin: 0px;\">\n" +
          "<li style=\"font-size: 10px; background-image: url(http://i.usatoday.net/_fronts/_shared/_i/small-spike-bullet.gif); background-repeat: no-repeat; padding-top: 0px; padding-right: 4px; padding-bottom: 0px; padding-left: 10px; background-position: 0px 2px; margin: 0px;\">" +
          "<a style=\"text-decoration: none; color: #666666;\" href=\"http://www.usatoday.com/news/graphics/2009_mytownmap/mytownmap.htm\">" +
          "<span class=\"strong\" style=\"font-weight: bold; color: #000000;\">YOUR TOWN:&nbsp;</span>&nbsp;Add your home to our Google Map</a>\n" +
          "</li>\n" +
          "<li style=\"font-size: 10px; background-image: url(http://i.usatoday.net/_fronts/_shared/_i/small-spike-bullet.gif); background-repeat: no-repeat; padding-top: 0px; padding-right: 4px; padding-bottom: 0px; padding-left: 10px; background-position: 0px 2px; margin: 0px;\">" +
          "<a style=\"text-decoration: none; color: #666666;\" href=\"http://www.usatoday.com/tech/news/2009-06-09-biking-maps-google_N.htm#comment\">\n" +
          "<span class=\"strong\" style=\"font-weight: bold; color: #000000;\">TELL US:&nbsp;</span>&nbsp;Are Google maps of trails a good idea?</a>\n" +
          "</li>\n" +
          "</ul>\n" +
          "</div>\n" +
          "</span>\n" +
          "</p>";

  public void testHTMLPreToWiki2() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, "9999999", project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", new Integer(9999999));

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample2, "");
    assertEquals("" +
        "[[http://www.usatoday.com/tech/news/2009-06-09-biking-maps-google_N.htm Google map cam heads for the trails]]\n" +
        "\n" +
        "Bikers and hikers will soon be able to scout locations.\n" +
        "\n" +
        "* [[http://www.usatoday.com/news/graphics/2009_mytownmap/mytownmap.htm " +
        "YOUR TOWN:  Add your home to our Google Map]]\n" +
        "* [[http://www.usatoday.com/tech/news/2009-06-09-biking-maps-google_N.htm#comment " +
        "TELL US:  Are Google maps of trails a good idea?]]" +
        "\n", wiki);
  }
}
