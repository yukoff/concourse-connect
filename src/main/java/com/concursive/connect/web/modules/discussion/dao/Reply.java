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
import com.concursive.connect.web.modules.discussion.utils.DiscussionUtils;
import com.concursive.connect.web.modules.documents.dao.FileItemList;

import java.sql.*;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Discussion forum topic reply object
 *
 * @author matt rajkowski
 * @created January 15, 2003
 */
public class Reply extends GenericBean {

  public static final String TABLE = "project_issue_replies";
  public static final String PRIMARY_KEY = "reply_id";

  private int id = -1;
  private int replyToId = -1;
  private String subject = null;
  private String body = "";
  private int importance = -1;
  //private boolean enabled = false;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private int modifiedBy = -1;
  private int issueId = -1;
  private boolean helpful = false;
  private boolean solution = false;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAverage = 0.0;
  private int inappropriateCount = 0;
  private Timestamp solutionDate = null;

  // @todo replace this by using a cache for forums and topics
  //helpers
  private int projectId = -1;
  private int categoryId = -1;
  // File attachments
  private String attachmentList = null;
  //Resources
  private FileItemList files = new FileItemList();

  private int answered = -1;
  // Helper constants to evaluate a reply
  public static int ANSWERED = 1;
  public static int HELPFUL = 2;
  public static int NOT_ANSWERED = 3;
  public static int ANSWER_NOT_REQUIRED = 4;


  /**
   * Constructor for the IssueReply object
   */
  public Reply() {
  }


  /**
   * Constructor for the IssueReply object
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Reply(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Constructor for the IssueReply object
   *
   * @param db      Description of the Parameter
   * @param replyId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Reply(Connection db, int replyId) throws SQLException {
    queryRecord(db, replyId);
  }


  /**
   * Constructor for the IssueReply object
   *
   * @param db      Description of the Parameter
   * @param replyId Description of the Parameter
   * @param issueId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Reply(Connection db, int replyId, int issueId) throws SQLException {
    this.setIssueId(issueId);
    queryRecord(db, replyId);
  }


  /**
   * Description of the Method
   *
   * @param db      Description of the Parameter
   * @param replyId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void queryRecord(Connection db, int replyId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT r.* " +
            "FROM project_issue_replies r " +
            "WHERE reply_id = ? ");
    if (issueId > -1) {
      sql.append("AND issue_id = ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setInt(1, replyId);
    if (issueId > -1) {
      pst.setInt(2, issueId);
    }
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    } else {
      rs.close();
      pst.close();
      throw new SQLException("Issue Reply record not found.");
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    //project_issue_replies table
    id = rs.getInt("reply_id");
    issueId = rs.getInt("issue_id");
    replyToId = rs.getInt("reply_to");
    subject = rs.getString("subject");
    body = rs.getString("message");
    importance = rs.getInt("importance");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    helpful = rs.getBoolean("helpful");
    solution = rs.getBoolean("solution");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAverage = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
    solutionDate = rs.getTimestamp("solution_date");
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
   * Sets the id attribute of the IssueReply object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the IssueReply object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the replyToId attribute of the IssueReply object
   *
   * @param tmp The new replyToId value
   */
  public void setReplyToId(int tmp) {
    this.replyToId = tmp;
  }


  /**
   * Sets the replyToId attribute of the IssueReply object
   *
   * @param tmp The new replyToId value
   */
  public void setReplyToId(String tmp) {
    this.replyToId = Integer.parseInt(tmp);
  }


  /**
   * Sets the subject attribute of the IssueReply object
   *
   * @param tmp The new subject value
   */
  public void setSubject(String tmp) {
    this.subject = tmp;
  }


  /**
   * Sets the body attribute of the IssueReply object
   *
   * @param tmp The new body value
   */
  public void setBody(String tmp) {
    this.body = tmp;
  }


  /**
   * Sets the importance attribute of the IssueReply object
   *
   * @param tmp The new importance value
   */
  public void setImportance(int tmp) {
    this.importance = tmp;
  }


  /**
   * Sets the importance attribute of the IssueReply object
   *
   * @param tmp The new importance value
   */
  public void setImportance(String tmp) {
    this.importance = Integer.parseInt(tmp);
  }


  /**
   * Sets the entered attribute of the IssueReply object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the entered attribute of the IssueReply object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the enteredBy attribute of the IssueReply object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the enteredBy attribute of the IssueReply object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the IssueReply object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the modified attribute of the IssueReply object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modifiedBy attribute of the IssueReply object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the IssueReply object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the issueId attribute of the IssueReply object
   *
   * @param tmp The new issueId value
   */
  public void setIssueId(int tmp) {
    this.issueId = tmp;
  }


