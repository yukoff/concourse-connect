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
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendee;
import com.concursive.connect.web.modules.calendar.dao.MeetingAttendeeList;
import com.concursive.connect.web.modules.profile.dao.Project;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Tests common badge database access
 *
 * @author lorraine bittner
 * @created July 3, 2008
 */
public class MeetingAttendeeSQLTest extends AbstractConnectionPoolTest {

  protected static final int USER_ID = 1;

  public void testMeetingAttendeeCRUD() throws SQLException {

    int projectId;
    int meetingId;
    // Insert a project
    Project project = new Project();
    project.setTitle("MeetingAttendeeSQLTest "+System.currentTimeMillis());
    project.setShortDescription("description");
    project.setRequestDate(new Timestamp(System.currentTimeMillis()));
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
    meeting.insert(db);
    meetingId = meeting.getId();


    // Insert meeting attendee
    MeetingAttendee attendee = new MeetingAttendee();
    attendee.setMeetingId(meeting.getId());
    attendee.setUserId(USER_ID);
    attendee.setIsTentative(true);
    attendee.insert(db);

    // Find the previously set meeting attendee
    int attendeeId = attendee.getId();
    attendee = null;
    MeetingAttendeeList attendeeList = new MeetingAttendeeList();
    attendeeList.setMeetingId(meeting.getId());
    attendeeList.setUserId(USER_ID);
    attendeeList.buildList(db);
    assertTrue(attendeeList.size() > 0);
    for(MeetingAttendee thisAttendee : attendeeList){
      if (thisAttendee.getId() == attendeeId) {
        attendee = thisAttendee;
        break;
      }
    }
    assertNotNull(attendee);

    // Delete the attendee
    attendee.delete(db);
    attendee = null;

    // Try to find the previously deleted attendee
    attendeeList = new MeetingAttendeeList();
    attendeeList.setMeetingId(meeting.getId());
    attendeeList.setUserId(USER_ID);
    attendeeList.buildList(db);
    for(MeetingAttendee thisAttendee : attendeeList) {
      if (thisAttendee.getId() == attendeeId) {
        assertNull("Meeting Attendee exists when it shouldn't", thisAttendee);
      }
    }
    //clean up the project and meeting created
    String basePath = null;
    meeting.delete(db);
    assertTrue("Project was not deleted", project.delete(db, basePath));
  }

}