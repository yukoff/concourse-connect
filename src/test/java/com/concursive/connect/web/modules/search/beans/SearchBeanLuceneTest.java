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
package com.concursive.connect.web.modules.search.beans;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.indexer.AbstractLuceneTest;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectIndexer;
import com.concursive.connect.web.modules.search.utils.SearchUtils;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * Tests search queries
 *
 * @author matt rajkowski
 * @created October 13, 2008
 */
public class SearchBeanLuceneTest extends AbstractLuceneTest {

  public void testSearchRules() throws Exception {

    // Index a few projects to compare search results
    Project project = new Project();

    DateFormatSymbols symbols = new DateFormatSymbols(Locale.US);

    for (int i = 0; i < 12; i++) {

      String month = symbols.getMonths()[i];
      String shortMonth = symbols.getShortMonths()[i];

      // Basic project info
      project.setId(i);
      project.setCategoryId(1);
      project.setAllowGuests(true);
      project.setMembershipRequired(false);
      project.setApproved(true);
      project.setClosed(false);
      project.setPortal(false);

      // Unique Title
      project.setTitle(month + " test project");
      project.setShortDescription(month + " description word");
      project.setKeywords(shortMonth);

      // Unique Location
      project.setCity("Virginia Beach");
      project.setState("VA");
      project.setPostalCode("234" + String.valueOf(50 + i));

      // Random Rating for now
      project.setRatingCount(i + 1);
      project.setRatingValue(project.getRatingCount() * StringUtils.rand(1, 5));
      project.setRatingAverage(project.getRatingValue() / project.getRatingCount());

      // Index it
      ProjectIndexer indexer = new ProjectIndexer();
      indexer.add(snowballWriter, project, true);
    }

    QueryParser parser = new QueryParser("contents", snowballAnalyzer);

    {
      // Make sure a single matching term yields a single hit
      SearchBean search = new SearchBean();
      search.setQuery(symbols.getMonths()[0]);
      search.setLocation("Virginia Beach");
      assertNotNull(search.getQuery());
      assertNotNull(search.getLocation());
      search.parseQuery();
      assertTrue(search.isValid());

      String queryString = SearchUtils.generateProjectQueryString(search, UserUtils.createGuestUser().getId(), -1);
      assertNotNull(queryString);

      // (approved:1)
      // AND (guests:1)
      // AND (closed:0)
      // AND (website:0)
      // AND ("january"^20 OR january^15 OR january*^4) AND (location:("virginia beach"^30))

      Query query = parser.parse(queryString);
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 1);
    }

    {
      // Make sure a single matching term stem yields a single hit
      SearchBean search = new SearchBean();
      search.setQuery(symbols.getMonths()[0] + "'s");
      search.setLocation("Virginia Beach");
      assertNotNull(search.getQuery());
      assertNotNull(search.getLocation());
      search.parseQuery();
      assertTrue(search.isValid());

      String queryString = SearchUtils.generateProjectQueryString(search, UserUtils.createGuestUser().getId(), -1);
      assertNotNull(queryString);

      // (approved:1)
      // AND (guests:1)
      // AND (closed:0)
      // AND (website:0)
      // AND ("january's"^20 OR january's^15 OR january's*^4) AND (location:("virginia beach"^30))

      Query query = parser.parse(queryString);
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 1);
    }

    {
      // Make sure multiple matching words yield two hits
      SearchBean search = new SearchBean();
      search.setQuery(symbols.getMonths()[0] + " " + symbols.getMonths()[1]);
      search.setLocation("Virginia Beach");
      search.parseQuery();
      assertTrue(search.isValid());

      String queryString = SearchUtils.generateProjectQueryString(search, UserUtils.createGuestUser().getId(), -1);
      assertNotNull(queryString);

      // (approved:1)
      // AND (guests:1)
      // AND (closed:0)
      // AND (website:0)
      // AND ("january february"^20 OR january^15 OR february^14 OR january*^4 OR february*^3) AND (location:("virginia beach"^30))

      Query query = parser.parse(queryString);
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 2);
    }

    {
      // Make sure wilcards yield multiple hits
      SearchBean search = new SearchBean();
      search.setQuery("j");
      search.setLocation("Virginia Beach");
      search.parseQuery();
      assertTrue(search.isValid());

      // Look for data with a "j" for comparison
      int jCount = 0;
      for (int i = 0; i < symbols.getMonths().length; i++) {
        if (symbols.getMonths()[i].toLowerCase().indexOf("j") > -1 ||
            symbols.getShortMonths()[i].toLowerCase().indexOf("j") > -1) {
          ++jCount;
        }
      }
      assertTrue(jCount > 0);

      String queryString = SearchUtils.generateProjectQueryString(search, UserUtils.createGuestUser().getId(), -1);
      assertNotNull(queryString);

      // (approved:1)
      // AND (guests:1)
      // AND (closed:0)
      // AND (website:0)
      // AND ("j"^20 OR j^15 OR j*^4) AND (location:("virginia beach"^30))

      Query query = parser.parse(queryString);
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == jCount);
    }

    {
      // Make sure alternate locations do not yield any hits
      SearchBean search = new SearchBean();
      search.setQuery(symbols.getMonths()[0]);
      search.setLocation("Norfolk");
      search.parseQuery();

      String queryString = SearchUtils.generateProjectQueryString(search, UserUtils.createGuestUser().getId(), -1);

      Query query = parser.parse(queryString);
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 0);
    }

    {
      // Make sure locations as query terms do not yield any hits
      SearchBean search = new SearchBean();
      search.setQuery("Virginia Beach");
      search.setLocation("Virginia Beach");
      search.parseQuery();

      String queryString = SearchUtils.generateProjectQueryString(search, UserUtils.createGuestUser().getId(), -1);

      Query query = parser.parse(queryString);
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 0);
    }
  }
}