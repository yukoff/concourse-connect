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

package com.concursive.connect.web.modules.issues.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.contacts.dao.Contact;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;

import java.sql.*;

/**
 * Description of Class
 *
 * @author matt rajkowski
 * @version $Id:TicketContact.java 2246 2007-03-22 05:57:41Z matt $
 * @created Mar 12, 2007
 */
public class TicketContact extends GenericBean {

  private int id = -1;
  private int ticketId = -1;
  private int userId = -1;
  private int contactId = -1;
  private String contactName = null;
  private String contactEmail = null;
  private Timestamp entered = null;
  private int enteredBy = -1;

  public TicketContact() {
  }

  public TicketContact(Connection db, int ticketContactId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT tc.* " +
            "FROM ticket_contacts tc " +
            "WHERE id = ? ");
    pst.setInt(1, ticketContactId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Record not found");
    }
  }

  public TicketContact(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTicketId() {
    return ticketId;
  }

  public void setTicketId(int ticketId) {
    this.ticketId = ticketId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getContactId() {
    return contactId;
  }

  public void setContactId(int contactId) {
    this.contactId = contactId;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public Timestamp getEntered() {
    return entered;
  }

  public void setEntered(Timestamp entered) {
    this.entered = entered;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("id");
    ticketId = rs.getInt("ticketid");
    userId = DatabaseUtils.getInt(rs, "user_id");
    contactId = DatabaseUtils.getInt(rs, "contact_id");
    contactName = rs.getString("contact_name");
    contactEmail = rs.getString("contact_email");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
  }

  public boolean insert(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "INSERT INTO ticket_contacts (ticketid, user_id, contact_id, contact_name, contact_email," +
            (entered != null ? "entered, " : "") +
            "enteredby) " +
            "VALUES (?, ?, ?, ?, ?, " + (entered != null ? "?, " : "") + "?)");
    int i = 0;
    pst.setInt(++i, ticketId);
    DatabaseUtils.setInt(pst, ++i, userId);
    DatabaseUtils.setInt(pst, ++i, contactId);
    pst.setString(++i, contactName);
    pst.setString(++i, contactEmail);
    if (entered != null) {
      pst.setTimestamp(++i, entered);
    }
    pst.setInt(++i, enteredBy);
    pst.execute();
    pst.close();
    id = DatabaseUtils.getCurrVal(db, "ticket_contacts_id_seq", -1);
    return true;
  }

  public void delete(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM ticket_contacts WHERE id = ? ");
    pst.setInt(1, id);
    pst.execute();
    pst.close();
  }

  public void buildName(Connection db) throws SQLException {
    if (userId > -1) {
      User user = UserUtils.loadUser(userId);
      contactName = user.getNameFirstLastInitial();
    } else if (contactId > -1) {
      Contact contact = new Contact(db, contactId);
      contactName = contact.getIndexAs();
    } else {
      if (contactEmail != null && contactEmail.indexOf("@") > 0) {
        // Protect the email address from view
        contactName = contactEmail.substring(0, contactEmail.indexOf("@") + 1) + "...";
      } else {
        contactName = contactEmail;
      }
    }
  }

  public void buildEmailAddress(Connection db) throws SQLException {
    if (userId > -1) {
      User user = UserUtils.loadUser(userId);
      contactEmail = user.getEmail();
    } else if (contactId > -1) {
      Contact contact = new Contact(db, contactId);
      contactEmail = contact.getEmailAddress();
    }
  }

  public String getValue() {
    if (userId > -1) {
      return String.valueOf(userId);
    } else if (contactId > -1) {
      return "C" + String.valueOf(contactId);
    } else {
      return contactEmail;
    }
  }
}
