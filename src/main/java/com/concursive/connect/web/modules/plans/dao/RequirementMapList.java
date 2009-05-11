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

package com.concursive.connect.web.modules.plans.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * A tree of items for displaying
 *
 * @author matt rajkowski
 * @version $Id$
 * @created March 2003
 */
public class RequirementMapList extends ArrayList {
  public final static int FILTER_PRIORITY = 1;

  private int projectId = -1;
  private int requirementId = -1;
  private int enteredBy = -1;
  private int modifiedBy = -1;
  // Cloning
  private long offset = 0;
  private boolean resetStatus = false;

  /**
   * Constructor for the RequirementMapList object
   */
  public RequirementMapList() {
  }


  /**
   * Sets the projectId attribute of the RequirementMapList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(int tmp) {
    this.projectId = tmp;
  }


  /**
   * Sets the projectId attribute of the RequirementMapList object
   *
   * @param tmp The new projectId value
   */
  public void setProjectId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the requirementId attribute of the RequirementMapList object
   *
   * @param tmp The new requirementId value
   */
  public void setRequirementId(int tmp) {
    this.requirementId = tmp;
  }


  /**
   * Sets the requirementId attribute of the RequirementMapList object
   *
   * @param tmp The new requirementId value
   */
  public void setRequirementId(String tmp) {
    this.requirementId = Integer.parseInt(tmp);
  }


  /**
   * Gets the projectId attribute of the RequirementMapList object
   *
   * @return The projectId value
   */
  public int getProjectId() {
    return projectId;
  }


  /**
   * Gets the requirementId attribute of the RequirementMapList object
   *
   * @return The requirementId value
   */
  public int getRequirementId() {
    return requirementId;
  }

  public int getEnteredBy() {
    return enteredBy;
  }

  public void setEnteredBy(int enteredBy) {
    this.enteredBy = enteredBy;
  }

