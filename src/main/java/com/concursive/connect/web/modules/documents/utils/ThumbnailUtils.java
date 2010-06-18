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

package com.concursive.connect.web.modules.documents.utils;

import com.concursive.commons.images.ImageUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.classifieds.dao.Classified;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.dao.ThumbnailList;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class to work with Thumbnail objects
 *
 * @author matt rajkowski
 * @created July 28, 2008
 */
public class ThumbnailUtils {

  public static Thumbnail retrieveThumbnail(Connection db, FileItem fileItem, int width, int height, String path) throws SQLException, IOException {
    // See if the thumbnail exists
    ThumbnailList thumbnailList = new ThumbnailList();
    thumbnailList.setItemId(fileItem.getId());
    thumbnailList.setVersion(fileItem.getVersion());
    thumbnailList.setWidth(width);
    thumbnailList.setHeight(height);
    thumbnailList.buildList(db);
    if (thumbnailList.size() == 0 && (width > 0 || height > 0)) {
      // Generate and insert the thumbnail
      synchronized (ThumbnailUtils.class) {
        thumbnailList.buildList(db);
        if (thumbnailList.size() == 0) {
          // Create a thumbnail, then add to list
          String filePath = path + GenericAction.getDatePath(fileItem.getModified());
          String thumbnailFilename = fileItem.getFilename() + "-" + width + "x" + height + "-TH";
          File originalFile = new File(filePath + fileItem.getFilename());
          File thumbnailFile = new File(filePath + thumbnailFilename);
          String format = fileItem.getExtension().substring(1);
          Thumbnail thumbnail = new Thumbnail(ImageUtils.saveThumbnail(originalFile, thumbnailFile, width, height, format));
          // Store thumbnail in database
          thumbnail.setId(fileItem.getId());
          thumbnail.setFilename(thumbnailFilename);
          thumbnail.setVersion(fileItem.getVersion());
          thumbnail.setSize((int) thumbnailFile.length());
          thumbnail.setEnteredBy(fileItem.getEnteredBy());
          thumbnail.setModifiedBy(fileItem.getModifiedBy());
          thumbnail.insert(db);
          // Add it to the list to return the reference
          thumbnailList.add(thumbnail);
        }
      }
    }
    if (thumbnailList.size() > 0) {
      Thumbnail thumbnail = thumbnailList.get(0);
      String filename = fileItem.getClientFilename();
      filename = filename.substring(0, filename.lastIndexOf(".") + 1) + thumbnail.getFormat();
      thumbnail.setClientFilename(filename);
      return thumbnail;
    } else {
      return null;
    }
  }

  public static Thumbnail prepareThumbnail(Connection db, FileItem fileItem, int width, int height, String path) throws SQLException, IOException {
    // See if the thumbnail exists
    ThumbnailList thumbnailList = new ThumbnailList();
    thumbnailList.setItemId(fileItem.getId());
    thumbnailList.setVersion(fileItem.getVersion());
    thumbnailList.setWidth(width);
    thumbnailList.setHeight(height);
    thumbnailList.buildList(db);
    if (thumbnailList.size() == 0 && (width > 0 || height > 0)) {
      // This task is now multi-threaded so since it's not ready, return a coming soon image
      return null;
    } else if (thumbnailList.size() > 0) {
      Thumbnail thumbnail = thumbnailList.get(0);
      String filename = fileItem.getClientFilename();
      filename = filename.substring(0, filename.lastIndexOf(".") + 1) + thumbnail.getFormat();
      thumbnail.setClientFilename(filename);
      return thumbnail;
    } else {
      return null;
    }
  }

  /**
   * This method returns the preset dimension for a given module. If there is no preset
   * for the module or the moduleId is unknown then null is returned.
   *
   * @param moduleId - the module whose preset is being found
   * @return int[] length 2 where int[0]=width and int[1]=height, if no preset is found
   *         then null is returned
   */
  public static int[] getPresetDimensionsForModule(int moduleId) {
    switch (moduleId) {
      case Constants.PROJECT_CLASSIFIEDS_FILES:
        return new int[]{Classified.DEFAULT_IMAGE_WIDTH, Classified.DEFAULT_IMAGE_HEIGHT};
    }
    return null;
  }
}