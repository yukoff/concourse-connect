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
package com.concursive.connect.cache;

import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.login.dao.Instance;
import com.concursive.connect.web.modules.login.dao.InstanceList;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;

/**
 * Provides the Application Instance
 *
 * @author matt rajkowski
 * @created July 6, 2009
 */
public class InstanceCacheEntryFactory implements CacheEntryFactory {

  private static final Log LOG = LogFactory.getLog(InstanceCacheEntryFactory.class);

  private CacheContext context = null;

  public InstanceCacheEntryFactory(CacheContext context) {
    this.context = context;
  }

  public Object createEntry(Object url) throws Exception {
    // The current URL
    // https://www.example.com/
    // http://www.example.com/
    // http://www.example.com:8080/
    // http://www.example.com/context/
    // http://www.example.com:8080/context/
    if (url == null) {
      return new Instance();
    }
    Connection db = null;
    try {
      db = CacheUtils.getConnection(context);
      InstanceList list = new InstanceList();
      // Determine the domain name and context
      String key = (String) url;
      int dIndex = key.indexOf("://") + 3;
      int pIndex = key.indexOf(":", dIndex);
      int cIndex = key.indexOf("/", dIndex);
      int eIndex = (pIndex != -1 ? pIndex : cIndex != -1 ? cIndex : key.length());
      if (dIndex > -1 && eIndex > -1) {
        LOG.info("Instance: " + key + " " + dIndex + "," + pIndex + "," + cIndex + "," + eIndex);
        String domainName = key.substring(dIndex, eIndex);
        String context = (cIndex != -1 ? key.substring(cIndex) : "/");
        LOG.info("Domain Name: " + domainName);
        LOG.info("Context: " + context);
        // Query the table
        list.setDomainName(domainName);
        list.setContext(context);
        list.buildList(db);
        if (list.size() > 0) {
          return list.get(0);
        }
      }
    } catch (Exception e) {
      LOG.error("Couldn't determine instance", e);
      return new Instance();
    } finally {
      CacheUtils.freeConnection(context, db);
    }
    return new Instance();
  }
}