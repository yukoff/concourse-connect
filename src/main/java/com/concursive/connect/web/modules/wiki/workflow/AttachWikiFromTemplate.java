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

package com.concursive.connect.web.modules.wiki.workflow;

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiTemplate;
import com.concursive.connect.web.modules.wiki.dao.WikiTemplateList;

import java.sql.Connection;

/**
 * Adds a wiki to the project if the source template exists
 *
 * @author matt rajkowski
 * @created July 29, 2008
 */
public class AttachWikiFromTemplate extends ObjectHookComponent implements ComponentInterface {
  // Preferences
  public final static String TEMPLATE_SOURCE = "source";
  public final static String TEMPLATE_TITLE = "template";
  public final static String WIKI_SUBJECT = "wiki";
  // Constants
  public final static String SOURCE_TYPE_CATEGORY_ID = "categoryId";

  public String getDescription() {
    return "Adds a wiki to the project if the source template exists";
  }

  public boolean execute(ComponentContext context) {
    // Parameters
    String source = context.getParameter(TEMPLATE_SOURCE);
    String templateTitle = context.getParameter(TEMPLATE_TITLE);
    String wikiSubject = context.getParameter(WIKI_SUBJECT);
    if (wikiSubject == null) {
      wikiSubject = "";
    }

    // Test and execute
    Connection db = null;
    Project thisProject = (Project) context.getThisObject();
    if (source == null) {
      return false;
    }
    if (source.equals(SOURCE_TYPE_CATEGORY_ID)) {
      if (thisProject.getCategoryId() < 1) {
        return false;
      } else {
        try {
          db = getConnection(context);
          // Locate the template
          WikiTemplateList templateList = new WikiTemplateList();
          templateList.setProjectCategoryId(thisProject.getCategoryId());
          if (templateTitle != null) {
            templateList.setTitle(templateTitle);
          }
          templateList.buildList(db);
          if (templateList.size() == 0) {
            return false;
          }
          WikiTemplate template = templateList.get(0);
          // Insert the wiki
          Wiki wiki = new Wiki();
          wiki.setProjectId(thisProject.getId());
          wiki.setSubject(wikiSubject);
          wiki.setContent(template.getContent());
          wiki.setEnteredBy(thisProject.getModifiedBy());
          wiki.setModifiedBy(thisProject.getModifiedBy());
          wiki.setTemplateId(template.getId());
          wiki.insert(db);
          return true;
        } catch (Exception e) {
          e.printStackTrace(System.out);
        } finally {
          freeConnection(context, db);
        }
      }
    }
    return false;
  }
}