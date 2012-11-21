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
package com.concursive.connect.web.modules.blog.actions;

import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import com.concursive.commons.images.ImageUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.blog.dao.BlogPostCategoryList;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.documents.utils.ThumbnailUtils;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.members.dao.TeamMember;
import com.concursive.connect.web.modules.members.dao.TeamMemberList;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.utils.HtmlSelect;
import freemarker.template.Template;
import freemarker.template.Configuration;

import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Actions for working with blog posts
 *
 * @author matt rajkowski
 * @created June 24, 2003
 */
public final class BlogActions extends GenericAction {

  public String executeCommandDetails(ActionContext context) {
    // BlogActions.do?command=Details&pid=139&id=535&popup=true /show/xyz/blog/535
    String projectId = context.getRequest().getParameter("pid");
    String id = context.getRequest().getParameter("id");
    String redirect = "/show/" + ProjectUtils.loadProject(Integer.parseInt(projectId)).getUniqueId() + "/blog/" + id;
    context.getRequest().setAttribute("redirectTo", redirect);
    context.getRequest().removeAttribute("PageLayout");
    return "Redirect301";
  }

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandEditCategoryList(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String previousId = context.getRequest().getParameter("previousId");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-add")) {
        return "PermissionError";
      }
      // Load the category list
      BlogPostCategoryList categoryList = new BlogPostCategoryList();
      categoryList.setProjectId(thisProject.getId());
      categoryList.setEnabled(Constants.TRUE);
      categoryList.buildList(db);
      context.getRequest().setAttribute("editList", categoryList.getHtmlSelect());
      // Edit List properties
      context.getRequest().setAttribute("subTitleKey", "projectManagementNews.subtitle");
      context.getRequest().setAttribute("subTitle", "Modify the blog post categories");
      context.getRequest().setAttribute("returnUrl", ctx(context) + "/BlogActions.do?command=SaveCategoryList&pid=" + thisProject.getId() + "&previousId=" + previousId);
      return ("EditListPopupOK");
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
  public String executeCommandSaveCategoryList(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String previousId = context.getRequest().getParameter("previousId");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-add")) {
        return "PermissionError";
      }
      // Parse the request for items
      String[] params = context.getRequest().getParameterValues("selectedList");
      String[] names = new String[params.length];
      int j = 0;
      StringTokenizer st = new StringTokenizer(context.getRequest().getParameter("selectNames"), "^");
      while (st.hasMoreTokens()) {
        names[j] = st.nextToken();
        if (System.getProperty("DEBUG") != null) {
          System.out.println("ProjectManagementNews-> Item: " + names[j]);
        }
        j++;
      }
      // Load the previous category list
      BlogPostCategoryList categoryList = new BlogPostCategoryList();
      categoryList.setProjectId(thisProject.getId());
      categoryList.buildList(db);
      categoryList.updateValues(db, params, names);
      // Reload the updated list for display
      categoryList.clear();
      categoryList.setEnabled(Constants.TRUE);
      categoryList.setIncludeId(previousId);
      categoryList.buildList(db);
      HtmlSelect thisSelect = categoryList.getHtmlSelect();
      thisSelect.addItem(-1, "-- None --", 0);
      context.getRequest().setAttribute("editList", thisSelect);
      return ("EditListPopupCloseOK");
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
  public String executeCommandEmailMe(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String newsId = context.getRequest().getParameter("id");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-view")) {
        return "PermissionError";
      }
      // Load the article and send the email
      BlogPost thisArticle = new BlogPost(db, Integer.parseInt(newsId), thisProject.getId());
      if (1 == 1) {
        ApplicationPrefs prefs = getApplicationPrefs(context);
        SMTPMessage mail = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
        mail.setFrom(this.getPref(context, ApplicationPrefs.EMAILADDRESS));
        mail.addReplyTo(getUser(context).getEmail());
        mail.setType("text/html");
        mail.addTo(getUser(context).getEmail());
        mail.setSubject(thisArticle.getSubject());
        // Populate the message template
        Template template = getFreemarkerConfiguration(context).getTemplate("blog_article_email_me_notification-html.ftl");
        Map bodyMappings = new HashMap();
        bodyMappings.put("post", thisArticle);
        bodyMappings.put("link", new HashMap());
        ((Map) bodyMappings.get("link")).put("post", getLink(context, "show/" + thisProject.getUniqueId() + "/post/" + thisArticle.getId()));
        // Parse and send
        StringWriter inviteBodyTextWriter = new StringWriter();
        template.process(bodyMappings, inviteBodyTextWriter);
        mail.setBody(inviteBodyTextWriter.toString());
        mail.send();
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "news_email_ok");
      context.getRequest().setAttribute("pid", projectId);
      return "EmailMeOK";
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
  public String executeCommandEmailTeam(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String newsId = context.getRequest().getParameter("id");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-add")) {
        return "PermissionError";
      }
      // Load the article and send the email
      BlogPost post = new BlogPost(db, Integer.parseInt(newsId), thisProject.getId());
      if (1 == 1) {
        ApplicationPrefs prefs = getApplicationPrefs(context);
        SMTPMessage mail = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
        mail.setFrom(this.getPref(context, ApplicationPrefs.EMAILADDRESS));
        mail.setType("text/html");
        mail.setSubject("[" + thisProject.getTitle() + "] Blog Post");
        // Populate the message template
        Template template = getFreemarkerConfiguration(context).getTemplate("blog_notification-html.ftl");
        Map bodyMappings = new HashMap();
        bodyMappings.put("project", thisProject);
        bodyMappings.put("post", post);
        bodyMappings.put("author", UserUtils.loadUser(post.getEnteredBy()));
        bodyMappings.put("link", new HashMap());
        ((Map) bodyMappings.get("link")).put("site", getServerUrl(context));
        ((Map) bodyMappings.get("link")).put("blog", getLink(context, "show/" + thisProject.getUniqueId() + "/blog"));
        ((Map) bodyMappings.get("link")).put("post", getLink(context, "show/" + thisProject.getUniqueId() + "/post/" + post.getId()));
        ((Map) bodyMappings.get("link")).put("settings", getLink(context, "show/" + thisProject.getUniqueId() + "/members"));
        // Send to those members that requested notifications
        TeamMemberList members = new TeamMemberList();
        members.setProjectId(thisProject.getId());
        members.setWithNotificationsSet(Constants.TRUE);
        members.buildList(db);
        for (TeamMember thisMember : members) {
          User recipient = UserUtils.loadUser(thisMember.getUserId());
          if (StringUtils.hasText(recipient.getEmail())) {
            // Tailor the email to the recipient
            bodyMappings.put("recipient", UserUtils.loadUser(thisMember.getUserId()));
            // Parse and send
            StringWriter inviteBodyTextWriter = new StringWriter();
            template.process(bodyMappings, inviteBodyTextWriter);
            mail.setBody(inviteBodyTextWriter.toString());
            mail.setTo(recipient.getEmail());
            mail.send();
          }
        }
      }
      context.getRequest().setAttribute("project", thisProject);
      context.getRequest().setAttribute("IncludeSection", "news_email_ok");
      context.getRequest().setAttribute("pid", projectId);
      return "EmailTeamOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }

