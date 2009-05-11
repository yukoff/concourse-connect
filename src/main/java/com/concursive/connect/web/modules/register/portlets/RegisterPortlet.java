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
package com.concursive.connect.web.modules.register.portlets;

import com.concursive.commons.codec.PrivateString;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import com.concursive.connect.web.modules.register.beans.RegisterBean;
import com.concursive.connect.web.portal.PortalUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;
import java.io.IOException;
import java.security.Key;
import java.sql.Connection;
import java.util.StringTokenizer;

/**
 * User Registration portlet
 *
 * @author Matt Rajkowski
 * @created August 5, 2008
 */
public class RegisterPortlet extends GenericPortlet {

  private static final Log LOG = LogFactory.getLog(RegisterPortlet.class);

  // Pages
  private static final String VIEW_PAGE_FORM = "/portlets/register/register_form-view.jsp";
  private static final String VIEW_PAGE_VERIFY_DETAILS = "/portlets/register/register_verify_details-view.jsp";
  private static final String VIEW_PAGE_TERMS = "/portlets/register/register_terms-view.jsp";
  private static final String VIEW_PAGE_CLOSED = "/portlets/register/register_closed-view.jsp";
  private static final String VIEW_PAGE_THANKS = "/portlets/register/register_thanks-view.jsp";

  // Attribute names for objects available in the view
  private static final String REGISTER_BEAN = "registerBean";
  private static final String SHOW_TERMS_AND_CONDITIONS = "showTermsAndConditions";
  private static final String CAPTCHA_PASSED = "captchaPassed";

  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

    // Example URLs
    // --> /page/register
    // --> /page/register/invited/data value
    // --> /page/register/confirm
    // --> /page/register/thanks

    // Determine which step of the process the user is on
    // A capability of the portal url
    String view = PortalUtils.getPageView(request);

    // Declare the default view
    String defaultView = VIEW_PAGE_FORM;

    try {
      // Setup prefs
      ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);
      boolean showLicense = "true".equals(prefs.get("LICENSE"));
      String captchaPassed = (String) request.getPortletSession().getAttribute("TE-REGISTER-CAPTCHA-PASSED");
      if (captchaPassed != null) {
        request.setAttribute(CAPTCHA_PASSED, captchaPassed);
      }

      // Step through the registration process
      if (!"true".equals(prefs.get("REGISTER"))) {
        // the application isn't allowing registrations
        defaultView = VIEW_PAGE_CLOSED;
      } else if (view == null) {
        // Show the form (user might have hit back button)
        RegisterBean bean = (RegisterBean) request.getPortletSession().getAttribute(REGISTER_BEAN);
        request.getPortletSession().removeAttribute(REGISTER_BEAN);
        if (bean == null) {
          bean = new RegisterBean();
        }
        if (bean.hasErrors()) {
          PortalUtils.processErrors(request, bean.getErrors());
        }
        request.setAttribute(REGISTER_BEAN, bean);
        request.setAttribute(SHOW_TERMS_AND_CONDITIONS, String.valueOf(showLicense));
      } else if ("invited".equals(view)) {
        // Check for the coded data that was generated during the invite
        String codedData = PortalUtils.getPageParameter(request);
        //Decode the user id
        Key key = PortalUtils.getApplicationKey(request);
        if (key == null) {
          LOG.error("Key was not found.");
        }
        String data = PrivateString.decrypt(key, codedData);
        // Process it into properties
        int userId = -1;
        int projectId = -1;
        StringTokenizer st = new StringTokenizer(data, ",");
        while (st.hasMoreTokens()) {
          String pair = (st.nextToken());
          StringTokenizer stPair = new StringTokenizer(pair, "=");
          String param = stPair.nextToken();
          String value = stPair.nextToken();
          if ("id".equals(param)) {
            userId = Integer.parseInt(value);
          } else if ("pid".equals(param)) {
            projectId = Integer.parseInt(value);
          }
        }
        // Auto-populate if user was invited
        LOG.debug("Invited user: " + userId);
        User thisUser = UserUtils.loadUser(userId);
        RegisterBean bean = new RegisterBean();
        bean.setEmail(thisUser.getEmail());
        bean.setNameFirst(thisUser.getFirstName());
        bean.setNameLast(thisUser.getLastName());
        bean.setData(codedData);
        request.setAttribute(REGISTER_BEAN, bean);
      } else if ("verify".equals(view)) {
        defaultView = VIEW_PAGE_VERIFY_DETAILS;
        // Process the bean for the request
        RegisterBean bean = (RegisterBean) request.getPortletSession().getAttribute(REGISTER_BEAN);
        request.getPortletSession().removeAttribute(REGISTER_BEAN);
        request.setAttribute(REGISTER_BEAN, bean);
      } else if ("thanks".equals(view)) {
        defaultView = VIEW_PAGE_THANKS;
        RegisterBean bean = (RegisterBean) request.getPortletSession().getAttribute(REGISTER_BEAN);
        request.getPortletSession().removeAttribute(REGISTER_BEAN);
        request.getPortletSession().removeAttribute(CAPTCHA_PASSED);
        request.setAttribute(REGISTER_BEAN, bean);
      }

