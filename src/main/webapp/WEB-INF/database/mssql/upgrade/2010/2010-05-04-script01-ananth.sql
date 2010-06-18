ALTER TABLE projects ADD inappropriate_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects_rating ADD inappropriate BIT DEFAULT 0;
ALTER TABLE projects_rating DROP modifiedby;