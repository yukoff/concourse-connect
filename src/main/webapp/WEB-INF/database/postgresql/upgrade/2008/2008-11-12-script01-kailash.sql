--
-- unique tags for projects
-- link_module_id is value of Constants.PROJECTS_FILES
--
CREATE OR REPLACE VIEW unique_projects_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 1
GROUP BY tag;

--
-- unique tags for badges
-- link_module_id is value of Constants.BADGE_FILES
--
CREATE OR REPLACE VIEW unique_badge_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008051215
GROUP BY tag;

--
-- unique tags for Ads
-- link_module_id is value of Constants.PROJECT_AD_FILES
--
CREATE OR REPLACE VIEW unique_ad_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715
GROUP BY tag;

--
-- unique tags for Classifieds
-- link_module_id is value of Constants.PROJECT_CLASSIFIEDS_FILES
--
CREATE OR REPLACE VIEW unique_project_classified_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071716
GROUP BY tag;

--
-- unique tags for Wiki
-- link_module_id is value of Constants.PROJECT_WIKI_FILES
--
CREATE OR REPLACE VIEW unique_project_wiki_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 20060220
GROUP BY tag;

--
-- unique tags
--
CREATE OR REPLACE VIEW unique_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
GROUP BY tag;


