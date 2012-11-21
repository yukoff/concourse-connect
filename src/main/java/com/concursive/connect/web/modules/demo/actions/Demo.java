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

package com.concursive.connect.web.modules.demo.actions;

import bsh.Interpreter;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.demo.beans.DemoBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.utils.ClientType;

import java.sql.Connection;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 20, 2004
 */
public final class Demo extends GenericAction {

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    String demoAllowed = (String) context.getServletContext().getAttribute("demoAllowed");
    if ("offline".equals(demoAllowed)) {
      return "DemoOfflineOK";
    }
    // If logged in, get the user's info
    DemoBean bean = (DemoBean) context.getFormBean();
    if (getUserId(context) > -1) {
      User thisUser = getUser(context);
      if (thisUser != null) {
        bean.setNameFirst(thisUser.getFirstName());
        bean.setNameLast(thisUser.getLastName());
        bean.setEmail(thisUser.getEmail());
        bean.setCompanyName(thisUser.getCompany());
      }
    }
    // TODO: Set language based on browser... need country code
    /*if (bean.getLanguage() == null) {
      ClientType clientType = (ClientType) context.getSession().getAttribute("clientType");
      if (clientType != null) {
        bean.setLanguage(clientType.getLanguage());
      }
    }*/
    return "DemoFormOK";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSave(ActionContext context) {
    // Make sure activations are currently allowed
    String demoAllowed = (String) context.getServletContext().getAttribute("demoAllowed");
    if ("offline".equals(demoAllowed)) {
      return "DemoOfflineOK";
    }
    // See if the form is valid
    DemoBean bean = (DemoBean) context.getFormBean();
    if (!bean.isValid(context.getSession())) {
      processErrors(context, bean.getErrors());
      return "DemoFormError";
    }
    // Record the referer
    ClientType clientType = (ClientType) context.getSession().getAttribute(Constants.SESSION_CLIENT_TYPE);
    bean.setIpAddress(context.getIpAddress());
    bean.setUserBrowser(context.getBrowser());
    if (clientType != null) {
      bean.setReferer(clientType.getReferer());
    }
    // Process the account activation
    Connection db = null;
    try {
      db = getConnection(context);

      // Reserve the URL through the management console
      try {
        String externalValidation = StringUtils.loadText(context.getServletContext().getResourceAsStream(
            "/WEB-INF/management_console_reservation.bsh"));
        Interpreter script = new Interpreter();
        script.set("db", db);
        script.set("context", context);
        script.set("bean", bean);
        script.eval(externalValidation);
      } catch (Exception e) {
        e.printStackTrace();
        return "DemoSaveError";
      }

      // Save to local database
      bean.insert(db);
      // Trigger the remote mangement console

      // Reserve the URL through the management console
      try {
        String externalValidation = StringUtils.loadText(context.getServletContext().getResourceAsStream(
            "/WEB-INF/management_console_activation.bsh"));
        Interpreter script = new Interpreter();
        script.set("db", db);
        script.set("context", context);
        script.set("bean", bean);
        script.eval(externalValidation);
      } catch (Exception e) {
        e.printStackTrace();
        return "DemoSaveError";
      }

      // Update the local database as in-process
      bean.markProcessed(db);
      processInsertHook(context, bean);
      return "DemoSaveOK";
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return "DemoSaveError";
  }
}

