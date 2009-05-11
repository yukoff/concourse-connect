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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.servlets.LinkGenerator;
import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.blog.dao.BlogPostComment;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.common.social.viewing.utils.Viewing;
import com.concursive.connect.web.modules.discussion.dao.Topic;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.reviews.dao.ProjectRating;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiComment;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Represents an issue object
 *
 * @author chris
 * @version $Id$
 * @created November 8, 2001
 */
public class Ticket extends GenericBean {

  public static final String TABLE = "ticket";
  public static final String PRIMARY_KEY = "ticketid";

  Object thisContact = new Object();

  private String errorMessage = "";
  private int id = -1;
  private int orgId = -1;
  private int contactId = -1;
  private int assignedTo = -1;

  private String problem = "";
  private String location = null;
  private String comment = "";
  private Timestamp estimatedResolutionDate = null;
  private String cause = null;
  private String solution = "";
  private int priorityCode = -1;
  private int levelCode = -1;
  private int departmentCode = -1;
  private int sourceCode = -1;
  private int catCode = 0;
  private int subCat1 = 0;
  private int subCat2 = 0;
  private int subCat3 = 0;
  private int severityCode = -1;

  private int enteredBy = -1;
  private int modifiedBy = -1;

  private Timestamp entered = null;
  private Timestamp modified = null;
  private Timestamp closed = null;

  private int relatedId = -1;
  private int causeId = -1;
  private int resolutionId = -1;
  private int defectId = -1;
  private int escalationId = -1;
  private int stateId = -1;

  private int readCount = 0;
  private Timestamp readDate = null;

  private int linkProjectId = -1;
  private int linkModuleId = -1;
  private int linkItemId = -1;

  // Helpers
  private String companyName = "";
  private String categoryName = "";
  private String subCategoryName1 = "";
  private String subCategoryName2 = "";
  private String subCategoryName3 = "";
  private String departmentName = "";
  private String priorityName = "";
  private String severityName = "";
  private String sourceName = "";

  private boolean closeIt = false;
  private boolean readyForClose = false;
  private boolean companyEnabled = true;

  // Distribution list
  private String insertMembers = null;
  private String deleteMembers = null;

  // File attachments
  private String attachmentList = null;

  private int ageDays = 0;
  private int ageHours = 0;
  private int campaignId = -1;
  private boolean hasEnabledOwnerAccount = true;
  private int projectId = -1;
  private int projectTicketCount = -1;

  //Resources
  private boolean buildFiles = false;
  private boolean buildTasks = false;
  private boolean buildHistory = false;
  private boolean buildLinkItem = false;
  private TicketLogList history = new TicketLogList();
  private FileItemList files = new FileItemList();
  private BlogPost linkBlogPost = null;
  private ProjectRating linkProjectRating = null;
  private Wiki linkWiki = null;
  private Topic linkTopic = null;
  private WikiComment linkWikiComment = null;
  private BlogPostComment linkBlogPostComment = null;
  private FileItem linkFileItem = null;
  private Meeting linkMeeting = null;

  private boolean foundTicketLinkObject = false;

  /**
   * Constructor for the Ticket object, creates an empty Ticket
   *
   * @since 1.0
   */
  public Ticket() {
  }


