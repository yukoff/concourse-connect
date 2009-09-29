CREATE OR REPLACE VIEW project_news_tag AS
SELECT link_item_id AS news_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008080809
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_news_tag_log AS
SELECT link_item_id AS news_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008080809;
