ALTER TABLE projects_rating ADD modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE projects_rating ADD modifiedby INTEGER NOT NULL REFERENCES users(user_id);

ALTER TABLE projects ADD profile_enabled BOOLEAN DEFAULT true NOT NULL;
ALTER TABLE projects ADD profile_label VARCHAR(50);
ALTER TABLE projects ADD profile_order INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects ADD profile_description VARCHAR(255);
