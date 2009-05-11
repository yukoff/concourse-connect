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

package com.concursive.connect.web.modules.profile.workflow;

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.dao.ProjectCategory;
import com.concursive.connect.web.modules.profile.dao.ProjectCategoryList;

import java.sql.Connection;

/**
 * Tests if the projects category matches the specified category
 *
 * @author Kailash Bhoopalam
 * @created June 11, 2008
 */
public class TestProjectCategory extends ObjectHookComponent implements ComponentInterface {
  public final static String PROJECT_CATEGORY = "project.category";

  public String getDescription() {
    return "Tests if the projects category matches the specified category";
  }

  public boolean execute(ComponentContext context) {
    boolean result = false;
    Connection db = null;
    Project thisProject = (Project) context.getThisObject();
    if (thisProject.getCategoryId() < 1) {
      result = false;
    }
    String allowedCategories = context.getParameter(PROJECT_CATEGORY);
    if (allowedCategories == null) {
      return false;
    }
    try {
      db = getConnection(context);
      // Load the categories
      ProjectCategoryList categories = new ProjectCategoryList();
      categories.setEnabled(Constants.TRUE);
      categories.setTopLevelOnly(true);
      categories.buildList(db);
      // See if the project matches any allowed category
      String[] categoryArray = allowedCategories.split(",");
      for (String thisCategory : categoryArray) {
        ProjectCategory allowedCategory = categories.getFromValue(thisCategory.trim());
        if (allowedCategory != null) {
          if (thisProject.getCategoryId() == allowedCategory.getId()) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return result;
  }
}