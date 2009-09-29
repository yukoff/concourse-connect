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
package com.concursive.connect.web.taglibs;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.controller.beans.URLControllerBean;
import static com.concursive.connect.web.portal.PortalUtils.*;

import java.sql.Connection;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagLogList;
import com.concursive.connect.web.modules.login.dao.User;
import javax.servlet.http.HttpServletRequest;

/**
 * Builds the panel for setting up tags
 *
 * @author Nanda Kumar
 * @created Aug 17, 2009
 */
public class TagsHandler extends TagSupport {

  private String url = null;
//  private boolean showAddEdit = false;
//  private int linkItemId = -1;
//  private String moduleName = null;

  public TagsHandler() {
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

//  public boolean isShowAddEdit() {
//    return showAddEdit;
//  }
//
//  public void setShowAddEdit(boolean showAddEdit) {
//    this.showAddEdit = showAddEdit;
//  }
//
//  public void setLinkItemId(int linkItemId) {
//    this.linkItemId = linkItemId;
//  }
//
//  public int getLinkItemId() {
//    return linkItemId;
//  }
//
//  public void setModule(String module) {
//    this.moduleName = module;
//  }
//
//  public String getModule() {
//    return moduleName;
//  }

  public int doStartTag() throws JspException {
    try {
      // Get the portlet's database connection
      Connection db = getConnection((PortletRequest) pageContext.getRequest());

      // Get current user
      User user = getUser((PortletRequest) pageContext.getRequest());

      // get the modules details from the submitted url
      String context = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
      URLControllerBean bean = new URLControllerBean(url, context);
      String moduleName = bean.getDomainObject();
      int linkItemId = Integer.parseInt(bean.getObjectValue());

      // Show a list of tags for this item
      TagLogList tagLogList = new TagLogList();
      tagLogList.setTableName(ModuleUtils.getTableFromModuleName(moduleName));
      tagLogList.setUniqueField(ModuleUtils.getPrimaryKeyFromModuleName(moduleName));
      //tagLogList.setUserId(currentUserId);
      tagLogList.setLinkItemId(linkItemId);
      tagLogList.buildList(db);

      // Generate and output the HTML
      String html = "Tags: " +
          "<span id='message_" + linkItemId + "'>" +
          StringUtils.toHtml(tagLogList.getTagsAsString()) +
          "</span>" +
          (user.isLoggedIn() ? " <a href=\"javascript:showPanel('Set Tags','" + url + "',500,'message_" + linkItemId + "');\">(add/edit)</a>" : "");
      pageContext.getOut().print(html);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
    return EVAL_PAGE;
  }
}
