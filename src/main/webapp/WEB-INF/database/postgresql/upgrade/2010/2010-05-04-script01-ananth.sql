ALTER TABLE projects ADD inappropriate_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects_rating ADD inappropriate BOOLEAN DEFAULT FALSE;
ALTER TABLE projects_rating DROP modifiedby;