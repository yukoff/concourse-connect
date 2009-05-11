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

package com.concursive.connect.web.modules.discussion.dao;

import com.concursive.commons.db.DatabaseUtils;
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
import java.util.Calendar;

/**
 * Discussion forum topic object properties
 *
 * @author matt rajkowski
 * @created July 23, 2001
 */
public class Topic extends GenericBean {

  public static final String TABLE = "project_issues";
  public static final String PRIMARY_KEY = "issue_id";

  private int id = -1;
  private int projectId = -1;
  private int categoryId = -1;
  private String subject = null;
  private String body = "";
  private int importance = -1;
  private boolean enabled = true;
  private java.sql.Timestamp entered = null;
  private int enteredBy = -1;
  private java.sql.Timestamp modified = null;
  private int modifiedBy = -1;
  private boolean question = false;
  private int solutionReplyId = -1;

  private int readCount = 0;
  private int replyCount = 0;
  private java.sql.Timestamp replyDate = null;
  private int replyBy = -1;

  private Timestamp readDate = null;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAvg = 0.0;
  private int inappropriateCount = 0;
  // File attachments
  private String attachmentList = null;
  //Resources
  private ReplyList replyList = new ReplyList();
  private FileItemList files = new FileItemList();


  /**
   * Constructor for the Issue object
   */
  public Topic() {
  }


  /**
   * Constructor for the Issue object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Topic(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the Issue object
   *
   * @param db        Description of the Parameter
   * @param issueId   Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Topic(Connection db, int issueId, int projectId) throws SQLException {
    this.projectId = projectId;
    queryRecord(db, issueId);
  }


  /**
   * Constructor for the Issue object
   *
   * @param db      Description of the Parameter
   * @param issueId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Topic(Connection db, int issueId) throws SQLException {
    queryRecord(db, issueId);
  }


  /**
   * Description of the Method
   *
   * @param db      Description of the Parameter
   * @param issueId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int issueId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT i.* " +
            "FROM project_issues i " +
            "WHERE issue_id = ? ");
    if (projectId > -1) {
      sql.append("AND project_id = ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, issueId);
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
      throw new SQLException("Issue record not found.");
    }
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    //project_issues table
    id = rs.getInt("issue_id");
    projectId = rs.getInt("project_id");
    categoryId = DatabaseUtils.getInt(rs, "category_id");
    subject = rs.getString("subject");
    body = rs.getString("message");
    importance = DatabaseUtils.getInt(rs, "importance");
    enabled = rs.getBoolean("enabled");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    replyCount = rs.getInt("reply_count");
    replyDate = rs.getTimestamp("last_reply_date");
    if (replyDate == null) {
      replyDate = modified;
    }
    replyBy = DatabaseUtils.getInt(rs, "last_reply_by");
    question = rs.getBoolean("question");
    solutionReplyId = DatabaseUtils.getInt(rs, "solution_reply_id");
    readCount = rs.getInt("read_count");
    readDate = rs.getTimestamp("read_date");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildReplyList(Connection db) throws SQLException {
    //replyList = new IssueReplyList();
    replyList.setIssueId(this.getId());
    replyList.buildList(db);
  }


  /**
   * Gets the RelativeIssueDateString attribute of the Issue object
   *
   * @return The RelativeIssueDateString value
   */
  public String getRelativeEnteredString() {
    Calendar rightNow = Calendar.getInstance();
    rightNow.set(Calendar.HOUR_OF_DAY, 0);
    rightNow.set(Calendar.MINUTE, 0);
    Calendar issuePostedDate = Calendar.getInstance();
    issuePostedDate.setTime(entered);
    issuePostedDate.set(Calendar.HOUR_OF_DAY, 0);
    issuePostedDate.set(Calendar.MINUTE, 0);
    issuePostedDate.add(Calendar.DATE, 1);
    if (rightNow.before(issuePostedDate)) {
      return "today";
    } else {
      issuePostedDate.add(Calendar.DATE, 1);
      if (rightNow.before(issuePostedDate)) {
        return "yesterday";
      } else {
        return getEnteredString();
      }
    }
  }


