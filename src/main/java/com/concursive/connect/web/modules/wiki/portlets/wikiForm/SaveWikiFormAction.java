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
package com.concursive.connect.web.modules.wiki.portlets.wikiForm;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.contactus.dao.ContactUsBean;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.CustomForm;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.utils.CustomFormUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * Action for saving a wiki form
 *
 * @author matt rajkowski
 * @created November 5, 2008
 */
public class SaveWikiFormAction implements IPortletAction {

  // Logger
  private static Log LOG = LogFactory.getLog(SaveWikiFormAction.class);
  // Preferences
  private static final String PREF_TITLE = "title";
  private static final String PREF_FORM_CONTENT = "form";

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Determine the form to use
    String formContent = request.getPreferences().getValue(PREF_FORM_CONTENT, null);
    if (formContent == null) {
      LOG.error("Form content is null");
      return null;
    }

    // populate the custom form with the request data
    Wiki wiki = new Wiki();
    wiki.setContent(formContent);
    CustomFormUtils.populateForm(wiki, request);

    // Access the form data and perform the requested action
    CustomForm form = CustomFormUtils.createForm(wiki.getContent());
    if (form == null) {
      LOG.error("Form didn't validate");
      return null;
    }

    String ipAddress = "none";
    String browser = null;

    // @todo Make this capability generic instead of using these defined fields...
    // Submit the data
    ContactUsBean bean = new ContactUsBean();
    bean.setDescription(request.getPreferences().getValue(PREF_TITLE, null));
    bean.setNameFirst(form.getField("firstName").getValue());
    bean.setNameLast(form.getField("lastName").getValue());
    bean.setBusinessPhone(form.getField("phone").getValue());
    bean.setEmail(form.getField("email").getValue());
    bean.setOrganization(form.getField("organization").getValue());
    if (!StringUtils.hasText(bean.getNameFirst())) {
      bean.addError("firstNameError", "required field");
    }
    if (!StringUtils.hasText(bean.getNameLast())) {
      bean.addError("lastNameError", "required field");
    }
    if (!StringUtils.hasText(bean.getBusinessPhone())) {
      bean.addError("phoneError", "required field");
    }
    if (!StringUtils.hasText(bean.getEmail())) {
      bean.addError("emailError", "required field");
    }
    if (!StringUtils.hasText(bean.getOrganization())) {
      bean.addError("organizationError", "required field");
    }
    if (bean.hasErrors()) {
      bean.addError("actionError", "Please fill out all fields.");
      return bean;
    }
    // Everything is there... save it
    bean.setEmailCopy(true);
    bean.save(PortalUtils.getConnection(request), PortalUtils.getApplicationPrefs(request), ipAddress, browser);

    // Show the success message
    return (PortalUtils.goToViewer(request, response, "success", bean));
  }
}
