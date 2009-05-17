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

package com.concursive.connect.web.modules.blog.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.beans.PortalBean;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Contains a collection of blog posts
 *
 * @author matt rajkowski
 * @created June 23, 2003
 */
public class BlogPostList extends ArrayList<BlogPost> {
  //filters
  private int projectId = -1;
  private boolean overviewAll = false;
  private int currentNews = Constants.UNDEFINED;
  private int archivedNews = Constants.UNDEFINED;
  private int unreleasedNews = Constants.UNDEFINED;
  private int incompleteNews = Constants.UNDEFINED;
  private int publicProjectPosts = Constants.UNDEFINED;
  private int forParticipant = Constants.UNDEFINED;
  private PagedListInfo pagedListInfo = null;
  private int lastNews = -1;
  private int forUser = -1;
  private int classificationId = -1;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  private int categoryId = -1;
  private boolean checkNullProjectId = false;
  private boolean checkNullCategoryId = false;
  private boolean checkNullEndDate = false;
  private int status = -1;
  private String publishedYearMonth = null;
  private Timestamp startOfCurrentMonth = null;
  private Timestamp startOfNextMonth = null;
  private int projectCategoryId = -1;

  //calendar
  protected java.sql.Timestamp alertRangeStart = null;
  protected java.sql.Timestamp alertRangeEnd = null;
  private boolean buildCommentCount = false;


  /**
   * Constructor for the NewsArticleList object
   */
  public BlogPostList() {
  }


  /**
   * Sets the projectId attribute of the NewsArticleList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the NewsArticleList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the overview attribute of the NewsArticleList object
   *
   * @param tmp The new overview value
   */
  public void setOverviewAll(boolean tmp) {
    this.overviewAll = tmp;
  }


