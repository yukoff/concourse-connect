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

package com.concursive.connect.web.modules.issues.workflow;

import com.concursive.commons.workflow.ComponentContext;
import com.concursive.commons.workflow.ComponentInterface;
import com.concursive.commons.workflow.ObjectHookComponent;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.issues.dao.Ticket;
import com.concursive.connect.web.modules.issues.dao.TicketCategory;
import com.concursive.connect.web.modules.issues.dao.TicketContactList;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.utils.LookupElement;

import java.sql.Connection;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 30, 2004
 */
public class LoadTicketDetails extends ObjectHookComponent implements ComponentInterface {

  public final static String CATEGORY_LOOKUP = "ticketCategoryLookup";
  public final static String SUBCATEGORY1_LOOKUP = "ticketSubCategory1Lookup";
  public final static String SUBCATEGORY2_LOOKUP = "ticketSubCategory2Lookup";
  public final static String SUBCATEGORY3_LOOKUP = "ticketSubCategory3Lookup";
  public final static String SEVERITY_LOOKUP = "ticketSeverityLookup";
  public final static String PRIORITY_LOOKUP = "ticketPriorityLookup";
  public final static String ASSIGNED_TO_CONTACT = "ticketAssignedToContact";
  public final static String ENTERED_BY_CONTACT = "ticketEnteredByContact";
  public final static String MODIFIED_BY_CONTACT = "ticketModifiedByContact";
  public final static String PROJECT = "project";
  public final static String CONTACT_LIST = "ticketContactList";


  /**
   * Gets the description attribute of the LoadTicketDetails object
   *
   * @return The description value
   */
  public String getDescription() {
    return "Load all ticket information for use in other steps";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean execute(ComponentContext context) {
    boolean result = false;
    Ticket thisTicket = (Ticket) context.getThisObject();
    Connection db = null;
    try {
      db = getConnection(context);
      // Set project
      if (thisTicket.getProjectId() > 0) {
        Project project = new Project(db, thisTicket.getProjectId());
        context.setAttribute(PROJECT, project);
      } else {
        context.setAttribute(PROJECT, new Project());
      }
      // Set category
      if (thisTicket.getCatCode() > 0) {
        TicketCategory categoryLookup = new TicketCategory(db, thisTicket.getCatCode());
        context.setAttribute(CATEGORY_LOOKUP, categoryLookup);
      } else {
        context.setAttribute(CATEGORY_LOOKUP, new TicketCategory());
      }
      // Set subcategory 1
      if (thisTicket.getSubCat1() > 0) {
        TicketCategory subCategory1Lookup = new TicketCategory(db, thisTicket.getSubCat1());
        context.setAttribute(SUBCATEGORY1_LOOKUP, subCategory1Lookup);
      } else {
        context.setAttribute(SUBCATEGORY1_LOOKUP, new TicketCategory());
      }
      // Set subcategory 2
      if (thisTicket.getSubCat2() > 0) {
        TicketCategory subCategory2Lookup = new TicketCategory(db, thisTicket.getSubCat2());
        context.setAttribute(SUBCATEGORY2_LOOKUP, subCategory2Lookup);
      } else {
        context.setAttribute(SUBCATEGORY2_LOOKUP, new TicketCategory());
      }
      // Set subcategory 3
      if (thisTicket.getSubCat3() > 0) {
        TicketCategory subCategory3Lookup = new TicketCategory(db, thisTicket.getSubCat3());
        context.setAttribute(SUBCATEGORY3_LOOKUP, subCategory3Lookup);
      } else {
        context.setAttribute(SUBCATEGORY3_LOOKUP, new TicketCategory());
      }
      // Set severity
      if (thisTicket.getSeverityCode() > 0) {
        LookupElement severityLookup = new LookupElement(db, thisTicket.getSeverityCode(), "ticket_severity");
        context.setAttribute(SEVERITY_LOOKUP, severityLookup);
      } else {
        context.setAttribute(SEVERITY_LOOKUP, new LookupElement());
      }
      // Set priority
      if (thisTicket.getPriorityCode() > 0) {
        LookupElement priorityLookup = new LookupElement(db, thisTicket.getPriorityCode(), "ticket_priority");
        context.setAttribute(PRIORITY_LOOKUP, priorityLookup);
      } else {
        context.setAttribute(PRIORITY_LOOKUP, new LookupElement());
      }
      // Set modifiedby
      if (thisTicket.getModifiedBy() > 0) {
        context.setAttribute(MODIFIED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, thisTicket.getModifiedBy())).getNameFirstLastInitial());
      } else {
        context.setAttribute(MODIFIED_BY_CONTACT, "");
      }
      // Set enteredby
      if (thisTicket.getEnteredBy() > 0) {
        context.setAttribute(ENTERED_BY_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, thisTicket.getEnteredBy())).getNameFirstLastInitial());
      } else {
        context.setAttribute(ENTERED_BY_CONTACT, "");
      }
      // Set assignedto
      if (thisTicket.getAssignedTo() > 0) {
        context.setAttribute(ASSIGNED_TO_CONTACT, ((User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, thisTicket.getAssignedTo())).getNameFirstLastInitial());
      } else {
        context.setAttribute(ASSIGNED_TO_CONTACT, "");
      }
      // Load Contact List
      TicketContactList contactList = new TicketContactList();
      contactList.setTicketId(thisTicket.getId());
      contactList.buildList(db);
      context.setAttribute(CONTACT_LIST, contactList);
      result = true;
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return result;
  }
}

