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
package com.concursive.connect.web.modules.activity.beans;

import com.concursive.commons.web.mvc.beans.GenericBean;

/**
 * Represents the properties of an activity reply
 *
 * @author Matt Rajkowski
 * @created Jan 19, 2010
 */
public class ProjectHistoryReplyBean extends GenericBean {
  private String description = null;
  private int parentId = -1;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getParentId() {
    return parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = Integer.parseInt(parentId);
  }
}
