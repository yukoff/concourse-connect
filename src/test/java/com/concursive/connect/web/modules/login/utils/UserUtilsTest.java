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
package com.concursive.connect.web.modules.login.utils;

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.login.dao.UserList;
import com.concursive.connect.web.modules.login.utils.UserUtils;

import java.sql.Timestamp;

/**
 * Tests methods in UserUtils
 *
 * @author matt rajkowski
 * @created July 30, 2008
 */
public class UserUtilsTest extends AbstractConnectionPoolTest {

  protected static final int GROUP_ID = 1;
  protected static final int DEPARTMENT_ID = 1;

  public void testGenerateGuid() throws Exception {
    // Setup a test user
    User user = new User();
    user.setGroupId(GROUP_ID);
    user.setDepartmentId(DEPARTMENT_ID);
    user.setFirstName("Test First");
    user.setLastName("Test Last");
    user.setEmail(System.currentTimeMillis() + "@concursive.com");
    user.setUsername(System.currentTimeMillis() + "@concursive.com");
    user.setPassword("e358bf645a205cf15efa983b5517d945");
    user.setCountry("UNITED STATES");
    user.setPostalCode("23456");
    Timestamp entered = new Timestamp(System.currentTimeMillis());
    entered.setNanos(23456);
    user.setEntered(entered);
    user.insert(db);

    // Reset the fields from the database
    user = new User(db, user.getId());

    // Generate a guid
    String guid = UserUtils.generateGuid(user);
    // Test the output
    assertEquals("UserId mismatch", String.valueOf(user.getId()), String.valueOf(UserUtils.getUserIdFromGuid(guid)));
    assertEquals("Entered mismatch", String.valueOf(user.getEntered().getTime()), String.valueOf(UserUtils.getEnteredTimestampFromGuid(guid).getTime()));
    assertEquals("PW Substring mismatch", user.getPassword().substring(2, 15), UserUtils.getPasswordSubStringFromGuid(guid));
    // Test UserList query
    UserList userList = new UserList();
    userList.setGuid(guid);
    userList.buildList(db);
    assertTrue("User not found by guid: " + user.getId()+ " (" + userList.size() + ")", userList.size() == 1);
    // Test UserUtils
    User retrievedUser = UserUtils.loadUserFromGuid(db, guid);
    assertNotNull("UserUtils did not find a user", retrievedUser);
    // Delete the test user
    user.delete(db);
  }

}