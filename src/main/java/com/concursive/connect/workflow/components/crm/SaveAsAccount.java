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
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.crm.api.client.CRMConnection;
import com.concursive.crm.api.client.DataRecord;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Saves a Lead to ConcourseSuite CRM using HTTP-XML API
 *
 * @author Josh Fielek
 * @created September 17, 2008
 */

public class SaveAsAccount extends ObjectHookComponent implements ComponentInterface {

  // TODO: When the CRM becomes load-balanced, then multiple cookies might exist
  // so determine parameter name based on the *.domain.ext
  private static String parameterName = "CRMConnection.cookie";

  public String getDescription() {
    return "Saves an Account to ConcourseSuite CRM using HTTP-XML API";
  }

  public String getUniqueName() {
    return "com.concursive.connect.workflow.components.crm.SaveAsAccount";
  }

  public boolean execute(ComponentContext context) {
    String url = "";
    String domainName = "";
    String code = "";
    String clientId = "";
    // Component properties
    int businessEmailType = -1;
    int businessAddressType = -1;
    int businessPhoneType = -1;
    int advertisementLeadSourceType = -1;
    int webLeadSourceType = -1;
    int businessFaxType = -1;

    int stageUnclaimed = -1;
    int stageSuggested = -1;
    int stageRequested = -1;
    // A reusable connection
    CRMConnection connection = null;
    String cookie;

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
    cookie = (String) context.getGlobalParameter(parameterName);

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

    // Related info is needed for inserting an Account
    // Load a Hashmap to pass these values around...
    HashMap<String, Integer> lookupListValues = new HashMap<String, Integer>();


    // Note: Hacked together as a short term fix to make the components thread-safe.
    businessEmailType = this.getIdFromValue(connection, context, "lookupContactEmailTypesList", "lookup_contactemail_types", "Business", cookie);
    lookupListValues.put("businessEmailType", businessEmailType);
    businessPhoneType = this.getIdFromValue(connection, context, "lookupContactPhoneTypesList", "lookup_contactphone_types", "Business", cookie);
    lookupListValues.put("businessPhoneType", businessPhoneType);
    businessFaxType = this.getIdFromValue(connection, context, "lookupContactPhoneTypesList", "lookup_contactphone_types", "Business Fax", cookie);
    lookupListValues.put("businessFaxType", businessFaxType);
    businessAddressType = this.getIdFromValue(connection, context, "lookupContactAddressTypesList", "lookup_contactaddress_types", "Business", cookie);
    lookupListValues.put("businessAddressType", businessAddressType);
    advertisementLeadSourceType = this.getIdFromValue(connection, context, "lookupContactSourceList", "lookup_contact_source", "Advertisement", cookie);
    lookupListValues.put("advertisementLeadSourceType", advertisementLeadSourceType);
    webLeadSourceType = this.getIdFromValue(connection, context, "lookupContactSourceList", "lookup_contact_source", "Web", cookie);
    lookupListValues.put("webLeadSourceType", webLeadSourceType);
    stageUnclaimed = this.getIdFromValue(connection, context, "lookupAccountStageList", "lookup_account_stage", "Unclaimed", cookie);
    lookupListValues.put("stageUnclaimed", stageUnclaimed);
    stageSuggested = this.getIdFromValue(connection, context, "lookupAccountStageList", "lookup_account_stage", "Suggested", cookie);
    lookupListValues.put("stageSuggested", stageSuggested);
    stageRequested = this.getIdFromValue(connection, context, "lookupAccountStageList", "lookup_account_stage", "Requested", cookie);
    lookupListValues.put("stageRequested", stageRequested);


    // Load the Account Owner's User id
    int ownerId = 1;
    {
      // Reuse the value between calls
      String lastNameToUse = "ConnectSales";
      String globalOwnerIdParam = getUniqueName() + ".contactList.lastName." + lastNameToUse;

      // Check the global workflow store
      Integer globalValue = (Integer) context.getGlobalParameter(globalOwnerIdParam);
      if (globalValue != null) {
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

    // Load the AccountType id based on the project category
    int accountTypeId = -1;
    Project project = (Project) context.getThisObject();
    if (project.getSubCategory1Id() > -1) {
      ProjectCategory subCategory = ProjectUtils.loadProjectCategory(project.getSubCategory1Id());
      if (subCategory != null) {
        accountTypeId = this.getIdFromValue(connection, context, "lookupAccountTypesList", "lookup_account_types", subCategory.getDescription(), cookie);
      }
    }

    // Prepare an account insert transaction
    connection.setAutoCommit(false);

    // Account record transaction
    DataRecord account = new DataRecord();
    account.setAction(DataRecord.INSERT);
    account.setShareKey(true);
    account.setName("account");
    account.addField("enteredBy", "$U{default}");
    account.addField("modifiedBy", "$U{default}");
    account.addField("owner", ownerId);
    // Set the uniqueId
    if (StringUtils.hasText(context.getParameter("lead.custom1"))) {
      account.addField("custom1", context.getParameter("lead.custom1"));
    }
    // Set the stage
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
    if (StringUtils.hasText(context.getParameter("lead.company"))) {
      account.addField("name", context.getParameter("lead.company"));
    }
    if (StringUtils.hasText(context.getParameter("lead.webPage")) && context.getParameter("lead.webPage").indexOf("${") == -1) {
      account.addField("url", context.getParameter("lead.webPage"));
    }
    if (StringUtils.hasText(context.getParameter("lead.notes"))) {
      account.addField("notes", context.getParameter("lead.notes"));
    }
    // Set the account type
    if (accountTypeId > -1) {
      account.addField("typeList", String.valueOf(accountTypeId));
    }
    connection.save(account);

    addOrganizationRecords(connection, context, lookupListValues);

    // Determine if we need to create an account...
    // If teh Lead Name is not set, skip[ the contact...
    boolean hasFirstName = StringUtils.hasText(context.getParameter("lead.firstName"));
    boolean hasLastName = StringUtils.hasText(context.getParameter("lead.lastName"));

    if (hasFirstName && hasLastName) {
      addContactRecords(connection, context, ownerId, lookupListValues);
    }

    // Submit everything...
    boolean success = connection.commit();
    checkCookie(context, connection, cookie);
    if (!success) {
      System.out.println("SaveAsAccount-> Commit message: " + connection.getLastResponse());
      return false;
    }
    return true;
  }

  private void addOrganizationRecords(CRMConnection connection, ComponentContext context, HashMap<String, Integer> lookupListValues) {

    // Account record transaction: Organization's Email Address
    DataRecord email = new DataRecord();
    if (StringUtils.hasText(context.getParameter("lead.businessEmail")) && lookupListValues.get("businessEmailType") > 0 &&
        context.getParameter("lead.businessEmail").indexOf("${") == -1) {
      email.setName("organizationEmailAddress");
      email.addField("email", context.getParameter("lead.businessEmail").trim());
      email.addField("type", lookupListValues.get("businessEmailType"));
      email.addField("OrgId", "$C{account.id}");
      email.addField("enteredBy", "$U{default}");
      email.addField("modifiedBy", "$U{default}");
      email.setAction(DataRecord.INSERT);
      connection.save(email);
    }
    // Account record transaction: Organization's Phone Numbers
    DataRecord businessPhone = new DataRecord();
    if (StringUtils.hasText(context.getParameter("lead.businessPhone")) && lookupListValues.get("businessPhoneType") > 0 &&
        context.getParameter("lead.businessPhone").indexOf("${") == -1) {
      businessPhone.setName("organizationPhoneNumber");
      businessPhone.addField("number", context.getParameter("lead.businessPhone").trim());
      businessPhone.addField("type", lookupListValues.get("businessPhoneType"));
      businessPhone.addField("OrgId", "$C{account.id}");
      businessPhone.addField("enteredBy", "$U{default}");
      businessPhone.addField("modifiedBy", "$U{default}");
      businessPhone.setAction(DataRecord.INSERT);
      connection.save(businessPhone);
    }
    DataRecord businessFax = new DataRecord();
    if (StringUtils.hasText(context.getParameter("lead.businessFax")) && lookupListValues.get("businessFaxType") > 0 &&
        context.getParameter("lead.businessFax").indexOf("${") == -1) {
      businessFax.setName("organizationPhoneNumber");
      businessFax.addField("number", context.getParameter("lead.businessFax").trim());
      businessFax.addField("type", lookupListValues.get("businessFaxType"));
      businessFax.addField("orgId", "$C{account.id}");
      businessFax.addField("enteredBy", "$U{default}");
      businessFax.addField("modifiedBy", "$U{default}");
      businessFax.setAction(DataRecord.INSERT);
      connection.save(businessFax);
    }

    // Account record transaction: Organization's Address
    DataRecord address = new DataRecord();
    if (((StringUtils.hasText(context.getParameter("lead.addressline1")) && context.getParameter("lead.addressline1").indexOf("${") == -1) ||
        (StringUtils.hasText(context.getParameter("lead.postalCode")) && context.getParameter("lead.postalCode").indexOf("${") == -1))
        && lookupListValues.get("businessAddressType") > 0) {
      address.setName("organizationAddress");
      if (StringUtils.hasText(context.getParameter("lead.addressline1")) && context.getParameter("lead.addressline1").indexOf("${") == -1) {
        address.addField("streetAddressLine1", context.getParameter("lead.addressline1"));
      }
      if (StringUtils.hasText(context.getParameter("lead.addressline2")) && context.getParameter("lead.addressline2").indexOf("${") == -1) {
        address.addField("streetAddressLine2", context.getParameter("lead.addressline2"));
      }
      if (StringUtils.hasText(context.getParameter("lead.addressline3")) && context.getParameter("lead.addressline3").indexOf("${") == -1) {
        address.addField("streetAddressLine3", context.getParameter("lead.addressline3"));
      }
      if (StringUtils.hasText(context.getParameter("lead.city")) && context.getParameter("lead.city").indexOf("${") == -1) {
        address.addField("city", context.getParameter("lead.city"));
      }
      if (StringUtils.hasText(context.getParameter("lead.state")) && context.getParameter("lead.state").indexOf("${") == -1) {
        address.addField("state", context.getParameter("lead.state"));
      }
      if (StringUtils.hasText(context.getParameter("lead.country")) && context.getParameter("lead.country").indexOf("${") == -1) {
        address.addField("country", context.getParameter("lead.country"));
      }
      if (StringUtils.hasText(context.getParameter("lead.postalCode")) && context.getParameter("lead.postalCode").indexOf("${") == -1) {
        address.addField("zip", context.getParameter("lead.postalCode"));
      }
      address.addField("type", lookupListValues.get("businessAddressType"));
      address.addField("orgId", "$C{account.id}");
      address.addField("enteredBy", "$U{default}");
      address.addField("modifiedBy", "$U{default}");
      address.setAction(DataRecord.INSERT);
      connection.save(address);
    }
  }


  private void addContactRecords(CRMConnection connection, ComponentContext context, int ownerId,
                                 HashMap<String, Integer> lookupListValues) {
    // Account record transaction: Contact
    DataRecord contact = new DataRecord();
    contact.setAction(DataRecord.INSERT);
    contact.setShareKey(true);
    contact.setName("contact");
    contact.addField("source", lookupListValues.get("webLeadSourceType") > -1 ?
        lookupListValues.get("webLeadSourceType") : lookupListValues.get("advertisementLeadSourceType"));
    contact.addField("accessType", "$AT{ACCOUNT_CONTACT_PUBLIC}");
    contact.addField("orgId", "$C{account.id}");
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
    if (StringUtils.hasText(context.getParameter("lead.email")) &&
        lookupListValues.get("businessEmailType") > 0 &&
        context.getParameter("lead.email").indexOf("${") == -1) {
      email.setName("contactEmailAddress");
      email.addField("email", context.getParameter("lead.email").trim());
      email.addField("type", lookupListValues.get("businessEmailType"));
      email.addField("contactId", "$C{contact.id}");
      email.addField("enteredBy", "$U{default}");
      email.addField("modifiedBy", "$U{default}");
      email.setAction(DataRecord.INSERT);
      connection.save(email);
    }
  }


  private int getIdFromValue(CRMConnection connection, ComponentContext context, String className, String tableName, String value, String cookie) {
    // Reuse the value between calls
    String globalParam = getUniqueName() + "." + className + "." + tableName + "." + value;

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
      checkCookie(context, connection, cookie);

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