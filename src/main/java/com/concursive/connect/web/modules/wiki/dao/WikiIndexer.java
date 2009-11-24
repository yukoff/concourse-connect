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

package com.concursive.connect.web.modules.wiki.dao;

import com.concursive.commons.content.ContentUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.Indexer;
import com.concursive.connect.indexer.IndexerContext;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLContext;
import com.concursive.connect.web.modules.wiki.utils.WikiToHTMLUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Class for working with the Lucene search engine
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 14, 2006
 */
public class WikiIndexer implements Indexer {

  /**
   * Given a database and a Lucene writer, this method will add content to the
   * searchable index
   *
   * @param writer  Description of the Parameter
   * @param db      Description of the Parameter
   * @param context web app action context
   * @throws java.sql.SQLException Description of the Exception
   * @throws java.io.IOException   Description of the Exception
   */
  public void add(IIndexerService writer, Connection db, IndexerContext context) throws SQLException, IOException {
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT w.* " +
            "FROM project_wiki w " +
            "WHERE w.wiki_id > -1 ");
    ResultSet rs = pst.executeQuery();
    while (rs.next() && context.getEnabled()) {
      ++count;
      Wiki wiki = new Wiki(rs);
      writer.indexAddItem(wiki, false);
    }
    rs.close();
    pst.close();
    if (System.getProperty("DEBUG") != null) {
      System.out.println("WikiIndexer-> Finished: " + count);
    }
  }


  /**
   * Description of the Method
   *
   * @param writer   Description of the Parameter
   * @param item     Description of the Parameter
   * @param modified Description of the Parameter
   * @throws java.io.IOException Description of the Exception
   */
  public void add(IndexWriter writer, Object item, boolean modified) throws IOException {
    Wiki wiki = (Wiki) item;
    // use the project's general access to speedup guest projects
    Project project = ProjectUtils.loadProject(wiki.getProjectId());
    // Use the wiki context
    WikiToHTMLContext wikiContext = new WikiToHTMLContext(wiki, new HashMap(), -1, false, "");
    // add the document
    Document document = new Document();
    document.add(new Field("type", "wiki", Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("wikiId", String.valueOf(wiki.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectId", String.valueOf(wiki.getProjectId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectCategoryId", String.valueOf(project.getCategoryId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    // use the project to determine when this data can be viewed...
    document.add(new Field("guests", (project.getFeatures().getAllowGuests() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("participants", (project.getFeatures().getAllowParticipants() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    // determine if membership is needed for this content based on a guest's access to the data
    int membership = project.getFeatures().getMembershipRequired() ? 1 : 0;
    if (membership == 1 && ProjectUtils.hasAccess(project.getId(), UserUtils.createGuestUser(), "project-wiki-view")) {
      membership = 0;
    }
    document.add(new Field("membership", String.valueOf(membership), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("title", wiki.getSubject(), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("titleLower", wiki.getSubject().toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(
        new Field(
            "contents",
            wiki.getSubject() + " " +
                ContentUtils.toText(ContentUtils.stripHTML(StringUtils.fromHtmlValue(WikiToHTMLUtils.getHTML(wikiContext, null)))), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("subjectLink", wiki.getSubjectLink(), Field.Store.YES, Field.Index.TOKENIZED));
    if (modified) {
      document.add(new Field("modified", String.valueOf(System.currentTimeMillis()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    } else {
      document.add(new Field("modified", String.valueOf(wiki.getModified().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    writer.addDocument(document);
    if (System.getProperty("DEBUG") != null && modified) {
      System.out.println("WikiIndexer-> Added: " + wiki.getId());
    }
  }


  /**
   * Gets the unique searchTerm attribute of the WikiIndexer class
   *
   * @param item Description of the Parameter
   * @return The searchTerm value
   */
  public Term getSearchTerm(Object item) {
    Wiki wiki = (Wiki) item;
    return new Term("wikiId", String.valueOf(wiki.getId()));
  }


  /**
   * Gets the deleteTerm attribute of the WikiIndexer class
   *
   * @param item Description of the Parameter
   * @return The deleteTerm value
   */
  public Term getDeleteTerm(Object item) {
    Wiki wiki = (Wiki) item;
    return new Term("wikiId", String.valueOf(wiki.getId()));
  }
}