  /**
   * Constructor for the Ticket object
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  public Ticket(ResultSet rs) throws SQLException {
    buildRecord(rs);
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @param id Description of Parameter
   * @throws SQLException Description of Exception
   */
  public Ticket(Connection db, int id) throws SQLException {
    setBuildFiles(true);
    queryRecord(db, id);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void queryRecord(Connection db, int id) throws SQLException {
    if (id == -1) {
      throw new SQLException("Invalid Ticket Number");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT t.*, " +
            "tp.description AS ticpri, " +
            "ts.description AS ticsev, " +
            "tc.description AS catname, " +
            "tc1.description AS subcatname1, " +
            "tc2.description AS subcatname2, " +
            "tc3.description AS subcatname3, " +
            "lu_ts.description AS sourcename, " +
            "tlp.project_id " +
            "FROM ticket t " +
            "LEFT JOIN ticket_priority tp ON (t.pri_code = tp.code) " +
            "LEFT JOIN ticket_severity ts ON (t.scode = ts.code) " +
            "LEFT JOIN ticket_category tc ON (t.cat_code = tc.id) " +
            "LEFT JOIN ticket_category tc1 ON (t.subcat_code1 = tc1.id) " +
            "LEFT JOIN ticket_category tc2 ON (t.subcat_code2 = tc2.id) " +
            "LEFT JOIN ticket_category tc3 ON (t.subcat_code3 = tc3.id) " +
            "LEFT JOIN lookup_ticketsource lu_ts ON (t.source_code = lu_ts.code) " +
            "LEFT JOIN ticketlink_project tlp ON (t.ticketid = tlp.ticket_id) " +
            "WHERE t.ticketid = ? ");
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      buildRecord(rs);
    }
    rs.close();
    pst.close();
    if (this.id == -1) {
      throw new SQLException("Ticket not found");
    }
    /*
     *  if (this.getContactId() > 0 && checkContactRecord(db, this.getContactId())) {
     *  thisContact = new Contact(db, this.getContactId());
     *  } else {
     *  thisContact = null;
     *  }
     */
    if (buildHistory) {
      this.buildHistory(db);
    }
    if (buildFiles) {
      this.buildFiles(db);
    }
    if (buildLinkItem) {
      this.buildLinkItem(db);
    }
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildHistory(Connection db) throws SQLException {
    history.clear();
    history.setTicketId(this.getId());
    history.buildList(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildFiles(Connection db) throws SQLException {
    files.clear();
    files.setLinkModuleId(Constants.PROJECT_TICKET_FILES);
    files.setLinkItemId(this.getId());
    files.buildList(db);
  }


  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @param id Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean checkContactRecord(Connection db, int id) throws SQLException {
    boolean contactFound = false;
    if (id != -1) {
      PreparedStatement pst = db.prepareStatement(
          "SELECT contact_id from contact c " +
              "WHERE c.contact_id = ? ");
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();
      if (rs.next()) {
        contactFound = true;
      }
      rs.close();
      pst.close();
    }
    return contactFound;
  }


  /**
   * Sets the closed attribute of the Ticket object
   *
   * @param closed The new closed value
   */
  public void setClosed(Timestamp closed) {
    this.closed = closed;
  }


  /**
   * Sets the closed attribute of the Ticket object
   *
   * @param tmp The new closed value
   */
  public void setClosed(String tmp) {
    this.closed = DateUtils.parseTimestampString(tmp);
  }


  /**
   * Sets the ThisContact attribute of the Ticket object
   *
   * @param thisContact The new ThisContact value
   */
  public void setThisContact(Object thisContact) {
    this.thisContact = thisContact;
  }


  /**
   * Gets the companyEnabled attribute of the Ticket object
   *
   * @return The companyEnabled value
   */
  public boolean getCompanyEnabled() {
    return companyEnabled;
  }


  /**
   * Sets the companyEnabled attribute of the Ticket object
   *
   * @param companyEnabled The new companyEnabled value
   */
  public void setCompanyEnabled(boolean companyEnabled) {
    this.companyEnabled = companyEnabled;
  }

  public String getInsertMembers() {
    return insertMembers;
  }

  public void setInsertMembers(String insertMembers) {
    this.insertMembers = insertMembers;
  }

  public String getDeleteMembers() {
    return deleteMembers;
  }

  public void setDeleteMembers(String deleteMembers) {
    this.deleteMembers = deleteMembers;
  }

  public String getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(String attachmentList) {
    this.attachmentList = attachmentList;
  }

  /**
   * Sets the Newticketlogentry attribute of the Ticket object
   *
   * @param newticketlogentry The new Newticketlogentry value
   */
  public void setNewticketlogentry(String newticketlogentry) {
    this.comment = newticketlogentry;
  }


  /**
   * Sets the AssignedTo attribute of the Ticket object
   *
   * @param assignedTo The new AssignedTo value
   */
  public void setAssignedTo(int assignedTo) {
    this.assignedTo = assignedTo;
  }


  /**
   * Sets the SubCat1 attribute of the Ticket object
   *
   * @param tmp The new SubCat1 value
   */
  public void setSubCat1(int tmp) {
    this.subCat1 = tmp;
  }


  /**
   * Sets the SubCat2 attribute of the Ticket object
   *
   * @param tmp The new SubCat2 value
   */
  public void setSubCat2(int tmp) {
    this.subCat2 = tmp;
  }


  /**
   * Sets the SourceName attribute of the Ticket object
   *
   * @param sourceName The new SourceName value
   */
  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }


  /**
   * Sets the entered attribute of the Ticket object
   *
   * @param tmp The new entered value
   */
  public void setEntered(Timestamp tmp) {
    this.entered = tmp;
  }


  /**
   * Sets the modified attribute of the Ticket object
   *
   * @param tmp The new modified value
   */
  public void setModified(Timestamp tmp) {
    this.modified = tmp;
  }


  /**
   * Sets the entered attribute of the Ticket object
   *
   * @param tmp The new entered value
   */
  public void setEntered(String tmp) {
    this.entered = DateUtils.parseTimestampString(tmp);
  }


  /**
   * Sets the modified attribute of the Ticket object
   *
   * @param tmp The new modified value
   */
  public void setModified(String tmp) {
    this.modified = DateUtils.parseTimestampString(tmp);
  }


  /**
   * Sets the SubCat3 attribute of the Ticket object
   *
   * @param tmp The new SubCat3 value
   */
  public void setSubCat3(int tmp) {
    this.subCat3 = tmp;
  }


  /**
   * Sets the SubCat1 attribute of the Ticket object
   *
   * @param tmp The new SubCat1 value
   */
  public void setSubCat1(String tmp) {
    this.subCat1 = Integer.parseInt(tmp);
  }


  /**
   * Sets the SubCat2 attribute of the Ticket object
   *
   * @param tmp The new SubCat2 value
   */
  public void setSubCat2(String tmp) {
    this.subCat2 = Integer.parseInt(tmp);
  }


  /**
   * Sets the SubCat3 attribute of the Ticket object
   *
   * @param tmp The new SubCat3 value
   */
  public void setSubCat3(String tmp) {
    this.subCat3 = Integer.parseInt(tmp);
  }


  /**
   * Sets the AssignedTo attribute of the Ticket object
   *
   * @param assignedTo The new AssignedTo value
   */
  public void setAssignedTo(String assignedTo) {
    this.assignedTo = Integer.parseInt(assignedTo);
  }


  /**
   * Sets the DepartmentName attribute of the Ticket object
   *
   * @param departmentName The new DepartmentName value
   */
  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }


  /**
   * Sets the CloseIt attribute of the Ticket object
   *
   * @param closeIt The new CloseIt value
   */
  public void setCloseIt(boolean closeIt) {
    this.closeIt = closeIt;
  }


  public void setReadyForClose(boolean tmp) {
    this.readyForClose = tmp;
  }

  public void setReadyForClose(String tmp) {
    this.readyForClose = DatabaseUtils.parseBoolean(tmp);
  }


  public boolean getReadyForClose() {
    return readyForClose;
  }

  /**
   * Sets the closeNow attribute of the Ticket object
   *
   * @param tmp The new closeNow value
   */
  public void setCloseNow(String tmp) {
    this.closeIt = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the SeverityName attribute of the Ticket object
   *
   * @param severityName The new SeverityName value
   */
  public void setSeverityName(String severityName) {
    this.severityName = severityName;
  }


  /**
   * Sets the ErrorMessage attribute of the Ticket object
   *
   * @param tmp The new ErrorMessage value
   */
  public void setErrorMessage(String tmp) {
    this.errorMessage = tmp;
  }


  /**
   * Sets the campaignId attribute of the Ticket object
   *
   * @param tmp The new campaignId value
   */
  public void setCampaignId(int tmp) {
    this.campaignId = tmp;
  }


  /**
   * Sets the campaignId attribute of the Ticket object
   *
   * @param tmp The new campaignId value
   */
  public void setCampaignId(String tmp) {
    this.campaignId = Integer.parseInt(tmp);
  }


  /**
   * Sets the History attribute of the Ticket object
   *
   * @param history The new History value
   */
  public void setHistory(TicketLogList history) {
    this.history = history;
  }


  /**
   * Sets the files attribute of the Ticket object
   *
   * @param tmp The new files value
   */
  public void setFiles(FileItemList tmp) {
    this.files = tmp;
  }


  /**
   * Sets the Id attribute of the Ticket object
   *
   * @param tmp The new Id value
   */
  public void setId(int tmp) {
    this.id = tmp;
    history.setTicketId(tmp);
  }


  /**
   * Sets the Id attribute of the Ticket object
   *
   * @param tmp The new Id value
   */
  public void setId(String tmp) {
    this.setId(Integer.parseInt(tmp));
  }


  /**
   * Sets the CompanyName attribute of the Ticket object
   *
   * @param companyName The new CompanyName value
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }


  /**
   * Sets the OrgId attribute of the Ticket object
   *
   * @param tmp The new OrgId value
   */
  public void setOrgId(int tmp) {
    this.orgId = tmp;
  }


  /**
   * Sets the OrgId attribute of the Ticket object
   *
   * @param tmp The new OrgId value
   */
  public void setOrgId(String tmp) {
    this.orgId = Integer.parseInt(tmp);
  }


  /**
   * Sets the buildFiles attribute of the Ticket object
   *
   * @param buildFiles The new buildFiles value
   */
  public void setBuildFiles(boolean buildFiles) {
    this.buildFiles = buildFiles;
  }


  /**
   * Sets the buildHistory attribute of the Ticket object
   *
   * @param buildHistory The new buildHistory value
   */
  public void setBuildHistory(boolean buildHistory) {
    this.buildHistory = buildHistory;
  }


  /**
   * Gets the buildFiles attribute of the Ticket object
   *
   * @return The buildFiles value
   */
  public boolean getBuildFiles() {
    return buildFiles;
  }


  /**
   * Gets the buildHistory attribute of the Ticket object
   *
   * @return The buildHistory value
   */
  public boolean getBuildHistory() {
    return buildHistory;
  }


  /**
   * Gets the hasEnabledOwnerAccount attribute of the Ticket object
   *
   * @return The hasEnabledOwnerAccount value
   */
  public boolean getHasEnabledOwnerAccount() {
    return hasEnabledOwnerAccount;
  }


  /**
   * Sets the hasEnabledOwnerAccount attribute of the Ticket object
   *
   * @param hasEnabledOwnerAccount The new hasEnabledOwnerAccount value
   */
  public void setHasEnabledOwnerAccount(boolean hasEnabledOwnerAccount) {
    this.hasEnabledOwnerAccount = hasEnabledOwnerAccount;
  }


  /**
   * Sets the projectId attribute of the Ticket object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the Ticket object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Gets the projectId attribute of the Ticket object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Sets the projectTicketCount attribute of the Ticket object
   *
   * @param tmp The new projectTicketCount value
   */
  public void setProjectTicketCount(int tmp) {
    this.projectTicketCount = tmp;
  }


  /**
   * Sets the projectTicketCount attribute of the Ticket object
   *
   * @param tmp The new projectTicketCount value
   */
  public void setProjectTicketCount(String tmp) {
    this.projectTicketCount = Integer.parseInt(tmp);
  }


  /**
   * Gets the projectTicketCount attribute of the Ticket object
   *
   * @return The projectTicketCount value
   */
  public int getProjectTicketCount() {
    return projectTicketCount;
  }


  /**
   * Sets the ContactId attribute of the Ticket object
   *
   * @param tmp The new ContactId value
   */
  public void setContactId(int tmp) {
    this.contactId = tmp;
  }


  /**
   * Sets the PriorityName attribute of the Ticket object
   *
   * @param priorityName The new PriorityName value
   */
  public void setPriorityName(String priorityName) {
    this.priorityName = priorityName;
  }


  /**
   * Sets the ContactId attribute of the Ticket object
   *
   * @param tmp The new ContactId value
   */
  public void setContactId(String tmp) {
    this.contactId = Integer.parseInt(tmp);
  }


  /**
   * Sets the AgeOf attribute of the Ticket object
   *
   * @param ageOf The new AgeOf value
   */
  public void setAgeDays(int ageOf) {
    this.ageDays = ageOf;
  }


  /**
   * Sets the CategoryName attribute of the Ticket object
   *
   * @param categoryName The new CategoryName value
   */
  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public void setSubCategoryName1(String subCategoryName1) {
    this.subCategoryName1 = subCategoryName1;
  }

  public void setSubCategoryName2(String subCategoryName2) {
    this.subCategoryName2 = subCategoryName2;
  }

  public void setSubCategoryName3(String subCategoryName3) {
    this.subCategoryName3 = subCategoryName3;
  }

  /**
   * Sets the Problem attribute of the Ticket object
   *
   * @param tmp The new Problem value
   */
  public void setProblem(String tmp) {
    this.problem = tmp;
  }


  /**
   * Sets the location attribute of the Ticket object
   *
   * @param tmp The new location value
   */
  public void setLocation(String tmp) {
    this.location = tmp;
  }


  /**
   * Sets the Comment attribute of the Ticket object
   *
   * @param tmp The new Comment value
   */
  public void setComment(String tmp) {
    this.comment = tmp;
  }


  /**
   * Sets the estimatedResolutionDate attribute of the Ticket object
   *
   * @param tmp The new estimatedResolutionDate value
   */
  public void setEstimatedResolutionDate(Timestamp tmp) {
    this.estimatedResolutionDate = tmp;
  }


  /**
   * Sets the estimatedResolutionDate attribute of the Ticket object
   *
   * @param tmp The new estimatedResolutionDate value
   */
  public void setEstimatedResolutionDate(String tmp) {
    this.estimatedResolutionDate = DatabaseUtils.parseTimestamp(tmp);
  }


  /**
   * Sets the cause attribute of the Ticket object
   *
   * @param tmp The new cause value
   */
  public void setCause(String tmp) {
    this.cause = tmp;
  }


  /**
   * Sets the Solution attribute of the Ticket object
   *
   * @param tmp The new Solution value
   */
  public void setSolution(String tmp) {
    this.solution = tmp;
  }


  /**
   * Sets the PriorityCode attribute of the Ticket object
   *
   * @param tmp The new PriorityCode value
   */
  public void setPriorityCode(int tmp) {
    this.priorityCode = tmp;
  }


  /**
   * Sets the PriorityCode attribute of the Ticket object
   *
   * @param tmp The new PriorityCode value
   */
  public void setPriorityCode(String tmp) {
    this.priorityCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the LevelCode attribute of the Ticket object
   *
   * @param tmp The new LevelCode value
   */
  public void setLevelCode(int tmp) {
    this.levelCode = tmp;
  }


  /**
   * Sets the levelCode attribute of the Ticket object
   *
   * @param tmp The new levelCode value
   */
  public void setLevelCode(String tmp) {
    this.levelCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the DepartmentCode attribute of the Ticket object
   *
   * @param tmp The new DepartmentCode value
   */
  public void setDepartmentCode(int tmp) {
    this.departmentCode = tmp;
  }


  /**
   * Sets the DepartmentCode attribute of the Ticket object
   *
   * @param tmp The new DepartmentCode value
   */
  public void setDepartmentCode(String tmp) {
    this.departmentCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the SourceCode attribute of the Ticket object
   *
   * @param tmp The new SourceCode value
   */
  public void setSourceCode(int tmp) {
    this.sourceCode = tmp;
  }


  /**
   * Sets the SourceCode attribute of the Ticket object
   *
   * @param tmp The new SourceCode value
   */
  public void setSourceCode(String tmp) {
    this.sourceCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the CatCode attribute of the Ticket object
   *
   * @param tmp The new CatCode value
   */
  public void setCatCode(int tmp) {
    this.catCode = tmp;
  }


  /**
   * Sets the CatCode attribute of the Ticket object
   *
   * @param tmp The new CatCode value
   */
  public void setCatCode(String tmp) {
    this.catCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the SeverityCode attribute of the Ticket object
   *
   * @param tmp The new SeverityCode value
   */
  public void setSeverityCode(int tmp) {
    this.severityCode = tmp;
  }


  /**
   * Sets the SeverityCode attribute of the Ticket object
   *
   * @param tmp The new SeverityCode value
   */
  public void setSeverityCode(String tmp) {
    this.severityCode = Integer.parseInt(tmp);
  }


  /**
   * Sets the EnteredBy attribute of the Ticket object
   *
   * @param tmp The new EnteredBy value
   */
  public void setEnteredBy(int tmp) {
    this.enteredBy = tmp;
  }


  /**
   * Sets the EnteredBy attribute of the Ticket object
   *
   * @param tmp The new EnteredBy value
   */
  public void setEnteredBy(String tmp) {
    this.enteredBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the ModifiedBy attribute of the Ticket object
   *
   * @param tmp The new ModifiedBy value
   */
  public void setModifiedBy(int tmp) {
    this.modifiedBy = tmp;
  }


  /**
   * Sets the ModifiedBy attribute of the Ticket object
   *
   * @param tmp The new ModifiedBy value
   */
  public void setModifiedBy(String tmp) {
    this.modifiedBy = Integer.parseInt(tmp);
  }


  /**
   * Sets the RequestItems attribute of the Ticket object
   *
   * @param request The new RequestItems value
   */
  public void setRequestItems(HttpServletRequest request) {
    history = new TicketLogList(request, this.getModifiedBy());
  }


  /**
   * Gets the closed attribute of the Ticket object
   *
   * @return The closed value
   */
  public Timestamp getClosed() {
    return closed;
  }


  /**
   * Gets the closed attribute of the Ticket object
   *
   * @return The closed value
   */
  public boolean isClosed() {
    return closed != null;
  }


  /**
   * Gets the paddedId attribute of the Ticket object
   *
   * @return The paddedId value
   */
  public String getPaddedId() {
    String padded = (String.valueOf(this.getId()));
    while (padded.length() < 6) {
      padded = "0" + padded;
    }
    return padded;
  }


  /**
   * Gets the entered attribute of the Ticket object
   *
   * @return The entered value
   */
  public Timestamp getEntered() {
    return entered;
  }


  /**
   * Gets the modified attribute of the Ticket object
   *
   * @return The modified value
   */
  public Timestamp getModified() {
    return modified;
  }


  /**
   * Gets the modifiedString attribute of the Ticket object
   *
   * @return The modifiedString value
   */
  public String getModifiedString() {
    String tmp = "";
    try {
      return DateFormat.getDateInstance(DateFormat.SHORT).format(modified);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the enteredString attribute of the Ticket object
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
   * Gets the modifiedDateTimeString attribute of the Ticket object
   *
   * @return The modifiedDateTimeString value
   */
  public String getModifiedDateTimeString() {
    String tmp = "";
    try {
      return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(modified);
    } catch (NullPointerException e) {
    }
    return tmp;
  }


  /**
   * Gets the ThisContact attribute of the Ticket object
   *
   * @return The ThisContact value
   */
  public Object getThisContact() {
    return thisContact;
  }


  /**
   * Gets the SourceName attribute of the Ticket object
   *
   * @return The SourceName value
   */
  public String getSourceName() {
    return sourceName;
  }


  /**
   * Gets the SubCat1 attribute of the Ticket object
   *
   * @return The SubCat1 value
   */
  public int getSubCat1() {
    return subCat1;
  }


  /**
   * Gets the SubCat2 attribute of the Ticket object
   *
   * @return The SubCat2 value
   */
  public int getSubCat2() {
    return subCat2;
  }


  /**
   * Gets the SubCat3 attribute of the Ticket object
   *
   * @return The SubCat3 value
   */
  public int getSubCat3() {
    return subCat3;
  }


  /**
   * Gets the Newticketlogentry attribute of the Ticket object
   *
   * @return The Newticketlogentry value
   */
  public String getNewticketlogentry() {
    return comment;
  }


  /**
   * Gets the AssignedTo attribute of the Ticket object
   *
   * @return The AssignedTo value
   */
  public int getAssignedTo() {
    return assignedTo;
  }


  /**
   * Gets the assigned attribute of the Ticket object
   *
   * @return The assigned value
   */
  public boolean isAssigned() {
    return (assignedTo > 0);
  }


  /**
   * Gets the CloseIt attribute of the Ticket object
   *
   * @return The CloseIt value
   */
  public boolean getCloseIt() {
    return closeIt;
  }


  /**
   * Gets the SeverityName attribute of the Ticket object
   *
   * @return The SeverityName value
   */
  public String getSeverityName() {
    return severityName;
  }


  /**
   * Gets the PriorityName attribute of the Ticket object
   *
   * @return The PriorityName value
   */
  public String getPriorityName() {
    return priorityName;
  }


  /**
   * Gets the DepartmentName attribute of the Ticket object
   *
   * @return The DepartmentName value
   */
  public String getDepartmentName() {
    return departmentName;
  }


  /**
   * Gets the campaignId attribute of the Ticket object
   *
   * @return The campaignId value
   */
  public int getCampaignId() {
    return campaignId;
  }


  /**
   * Gets the History attribute of the Ticket object
   *
   * @return The History value
   */
  public TicketLogList getHistory() {
    return history;
  }


  /**
   * Gets the files attribute of the Ticket object
   *
   * @return The files value
   */
  public FileItemList getFiles() {
    return files;
  }


  /**
   * Gets the AgeOf attribute of the Ticket object
   *
   * @return The AgeOf value
   */
  public String getAgeOf() {
    return ageDays + "d " + ageHours + "h";
  }


  /**
   * Gets the CategoryName attribute of the Ticket object
   *
   * @return The CategoryName value
   */
  public String getCategoryName() {
    return categoryName;
  }

  public String getSubCategoryName1() {
    return subCategoryName1;
  }

  public String getSubCategoryName2() {
    return subCategoryName2;
  }

  public String getSubCategoryName3() {
    return subCategoryName3;
  }

  /**
   * Gets the CompanyName attribute of the Ticket object
   *
   * @return The CompanyName value
   */
  public String getCompanyName() {
    return companyName;
  }


  /**
   * Gets the ErrorMessage attribute of the Ticket object
   *
   * @return The ErrorMessage value
   */
  public String getErrorMessage() {
    return errorMessage;
  }


  /**
   * Gets the Id attribute of the Ticket object
   *
   * @return The Id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the OrgId attribute of the Ticket object
   *
   * @return The OrgId value
   */
  public int getOrgId() {
    return orgId;
  }


  /**
   * Gets the ContactId attribute of the Ticket object
   *
   * @return The ContactId value
   */
  public int getContactId() {
    return contactId;
  }


  /**
   * Gets the Problem attribute of the Ticket object
   *
   * @return The Problem value
   */
  public String getProblem() {
    return problem;
  }


  /**
   * Gets the location attribute of the Ticket object
   *
   * @return The location value
   */
  public String getLocation() {
    return location;
  }


  /**
   * Gets the problemHeader attribute of the Ticket object
   *
   * @return The problemHeader value
   */
  public String getProblemHeader() {
    if (problem.trim().length() > 100) {
      return (problem.substring(0, 100) + "...");
    } else {
      return getProblem();
    }
  }

  public int getRelatedId() {
    return relatedId;
  }

  public void setRelatedId(int relatedId) {
    this.relatedId = relatedId;
  }

  public void setRelatedId(String relatedId) {
    this.relatedId = Integer.parseInt(relatedId);
  }

  public int getCauseId() {
    return causeId;
  }

  public void setCauseId(int causeId) {
    this.causeId = causeId;
  }

  public void setCauseId(String causeId) {
    this.causeId = Integer.parseInt(causeId);
  }

  public int getResolutionId() {
    return resolutionId;
  }

  public void setResolutionId(int resolutionId) {
    this.resolutionId = resolutionId;
  }

  public void setResolutionId(String resolutionId) {
    this.resolutionId = Integer.parseInt(resolutionId);
  }

  public int getDefectId() {
    return defectId;
  }

  public void setDefectId(int defectId) {
    this.defectId = defectId;
  }

  public void setDefectId(String defectId) {
    this.defectId = Integer.parseInt(defectId);
  }

  public int getEscalationId() {
    return escalationId;
  }

  public void setEscalationId(int escalationId) {
    this.escalationId = escalationId;
  }

  public void setEscalationId(String escalationId) {
    this.escalationId = Integer.parseInt(escalationId);
  }

  public int getStateId() {
    return stateId;
  }

  public void setStateId(int stateId) {
    this.stateId = stateId;
  }

  public void setStateId(String stateId) {
    this.stateId = Integer.parseInt(stateId);
  }

  public int getReadCount() {
    return readCount;
  }

  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void checkEnabledOwnerAccount(Connection db) throws SQLException {
    if (this.getAssignedTo() == -1) {
      throw new SQLException("ID not specified for lookup.");
    }
    PreparedStatement pst = db.prepareStatement(
        "SELECT * " +
            "FROM users " +
            "WHERE user_id = ? AND enabled = ? ");
    pst.setInt(1, this.getAssignedTo());
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
   * Gets the Comment attribute of the Ticket object
   *
   * @return The Comment value
   */
  public String getComment() {
    return comment;
  }


  /**
   * Gets the estimatedResolutionDate attribute of the Ticket object
   *
   * @return The estimatedResolutionDate value
   */
  public Timestamp getEstimatedResolutionDate() {
    return estimatedResolutionDate;
  }


  /**
   * Gets the cause attribute of the Ticket object
   *
   * @return The cause value
   */
  public String getCause() {
    return cause;
  }


  /**
   * Gets the Solution attribute of the Ticket object
   *
   * @return The Solution value
   */
  public String getSolution() {
    return solution;
  }


  /**
   * Gets the PriorityCode attribute of the Ticket object
   *
   * @return The PriorityCode value
   */
  public int getPriorityCode() {
    return priorityCode;
  }


  /**
   * Gets the LevelCode attribute of the Ticket object
   *
   * @return The LevelCode value
   */
  public int getLevelCode() {
    return levelCode;
  }


  /**
   * Gets the DepartmentCode attribute of the Ticket object
   *
   * @return The DepartmentCode value
   */
  public int getDepartmentCode() {
    return departmentCode;
  }


  /**
   * Gets the SourceCode attribute of the Ticket object
   *
   * @return The SourceCode value
   */
  public int getSourceCode() {
    return sourceCode;
  }


  /**
   * Gets the CatCode attribute of the Ticket object
   *
   * @return The CatCode value
   */
  public int getCatCode() {
    return catCode;
  }


  /**
   * Gets the SeverityCode attribute of the Ticket object
   *
   * @return The SeverityCode value
   */
  public int getSeverityCode() {
    return severityCode;
  }


  /**
   * Gets the EnteredBy attribute of the Ticket object
   *
   * @return The EnteredBy value
   */
  public int getEnteredBy() {
    return enteredBy;
  }


  /**
   * Gets the ModifiedBy attribute of the Ticket object
   *
   * @return The ModifiedBy value
   */
  public int getModifiedBy() {
    return modifiedBy;
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @throws SQLException Description of Exception
   */
  public void buildContactInformation(Connection db) throws SQLException {
    if (contactId > -1) {
      //thisContact = new Contact(db, contactId + "");
    }
  }


  /**
   * Inserts this ticket into the database, and populates this Id. Inserts
   * required fields, then calls update to finish record entry
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean insert(Connection db) throws SQLException {
    if (!isValid(db)) {
      return false;
    }
    StringBuffer sql = new StringBuffer();
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      if (projectId > -1 && projectTicketCount == -1) {
        updateProjectTicketCount(db, projectId);
      }
      sql.append(
          "INSERT INTO ticket (contact_id, problem, pri_code, " +
              "department_code, cat_code, scode, org_id, key_count, link_project_id, link_module_id, link_item_id, ");
      if (entered != null) {
        sql.append("entered, ");
      }
      if (modified != null) {
        sql.append("modified, ");
      }
      sql.append("enteredby, modifiedby ) ");
      sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
      if (entered != null) {
        sql.append("?, ");
      }
      if (modified != null) {
        sql.append("?, ");
      }
      sql.append("?, ?) ");
      int i = 0;
      PreparedStatement pst = db.prepareStatement(sql.toString());
      DatabaseUtils.setInt(pst, ++i, this.getContactId());
      pst.setString(++i, this.getProblem());
      if (this.getPriorityCode() > 0) {
        pst.setInt(++i, this.getPriorityCode());
      } else {
        pst.setNull(++i, java.sql.Types.INTEGER);
      }
      if (this.getDepartmentCode() > 0) {
        pst.setInt(++i, this.getDepartmentCode());
      } else {
        pst.setNull(++i, java.sql.Types.INTEGER);
      }
      if (this.getCatCode() > 0) {
        pst.setInt(++i, this.getCatCode());
      } else {
        pst.setNull(++i, java.sql.Types.INTEGER);
      }
      if (this.getSeverityCode() > 0) {
        pst.setInt(++i, this.getSeverityCode());
      } else {
        pst.setNull(++i, java.sql.Types.INTEGER);
      }
      DatabaseUtils.setInt(pst, ++i, orgId);
      DatabaseUtils.setInt(pst, ++i, projectTicketCount);
      DatabaseUtils.setInt(pst, ++i, linkProjectId);
      DatabaseUtils.setInt(pst, ++i, linkModuleId);
      DatabaseUtils.setInt(pst, ++i, linkItemId);
      if (entered != null) {
        pst.setTimestamp(++i, entered);
      }
      if (modified != null) {
        pst.setTimestamp(++i, modified);
      }
      pst.setInt(++i, this.getEnteredBy());
      pst.setInt(++i, this.getModifiedBy());
      pst.execute();
      pst.close();
      id = DatabaseUtils.getCurrVal(db, "ticket_ticketid_seq", -1);
      //Update the rest of the fields
      if (this.getEntered() == null) {
        this.update(db);
      } else {
        this.update(db, true);
      }
      if (projectId > -1) {
        insertProjectLink(db, projectId);
      }
      if (autoCommit) {
        db.commit();
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
   * Update this ticket in the database
   *
   * @param db       Description of Parameter
   * @param override Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int update(Connection db, boolean override) throws SQLException {
    int resultCount = 0;
    if (!isValid(db)) {
      return -1;
    }
    PreparedStatement pst = null;
    StringBuffer sql = new StringBuffer();
    sql.append(
        "UPDATE ticket " +
            "SET department_code = ?, pri_code = ?, scode = ?, " +
            "cat_code = ?, assigned_to = ?, " +
            "subcat_code1 = ?, subcat_code2 = ?, subcat_code3 = ?, " +
            "source_code = ?, contact_id = ?, problem = ?, ");
    sql.append("modified = " + DatabaseUtils.getCurrentTimestamp(db) + ", modifiedby = ?, ");
    if (this.getCloseIt()) {
      sql.append("closed = " + DatabaseUtils.getCurrentTimestamp(db) + ", ");
    } else {
      if (closed != null) {
        sql.append("closed = ?, ");
      }
    }
    sql.append("solution = ?, est_resolution_date = ?, ");
    sql.append("cause_id = ?, resolution_id = ?, defect_id = ?, escalation_id = ?, state_id = ?, related_id = ?, ready_for_close = ? ");
    sql.append("WHERE ticketid = ? ");
    if (!override) {
      sql.append("AND modified = ? ");
    }
    int i = 0;
    pst = db.prepareStatement(sql.toString());
    if (this.getDepartmentCode() > 0) {
      pst.setInt(++i, this.getDepartmentCode());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (this.getPriorityCode() > 0) {
      pst.setInt(++i, this.getPriorityCode());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (this.getSeverityCode() > 0) {
      pst.setInt(++i, this.getSeverityCode());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (this.getCatCode() > 0) {
      pst.setInt(++i, this.getCatCode());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (assignedTo > 0) {
      pst.setInt(++i, assignedTo);
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (this.getSubCat1() > 0) {
      pst.setInt(++i, this.getSubCat1());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (this.getSubCat2() > 0) {
      pst.setInt(++i, this.getSubCat2());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (this.getSubCat3() > 0) {
      pst.setInt(++i, this.getSubCat3());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    if (this.getSourceCode() > 0) {
      pst.setInt(++i, this.getSourceCode());
    } else {
      pst.setNull(++i, java.sql.Types.INTEGER);
    }
    DatabaseUtils.setInt(pst, ++i, this.getContactId());
    pst.setString(++i, this.getProblem());
    pst.setInt(++i, this.getModifiedBy());
    if (!this.getCloseIt() && closed != null) {
      pst.setTimestamp(++i, closed);
    }
    pst.setString(++i, this.getSolution());
    DatabaseUtils.setTimestamp(pst, ++i, estimatedResolutionDate);
    DatabaseUtils.setInt(pst, ++i, causeId);
    DatabaseUtils.setInt(pst, ++i, resolutionId);
    DatabaseUtils.setInt(pst, ++i, defectId);
    DatabaseUtils.setInt(pst, ++i, escalationId);
    DatabaseUtils.setInt(pst, ++i, stateId);
    DatabaseUtils.setInt(pst, ++i, relatedId);
    pst.setBoolean(++i, readyForClose);
    pst.setInt(++i, id);
    if (!override) {
      pst.setTimestamp(++i, this.getModified());
    }
    resultCount = pst.executeUpdate();
    pst.close();
    if (this.getCloseIt()) {
      TicketLog thisEntry = new TicketLog();
      thisEntry.setEnteredBy(this.getModifiedBy());
      thisEntry.setDepartmentCode(this.getDepartmentCode());
      thisEntry.setAssignedTo(this.getAssignedTo());
      thisEntry.setPriorityCode(this.getPriorityCode());
      thisEntry.setSeverityCode(this.getSeverityCode());
      thisEntry.setTicketId(this.getId());
      thisEntry.setClosed(true);
      thisEntry.process(db, this.getId(), this.getEnteredBy(), this.getModifiedBy());
    }
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param db       Description of the Parameter
   * @param newOwner Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public boolean reassign(Connection db, int newOwner) throws SQLException {
    int result = -1;
    this.setAssignedTo(newOwner);
    result = this.update(db);
    if (result == -1) {
      return false;
    }
    return true;
  }


  /**
   * Reopens a ticket so that it can be modified again
   *
   * @param db Description of the Parameter
   * @return Description of the Return Value
   * @throws SQLException Description of the Exception
   */
  public int reopen(Connection db) throws SQLException {
    int resultCount = 0;
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      PreparedStatement pst = null;
      String sql =
          "UPDATE ticket " +
              "SET closed = ?, modified = " + DatabaseUtils.getCurrentTimestamp(db) + ", modifiedby = ? " +
              "WHERE ticketid = ? ";
      int i = 0;
      pst = db.prepareStatement(sql);
      pst.setNull(++i, java.sql.Types.TIMESTAMP);
      pst.setInt(++i, this.getModifiedBy());
      pst.setInt(++i, this.getId());
      resultCount = pst.executeUpdate();
      pst.close();
      //Update the ticket log
      TicketLog thisEntry = new TicketLog();
      thisEntry.setEnteredBy(this.getModifiedBy());
      thisEntry.setDepartmentCode(this.getDepartmentCode());
      thisEntry.setAssignedTo(this.getAssignedTo());
      thisEntry.setPriorityCode(this.getPriorityCode());
      thisEntry.setSeverityCode(this.getSeverityCode());
      thisEntry.setEntryText(this.getComment());
      thisEntry.setTicketId(this.getId());
      thisEntry.process(db, this.getId(), this.getEnteredBy(), this.getModifiedBy());
      if (autoCommit) {
        db.commit();
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
    return resultCount;
  }


  /**
   * Description of the Method
   *
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public boolean delete(Connection db, String baseFilePath) throws SQLException {
    if (this.getId() == -1) {
      throw new SQLException("Ticket ID not specified.");
    }
    boolean commit = db.getAutoCommit();
    try {
      if (commit) {
        db.setAutoCommit(false);
      }

      //Delete any documents
      FileItemList fileList = new FileItemList();
      fileList.setLinkModuleId(Constants.PROJECT_TICKET_FILES);
      fileList.setLinkItemId(this.getId());
      fileList.buildList(db);
      fileList.delete(db, baseFilePath);

      //Delete the ticket log
      PreparedStatement pst = db.prepareStatement(
          "DELETE FROM ticketlog " +
              "WHERE ticketid = ?");
      pst.setInt(1, this.getId());
      pst.execute();
      //Delete the related project link
      pst = db.prepareStatement(
          "DELETE FROM ticketlink_project " +
              "WHERE ticket_id = ? ");
      pst.setInt(1, this.getId());
      pst.execute();
      // Delete the distribution list
      pst = db.prepareStatement(
          "DELETE FROM ticket_contacts " +
              "WHERE ticketid = ? ");
      pst.setInt(1, this.getId());
      pst.execute();

      Viewing.delete(db, id, TABLE, PRIMARY_KEY);

      //Delete the ticket
      pst = db.prepareStatement(
          "DELETE FROM ticket " +
              "WHERE ticketid = ? ");
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
      throw new SQLException(e.getMessage());
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
   * @param db Description of Parameter
   * @return Description of the Returned Value
   * @throws SQLException Description of Exception
   */
  public int update(Connection db) throws SQLException {
    int i = -1;
    if (!isValid(db)) {
      return -1;
    }
    boolean autoCommit = db.getAutoCommit();
    try {
      if (autoCommit) {
        db.setAutoCommit(false);
      }
      if (entered != null) {
        i = this.update(db, false);
      } else {
        i = this.update(db, true);
      }
      TicketLog thisEntry = new TicketLog();
      thisEntry.setEnteredBy(this.getModifiedBy());
      thisEntry.setDepartmentCode(this.getDepartmentCode());
      thisEntry.setAssignedTo(this.getAssignedTo());
      thisEntry.setEntryText(this.getComment());
      thisEntry.setTicketId(this.getId());
      thisEntry.setPriorityCode(this.getPriorityCode());
      thisEntry.setSeverityCode(this.getSeverityCode());
      if (this.getCloseIt()) {
        thisEntry.setClosed(true);
      }
      history.add(thisEntry);
      Iterator hist = history.iterator();
      while (hist.hasNext()) {
        TicketLog thisLog = (TicketLog) hist.next();
        thisLog.process(db, this.getId(), this.getEnteredBy(), this.getModifiedBy());
      }
      // Check for a change in the distribution list
      if (insertMembers != null || deleteMembers != null) {
        TicketContactList.updateContacts(db, this.getModifiedBy(), id, insertMembers, deleteMembers);
      }
      if (attachmentList != null) {
        FileItemList.convertTempFiles(db, Constants.PROJECT_TICKET_FILES, this.getModifiedBy(), id, attachmentList);
      }
      if (autoCommit) {
        db.commit();
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
    return i;
  }


  /**
   * Gets the Valid attribute of the Ticket object
   *
   * @param db Description of Parameter
   * @return The Valid value
   * @throws SQLException Description of Exception
   */
  protected boolean isValid(Connection db) throws SQLException {
    errors.clear();
    if (!StringUtils.hasText(problem)) {
      errors.put("problemError", "An issue is required");
    }
    if (readyForClose && (!StringUtils.hasText(solution))) {
      errors.put("readyForClose", "A solution is required when closing a ticket");
    }
    if (closeIt && (!StringUtils.hasText(solution))) {
      errors.put("closedError", "A solution is required when closing a ticket");
    }
    if (contactId == -1) {
      errors.put("contactIdError", "You must associate a Contact with a Ticket");
    }
    return !hasErrors();
  }


  /**
   * Description of the Method
   *
   * @param rs Description of Parameter
   * @throws SQLException Description of Exception
   */
  protected void buildRecord(ResultSet rs) throws SQLException {
    //ticket table
    this.setId(rs.getInt("ticketid"));
    orgId = DatabaseUtils.getInt(rs, "org_id");
    contactId = DatabaseUtils.getInt(rs, "contact_id");
    problem = rs.getString("problem");
    entered = rs.getTimestamp("entered");
    enteredBy = rs.getInt("enteredby");
    modified = rs.getTimestamp("modified");
    modifiedBy = rs.getInt("modifiedby");
    closed = rs.getTimestamp("closed");
    if (!rs.wasNull()) {
      closeIt = true;
    }
    priorityCode = DatabaseUtils.getInt(rs, "pri_code");
    levelCode = DatabaseUtils.getInt(rs, "level_code");
    departmentCode = DatabaseUtils.getInt(rs, "department_code");
    sourceCode = DatabaseUtils.getInt(rs, "source_code");
    catCode = DatabaseUtils.getInt(rs, "cat_code", 0);
    subCat1 = DatabaseUtils.getInt(rs, "subcat_code1", 0);
    subCat2 = DatabaseUtils.getInt(rs, "subcat_code2", 0);
    subCat3 = DatabaseUtils.getInt(rs, "subcat_code3", 0);
    assignedTo = DatabaseUtils.getInt(rs, "assigned_to");
    solution = rs.getString("solution");
    severityCode = DatabaseUtils.getInt(rs, "scode");
    location = rs.getString("location");
    //assignedDate = rs.getTimestamp("assigned_date");
    estimatedResolutionDate = rs.getTimestamp("est_resolution_date");
    //resolutionDate = rs.getTimestamp("resolution_date");
    cause = rs.getString("cause");
    projectTicketCount = rs.getInt("key_count");
    causeId = DatabaseUtils.getInt(rs, "cause_id");
    resolutionId = DatabaseUtils.getInt(rs, "resolution_id");
    defectId = DatabaseUtils.getInt(rs, "defect_id");
    escalationId = DatabaseUtils.getInt(rs, "escalation_id");
    stateId = DatabaseUtils.getInt(rs, "state_id");
    relatedId = DatabaseUtils.getInt(rs, "related_id");
    readyForClose = rs.getBoolean("ready_for_close");
    readCount = rs.getInt("read_count");
    readDate = rs.getTimestamp("read_date");
    linkProjectId = DatabaseUtils.getInt(rs, "link_project_id");
    linkModuleId = DatabaseUtils.getInt(rs, "link_module_id");
    linkItemId = DatabaseUtils.getInt(rs, "link_item_id");

    /*
     *  /organization table
     *  companyName = rs.getString("orgname");
     *  companyEnabled = rs.getBoolean("orgenabled");
     *  /lookup_department table
     *  departmentName = rs.getString("dept");
     */
    //ticket_priority table
    priorityName = rs.getString("ticpri");

    //ticket_severity table
    severityName = rs.getString("ticsev");

    //ticket_category table
    categoryName = rs.getString("catname");
    subCategoryName1 = rs.getString("subcatname1");
    subCategoryName2 = rs.getString("subcatname2");
    subCategoryName3 = rs.getString("subcatname3");

    //lookup_ticket_source table
    sourceName = rs.getString("sourcename");

    //ticketlink_project
    projectId = DatabaseUtils.getInt(rs, "project_id");

    //Calculations
    if (entered != null) {
      if (closed != null) {
        //float ageCheck = ((closed.getTime() - entered.getTime()) / 86400000);
        //ageDays = java.lang.Math.round(ageCheck);
        float ageCheck = ((closed.getTime() - entered.getTime()) / 3600000);
        int totalHours = java.lang.Math.round(ageCheck);
        ageDays = java.lang.Math.round(totalHours / 24);
        ageHours = java.lang.Math.round(totalHours - (24 * ageDays));
      } else {
        //float ageCheck = ((System.currentTimeMillis() - entered.getTime()) / 86400000);
        //ageDays = java.lang.Math.round(ageCheck);
        float ageCheck = ((System.currentTimeMillis() - entered.getTime()) / 3600000);
        int totalHours = java.lang.Math.round(ageCheck);
        ageDays = java.lang.Math.round(totalHours / 24);
        ageHours = java.lang.Math.round(totalHours - (24 * ageDays));
      }
    }
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean hasFiles() {
    return (files != null && files.size() > 0);
  }


  /**
   * Gets the properties that are TimeZone sensitive for auto-populating
   *
   * @return The timeZoneParams value
   */
  public static ArrayList getTimeZoneParams() {
    ArrayList thisList = new ArrayList();
    //thisList.add("assignedDate");
    thisList.add("estimatedResolutionDate");
    //thisList.add("resolutionDate");
    //thisList.add("contractStartDate");
    //thisList.add("contractEndDate");
    return thisList;
  }


  /**
   * Description of the Method
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  private void insertProjectLink(Connection db, int projectId) throws SQLException {
    String sql = "INSERT INTO ticketlink_project " +
        "(ticket_id, project_id) " +
        "VALUES (?, ?) ";
    int i = 0;
    PreparedStatement pst = db.prepareStatement(sql);
    pst.setInt(++i, this.getId());
    pst.setInt(++i, projectId);
    pst.execute();
    pst.close();
  }


  /**
   * Each ticket in a project has its own unique count
   *
   * @param db        Description of the Parameter
   * @param projectId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public synchronized void updateProjectTicketCount(Connection db, int projectId) throws SQLException {
    int i = 0;
    // Get a new project id value
    PreparedStatement pst = db.prepareStatement(
        "UPDATE project_ticket_count " +
            "SET key_count = key_count + 1 " +
            "WHERE project_id = ? ");
    pst.setInt(++i, projectId);
    pst.execute();
    pst.close();
    // Retrieve the new value
    i = 0;
    pst = db.prepareStatement(
        "SELECT key_count " +
            "FROM project_ticket_count " +
            "WHERE project_id = ? ");
    pst.setInt(++i, projectId);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      projectTicketCount = rs.getInt("key_count");
    }
    rs.close();
    pst.close();
  }

  public void queryProjectTicketCount(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "SELECT key_count " +
            "FROM ticket " +
            "WHERE ticketid = ? "
    );
    pst.setInt(1, id);
    ResultSet rs = pst.executeQuery();
    if (rs.next()) {
      projectTicketCount = rs.getInt("key_count");
    }
    rs.close();
    pst.close();
  }

  public void addToInsertMembers(int userId) {
    if (insertMembers == null) {
      insertMembers = String.valueOf(userId);
    } else {
      System.out.println("insertMembers=" + insertMembers);
      insertMembers += "|" + String.valueOf(userId);
    }
  }

  public void addToDeleteMembers(int userId) {
    if (deleteMembers == null) {
      deleteMembers = String.valueOf(userId);
    } else {
      System.out.println("deleteMembers=" + deleteMembers);
      deleteMembers += "|" + String.valueOf(userId);
    }
  }

  public void updateModified(Connection db) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE ticket " +
            "SET modified = " + DatabaseUtils.getCurrentTimestamp(db) + ", modifiedby = ? " +
            "WHERE ticketid = ? ");
    pst.setInt(1, modifiedBy);
    pst.setInt(2, id);
    pst.execute();
    pst.close();
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
   * @return the linkProjectId
   */
  public int getLinkProjectId() {
    return linkProjectId;
  }


  /**
   * @param linkProjectId the linkProjectId to set
   */
  public void setLinkProjectId(int linkProjectId) {
    this.linkProjectId = linkProjectId;
  }

  public void setLinkProjectId(String linkProjectId) {
    this.linkProjectId = Integer.parseInt(linkProjectId);
  }

  /**
   * @return the linkModuleId
   */
  public int getLinkModuleId() {
    return linkModuleId;
  }


  /**
   * @param linkModuleId the linkModuleId to set
   */
  public void setLinkModuleId(int linkModuleId) {
    this.linkModuleId = linkModuleId;
  }

  public void setLinkModuleId(String linkModuleId) {
    this.linkModuleId = Integer.parseInt(linkModuleId);
  }

  /**
   * @return the linkItemId
   */
  public int getLinkItemId() {
    return linkItemId;
  }


  /**
   * @param linkItemId the linkItemId to set
   */
  public void setLinkItemId(int linkItemId) {
    this.linkItemId = linkItemId;
  }

  public void setLinkItemId(String linkItemId) {
    this.linkItemId = Integer.parseInt(linkItemId);
  }

  public Project getLinkProject() {
    if (linkProjectId != -1) {
      return ProjectUtils.loadProject(linkProjectId);
    }
    return null;
  }

  public String getItemLink() {
    if (!foundTicketLinkObject) {
      return null;
    }
    if (linkModuleId == Constants.PROJECT_WIKI_COMMENT_FILES) {
      return LinkGenerator.getItemLink(linkModuleId, linkWikiComment.getWikiId(), linkItemId);
    } else if (linkModuleId == Constants.BLOG_POST_COMMENT_FILES) {
      return LinkGenerator.getItemLink(linkModuleId, linkBlogPostComment.getNewsId(), linkItemId);
    } else if (linkModuleId == Constants.PROJECTS_CALENDAR_EVENT_FILES) {
      Timestamp meetingStartDate = linkMeeting.getStartDate();
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(meetingStartDate.getTime());
      String day = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
      return LinkGenerator.getItemLink(linkModuleId, day);
    }
    return LinkGenerator.getItemLink(linkModuleId, linkItemId);
  }

  public String getItemLabel() {
    if (!foundTicketLinkObject) {
      return "[The item has been deleted]";
    }
    return ModuleUtils.getItemLabel(linkModuleId);
  }


  /**
   * @return the buildLinkItem
   */
  public boolean getBuildLinkItem() {
    return buildLinkItem;
  }

  /**
   * @param buildLinkItem the buildLinkItem to set
   */
  public void setBuildLinkItem(boolean buildLinkItem) {
    this.buildLinkItem = buildLinkItem;
  }

  public void setBuildLinkItem(String buildLinkItem) {
    this.buildLinkItem = DatabaseUtils.parseBoolean(buildLinkItem);
  }

  /**
   * @return the foundTicketLinkObject
   */
  public boolean getFoundTicketLinkObject() {
    return foundTicketLinkObject;
  }


  public void buildLinkItem(Connection db) {
    foundTicketLinkObject = true;
    try {
      if (linkModuleId != -1 && linkItemId != -1) {
        if (linkModuleId == Constants.PROJECT_BLOG_FILES) {
          linkBlogPost = new BlogPost(db, linkItemId);
        } else if (linkModuleId == Constants.PROJECT_REVIEW_FILES) {
          linkProjectRating = new ProjectRating(db, linkItemId);
        } else if (linkModuleId == Constants.PROJECT_WIKI_FILES) {
          linkWiki = new Wiki(db, linkItemId);
        } else if (linkModuleId == Constants.DISCUSSION_FILES_TOPIC) {
          linkTopic = new Topic(db, linkItemId);
        } else if (linkModuleId == Constants.PROJECT_WIKI_COMMENT_FILES) {
          linkWikiComment = new WikiComment(db, linkItemId);
        } else if (linkModuleId == Constants.BLOG_POST_COMMENT_FILES) {
          linkBlogPostComment = new BlogPostComment(db, linkItemId);
        } else if (linkModuleId == Constants.PROJECTS_FILES) {
          linkFileItem = new FileItem(db, linkItemId);
        } else if (linkModuleId == Constants.PROJECTS_CALENDAR_EVENT_FILES) {
          linkMeeting = new Meeting(db, linkItemId);
        } else if (linkModuleId == Constants.PROJECT_IMAGE_FILES) {
          linkFileItem = new FileItem(db, linkItemId);
        } else {
          foundTicketLinkObject = false;
        }
      }
    } catch (Exception e) {
      foundTicketLinkObject = false;
    }
  }
}

