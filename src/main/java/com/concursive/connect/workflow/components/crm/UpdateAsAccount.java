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

package com.concursive.connect.workflow.components.crm;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.crm.api.client.CRMConnection;
import com.concursive.crm.api.client.DataRecord;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Saves a Lead to ConcourseSuite CRM using HTTP-XML API
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created June 12, 2008
 */

public class UpdateAsAccount extends ObjectHookComponent implements ComponentInterface {

  // TODO: When the CRM becomes load-balanced, then multiple cookies might exist
  // so determine parameter name based on the *.domain.ext
  private static String parameterName = "CRMConnection.cookie";

  public String getUniqueName() {
    return "com.concursive.connect.workflow.components.crm.UpdateAsAccount";
  }

  public String getDescription() {
    return "Updates an Account to ConcourseSuite CRM using HTTP-XML API";
  }

  public boolean execute(ComponentContext context) {
    int businessEmailType = -1;
    int businessAddressType = -1;
    int businessPhoneType = -1;
    int advertisementLeadSourceType = -1;
    int webLeadSourceType = -1;
    int businessFaxType = -1;
    int stageUnclaimed = -1;
    int stageSuggested = -1;
    int stageRequested = -1;

    String url = "";
    String domainName = "";
    String code = "";
    String clientId = "";

    // A reusable connection
    CRMConnection connection = null;

    // Populate the variables
    if (!StringUtils.hasText(context.getParameter("suite.url"))) {
      url = context.getApplicationPrefs().get("CONCURSIVE_CRM.SERVER");
    } else {
      url = context.getParameter("suite.url");
    }

    if (!StringUtils.hasText(context.getParameter("suite.domainName"))) {
      domainName = context.getApplicationPrefs().get("CONCURSIVE_CRM.ID");
    } else {
      domainName = context.getParameter("suite.domainName");
    }

    if (!StringUtils.hasText(context.getParameter("suite.code"))) {
      code = context.getApplicationPrefs().get("CONCURSIVE_CRM.CODE");
    } else {
      code = context.getParameter("suite.code");
    }

    clientId = context.getApplicationPrefs().get("CONCURSIVE_CRM.CLIENT");

    // Create the connection
    connection = new CRMConnection();
    connection.setUrl(url);
    connection.setId(domainName);
    connection.setCode(code);
    //connection.setClientId(clientId);
    // NOTE: the CRMConnection invalidates sessions preventing this from working
    /*if (cookie != null) {
      connection.setCookie(cookie);
    }*/

    // Related info is needed for updating an Account
    // Load a Hashmap to pass these values around...
    HashMap<String, Integer> lookupListValues = new HashMap<String, Integer>();

    // Note: Hacked together as a short term fix to make the components thread-safe.
    businessEmailType = this.getIdFromValue(context, connection, "lookupContactEmailTypesList",
        "lookup_contactemail_types", "Business");
    lookupListValues.put("businessEmailType", businessEmailType);
    businessPhoneType = this.getIdFromValue(context, connection, "lookupContactPhoneTypesList",
        "lookup_contactphone_types", "Business");
    lookupListValues.put("businessPhoneType", businessPhoneType);
    businessFaxType = this.getIdFromValue(context, connection, "lookupContactPhoneTypesList",
        "lookup_contactphone_types", "Business Fax");
    lookupListValues.put("businessFaxType", businessFaxType);
    businessAddressType = this.getIdFromValue(context, connection, "lookupContactAddressTypesList",
        "lookup_contactaddress_types", "Business");
    lookupListValues.put("businessAddressType", businessAddressType);
    advertisementLeadSourceType = this.getIdFromValue(context, connection, "lookupContactSourceList",
        "lookup_contact_source", "Advertisement");
    lookupListValues.put("advertisementLeadSourceType", advertisementLeadSourceType);
    webLeadSourceType = this.getIdFromValue(context, connection, "lookupContactSourceList",
        "lookup_contact_source", "Web");
    lookupListValues.put("webLeadSourceType", webLeadSourceType);
    stageUnclaimed = this.getIdFromValue(context, connection, "lookupAccountStageList",
        "lookup_account_stage", "Unclaimed");
    lookupListValues.put("stageUnclaimed", stageUnclaimed);
    stageSuggested = this.getIdFromValue(context, connection, "lookupAccountStageList",
        "lookup_account_stage", "Suggested");
    lookupListValues.put("stageSuggested", stageSuggested);
    stageRequested = this.getIdFromValue(context, connection, "lookupAccountStageList",
        "lookup_account_stage", "Requested");
    lookupListValues.put("stageRequested", stageRequested);

    // Find the account's orgId
    int orgId = -1;
    String orgName = null;
    {
      //Add Meta Info with fields required
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("orgId");
      meta.add("name");
      connection.setTransactionMeta(meta);

      // Find the account id
      DataRecord record = new DataRecord();
      record.setName("accountList");
      record.setAction(DataRecord.SELECT);
      record.addField("custom1", context.getParameter("lead.custom1"));
      connection.save(record);

      orgId = Integer.parseInt(connection.getResponseValue("orgId"));
      orgName = connection.getResponseValue("name");
    }

    // An orgId to update must exist
    if (orgId == -1) {
      return false;
    }

    // Load the Account Owner's User id
    int ownerId = 1;
    {
      // Reuse the value between calls
      String lastNameToUse = "ConnectSales";
      String globalOwnerIdParam = getUniqueName() + ".contactList.lastName." + lastNameToUse;

      // Check the global workflow store
      Integer globalValue = (Integer) context.getGlobalParameter(globalOwnerIdParam);
      if (globalValue != null) {
        System.out.println("UpdateAsAccount-> User |" + lastNameToUse + "| not found on CRM");
        ownerId = globalValue;
      } else {
        //Add Meta Info with fields required
        ArrayList<String> meta = new ArrayList<String>();
        meta.add("userId");
        connection.setTransactionMeta(meta);

        // Find the sales person
        DataRecord record = new DataRecord();
        record.setName("contactList");
        record.setAction(DataRecord.SELECT);
        record.addField("employeesOnly", "1");
        record.addField("lastName", lastNameToUse);
        connection.save(record);

        int foundValue = Integer.parseInt(connection.getResponseValue("userId"));
        context.setGlobalParameterIfAbsent(globalOwnerIdParam, foundValue);
        ownerId = foundValue;
      }
    }

    // Update the matching account
    DataRecord account = new DataRecord();
    account.setAction(DataRecord.UPDATE);
    account.setName("account");
    account.addField("id", orgId);
    account.addField("name", orgName);
    account.addField("enteredBy", "$U{default}");
    account.addField("modifiedBy", "$U{default}");
    // Change the stage of the account
    if (StringUtils.hasText(context.getParameter("lead.stageName"))) {
      account.addField("stageName", context.getParameter("lead.stageName"));
      if (context.getParameter("lead.stageName").equalsIgnoreCase("Unclaimed")) {
        account.addField("stageId", stageUnclaimed);
      } else {
        if (context.getParameter("lead.stageName").equalsIgnoreCase("Requested")) {
          account.addField("stageId", stageRequested);
        } else {
          if (context.getParameter("lead.stageName").equalsIgnoreCase("Suggested")) {
            account.addField("stageId", stageSuggested);
          }
        }
      }
    } else {
      account.addField("stageName", "Unclaimed");
      account.addField("stageId", stageUnclaimed);
    }
    connection.save(account);

    // Will be adding several records, so make sure we add them as a group...
    connection.setAutoCommit(false);

    // Add the user information as a contact...
    // Account record transaction: Contact
    DataRecord contact = new DataRecord();
    contact.setAction(DataRecord.INSERT);
    contact.setShareKey(true);
    contact.setName("contact");
    contact.addField("source", webLeadSourceType > -1 ? webLeadSourceType : advertisementLeadSourceType);
    contact.addField("accessType", "$AT{GENERAL_CONTACT_PUBLIC}");
    contact.addField("orgId", orgId);
    contact.addField("enteredBy", "$U{default}");
    contact.addField("modifiedBy", "$U{default}");
    contact.addField("owner", ownerId);
    if (StringUtils.hasText(context.getParameter("lead.custom1"))) {
      contact.addField("custom1", context.getParameter("lead.custom1"));
    }
    if (StringUtils.hasText(context.getParameter("lead.firstName"))) {
      contact.addField("nameFirst", context.getParameter("lead.firstName"));
    }
    if (StringUtils.hasText(context.getParameter("lead.lastName"))) {
      contact.addField("nameLast", context.getParameter("lead.lastName"));
    }
    if (StringUtils.hasText(context.getParameter("lead.company"))) {
      contact.addField("company", context.getParameter("lead.company"));
    }
    if (StringUtils.hasText(context.getParameter("lead.webPage")) &&
        context.getParameter("lead.webPage").indexOf("${") == -1) {
      contact.addField("url", context.getParameter("lead.webPage"));
    }
    connection.save(contact);

    // Account record transaction: Contact's Email Address
    DataRecord email = new DataRecord();
    if (StringUtils.hasText(context.getParameter("lead.businessEmail")) && businessEmailType > 0 &&
        context.getParameter("lead.businessEmail").indexOf("${") == -1) {
      email.setName("contactEmailAddress");
      email.addField("email", context.getParameter("lead.businessEmail").trim());
      email.addField("type", businessEmailType);
      email.addField("contactId", "$C{contact.id}");
      email.addField("enteredBy", "$U{default}");
      email.addField("modifiedBy", "$U{default}");
      email.setAction(DataRecord.INSERT);
      connection.save(email);
    }

    // Submit everything...
    boolean success = connection.commit();
    if (!success) {
      System.out.println("UpdateAsAccount-> Commit message: " + connection.getLastResponse());
      return false;
    }
    return true;
  }


