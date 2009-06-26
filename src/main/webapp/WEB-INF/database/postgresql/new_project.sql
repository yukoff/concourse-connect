/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created
 */
 
CREATE SEQUENCE lookup_project_activit_code_seq;
CREATE TABLE lookup_project_activity (
  code INTEGER DEFAULT nextval('lookup_project_activit_code_seq') NOT NULL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL,
  template_id INTEGER DEFAULT 0
);

CREATE SEQUENCE lookup_project_priorit_code_seq;
CREATE TABLE lookup_project_priority (
  code INTEGER DEFAULT nextval('lookup_project_priorit_code_seq') NOT NULL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL,
  graphic VARCHAR(75),
  type INTEGER NOT NULL
);

CREATE TABLE lookup_project_status (
  code SERIAL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL,
  graphic VARCHAR(75),
  type INTEGER NOT NULL
);

CREATE TABLE lookup_project_loe (
  code SERIAL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  base_value INTEGER DEFAULT 0 NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL
);

CREATE TABLE lookup_project_role (
  code SERIAL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL
);

CREATE SEQUENCE lookup_project_cat_code_seq;
CREATE TABLE lookup_project_category (
  code INTEGER DEFAULT nextval('lookup_project_cat_code_seq') NOT NULL PRIMARY KEY,
  description VARCHAR(80) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL,
  logo_id INTEGER,
  parent_category INTEGER,
  style TEXT,
  style_enabled BOOLEAN DEFAULT false
);

CREATE SEQUENCE lookup_project_ass_code_seq;
CREATE TABLE lookup_project_assignment_role (
  code INTEGER DEFAULT nextval('lookup_project_ass_code_seq') NOT NULL PRIMARY KEY,
  description VARCHAR(80) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL
);

CREATE TABLE lookup_project_language (
  id SERIAL PRIMARY KEY,
  language_name VARCHAR(200) NOT NULL,
  language_locale VARCHAR(12),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enabled BOOLEAN DEFAULT true NOT NULL,
  default_item BOOLEAN DEFAULT false NOT NULL
);
CREATE INDEX look_proj_lang_lan_nm ON lookup_project_language(language_name);
CREATE INDEX look_proj_lang_lan_lo ON lookup_project_language(language_locale);

