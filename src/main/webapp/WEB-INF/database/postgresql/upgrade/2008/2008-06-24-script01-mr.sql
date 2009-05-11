DROP INDEX projects_comment_prj_idx;
DROP TABLE projects_comment;
DROP INDEX projects_rtg_idx;
DROP INDEX projects_rtg_rtg_idx;
DROP TABLE projects_rating;

CREATE TABLE projects_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  title VARCHAR(100),
  comment TEXT,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0 NOT NULL
);
CREATE INDEX projects_rtg_idx ON projects_rating(project_id);
CREATE INDEX projects_rtg_rtg_idx ON projects_rating(rating);

CREATE TABLE projects_rating_rating (
  record_id BIGSERIAL PRIMARY KEY,
  rating_id BIGINT REFERENCES projects_rating(rating_id),
  rating INTEGER NOT NULL,
  inappropriate BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
);
CREATE INDEX projects_rtgrtg_idx ON projects_rating_rating(rating_id);

ALTER TABLE project_issue_replies ADD inappropriate_count INTEGER DEFAULT 0 NOT NULL;

ALTER TABLE project_issue_replies_rating ADD rating_id BIGSERIAL PRIMARY KEY;
ALTER TABLE project_issue_replies_rating ADD inappropriate BOOLEAN DEFAULT false NOT NULL;

DROP INDEX project_files_comment_item_idx;
DROP TABLE project_files_comment;
DROP INDEX project_files_rtg_idx;
DROP INDEX project_files_rtg_rtg_idx;
DROP TABLE project_files_rating;

CREATE TABLE project_files_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  item_id INTEGER NOT NULL REFERENCES project_files(item_id),
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  title VARCHAR(100),
  comment TEXT,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0 NOT NULL
);
CREATE INDEX project_files_rtg_idx ON project_files_rating(item_id);
CREATE INDEX project_files_rtg_rtg_idx ON project_files_rating(rating);

ALTER TABLE project_wiki_rating ADD rating_id BIGSERIAL PRIMARY KEY;
ALTER TABLE project_news_rating ADD rating_id BIGSERIAL PRIMARY KEY;
ALTER TABLE ad_rating ADD rating_id BIGSERIAL PRIMARY KEY;
ALTER TABLE badge_rating ADD rating_id BIGSERIAL PRIMARY KEY;
ALTER TABLE project_classified_rating ADD rating_id BIGSERIAL PRIMARY KEY;
ALTER TABLE task_rating ADD rating_id BIGSERIAL PRIMARY KEY;
