ALTER TABLE projects ADD messages_enabled BIT DEFAULT 1 NOT NULL;
ALTER TABLE projects ADD messages_label VARCHAR(50);
ALTER TABLE projects ADD messages_order INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects ADD messages_description VARCHAR(255);
