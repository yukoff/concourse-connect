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

import com.concursive.commons.api.DataRecord;
import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.register.beans.RegisterBean;

/**
 * Prepares the Registration page information for sending to ConcourseSuite
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Apr 11, 2005
 */

public class TransformRegisterToLead extends ObjectHookComponent implements ComponentInterface {

  public String getDescription() {
    return "Prepares the Registration page information for sending to ConcourseSuite";
  }

  public boolean execute(ComponentContext context) {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("TransformContactUsToLead-> Step 1");
    }
    RegisterBean bean = (RegisterBean) context.getThisObject();
    // Transform the base contact info
    DataRecord record = new DataRecord();
    record.setName("contact");
    record.setAction(DataRecord.INSERT);
    record.addField("nameFirst", bean.getNameFirst());
    record.addField("nameLast", bean.getNameLast());
    record.addField("company", bean.getOrganization());
    context.setAttribute(SaveLead.LEAD, record);
    // Transform the email
    DataRecord email = new DataRecord();
    email.setName("contactEmailAddress");
    email.setAction(DataRecord.INSERT);
    email.addField("email", bean.getEmail());
    context.setAttribute(SaveLead.LEAD_EMAIL, email);
    return true;
  }
}