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

package com.concursive.connect.cms.portal.actions;

import com.concursive.commons.images.ImageUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.beans.PortalBean;
import com.concursive.connect.cms.portal.dao.DashboardPage;
import com.concursive.connect.cms.portal.dao.DashboardTemplateList;
import com.concursive.connect.cms.portal.utils.DashboardUtils;
import com.concursive.connect.cms.portal.utils.WebPortalUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.IIndexerSearch;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.badges.dao.Badge;
import com.concursive.connect.web.modules.badges.utils.BadgeUtils;
import com.concursive.connect.web.modules.blog.dao.BlogPost;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.common.social.images.jobs.ImageResizerBean;
import com.concursive.connect.web.modules.common.social.images.jobs.ImageResizerJob;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.Thumbnail;
import com.concursive.connect.web.modules.documents.utils.FileInfo;
import com.concursive.connect.web.modules.documents.utils.HttpMultiPartParser;
import com.concursive.connect.web.modules.documents.utils.ThumbnailUtils;
import com.concursive.connect.web.modules.lists.dao.Task;
import com.concursive.connect.web.modules.lists.dao.TaskCategory;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.search.utils.SearchUtils;
import com.concursive.connect.web.portal.PortletManager;
import com.concursive.connect.web.utils.ClientType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Vector;

/**
 * Actions for working with the portal page
 *
 * @author matt rajkowski
 * @version $Id$
 * @created August 5, 2003
 */
public final class Portal extends GenericAction {

  private static Log LOG = LogFactory.getLog(Portal.class);

