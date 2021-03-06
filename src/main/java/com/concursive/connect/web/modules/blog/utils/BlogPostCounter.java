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

package com.concursive.connect.web.modules.blog.utils;

import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.utils.CounterDateMap;
import com.concursive.connect.web.utils.CounterStringMap;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Counts blog posts by category, dates, author, etc.
 *
 * @author matt rajkowski
 * @created Apr 23, 2008
 */
public class BlogPostCounter {
  private int total = 0;
  private int projectId = -1;
  private CounterDateMap dates = new CounterDateMap();
  private CounterStringMap authors = new CounterStringMap();
  private CounterStringMap categories = new CounterStringMap();

  public BlogPostCounter() {
  }

  /**
   * @return the projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }


  public CounterDateMap getDates() {
    return dates;
  }

  public CounterStringMap getAuthors() {
    return authors;
  }

  public CounterStringMap getCategories() {
    return categories;
  }

  public void buildCounts(Connection db) throws SQLException {
    // Go through the news
    BlogPostList newsList = new BlogPostList();
    newsList.setProjectId(projectId);
    newsList.buildList(db);
    for (BlogPost thisArticle : newsList) {
      dates.add(thisArticle.getStartDate());
      authors.add(String.valueOf(thisArticle.getEnteredBy()));
      categories.add(String.valueOf(thisArticle.getCategoryId()));
      ++total;
    }
  }

  public boolean hasCounts() {
    return (dates.size() > 0 || authors.size() > 0 || categories.size() > 0);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("== BlogPostCounter ======================");
    sb.append(" Dates: ").append(dates.getTotal());
    sb.append(" Authors: ").append(authors.getTotal());
    sb.append(" Categories: ").append(categories.getTotal());
    return sb.toString();
  }

}
