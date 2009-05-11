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

import com.concursive.commons.math.StatisticsBean;
import com.concursive.connect.web.modules.common.social.tagging.utils.TagUtils;
import com.concursive.connect.web.modules.common.social.tagging.dao.Tag;
import com.concursive.connect.web.modules.common.social.tagging.dao.TagList;
import junit.framework.TestCase;

/**
 * Test the tag clould
 *
 * @author matt rajkowski
 * @created July 22, 2008
 */
public class TagCloudTest extends TestCase {

  public void testStandardDeviation() throws Exception {
    // sample 1
    double[] tagCounts1 = {0, 0, 14, 14};
    StatisticsBean stats1 = new StatisticsBean(tagCounts1);
    assertTrue("Standard deviation incorrect: " + stats1.getStandardDeviation(), stats1.getStandardDeviation() == 7.0);
    // sample 2
    double[] tagCounts2 = {3, 7, 7, 19};
    StatisticsBean stats2 = new StatisticsBean(tagCounts2);
    assertTrue("Standard deviation incorrect: " + stats2.getStandardDeviation(), stats2.getStandardDeviation() == 6.0 || stats2.getStandardDeviation() == 5.999999999999999);
    // sample 3
    double[] tagCounts3 = {10, 10, 10, 10, 10};
    StatisticsBean stats3 = new StatisticsBean(tagCounts3);
    assertTrue("Standard deviation incorrect: " + stats3.getStandardDeviation(), stats3.getStandardDeviation() == 0.0);
  }

  public void testTagWeight() throws Exception {
    TagList tagList = new TagList();
    // Add some tags...
    int[] tagCounts = {10, 20, 30, 40, 50};
    for (int tagCount : tagCounts) {
      Tag tag = new Tag();
      tag.setTag("tag " + tagCount);
      tag.setTagCount(tagCount);
      tagList.add(tag);
    }
    // Calculate the weight
    TagUtils.determineTagWeight(tagList);
    // Verify the distribution
    assertTrue("Tag distribution changed: " + tagList.get(0).getWeight(), tagList.get(0).getWeight() == 1);
    assertTrue("Tag distribution changed: " + tagList.get(1).getWeight(), tagList.get(1).getWeight() == 2);
    assertTrue("Tag distribution changed: " + tagList.get(2).getWeight(), tagList.get(2).getWeight() == 3);
    assertTrue("Tag distribution changed: " + tagList.get(3).getWeight(), tagList.get(3).getWeight() == 4);
    assertTrue("Tag distribution changed: " + tagList.get(4).getWeight(), tagList.get(4).getWeight() == 5);
  }

  public void testTagWeightNormal() throws Exception {
    TagList tagList = new TagList();
    int[] tagCounts = {10, 10, 10, 10, 10};
    for (int tagCount : tagCounts) {
      Tag tag = new Tag();
      tag.setTag("tag " + tagCount);
      tag.setTagCount(tagCount);
      tagList.add(tag);
    }
    TagUtils.determineTagWeight(tagList);
    for (Tag tag : tagList) {
      assertEquals(tag.getWeight(), 3);
    }
  }

  public void testTagWeightSmallSet() throws Exception {
    TagList tagList = new TagList();
    int[] tagCounts = {1, 2, 3, 2, 1};
    for (int tagCount : tagCounts) {
      Tag tag = new Tag();
      tag.setTag("tag " + tagCount);
      tag.setTagCount(tagCount);
      tagList.add(tag);
    }
    // Calculate the weight
    TagUtils.determineTagWeight(tagList);
    // Verify the distribution
    assertTrue("Tag distribution changed: " + tagList.get(0).getWeight(), tagList.get(0).getWeight() == 2);
    assertTrue("Tag distribution changed: " + tagList.get(1).getWeight(), tagList.get(1).getWeight() == 3);
    assertTrue("Tag distribution changed: " + tagList.get(2).getWeight(), tagList.get(2).getWeight() == 5);
    assertTrue("Tag distribution changed: " + tagList.get(3).getWeight(), tagList.get(3).getWeight() == 3);
    assertTrue("Tag distribution changed: " + tagList.get(4).getWeight(), tagList.get(4).getWeight() == 2);
  }


  public void testTagWeightSpreadOut() throws Exception {
    TagList tagList = new TagList();
    int[] tagCounts = {100, 80, 79, 60, 1};
    for (int tagCount : tagCounts) {
      Tag tag = new Tag();
      tag.setTag("tag " + tagCount);
      tag.setTagCount(tagCount);
      tagList.add(tag);
    }
    // Calculate the weight
    TagUtils.determineTagWeight(tagList);
    // Verify the distribution
    assertTrue("Tag distribution changed: " + tagList.get(0).getWeight(), tagList.get(0).getWeight() == 4);
    assertTrue("Tag distribution changed: " + tagList.get(1).getWeight(), tagList.get(1).getWeight() == 3);
    assertTrue("Tag distribution changed: " + tagList.get(2).getWeight(), tagList.get(2).getWeight() == 3);
    assertTrue("Tag distribution changed: " + tagList.get(3).getWeight(), tagList.get(3).getWeight() == 3);
    assertTrue("Tag distribution changed: " + tagList.get(4).getWeight(), tagList.get(4).getWeight() == 1);
  }

  public void testTagWeightBigSetMedium() throws Exception {
    // NOTE: Since there are more lower numbers, the expectation is that those will look normal
    TagList tagList = new TagList();
    int[] tagCounts = {3, 7, 7, 19, 1, 1, 22, 50, 105, 30, 52, 88, 22, 77, 200, 1, 1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6, 7, 12, 8, 3, 3, 2, 2};
    for (int tagCount : tagCounts) {
      Tag tag = new Tag();
      tag.setTag("tag " + tagCount);
      tag.setTagCount(tagCount);
      tagList.add(tag);
    }
    TagUtils.determineTagWeight(tagList);
    int[] tagWeights = {0, 0, 0, 0, 0, 0};
    for (Tag tag : tagList) {
      // Count the weights
      tagWeights[tag.getWeight()] += 1;
    }
    assertEquals(tagWeights[0], 0);
    assertEquals(tagWeights[1], 0);
    assertEquals(tagWeights[2], 0);
    assertEquals(tagWeights[3], 29);
    assertEquals(tagWeights[4], 2);
    assertEquals(tagWeights[5], 3);
  }


  public void testTagWeightBigSetLowAndHigh() throws Exception {
    TagList tagList = new TagList();
    int[] tagCounts = {1, 1, 2, 2, 3, 3, 25, 50, 44, 47, 52, 60, 55};
    for (int tagCount : tagCounts) {
      Tag tag = new Tag();
      tag.setTag("tag " + tagCount);
      tag.setTagCount(tagCount);
      tagList.add(tag);
    }
    TagUtils.determineTagWeight(tagList);
    int[] tagWeights = {0, 0, 0, 0, 0, 0};
    for (Tag tag : tagList) {
      // Count the weights
      tagWeights[tag.getWeight()] += 1;
    }
    assertEquals(tagWeights[0], 0);
    assertEquals(tagWeights[1], 0);
    assertEquals(tagWeights[2], 6);
    assertEquals(tagWeights[3], 1);
    assertEquals(tagWeights[4], 6);
    assertEquals(tagWeights[5], 0);
  }


}