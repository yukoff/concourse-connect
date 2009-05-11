
CREATE TABLE classified_category (
  code INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id INTEGER REFERENCES project_files(item_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BIT DEFAULT 1
);
CREATE INDEX classified_cat_prj_cat_idx ON classified_category(project_category_id);

CREATE TABLE project_classified (
  classified_id INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  classified_category_id INTEGER REFERENCES classified_category(code),
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  amount FLOAT,
  amount_currency VARCHAR(5),
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
  rating_avg FLOAT DEFAULT 0 NOT NULL
);
CREATE INDEX project_classified_prj_idx ON project_classified(project_id);
CREATE INDEX project_classified_cat_idx ON project_classified(classified_category_id);
CREATE INDEX project_classified_prj_cat_idx ON project_classified(project_category_id);

CREATE TABLE project_classified_view (
  classified_id INTEGER NOT NULL REFERENCES project_classified(classified_id),
  user_id INTEGER REFERENCES users(user_id),
  view_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_classified_vw_idx ON project_classified_view(classified_id);

CREATE TABLE project_classified_comment (
  comment_id INT IDENTITY PRIMARY KEY,
  classified_id INTEGER REFERENCES project_classified(classified_id) NOT NULL,
  comment TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed DATETIME,
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX project_classified_cmt_idx ON project_classified_comment(classified_id);

CREATE TABLE project_classified_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  classified_id INTEGER NOT NULL REFERENCES project_classified(classified_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
);
CREATE INDEX project_classified_rtg_idx ON project_classified_rating(classified_id);

CREATE OR REPLACE VIEW project_classified_tag AS
SELECT link_item_id AS classified_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071716
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_classified_tag_log AS
SELECT link_item_id AS classified_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 2008071716;

CREATE OR REPLACE VIEW unique_project_classified_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 2008071716
GROUP BY tag;
