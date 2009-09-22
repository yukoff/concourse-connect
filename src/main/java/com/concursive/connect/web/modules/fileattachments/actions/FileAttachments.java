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

package com.concursive.connect.web.modules.fileattachments.actions;

import com.concursive.commons.images.ImageUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.common.social.images.jobs.ImageResizerBean;
import com.concursive.connect.web.modules.common.social.images.jobs.ImageResizerJob;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.documents.utils.ThumbnailUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.HtmlSelect;
import org.quartz.Scheduler;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Actions for generating allocation
 *
 * @author matt rajkowski
 * @version $Id:FileAttachments.java 2246 2007-03-22 05:57:41Z matt $
 * @created March 22, 2007
 */
public final class FileAttachments extends GenericAction {

  public String executeCommandShowForm(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    try {
      // Process the parameters
      String projectIdValue = context.getRequest().getParameter("pid");
      int projectId = -1;
      int linkModuleId = Integer.parseInt(context.getRequest().getParameter("lmid"));
      int linkItemId = Integer.parseInt(context.getRequest().getParameter("liid"));
      int selectorId = Integer.parseInt(context.getRequest().getParameter("selectorId"));
      String selectorMode = context.getRequest().getParameter("selectorMode");
      db = getConnection(context);
      Project thisProject = null;
      if (projectIdValue != null) {
        projectId = Integer.parseInt(projectIdValue);
        thisProject = retrieveAuthorizedProject(projectId, context);
      }
      // Check module permissions
      switch (linkModuleId) {
        case Constants.PROJECT_IMAGE_FILES:
          if (thisProject != null) {
            if (!hasProjectAccess(context, thisProject.getId(), "project-profile-images-add")) {
              return "PermissionError";
            }
          }
          break;
        case Constants.PROJECT_TICKET_FILES:
          if (!hasProjectAccess(context, thisProject.getId(), "project-tickets-view")) {
            return "PermissionError";
          }
          break;
        case Constants.DISCUSSION_FILES_TOPIC:
          if (!hasProjectAccess(context, thisProject.getId(), "project-discussion-topics-add")) {
            return "PermissionError";
          }
          break;
        case Constants.DISCUSSION_FILES_REPLY:
          if (!hasProjectAccess(context, thisProject.getId(), "project-discussion-messages-reply")) {
            return "PermissionError";
          }
          break;
        case Constants.PROJECT_REQUIREMENT_FILES:
          if (!hasProjectAccess(context, thisProject.getId(), "project-plan-outline-edit")) {
            return "PermissionError";
          }
          break;
        case Constants.PROJECT_CLASSIFIEDS_FILES:
          if (!hasProjectAccess(context, thisProject.getId(), "project-classifieds-add")) {
            return "PermissionError";
          }
          break;
        case Constants.BADGE_CATEGORY_FILES:
          if (!getUser(context).getAccessAdmin()) {
            return "PermissionError";
          }
          break;
        case Constants.AD_CATEGORY_FILES:
          if (!getUser(context).getAccessAdmin()) {
            return "PermissionError";
          }
          break;
        case Constants.BADGE_FILES:
          if (!getUser(context).getAccessAdmin()) {
            return "PermissionError";
          }
          break;
        case Constants.PROJECT_CATEGORY_FILES:
          if (!getUser(context).getAccessAdmin()) {
            return "PermissionError";
          }
          break;
        case Constants.CLASSIFIED_CATEGORY_FILES:
          if (!getUser(context).getAccessAdmin()) {
            return "PermissionError";
          }
          break;
        case Constants.SITE_LOGO_FILES:
          if (!getUser(context).getAccessAdmin()) {
            return "PermissionError";
          }
          break;
        default:
          return "PermissionError";
      }
      // FileAttachments uses its own linkModuleId for uploads +
      // a time based linkItemId +
      // the user's enteredBy

      // Find this user's temporary files
      FileItemList temporaryFiles = new FileItemList();
      temporaryFiles.setLinkModuleId(Constants.TEMP_FILES);
      temporaryFiles.setLinkItemId(selectorId);
      temporaryFiles.setOwner(getUserId(context));
      temporaryFiles.buildList(db);
      // Find the rest of this object's files
      FileItemList existingFiles = new FileItemList();
      //existingFiles.setLinkModuleId(linkModuleId);
      //existingFiles.setLinkItemId(linkItemId);
      //existingFiles.buildList(db);
      // Create an HTML Select for displaying and managing the files
      HtmlSelect fileItemList = new HtmlSelect();
      if ("single".equals(selectorMode)) {
        if (temporaryFiles.size() > 0) {
          fileItemList.addItem(temporaryFiles.get(temporaryFiles.size() - 1).getId(), temporaryFiles.get(temporaryFiles.size() - 1).getClientFilename());
        }
      } else {
        fileItemList.addItems(temporaryFiles, "id", "clientFilename");
      }
      fileItemList.addItems(existingFiles, "id", "clientFilename");
      context.getRequest().setAttribute("fileItemList", fileItemList);
      context.getRequest().setAttribute("fileSize", String.valueOf(temporaryFiles.getFileSize() + existingFiles.getFileSize()));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "FormOK";
  }

  public String executeCommandAttach(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    Connection db = null;
    boolean recordInserted = false;
    try {
      String filePath = this.getPath(context, "projects");
      //Process the form data
      HttpMultiPartParser multiPart = new HttpMultiPartParser();
      multiPart.setUsePathParam(false);
      multiPart.setUseUniqueName(true);
      multiPart.setUseDateForFolder(true);
      multiPart.setExtensionId(getUserId(context));
      HashMap parts = multiPart.parseData(context.getRequest(), filePath);

      int projectId = -1;
      String projectIdValue = (String) parts.get("pid");
      if (projectIdValue != null) {
        projectId = Integer.parseInt(projectIdValue);
      }
      int linkModuleId = Integer.parseInt((String) parts.get("lmid"));
      int linkItemId = Integer.parseInt((String) parts.get("liid"));
      int selectorId = Integer.parseInt((String) parts.get("selectorId"));
      String selectorMode = (String) parts.get("selectorMode");
      String added = (String) parts.get("added");
      String comment = (String) parts.get("comment");

      db = getConnection(context);
      // Must be a team member or a user with admin privileges
      Project thisProject = null;
      if (projectId > -1) {
        thisProject = retrieveAuthorizedProject(projectId, context);
      }
      //TODO: The conditions under which a file can be attached will change, so this condition check needs to be refactored
      if (thisProject == null && getUser(context).getId() < 0) {
        if (parts.get("id" + projectIdValue) instanceof FileInfo) {
          FileInfo newFileInfo = (FileInfo) parts.get("id" + projectIdValue);
          File thisFile = newFileInfo.getLocalFile();
          if (thisFile.exists()) {
            thisFile.delete();
            System.out.println("FileAttachments-> Unallowed file deleted");
          }
        }
        return "PermissionError";
      }
      //Update the database with the resulting file
      if ((Object) parts.get("id" + projectIdValue) instanceof FileInfo) {
        FileInfo newFileInfo = (FileInfo) parts.get("id" + projectIdValue);

        FileItem thisItem = new FileItem();
        thisItem.setLinkModuleId(Constants.TEMP_FILES);
        thisItem.setLinkItemId(selectorId);
        thisItem.setEnteredBy(getUserId(context));
        thisItem.setModifiedBy(getUserId(context));
        thisItem.setSubject(newFileInfo.getClientFileName());
        thisItem.setClientFilename(newFileInfo.getClientFileName());
        thisItem.setFilename(newFileInfo.getRealFilename());
        thisItem.setSize(newFileInfo.getSize());
        thisItem.setComment(comment);
        if (thisItem.isImageFormat() || Constants.PROJECT_IMAGE_FILES == linkModuleId) {
          // Verify that an image was correctly sent
          thisItem.setImageSize(ImageUtils.getImageSize(newFileInfo.getLocalFile()));
          if (thisItem.getImageWidth() == 0 || thisItem.getImageHeight() == 0) {
            // A bad image was sent
            return ("ImageUploadERROR");
          }
        }
        // this is a new document
        thisItem.setVersion(1.0);
        recordInserted = thisItem.insert(db);
        thisItem.setDirectory(filePath);
        if (!recordInserted) {
          processErrors(context, thisItem.getErrors());
        } else {
          if (thisItem.isImageFormat() && thisItem.hasValidImageSize()) {
            // Prepare this image for thumbnail conversion
            ImageResizerBean bean = new ImageResizerBean();
            bean.setFileItemId(thisItem.getId());
            bean.setImagePath(newFileInfo.getLocalFile().getParent());
            bean.setImageFilename(thisItem.getFilename());
            bean.setEnteredBy(thisItem.getEnteredBy());
            int[] dimensions = ThumbnailUtils.getPresetDimensionsForModule(linkModuleId);
            if (dimensions != null) {
              bean.setWidth(dimensions[0]);
              bean.setHeight(dimensions[1]);
            }
            // Add this to the ImageResizerJob to multi-thread the thumbnails
            Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute("Scheduler");
            ((Vector) scheduler.getContext().get(ImageResizerJob.IMAGE_RESIZER_ARRAY)).add(bean);
            scheduler.triggerJob("imageResizer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
          }
          context.getRequest().setAttribute("itemId", String.valueOf(thisItem.getId()));
        }
      }
      if (projectId > -1) {
        context.getRequest().setAttribute("pid", String.valueOf(projectId));
      }
      context.getRequest().setAttribute("lmid", String.valueOf(linkModuleId));
      context.getRequest().setAttribute("liid", String.valueOf(linkItemId));
      context.getRequest().setAttribute("selectorId", String.valueOf(selectorId));
      context.getRequest().setAttribute("selectorMode", selectorMode);
      context.getRequest().setAttribute("added", added);
      if (Constants.PROJECT_IMAGE_FILES == linkModuleId && linkItemId != -1) {
        return "AttachProjectImageFilesOK";
      }
      return "AttachOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }


  public String executeCommandAttachProjectImageFiles(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    // Use the parameters from the file attachment panel
    int projectId = Integer.parseInt((String) context.getRequest().getAttribute("pid"));
    String itemId = (String) context.getRequest().getAttribute("itemId");
    Connection db = null;
    try {
      db = getConnection(context);
      // Check for project permissions
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-profile-images-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Update the project by finding the id of the uploaded image
      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.PROJECT_IMAGE_FILES);
      files.setLinkItemId(projectId);
      files.setEnteredBy(getUserId(context));
      files.buildList(db);
      // Convert the uploaded file
      boolean isFirst = (files.size() == 0);
      FileItemList.convertTempFiles(db, Constants.PROJECT_IMAGE_FILES, getUserId(context), projectId, itemId, isFirst);
      // This is a guess for now
      if (isFirst) {
        thisProject.setLogoId(itemId);
        thisProject.updateLogoId(db);
      }
      //trigger the workflow
      FileItemList convertedFiles = new FileItemList();
      StringTokenizer items = new StringTokenizer(itemId, ",");
      while (items.hasMoreTokens()) {
        int convertedId = Integer.parseInt(items.nextToken().trim());
        FileItem convertedFile = new FileItem(db, convertedId);
        convertedFiles.add(convertedFile);
      }
      if (convertedFiles.size() > 0) {
        this.processInsertHook(context, convertedFiles);
      }

      CacheUtils.invalidateValue(Constants.SYSTEM_PROJECT_CACHE, projectId);
      return "Attach201OK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }


  public String executeCommandRemove(ActionContext context) {
    if (getUser(context).getId() < 0) {
      return "PermissionError";
    }
    int fileItemId = Integer.parseInt(context.getRequest().getParameter("fid"));
    Connection db = null;
    try {
      db = getConnection(context);
      // Allow the owner to delete the file
      FileItem thisItem = new FileItem(db, fileItemId);
      if (thisItem.getLinkModuleId() == Constants.TEMP_FILES &&
          thisItem.getEnteredBy() != getUserId(context)) {
        return "PermissionError";
      }
      thisItem.delete(db, this.getPath(context, "projects"));
      return "RemoveOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

}
