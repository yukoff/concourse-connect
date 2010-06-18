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

package com.concursive.connect.web.modules.issues.actions;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.dao.ProjectItemList;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.contacts.beans.ContactSearchBean;
import com.concursive.connect.web.modules.contacts.dao.ContactList;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.utils.ThumbnailUtils;
import com.concursive.connect.web.modules.issues.beans.CategoryEditor;
import com.concursive.connect.web.modules.issues.dao.*;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.utils.HtmlSelect;
import com.concursive.connect.web.utils.LookupList;
import com.concursive.connect.web.utils.PagedListInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementTickets.java,v 1.12.4.1 2004/08/20 19:48:25
 *          matt Exp $
 * @created June 8, 2004
 */
public final class ProjectManagementTickets extends GenericAction {

  /*
  public String executeCommandDetails(ActionContext context) {
    // ProjectManagementTickets.do?command=Details&pid=139&id=3396
    String projectId = context.getRequest().getParameter("pid");
    String id = context.getRequest().getParameter("id");
    int projectTicketId = TicketUtils.queryProjectTicketIdFromTicketId(db, id);
    String redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/issue/" + projectTicketId;
    context.getRequest().setAttribute("redirectTo", redirect);
    context.getRequest().removeAttribute("PageLayout");
    return "Redirect301";
  }
  */

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAdd(ActionContext context) {
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = this.getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      // NOTE: the Modify method uses the add method too...
      String isEditing = (String) context.getRequest().getAttribute("isEditing");
      if (isEditing == null && !hasProjectAccess(context, thisProject.getId(), "project-tickets-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "tickets_add");
      // Load the team
      PagedListInfo projectTeamInfo = new PagedListInfo();
      projectTeamInfo.setItemsPerPage(0);
      projectTeamInfo.setDefaultSort("last_name", null);
      TeamMemberList team = new TeamMemberList();
      team.setProjectId(thisProject.getId());
      team.setPagedListInfo(projectTeamInfo);
      team.buildList(db);
      context.getRequest().setAttribute("teamMemberList", team);
      // Prepare the form
      Ticket thisTicket = (Ticket) context.getFormBean();
      thisTicket.setOrgId(0);
      thisTicket.setContactId(getUserId(context));
      if (thisTicket.getId() > 0) {
        thisTicket.buildHistory(db);
        thisTicket.queryProjectTicketCount(db);
      }
      buildFormElements(context, db, thisProject, thisTicket);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if ("true".equals(context.getRequest().getParameter("popup"))) {
      return ("ProjectTicketsPopupOK");
    }
    return ("ProjectCenterOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSave(ActionContext context) {
    Connection db = null;
    int resultCount = 0;
    boolean recordInserted = false;
    String returnTo = context.getRequest().getParameter("return");
    try {
      //Process the ticket
      int projectId = Integer.parseInt(context.getRequest().getParameter("pid"));
      Ticket thisTicket = (Ticket) context.getFormBean();
      thisTicket.setProjectId(projectId);
      boolean newTicket = (thisTicket.getId() == -1);
      if (newTicket) {
        thisTicket.setEnteredBy(getUserId(context));
      }
      thisTicket.setModifiedBy(getUserId(context));
      // TODO: Null out the ticket values if this user does not have access to
      // the distribution list...

      if ("ON".equals(context.getParameter("emailUpdates"))) {
        thisTicket.addToInsertMembers(getUserId(context));
      }
      if ("ON".equals(context.getParameter("doNotEmailUpdates"))) {
        thisTicket.addToDeleteMembers(getUserId(context));
      }
      db = this.getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      // Only assign to users of the project
      if (thisTicket.getAssignedTo() > -1 && !TeamMemberList.isOnTeam(db, thisProject.getId(), thisTicket.getAssignedTo())) {
        return "PermissionError";
      }
      if (newTicket) {
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-add")) {
          return "PermissionError";
        }
        recordInserted = thisTicket.insert(db);
        if (recordInserted) {
          indexAddItem(context, thisTicket);
          processInsertHook(context, thisTicket);
        }
      } else {
        // allow access to edit if assigned to this user and ticket is not closed
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-edit") &&
            !(hasProjectAccess(context, thisProject.getId(), "project-tickets-view") &&
                thisTicket.getClosed() == null && thisTicket.getAssignedTo() == getUserId(context))) {
          return "PermissionError";
        }
        if (thisProject.getId() != thisTicket.getProjectId()) {
          return "PermissionError";
        }
        Ticket previousTicket = new Ticket(db, thisTicket.getId());
        // Deny if this user is not allowed to view other's tickets
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
            previousTicket.getEnteredBy() != getUser(context).getId() &&
            previousTicket.getAssignedTo() != getUser(context).getId()) {
          return "PermissionError";
        }
        // Depending on the fields that the user has access to, the previous
        // fields must stay intact during the update
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-edit") &&
            !hasProjectAccess(context, thisProject.getId(), "project-tickets-assign")) {
          thisTicket.setStateId(previousTicket.getStateId());
          thisTicket.setCause(previousTicket.getCause());
          thisTicket.setDefectId(previousTicket.getDefectId());
          thisTicket.setRelatedId(previousTicket.getRelatedId());
        }
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-assign")) {
          thisTicket.setEscalationId(previousTicket.getEscalationId());
          thisTicket.setPriorityCode(previousTicket.getPriorityCode());
          thisTicket.setAssignedTo(previousTicket.getAssignedTo());
          thisTicket.setEstimatedResolutionDate(previousTicket.getEstimatedResolutionDate());
        }
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-edit") &&
            !hasProjectAccess(context, thisProject.getId(), "project-tickets-assign")) {
          thisTicket.setResolutionId(previousTicket.getResolutionId());
          thisTicket.setSolution(previousTicket.getSolution());
          thisTicket.setReadyForClose(previousTicket.getReadyForClose());
        }
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-close")) {
          thisTicket.setClosed(previousTicket.getClosed());
          thisTicket.setCloseIt(previousTicket.getCloseIt());
        }
        resultCount = thisTicket.update(db);
        if (resultCount == 1) {
          // Since ticket is now closed, return user to the list
          if (thisTicket.getCloseIt()) {
            returnTo = "list";
          }
          // Reload, index, and process the hook
          thisTicket.queryRecord(db, thisTicket.getId());
          indexAddItem(context, thisTicket);
          processUpdateHook(context, previousTicket, thisTicket);
        }
      }
      if (!recordInserted && resultCount < 1) {
        processErrors(context, thisTicket.getErrors());
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (recordInserted) {
      return ("SaveOK");
    } else if (resultCount == 1) {
      if ("list".equals(returnTo)) {
        return ("SaveOK");
      } else {
        return ("SaveDetailsOK");
      }
    } else {
      // TODO: The following goes to an add form, but if the user was modifying
      // the ticket, then the id gets lost
      return (executeCommandAdd(context));
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandModify(ActionContext context) {
    Connection db = null;
    Ticket thisTicket = null;
    try {
      String ticketId = context.getRequest().getParameter("id");
      db = this.getConnection(context);
      //Load the ticket
      thisTicket = (Ticket) context.getFormBean();
      thisTicket.queryRecord(db, Integer.parseInt(ticketId));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      // allow access to edit if assigned to this user and ticket is not closed
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-edit") &&
          !(hasProjectAccess(context, thisProject.getId(), "project-tickets-view") &&
              thisTicket.getClosed() == null && thisTicket.getAssignedTo() == getUserId(context))) {
        return "PermissionError";
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      // The ticket is closed and cannot be modified
      if (thisTicket.getClosed() != null) {
        return "ClosedOK";
      }
      // Load the distribution list and prepare the first array of those on list
      TicketContactList contactList = new TicketContactList();
      contactList.setTicketId(thisTicket.getId());
      contactList.buildList(db);
      StringBuffer vectorUserId = new StringBuffer();
      StringBuffer vectorState = new StringBuffer();
      // Convert the list into an array for javascript
      HtmlSelect distributionList = new HtmlSelect();
      Iterator i = contactList.iterator();
      while (i.hasNext()) {
        TicketContact thisContact = (TicketContact) i.next();
        distributionList.addItem(thisContact.getValue(), thisContact.getContactName());
        vectorUserId.append(thisContact.getValue());
        vectorState.append("1");
        if (i.hasNext()) {
          vectorUserId.append("|");
          vectorState.append("|");
        }
      }
      context.getRequest().setAttribute("distributionList", distributionList);
      context.getRequest().setAttribute("vectorUserId", vectorUserId.toString());
      context.getRequest().setAttribute("vectorState", vectorState.toString());
      //addRecentItem(context, newTic);
      context.getRequest().setAttribute("isEditing", "true");
      return (executeCommandAdd(context));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDetails(ActionContext context) {
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    String ticketId = context.getRequest().getParameter("id");
    String returnAction = context.getRequest().getParameter("return");
    try {
      db = this.getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      Ticket thisTicket = null;
      if (returnAction == null) {
        //Determine pagedlist
        PagedListInfo projectTicketsInfo = this.getPagedListInfo(context, "projectTicketsInfo", "t.entered", null);
        projectTicketsInfo.setLink(context, ctx(context) + "/show/" + thisProject.getUniqueId() + "/issue");
        projectTicketsInfo.setMode(PagedListInfo.DETAILS_VIEW);
        //Load the ticket based on the offset
        TicketList tickets = new TicketList();
        tickets.setProjectId(thisProject.getId());
        tickets.setPagedListInfo(projectTicketsInfo);
        if (ticketId != null) {
          tickets.setId(Integer.parseInt(ticketId));
        }
        if (projectTicketsInfo.getListView() == null) {
          projectTicketsInfo.setListView("open");
          // Categories
          projectTicketsInfo.addFilter(1, "-1");
        }
        // Deny if this user is not allowed to view other's tickets
        if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other")) {
          tickets.setOwnTickets(getUser(context).getId());
        }
        if ("all".equals(projectTicketsInfo.getListView())) {

        } else if ("review".equals(projectTicketsInfo.getListView())) {
          tickets.setOnlyOpen(true);
          tickets.setForReview(Constants.TRUE);
        } else if ("closed".equals(projectTicketsInfo.getListView())) {
          tickets.setOnlyClosed(true);
        } else {
          tickets.setOnlyOpen(true);
        }
        tickets.setCatCode(projectTicketsInfo.getFilterValueAsInt("listFilter1"));
        tickets.buildList(db);
        // Retrieve the ticket
        if (tickets.size() > 0) {
          thisTicket = tickets.get(0);
        }
        if (thisTicket != null) {
          context.getRequest().setAttribute(PagedListInfo.REFRESH_PARAMETER, "/" + thisTicket.getProjectTicketCount());
        }
      }
      if (thisTicket == null) {
        thisTicket = new Ticket(db, Integer.parseInt(ticketId));
      }
      if (thisTicket.getAssignedTo() > 0) {
        thisTicket.checkEnabledOwnerAccount(db);
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      // Load the ticket history
      thisTicket.buildHistory(db);
      thisTicket.buildLinkItem(db);
      context.getRequest().setAttribute("ticket", thisTicket);
      processSelectHook(context, thisTicket);
      // Load the distribution list
      TicketContactList distributionList = new TicketContactList();
      distributionList.setTicketId(thisTicket.getId());
      distributionList.buildList(db);
      context.getRequest().setAttribute("distributionList", distributionList);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    context.getRequest().setAttribute("IncludeSection", "tickets_details");
    return ("ProjectCenterOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandReopen(ActionContext context) {
    Connection db = null;
    Ticket thisTicket = null;
    int resultCount = -1;
    try {
      db = this.getConnection(context);
      //Load the ticket and change the status
      thisTicket = new Ticket(db, Integer.parseInt(context.getRequest().getParameter("id")));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-close")) {
        return "PermissionError";
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      thisTicket.setModifiedBy(getUserId(context));
      resultCount = thisTicket.reopen(db);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("ReopenOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDelete(ActionContext context) {
    Connection db = null;
    boolean recordDeleted = false;
    Ticket thisTicket = null;
    String ticketId = context.getRequest().getParameter("id");
    String returnTo = context.getRequest().getParameter("return");
    try {
      db = this.getConnection(context);
      //Load and delete the ticket
      thisTicket = new Ticket(db, Integer.parseInt(ticketId));
      //Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-delete")) {
        return "PermissionError";
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      thisTicket.delete(db, getPath(context, "projects"));
      indexDeleteItem(context, thisTicket);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    //deleteRecentItem(context, thisTicket);
    //if ("list".equals(returnTo)) {
    //  return ("DeleteOK");
    //}
    // TODO: This needs to determine the next ticket to go to
    //return ("DeleteDetailsOK");
    return ("DeleteOK");
  }

  public String executeCommandCategoryJSList(ActionContext context) {
    Connection db = null;
    try {
      int projectId = Integer.parseInt(context.getRequest().getParameter("projectId"));
      int categoryId = Integer.parseInt(context.getRequest().getParameter("categoryId"));
      int nextLevel = Integer.parseInt(context.getRequest().getParameter("nextLevel"));
      db = this.getConnection(context);
      // Basic permission checking for the list
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
        return "PermissionError";
      }
      // Update the list
      TicketCategoryList subList = new TicketCategoryList();
      subList.setProjectId(projectId);
      subList.setParentCode(categoryId);
      subList.setCatLevel(nextLevel);
      subList.buildList(db);
      context.getRequest().setAttribute("editList", subList);
      // Define this editor
      CategoryEditor editor = new CategoryEditor();
      editor.setMaxLevels(4);
      context.getRequest().setAttribute("categoryEditor", editor);
    } catch (Exception errorMessage) {
    } finally {
      this.freeConnection(context, db);
    }
    return ("CategoryJSListOK");
  }


  public String executeCommandAddComments(ActionContext context) {
    Connection db = null;
    Ticket thisTicket = null;
    try {
      db = this.getConnection(context);
      //Load the ticket and change the status
      thisTicket = new Ticket(db, Integer.parseInt(context.getRequest().getParameter("id")));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
        return "PermissionError";
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Load the ticket history
      thisTicket.buildHistory(db);
      context.getRequest().setAttribute("ticket", thisTicket);
      context.getRequest().setAttribute("IncludeSection", "tickets_comments_add");
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("ProjectCenterOK");
  }

  public String executeCommandSaveComments(ActionContext context) {
    Connection db = null;
    Ticket thisTicket = null;
    try {
      db = this.getConnection(context);
      //Load the ticket and change the status
      thisTicket = new Ticket(db, Integer.parseInt(context.getRequest().getParameter("id")));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
        return "PermissionError";
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      String comment = context.getRequest().getParameter("comment");
      String attachmentList = context.getRequest().getParameter("attachmentList");
      if (StringUtils.hasText(comment)) {
        // Track the comment as part of the ticket update
        thisTicket.setComment(comment);
        Ticket previousTicket = new Ticket(db, thisTicket.getId());
        // Insert the new comment
        TicketLog thisEntry = new TicketLog();
        thisEntry.setAssignedTo(thisTicket.getAssignedTo());
        thisEntry.setPriorityCode(thisTicket.getPriorityCode());
        thisEntry.setSeverityCode(thisTicket.getSeverityCode());
        thisEntry.setEntryText(comment);
        thisEntry.setTicketId(thisTicket.getId());
        thisEntry.process(db, thisTicket.getId(), getUserId(context), getUserId(context));
        thisTicket.setModifiedBy(getUserId(context));
        thisTicket.updateModified(db);
        FileItemList.convertTempFiles(db, Constants.PROJECT_TICKET_FILES, getUserId(context), thisTicket.getId(), attachmentList);
        processUpdateHook(context, previousTicket, thisTicket);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("SaveCommentsOK");
  }


  public String executeCommandFileDownload(ActionContext context) {
    Exception errorMessage = null;
    String ticketId = context.getRequest().getParameter("id");
    String itemId = context.getRequest().getParameter("fid");
    String view = context.getRequest().getParameter("view");
    FileItem thisItem = null;
    Connection db = null;
    Ticket thisTicket = null;
    try {
      db = getConnection(context);
      //Load the ticket
      thisTicket = (Ticket) context.getFormBean();
      thisTicket.queryRecord(db, Integer.parseInt(ticketId));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      // allow access to download
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
        return "PermissionError";
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      // Load the file for download
      thisItem = new FileItem(db, Integer.parseInt(itemId), thisTicket.getId(), Constants.PROJECT_TICKET_FILES);
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      this.freeConnection(context, db);
    }
    //Start the download
    try {
      FileItem itemToDownload = thisItem;
      itemToDownload.setEnteredBy(getUserId(context));
      String filePath = this.getPath(context, "projects") + getDatePath(itemToDownload.getModified()) + itemToDownload.getFilename();
      FileDownload fileDownload = new FileDownload();
      fileDownload.setFullPath(filePath);
      fileDownload.setDisplayName(itemToDownload.getClientFilename());
      if (fileDownload.fileExists()) {
        if (view != null && "true".equals(view)) {
          if (thisItem.isImageFormat() && thisItem.hasValidImageSize()) {
            // Use the panel preview
            Thumbnail thumbnail = ThumbnailUtils.retrieveThumbnail(db, itemToDownload, 640, 480, this.getPath(context, "projects"));
            filePath = this.getPath(context, "projects") + getDatePath(itemToDownload.getModified()) + thumbnail.getFilename();
            fileDownload.setFullPath(filePath);
            fileDownload.setFileTimestamp(itemToDownload.getModificationDate().getTime());
            fileDownload.streamThumbnail(context, thumbnail);
          } else {
            // Use the browser's capability
            fileDownload.streamContent(context);
          }
        } else {
          fileDownload.sendFile(context);
        }
        //Get a db connection now that the download is complete
        db = getConnection(context);
        itemToDownload.updateCounter(db);
      } else {
        System.err.println("ProjectManagementTickets-> Trying to send a file that does not exist: " + filePath);
      }
    } catch (java.net.SocketException se) {
      //User either canceled the download or lost connection
    } catch (Exception e) {
      errorMessage = e;
      System.out.println(e.toString());
    } finally {
      this.freeConnection(context, db);
    }
    if (errorMessage == null) {
      return ("-none-");
    } else {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }

  public String executeCommandFileDelete(ActionContext context) {
    Connection db = null;
    Ticket thisTicket = null;
    try {
      db = this.getConnection(context);
      //Load the ticket and change the status
      thisTicket = new Ticket(db, Integer.parseInt(context.getRequest().getParameter("id")));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-edit")) {
        return "PermissionError";
      }
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      String itemId = context.getRequest().getParameter("fid");
      FileItem thisItem = new FileItem(db, Integer.parseInt(itemId), thisTicket.getId(), Constants.PROJECT_TICKET_FILES);
      thisItem.delete(db, this.getPath(context, "projects"));
      return "FileDeleteOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context     Description of the Parameter
   * @param db          Description of the Parameter
   * @param thisProject Description of the Parameter
   * @param thisTicket  Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  protected void buildFormElements(ActionContext context, Connection db, Project thisProject, Ticket thisTicket) throws SQLException {
    context.getRequest().setAttribute("TicketDetails", thisTicket);
    //Severity List
    LookupList severityList = new LookupList(db, "ticket_severity");
    context.getRequest().setAttribute("SeverityList", severityList);
    //Priority List
    LookupList priorityList = new LookupList(db, "ticket_priority");
    context.getRequest().setAttribute("PriorityList", priorityList);

    TicketCategoryList categoryList = new TicketCategoryList();
    categoryList.setProjectId(thisProject.getId());
    categoryList.setCatLevel(0);
    categoryList.setParentCode(0);
    categoryList.setHtmlJsEvent("onChange=\"javascript:updateSubList1();\"");
    categoryList.buildList(db);
    categoryList.getCatListSelect().addItem(0, "Undetermined");
    context.getRequest().setAttribute("CategoryList", categoryList);

    TicketCategoryList subList1 = new TicketCategoryList();
    subList1.setProjectId(thisProject.getId());
    subList1.setCatLevel(1);
    subList1.setParentCode(thisTicket.getCatCode());
    subList1.setHtmlJsEvent("onChange=\"javascript:updateSubList2();\"");
    subList1.buildList(db);
    if (System.getProperty("DEBUG") != null) {
      System.out.println("ProjectManagementTickets-> SubList1 size: " + subList1.size());
      System.out.println(subList1.toString());
    }
    subList1.getCatListSelect().addItem(0, "Undetermined");
    subList1.getCatListSelect().setDefaultKey(thisTicket.getSubCat1());
    context.getRequest().setAttribute("SubList1", subList1);

    TicketCategoryList subList2 = new TicketCategoryList();
    subList2.setProjectId(thisProject.getId());
    subList2.setCatLevel(2);
    subList2.setParentCode(thisTicket.getSubCat1());
    subList2.setHtmlJsEvent("onChange=\"javascript:updateSubList3();\"");
    subList2.buildList(db);
    subList2.getCatListSelect().addItem(0, "Undetermined");
    subList2.getCatListSelect().setDefaultKey(thisTicket.getSubCat2());
    context.getRequest().setAttribute("SubList2", subList2);

    TicketCategoryList subList3 = new TicketCategoryList();
    subList3.setProjectId(thisProject.getId());
    subList3.setCatLevel(3);
    subList3.setParentCode(thisTicket.getSubCat2());
    subList3.buildList(db);
    subList3.getCatListSelect().addItem(0, "Undetermined");
    subList3.getCatListSelect().setDefaultKey(thisTicket.getSubCat3());
    context.getRequest().setAttribute("SubList3", subList3);

    ProjectItemList causeList = new ProjectItemList();
    causeList.setProjectId(thisProject.getId());
    causeList.setEnabled(Constants.TRUE);
    causeList.setIncludeId(thisTicket.getCauseId());
    causeList.buildList(db, ProjectItemList.TICKET_CAUSE);
    context.getRequest().setAttribute("TicketCauseList", causeList);

    ProjectItemList resolutionList = new ProjectItemList();
    resolutionList.setProjectId(thisProject.getId());
    resolutionList.setEnabled(Constants.TRUE);
    resolutionList.setIncludeId(thisTicket.getResolutionId());
    resolutionList.buildList(db, ProjectItemList.TICKET_RESOLUTION);
    context.getRequest().setAttribute("TicketResolutionList", resolutionList);

    ProjectItemList defectList = new ProjectItemList();
    defectList.setProjectId(thisProject.getId());
    defectList.setEnabled(Constants.TRUE);
    defectList.setIncludeId(thisTicket.getDefectId());
    defectList.buildList(db, ProjectItemList.TICKET_DEFECT);
    context.getRequest().setAttribute("TicketDefectList", defectList);

    ProjectItemList escalationList = new ProjectItemList();
    escalationList.setProjectId(thisProject.getId());
    escalationList.setEnabled(Constants.TRUE);
    escalationList.setIncludeId(thisTicket.getEscalationId());
    escalationList.buildList(db, ProjectItemList.TICKET_ESCALATION);
    context.getRequest().setAttribute("TicketEscalationList", escalationList);

    ProjectItemList stateList = new ProjectItemList();
    stateList.setProjectId(thisProject.getId());
    stateList.setEnabled(Constants.TRUE);
    stateList.setIncludeId(thisTicket.getStateId());
    stateList.buildList(db, ProjectItemList.TICKET_STATE);
    context.getRequest().setAttribute("TicketStateList", stateList);
  }

  public String executeCommandProjects(ActionContext context) {
    //Parameters
    String value = context.getRequest().getParameter("source");
    StringTokenizer st = new StringTokenizer(value, "|");
    String source = st.nextToken();
    String status = st.nextToken();
    //Build the list
    Connection db = null;
    try {
      db = getConnection(context);
      if ("my".equals(source) || "all".equals(source)) {
        ProjectList projects = new ProjectList();
        projects.setProjectsForUser(getUserId(context));
        projects.setIncludeGuestProjects(false);
        if ("open".equals(status)) {
          //Check if open or closed
          projects.setOpenProjectsOnly(true);
        } else {
          projects.setClosedProjectsOnly(true);
        }
        projects.buildList(db);
        context.getRequest().setAttribute("projectList", projects);
        return "ProjectsOK";
      }
    } catch (Exception e) {

    } finally {
      freeConnection(context, db);
    }
    return null;
  }


  public String executeCommandItems(ActionContext context) {
    //Parameters
    String value = context.getRequest().getParameter("source");
    StringTokenizer st = new StringTokenizer(value, "|");
    String source = st.nextToken();
    String status = st.nextToken();
    String id = st.nextToken();
    Connection db = null;
    try {
      db = getConnection(context);
      if ("contacts".equals(source)) {
        // Determine data to search for
        ContactSearchBean searchCriteria = new ContactSearchBean();
        searchCriteria.setMethod(ContactSearchBean.JOIN_OR);
        searchCriteria.setFirstName(id);
        searchCriteria.setLastName(id);
        searchCriteria.setOrganization(id);
        searchCriteria.setEmail(id);
        // Perform the contact search
        ContactList contacts = new ContactList();
        contacts.setSearchCriteria(searchCriteria);
        contacts.setForUser(getUserId(context));
        if (getUser(context).getAccessViewAllContacts()) {
          contacts.setIncludeAllGlobal(Constants.TRUE);
        }
        contacts.buildList(db);
        context.getRequest().setAttribute("contacts", contacts);
        return ("MakeContactListOK");
      } else if ("my".equals(source) || "all".equals(source) || "this".equals(source)) {
        //Load the project and check permissions
        Project thisProject = retrieveAuthorizedProject(Integer.parseInt(id), context);
        //Prepare list of team members
        TeamMemberList team = new TeamMemberList();
        team.setProjectId(Integer.parseInt(id));
        //Check permission first
        if (hasProjectAccess(context, thisProject.getId(), "project-team-view")) {
          team.buildList(db);
        }
        context.getRequest().setAttribute("team", team);
        return ("MakeTeamMemberListOK");
      }
      if ("dept".equals(source) && "true".equals(getPref(context, "DEPARTMENT"))) {
        //Load departments and get the contacts
        UserList users = new UserList();
        users.setDepartmentId(Integer.parseInt(id));
        users.buildList(db);
        context.getRequest().setAttribute("UserList", users);
        return ("MakeUserListOK");
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return null;
  }

  public String executeCommandChangeWatch(ActionContext context) {
    //Parameters
    String ticketId = context.getRequest().getParameter("ticketId");
    String value = context.getRequest().getParameter("value");
    Connection db = null;
    try {
      db = getConnection(context);
      // Verify ticket view permissions
      Ticket thisTicket = new Ticket(db, Integer.parseInt(ticketId));
      //Load the project
      Project thisProject = retrieveAuthorizedProject(thisTicket.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
        return "PermissionError";
      }
      //if (thisTicket.getAssignedTo() > 0) {
      //  thisTicket.checkEnabledOwnerAccount(db);
      //}
      // Deny if this user is not allowed to view other's tickets
      if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-other") &&
          thisTicket.getEnteredBy() != getUser(context).getId() &&
          thisTicket.getAssignedTo() != getUser(context).getId()) {
        return "PermissionError";
      }
      if ("true".equals(value)) {
        // Add user to watch list
        TicketContact ticketContact = new TicketContact();
        ticketContact.setTicketId(thisTicket.getId());
        ticketContact.setEnteredBy(getUserId(context));
        ticketContact.setUserId(getUserId(context));
        ticketContact.insert(db);
      } else if ("false".equals(value)) {
        // Remove user from watch list
        TicketContactList.deleteUserId(db, thisTicket.getId(), getUserId(context));
      }
      context.getRequest().setAttribute("watchResult", value);
      return "MakeWatchOK";
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return null;
  }
}

