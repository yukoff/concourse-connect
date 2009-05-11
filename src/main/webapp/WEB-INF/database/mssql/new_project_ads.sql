
CREATE TABLE ad_category (
  code INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id INTEGER REFERENCES project_files(item_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BIT DEFAULT 1
);
CREATE INDEX ad_cat_prj_cat_idx ON ad_category(project_category_id);

CREATE TABLE ad (
  ad_id INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  ad_category_id INTEGER REFERENCES ad_category(code),
  project_id INTEGER REFERENCES projects(project_id),
  heading VARCHAR(255) NOT NULL,
  content TEXT,
  web_page VARCHAR(255),
  publish_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  expiration_date DATETIME,
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
  brief_description_1 VARCHAR(35),
  brief_description_2 VARCHAR(35),
  destination_url VARCHAR(1024)
);
CREATE INDEX ad_ad_cat_idx ON ad(ad_category_id);
CREATE INDEX ad_prj_cat_idx ON ad(project_category_id);

CREATE TABLE ad_view (
  ad_id INTEGER NOT NULL REFERENCES ad(ad_id),
  user_id INTEGER REFERENCES users(user_id),
  view_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX ad_vw_idx ON ad_view(ad_id);

CREATE TABLE ad_comment (
  comment_id INT IDENTITY PRIMARY KEY,
  ad_id INTEGER REFERENCES ad(ad_id) NOT NULL,
  comment TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed DATETIME,
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX ad_cmt_idx ON ad_comment(ad_id);

CREATE TABLE ad_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  ad_id INTEGER NOT NULL REFERENCES ad(ad_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
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

