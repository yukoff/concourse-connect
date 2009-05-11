/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created    may 7, 2008
 */

CREATE TABLE badge_category (
  code SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id BIGINT REFERENCES project_files(item_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);
CREATE INDEX badge_cat_prj_cat_idx ON badge_category(project_category_id);

CREATE TABLE badge (
  badge_id BIGSERIAL PRIMARY KEY,
  badge_category_id INTEGER REFERENCES badge_category(code),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  logo_id BIGINT REFERENCES project_files(item_id),
  email1 VARCHAR(255),
  email2 VARCHAR(255),
  email3 VARCHAR(255),
  business_phone VARCHAR(30),
  business_phone_ext VARCHAR(30),
  web_page VARCHAR(255),
  addrline1 VARCHAR(255),
  addrline2 VARCHAR(255),
  addrline3 VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  postalcode VARCHAR(255),
  latitude FLOAT DEFAULT 0,
  longitude FLOAT DEFAULT 0,
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
  system_assigned BOOLEAN DEFAULT false,
  system_constant INTEGER
);
CREATE INDEX badge_bad_cat_idx ON badge(badge_category_id);

CREATE TABLE badge_view (
  badge_id BIGINT REFERENCES badge(badge_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX badge_vw_idx ON badge_view(badge_id);

CREATE TABLE badge_comment (
  comment_id BIGSERIAL PRIMARY KEY,
  badge_id BIGINT REFERENCES badge(badge_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby BIGINT REFERENCES users(user_id)
);
CREATE INDEX badge_cmt_idx ON badge_comment(badge_id);

CREATE TABLE badge_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  badge_id BIGINT REFERENCES badge(badge_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id)
);
CREATE INDEX badge_rtg_idx ON badge_rating(badge_id);

CREATE OR REPLACE VIEW badge_tag AS
SELECT link_item_id AS badge_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008051215
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW badge_tag_log AS
SELECT link_item_id AS badge_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008051215;

CREATE OR REPLACE VIEW unique_badge_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008051215
GROUP BY tag;

CREATE TABLE badgelink_project (
  id BIGSERIAL PRIMARY KEY,
  badge_id BIGINT REFERENCES badge(badge_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX badgelink_project_idx ON badgelink_project(badge_id);
CREATE INDEX badgelink_project_prj_idx ON badgelink_project(project_id);
