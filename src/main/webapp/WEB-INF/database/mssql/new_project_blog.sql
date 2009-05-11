
CREATE TABLE lookup_news_template (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(255) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1,
  group_id INTEGER NOT NULL DEFAULT 0,
  load_article BIT DEFAULT 0,
  load_project_article_list BIT DEFAULT 0,
  load_article_linked_list BIT DEFAULT 0,
  load_public_projects BIT DEFAULT 0,
  load_article_category_list BIT DEFAULT 0,
  mapped_jsp VARCHAR(255) NOT NULL
);

CREATE TABLE project_news_category (
  category_id INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  category_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);
CREATE INDEX projects_newsc_pid_idx ON project_news_category(project_id);
CREATE INDEX projects_newsc_ena_idx ON project_news_category(enabled);

CREATE TABLE project_news (
  news_id INT IDENTITY PRIMARY KEY,
  project_id INTEGER REFERENCES projects(project_id),
  category_id INTEGER REFERENCES project_news_category(category_id),
  subject VARCHAR(255) NOT NULL,
  intro TEXT NULL,
  message TEXT,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER NOT NULL REFERENCES users(user_id),
  start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  end_date DATETIME DEFAULT NULL,
  allow_replies BIT DEFAULT 0,
  allow_rating BIT DEFAULT 0,
  rating_count INTEGER NOT NULL DEFAULT 0,
  avg_rating FLOAT DEFAULT 0,
  priority_id INTEGER DEFAULT 10,
  read_count INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1,
  status INTEGER DEFAULT NULL,
  html BIT NOT NULL DEFAULT 1,
  classification_id INTEGER NOT NULL,
  template_id INTEGER REFERENCES lookup_news_template,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  portal_key VARCHAR(100),
  redirect VARCHAR(500),
  page_title VARCHAR(250),
  keywords VARCHAR(500),
  description VARCHAR(500),
  meta_name VARCHAR(100),
  meta_content VARCHAR(500),
  read_date DATETIME,
  inappropriate_count INTEGER DEFAULT 0
);
CREATE INDEX projects_news_pid_idx ON project_news(project_id);
CREATE INDEX projects_news_stat_idx ON project_news(status);
CREATE INDEX projects_news_port_idx ON project_news(portal_key);
CREATE INDEX projects_news_start_idx ON project_news(start_date);
CREATE INDEX projects_news_end_idx ON project_news(end_date);

CREATE TABLE project_news_view (
  news_id INTEGER NOT NULL REFERENCES project_news(news_id),
  user_id INTEGER NULL REFERENCES users(user_id),
  view_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_news_vw_idx ON project_news_view(news_id);

CREATE TABLE project_news_comment (
  comment_id INT IDENTITY PRIMARY KEY,
  news_id INTEGER REFERENCES project_news(news_id) NOT NULL,
  comment TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed DATETIME,
  closedby INTEGER REFERENCES users(user_id),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0
);
CREATE INDEX project_news_cmt_idx ON project_news_comment(news_id);

CREATE TABLE project_news_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  news_id INTEGER NOT NULL REFERENCES project_news(news_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
);
CREATE INDEX project_news_rtg_idx ON project_news_rating(news_id);

CREATE TABLE project_news_comment_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  comment_id INTEGER REFERENCES project_news_comment(comment_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  project_id INTEGER REFERENCES projects(project_id),
  inappropriate BIT DEFAULT 0
);
CREATE INDEX project_news_cmt_rtg_idx on project_news_comment_rating(comment_id);

CREATE TABLE project_news_tags (
  news_id INTEGER NOT NULL REFERENCES project_news(news_id),
  tag VARCHAR(255) NOT NULL,
  tag_count INTEGER DEFAULT 0
);
CREATE INDEX project_news_tag_idx ON project_news_tags(news_id);
