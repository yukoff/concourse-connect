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
import com.concursive.connect.web.modules.contacts.dao.ContactList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Description of Class
 *
 * @author matt rajkowski
 * @version $Id:TicketContactList.java 2246 2007-03-22 05:57:41Z matt $
 * @created Mar 12, 2007
 */
public class TicketContactList extends ArrayList {

  private PagedListInfo pagedListInfo = null;
  private int ticketId = -1;
  private int userId = -1;
  private int contactId = -1;


  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
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

  public boolean hasUserId(int userId) {
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketContact contact = (TicketContact) i.next();
      if (contact.getUserId() == userId) {
        return true;
      }
    }
    return false;
  }

  public void buildList(Connection db) throws SQLException {
    PreparedStatement pst = null;
    ResultSet rs;
    int items = -1;
    StringBuffer sqlSelect = new StringBuffer();
    StringBuffer sqlCount = new StringBuffer();
    StringBuffer sqlFilter = new StringBuffer();
    StringBuffer sqlOrder = new StringBuffer();
    //Need to build a base SQL statement for counting records
    sqlCount.append(
        "SELECT COUNT(*) AS recordcount " +
            "FROM ticket_contacts tc " +
            "WHERE tc.id > -1 ");
    createFilter(sqlFilter);
    if (pagedListInfo == null) {
      pagedListInfo = new PagedListInfo();
      pagedListInfo.setItemsPerPage(-1);
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
    //Determine column to sort by
    pagedListInfo.setDefaultSort("tc.contact_name", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "tc.* " +
            "FROM ticket_contacts tc " +
            "WHERE tc.id > -1 ");
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
      TicketContact thisRecord = new TicketContact(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
    // Get the details
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketContact contact = (TicketContact) i.next();
      contact.buildName(db);
    }
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (ticketId > -1) {
      sqlFilter.append("AND tc.ticketid = ? ");
    }
    if (userId > -1) {
      sqlFilter.append("AND tc.user_id = ? ");
    }
    if (contactId > -1) {
      sqlFilter.append("AND tc.contact_id = ? ");
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (ticketId > -1) {
      pst.setInt(++i, ticketId);
    }
    if (userId > -1) {
      pst.setInt(++i, userId);
    }
    if (contactId > -1) {
      pst.setInt(++i, contactId);
    }
    return i;
  }

  public static void updateContacts(Connection db, int userId, int ticketId, String insertMembers, String deleteMembers) throws SQLException {
    if (insertMembers != null && !insertMembers.equals("") && ticketId > -1) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("TicketContactList-> New: " + insertMembers);
      }
      StringTokenizer items = new StringTokenizer(insertMembers, "|");
      while (items.hasMoreTokens()) {
        String itemIdValue = items.nextToken();
        // Insert the item type...
        TicketContact ticketContact = new TicketContact();
        ticketContact.setTicketId(ticketId);
        ticketContact.setEnteredBy(userId);

        // Lookup the email in the user table, if found associate the user
        if (itemIdValue.indexOf("@") > 0) {
          int existingUserId = User.getIdByEmailAddress(db, itemIdValue);
          if (existingUserId > -1) {
            itemIdValue = String.valueOf(existingUserId);
          }
        }

        // Lookup the email in the contact table, if found associate the contact
        if (itemIdValue.indexOf("@") > 0) {
          int existingContactId = ContactList.getIdByEmailAddress(db, itemIdValue);
          if (existingContactId > -1) {
            itemIdValue = "C" + String.valueOf(existingContactId);
          }
        }

        // TODO: Lookup the contact in ConcourseSuite, append to contacts table
        // NOTE: make sure UI is prepared for any delay


        if (itemIdValue.indexOf("@") > 0) {
          // Contains "@" for using email
          ticketContact.setContactEmail(itemIdValue);
          ticketContact.insert(db);
        } else if (itemIdValue.startsWith("C")) {
          // Contains "C" + int for using contactId
          int newContactId = Integer.parseInt(itemIdValue.substring(1));
          // TODO: verify access to this contact id
          //if (hasContactAccess(db, userId, newContactId)) {
          ticketContact.setContactId(newContactId);
          ticketContact.insert(db);
          //}
        } else {
          // Contains int for using userId
          int newUserId = Integer.parseInt(itemIdValue);
          //if (UserList.hasUserAccess(db, userId, newUserId)) {
          ticketContact.setUserId(newUserId);
          // TODO: verify access to this userId
          ticketContact.insert(db);
          //}
        }
      }
    }
    //Removed deleted members
    if (deleteMembers != null && !deleteMembers.equals("") && ticketId > -1) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("TicketContactList-> Del: " + deleteMembers);
      }
      //Delete everyone but self
      StringTokenizer items = new StringTokenizer(deleteMembers, "|");
      while (items.hasMoreTokens()) {
        String itemId = items.nextToken();

        String query = "AND id = 0 ";
        if (itemId.indexOf("@") > 0) {
          // Contains "@" for using email
          query = "AND contact_email = ? ";
        } else if (itemId.startsWith("C")) {
          // Contains "C" + int for using contactId
          query = "AND contact_id = ? ";
        } else {
          // Contains int for using userId
          query = "AND user_id = ? ";
        }

        PreparedStatement pst = db.prepareStatement(
            "DELETE FROM ticket_contacts " +
                "WHERE ticketid = ? " + query);
        pst.setInt(1, ticketId);
        if (itemId.indexOf("@") > 0) {
          // Contains "@" for using email
          pst.setString(2, itemId);
        } else if (itemId.startsWith("C")) {
          // Contains "C" + int for using contactId
          pst.setInt(2, Integer.parseInt(itemId.substring(1)));
        } else {
          // Contains int for using userId
          pst.setInt(2, Integer.parseInt(itemId));
        }
        pst.execute();
        pst.close();
      }
    }
  }

  public static void deleteUserId(Connection db, int ticketId, int userId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM ticket_contacts WHERE ticketid = ? AND user_id = ?");
    pst.setInt(1, ticketId);
    pst.setInt(2, userId);
    pst.execute();
    pst.close();
  }

  public static void deleteContactId(Connection db, int ticketId, int userId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM ticket_contacts WHERE ticketid = ? AND contact_id = ?");
    pst.setInt(1, ticketId);
    pst.setInt(2, userId);
    pst.execute();
    pst.close();
  }

  public void delete(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM ticket_contacts WHERE contact_id = ?");
    Iterator i = this.iterator();
    while (i.hasNext()) {
      TicketContact contact = (TicketContact) i.next();
      pst.setInt(1, contact.getId());
      pst.execute();
    }
    pst.close();
  }


}