CREATE TABLE project_language_team (
  id SERIAL PRIMARY KEY,
  member_id BIGINT REFERENCES users(user_id) NOT NULL,
  language_id INTEGER REFERENCES lookup_project_language NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX proj_lan_team_mem_id ON project_language_team(member_id);
CREATE INDEX proj_lan_team_lan_id ON project_language_team(language_id);

CREATE TABLE projects (
  project_id BIGSERIAL PRIMARY KEY,
  group_id INTEGER,
  department_id INTEGER REFERENCES departments(code),
  template_id INTEGER,
  title VARCHAR(100) NOT NULL,
  requestedby VARCHAR(50),
  requesteddept VARCHAR(50),
  requestdate TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  approvaldate TIMESTAMP(3),
  approvalby BIGINT REFERENCES users(user_id),
  closedate TIMESTAMP(3),
  owner INTEGER,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  category_id INTEGER REFERENCES lookup_project_category(code),
  portal BOOLEAN DEFAULT false NOT NULL,
  allow_guests BOOLEAN DEFAULT false NOT NULL,
  news_enabled BOOLEAN DEFAULT true NOT NULL,
  details_enabled BOOLEAN DEFAULT true NOT NULL,
  team_enabled BOOLEAN DEFAULT true NOT NULL,
  plan_enabled BOOLEAN DEFAULT true NOT NULL,
  lists_enabled BOOLEAN DEFAULT true NOT NULL,
  discussion_enabled BOOLEAN DEFAULT true NOT NULL,
  tickets_enabled BOOLEAN DEFAULT true NOT NULL,
  documents_enabled BOOLEAN DEFAULT true NOT NULL,
  news_label VARCHAR(50),
  details_label VARCHAR(50),
  team_label VARCHAR(50),
  plan_label VARCHAR(50),
  lists_label VARCHAR(50),
  discussion_label VARCHAR(50),
  tickets_label VARCHAR(50),
  documents_label VARCHAR(50),
  est_closedate TIMESTAMP(3),
  budget FLOAT,
  budget_currency VARCHAR(5),
  portal_default BOOLEAN DEFAULT false NOT NULL,
  portal_header VARCHAR(255),
  portal_format VARCHAR(255),
  portal_key VARCHAR(255),
  portal_build_news_body BOOLEAN DEFAULT false NOT NULL,
  portal_news_menu BOOLEAN DEFAULT false NOT NULL,
  description TEXT,
  allows_user_observers BOOLEAN DEFAULT false NOT NULL,
  level INTEGER DEFAULT 10 NOT NULL,
  portal_page_type INTEGER,
  calendar_enabled BOOLEAN DEFAULT true NOT NULL,
  calendar_label VARCHAR(50),
  template BOOLEAN DEFAULT false NOT NULL,
  wiki_enabled BOOLEAN DEFAULT true NOT NULL,
  wiki_label VARCHAR(50),
  dashboard_order INTEGER DEFAULT 1 NOT NULL,
  news_order INTEGER DEFAULT 2 NOT NULL,
  calendar_order INTEGER DEFAULT 3 NOT NULL,
  wiki_order INTEGER DEFAULT 4 NOT NULL,
  discussion_order INTEGER DEFAULT 5 NOT NULL,
  documents_order INTEGER DEFAULT 6 NOT NULL,
  lists_order INTEGER DEFAULT 7 NOT NULL,
  plan_order INTEGER DEFAULT 8 NOT NULL,
  tickets_order INTEGER DEFAULT 9 NOT NULL,
  team_order INTEGER DEFAULT 10 NOT NULL,
  details_order INTEGER DEFAULT 11 NOT NULL,
  dashboard_enabled BOOLEAN DEFAULT true NOT NULL,
  dashboard_label VARCHAR(50),
  language_id INTEGER REFERENCES lookup_project_language,
  projecttextid VARCHAR(100),
  read_count BIGINT DEFAULT 0 NOT NULL,
  read_date TIMESTAMP(3),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  logo_id INTEGER,
  dashboard_description VARCHAR(255),
  news_description VARCHAR(255),
  calendar_description VARCHAR(255),
  wiki_description VARCHAR(255),
  discussion_description VARCHAR(255),
  documents_description VARCHAR(255),
  lists_description VARCHAR(255),
  plan_description VARCHAR(255),
  tickets_description VARCHAR(255),
  team_description VARCHAR(255),
  concursive_crm_url VARCHAR(255),
  concursive_crm_domain VARCHAR(255),
  concursive_crm_code VARCHAR(255),
  concursive_crm_client VARCHAR(255),
  email1 VARCHAR(255),
  email2 VARCHAR(255),
  email3 VARCHAR(255),
  home_phone VARCHAR(30),
  home_phone_ext VARCHAR(30),
  home2_phone VARCHAR(30),
  home2_phone_ext VARCHAR(30),
  home_fax VARCHAR(30),
  business_phone VARCHAR(30),
  business_phone_ext VARCHAR(30),
  business2_phone VARCHAR(30),
  business2_phone_ext VARCHAR(30),
  business_fax VARCHAR(30),
  mobile_phone VARCHAR(30),
  pager_number VARCHAR(30),
  car_phone VARCHAR(30),
  radio_phone VARCHAR(30),
  web_page VARCHAR(255),
  address_to VARCHAR(255),
  addrline1 VARCHAR(255),
  addrline2 VARCHAR(255),
  addrline3 VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  postalcode VARCHAR(255),
  latitude FLOAT DEFAULT 0,
  longitude FLOAT DEFAULT 0,
  badges_enabled BOOLEAN DEFAULT true NOT NULL,
  badges_label VARCHAR(50),
  badges_order INTEGER DEFAULT 12 NOT NULL,
  badges_description VARCHAR(255),
  reviews_enabled BOOLEAN DEFAULT true NOT NULL,
  reviews_label VARCHAR(50),
  reviews_order INTEGER DEFAULT 13 NOT NULL,
  reviews_description VARCHAR(255),
  classifieds_enabled BOOLEAN DEFAULT true NOT NULL,
  classifieds_label VARCHAR(50),
  classifieds_order INTEGER DEFAULT 14 NOT NULL,
  classifieds_description VARCHAR(255),
  ads_enabled BOOLEAN DEFAULT true NOT NULL,
  ads_label VARCHAR(50),
  ads_order INTEGER DEFAULT 15 NOT NULL,
  ads_description VARCHAR(255),
  membership_required BOOLEAN DEFAULT true,
  subcategory1_id INTEGER REFERENCES lookup_project_category(code),
  subcategory2_id INTEGER REFERENCES lookup_project_category(code),
  subcategory3_id INTEGER REFERENCES lookup_project_category(code),
  keywords VARCHAR(255),
  profile BOOLEAN DEFAULT false NOT NULL,
  profile_enabled BOOLEAN DEFAULT true NOT NULL,
  profile_label VARCHAR(50),
  profile_order INTEGER DEFAULT 0 NOT NULL,
  profile_description VARCHAR(255),
  source VARCHAR(255),
  style VARCHAR(4096),
  style_enabled BOOLEAN DEFAULT false NOT NULL,
  messages_enabled BOOLEAN DEFAULT true NOT NULL,
  messages_label VARCHAR(50),
  messages_order INTEGER DEFAULT 0 NOT NULL,
  messages_description VARCHAR(255),
  system_default BOOLEAN DEFAULT false NOT NULL,
  shortdescription VARCHAR(1000) NOT NULL
);
CREATE INDEX "projects_idx"
  ON "projects"
  USING btree ("group_id", "project_id");
CREATE INDEX projects_closedate_idx ON projects (closedate);
CREATE INDEX projects_portal_idx ON projects(portal);
CREATE INDEX projects_title_idx ON projects(title);
CREATE INDEX projects_cat_idx ON projects(category_id);
CREATE INDEX projects_subcat1_idx ON projects(subcategory1_id);

CREATE TABLE projects_view (
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX projects_vw_idx ON projects_view(project_id);

CREATE TABLE projects_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  title VARCHAR(100),
  comment TEXT,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0 NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL
);
CREATE INDEX projects_rtg_idx ON projects_rating(project_id);
CREATE INDEX projects_rtg_rtg_idx ON projects_rating(rating);

CREATE TABLE projects_rating_rating (
  record_id BIGSERIAL PRIMARY KEY,
  rating_id BIGINT REFERENCES projects_rating(rating_id),
  rating INTEGER NOT NULL,
  inappropriate BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id)
);
CREATE INDEX projects_rtgrtg_idx ON projects_rating_rating(rating_id);

