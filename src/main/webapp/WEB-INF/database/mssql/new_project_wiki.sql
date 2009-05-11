CREATE TABLE lookup_wiki_state (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_wiki_categories (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BIT DEFAULT 1
);

CREATE TABLE project_wiki_template (
  template_id INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_wiki (
  wiki_id INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  subject VARCHAR(500) NOT NULL,
  content TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER NOT NULL REFERENCES users(user_id),
  read_count INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1,
  read_only BIT DEFAULT 0
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  state_id INT REFERENCES lookup_wiki_state(code),
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  read_date DATETIME,
  template_id INTEGER REFERENCES project_wiki_template(template_id),
  inappropriate_count INTEGER DEFAULT 0
);
CREATE UNIQUE INDEX project_wiki_uniq ON project_wiki (project_id, subject);
CREATE INDEX project_wiki_prj_idx on project_wiki(project_id);
CREATE INDEX project_wiki_subj_idx on project_wiki(subject);

CREATE TABLE project_wiki_version (
  version_id INT IDENTITY PRIMARY KEY,
  wiki_id INT NOT NULL REFERENCES project_wiki(wiki_id),
  content TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  read_count INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1,
  summary TEXT,
  lines_added INTEGER NOT NULL DEFAULT 0,
  lines_changed INTEGER NOT NULL DEFAULT 0,
  lines_deleted INTEGER NOT NULL DEFAULT 0,
  size INTEGER NOT NULL DEFAULT 0,
  lines_total INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX project_wiki_vers_idx on project_wiki_version(wiki_id);

CREATE TABLE project_wiki_ref (
  ref_id INT IDENTITY PRIMARY KEY,
  wiki_id INT NOT NULL REFERENCES project_wiki(wiki_id),
  subject_from VARCHAR(500) NOT NULL,
  subject_to VARCHAR(500) NOT NULL
);
CREATE INDEX project_wiki_ref_idx on project_wiki_ref(wiki_id);

CREATE TABLE project_wiki_categories (
  wiki_id INT NOT NULL REFERENCES project_wiki(wiki_id),
  tag_id INT NOT NULL REFERENCES lookup_wiki_categories(code),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id)
);
CREATE INDEX project_wiki_cat_idx on project_wiki_categories(wiki_id);

CREATE TABLE project_wiki_view (
  wiki_id INTEGER NOT NULL REFERENCES project_wiki(wiki_id),
  user_id INTEGER NULL REFERENCES users(user_id),
  view_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_wiki_vw_idx on project_wiki_view(wiki_id);

CREATE TABLE project_wiki_comment (
  comment_id INT IDENTITY PRIMARY KEY,
  wiki_id INTEGER REFERENCES project_wiki(wiki_id) NOT NULL,
  comment TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby INTEGER REFERENCES users(user_id),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0
);
CREATE INDEX project_wiki_cmt_idx on project_wiki_comment(wiki_id);

CREATE TABLE project_wiki_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  wiki_id INTEGER NOT NULL REFERENCES project_wiki(wiki_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
);
CREATE INDEX project_wiki_rtg_idx on project_wiki_rating(wiki_id);

CREATE TABLE project_wiki_comment_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  comment_id INTEGER REFERENCES project_wiki_comment(comment_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
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