  /**
   * Sets the id attribute of the Issue object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the Issue object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the projectId attribute of the Issue object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the Issue object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the categoryId attribute of the Issue object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the categoryId attribute of the Issue object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }


  /**
   * Sets the subject attribute of the Issue object
   *
   * @param tmp The new subject value
   */
  public void setSubject(String tmp) {
    this.subject = tmp;
  }


  /**
   * Sets the body attribute of the Issue object
   *
   * @param tmp The new body value
   */
  public void setBody(String tmp) {
    this.body = tmp;
  }


  /**
   * Sets the importance attribute of the Issue object
   *
   * @param tmp The new importance value
   */
  public void setImportance(int tmp) {
    this.importance = tmp;
  }


  /**
   * Sets the importance attribute of the Issue object
   *
   * @param tmp The new importance value
   */
  public void setImportance(String tmp) {
    this.importance = Integer.parseInt(tmp);
  }


  /**
   * Sets the enabled attribute of the Issue object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(boolean tmp) {
    this.enabled = tmp;
  }


  /**
   * Sets the enabled attribute of the Issue object
   *
   * @param tmp The new enabled value
   */
  public void setEnabled(String tmp) {
    this.enabled = ("on".equalsIgnoreCase(tmp) || "true".equalsIgnoreCase(tmp));
  }


