/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created    may 7, 2008
 */

CREATE TABLE classified_category (
  code SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id BIGINT REFERENCES project_files(item_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);
CREATE INDEX classified_cat_prj_cat_idx ON classified_category(project_category_id);

CREATE TABLE project_classified (
  classified_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  classified_category_id INTEGER REFERENCES classified_category(code),
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  amount FLOAT,
  amount_currency VARCHAR(5),
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
  rating_avg FLOAT DEFAULT 0 NOT NULL
);
CREATE INDEX project_classified_prj_idx ON project_classified(project_id);
CREATE INDEX project_classified_cat_idx ON project_classified(classified_category_id);
CREATE INDEX project_classified_prj_cat_idx ON project_classified(project_category_id);

CREATE TABLE project_classified_view (
  classified_id BIGINT REFERENCES project_classified(classified_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_classified_vw_idx ON project_classified_view(classified_id);

CREATE TABLE project_classified_comment (
  comment_id BIGSERIAL PRIMARY KEY,
  classified_id BIGINT REFERENCES project_classified(classified_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby BIGINT REFERENCES users(user_id)
);
CREATE INDEX project_classified_cmt_idx ON project_classified_comment(classified_id);

CREATE TABLE project_classified_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  classified_id BIGINT REFERENCES project_classified(classified_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id)
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
