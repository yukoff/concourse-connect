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

import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.commons.web.URLFactory;
import com.concursive.connect.cms.portal.beans.PortalBean;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.modules.blog.dao.BlogPostList;
import com.concursive.connect.web.modules.profile.dao.ProjectList;
import com.concursive.connect.web.modules.translation.dao.WebSiteLanguage;
import com.concursive.connect.web.modules.translation.dao.WebSiteLanguageList;
import com.concursive.connect.web.utils.ClientType;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Utilities for working with the integrated website capability
 *
 * @author matt rajkowski
 * @created May 13, 2008
 */
public class WebPortalUtils {
  public static boolean isVersion1Url(PortalBean bean, ActionContext context, Connection db) throws SQLException {
    // Use case: an old style url is referenced (Portal.do?key=xxx[&nid=yyy])
    if (bean.getProjectKey() != null || bean.getNewsId() > -1) {
      String redirect;
      if (bean.getProjectId() == -1) {
        // Load the project based on the key, then get the homePageId
        bean.setProjectId(ProjectList.queryIdFromKey(db, bean.getProjectKey()));
      }
      if (bean.getNewsId() == -1) {
        // Load the newsId
        bean.setNewsId(BlogPostList.queryHomePageId(db, bean.getProjectId()));
      }
      redirect = BlogPostList.queryRedirect(db, bean.getNewsId());
      if (redirect == null) {
        redirect = "";
      }
      redirect = redirect.trim();
      context.getRequest().setAttribute("redirectTo", redirect);
      context.getRequest().removeAttribute("PageLayout");
      return true;
    }
    return false;
  }

  public static boolean isUnexpectedHost(PortalBean bean, ActionContext context, ApplicationPrefs prefs) {
    // Use case: An old domain name or virtual host is used
    String expectedDomainName = prefs.get(ApplicationPrefs.WEB_DOMAIN_NAME);
    if (expectedDomainName != null &&
        !"127.0.0.1".equals(bean.getServerName()) &&
        !"localhost".equals(bean.getServerName()) &&
        !bean.getServerName().equals(expectedDomainName)) {
      // Preserve the URI
      String uri = "";
      String uriValue = context.getRequest().getRequestURI();
      if (uriValue != null && !uriValue.substring(1).equals(prefs.get("PORTAL.INDEX"))) {
        uriValue = uriValue.substring(uriValue.lastIndexOf("/") + 1);
        if (uriValue != null && !uriValue.startsWith("/")) {
          uriValue = "/" + uriValue;
        }
        uri = uriValue;
      }
      // Convert the current URL to a redirect of the expected URL
      HashMap expectedURL = new HashMap();
      expectedURL.put(ApplicationPrefs.WEB_SCHEME, context.getRequest().getScheme());
      expectedURL.put(ApplicationPrefs.WEB_DOMAIN_NAME, expectedDomainName);
      expectedURL.put(ApplicationPrefs.WEB_PORT, context.getRequest().getServerPort());
      String newUrl = URLFactory.createURL(expectedURL);
      // Send a redirect
      context.getRequest().setAttribute("redirectTo", newUrl + uri);
      context.getRequest().removeAttribute("PageLayout");
      return true;
    }
    return false;
  }

  public static void checkForLanguageInURL(HttpServletRequest request) {
    ClientType clientType = (ClientType) request.getSession().getAttribute("clientType");
    WebSiteLanguageList webSiteLanguageList = (WebSiteLanguageList) request.getAttribute("webSiteLanguageList");
    // The url already contains a language, and it's different from the clientType...
    String urlLanguage = webSiteLanguageList.getLanguage(request);
    if (urlLanguage != null && !urlLanguage.equals(clientType.getLanguage())) {
      clientType.setLanguage(urlLanguage);
    }
  }

  public static void checkForLanguageSelection(HttpServletRequest request, PortalBean bean) {
    ClientType clientType = (ClientType) request.getSession().getAttribute("clientType");
    WebSiteLanguageList webSiteLanguageList = (WebSiteLanguageList) request.getAttribute("webSiteLanguageList");
    // The user is changing languages using the drop-down
    if (webSiteLanguageList.getLanguage(bean.getLanguage()) != null) {
      if (!bean.getLanguage().equals(clientType.getLanguage())) {
        clientType.setLanguage(bean.getLanguage());
      }
    }
  }

