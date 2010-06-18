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
package com.concursive.connect.web.modules.classifieds.portlets.classifiedSearch;

import com.concursive.connect.web.portal.AbstractPortletModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Search portlet for classifieds
 *
 * @author Kailash Bhoopalam
 * @created December 11, 2009
 */
public class ClassifiedSearchPortlet extends AbstractPortletModule {

  // Logger
  private static Log LOG = LogFactory.getLog(ClassifiedSearchPortlet.class);

  // Viewers
  public static final String DEFAULT_VIEW = "list";
  public static final String LIST_VIEW = "list";


  public ClassifiedSearchPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }

  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(LIST_VIEW, new ClassifiedSearchViewer());
  }

}