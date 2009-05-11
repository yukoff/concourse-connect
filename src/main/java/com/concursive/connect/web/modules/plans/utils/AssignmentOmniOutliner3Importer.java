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

package com.concursive.connect.web.modules.plans.utils;

import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.web.modules.plans.dao.Assignment;
import com.concursive.connect.web.modules.plans.dao.AssignmentFolder;
import com.concursive.connect.web.modules.plans.dao.Requirement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;


/**
 * Imports data from other formats into an outline
 *
 * @author matt rajkowski
 * @created June 30, 2004
 */
public class AssignmentOmniOutliner3Importer {


  public static boolean parse(byte[] buffer, Requirement requirement, Connection db) throws SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentOmniOutliner3Importer-> parseOmniOutliner3");
    }
    try {
      db.setAutoCommit(false);

      // stream the XML Outline from the uploaded byte array, ignore the DTD
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setEntityResolver(
          new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, java.io.IOException {
              return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='utf-8' ?>".getBytes()));
            }
          });
      Document document = null;
      try {
        // try to read as compressed format
        document = builder.parse(new GZIPInputStream(new ByteArrayInputStream(buffer)));
      } catch (Exception nrf) {
        // try to read without compression
        document = builder.parse(new ByteArrayInputStream(buffer));
      }
      if (document == null) {
        throw new Exception("Invalid oo3 FILE FORMAT");
      }
      short itemPosition = -1;
      short priorityPosition = -1;
      short assignedToPosition = -1;
      short effortPosition = -1;
      short startPosition = -1;
      short endPosition = -1;
      short folderPosition = -1;
      short percentPosition = -1;
      // Parse the columns
      ArrayList columnList = new ArrayList();
      Element columnElement = XMLUtils.getFirstChild(document, "columns");
      XMLUtils.getAllChildren(columnElement, columnList);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentOmniOutliner3Importer-> Columns: " + columnList.size());
      }
      Iterator columnIterator = columnList.iterator();
      short position = -1;
      boolean foundOutline = false;
      while (columnIterator.hasNext()) {
        Element columnNode = (Element) columnIterator.next();
        if ("yes".equals(columnNode.getAttribute("is-note-column"))) {
          continue;
        }
        ++position;
        Element column1 = XMLUtils.getFirstChild(columnNode, "title");
        Element column2 = XMLUtils.getFirstChild(column1, "text");
        Element column3 = XMLUtils.getFirstChild(column2, "p");
        Element column4 = XMLUtils.getFirstChild(column3, "run");
        Element column = XMLUtils.getFirstChild(column4, "lit");
        String columnName = XMLUtils.getNodeText(column);
        if (System.getProperty("DEBUG") != null) {
          System.out.println(
              "AssignmentOmniOutliner3Importer-> Column name: " + columnName);
        }
        if ("yes".equals(columnNode.getAttribute("is-outline-column"))) {
          foundOutline = true;
        }
        if ("topic".equalsIgnoreCase(columnName)) {
          itemPosition = position;
        } else if ("priority".equalsIgnoreCase(columnName)) {
          priorityPosition = position;
        } else if ("lead".equalsIgnoreCase(columnName)) {
          assignedToPosition = position;
        } else if ("assigned to".equalsIgnoreCase(columnName)) {
          assignedToPosition = position;
        } else if ("effort".equalsIgnoreCase(columnName)) {
          effortPosition = position;
        } else if ("start".equalsIgnoreCase(columnName)) {
          startPosition = position;
        } else if ("end".equalsIgnoreCase(columnName)) {
          endPosition = position;
        } else if ("folder?".equalsIgnoreCase(columnName)) {
          folderPosition = position;
        } else if ("percent".equalsIgnoreCase(columnName)) {
          percentPosition = position;
        }
      }
      // Process the outline
      if (foundOutline) {
        ArrayList itemList = new ArrayList();
        Element rootElement = XMLUtils.getFirstChild(document, "root");
        XMLUtils.getAllChildren(rootElement, "item", itemList);
        if (System.getProperty("DEBUG") != null) {
          System.out.println("AssignmentOmniOutliner3Importer-> Top-Level Outline Record Items: " + itemList.size());
        }
        // Go through the items, for each item see if it has children, with items, etc.
        Iterator itemIterator = itemList.iterator();
        while (itemIterator.hasNext()) {
          Element itemElement = (Element) itemIterator.next();
          parseItemElement3(itemElement, db, 0, requirement.getProjectId(), requirement.getId(),
              requirement.getEnteredBy(), requirement.getModifiedBy(),
              itemPosition, priorityPosition, assignedToPosition, effortPosition, startPosition, endPosition,
              folderPosition, percentPosition);
        }
      }
      db.commit();
    } catch (Exception e) {
      db.rollback();
      e.printStackTrace(System.out);
      return false;
    } finally {
      db.setAutoCommit(true);
    }
    return true;
  }

  private static void parseItemElement3(Element item, Connection db, int indent, int projectId, int requirementId,
                                        int enteredBy, int modifiedBy,
                                        int itemPosition, int priorityPosition, int assignedToPosition, int effortPosition,
                                        int startPosition, int endPosition, int folderPosition, int percentPosition) throws Exception {
    // Get the values for the item
    ArrayList valuesList = new ArrayList();
    Element valuesElement = XMLUtils.getFirstChild(item, "values");
    XMLUtils.getAllChildren(valuesElement, valuesList);

    boolean isFolder = false;
    // Default this item to a folder if it has children
    Element childrenElement = XMLUtils.getFirstChild(item, "children");
    if (childrenElement != null) {
      isFolder = true;
    }
    // If it does not have children, see if there is an explicit folderPosition
    if (!isFolder && folderPosition > -1) {
      isFolder = "checked".equals(extractText3((Element) valuesList.get(folderPosition)));
    }
    if (isFolder) {
      // Insert the folder
      AssignmentFolder folder = new AssignmentFolder();
      folder.setProjectId(projectId);
      folder.setRequirementId(requirementId);
      folder.setIndent(indent);
      folder.setEnteredBy(enteredBy);
      folder.setModifiedBy(modifiedBy);
      // Topic
      if (itemPosition > -1) {
        folder.setName(extractText3((Element) valuesList.get(itemPosition)));
      }
      // TODO: Check for notes...
      folder.insert(db);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentOmniOutliner3Importer-> Folder Inserted: " + folder.getId());
      }
    } else {
      // Insert the assignment
      Assignment assignment = new Assignment();
      assignment.setProjectId(projectId);
      assignment.setRequirementId(requirementId);
      assignment.setIndent(indent);
      assignment.setEnteredBy(enteredBy);
      assignment.setModifiedBy(modifiedBy);
      // Topic
      if (itemPosition > -1) {
        assignment.setRole(extractText3((Element) valuesList.get(itemPosition)));
      }
      // Effort
      if (effortPosition > -1) {
        assignment.setEstimatedLoe(extractText3((Element) valuesList.get(effortPosition)));
        if (assignment.getEstimatedLoeTypeId() == -1) {
          assignment.setEstimatedLoeTypeId(2);
        }
      }
      // Start Date
      if (startPosition > -1) {
        String tmp = extractText3((Element) valuesList.get(startPosition));
        if (tmp != null && tmp.indexOf("00:00:00") > -1) {
          tmp = tmp.substring(0, tmp.indexOf("00:00:00") + 8);
        }
        assignment.setEstStartDate(tmp);
      }
      // End Date
      if (endPosition > -1) {
        String tmp = extractText3((Element) valuesList.get(endPosition));
        if (tmp != null && tmp.indexOf("00:00:00") > -1) {
          tmp = tmp.substring(0, tmp.indexOf("00:00:00") + 8);
        }
        assignment.setDueDate(tmp);
      }
      // Priority
      if (priorityPosition > -1) {
        assignment.setPriorityId(extractText3((Element) valuesList.get(priorityPosition)));
      }
      // Make sure a valid priority is set
      if (assignment.getPriorityId() < 1 || assignment.getPriorityId() > 3) {
        assignment.setPriorityId(2);
      }
      // Assigned To
      if (assignedToPosition > -1) {
        assignment.addUsers(extractText3((Element) valuesList.get(assignedToPosition)));
      }
      // Make sure user is on team, before adding, else unset the field
      if (!assignment.hasValidTeam(db)) {
        assignment.getAssignedUserList().clear();
      }
      // Status (default to Not Started)
      assignment.setStatusId(1);
      // Completed
      if ("checked".equals(item.getAttribute("state"))) {
        // Item is complete
        assignment.setStatusId(5);
        if (percentPosition == -1) {
          assignment.setPercentComplete(100);
        }
      }
      // Percent (overrides completion)
      if (percentPosition > -1) {
        assignment.setPercentComplete(extractText3((Element) valuesList.get(percentPosition)));
      }
      // If incomplete, but work has been done, then set to In Progress
      if (assignment.getPercentComplete() > 0 && assignment.getStatusId() == 1) {
        assignment.setStatusId(2);
      }
      // TODO: Check for notes...
      assignment.insert(db);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentOmniOutliner3Importer-> Assignment Inserted: " + assignment.getId());
      }
    }

    // See if there are children, then parse the children items
    if (childrenElement != null) {
      ArrayList itemList = new ArrayList();
      XMLUtils.getAllChildren(childrenElement, itemList);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentOmniOutliner3Importer-> Children items: " + itemList.size());
      }
      // Go through the items, for each item see if it has children, with items, etc.
      Iterator itemIterator = itemList.iterator();
      while (itemIterator.hasNext()) {
        Element itemElement = (Element) itemIterator.next();
        parseItemElement3(itemElement, db, (indent + 1), projectId, requirementId,
            enteredBy, modifiedBy,
            itemPosition, priorityPosition, assignedToPosition, effortPosition, startPosition, endPosition, folderPosition, percentPosition);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param element Description of the Parameter
   * @return Description of the Return Value
   * @throws Exception Description of the Exception
   */
  public static String extractText3(Element element) throws Exception {
    if (element == null) {
      return "";
    }
    Element p = XMLUtils.getFirstChild(element, "p");
    if (p == null) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println(
            "AssignmentOmniOutliner3Importer-> TEXT: " + XMLUtils.getNodeText(element));
      }
      return XMLUtils.getNodeText(element);
    }
    Element run = XMLUtils.getFirstChild(p, "run");
    Element lit = XMLUtils.getFirstChild(run, "lit");
    if (System.getProperty("DEBUG") != null) {
      System.out.println(
          "AssignmentOmniOutliner3Importer-> TEXT: " + XMLUtils.getNodeText(lit));
    }
    return XMLUtils.getNodeText(lit);
  }
}
