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
package com.concursive.connect.web.modules.badges.utils;

import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.badges.dao.*;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lorraine Bittner
 * @version $Id$
 * @created Jun 5, 2008
 */
public class BadgeUtils {

  /**
   * This method reads through all the badges and map them to the proper category.
   * If a badge's category does not appear in the badgeCategoryList then it is
   * skipped.
   *
   * @param badgeList         list of all the badges to map
   * @param badgeCategoryList list of all the categories to map badges to
   * @return a mapping of all badges to their categories
   */
  public static Map<BadgeCategory, BadgeList> createBadgeByCategoryMap(BadgeList badgeList, BadgeCategoryList badgeCategoryList) {
    Map<BadgeCategory, BadgeList> badgeByCategoryMap = new LinkedHashMap<BadgeCategory, BadgeList>();
    Map<Integer, BadgeCategory> categoryByIdMap = new LinkedHashMap<Integer, BadgeCategory>();
    for (BadgeCategory category : badgeCategoryList) {
      categoryByIdMap.put(category.getId(), category);
    }
    for (Badge badge : badgeList) {
      BadgeCategory category = categoryByIdMap.get(badge.getCategoryId());
      if (category == null) {
        continue;
      }
      BadgeList list = badgeByCategoryMap.get(category);
      if (list == null) {
        list = new BadgeList();
      }
      list.add(badge);
      badgeByCategoryMap.put(category, list);
    }
    return badgeByCategoryMap;
  }

  public static Map<Integer, ProjectBadge> createBadgeIdToProjectBadgeMap(List<ProjectBadge> projectBadgeList) {
    Map<Integer, ProjectBadge> map = new LinkedHashMap<Integer, ProjectBadge>();
    for (ProjectBadge projectBadge : projectBadgeList) {
      map.put(projectBadge.getBadgeId(), projectBadge);
    }
    return map;
  }

  public static Badge loadBadge(int badgeId) {
    Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_BADGE_LIST_CACHE);
    Element element = cache.get(badgeId);
    return (Badge) element.getObjectValue();
  }
}
