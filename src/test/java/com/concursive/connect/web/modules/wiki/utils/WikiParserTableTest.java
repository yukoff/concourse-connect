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
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.Wiki;

import java.util.HashMap;

/**
 * Tests wiki parser
 *
 * @author matt rajkowski
 * @created January 29, 2010
 */
public class WikiParserTableTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample1 =
      "<table>" +
          "<tbody>\n" +
          "<tr>\n" +
          "<th>ConcourseConnect</th>\n" +
          "<th>ConcourseSuite</th>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<td><span style=\"font-family: Helvetica, Verdana, Arial, sans-serif; -webkit-border-horizontal-spacing: 0px; -webkit-border-vertical-spacing: 0px; font-size: 12px; color: #333333; line-height: 18px;\">\n" +
          "<p style=\"margin-top: 5px; margin-right: 0px; margin-bottom: 10px; margin-left: 0px; float: none;\">ConcourseConnect is the first platform that enables the creation of true commercial networks. Commercial networks are the intersection of social networking, web 2.0 collaboration and sales and marketing tools.</p>\n" +
          "<p style=\"margin-top: 5px; margin-right: 0px; margin-bottom: 10px; margin-left: 0px; float: none;\">ConcourseConnect enables organizations to create dynamic communities to connect various stakeholders and manage the entire ecosystem with an integrated management console and backend CRM tools. " +
          "<a class=\"wikiLink\" href=\"/show/some-project/wiki/Security%2C+Registration%2C+Invitation\">Installation Options</a>" +
          "</p>\n" +
          "</span></td>\n" +
          "<td><span style=\"font-family: Helvetica, Verdana, Arial, sans-serif; -webkit-border-horizontal-spacing: 0px; -webkit-border-vertical-spacing: 0px; font-size: 12px; color: #333333; line-height: 18px;\">ConcourseSuite is Concursive's dedicated Customer Relationship Management (CRM) product. It is a complete front office solution that integrates CRM, website creation, content management and team collaboration capabilities into one easy to use, easy to deploy solution that is available in both hosted and on-premise configurations. <a class=\"wikiLink\" href=\"/show/some-project/wiki/Technical+Documentation\">Technical Documentation</a> (Owner)</span></td>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>";

  public void testHTMLTableToWiki() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample1, "", project.getId());
    assertEquals("" +
        "||ConcourseConnect||ConcourseSuite||\n" +
        "|ConcourseConnect is the first platform that enables the creation of true commercial networks. Commercial networks are the intersection of social networking, web 2.0 collaboration and sales and marketing tools.\n" +
        "!\n" +
        "!ConcourseConnect enables organizations to create dynamic communities to connect various stakeholders and manage the entire ecosystem with an integrated management console and backend CRM tools. [[Security, Registration, Invitation|Installation Options]]|ConcourseSuite is Concursive's dedicated Customer Relationship Management (CRM) product. It is a complete front office solution that integrates CRM, website creation, content management and team collaboration capabilities into one easy to use, easy to deploy solution that is available in both hosted and on-premise configurations. [[Technical Documentation]] (Owner)|\n" +
        "", wiki);

    // Read it, re-write it, and compare
    Wiki thisWiki = new Wiki();
    thisWiki.setProjectId(project.getId());
    thisWiki.setContent(wiki);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    assertEquals(wiki, HTMLToWikiUtils.htmlToWiki(html, "", project.getId()));
  }
}