CREATE OR REPLACE VIEW projects_tag AS
SELECT link_item_id AS project_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 1
GROUP BY link_item_id, tag;

CREATE OR REPLACE VIEW projects_tag_log AS
SELECT link_item_id AS project_id, user_id, tag, tag_date
FROM user_tag_log
WHERE link_module_id = 1;

CREATE OR REPLACE VIEW unique_projects_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
WHERE link_module_id = 1
GROUP BY tag;

CREATE SEQUENCE project_requi_requirement_i_seq;
CREATE TABLE project_requirements (
  requirement_id BIGINT DEFAULT nextval('project_requi_requirement_i_seq') NOT NULL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  submittedby VARCHAR(50),
  departmentby VARCHAR(30),
  shortdescription VARCHAR(255) NOT NULL,
  description TEXT,
  datereceived TIMESTAMP(3),
  estimated_loevalue INTEGER,
  estimated_loetype INTEGER REFERENCES lookup_project_loe,
  actual_loevalue INTEGER,
  actual_loetype INTEGER REFERENCES lookup_project_loe,
  deadline TIMESTAMP(3),
  approvedby BIGINT REFERENCES users(user_id),
  approvaldate TIMESTAMP(3),
  closedby BIGINT REFERENCES users(user_id),
  closedate TIMESTAMP(3),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  startdate TIMESTAMP(3),
  wiki_link VARCHAR(500),
  read_only BOOLEAN DEFAULT false NOT NULL
);
CREATE INDEX proj_req_cdate_idx ON project_requirements (closedate);

