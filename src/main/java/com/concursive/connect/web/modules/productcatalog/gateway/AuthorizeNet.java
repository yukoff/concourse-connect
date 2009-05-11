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

package com.concursive.connect.web.modules.productcatalog.gateway;

import com.concursive.commons.currency.CurrencyUtils;
import com.concursive.commons.net.HTTPUtils;
import com.concursive.connect.web.modules.productcatalog.beans.OrderBean;

import java.util.LinkedHashMap;
import java.util.StringTokenizer;


/**
 * Facilitates using Authorize.net as a credit card processing agent
 *
 * @author matt rajkowski
 * @version $Id$
 * @created August 12, 2005
 */
public class AuthorizeNet {

  public final static int UNDEFINED = -1;
  public final static int APPROVED = 1;
  public final static int DECLINED = 2;
  public final static int ERROR = 3;
  public final static String AUTH_CAPTURE = "AUTH_CAPTURE";
  // inputs
  private OrderBean orderBean = null;
  private String login = null;
  private String transactionKey = null;
  private String description = null;
  private boolean testMode = false;
  // responses
  private String response = null;
  private String responseText = null;
  private String transactionId = null;
  private int responseCode = UNDEFINED;
  private String approvalCode = null;
  private String avsResultCode = null;


  public AuthorizeNet() {
  }

  public AuthorizeNet(OrderBean tmp) {
    orderBean = tmp;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public void setTransactionKey(String transactionKey) {
    this.transactionKey = transactionKey;
  }


  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isTestMode() {
    return testMode;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public String getResponse() {
    return response;
  }

  public String getResponseText() {
    return responseText;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public String getApprovalCode() {
    return approvalCode;
  }

  public String getAvsResultCode() {
    return avsResultCode;
  }

  public void authorizeAndCapture() {
    LinkedHashMap params = new LinkedHashMap();
    // Authentication
    params.put("x_version", "3.0");
    params.put("x_delim_data", "TRUE");
    params.put("x_relay_response", "FALSE");
    params.put("x_login", login);
    params.put("x_tran_key", transactionKey);
    if (testMode) {
      params.put("x_test_request", "TRUE");
    }
    // Payment Info
    params.put("x_type", AUTH_CAPTURE);
    params.put("x_amount", CurrencyUtils.formatCurrency(orderBean.getChargeAmount(), "0"));
    params.put("x_card_num", orderBean.getPayment().getCreditCard().getNumericNumber());
    params.put(
        "x_exp_date", orderBean.getPayment().getCreditCard().getExpirationMonthString() + "/" +
            orderBean.getPayment().getCreditCard().getExpirationYear());
    //x_recurring_billing

    // Contact Info
    params.put("x_first_name", orderBean.getBilling().getNameFirst());
    params.put("x_last_name", orderBean.getBilling().getNameLast());
    params.put("x_address", orderBean.getBilling().getStreetAddress());
    params.put("x_city", orderBean.getBilling().getCity());
    params.put("x_state", orderBean.getBilling().getState());
    params.put("x_zip", orderBean.getBilling().getPostalCode());
    params.put("x_country", orderBean.getBilling().getCountry());
    // Customer Data
    params.put("x_customer_ip", orderBean.getIpAddress());
    params.put("x_email", orderBean.getBilling().getEmail());
    params.put("x_email_customer", "TRUE");
    params.put("x_invoice_num", String.valueOf(orderBean.getId()));
    params.put("x_description", description);
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AuthorizeNet-> Checking authorization...");
    }
    response = HTTPUtils.post("https://secure.authorize.net/gateway/transact.dll", params);
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AuthorizeNet-> Response: " + response);
    }
    if (response != null) {
      updateReponseValues();
    } else {
      responseText = "Communication with the credit card processing center failed at this time";
    }
    updateOrderBean();
  }

  private void updateReponseValues() {
    StringTokenizer st = new StringTokenizer(response, ",");
    responseCode = Integer.parseInt(st.nextToken());
    st.nextToken();
    st.nextToken();
    responseText = st.nextToken();
    approvalCode = st.nextToken();
    avsResultCode = st.nextToken();
    transactionId = st.nextToken();
  }

  private void updateOrderBean() {
    if (responseCode == DECLINED) {
      // An error occurred, send user back to credit card form with message
      orderBean.setSaved(false);
    }
    if (responseCode == ERROR) {
      // An error occurred, send user back to credit card form with message
      orderBean.setSaved(false);
    }
    if (responseCode == UNDEFINED) {
      orderBean.setSaved(false);
    }
  }


}

