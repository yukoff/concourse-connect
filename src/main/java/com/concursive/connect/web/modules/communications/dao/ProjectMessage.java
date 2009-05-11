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
import com.concursive.connect.web.modules.contacts.dao.Contact;
import com.concursive.connect.web.modules.contacts.dao.ContactList;

import java.sql.*;
import java.util.Iterator;

/**
 * Represents a project message that can be sent to contacts or team members
 *
 * @author Ananth
 * @version ProjectMessage.java Jul 25, 2008 12:28:45 PM Ananth $
 * @created Jul 25, 2008
 */
public class ProjectMessage extends GenericBean {
  private int id = -1;
  private String subject = null;
  private String body = null;
  private int projectId = -1;
  private int enteredBy = -1;
  private Timestamp entered = null;

  private ProjectMessageRecipientList recipients = null;
  private ContactList contacts = new ContactList();
  private String contactFirstName = "";
  private String contactLastName = "";
  private String contactEmailAddress = "";

  /**
   * Gets the 'contactEmailAddress' attribute of the ProjectMessage object
   *
   * @return The 'contactEmailAddress' value
   */
  public String getContactEmailAddress() {
    return contactEmailAddress;
  }

  /**
   * Gets the 'contactFirstName' attribute of the ProjectMessage object
   *
   * @return The 'contactFirstName' value
   */
  public String getContactFirstName() {
    return contactFirstName;
  }

  /**
   * Sets the 'contactFirstName' attribute of the ProjectMessage
   *
   * @param contactFirstName The new 'contactFirstName' value
   */
  public void setContactFirstName(String contactFirstName) {
    this.contactFirstName = contactFirstName;
  }

  /**
   * Gets the 'contactLastName' attribute of the ProjectMessage object
   *
   * @return The 'contactLastName' value
   */
  public String getContactLastName() {
    return contactLastName;
  }

  /**
   * Sets the 'contactLastName' attribute of the ProjectMessage
   *
   * @param contactLastName The new 'contactLastName' value
   */
  public void setContactLastName(String contactLastName) {
    this.contactLastName = contactLastName;
  }

  /**
   * Gets the 'contacts' attribute of the ProjectMessage object
   *
   * @return The 'contacts' value
   */
  public ContactList getContacts() {
    return contacts;
  }

  /**
   * Sets the 'contacts' attribute of the ProjectMessage
   *
   * @param contacts The new 'contacts' value
   */
  public void setContacts(ContactList contacts) {
    this.contacts = contacts;
  }

  /**
   * Gets the 'recipients' attribute of the ProjectMessage object
   *
   * @return The 'recipients' value
   */
  public ProjectMessageRecipientList getRecipients() {
    return recipients;
  }

  /**
   * Sets the 'recipients' attribute of the ProjectMessage
   *
   * @param recipients The new 'recipients' value
   */
  public void setRecipients(ProjectMessageRecipientList recipients) {
    this.recipients = recipients;
  }

  /**
   * Gets the 'body' attribute of the ProjectMessage object
   *
   * @return The 'body' value
   */
  public String getBody() {
    return body;
  }

  /**
   * Sets the 'body' attribute of the ProjectMessage
   *
   * @param body The new 'body' value
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * Gets the 'entered' attribute of the ProjectMessage object
   *
   * @return The 'entered' value
   */
  public Timestamp getEntered() {
    return entered;
  }

  /**
   * Sets the 'entered' attribute of the ProjectMessage
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
   * Gets the 'enteredBy' attribute of the ProjectMessage object
   *
   * @return The 'enteredBy' value
   */
  public int getEnteredBy() {
    return enteredBy;
  }

  /**
   * Sets the 'enteredBy' attribute of the ProjectMessage
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
   * Gets the 'id' attribute of the ProjectMessage object
   *
   * @return The 'id' value
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the 'id' attribute of the ProjectMessage
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
   * Gets the 'projectId' attribute of the ProjectMessage object
   *
   * @return The 'projectId' value
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * Sets the 'projectId' attribute of the ProjectMessage
   *
   * @param projectId The new 'projectId' value
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = Integer.parseInt(projectId);
  }

  /**
   * Gets the 'subject' attribute of the ProjectMessage object
   *
   * @return The 'subject' value
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Sets the 'subject' attribute of the ProjectMessage
   *
   * @param subject The new 'subject' value
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  public ProjectMessage() {
  }

  public ProjectMessage(Connection db, int id) throws SQLException {
    queryRecord(db, id);
  }

  public ProjectMessage(Connection db, String id) throws SQLException {
    queryRecord(db, Integer.parseInt(id));
  }

  public ProjectMessage(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Id not specified..");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT * " +
            "FROM project_message " +
            "WHERE message_id = ?");
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

    buildRecipients(db);
  }


  public void buildRecipients(Connection db) throws SQLException {
    if (recipients == null) {
      recipients = new ProjectMessageRecipientList();
    }
    recipients.setMessageId(id);
    recipients.buildList(db);
  }

  public void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("message_id");
    subject = rs.getString("subject");
    body = rs.getString("body");
    projectId = rs.getInt("project_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
  }

  public boolean insert(Connection db) throws SQLException {
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }

      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO project_message (subject, body, project_id, ");
      if (id > -1) {
        sql.append("message_id, ");
      }
      sql.append("entered, enteredby) VALUES (?, ?, ?, ");
      if (id > -1) {
        sql.append("?, ");
      }
      sql.append("?, ?) ");

      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      pst.setString(++i, subject);
      pst.setString(++i, body);
      pst.setInt(++i, projectId);
      if (id > -1) {
        pst.setInt(++i, id);
      }
      pst.setTimestamp(++i, entered);
      pst.setInt(++i, enteredBy);
      pst.execute();
      pst.close();

      id = DatabaseUtils.getCurrVal(db, "project_message_message_id_seq", id);

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

  public void update(Connection db) throws SQLException {
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }

      /**
       method logic goes here 
       */

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
  }

  public void delete(Connection db) throws SQLException {
    boolean commit = false;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }

      /**
       method logic goes here 
       */

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
  }

  /**
   * Sets the 'contactEmailAddress' attribute of the ProjectMessage
   *
   * @param contactEmailAddress The new 'contactEmailAddress' value
   */
  public void setContactEmailAddress(String contactEmailAddress) {
    this.contactEmailAddress = contactEmailAddress;
    addContact();
  }

  public void addContact() {
    if (contactEmailAddress != null) {
      Contact contact = new Contact();
      contact.setFirstName(contactFirstName);
      contact.setLastName(contactLastName);
      contact.setEmail1(contactEmailAddress);
      contacts.add(contact);
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("MESSAGE: " + body + "\n");
    sb.append("PROJECT: " + projectId + "\n");

    int count = 0;
    Iterator i = contacts.iterator();
    while (i.hasNext()) {
      count++;
      Contact contact = (Contact) i.next();
      sb.append("EMAIL " + count + ": " + contact.getFirstName() + " " + contact.getLastName() + ", " + contact.getEmail1() + "\n");
    }

    return sb.toString();
  }
}