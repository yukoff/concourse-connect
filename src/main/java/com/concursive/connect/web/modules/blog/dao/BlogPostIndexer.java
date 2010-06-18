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

package com.concursive.connect.web.modules.blog.dao;

import com.concursive.commons.content.ContentUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.Indexer;
import com.concursive.connect.indexer.IndexerContext;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Indexer for blog posts
 *
 * @author matt rajkowski
 * @created May 27, 2004
 */
public class BlogPostIndexer implements Indexer {

  /**
   * Given a database and a Lucene writer, this method will add content to the
   * searchable index
   *
   * @param writer  Description of the Parameter
   * @param db      Description of the Parameter
   * @param context
   * @throws SQLException Description of the Exception
   * @throws IOException  Description of the Exception
   */
  public void add(IIndexerService writer, Connection db, IndexerContext context) throws SQLException, IOException {
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT news_id, project_id, subject, intro, message, start_date, status, " +
            "classification_id, template_id, portal_key " +
            "FROM project_news " +
            "WHERE project_id > -1 ");
    ResultSet rs = pst.executeQuery();
    while (rs.next() && context.getEnabled()) {
      ++count;
      // read the record
      BlogPost blogPost = new BlogPost();
      blogPost.setId(rs.getInt("news_id"));
      blogPost.setProjectId(rs.getInt("project_id"));
      blogPost.setSubject(rs.getString("subject"));
      blogPost.setIntro(rs.getString("intro"));
      blogPost.setMessage(rs.getString("message"));
      blogPost.setStartDate(rs.getTimestamp("start_date"));
      blogPost.setStatus(DatabaseUtils.getInt(rs, "status"));
      blogPost.setClassificationId(DatabaseUtils.getInt(rs, "classification_id"));
      blogPost.setTemplateId(DatabaseUtils.getInt(rs, "template_id"));
      blogPost.setPortalKey(rs.getString("portal_key"));
      // add to index
      writer.indexAddItem(blogPost, false);
    }
    rs.close();
    pst.close();
    if (System.getProperty("DEBUG") != null) {
      System.out.println("PostIndexer-> Finished: " + count);
    }
  }


  /**
   * Description of the Method
   *
   * @param writer   Description of the Parameter
   * @param item     Description of the Parameter
   * @param modified Description of the Parameter
   * @throws IOException Description of the Exception
   */
  public void add(IndexWriter writer, Object item, boolean modified) throws IOException {
    BlogPost article = (BlogPost) item;
    // add the document
    Document document = new Document();
    document.add(new Field("type", "news", Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("newsId", String.valueOf(article.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectId", String.valueOf(article.getProjectId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    if (article.getProjectId() > -1) {
      // use the project's general access to speedup guest projects
      Project project = ProjectUtils.loadProject(article.getProjectId());
      document.add(new Field("projectCategoryId", String.valueOf(project.getCategoryId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
      document.add(new Field("guests", (project.getFeatures().getAllowGuests() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
      document.add(new Field("participants", (project.getFeatures().getAllowParticipants() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
      document.add(new Field("instanceId", String.valueOf(project.getInstanceId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
      // determine if membership is needed for this content based on a guest's access to the data
      int membership = project.getFeatures().getMembershipRequired() ? 1 : 0;
      if (membership == 1 && ProjectUtils.hasAccess(project.getId(), UserUtils.createGuestUser(), "project-news-view")) {
        membership = 0;
      }
      document.add(new Field("membership", String.valueOf(membership), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    document.add(new Field("title", article.getSubject(), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("titleLower", article.getSubject().toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("contents",
        article.getSubject() + " " +
            ContentUtils.toText(ContentUtils.stripHTML(article.getIntro())) + " " +
            ContentUtils.toText(ContentUtils.stripHTML(article.getMessage())), Field.Store.YES, Field.Index.TOKENIZED));
    if (article.getStartDate() != null) {
      document.add(new Field("modified", String.valueOf(article.getStartDate().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
      document.add(new Field("newsDate", String.valueOf(formatter.format(article.getStartDate())), Field.Store.YES, Field.Index.UN_TOKENIZED));
    } else {
      // If there is no date, set the date to an empty string to maintain the search.
      document.add(new Field("newsDate", "", Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    document.add(new Field("newsStatus", String.valueOf(article.getStatus()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("newsClassificationId", String.valueOf(article.getClassificationId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("newsTemplateId", String.valueOf(article.getTemplateId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    if (article.getPortalKey() != null && !"".equals(article.getPortalKey())) {
      document.add(new Field("newsPortal", "true", Field.Store.YES, Field.Index.UN_TOKENIZED));
      document.add(new Field("newsPortalKey", article.getPortalKey(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    writer.addDocument(document);
    if (System.getProperty("DEBUG") != null && modified) {
      System.out.println("PostIndexer-> Added: " + article.getId());
    }
  }


  /**
   * Gets the unique searchTerm attribute of the NewsArticleIndexer class
   *
   * @param item Description of the Parameter
   * @return The searchTerm value
   */
  public Term getSearchTerm(Object item) {
    BlogPost article = (BlogPost) item;
    return new Term("newsId", String.valueOf(article.getId()));
  }


  /**
   * Gets the deleteTerm attribute of the NewsArticleIndexer class
   *
   * @param item Description of the Parameter
   * @return The deleteTerm value
   */
  public Term getDeleteTerm(Object item) {
    BlogPost article = (BlogPost) item;
    return new Term("newsId", String.valueOf(article.getId()));
  }
}

