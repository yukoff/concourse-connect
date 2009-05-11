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
package com.concursive.connect.web.modules.reviews.portlets.main;

import com.concursive.connect.web.modules.common.social.rating.beans.RatingBean;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.profile.utils.ProjectUtils;
import com.concursive.connect.web.portal.IPortletViewer;
import static com.concursive.connect.web.portal.PortalUtils.findProject;
import static com.concursive.connect.web.portal.PortalUtils.getUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.portlet.*;

/**
 * Project rating ajax command
 *
 * @author matt rajkowski
 * @created October 31, 2008
 */
public class ReviewsSetProjectRatingViewer implements IPortletViewer {

  // Logger
  private static Log LOG = LogFactory.getLog(ReviewsSetProjectRatingViewer.class);

  // Pages
  private static final String VIEW_PAGE = "/star_rating.jsp";

  // Object Results
  private static final String RATING_BEAN = "ratingBean";

  public String doView(RenderRequest request, RenderResponse response) throws Exception {
    // The JSP to show upon success
    String defaultView = VIEW_PAGE;

    // Determine the project container to use
    Project project = findProject(request);

    // Check the user's permissions
    User user = getUser(request);
    if (!ProjectUtils.hasAccess(project.getId(), user, "project-reviews-add")) {
      throw new PortletException("Unauthorized to add in this project");
    }

    //The owner of a project may not review the project unless he is an admin.
    if (project.getOwner() != -1 && project.getOwner() == user.getId() && !ProjectUtils.hasAccess(project.getId(), user, "project-reviews-admin")) {
      throw new PortletException("PermissionError");
    }

    // Parameters
    String vote = request.getParameter("v");
    boolean showText = "true".equals(request.getParameter("ratingShowText"));

    PortletURL renderURL = response.createRenderURL();
    renderURL.setParameter("portlet-command", "setProjectRating");
    renderURL.setParameter("v", "${vote}");
    renderURL.setParameter("out", "text");
    if ("true".equals(request.getParameter("popup"))) {
      renderURL.setParameter("popup", "true");
    }
    renderURL.setWindowState(WindowState.MAXIMIZED);
    String url = renderURL.toString();
    LOG.debug("URL: " + url);

    request.setAttribute("url", url);
    request.setAttribute("ratingShowText", showText);
    request.setAttribute("field", "rating");

    // Prepare the bean
    RatingBean ratingBean = new RatingBean();
    ratingBean.setCount(1);
    ratingBean.setItemId(project.getId());
    ratingBean.setValue(Integer.parseInt(vote));
    request.setAttribute(RATING_BEAN, ratingBean);

    return defaultView;
  }
}