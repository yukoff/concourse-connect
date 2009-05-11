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
package com.concursive.connect.web.modules.calendar.dao;

import com.concursive.commons.db.AbstractConnectionPoolTest;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.calendar.dao.MeetingList;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Tests common badge database access
 *
 * @author lorraine bittner
 * @created July 18, 2008
 */
public class MeetingSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  public void testMeetingCRUD() throws SQLException {

    int projectId;
    // Insert a project
    Project project = new Project();
    project.setTitle("MeetingSQLTest "+System.currentTimeMillis());
    project.setShortDescription("description");
    project.setRequestDate(new java.sql.Timestamp(System.currentTimeMillis()));
    project.setEnteredBy(USER_ID);
    project.setModifiedBy(USER_ID);
    project.insert(db);
    projectId = project.getId();

    // Insert meeting
    Meeting meeting = new Meeting();
    meeting.setTitle("Meeting SQL Test");
    meeting.setDescription("Meeting SQL Test Description");
    meeting.setEnteredBy(USER_ID);
    meeting.setModifiedBy(USER_ID);
    meeting.setStartDate(new Timestamp(System.currentTimeMillis()));
    meeting.setEndDate(new Timestamp(System.currentTimeMillis()+3600));
    meeting.setIsTentative(false);
    meeting.setByInvitationOnly(false);
    meeting.setProjectId(projectId);
    meeting.setOwner(USER_ID);
    assertNotNull(meeting);
    boolean result = meeting.insert(db);
    assertTrue("Meeting was not inserted", result);
    assertTrue("Inserted meeting did not have an id", meeting.getId() > -1);

    // Update meeting
    // Try updating without reloading the meeting
    assertNull(meeting.getModified());
    assertTrue("Meeting to update does not have an id", meeting.getId() > -1);
    int updateCount = meeting.update(db);
    assertTrue("The modified field checks for concurrent updates so the modified field must match the load value", updateCount == 0);
    // Reload the meeting, then update
    assertTrue(meeting.getId() > -1);
    meeting = new Meeting(db, meeting.getId());
    meeting.setTitle("Meeting SQL Test Updated Badge");
    updateCount = meeting.update(db);
    assertTrue("The meeting was not updated by the database", updateCount == 1);

    // Find the previously set meeting
    int meetingId = meeting.getId();
    meeting = null;
    MeetingList meetingList = new MeetingList();
    meetingList.setProjectId(projectId);
    meetingList.buildList(db);
    assertTrue(meetingList.size() > 0);
    for(Meeting thisMeeting : meetingList){
      if (thisMeeting.getId() == meetingId) {
        meeting = thisMeeting;
        break;
      }
    }
    assertNotNull(meeting);

    // Delete the meeting
    meeting.delete(db);
    meetingId = meeting.getId();
    meeting = null;

    // Try to find the previously deleted meeting
    meetingList = new MeetingList();
    meetingList.setProjectId(projectId);
    meetingList.buildList(db);
    for(Meeting thisMeeting : meetingList) {
      if (thisMeeting.getId() == meetingId) {
        assertNull("Meeting exists when it shouldn't", thisMeeting);
      }
    }
    
    //cleanup data
    project.delete(db, null);
  }

}