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
package com.concursive.connect.web.modules.profile.dao;

import com.concursive.connect.indexer.AbstractLuceneTest;
import com.concursive.connect.web.modules.profile.dao.Project;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexWriter;

/**
 * Tests common project lucene access
 *
 * @author matt rajkowski
 * @created May 7, 2008
 */
public class ProjectLuceneTest extends AbstractLuceneTest {

  public void testProjectSearch() throws Exception {

    // The indexer are reusable
    ProjectIndexer indexer = new ProjectIndexer();

    // Add some projects
    Project project = new Project();
    project.setId(1);
    project.setTitle("Test project");
    project.setShortDescription("This is a test of the description");
    indexer.add(snowballWriter, project, true);
    project.setId(2);
    indexer.add(snowballWriter, project, true);

    // Various queries against the projects
    QueryParser parser = new QueryParser("contents", snowballAnalyzer);
    {
      Query query = parser.parse("(projectId:1 AND type:project) AND (testing)");
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 1);
    }
    {
      Query query = parser.parse("(projectId:2 AND type:project) AND (testing)");
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 1);
    }
    {
      Query query = parser.parse("(projectId:1 AND type:project) AND (test)");
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 1);
    }
    {
      Query query = parser.parse("(projectId:1 AND type:other) AND (test)");
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 0);
    }
    {
      Query query = parser.parse("(test)");
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 2);

      Document document = hits.doc(0);
      assertEquals("1", document.get("projectId"));
      assertEquals("Test project This is a test of the description", document.get("contents"));
    }
    {
      Query query = parser.parse("(testing)");
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 2);
    }
    {
      Query query = parser.parse("(projectId:3)");
      Hits hits = getSnowballSearcher().search(query);
      assertTrue(hits.length() == 0);
    }
  }
}
