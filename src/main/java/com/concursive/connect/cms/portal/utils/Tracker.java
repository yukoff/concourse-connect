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

package com.concursive.connect.cms.portal.utils;

import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.login.dao.User;
import net.sf.ehcache.Ehcache;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 30, 2004
 */
public class Tracker {

  private final static int ADD = 1;
  private final static int REMOVE = -1;
  private LinkedHashMap<Integer, ArrayList<TrackerElement>> users = new LinkedHashMap<Integer, ArrayList<TrackerElement>>();
  private ArrayList<String> guests = new ArrayList<String>();


  public Tracker() {
  }

  public int getGuestCount() {
    return guests.size();
  }

  public int getUserCount() {
    return users.size();
  }

  public void add(User thisUser) {
    adjustCount(thisUser, ADD);

  }

  public void remove(User thisUser) {
    adjustCount(thisUser, REMOVE);
  }

  private synchronized void adjustCount(User user, int adjustment) {
    String sessionId = user.getSessionId();
    if (ADD == adjustment) {
      if (user.getId() > 0) {
        // Items being tracked
        TrackerElement element = new TrackerElement(user.getId());
        element.setSessionId(user.getSessionId());
        element.setBrowserType(user.getBrowserType());
        // Append to any active sessions
        ArrayList<TrackerElement> elements = null;
        if (!users.containsKey(user.getId())) {
          elements = new ArrayList<TrackerElement>();
          users.put(user.getId(), elements);
        } else {
          elements = users.get(user.getId());
        }
        elements.add(element);
      } else {
        guests.add(sessionId);
      }
    } else {
      // Remove
      if (users.containsKey(user.getId())) {
        ArrayList<TrackerElement> elements = users.get(user.getId());
        for (TrackerElement element : elements) {
          if (element.getSessionId().equals(sessionId)) {
            elements.remove(element);
            break;
          }
        }
        if (elements.size() == 0) {
          users.remove(user.getId());
        }
      }
      if (guests.contains(sessionId)) {
        guests.remove(sessionId);
      }
    }
  }


  /**
   * Gets the totalMembers attribute of the Tracker object
   *
   * @param request Description of the Parameter
   * @return The totalMembers value
   */
  public int getTotalMembers(HttpServletRequest request) {
    try {
      Ehcache userCache = CacheUtils.getCache(Constants.SYSTEM_USER_CACHE);
      return userCache.getSize();
    } catch (Exception e) {
    }
    return -1;
  }

  public Collection<Integer> getUserList() {
    return users.keySet();
  }

  public boolean hasUserId(int id) {
    return users.containsKey(id);
  }

  public TrackerElement getLastUserElement(Integer userId) {
    ArrayList<TrackerElement> elements = users.get(userId);
    if (elements != null && elements.size() > 0) {
      return (elements.get(elements.size() - 1));
    }
    return null;
  }

  public int getSessionCount(int userId) {
    ArrayList<TrackerElement> elements = users.get(userId);
    if (elements != null) {
      return elements.size();
    }
    return 0;
  }
}

