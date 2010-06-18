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
package com.concursive.connect.cms.portal.cache;

import com.concursive.connect.cache.CacheContext;
import com.concursive.connect.cms.portal.dao.DashboardTemplate;
import com.concursive.connect.cms.portal.dao.DashboardTemplateList;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides dashboard pages, which are often used
 *
 * @author matt rajkowski
 * @created October 3, 2008
 */
public class DashboardPageCacheEntryFactory implements CacheEntryFactory {

  private static final Log LOG = LogFactory.getLog(DashboardPageCacheEntryFactory.class);

  private CacheContext context = null;

  public DashboardPageCacheEntryFactory(CacheContext context) {
    this.context = context;
  }

  public Object createEntry(Object key) throws Exception {
    if (key == null) {
      throw new Exception("DashboardPageCacheEntryFactory-> Invalid page specified: null");
    }
    // An entry is made up of a portal type and a page name
    String[] items = key.toString().split("[|]");
    String type = items[0];
    String page = items[1];
    String file = null;
    if (items.length > 2) {
      file = items[2];
    } else {
      file = "dashboards_en_US.xml";
    }

    LOG.debug("Loading cache object for [" + items.length + "] " + key.toString() + "|" + file);

    // Load the entry
    DashboardTemplateList templates = new DashboardTemplateList(type, file);
    if (templates.size() == 0) {
      LOG.warn("No template entries found in: " + file);
    }
    DashboardTemplate template = templates.getTemplateByName(page);
    if (template == null) {
      return null;
    }
    return template;
  }
}
