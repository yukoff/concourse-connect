
ALTER TABLE lookup_project_category ADD parent_category INTEGER;

ALTER TABLE projects ADD subcategory1_id INTEGER REFERENCES lookup_project_category(code);
ALTER TABLE projects ADD subcategory2_id INTEGER REFERENCES lookup_project_category(code);
ALTER TABLE projects ADD subcategory3_id INTEGER REFERENCES lookup_project_category(code);

ALTER TABLE projects_rating ADD comment VARCHAR(255);

ALTER TABLE project_issue_replies ADD rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_issue_replies ADD rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_issue_replies ADD rating_avg FLOAT DEFAULT 0 NOT NULL;

CREATE TABLE project_issue_replies_rating (
  reply_id INTEGER NOT NULL REFERENCES project_issue_replies(reply_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id)
);
CREATE INDEX project_iss_repl_rtg_idx ON project_issue_replies_rating(reply_id);
CREATE INDEX project_iss_repl_rtg_rtg_idx ON project_issue_replies_rating(rating);

CREATE INDEX badgelink_project_idx ON badgelink_project(badge_id);
CREATE INDEX badgelink_project_prj_idx ON badgelink_project(project_id);

ALTER TABLE project_calendar_meeting ADD by_invitation_only BIT DEFAULT 0 NOT NULL;
ALTER TABLE project_calendar_meeting ADD description TEXT;

CREATE TABLE project_calendar_meeting_attendees (
  attendee_id INT IDENTITY PRIMARY KEY,
  meeting_id INTEGER REFERENCES project_calendar_meeting(meeting_id) NOT NULL,
  user_id INTEGER REFERENCES users(user_id) NOT NULL,
  is_tentative BIT DEFAULT 0,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX project_cal_mtg_att_idx ON project_calendar_meeting_attendees(meeting_id);
CREATE INDEX project_cal_mtg_att_att_idx ON project_calendar_meeting_attendees(user_id);
