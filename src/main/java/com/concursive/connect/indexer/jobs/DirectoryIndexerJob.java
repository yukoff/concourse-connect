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

package com.concursive.connect.indexer.jobs;

import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.IndexerContext;
import com.concursive.connect.indexer.IndexerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;

import javax.servlet.ServletContext;
import java.sql.Connection;

/**
 * A queue for adding to the Lucene Indexes
 *
 * @author matt rajkowski
 * @created February 17, 2009
 */

public class DirectoryIndexerJob implements StatefulJob, InterruptableJob {

  // Logger
  private static Log LOG = LogFactory.getLog(DirectoryIndexerJob.class);

  private IndexerContext indexerContext = null;

  public void execute(JobExecutionContext context) throws JobExecutionException {
    // The RAM index is lazy loaded so that application startup is not blocked.
    // The index can also be interrupted if the application needs to shutdown
    // before the index is loaded.
    SchedulerContext schedulerContext = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      ApplicationPrefs prefs = (ApplicationPrefs) schedulerContext.get("ApplicationPrefs");
      ServletContext servletContext = (ServletContext) schedulerContext.get("ServletContext");
      //@todo remove the servlet context and use the indexer
      if (servletContext != null) {
        IIndexerService indexer = IndexerFactory.getInstance().getIndexerService();
        if (indexer == null) {
          throw (new JobExecutionException("Indexer Configuration error: No indexer defined."));
        }
        // Determine the database connection to use
        Connection db = null;
        ConnectionPool commonCP = (ConnectionPool) servletContext.getAttribute(Constants.CONNECTION_POOL);
        ConnectionElement ce = new ConnectionElement();
        ce.setDriver(prefs.get(ApplicationPrefs.CONNECTION_DRIVER));
        ce.setUrl(prefs.get(ApplicationPrefs.CONNECTION_URL));
        ce.setUsername(prefs.get(ApplicationPrefs.CONNECTION_USER));
        ce.setPassword(prefs.get(ApplicationPrefs.CONNECTION_PASSWORD));
        // Setup the directory index
        indexerContext = new IndexerContext(prefs);
        indexerContext.setIndexType(Constants.INDEXER_DIRECTORY);
        try {
          db = commonCP.getConnection(ce, true);
          indexer.initializeData(indexerContext, db);
        } catch (Exception e) {
          LOG.error("Could not load RAM index", e);
        } finally {
          commonCP.free(db);
        }
        // Tell the indexer it's ok to create other writers now
        servletContext.setAttribute(Constants.DIRECTORY_INDEX_INITIALIZED, "true");
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new JobExecutionException(e.getMessage());
    }
  }

  public void interrupt() throws UnableToInterruptJobException {
    LOG.debug("INTERRUPT CALLED");
    // Do something which prevents the projectIndexer from continuing
    if (indexerContext != null) {
      LOG.debug("INTERRUPTING NOW...");
      indexerContext.setEnabled(false);
    } else {
      LOG.warn("DIDN'T INTERRUPT");
    }

  }
}
