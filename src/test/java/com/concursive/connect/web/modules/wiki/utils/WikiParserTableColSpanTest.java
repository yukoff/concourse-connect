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
 * @created February 1, 2010
 */
public class WikiParserTableColSpanTest extends AbstractConnectionPoolTest {

  protected final static String htmlSample1 =
      "<table class=\"wikiTable\">\n" +
          "<tbody>\n" +
          "<tr>\n" +
          "<th colspan=\"2\">Database Servers</th>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.postgresql.org\" target=\"_blank\">PostgreSQL</a></p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>7.4 - 8.2.4</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.microsoft.com/sql\" target=\"_blank\">Microsoft SQL Server</a></p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>2000-2005</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://msdn.microsoft.com/sql/express\" target=\"_blank\">Microsoft SQL Server Express</a></p>\n" +
          "</td>\n" +
          "<td> </td>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.daffodildb.com/daffodildb.html\" target=\"_blank\">Daffodil DB</a></p>\n" +
          "</td>\n" +
          "<td> </td>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.daffodildb.com/one-dollar-db.html\" target=\"_blank\">One$DB</a></p>\n" +
          "</td>\n" +
          "<td> </td>\n" +
          "</tr>\n" +
          "<tr>\n" +
          "<th colspan=\"2\"> Preliminary support in source version only</th>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.ibm.com/db2\" target=\"_blank\">DB2 UDB Express Edition</a></p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>8.2 - 9</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.firebirdsql.org\" target=\"_blank\">FirebirdSQL</a></p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>1.5 - 2</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row2\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.oracle.com\" target=\"_blank\">Oracle</a></p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>10g</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "<tr class=\"row1\">\n" +
          "<td>\n" +
          "<p><a class=\"wikiLink external\" href=\"http://www.mysql.org\" target=\"_blank\">MySQL</a></p>\n" +
          "</td>\n" +
          "<td>\n" +
          "<p>5.0</p>\n" +
          "</td>\n" +
          "</tr>\n" +
          "</tbody>\n" +
          "</table>";

  public void testHTMLTableColSpanToWiki() throws Exception {
    // Stage a project for the cache
    Project project = new Project();
    project.setId(9999999);
    project.setTitle("Some Project");
    project.setUniqueId("some-project");
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_CACHE, 9999999, project);
    CacheUtils.updateValue(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, "some-project", 9999999);

    String wiki = HTMLToWikiUtils.htmlToWiki(htmlSample1, "", project.getId());
    assertEquals("" +
        "||||Database Servers||\n" +
        "|[[http://www.postgresql.org PostgreSQL]]|7.4 - 8.2.4|\n" +
        "|[[http://www.microsoft.com/sql Microsoft SQL Server]]|2000-2005|\n" +
        "|[[http://msdn.microsoft.com/sql/express Microsoft SQL Server Express]]| |\n" +
        "|[[http://www.daffodildb.com/daffodildb.html Daffodil DB]]| |\n" +
        "|[[http://www.daffodildb.com/one-dollar-db.html One$DB]]| |\n" +
        "|||| Preliminary support in source version only||\n" +
        "|[[http://www.ibm.com/db2 DB2 UDB Express Edition]]|8.2 - 9|\n" +
        "|[[http://www.firebirdsql.org FirebirdSQL]]|1.5 - 2|\n" +
        "|[[http://www.oracle.com Oracle]]|10g|\n" +
        "|[[http://www.mysql.org MySQL]]|5.0|\n" +
        "", wiki);

    // Read it, re-write it, and compare
    Wiki thisWiki = new Wiki();
    thisWiki.setProjectId(project.getId());
    thisWiki.setContent(wiki);
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(thisWiki, new HashMap<String, ImageInfo>(), -1, false, "");
    String html = WikiToHTMLUtils.getHTML(wikiContext, null);
    // The following is useful for debug, it of course won't match the original 
//    assertEquals(htmlSample1, html);
    assertEquals(wiki, HTMLToWikiUtils.htmlToWiki(html, "", project.getId()));
  }
}