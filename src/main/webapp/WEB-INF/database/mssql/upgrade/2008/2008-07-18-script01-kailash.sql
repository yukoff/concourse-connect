--
-- Tags by User - Drop tables and Create views.
--
DROP TABLE user_tag;

CREATE OR REPLACE VIEW user_tag AS
SELECT user_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
GROUP BY user_id, tag;

--
-- Tags for Projects - Drop tables and Create views.
-- link_module_id is value of Constants.PROJECTS_FILES
--
DROP TABLE projects_tag;
DROP TABLE projects_tag_log;

CREATE OR REPLACE VIEW projects_tag AS
SELECT link_item_id AS project_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 1
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW projects_tag_log AS
SELECT link_item_id AS project_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 1;

--
-- Tags for Badge - Drop tables and Create views.
-- link_module_id is value of Constants.BADGE_FILES
--
DROP TABLE badge_tag;
DROP TABLE badge_tag_log;

CREATE OR REPLACE VIEW badge_tag AS
SELECT link_item_id AS badge_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008051215
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW badge_tag_log AS
SELECT link_item_id AS badge_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008051215;


--
-- Tags for Ads - Drop tables and Create views.
-- link_module_id is value of Constants.PROJECT_AD_FILES
--
DROP TABLE ad_tag;
DROP TABLE ad_tag_log;

CREATE OR REPLACE VIEW ad_tag AS
SELECT link_item_id AS ad_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW ad_tag_log AS
SELECT link_item_id AS ad_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715;

--
-- Tags for Classifieds - Drop tag tables and Create views.
-- link_module_id is value of Constants.PROJECT_CLASSIFIEDS_FILES
--
DROP TABLE project_classified_tag;
DROP TABLE project_classified_tag_log;

CREATE OR REPLACE VIEW project_classified_tag AS
SELECT link_item_id AS classified_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071716
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_classified_tag_log AS
SELECT link_item_id AS classified_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008071716;


--
-- Tags for Wiki - Drop tag tables and Create views.
-- link_module_id is value of Constants.PROJECT_WIKI_FILES
--
DROP TABLE project_wiki_tag;
DROP TABLE project_wiki_tag_log;

CREATE OR REPLACE VIEW project_wiki_tag AS
SELECT link_item_id AS wiki_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 20060220
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_wiki_tag_log AS
SELECT link_item_id AS wiki_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 20060220;



