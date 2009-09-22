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
package com.concursive.connect.web.modules.wiki.actions;

import com.concursive.commons.images.ImageUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.controller.beans.URLControllerBean;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.documents.utils.ThumbnailUtils;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.concursive.connect.web.modules.wiki.utils.HTMLToWikiUtils;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Actions for working with Wikis
 *
 * @author matt rajkowski
 * @version $Id: ProjectManagementWiki.java,v 1.1 2003/06/25 04:58:38 matt Exp
 *          $
 * @created February 7, 2006
 */
public final class ProjectManagementWiki extends GenericAction {

  public String executeCommandImg(ActionContext context) {
    Connection db = null;
    String pid = context.getRequest().getParameter("pid");
    String filename = context.getRequest().getParameter("subject");
    String thumbnailValue = context.getRequest().getParameter("th");
    FileDownload fileDownload = null;
    FileItem fileItem = null;
    Thumbnail thumbnail = null;
    try {
      int projectId = Integer.parseInt(pid);
      boolean showThumbnail = "true".equals(thumbnailValue);
      fileDownload = new FileDownload();
      db = getConnection(context);
      // Check project permissions
      Project thisProject = retrieveAuthorizedProject(projectId, context);
      // Check access to this project
      boolean allowed = false;
      if (thisProject.getPortal() && thisProject.getApproved()) {
        allowed = true;
      } else if (hasProjectAccess(context, thisProject.getId(), "project-wiki-view")) {
        allowed = true;
      }
      if (!allowed) {
        return "PermissionError";
      }
      // Load the file for download
      FileItemList fileItemList = new FileItemList();
      fileItemList.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
      fileItemList.setLinkItemId(projectId);
      fileItemList.setFilename(filename);
      fileItemList.buildList(db);
      if (fileItemList.size() > 0) {
        fileItem = fileItemList.get(0);
        if (showThumbnail) {
          thumbnail = ThumbnailUtils.retrieveThumbnail(db, fileItem, 0, 0, this.getPath(context, "projects"));
          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + thumbnail.getFilename();
          fileDownload.setFullPath(filePath);
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        } else {
          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + (showThumbnail ? fileItem.getThumbnailFilename() : fileItem.getFilename());
          fileDownload.setFullPath(filePath);
          fileDownload.setDisplayName(fileItem.getClientFilename());
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    try {
      // Stream the file
      if (thumbnail != null) {
        fileDownload.streamThumbnail(context, thumbnail);
      } else if (fileItem != null && fileDownload.fileExists()) {
        fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        fileDownload.streamContent(context);
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
    return null;
  }

  public String executeCommandImageSelect(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-wiki-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Load the file for download
      FileItemList imageList = new FileItemList();
      imageList.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
      imageList.setLinkItemId(thisProject.getId());
      imageList.buildList(db);
      context.getRequest().setAttribute("imageList", imageList);
      return ("ImageSelectOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandUploadImage(ActionContext context) {
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
      String projectId = (String) parts.get("pid");
      String subject = (String) parts.get("subject");
      db = getConnection(context);
      // Project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-wiki-add")) {
        //TODO: Should delete the uploads, then exit
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Update the database with the resulting file
      if (parts.get("id" + projectId) instanceof FileInfo) {
        FileInfo newFileInfo = (FileInfo) parts.get("id" + projectId);
        FileItem thisItem = new FileItem();
        thisItem.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
        thisItem.setLinkItemId(thisProject.getId());
        thisItem.setEnteredBy(getUserId(context));
        thisItem.setModifiedBy(getUserId(context));
        thisItem.setSubject("Wiki Image");
        thisItem.setClientFilename(newFileInfo.getClientFileName());
        thisItem.setFilename(newFileInfo.getRealFilename());
        thisItem.setSize(newFileInfo.getSize());
        // Verify the integrity of the image
        thisItem.setImageSize(ImageUtils.getImageSize(newFileInfo.getLocalFile()));
        if (thisItem.getImageWidth() == 0 || thisItem.getImageHeight() == 0) {
          // A bad image was sent
          return ("ImageUploadERROR");
        }
        // check to see if this filename already exists for automatic versioning
        FileItemList fileItemList = new FileItemList();
        fileItemList.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
        fileItemList.setLinkItemId(thisProject.getId());
        fileItemList.setFilename(newFileInfo.getClientFileName());
        fileItemList.buildList(db);
        if (fileItemList.size() == 0) {
          // this is a new document
          thisItem.setVersion(1.0);
          recordInserted = thisItem.insert(db);
        } else {
          // this is a new version of an existing document
          FileItem previousItem = fileItemList.get(0);
          thisItem.setId(previousItem.getId());
          thisItem.setVersion(previousItem.getVersionNextMajor());
          recordInserted = thisItem.insertVersion(db);
        }
        thisItem.setDirectory(filePath);
        if (!recordInserted) {
          processErrors(context, thisItem.getErrors());
        } else {
          if (thisItem.isImageFormat() && thisItem.hasValidImageSize()) {
            // Create a thumbnail if this is an image
            String format = thisItem.getExtension().substring(1);
            File thumbnailFile = new File(newFileInfo.getLocalFile().getPath() + "TH");
            Thumbnail thumbnail = new Thumbnail(ImageUtils.saveThumbnail(newFileInfo.getLocalFile(), thumbnailFile, 200d, 200d, format));
            if (thumbnail != null) {
              // Store thumbnail in database
              thumbnail.setId(thisItem.getId());
              thumbnail.setFilename(newFileInfo.getRealFilename() + "TH");
              thumbnail.setVersion(thisItem.getVersion());
              thumbnail.setSize((int) thumbnailFile.length());
              thumbnail.setEnteredBy(thisItem.getEnteredBy());
              thumbnail.setModifiedBy(thisItem.getModifiedBy());
              recordInserted = thumbnail.insert(db);
            }
          }
        }
        context.getRequest().setAttribute("popup", "true");
        context.getRequest().setAttribute("PageLayout", "/layout1.jsp");
        // Image List
        FileItemList imageList = new FileItemList();
        imageList.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
        imageList.setLinkItemId(thisProject.getId());
        imageList.buildList(db);
        context.getRequest().setAttribute("imageList", imageList);
        // Send the image name so it can be auto-selected
        context.getRequest().setAttribute("uploadedImage", newFileInfo.getClientFileName());
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    return "UploadImageOK";
  }

  public String executeCommandLinkSelect(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String content = StringUtils.fromHtmlValue(context.getRequest().getParameter("content"));
    String link = context.getRequest().getParameter("link");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-wiki-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Use WikiLink to determine type of link
      //<a class="wikiLink newWiki" title="other pages" href="/ProjectManagementWiki.do?command=Edit&amp;pid=139&amp;subject=other+pages">other pages</a>
      //<a class="wikiLink external" href="http://www.cnn.com" target="_blank">http://www.cnn.com</a>
      //<p><a class="wikiLink newWiki" title="called something else" href="/ProjectManagementWiki.do?command=Edit&amp;pid=139&amp;subject=A+link">called something else</a></p>
      //<p><a class="wikiLink external" href="http://www.concursive.com" target="_blank">called something else</a></p>
      String contextPath = context.getRequest().getContextPath();
      if (!StringUtils.hasText(contextPath)) {
        contextPath = "";
      }
      if (HTMLToWikiUtils.isExternalLink(link, contextPath)) {
        context.getRequest().setAttribute("link", link);
      } else {
        // Check to see if the target wiki exists
        String subject = null;
        if (StringUtils.hasText(link)) {
          URLControllerBean url = new URLControllerBean(link, contextPath);
          subject = StringUtils.jsUnEscape(url.getObjectValue());
          subject = StringUtils.replace(subject, "+", " ");
        } else {
          if (HTMLToWikiUtils.isExternalLink(content, contextPath)) {
            context.getRequest().setAttribute("link", content);
          } else {
            subject = content;
          }
        }
        if (subject != null) {
          Wiki targetWiki = WikiList.queryBySubject(db, subject, thisProject.getId());
          context.getRequest().setAttribute("targetWiki", targetWiki);
        }
      }
      // Regardless, send the displayed value
      context.getRequest().setAttribute("content", content);
      return ("LinkSelectOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandVideoSelect(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-wiki-add")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      return ("VideoSelectOK");
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }
}
