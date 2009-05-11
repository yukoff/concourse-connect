ALTER TABLE project_files ADD rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_files ADD rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_files ADD rating_avg FLOAT DEFAULT 0 NOT NULL;
ALTER TABLE project_files ADD inappropriate_count INTEGER DEFAULT 0;

DROP TABLE project_files_rating;

CREATE TABLE project_files_rating (
  rating_id IDENTITY PRIMARY KEY,
  item_id INTEGER REFERENCES project_files(item_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
);
CREATE INDEX project_files_rtg_idx on project_files_rating(item_id);


ALTER TABLE project_calendar_meeting ADD rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_calendar_meeting ADD rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_calendar_meeting ADD rating_avg FLOAT DEFAULT 0 NOT NULL;
ALTER TABLE project_calendar_meeting ADD inappropriate_count INTEGER DEFAULT 0;

CREATE TABLE project_calendar_meeting_rating (
  rating_id IDENTITY PRIMARY KEY,
  meeting_id INTEGER REFERENCES project_calendar_meeting(meeting_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
);
CREATE INDEX project_cal_mtg_cmt_rtg_idx on project_calendar_meeting_rating(meeting_id);

