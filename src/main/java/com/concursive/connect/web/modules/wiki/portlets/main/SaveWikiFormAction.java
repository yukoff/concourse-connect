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
package com.concursive.connect.web.modules.wiki.portlets.main;

import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiTemplate;
import com.concursive.connect.web.modules.wiki.utils.CustomFormUtils;
import com.concursive.connect.web.portal.IPortletAction;
import com.concursive.connect.web.portal.PortalUtils;
import static com.concursive.connect.web.portal.PortalUtils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.sql.Connection;

/**
 * Action for saving a wiki form
 *
 * @author matt rajkowski
 * @created November 5, 2008
 */
public class SaveWikiFormAction implements IPortletAction {

  // Logger
  private static Log LOG = LogFactory.getLog(SaveWikiFormAction.class);

  public GenericBean processAction(ActionRequest request, ActionResponse response) throws Exception {

    // Determine the project container to use
    Project project = findProject(request);
    if (project == null) {
      throw new Exception("Project is null");
    }

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-wiki-add")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    // Update the record
    Connection db = useConnection(request);
    boolean recordInserted = false;
    int resultCount = -1;

    // Parameters
    String wikiId = request.getParameter("id");
    String templateId = request.getParameter("tid");

    // Determine where to redirect
    String wikiSubject = null;

    // Determine if inserting or updating an existing entry
    if (!StringUtils.isNumber(wikiId)) {
      // Load the template
      WikiTemplate template = new WikiTemplate(db, Integer.parseInt(templateId));
      // New entry
      Wiki wiki = new Wiki();
      wiki.setProjectId(project.getId());
      wiki.setEnteredBy(user.getId());
      wiki.setTemplateId(templateId);
      wiki.setSubject(template.getTitle());
      wiki.setContent(CustomFormUtils.populateForm(template, request));
      recordInserted = wiki.insert(db);
      if (!recordInserted) {
        return wiki;
      }
      // Index this wiki
      indexAddItem(request, wiki);
      wikiSubject = wiki.getSubjectLink();
    } else {
      // Existing entry
      Wiki originalWiki = new Wiki(db, Integer.parseInt(wikiId), project.getId());
      // Replace the form in the wiki with the modified form...
      Wiki wiki = new Wiki(db, originalWiki.getId());
      wiki.setModifiedBy(user.getId());
      CustomFormUtils.populateForm(wiki, request);
      // Update it...
      if (!originalWiki.getContent().equals(wiki.getContent())) {
        resultCount = wiki.update(db, originalWiki);
        if (resultCount <= 0) {
          // The record didn't get updated so alert user to resolve conflict
          return wiki;
        }
        // Index this wiki
        indexAddItem(request, wiki);
        wikiSubject = wiki.getSubjectLink();
      }
    }

    if (!StringUtils.hasText(wikiSubject)) {
      wikiSubject = "";
    }

    // This call will close panels and perform redirects
    return (PortalUtils.performRefresh(request, response, "/show/wiki/" + wikiSubject));
  }
}
