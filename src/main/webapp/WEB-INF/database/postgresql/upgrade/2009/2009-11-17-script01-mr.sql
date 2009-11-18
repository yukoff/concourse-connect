UPDATE user_tag_log SET link_module_id = 2009021823 WHERE link_module_id = 1;

CREATE OR REPLACE VIEW projects_tag AS
SELECT link_item_id AS project_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2009021823
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW projects_tag_log AS
SELECT link_item_id AS project_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2009021823;

CREATE OR REPLACE VIEW unique_projects_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2009021823
GROUP BY tag;
