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

package com.concursive.connect.web.modules.contacts.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.Indexer;
import com.concursive.connect.indexer.IndexerContext;
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
public class ContactIndexer implements Indexer {

  /**
   * Given a database and a Lucene writer, this method will add content to the
   * searchable index
   *
   * @param writer  Description of the Parameter
   * @param db      Description of the Parameter
   * @param context
   * @throws java.sql.SQLException Description of the Exception
   * @throws java.io.IOException   Description of the Exception
   */
  public void add(IIndexerService writer, Connection db, IndexerContext context) throws SQLException, IOException {
    int count = 0;
    PreparedStatement pst = db.prepareStatement(
        "SELECT contact_id, owner, first_name, middle_name, last_name, organization, email1, email2, email3, web_page, comments " +
            "FROM contacts " +
            "WHERE contact_id > -1 ");
    ResultSet rs = pst.executeQuery();
    while (rs.next() && context.getEnabled()) {
      ++count;
      Contact contact = new Contact();
      contact.setId(rs.getInt("contact_id"));
      contact.setOwner(DatabaseUtils.getInt(rs, "owner"));
      contact.setFirstName(rs.getString("first_name"));
      contact.setMiddleName(rs.getString("middle_name"));
      contact.setLastName(rs.getString("last_name"));
      contact.setOrganization(rs.getString("organization"));
      contact.setEmail1(rs.getString("email1"));
      contact.setEmail2(rs.getString("email2"));
      contact.setEmail3(rs.getString("email3"));
      contact.setWebPage(rs.getString("web_page"));
      contact.setComments(rs.getString("comments"));
      // add to index
      writer.indexAddItem(contact, false);
    }
    rs.close();
    pst.close();
    if (System.getProperty("DEBUG") != null) {
      System.out.println("ContactIndexer-> Finished: " + count);
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
    Contact contact = (Contact) item;
    // add the document
    Document document = new Document();
    document.add(new Field("type", "contact", Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("contactId", String.valueOf(contact.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("userId", String.valueOf(contact.getOwner()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("title", contact.getIndexAs(), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("titleLower", contact.getIndexAs().toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("contents",
        contact.getIndexAs() + " " +
            contact.getComments() + " " +
            contact.getWebPage() + " " +
            contact.getEmail1() + " " +
            contact.getEmail1() + " " +
            contact.getEmail2() + " " +
            contact.getEmail3() + " ", Field.Store.YES, Field.Index.TOKENIZED));
    if (contact.getEntered() != null) {
      document.add(new Field("modified", String.valueOf(contact.getEntered().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    }
    writer.addDocument(document);
    if (System.getProperty("DEBUG") != null && modified) {
      System.out.println("ContactIndexer-> Added: " + contact.getId());
    }
  }


  /**
   * Gets the unique searchTerm attribute of the ContactIndexer class
   *
   * @param item Description of the Parameter
   * @return The searchTerm value
   */
  public Term getSearchTerm(Object item) {
    Contact contact = (Contact) item;
    return new Term("contactId", String.valueOf(contact.getId()));
  }


  /**
   * Gets the deleteTerm attribute of the ContactIndexer class
   *
   * @param item Description of the Parameter
   * @return The deleteTerm value
   */
  public Term getDeleteTerm(Object item) {
    Contact contact = (Contact) item;
    return new Term("contactId", String.valueOf(contact.getId()));
  }
}
