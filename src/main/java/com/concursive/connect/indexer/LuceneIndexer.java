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

import com.concursive.commons.api.DataField;
import com.concursive.commons.api.DataRecord;
import com.concursive.commons.api.DataRecordFactory;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: jfielek
 * Date: Dec 16, 2008
 * Time: 3:49:29 PM
 */
public class LuceneIndexer implements IIndexerService {

  private static Log LOG = LogFactory.getLog(LuceneIndexer.class);

  // Only one index reference must exist for the application
  protected Directory fullIndex = null;
  protected Directory directoryIndex = null;
  protected boolean directoryIndexInitialized = false;

  // Only one writer for each index can exist at any given time
  private final ReentrantLock writeLock = new ReentrantLock();
  protected IndexWriter fullWriter = null;
  protected IndexWriter directoryWriter = null;

  // Reuse searchers for performance, and for point-in-time index queries
  protected LuceneIndexerSearch fullSearcher = null;
  protected LuceneIndexerSearch directorySearcher = null;
  private boolean directorySearcherReady = false;

  // Cache the indexer classes for reuse
  private Map<String, Object> classes = new HashMap<String, Object>();


  /**
   * Sets up any Lucene Indexer classes
   *
   * @param context
   * @return
   * @throws Exception
   */
  public boolean setup(IndexerContext context) throws Exception {
    // Make sure the complex queries can use more than the default clause count
    BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

    // Setup the Indexes so they are in a state in which they can immediately
    // be accessed
    ApplicationPrefs prefs = context.getApplicationPrefs();
    {
      // Establish the full directory
      LOG.info("Starting Lucene disk index");
      File path = new File(prefs.get("FILELIBRARY") + Constants.FULL_INDEX);
      boolean create = !path.exists();
      fullIndex = FSDirectory.getDirectory(prefs.get("FILELIBRARY") + Constants.FULL_INDEX);
      if (create) {
        LOG.warn("Lucene index not found, creating new index: " + path.getPath());
        Analyzer fullAnalyzer = new StandardAnalyzer();
        fullWriter = new IndexWriter(fullIndex, fullAnalyzer, true);
        fullWriter.close();
      }
    }

    {
      // Load up the ram directory
      LOG.info("Creating Lucene RAM index...");
      // Create the Ram Directory
      directoryIndex = new RAMDirectory();
      Analyzer directoryAnalyzer = new SnowballAnalyzer("English");
      directoryWriter = new IndexWriter(directoryIndex, directoryAnalyzer, true);
      directoryWriter.close();
      LOG.info("Initialization of RAM index complete...");
    }
    // Search using the updated index
    resetSearchers();
    return true;
  }

  /**
   * Initializes any data
   *
   * @param context
   * @param db
   * @return
   * @throws Exception
   */
  public boolean initializeData(IndexerContext context, Connection db) throws Exception {
    LOG.info("Loading Lucene RAM index...");

    // Load default data into the RAM index since it isn't persisted
    obtainWriterLock();
    try {
      ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.profile.dao.ProjectIndexer")).add(this, db, context);
      directoryIndexInitialized = true;
    } catch (Exception e) {
      LOG.error("Could not initialize RAM index", e);
    } finally {
      releaseWriterLock();
    }
    return true;
  }

  public boolean reindexAllData(IndexerContext context, Connection db) throws Exception {
    writeLock.lock();
    try {
      if (context.getIndexType() == Constants.INDEXER_FULL) {
        // Erase the full index
        LOG.info("Reloading the full index...");
        fullWriter = new IndexWriter(fullIndex, new StandardAnalyzer(), true);
        // Reindex all data
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.profile.dao.ProjectIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.blog.dao.BlogPostIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.wiki.dao.WikiIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.discussion.dao.ForumIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.discussion.dao.TopicIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.discussion.dao.ReplyIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.documents.dao.FileItemIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.lists.dao.TaskCategoryIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.lists.dao.TaskIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.issues.dao.TicketIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.plans.dao.RequirementIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.plans.dao.AssignmentFolderIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.plans.dao.AssignmentIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.plans.dao.AssignmentNoteIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.promotions.dao.AdIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.classifieds.dao.ClassifiedIndexer")).add(this, db, context);
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.reviews.dao.ProjectRatingIndexer")).add(this, db, context);
        // Optimize the index
        fullWriter.flush();
        fullWriter.optimize();
        fullWriter.close();
        // Update the searchers
        resetSearchers();
        return true;
      } else if (context.getIndexType() == Constants.INDEXER_DIRECTORY) {
        // Erase the directory index
        LOG.info("Reloading the directory index...");
        directoryWriter = new IndexWriter(directoryIndex, new SnowballAnalyzer("English"), true);
        // Just the project data belongs in the directory index
        ((Indexer) getObjectIndexer("com.concursive.connect.web.modules.profile.dao.ProjectIndexer")).add(this, db, context);
        // Optimize the index
        directoryWriter.flush();
        directoryWriter.optimize();
        directoryWriter.close();
        // Update the searchers
        resetSearchers();
        return true;
      }
    } catch (Exception e) {
      LOG.error("Erase index error", e);
    } finally {
      writeLock.unlock();
    }
    return false;
  }

