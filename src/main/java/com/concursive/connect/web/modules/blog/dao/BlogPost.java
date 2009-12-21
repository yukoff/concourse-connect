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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.html.HTMLUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.common.social.viewing.utils.Viewing;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * The properties of a blog post
 *
 * @author matt rajkowski
 * @created June 23, 2003
 */
public class BlogPost extends GenericBean {
  public static final String TABLE = "project_news";
  public static final String PRIMARY_KEY = "news_id";

  //display priorities
  public final static int HIGH = 1;
  public final static int NORMAL = 10;
  //status properties
  public final static int DRAFT = -1;
  public final static int UNAPPROVED = 1;
  public final static int PUBLISHED = 2;
  //portal
  public final static int HOMEPAGE = 10;
  public final static int ARTICLE = 20;
  //templates
  public final static int TEMPLATE_ARTICLE = 1;
  public final static int TEMPLATE_ARTICLE_LINKEDLIST = 2;
  public final static int TEMPLATE_ARTICLE_LIST_PROJECTS = 3;
  public final static int TEMPLATE_LIST_BY_CATEGORIES = 4;
  public final static int TEMPLATE_LIST_PROJECTS = 5;
  //article properties
  private int id = -1;
  private int projectId = -1;
  private int categoryId = -1;
  private String subject = null;
  private String intro = null;
  private String message = null;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private int modifiedBy = -1;
  private Timestamp startDate = null;
  private Timestamp endDate = null;
  private int priorityId = NORMAL;
  private boolean enabled = true;
  private boolean allowReplies = false;
  private boolean allowRatings = false;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double avgRating = 0;
  private double ratingAvg = 0.0;
  private int readCount = 0;
  private int status = -1;
  private int taskCategoryId = -1;
  private int classificationId = ARTICLE;
  private int templateId = -1;
  private String portalKey = null;
  private String redirect = null;
  private String pageTitle = null;
  private String metaName = null;
  private String metaContent = null;
  private String keywords = null;
  private String description = null;
  private Timestamp readDate = null;
  private int inappropriateCount = 0;

  private int numberOfComments = 0;


  /**
   * Constructor for the NewsArticle object
   */
  public BlogPost() {
  }


  /**
   * Constructor for the NewsArticle object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public BlogPost(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the NewsArticle object
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public BlogPost(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }


  /**
   * Constructor for the NewsArticle object
   *
   * @param db        Description of the Parameter
   * @param id        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public BlogPost(Connection db, int id, int projectId) throws SQLException {
    this.projectId = projectId;
    queryRecord(db, id);
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param newsId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int newsId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT n.* " +
            "FROM project_news n " +
            "WHERE news_id = ? ");
    if (projectId > -1) {
      sql.append("AND project_id = ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, newsId);
    if (projectId > -1) {
      pst.setInt(++i, projectId);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("News record not found.");
    }
    buildResources(db);
  }


  /**
   * Sets the id attribute of the NewsArticle object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the NewsArticle object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the projectId attribute of the NewsArticle object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the NewsArticle object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the categoryId attribute of the NewsArticle object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the categoryId attribute of the NewsArticle object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }


  /**
   * Sets the subject attribute of the NewsArticle object
   *
   * @param tmp The new subject value
   */
  public void setSubject(String tmp) {
    this.subject = tmp;
  }


  /**
   * Sets the intro attribute of the NewsArticle object
   *
   * @param tmp The new intro value
   */
  public void setIntro(String tmp) {
    setIntro(tmp, true);
  }

  public void setIntro(String tmp, boolean filter) {
    if (filter) {
      this.intro = HTMLUtils.makePublicHtml(tmp);
    } else {
      intro = tmp;
    }
  }


  /**
   * Sets the message attribute of the NewsArticle object
   *
   * @param tmp The new message value
   */
  public void setMessage(String tmp) {
    if (HTMLUtils.isEmpty(tmp)) {
      message = null;
    } else {
      this.message = HTMLUtils.makePublicHtml(tmp);
    }
  }


  /**
   * Sets the entered attribute of the NewsArticle object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the NewsArticle object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the NewsArticle object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the NewsArticle object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the NewsArticle object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the NewsArticle object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the NewsArticle object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the NewsArticle object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the startDate attribute of the NewsArticle object
   *
   * @param tmp The new startDate value
   */
  public void setStartDate(Timestamp tmp) {
    this.startDate = tmp;
  }


