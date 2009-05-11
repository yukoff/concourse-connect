ALTER TABLE project_news_rating ADD inappropriate BIT DEFAULT 0;
ALTER TABLE project_news ADD inappropriate_count INTEGER DEFAULT 0;

ALTER TABLE project_wiki_rating ADD inappropriate BIT DEFAULT 0;
ALTER TABLE project_wiki ADD inappropriate_count INTEGER DEFAULT 0;

ALTER TABLE project_issues ADD rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_issues ADD rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_issues ADD rating_avg FLOAT DEFAULT 0 NOT NULL;
ALTER TABLE project_issues ADD inappropriate_count INTEGER DEFAULT 0;

CREATE TABLE project_issues_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  issue_id INTEGER NOT NULL REFERENCES project_issues(issue_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
);
CREATE INDEX project_issues_rtg_idx on project_issues_rating(issue_id);
