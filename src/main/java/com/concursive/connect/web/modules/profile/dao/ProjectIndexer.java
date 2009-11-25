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

import com.concursive.commons.content.ContentUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.Indexer;
import com.concursive.connect.indexer.IndexerContext;
import com.concursive.connect.indexer.LuceneIndexer;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Transforms a project into a document and adds to Lucene search engine
 *
 * @author matt rajkowski
 * @version $Id$
 * @created May 27, 2004
 */
public class ProjectIndexer implements Indexer {

  private static Log LOG = LogFactory.getLog(ProjectIndexer.class);

  /**
   * Given a database and an indexer, this method will add content to the
   * searchable index
   *
   * @param writer  Description of the Parameter
   * @param db      Description of the Parameter
   * @param context Servlet context
   * @throws SQLException Description of the Exception
   * @throws IOException  Description of the Exception
   */
  public void add(IIndexerService writer, Connection db, IndexerContext context) throws Exception {
    long startTime = System.currentTimeMillis();
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT instance_id, project_id, title, shortdescription, description, requestedby, requesteddept, entered, modified, " +
            "category_id, subcategory1_id, " +
            "allow_guests, membership_required, allows_user_observers, approvaldate, closedate, portal, " +
            "city, state, postalcode, keywords, " +
            "rating_count, rating_value, rating_avg " +
            "FROM projects " +
            "WHERE project_id > -1 ");
    ResultSet rs = pst.executeQuery();
    while (rs.next() && context.getEnabled()) {
      ++count;
      // read the record
      Project project = new Project();
      project.setInstanceId(rs.getInt("instance_id"));
      project.setId(rs.getInt("project_id"));
      project.setTitle(rs.getString("title"));
      project.setShortDescription(rs.getString("shortdescription"));
      project.setDescription(rs.getString("description"));
      project.setRequestedBy(rs.getString("requestedby"));
      project.setRequestedByDept(rs.getString("requesteddept"));
      project.setEntered(rs.getTimestamp("entered"));
      project.setModified(rs.getTimestamp("modified"));
      project.setCategoryId(DatabaseUtils.getInt(rs, "category_id"));
      project.setSubCategory1Id(DatabaseUtils.getInt(rs, "subcategory1_id"));
      project.getFeatures().setAllowGuests(rs.getBoolean("allow_guests"));
      project.getFeatures().setMembershipRequired(rs.getBoolean("membership_required"));
      project.getFeatures().setAllowParticipants(rs.getBoolean("allows_user_observers"));
      project.setApprovalDate(rs.getTimestamp("approvaldate"));
      if (project.getApprovalDate() != null) {
        project.setApproved(true);
      }
      project.setCloseDate(rs.getTimestamp("closedate"));
      if (project.getCloseDate() != null) {
        project.setClosed(true);
      }
      project.setPortal(rs.getBoolean("portal"));
      project.setCity(rs.getString("city"));
      project.setState(rs.getString("state"));
      project.setPostalCode(rs.getString("postalcode"));
      project.setKeywords(rs.getString("keywords"));
      project.setRatingCount(rs.getInt("rating_count"));
      project.setRatingValue(rs.getInt("rating_value"));
      project.setRatingAverage((rs.getDouble("rating_avg")));
      // add the document
      if (writer instanceof LuceneIndexer) {
        LuceneIndexer luceneWriter = (LuceneIndexer) writer;
        luceneWriter.indexAddItem(project, false, context.getIndexType());
      } else {
        // Don't know specifically what to do...
        writer.indexAddItem(project, false);
      }
    }
    rs.close();
    pst.close();
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    LOG.info("Finished: " + count + " took " + totalTime + " ms");
  }


  /**
   * Converts a project to a Lucene Document and adds to the index
   *
   * @param writer   Description of the Parameter
   * @param item     Description of the Parameter
   * @param modified Description of the Parameter
   * @throws IOException Description of the Exception
   */
  public void add(IndexWriter writer, Object item, boolean modified) throws IOException {
    Project project = (Project) item;
    // add the document
    Document document = new Document();
    document.add(new Field("type", "project", Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("instanceId", String.valueOf(project.getInstanceId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectKeyId", String.valueOf(project.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectId", String.valueOf(project.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectCategoryId", String.valueOf(project.getCategoryId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectCategoryId1", String.valueOf(project.getSubCategory1Id()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("guests", (project.getFeatures().getAllowGuests() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("participants", (project.getFeatures().getAllowParticipants() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    // determine if membership is needed for this content based on a guest's access to the data
    int membership = project.getFeatures().getMembershipRequired() ? 1 : 0;
    if (membership == 1 && ProjectUtils.hasAccess(project.getId(), UserUtils.createGuestUser(), "project-profile-view")) {
      membership = 0;
    }
    document.add(new Field("membership", String.valueOf(membership), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("approved", ((project.getApproved() || project.getApprovalDate() != null) ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("closed", ((project.getClosed() || project.getCloseDate() != null) ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("website", (project.getPortal() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("title", project.getTitle(), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("titleLower", project.getTitle().toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("keywords", (StringUtils.hasText(project.getKeywords()) ? project.getKeywords() : ""), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("contents",
        project.getTitle() + " " +
            project.getShortDescription() +
            (StringUtils.hasText(project.getDescription()) ? " " + ContentUtils.toText(ContentUtils.stripHTML(project.getDescription())) : "") +
            (StringUtils.hasText(project.getKeywords()) ? " " + project.getKeywords() : ""),
        Field.Store.YES, Field.Index.TOKENIZED));
    if (project.getEntered() == null) {
      document.add(new Field("entered", String.valueOf(System.currentTimeMillis()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    } else {
      document.add(new Field("entered", String.valueOf(project.getEntered().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    if (modified || project.getModified() == null) {
      document.add(new Field("modified", String.valueOf(System.currentTimeMillis()), Field.Store.YES, Field.Index.UN_TOKENIZED));
      document.add(new Field("newsDate", String.valueOf(System.currentTimeMillis()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    } else {
      document.add(new Field("modified", String.valueOf(project.getModified().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
      document.add(new Field("newsDate", String.valueOf(project.getModified().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    document.add(new Field("location",
        project.getLocation(), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("ratingCount", String.valueOf(project.getRatingCount()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("ratingValue", String.valueOf(project.getRatingValue()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("ratingAverage", String.valueOf(project.getRatingAverage()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    writer.addDocument(document);
    if (LOG.isDebugEnabled() && modified) {
      LOG.debug("Added: " + project.getId());
    }
  }


  /**
   * Gets the searchTerm attribute of the ProjectIndexer class to retrieve
   * just the specified project
   *
   * @param item Description of the Parameter
   * @return The searchTerm value
   */
  public Term getSearchTerm(Object item) {
    Project project = (Project) item;
    return new Term("projectKeyId", String.valueOf(project.getId()));
  }


  /**
   * Gets the deleteTerm attribute of the ProjectIndexer class to ensure
   * that ALL project related data is also deleted from the index
   *
   * @param item Description of the Parameter
   * @return The deleteTerm value
   */
  public Term getDeleteTerm(Object item) {
    Project project = (Project) item;
    return new Term("projectId", String.valueOf(project.getId()));
  }
}

