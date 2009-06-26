ALTER TABLE project_calendar_meeting_attendees ADD enteredby INTEGER REFERENCES users(user_id);
ALTER TABLE project_calendar_meeting_attendees ADD modified DATETIME DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE project_calendar_meeting_attendees ADD modifiedby INTEGER REFERENCES users(user_id);
ALTER TABLE project_calendar_meeting_attendees ADD dimdim_status INTEGER;

ALTER TABLE project_calendar_meeting ADD is_dimdim BIT DEFAULT 0;
ALTER TABLE project_calendar_meeting ADD dimdim_url VARCHAR(255);
ALTER TABLE project_calendar_meeting ADD dimdim_meetingid VARCHAR(255);
ALTER TABLE project_calendar_meeting ADD dimdim_username VARCHAR(255);
ALTER TABLE project_calendar_meeting ADD dimdim_password VARCHAR(255);
