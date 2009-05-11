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
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created January 21, 2004
 */
public class ProjectNameHandler extends TagSupport {

  private int projectId = -1;
  private boolean showLink = false;


  /**
   * Sets the id attribute of the ProjectNameHandler object
   *
   * @param tmp The new id value
   */
  public void setId(String tmp) {
    this.projectId = Integer.parseInt(tmp);
  }


  /**
   * Sets the id attribute of the ProjectNameHandler object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.projectId = tmp;
  }

  public void setShowLink(boolean showLink) {
    this.showLink = showLink;
  }

  public void setShowLink(String showLink) {
    this.showLink = DatabaseUtils.parseBoolean(showLink);
  }

  /**
   * Outputs the project name if found from the ProjectNameCache
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    try {
      String title = CacheUtils.getStringValue(Constants.SYSTEM_PROJECT_NAME_CACHE, projectId);
      if (showLink) {
        if (title != null) {
          Project project = ProjectUtils.loadProject(projectId);
          this.pageContext.getOut().write(
              "<a " +
                  "href=\"" + RequestUtils.getAbsoluteServerUrl((HttpServletRequest) pageContext.getRequest()) + "/show/" + project.getUniqueId() + "\">" +
                  StringUtils.toHtml(title) +
                  "</a>");
        } else {
          this.pageContext.getOut().write("&nbsp;");
        }
      } else {
        if (title != null) {
          this.pageContext.getOut().write(StringUtils.toHtml(title));
        } else {
          this.pageContext.getOut().write("&nbsp;");
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      throw new JspException("ProjectNameHandler-> Error: " + e.getMessage());
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