CREATE SEQUENCE project_assignmen_folder_id_seq;
CREATE TABLE project_assignments_folder (
  folder_id BIGINT DEFAULT nextval('project_assignmen_folder_id_seq') NOT NULL PRIMARY KEY,
  parent_id BIGINT REFERENCES project_assignments_folder(folder_id),
  requirement_id BIGINT REFERENCES project_requirements(requirement_id) NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL
);

CREATE SEQUENCE project_assig_assignment_id_seq;
CREATE TABLE project_assignments (
  assignment_id BIGINT DEFAULT nextval('project_assig_assignment_id_seq') NOT NULL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  requirement_id BIGINT REFERENCES project_requirements(requirement_id),
  technology VARCHAR(50),
  role VARCHAR(255),
  estimated_loevalue INTEGER,
  estimated_loetype INTEGER REFERENCES lookup_project_loe,
  actual_loevalue INTEGER,
  actual_loetype INTEGER REFERENCES lookup_project_loe,
  priority_id INTEGER REFERENCES lookup_project_priority,
  est_start_date TIMESTAMP(3),
  start_date TIMESTAMP(3),
  due_date TIMESTAMP(3),
  status_id INTEGER REFERENCES lookup_project_status,
  status_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  complete_date TIMESTAMP(3),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  folder_id BIGINT REFERENCES project_assignments_folder(folder_id),
  percent_complete INTEGER,
  responsible VARCHAR(255)
);

CREATE INDEX proj_assign_req_id_idx ON project_assignments (requirement_id);
CREATE INDEX proj_assign_cdate_idx ON project_assignments (complete_date);

CREATE TABLE project_assignments_user (
  id BIGSERIAL PRIMARY KEY,
  assignment_id BIGINT REFERENCES project_assignments NOT NULL,
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  assignment_role_id INTEGER REFERENCES lookup_project_assignment_role(code),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL
);
CREATE INDEX proj_assign_usr_aid_idx ON project_assignments_user (assignment_id);
CREATE INDEX proj_assign_usr_usr_idx ON project_assignments_user (user_id);

CREATE SEQUENCE project_assignmen_status_id_seq;
CREATE TABLE project_assignments_status (
  status_id BIGINT DEFAULT nextval('project_assignmen_status_id_seq') NOT NULL PRIMARY KEY,
  assignment_id BIGINT REFERENCES project_assignments NOT NULL,
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  description TEXT NOT NULL,
  status_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  percent_complete INTEGER,
  project_status_id INTEGER REFERENCES lookup_project_status,
  user_assign_id BIGINT REFERENCES users(user_id)
);


CREATE SEQUENCE project_issue_cate_categ_id_seq;
CREATE TABLE project_issues_categories (
  category_id BIGINT DEFAULT nextval('project_issue_cate_categ_id_seq') NOT NULL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  description TEXT,
  enabled BOOLEAN DEFAULT true NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  topics_count INTEGER DEFAULT 0 NOT NULL,
  posts_count INTEGER DEFAULT 0 NOT NULL,
  last_post_date TIMESTAMP(3),
  last_post_by INTEGER,
  allow_files BOOLEAN DEFAULT false NOT NULL,
  read_count INTEGER DEFAULT 0 NOT NULL,
  read_date TIMESTAMP(3)
);