  private int getIdFromValue(ComponentContext context, CRMConnection connection, String className, String tableName, String value) {
    // Reuse the value between calls
    String globalParam = "com.concursive.connect.workflow.components.crm.SaveAsAccount" + "." + className + "." + tableName + "." + value;

    // Check the global workflow store
    Integer globalValue = (Integer) context.getGlobalParameter(globalParam);
    if (globalValue != null) {
      return globalValue;
    }

    // Not found so load it from the server
    try {
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("code");
      connection.setTransactionMeta(meta);

      DataRecord list = new DataRecord();
      list.setName(className);
      list.setAction(DataRecord.SELECT);
      list.addField("tableName", tableName);
      list.addField("description", value);
      connection.save(list);

      if (connection.hasError()) {
        throw new Exception(connection.getLastResponse());
      }

      if (connection.getRecordCount() == 0) {
        throw new Exception("Records not found on request");
      }
      int foundValue = Integer.parseInt(connection.getResponseValue("code"));
      context.setGlobalParameterIfAbsent(globalParam, foundValue);
      return foundValue;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  private void checkCookie(ComponentContext context, CRMConnection connection, String cookie) {
    if (connection != null) {
      String sCookie = connection.getCookie();
      if (sCookie != null && (cookie == null || !cookie.equals(sCookie))) {
        cookie = sCookie;
        context.setGlobalParameter(parameterName, cookie);
      }
    }
  }

}