  public static void checkForValidClientLanguage(HttpServletRequest request) {
    ClientType clientType = (ClientType) request.getSession().getAttribute("clientType");
    WebSiteLanguageList webSiteLanguageList = (WebSiteLanguageList) request.getAttribute("webSiteLanguageList");
    // The user has a language that isn't a match with enabled web sites
    if (webSiteLanguageList.getLanguage(clientType.getLanguage()) == null) {
      clientType.setLanguage(webSiteLanguageList.getDefault());
    }

  }

  public static boolean clientLanguageMatchesURL(HttpServletRequest request) {
    ClientType clientType = (ClientType) request.getSession().getAttribute("clientType");
    WebSiteLanguageList webSiteLanguageList = (WebSiteLanguageList) request.getAttribute("webSiteLanguageList");
    //Portal-> URI: /index.shtml   <--->   Portal-> URL: http://127.0.0.1:8080/index.shtml
    //Portal-> URI: /en_AU/index.shtml   <--->   Portal-> URL: http://127.0.0.1:8080/en_AU/index.shtml
    if (request.getRequestURI().indexOf(clientType.getLanguage()) == -1 &&
        !clientType.getLanguage().equals(webSiteLanguageList.getDefault())) {
      return false;
    }
    if (request.getRequestURI().indexOf(clientType.getLanguage()) > -1 &&
        clientType.getLanguage().equals(webSiteLanguageList.getDefault())) {
      return false;
    }
    return true;
  }

  public static boolean isChangingLanguage(ActionContext context, ApplicationPrefs prefs, PortalBean bean) {
    // Base the response on the user's preferred language
    ClientType clientType = (ClientType) context.getSession().getAttribute("clientType");
    if (clientType == null) {
      return false;
    }
    WebSiteLanguageList webSiteLanguageList = (WebSiteLanguageList) context.getRequest().getAttribute("webSiteLanguageList");
    if (webSiteLanguageList == null) {
      return false;
    }
    checkForLanguageInURL(context.getRequest());
    checkForLanguageSelection(context.getRequest(), bean);
    checkForValidClientLanguage(context.getRequest());
    if (!clientLanguageMatchesURL(context.getRequest())) {

      // Redirect to language URL
      WebSiteLanguage webSite = webSiteLanguageList.getLanguage(clientType.getLanguage());

      //Portal-> URI: /index.shtml   <--->   Portal-> URL: http://127.0.0.1:8080/index.shtml
      //Portal-> URI: /en_AU/index.shtml   <--->   Portal-> URL: http://127.0.0.1:8080/en_AU/index.shtml

      // Determine the URI
      String uriToUse = "";
      String uri = context.getRequest().getRequestURI();
      //if (uri != null && !uri.endsWith(prefs.get("PORTAL.INDEX"))) {
      //  uriToUse = uri.substring(uri.lastIndexOf("/") + 1);
      //}
      if (uri != null && !uri.substring(uri.lastIndexOf("/") + 1).equals(prefs.get("PORTAL.INDEX"))) {
        uriToUse = uri.substring(uri.lastIndexOf("/") + 1);
      }

      // Determine the URL
      String webSiteLanguage = "/";
      if (!webSiteLanguageList.getDefault().equals(webSite.getLanguageLocale())) {
        webSiteLanguage = "/" + webSite.getLanguageLocale() + "/";
      }

      String url = RequestUtils.getAbsoluteServerUrl(context.getRequest()) + webSiteLanguage + uriToUse;
      context.getRequest().setAttribute("redirectTo", url);
      context.getRequest().removeAttribute("PageLayout");
      return true;
    }
    // Make sure the bean has the proper matching language
    bean.setLanguageId(webSiteLanguageList.getLanguage(clientType.getLanguage()).getId());
    return false;
  }

  public static boolean hasRedirect(ActionContext context, PortalBean bean) {
    if (bean.getRedirect() != null) {
      context.getRequest().setAttribute("redirectTo", bean.getRedirect());
      context.getRequest().removeAttribute("PageLayout");
      return true;
    }
    return false;
  }
}
