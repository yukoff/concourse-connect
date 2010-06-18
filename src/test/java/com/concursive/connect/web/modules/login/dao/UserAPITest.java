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
package com.concursive.connect.web.modules.login.dao;

import com.concursive.commons.api.AbstractAPITest;
import com.concursive.commons.api.DataRecord;
import com.concursive.commons.codec.PasswordHash;
import com.concursive.connect.web.modules.login.utils.UserUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Tests common project database access
 *
 * @author wli
 * @created January 31, 2008
 */
public class UserAPITest extends AbstractAPITest {

  public void testInsertUser() throws Exception {

    // The minimum number of fields for a successful user insert
    DataRecord record = new DataRecord();
    record.setName("user");
    record.setAction(DataRecord.INSERT);
    record.addField("firstName", "John");
    record.addField("lastName", "Smith");
    record.addField("username", "jsmith@concursive.com");
    record.addField("password", PasswordHash.encrypt("password"));
    record.addField("groupId", GROUP_ID);
    record.addField("departmentId", "1");
    record.addField("enabled", true);
    api.save(record);

    //Add Meta Info with fields required
    ArrayList<String> meta = new ArrayList<String>();
    meta.add("id");
    api.setTransactionMeta(meta);

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

    // When supplying meta, the API will return the record inserted
    int newUserId = Integer.parseInt(api.getResponseValue("id"));
    assertTrue(newUserId > 0);

    //delete the inserted user
    User thisUser = new User(db, newUserId);
    thisUser.getProfileProject().delete(db, null);
    assertTrue(thisUser.delete(db) == 1);
  }


  public void testInsertAndSelectUser() throws Exception {
    String username = "jsmith@concursive.com";
    {
      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.INSERT);
      record.setShareKey(true);
      record.addField("firstName", "John");
      record.addField("lastName", "Smith");
      record.addField("username", username);
      record.addField("password", PasswordHash.encrypt("password"));
      record.addField("groupId", GROUP_ID);
      record.addField("departmentId", "1");
      record.addField("enabled", true);
      api.save(record);
    }
    {
      // build user list in the same transaction
      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", "$C{user.id}");
      record.addField("username", username);
      api.save(record);

      // When a SELECT action is used, metadata must be supplied for returned records
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("password");
      api.setTransactionMeta(meta);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue("Couldn't find inserted user", api.getRecordCount() == 1);

    User user = null;
    ArrayList<Object> userListObjects = api.getRecords("com.concursive.connect.web.modules.login.dao.User");
    assertTrue(userListObjects.size() == 1);
    int count = 0;
    for (Object userObject : userListObjects) {
      user = (User) userObject;
      User thisUser = new User(db, user.getId());
      assertTrue("Password isn't encrypted", !thisUser.getPassword().equals("password"));
      thisUser.getProfileProject().delete(db, null);
      assertTrue("Password isn't encrypted", thisUser.getPassword().equals(PasswordHash.encrypt("password")));
      assertTrue(thisUser.delete(db) == 1);
      count++;
    }
    assertTrue("Delete inserted user error", count == 1);
  }


  public void testInsertAndUpdateUser() throws Exception {
    String username = "jsmith@concursive.com";
    {
      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.INSERT);
      record.setShareKey(true);
      record.addField("firstName", "John");
      record.addField("lastName", "Smith");
      record.addField("username", username);
      record.addField("password", PasswordHash.encrypt("password"));
      record.addField("groupId", GROUP_ID);
      record.addField("departmentId", "1");
      record.addField("enabled", true);
      api.save(record);
    }

    {
      //build user list
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("firstName");
      meta.add("lastName");
      meta.add("username");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", "$C{user.id}");
      record.addField("username", username);
      api.save(record);
    }

    // Process the complete transaction
    processTheTransactions(api, packetContext);
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue("Couldn't find inserted user", api.getRecordCount() == 1);

    User user = null;
    ArrayList<Object> userListObjects = api.getRecords("com.concursive.connect.web.modules.login.dao.User");
    assertTrue(userListObjects.size() > 0);