  /**
   * Sets the issueId attribute of the IssueReply object
   *
   * @param tmp The new issueId value
   */
  public void setIssueId(String tmp) {
    this.issueId = Integer.parseInt(tmp);
  }


  /**
   * Sets the categoryId attribute of the IssueReply object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the categoryId attribute of the IssueReply object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    this.categoryId = Integer.parseInt(tmp);
  }


  /**
   * Sets the projectId attribute of the IssueReply object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the IssueReply object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Gets the id attribute of the IssueReply object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the replyToId attribute of the IssueReply object
   *
   * @return The replyToId value
   */
  public int getReplyToId() {
    return replyToId;
  }


  /**
   * Gets the subject attribute of the IssueReply object
   *
   * @return The subject value
   */
  public String getSubject() {
    return subject;
  }


  /**
   * Gets the body attribute of the IssueReply object
   *
   * @return The body value
   */
  public String getBody() {
    return body;
  }


  /**
   * Gets the importance attribute of the IssueReply object
   *
   * @return The importance value
   */
  public int getImportance() {
    return importance;
  }


  /**
   * Gets the entered attribute of the IssueReply object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredBy attribute of the IssueReply object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the IssueReply object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedBy attribute of the IssueReply object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the issueId attribute of the IssueReply object
   *
   * @return The issueId value
   */
  public int getIssueId() {
    return issueId;
  }


  /**
   * Gets the enteredString attribute of the IssueReply object
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
   * Gets the enteredDateTimeString attribute of the IssueReply object
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

  public boolean getHelpful() {
    return helpful;
  }

  public void setHelpful(boolean helpful) {
    this.helpful = helpful;
  }

  public void setHelpful(String tmp) {
    helpful = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean getSolution() {
    return solution;
  }

  public void setSolution(boolean solution) {
    this.solution = solution;
  }

  public void setSolution(String tmp) {
    solution = DatabaseUtils.parseBoolean(tmp);
  }


  public int getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public int getRatingValue() {
    return ratingValue;
  }

  public void setRatingValue(int ratingValue) {
    this.ratingValue = ratingValue;
  }

  public double getRatingAverage() {
    return ratingAverage;
  }

  public void setRatingAverage(double ratingAverage) {
    this.ratingAverage = ratingAverage;
  }

  public void setRatingAverage(String ratingAverage) {
    this.ratingAverage = Double.parseDouble(ratingAverage);
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


  /**
   * @param inappropriateCount the inappropriateCount to set
   */
  public void setInappropriateCount(String inappropriateCount) {
    this.inappropriateCount = Integer.parseInt(inappropriateCount);
  }


  /**
   * @return the solutionDate
   */
  public Timestamp getSolutionDate() {
    return solutionDate;
  }


  /**
   * @param solutionDate the solutionDate to set
   */
  public void setSolutionDate(Timestamp solutionDate) {
    this.solutionDate = solutionDate;
  }

  public void setSolutionDate(String solutionDate) {
    this.solutionDate = DatabaseUtils.parseTimestamp(solutionDate);
  }

  /**
   * Gets the categoryId attribute of the IssueReply object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * Gets the projectId attribute of the IssueReply object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * @return the answered
   */
  public int getAnswered() {
    return answered;
  }


  /**
   * @param answered the answered to set
   */
  public void setAnswered(String answered) {
    this.answered = Integer.parseInt(answered);
  }

