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

package com.concursive.connect.web.modules.issues.dao;

import com.concursive.commons.content.ContentUtils;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.Indexer;
import com.concursive.connect.indexer.IndexerContext;
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
 * @created May 27, 2004
 */
public class TicketIndexer implements Indexer {

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
        "SELECT ticketid, project_id, problem, solution, location, cause, modified, enteredby, key_count " +
            "FROM ticket t, ticketlink_project l " +
            "WHERE t.ticketid = l.ticket_id ");
    ResultSet rs = pst.executeQuery();
    while (rs.next() && context.getEnabled()) {
      ++count;
      // read the record
      Ticket ticket = new Ticket();
      ticket.setId(rs.getInt("ticketid"));
      ticket.setProjectId(rs.getInt("project_id"));
      ticket.setProblem(rs.getString("problem"));
      ticket.setSolution(rs.getString("solution"));
      ticket.setLocation(rs.getString("location"));
      ticket.setCause(rs.getString("cause"));
      ticket.setModified(rs.getTimestamp("modified"));
      ticket.setEnteredBy(rs.getInt("enteredby"));
      ticket.setProjectTicketCount(rs.getInt("key_count"));
      // add the document
      writer.indexAddItem(ticket, false);
    }
    rs.close();
    pst.close();
    System.out.println("TicketIndexer-> Finished: " + count);
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
    Ticket ticket = (Ticket) item;
    // use the project's general access to speedup guest projects
    Project project = ProjectUtils.loadProject(ticket.getProjectId());
    // populate the document
    Document document = new Document();
    document.add(new Field("type", "ticket", Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("ticketId", String.valueOf(ticket.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectTicketId", String.valueOf(ticket.getProjectTicketCount()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectId", String.valueOf(ticket.getProjectId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectCategoryId", String.valueOf(project.getCategoryId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("guests", (project.getFeatures().getAllowGuests() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("participants", (project.getFeatures().getAllowParticipants() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("membership", (project.getFeatures().getMembershipRequired() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("title", "#" + ticket.getProjectTicketCount() + " " +
        (ticket.getProblem().length() > 150 ? ContentUtils.toText(ticket.getProblem().substring(0, 150)) : ContentUtils.toText(ticket.getProblem())), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("titleLower", "#" + ticket.getProjectTicketCount() + " " +
        (ticket.getProblem().length() > 150 ? ContentUtils.toText(ticket.getProblem().substring(0, 150)) : ContentUtils.toText(ticket.getProblem())).toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("contents",
        ContentUtils.toText("#" + ticket.getProjectTicketCount()) + " " +
            ContentUtils.toText(ticket.getProblem()) + " " +
            ContentUtils.toText(ticket.getSolution()) + " " +
            ContentUtils.toText(ticket.getLocation()) + " " +
            ContentUtils.toText(ticket.getCause()), Field.Store.YES, Field.Index.TOKENIZED));
    if (modified) {
      document.add(new Field("modified", String.valueOf(System.currentTimeMillis()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    } else {
      document.add(new Field("modified", String.valueOf(ticket.getModified().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    document.add(new Field("enteredBy", String.valueOf(ticket.getEnteredBy()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    writer.addDocument(document);
    if (System.getProperty("DEBUG") != null && modified) {
      System.out.println("TicketIndexer-> Added: " + ticket.getId());
    }
  }


  /**
   * Gets the searchTerm attribute of the TicketIndexer class
   *
   * @param item Description of the Parameter
   * @return The searchTerm value
   */
  public Term getSearchTerm(Object item) {
    Ticket ticket = (Ticket) item;
    return new Term("ticketId", String.valueOf(ticket.getId()));
  }


  /**
   * Gets the deleteTerm attribute of the TicketIndexer class
   *
   * @param item Description of the Parameter
   * @return The deleteTerm value
   */
  public Term getDeleteTerm(Object item) {
    Ticket ticket = (Ticket) item;
    return new Term("ticketId", String.valueOf(ticket.getId()));
  }
}

