/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created    March 25, 2003
 *@version    $Id: new_ticket.sql 4004 2009-02-18 13:17:09Z kailash $
 */
 
CREATE TABLE ticket_level (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);


CREATE TABLE ticket_severity (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  style text DEFAULT '' NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);


CREATE TABLE lookup_ticketsource (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);


CREATE TABLE lookup_ticket_status (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);


CREATE TABLE ticket_priority (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  style text DEFAULT '' NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);


CREATE TABLE ticket_category ( 
  id BIGSERIAL PRIMARY KEY,
  cat_level BIGINT DEFAULT 0 NOT NULL,
  parent_cat_code BIGINT NOT NULL,
  description VARCHAR(300) NOT NULL,
  full_description TEXT DEFAULT '' NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  project_id BIGINT REFERENCES projects(project_id)
);

CREATE TABLE ticket_cause (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE ticket_resolution (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE ticket_defect (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE ticket_escalation (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE ticket_state (
  code BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE ticket (
  ticketid BIGSERIAL PRIMARY KEY,
  org_id BIGINT,
  contact_id BIGINT REFERENCES users(user_id),
  problem TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP,
  pri_code INTEGER REFERENCES ticket_priority(code),
  level_code INTEGER REFERENCES ticket_level(code),
  department_code INTEGER REFERENCES departments(code),
  source_code INTEGER REFERENCES lookup_ticketsource(code),
  cat_code BIGINT,
  subcat_code1 BIGINT,
  subcat_code2 BIGINT,
  subcat_code3 BIGINT,
  assigned_to BIGINT REFERENCES users(user_id),
  comment TEXT,
  solution TEXT,
  scode INTEGER REFERENCES ticket_severity(code),
  critical TIMESTAMP,
  notified TIMESTAMP,
  custom_data TEXT,
  location VARCHAR(255),
  assigned_date TIMESTAMP(3),
  est_resolution_date TIMESTAMP(3),
  resolution_date TIMESTAMP(3),
  cause TEXT,
  key_count INTEGER NOT NULL,
  status_id INTEGER REFERENCES lookup_ticket_status(code),
  cause_id BIGINT REFERENCES ticket_cause(code),
  resolution_id BIGINT REFERENCES ticket_resolution(code),
  defect_id BIGINT REFERENCES ticket_defect(code),
  escalation_id BIGINT REFERENCES ticket_escalation(code),
  state_id BIGINT REFERENCES ticket_state(code),
  related_id BIGINT,
  ready_for_close BOOLEAN DEFAULT false NOT NULL,
  read_count BIGINT DEFAULT 0 NOT NULL,
  read_date TIMESTAMP(3),
  link_project_id BIGINT REFERENCES projects(project_id),
  link_module_id BIGINT,
  link_item_id BIGINT
);

CREATE INDEX "ticket_cidx" ON "ticket" USING btree ("assigned_to", "closed");
CREATE INDEX "ticketlist_entered" ON "ticket" (entered);
CREATE INDEX ticketlist_assigned ON ticket (assigned_to);
CREATE INDEX ticketlist_closed ON ticket (closed);

CREATE TABLE project_ticket_count (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) UNIQUE NOT NULL,
  key_count INTEGER DEFAULT 0 NOT NULL
);

CREATE TABLE ticket_view (
  ticketid BIGINT REFERENCES ticket(ticketid) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX ticket_vw_idx on ticket_view(ticketid);


CREATE TABLE ticketlog (
  id BIGSERIAL PRIMARY KEY,
  ticketid BIGINT REFERENCES ticket(ticketid),
  assigned_to BIGINT REFERENCES users(user_id),
  comment TEXT,
  closed BOOLEAN,
  pri_code INTEGER REFERENCES ticket_priority(code),
  level_code INTEGER,
  department_code INTEGER REFERENCES departments(code),
  cat_code BIGINT,
  scode INTEGER REFERENCES ticket_severity(code),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL
);

CREATE TABLE ticketlink_project (
  id BIGSERIAL PRIMARY KEY,
  ticket_id BIGINT REFERENCES ticket(ticketid) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL
);
CREATE INDEX ticketlink_project_idx ON ticketlink_project(ticket_id);
CREATE INDEX ticketlink_projectid_idx ON ticketlink_project(project_id);

CREATE TABLE ticket_contacts (
  id BIGSERIAL,
  ticketid BIGINT REFERENCES ticket(ticketid),
  user_id BIGINT REFERENCES users(user_id),
  contact_id BIGINT REFERENCES contacts(contact_id),
  contact_name VARCHAR(255),
  contact_email VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL
);
