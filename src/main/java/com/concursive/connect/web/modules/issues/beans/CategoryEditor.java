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

package com.concursive.connect.web.modules.issues.beans;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.issues.dao.TicketCategory;
import com.concursive.connect.web.modules.issues.dao.TicketCategoryList;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Description
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Sep 29, 2005
 */

public class CategoryEditor {
  private int maxLevels = -1;

  public CategoryEditor() {
  }

  public int getMaxLevels() {
    return maxLevels;
  }

  public void setMaxLevels(int maxLevels) {
    this.maxLevels = maxLevels;
  }

  // TODO: move to TicketCategoryList class
  public static TicketCategoryList update(Connection db, HttpServletRequest request) throws SQLException {
    // Work from the request items
    int parentId = Integer.parseInt(request.getParameter("categoryId"));
    int catLevel = Integer.parseInt(request.getParameter("catLevel"));
    int projectId = -1;
    try {
      projectId = Integer.parseInt(request.getParameter("projectId"));
    } catch (Exception e) {
      // Not in a project
    }
    // Load the base set
    TicketCategoryList categoryList = new TicketCategoryList();
    categoryList.setParentCode(parentId);
    categoryList.setCatLevel(catLevel);
    categoryList.setEnabledState(Constants.UNDEFINED);
    categoryList.setProjectId(projectId);
    categoryList.buildList(db);
    // Perform the comparison

    // Parse the request for items
    String[] params = request.getParameterValues("selectedList");
    String[] names = new String[params.length];
    int j = 0;
    StringTokenizer st = new StringTokenizer(request.getParameter("selectNames"), "^");
    while (st.hasMoreTokens()) {
      names[j] = (String) st.nextToken();
      if (System.getProperty("DEBUG") != null) {
        System.out.println("CategoryEditor-> " + params[j] + " Item: " + names[j]);
      }
      j++;
    }


    // Put into something manageable
    ArrayList arrayList = new ArrayList();
    for (int i = 0; i < params.length; i++) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("CategoryEditor-> Name: " + names[i]);
        System.out.println("CategoryEditor-> Param: " + params[i]);
      }
      arrayList.add(params[i]);
    }

    // BEGIN TRANSACTION

    // Disable removed items
    Iterator items = categoryList.iterator();
    while (items.hasNext()) {
      TicketCategory thisCategory = (TicketCategory) items.next();
      // If item is not in the passed array, then disable the entry
      if (!arrayList.contains(String.valueOf(thisCategory.getId()))) {
        thisCategory.setEnabled(false);
        thisCategory.update(db);
        items.remove();
      }
    }

    // Iterate through the array
    for (int i = 0; i < params.length; i++) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("CategoryEditor-> Name: " + names[i]);
        System.out.println("CategoryEditor-> Param: " + params[i]);
      }
      if (params[i].startsWith("*")) {
        // TODO: Check to see if a previously disabled entry has the same name,
        // and enable it


        // New item, add it at the correct position
        TicketCategory thisCategory = new TicketCategory();
        //thisCategory.setProjectId(projectId);
        thisCategory.setCategoryLevel(catLevel);
        thisCategory.setDescription(names[i]);
        thisCategory.setLevel(i);
        thisCategory.setParentCode(parentId);
        thisCategory.setProjectId(projectId);
        thisCategory.insert(db);
        categoryList.add(thisCategory);
      } else {
        // Existing item, update the name and position
        updateName(db, Integer.parseInt(params[i]), names[i]);
        updateLevel(db, Integer.parseInt(params[i]), i);
      }
    }

    // END TRANSACTION
    return categoryList;
  }

  /**
   * Description of the Method
   *
   * @param db    Description of the Parameter
   * @param id    Description of the Parameter
   * @param level Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void updateLevel(Connection db, int id, int level) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE ticket_category " +
            "SET level = ? " +
            "WHERE id = ? ");
    int i = 0;
    pst.setInt(++i, level);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param db   Description of the Parameter
   * @param id   Description of the Parameter
   * @param name Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void updateName(Connection db, int id, String name) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "UPDATE ticket_category " +
            "SET description = ? " +
            "WHERE id = ? ");
    int i = 0;
    pst.setString(++i, name);
    pst.setInt(++i, id);
    pst.executeUpdate();
    pst.close();
  }

  public static void toggleSubCategories(Connection db, TicketCategory thisCategory, boolean enabled) throws SQLException {
    //remove from universal list
    boolean recordDeleted = false;
/*
    if (thisCategory.getActualCatId() == -1) {
      categoryList.remove(new Integer(thisCategory.getId()));
      //remove from parent
      if (thisCategory.getParentCode() > 0) {
        TicketCategory parentCategory = (TicketCategoryDraft) categoryList.get(
            new Integer(thisCategory.getParentCode()));
        parentCategory.removeChild(thisCategory.getId());
      }
      //check if it is a top level category
      if (thisCategory.getParentCode() == 0) {
        topCategoryList.remove(thisCategory);
      }
      recordDeleted = thisCategory.delete(db, tableName);
      if (!recordDeleted) {
        thisCategory.getErrors().put(
            "actionError", systemStatus.getLabel(
                "object.validation.actionError.ticketCategoryDeletion"));
      }
    } else {
      thisCategory.setEnabled(enabled);
      thisCategory.update(db, tableName);
    }
    if (thisCategory.getShortChildList() != null) {
      Iterator i = thisCategory.getShortChildList().iterator();
      while (i.hasNext()) {
        TicketCategory tmpCategory = (TicketCategoryDraft) i.next();
        this.toggleSubCategories(db, tmpCategory, enabled);
      }
    }
*/
  }
}