  /**
   * Sets the startDate attribute of the NewsArticle object
   *
   * @param tmp The new startDate value
   */
  public void setStartDate(String tmp) {
    this.startDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the endDate attribute of the NewsArticle object
   *
   * @param tmp The new endDate value
   */
  public void setEndDate(Timestamp tmp) {
    this.endDate = tmp;
  }


  /**
   * Sets the endDate attribute of the NewsArticle object
   *
   * @param tmp The new endDate value
   */
  public void setEndDate(String tmp) {
    this.endDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the priorityId attribute of the NewsArticle object
   *
   * @param tmp The new priorityId value
   */
  public void setPriorityId(int tmp) {
    this.priorityId = tmp;
  }


  /**
   * Sets the priorityId attribute of the NewsArticle object
   *
   * @param tmp The new priorityId value
   */
  public void setPriorityId(String tmp) {
    this.priorityId = Integer.parseInt(tmp);
  }


  /**
   * Sets the enabled attribute of the NewsArticle object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the allowReplies attribute of the NewsArticle object
   *
   * @param tmp The new allowReplies value
   */
  public void setAllowReplies(boolean tmp) {
    this.allowReplies = tmp;
  }


  /**
   * Sets the allowReplies attribute of the NewsArticle object
   *
   * @param tmp The new allowReplies value
   */
  public void setAllowReplies(String tmp) {
    this.allowReplies = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the allowRatings attribute of the NewsArticle object
   *
   * @param tmp The new allowRatings value
   */
  public void setAllowRatings(boolean tmp) {
    this.allowRatings = tmp;
  }


  /**
   * Sets the allowRatings attribute of the NewsArticle object
   *
   * @param tmp The new allowRatings value
   */
  public void setAllowRatings(String tmp) {
    this.allowRatings = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the ratingCount attribute of the NewsArticle object
   *
   * @param tmp The new ratingCount value
   */
  public void setRatingCount(int tmp) {
    this.ratingCount = tmp;
  }


  /**
   * Sets the ratingCount attribute of the NewsArticle object
   *
   * @param tmp The new ratingCount value
   */
  public void setRatingCount(String tmp) {
    this.ratingCount = Integer.parseInt(tmp);
  }

  public int getRatingValue() {
    return ratingValue;
  }

  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public void setRatingValue(String ratingValue) {
    this.ratingValue = Integer.parseInt(ratingValue);
  }

  /**
   * Sets the avgRating attribute of the NewsArticle object
   *
   * @param tmp The new avgRating value
   */
  public void setAvgRating(double tmp) {
    this.avgRating = tmp;
  }


  /**
   * @return the ratingAvg
   */
  public double getRatingAvg() {
    return ratingAvg;
  }


  /**
   * @param ratingAvg the ratingAvg to set
   */
  public void setRatingAvg(double ratingAvg) {
    this.ratingAvg = ratingAvg;
  }


  public void setRatingAvg(String ratingAvg) {
    this.ratingAvg = Double.parseDouble(ratingAvg);
  }

  /**
   * Sets the readCount attribute of the NewsArticle object
   *
   * @param tmp The new readCount value
   */
  public void setReadCount(int tmp) {
    this.readCount = tmp;
  }


  /**
   * Sets the readCount attribute of the NewsArticle object
   *
   * @param tmp The new readCount value
   */
  public void setReadCount(String tmp) {
    this.readCount = Integer.parseInt(tmp);
  }


  /**
   * Sets the enabled attribute of the NewsArticle object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the status attribute of the NewsArticle object
   *
   * @param tmp The new status value
   */
  public void setStatus(int tmp) {
    this.status = tmp;
  }


  /**
   * Sets the status attribute of the NewsArticle object
   *
   * @param tmp The new status value
   */
  public void setStatus(String tmp) {
    if ("Publish".equals(tmp)) {
      this.status = PUBLISHED;
    } else if ("Save as Draft".equals(tmp)) {
      this.status = DRAFT;
    } else if ("Save for Review".equals(tmp)) {
      this.status = UNAPPROVED;
    } else {
      this.status = Integer.parseInt(tmp);
    }
  }


  /**
   * Gets the taskCategoryId attribute of the NewsArticle object
   *
   * @return The taskCategory value
   */
  public int getTaskCategoryId() {
    return taskCategoryId;
  }


  /**
   * Sets the taskCategoryId attribute of the NewsArticle object
   *
   * @param tmp The new taskCategoryId value
   */
  public void setTaskCategoryId(int tmp) {
    this.taskCategoryId = tmp;
  }


  /**
   * Sets the taskCategoryId attribute of the NewsArticle object
   *
   * @param tmp The new taskCategoryId value
   */
  public void setTaskCategoryId(String tmp) {
    this.taskCategoryId = Integer.parseInt(tmp);
  }


  /**
   * Gets the classificationId attribute of the NewsArticle object
   *
   * @return The classificationId value
   */
  public int getClassificationId() {
    return classificationId;
  }


  /**
   * Sets the classificationId attribute of the NewsArticle object
   *
   * @param tmp The new classificationId value
   */
  public void setClassificationId(int tmp) {
    this.classificationId = tmp;
  }


  /**
   * Sets the classificationId attribute of the NewsArticle object
   *
   * @param tmp The new classificationId value
   */
  public void setClassificationId(String tmp) {
    this.classificationId = Integer.parseInt(tmp);
  }


  /**
   * Gets the templateId attribute of the NewsArticle object
   *
   * @return The templateId value
   */
  public int getTemplateId() {
    return templateId;
  }


  /**
   * Sets the templateId attribute of the NewsArticle object
   *
   * @param tmp The new templateId value
   */
  public void setTemplateId(int tmp) {
    this.templateId = tmp;
  }


  /**
   * Sets the templateId attribute of the NewsArticle object
   *
   * @param tmp The new templateId value
   */
  public void setTemplateId(String tmp) {
    this.templateId = Integer.parseInt(tmp);
  }

  public String getRedirect() {
    return redirect;
  }

  public void setRedirect(String redirect) {
    this.redirect = redirect;
  }

  public String getPortalKey() {
    return portalKey;
  }

  public void setPortalKey(String portalKey) {
    this.portalKey = portalKey;
  }

  /**
   * Gets the id attribute of the NewsArticle object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the projectId attribute of the NewsArticle object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Gets the categoryId attribute of the NewsArticle object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * Gets the subject attribute of the NewsArticle object
   *
   * @return The subject value
   */
  public String getSubject() {
    return subject;
  }


  /**
   * Gets the intro attribute of the NewsArticle object
   *
   * @return The intro value
   */
  public String getIntro() {
    return intro;
  }


  /**
   * Gets the message attribute of the NewsArticle object
   *
   * @return The message value
   */
  public String getMessage() {
    return message;
  }


  /**
   * A method for finding out if the blog has content after the intro
   *
   * @return Description of the Return Value
   */
  public boolean hasMessage() {
    return (StringUtils.hasText(message));
  }


  /**
   * Gets the entered attribute of the NewsArticle object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredBy attribute of the NewsArticle object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the NewsArticle object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedBy attribute of the NewsArticle object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the startDate attribute of the NewsArticle object
   *
   * @return The startDate value
   */
  public Timestamp getStartDate() {
    return startDate;
  }


  /**
   * Gets the startDateValue attribute of the NewsArticle object
   *
   * @return The startDateValue value
   */
  public String getStartDateValue() {
    try {
      return DateFormat.getDateInstance(3).format(startDate);
    } catch (NullPointerException e) {
    }
    return "";
  }


  /**
   * Gets the endDate attribute of the NewsArticle object
   *
   * @return The endDate value
   */
  public Timestamp getEndDate() {
    return endDate;
  }


  /**
   * Gets the endDateValue attribute of the NewsArticle object
   *
   * @return The endDateValue value
   */
  public String getEndDateValue() {
    try {
      return DateFormat.getDateInstance(3).format(endDate);
    } catch (NullPointerException e) {
    }
    return "";
  }


  /**
   * Gets the priorityId attribute of the NewsArticle object
   *
   * @return The priorityId value
   */
  public int getPriorityId() {
    return priorityId;
  }


  /**
   * Gets the enabled attribute of the NewsArticle object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Gets the allowReplies attribute of the NewsArticle object
   *
   * @return The allowReplies value
   */
  public boolean getAllowReplies() {
    return allowReplies;
  }


  /**
   * Gets the allowRatings attribute of the NewsArticle object
   *
   * @return The allowRatings value
   */
  public boolean getAllowRatings() {
    return allowRatings;
  }


  /**
   * Gets the ratingCount attribute of the NewsArticle object
   *
   * @return The ratingCount value
   */
  public int getRatingCount() {
    return ratingCount;
  }


  /**
   * Gets the avgRating attribute of the NewsArticle object
   *
   * @return The avgRating value
   */
  public double getAvgRating() {
    return avgRating;
  }


  /**
   * Gets the readCount attribute of the NewsArticle object
   *
   * @return The readCount value
   */
  public int getReadCount() {
    return readCount;
  }


  /**
   * Gets the status attribute of the NewsArticle object
   *
   * @return The status value
   */
  public int getStatus() {
    return status;
  }


  /**
   * Gets the enteredDateTimeString attribute of the NewsArticle object
   *
   * @return The enteredDateTimeString value
   */
  public String getEnteredDateTimeString() {
    String tmp = "";
    try {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(
          entered);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the enteredDateString attribute of the NewsArticle object
   *
   * @return The enteredDateString value
   */
  public String getEnteredDateString() {
    String tmp = "";
    try {
      return DateFormat.getDateInstance(3).format(entered);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the modifiedDateTimeString attribute of the NewsArticle object
   *
   * @return The modifiedDateTimeString value
   */
  public String getModifiedDateTimeString() {
    String tmp = "";
    try {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(
          modified);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the startDateTimeString attribute of the NewsArticle object
   *
   * @return The startDateTimeString value
   */
  public String getStartDateTimeString() {
    String tmp = "";
    try {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(
          startDate);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the pageTitle attribute of the NewsArticle object
   *
   * @return The pageTitle value
   */
  public String getPageTitle() {
    return pageTitle;
  }


  /**
   * Sets the pageTitle attribute of the NewsArticle object
   *
   * @param pageTitle The new classificationId value
   */
  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }


  /**
   * Gets the metaName attribute of the NewsArticle object
   *
   * @return The metaName value
   */
  public String getMetaName() {
    return metaName;
  }


  /**
   * Sets the metaName attribute of the NewsArticle object
   *
   * @param metaName The new classificationId value
   */
  public void setMetaName(String metaName) {
    this.metaName = metaName;
  }


  /**
   * Gets the metaContent attribute of the NewsArticle object
   *
   * @return The metaContent value
   */
  public String getMetaContent() {
    return metaContent;
  }


  /**
   * Sets the metaContent attribute of the NewsArticle object
   *
   * @param metaContent The new classificationId value
   */
  public void setMetaContent(String metaContent) {
    this.metaContent = metaContent;
  }


  /**
   * Gets the keywords attribute of the NewsArticle object
   *
   * @return The keywords value
   */
  public String getKeywords() {
    return keywords;
  }


  /**
   * Sets the keywords attribute of the NewsArticle object
   *
   * @param keywords The new classificationId value
   */
  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }


  /**
   * Gets the description attribute of the NewsArticle object
   *
   * @return The description value
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description attribute of the NewsArticle object
   *
   * @param description The new classificationId value
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * @return the readDate
   */
  public Timestamp getReadDate() {
    return readDate;
  }


  /**
   * @param readDate the readDate to set
   */
  public void setReadDate(Timestamp readDate) {
    this.readDate = readDate;
  }

  public void setReadDate(String readDate) {
    this.readDate = DatabaseUtils.parseTimestamp(readDate);
  }

  /**
   * @return the inappropriateCount
   */
  public int getInappropriateCount() {
    return inappropriateCount;
  }


  /**
   * @param inappropriateCount the inappropriateCount to set
   */
  public void setInappropriateCount(int inappropriateCount) {
    this.inappropriateCount = inappropriateCount;
  }

  public void setInappropriateCount(String inappropriateCount) {
    this.inappropriateCount = Integer.parseInt(inappropriateCount);
  }

  /**
   * @return the numberOfComments
   */
  public int getNumberOfComments() {
    return numberOfComments;
  }


  /**
   * @param numberOfComments the numberOfComments to set
   */
  public void setNumberOfComments(int numberOfComments) {
    this.numberOfComments = numberOfComments;
  }

  public void setNumberOfComments(String numberOfComments) {
    this.numberOfComments = Integer.parseInt(numberOfComments);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean hasTaskCategoryId() {
    return taskCategoryId > -1;
  }

  public User getUser() {
    if (enteredBy != -1) {
      return UserUtils.loadUser(enteredBy);
    }
    return null;
  }

  public Project getProject() {
    if (projectId != -1) {
      return ProjectUtils.loadProject(projectId);
    }
    return null;
  }


  /**
   * Populates this news article from a database result set
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("news_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    categoryId = DatabaseUtils.getInt(rs, "category_id");
    subject = rs.getString("subject");
    intro = rs.getString("intro");
    message = rs.getString("message");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    startDate = rs.getTimestamp("start_date");
    endDate = rs.getTimestamp("end_date");
    allowReplies = rs.getBoolean("allow_replies");
    allowRatings = rs.getBoolean("allow_rating");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    avgRating = rs.getDouble("avg_rating");
    priorityId = rs.getInt("priority_id");
    readCount = rs.getInt("read_count");
    enabled = rs.getBoolean("enabled");
    status = DatabaseUtils.getInt(rs, "status");
    classificationId = DatabaseUtils.getInt(rs, "classification_id");
    templateId = DatabaseUtils.getInt(rs, "template_id");
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    portalKey = rs.getString("portal_key");
    redirect = rs.getString("redirect");
    pageTitle = rs.getString("page_title");
    keywords = rs.getString("keywords");
    description = rs.getString("description");
    metaName = rs.getString("meta_name");
    metaContent = rs.getString("meta_content");
    readDate = rs.getTimestamp("read_date");
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildResources(Connection db) throws SQLException {
    if (id == -1) {
      throw new SQLException("ID not specified");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT category_id " +
            "FROM taskcategorylink_news " +
            "WHERE news_id = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      taskCategoryId = rs.getInt("category_id");
    }
    rs.close();
    pst.close();
  }


  /**
   * Gets the valid attribute of the NewsArticle object
   *
   * @return The valid value
   */
  public boolean isValid() {
    if (!StringUtils.hasText(subject)) {
      errors.put("subjectError", "Required field");
    }
    if (!StringUtils.hasText(intro) || intro.equals(" \r\n<br />\r\n ")) {
      errors.put("introError", "Required field");
    }
    if (startDate == null) {
      errors.put("startDateError", "Required field");
    }
    return !hasErrors();
  }

  public boolean clone(Connection db, int userId) throws SQLException {
    this.setId(-1);
    this.setStatus(BlogPost.DRAFT);
    this.setEnteredBy(userId);
    this.setEntered((Timestamp) null);
    this.setModifiedBy(userId);
    this.setModified((Timestamp) null);
    this.setStartDate(DateUtils.roundUpToNextFive());
    this.setEndDate((Timestamp) null);
    this.setRatingCount(0);
    this.setRatingValue(0);
    this.setAvgRating(0);
    this.setReadCount(0);
    return this.insert(db);
  }

  /**
   * Inserts a news article in the database
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_news " +
              "(" + (id > -1 ? "news_id, " : "") + "project_id, category_id, subject, intro, message, enabled, status, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      if (pageTitle != null) {
        sql.append("page_title, ");
      }
      if (keywords != null) {
        sql.append("keywords, ");
      }
      if (description != null) {
        sql.append("description, ");
      }
      if (metaName != null) {
        sql.append("meta_name, ");
      }
      if (metaContent != null) {
        sql.append("meta_content, ");
      }
      sql.append(
          "enteredBy, modifiedBy, " +
              "start_date, end_date, allow_replies, allow_rating, rating_count, " +
              "avg_rating, priority_id, read_count, classification_id, template_id, " +
              "portal_key, redirect) ");
      sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ");
      if (id > -1) {
        sql.append("?, ");
      }
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      if (pageTitle != null) {
        sql.append("?, ");
      }
      if (keywords != null) {
        sql.append("?, ");
      }
      if (description != null) {
        sql.append("?, ");
      }
      if (metaName != null) {
        sql.append("?, ");
      }
      if (metaContent != null) {
        sql.append("?, ");
      }
      sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
      int i = 0;
      //Insert the topic
      PreparedStatement pst = db.prepareStatement(sql.toString());
      if (id > -1) {
        pst.setInt(++i, id);
      }
      DatabaseUtils.setInt(pst, ++i, projectId);
      DatabaseUtils.setInt(pst, ++i, categoryId);
      pst.setString(++i, subject);
      pst.setString(++i, intro);
      pst.setString(++i, message);
      pst.setBoolean(++i, enabled);
      DatabaseUtils.setInt(pst, ++i, status);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      if (pageTitle != null) {
        pst.setString(++i, pageTitle);
      }
      if (keywords != null) {
        pst.setString(++i, keywords);
      }
      if (description != null) {
        pst.setString(++i, description);
      }
      if (metaName != null) {
        pst.setString(++i, metaName);
      }
      if (metaContent != null) {
        pst.setString(++i, metaContent);
      }
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      DatabaseUtils.setTimestamp(pst, ++i, startDate);
      DatabaseUtils.setTimestamp(pst, ++i, endDate);
      pst.setBoolean(++i, allowReplies);
      pst.setBoolean(++i, allowRatings);
      pst.setInt(++i, ratingCount);
      pst.setDouble(++i, avgRating);
      DatabaseUtils.setInt(pst, ++i, priorityId);
      pst.setInt(++i, readCount);
      DatabaseUtils.setInt(pst, ++i, classificationId);
      DatabaseUtils.setInt(pst, ++i, templateId);
      pst.setString(++i, portalKey);
      pst.setString(++i, redirect);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_news_news_id_seq", id);
      // If there is a task category being associated, insert that too
      if (taskCategoryId > -1) {
        insertTaskCategoryLink(db);
      }
      if (commit) {
        db.commit();
      }
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }


  /**
   * Updates the specified news article in the database
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;
    int i = 0;

    StringBuffer sbf = new StringBuffer("");
    if (pageTitle != null) {
      sbf.append(" page_title = ?,");
    }
    if (keywords != null) {
      sbf.append("keywords = ?,");
    }
    if (description != null) {
      sbf.append("description = ?, ");
    }
    if (metaName != null) {
      sbf.append("meta_name = ?,");
    }
    if (metaContent != null) {
      sbf.append("meta_content = ?, ");
    }
    if (portalKey != null) {
      sbf.append("portal_key = ?, ");
    }
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news SET " +
            sbf.toString() + " " +
            "subject = ?, intro = ?, message = ?, " +
            "modifiedby = ?, modified = CURRENT_TIMESTAMP, " +
            "start_date = ?, end_date = ?, allow_replies = ?, allow_rating = ?, " +
            "priority_id = ?, enabled = ?, status = ?, category_id = ?, " +
            "classification_id = ?, template_id = ? " +
            "WHERE news_id = ? " +
            "AND modified = ? ");

    if (pageTitle != null) {
      pst.setString(++i, pageTitle);
    }
    if (keywords != null) {
      pst.setString(++i, keywords);
    }
    if (description != null) {
      pst.setString(++i, description);
    }
    if (metaName != null) {
      pst.setString(++i, metaName);
    }
    if (metaContent != null) {
      pst.setString(++i, metaContent);
    }
    if (portalKey != null) {
      pst.setString(++i, portalKey);
    }
    pst.setString(++i, subject);
    pst.setString(++i, intro);
    pst.setString(++i, message);
    pst.setInt(++i, this.getModifiedBy());
    DatabaseUtils.setTimestamp(pst, ++i, startDate);
    DatabaseUtils.setTimestamp(pst, ++i, endDate);
    pst.setBoolean(++i, allowReplies);
    pst.setBoolean(++i, allowRatings);
    DatabaseUtils.setInt(pst, ++i, priorityId);
    pst.setBoolean(++i, enabled);
    DatabaseUtils.setInt(pst, ++i, status);
    DatabaseUtils.setInt(pst, ++i, categoryId);
    DatabaseUtils.setInt(pst, ++i, classificationId);
    DatabaseUtils.setInt(pst, ++i, templateId);
    pst.setInt(++i, id);
    pst.setTimestamp(++i, modified);
    resultCount = pst.executeUpdate();
    pst.close();
    if (resultCount == 1) {
      // See if there is a link already
      boolean hasTaskCategoryLink = false;
      boolean sameTaskCategoryLink = false;
      pst = db.prepareStatement(
          "SELECT category_id " +
              "FROM taskcategorylink_news " +
              "WHERE news_id = ? ");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        hasTaskCategoryLink = true;
        if (rs.getInt("category_id") == taskCategoryId) {
          sameTaskCategoryLink = true;
        }
      }
      rs.close();
      pst.close();
      //
      if (hasTaskCategoryLink && taskCategoryId > -1 && !sameTaskCategoryLink) {
        // Delete the previous link(s)
        deleteTaskCategoryLink(db);
        // Insert the new link
        insertTaskCategoryLink(db);
      }
      //
      if (hasTaskCategoryLink && taskCategoryId == -1) {
        // Delete the previous link(s)
        deleteTaskCategoryLink(db);
      }
      //
      if (!hasTaskCategoryLink && taskCategoryId > -1) {
        // Insert the new link
        insertTaskCategoryLink(db);
      }
    }
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void insertTaskCategoryLink(Connection db) throws SQLException {
    PreparedStatement pst = null;
    // First, make sure the category belongs to this project
    boolean belongsToProject = false;
    pst = db.prepareStatement(
        "SELECT category_id " +
            "FROM taskcategory_project " +
            "WHERE category_id = ? " +
            "AND project_id = ? ");
    pst.setInt(1, taskCategoryId);
    pst.setInt(2, projectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      belongsToProject = true;
    }
    rs.close();
    pst.close();
    if (belongsToProject) {
      // Then insert the link
      pst = db.prepareStatement(
          "INSERT INTO taskcategorylink_news " +
              "(category_id, news_id) VALUES (?, ?) ");
      pst.setInt(1, taskCategoryId);
      pst.setInt(2, id);
      pst.execute();
      pst.close();
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void deleteTaskCategoryLink(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE from taskcategorylink_news " +
            "WHERE news_id = ? ");
    pst.setInt(1, this.getId());
    pst.execute();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int updatePage(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news " +
            "SET message = ?, " +
            "modifiedBy = ?, " +
            "modified = CURRENT_TIMESTAMP " +
            "WHERE news_id = ? ");
    pst.setString(++i, message);
    pst.setInt(++i, this.getModifiedBy());
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }


  public int updatePortal(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news " +
            "SET portal_key = ? " +
            "WHERE news_id = ? ");
    pst.setString(++i, portalKey);
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int deletePage(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news " +
            "SET message = null, " +
            "modifiedBy = ?, modified = CURRENT_TIMESTAMP " +
            "WHERE news_id = ? ");
    pst.setInt(++i, this.getModifiedBy());
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    message = null;
    return resultCount;
  }


  /**
   * Deletes the specified news article from the database
   *
   * @param db       database connection
   * @param basePath Path to where newsarticle files are stored
   * @return boolean on success
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db, String basePath) throws SQLException {
    if (id == -1 || projectId == -1) {
      throw new SQLException("ID was not specified");
    }
    boolean commit = db.getAutoCommit();
    PreparedStatement pst = null;
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      // Delete task category link
      deleteTaskCategoryLink(db);
      // Delete attached files
      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.BLOG_POST_FILES);
      files.setLinkItemId(id);
      files.buildList(db);
      files.delete(db, basePath);
      // Delete the collaboration items
      Rating.delete(db, id, TABLE, PRIMARY_KEY);
      Viewing.delete(db, id, TABLE, PRIMARY_KEY);
      BlogPostCommentList postCommentList = new BlogPostCommentList();
      postCommentList.setNewsId(id);
      postCommentList.buildList(db);
      postCommentList.delete(db);

      // Delete the news
      pst = db.prepareStatement(
          "DELETE FROM project_news " +
              "WHERE news_id = ? ");
      pst.setInt(1, id);
      pst.execute();
      pst.close();
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }


  /**
   * The following fields depend on a timezone preference
   *
   * @return The dateTimeParams value
   */
  public static ArrayList<String> getTimeZoneParams() {
    ArrayList<String> thisList = new ArrayList<String>();
    thisList.add("startDate");
    thisList.add("endDate");
    return thisList;
  }

  public boolean archive(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int resultCount = 0;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_news " +
            "SET end_date = CURRENT_TIMESTAMP, " +
            "modifiedBy = ?, modified = CURRENT_TIMESTAMP " +
            "WHERE news_id = ? ");
    pst.setInt(++i, this.getModifiedBy());
    pst.setInt(++i, id);
    resultCount = pst.executeUpdate();
    pst.close();
    endDate = new Timestamp(System.currentTimeMillis());
    return resultCount == 1;
  }
}

