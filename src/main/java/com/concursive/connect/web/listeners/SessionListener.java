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

package com.concursive.connect.web.listeners;

import com.concursive.connect.Constants;
import com.concursive.connect.cms.portal.utils.Tracker;
import com.concursive.connect.web.modules.login.dao.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Listener for monitoring session changes
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 30, 2004
 */
public class SessionListener implements HttpSessionAttributeListener, HttpSessionListener {

  /**
   * Description of the Method
   *
   * @param event Description of the Parameter
   */
  public void sessionCreated(HttpSessionEvent event) {
    //System.out.println("SessionListener-> created");
  }


  /**
   * Description of the Method
   *
   * @param event Description of the Parameter
   */
  public void sessionDestroyed(HttpSessionEvent event) {
    //System.out.println("SessionListener-> destroyed");
  }


  /**
   * Description of the Method
   *
   * @param event Description of the Parameter
   */
  public void attributeAdded(HttpSessionBindingEvent event) {
    ServletContext context = event.getSession().getServletContext();
    if (Constants.SESSION_USER.equals(event.getName())) {
      // Process the user session
      User thisUser = (User) event.getValue();
      thisUser.setSessionId(event.getSession().getId());
      // Add to the tracker
      Tracker tracker = (Tracker) context.getAttribute(Constants.USER_SESSION_TRACKER);
      tracker.add(thisUser);
    }
  }


  /**
   * Description of the Method
   *
   * @param event Description of the Parameter
   */
  public void attributeRemoved(HttpSessionBindingEvent event) {
    ServletContext context = event.getSession().getServletContext();
    //System.out.println("SessionListener-> removed: " + event.getName());
    if (Constants.SESSION_USER.equals(event.getName())) {
      User thisUser = (User) event.getValue();
      Tracker tracker = (Tracker) context.getAttribute(Constants.USER_SESSION_TRACKER);
      tracker.remove(thisUser);
    }
  }


  /**
   * Description of the Method
   *
   * @param event Description of the Parameter
   */
  public void attributeReplaced(HttpSessionBindingEvent event) {
    // This event has a handle to the old User object
    ServletContext context = event.getSession().getServletContext();
    //System.out.println("SessionListener-> replaced: " + event.getName());
    if (Constants.SESSION_USER.equals(event.getName())) {
      // New user
      User thisUser = (User) event.getSession().getAttribute(Constants.SESSION_USER);
      thisUser.setSessionId(event.getSession().getId());
      // Old user
      User oldUser = new User();
      oldUser.setSessionId(event.getSession().getId());
      // Update the tracker
      Tracker tracker = (Tracker) context.getAttribute(Constants.USER_SESSION_TRACKER);
      tracker.add(thisUser);
      tracker.remove(oldUser);
    }
  }

}

