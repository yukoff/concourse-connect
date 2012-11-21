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
package com.concursive.connect.web.modules.documents.actions;

import com.concursive.commons.images.ImageUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.common.social.images.jobs.ImageResizerBean;
import com.concursive.connect.web.modules.common.social.images.jobs.ImageResizerJob;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemVersion;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.documents.utils.ThumbnailUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import org.quartz.Scheduler;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Vector;

/**
 * Actions to support the Documents
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementFiles.java,v 1.2 2003/02/03 02:00:03 matt
 *          Exp $
 * @created December 4, 2001
 */
public final class ProjectManagementFiles extends GenericAction {

  public String executeCommandDetails(ActionContext context) {
    // ProjectManagementFiles.do?command=Details&pid=139&fid=1889&folderId=486
    String projectId = context.getRequest().getParameter("pid");
    String id = context.getRequest().getParameter("fid");
    String redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/file/" + id;
    context.getRequest().setAttribute("redirectTo", redirect);
    context.getRequest().removeAttribute("PageLayout");
    return "Redirect301";
  }

  /**
   * Description of the Method
   *
   * @param context Description of Parameter
   * @return Description of the Returned Value
   */
  public String executeCommandUpload(ActionContext context) {
    Connection db = null;
    boolean recordInserted = false;
    try {
      String filePath = this.getPath(context, "projects");
      // Process the form data
      HttpMultiPartParser multiPart = new HttpMultiPartParser();
      multiPart.setUsePathParam(false);
      multiPart.setUseUniqueName(true);
      multiPart.setUseDateForFolder(true);
      multiPart.setExtensionId(getUserId(context));
      HashMap parts = multiPart.parseData(context.getRequest(), filePath);
      String projectId = (String) parts.get("pid");
      String subject = (String) parts.get("subject");
      String comment = (String) parts.get("comment");
      String featuredFile = (String) parts.get("featuredFile");
      String folderId = (String) parts.get("folderId");
      String itemId = (String) parts.get("fid");
      String versionId = (String) parts.get("versionId");
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-documents-files-upload")) {
        // Delete the unallowed uploads, then exit
        if (parts.get("id" + projectId) instanceof FileInfo) {
          FileInfo newFileInfo = (FileInfo) parts.get("id" + projectId);
          File thisFile = newFileInfo.getLocalFile();
          if (thisFile.exists()) {
            thisFile.delete();
            LOG.warn("FileAttachments-> Unallowed file deleted");
          }
        }
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("folderId", folderId);
      context.getRequest().setAttribute("fid", itemId);
      // Update the database with the resulting file
      if (parts.get("id" + projectId) instanceof FileInfo) {
        FileInfo newFileInfo = (FileInfo) parts.get("id" + projectId);

        FileItem thisItem = new FileItem();
        thisItem.setLinkModuleId(Constants.PROJECTS_FILES);
        thisItem.setLinkItemId(thisProject.getId());
        thisItem.setEnteredBy(getUserId(context));
        thisItem.setModifiedBy(getUserId(context));
        thisItem.setFolderId(Integer.parseInt(folderId));
        thisItem.setSubject(subject);
        thisItem.setComment(comment);
        thisItem.setFeaturedFile(featuredFile);
        thisItem.setClientFilename(newFileInfo.getClientFileName());
        thisItem.setFilename(newFileInfo.getRealFilename());
        thisItem.setSize(newFileInfo.getSize());
        // Verify the integrity of the image
        if (thisItem.isImageFormat()) {
          thisItem.setImageSize(ImageUtils.getImageSize(newFileInfo.getLocalFile()));
          if (thisItem.getImageWidth() == 0 || thisItem.getImageHeight() == 0) {
            // A bad image was sent
            return ("ImageUploadERROR");
          }
        }
        if (itemId == null || "-1".equals(itemId)) {
          // this is a new document
          thisItem.setVersion(1.0);
          recordInserted = thisItem.insert(db);
          // Index the record and its file contents
          thisItem.setDirectory(filePath);
          indexAddItem(context, thisItem);
        } else {
          // this is a new version of an existing document
          thisItem.setId(Integer.parseInt(itemId));
          thisItem.setVersion(Double.parseDouble(versionId));
          recordInserted = thisItem.insertVersion(db);
          // Index the record and its file contents
          thisItem.setDirectory(filePath);
          indexAddItem(context, thisItem);
        }
        if (!recordInserted) {
          processErrors(context, thisItem.getErrors());
        } else {
          //trigger the workflow
          this.processInsertHook(context, thisItem);

          if (thisItem.isImageFormat()) {
            // Prepare this image for thumbnail conversion
            ImageResizerBean bean = new ImageResizerBean();
            bean.setFileItemId(thisItem.getId());
            bean.setImagePath(newFileInfo.getLocalFile().getParent());
            bean.setImageFilename(thisItem.getFilename());
            bean.setEnteredBy(thisItem.getEnteredBy());
            // Add this to the ImageResizerJob to multi-thread the thumbnails
            Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute("Scheduler");
            ((Vector) scheduler.getContext().get(ImageResizerJob.IMAGE_RESIZER_ARRAY)).add(bean);
            scheduler.triggerJob("imageResizer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
          }
        }
        return ("AddOK");
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "TODO Error";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDownload(ActionContext context) {
    Exception errorMessage = null;
    // Determine the download parameters
    String projectId = context.getRequest().getParameter("pid");
    String itemId = context.getRequest().getParameter("fid");
    String version = context.getRequest().getParameter("ver");
    String view = context.getRequest().getParameter("view");
    // Locate the item in the database
    FileItem thisItem = null;
    Connection db = null;
    try {
      db = getConnection(context);
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-documents-files-download")) {
        return "PermissionError";
      }
      thisItem = new FileItem(db, Integer.parseInt(itemId), thisProject.getId(), Constants.PROJECTS_FILES);
      // Reset version if it's not a real version number
      if ("0".equals(version)) {
        version = null;
      }
      if (version != null) {
        thisItem.buildVersionList(db);
      }
    } catch (Exception e) {
      errorMessage = e;
    } finally {
      this.freeConnection(context, db);
    }
    // Start the download
    try {
      if (version == null) {
        // Send the latest version
        FileItem itemToDownload = thisItem;
        itemToDownload.setEnteredBy(getUserId(context));
        String filePath = this.getPath(context, "projects") + getDatePath(itemToDownload.getModified()) + itemToDownload.getFilename();
        FileDownload fileDownload = new FileDownload();
        fileDownload.setFullPath(filePath);
        fileDownload.setDisplayName(itemToDownload.getClientFilename());
        if (fileDownload.fileExists()) {
          if (view != null && "true".equals(view)) {
            fileDownload.setFileTimestamp(itemToDownload.getModificationDate().getTime());
            fileDownload.streamContent(context);
          } else {
            fileDownload.sendFile(context);
          }
          // Get a db connection now that the download is complete
          db = getConnection(context);
          itemToDownload.updateCounter(db);

          //trigger the workflow
          FileItem fileItem = new FileItem(db, itemToDownload.getId());
          this.processUpdateHook(context, itemToDownload, fileItem);
        } else {
          System.err.println("ProjectManagementFiles-> Trying to send a file that does not exist");
        }
      } else {
        // Send the specified version
        FileItemVersion itemToDownload = thisItem.getVersion(Double.parseDouble(version));
        itemToDownload.setEnteredBy(getUserId(context));
        String filePath = this.getPath(context, "projects") + getDatePath(itemToDownload.getModified()) + itemToDownload.getFilename();
        FileDownload fileDownload = new FileDownload();
        fileDownload.setFullPath(filePath);
        fileDownload.setDisplayName(itemToDownload.getClientFilename());
        if (fileDownload.fileExists()) {
          if (view != null && "true".equals(view)) {
            fileDownload.setFileTimestamp(itemToDownload.getModificationDate().getTime());
            fileDownload.streamContent(context);
          } else {
            fileDownload.sendFile(context);
          }
          // Get a db connection now that the download is complete
          db = getConnection(context);
          itemToDownload.updateCounter(db);

          //trigger the workflow
          FileItem fileItem = new FileItem(db, itemToDownload.getId());
          this.processUpdateHook(context, thisItem, fileItem);
        } else {
          System.err.println("PMF-> Trying to send a file that does not exist");
        }
      }
    } catch (java.net.SocketException se) {
      // User either canceled the download or lost connection
    } catch (Exception e) {
      errorMessage = e;
      LOG.error("file download", e);
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

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandShowThumbnail(ActionContext context) {
    String projectId = context.getRequest().getParameter("p");
    String itemId = context.getRequest().getParameter("i");
    String version = context.getRequest().getParameter("v");
    String module = context.getRequest().getParameter("m");
    boolean showFull = (context.getRequest().getParameter("s") != null);
    FileItem thisItem = null;
    Connection db = null;
    Thumbnail thumbnail = null;
    try {
      db = getConnection(context);
      //TODO: The conditions under which a file can be attached will change, so this condition check needs to be refactored
      if (module != null) {
        if (!getUser(context).getAccessAdmin()) {
          return "PermissionError";
        }
        //Load the file
        thisItem = new FileItem(db, Integer.parseInt(itemId), Integer.parseInt(projectId), Integer.parseInt(module));
      } else {
        // Load the project and check permissions
        Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
        if (!hasProjectAccess(context, thisProject.getId(), "project-documents-files-download")) {
          return "PermissionError";
        }
        // Load the file
        thisItem = new FileItem(db, Integer.parseInt(itemId), thisProject.getId(), Constants.PROJECTS_FILES);
        if (!showFull) {
          thumbnail = ThumbnailUtils.retrieveThumbnail(db, thisItem, 0, 0, this.getPath(context, "projects"));
        }
      }
    } catch (Exception e) {
    } finally {
      this.freeConnection(context, db);
    }
    //Start the download
    try {
      FileDownload fileDownload = new FileDownload();
      if (thumbnail != null) {
        String filePath = this.getPath(context, "projects") + getDatePath(thisItem.getModified()) + thumbnail.getFilename();
        fileDownload.setFullPath(filePath);
        fileDownload.setFileTimestamp(thisItem.getModificationDate().getTime());
        fileDownload.streamThumbnail(context, thumbnail);
      } else {
        String filePath = null;
        if (context.getRequest().getParameter("s") != null) {
          filePath = this.getPath(context, "projects") + getDatePath(thisItem.getModified()) + thisItem.getFilename();
        } else {
          filePath = this.getPath(context, "projects") + getDatePath(thisItem.getModified()) + thisItem.getThumbnailFilename();
        }
        fileDownload.setFullPath(filePath);
        fileDownload.setDisplayName(thisItem.getThumbnailFilename());
        if (fileDownload.fileExists()) {
          fileDownload.setFileTimestamp(thisItem.getModificationDate().getTime());
          fileDownload.streamContent(context);
          return "-none-";
        } else {
          return "SystemERROR";
        }
      }
    } catch (java.net.SocketException se) {
      //User either canceled the download or lost connection
    } catch (Exception e) {
    }
    return ("-none-");
  }
}


