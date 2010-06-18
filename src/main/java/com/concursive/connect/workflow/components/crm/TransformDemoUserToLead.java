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
import com.concursive.connect.web.modules.demo.beans.DemoBean;
import com.concursive.crm.api.client.DataRecord;

/**
 * Prepares the Demo User information for sending to ConcourseSuite
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Apr 11, 2005
 */

public class TransformDemoUserToLead extends ObjectHookComponent implements ComponentInterface {
  public static final String CRLF = System.getProperty("line.separator");

  public String getDescription() {
    return "Prepares the Demo User information for sending to ConcourseSuite";
  }

  public boolean execute(ComponentContext context) {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("TransformDemoUserToLead-> Step 1");
    }
    DemoBean bean = (DemoBean) context.getThisObject();
    // Transform the base contact info
    DataRecord record = new DataRecord();
    record.setName("contact");
    record.setAction(DataRecord.INSERT);
    record.addField("nameFirst", bean.getNameFirst());
    record.addField("nameLast", bean.getNameLast());
    record.addField("company", bean.getCompanyName());
    if (StringUtils.hasText(bean.getTitle())) {
      record.addField("title", bean.getTitle());
    }
    record.addField("notes",
        "Language: " + bean.getLanguage() + CRLF +
            "Referer: " + StringUtils.toString(bean.getReferer()));
    context.setAttribute(SaveLead.LEAD, record);
    // Transform the email
    DataRecord email = new DataRecord();
    email.setName("contactEmailAddress");
    email.setAction(DataRecord.INSERT);
    email.addField("email", bean.getEmail());
    context.setAttribute(SaveLead.LEAD_EMAIL, email);
    // Transform the phone number
    DataRecord phone = new DataRecord();
    phone.setName("contactPhoneNumber");
    phone.setAction(DataRecord.INSERT);
    phone.addField("number", bean.getPhone());
    phone.addField("extension", bean.getPhoneExt());
    context.setAttribute(SaveLead.LEAD_PHONE, phone);
    // Transform the address
    DataRecord address = new DataRecord();
    address.setName("contactAddress");
    address.setAction(DataRecord.INSERT);
    address.addField("streetAddressLine1", bean.getAddressLine1());
    address.addField("streetAddressLine2", bean.getAddressLine2());
    address.addField("streetAddressLine3", bean.getAddressLine3());
    address.addField("city", bean.getCity());
    address.addField("state", bean.getState());
    address.addField("zip", bean.getPostalCode());
    address.addField("country", bean.getCountry());
    context.setAttribute(SaveLead.LEAD_ADDRESS, address);
    return true;
  }
}
