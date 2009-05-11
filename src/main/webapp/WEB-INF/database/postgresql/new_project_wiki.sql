/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created
 */

CREATE TABLE lookup_wiki_state (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_wiki_categories (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE project_wiki_template (
  template_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_wiki (
  wiki_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  subject VARCHAR(500) NOT NULL,
  content TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  read_count INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true,
  read_only BOOLEAN DEFAULT false,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  state_id INTEGER REFERENCES lookup_wiki_state(code),
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  read_date TIMESTAMP(3),
  template_id INTEGER REFERENCES project_wiki_template(template_id),
  inappropriate_count INTEGER DEFAULT 0
);
CREATE UNIQUE INDEX project_wiki_uniq ON project_wiki (project_id, subject);
CREATE INDEX project_wiki_prj_idx on project_wiki(project_id);
CREATE INDEX project_wiki_subj_idx on project_wiki(subject);

CREATE TABLE project_wiki_version (
  version_id BIGSERIAL PRIMARY KEY,
  wiki_id BIGINT REFERENCES project_wiki(wiki_id) NOT NULL,
  content TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  read_count INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true,
  summary TEXT,
  lines_added INTEGER DEFAULT 0 NOT NULL,
  lines_changed INTEGER DEFAULT 0 NOT NULL,
  lines_deleted INTEGER DEFAULT 0 NOT NULL,
  size INTEGER DEFAULT 0 NOT NULL,
  lines_total INTEGER DEFAULT 0 NOT NULL
);
CREATE INDEX project_wiki_vers_idx on project_wiki_version(wiki_id);

CREATE TABLE project_wiki_ref (
  ref_id BIGSERIAL PRIMARY KEY,
  wiki_id BIGINT REFERENCES project_wiki(wiki_id) NOT NULL,
  subject_from VARCHAR(500) NOT NULL,
  subject_to VARCHAR(500) NOT NULL
);
CREATE INDEX project_wiki_ref_idx on project_wiki_ref(wiki_id);

CREATE TABLE project_wiki_categories (
  wiki_id BIGINT REFERENCES project_wiki(wiki_id) NOT NULL,
  tag_id BIGINT REFERENCES lookup_wiki_categories(code) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL
);
CREATE INDEX project_wiki_cat_idx on project_wiki_categories(wiki_id);

CREATE TABLE project_wiki_view (
  wiki_id BIGINT REFERENCES project_wiki(wiki_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_wiki_vw_idx on project_wiki_view(wiki_id);

CREATE TABLE project_wiki_comment (
  comment_id BIGSERIAL PRIMARY KEY,
  wiki_id BIGINT REFERENCES project_wiki(wiki_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby BIGINT REFERENCES users(user_id),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0
);
CREATE INDEX project_wiki_cmt_idx on project_wiki_comment(wiki_id);

CREATE TABLE project_wiki_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  wiki_id BIGINT REFERENCES project_wiki(wiki_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT false
);
CREATE INDEX project_wiki_rtg_idx on project_wiki_rating(wiki_id);

CREATE TABLE project_wiki_comment_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  comment_id BIGINT REFERENCES project_wiki_comment(comment_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT false
);
CREATE INDEX project_wiki_cmt_rtg_idx on project_wiki_comment_rating(comment_id);


CREATE OR REPLACE VIEW project_wiki_tag AS
SELECT link_item_id AS wiki_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 20060220
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW project_wiki_tag_log AS
SELECT link_item_id AS wiki_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 20060220;

CREATE OR REPLACE VIEW unique_project_wiki_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 20060220
GROUP BY tag;