  public boolean shutdown() throws IOException {
    writeLock.lock();
    try {
      if (fullIndex != null) {
        fullIndex.close();
        fullIndex = null;
      }
      if (directoryIndex != null) {
        directoryIndex.close();
        directoryIndex = null;
      }
    } catch (Exception e) {
      LOG.error("Shutdown error", e);
    } finally {
      writeLock.unlock();
    }
    return false;
  }

  public void obtainWriterLock() throws Exception {
    writeLock.lock();

    // Open the full writer
    try {
      fullWriter = new IndexWriter(fullIndex, new StandardAnalyzer());
    } catch (Exception e) {
      LOG.error("Begin indexing error on fullWriter", e);
    }

    // Open the directory writer
    try {
      directoryWriter = new IndexWriter(directoryIndex, new SnowballAnalyzer("English"));
    } catch (Exception e) {
      LOG.error("Begin indexing error on directoryWriter", e);
    }
  }

  public void releaseWriterLock() throws Exception {
    try {
      // Directory Writer
      directoryWriter.flush();
      directoryWriter.optimize();
      directoryWriter.close();
      // Full Writer
      fullWriter.flush();
      fullWriter.optimize();
      fullWriter.close();
    } catch (Exception e) {
      LOG.error("End indexing error", e);
    } finally {
      writeLock.unlock();
    }
    // Let the readers know about the updates
    resetSearchers();
  }

  public boolean indexDeleteItem(Object item) throws IOException {
    // Delete the item and related data
    indexDeleteItem(fullWriter, item, "getDeleteTerm");
    // Project index
    if (item.getClass().getName().equals("com.concursive.connect.web.modules.profile.dao.Project")) {
      // Delete the item and related data
      indexDeleteItem(directoryWriter, item, "getDeleteTerm");
    }
    return true;
  }

  private boolean indexDeleteItem(IndexWriter writer, Object item, String deleteTerm) throws IOException {
    // Determine the object's indexer key term for deletion
    Indexer objectIndexer = (Indexer) getObjectIndexer(item.getClass().getName() + "Indexer");
    // Delete the previous item from the index
    Object o = null;
    try {
      // now we are ready for the next to last step..to call upon the method in the
      // class instance we have.
      Method method = objectIndexer.getClass().getMethod(deleteTerm, Object.class);
      o = method.invoke(objectIndexer, item);
    } catch (NoSuchMethodException nm) {
      LOG.error("No Such Method Exception for method " + deleteTerm + ". MESAGE = " + nm.getMessage(), nm);
    } catch (IllegalAccessException ia) {
      LOG.error("Illegal Access Exception. MESSAGE = " + ia.getMessage(), ia);
    } catch (Exception e) {
      LOG.error("Exception. MESSAGE = " + e.getMessage(), e);
    }
    if (o != null) {
      LOG.debug("Deleting with deleteTerm: " + deleteTerm);
      writer.deleteDocuments((Term) o);
    }
    return true;
  }

  public boolean indexAddItem(Object item) {
    // Adding an item w/o specifically stating the modified state always results in it being
    // treated as modified
    return indexAddItem(item, true);
  }

  public boolean indexAddItem(Object item, boolean modified) {

    DataRecordFactory factory = DataRecordFactory.INSTANCE;
    DataRecord record = factory.parse(item);

    if (LOG.isDebugEnabled()) {
      StringBuffer sb = new StringBuffer();
      for (DataField thisField : record) {
        sb.append(thisField.getName()).append("=");
        if (thisField.hasValue()) {
          sb.append(thisField.getValue());
        }
        sb.append(System.getProperty("line.separator"));
      }
      LOG.debug("Values (modified? " + modified + "):" + System.getProperty("line.separator") + sb.toString());
    }

    try {
      // Add to the index
      LOG.debug("FULL");
      indexAddItem(fullWriter, item, modified);
      if (item.getClass().getName().equals("com.concursive.connect.web.modules.profile.dao.Project")) {
        LOG.debug("DIRECTORY");
        indexAddItem(directoryWriter, item, modified);
      }
    } catch (Exception e) {
      LOG.error("indexAddItem error", e);
    }
    return true;
  }

