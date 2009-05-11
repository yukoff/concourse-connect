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
 * Deletes an Account in ConcourseSuite CRM using HTTP-XML API
 *
 * @author Josh Fielek
 * @created September 17, 2008
 */

public class DeleteAsAccount extends ObjectHookComponent implements ComponentInterface {

  // TODO: When the CRM becomes load-balanced, then multiple cookies might exist
  // so determine parameter name based on the *.domain.ext
  private static String parameterName = "CRMConnection.cookie";

  public String getDescription() {
    return "Deletes an Account in ConcourseSuite CRM using HTTP-XML API";
  }

  public String getUniqueName() {
    return "com.concursive.connect.workflow.components.crm.DeleteAsAccount";
  }

  public boolean execute(ComponentContext context) {
    String url = "";
    String domainName = "";
    String code = "";
    String clientId = "";
    // Component properties

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


    // Prepare an account insert transaction
    connection.setAutoCommit(true);

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

    // Account record transaction
    ArrayList<String> meta = new ArrayList<String>();
    DataRecord account = new DataRecord();
    account.setAction(DataRecord.DELETE);
    account.setName("account");
    // Set the uniqueId
    account.addField("id", orgId);
    connection.save(account);

    // Submit everything...
    boolean success = connection.commit();
    checkCookie(context, connection, cookie);
    if (!success) {
      System.out.println("DeleteAsAccount-> Commit message: " + connection.getLastResponse());
      return false;
    }
    return true;
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