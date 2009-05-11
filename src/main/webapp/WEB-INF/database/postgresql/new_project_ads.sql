/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created    may 7, 2008
 */

CREATE TABLE ad_category (
  code SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id BIGINT REFERENCES project_files(item_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);
CREATE INDEX ad_cat_prj_cat_idx ON ad_category(project_category_id);

CREATE TABLE ad (
  ad_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  ad_category_id INTEGER REFERENCES ad_category(code),
  project_id BIGINT REFERENCES projects(project_id),
  heading VARCHAR(255) NOT NULL,
  content TEXT,
  web_page VARCHAR(255),
  publish_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  expiration_date TIMESTAMP(3),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  enabled BOOLEAN DEFAULT true,
  read_count INTEGER DEFAULT 0 NOT NULL,
  read_date TIMESTAMP(3),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  brief_description_1 VARCHAR(35),
  brief_description_2 VARCHAR(35),
  destination_url VARCHAR(1024)
);
CREATE INDEX ad_ad_cat_idx ON ad(ad_category_id);
CREATE INDEX ad_prj_cat_idx ON ad(project_category_id);

CREATE TABLE ad_view (
  ad_id BIGINT REFERENCES ad(ad_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX ad_vw_idx ON ad_view(ad_id);

CREATE TABLE ad_comment (
  comment_id BIGSERIAL PRIMARY KEY,
  ad_id BIGINT REFERENCES ad(ad_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby BIGINT REFERENCES users(user_id)
);
CREATE INDEX ad_cmt_idx ON ad_comment(ad_id);

CREATE TABLE ad_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  ad_id BIGINT REFERENCES ad(ad_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id)
);
CREATE INDEX ad_rtg_idx ON ad_rating(ad_id);

CREATE OR REPLACE VIEW ad_tag AS
SELECT link_item_id AS ad_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW ad_tag_log AS
SELECT link_item_id AS ad_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715;

CREATE OR REPLACE VIEW unique_ad_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071715
GROUP BY tag;

