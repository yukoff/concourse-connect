ALTER TABLE projects ADD facebook_page VARCHAR(255);
ALTER TABLE projects ADD youtube_channel_id VARCHAR(255);

-- 3rd party live video Ids
ALTER TABLE projects ADD ustream_id VARCHAR(255);
ALTER TABLE projects ADD livestream_id VARCHAR(255);
ALTER TABLE projects ADD justintv_id VARCHAR(255);
ALTER TABLE projects ADD qik_id VARCHAR(255);

ALTER TABLE projects ADD webcasts_enabled BOOLEAN DEFAULT true NOT NULL;
ALTER TABLE projects ADD webcasts_label VARCHAR(50);
ALTER TABLE projects ADD webcasts_order INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE projects ADD webcasts_description VARCHAR(255);

UPDATE projects set webcasts_order = 17;
UPDATE projects set messages_order = 18;
