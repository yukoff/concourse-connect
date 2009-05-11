ALTER TABLE project_news_rating ADD COLUMN inappropriate BOOLEAN DEFAULT FALSE;
ALTER TABLE project_news ADD COLUMN inappropriate_count INTEGER DEFAULT 0;

ALTER TABLE project_wiki_rating ADD COLUMN inappropriate BOOLEAN DEFAULT FALSE;
ALTER TABLE project_wiki ADD COLUMN inappropriate_count INTEGER DEFAULT 0;

ALTER TABLE project_issues ADD COLUMN rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_issues ADD COLUMN rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_issues ADD COLUMN rating_avg FLOAT DEFAULT 0 NOT NULL;
ALTER TABLE project_issues ADD COLUMN inappropriate_count INTEGER DEFAULT 0;

CREATE TABLE project_issues_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  issue_id BIGINT REFERENCES project_issues(issue_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT false
);
CREATE INDEX project_issues_rtg_idx on project_issues_rating(issue_id);
