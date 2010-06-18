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

import com.concursive.connect.web.portal.AbstractPortletModule;

/**
 * CarouselPortlet
 *
 * @author Nanda Kumar
 * @created January 18, 2010
 */
public class CarouselPortlet extends AbstractPortletModule {

  //private static Log LOG = LogFactory.getLog(CarouselPortlet.class);

  // Viewers
  public static final String LIST_VIEW = "list";
  public static final String DEFAULT_VIEW = LIST_VIEW;


  public CarouselPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }

  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(LIST_VIEW, new CarouselViewer());

  }
}
