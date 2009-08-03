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

import org.aspcfs.apps.transfer.DataRecord;
import org.aspcfs.utils.CRMConnection;
import org.aspcfs.utils.HTTPUtils;
import org.aspcfs.utils.StringUtils;
import org.quartz.Scheduler;

import java.util.ArrayList;
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
    boolean isValid = false;
    String serverURL = null;
    String apiClientId = null;
    String apiCode = null;
    String startSync = null;
    String saveConnectionDetails = null;
    try {

      Scheduler scheduler = (Scheduler) context.getServletContext().getAttribute(Constants.SCHEDULER);
      Vector syncStatus = (Vector) scheduler.getContext().get("CRMSyncStatus");

      String syncListings = context.getRequest().getParameter("syncListings");
      startSync = context.getRequest().getParameter("startSync");
      if ("true".equals(startSync)){
      	isValid = true;
	      if (syncStatus != null && syncStatus.size() == 0) {
	        // Trigger the sync job
	        triggerJob(context, "syncSystem", syncListings);
	      } else {
	        // Do nothing as a sync is already in progress.
	      }
      }
      
      saveConnectionDetails = context.getRequest().getParameter("saveConnectionDetails");
      if ("true".equals(saveConnectionDetails)){
      	
      	ApplicationPrefs prefs = this.getApplicationPrefs(context);
      	
        serverURL = context.getRequest().getParameter("serverURL");
        apiClientId = context.getRequest().getParameter("apiClientId");
        apiCode = context.getRequest().getParameter("apiCode");
        String domainAndPort = "";
        if (serverURL.indexOf("http://") != -1){
        	domainAndPort = serverURL.substring(7).split("/")[0];
        } else if (serverURL.indexOf("https://") != -1){
        	domainAndPort = serverURL.substring(8).split("/")[0];
        }
        String domain = domainAndPort;
        if (domainAndPort.indexOf(":") != -1){
        	domain = domainAndPort.split(":")[0];
        }
        
        if (StringUtils.hasText(serverURL) &&  StringUtils.hasText(domain) &&
        		StringUtils.hasText(apiClientId) && StringUtils.hasText(apiCode)){
        	if (testConnection(serverURL, domain, apiCode,apiClientId)){
        		
        		isValid = true;
        		
	        	prefs.add("CONCURSIVE_CRM.SERVER", serverURL);
	        	prefs.add("CONCURSIVE_CRM.ID", domain);
	        	prefs.add("CONCURSIVE_CRM.CODE", apiCode);
	        	prefs.add("CONCURSIVE_CRM.CLIENT", apiClientId);
	        	prefs.save();
	        	
		        triggerJob(context, "syncSystem", syncListings);
        	}
        }
      }
    } catch (Exception e) {
      context.getRequest().setAttribute("Error", e);
      return ("SystemError");
    } finally {
    }
    if (!isValid && "true".equals(saveConnectionDetails)){
    	context.getRequest().setAttribute("serverURL", context.getRequest().getParameter("serverURL"));
      context.getRequest().setAttribute("apiClientId", context.getRequest().getParameter("apiClientId"));
      context.getRequest().setAttribute("apiCode", context.getRequest().getParameter("apiCode"));

      context.getRequest().setAttribute("actionError","Could not connect to the suite.");
      return executeCommandDefault(context);
    }
    return "StartSyncOK";
  }
  
  private boolean testConnection(String serverURL, String id, String code, String clientId){
  	
    CRMConnection crmConnection = new CRMConnection();
    
    crmConnection.setUrl(serverURL);
    crmConnection.setCode(code);
    crmConnection.setClientId(clientId);
    crmConnection.setId(id);
    crmConnection.setAutoCommit(false);
    
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("code");
    crmConnection.setTransactionMeta(meta);

    DataRecord list = new DataRecord();
    list.setName("lookupAccountTypesList");
    list.setAction(DataRecord.SELECT);
    list.addField("uniqueField", "code");
    list.addField("tableName", "lookup_account_types");
    list.addField("description", "test");
  	try {
  		crmConnection.save(list);
    	crmConnection.commit();
    	
    	if (crmConnection.hasError()){
    		return false;
    	}
    	
  	}catch (Exception e){
  		return false;
  	}
    return true;
  }

}
