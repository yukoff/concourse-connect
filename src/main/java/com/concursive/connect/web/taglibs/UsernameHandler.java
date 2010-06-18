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

package com.concursive.connect.web.taglibs;

import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.cms.portal.utils.Tracker;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.PortalUtils;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * Displays a link to the specified userId
 *
 * @author matt rajkowski
 * @version $Id$
 * @created June 19, 2003
 */
public class UsernameHandler extends TagSupport implements TryCatchFinally {

  private int userId = -1;
  private boolean showPresence = true;
  private boolean showProfile = true;
  private boolean jsEscape = false;
  private boolean jsQuote = false;
  private boolean showLink = true;
  private boolean showLinkTitle = true;
  private String idTag = null;
  private boolean showCityState = false;

  public void doCatch(Throwable throwable) throws Throwable {
    // Required but not needed
  }

  public void doFinally() {
    // Reset each property or else the value gets reused
    userId = -1;
    showPresence = true;
    showProfile = true;
    jsEscape = false;
    jsQuote = false;
    showLink = true;
    showLinkTitle = true;
    idTag = null;
    showCityState = false;
  }


  /**
   * Sets the id attribute of the UsernameHandler object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.userId = Integer.parseInt(tmp);
  }


  /**
   * Sets the id attribute of the UsernameHandler object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.userId = tmp;
  }

  public void setShowPresence(boolean showPresence) {
    this.showPresence = showPresence;
  }

  public void setShowProfile(boolean showProfile) {
    this.showProfile = showProfile;
  }

  public void setJsEscape(boolean jsEscape) {
    this.jsEscape = jsEscape;
  }

  public void setJsQuote(boolean jsQuote) {
    this.jsQuote = jsQuote;
  }

  public void setShowLink(boolean showLink) {
    this.showLink = showLink;
  }

  public void setShowLinkTitle(boolean showLinkTitle) {
    this.showLinkTitle = showLinkTitle;
  }

  public void setIdTag(String idTag) {
    this.idTag = idTag;
  }

  /**
   * @return the showCityState
   */
  public boolean getShowCityState() {
    return showCityState;
  }

  /**
   * @param showCityState the showCityState to set
   */
  public void setShowCityState(boolean showCityState) {
    this.showCityState = showCityState;
  }

  public void setShowCityState(String showCityState) {
    this.showCityState = DatabaseUtils.parseBoolean(showCityState);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    try {
      // Get the user from cache
      User userObject = (User) CacheUtils.getObjectValue(Constants.SYSTEM_USER_CACHE, userId);

      ApplicationPrefs prefs = (ApplicationPrefs) this.pageContext.getServletContext().getAttribute(Constants.APPLICATION_PREFS);
      String user = "";
      if ("true".equals(prefs.get(ApplicationPrefs.USERS_ARE_ANONYMOUS))) {
        user = userObject.getNameFirstLastInitial();
      } else {
        user = userObject.getNameFirstLast();
      }
      // Find a related profile
      boolean foundProfile = false;
      Project userProject = null;
      if (userObject.getProfileProjectId() > -1) {
        userProject = ProjectUtils.loadProject(userObject.getProfileProjectId());
      }
      if (showProfile) {
        if (userProject != null) {
          // Determine if access to profiles is limited
          if ("true".equals(prefs.get(ApplicationPrefs.INFORMATION_IS_SENSITIVE))) {
            // Check this user's access
            User thisUser = null;
            PortletRequest renderRequest = (PortletRequest) pageContext.getRequest().getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
            // Check the portlet request object
            if (renderRequest != null) {
              thisUser = PortalUtils.getUser(renderRequest);
            }
            // Check the session object
            if (thisUser == null) {
              thisUser = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
            }
            // Deny if not allowed
            foundProfile = !(thisUser == null || !thisUser.isLoggedIn());
          } else {
            // Link to the user
            foundProfile = true;
          }
        }
      }
      if (userProject != null && showCityState && StringUtils.hasText(userProject.getCityStateString())) {
        user = user + " " + userProject.getCityStateString();
      }
      // Output as requested
      if (foundProfile) {
        if (showLink) {
          this.pageContext.getOut().write(
              "<a " +
                  "href=\"" + RequestUtils.getAbsoluteServerUrl((HttpServletRequest) pageContext.getRequest()) + "/show/" + userProject.getUniqueId() + "\"" +
                  (idTag != null ? " id=\"" + idTag + "\"" : "") +
                  (showLinkTitle ? " title=\"Profile for " + user + "\"" : "") +
                  ">");
        }
      }
      if (jsEscape) {
        this.pageContext.getOut().write(StringUtils.jsEscape(user));
      } else if (jsQuote) {
        this.pageContext.getOut().write(StringUtils.jsQuote(user));
      } else {
        this.pageContext.getOut().write(StringUtils.toHtml(user));
      }
      if (foundProfile) {
        if (showLink) {
          this.pageContext.getOut().write("</a>");
        }
      }
      // display if user is online
      if (showPresence) {
        Tracker thisTracker = (Tracker) pageContext.getServletContext().getAttribute("Tracker");
        if (thisTracker != null) {
          if (thisTracker.hasUserId(userId)) {
            this.pageContext.getOut().write(" <img border='0' src='" + RequestUtils.getAbsoluteServerUrl((HttpServletRequest) pageContext.getRequest()) + "/images/icons/online.gif' align='absmiddle' />");
          }
        }
      }
    } catch (Exception e) {
      throw new JspException("Username Error: " + e.getMessage());
    }
    return SKIP_BODY;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public int doEndTag() {
    return EVAL_PAGE;
  }

}

