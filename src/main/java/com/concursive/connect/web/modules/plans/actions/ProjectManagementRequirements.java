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

package com.concursive.connect.web.modules.plans.actions;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.files.FileUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.plans.dao.Requirement;
import com.concursive.connect.web.modules.plans.utils.AssignmentImporter;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.LookupList;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Actions for the Plans module
 *
 * @author matt rajkowski
 * @created November 12, 2001
 */
public final class ProjectManagementRequirements extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAdd(ActionContext context) {
    Exception errorMessage = null;
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "requirements_add");

      LookupList loeList = new LookupList(db, "lookup_project_loe");
      context.getRequest().setAttribute("LoeList", loeList);
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      this.freeConnection(context, db);
    }
    if (errorMessage == null) {
      return ("ProjectCenterOK");
    } else {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandInsert(ActionContext context) {
    Connection db = null;
    String projectId = context.getRequest().getParameter("pid");
    boolean recordInserted = false;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "requirements_add");

      Requirement thisRequirement = (Requirement) context.getFormBean();
      thisRequirement.setProjectId(thisProject.getId());
      thisRequirement.setEnteredBy(getUserId(context));
      thisRequirement.setModifiedBy(getUserId(context));
      recordInserted = thisRequirement.insert(db);
      indexAddItem(context, thisRequirement);
      if (!recordInserted) {
        processErrors(context, thisRequirement.getErrors());
      } else {
        context.getRequest().setAttribute("requirement", thisRequirement);
        // if an attachment list exists, try to import the file
        if (StringUtils.hasText(thisRequirement.getAttachmentList()) &&
            hasProjectAccess(context, thisProject.getId(), "project-plan-outline-edit")) {
          // Import all files and append them
          FileItemList files = Requirement.retrieveFiles(db, thisRequirement.getId());
          for (FileItem thisFile : files) {
            String filePath = this.getPath(context, "projects") +
                getDatePath(thisFile.getModified()) +
                thisFile.getFilename();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setClientFileName(thisFile.getClientFilename());
            fileInfo.setFileContents(FileUtils.getBytesFromFile(new File(filePath)));
            AssignmentImporter.parse(fileInfo, thisRequirement, db);
            thisFile.delete(db, filePath);
          }
        }
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    if (recordInserted) {
      return ("AddOK");
    }
    return (executeCommandAdd(context));
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDetails(ActionContext context) {
    Exception errorMessage = null;
    String projectId = context.getRequest().getParameter("pid");
    String requirementId = context.getRequest().getParameter("rid");
    Connection db = null;
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      //Load the requirement
      Requirement thisRequirement = new Requirement(db, Integer.parseInt(requirementId), thisProject.getId());
      context.getRequest().setAttribute("Requirement", thisRequirement);
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      this.freeConnection(context, db);
    }
    if (errorMessage == null) {
      return ("PopupOK");
    } else {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandModify(ActionContext context) {
    //Params
    String projectId = context.getRequest().getParameter("pid");
    String requirementId = context.getRequest().getParameter("rid");
    Connection db = null;
    try {
      db = getConnection(context);
      //Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-edit")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "requirements_add");
      //Requirement
      Requirement thisRequirement = new Requirement(db, Integer.parseInt(requirementId), thisProject.getId());
      context.getRequest().setAttribute("Requirement", thisRequirement);
      //Form data
      LookupList loeList = new LookupList(db, "lookup_project_loe");
      context.getRequest().setAttribute("LoeList", loeList);
      return ("ProjectCenterOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
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
  public String executeCommandUpdate(ActionContext context) {
    Requirement thisRequirement = (Requirement) context.getFormBean();
    Connection db = null;
    int resultCount = 0;
    try {
      db = this.getConnection(context);
      Project thisProject = retrieveAuthorizedProject(thisRequirement.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-edit")) {
        return "PermissionError";
      }
      thisRequirement.setProjectId(thisProject.getId());
      thisRequirement.setModifiedBy(getUserId(context));
      resultCount = thisRequirement.update(db, context);
      if (resultCount == -1) {
        processErrors(context, thisRequirement.getErrors());
        context.getRequest().setAttribute("project", thisProject);
        context.getRequest().setAttribute("Requirement", thisRequirement);
        context.getRequest().setAttribute("IncludeSection", "requirements_add");
      } else {
        indexAddItem(context, thisRequirement);
        context.getRequest().setAttribute("pid", String.valueOf(thisProject.getId()));
        context.getRequest().setAttribute("IncludeSection", "requirements");
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (resultCount == -1) {
      return ("ProjectCenterOK");
    } else if (resultCount == 1) {
      return ("UpdateOK");
    } else {
      context.getRequest().setAttribute(
          "Error",
          "<b>This record could not be updated because someone else updated it first.</b><p>" +
              "You can hit the back button to review the changes that could not be committed, " +
              "but you must reload the record and make the changes again.");
      return ("UserError");
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDelete(ActionContext context) {
    //Params
    String projectId = context.getRequest().getParameter("pid");
    String requirementId = context.getRequest().getParameter("rid");
    Connection db = null;
    try {
      db = getConnection(context);
      //Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-delete")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      //Requirement
      Requirement thisRequirement = new Requirement(db, Integer.parseInt(requirementId), thisProject.getId());
      String filePath = this.getPath(context, "projects");
      thisRequirement.delete(db, filePath);
      indexDeleteItem(context, thisRequirement);
      context.getRequest().setAttribute("IncludeSection", "requirements");
      return ("DeleteOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandTemplates(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    Connection db = null;
    try {
      db = this.getConnection(context);
      // Check permissions
      retrieveAuthorizedProject(Integer.parseInt(projectId), context);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e.getMessage());
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    return ("ShowTemplatesOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandPrepareImport(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String requirementId = context.getRequest().getParameter("rid");
    Connection db = null;
    try {
      db = this.getConnection(context);
      //Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-delete")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      //Load the requirement
      Requirement thisRequirement = new Requirement(db, Integer.parseInt(requirementId), thisProject.getId());
      context.getRequest().setAttribute("Requirement", thisRequirement);
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e.getMessage());
      return ("SystemError");
    } finally {
      if (db != null) {
        this.freeConnection(context, db);
      }
    }
    context.getRequest().setAttribute("IncludeSection", ("requirements_import"));
    return ("ProjectCenterOK");
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandImport(ActionContext context) {
    Connection db = null;
    try {
      HttpMultiPartParser multiPart = new HttpMultiPartParser();
      HashMap parts = multiPart.parseData(context.getRequest(), null);
      // Wait until stream is done
      db = this.getConnection(context);
      // Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt((String) parts.get("pid")), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-edit")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);

      // Load the requirement to use for saving the plan
      Requirement thisRequirement = new Requirement(
          db, Integer.parseInt((String) parts.get("rid")), thisProject.getId());
      context.getRequest().setAttribute("requirement", thisRequirement);

      // Import
      FileInfo fileInfo = (FileInfo) parts.get("file");
      AssignmentImporter.parse(fileInfo, thisRequirement, db);

      return ("ImportOK");
    } catch (Exception e) {
      e.printStackTrace(System.out);
      return "ImportERROR";
    } finally {
      if (db != null) {
        this.freeConnection(context, db);
      }
    }
  }

  public String executeCommandImportPlan(ActionContext context) {
    Connection db = null;
    try {
      // Parameters (no bean)
      String projectId = context.getParameter("pid");
      String requirementId = context.getParameter("rid");
      String overwrite = context.getParameter("overwrite");
      String attachmentList = context.getParameter("attachmentList");

      // Plan is being added to existing Requirement
      db = this.getConnection(context);

      // Load the project and permissions
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-edit")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);

      // Load the requirement to use for saving the plan
      Requirement thisRequirement = new Requirement(
          db, Integer.parseInt(requirementId), thisProject.getId());
      context.getRequest().setAttribute("requirement", thisRequirement);

      if (DatabaseUtils.parseBoolean(overwrite)) {
        // Delete the existing project plan before import
        Requirement.deletePlan(db, thisRequirement.getId());
      }

      // Import all files and append them
      FileItemList.convertTempFiles(db, Constants.PROJECT_REQUIREMENT_FILES, getUserId(context), thisRequirement.getId(), attachmentList);
      FileItemList files = Requirement.retrieveFiles(db, thisRequirement.getId());
      for (FileItem thisFile : files) {
        String filePath = this.getPath(context, "projects") +
            getDatePath(thisFile.getModified()) +
            thisFile.getFilename();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setClientFileName(thisFile.getClientFilename());
        fileInfo.setFileContents(FileUtils.getBytesFromFile(new File(filePath)));
        AssignmentImporter.parse(fileInfo, thisRequirement, db);
        thisFile.delete(db, filePath);
      }

      return ("ImportOK");
    } catch (Exception e) {
      e.printStackTrace(System.out);
      return "ImportERROR";
    } finally {
      if (db != null) {
        this.freeConnection(context, db);
      }
    }
  }
}

