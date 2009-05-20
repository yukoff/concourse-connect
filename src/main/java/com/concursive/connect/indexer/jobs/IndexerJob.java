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

import com.concursive.connect.Constants;
import com.concursive.connect.indexer.IIndexerService;
import com.concursive.connect.indexer.IndexEvent;
import com.concursive.connect.indexer.IndexerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import javax.servlet.ServletContext;
import java.util.Vector;

/**
 * A queue for adding to the Lucene Indexes
 *
 * @author matt rajkowski
 * @created Jun 22, 2005
 */

public class IndexerJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(IndexerJob.class);

  public static final String INDEX_ARRAY = "IndexArray";

  public static void init(SchedulerContext schedulerContext) {
    schedulerContext.put(INDEX_ARRAY, new Vector());
    LOG.info("Indexer queue initialized");
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {
    SchedulerContext schedulerContext = null;
    try {
      schedulerContext = context.getScheduler().getContext();
      // Determine the indexer service
      IIndexerService indexer = IndexerFactory.getInstance().getIndexerService();
      if (indexer == null) {
        throw (new JobExecutionException("Indexer Configuration error: No indexer defined."));
      }
      // Determine if the indexer job can run
      boolean canExecute = true;
      ServletContext servletContext = (ServletContext) schedulerContext.get("ServletContext");
      if (servletContext != null) {
        // If used in a servlet environment, make sure the indexer is initialized
        canExecute = "true".equals(servletContext.getAttribute(Constants.DIRECTORY_INDEX_INITIALIZED));
      }
      // Execute the indexer
      if (canExecute) {
        Vector eventList = (Vector) schedulerContext.get(INDEX_ARRAY);
        if (eventList.size() > 0) {
          LOG.debug("Indexing data... " + eventList.size());
          indexer.obtainWriterLock();
          try {
            while (eventList.size() > 0) {
              IndexEvent indexEvent = (IndexEvent) eventList.get(0);
              if (indexEvent.getAction() == IndexEvent.ADD) {
                // The object was either added or updated
                indexer.indexAddItem(indexEvent.getItem());
              } else if (indexEvent.getAction() == IndexEvent.DELETE) {
                // Delete the item and related data
                indexer.indexDeleteItem(indexEvent.getItem());
              }
              eventList.remove(0);
            }
          } catch (Exception e) {
            LOG.error("Indexing error", e);
            throw new JobExecutionException(e.getMessage());
          } finally {
            indexer.releaseWriterLock();
          }
        }
      }
    } catch (Exception e) {
      LOG.error("Indexing job error", e);
      throw new JobExecutionException(e.getMessage());
    }
  }

}
