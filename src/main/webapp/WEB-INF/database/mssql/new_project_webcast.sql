CREATE TABLE project_webcast (
  webcast_id INT IDENTITY PRIMARY KEY,
  project_id INTEGER REFERENCES projects(project_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0      
);
CREATE INDEX project_webcast_pid_idx ON project_webcast(project_id);

CREATE TABLE project_webcast_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  webcast_id INTEGER NOT NULL REFERENCES project_webcast(webcast_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
);
CREATE INDEX project_webcast_rtg_idx ON project_webcast_rating(webcast_id);