  /**
   * Sets the overview attribute of the NewsArticleList object
   *
   * @param tmp The new overview value
   */
  public void setOverviewAll(String tmp) {
    this.overviewAll = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Gets the overview attribute of the NewsArticleList object
   *
   * @return The overview value
   */
  public boolean getOverviewAll() {
    return overviewAll;
  }


  /**
   * Sets the currentNews attribute of the NewsArticleList object
   *
   * @param tmp The new currentNews value
   */
  public void setCurrentNews(int tmp) {
    this.currentNews = tmp;
  }


  /**
   * Sets the currentNews attribute of the NewsArticleList object
   *
   * @param tmp The new currentNews value
   */
  public void setCurrentNews(String tmp) {
    this.currentNews = Integer.parseInt(tmp);
  }


  /**
   * Sets the archivedNews attribute of the NewsArticleList object
   *
   * @param tmp The new archivedNews value
   */
  public void setArchivedNews(int tmp) {
    this.archivedNews = tmp;
  }


  /**
   * Sets the archivedNews attribute of the NewsArticleList object
   *
   * @param tmp The new archivedNews value
   */
  public void setArchivedNews(String tmp) {
    this.archivedNews = Integer.parseInt(tmp);
  }


  /**
   * Sets the unreleasedNews attribute of the NewsArticleList object
   *
   * @param tmp The new unreleasedNews value
   */
  public void setUnreleasedNews(int tmp) {
    this.unreleasedNews = tmp;
  }


  /**
   * Sets the unreleasedNews attribute of the NewsArticleList object
   *
   * @param tmp The new unreleasedNews value
   */
  public void setUnreleasedNews(String tmp) {
    this.unreleasedNews = Integer.parseInt(tmp);
  }


  /**
   * Sets the incompleteNews attribute of the NewsArticleList object
   *
   * @param tmp The new incompleteNews value
   */
  public void setIncompleteNews(int tmp) {
    this.incompleteNews = tmp;
  }


  /**
   * Sets the incompleteNews attribute of the NewsArticleList object
   *
   * @param tmp The new incompleteNews value
   */
  public void setIncompleteNews(String tmp) {
    this.incompleteNews = Integer.parseInt(tmp);
  }


  /**
   * Sets the lastNews attribute of the NewsArticleList object
   *
   * @param tmp The new lastNews value
   */
  public void setLastNews(int tmp) {
    this.lastNews = tmp;
  }


  /**
   * Sets the forUser attribute of the NewsArticleList object
   *
   * @param tmp The new forUser value
   */
  public void setForUser(int tmp) {
    this.forUser = tmp;
  }


  /**
   * Sets the forUser attribute of the NewsArticleList object
   *
   * @param tmp The new forUser value
   */
  public void setForUser(String tmp) {
    this.forUser = Integer.parseInt(tmp);
  }


  /**
   * Gets the classificationId attribute of the NewsArticleList object
   *
   * @return The classificationId value
   */
  public int getClassificationId() {
    return classificationId;
  }


  /**
   * Sets the classificationId attribute of the NewsArticleList object
   *
   * @param tmp The new classificationId value
   */
  public void setClassificationId(int tmp) {
    this.classificationId = tmp;
  }


  /**
   * Sets the classificationId attribute of the NewsArticleList object
   *
   * @param tmp The new classificationId value
   */
  public void setClassificationId(String tmp) {
    this.classificationId = Integer.parseInt(tmp);
  }


  /**
   * Sets the pagedListInfo attribute of the NewsArticleList object
   *
   * @param tmp The new pagedListInfo value
   */
  public void setPagedListInfo(PagedListInfo tmp) {
    this.pagedListInfo = tmp;
  }


  /**
   * Sets the alertRangeStart attribute of the NewsArticleList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(java.sql.Timestamp tmp) {
    this.alertRangeStart = tmp;
  }


  /**
   * Sets the alertRangeStart attribute of the NewsArticleList object
   *
   * @param tmp The new alertRangeStart value
   */
  public void setAlertRangeStart(String tmp) {
    this.alertRangeStart = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the alertRangeEnd attribute of the NewsArticleList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(java.sql.Timestamp tmp) {
    this.alertRangeEnd = tmp;
  }


  /**
   * Sets the alertRangeEnd attribute of the NewsArticleList object
   *
   * @param tmp The new alertRangeEnd value
   */
  public void setAlertRangeEnd(String tmp) {
    this.alertRangeEnd = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Gets the projectId attribute of the NewsArticleList object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Gets the currentNews attribute of the NewsArticleList object
   *
   * @return The currentNews value
   */
  public int getCurrentNews() {
    return currentNews;
  }


  /**
   * Gets the archivedNews attribute of the NewsArticleList object
   *
   * @return The archivedNews value
   */
  public int getArchivedNews() {
    return archivedNews;
  }


  /**
   * Gets the unreleasedNews attribute of the NewsArticleList object
   *
   * @return The unreleasedNews value
   */
  public int getUnreleasedNews() {
    return unreleasedNews;
  }


  /**
   * Gets the incompleteNews attribute of the NewsArticleList object
   *
   * @return The incompleteNews value
   */
  public int getIncompleteNews() {
    return incompleteNews;
  }


  /**
   * @return the publicProjectPosts
   */
  public int getPublicProjectPosts() {
    return publicProjectPosts;
  }


  /**
   * @param publicProjectPosts the publicPosts to set
   */
  public void setPublicProjectPosts(int publicProjectPosts) {
    this.publicProjectPosts = publicProjectPosts;
  }

  public void setPublicProjectPosts(String publicProjectPosts) {
    this.publicProjectPosts = Integer.parseInt(publicProjectPosts);
  }

  public int getForParticipant() {
    return forParticipant;
  }

  public void setForParticipant(int forParticipant) {
    this.forParticipant = forParticipant;
  }

  public void setForParticipant(String tmp) {
    forParticipant = DatabaseUtils.parseBooleanToConstant(tmp);
  }

  /**
   * Gets the pagedListInfo attribute of the NewsArticleList object
   *
   * @return The pagedListInfo value
   */
  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }


  /**
   * Gets the alertRangeStart attribute of the NewsArticleList object
   *
   * @return The alertRangeStart value
   */
  public java.sql.Timestamp getAlertRangeStart() {
    return alertRangeStart;
  }


  /**
   * Gets the alertRangeEnd attribute of the NewsArticleList object
   *
   * @return The alertRangeEnd value
   */
  public java.sql.Timestamp getAlertRangeEnd() {
    return alertRangeEnd;
  }


  /**
   * Gets the forUser attribute of the NewsArticleList object
   *
   * @return The forUser value
   */
  public int getForUser() {
    return forUser;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = Integer.parseInt(modifiedBy);
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = Integer.parseInt(categoryId);
  }

  public boolean getCheckNullProjectId() {
    return checkNullProjectId;
  }

  public void setCheckNullProjectId(boolean checkNullProjectId) {
    this.checkNullProjectId = checkNullProjectId;
  }

  public boolean getCheckNullCategoryId() {
    return checkNullCategoryId;
  }

  public void setCheckNullCategoryId(boolean checkNullCategoryId) {
    this.checkNullCategoryId = checkNullCategoryId;
  }

  public boolean getCheckNullEndDate() {
    return checkNullEndDate;
  }

  public void setCheckNullEndDate(boolean checkNullEndDate) {
    this.checkNullEndDate = checkNullEndDate;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  /**
   * @return the projectCategoryId
   */
  public int getProjectCategoryId() {
    return projectCategoryId;
  }


  /**
   * @param projectCategoryId the projectCategoryId to set
   */
  public void setProjectCategoryId(int projectCategoryId) {
    this.projectCategoryId = projectCategoryId;
  }

  public void setProjectCategoryId(String projectCategoryId) {
    this.projectCategoryId = Integer.parseInt(projectCategoryId);
  }

  /**
   * @return the publishedYearMonth
   */
  public String getPublishedYearMonth() {
    return publishedYearMonth;
  }

  public void setBuildCommentCount(boolean buildCommentCount) {
    this.buildCommentCount = buildCommentCount;
  }

  public void setBuildCommentCount(String buildCommentCount) {
    this.buildCommentCount = DatabaseUtils.parseBoolean(buildCommentCount);
  }

  public boolean getBuildCommentCount() {
    return this.buildCommentCount;
  }

  /**
   * @param publishedYearMonth the publishedYearMonth to set
   */
  public void setPublishedYearMonth(String publishedYearMonth) {
    this.publishedYearMonth = publishedYearMonth;
    int year = Integer.parseInt(publishedYearMonth.substring(0, publishedYearMonth.indexOf("-")));
    int month = Integer.parseInt(publishedYearMonth.substring(publishedYearMonth.indexOf("-") + 1)) - 1; //0 based month

    // Calculate the start of the month
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, 1, 0, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    startOfCurrentMonth = new Timestamp(calendar.getTimeInMillis());

    // Calculate the end of the month
    calendar.set(year, month, 1, 23, 59, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    calendar.add(Calendar.MONTH, 1);
    startOfNextMonth = new Timestamp(calendar.getTimeInMillis());
  }


  public int queryCount(Connection db) throws SQLException {
    int count = 0;
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_news n " +
            "WHERE n.news_id > -1 ");
    createFilter(sqlFilter);
    PreparedStatement pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
    prepareFilter(pst);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      count = rs.getInt("recordcount");
    }
    rs.close();
    pst.close();
    return count;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs = null;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM project_news n " +
            "WHERE n.news_id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(lastNews);
    }
    //Get the total number of records matching filter
    pst = db.prepareStatement(sqlCount.toString() + sqlFilter.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (rs.next()) {
      int maxRecords = rs.getInt("recordcount");
      pagedListInfo.setMaxRecords(maxRecords);
    }
    rs.close();
    pst.close();
    //Determine the offset, based on the filter, for the first record to show
    if (!pagedListInfo.getCurrentLetter().equals("")) {
      pst = db.prepareStatement(sqlCount.toString() +
          sqlFilter.toString() +
          "AND lower(n.subject) > ? ");
      items = prepareFilter(pst);
      pst.setString(++items, pagedListInfo.getCurrentLetter().toLowerCase());
      rs = pst.executeQuery();
      if (rs.next()) {
        int offsetCount = rs.getInt("recordcount");
        pagedListInfo.setCurrentOffset(offsetCount);
      }
      rs.close();
      pst.close();
    }
    //Determine column to sort by
    pagedListInfo.setDefaultSort("n.priority_id asc, n.start_date desc", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "n.* " +
            "FROM project_news n " +
            "LEFT JOIN project_news_category c ON (n.category_id = c.category_id) " +
            "WHERE n.news_id > -1 ");
    pst = db.prepareStatement(sqlSelect.toString() + sqlFilter.toString() + sqlOrder.toString());
    items = prepareFilter(pst);
    rs = pst.executeQuery();
    if (pagedListInfo != null) {
      pagedListInfo.doManualOffset(db, rs);
    }
    int count = 0;
    while (rs.next()) {
      if (pagedListInfo != null && pagedListInfo.getItemsPerPage() > 0 &&
          DatabaseUtils.getType(db) == DatabaseUtils.MSSQL &&
          count >= pagedListInfo.getItemsPerPage()) {
        break;
      }
      ++count;
      BlogPost thisArticle = new BlogPost(rs);
      this.add(thisArticle);
    }
    rs.close();
    pst.close();
    // News articles need to build extra data from other tables...
    Iterator i = iterator();
    while (i.hasNext()) {
      BlogPost thisArticle = (BlogPost) i.next();
      thisArticle.buildResources(db);
      if (this.buildCommentCount) {
        BlogPostCommentList postCommentList = new BlogPostCommentList();
        postCommentList.setNewsId(thisArticle.getId());
        thisArticle.setNumberOfComments(postCommentList.queryCount(db));
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param sqlFilter Description of the Parameter
   */
  protected void createFilter(StringBuffer sqlFilter) {
    if (projectId > 0) {
      sqlFilter.append("AND n.project_id = ? ");
    }
    if (currentNews == Constants.TRUE) {
      sqlFilter.append(
          "AND n.start_date <= CURRENT_TIMESTAMP " +
              "AND (CURRENT_TIMESTAMP <= n.end_date OR n.end_date IS NULL) " +
              "AND status = ? ");
    }
    if (archivedNews == Constants.TRUE) {
      sqlFilter.append(
          "AND CURRENT_TIMESTAMP > n.end_date " +
              "AND n.end_date IS NOT NULL " +
              "AND n.start_date IS NOT NULL " +
              "AND status = ? ");
    }
    if (unreleasedNews == Constants.TRUE) {
      sqlFilter.append(
          "AND (CURRENT_TIMESTAMP < n.start_date OR n.start_date IS NULL) " +
              "AND status = ? ");
    }
    if (incompleteNews == Constants.TRUE) {
      sqlFilter.append("AND (status = ? OR status IS NULL) ");
    }
    if (publicProjectPosts == Constants.TRUE) {
      sqlFilter.append("AND n.project_id IN ( SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL ) ");
    }
    if (forParticipant == Constants.TRUE) {
      sqlFilter.append("AND n.project_id IN ( SELECT project_id FROM projects WHERE (allows_user_observers = ? OR allow_guests = ?) AND approvaldate IS NOT NULL ) ");
    }
    if (alertRangeStart != null) {
      sqlFilter.append("AND n.start_date >= ? ");
    }
    if (alertRangeEnd != null) {
      sqlFilter.append("AND n.start_date < ? ");
    }
    if (forUser > -1) {
      sqlFilter.append("AND (n.project_id IN (SELECT DISTINCT project_id FROM project_team WHERE user_id = ? " +
          "AND status IS NULL) OR n.project_id IN (SELECT project_id FROM projects WHERE allow_guests = ? AND approvaldate IS NOT NULL)) ");
    }
    if (classificationId > -1) {
      sqlFilter.append("AND n.classification_id = ? ");
    }
    if (overviewAll) {
      sqlFilter.append(
          "AND (( " +
              "    n.start_date <= CURRENT_TIMESTAMP " +
              "    AND (CURRENT_TIMESTAMP <= n.end_date OR n.end_date IS NULL) " +
              "    AND status = ?) " +
              "  OR ( " +
              "          (CURRENT_TIMESTAMP < n.start_date OR n.start_date IS NULL)  " +
              "          AND status = ?) " +
              "  OR ( " +
              "          (status = ? OR status IS NULL) " +
              ")) ");
    }
    if (categoryId > -1) {
      sqlFilter.append("AND n.category_id = ? ");
    }
    if (checkNullProjectId) {
      sqlFilter.append("AND n.project_id IS NULL ");
    }
    if (checkNullCategoryId) {
      sqlFilter.append("AND n.category_id IS NULL ");
    }
    if (checkNullEndDate) {
      sqlFilter.append("AND n.end_date IS NULL ");
    }
    if (status > -1) {
      sqlFilter.append("AND n.status = ? ");
    }
    if (publishedYearMonth != null) {
      sqlFilter.append(" AND ( start_date >= ? AND start_date < ? )");
    }
    if (enteredBy > 0) {
      sqlFilter.append(" AND enteredby = ? ");
    }
    if (projectCategoryId != -1) {
      sqlFilter.append(" AND n.project_id IN ( SELECT project_id FROM projects WHERE category_id = ? ) ");
    }
  }


  /**
   * Description of the Method
   *
   * @param pst Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (projectId > 0) {
      pst.setInt(++i, projectId);
    }
    if (currentNews == Constants.TRUE) {
      pst.setInt(++i, BlogPost.PUBLISHED);
    }
    if (archivedNews == Constants.TRUE) {
      pst.setInt(++i, BlogPost.PUBLISHED);
    }
    if (unreleasedNews == Constants.TRUE) {
      pst.setInt(++i, BlogPost.PUBLISHED);
    }
    if (incompleteNews == Constants.TRUE) {
      pst.setInt(++i, BlogPost.UNAPPROVED);
    }
    if (publicProjectPosts == Constants.TRUE) {
      pst.setBoolean(++i, true);
    }
    if (forParticipant == Constants.TRUE) {
      pst.setBoolean(++i, true);
      pst.setBoolean(++i, true);
    }
    if (alertRangeStart != null) {
      pst.setTimestamp(++i, alertRangeStart);
    }
    if (alertRangeEnd != null) {
      pst.setTimestamp(++i, alertRangeEnd);
    }
    if (forUser > -1) {
      pst.setInt(++i, forUser);
      pst.setBoolean(++i, true);
    }
    if (classificationId > -1) {
      pst.setInt(++i, classificationId);
    }
    if (overviewAll) {
      pst.setInt(++i, BlogPost.PUBLISHED);
      pst.setInt(++i, BlogPost.PUBLISHED);
      pst.setInt(++i, BlogPost.UNAPPROVED);
    }
    if (categoryId > -1) {
      pst.setInt(++i, categoryId);
    }
    if (status > -1) {
      pst.setInt(++i, status);
    }
    if (publishedYearMonth != null) {
      pst.setTimestamp(++i, startOfCurrentMonth);
      pst.setTimestamp(++i, startOfNextMonth);
    }
    if (enteredBy > 0) {
      pst.setInt(++i, enteredBy);
    }
    if (projectCategoryId != -1) {
      pst.setInt(++i, projectCategoryId);
    }
    return i;
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void delete(Connection db, int projectId, String basePath) throws SQLException {
    BlogPostList newsList = new BlogPostList();
    newsList.setProjectId(projectId);
    newsList.buildList(db);
    for (BlogPost news : newsList) {
      news.delete(db, basePath);
    }
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static int queryHomePageId(Connection db, int projectId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT news_id " +
            "FROM project_news " +
            "WHERE project_id = ? " +
            "AND classification_id = ? " +
            "AND status = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, BlogPost.HOMEPAGE);
    pst.setInt(3, BlogPost.PUBLISHED);
    ResultSet rs = pst.executeQuery();
    int id = -1;
    if (rs.next()) {
      id = rs.getInt("news_id");
    }
    rs.close();
    pst.close();
    return id;
  }

  public static String queryPagePortalKey(Connection db, int projectId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT portal_key " +
            "FROM project_news " +
            "WHERE project_id = ? " +
            "AND status = ? " +
            "AND classification_id = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, BlogPost.PUBLISHED);
    pst.setInt(3, BlogPost.HOMEPAGE);
    ResultSet rs = pst.executeQuery();
    String key = null;
    if (rs.next()) {
      key = rs.getString("portal_key");
    }
    rs.close();
    pst.close();
    return key;
  }

  public static BlogPost retrieveNewsArticleBySubject(Connection db, int projectId, int categoryId, String subject) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT news_id " +
            "FROM project_news " +
            "WHERE project_id = ? " +
            "AND category_id = ? " +
            "AND subject = ? ");
    pst.setInt(1, projectId);
    pst.setInt(2, categoryId);
    pst.setString(3, subject);
    ResultSet rs = pst.executeQuery();
    int id = -1;
    if (rs.next()) {
      id = rs.getInt("news_id");
    }
    rs.close();
    pst.close();
    if (id == -1) {
      return null;
    }
    return new BlogPost(db, id);
  }

  public static void configureIdsByPortalPath(Connection db, PortalBean bean) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT news_id, project_id, category_id, redirect " +
            "FROM project_news " +
            "WHERE portal_key = ? " +
            "AND " +
            "(" +
            "project_id IS NOT NULL " +
            "AND project_id IN (SELECT project_id FROM projects WHERE language_id = ?) " +
            "OR project_id IS NULL" +
            ") " +
            "AND end_date IS NULL ");
    pst.setString(1, bean.getPortalPath() + "." + bean.getPortalExtension());
    pst.setInt(2, bean.getLanguageId());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      bean.setNewsId(rs.getInt("news_id"));
      bean.setProjectId(DatabaseUtils.getInt(rs, "project_id"));
      bean.setCategoryId(DatabaseUtils.getInt(rs, "category_id"));
      bean.setRedirect(rs.getString("redirect"));
    }
    rs.close();
    pst.close();
  }

  public static String queryRedirect(Connection db, int newsId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT redirect, portal_key " +
            "FROM project_news " +
            "WHERE news_id = ? ");
    pst.setInt(1, newsId);
    ResultSet rs = pst.executeQuery();
    String value = null;
    if (rs.next()) {
      value = rs.getString("redirect");
      if (value == null) {
        value = rs.getString("portal_key");
      }
    }
    rs.close();
    pst.close();
    return value;
  }


  public void insert(Connection db) throws SQLException {
    for (BlogPost thisArticle : this) {
      thisArticle.setId(-1);
      thisArticle.setProjectId(projectId);
      thisArticle.setEnteredBy(enteredBy);
      thisArticle.setModifiedBy(modifiedBy);
      thisArticle.setEntered((Timestamp) null);
      thisArticle.setModified((Timestamp) null);
      thisArticle.insert(db);
    }
  }

  public void remapCategories(HashMap map) throws SQLException {
    for (BlogPost thisArticle : this) {
      int currentId = thisArticle.getCategoryId();
      if (map.containsKey(new Integer(currentId))) {
        int newId = (Integer) map.get(new Integer(currentId));
        thisArticle.setCategoryId(newId);
      } else {
        thisArticle.setCategoryId(-1);
      }
    }
  }
}

