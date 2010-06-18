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

package com.concursive.connect.web.modules.reviews.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.*;

/**
 * Represents a project review
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created June 26, 2008
 */
public class ProjectRating extends GenericBean {

  public static final String TABLE = "projects_rating";
  public static final String PRIMARY_KEY = "rating_id";

  // The user's rating and review
  private int id = -1;
  private int projectId = -1;
  private int rating = -1;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private String title = null;
  private String comment = null;
  // A summary of how other user's found this user's rating useful
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAverage = 0.0;
  private int inappropriateCount = 0;
  private int modifiedBy = -1;

  // Helper objects
  private Project project = null;


  /**
   * Constructor for the ProjectRating
   */
  public ProjectRating() {
  }


  /**
   * Constructor for the ProjectRating object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public ProjectRating(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * Constructor for the ProjectRating object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public ProjectRating(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int thisId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT * " +
            "FROM projects_rating pr " +
            "WHERE pr.rating_id = ? ");

    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, thisId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
  }


  /**
   * Sets the Id attribute of the ProjectRating object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the id attribute of the ProjectRating object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }


  /**
   * Sets the enteredBy attribute of the Project object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the entered attribute of the Project object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the entered attribute of the Project object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    entered = tmp;
  }


  /**
   * Sets the enteredBy attribute of the Project object
   *
   * @param tmp The new enteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the modified attribute of the Project object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the modified attribute of the Project object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    modified = tmp;
  }


  /**
   * Gets the Id attribute of the Project object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the entered attribute of the Project object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredBy attribute of the Project object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the modified attribute of the Project object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
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
   * @return the modifiedBy
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * @param modifiedBy the modifiedBy to set
   */
  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * @param modifiedBy the modifiedBy to set
   */
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = Integer.parseInt(modifiedBy);
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


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }


  /**
   * @return the rating
   */
  public int getRating() {
    return rating;
  }


  /**
   * @param rating the rating to set
   */
  public void setRating(int rating) {
    this.rating = rating;
  }


  /**
   * @param rating the rating to set
   */
  public void setRating(String rating) {
    this.rating = Integer.parseInt(rating);
  }


  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }


  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }


  /**
   * @return the comment
   */
  public String getComment() {
    return comment;
  }


  /**
   * @param comment the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
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
   * @return the project
   */
  public Project getProject() {
    return project;
  }


  /**
   * @param project the project to set
   */
  public void setProject(Project project) {
    this.project = project;
  }

  public User getUser() {
    if (enteredBy > -1) {
      return UserUtils.loadUser(enteredBy);
    }
    return null;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public static synchronized boolean save(Connection db, ProjectRating projectRating) throws SQLException {
    if (!isValid(projectRating)) {
      return false;
    }

    Exception errorMessage = null;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      int existingVote = Rating.queryUserRating(db, projectRating.getEnteredBy(), projectRating.getProjectId(), "projects", "project_id");
      int newVote = projectRating.getRating();
      // Determine if an update or insert is required
      if (existingVote == -1) {
        insertRating(db, projectRating);
      } else {
        updateRating(db, projectRating);
      }

      if (existingVote != newVote) {
        // Update the project rating count and value
        PreparedStatement pst = db.prepareStatement(
            "UPDATE projects " +
                " SET rating_count = rating_count + ?, rating_value = rating_value + ?, " +
                " rating_avg = ((rating_value + ?) / (rating_count + ?)) " +
                " WHERE project_id = ? ");
        int i = 0;
        if (existingVote == -1) {
          pst.setInt(++i, 1);
          pst.setInt(++i, newVote);
          pst.setInt(++i, newVote);
          pst.setInt(++i, 1);
        } else {
          pst.setInt(++i, 0);
          pst.setInt(++i, newVote - existingVote);
          pst.setInt(++i, newVote - existingVote);
          pst.setInt(++i, 0);
        }
        pst.setInt(++i, projectRating.getProjectId());
        pst.execute();
        pst.close();
      }
      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectRating.getProjectId());
    } catch (Exception e) {
      errorMessage = e;
      if (commit) {
        db.rollback();
      }
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (errorMessage != null) {
      throw new SQLException(errorMessage.getMessage());
    }
    return true;
  }


  /**
   * @param db
   * @throws SQLException
   */
  private static void insertRating(Connection db, ProjectRating projectRating) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "INSERT INTO projects_rating " +
            "(project_id, rating, title, comment, enteredby, modifiedby ");
    if (projectRating.getEntered() != null) {
      sql.append(", entered ");
    }
    if (projectRating.getModified() != null) {
      sql.append(",modified ");
    }
    sql.append(") VALUES (");
    sql.append("?, ?, ?, ?, ?, ?");
    if (projectRating.getEntered() != null) {
      sql.append(",? ");
    }
    if (projectRating.getModified() != null) {
      sql.append(",? ");
    }
    sql.append(")");
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    DatabaseUtils.setInt(pst, ++i, projectRating.getProjectId());
    DatabaseUtils.setInt(pst, ++i, projectRating.getRating());
    pst.setString(++i, projectRating.getTitle());
    pst.setString(++i, projectRating.getComment());
    pst.setInt(++i, projectRating.getEnteredBy());
    if (projectRating.getModifiedBy() > -1) {
      pst.setInt(++i, projectRating.getModifiedBy());
    } else {
      pst.setInt(++i, projectRating.getEnteredBy());
    }
    if (projectRating.getEntered() != null) {
      pst.setTimestamp(++i, projectRating.getEntered());
    }
    if (projectRating.getModified() != null) {
      pst.setTimestamp(++i, projectRating.getModified());
    }
    pst.execute();
    pst.close();
    projectRating.setId(DatabaseUtils.getCurrVal(db, "projects_rating_rating_id_seq", projectRating.getId()));
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  private static int updateRating(Connection db, ProjectRating projectRating) throws SQLException {
    if (projectRating.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    if (!isValid(projectRating)) {
      return -1;
    }
    int resultCount = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE projects_rating " +
            " SET rating = ?, title = ?, comment = ?,  " +
            " modified = CURRENT_TIMESTAMP , " +
            " modifiedby = ? " +
            " WHERE rating_id = ? ");
    int i = 0;
    DatabaseUtils.setInt(pst, ++i, projectRating.getRating());
    pst.setString(++i, projectRating.getTitle());
    pst.setString(++i, projectRating.getComment());
    pst.setInt(++i, projectRating.getModifiedBy());
    pst.setInt(++i, projectRating.getId());
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
  public static synchronized boolean delete(Connection db, ProjectRating projectRating) throws SQLException {
    if (projectRating.getId() == -1) {
      throw new SQLException("ID was not specified");
    }
    int deleteCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      Rating.delete(db, projectRating.getId(), TABLE, PRIMARY_KEY);

      // Get the project's rating
      int ratingCount = Rating.queryObjectRatingCount(db, projectRating.getProjectId(), Project.TABLE, Project.PRIMARY_KEY);

      // Get the user's rating
      int thisRating = Rating.queryUserRating(db, projectRating.getEnteredBy(), projectRating.getProjectId(), Project.TABLE, Project.PRIMARY_KEY);

      //Delete the actual project rating
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM projects_rating " +
              "WHERE rating_id = ? ");
      pst.setInt(1, projectRating.getId());
      deleteCount = pst.executeUpdate();
      pst.close();

      if (deleteCount > 0) {
        // Update the parent table's rating information
        // NOTE: make sure not to divide by 0
        pst = db.prepareStatement(
            "UPDATE projects " +
                "SET rating_count = rating_count - ?, rating_value = rating_value - ?, " +
                (ratingCount > 1 ?
                    "rating_avg = ((rating_value - ?) / (rating_count - ?)) " : "rating_avg = 0 ") +
                "WHERE project_id = ? ");
        int i = 0;
        pst.setInt(++i, 1);
        pst.setInt(++i, thisRating);
        if (ratingCount > 1) {
          pst.setInt(++i, thisRating);
          pst.setInt(++i, 1);
        }
        pst.setInt(++i, projectRating.getProjectId());
        pst.execute();
        pst.close();
      }

      if (commit) {
        db.commit();
      }
      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectRating.getProjectId());
    } catch (Exception e) {
      if (commit) {
        db.rollback();
      }
      e.printStackTrace(System.out);
      throw new SQLException(e.getMessage());
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    if (deleteCount == 0) {
      projectRating.getErrors().put("actionError", "Project Rating could not be deleted because it no longer exists.");
      return false;
    } else {
      return true;
    }
  }


  /**
   * Gets the valid attribute of the Project object
   *
   * @return The valid value
   */
  private static boolean isValid(ProjectRating projectRating) {
    if (projectRating.getRating() == -1) {
      projectRating.getErrors().put("ratingError", "Rating is required");
    }
    if (projectRating.getTitle().equals("")) {
      projectRating.getErrors().put("titleError", "Title is required");
    }
    if (projectRating.getComment().equals("")) {
      projectRating.getErrors().put("commentError", "Comment is required");
    }
    return !projectRating.hasErrors();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("rating_id");
    projectId = DatabaseUtils.getInt(rs, "project_id");
    rating = DatabaseUtils.getInt(rs, "rating");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    title = rs.getString("title");
    comment = rs.getString("comment");
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAverage = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
  }


  /**
   * @param db
   * @param ratingId
   */
  public static void incrementInappropriate(Connection db, int ratingId) throws SQLException {
    PreparedStatement pst = db.prepareStatement("UPDATE projects_rating set inappropriate_count = inappropriate_count + 1 where rating_id = ?");
    pst.setInt(1, ratingId);
    pst.executeUpdate();
    pst.close();
  }
}