  /**
   * Sets the entered attribute of the Issue object
   *
   * @param tmp The new entered value
   */
  public void setEntered(java.sql.Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the Issue object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the Issue object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the Issue object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the Issue object
   *
   * @param tmp The new modified value
   */
  public void setModified(java.sql.Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the Issue object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the Issue object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the Issue object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }

  /**
   * Sets the allowRegistration attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowRegistration value
   */
  public void setQuestion(boolean tmp) {
    this.question = tmp;
  }


  /**
   * Sets the allowRegistration attribute of the SiteSettingsBean object
   *
   * @param tmp The new allowRegistration value
   */
  public void setQuestion(String tmp) {
    this.question = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getQuestion() {
    return question;
  }

  public int getReadCount() {
    return readCount;
  }

  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }

  /**
   * Sets the replyCount attribute of the Issue object
   *
   * @param tmp The new replyCount value
   */
  public void setReplyCount(int tmp) {
    this.replyCount = tmp;
  }


  /**
   * Sets the replyDate attribute of the Issue object
   *
   * @param tmp The new replyDate value
   */
  public void setReplyDate(java.sql.Timestamp tmp) {
    this.replyDate = tmp;
  }


  /**
   * Sets the replyDate attribute of the Issue object
   *
   * @param tmp The new replyDate value
   */
  public void setReplyDate(String tmp) {
    this.replyDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the replyBy attribute of the Issue object
   *
   * @param tmp The new replyBy value
   */
  public void setReplyBy(int tmp) {
    this.replyBy = tmp;
  }


  /**
   * Sets the replyBy attribute of the Issue object
   *
   * @param tmp The new replyBy value
   */
  public void setReplyBy(String tmp) {
    this.replyBy = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the Issue object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the projectId attribute of the Issue object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Gets the categoryId attribute of the Issue object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * Gets the subject attribute of the Issue object
   *
   * @return The subject value
   */
  public String getSubject() {
    return subject;
  }


  /**
   * Gets the body attribute of the Issue object
   *
   * @return The body value
   */
  public String getBody() {
    return body;
  }


  /**
   * Gets the importance attribute of the Issue object
   *
   * @return The importance value
   */
  public int getImportance() {
    return importance;
  }


  /**
   * Gets the enabled attribute of the Issue object
   *
   * @return The enabled value
   */
  public boolean getEnabled() {
    return enabled;
  }


  /**
   * Gets the entered attribute of the Issue object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredString attribute of the Issue object
   *
   * @return The enteredString value
   */
  public String getEnteredString() {
    String tmp = "";
    try {
      return DateFormat.getDateInstance(3).format(entered);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the enteredDateTimeString attribute of the Issue object
   *
   * @return The enteredDateTimeString value
   */
  public String getEnteredDateTimeString() {
    String tmp = "";
    try {
      return DateFormat.getDateTimeInstance(3, 3).format(entered);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the enteredBy attribute of the Issue object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the Issue object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedBy attribute of the Issue object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the replyCount attribute of the Issue object
   *
   * @return The replyCount value
   */
  public int getReplyCount() {
    return replyCount;
  }


  /**
   * Gets the replyCountString attribute of the Issue object
   *
   * @return The replyCountString value
   */
  public String getReplyCountString() {
    if (replyCount == 0) {
      return "are no replies";
    } else if (replyCount == 1) {
      return "is 1 reply";
    } else {
      return "are " + replyCount + " replies";
    }
  }


  /**
   * Gets the replyDateTimeString attribute of the Issue object
   *
   * @return The replyDateTimeString value
   */
  public String getReplyDateTimeString() {
    String tmp = "";
    try {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(replyDate);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the replyDateString attribute of the Issue object
   *
   * @return The replyDateString value
   */
  public String getReplyDateString() {
    String tmp = "";
    try {
      return DateFormat.getDateInstance(3).format(replyDate);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the replyDate attribute of the Issue object
   *
   * @return The replyDate value
   */
  public Timestamp getReplyDate() {
    return replyDate;
  }


  /**
   * Gets the replyBy attribute of the Issue object
   *
   * @return The replyBy value
   */
  public int getReplyBy() {
    return replyBy;
  }

  public int getSolutionReplyId() {
    return solutionReplyId;
  }

  public void setSolutionReplyId(int solutionReplyId) {
    this.solutionReplyId = solutionReplyId;
  }

  public void setSolutionReplyId(String solutionReplyId) {
    this.solutionReplyId = Integer.parseInt(solutionReplyId);
  }

  /**
   * Gets the replyList attribute of the Issue object
   *
   * @return The replyList value
   */
  public ReplyList getReplyList() {
    return replyList;
  }

  public FileItemList getFiles() {
    return files;
  }

  public boolean hasFiles() {
    return (files != null && files.size() > 0);
  }

  public String getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(String attachmentList) {
    this.attachmentList = attachmentList;
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
   * @return the ratingCount
   */
  public int getRatingCount() {
    return ratingCount;
  }


  /**
   * @param ratingCount the ratingCount to set
   */
  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public void setRatingCount(String ratingCount) {
    this.ratingCount = Integer.parseInt(ratingCount);
  }

  /**
   * @return the ratingValue
   */
  public int getRatingValue() {
    return ratingValue;
  }


  /**
   * @param ratingValue the ratingValue to set
   */
  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public void setRatingValue(String ratingValue) {
    this.ratingValue = Integer.parseInt(ratingValue);
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

  public Project getProject() {
    return ProjectUtils.loadProject(projectId);
  }

  public User getUser() {
    return UserUtils.loadUser(enteredBy);
  }

  /**
   * Gets the valid attribute of the Issue object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (projectId == -1) {
      errors.put("actionError", "Project ID not specified");
    }
    if (!StringUtils.hasText(subject)) {
      errors.put("subjectError", "Required field");
    }
    if (!StringUtils.hasText(body)) {
      errors.put("bodyError", "Required field");
    }
    if (categoryId == -1) {
      errors.put("categoryIdError", "Required");
    }
    return !hasErrors();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    if (!isValid()) {
      return false;
    }
    StringBuffer sql = new StringBuffer();
    sql.append(
        "INSERT INTO project_issues " +
            "(project_id, category_id, subject, message, importance, enabled, solution_reply_id, ");
    if (entered != null) {
      sql.append("entered, ");
    }
    if (modified != null) {
      sql.append("modified, ");
    }
    if (replyDate != null) {
      sql.append("last_reply_date, ");
    }
    sql.append(
        "enteredBy, modifiedBy, " +
            "reply_count, last_reply_by, question) ");
    sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ");
    if (entered != null) {
      sql.append("?, ");
    }
    if (modified != null) {
      sql.append("?, ");
    }
    if (replyDate != null) {
      sql.append("?, ");
    }
    sql.append("?, ?, ?, ?, ?) ");
    int i = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      //Insert the topic
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, projectId);
      pst.setInt(++i, categoryId);
      pst.setString(++i, subject);
      pst.setString(++i, body);
      DatabaseUtils.setInt(pst, ++i, importance);
      pst.setBoolean(++i, enabled);
      DatabaseUtils.setInt(pst, ++i, solutionReplyId);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      if (replyDate != null) {
        DatabaseUtils.setTimestamp(pst, ++i, replyDate);
      }
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      pst.setInt(++i, replyCount);
      DatabaseUtils.setInt(pst, ++i, replyBy);
      pst.setBoolean(++i, question);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_issues_issue_id_seq", -1);
      //Update the category count
      i = 0;
      pst = db.prepareStatement(
          "UPDATE project_issues_categories " +
              "SET topics_count = topics_count + 1, " +
              "posts_count = posts_count + 1, " +
              (entered != null ? "last_post_date = ?, " :
                  "last_post_date = " + DatabaseUtils.getCurrentTimestamp(db) + ", ") +
              "last_post_by = ? " +
              "WHERE project_id = ? " +
              "AND category_id = ? ");
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.setInt(++i, modifiedBy);
      pst.setInt(++i, projectId);
      pst.setInt(++i, categoryId);
      pst.executeUpdate();
      pst.close();
      if (attachmentList != null) {
        FileItemList.convertTempFiles(db, Constants.DISCUSSION_FILES_TOPIC, this.getModifiedBy(), id, attachmentList);
      }
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      throw e;
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param filePath Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public synchronized boolean delete(Connection db, String filePath) throws SQLException {
    if (id == -1 || projectId == -1 || categoryId == -1) {
      throw new SQLException("Issue ID was not specified");
    }
    boolean canDelete = false;
    boolean autoCommit = db.getAutoCommit();
    try {
      PreparedStatement pst = null;
      int i = 0;
      //Make sure the issue exists, then delete all
      pst = db.prepareStatement(
          "SELECT count(issue_id) AS issue_count " +
              "FROM project_issues " +
              "WHERE issue_id = ?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        canDelete = (rs.getInt("issue_count") == 1);
      }
      rs.close();
      pst.close();
      if (canDelete) {
        buildFiles(db);
        if (autoCommit) {
          db.setAutoCommit(false);
        }
        files.delete(db, filePath);
        //Delete the replies
        ReplyList replyList = new ReplyList();
        replyList.setIssueId(id);
        replyList.setProjectId(projectId);
        replyList.setCategoryId(categoryId);
        replyList.buildList(db);
        int replyCount = replyList.size();
        replyList.delete(db, filePath);
        //Update the category count (plus the issue count=1)
        i = 0;
        pst = db.prepareStatement(
            "UPDATE project_issues_categories " +
                "SET posts_count = posts_count - " + (replyCount + 1) + ", " +
                "topics_count = topics_count - 1 " +
                "WHERE project_id = ? " +
                "AND category_id = ? ");
        pst.setInt(++i, projectId);
        pst.setInt(++i, categoryId);
        pst.executeUpdate();
        pst.close();
        /*// Delete the distribution list
        pst = db.prepareStatement(
            "DELETE FROM project_issues_contacts " +
                "WHERE issue_id = ? ");
        pst.setInt(1, this.getId());
        pst.execute();*/
        //Delete the views
        Viewing.delete(db, id, TABLE, PRIMARY_KEY);

        //Delete Issue Rating
        Rating.delete(db, id, TABLE, PRIMARY_KEY);

        //Delete the issue
        pst = db.prepareStatement(
            "DELETE FROM project_issues " +
                "WHERE issue_id = ? ");
        pst.setInt(1, id);
        pst.execute();
        pst.close();
        if (autoCommit) {
          db.commit();
        }
      }
    } catch (SQLException e) {
      if (autoCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    if (this.getId() == -1 || this.projectId == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_issues " +
            "SET subject = ?, message = ?, importance = ?, question = ?, " +
            "modifiedBy = ?, modified = CURRENT_TIMESTAMP " +
            "WHERE issue_id = ? " +
            "AND modified = ? ");
    pst.setString(++i, subject);
    pst.setString(++i, body);
    DatabaseUtils.setInt(pst, ++i, importance);
    pst.setBoolean(++i, question);
    pst.setInt(++i, this.getModifiedBy());
    pst.setInt(++i, this.getId());
    pst.setTimestamp(++i, modified);
    resultCount = pst.executeUpdate();
    pst.close();

    // If the issue went from a question, to not a question, then several
    // things need to be cleaned up

    return resultCount;
  }

  public void buildFiles(Connection db) throws SQLException {
    files = new FileItemList();
    files.setLinkModuleId(Constants.DISCUSSION_FILES_TOPIC);
    files.setLinkItemId(this.getId());
    files.buildList(db);
  }
}
