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
import com.concursive.connect.web.modules.productcatalog.beans.OrderBean;
import com.concursive.connect.web.modules.productcatalog.dao.*;
import com.concursive.crm.api.client.DataRecord;

/**
 * Transform Order information to ConcourseSuite Leads
 *
 * @author wli
 * @version $Id: TransformOrderToLead.java
 * @created December 21, 2007
 */
public class TransformOrderToLead extends ObjectHookComponent implements ComponentInterface {

  public String getDescription() {
    return "Transform Order information to ConcourseSuite Leads";
  }

  public boolean execute(ComponentContext context) {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("TransformOrderToLead-> Step 1");
    }

    OrderBean bean = (OrderBean) context.getThisObject();
    ContactInformation contact_info = bean.getContactInformation();
    BillingAddress bill_address = bean.getBilling();
    Payment payment_info = bean.getPayment();
    ProductList product_list = bean.getProductList();
    Product product_info = null;

    // Transform the base contact info

    DataRecord record = new DataRecord();
    record.setName("contact");
    record.setAction(DataRecord.INSERT);
    record.addField("nameFirst", contact_info.getNameFirst());
    record.addField("nameLast", contact_info.getNameLast());
    record.addField("company", contact_info.getOrganization());
    StringBuffer sbf = new StringBuffer();
    sbf.append(contact_info.getMessage() + "\n\t");
    sbf.append(bean.toString());
    record.addField("notes", sbf.toString());
    context.setAttribute(SaveLead.LEAD, record);

    // Transform the email

    DataRecord email = new DataRecord();
    email.setName("contactEmailAddress");
    email.setAction(DataRecord.INSERT);
    email.addField("email", contact_info.getEmail());
    context.setAttribute(SaveLead.LEAD_EMAIL, email);

    // Transform the phone number

    DataRecord phone = new DataRecord();
    phone.setName("contactPhoneNumber");
    phone.setAction(DataRecord.INSERT);
    phone.addField("number", contact_info.getPhoneNumber());
    phone.addField("extension", contact_info.getPhoneNumberExt());
    context.setAttribute(SaveLead.LEAD_PHONE, phone);

    return true;
  }
}