CREATE TABLE project_issues (
  issue_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  category_id BIGINT REFERENCES project_issues_categories(category_id),
  subject VARCHAR(255) NOT NULL,
  message TEXT NOT NULL,
  importance INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  reply_count INTEGER DEFAULT 0 NOT NULL,
	last_reply_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_reply_by INTEGER,
  question BOOLEAN DEFAULT false NOT NULL,
  view_count INTEGER DEFAULT 0 NOT NULL,
  solution_reply_id INT,
  read_count INTEGER DEFAULT 0 NOT NULL,
  read_date TIMESTAMP(3),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0
);

CREATE TABLE project_issues_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  issue_id BIGINT REFERENCES project_issues(issue_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT false
);
CREATE INDEX project_issues_rtg_idx on project_issues_rating(issue_id);

CREATE TABLE project_issues_view (
  issue_id BIGINT REFERENCES project_issues(issue_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_issue_vw_idx ON project_issues_view(issue_id);

CREATE SEQUENCE project_issue_repl_reply_id_seq;
CREATE TABLE project_issue_replies (
  reply_id BIGINT DEFAULT nextval('project_issue_repl_reply_id_seq') NOT NULL PRIMARY KEY,
  issue_id BIGINT REFERENCES project_issues NOT NULL,
  reply_to INTEGER DEFAULT 0 ,
  subject VARCHAR(255) NOT NULL,
  message TEXT NOT NULL,
  importance INTEGER,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  helpful BOOLEAN DEFAULT false NOT NULL,
  solution BOOLEAN DEFAULT false NOT NULL,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0 NOT NULL,
  solution_date TIMESTAMP(3)
);

ALTER TABLE project_issues ADD CONSTRAINT proj_iss_sol_id_fkey
  FOREIGN KEY (solution_reply_id) REFERENCES project_issue_replies(reply_id);

CREATE TABLE project_issue_replies_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  reply_id BIGINT REFERENCES project_issue_replies(reply_id) NOT NULL,
  rating INTEGER NOT NULL,
  inappropriate BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id)
);
CREATE INDEX project_iss_repl_rtg_idx ON project_issue_replies_rating(reply_id);
CREATE INDEX project_iss_repl_rtg_rtg_idx ON project_issue_replies_rating(rating);

--CREATE TABLE project_issues_contacts (
--  id SERIAL,
--  issue_id BIGINT REFERENCES project_issues(issue_id),
--  user_id BIGINT REFERENCES users(user_id),
--  contact_id BIGINT REFERENCES contacts(contact_id),
--  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
--  enteredby BIGINT REFERENCES users(user_id) NOT NULL
--);


CREATE TABLE project_folders (
  folder_id BIGSERIAL PRIMARY KEY,
  link_module_id INTEGER NOT NULL,
  link_item_id BIGINT NOT NULL,
  subject VARCHAR(255) NOT NULL,
  description TEXT,
  parent_id INT,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  display INTEGER
);

CREATE TABLE project_files (
  item_id BIGSERIAL PRIMARY KEY ,
  link_module_id INTEGER NOT NULL,
  link_item_id BIGINT NOT NULL,
  folder_id BIGINT REFERENCES project_folders(folder_id),
  client_filename VARCHAR(255) NOT NULL,
  filename VARCHAR(255) NOT NULL,
  subject VARCHAR(500) NOT NULL,
  size INTEGER DEFAULT 0 ,
  version FLOAT DEFAULT 0 ,
  enabled BOOLEAN DEFAULT TRUE ,
  downloads INTEGER DEFAULT 0,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  default_file BOOLEAN DEFAULT FALSE,
  image_width INTEGER DEFAULT 0 NOT NULL,
  image_height INTEGER DEFAULT 0 NOT NULL,
  comment VARCHAR(500),
  featured_file BOOLEAN DEFAULT FALSE,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0 NOT NULL
);

CREATE INDEX "project_files_cidx" ON "project_files"
  USING btree ("link_module_id", "link_item_id");
CREATE INDEX project_files_lmid ON project_files(link_module_id);
CREATE INDEX project_files_mod ON project_files(modified);

