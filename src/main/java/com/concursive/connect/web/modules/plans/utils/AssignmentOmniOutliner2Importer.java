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


/**
 * Imports data from other formats into an outline
 *
 * @author matt rajkowski
 * @created June 30, 2004
 */
public class AssignmentOmniOutliner2Importer {

  /**
   * Description of the Method
   *
   * @param buffer      Description of the Parameter
   * @param requirement Description of the Parameter
   * @param db          Description of the Parameter
   * @return Description of the Return Value
   * @throws java.sql.SQLException Description of the Exception
   */
  public static boolean parse(byte[] buffer, Requirement requirement, Connection db) throws SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentOmniOutliner2Importer-> parseOmniOutliner");
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
              return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
            }
          });
      Document document = builder.parse(new ByteArrayInputStream(buffer));
      // Position
      boolean positionItemComplete = false;
      // Umm... put in an object
      short itemPosition = -1;
      short priorityPosition = -1;
      short assignedToPosition = -1;
      short effortPosition = -1;
      short startPosition = -1;
      short endPosition = -1;
      // Parse the columns
      ArrayList columnList = new ArrayList();
      Element columnElement = XMLUtils.getFirstChild(document, "oo:columns");
      XMLUtils.getAllChildren(columnElement, columnList);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentOmniOutliner2Importer-> Columns: " + columnList.size());
      }
      Iterator columnIterator = columnList.iterator();
      short position = -1;
      boolean foundOutline = false;
      while (columnIterator.hasNext()) {
        Element columnNode = (Element) columnIterator.next();
        Element column = XMLUtils.getFirstChild(columnNode, "oo:title");
        String columnName = XMLUtils.getNodeText(column);
        if (System.getProperty("DEBUG") != null) {
          System.out.println(
              "AssignmentOmniOutliner2Importer-> Column name: " + columnName);
        }
        if ("yes".equals(
            (String) columnNode.getAttribute("is-outline-column"))) {
          foundOutline = true;
        }
        if (foundOutline) {
          ++position;
          if ("topic".equalsIgnoreCase(columnName)) {
            positionItemComplete = true;
            itemPosition = position;
          }
          if (positionItemComplete) {
            if ("priority".equalsIgnoreCase(columnName)) {
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
            }
          }
        }
      }
      // Process the outline
      if (positionItemComplete) {
        ArrayList itemList = new ArrayList();
        Element rootElement = XMLUtils.getFirstChild(document, "oo:root");
        XMLUtils.getAllChildren(rootElement, itemList);
        if (System.getProperty("DEBUG") != null) {
          System.out.println("AssignmentOmniOutliner2Importer-> Items: " + itemList.size());
        }
        // Go through the items, for each item see if it has children, with items, etc.
        Iterator itemIterator = itemList.iterator();
        while (itemIterator.hasNext()) {
          Element itemElement = (Element) itemIterator.next();
          parseItemElement(itemElement, db, 0, requirement.getProjectId(), requirement.getId(),
              requirement.getEnteredBy(), requirement.getModifiedBy(),
              itemPosition, priorityPosition, assignedToPosition, effortPosition, startPosition, endPosition);
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


  /**
   * Description of the Method
   *
   * @param item               Description of the Parameter
   * @param db                 Description of the Parameter
   * @param indent             Description of the Parameter
   * @param projectId          Description of the Parameter
   * @param requirementId      Description of the Parameter
   * @param enteredBy          Description of the Parameter
   * @param modifiedBy         Description of the Parameter
   * @param itemPosition       Description of the Parameter
   * @param priorityPosition   Description of the Parameter
   * @param assignedToPosition Description of the Parameter
   * @param effortPosition     Description of the Parameter
   * @param startPosition      Description of the Parameter
   * @param endPosition        Description of the Parameter
   * @throws Exception Description of the Exception
   */
  private static void parseItemElement(Element item, Connection db, int indent, int projectId, int requirementId,
                                       int enteredBy, int modifiedBy,
                                       int itemPosition, int priorityPosition, int assignedToPosition, int effortPosition, int startPosition, int endPosition) throws Exception {
    // Get the values for the item
    ArrayList valuesList = new ArrayList();
    Element valuesElement = XMLUtils.getFirstChild(item, "oo:values");
    XMLUtils.getAllChildren(valuesElement, valuesList);
    // Insert the assignment
    Assignment assignment = new Assignment();
    assignment.setProjectId(projectId);
    assignment.setRequirementId(requirementId);
    assignment.setRole(extractText((Element) valuesList.get(itemPosition)));
    assignment.setIndent(indent);
    if (effortPosition > -1) {
      assignment.setEstimatedLoe(extractText((Element) valuesList.get(effortPosition)));
      if (assignment.getEstimatedLoeTypeId() == -1) {
        assignment.setEstimatedLoeTypeId(2);
      }
    }
    if (startPosition > -1) {
      assignment.setEstStartDate(extractText((Element) valuesList.get(startPosition)));
    }
    if (endPosition > -1) {
      assignment.setDueDate(extractText((Element) valuesList.get(endPosition)));
    }
    assignment.setEnteredBy(enteredBy);
    assignment.setModifiedBy(modifiedBy);
    assignment.setStatusId(1);
    assignment.setPriorityId(2);
    assignment.insert(db);
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentOmniOutliner2Importer-> Assignment Inserted: " + assignment.getId());
    }

    // See if there are children, then parse the children items
    Element childrenElement = XMLUtils.getFirstChild(item, "oo:children");
    if (childrenElement != null) {
      ArrayList itemList = new ArrayList();
      XMLUtils.getAllChildren(childrenElement, itemList);
      if (System.getProperty("DEBUG") != null) {
        System.out.println("AssignmentOmniOutliner2Importer-> Children items: " + itemList.size());
      }
      // Go through the items, for each item see if it has children, with items, etc.
      Iterator itemIterator = itemList.iterator();
      while (itemIterator.hasNext()) {
        Element itemElement = (Element) itemIterator.next();
        parseItemElement(itemElement, db, (indent + 1), projectId, requirementId,
            enteredBy, modifiedBy,
            itemPosition, priorityPosition, assignedToPosition, effortPosition, startPosition, endPosition);
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
  public static String extractText(Element element) throws Exception {
    Element p = XMLUtils.getFirstChild(element, "oo:p");
    if (p == null) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println(
            "AssignmentOmniOutliner2Importer-> TEXT: " + XMLUtils.getNodeText(element));
      }
      return XMLUtils.getNodeText(element);
    }
    if (System.getProperty("DEBUG") != null) {
      System.out.println(
          "AssignmentOmniOutliner2Importer-> TEXT: " + XMLUtils.getNodeText(p));
    }
    return XMLUtils.getNodeText(p);
  }

}