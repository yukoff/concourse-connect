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

import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.utils.CounterDateMap;
import com.concursive.connect.web.utils.CounterStringMap;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Counts files by date and folder
 *
 * @author matt rajkowski
 * @created January 21, 2009
 */
public class FileItemCounter {
  private int total = 0;
  private int linkModuleId = -1;
  private int linkItemId = -1;
  private CounterDateMap dates = new CounterDateMap();
  private CounterStringMap folders = new CounterStringMap();

  public FileItemCounter() {
  }

  public int getLinkModuleId() {
    return linkModuleId;
  }

  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  public int getLinkItemId() {
    return linkItemId;
  }

  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public CounterDateMap getDates() {
    return dates;
  }

  public CounterStringMap getFolders() {
    return folders;
  }

  public void buildCounts(Connection db) throws SQLException {
    // Go through the news
    FileItemList files = new FileItemList();
    files.setLinkModuleId(linkModuleId);
    files.setLinkItemId(linkItemId);
    files.buildList(db);
    for (FileItem thisItem : files) {
      dates.add(thisItem.getModified());
      folders.add(String.valueOf(thisItem.getFolderId()));
      ++total;
    }
  }

  public boolean hasCounts() {
    return (dates.size() > 0 || folders.size() > 0);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("== FileItemCounter ======================");
    sb.append(" Dates: ").append(dates.getTotal());
    sb.append(" Folders: ").append(folders.getTotal());
    return sb.toString();
  }

}