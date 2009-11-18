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
package com.concursive.connect.web.modules.documents.portlets.main;

import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getConnection;
import static com.concursive.connect.web.portal.PortalUtils.getUser;

import java.sql.Connection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.ModuleUtils;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.common.social.tagging.dao.UserTagLogList;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import com.concursive.connect.web.utils.PagedListInfo;

/**
 * Updates users tag for a module and link
 *
 * @author Nanda Kumar
 * @created Oct 6, 2009
 */
public class SaveTagsAction implements IPortletAction {
  private static final String TAGS = "tagsText";
  private static final String CURRENT_TAGS = "ajaxMessage";
  private static final String POPUP = "popup";
  
	public SaveTagsAction() {
	}

	public GenericBean processAction(ActionRequest request,	ActionResponse response) throws Exception {
    // get the modules details
    int linkItemId = PortalUtils.getPageViewAsInt(request);
    String moduleName = PortalUtils.getPageDomainObject(request);
    
    // get parameter values
    String tags = request.getParameter(TAGS);
    request.setAttribute(POPUP, request.getParameter(POPUP));
    
    //validate the object being tagged
    if (!ModuleUtils.MODULENAME_DOCUMENTS.equals(moduleName)) {
    	return logErrorClosePanel(request, response, "Module mismatch while tagging object");
    }
    
    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
    	return logErrorClosePanel(request, response, "Project is null");
    }
    
    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-profile-view")) {
    	return logErrorClosePanel(request, response, "Unauthorized to add in this project");
    }
    
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-documents-view")) {
    	return logErrorClosePanel(request, response, "Unauthorized to view this record");
    }

    // get database connection
    Connection db = getConnection(request);

    //Load the object being tagged to make sure it exists
    try {
    	FileItem file = new FileItem(db, linkItemId);    	
    } catch (Exception e) {
    	return logErrorClosePanel(request, response, e.getMessage());
    }

    // update the tag changes
    UserTagLogList userTagLogList = new UserTagLogList();
    userTagLogList.setUserId(user.getId());
    userTagLogList.setLinkModuleId(Constants.PROJECTS_FILES);
    userTagLogList.setLinkItemId(linkItemId);
    userTagLogList.buildList(db);
    userTagLogList.updateTags(db, tags);

    // reload the tags to the page
    TagList moduleTagList = new TagList();
    moduleTagList.setTableName(FileItem.TABLE);
    moduleTagList.setUniqueField(FileItem.PRIMARY_KEY);
    moduleTagList.setLinkItemId(linkItemId);
    PagedListInfo tagListInfo = new PagedListInfo();
    tagListInfo.setColumnToSortBy("tag_count DESC, tag");
    moduleTagList.setPagedListInfo(tagListInfo);
    moduleTagList.buildList(db);
    
    HttpServletRequest req = (HttpServletRequest) request;
    req.getSession().setAttribute(CURRENT_TAGS, StringUtils.toHtml(moduleTagList.getTagsAsString(",")));
    response.sendRedirect(response.encodeURL(request.getContextPath() + "/projects_center_ajax_message.jsp"));

    return null;    
	}

  /*
   * Writes out the exception message and closes the panel without refreshing the parent page.
   * When the parent page refreshes due to error the Blog details gets displayed on the page.
   */
  private GenericBean logErrorClosePanel(ActionRequest request, ActionResponse response, String msg) throws Exception {
  	new Exception(msg).printStackTrace(System.out);
  	response.sendRedirect(request.getContextPath() + "/projects_center_panel_refresh.jsp");
  	return null;
  }	
}
