ALTER TABLE projects ADD twitter_id VARCHAR(255);

CREATE OR REPLACE VIEW ad_tag AS
SELECT link_item_id AS ad_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW ad_tag_log AS
SELECT link_item_id AS ad_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715;

CREATE TABLE process_log (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL,
  long_value INTEGER,
  enabled BOOLEAN DEFAULT true,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  processed DATETIME
);

INSERT INTO process_log (description) VALUES ('TwitterQueryJob');
