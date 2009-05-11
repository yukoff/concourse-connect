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

package com.concursive.connect.web.modules.communications.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;

import java.sql.*;

/**
 * Represents a Contact/Team Member who can be sent a message..
 *
 * @author Ananth
 * @version ProjectMessageRecipient.java Jul 25, 2008 4:05:24 PM Ananth $
 * @created Jul 25, 2008
 */
public class ProjectMessageRecipient extends GenericBean {
  public static final int STATUS_INVITING = 1;
  public static final int STATUS_MAILERROR = 2;
  public static final int STATUS_PENDING = 3;

  public static final String INVITING = "Inviting";
  public static final String MAILERROR = "Mail Error";
  public static final String PENDING = "Pending";

  private int id = -1;
  private int messageId = -1;
  private int contactId = -1;
  private int statusId = 0;
  private String status = "";
  private int enteredBy = -1;
  private Timestamp entered = null;

  private String firstName = "";
  private String lastName = "";

  /**
   * Gets the 'firstName' attribute of the ProjectMessageRecipient object
   *
   * @return The 'firstName' value
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the 'firstName' attribute of the ProjectMessageRecipient
   *
   * @param firstName The new 'firstName' value
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the 'lastName' attribute of the ProjectMessageRecipient object
   *
   * @return The 'lastName' value
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the 'lastName' attribute of the ProjectMessageRecipient
   *
   * @param lastName The new 'lastName' value
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the 'contactId' attribute of the ProjectMessageRecipient object
   *
   * @return The 'contactId' value
   */
  public int getContactId() {
    return contactId;
  }

  /**
   * Sets the 'contactId' attribute of the ProjectMessageRecipient
   *
   * @param contactId The new 'contactId' value
   */
  public void setContactId(int contactId) {
    this.contactId = contactId;
  }

  public void setContactId(String contactId) {
    this.contactId = Integer.parseInt(contactId);
  }

  /**
   * Gets the 'entered' attribute of the ProjectMessageRecipient object
   *
   * @return The 'entered' value
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * Sets the 'entered' attribute of the ProjectMessageRecipient
   *
   * @param entered The new 'entered' value
   */
  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }

  /**
   * Gets the 'enteredBy' attribute of the ProjectMessageRecipient object
   *
   * @return The 'enteredBy' value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  /**
   * Sets the 'enteredBy' attribute of the ProjectMessageRecipient
   *
   * @param enteredBy The new 'enteredBy' value
   */
  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }

  /**
   * Gets the 'id' attribute of the ProjectMessageRecipient object
   *
   * @return The 'id' value
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the 'id' attribute of the ProjectMessageRecipient
   *
   * @param id The new 'id' value
   */
  public void setId(int id) {
    this.id = id;
  }

  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }

  /**
   * Gets the 'messageId' attribute of the ProjectMessageRecipient object
   *
   * @return The 'messageId' value
   */
  public int getMessageId() {
    return messageId;
  }

  /**
   * Sets the 'messageId' attribute of the ProjectMessageRecipient
   *
   * @param messageId The new 'messageId' value
   */
  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = Integer.parseInt(messageId);
  }

  /**
   * Gets the 'status' attribute of the ProjectMessageRecipient object
   *
   * @return The 'status' value
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the 'status' attribute of the ProjectMessageRecipient
   *
   * @param status The new 'status' value
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the 'statusId' attribute of the ProjectMessageRecipient object
   *
   * @return The 'statusId' value
   */
  public int getStatusId() {
    return statusId;
  }

  /**
   * Sets the 'statusId' attribute of the ProjectMessageRecipient
   *
   * @param statusId The new 'statusId' value
   */
  public void setStatusId(int statusId) {
    this.statusId = statusId;
  }

  public void setStatusId(String statusId) {
    this.statusId = Integer.parseInt(statusId);
  }

  public ProjectMessageRecipient() {
  }

  public ProjectMessageRecipient(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public ProjectMessageRecipient(Connection db, String id) throws SQLException {
    queryRecord(db, Integer.parseInt(id));
  }

  public ProjectMessageRecipient(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Id not specified..");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT pmr.*, " +
            "c.first_name, c.last_name " +
            "FROM project_msg_recipients pmr " +
            "LEFT JOIN contacts c ON (pmr.contact_id = c.contact_id) " +
            "WHERE pmr.recipient_id = ?");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();

    if (this.id == -1) {
      throw new SQLException("Record not found");
    }
  }

  public void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("recipient_id");
    messageId = rs.getInt("message_id");
    contactId = rs.getInt("contact_id");
    statusId = DatabaseUtils.getInt(rs, "status_id", 0);
    status = rs.getString("status");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");

    //contact fields
    firstName = rs.getString("first_name");
    lastName = rs.getString("last_name");
  }

  public boolean insert(Connection db) throws SQLException {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO project_msg_recipients (message_id, contact_id, ");
    if (id > -1) {
      sql.append("recipient_id, ");
    }
    sql.append("status_id, status, entered, enteredby) VALUES (?, ?, ");
    if (id > -1) {
      sql.append("?, ");
    }
    sql.append("?, ?, ?, ?) ");

    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql.toString());
    pst.setInt(++i, messageId);
    pst.setInt(++i, contactId);
    if (id > -1) {
      pst.setInt(++i, id);
    }
    pst.setInt(++i, statusId);
    pst.setString(++i, status);
    pst.setTimestamp(++i, entered);
    pst.setInt(++i, enteredBy);
    pst.execute();
    pst.close();

    id = DatabaseUtils.getCurrVal(db, "project_msg_recipients_recipient_id_seq", id);

    return true;
  }

  public void update(Connection db) throws SQLException {
    //TODO: Implement
  }

  public void delete(Connection db) throws SQLException {
    //TODO: Implement
  }
}