CREATE TABLE project_files_version (
  item_id BIGINT REFERENCES project_files(item_id),
  client_filename VARCHAR(255) NOT NULL,
  filename VARCHAR(255) NOT NULL,
  subject VARCHAR(500) NOT NULL,
  size INTEGER DEFAULT 0 ,
  version FLOAT DEFAULT 0 ,
  enabled BOOLEAN DEFAULT TRUE ,
  downloads INTEGER DEFAULT 0,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  image_width INTEGER DEFAULT 0 NOT NULL,
  image_height INTEGER DEFAULT 0 NOT NULL,
  comment VARCHAR(500)
);

CREATE TABLE project_files_download (
  item_id BIGINT REFERENCES project_files(item_id) NOT NULL,
  version FLOAT DEFAULT 0 ,
  user_download_id BIGINT REFERENCES users(user_id),
  download_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX prj_files_dwn_idx ON project_files_download(item_id);

CREATE TABLE project_files_thumbnail (
  item_id BIGINT REFERENCES project_files(item_id),
  filename VARCHAR(255) NOT NULL,
  size INTEGER DEFAULT 0 ,
  version FLOAT DEFAULT 0 ,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  image_width INTEGER DEFAULT 0 NOT NULL,
  image_height INTEGER DEFAULT 0 NOT NULL,
  format VARCHAR(5)
);
CREATE INDEX prj_files_thm_itid ON project_files_thumbnail(item_id);

CREATE TABLE project_files_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  item_id BIGINT REFERENCES project_files(item_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT false
);
CREATE INDEX project_files_rtg_idx on project_files_rating(item_id);

CREATE TABLE project_team (
  team_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  userlevel INTEGER REFERENCES lookup_project_role(code) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  status INTEGER,
  last_accessed TIMESTAMP(3),
  purpose INTEGER,
  notification BOOLEAN DEFAULT false,
  tools BOOLEAN DEFAULT false NOT NULL
);
CREATE UNIQUE INDEX project_team_uni_idx ON project_team (project_id, user_id);
CREATE INDEX project_team_user_idx ON project_team(user_id);
CREATE INDEX project_team_stat_idx ON project_team(status);
CREATE INDEX project_team_proj_idx ON project_team(project_id);

CREATE TABLE project_requirements_map (
  map_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects NOT NULL,
  requirement_id BIGINT REFERENCES project_requirements NOT NULL,
  position INTEGER NOT NULL,
  indent INTEGER DEFAULT 0 NOT NULL,
  folder_id BIGINT REFERENCES project_assignments_folder,
  assignment_id BIGINT REFERENCES project_assignments
);
CREATE INDEX proj_req_map_pr_req_pos_idx ON project_requirements_map (project_id, requirement_id, position);

CREATE TABLE lookup_project_permission_category (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL
);

CREATE TABLE lookup_project_permission (
  code SERIAL PRIMARY KEY,
  category_id INTEGER REFERENCES lookup_project_permission_category(code),
  permission VARCHAR(300) UNIQUE NOT NULL,
  description VARCHAR(300) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL,
  default_role INTEGER REFERENCES lookup_project_role(code)
);

CREATE TABLE project_permissions (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  permission_id INTEGER REFERENCES lookup_project_permission(code) NOT NULL,
  userlevel INTEGER REFERENCES lookup_project_role(code) NOT NULL
);
CREATE INDEX project_perm_proj_idx ON project_permissions(project_id);

CREATE TABLE project_calendar_meeting (
  meeting_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  owner BIGINT REFERENCES users(user_id) NOT NULL,
  title VARCHAR(255) NOT NULL,
  location VARCHAR(255),
  start_date TIMESTAMP(3),
  end_date TIMESTAMP(3),
  is_tentative BOOLEAN DEFAULT false,
  by_invitation_only BOOLEAN DEFAULT false NOT NULL,
  description TEXT,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0 NOT NULL,
  is_dimdim BOOLEAN DEFAULT false,
  dimdim_url VARCHAR(255),
  dimdim_meetingid VARCHAR(255),
  dimdim_username VARCHAR(255),
  dimdim_password VARCHAR(255)
);
CREATE INDEX project_cal_mtg_idx ON project_calendar_meeting(project_id);

CREATE TABLE project_calendar_meeting_attendees (
  attendee_id SERIAL PRIMARY KEY,
  meeting_id BIGINT REFERENCES project_calendar_meeting(meeting_id) NOT NULL,
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  is_tentative BOOLEAN DEFAULT false,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  dimdim_status INTEGER
);
CREATE INDEX project_cal_mtg_att_idx ON project_calendar_meeting_attendees(meeting_id);
CREATE INDEX project_cal_mtg_att_att_idx ON project_calendar_meeting_attendees(user_id);

CREATE TABLE project_calendar_meeting_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  meeting_id BIGINT REFERENCES project_calendar_meeting(meeting_id) NOT NULL,
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT false
);
CREATE INDEX project_cal_mtg_cmt_rtg_idx on project_calendar_meeting_rating(meeting_id);