      // JSP view
      PortletContext context = getPortletContext();
      PortletRequestDispatcher requestDispatcher =
          context.getRequestDispatcher(defaultView);
      requestDispatcher.include(request, response);

    } catch (Exception e) {
      LOG.debug("JSP: " + defaultView);
      LOG.error("Error for view: " + view, e);
      throw new PortletException(e.getMessage());
    }
  }

  public void processAction(ActionRequest request, ActionResponse response)
      throws PortletException, IOException {

    // Check to see if the application is allowing registration
    ApplicationPrefs prefs = PortalUtils.getApplicationPrefs(request);
    if (!"true".equals(prefs.get("REGISTER"))) {
      throw new PortletException("Registration is currently by invitation only");
    }
    boolean showLicense = "true".equals(prefs.get("LICENSE"));

    Connection db = PortalUtils.getConnection(request);
    String ctx = request.getContextPath();

    // Determine the page that submitted the data
    String currentPage = request.getParameter("currentPage");

    // Handle the form post
    RegisterBean bean = new RegisterBean();
    PortalUtils.populateObject(bean, request);
    request.getPortletSession().setAttribute(REGISTER_BEAN, bean);

    try {
      if ("form".equals(currentPage)) {
        // Validate the terms
        if (showLicense && !bean.getTerms()) {
          bean.getErrors().put("termsError", "You must check the terms of use");
        }
        // Validate and check captcha
        if (!bean.isValid(request.getPortletSession())) {
          response.sendRedirect(ctx + "/page/register");
        } else if (bean.isAlreadyRegistered(db)) {
          bean.getErrors().put("actionError",
              "This email address is already registered. " +
                  "If you forgot your login information then you can request your registration " +
                  "information from the login page.");
          response.sendRedirect(ctx + "/page/register");
        } else {
          // Forward to the verification page
          response.sendRedirect(ctx + "/page/register/verify");
        }
      } else if ("verify".equals(currentPage)) {
        // Handle the back button
        String submitAction = request.getParameter("submitAction");
        if ("Back".equals(submitAction)) {
          // User chose to go back and modify the form data
          response.sendRedirect(ctx + "/page/register");
        } else {
          // User chose to continue, so save all data and provide confirmation
          boolean saved = bean.save(db, prefs, request, null);
          if (saved) {
            PortalUtils.processInsertHook(request, bean);
            PortalUtils.processInsertHook(request, bean.getProject());
            PortalUtils.indexAddItem(request, bean.getProject());
            response.sendRedirect(ctx + "/page/register/thanks");
          } else {
            if (bean.getErrors().size() > 0) {
              bean.getErrors().put("actionError", "Email could not be sent to the specified address: " + bean.getErrors().get("emailError"));
              bean.getErrors().put("emailError", "Check email address");
            } else {
              bean.getErrors().put("actionError", "Account could not be created.  An account might already exist for this email address or a problem with sending an email to this address.");
            }
            response.sendRedirect(ctx + "/page/register");
          }
        }
      } else if ("thanks".equals(currentPage)) {
        response.sendRedirect(ctx + "/close_panel_refresh.jsp");
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }
}