  public String executeCommandArchive(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    String newsId = context.getRequest().getParameter("id");
    boolean recordUpdated = false;
    try {
      db = getConnection(context);
      //Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-edit")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Update the archive status of the article
      BlogPost thisArticle = new BlogPost(db, Integer.parseInt(newsId), thisProject.getId());
      context.getRequest().setAttribute("newsArticle", thisArticle);
      thisArticle.setModifiedBy(getUserId(context));
      recordUpdated = thisArticle.archive(db);
      if (recordUpdated) {
        indexAddItem(context, thisArticle);
      } else {
        processErrors(context, thisArticle.getErrors());
      }
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
    if (recordUpdated) {
      return ("ArchiveOK");
    } else {
      return ("ArchiveERROR");
    }
  }

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
      } else if (hasProjectAccess(context, thisProject.getId(), "project-news-view")) {
        allowed = true;
      }
      if (!allowed) {
        return "PermissionError";
      }
      // Load the file for download
      FileItemList fileItemList = new FileItemList();
      fileItemList.setLinkModuleId(Constants.PROJECT_BLOG_FILES);
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
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-view")) {
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Load the file for download
      FileItemList imageList = new FileItemList();
      imageList.setLinkModuleId(Constants.PROJECT_BLOG_FILES);
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
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-edit")) {
        //TODO: Should delete the uploads, then exit
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Update the database with the resulting file
      if (parts.get("id" + projectId) instanceof FileInfo) {
        FileInfo newFileInfo = (FileInfo) parts.get("id" + projectId);
        FileItem thisItem = new FileItem();
        thisItem.setLinkModuleId(Constants.PROJECT_BLOG_FILES);
        thisItem.setLinkItemId(thisProject.getId());
        thisItem.setEnteredBy(getUserId(context));
        thisItem.setModifiedBy(getUserId(context));
        thisItem.setSubject("Blog Image");
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
        fileItemList.setLinkModuleId(Constants.PROJECT_BLOG_FILES);
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
        context.getRequest().setAttribute(Constants.REQUEST_PAGE_LAYOUT, "/layout1.jsp");
        // Image List
        FileItemList imageList = new FileItemList();
        imageList.setLinkModuleId(Constants.PROJECT_BLOG_FILES);
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

  public String executeCommandVideoSelect(ActionContext context) {
    Connection db = null;
    //Parameters
    String projectId = context.getRequest().getParameter("pid");
    try {
      db = getConnection(context);
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-view")) {
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
