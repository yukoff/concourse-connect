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
package com.concursive.connect.indexer;

import com.concursive.connect.Constants;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * User: jfielek
 * Date: Dec 17, 2008
 * Time: 2:40:07 PM
 */
public class LuceneIndexerSearch implements IIndexerSearch {

  private static Log LOG = LogFactory.getLog(LuceneIndexerSearch.class);

  protected IndexSearcher searcher = null;
  protected Analyzer analyzer = null;
  protected int indexType = Constants.UNDEFINED;

  // Initial configuration
  public void setup(Directory index, Analyzer analyzer, int indexType) throws IOException {
    this.indexType = indexType;
    try {
      this.searcher = new IndexSearcher(index);
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
    this.analyzer = analyzer;
  }

  public void teardown() throws IOException {
    if (searcher != null) {
      searcher.close();
    }
  }

  public void search(IndexerQueryResultList response) {
    LOG.debug("Search called, using search index type: " + indexType);
    QueryParser parser = new QueryParser("contents", analyzer);
    try {
      Hits hits;
      Query query = parser.parse(response.getQueryString());
      if (response.getPagedListInfo().getColumnToSortBy() == null) {
        hits = searcher.search(query);
      } else {
        Sort sort = new Sort(new SortField(response.getPagedListInfo().getColumnToSortBy()));
        hits = searcher.search(query, sort);
      }
      // Convert hits to IndexerResponse objects...
      convertHits(response, hits);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void convertHits(IndexerQueryResultList response, Hits hits) {
    PagedListInfo pagedListInfo = response.getPagedListInfo();

    // No need to process any further if no hits found
    if (hits.length() > 0) {

      // The query needs to update the pagedListInfo with the max records
      pagedListInfo.setMaxRecords(hits.length());
      // Use the pagedListInfo properties for sorting below
      int currentOffset = pagedListInfo.getCurrentOffset();
      int itemsPerPage = pagedListInfo.getItemsPerPage();
      int maxRecords = pagedListInfo.getMaxRecords();
      String sortOrder = pagedListInfo.getSortOrder();

      // Determine the requested sort order
      boolean asc = true;
      if ("desc".equalsIgnoreCase(sortOrder)) {
        asc = false;
      }

      // Convert the Lucene hits to a subset
      if (asc) {
        // Determine the startIndex
        int startIndex = currentOffset;
        if (startIndex < 0) {
          startIndex = 0;
        }
        // Determine the upper limit
        int limit = currentOffset + itemsPerPage;
        if (limit == -1 || limit > maxRecords) {
          limit = maxRecords;
        }
        // Retrieve the range of hits
        for (int i = startIndex; i < limit; i++) {
          try {
            Document doc = hits.doc(i);
            IndexerQueryResult responseItem = new IndexerQueryResult(response);
            responseItem.setIndexId(hits.id(i));

            // Get the following fields...
            responseItem.setType(doc.getField("type").stringValue());
            responseItem.setObjectId(doc.getField(responseItem.getType() + "Id").stringValue());
            responseItem.setProjectId(doc.getField("projectId").stringValue());
            responseItem.setGuests(doc.getField("guests").stringValue());
            responseItem.setMembership(doc.getField("membership").stringValue());
            responseItem.setTitle(doc.getField("title").stringValue());
            responseItem.setContents(doc.getField("contents").stringValue());
            responseItem.setModified(doc.getField("modified").stringValue());
            // Following need to be added...
//        LOG.debug("Getting size...");
//        responseItem.setSize(doc.getField("size").stringValue());
//        LOG.debug("Getting filename...");
//        responseItem.setFilename(doc.getField("filename").stringValue());
//        LOG.debug("Getting type...");
//        responseItem.setNewsPortal(doc.getField("newsPortal").stringValue());
//        LOG.debug("Getting type...");
//        responseItem.setNewsStatus(doc.getField("newsStatus").stringValue());
            response.add(responseItem);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } else {
        // Determine the start index
        int startIndex = maxRecords - 1 - currentOffset;
        // Determine the lower limit
        int limit = maxRecords - 1 - currentOffset - itemsPerPage;
        if (limit < -1) {
          limit = -1;
        }
        // Retrieve the range of hits
        for (int i = startIndex; i > limit; i--) {
          try {
            Document doc = hits.doc(i);
            IndexerQueryResult responseItem = new IndexerQueryResult(response);
            responseItem.setIndexId(hits.id(i));

            // Get the following fields...
            responseItem.setType(doc.getField("type").stringValue());
            responseItem.setObjectId(doc.getField(responseItem.getType() + "Id").stringValue());
            responseItem.setProjectId(doc.getField("projectId").stringValue());
            responseItem.setGuests(doc.getField("guests").stringValue());
            responseItem.setMembership(doc.getField("membership").stringValue());
            responseItem.setTitle(doc.getField("title").stringValue());
            responseItem.setContents(doc.getField("contents").stringValue());
            responseItem.setModified(doc.getField("modified").stringValue());
            // Following need to be added...
//        LOG.debug("Getting size...");
//        responseItem.setSize(doc.getField("size").stringValue());
//        LOG.debug("Getting filename...");
//        responseItem.setFilename(doc.getField("filename").stringValue());
//        LOG.debug("Getting type...");
//        responseItem.setNewsPortal(doc.getField("newsPortal").stringValue());
//        LOG.debug("Getting type...");
//        responseItem.setNewsStatus(doc.getField("newsStatus").stringValue());
            response.add(responseItem);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
