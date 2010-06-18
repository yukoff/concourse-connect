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
package com.concursive.connect.web.modules.promotions.portlets.promotionSearch;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * Search for promotions
 *
 * @author Kailash Bhoopalam
 * @created December 11, 2009
 */
public class PromotionSearchViewer implements IPortletViewer {

  // Pages
  private static final String VIEW_PAGE = "/portlets/promotion_search/promotion_search-view.jsp";

  // Attribute names for objects available in the view
  private static final String PAGE_URL = "pageUrl";
  private static final String SORT_ORDER = "sort";
  private static final String QUERY = "query";
  private static final String LOCATION = "location";

  //Preferences
  private static final String PREF_PAGE_URL = "pageURL";

  public String doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    try {
      String defaultView = VIEW_PAGE;

      String categoryValue = PortalUtils.getPageView(request);
      String pageURL = request.getPreferences().getValue(PREF_PAGE_URL, "/page/promotions/all");
      pageURL = StringUtils.replace(pageURL, "${category}", categoryValue);
      String[] params = PortalUtils.getPageParameters(request);
      if (params != null){
      	int i = 0;
      	pageURL += "/";
      	while (i < params.length){
      		pageURL += params[i];
      		if (i + 1 < params.length){
      			pageURL += "/";
      		}
      		i++;
      	}
      }
      
      request.setAttribute(PAGE_URL, pageURL);
      request.setAttribute(SORT_ORDER, PortalUtils.getQueryParameter(request, "sort"));
      request.setAttribute(QUERY, PortalUtils.getQueryParameter(request, "query"));
      request.setAttribute(LOCATION, PortalUtils.getQueryParameter(request, "location"));
      
      // JSP view
      return defaultView;
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new PortletException(e.getMessage());
    }
  }
}
