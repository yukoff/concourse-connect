package com.concursive.connect.web.modules.tools.actions;

import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.text.StringUtils;

import java.util.Random;
import java.util.ArrayList;
import java.sql.SQLException;

import org.aspcfs.utils.CRMConnection;
import org.aspcfs.apps.transfer.DataRecord;

/**
 * Action that redirects the current logged-in user to the Management CRM
 *
 * @author Ananth
 * @created Aug 7, 2009
 */
public class ProjectManagementCRM extends GenericAction {
  static Random random = new Random();
  static String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxy0123456789";

  public String executeCommandDefault(ActionContext context) {
    if (!getUser(context).isConnectCRMAdmin() && !getUser(context).isConnectCRMManager()) {
      return "PermissionError";
    }
    //Base Visible Format is: /show/main-profile/crm/
    ApplicationPrefs prefs = this.getApplicationPrefs(context);
    String crmServer = prefs.get(ApplicationPrefs.CONCURSIVE_CRM_SERVER);
    if (!StringUtils.hasText(crmServer)) {
      return "PermissionError";
    }
    String token = generateRandomToken();
    if (sendToken(prefs, getUser(context), token)) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("ProjectManagementCRM-> sending redirect");
      }
      // if successful, send the redirect...
      String redirect = "MyCFS.do?command=Home";
      context.getRequest().setAttribute("redirectTo", prefs.get(ApplicationPrefs.CONCURSIVE_CRM_SERVER) + "/" + redirect + "&SessionId=" + token);
      return ("Redirect301");
    } else {
      return "CRMError";
    }
  }


  public String executeCommandShowAccount(ActionContext context) {
    if (!getUser(context).isConnectCRMAdmin() && !getUser(context).isConnectCRMManager()) {
      return "PermissionError";
    }
    String projectId = context.getRequest().getParameter("pid");
    try {
      // Load the project
      Project thisProject = retrieveAuthorizedProject(Integer.parseInt(projectId), context);

      ApplicationPrefs prefs = this.getApplicationPrefs(context);
      String crmServer = prefs.get(ApplicationPrefs.CONCURSIVE_CRM_SERVER);
      if (!StringUtils.hasText(crmServer)) {
        return "PermissionError";
      }
      // Determine the corresponding crm organization record ID
      int crmAccountId = fetchCRMAccountId(prefs, thisProject.getId());
      if (crmAccountId != -1) {
        String token = generateRandomToken();
        if (sendToken(prefs, getUser(context), token)) {
          if (System.getProperty("DEBUG") != null) {
            System.out.println("ProjectManagementCRM-> sending redirect");
          }
          // if successful, send the redirect...
          //TODO: Need to use modules/accounts/show/<id> instead of Accounts.do?command=Details URL...
          String redirect = "Accounts.do?command=Details&orgId=" + crmAccountId;
          context.getRequest().setAttribute("redirectTo", prefs.get(ApplicationPrefs.CONCURSIVE_CRM_SERVER) + "/" + redirect + "&SessionId=" + token);
          return ("Redirect301");
        } else {
          return "CRMError";
        }
      } else {
        return "CRMError";
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (System.getProperty("DEBUG") != null) {
      System.out.println("ProjectManagementTools-> End reached");
    }
    return "SystemError";
  }

  private int fetchCRMAccountId(ApplicationPrefs prefs, int projectId) {
    // Connection details
    CRMConnection conn = new CRMConnection();
    conn.setUrl(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_SERVER));
    conn.setId(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_ID));
    conn.setCode(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_CODE));
    conn.setClientId(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_CLIENT));

    ArrayList meta = new ArrayList<String>();
    meta.add("orgId");
    conn.setTransactionMeta(meta);
    // Request info
    DataRecord record = new DataRecord();
    record.setName("accountList");
    record.setAction("select");
    record.addField("custom1", projectId);
    boolean success = conn.save(record);
    if (!success) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("ProjectManagementCRM-> fetchCRMAccountId error: " + conn.getLastResponse());
      }
      return -1;
    }
    String orgId = conn.getResponseValue("orgId");
    if (orgId != null) {
      return Integer.parseInt(orgId);
    }
    return -1;
  }

  private boolean sendToken(ApplicationPrefs prefs, User user, String token) {
    // Connection details
    CRMConnection conn = new CRMConnection();
    conn.setUrl(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_SERVER));
    conn.setId(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_ID));
    conn.setCode(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_CODE));
    conn.setClientId(prefs.get(ApplicationPrefs.CONCURSIVE_CRM_CLIENT));
    // Request info
    DataRecord record = new DataRecord();
    record.setName("importSessionAuthenticationId");
    record.setAction("execute");
    record.addField("userEmail", user.getEmail());
    record.addField("sessionToken", token);
    record.addField("userFirstName", user.getFirstName());
    record.addField("userLastName", user.getLastName());
    record.addField("userCompany", user.getCompany());
    record.addField("userPassword", user.getPassword());
    boolean success = conn.save(record);
    if (!success) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("ProjectManagementCRM-> sendToken error: " + conn.getLastResponse());
      }
    }
    return success;
  }

  private String generateRandomToken() {
    String token = new String("");
    for (int i = 0; i < 128; i++) {
      // Generate a random character..
      int charNum = random.nextInt(validCharacters.length());
      token += validCharacters.charAt(charNum);
      //token += validCharacters.substring(charNum, charNum + 1);
    }
    return token;
  }
}