  public int getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(int modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public long getOffset() {
    return offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public boolean getResetStatus() {
    return resetStatus;
  }

  public void setResetStatus(boolean resetStatus) {
    this.resetStatus = resetStatus;
  }

  /**
   * Description of the Method
   *
   * @param db Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public void buildList(Connection db) throws SQLException {
    //All the items are in order by position
    PreparedStatement pst = db.prepareStatement(
        "SELECT * " +
            "FROM project_requirements_map " +
            "WHERE project_id = ? " +
            "AND requirement_id = ? " +
            "ORDER BY position ");
    pst.setInt(1, projectId);
    pst.setInt(2, requirementId);
    ResultSet rs = pst.executeQuery();
    RequirementMapItem previousItem = null;
    HashMap indents = new HashMap();
    while (rs.next()) {
      RequirementMapItem thisItem = new RequirementMapItem(rs);
      this.add(thisItem);
      //Find the parent
      if (previousItem != null) {
        if (thisItem.getIndent() == 0) {
          //Top level
          ((RequirementMapItem) indents.get(new Integer(thisItem.getIndent()))).setFinalNode(false);
        } else {
          if (previousItem.getIndent() < thisItem.getIndent()) {
            //The parent was the previous item
            thisItem.setParent(previousItem);
            previousItem.getChildren().add(thisItem);
          } else if (previousItem.getIndent() >= thisItem.getIndent()) {
            //The parent is somewhere back...
            thisItem.setParent((RequirementMapItem) indents.get(new Integer(thisItem.getIndent() - 1)));
            ((RequirementMapItem) indents.get(new Integer(thisItem.getIndent() - 1))).getChildren().add(thisItem);
            ((RequirementMapItem) indents.get(new Integer(thisItem.getIndent()))).setFinalNode(false);
          }
        }
      }
      RequirementMapItem previousIndent = (RequirementMapItem) indents.get(new Integer(thisItem.getIndent()));
      if (previousIndent != null) {
        thisItem.setPreviousSameIndent(previousIndent);
        previousIndent.setNextSameIndent(thisItem);
      }
      indents.put(new Integer(thisItem.getIndent()), thisItem);
      previousItem = thisItem;
    }
    rs.close();
    pst.close();
  }


  /**
   * Gets the item attribute of the RequirementMapList object
   *
   * @param position Description of the Parameter
   * @return The item value
   */
  public RequirementMapItem getItem(int position) {
    return (RequirementMapItem) this.get(position - 1);
    /*
     *  Iterator i = this.iterator();
     *  while (i.hasNext()) {
     *  RequirementMapItem thisItem = (RequirementMapItem) i.next();
     *  if (thisItem.getPosition() == position) {
     *  return thisItem;
     *  }
     *  }
     *  return null;
     */
  }


  /**
   * Description of the Method
   *
   * @param db            Description of the Parameter
   * @param requirementId Description of the Parameter
   * @throws SQLException Description of the Exception
   */
  public static void delete(Connection db, int requirementId) throws SQLException {
    PreparedStatement pst = db.prepareStatement(
        "DELETE FROM project_requirements_map " +
            "WHERE requirement_id = ? ");
    pst.setInt(1, requirementId);
    pst.execute();
    pst.close();
  }


  /**
   * Description of the Method
   *
   * @param assignments Description of the Parameter
   * @param filterType  Description of the Parameter
   * @param value       Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean filter(AssignmentList assignments, int filterType, String value) {
    if (value != null) {
      int id = Integer.parseInt(value);
      if (id == -1) {
        return false;
      }
      //Go through list from the bottom, if item has no children and doesn't meet the value, then
      //remove it from the parent and remove it from the iterator
      ListIterator list = this.listIterator(this.size());
      while (list.hasPrevious()) {
        RequirementMapItem thisItem = (RequirementMapItem) list.previous();
        RequirementMapItem thisParent = thisItem.getParent();
        if (thisItem.getChildren().isEmpty() && check(thisItem, assignments, filterType, id)) {
          //Remove this item because it's not valid
          if (thisParent != null) {
            thisParent.getChildren().remove(thisItem);
            if (thisParent.getChildren().isEmpty()) {
              thisParent.setFinalNode(true);
            }
          }
          list.remove();
        } else {
          //Check to see if this item has visually changed because of other items
          if (thisParent != null) {
            if (!thisItem.getFinalNode() && thisParent.getChildren().indexOf(thisItem) == thisParent.getChildren().size() - 1) {
              thisItem.setFinalNode(true);
            }
          }
        }
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param thisItem    Description of the Parameter
   * @param assignments Description of the Parameter
   * @param filterType  Description of the Parameter
   * @param value       Description of the Parameter
   * @return Description of the Return Value
   */
  private boolean check(RequirementMapItem thisItem, AssignmentList assignments, int filterType, int value) {
    if (thisItem.getFolderId() != -1) {
      return true;
    }
    if (filterType == FILTER_PRIORITY &&
        assignments.getAssignment(thisItem.getAssignmentId()).getPriorityId() != value) {
      return true;
    }
    return false;
  }


  /**
   * Description of the Method
   *
   * @param assignments Description of the Parameter
   * @param value       Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean filterAssignments(AssignmentList assignments, String value) {
    if (value != null) {
      //Go through list from the bottom, if item doesn't meet the value, then
      //remove it from the parent and remove it from the iterator
      ListIterator list = this.listIterator(this.size());
      while (list.hasPrevious()) {
        RequirementMapItem thisItem = (RequirementMapItem) list.previous();
        RequirementMapItem thisParent = thisItem.getParent();
        if (checkAssignment(thisItem, thisItem.getChildren(), assignments, value)) {
          //Remove this item because it's not valid
          if (thisParent != null) {
            thisParent.getChildren().remove(thisItem);
            if (thisParent.getChildren().isEmpty()) {
              thisParent.setFinalNode(true);
            }
          }
          list.remove();
        } else {
          //Check to see if this item has visually changed because of other items
          if (thisParent != null) {
            if (!thisItem.getFinalNode() && thisParent.getChildren().indexOf(thisItem) == thisParent.getChildren().size() - 1) {
              thisItem.setFinalNode(true);
            }
          }
        }
      }
    }
    return true;
  }


  /**
   * Description of the Method
   *
   * @param thisItem    Description of the Parameter
   * @param assignments Description of the Parameter
   * @param value       Description of the Parameter
   * @return Description of the Return Value
   */
  private boolean checkAssignment(RequirementMapItem thisItem, ArrayList children, AssignmentList assignments, String value) {
    boolean childrenResult = true;
    if (children.size() > 0) {
      Iterator childrenIterator = children.iterator();
      while (childrenIterator.hasNext()) {
        RequirementMapItem child = (RequirementMapItem) childrenIterator.next();
        if (!checkAssignment(child, child.getChildren(), assignments, value)) {
          childrenResult = false;
          break;
        }
      }
    }
    if (value.equals("incompleteOnly")) {
      Assignment assign = assignments.getAssignment(thisItem.getAssignmentId());
      if (assign != null && assign.getCompleteDate() != null) {
        return (childrenResult);
      }
    } else if (value.equals("closedOnly")) {
      Assignment assign = assignments.getAssignment(thisItem.getAssignmentId());
      if (assign != null && assign.getCompleteDate() == null) {
        return (childrenResult);
      }
    }
    return false;
  }

  public void insert(Connection db) throws SQLException {
    HashMap assignmentMap = new HashMap();
    HashMap folderMap = new HashMap();
    Iterator i = this.iterator();
    int prevIndent = -1;
    int prevMapId = -1;
    while (i.hasNext()) {
      RequirementMapItem thisItem = (RequirementMapItem) i.next();
      if (thisItem.getAssignmentId() > -1) {
        Assignment thisAssignment = new Assignment(db, thisItem.getAssignmentId());
        thisAssignment.setProjectId(projectId);
        thisAssignment.setRequirementId(requirementId);
        thisAssignment.setEnteredBy(enteredBy);
        thisAssignment.setModifiedBy(modifiedBy);
        thisAssignment.setId(-1);
        thisAssignment.setOffset(offset);
        if (resetStatus) {
          thisAssignment.setCompleteDate((Timestamp) null);
          thisAssignment.setStatusId(1);
          thisAssignment.setPercentComplete(-1);
        }
        if (thisAssignment.getFolderId() > -1) {
          if (folderMap.containsKey(new Integer(thisAssignment.getFolderId()))) {
            int folderId = ((Integer) folderMap.get(new Integer(thisAssignment.getFolderId()))).intValue();
            thisAssignment.setFolderId(folderId);
          } else {
            thisAssignment.setFolderId(-1);
          }
        }
        thisAssignment.setPrevIndent(prevIndent);
        thisAssignment.setPrevMapId(prevMapId);
        thisAssignment.setIndent(thisItem.getIndent());
        thisAssignment.insert(db);
        prevIndent = thisAssignment.getPrevIndent();
        prevMapId = thisAssignment.getPrevMapId();
        int newId = thisAssignment.getId();
      } else if (thisItem.getFolderId() > -1) {
        AssignmentFolder thisFolder = new AssignmentFolder(db, thisItem.getFolderId(), thisItem.getProjectId());
        int oldId = thisFolder.getId();
        thisFolder.setProjectId(projectId);
        thisFolder.setRequirementId(requirementId);
        thisFolder.setEnteredBy(enteredBy);
        thisFolder.setModifiedBy(modifiedBy);
        thisFolder.setId(-1);
        if (thisFolder.getParentId() > -1) {
          if (folderMap.containsKey(new Integer(thisFolder.getParentId()))) {
            int parentId = ((Integer) folderMap.get(new Integer(thisFolder.getParentId()))).intValue();
            thisFolder.setParentId(parentId);
          } else {
            thisFolder.setParentId(-1);
          }
        }
        thisFolder.setPrevIndent(prevIndent);
        thisFolder.setPrevMapId(prevMapId);
        thisFolder.setIndent(thisItem.getIndent());
        thisFolder.insert(db);
        prevIndent = thisFolder.getPrevIndent();
        prevMapId = thisFolder.getPrevMapId();
        int newId = thisFolder.getId();
        folderMap.put(new Integer(oldId), new Integer(newId));
      }
    }
  }

}

