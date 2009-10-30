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

package com.concursive.connect.web.modules.lists.dao;

import com.concursive.connect.Constants;
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
public class TaskCategoryIndexer implements Indexer {

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
        "SELECT code, project_id, description " +
            "FROM lookup_task_category t, taskcategory_project c " +
            "WHERE t.code = c.category_id ");
    ResultSet rs = pst.executeQuery();
    while (rs.next() && context.getEnabled()) {
      ++count;
      // read the record
      TaskCategory taskCategory = new TaskCategory();
      taskCategory.setId(rs.getInt("code"));
      taskCategory.setLinkModuleId(Constants.TASK_CATEGORY_PROJECTS);
      taskCategory.setLinkItemId(rs.getInt("project_id"));
      taskCategory.setDescription(rs.getString("description"));
      // add the document
      writer.indexAddItem(taskCategory, false);
    }
    rs.close();
    pst.close();
    System.out.println("TaskCategoryIndexer-> Finished: " + count);
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
    TaskCategory taskCategory = (TaskCategory) item;
    // use the project's general access to speedup guest projects
    Project project = ProjectUtils.loadProject(taskCategory.getLinkItemId());
    // add the document
    Document document = new Document();
    document.add(new Field("type", "listCategory", Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("listCategoryKeyId", String.valueOf(taskCategory.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("listCategoryId", String.valueOf(taskCategory.getId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectId", String.valueOf(taskCategory.getLinkItemId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("projectCategoryId", String.valueOf(project.getCategoryId()), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("guests", (project.getFeatures().getAllowGuests() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("participants", (project.getFeatures().getAllowParticipants() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("membership", (project.getFeatures().getMembershipRequired() ? "1" : "0"), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("title", taskCategory.getDescription(), Field.Store.YES, Field.Index.TOKENIZED));
    document.add(new Field("titleLower", taskCategory.getDescription().toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED));
    document.add(new Field("contents", taskCategory.getDescription(), Field.Store.YES, Field.Index.TOKENIZED));
    /*
     *  if (modified) {
     *  document.add(Field.Keyword("modified", String.valueOf(System.currentTimeMillis())));
     *  } else {
     *  document.add(Field.Keyword("modified", String.valueOf(taskCategory.getModified().getTime())));
     *  }
     */
    writer.addDocument(document);
    if (System.getProperty("DEBUG") != null && modified) {
      System.out.println("TaskCategoryIndexer-> Added: " + taskCategory.getId());
    }
  }


  /**
   * Gets the searchTerm attribute of the TaskCategoryIndexer class
   *
   * @param item Description of the Parameter
   * @return The searchTerm value
   */
  public Term getSearchTerm(Object item) {
    TaskCategory taskCategory = (TaskCategory) item;
    return new Term("listCategoryKeyId", String.valueOf(taskCategory.getId()));
  }


  /**
   * Gets the deleteTerm attribute of the TaskCategoryIndexer class
   *
   * @param item Description of the Parameter
   * @return The deleteTerm value
   */
  public Term getDeleteTerm(Object item) {
    TaskCategory taskCategory = (TaskCategory) item;
    return new Term("listCategoryId", String.valueOf(taskCategory.getId()));
  }
}

