CREATE OR REPLACE VIEW project_files_tag AS
SELECT link_item_id AS item_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 1
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_files_tag_log AS
SELECT link_item_id AS item_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 1;

CREATE OR REPLACE VIEW project_issues_tag AS
SELECT link_item_id AS issue_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2005020616
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_issues_tag_log AS
SELECT link_item_id AS issue_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2005020616;

CREATE OR REPLACE VIEW project_calendar_meeting_tag AS
SELECT link_item_id AS meeting_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2009021619
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_calendar_meeting_tag_log AS
SELECT link_item_id AS meeting_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2009021619;

CREATE OR REPLACE VIEW project_issue_replies_tag AS
SELECT link_item_id AS reply_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 20050201
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_issue_replies_tag_log AS
SELECT link_item_id AS reply_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 20050201;