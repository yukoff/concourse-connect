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

package com.concursive.connect.web.modules.plans.dao;

import com.concursive.commons.content.ContentUtils;
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

/**
 * Class for working with the Lucene search engine
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 17, 2004
 */
public class AssignmentNoteIndexer implements Indexer {

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
    PreparedStatement pst = db.prepareStatement("SELECT s.status_id, s.assignment_id, s.user_id, s.description, s.status_date, " +
        "a.project_id " +
        "FROM project_assignments_status s " +
        "LEFT JOIN project_assignments a ON s.assignment_id = a.assignment_id " +
        "WHERE s.status_id > -1 ");
    ResultSet rs = pst.executeQuery();
    while (rs.next() && context.getEnabled()) {
      ++count;
      // read the record
      AssignmentNote assignmentNote = new AssignmentNote();
      assignmentNote.setId(rs.getInt("status_id"));
      assignmentNote.setAssignmentId(rs.getInt("assignment_id"));
      assignmentNote.setUserId(rs.getInt("user_id"));
      assignmentNote.setDescription(rs.getString("description"));
      assignmentNote.setEntered(rs.getTimestamp("status_date"));
      assignmentNote.setProjectId(rs.getInt("project_id"));
      // add the document
      writer.indexAddItem(assignmentNote);
    }
    rs.close();
    pst.close();
    System.out.println("AssignmentNoteIndexer-> Finished: " + count);
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
    AssignmentNote assignmentNote = (AssignmentNote) item;
    // use the project's general access to speedup guest projects
    Project project = ProjectUtils.loadProject(assignmentNote.getProjectId());
    // add the document
    Document document = new Document();
    document.add(new Field("type", "activitynote", Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("assignmentNoteId", String.valueOf(assignmentNote.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("assignmentId", String.valueOf(assignmentNote.getAssignmentId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectId", String.valueOf(assignmentNote.getProjectId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("guests", (project.getFeatures().getAllowGuests() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("participants", (project.getFeatures().getAllowParticipants() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    // determine if membership is needed for this content based on a guest's access to the data
    int membership = project.getFeatures().getMembershipRequired() ? 1 : 0;
    if (membership == 1 && ProjectUtils.hasAccess(project.getId(), UserUtils.createGuestUser(), "project-plan-view")) {
      membership = 0;
    }
    document.add(new Field("membership", String.valueOf(membership), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("title", ContentUtils.toText(assignmentNote.getDescription()), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("titleLower", ContentUtils.toText(assignmentNote.getDescription()).toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("contents",
        ContentUtils.toText(assignmentNote.getDescription()), Field.Store.YES, Field.Index.TOKENIZED));
    if (modified) {
      document.add(new Field("modified", String.valueOf(System.currentTimeMillis()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    } else {
      document.add(new Field("modified", String.valueOf(assignmentNote.getEntered().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    writer.addDocument(document);
    if (System.getProperty("DEBUG") != null && modified) {
      System.out.println("AssignmentNoteIndexer-> Added: " + assignmentNote.getId());
    }
  }


  /**
   * Gets the searchTerm attribute of the AssignmentIndexer class
   *
   * @param item Description of the Parameter
   * @return The searchTerm value
   */
  public Term getSearchTerm(Object item) {
    AssignmentNote assignmentNote = (AssignmentNote) item;
    return new Term("assignmentNoteId", String.valueOf(assignmentNote.getId()));
  }


  /**
   * Gets the deleteTerm attribute of the AssignmentIndexer class
   *
   * @param item Description of the Parameter
   * @return The deleteTerm value
   */
  public Term getDeleteTerm(Object item) {
    AssignmentNote assignmentNote = (AssignmentNote) item;
    return new Term("assignmentNoteId", String.valueOf(assignmentNote.getId()));
  }
}

