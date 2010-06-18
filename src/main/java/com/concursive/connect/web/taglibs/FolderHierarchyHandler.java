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
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;

import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.LinkedHashMap;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id: FolderHierarchyHandler.java,v 1.4 2003/09/04 03:49:45 matt
 *          Exp $
 * @created April 20, 2003
 */
public class FolderHierarchyHandler extends TagSupport {
  private boolean showLastLink = false;


  /**
   * Sets the showLastLink attribute of the FolderHierarchyHandler object
   *
   * @param tmp The new showLastLink value
   */
  public void setShowLastLink(String tmp) {
    this.showLastLink = DatabaseUtils.parseBoolean(tmp);
  }


  /**
   * Sets the showLastLink attribute of the FolderHierarchyHandler object
   *
   * @param tmp The new showLastLink value
   */
  public void setShowLastLink(boolean tmp) {
    this.showLastLink = tmp;
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   * @throws JspException Description of the Exception
   */
  public int doStartTag() throws JspException {
    try {

      Project thisProject = null;
      String projectId = pageContext.getRequest().getParameter("pid");
      if (projectId != null) {
        thisProject = ProjectUtils.loadProject(Integer.parseInt(projectId));
      }

      if (thisProject == null) {
        PortletRequest renderRequest = (PortletRequest) pageContext.getRequest().getAttribute(org.apache.pluto.tags.Constants.PORTLET_REQUEST);
        RenderResponse renderResponse = (RenderResponse) pageContext.getRequest().getAttribute(org.apache.pluto.tags.Constants.PORTLET_RESPONSE);
        thisProject = (Project) renderRequest.getAttribute("project");
        projectId = String.valueOf(thisProject.getId());
      }

      String ctx = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
      if (ctx == null) {

      }


      //Show the open folder image
      this.pageContext.getOut().write("<img border=\"0\" src=\"" + RequestUtils.getAbsoluteServerUrl((HttpServletRequest) pageContext.getRequest()) + "/images/icons/stock_home-16.gif\" align=\"absmiddle\"> ");
      //Generate the folder path
      LinkedHashMap folderLevels = (LinkedHashMap) pageContext.getRequest().getAttribute("folderLevels");
      if (folderLevels == null) {
        if (showLastLink) {
          this.pageContext.getOut().write("<a href=\"" + ctx + "/show/" + thisProject.getUniqueId() + "/documents\">");
        }
        this.pageContext.getOut().write("Top Folder");
        if (showLastLink) {
          this.pageContext.getOut().write("</a>");
        }
      } else {
        Object[] hierarchy = folderLevels.keySet().toArray();
        if (hierarchy.length > 0) {
          //Show a Home link
          this.pageContext.getOut().write("<a href=\"" + ctx + "/show/" + thisProject.getUniqueId() + "/documents\">");
          final ApplicationPrefs prefs =
              (ApplicationPrefs) pageContext.getServletContext()
                  .getAttribute("applicationPrefs");
          final User user = (User) pageContext.getSession().getAttribute(Constants.SESSION_USER);
          final String language = (null != user.getLanguage())
              ? user.getLanguage()
              : prefs.get(ApplicationPrefs.LANGUAGE);
          final String topFolderName =
              prefs.getLabel("folderHierarchyHandler.topFolder", language);
          this.pageContext.getOut().write(((null != topFolderName)
              ? topFolderName
              : "Top Folder"));
          this.pageContext.getOut().write("</a>");
          //Show the rest of the links, except the last, unless specified
          this.pageContext.getOut().write(" > ");
          for (int i = hierarchy.length - 1; i >= 0; i--) {
            Integer thisId = (Integer) hierarchy[i];
            String[] sa = (String[]) folderLevels.get(thisId);
            String subject = sa[0];
            String display = sa[1];
            if (i > 0 || showLastLink) {
              if ("-1".equals(display)) {
                this.pageContext.getOut().write("<a href=\"" + ctx + "/show/" + thisProject.getUniqueId() + "/folder/" + thisId + "\">");
              } else {
                this.pageContext.getOut().write("<a href=\"" + ctx + "/ProjectManagement.do?command=ProjectCenter&section=File_Gallery&pid=" + projectId + "&folderId=" + thisId.intValue() + ("2".equals(display) ? "&details=true" : "") + "\">");
              }
            }
            this.pageContext.getOut().write(StringUtils.toHtml(subject));
            if (i > 0 || showLastLink) {
              this.pageContext.getOut().write("</a>");
            }
            if (i > 0) {
              this.pageContext.getOut().write(" > ");
            }
          }
        } else {
          //Show home link
          if (showLastLink) {
            this.pageContext.getOut().write("<a href=\"" + ctx + "/show/" + thisProject.getUniqueId() + "/documents\">");
          }
          this.pageContext.getOut().write("Top Folder");
          if (showLastLink) {
            this.pageContext.getOut().write("</a>");
          }
        }
      }
    } catch (Exception e) {
      throw new JspException("FolderHierarchyHandler Error: " + e.getMessage());
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

