ALTER TABLE project_issues ADD read_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_issues_categories ADD read_count INTEGER DEFAULT 0 NOT NULL;
