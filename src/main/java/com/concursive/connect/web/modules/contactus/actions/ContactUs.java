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
package com.concursive.connect.web.modules.contactus.actions;

import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.contactus.dao.ContactUsBean;
import com.concursive.connect.web.modules.login.dao.User;

import java.sql.Connection;

/**
 * Allows user to contact the site owner
 *
 * @author matt rajkowski
 * @version $Id$
 * @created December 2, 2003
 */
public final class ContactUs extends GenericAction {

  public String executeCommandDefault(ActionContext context) {
    // If logged in, get the user's info
    if (getUserId(context) > -1) {
      User thisUser = getUser(context);
      if (thisUser != null) {
        ContactUsBean bean = (ContactUsBean) context.getFormBean();
        bean.setNameFirst(thisUser.getFirstName());
        bean.setNameLast(thisUser.getLastName());
        bean.setEmail(thisUser.getEmail());
        bean.setOrganization(thisUser.getCompany());
      }
    }
    // Show the contact us form
    return "ContactUsOK";
  }


  public String executeCommandSend(ActionContext context) {
    // Validate the form
    ContactUsBean bean = (ContactUsBean) context.getFormBean();
    if (!bean.isValid(context.getSession())) {
      processErrors(context, bean.getErrors());
      return "ContactUsERROR";
    }
    // Save the form
    Connection db = null;
    try {
      db = getConnection(context);
      bean.setInstanceId(getInstance(context).getId());
      bean.save(context, db);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    if (bean.getId() > -1) {
      processInsertHook(context, bean);
      return "SendOK";
    } else {
      return "ContactUsERROR";
    }
  }
}

