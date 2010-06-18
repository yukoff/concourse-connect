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
package com.concursive.connect.web.modules.calendar.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.calendar.utils.DimDimUtils;
import com.concursive.connect.web.modules.common.social.rating.dao.Rating;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.*;
import java.util.ArrayList;

/**
 * Represents a Meeting page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 7, 2006
 */
public class Meeting extends GenericBean {

  public static final String TABLE = "project_calendar_meeting";
  public static final String PRIMARY_KEY = "meeting_id";
  private int id = -1;
  private int projectId = -1;
  private Timestamp entered = null;
  private int enteredBy = -1;
  private Timestamp modified = null;
  private int modifiedBy = -1;
  private int owner = -1;
  private String title = null;
  private String location = null;
  private Timestamp startDate = null;
  private Timestamp endDate = null;
  private boolean isTentative = false;
  private int durationDays = 0;
  private int durationHours = 0;
  private int durationMinutes = 0;
  private boolean byInvitationOnly = true;
  private String description = null;
  private boolean isDimdim = false;
  private String meetingInvitees = "";
  private String dimdimMeetingId = null;
  private String dimdimUrl = null;
  private String dimdimUsername = null;
  private String dimdimPassword = null;
  private String dimdimMeetingKey = null;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAvg = 0.0;
  private int inappropriateCount = 0;
  private boolean isWebcast = false;

  // Helper objects
  private MeetingAttendeeList meetingAttendeeList = null;

  public Meeting() {
  }

