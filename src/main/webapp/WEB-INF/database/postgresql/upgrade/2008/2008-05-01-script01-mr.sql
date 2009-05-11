ALTER TABLE projects ADD rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects ADD rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects ADD rating_avg FLOAT DEFAULT 0 NOT NULL;
  
CREATE TABLE projects_rating (
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id)
);
CREATE INDEX projects_rtg_idx on projects_rating(project_id);
CREATE INDEX projects_rtg_rtg_idx on projects_rating(rating);

CREATE TABLE projects_tag (
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  tag VARCHAR(255) NOT NULL,
  tag_count INT DEFAULT 0,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX projects_tag_idx ON projects_tag(project_id);
CREATE INDEX projects_tag_count_idx ON projects_tag(tag_count);

CREATE TABLE projects_tag_log (
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  tag VARCHAR(255) NOT NULL,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX projects_tag_log_idx ON projects_tag_log(project_id);
CREATE INDEX projects_tag_log_usr_idx ON projects_tag_log(user_id);
