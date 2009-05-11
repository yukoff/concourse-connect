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

package com.concursive.connect.web.modules.common.social.tagging.utils;

import com.concursive.commons.math.MathUtils;
import com.concursive.commons.math.StatisticsBean;
import com.concursive.connect.web.modules.common.social.tagging.dao.Tag;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import com.concursive.connect.web.modules.common.social.tagging.dao.UniqueTag;
import com.concursive.connect.web.modules.common.social.tagging.dao.UniqueTagList;

/**
 * Utilities for working with Tags
 *
 * @author matt rajkowski
 * @created July 23, 2008
 */
public class TagUtils {

  public static StatisticsBean determineTagWeight(TagList tagList) {
    // First determine the overall statistics of the tags
    double[] values = new double[tagList.size()];
    int i = -1;
    for (Tag tag : tagList) {
      values[++i] = tag.getTagCount();
    }
    StatisticsBean stats = MathUtils.getStatistics(values);
    double mean = stats.getMean();
    double stdDev = stats.getStandardDeviation();
    // Set the weight on each tag using a scale
    for (Tag tag : tagList) {
      if (stdDev == 0.0) {
        tag.setWeight(3);
      } else {
        double factor = (tag.getTagCount() - mean) / (stdDev);
        // for a scale of 5 use -1.414, -.707 .707, 1.414
        // for a scale of 7 use -2, -1, -0.5 0.5, 1, 2
        if (factor <= -1.414) {
          tag.setWeight(1);
        } else if (-1.414 < factor && factor <= -0.707) {
          tag.setWeight(2);
        } else if (-0.707 < factor && factor < 0.707) {
          tag.setWeight(3);
        } else if (0.707 <= factor && factor < 1.414) {
          tag.setWeight(4);
        } else if (factor >= 1.414) {
          tag.setWeight(5);
        }
      }
    }
    return stats;
  }

  public static StatisticsBean determineTagWeight(UniqueTagList tagList) {
    // First determine the overall statistics of the tags
    double[] values = new double[tagList.size()];
    int i = -1;
    for (UniqueTag tag : tagList) {
      values[++i] = tag.getTagCount();
    }
    StatisticsBean stats = MathUtils.getStatistics(values);
    double mean = stats.getMean();
    double stdDev = stats.getStandardDeviation();
    // Set the weight on each tag using a scale
    for (UniqueTag tag : tagList) {
      if (stdDev == 0.0) {
        tag.setWeight(3);
      } else {
        double factor = (tag.getTagCount() - mean) / (stdDev);
        // for a scale of 5 use -1.414, -.707 .707, 1.414
        // for a scale of 7 use -2, -1, -0.5 0.5, 1, 2
        if (factor <= -1.414) {
          tag.setWeight(1);
        } else if (-1.414 < factor && factor <= -0.707) {
          tag.setWeight(2);
        } else if (-0.707 < factor && factor < 0.707) {
          tag.setWeight(3);
        } else if (0.707 <= factor && factor < 1.414) {
          tag.setWeight(4);
        } else if (factor >= 1.414) {
          tag.setWeight(5);
        }
      }
    }
    return stats;
  }
}