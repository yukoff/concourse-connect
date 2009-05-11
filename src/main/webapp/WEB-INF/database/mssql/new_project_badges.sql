
CREATE TABLE badge_category (
  code INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id INTEGER REFERENCES project_files(item_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BIT DEFAULT 1
);
CREATE INDEX badge_cat_prj_cat_idx ON badge_category(project_category_id);

CREATE TABLE badge (
  badge_id INT IDENTITY PRIMARY KEY,
  badge_category_id INTEGER REFERENCES badge_category(code),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  logo_id INTEGER REFERENCES project_files(item_id),
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
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER NOT NULL REFERENCES users(user_id),
  enabled BIT DEFAULT 1,
  read_count INTEGER NOT NULL DEFAULT 0,
  read_date DATETIME,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  system_assigned BIT DEFAULT 0,
  system_constant INTEGER
);
CREATE INDEX badge_bad_cat_idx ON badge(badge_category_id);

CREATE TABLE badge_view (
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  user_id INTEGER REFERENCES users(user_id),
  view_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX badge_vw_idx ON badge_view(badge_id);

CREATE TABLE badge_comment (
  comment_id INT IDENTITY PRIMARY KEY,
  badge_id INTEGER REFERENCES badge(badge_id) NOT NULL,
  comment TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed DATETIME,
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX badge_cmt_idx ON badge_comment(badge_id);

CREATE TABLE badge_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
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

CREATE TABLE badgelink_project (
  id INT IDENTITY PRIMARY KEY,
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  project_id INT NOT NULL REFERENCES projects(project_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX badgelink_project_idx ON badgelink_project(badge_id);
CREATE INDEX badgelink_project_prj_idx ON badgelink_project(project_id);

CREATE OR REPLACE VIEW unique_badge_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008051215
GROUP BY tag;

