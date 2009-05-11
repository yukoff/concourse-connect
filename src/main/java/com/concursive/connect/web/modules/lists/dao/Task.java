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

package com.concursive.connect.web.modules.lists.dao;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.issues.dao.Ticket;
import com.concursive.connect.web.utils.Dependency;
import com.concursive.connect.web.utils.DependencyList;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Description of the Class
 *
 * @author akhi_m
 * @version $Id$
 * @created August 15, 2002
 */
public class Task extends GenericBean {

  //static variables
  public static int DONE = 1;

  //db variables
  private int id = -1;
  private int enteredBy = -1;
  private int priority = -1;
  private int reminderId = -1;
  private int sharing = -1;
  private int modifiedBy = -1;
  private double estimatedLOE = -1;
  private int estimatedLOEType = -1;
  private int owner = -1;
  private int categoryId = -1;
  private int age = -1;
  private String notes = null;
  private String description = null;
  private boolean complete = false;
  private boolean enabled = false;
  private java.sql.Timestamp dueDate = null;
  private java.sql.Timestamp modified = null;
  private java.sql.Timestamp entered = null;
  private java.sql.Timestamp completeDate = null;
  private int ratingCount = 0;
  private int ratingValue = 0;
  private double ratingAverage = 0;
  private int functionalArea = -1;
  private int status = -1;
  private int businessValue = -1;
  private int complexity = -1;
  private int targetRelease = -1;
  private int targetSprint = -1;
  private int loeRemaining = -1;
  private int assignedPriority = -1;
  private int linkModuleId = -1;
  private int linkItemId = -1;
  private double linkItemRating = -1;

  //other
  private int contactId = -1;
  private int ticketId = -1;
  private int projectId = -1;
  private boolean hasLinks = false;
  private String contactName = null;
  private String ownerName = null;

  private Object contact = null;
  private Ticket ticket = null;

  private boolean hasEnabledOwnerAccount = true;
  private boolean hasEnabledLinkAccount = true;

  private boolean buildResources = false;
  private boolean buildProject = true;
  private boolean buildOwnerLinkItemRating = false;


  /**
   * Description of the Method
   */
  public Task() {
  }


  /**
   * Constructor for the Task object
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Task(Connection db, int thisId) throws SQLException {
    queryRecord(db, thisId);
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public Task(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }

  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param thisId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void queryRecord(Connection db, int thisId) throws SQLException {
    if (thisId == -1) {
      throw new SQLException("Task ID not specified");
    }

    PreparedStatement pst = db.prepareStatement(
        "SELECT t.task_id, t.entered, t.enteredby, t.priority, t.description, " +
            "t.duedate, t.notes, t.sharing, t.complete, t.estimatedloe, " +
            "t.estimatedloetype, t.owner, t.completedate, t.modified, " +
            "t.modifiedby, t.category_id, t.rating_count, t.rating_value, rating_avg, " +
            "functional_area, status, business_value, complexity, target_release, target_sprint, loe_remaining, " +
            "assigned_priority, link_module_id, link_item_id " +
            "FROM task t " +
            "WHERE task_id = ? ");
    int i = 0;
    pst.setInt(++i, thisId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (id == -1) {
      throw new SQLException("Task ID not found");
    }
    if (buildResources) {
      buildResources(db);
    } else if (buildProject) {
      buildProject(db);
    }
    if (buildOwnerLinkItemRating) {
      buildOwnerLinkedItemRating(db);
    }
  }


  /**
   * Sets the entered attribute of the Task object
   *
   * @param entered The new entered value
   */
  public void setEntered(java.sql.Timestamp entered) {
    this.entered = entered;
  }


  /**
   * Sets the entered attribute of the Task object
   *
   * @param entered The new entered value
   */
  public void setEntered(String entered) {
    this.entered = DatabaseUtils.parseTimestamp(entered);
  }