CREATE TABLE project_message_template (
  template_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  title VARCHAR(255) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_message (
  message_id BIGSERIAL PRIMARY KEY,
  subject VARCHAR(255) NOT NULL,
  body TEXT,
  project_id BIGINT NOT NULL REFERENCES projects(project_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL
);

CREATE TABLE project_msg_recipients (
  recipient_id BIGSERIAL PRIMARY KEY,
  message_id BIGINT NOT NULL REFERENCES project_message(message_id),
  contact_id BIGINT NOT NULL REFERENCES contacts(contact_id),
  status_id INT NOT NULL DEFAULT 0,
  status VARCHAR(80),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL
);

CREATE TABLE project_discussion_forum_template (
  template_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  forum_names TEXT NOT NULL,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_document_folder_template (
  template_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  folder_names TEXT NOT NULL,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_list_template (
  template_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  list_names TEXT NOT NULL,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_service (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  service_id INTEGER REFERENCES lookup_service(code) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_private_message (
	message_id BIGSERIAL PRIMARY KEY,
	project_id BIGINT REFERENCES projects(project_id),
	parent_id BIGINT,
	link_module_id INTEGER,
	link_item_id BIGINT,
	body TEXT,
	entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	enteredby BIGINT REFERENCES users(user_id),
	read_date TIMESTAMP(3),
	read_by INTEGER REFERENCES users(user_id),
	deleted_by_entered_by BOOLEAN DEFAULT false,
	deleted_by_user_id BOOLEAN DEFAULT false,
	last_reply_date TIMESTAMP(3),
	link_project_id BIGINT REFERENCES projects(project_id)
);
CREATE INDEX prj_prvt_msg_prjt_idx ON project_private_message(project_id);

CREATE TABLE project_featured_listing (
  featured_id SERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  portlet_key VARCHAR(255),
  featured_date TIMESTAMP(3)
);

-- Record project related events
CREATE TABLE project_history (
  history_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  link_object VARCHAR(255) NOT NULL,
  link_item_id BIGINT,
  link_start_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT true,
  event_type INT
);


create table project_ticket_category_template(
  template_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  ticket_categories text NOT NULL,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

-- Tracks a user's contribution for each day
CREATE TABLE user_contribution_log (
	record_id BIGSERIAL PRIMARY KEY,
	user_id BIGINT REFERENCES users(user_id) NOT NULL,
	contribution_date TIMESTAMP(3) NOT NULL,
	contribution_id INT REFERENCES lookup_contribution(code) NOT NULL,
	points BIGINT DEFAULT 0 NOT NULL,
	entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
	project_id BIGINT REFERENCES projects(project_id)
);
CREATE INDEX projects_usr_conl_usr_idx ON user_contribution_log(user_id);
CREATE INDEX projects_usr_conl_prj_idx ON user_contribution_log(project_id);
CREATE INDEX projects_usr_conl_cdt_idx ON user_contribution_log(contribution_date);
