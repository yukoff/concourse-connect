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

package com.concursive.connect.web.modules.common.social.images.jobs;

import com.concursive.commons.images.ImageUtils;
import com.concursive.connect.scheduler.SchedulerUtils;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.StatefulJob;

import java.io.File;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Off-loads image processing
 *
 * @author matt rajkowski
 * @created September 28, 2008
 */

public class ImageResizerJob implements StatefulJob {

  private static Log LOG = LogFactory.getLog(ImageResizerJob.class);

  public static final String IMAGE_RESIZER_ARRAY = "ImageResizerArray";

  public class TransactionTask implements Callable<Thumbnail> {

    private final ImageResizerBean bean;
    private final FileItem fileItem;
    private final int width;
    private final int height;
    private final boolean defaultThumbnail;

    public TransactionTask(ImageResizerBean thisBean, FileItem thisFileItem, int thisWidth, int thisHeight, boolean thisDefaultThumbnail) {
      this.defaultThumbnail = thisDefaultThumbnail;
      this.bean = thisBean;
      this.fileItem = thisFileItem;
      this.width = thisWidth;
      this.height = thisHeight;
    }

    public Thumbnail call() throws Exception {
      // Create a thumbnail, it will be inserted in db after all the calls
      File originalFile = new File(bean.getImagePath() + bean.getImageFilename());
      if (!originalFile.exists()) {
        LOG.error("File does not exist for resizing: " + originalFile.getAbsolutePath());
        return null;
      }
      String thumbnailFilename = "TH";
      if (!defaultThumbnail) {
        thumbnailFilename = "-" + width + "x" + height + "-TH";
      }
      File thumbnailFile = new File(originalFile.getPath() + thumbnailFilename);
      String format = fileItem.getExtension().substring(1);
      Thumbnail thumbnail = new Thumbnail(ImageUtils.saveThumbnail(originalFile, thumbnailFile, width, height, format));
      if (thumbnail != null) {
        //Store thumbnail in database
        thumbnail.setId(fileItem.getId());
        thumbnail.setFilename(fileItem.getFilename() + thumbnailFilename);
        thumbnail.setVersion(fileItem.getVersion());
        thumbnail.setSize((int) thumbnailFile.length());
        thumbnail.setEnteredBy(bean.getEnteredBy());
        thumbnail.setModifiedBy(bean.getEnteredBy());
      } else {
        LOG.error("(Thread) saveThumbnail IS NULL");
      }
      return thumbnail;
    }
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {
    LOG.debug("Starting...");
    SchedulerContext schedulerContext = null;
    Connection db = null;

    // Initial setup
    try {
      schedulerContext = context.getScheduler().getContext();
    } catch (Exception e) {
      LOG.error("ImageResizerJob Exception due to scheduler", e);
      throw new JobExecutionException(e);
    }

    // Process the arrays
    Vector exportList = (Vector) schedulerContext.get(IMAGE_RESIZER_ARRAY);

    while (exportList.size() > 0) {

      // Holds the transactions to be threaded
      List<TransactionTask> renderTasks = new ArrayList<TransactionTask>();

      // Pre-process the files using a database connection
      try {
        db = SchedulerUtils.getConnection(schedulerContext);
        // The imageResizerBean contains the image handle to be processed
        ImageResizerBean bean = (ImageResizerBean) exportList.remove(0);

        LOG.debug("Preparing thumbnails for FileItem (" + bean.getFileItemId() + ")... " + bean.getWidth() + "x" + bean.getHeight());
        // Load the fileItem
        FileItem fileItem = new FileItem(db, bean.getFileItemId());
        if (bean.getWidth() > 0 || bean.getHeight() > 0) {
          // A specific size needs to be rendered
          renderTasks.add(new TransactionTask(bean, fileItem, bean.getWidth(), bean.getHeight(), false));
        } else {
          // No specific size so for each fileItem, generate several sizes of the image
          renderTasks.add(new TransactionTask(bean, fileItem, 210, 150, false));
          renderTasks.add(new TransactionTask(bean, fileItem, 133, 133, true));
          renderTasks.add(new TransactionTask(bean, fileItem, 100, 100, false));
          renderTasks.add(new TransactionTask(bean, fileItem, 75, 75, false));
          renderTasks.add(new TransactionTask(bean, fileItem, 50, 50, false));
          renderTasks.add(new TransactionTask(bean, fileItem, 45, 45, false));
          renderTasks.add(new TransactionTask(bean, fileItem, 30, 30, false));
        }
      } catch (Exception e) {
        LOG.error("ImageResizerJob Exception", e);
        continue;
      } finally {
        SchedulerUtils.freeConnection(schedulerContext, db);
      }

      int threads = 2;
      // Process the files
      ExecutorService executor = null;
      List<Future<Thumbnail>> futures = null;
      try {
        executor = Executors.newFixedThreadPool(threads);
        // NOTE: this wrapper fix is for Java 1.5
        final Collection<Callable<Thumbnail>> wrapper =
            Collections.<Callable<Thumbnail>>unmodifiableCollection(renderTasks);
        LOG.debug("Generating thumbnails... " + renderTasks.size());
        futures = executor.invokeAll(wrapper);
      } catch (InterruptedException e) {
        LOG.error("ImageResizerJob executor exception", e);
        if (executor != null) {
          executor.shutdown();
        }
        throw new JobExecutionException(e);
      }

      // Insert the thumbnails using the database connection
      try {
        db = SchedulerUtils.getConnection(schedulerContext);
        LOG.debug("Inserting thumbnails into database... " + futures.size());
        // Process the executor results
        for (Future<Thumbnail> f : futures) {
          Thumbnail thumbnail = f.get();
          thumbnail.insert(db);
        }
      } catch (Exception e) {
        LOG.error("ImageResizerJob insert thumbnails into database exception", e);
        throw new JobExecutionException(e);
      } finally {
        SchedulerUtils.freeConnection(schedulerContext, db);
        if (executor != null) {
          executor.shutdown();
        }
      }
    }
  }
}
