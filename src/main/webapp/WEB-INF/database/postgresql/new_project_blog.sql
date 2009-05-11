/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created
 */

CREATE TABLE lookup_news_template (
  code SERIAL PRIMARY KEY,
  description VARCHAR(255) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL,
  load_article BOOLEAN DEFAULT false,
  load_project_article_list BOOLEAN DEFAULT false,
  load_article_linked_list BOOLEAN DEFAULT false,
  load_public_projects BOOLEAN DEFAULT false,
  load_article_category_list BOOLEAN DEFAULT false,
  mapped_jsp VARCHAR(255) NOT NULL
);

CREATE TABLE project_news_category (
  category_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  category_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);
CREATE INDEX projects_newsc_pid_idx ON project_news_category(project_id);
CREATE INDEX projects_newsc_ena_idx ON project_news_category(enabled);

CREATE TABLE project_news (
  news_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id),
  category_id INTEGER REFERENCES project_news_category(category_id),
  subject VARCHAR(255) NOT NULL,
  intro TEXT NULL,
  message TEXT,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  end_date TIMESTAMP DEFAULT NULL,
  allow_replies BOOLEAN DEFAULT false,
  allow_rating BOOLEAN DEFAULT false,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  avg_rating FLOAT DEFAULT 0,
  priority_id INTEGER DEFAULT 10,
  read_count INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true,
  status INTEGER DEFAULT NULL,
  html BOOLEAN DEFAULT true NOT NULL,
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
  read_date TIMESTAMP(3),
  inappropriate_count INTEGER DEFAULT 0
);
CREATE INDEX projects_news_pid_idx ON project_news(project_id);
CREATE INDEX projects_news_stat_idx ON project_news(status);
CREATE INDEX projects_news_port_idx ON project_news(portal_key);
CREATE INDEX projects_news_start_idx ON project_news(start_date);
CREATE INDEX projects_news_end_idx ON project_news(end_date);

CREATE TABLE project_news_view (
  news_id BIGINT REFERENCES project_news(news_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_news_vw_idx ON project_news_view(news_id);

CREATE TABLE project_news_comment (
  comment_id BIGSERIAL PRIMARY KEY,
  news_id BIGINT REFERENCES project_news(news_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
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
CREATE INDEX project_news_cmt_idx ON project_news_comment(news_id);

CREATE TABLE project_news_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  news_id BIGINT REFERENCES project_news(news_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT FALSE 
);
CREATE INDEX project_news_rtg_idx ON project_news_rating(news_id);

CREATE TABLE project_news_comment_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  comment_id BIGINT REFERENCES project_news_comment(comment_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT false
);
CREATE INDEX project_news_cmt_rtg_idx on project_news_comment_rating(comment_id);

CREATE TABLE project_news_tags (
  news_id BIGINT REFERENCES project_news(news_id) NOT NULL,
  tag VARCHAR(255) NOT NULL,
  tag_count INTEGER DEFAULT 0
);
CREATE INDEX project_news_tag_idx ON project_news_tags(news_id);