  /**
   * Sets the enteredBy attribute of the Task object
   *
   * @param enteredBy The new enteredBy value
   */
  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }


  /**
   * Gets the hasEnabledOwnerAccount attribute of the Task object
   *
   * @return The hasEnabledOwnerAccount value
   */
  public boolean getHasEnabledOwnerAccount() {
    return hasEnabledOwnerAccount;
  }


  /**
   * Gets the hasEnabledLinkAccount attribute of the Task object
   *
   * @return The hasEnabledLinkAccount value
   */
  public boolean getHasEnabledLinkAccount() {
    return hasEnabledLinkAccount;
  }


  /**
   * Sets the hasEnabledOwnerAccount attribute of the Task object
   *
   * @param tmp The new hasEnabledOwnerAccount value
   */
  public void setHasEnabledOwnerAccount(boolean tmp) {
    this.hasEnabledOwnerAccount = tmp;
  }


  /**
   * Sets the hasEnabledLinkAccount attribute of the Task object
   *
   * @param tmp The new hasEnabledLinkAccount value
   */
  public void setHasEnabledLinkAccount(boolean tmp) {
    this.hasEnabledLinkAccount = tmp;
  }


  /**
   * Sets the enteredBy attribute of the Task object
   *
   * @param enteredBy The new enteredBy value
   */
  public void setEnteredBy(String enteredBy) {
    this.enteredBy = Integer.parseInt(enteredBy);
  }


  /**
   * Sets the description attribute of the Task object
   *
   * @param description The new description value
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * Sets the priority attribute of the Task object
   *
   * @param priority The new priority value
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }


  /**
   * Sets the priority attribute of the Task object
   *
   * @param priority The new priority value
   */
  public void setPriority(String priority) {
    this.priority = Integer.parseInt(priority);
  }


  /**
   * Sets the dueDate attribute of the Task object
   *
   * @param dueDate The new dueDate value
   */
  public void setDueDate(java.sql.Timestamp dueDate) {
    this.dueDate = dueDate;
  }


  /**
   * Sets the dueDate attribute of the Task object
   *
   * @param tmp The new dueDate value
   */
  public void setDueDate(String tmp) {
    this.dueDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the notes attribute of the Task object
   *
   * @param notes The new notes value
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }


  /**
   * Sets the sharing attribute of the Task object
   *
   * @param sharing The new sharing value
   */
  public void setSharing(int sharing) {
    this.sharing = sharing;
  }


  /**
   * Sets the sharing attribute of the Task object sharing is set to 1 if
   * bussiness else for personal set to 0
   *
   * @param sharing The new sharing value
   */
  public void setSharing(String sharing) {
    this.sharing = Integer.parseInt(sharing);
  }


  /**
   * Sets the modifiedBy attribute of the Task object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the modifiedBy attribute of the Task object
   *
   * @param tmp The new modifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the complete attribute of the Task object
   *
   * @param complete The new complete value
   */
  public void setComplete(boolean complete) {
    this.complete = complete;
  }


  /**
   * Sets the complete attribute of the Task object
   *
   * @param complete The new complete value
   */
  public void setComplete(String complete) {
    this.complete = DatabaseUtils.parseBoolean(complete);
  }


  /**
   * Sets the estimatedLOE attribute of the Task object
   *
   * @param estimatedLOE The new estimatedLOE value
   */
  public void setEstimatedLOE(double estimatedLOE) {
    this.estimatedLOE = estimatedLOE;
  }


  /**
   * Sets the estimatedLOE attribute of the Task object
   *
   * @param estimatedLOE The new estimatedLOE value
   */
  public void setEstimatedLOE(String estimatedLOE) {
    this.estimatedLOE = Double.parseDouble(estimatedLOE);
  }


  /**
   * Sets the owner attribute of the Task object
   *
   * @param owner The new owner value
   */
  public void setOwner(int owner) {
    this.owner = owner;
  }


  /**
   * Sets the categoryId attribute of the Task object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(int tmp) {
    this.categoryId = tmp;
  }


  /**
   * Sets the categoryId attribute of the Task object
   *
   * @param tmp The new categoryId value
   */
  public void setCategoryId(String tmp) {
    this.setCategoryId(Integer.parseInt(tmp));
  }


  /**
   * Sets the owner attribute of the Task object
   *
   * @param owner The new owner value
   */
  public void setOwner(String owner) {
    this.owner = Integer.parseInt(owner);
  }


  /**
   * Sets the id attribute of the Task object
   *
   * @param id The new id value
   */
  public void setId(int id) {
    this.id = id;
  }


  /**
   * Sets the id attribute of the Task object
   *
   * @param id The new id value
   */
  public void setId(String id) {
    this.id = Integer.parseInt(id);
  }


  /**
   * Sets the age attribute of the Task object
   *
   * @param age The new age value
   */
  public void setAge(int age) {
    this.age = age;
  }


  /**
   * Sets the age attribute of the Task object
   *
   * @param age The new age value
   */
  public void setAge(String age) {
    this.age = Integer.parseInt(age);
  }


  /**
   * Sets the contactId attribute of the Task object
   *
   * @param contactId The new contactId value
   */
  @Deprecated
  public void setContactId(int contactId) {
    this.contactId = contactId;
  }


  /**
   * Sets the contactId attribute of the Task object
   *
   * @param contactId The new contactId value
   */
  @Deprecated
  public void setContactId(String contactId) {
    this.contactId = Integer.parseInt(contactId);
  }


  /**
   * Sets the contactName attribute of the Task object
   *
   * @param contactName The new contactName value
   */
  @Deprecated
  public void setContactName(String contactName) {
    this.contactName = contactName;
  }


  /**
   * Sets the contact attribute of the Task object
   *
   * @param contact_id The new contact value
   */
  @Deprecated
  public void setContact(String contact_id) {
    this.contactId = Integer.parseInt(contact_id);
  }


  /**
   * Sets the ticketId attribute of the Task object
   *
   * @param ticketId The new ticketId value
   */
  @Deprecated
  public void setTicketId(int ticketId) {
    this.ticketId = ticketId;
  }


  /**
   * Sets the ticketId attribute of the Task object
   *
   * @param ticketId The new ticketId value
   */
  @Deprecated
  public void setTicketId(String ticketId) {
    this.ticketId = Integer.parseInt(ticketId);
  }


  /**
   * Gets the projectId attribute of the Task object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Sets the projectId attribute of the Task object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the Task object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the estimatedLOEType attribute of the Task object
   *
   * @param estimatedLOEType The new estimatedLOEType value
   */
  public void setEstimatedLOEType(int estimatedLOEType) {
    this.estimatedLOEType = estimatedLOEType;
  }


  /**
   * Sets the estimatedLOEType attribute of the Task object
   *
   * @param estimatedLOEType The new estimatedLOEType value
   */
  public void setEstimatedLOEType(String estimatedLOEType) {
    this.estimatedLOEType = Integer.parseInt(estimatedLOEType);
  }


  /**
   * Gets the estimatedLOEType attribute of the Task object
   *
   * @return The estimatedLOEType value
   */
  public int getEstimatedLOEType() {
    return estimatedLOEType;
  }


  /**
   * Sets the completeDate attribute of the Task object
   *
   * @param completeDate The new completeDate value
   */
  public void setCompleteDate(java.sql.Timestamp completeDate) {
    this.completeDate = completeDate;
  }


  /**
   * Sets the modified attribute of the Task object
   *
   * @param modified The new modified value
   */
  public void setModified(java.sql.Timestamp modified) {
    this.modified = modified;
  }


  /**
   * Sets the modified attribute of the Task object
   *
   * @param modified The new modified value
   */
  public void setModified(String modified) {
    this.modified = DatabaseUtils.parseTimestamp(modified);
  }


  /**
   * Gets the modified attribute of the Task object
   *
   * @return The modified value
   */
  public java.sql.Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedBy attribute of the Task object
   *
   * @return The modifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Gets the completeDate attribute of the Task object
   *
   * @return The completeDate value
   */
  public java.sql.Timestamp getCompleteDate() {
    return completeDate;
  }


  /**
   * Gets the completeDateString attribute of the Task object
   *
   * @return The completeDateString value
   */
  public String getCompleteDateString() {
    String tmp = "";
    try {
      return DateFormat.getDateInstance(3).format(completeDate);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the contact attribute of the Task object
   *
   * @return The contact value
   */
  @Deprecated
  public Object getContact() {
    return contact;
  }


  /**
   * Gets the ticket attribute of the Task object
   *
   * @return The ticket value
   */
  @Deprecated
  public Ticket getTicket() {
    return ticket;
  }


  /**
   * Gets the hasLinks attribute of the Task object
   *
   * @return The hasLinks value
   */
  @Deprecated
  public boolean getHasLinks() {
    return hasLinks;
  }


  /**
   * Gets the ticketId attribute of the Task object
   *
   * @return The ticketId value
   */
  @Deprecated
  public int getTicketId() {
    return ticketId;
  }


  /**
   * Gets the contactName attribute of the Task object
   *
   * @return The contactName value
   */
  @Deprecated
  public String getContactName() {
    return contactName;
  }

  public int getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  public void setRatingCount(String ratingCount) {
    this.ratingCount = Integer.parseInt(ratingCount);
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

  public double getRatingAverage() {
    return ratingAverage;
  }

  public void setRatingAverage(double ratingAverage) {
    this.ratingAverage = ratingAverage;
  }

  public int getFunctionalArea() {
    return functionalArea;
  }

  public void setFunctionalArea(int functionalArea) {
    this.functionalArea = functionalArea;
  }

  public void setFunctionalArea(String tmp) {
    functionalArea = Integer.parseInt(tmp);
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setStatus(String tmp) {
    status = Integer.parseInt(tmp);
  }

  public int getBusinessValue() {
    return businessValue;
  }

  public void setBusinessValue(int businessValue) {
    this.businessValue = businessValue;
  }

  public void setBusinessValue(String tmp) {
    businessValue = Integer.parseInt(tmp);
  }

  public int getComplexity() {
    return complexity;
  }

  public void setComplexity(int complexity) {
    this.complexity = complexity;
  }

  public void setComplexity(String tmp) {
    complexity = Integer.parseInt(tmp);
  }

  public int getTargetRelease() {
    return targetRelease;
  }

  public void setTargetRelease(int targetRelease) {
    this.targetRelease = targetRelease;
  }

  public void setTargetRelease(String tmp) {
    targetRelease = Integer.parseInt(tmp);
  }

  public int getTargetSprint() {
    return targetSprint;
  }

  public void setTargetSprint(int targetSprint) {
    this.targetSprint = targetSprint;
  }

  public void setTargetSprint(String tmp) {
    targetSprint = Integer.parseInt(tmp);
  }

  public int getLoeRemaining() {
    return loeRemaining;
  }

  public void setLoeRemaining(int loeRemaining) {
    this.loeRemaining = loeRemaining;
  }

  public void setLoeRemaining(String tmp) {
    this.loeRemaining = Integer.parseInt(tmp);
  }

  public int getAssignedPriority() {
    return assignedPriority;
  }

  public void setAssignedPriority(int assignedPriority) {
    this.assignedPriority = assignedPriority;
  }

  public void setAssignedPriority(String tmp) {
    this.assignedPriority = Integer.parseInt(tmp);
  }

  public int getLinkModuleId() {
    return linkModuleId;
  }

  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  public void setLinkModuleId(String tmp) {
    linkModuleId = Integer.parseInt(tmp);
  }

  public int getLinkItemId() {
    return linkItemId;
  }

  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public void setLinkItemId(String tmp) {
    linkItemId = Integer.parseInt(tmp);
  }


  public double getLinkItemRating() {
    return linkItemRating;
  }

  public void setLinkItemRating(double linkItemRating) {
    this.linkItemRating = linkItemRating;
  }

  public void setLinkItemRating(String tmp) {
    linkItemRating = Integer.parseInt(tmp);
  }

  public boolean getBuildResources() {
    return buildResources;
  }

  public void setBuildResources(boolean buildResources) {
    this.buildResources = buildResources;
  }


  public boolean getBuildOwnerLinkItemRating() {
    return buildOwnerLinkItemRating;
  }

  public void setBuildOwnerLinkItemRating(boolean buildOwnerLinkItemRating) {
    this.buildOwnerLinkItemRating = buildOwnerLinkItemRating;
  }

  /**
   * Gets the alertDateStringLongYear attribute of the Task object
   *
   * @return The alertDateStringLongYear value
   */
  public String getAlertDateStringLongYear() {
    String tmp = "";
    try {
      SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG);
      formatter.applyPattern("M/d/yyyy");
      return formatter.format(dueDate);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the alertDateStringLongYear attribute of the Task class
   *
   * @param dueDate Description of the Parameter
   * @return The alertDateStringLongYear value
   */
  public static String getAlertDateStringLongYear(java.sql.Timestamp dueDate) {
    String tmp = "";
    try {
      SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG);
      formatter.applyPattern("M/d/yyyy");
      return formatter.format(dueDate);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the contactId attribute of the Task object
   *
   * @return The contactId value
   */
  @Deprecated
  public int getContactId() {
    return contactId;
  }


  /**
   * Gets the age attribute of the Task object
   *
   * @return The age value
   */
  public int getAge() {
    return age;
  }


  /**
   * Gets the age attribute of the Task object
   *
   * @return The age value
   */
  public String getAgeString() {
    return age + "d";
  }


  /**
   * Gets the estimatedLOE attribute of the Task object
   *
   * @return The estimatedLOE value
   */
  public double getEstimatedLOE() {
    return estimatedLOE;
  }


  /**
   * Gets the estimatedLOEValue attribute of the Task object
   *
   * @return The estimatedLOEValue value
   */
  public String getEstimatedLOEValue() {
    String toReturn = String.valueOf(estimatedLOE);
    if (toReturn.endsWith(".0")) {
      toReturn = (toReturn.substring(0, toReturn.length() - 2));
    }
    if ("0".equals(toReturn) || estimatedLOE < 0) {
      toReturn = "";
    }
    return toReturn;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void checkEnabledOwnerAccount(Connection db) throws SQLException {
    if (this.getOwner() == -1) {
      throw new SQLException("ID not specified for lookup.");
    }

    PreparedStatement pst = db.prepareStatement(
        "SELECT * " +
            "FROM access " +
            "WHERE user_id = ? AND enabled = ? ");
    pst.setInt(1, this.getOwner());
    pst.setBoolean(2, true);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      this.setHasEnabledOwnerAccount(true);
    } else {
      this.setHasEnabledOwnerAccount(false);
    }
    rs.close();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  @Deprecated
  public void checkEnabledLinkAccount(Connection db) throws SQLException {
    if (this.getContactId() == -1) {
      throw new SQLException("ID not specified for lookup.");
    }

    PreparedStatement pst = db.prepareStatement(
        "SELECT * " +
            "FROM access " +
            "WHERE user_id = ? AND enabled = ? ");
    pst.setInt(1, this.getContactId());
    pst.setBoolean(2, true);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      this.setHasEnabledLinkAccount(true);
    } else {
      this.setHasEnabledLinkAccount(false);
    }
    rs.close();
    pst.close();
  }


  /**
   * Gets the sharing attribute of the Task object
   *
   * @return The sharing value
   */
  public int getSharing() {
    return sharing;
  }


  /**
   * Gets the owner attribute of the Task object
   *
   * @return The owner value
   */
  public int getOwner() {
    return owner;
  }


  /**
   * Gets the categoryId attribute of the Task object
   *
   * @return The categoryId value
   */
  public int getCategoryId() {
    return categoryId;
  }


  /**
   * Gets the ownerName attribute of the Task object
   *
   * @return The ownerName value
   */
  public String getOwnerName() {
    return ownerName;
  }


  /**
   * Gets the complete attribute of the Task object
   *
   * @return The complete value
   */
  public boolean getComplete() {
    return complete;
  }


  /**
   * Gets the notes attribute of the Task object
   *
   * @return The notes value
   */
  public String getNotes() {
    return notes;
  }


  /**
   * Gets the reminderId attribute of the Task object
   *
   * @return The reminderId value
   */
  public int getReminderId() {
    return reminderId;
  }


  /**
   * Gets the dueDate attribute of the Task object
   *
   * @return The dueDate value
   */
  public java.sql.Timestamp getDueDate() {
    return dueDate;
  }


  /**
   * Gets the dueDateString attribute of the Task object
   *
   * @return The dueDateString value
   */
  public String getDueDateString() {
    String tmp = "";
    try {
      return DateFormat.getDateInstance(3).format(dueDate);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the description attribute of the Task object
   *
   * @return The description value
   */
  public String getDescription() {
    return description;
  }


  /**
   * Gets the priority attribute of the Task object
   *
   * @return The priority value
   */
  public int getPriority() {
    return priority;
  }


  /**
   * Gets the enteredBy attribute of the Task object
   *
   * @return The enteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the entered attribute of the Task object
   *
   * @return The entered value
   */
  public java.sql.Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the enteredString attribute of the Task object
   *
   * @return The enteredString value
   */
  public String getEnteredString() {
    String tmp = "";
    try {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(entered);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the id attribute of the Task object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean insert(Connection db) throws SQLException {
    String sql = null;
    if (!isValid()) {
      return false;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      sql = "INSERT INTO task " +
          "(enteredby, modifiedby, priority, description, notes, sharing, owner, duedate, estimatedloe, " +
          (estimatedLOEType == -1 ? "" : "estimatedloetype, ") +
          "complete, completedate, category_id, rating_count, rating_value, rating_avg, " +
          "functional_area, status, business_value, complexity, target_release, target_sprint, loe_remaining, " +
          "assigned_priority ";
      if (linkModuleId > -1 && linkItemId > -1) {
        sql += ", link_module_id, link_item_id ";
      }
      sql += ") " +
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
          (estimatedLOEType == -1 ? "" : "?, ") +
          "?, ?, ?, ?, ?, ?, " +
          "?, ?, ?, ?, ? ";
      if (linkModuleId > -1 && linkItemId > -1) {
        sql += ", ?, ? ";
      }
      sql += ") ";
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql);
      pst.setInt(++i, this.getEnteredBy());
      pst.setInt(++i, this.getModifiedBy());
      pst.setInt(++i, this.getPriority());
      pst.setString(++i, this.getDescription());
      pst.setString(++i, this.getNotes());
      pst.setInt(++i, this.getSharing());
      DatabaseUtils.setInt(pst, ++i, this.getOwner());
      pst.setTimestamp(++i, this.getDueDate());
      pst.setDouble(++i, this.getEstimatedLOE());
      if (this.getEstimatedLOEType() != -1) {
        pst.setInt(++i, this.getEstimatedLOEType());
      }
      pst.setBoolean(++i, this.getComplete());
      if (this.getComplete()) {
        if (completeDate == null) {
          completeDate = new Timestamp(System.currentTimeMillis());
        }
      } else {
        completeDate = null;
      }
      DatabaseUtils.setTimestamp(pst, ++i, completeDate);
      DatabaseUtils.setInt(pst, ++i, categoryId);
      pst.setInt(++i, ratingCount);
      pst.setInt(++i, ratingValue);
      pst.setDouble(++i, ratingAverage);
      DatabaseUtils.setInt(pst, ++i, functionalArea);
      DatabaseUtils.setInt(pst, ++i, status);
      DatabaseUtils.setInt(pst, ++i, businessValue);
      DatabaseUtils.setInt(pst, ++i, complexity);
      DatabaseUtils.setInt(pst, ++i, targetRelease);
      DatabaseUtils.setInt(pst, ++i, targetSprint);
      DatabaseUtils.setInt(pst, ++i, loeRemaining);
      DatabaseUtils.setInt(pst, ++i, assignedPriority);
      if (linkModuleId > -1 && linkItemId > -1) {
        pst.setInt(++i, linkModuleId);
        pst.setInt(++i, linkItemId);
      }
      pst.execute();
      this.id = DatabaseUtils.getCurrVal(db, "task_task_id_seq", -1);
      pst.close();
      if (this.getContactId() != -1) {
        //insertContacts(db, true);
      }
      // Log the task
      TaskLog taskLog = new TaskLog(this);
      taskLog.insert(db);
      // Link it to the appropriate project
      if (projectId > -1) {
        insertProjectLink(db);
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
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int update(Connection db) throws SQLException {
    String sql = null;
    PreparedStatement pst = null;
    int count = 0;
    if (id == -1) {
      throw new SQLException("Task ID not specified");
    }
    if (!isValid()) {
      return -1;
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      Task previousTask = new Task(db, id);
      sql = "UPDATE task " +
          "SET modifiedby = ?, priority = ?, description = ?, notes = ?, " +
          "sharing = ?, owner = ?, duedate = ?, estimatedloe = ?, " +
          (estimatedLOEType == -1 ? "" : "estimatedloetype = ?, ") +
          "functional_area = ?, status = ?, business_value = ?, complexity = ?, target_release = ?, target_sprint = ?, loe_remaining = ?, " +
          "assigned_priority = ?, " +
          "modified = CURRENT_TIMESTAMP, complete = ?, completedate = ?, category_id = ? ";
      if (linkModuleId > -1 && linkItemId > -1) {
        sql += ", link_module_id = ?, link_item_id = ? ";
      }
      sql += "WHERE task_id = ? AND modified = ? ";
      int i = 0;
      pst = db.prepareStatement(sql);
      pst.setInt(++i, this.getModifiedBy());
      pst.setInt(++i, this.getPriority());
      pst.setString(++i, this.getDescription());
      pst.setString(++i, this.getNotes());
      pst.setInt(++i, this.getSharing());
      DatabaseUtils.setInt(pst, ++i, this.getOwner());
      pst.setTimestamp(++i, this.getDueDate());
      pst.setDouble(++i, this.getEstimatedLOE());
      if (this.getEstimatedLOEType() != -1) {
        pst.setInt(++i, this.getEstimatedLOEType());
      }
      DatabaseUtils.setInt(pst, ++i, functionalArea);
      DatabaseUtils.setInt(pst, ++i, status);
      DatabaseUtils.setInt(pst, ++i, businessValue);
      DatabaseUtils.setInt(pst, ++i, complexity);
      DatabaseUtils.setInt(pst, ++i, targetRelease);
      DatabaseUtils.setInt(pst, ++i, targetSprint);
      DatabaseUtils.setInt(pst, ++i, loeRemaining);
      DatabaseUtils.setInt(pst, ++i, assignedPriority);
      pst.setBoolean(++i, this.getComplete());
      if (previousTask.getComplete() && this.getComplete()) {
        completeDate = previousTask.getCompleteDate();
      } else if (this.getComplete() && !previousTask.getComplete()) {
        completeDate = new Timestamp(System.currentTimeMillis());
      } else {
        completeDate = null;
      }
      DatabaseUtils.setTimestamp(pst, ++i, completeDate);
      DatabaseUtils.setInt(pst, ++i, categoryId);
      if (linkModuleId > -1 && linkItemId > -1) {
        pst.setInt(++i, linkModuleId);
        pst.setInt(++i, linkItemId);
      }
      pst.setInt(++i, id);
      pst.setTimestamp(++i, this.getModified());
      count = pst.executeUpdate();
      pst.close();
      if (contactId == -1) {
        //processContacts(db, false);
      } else {
        processContacts(db, true);
      }
      // Log the task
      TaskLog taskLog = new TaskLog(this);
      taskLog.insert(db);
      if (commit) {
        db.commit();
      }
    } catch (SQLException e) {
      if (commit) {
        db.rollback();
      }
      e.printStackTrace();
      throw e;
    } finally {
      if (commit) {
        db.setAutoCommit(true);
      }
    }
    return count;
  }


  /**
   * Description of the Method
   *
   * @param db           Description of the Parameter
   * @param linkContacts Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean processContacts(Connection db, boolean linkContacts) throws SQLException {
    String sql = null;
    if (this.getId() == -1) {
      throw new SQLException("Task ID not specified");
    }
    boolean commit = true;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }
      sql = "DELETE FROM tasklink_contact " +
          "WHERE task_id = ? ";
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql);
      pst.setInt(++i, this.getId());
      pst.execute();
      pst.close();
      if (linkContacts) {
        if (contactId == -1) {
          throw new SQLException("Contact ID incorrect");
        }
        sql = "INSERT INTO tasklink_contact " +
            "(task_id, contact_id) " +
            "VALUES (?, ?) ";
        i = 0;
        pst = db.prepareStatement(sql);
        pst.setInt(++i, this.getId());
        pst.setInt(++i, this.getContactId());
        pst.execute();
        pst.close();
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
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public DependencyList processDependencies(Connection db) throws SQLException {
    ResultSet rs = null;
    String sql = null;
    DependencyList dependencyList = new DependencyList();
    sql = "SELECT count(*) as linkcount " +
        "FROM tasklink_contact " +
        "WHERE task_id = ? ";
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(++i, this.getId());
    rs = pst.executeQuery();
    if (rs.next()) {
      int linkcount = rs.getInt("linkcount");
      if (linkcount != 0) {
        Dependency thisDependency = new Dependency();
        thisDependency.setName("contacts");
        thisDependency.setCount(linkcount);
        thisDependency.setCanDelete(true);
        dependencyList.add(thisDependency);
      }
    }
    rs.close();
    pst.close();

    sql = "SELECT count(*) as linkcount " +
        "FROM tasklink_ticket " +
        "WHERE task_id = ? ";
    i = 0;
    pst = db.prepareStatement(sql);
    pst.setInt(++i, this.getId());
    rs = pst.executeQuery();
    if (rs.next()) {
      int linkcount = rs.getInt("linkcount");
      if (linkcount != 0) {
        Dependency thisDependency = new Dependency();
        thisDependency.setName("tickets");
        thisDependency.setCount(linkcount);
        thisDependency.setCanDelete(true);
        dependencyList.add(thisDependency);
      }
    }
    rs.close();
    pst.close();

    sql = "SELECT count(*) as linkcount " +
        "FROM tasklink_project " +
        "WHERE task_id = ? ";
    i = 0;
    pst = db.prepareStatement(sql);
    pst.setInt(++i, this.getId());
    rs = pst.executeQuery();
    if (rs.next()) {
      int linkcount = rs.getInt("linkcount");
      if (linkcount != 0) {
        Dependency thisDependency = new Dependency();
        thisDependency.setName("projects");
        thisDependency.setCount(linkcount);
        thisDependency.setCanDelete(true);
        dependencyList.add(thisDependency);
      }
    }
    rs.close();
    pst.close();
    return dependencyList;
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean delete(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("Task ID not specified");
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }
      deleteRelationships(db);
      deleteRatings(db);
      deleteTaskLog(db);
      PreparedStatement pst = db.prepareStatement(
          "DELETE from task " +
              "WHERE task_id = ? ");
      pst.setInt(1, this.getId());
      pst.execute();
      pst.close();
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
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean deleteRelationships(Connection db) throws SQLException {
    boolean commit = true;
    try {
      commit = db.getAutoCommit();
      if (commit) {
        db.setAutoCommit(false);
      }
      //Delete contact link
      PreparedStatement pst = db.prepareStatement(
          "DELETE from tasklink_contact " +
              "WHERE task_id = ? "
      );
      pst.setInt(1, this.getId());
      pst.execute();
      pst.close();
      //Delete ticket link
      pst = db.prepareStatement(
          "DELETE from tasklink_ticket " +
              "WHERE task_id = ? "
      );
      pst.setInt(1, this.getId());
      pst.execute();
      pst.close();
      //Delete project link
      pst = db.prepareStatement(
          "DELETE from tasklink_project " +
              "WHERE task_id = ? ");
      pst.setInt(1, this.getId());
      pst.execute();
      pst.close();
      //Commit
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

  public void deleteRatings(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE from task_rating " +
            "WHERE task_id = ? ");
    pst.setInt(1, this.getId());
    pst.execute();
    pst.close();
  }


  public void deleteTaskLog(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE from tasklog " +
            "WHERE task_id = ? ");
    pst.setInt(1, this.getId());
    pst.execute();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildResources(Connection db) throws SQLException {
    ResultSet rs = null;
    String sql = null;
    if (this.getId() == -1) {
      throw new SQLException("Task ID not specified");
    }
    //build the linked contact info
    sql = "SELECT contact_id " +
        "FROM tasklink_contact tl_ct " +
        "WHERE task_id = ? ";
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(++i, this.getId());
    rs = pst.executeQuery();
    if (rs.next()) {
      contactId = rs.getInt("contact_id");
      hasLinks = true;
    }
    if (contactId > 0) {
      //contact = new Contact(db, contactId);
      //contactName = contact.getNameLastFirst();
    }
    //build the linked ticket info
    sql = "SELECT ticket_id " +
        "FROM tasklink_ticket " +
        "WHERE task_id = ? ";
    i = 0;
    pst = db.prepareStatement(sql);
    pst.setInt(++i, this.getId());
    rs = pst.executeQuery();
    if (rs.next()) {
      this.ticketId = rs.getInt("ticket_id");
    }
    rs.close();
    pst.close();
    if (ticketId > 0) {
      this.ticket = new Ticket(db, this.ticketId);
    }
    buildProject(db);
  }

  private void buildProject(Connection db) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("Task ID not specified");
    }
    // find the linked project
    String sql = "SELECT project_id " +
        "FROM tasklink_project " +
        "WHERE task_id = ? ";
    PreparedStatement pst = db.prepareStatement(sql);
    int i = 0;
    pst.setInt(++i, this.getId());
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      this.projectId = rs.getInt("project_id");
    }
    rs.close();
    pst.close();
  }

  public void buildOwnerLinkedItemRating(Connection db) throws SQLException {
    //skip if no linked object is specified, or if no owner exists
    if (linkModuleId == -1 || linkItemId == -1 || owner == -1) {
      return;
    }
    if (linkModuleId == Constants.TASK_CATEGORY_PROJECTS) {
      //get the project's rating for that owner
      String sql = "SELECT rating " +
          "FROM projects_rating " +
          "WHERE project_id = ? " +
          "AND enteredby = ? ";
      PreparedStatement pst = db.prepareStatement(sql);
      int i = 0;
      pst.setInt(++i, this.getLinkItemId());
      pst.setInt(++i, this.getOwner());

      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        this.linkItemRating = rs.getInt("rating");
      }
      rs.close();
      pst.close();
    }
  }


  /**
   * Description of the Method
   *
   * @param rs Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void buildRecord(ResultSet rs) throws SQLException {
    id = rs.getInt("task_id");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    priority = rs.getInt("priority");
    description = rs.getString("description");
    dueDate = rs.getTimestamp("duedate");
    notes = rs.getString("notes");
    sharing = rs.getInt("sharing");
    complete = rs.getBoolean("complete");
    estimatedLOE = rs.getDouble("estimatedloe");
    estimatedLOEType = DatabaseUtils.getInt(rs, "estimatedloetype");
    owner = DatabaseUtils.getInt(rs, "owner");
    completeDate = rs.getTimestamp("completedate");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    categoryId = DatabaseUtils.getInt(rs, "category_id");
    ratingCount = rs.getInt("rating_count");
    ratingValue = rs.getInt("rating_value");
    ratingAverage = rs.getDouble("rating_avg");
    functionalArea = DatabaseUtils.getInt(rs, "functional_area");
    status = DatabaseUtils.getInt(rs, "status");
    businessValue = DatabaseUtils.getInt(rs, "business_value");
    complexity = DatabaseUtils.getInt(rs, "complexity");
    targetRelease = DatabaseUtils.getInt(rs, "target_release");
    targetSprint = DatabaseUtils.getInt(rs, "target_sprint");
    loeRemaining = DatabaseUtils.getInt(rs, "loe_remaining");
    assignedPriority = DatabaseUtils.getInt(rs, "assigned_priority");
    linkModuleId = DatabaseUtils.getInt(rs, "link_module_id");
    linkItemId = DatabaseUtils.getInt(rs, "link_item_id");
    // Other items
    if (entered != null) {
      float ageCheck = ((System.currentTimeMillis() - entered.getTime()) / 86400000);
      age = java.lang.Math.round(ageCheck);
    }
  }


  /**
   * Gets the valid attribute of the Task object
   *
   * @return The valid value
   * @throws SQLException Description of the Exception
   */
  protected boolean isValid() throws SQLException {
    errors.clear();
    if (!StringUtils.hasText(this.getDescription())) {
      errors.put("descriptionError", "Task Description is required");
    }
    if (this.getCategoryId() == -1 && owner == -1) {
      errors.put("ownerError", "Owner is required");
    }
    return !hasErrors();
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void insertProjectLink(Connection db) throws SQLException {
    if (projectId == -1) {
      throw new SQLException("ProjectId not set");
    }
    String sql = "INSERT INTO tasklink_project " +
        "(task_id, project_id) " +
        "VALUES (?, ?) ";
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(++i, this.getId());
    pst.setInt(++i, projectId);
    pst.execute();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db            Description of the Parameter
   * @param newCategoryId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void updateCategoryId(Connection db, int newCategoryId) throws SQLException {
    int i = 0;
    PreparedStatement pst = db.prepareStatement(
        "UPDATE task " +
            "SET category_id = ? " +
            "WHERE task_id = ? ");
    pst.setInt(++i, newCategoryId);
    pst.setInt(++i, id);
    pst.execute();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param taskId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void markComplete(Connection db, int taskId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE task " +
            "SET complete = ?, completedate = ? " +
            "WHERE task_id = ? ");
    pst.setBoolean(1, true);
    pst.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
    pst.setInt(3, taskId);
    pst.execute();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db     Description of the Parameter
   * @param taskId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void markIncomplete(Connection db, int taskId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE task " +
            "SET complete = ?, completedate = ? " +
            "WHERE task_id = ? ");
    pst.setBoolean(1, false);
    pst.setNull(2, java.sql.Types.TIMESTAMP);
    pst.setInt(3, taskId);
    pst.execute();
    pst.close();
  }


  /**
   * Gets the timeZoneParams attribute of the Task class
   *
   * @return The timeZoneParams value
   */
  public static ArrayList<String> getTimeZoneParams() {
    ArrayList<String> thisList = new ArrayList<String>();
    thisList.add("dueDate");
    return thisList;
  }
}