  /**
   * @param answered the answered to set
   */
  public void setAnswered(int answered) {
    this.answered = answered;
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
   * Gets the valid attribute of the IssueReply object
   *
   * @return The valid value
   */
  private boolean isValid() {
    if (projectId == -1) {
      errors.put("actionError", "Project ID not specified");
    }
    if (categoryId == -1) {
      errors.put("actionError", "Category ID not specified");
    }
    if (issueId == -1) {
      errors.put("actionError", "Issue ID not specified");
    }
    if (!StringUtils.hasText(subject)) {
      errors.put("subjectError", "Required field");
    }
    if (!StringUtils.hasText(body)) {
      errors.put("bodyError", "Required field");
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
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      // Insert the reply
      StringBuffer sql = new StringBuffer();
      sql.append(
          "INSERT INTO project_issue_replies " +
              "(issue_id, reply_to, subject, message, importance, helpful, solution, solution_date, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      sql.append("enteredby, modifiedby ) ");
      sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ");
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      sql.append("?, ?) ");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, issueId);
      pst.setInt(++i, replyToId);
      pst.setString(++i, subject);
      pst.setString(++i, body);
      pst.setInt(++i, importance);
      pst.setBoolean(++i, helpful);
      pst.setBoolean(++i, solution);
      pst.setTimestamp(++i, solutionDate);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_issue_repl_reply_id_seq", -1);
      // Update the issue count
      i = 0;
      pst = db.prepareStatement(
          "UPDATE project_issues " +
              "SET reply_count = reply_count + 1, " +
              (entered != null ? "last_reply_date = ?, " :
                  "last_reply_date = " + DatabaseUtils.getCurrentTimestamp(db) + ", ") +
              "last_reply_by = ? " +
              "WHERE project_id = ? " +
              "AND issue_id = ? ");
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      pst.setInt(++i, modifiedBy);
      pst.setInt(++i, projectId);
      pst.setInt(++i, issueId);
      pst.executeUpdate();
      pst.close();
      // Update the category count
      i = 0;
      pst = db.prepareStatement(
          "UPDATE project_issues_categories " +
              "SET posts_count = posts_count + 1, " +
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
        FileItemList.convertTempFiles(db, Constants.DISCUSSION_FILES_REPLY, this.getModifiedBy(), id, attachmentList);
      }
      if (replyToId > -1) {
        // Update the topic response
        DiscussionUtils.updateTopicResponse(db, this);
      }
      if (autoCommit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (autoCommit) {
        db.rollback();
      }
      throw e;
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
  public synchronized boolean delete(Connection db, String filePath) throws SQLException {
    if (id == -1 || issueId == -1 || projectId == -1 || categoryId == -1) {
      throw new SQLException("IssueReply ID was not specified");
    }
    boolean canDelete = false;
    boolean autoCommit = db.getAutoCommit();
    try {
      PreparedStatement pst = null;
      int i = 0;
      //Make sure the reply exists, then delete all
      pst = db.prepareStatement(
          "SELECT count(reply_id) AS reply_count " +
              "FROM project_issue_replies " +
              "WHERE reply_id = ?");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        canDelete = (rs.getInt("reply_count") == 1);
      }
      rs.close();
      pst.close();
      if (canDelete) {
        buildFiles(db);
        if (autoCommit) {
          db.setAutoCommit(false);
        }
        files.delete(db, filePath);
        //Update the category count
        pst = db.prepareStatement(
            "UPDATE project_issues_categories " +
                "SET posts_count = posts_count - 1 " +
                "WHERE project_id = ? " +
                "AND category_id = ? ");
        pst.setInt(++i, projectId);
        pst.setInt(++i, categoryId);
        pst.executeUpdate();
        pst.close();
        //Update the issue count
        i = 0;
        pst = db.prepareStatement(
            "UPDATE project_issues " +
                "SET reply_count = reply_count - 1 " +
                "WHERE project_id = ? " +
                "AND issue_id = ? ");
        pst.setInt(++i, projectId);
        pst.setInt(++i, issueId);
        pst.executeUpdate();
        pst.close();

        //update solution reply id of the project if this reply was the solution
        i = 0;
        pst = db.prepareStatement(
            "UPDATE project_issues " +
                "SET solution_reply_id = ? " +
                "WHERE project_id = ? " +
                "AND issue_id = ? " +
                "AND solution_reply_id = ? ");
        DatabaseUtils.setInt(pst, ++i, -1);
        pst.setInt(++i, projectId);
        pst.setInt(++i, issueId);
        DatabaseUtils.setInt(pst, ++i, id);
        pst.executeUpdate();
        pst.close();

        //Deleting ratings for this reply
        Rating.delete(db, id, TABLE, PRIMARY_KEY);

        //Delete the reply
        pst = db.prepareStatement(
            "DELETE FROM project_issue_replies " +
                "WHERE reply_id = ? ");
        pst.setInt(1, id);
        pst.execute();
        pst.close();
        // Update the last reply date by finding the latest reply, or by using
        // the original modified date
        pst = db.prepareStatement(
            "SELECT entered, enteredby " +
                "FROM project_issue_replies " +
                "WHERE issue_id = ? " +
                "ORDER BY entered desc "
        );
        pst.setInt(1, issueId);
        rs = pst.executeQuery();
        Timestamp lastReplyTimestamp = null;
        int lastReplyBy = -1;
        if (rs.next()) {
          lastReplyTimestamp = rs.getTimestamp("entered");
          lastReplyBy = rs.getInt("enteredby");
        }
        rs.close();
        pst.close();
        if (lastReplyTimestamp != null) {
          pst = db.prepareStatement(
              "UPDATE project_issues " +
                  "SET last_reply_date = ?, last_reply_by = ? " +
                  "WHERE issue_id = ? " +
                  "AND last_reply_date <> ? ");
          pst.setTimestamp(1, lastReplyTimestamp);
          pst.setInt(2, lastReplyBy);
          pst.setInt(3, issueId);
          pst.setTimestamp(4, lastReplyTimestamp);
          pst.executeUpdate();
          pst.close();
        } else {
          pst = db.prepareStatement("UPDATE project_issues " +
              "SET last_reply_date = entered, last_reply_by = enteredby " +
              "WHERE issue_id = ? " +
              "AND last_reply_date <> entered ");
          pst.setInt(1, issueId);
          pst.executeUpdate();
          pst.close();
        }
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
    if (this.getId() == -1 || this.issueId == -1 || this.getEnteredBy() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid()) {
      return -1;
    }
    int resultCount = 0;
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = null;
      StringBuffer sql = new StringBuffer();
      sql.append(
          "UPDATE project_issue_replies " +
              "SET subject = ?, message = ?, importance = ?, " +
              "modifiedby = ?, modified = CURRENT_TIMESTAMP " +
              "WHERE reply_id = ? " +
              "AND modified = ? ");
      int i = 0;
      pst = db.prepareStatement(sql.toString());
      pst.setString(++i, subject);
      pst.setString(++i, body);
      pst.setInt(++i, importance);
      pst.setInt(++i, this.getModifiedBy());
      pst.setInt(++i, this.getId());
      pst.setTimestamp(++i, modified);
      resultCount = pst.executeUpdate();
      pst.close();
      if (resultCount == 1 && replyToId > -1) {
        // Update the topic response
        DiscussionUtils.updateTopicResponse(db, this);
      }
      if (autoCommit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (autoCommit) {
        db.rollback();
      }
      throw (e);
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
    return resultCount;
  }

  public void buildFiles(Connection db) throws SQLException {
    files = new FileItemList();
    files.setLinkModuleId(Constants.DISCUSSION_FILES_REPLY);
    files.setLinkItemId(this.getId());
    files.buildList(db);
  }

  public void updateSolutionForTopicAndReply(Connection db, boolean isQuestion) throws SQLException {
    if (this.getId() == -1 || this.projectId == -1 || this.issueId == -1) {
      throw new SQLException("ID was not specified");
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int i = 0;
      PreparedStatement pst = null;
      if (getSolution()) {
        // Remove the flag for other replies
        i = 0;
        pst = db.prepareStatement(
            "UPDATE project_issue_replies " +
                "SET solution = ? , " +
                "solution_date = ? " +
                "WHERE issue_id = ? AND reply_id <> ? ");
        pst.setBoolean(++i, false);
        pst.setTimestamp(++i, null);
        pst.setInt(++i, issueId);
        pst.setInt(++i, id);
        pst.executeUpdate();
        pst.close();
        // Update the issue
        i = 0;
        pst = db.prepareStatement(
            "UPDATE project_issues " +
                "SET solution_reply_id = ? " +
                "WHERE issue_id = ? ");
        pst.setInt(++i, id);
        pst.setInt(++i, issueId);
        pst.executeUpdate();
        pst.close();
      } else {
        // Make sure the issue doesn't have this set as the solution if it isn't
        i = 0;
        pst = db.prepareStatement(
            "UPDATE project_issues " +
                "SET solution_reply_id = ? " +
                "WHERE issue_id = ? AND solution_reply_id = ?");
        DatabaseUtils.setInt(pst, ++i, -1);
        pst.setInt(++i, issueId);
        pst.setInt(++i, id);
        pst.executeUpdate();
        pst.close();
      }
      // Update the reply
      i = 0;
      pst = db.prepareStatement(
          "UPDATE project_issue_replies " +
              "SET helpful = ?, solution = ? , " +
              (this.getSolution() ? " solution_date = " + DatabaseUtils.getCurrentTimestamp(db) + " " : " solution_date = ? ") +
              "WHERE reply_id = ? ");
      pst.setBoolean(++i, this.getHelpful());
      pst.setBoolean(++i, this.getSolution());
      if (this.getSolution()) {
        //Do nothing
      } else {
        pst.setTimestamp(++i, null);
      }
      pst.setInt(++i, id);
      pst.execute();
      pst.close();
      // Update the issue question state
      i = 0;
      pst = db.prepareStatement(
          "UPDATE project_issues " +
              "SET question = ? " +
              "WHERE issue_id = ? ");
      pst.setBoolean(++i, isQuestion);
      pst.setInt(++i, issueId);
      pst.executeUpdate();
      pst.close();
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
  }
}


