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
import com.concursive.connect.web.modules.issues.dao.Ticket;
import com.concursive.connect.web.modules.userprofile.workflow.SendUserNotification;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id: QueryTicketJustUpdated.java,v 1.3 2007/03/19 21:23:13
 *          Exp $
 * @created March 19, 2007
 */
public class QueryTicketJustUpdated extends ObjectHookComponent implements ComponentInterface {

  public final static String PRIORITY_ISNEW = "ticketPriorityIsNew";
  public final static String LEVEL_ISNEW = "ticketLevelIsNew";
  public final static String SEVERITY_ISNEW = "ticketSeverityIsNew";
  public final static String PROBLEM_ISNEW = "ticketProblemIsNew";

  /**
   * Gets the description attribute of the QueryTicketJustClosed object
   *
   * @return The description value
   */
  public String getDescription() {
    return "Was the ticket just updated?";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean execute(ComponentContext context) {
    Ticket thisTicket = (Ticket) context.getThisObject();
    Ticket previousTicket = (Ticket) context.getPreviousObject();
    if (context.isUpdate()) {
      int counter = 0;
      counter = addIsNewValue(context, PRIORITY_ISNEW, thisTicket.getPriorityCode() != previousTicket.getPriorityCode(), counter);
      counter = addIsNewValue(context, LEVEL_ISNEW, thisTicket.getLevelCode() != previousTicket.getLevelCode(), counter);
      counter = addIsNewValue(context, SEVERITY_ISNEW, thisTicket.getSeverityCode() != previousTicket.getSeverityCode(), counter);
      counter = addIsNewValue(context, PROBLEM_ISNEW, !thisTicket.getProblem().equals(previousTicket.getProblem()), counter);
      return (counter > 0);
    } else if (context.isInsert()) {
      return false;
    }
    return false;
  }

  private static int addIsNewValue(ComponentContext context, String field, boolean isNew, int counter) {
    if (isNew) {
      String color = context.getParameter(SendUserNotification.ALERT_FONT_COLOR);
      if (color == null) {
        color = "red";
      }
      context.setAttribute(field, "<font color=\"" + color + "\">(updated)</font>");
      ++counter;
    } else {
      context.setAttribute(field, "");
    }
    return counter;
  }
}
