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
import org.aspcfs.apps.transfer.DataRecord;
import org.aspcfs.utils.CRMConnection;

import java.util.ArrayList;

/**
 * Saves a Lead to ConcourseSuite CRM using HTTP-XML API
 *
 * @author Kailash Bhoopalam
 * @version $Id$
 * @created June 12, 2008
 */

public class SaveAsLead extends ObjectHookComponent implements ComponentInterface {

  private int businessEmailType = -1;
  private int businessAddressType = -1;
  private int businessPhoneType = -1;
  private int advertisementLeadSourceType = -1;
  private int webLeadSourceType = -1;
  private int businessFaxType = -1;

  private static final int LEAD_UNPROCESSED = 1;
  private String url = "";
  private String domainName = "";
  private String code = "";
  private String clientId = "";

  // Connect to CRM
  CRMConnection connection = null;

  public String getDescription() {
    return "Saves a Lead to ConcourseSuite CRM using HTTP-XML API";
  }

  public boolean execute(ComponentContext context) {

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

    this.initialiseConnection();
    loadTypes();

    // Check for contact record
    DataRecord contact = new DataRecord();
    contact.setAction(DataRecord.INSERT);
    contact.setShareKey(true);
    contact.setName("contact");
    contact.addField("source", webLeadSourceType > -1 ? webLeadSourceType : advertisementLeadSourceType);
    contact.addField("accessType", "$AT{GENERAL_CONTACT_PUBLIC}");
    contact.addField("isLead", "true");
    contact.addField("leadStatus", LEAD_UNPROCESSED);
    contact.addField("enteredBy", "$U{default}");
    contact.addField("modifiedBy", "$U{default}");

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
    if (StringUtils.hasText(context.getParameter("lead.webPage")) && context.getParameter("lead.webPage").indexOf("${") == -1) {
      contact.addField("url", context.getParameter("lead.webPage"));
    }
    if (StringUtils.hasText(context.getParameter("lead.notes"))) {
      contact.addField("notes", context.getParameter("lead.notes"));
    }
    connection.save(contact);

    // Save the Lead's email address
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
    // Save the Lead's phone numbers
    DataRecord businessPhone = new DataRecord();
    if (StringUtils.hasText(context.getParameter("lead.businessPhone")) && businessPhoneType > 0 &&
        context.getParameter("lead.businessPhone").indexOf("${") == -1) {
      businessPhone.setName("contactPhoneNumber");
      businessPhone.addField("number", context.getParameter("lead.businessPhone").trim());
      businessPhone.addField("type", businessPhoneType);
      businessPhone.addField("contactId", "$C{contact.id}");
      businessPhone.addField("enteredBy", "$U{default}");
      businessPhone.addField("modifiedBy", "$U{default}");
      businessPhone.setAction(DataRecord.INSERT);
      connection.save(businessPhone);
    }

    DataRecord businessFax = new DataRecord();
    if (StringUtils.hasText(context.getParameter("lead.businessFax")) && businessFaxType > 0 &&
        context.getParameter("lead.businessFax").indexOf("${") == -1) {
      businessFax.setName("contactPhoneNumber");
      businessFax.addField("number", context.getParameter("lead.businessFax").trim());
      businessFax.addField("type", businessFaxType);
      businessFax.addField("contactId", "$C{contact.id}");
      businessFax.addField("enteredBy", "$U{default}");
      businessFax.addField("modifiedBy", "$U{default}");
      businessFax.setAction(DataRecord.INSERT);
      connection.save(businessFax);
    }

    // Save the Lead's address
    DataRecord address = new DataRecord();
    if (((StringUtils.hasText(context.getParameter("lead.addressline1")) && context.getParameter("lead.addressline1").indexOf("${") == -1) ||
        (StringUtils.hasText(context.getParameter("lead.postalCode")) && context.getParameter("lead.postalCode").indexOf("${") == -1))
        && businessAddressType > 0) {
      address.setName("contactAddress");
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
      address.addField("type", businessAddressType);
      address.addField("contactId", "$C{contact.id}");
      address.addField("enteredBy", "$U{default}");
      address.addField("modifiedBy", "$U{default}");
      address.setAction(DataRecord.INSERT);
      connection.save(address);
    }
    // Submit everything...
    boolean success = connection.commit();
    if (!success) {
      System.out.println("SaveAsLead-> Commit message: " + connection.getLastResponse());
    }

    return true;
  }


  private void loadTypes() {
    businessEmailType = this.getIdFromValue("lookupContactEmailTypesList", "lookup_contactemail_types", "Business");
    this.initialiseConnection();

    businessPhoneType = this.getIdFromValue("lookupContactPhoneTypesList", "lookup_contactphone_types", "Business");
    this.initialiseConnection();

    businessFaxType = this.getIdFromValue("lookupContactPhoneTypesList", "lookup_contactphone_types", "Business Fax");
    this.initialiseConnection();

    businessAddressType = this.getIdFromValue("lookupContactAddressTypesList", "lookup_contactaddress_types", "Business");
    this.initialiseConnection();

    advertisementLeadSourceType = this.getIdFromValue("lookupContactSourceList", "lookup_contact_source", "Advertisement");
    this.initialiseConnection();

    webLeadSourceType = this.getIdFromValue("lookupContactSourceList", "lookup_contact_source", "Web");
    this.initialiseConnection();
  }

  private int getIdFromValue(String className, String tableName, String value) {
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
      connection.commit();

      if (connection.getRecordCount() > 0) {
        String responseValue = connection.getResponseValue("code");
        //System.out.println("responseValue 1 ==> " + responseValue);
        return Integer.parseInt(responseValue);
      } else {
        System.out.println("No data found for " + value + " in class " + className);
        this.initialiseConnection();
        list = new DataRecord();
        list.setName(className.substring(0, className.indexOf("List")));
        list.setAction(DataRecord.INSERT);
        list.addField("tableName", tableName);
        list.addField("description", value);
        connection.save(list);
        connection.commit();
        String responseValue = connection.getResponseValue("code");
        //System.out.println("responseValue 2 ==> " + responseValue);
        return Integer.parseInt(responseValue);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      this.initialiseConnection();
    }
    return -1;
  }

  private void initialiseConnection() {
    connection = new CRMConnection();
    connection.setUrl(url);
    connection.setId(domainName);
    connection.setCode(code);
    //connection.setClientId(clientId);
    connection.setAutoCommit(false);
  }
}
