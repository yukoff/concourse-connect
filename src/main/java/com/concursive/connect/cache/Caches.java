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

import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.cms.portal.cache.DashboardPageCacheEntryFactory;
import com.concursive.connect.web.cache.LookupListCacheEntryFactory;
import com.concursive.connect.web.modules.badges.cache.BadgeCacheEntryFactory;
import com.concursive.connect.web.modules.common.social.geotagging.cache.LocationCacheEntryFactory;
import com.concursive.connect.web.modules.issues.cache.ProjectTicketIdCacheEntryFactory;
import com.concursive.connect.web.modules.login.cache.UserCacheEntryFactory;
import com.concursive.connect.web.modules.login.cache.ValidationTokenEntryFactory;
import com.concursive.connect.web.modules.profile.cache.ProjectCacheEntryFactory;
import com.concursive.connect.web.modules.profile.cache.ProjectCategoryCacheEntryFactory;
import com.concursive.connect.web.modules.profile.cache.ProjectNameCacheEntryFactory;
import com.concursive.connect.web.modules.profile.cache.ProjectUniqueIdCacheEntryFactory;
import com.concursive.connect.web.rss.cache.FeedCacheEntryFactory;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Programmatic addition of caches to support a CacheContext
 *
 * @author matt rajkowski
 * @created Apr 9, 2008
 */
public class Caches {

  private static Log LOG = LogFactory.getLog(Caches.class);

  public static void addCaches(CacheContext context) {
    LOG.info("adding caches");

    CacheManager manager = CacheManager.getInstance();

    // NOTE: The default createInMemoryCache requires that objects are invalidated by the system
    // otherwise the objects will not expire

    // 1 = Key
    CacheUtils.createInMemoryCache(Constants.SYSTEM_KEY_CACHE, new PrivateKeyCacheEntryFactory(context), 1);

    // userId = User
    CacheUtils.createInMemoryCache(Constants.SYSTEM_USER_CACHE, new UserCacheEntryFactory(context), 100000);

    // tableName = LookupList
    CacheUtils.createInMemoryCache(Constants.SYSTEM_LOOKUP_LIST_CACHE, new LookupListCacheEntryFactory(context), 100);

    // projectId = project title
    CacheUtils.createInMemoryCache(Constants.SYSTEM_PROJECT_NAME_CACHE, new ProjectNameCacheEntryFactory(context), 100000);

    // projectUniqueId = projectId
    CacheUtils.createInMemoryCache(Constants.SYSTEM_PROJECT_UNIQUE_ID_CACHE, new ProjectUniqueIdCacheEntryFactory(context), 100000);

    // projectId = project w/features w/permissions w/team members
    CacheUtils.createInMemoryCache(Constants.SYSTEM_PROJECT_CACHE, new ProjectCacheEntryFactory(context), 2000);

    // feed =
    {
      // Create a cache with 20 objects max and which expire after 60 seconds
      String cacheName = Constants.SYSTEM_RSS_FEED_CACHE;
      Ehcache cache = CacheUtils.getCache(cacheName);
      if (cache == null) {
        cache = new Cache(cacheName,
            20,
            CacheUtils.memoryStoreEvictionPolicy, CacheUtils.overflowToDisk,
            CacheUtils.diskStorePath, false, 60,
            CacheUtils.timeToIdle, CacheUtils.diskPersistent,
            CacheUtils.diskExpiryThreadIntervalSeconds, null);
        SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(cache,
            new FeedCacheEntryFactory(context));
        manager.addCache(selfPopulatingCache);
        LOG.info("cache created: " + cache.getName());
      }
    }

    // newsId = news article
    // TODO: Make use of this cache...
    //CacheUtils.createInMemoryCache(Constants.SYSTEM_NEWS_ARTICLE_CACHE, new NewsArticleCacheEntryFactory(context), 10000);

    // wikiId = wiki
    // TODO: Make use of this cache...
    //CacheUtils.createInMemoryCache(Constants.SYSTEM_WIKI_CACHE, new WikiCacheEntryFactory(context), 10000);

    // zipCode = locationBean
    CacheUtils.createInMemoryCache(Constants.SYSTEM_ZIP_CODE_CACHE, new LocationCacheEntryFactory(context), 44000);

    // badgeId = Badge
    CacheUtils.createInMemoryCache(Constants.SYSTEM_BADGE_LIST_CACHE, new BadgeCacheEntryFactory(context), 200);

    // categoryId = ProjectCategory
    CacheUtils.createInMemoryCache(Constants.SYSTEM_PROJECT_CATEGORY_LIST_CACHE, new ProjectCategoryCacheEntryFactory(context), 200);

    // (String) project id - project ticket id = (Integer) ticket id
    CacheUtils.createInMemoryCache(Constants.SYSTEM_PROJECT_TICKET_ID_CACHE, new ProjectTicketIdCacheEntryFactory(context), 200);

    // (String) portal type | page name | [file name] = (DashboardPage) dashboardPage
    CacheUtils.createInMemoryCache(Constants.SYSTEM_DASHBOARD_PAGE_CACHE, new DashboardPageCacheEntryFactory(context), 300);

    // (String) dashboard page | portlet window id = (String) portlet content
    CacheUtils.createInMemoryBlockingCache(Constants.SYSTEM_DASHBOARD_PORTLET_CACHE, 300);

    // Session authentication for Token Session Validation when the application is running...
    if (context.getApplicationPrefs() != null) {
      if ("Token".equals(context.getApplicationPrefs().get("LOGIN.MODE"))) {
        String cacheName = Constants.SESSION_AUTHENTICATION_TOKEN_CACHE;
        Ehcache cache = CacheUtils.getCache(cacheName);
        if (cache == null) {
          // Create a short-lived cache because the tokens are for a single use
          cache = new Cache(cacheName,
              10000,
              CacheUtils.memoryStoreEvictionPolicy, CacheUtils.overflowToDisk,
              CacheUtils.diskStorePath, false, 30,
              CacheUtils.timeToIdle, CacheUtils.diskPersistent,
              CacheUtils.diskExpiryThreadIntervalSeconds, null);
          SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(cache,
              new ValidationTokenEntryFactory(context));
          manager.addCache(selfPopulatingCache);
          LOG.info("cache created: " + cache.getName());
        }
      }
    }
  }
}
