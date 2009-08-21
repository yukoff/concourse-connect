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

package com.concursive.connect.cache.utils;

import com.concursive.connect.Constants;
import com.concursive.connect.cache.CacheContext;
import com.concursive.connect.web.utils.LookupList;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.distribution.RMIBootstrapCacheLoaderFactory;
import net.sf.ehcache.distribution.RMICacheReplicatorFactory;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utilties that a cache can use
 *
 * @author matt rajkowski
 * @created Apr 9, 2008
 */
public class CacheUtils {

  private static Log LOG = LogFactory.getLog(CacheUtils.class);

  public static MemoryStoreEvictionPolicy memoryStoreEvictionPolicy = MemoryStoreEvictionPolicy.LRU;
  public static boolean overflowToDisk = false;
  public static String diskStorePath = "";
  public static boolean eternal = true;
  public static long timeToLive = 0;
  public static long timeToIdle = 0;
  public static boolean diskPersistent = false;
  public static long diskExpiryThreadIntervalSeconds = 0;

  public static Connection getConnection(CacheContext context) throws SQLException {
    if (context.getConnectionPool() != null) {
      return context.getConnectionPool().getConnection(context.getConnectionElement());
    } else {
      return context.getUpgradeConnection();
    }
  }

  public static void freeConnection(CacheContext context, Connection db) {
    if (db != null && context.getConnectionPool() != null) {
      context.getConnectionPool().free(db);
    }
    db = null;
  }

  public static void renewConnection(CacheContext context, Connection db) {
    if (db != null) {
      context.getConnectionPool().renew(db);
    }
  }

  public static Ehcache getCache(String cacheName) {
    CacheManager manager = CacheManager.getInstance();
    return manager.getEhcache(cacheName);
  }

  public static String getStringValue(String cacheName, int key) {
    Ehcache cache = getCache(cacheName);
    Element element = cache.get(key);
    return String.valueOf(element.getValue());
  }

  public static LookupList getLookupList(String tableName) {
    Ehcache cache = getCache(Constants.SYSTEM_LOOKUP_LIST_CACHE);
    Element element = cache.get(tableName);
    return (LookupList) element.getObjectValue();
  }

  public static Object getObjectValue(String cacheName, int key) {
    Ehcache cache = getCache(cacheName);
    Element element = cache.get(key);
    return element.getObjectValue();
  }

  public static void updateValue(String cacheName, int key, String value) {
    Ehcache cache = getCache(cacheName);
    if (value != null) {
      cache.put(new Element(key, value));
    } else {
      invalidateValue(cacheName, key);
    }
  }

  public static void updateValue(String cacheName, String key, Object value) {
    Ehcache cache = getCache(cacheName);
    if (value != null) {
      cache.put(new Element(key, value));
    } else {
      invalidateValue(cacheName, key);
    }
  }

  public static void invalidateValue(String cacheName, int key) {
    Ehcache cache = getCache(cacheName);
    if (cache != null) {
      cache.remove(key);
    }
  }

  public static void invalidateValue(String cacheName, String key) {
    Ehcache cache = getCache(cacheName);
    if (cache != null) {
      cache.remove(key);
    }
  }

  public static Ehcache createInMemoryCache(String cacheName, CacheEntryFactory entryFactory, int maxElements) {
    CacheManager manager = CacheManager.getInstance();
    Ehcache cache = getCache(cacheName);
    if (cache == null) {
      // Create the cache
      cache = new Cache(cacheName,
          maxElements,
          memoryStoreEvictionPolicy, overflowToDisk,
          diskStorePath, eternal, timeToLive,
          timeToIdle, diskPersistent,
          diskExpiryThreadIntervalSeconds, null);
      // Associate the cacheEntryFactory with the cache
      SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(cache,
          entryFactory);
      // Add any additional listener properties
      if (manager.getCachePeerListener("RMI") != null) {
        LOG.info("Setting RMI properties");
        Properties properties = new Properties();
        properties.put("replicateAsynchronously", "true");
        properties.put("replicatePuts", "false");
        properties.put("replicateUpdates", "true");
        properties.put("replicateRemovals", "true");
        properties.put("replicateUpdatesViaCopy", "false");
        properties.put("asynchronousReplicationIntervalMillis", "1000");
        RMICacheReplicatorFactory factory = new RMICacheReplicatorFactory();
        CacheEventListener listener = factory.createCacheEventListener(properties);
        selfPopulatingCache.getCacheEventNotificationService().registerListener(listener);
        RMIBootstrapCacheLoaderFactory bootstrapFactory = new RMIBootstrapCacheLoaderFactory();
        BootstrapCacheLoader bootstrapCacheLoader = bootstrapFactory.createBootstrapCacheLoader(new Properties());
        selfPopulatingCache.setBootstrapCacheLoader(bootstrapCacheLoader);
        LOG.debug("RMI enabled");
      }
      // Make the cache available
      manager.addCache(selfPopulatingCache);
      LOG.info("cache created: " + cache.getName());
    }
    return cache;
  }

  public static Ehcache createInMemoryBlockingCache(String cacheName, int maxElements) {
    CacheManager manager = CacheManager.getInstance();
    Ehcache cache = getCache(cacheName);
    if (cache == null) {
      cache = new Cache(cacheName,
          maxElements,
          memoryStoreEvictionPolicy, overflowToDisk,
          diskStorePath, eternal, timeToLive,
          timeToIdle, diskPersistent,
          diskExpiryThreadIntervalSeconds, null);
      BlockingCache blockingCache = new BlockingCache(cache);
      manager.addCache(blockingCache);
      LOG.info("blocking cache created: " + cache.getName());
    }
    return cache;
  }
}
