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

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.crm.api.client.CRMConnection;
import com.concursive.crm.api.client.DataRecord;

/**
 * Saves a Lead to ConcourseSuite CRM using HTTP-XML API
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Apr 11, 2005
 */

public class SaveLead extends ObjectHookComponent implements ComponentInterface {

  public final static String LEAD = "lead";
  public final static String LEAD_EMAIL = "lead_email";
  public final static String LEAD_PHONE = "lead_phone";
  public final static String LEAD_ADDRESS = "lead_address";

  public String getDescription() {
    return "Saves a Lead to ConcourseSuite CRM using HTTP-XML API";
  }

  public boolean execute(ComponentContext context) {
    // Connect to CRM
    CRMConnection crm = new CRMConnection();
    crm.setUrl(context.getParameter("crm.url"));
    crm.setId(context.getParameter("crm.domainName"));
    crm.setCode(context.getParameter("crm.code"));
    int clientId = context.getParameterAsInt("crm.clientId");
    if (clientId == -1) {
      clientId = crm.retrieveNewClientId();
    }
    crm.setClientId(clientId);
    crm.setAutoCommit(false);
    // Check for contact record
    DataRecord contact = (DataRecord) context.getAttribute(LEAD);
    if (contact == null) {
      return false;
    }
    contact.setShareKey(true);
    contact.addField("instanceId", 1);
    contact.addField("isLead", "true");
    contact.addField("leadStatus", 1);
    contact.addField("enteredBy", 0);
    contact.addField("modifiedBy", 0);
    try {
      int sourceId = Integer.parseInt(context.getParameter("sourceId"));
      contact.addField("source", sourceId);
    } catch (Exception e) {
    }
    try {
      int accessType = Integer.parseInt(context.getParameter("accessType"));
      contact.addField("accessType", accessType);
    } catch (Exception e) {
    }
    crm.save(contact);
    // Save the Lead's email address
    DataRecord email = (DataRecord) context.getAttribute(LEAD_EMAIL);
    if (email != null && email.getValue("email") != null) {
      email.addField("instanceId", 1);
      email.addField("contactId", "$C{contact.id}");
      email.addField("type", 1);
      email.addField("enteredBy", 0);
      email.addField("modifiedBy", 0);
      crm.save(email);
    }
    // Save the Lead's phone number
    DataRecord phone = (DataRecord) context.getAttribute(LEAD_PHONE);
    if (phone != null && phone.getValue("number") != null) {
      phone.addField("instanceId", 1);
      phone.addField("contactId", "$C{contact.id}");
      phone.addField("type", 1);
      phone.addField("enteredBy", 0);
      phone.addField("modifiedBy", 0);
      crm.save(phone);
    }
    // Save the Lead's address
    DataRecord address = (DataRecord) context.getAttribute(LEAD_ADDRESS);
    if (address != null && address.getValue("streetAddressLine1") != null) {
      address.addField("instanceId", 1);
      address.addField("contactId", "$C{contact.id}");
      address.addField("type", 1);
      address.addField("enteredBy", 0);
      address.addField("modifiedBy", 0);
      crm.save(address);
    }
    // Submit everything...
    boolean success = crm.commit();
    if (!success) {
      System.out.println("SaveLead-> Commit message: " + crm.getLastResponse());
    }
    return true;
  }
}
