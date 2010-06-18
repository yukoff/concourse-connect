ALTER TABLE lookup_project_category ADD label VARCHAR(80);
UPDATE lookup_project_category SET label = description;
ALTER TABLE lookup_project_category ALTER COLUMN label SET NOT NULL;
