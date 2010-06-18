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
package com.concursive.connect.web.modules.activity.portlets.activityStream;

import com.concursive.connect.web.portal.AbstractPortletModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * View activity stream controller
 *
 * @author Kailash Bhoopalam
 * @created March 04, 2009
 */
public class ActivityStreamPortlet extends AbstractPortletModule {

  // Logger
  private static Log LOG = LogFactory.getLog(ActivityStreamPortlet.class);

  // Viewers
  public static final String DEFAULT_VIEW = "list";
  public static final String LIST_VIEW = "list";

  // Actions
  public static final String SAVE_FORM_ACTION = "saveForm";
  public static final String SAVE_REPLY_FORM_ACTION = "reply-saveForm";
  public static final String DELETE_ACTION = "delete";

  public ActivityStreamPortlet() {
    defaultCommand = DEFAULT_VIEW;
  }


  protected void doPopulateActionsAndViewers() {
    // Viewers
    viewers.put(LIST_VIEW, new ActivityStreamViewer());
    
    //Actions
    actions.put(SAVE_REPLY_FORM_ACTION, new SaveActivityReplyAction());
    actions.put(DELETE_ACTION, new DeleteActivityAction());
  }
}