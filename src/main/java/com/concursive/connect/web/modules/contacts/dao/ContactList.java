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

package com.concursive.connect.web.modules.contacts.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.contacts.beans.ContactSearchBean;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Description of Class
 *
 * @author matt rajkowski
 * @version $Id:ContactList.java 2246 2007-03-22 05:57:41Z matt $
 * @created Mar 12, 2007
 */
public class ContactList extends ArrayList<Contact> {

  private PagedListInfo pagedListInfo = null;
  private int enteredBy = -1;
  private int forUser = -1;
  private ContactSearchBean searchCriteria = null;
  private int includeAllGlobal = Constants.UNDEFINED;
  private int contactId = -1;

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  public void setPagedListInfo(PagedListInfo pagedListInfo) {
    this.pagedListInfo = pagedListInfo;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getForUser() {
    return forUser;
  }

  public void setForUser(int forUser) {
    this.forUser = forUser;
  }

  public ContactSearchBean getSearchCriteria() {
    return searchCriteria;
  }

  public void setSearchCriteria(ContactSearchBean searchCriteria) {
    this.searchCriteria = searchCriteria;
  }


  public void setIncludeAllGlobal(int includeAllGlobal) {
    this.includeAllGlobal = includeAllGlobal;
  }


  public void setContactId(int contactId) {
    this.contactId = contactId;
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
            "FROM contacts c " +
            "WHERE c.contact_id > -1 ");
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
    pagedListInfo.setDefaultSort("c.file_as, c.last_name, c.first_name, c.organization", null);
    pagedListInfo.appendSqlTail(db, sqlOrder);
    //Need to build a base SQL statement for returning records
    if (pagedListInfo != null) {
      pagedListInfo.appendSqlSelectHead(db, sqlSelect);
    } else {
      sqlSelect.append("SELECT ");
    }
    sqlSelect.append(
        "c.* " +
            "FROM contacts c " +
            "WHERE c.contact_id > -1 ");
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
      Contact thisRecord = new Contact(rs);
      this.add(thisRecord);
    }
    rs.close();
    pst.close();
  }


  protected void createFilter(StringBuffer sqlFilter) {
    if (enteredBy > -1) {
      sqlFilter.append("AND c.enteredby = ? ");
    }
    if (forUser > -1) {
      if (includeAllGlobal == Constants.TRUE) {
        sqlFilter.append("AND (c.owner = ? OR c.global = ?) ");
      } else {
        sqlFilter.append("AND c.owner = ? ");
      }
    }
    if (contactId > -1) {
      sqlFilter.append("AND c.contact_id = ? ");
    }
    if (searchCriteria != null) {
      boolean found = false;
      StringBuffer search = new StringBuffer("");
      if (StringUtils.hasText(searchCriteria.getFirstName())) {
        search.append(searchCriteria.appendMethod(found));
        search.append("lower(c.first_name) LIKE ? ");
        found = true;
      }
      if (StringUtils.hasText(searchCriteria.getLastName())) {
        search.append(searchCriteria.appendMethod(found));
        search.append("lower(c.last_name) LIKE ? ");
        found = true;
      }
      if (StringUtils.hasText(searchCriteria.getOrganization())) {
        search.append(searchCriteria.appendMethod(found));
        search.append("lower(c.organization) LIKE ? ");
        found = true;
      }
      if (StringUtils.hasText(searchCriteria.getEmail())) {
        search.append(searchCriteria.appendMethod(found));
        search.append("(lower(c.email1) LIKE ? OR lower(c.email2) LIKE ? OR lower(c.email3) LIKE ?) ");
        found = true;
      }
      if (found) {
        sqlFilter.append("AND (").append(search).append(") ");
      }
    }
  }


  protected int prepareFilter(PreparedStatement pst) throws SQLException {
    int i = 0;
    if (enteredBy > -1) {
      pst.setInt(++i, enteredBy);
    }
    if (forUser > -1) {
      if (includeAllGlobal == Constants.TRUE) {
        pst.setInt(++i, forUser);
        pst.setBoolean(++i, true);
      } else {
        pst.setInt(++i, forUser);
      }
    }
    if (contactId > -1) {
      pst.setInt(++i, contactId);
    }
    if (searchCriteria != null) {
      if (StringUtils.hasText(searchCriteria.getFirstName())) {
        pst.setString(++i, "%" + searchCriteria.getFirstName().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getLastName())) {
        pst.setString(++i, "%" + searchCriteria.getLastName().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getOrganization())) {
        pst.setString(++i, "%" + searchCriteria.getOrganization().toLowerCase() + "%");
      }
      if (StringUtils.hasText(searchCriteria.getEmail())) {
        pst.setString(++i, "%" + searchCriteria.getEmail().toLowerCase() + "%");
        pst.setString(++i, "%" + searchCriteria.getEmail().toLowerCase() + "%");
        pst.setString(++i, "%" + searchCriteria.getEmail().toLowerCase() + "%");
      }
    }
    return i;
  }

  public static int getIdByEmailAddress(Connection db, String email) throws SQLException {
    int contactId = -1;
    PreparedStatement pst = db.prepareStatement(
        "SELECT contact_id " +
            "FROM contacts c " +
            "WHERE (lower(c.email1) LIKE ? OR lower(c.email2) LIKE ? OR lower(c.email3) LIKE ?)  ");
    pst.setString(1, email.toLowerCase());
    pst.setString(2, email.toLowerCase());
    pst.setString(3, email.toLowerCase());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      contactId = rs.getInt("contact_id");
    }
    rs.close();
    pst.close();
    return contactId;
  }

}
