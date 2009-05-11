ALTER TABLE projects ADD messages_enabled BOOLEAN DEFAULT true NOT NULL;
ALTER TABLE projects ADD messages_label VARCHAR(50);
ALTER TABLE projects ADD messages_order INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects ADD messages_description VARCHAR(255);
