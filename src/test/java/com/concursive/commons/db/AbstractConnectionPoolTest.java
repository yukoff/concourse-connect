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
package com.concursive.commons.db;

import com.concursive.connect.cache.CacheContext;
import com.concursive.connect.cache.Caches;
import com.concursive.commons.db.ConnectionElement;
import com.concursive.commons.db.ConnectionPool;
import com.concursive.connect.config.ApplicationPrefs;
import junit.framework.TestCase;
import junitx.util.PropertyManager;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.sql.Connection;

public abstract class AbstractConnectionPoolTest extends TestCase {

  protected static Log LOG = LogFactory.getLog(AbstractConnectionPoolTest.class);

  protected String DATABASE_URL = null;
  protected String DATABASE_USER = null;
  protected String DATABASE_PASSWORD = null;
  protected String DATABASE_DRIVER = null;

  protected ConnectionPool connectionPool;
  protected Connection db;
  protected ConnectionElement ce;
  protected ApplicationPrefs mockPrefs = new ApplicationPrefs();

  protected void setUp() throws Exception {

    DATABASE_URL = PropertyManager.getProperty("TEST.SITE.URL", PropertyManager.getProperty("SITE.URL"));
    DATABASE_USER = PropertyManager.getProperty("TEST.SITE.USER", PropertyManager.getProperty("SITE.USER"));
    DATABASE_PASSWORD = PropertyManager.getProperty("TEST.SITE.PASSWORD", PropertyManager.getProperty("SITE.PASSWORD"));
    DATABASE_DRIVER = PropertyManager.getProperty("TEST.SITE.DRIVER", PropertyManager.getProperty("SITE.DRIVER"));
    mockPrefs.add("FILELIBRARY", System.getProperty("java.io.tmpdir") + "/test-fileLibrary-" + System.currentTimeMillis() + File.separator);
    mockPrefs.add("TITLE", PropertyManager.getProperty("TEST.TITLE", PropertyManager.getProperty("TITLE")));

    connectionPool = new ConnectionPool();
    connectionPool.setDebug(false);
    connectionPool.setTestConnections(false);
    connectionPool.setAllowShrinking(true);
    connectionPool.setMaxConnections(5);
    connectionPool.setMaxIdleTime(60000);
    connectionPool.setMaxDeadTime(1000000);

    // Test a connection
    ce = new ConnectionElement(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    ce.setDriver(DATABASE_DRIVER);
    db = connectionPool.getConnection(ce);

    assertTrue("The database connection was null.", db != null);

    CacheManager.create();
    CacheContext cacheContext = new CacheContext();
    // Give the cache manager its own connection pool... this can speed up the web-tier
    // when background processing is occurring
    cacheContext.setConnectionPool(connectionPool);
    cacheContext.setConnectionElement(ce);
    cacheContext.setApplicationPrefs(mockPrefs);
    Caches.addCaches(cacheContext);
  }

  protected void tearDown() throws Exception {
    // Stop the cache manager
    CacheManager.getInstance().shutdown();
    // Close connections
    connectionPool.free(db);
    connectionPool.closeAllConnections();
    connectionPool.destroy();
    connectionPool = null;
  }
}
