/*
 * ConcourseConnect
 * Copyright 2010 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect and is licensed under a commercial
 * license, not an open source license.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.web.modules.common.social.images.portlets;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.getUser;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.sql.Connection;

/**
 * Carousel viewer for image carousel control
 *
 * @author Nanda Kumar
 * @created January 18, 2010
 */

public class CarouselViewer implements IPortletViewer {

  private static Log LOG = LogFactory.getLog(CarouselViewer.class);

  // Pages
  private static final String VIEW_PAGE = "/portlets/carousel/carousel-view.jsp";
  private static final String AJAX_VIEW_PAGE = "/portlets/carousel/carousel-view_ajax.jsp";

  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_CATEGORY = "category";
  private static final String PREF_WIDTH = "width";
  private static final String PREF_NUMBER_VISIBLE = "visiblePhotos";
  private static final String PREF_LIMIT = "limit";

  // Object Results
  private static final String FILE_ITEM_LIST = "fileItemList";
  private static final String TITLE = "title";
  private static final String LIMIT = "limit";
  private static final String MAX_RECORDS = "maxRecords";
  private static final String WIDTH = "width";
  private static final String NUMBER_VISIBLE = "visiblePhotos";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {

    String defaultView = VIEW_PAGE;

    // Check if the user is logged in
    User user = getUser(request);

    // If this is a sensitive site, and the user isn't logged in, then don't show
    if (PortalUtils.isPortletInProtectedMode(request) && !user.isLoggedIn()) {
      return null;
    }

    // Read the preferences
    int limit = Integer.parseInt(request.getPreferences().getValue(PREF_LIMIT, "10"));
    String projectCategoryName = request.getPreferences().getValue(PREF_CATEGORY, null);
    int visiblePerPage = Integer.parseInt(request.getPreferences().getValue(PREF_NUMBER_VISIBLE, "1"));

    // Determine the category id
    int projectCategoryId = -1;
    if (StringUtils.hasText(projectCategoryName)) {
      ProjectCategoryList projectCategoryList = (ProjectCategoryList) request.getAttribute(Constants.REQUEST_TAB_CATEGORY_LIST);
      if (projectCategoryList.size() > 0) {
        projectCategoryId = projectCategoryList.getIdFromValue(projectCategoryName);
      }
    }

    // Determine the offset to read photos from
    PagedListInfo pagedListInfo = new PagedListInfo();
    pagedListInfo.setDefaultSort("f.entered", "desc");
    
    // Sometimes we have to take more data than the default limit
    String extraLimit = request.getParameter("limit");
    if(extraLimit != null && Integer.parseInt(extraLimit) > (visiblePerPage * 2)) {
    	pagedListInfo.setItemsPerPage(extraLimit);
    } else {
    	pagedListInfo.setItemsPerPage(visiblePerPage * 2);
    }
    
    String offset = request.getParameter("offset");
    if (offset != null) {
      pagedListInfo.setCurrentOffset(Integer.parseInt(offset));
      defaultView = AJAX_VIEW_PAGE;
    }

    // Load the list of photos to show in this page
    Connection db = PortalUtils.useConnection(request);
    FileItemList fileItemList = new FileItemList();
    fileItemList.setLinkModuleId(Constants.PROJECT_IMAGE_FILES);
    fileItemList.setProjectCategoryId(projectCategoryId);
    // Show the user their photos, unless this portlet is cached
    if (!PortalUtils.getDashboardPortlet(request).isCached() && user.isLoggedIn()) {
      fileItemList.setForProjectUser(user.getId());
    } else {
      fileItemList.setPublicProjectFiles(Constants.TRUE);
    }
    fileItemList.setPagedListInfo(pagedListInfo);
    fileItemList.buildList(db);
    if (LOG.isDebugEnabled()) {
      LOG.debug("current offset = " + pagedListInfo.getCurrentOffset());
      LOG.debug("items found = " + fileItemList.size());
      LOG.debug("max records = " + pagedListInfo.getMaxRecords());
    }

    // Hide the portlet when there are no photos
    if (fileItemList.size() == 0) {
      return null;
    }

    // Provide the objects to the view
    request.setAttribute(TITLE, request.getPreferences().getValue(PREF_TITLE, "Latest Photos"));
    request.setAttribute(NUMBER_VISIBLE, String.valueOf(visiblePerPage));
    request.setAttribute(WIDTH, request.getPreferences().getValue(PREF_WIDTH, "334"));
    request.setAttribute(FILE_ITEM_LIST, fileItemList);
    request.setAttribute(LIMIT, limit);

    // Limit the paging to the preferences
    if (fileItemList.getPagedListInfo().getMaxRecords() > limit) {
      request.setAttribute(MAX_RECORDS, limit);
    } else {
      request.setAttribute(MAX_RECORDS, fileItemList.getPagedListInfo().getMaxRecords());
    }

    // JSP view
    return defaultView;
  }
}