  public Meeting(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public Meeting(Connection db, int id, int projectId) throws SQLException {
    this.projectId = projectId;
    queryRecord(db, id);
  }

  public Meeting(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public static Meeting createMeetingFromProject(Project project) {
    Meeting meeting = new Meeting();
    meeting.setTitle(project.getTitle());
    meeting.setStartDate(project.getRequestDate());
    meeting.setEndDate(project.getEstimatedCloseDate());
    meeting.setEnteredBy(project.getEnteredBy());
    meeting.setModifiedBy(project.getModifiedBy());
    meeting.setProjectId(project.getId());
    meeting.setOwner(project.getEnteredBy());
    meeting.setLocation(project.getAddressToAndLocation());
    return meeting;
  }

  public void queryRecord(Connection db, int meetingId) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append(
        "SELECT m.* " +
        "FROM project_calendar_meeting m " +
        "WHERE meeting_id = ? ");
    if (projectId > -1) {
      sql.append("AND project_id = ? ");
    }
    PreparedStatement pst = db.prepareStatement(sql.toString());
    int i = 0;
    pst.setInt(++i, meetingId);
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
      throw new SQLException("Meeting record not found.");
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setId(String tmp) {
    this.id = Integer.parseInt(tmp);
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }

  public int getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp startDate) {
    this.startDate = startDate;
  }

  /**
   * Sets the startDate attribute of the Project object
   *
   * @param startDate The new startDate value
   */
  public void setStartDate(String startDate) {
    this.startDate = DatabaseUtils.parseTimestamp(startDate);
  }

  public Timestamp getEndDate() {
    return endDate;
  }

  public void setEndDate(Timestamp endDate) {
    this.endDate = endDate;
  }

  /**
   * sets endDate attribute of the ProjectObject
   *
   * @param endDate new endDate value
   */
  public void setEndDate(String endDate) {
    this.endDate = DatabaseUtils.parseTimestamp(endDate);
  }

  public boolean getIsTentative() {
    return isTentative;
  }

  public void setIsTentative(boolean isTentative) {
    this.isTentative = isTentative;
  }

  /**
   * Sets the isTentative attribute of the Project object
   *
   * @param isTentative The new isTentative value
   */
  public void setIsTentative(String isTentative) {
    this.isTentative = DatabaseUtils.parseBoolean(isTentative);
  }

  /**
   * @return if true meeting is a Dimdim meeting
   */
  public boolean getIsDimdim() {
    return isDimdim;
  }

  /**
   * @param isDimdim Set to true if its a Dimdim meeting
   */
  public void setIsDimdim(boolean isDimdim) {
    this.isDimdim = isDimdim;
  }

  /**
   * @param isDimdim Set to true if its a Dimdim meeting
   */
  public void setIsDimdim(String isDimdim) {
    this.isDimdim = DatabaseUtils.parseBoolean(isDimdim);
  }

  /**
   * @param meetingInvitees Dimdim meeting invitees
   */
  public void setMeetingInvitees(String meetingInvitees) {
    this.meetingInvitees = meetingInvitees;
  }

  public String getMeetingInvitees() {
    return meetingInvitees;
  }

  /**
   * @return URL of dimdim server
   */
  public String getDimdimUrl() {
    return dimdimUrl;
  }

  /**
   * @param dimdimUrl URL of dimdim server
   */
  public void setDimdimUrl(String dimdimUrl) {
    //add a slash if the URL does not end with it
    if (StringUtils.hasText(dimdimUrl) && !dimdimUrl.endsWith("/")) {
      dimdimUrl += "/";
    }
    this.dimdimUrl = dimdimUrl;
  }

  /**
   * @return Dimdim web meeting id
   */
  public String getDimdimMeetingId() {
    return dimdimMeetingId;
  }

  /**
   * @param dimdimMeetingId Dimdim web meeting id
   */
  public void setDimdimMeetingId(String dimdimMeetingId) {
    this.dimdimMeetingId = dimdimMeetingId;
  }

  /**
   * @return Username to dimdim server
   */
  public String getDimdimUsername() {
    return dimdimUsername;
  }

  /**
   * @param dimdimUsername username to dimdim server
   */
  public void setDimdimUsername(String dimdimUsername) {
    this.dimdimUsername = dimdimUsername;
  }

  /**
   * @return password to dimdim server
   */
  public String getDimdimPassword() {
    return dimdimPassword;
  }

  /**
   * @param dimdimPassword password to dimdim server
   */
  public void setDimdimPassword(String dimdimPassword) {
    this.dimdimPassword = dimdimPassword;
  }

  /** 
   * @return - Participant meeting key for private Dimdim meeting.
   */
  public String getDimdimMeetingKey() {
    if (!StringUtils.hasText(dimdimMeetingKey) && isDimdim && byInvitationOnly) {
      dimdimMeetingKey = StringUtils.randomString(5, 6);
    }
    return dimdimMeetingKey;
  }

  /**
   * 
   * @param dimdimMeetingKey
   */
  public void setDimdimMeetingKey(String dimdimMeetingKey) {
    this.dimdimMeetingKey = dimdimMeetingKey;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String tmp) {
    this.entered = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public Timestamp getModified() {
    return modified;
  }

  public void setModified(Timestamp modified) {
    this.modified = modified;
  }

  public void setModified(String tmp) {
    this.modified = DatabaseUtils.parseTimestamp(tmp);
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
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

  public MeetingAttendeeList getMeetingAttendeeList() {
    return meetingAttendeeList;
  }

  public void setMeetingAttendeeList(MeetingAttendeeList meetingAttendeeList) {
    this.meetingAttendeeList = meetingAttendeeList;
  }

  public boolean getIsWebcast() {
    return isWebcast;
  }

  public void setIsWebcast(boolean isWebcast) {
    this.isWebcast = isWebcast;
  }

  public void setIsWebcast(String tmp) {
    this.isWebcast = DatabaseUtils.parseBoolean(tmp);
  }

  public boolean hasDuration() {
    return (durationDays > 0 || durationHours > 0 || durationMinutes > 0);
  }

  public String getDuration() {
    StringBuffer duration = new StringBuffer();
    if (durationDays > 0) {
      duration.append(durationDays).append(" day");
      if (durationDays > 1) {
        duration.append("s");
      }
    }
    if (durationHours > 0) {
      if (duration.length() > 0) {
        duration.append(" ");
      }
      duration.append(durationHours).append(" hour");
      if (durationHours > 1) {
        duration.append("s");
      }
    }
    if (durationMinutes > 0) {
      if (duration.length() > 0) {
        duration.append(" ");
      }
      duration.append(durationMinutes).append(" minute");
      if (durationMinutes > 1) {
        duration.append("s");
      }
    }
    return duration.toString();
  }

  public boolean getByInvitationOnly() {
    return byInvitationOnly;
  }

  public void setByInvitationOnly(boolean byInvitationOnly) {
    this.byInvitationOnly = byInvitationOnly;
  }

  public void setByInvitationOnly(String byInvitationOnly) {
    this.byInvitationOnly = DatabaseUtils.parseBoolean(byInvitationOnly);
  }

  public boolean getAllowUsersToJoin() {
    return !byInvitationOnly;
  }

  public void setAllowUsersToJoin(boolean allowUsersToJoin) {
    this.byInvitationOnly = !allowUsersToJoin;
  }

  public void setAllowUsersToJoin(String allowUsersToJoin) {
    this.byInvitationOnly = !DatabaseUtils.parseBoolean(allowUsersToJoin);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * The following fields depend on a timezone preference
   *
   * @return The timeZoneParams value
   */
  public static ArrayList<String> getTimeZoneParams() {
    ArrayList<String> thisList = new ArrayList<String>();
    thisList.add("startDate");
    thisList.add("endDate");
    return thisList;
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("meeting_id");
    projectId = rs.getInt("project_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    owner = rs.getInt("owner");
    title = rs.getString("title");
    location = rs.getString("location");
    startDate = rs.getTimestamp("start_date");
    endDate = rs.getTimestamp("end_date");
    isTentative = rs.getBoolean("is_tentative");
    byInvitationOnly = rs.getBoolean("by_invitation_only");
    description = rs.getString("description");
    if (startDate != null) {
      if (endDate != null) {
        float durationCheck = ((endDate.getTime() - startDate.getTime()) / 60000);
        int totalMinutes = java.lang.Math.round(durationCheck);
        durationDays = java.lang.Math.round(totalMinutes / 60 / 24);
        durationHours = java.lang.Math.round((totalMinutes - (60 * 24 * durationDays)) / 60);
        durationMinutes = java.lang.Math.round(totalMinutes - (60 * durationHours));
      }
    }
    ratingCount = DatabaseUtils.getInt(rs, "rating_count", 0);
    ratingValue = DatabaseUtils.getInt(rs, "rating_value", 0);
    ratingAvg = DatabaseUtils.getDouble(rs, "rating_avg", 0.0);
    inappropriateCount = DatabaseUtils.getInt(rs, "inappropriate_count", 0);
    isDimdim = rs.getBoolean("is_dimdim");
    dimdimUrl = rs.getString("dimdim_url");
    dimdimMeetingId = rs.getString("dimdim_meetingid");
    dimdimUsername = rs.getString("dimdim_username");
    dimdimPassword = DimDimUtils.decryptData(rs.getString("dimdim_password"));
    dimdimMeetingKey = rs.getString("dimdim_meeting_key");
    isWebcast = rs.getBoolean("is_webcast");
  }

  public boolean isValid() {
    if (projectId == -1) {
      errors.put("actionError", "Project ID not specified");
    }
    if (!StringUtils.hasText(title)) {
      errors.put("titleError", "Required field");
    }
    if (startDate == null) {
      errors.put("startDateError", "Required field");
    }
    if (endDate == null) {
      errors.put("endDateError", "Required field");
    }
    if (owner == -1 && enteredBy > -1) {
      owner = enteredBy;
    }

    /*
    if (owner == -1 || enteredBy == -1 || modifiedBy == -1) {
    errors.put("contentError", "Required field");
    } */
    return !hasErrors();
  }

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
          "INSERT INTO project_calendar_meeting " +
          "(project_id, title, location, start_date, end_date, is_tentative, ");

      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      if (description != null) {
        sql.append("description, ");
      }
      if (dimdimMeetingId != null) {
        sql.append("dimdim_meetingid, ");
      }
      if (dimdimUrl != null) {
        sql.append("dimdim_url, ");
      }
      if (dimdimUsername != null) {
        sql.append("dimdim_username, ");
      }
      if (dimdimPassword != null) {
        sql.append("dimdim_password, ");
      }
      if (getDimdimMeetingKey() != null) {
        sql.append("dimdim_meeting_key, ");
      }
      sql.append("owner,enteredby, modifiedby, by_invitation_only, is_dimdim, is_webcast)");
      sql.append("VALUES (?, ?, ?, ?, ?, ?, ");

      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      if (description != null) {
        sql.append("?, ");
      }
      if (dimdimMeetingId != null) {
        sql.append("?, ");
      }
      if (dimdimUrl != null) {
        sql.append("?, ");
      }
      if (dimdimUsername != null) {
        sql.append("?, ");
      }
      if (dimdimPassword != null) {
        sql.append("?, ");
      }
      if (dimdimMeetingKey != null) {
        sql.append("?, ");
      }
      sql.append("?,?,?,?,?,?) ");
      int i = 0;
      //Insert the Meeting
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setInt(++i, projectId);
      pst.setString(++i, title);
      pst.setString(++i, location);
      pst.setTimestamp(++i, startDate);
      pst.setTimestamp(++i, endDate);
      pst.setBoolean(++i, isTentative);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      if (description != null) {
        pst.setString(++i, description);
      }
      if (dimdimMeetingId != null) {
        pst.setString(++i, dimdimMeetingId);
      }
      if (dimdimUrl != null) {
        pst.setString(++i, dimdimUrl);
      }
      if (dimdimUsername != null) {
        pst.setString(++i, dimdimUsername);
      }
      if (dimdimPassword != null) {
        pst.setString(++i, DimDimUtils.encryptData(dimdimPassword));
      }
      if (dimdimMeetingKey != null) {
        pst.setString(++i, dimdimMeetingKey);
      }
      pst.setInt(++i, owner);
      pst.setInt(++i, enteredBy);
      pst.setInt(++i, modifiedBy);
      pst.setBoolean(++i, byInvitationOnly);
      pst.setBoolean(++i, isDimdim);
      pst.setBoolean(++i, isWebcast);
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "project_calendar_meeting_meeting_id_seq", -1);
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

  public int update(Connection db) throws SQLException {
    int resultCount = 0;
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      if (this.getId() == -1) {
        throw new SQLException("ID was not specified");
      }
      if (!isValid()) {
        return -1;
      }
      int i = 0;
      String sql = "UPDATE project_calendar_meeting " +
          "SET title = ?, " +
          "location = ?, end_date = ?, start_date = ?, is_tentative = ?, " +
          "modifiedby = ?, modified = CURRENT_TIMESTAMP, by_invitation_only = ?, " +
          "is_dimdim = ?, is_webcast = ?, dimdim_meetingid = ? ";
      if (description != null) {
        sql += ", description = ? ";
      }
      if (dimdimUrl != null) {
        sql += ", dimdim_url = ? ";
      }
      if (dimdimUsername != null) {
        sql += ", dimdim_username = ? ";
      }
      if (dimdimPassword != null) {
        sql += ", dimdim_password = ? ";
      }
      if (getDimdimMeetingKey() != null) {
        sql += ", dimdim_meeting_key = ? ";
      }
      sql += "WHERE meeting_id = ? AND modified = ?";

      PreparedStatement pst = db.prepareStatement(sql);
      pst.setString(++i, title);
      pst.setString(++i, location);
      pst.setTimestamp(++i, endDate);
      pst.setTimestamp(++i, startDate);
      pst.setBoolean(++i, isTentative);
      pst.setInt(++i, this.getModifiedBy());
      pst.setBoolean(++i, byInvitationOnly);
      pst.setBoolean(++i, isDimdim);
      pst.setBoolean(++i, isWebcast);
      pst.setString(++i, dimdimMeetingId);
      if (description != null) {
        pst.setString(++i, description);
      }
      if (dimdimUrl != null) {
        pst.setString(++i, dimdimUrl);
      }
      if (dimdimUsername != null) {
        pst.setString(++i, dimdimUsername);
      }
      if (dimdimPassword != null) {
        pst.setString(++i, DimDimUtils.encryptData(dimdimPassword));
      }
      if (dimdimMeetingKey != null) {
        pst.setString(++i, dimdimMeetingKey);
      }
      pst.setInt(++i, id);
      pst.setTimestamp(++i, modified);
      resultCount = pst.executeUpdate();
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
    return resultCount;
  }

  public void delete(Connection db) throws SQLException {
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      //Delete rating
      Rating.delete(db, id, TABLE, PRIMARY_KEY);

      // Delete the Meeting Attendees
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM project_calendar_meeting_attendees " +
          "WHERE meeting_id = ? ");
      pst.setInt(1, id);
      pst.execute();
      pst.close();

      // Delete the Meeting
      pst = db.prepareStatement(
          "DELETE FROM project_calendar_meeting " +
          "WHERE meeting_id = ? ");
      int i = 0;
      pst.setInt(++i, id);
      pst.execute();
      pst.close();
      if (autoCommit) {
        db.commit();
      }
    } catch (Exception e) {
      if (autoCommit) {
        db.rollback();
      }
      throw new SQLException(e.getMessage());
    } finally {
      if (autoCommit) {
        db.setAutoCommit(true);
      }
    }
  }

  public void buildAttendeeList(Connection db) throws SQLException {
    meetingAttendeeList = new MeetingAttendeeList();
    meetingAttendeeList.setMeetingId(id);
    meetingAttendeeList.buildList(db);
  }
}