  public boolean indexAddItem(Object item, boolean modified, int addType) throws Exception {
    // Determine the index type
    if (addType == Constants.INDEXER_FULL) {
      indexAddItem(fullWriter, item, modified);
      return true;
    } else if (addType == Constants.INDEXER_DIRECTORY) {
      indexAddItem(directoryWriter, item, modified);
      return true;
    }
    return false;
  }


  protected boolean indexAddItem(IndexWriter writer, Object item, boolean modified) throws Exception {
    if (writer == null) {
      throw new NullPointerException("Writer is null");
    }
    if (item == null) {
      throw new NullPointerException("Item is null");
    }

    if (modified) {
      // Delete the previous item from the index
      indexDeleteItem(writer, item, "getSearchTerm");
    }
    try {
      Indexer objectIndexer = (Indexer) getObjectIndexer(item.getClass().getName() + "Indexer");
      if (objectIndexer == null) {
        throw new NullPointerException("ObjectIndexer is null for: " + item.getClass().getName() + "Indexer");
      }
      // Add the item to the index
      Object o = null;
      try {
        // now we are ready for the next to last step..to call upon the method in the
        // class instance we have.
        Method method = objectIndexer.getClass().getMethod("add", new Class[]{writer.getClass(), Object.class, boolean.class});
        o = method.invoke(objectIndexer, new Object[]{writer, item, new Boolean(modified)});
      } catch (NoSuchMethodException nm) {
        LOG.error("No Such Method Exception for method add. MESAGE = " + nm.getMessage(), nm);
      } catch (IllegalAccessException ia) {
        LOG.error("Illegal Access Exception. MESSAGE = " + ia.getMessage(), ia);
      } catch (Exception e) {
        LOG.error("Exception. MESSAGE = " + e.getMessage(), e);
      }
    } catch (Exception io) {
      io.printStackTrace(System.out);
      throw new IOException("Writer: " + io.getMessage());
    }
    return true;
  }

  public IIndexerSearch getIndexerSearch(int searchType) {
    if (searchType == Constants.INDEXER_FULL || !directorySearcherReady) {
      return fullSearcher;
    } else if (searchType == Constants.INDEXER_DIRECTORY) {
      return directorySearcher;
    }
    return null;
  }

  public float getIndexSizeInBytes(int searchType) {
    if (searchType == Constants.INDEXER_DIRECTORY && directoryIndex != null) {
      return ((RAMDirectory) directoryIndex).sizeInBytes();
    }
    return -1;
  }

  private void resetSearchers() throws IOException {
    // New analyzers and readers since there is new data
    Analyzer fullAnalyzer = new StandardAnalyzer();
    LuceneIndexerSearch newFullSearcher = new LuceneIndexerSearch();
    newFullSearcher.setup(fullIndex, fullAnalyzer, Constants.INDEXER_FULL);
    fullSearcher = newFullSearcher;

    // New analyzers and readers since there is new data
    Analyzer directoryAnalyzer = new SnowballAnalyzer("English");
    LuceneIndexerSearch newDirectorySearcher = new LuceneIndexerSearch();
    newDirectorySearcher.setup(directoryIndex, directoryAnalyzer, Constants.INDEXER_DIRECTORY);
    directorySearcher = newDirectorySearcher;
    if (directoryIndexInitialized) {
      directorySearcherReady = true;
    }
  }

  private Object getObjectIndexer(String className) {
    Object classRef = null;
    if (classes.containsKey(className)) {
      classRef = classes.get(className);
    } else {
      try {
        classRef = Class.forName(className).newInstance();
        classes.put(className, classRef);
      } catch (ClassNotFoundException cnfe) {
        LOG.warn("Class Not Found Exception. MESSAGE = " + cnfe.getMessage(), cnfe);
      } catch (InstantiationException ie) {
        LOG.error("Instantiation Exception. MESSAGE = " + ie.getMessage(), ie);
      } catch (IllegalAccessException iae) {
        LOG.error("Illegal Argument Exception. MESSAGE = " + iae.getMessage(), iae);
      }
    }
    return classRef;
  }
}
