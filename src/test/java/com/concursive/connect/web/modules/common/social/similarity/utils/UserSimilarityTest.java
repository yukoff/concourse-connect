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
package com.concursive.connect.web.modules.common.social.similarity.utils;

import com.concursive.commons.math.MathUtils;
import junit.framework.TestCase;

/**
 * Compares the similarity between users
 *
 * @author matt rajkowski
 * @created April 30, 2008
 */
public class UserSimilarityTest extends TestCase {

  public void testUserSimilary() throws Exception {

    // The following users have rated similar items
    double[] user1 = new double[5];
    user1[0] = 5;
    user1[1] = 4;
    user1[2] = 3;
    user1[3] = 4;
    user1[4] = 2;

    double[] user2 = new double[5];
    user2[0] = 4;
    user2[1] = 5;
    user2[2] = 4;
    user2[3] = 3;
    user2[4] = 1;

    double[] user3 = new double[5];
    user3[0] = 3;
    user3[1] = 2;
    user3[2] = 1;
    user3[3] = 1;
    user3[4] = 0;

    double[] user4 = new double[5];
    user4[0] = 5;
    user4[1] = 4;
    user4[2] = 3;
    user4[3] = 4;
    user4[4] = 2;

    // Based on ratings, describe who is most similar
    double score12 = MathUtils.getPearsonCorrelation(user1, user2);
    double score13 = MathUtils.getPearsonCorrelation(user1, user3);
    double score14 = MathUtils.getPearsonCorrelation(user1, user4);

    //score12: 0.6939779183594884
    //score13: 0.9230769230769232
    //score14: 1.0
    //User 1 is similar to user 4

    System.out.println("score12: " + score12);
    System.out.println("score13: " + score13);
    System.out.println("score14: " + score14);

    if (score12 > score13 && score12 > score14) {
      System.out.println("User 1 is similar to user 2");
    }
    if (score13 > score12 && score13 > score14) {
      System.out.println("User 1 is similar to user 3");
    }
    if (score14 > score12 && score14 > score13) {
      System.out.println("User 1 is similar to user 4");
    }
  }

  public void testUserSimilary2() throws Exception {



    // The following users have rated similar items

    //'Lisa Rose': {'Lady in the Water': 2.5, 'Snakes on a Plane': 3.5,
    //'Just My Luck': 3.0, 'Superman Returns': 3.5, 'You, Me and Dupree': 2.5,
    //'The Night Listener': 3.0}

    // Lisa Rose
    double[] user1 = new double[6];
    user1[0] = 2.5;
    user1[1] = 3.5;
    user1[2] = 3.0;
    user1[3] = 3.5;
    user1[4] = 2.5;
    user1[5] = 3.0;

    //Toby': {'Snakes on a Plane':4.5,'You, Me and Dupree':1.0,'Superman Returns':4.0}

    double[] user2 = new double[6];
    user2[0] = 0.0;
    user2[1] = 4.5;
    user2[2] = 0.0;
    user2[3] = 4.0;
    user2[4] = 1.0;
    user2[5] = 0.0;

    //'Gene Seymour': {'Lady in the Water': 3.0, 'Snakes on a Plane': 3.5,
    //'Just My Luck': 1.5, 'Superman Returns': 5.0, 'The Night Listener': 3.0,
    //'You, Me and Dupree': 3.5}

    double[] user3 = new double[6];
    user3[0] = 3.0;
    user3[1] = 3.5;
    user3[2] = 1.5;
    user3[3] = 5.0;
    user3[4] = 3.5;
    user3[5] = 3.0;

    // Based on ratings, describe who is most similar (or consistently similar)
    double score12 = MathUtils.getPearsonCorrelation(user1, user2);
    double score13 = MathUtils.getPearsonCorrelation(user1, user3);

    //score12: 0.7957438376509582
    //score13: 0.396059017191

    System.out.println("score12: " + score12);
    System.out.println("score13: " + score13);

    if (score12 > score13) {
      System.out.println("User 1 is similar to user 2");
    }
    if (score13 > score12) {
      System.out.println("User 1 is similar to user 3");
    }
  }
}