    int count = 0;
    for (Object userObject : userListObjects) {
      user = (User) userObject;
      if (user.getUsername().equals(username)) {

        {
          DataRecord record = new DataRecord();
          record.setName("user");
          record.setAction(DataRecord.UPDATE);
          record.addField("id", user.getId());
          record.addField("firstName", "James");
          //record.addField("lastName", "Smith");
          record.addField("username", username);
          record.addField("password", PasswordHash.encrypt("password2"));
          //record.addField("groupId", GROUP_ID);
          //record.addField("departmentId", "1");
          api.save(record);

          // Process the complete transaction
          processTheTransactions(api, packetContext);
          assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
        }

        {

          //Add Meta Info with fields required
          ArrayList<String> meta = new ArrayList<String>();
          meta.add("id");
          meta.add("firstName");
          meta.add("lastName");
          meta.add("password");
          api.setTransactionMeta(meta);

          // Reload and verify the last name didn't get updated when saved
          // just because it wasn't specified in the update
          DataRecord record = new DataRecord();
          record.setName("userList");
          record.setAction(DataRecord.SELECT);
          record.addField("userId", user.getId());
          api.save(record);

          // Process the complete transaction
          processTheTransactions(api, packetContext);
          assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

          User user2 = null;
          ArrayList<Object> userListObjects2 = api.getRecords("com.concursive.connect.web.modules.login.dao.User");
          assertTrue(userListObjects2.size() == 1);
          for (Object userObject2 : userListObjects2) {
            user2 = (User) userObject2;
            assertTrue("James".equals(user2.getFirstName()));
            assertTrue("Smith".equals(user2.getLastName()));
            assertTrue("Password does not match", PasswordHash.encrypt("password2").equals(user2.getPassword()));
          }
        }

        User thisUser = new User(db, user.getId());
        thisUser.getProfileProject().delete(db, null);
        assertTrue("Delete inserted user error", 1 == thisUser.delete(db));
        count++;
      }
    }
    assertTrue("Delete inserted user error", count >= 1);
  }


  public void testInsertAndDisableUser() throws Exception {

    String username = "user2disable@concursive.com";
    {
      /* Insert the user */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.INSERT);
      record.addField("firstName", "John");
      record.addField("lastName", "Smith");
      record.addField("username", username);
      record.addField("password", PasswordHash.encrypt("password"));
      record.addField("groupId", GROUP_ID);
      record.addField("departmentId", 1);
      record.addField("enabled", true);
      api.save(record);

      processTheTransactions(api, packetContext);
    }
    int userId = api.getResponseValueAsInt("id");
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue("UserId didn't get set in insert", userId > -1);

    {
      /* Verify the user is enabled */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("enabled");
      api.setTransactionMeta(meta);

      // Reload and verify the last name didn't get updated when saved
      // just because it wasn't specified in the update
      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", userId);
      api.save(record);

      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
      assertTrue("Record didn't get set to enabled", api.getResponseValueAsBoolean("enabled"));
    }

    {
      /* Verify the user can be found when querying validUsers */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("enabled");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", userId);
      record.addField("validUser", "true");
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("Enabled user was incorrectly found", api.getRecordCount() == 1);
    }

    {
      /* Disable the user */
      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.UPDATE);
      record.addField("id", userId);
      record.addField("enabled", false);
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("An updated record must get an action of 'processed' returned", api.getRecords().get(0).getAction().equals("processed"));
      assertTrue("The record name was not set correctly: " + api.getRecords().get(0).getName(), api.getRecords().get(0).getName().equals("user"));
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }


    {
      /* Verify the user got disabled */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("enabled");
      api.setTransactionMeta(meta);

      // Reload and verify the last name didn't get updated when saved
      // just because it wasn't specified in the update
      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", userId);
      api.save(record);

      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
      assertFalse("Record didn't get set to disabled", api.getResponseValueAsBoolean("enabled"));
    }

    {
      /* Verify the user cannot be found when querying validUsers */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("enabled");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", userId);
      record.addField("validUser", "true");
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("Enabled user was incorrectly found", api.getRecordCount() == 0);
    }


    {
      /* Verify the user can be found when querying invalidUsers */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("enabled");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", userId);
      record.addField("validUser", "false");
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("Disabled user was not found", api.getRecordCount() == 1);
    }

    {
      User thisUser = new User(db, userId);
      assertTrue("Should have found the user", thisUser.getId() == userId);

      // Delete the user's profile, then delete the user
      thisUser.getProfileProject().delete(db, null);

      // Construct a delete request
      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.DELETE);
      record.addField("id", userId);
      api.save(record);

      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    User thisUser = new User(db, userId);
    assertTrue("Shouldn't have found the deleted user", thisUser.getId() == -1);
  }

  public void testInsertAndExpireUser() throws Exception {

    String username = "user2disable@concursive.com";
    {
      /* Insert the user */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.INSERT);
      record.addField("firstName", "John");
      record.addField("lastName", "Smith");
      record.addField("username", username);
      record.addField("password", PasswordHash.encrypt("password"));
      record.addField("groupId", GROUP_ID);
      record.addField("departmentId", "1");
      record.addField("enabled", true);
      api.save(record);

      processTheTransactions(api, packetContext);
    }
    int userId = api.getResponseValueAsInt("id");
    assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    assertTrue("UserId didn't get set in insert", userId > -1);


    {
      /* Disable the user */
      // Use yesterday as the expiration date
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.DATE, -1);
      // Update the record
      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.UPDATE);
      record.addField("id", userId);
      record.addField("expiration", cal.getTime());
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("An updated record must get an action of 'processed' returned", api.getRecords().get(0).getAction().equals("processed"));
      assertTrue("The record name was not set correctly: " + api.getRecords().get(0).getName(), api.getRecords().get(0).getName().equals("user"));
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

      User thisUser = new User(db, userId);
      assertTrue("API didn't set the expiration date", thisUser.getExpiration() != null);
    }

    {
      /* Verify the user cannot be found when querying validUsers */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("expiration");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", userId);
      record.addField("validUser", "true");
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("Enabled user was incorrectly found", api.getRecordCount() == 0);
    }


    int userProfileProjectId = -1;
    {
      /* Verify the user can be found when querying invalidUsers */
      ArrayList<String> meta = new ArrayList<String>();
      meta.add("id");
      meta.add("expiration");
      meta.add("profileProjectId");
      api.setTransactionMeta(meta);

      DataRecord record = new DataRecord();
      record.setName("userList");
      record.setAction(DataRecord.SELECT);
      record.addField("userId", userId);
      record.addField("validUser", "false");
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("Disabled user was not found", api.getRecordCount() == 1);

      User thisUser = new User(db, api.getResponseValueAsInt("id"));
      assertTrue(UserUtils.isUserDisabled(thisUser));

      userProfileProjectId = api.getResponseValueAsInt("profileProjectId");
      assertTrue("profileProjectId was not retrieved", userProfileProjectId > -1);
    }

    {
      /* Re-enable the user */
      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.UPDATE);
      record.addField("id", userId);
      record.addField("expiration", DataRecord.NULL);
      api.save(record);

      processTheTransactions(api, packetContext);
      assertTrue("An updated record must get an action of 'processed' returned", api.getRecords().get(0).getAction().equals("processed"));
      assertTrue("The record name was not set correctly: " + api.getRecords().get(0).getName(), api.getRecords().get(0).getName().equals("user"));
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());

      User thisUser = new User(db, userId);
      assertTrue("API didn't set the value to null", thisUser.getExpiration() == null);

    }

    {
      // Delete the user's profile
      DataRecord record = new DataRecord();
      record.setName("project");
      record.setAction(DataRecord.DELETE);
      record.addField("id", userProfileProjectId);
      api.save(record);

      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    {
      // Delete the user
      DataRecord record = new DataRecord();
      record.setName("user");
      record.setAction(DataRecord.DELETE);
      record.addField("id", userId);
      api.save(record);

      processTheTransactions(api, packetContext);
      assertFalse("API reported a transaction error: " + api.getLastResponse(), api.hasError());
    }

    User thisUser = new User(db, userId);
    assertTrue("Shouldn't have found the deleted user", thisUser.getId() == -1);
  }

}
