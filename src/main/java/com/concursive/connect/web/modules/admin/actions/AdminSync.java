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

package com.concursive.connect.web.modules.admin.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;

import org.aspcfs.utils.StringUtils;
import org.quartz.Scheduler;

import java.util.Vector;

/**
 * Actions for the administration module
 *
 * @author Kailash Bhoopalam
 * @created June 18, 2009
 */
public final class AdminSync extends GenericAction {

  /**
   * Action to prepare a list of Admin options
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    try {
      Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute(Constants.SCHEDULER);
      context.getRequest().setAttribute("syncStatus", scheduler.getContext().get("CRMSyncStatus"));
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
    }
    return "DefaultOK";
  }


  public String executeCommandStartSync(ActionContext context) {
    if (!getUser(context).getAccessAdmin()) {
      return "PermissionError";
    }
    try {

      Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute(Constants.SCHEDULER);
      Vector syncStatus = (Vector) scheduler.getContext().get("CRMSyncStatus");

      String startSync = context.getRequest().getParameter("startSync");
      if ("true".equals(startSync)){
	      if (syncStatus != null && syncStatus.size() == 0) {
	        // Trigger the sync job
	        triggerJob(context, "syncSystem");
	      } else {
	        // Do nothing as a sync is already in progress.
	      }
      }
      String saveConnectionDetails = context.getRequest().getParameter("saveConnectionDetails");
      if ("true".equals(saveConnectionDetails)){
      	
      	ApplicationPrefs prefs = this.getApplicationPrefs(context);
      	
        String serverURL = context.getRequest().getParameter("serverURL");
        String apiClientId = context.getRequest().getParameter("apiClientId");
        String apiCode = context.getRequest().getParameter("apiCode");
        
        String domainAndPort = serverURL.substring(7).split("/")[0];
        String domain = domainAndPort;
        if (domainAndPort.indexOf(":") != -1){
        	domain = domainAndPort.split(":")[0];
        }
        
        if (StringUtils.hasText(serverURL) &&  StringUtils.hasText(domain) &&
        		StringUtils.hasText(apiClientId) && StringUtils.hasText(apiCode)){
        	prefs.add("CONCURSIVE_CRM.SERVER", serverURL);
        	prefs.add("CONCURSIVE_CRM.ID", domain);
        	prefs.add("CONCURSIVE_CRM.CODE", apiCode);
        	prefs.add("CONCURSIVE_CRM.CLIENT", apiClientId);
        	prefs.save();
        	
	        triggerJob(context, "syncSystem");
        } else {
        	context.getRequest().setAttribute("serverURL", serverURL);
          context.getRequest().setAttribute("apiClientId", apiClientId);
          context.getRequest().setAttribute("apiCode", apiCode);
        }
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
    }
    return "StartSyncOK";
  }

}
