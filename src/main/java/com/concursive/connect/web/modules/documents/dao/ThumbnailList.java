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

package com.concursive.connect.web.modules.documents.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains a collection of thumbnail images for a FileItem
 *
 * @author matt rajkowski
 * @created July 28, 2008
 */
public class ThumbnailList extends ArrayList<Thumbnail> {

  private int itemId = -1;
  private double version = 0;
  private int width = 0;
  private int height = 0;

  public ThumbnailList() {
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public double getVersion() {
    return version;
  }

  public void setVersion(double version) {
    this.version = version;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    createFilter(sqlFilter);
    sqlSelect.append("SELECT ");
    sqlSelect.append(
        "t.* " +
            "FROM project_files_thumbnail t " +
            "WHERE t.item_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    while (rs.next()) {
      Thumbnail thisRecord = new Thumbnail(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (itemId > 0) {
      sqlFilter.append("AND t.item_id = ? ");
    }
    if (version > 0) {
      sqlFilter.append("AND t.version = ? ");
    }
    if (width > 0 && height > 0) {
      sqlFilter.append("AND (t.image_width = ? AND t.image_height = ?) ");
    } else {
      if (width > 0) {
        sqlFilter.append("AND t.image_width = ? ");
      }
      if (height > 0) {
        sqlFilter.append("AND t.image_height = ? ");
      }
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (itemId > 0) {
      pst.setInt(++i, itemId);
    }
    if (version > 0) {
      pst.setDouble(++i, version);
    }
    if (width > 0 && height > 0) {
      pst.setInt(++i, width);
      pst.setInt(++i, height);
    } else {
      if (width > 0) {
        pst.setInt(++i, width);
      }
      if (height > 0) {
        pst.setInt(++i, height);
      }
    }
    return i;
  }
}