  /**
   * Shows the news of the portal project and a list of public projects the
   * user can choose from
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    setMaximized(context);
    ApplicationPrefs prefs = getApplicationPrefs(context);
    Connection db = null;
    try {
      db = getConnection(context);

      // Make sure the requested URL is up-to-date
      PortalBean bean = new PortalBean(context.getRequest());
      if (WebPortalUtils.isVersion1Url(bean, context, db)) {
        return "Redirect301";
      }
      if (WebPortalUtils.isUnexpectedHost(bean, context, prefs)) {
        return "Redirect301";
      }
      if (WebPortalUtils.isChangingLanguage(context, prefs, bean)) {
        return "Redirect301";
      }

      // Lookup the content from the dashboards file first...
      if (bean.getPortalPath() != null) {
        DashboardPage page = DashboardUtils.loadDashboardPage(DashboardTemplateList.TYPE_PORTAL, bean.getPortalPath() + "." + bean.getPortalExtension());
        if (page != null) {
          LOG.debug("Found portal template for rendering: " + page.getName());
          // A dashboard has a page
          context.getRequest().setAttribute("dashboardPage", page);
          // Set shared objects
          context.getRequest().setAttribute("portletView", context.getRequest().getParameter("view"));
          context.getRequest().setAttribute("portletParams", context.getRequest().getParameter("params"));
          context.getRequest().setAttribute("TEAM.KEY", context.getServletContext().getAttribute("TEAM.KEY"));
          // Set shared project searcher
          IIndexerSearch projectSearcher = null;
          if ("true".equals(context.getServletContext().getAttribute(Constants.DIRECTORY_INDEX_INITIALIZED))) {
            // Search public projects only
            LOG.debug("Using directory index...");
            projectSearcher = SearchUtils.retrieveSearcher(Constants.INDEXER_DIRECTORY);
          } else {
            // Use the full index because the directory hasn't loaded
            LOG.debug("Using full index...");
            projectSearcher = SearchUtils.retrieveSearcher(Constants.INDEXER_FULL);
          }
          String queryString =
              "(approved:1) " +
                  "AND (closed:0) " +
                  "AND (website:0) ";
          context.getRequest().setAttribute("projectSearcher", projectSearcher);
          context.getRequest().setAttribute("baseQueryString", queryString);
          // Render the pages
          boolean isAction = PortletManager.processPage(context, db, page);
          if (isAction) {
            return ("-none-");
          }
          return "ShowPortalPageOK";
        }
      }

      // Use case: a new style url is referenced (something.shtml)
      if (bean.getPortalPath() != null) {
        // Set the newsId and projectId (if it has one)
        BlogPostList.configureIdsByPortalPath(db, bean);
      }

      if (WebPortalUtils.hasRedirect(context, bean)) {
        return "Redirect301";
      }

      if (LOG.isDebugEnabled()) {
        ClientType clientType = (ClientType) context.getSession().getAttribute("clientType");
        LOG.debug("Client language: " + clientType.getLanguage());
        LOG.trace(bean.toString());
      }
      return null;
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
  public String executeCommandListItem(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String taskId = context.getRequest().getParameter("tid");
    Connection db = null;
    try {
      // Get the portal project
      ProjectList projects = new ProjectList();
      projects.setPortalState(Constants.TRUE);
      projects.setApprovedOnly(true);
      projects.setProjectId(Integer.parseInt(projectId));
      db = getConnection(context);
      // Load the requested portal project
      projects.buildList(db);
      if (projects.size() > 0) {
        // Get only the first returned (should only be one, but could be more)
        Project portal = (Project) projects.get(0);
        // Query the list item
        Task task = new Task(db, Integer.parseInt(taskId));
        if (task.getProjectId() == portal.getId()) {
          TaskCategory category = new TaskCategory(db, task.getCategoryId());
          context.getRequest().setAttribute("category", category);
          context.getRequest().setAttribute("task", task);
        }
        return ("ListItemOK");
      }
      return ("PermissionError");
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
  public String executeCommandNews(ActionContext context) {
    String projectId = context.getRequest().getParameter("pid");
    String newsId = context.getRequest().getParameter("nid");
    Connection db = null;
    try {
      // Get the portal project
      ProjectList projects = new ProjectList();
      projects.setPortalState(Constants.TRUE);
      projects.setApprovedOnly(true);
      projects.setProjectId(Integer.parseInt(projectId));
      db = getConnection(context);
      // Load the requested portal project
      projects.buildList(db);
      if (projects.size() > 0) {
        // Get only the first returned (should only be one, but could be more)
        Project portal = projects.get(0);
        // Query the list item
        BlogPost article = new BlogPost(db, Integer.parseInt(newsId));
        if (article.getProjectId() != portal.getId()) {
          return "PermissionError";
        }
        if (article.getStatus() == BlogPost.DRAFT) {
          return "PermissionError";
        } else if (article.getStatus() == BlogPost.UNAPPROVED) {
          return "PermissionError";
        }
        context.getRequest().setAttribute("newsArticle", article);
        return ("NewsDetailsPopupOK");
      }
      return ("PermissionError");
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
  public String executeCommandContact(ActionContext context) {
    return "ContactOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandImages(ActionContext context) {
    Connection db = null;
    try {
      db = getConnection(context);
      //Build the file item list
      FileItemList files = new FileItemList();
      files.setLinkModuleId(Constants.IMAGELIBRARY_FILES);
      files.buildList(db);
      context.getRequest().setAttribute("imageList", files);
      return "ImageListOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandImage(ActionContext context) {
    Connection db = null;
    FileItem fileItem = null;
    // Use the database connection
    try {
      db = getConnection(context);
      String itemId = context.getRequest().getParameter("i");
      fileItem = new FileItem(db, Integer.parseInt(itemId));
    } catch (Exception e) {
      LOG.error("executeCommandImage", e);
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    // Stream the file
    try {
      String filePath = this.getPath(context, "imageLibrary") + getDatePath(fileItem.getModified()) + fileItem.getFilename();
      FileDownload fileDownload = new FileDownload();
      fileDownload.setFullPath(filePath);
      fileDownload.setDisplayName(fileItem.getClientFilename());
      if (fileDownload.fileExists()) {
        fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        fileDownload.streamContent(context);
      }
    } catch (Exception e) {
      LOG.error("stream", e);
      e.printStackTrace(System.out);
    }
    return "-none-";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandThumbnailImage(ActionContext context) {
    Connection db = null;
    FileItem fileItem = null;
    // Use the database connection
    try {
      db = getConnection(context);
      String itemId = context.getRequest().getParameter("i");
      fileItem = new FileItem(db, Integer.parseInt(itemId));
    } catch (Exception e) {
      LOG.error("executeCommandThumbnailImage", e);
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    // Stream the file
    try {
      String filePath = this.getPath(context, "imageLibrary") + getDatePath(fileItem.getModified()) + fileItem.getThumbnailFilename();
      FileDownload fileDownload = new FileDownload();
      fileDownload.setFullPath(filePath);
      fileDownload.setDisplayName(fileItem.getClientFilename());
      if (fileDownload.fileExists()) {
        fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        fileDownload.streamContent(context);
      }
    } catch (Exception e) {
      LOG.error("stream", e);
      e.printStackTrace(System.out);
    }
    return "-none-";
  }


  /**
   * Action for displaying the Image Selector
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandImageSelect(ActionContext context) {
    Connection db = null;
    String constant = context.getRequest().getParameter("constant");
    String id = context.getRequest().getParameter("id");
    String news = context.getRequest().getParameter("nid");
    String project = context.getRequest().getParameter("pid");
    try {
      // Prepare parameters
      int linkModuleId = Integer.parseInt(constant);
      int linkItemId = Integer.parseInt(id);
      int newsId = -1;
      int projectId = -1;
      // From News Article tab
      if (linkModuleId == Constants.BLOG_POST_FILES) {
        newsId = linkItemId;
      } else {
        if (news != null) {
          newsId = Integer.parseInt(news);
        }
      }
      // From Project tab
      if (linkModuleId == Constants.PROJECTS_FILES) {
        projectId = linkItemId;
      }
      // From External URL tab
      if (linkModuleId == -1) {
        if (project != null) {
          projectId = Integer.parseInt(project);
        }
      }
      db = getConnection(context);
      if (newsId > -1) {
        // Load the news
        BlogPost blogPost = new BlogPost(db, newsId);
        projectId = blogPost.getProjectId();
        context.getRequest().setAttribute("newsArticle", blogPost);
      }
      if (projectId > -1) {
        // Load the project and check permissions
        Project thisProject = retrieveAuthorizedProject(projectId, context);
        if (newsId > -1) {
          if (!hasProjectAccess(context, thisProject.getId(), "project-news-view")) {
            return "PermissionError";
          }
        } else {
          if (!hasProjectAccess(context, thisProject.getId(), "project-documents-view")) {
            return "PermissionError";
          }
        }
        context.getRequest().setAttribute("project", thisProject);
      }
      //Build the image list
      FileItemList files = new FileItemList();
      files.setLinkModuleId(linkModuleId);
      files.setLinkItemId(linkItemId);
      if (linkModuleId > -1) {
        files.setWebImageFormatOnly(true);
        files.buildList(db);
      }
      context.getRequest().setAttribute("imageList", files);
      return "ImageSelectTINYMCEOK";
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
  }


  /**
   * Action for streaming a selected image, based on project permissions
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandImg(ActionContext context) {
    Connection db = null;
    String constant = context.getRequest().getParameter("constant");
    String id = context.getRequest().getParameter("id");
    String file = context.getRequest().getParameter("fid");
    String thumbnailValue = context.getRequest().getParameter("th");
    String url = context.getRequest().getParameter("url");
    String maxDimensions = null;
    if (url != null) {
      String[] values = url.split("-");
      constant = values[0];
      id = values[1];
      file = values[2];
      if (values.length > 3) {
        maxDimensions = values[3];
      }
      if (values.length > 4) {
        thumbnailValue = values[4];
      }
    }
    FileItem fileItem = null;
    FileDownload fileDownload = new FileDownload();
    Thumbnail thumbnail = null;

    try {
      int linkModuleId = Integer.parseInt(constant);
      int linkItemId = Integer.parseInt(id);
      int fileId = Integer.parseInt(file);
      boolean showThumbnail = "true".equals(thumbnailValue);
      db = getConnection(context);
      // Determine the image type and permissions around them
      if (linkModuleId == Constants.PROJECTS_FILES) {
        // Check project permissions
        Project thisProject = retrieveAuthorizedProject(linkItemId, context);
        // Check access to this project
        boolean allowed = false;
        if (thisProject.getPortal() && thisProject.getApproved()) {
          allowed = true;
        } else if (hasProjectAccess(context, thisProject.getId(), "project-documents-view")) {
          allowed = true;
        }
        if (!allowed) {
          return "PermissionError";
        }
        // Load the file for download
        fileItem = new FileItem(db, fileId);
        String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + (showThumbnail ? fileItem.getThumbnailFilename() : fileItem.getFilename());
        fileDownload.setFullPath(filePath);
        fileDownload.setDisplayName(fileItem.getClientFilename());
      } else if (linkModuleId == Constants.BLOG_POST_FILES) {
        // Load news article for reference
        BlogPost blogPost = new BlogPost(db, linkItemId);
        // Check project permissions, based on article
        Project thisProject = retrieveAuthorizedProject(blogPost.getProjectId(), context);
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
        fileItem = new FileItem(db, fileId);
        String filePath = this.getPath(context, "projects-news") + getDatePath(fileItem.getModified()) + (showThumbnail ? fileItem.getThumbnailFilename() : fileItem.getFilename());
        fileDownload.setFullPath(filePath);
        fileDownload.setDisplayName(fileItem.getClientFilename());
      } else if (linkModuleId == Constants.PROJECT_CATEGORY_FILES) {
        ProjectCategory category = ProjectUtils.loadProjectCategory(linkItemId);
        if (category == null || !category.getEnabled()) {
          return "PermissionError";
        }
        // Load the file for download
        fileItem = new FileItem(db, fileId);
        if (fileItem.getLinkModuleId() != linkModuleId || fileItem.getLinkItemId() != linkItemId) {
          return "PermissionError";
        }
        // Get the requested thumbnail, if not found, generate it
        if (fileItem.isImageFormat()) {
          String[] xy = maxDimensions.split("x");
          // Get a reference to the thumbnail, it will be created if it doesn't exist
          thumbnail = ThumbnailUtils.retrieveThumbnail(db, fileItem, Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), this.getPath(context, "projects"));
          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + thumbnail.getFilename();
          fileDownload.setFullPath(filePath);
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        }
      } else if (linkModuleId == Constants.BADGE_FILES) {
        Badge badge = BadgeUtils.loadBadge(linkItemId);
        if (badge == null || !badge.getEnabled()) {
          return "PermissionError";
        }
        // Load the file for download
        fileItem = new FileItem(db, fileId);
        if (fileItem.getLinkModuleId() != linkModuleId || fileItem.getLinkItemId() != linkItemId) {
          return "PermissionError";
        }
        // Get the requested thumbnail, if not found, generate it
        if (fileItem.isImageFormat()) {
          String[] xy = maxDimensions.split("x");
          // Get a reference to the thumbnail, it will be created if it doesn't exist
          thumbnail = ThumbnailUtils.retrieveThumbnail(db, fileItem, Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), this.getPath(context, "projects"));
          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + thumbnail.getFilename();
          fileDownload.setFullPath(filePath);
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        }
      } else if (linkModuleId == Constants.SITE_LOGO_FILES) {

        // Load the file for download
        fileItem = new FileItem(db, fileId);
        if (fileItem.getLinkModuleId() != linkModuleId) {
          return "PermissionError";
        }
        // Get the requested thumbnail, if not found, generate it
        if (fileItem.isImageFormat()) {
          String[] xy = maxDimensions.split("x");
          // Get a reference to the thumbnail, it will be created if it doesn't exist
          thumbnail = ThumbnailUtils.retrieveThumbnail(db, fileItem, Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), this.getPath(context, "projects"));
          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + thumbnail.getFilename();
          fileDownload.setFullPath(filePath);
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        }

      } else if (linkModuleId == Constants.TEMP_FILES) {

        // Load the file for download
        fileItem = new FileItem(db, fileId);
        if (fileItem.getLinkModuleId() != linkModuleId) {
          return "PermissionError";
        }
        if (fileItem.getEnteredBy() != getUser(context).getId()) {
          return "PermissionError";
        }
        // Get the requested thumbnail, if not found, generate it
        if (fileItem.isImageFormat()) {
          String[] xy = maxDimensions.split("x");
          // Get a reference to the thumbnail, it will be created if it doesn't exist
          thumbnail = ThumbnailUtils.retrieveThumbnail(db, fileItem, Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), this.getPath(context, "projects"));

          String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + thumbnail.getFilename();
          fileDownload.setFullPath(filePath);
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
        }

      } else if (linkModuleId == Constants.PROJECT_IMAGE_FILES) {
        // Check project permissions, based on article
        Project thisProject = retrieveAuthorizedProject(linkItemId, context);
        if (!hasProjectAccess(context, thisProject.getId(), "project-profile-view")) {
          return "PermissionError";
        }
        // Load the file for download
        fileItem = new FileItem(db, fileId);
        if (fileItem.getLinkModuleId() != linkModuleId || fileItem.getLinkItemId() != linkItemId) {
          return "PermissionError";
        }
        // Get the requested thumbnail, if not found, generate it
        if (fileItem.isImageFormat()) {
          String[] xy = null;
          if (maxDimensions != null) {
            xy = maxDimensions.split("x");
          }
          int width = 0;
          int height = 0;
          if (xy != null && xy.length == 2) {
            width = Integer.parseInt(xy[0]);
            height = Integer.parseInt(xy[1]);
          }
          String filename = null;
          boolean isThumbnail = width > 0 || height > 0;
          if (isThumbnail) {
            // Get a reference to the thumbnail
            thumbnail = ThumbnailUtils.prepareThumbnail(db, fileItem, Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), this.getPath(context, "projects"));
            if (thumbnail != null) {
              filename = thumbnail.getFilename();
            }
          } else {
            // Get a reference to the file
            filename = fileItem.getFilename();
            fileDownload.setDisplayName(fileItem.getClientFilename());
          }
          if (filename != null) {
            String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getModified()) + filename;
            fileDownload.setFullPath(filePath);
            fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
          } else if (isThumbnail) {
            // Prepare this image for thumbnail conversion
            String filePath = this.getPath(context, "projects") + getDatePath(fileItem.getEntered());
            ImageResizerBean bean = new ImageResizerBean();
            bean.setFileItemId(fileItem.getId());
            bean.setImagePath(filePath);
            bean.setImageFilename(fileItem.getFilename());
            bean.setEnteredBy(fileItem.getEnteredBy());
            bean.setWidth(Integer.parseInt(xy[0]));
            bean.setHeight(Integer.parseInt(xy[1]));
            // Add this to the ImageResizerJob to multi-thread the thumbnails
            Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute("Scheduler");
            ((Vector) scheduler.getContext().get(ImageResizerJob.IMAGE_RESIZER_ARRAY)).add(bean);
            scheduler.triggerJob("imageResizer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
            // Return a temporary image missing error
            return "404Error";
          } else {
            // couldn't find image
            return "404Error";
          }
        }
      }
    } catch (Exception e) {
      LOG.error("executeCommandImg", e);
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    try {
      // Stream the file
      if (thumbnail != null) {
        if (fileDownload.fileExists()) {
          fileDownload.streamThumbnail(context, thumbnail);
        }
      } else {
        if (fileItem != null && fileDownload.fileExists()) {
          fileDownload.setFileTimestamp(fileItem.getModificationDate().getTime());
          fileDownload.streamContent(context);
        }
      }
    } catch (Exception e) {
      LOG.error("stream", e);
      e.printStackTrace(System.out);
    }
    return null;
  }


  /**
   * Processes an uploaded image
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandUploadImage(ActionContext context) {
    Connection db = null;
    boolean recordInserted = false;
    String editor = null;
    try {
      String filePath = this.getPath(context, "projects-news");
      //Process the form data
      HttpMultiPartParser multiPart = new HttpMultiPartParser();
      multiPart.setUsePathParam(false);
      multiPart.setUseUniqueName(true);
      multiPart.setUseDateForFolder(true);
      multiPart.setExtensionId(getUserId(context));
      HashMap parts = multiPart.parseData(context.getRequest(), filePath);
      String newsId = (String) parts.get("nid");
      String subject = (String) parts.get("subject");
      editor = (String) parts.get("editor");
      if (editor == null) {
        editor = context.getParameter("editor");
      }
      db = getConnection(context);
      // News
      BlogPost blogPost = new BlogPost(db, Integer.parseInt(newsId));
      context.getRequest().setAttribute("newsArticle", blogPost);
      // Project
      Project thisProject = retrieveAuthorizedProject(blogPost.getProjectId(), context);
      if (!hasProjectAccess(context, thisProject.getId(), "project-news-add")) {
        //TODO: Should delete the uploads, then exit
        return "PermissionError";
      }
      context.getRequest().setAttribute("project", thisProject);
      // Update the database with the resulting file
      if ((Object) parts.get("id" + newsId) instanceof FileInfo) {
        FileInfo newFileInfo = (FileInfo) parts.get("id" + newsId);

        FileItem thisItem = new FileItem();
        thisItem.setLinkModuleId(Constants.BLOG_POST_FILES);
        thisItem.setLinkItemId(blogPost.getId());
        thisItem.setEnteredBy(getUserId(context));
        thisItem.setModifiedBy(getUserId(context));
        thisItem.setSubject("News Article Image");
        thisItem.setClientFilename(newFileInfo.getClientFileName());
        thisItem.setFilename(newFileInfo.getRealFilename());
        thisItem.setSize(newFileInfo.getSize());
        // this is a new document
        thisItem.setVersion(1.0);
        recordInserted = thisItem.insert(db);
        thisItem.setDirectory(filePath);
        if (!recordInserted) {
          processErrors(context, thisItem.getErrors());
        } else {
          if (thisItem.isImageFormat()) {
            //Create a thumbnail if this is an image
            File thumbnailFile = new File(newFileInfo.getLocalFile().getPath() + "TH");
            Thumbnail thumbnail = new Thumbnail(ImageUtils.saveThumbnail(newFileInfo.getLocalFile(), thumbnailFile, 133d, 133d, "jpg"));
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
        context.getRequest().setAttribute("popup", "true");
        context.getRequest().setAttribute("PageLayout", "/layout1.jsp");
        // Image List
        FileItemList files = new FileItemList();
        files.setLinkModuleId(Constants.BLOG_POST_FILES);
        files.setLinkItemId(blogPost.getId());
        files.setWebImageFormatOnly(true);
        files.buildList(db);
        context.getRequest().setAttribute("imageList", files);
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
      freeConnection(context, db);
    }
    LOG.debug("Editor: " + editor);
    return "UploadImageTINYMCEOK";
  }

  public String executeCommandShowPortalPage(ActionContext context) {
    Connection db = null;
    try {
      String pageName = context.getRequest().getParameter("name");
      db = this.getConnection(context);
      // Find the portal page
      DashboardPage page = DashboardUtils.loadDashboardPage(DashboardTemplateList.TYPE_PAGES, pageName);
      if (page == null) {
        return "SystemError";
      }
      context.getRequest().setAttribute("dashboardPage", page);
      // Set shared values
      context.getRequest().setAttribute("portletView", context.getRequest().getParameter("view"));
      context.getRequest().setAttribute("portletParams", context.getRequest().getParameter("params"));
      context.getRequest().setAttribute("TEAM.KEY", context.getServletContext().getAttribute("TEAM.KEY"));
      // Set shared project searcher
      IIndexerSearch projectSearcher = null;
      if ("true".equals(context.getServletContext().getAttribute(Constants.DIRECTORY_INDEX_INITIALIZED))) {
        // Search public projects only
        LOG.debug("Using directory index...");
        projectSearcher = SearchUtils.retrieveSearcher(Constants.INDEXER_DIRECTORY);
      } else {
        // Use the full index because the directory hasn't loaded
        LOG.debug("Using full index...");
        projectSearcher = SearchUtils.retrieveSearcher(Constants.INDEXER_FULL);
      }
      String queryString =
          "(approved:1) " +
              "AND (closed:0) " +
              "AND (website:0) ";
      context.getRequest().setAttribute("projectSearcher", projectSearcher);
      context.getRequest().setAttribute("baseQueryString", queryString);
      boolean isAction = PortletManager.processPage(context, db, page);
      if (isAction) {
        return ("-none-");
      }
      return "ShowPortalPageOK";
    } catch (Exception errorMessage) {
      context.getRequest().setAttribute("Error", errorMessage);
      errorMessage.printStackTrace(System.out);
      LOG.error("executeCommandShowPortalPage", errorMessage);
      return ("SystemError");
    } finally {
      this.freeConnection(context, db);
    }
  }
}
