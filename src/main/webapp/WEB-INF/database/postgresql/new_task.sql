/**
 *  PostgreSQL Table Creation
 *
 *@author     a mathur
 *@created    September 2, 2002
 *@version    $Id: new_task.sql 3140 2008-09-08 08:05:04Z lbittner@centriccrm.com $
 */
 
CREATE TABLE lookup_task_priority (
  code SERIAL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_loe (
  code SERIAL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_category (
  code SERIAL PRIMARY KEY,
  description VARCHAR(255) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_functional_area (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_status (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_value (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_complexity (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_release (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_sprint (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_loe_remaining (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_assigned_to (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE lookup_task_assigned_priority (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE task (
  task_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  priority INTEGER REFERENCES lookup_task_priority NOT NULL,
  description VARCHAR(255),
  duedate DATE,
  reminderid INTEGER,
  notes TEXT,
  sharing INTEGER NOT NULL,
  complete BOOLEAN DEFAULT false NOT NULL,
  enabled BOOLEAN DEFAULT false NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id),
  estimatedloe FLOAT,
  estimatedloetype INTEGER REFERENCES lookup_task_loe,
  owner BIGINT REFERENCES users(user_id),
  completedate TIMESTAMP(3),
  category_id INTEGER REFERENCES lookup_task_category,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  functional_area INTEGER REFERENCES lookup_task_functional_area,
  status INTEGER REFERENCES lookup_task_status,
  business_value INTEGER REFERENCES lookup_task_value,
  complexity INTEGER REFERENCES lookup_task_complexity,
  target_release INTEGER REFERENCES lookup_task_release,
  target_sprint INTEGER REFERENCES lookup_task_sprint,
  loe_remaining INTEGER REFERENCES lookup_task_loe_remaining,
  assigned_to INTEGER REFERENCES lookup_task_assigned_to,
  assigned_priority INTEGER REFERENCES lookup_task_assigned_priority,
  link_module_id INTEGER,
  link_item_id BIGINT
);
CREATE INDEX task_catid_idx ON task(category_id);
CREATE INDEX task_trelid_idx ON task(target_release);
CREATE INDEX task_statusid_idx ON task(status);
CREATE INDEX task_lm_idx ON task(link_module_id);
CREATE INDEX task_li_idx ON task(link_item_id);

CREATE TABLE tasklog (
  id SERIAL PRIMARY KEY,
  task_id BIGINT REFERENCES task(task_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  priority INTEGER REFERENCES lookup_task_priority NOT NULL,
  duedate TIMESTAMP(3),
  complete BOOLEAN DEFAULT false NOT NULL,
  estimatedloe FLOAT,
  estimatedloetype INTEGER REFERENCES lookup_task_loe,
  owner BIGINT REFERENCES users(user_id),
  completedate TIMESTAMP(3),
  category_id INTEGER REFERENCES lookup_task_category,
  functional_area BIGINT REFERENCES lookup_task_functional_area,
  status BIGINT REFERENCES lookup_task_status,
  business_value BIGINT REFERENCES lookup_task_value,
  complexity BIGINT REFERENCES lookup_task_complexity,
  target_release BIGINT REFERENCES lookup_task_release,
  target_sprint BIGINT REFERENCES lookup_task_sprint,
  loe_remaining BIGINT REFERENCES lookup_task_loe_remaining,
  assigned_to BIGINT REFERENCES lookup_task_assigned_to,
  assigned_priority BIGINT REFERENCES lookup_task_assigned_priority
);
CREATE INDEX tasklog_taskid_idx ON tasklog(task_id);

 
CREATE TABLE tasklink_contact (
  task_id BIGINT REFERENCES task NOT NULL,
  contact_id BIGINT NOT NULL,
  notes TEXT
);

CREATE TABLE tasklink_ticket (
  task_id BIGINT REFERENCES task NOT NULL,
  ticket_id BIGINT NOT NULL
);

CREATE TABLE tasklink_project (
  task_id BIGINT REFERENCES task NOT NULL,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL
);

CREATE TABLE taskcategory_project (
  category_id INTEGER REFERENCES lookup_task_category NOT NULL,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL
);

CREATE TABLE taskcategorylink_news (
  news_id BIGINT REFERENCES project_news(news_id) NOT NULL,
  category_id INTEGER REFERENCES lookup_task_category NOT NULL
);

CREATE TABLE task_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  task_id BIGINT REFERENCES task(task_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id)
);
CREATE UNIQUE INDEX task_rating_uni_idx ON task_rating (task_id, enteredby);
CREATE INDEX project_task_rtg_idx on task_rating(task_id);
CREATE INDEX project_task_rtge_idx on task_rating(enteredby);
