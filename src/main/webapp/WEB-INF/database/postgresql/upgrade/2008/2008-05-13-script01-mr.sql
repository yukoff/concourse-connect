CREATE TABLE ad_category (
  code SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id INTEGER REFERENCES project_files(item_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);
CREATE INDEX ad_cat_prj_cat_idx ON ad_category(project_category_id);

CREATE TABLE ad (
  ad_id SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  ad_category_id INTEGER REFERENCES ad_category(code),
  project_id INTEGER REFERENCES projects(project_id),
  heading VARCHAR(255) NOT NULL,
  content TEXT,
  web_page VARCHAR(255),
  publish_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  expiration_date TIMESTAMP(3),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER NOT NULL REFERENCES users(user_id),
  enabled BOOLEAN DEFAULT true,
  read_count INTEGER NOT NULL DEFAULT 0,
  read_date TIMESTAMP(3),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL
);
CREATE INDEX ad_ad_cat_idx ON ad(ad_category_id);
CREATE INDEX ad_prj_cat_idx ON ad(project_category_id);

CREATE TABLE ad_view (
  ad_id INTEGER NOT NULL REFERENCES ad(ad_id),
  user_id INTEGER REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX ad_vw_idx ON ad_view(ad_id);

CREATE TABLE ad_comment (
  comment_id SERIAL PRIMARY KEY,
  ad_id INTEGER REFERENCES ad(ad_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX ad_cmt_idx ON ad_comment(ad_id);

CREATE TABLE ad_rating (
  ad_id INTEGER NOT NULL REFERENCES ad(ad_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
);
CREATE INDEX ad_rtg_idx ON ad_rating(ad_id);

CREATE TABLE ad_tag (
  ad_id INTEGER NOT NULL REFERENCES ad(ad_id),
  tag VARCHAR(255) NOT NULL,
  tag_count INT DEFAULT 0,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX ad_tag_idx ON ad_tag(ad_id);
CREATE INDEX ad_tag_count_idx ON ad_tag(tag_count);

CREATE TABLE ad_tag_log (
  ad_id INTEGER NOT NULL REFERENCES ad(ad_id),
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  tag VARCHAR(255) NOT NULL,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX ad_tag_log_idx ON ad_tag_log(ad_id);
CREATE INDEX ad_tag_log_usr_idx ON ad_tag_log(user_id);

CREATE TABLE badge_category (
  code SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id INTEGER REFERENCES project_files(item_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);
CREATE INDEX badge_cat_prj_cat_idx ON badge_category(project_category_id);

CREATE TABLE badge (
  badge_id SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  badge_category_id INTEGER REFERENCES badge_category(code),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  logo_id INTEGER REFERENCES project_files(item_id),
  email1 VARCHAR(255),
  email2 VARCHAR(255),
  email3 VARCHAR(255),
  business_phone VARCHAR(30),
  business_phone_ext VARCHAR(30),
  web_page VARCHAR(255),
  addrline1 VARCHAR(255),
  addrline2 VARCHAR(255),
  addrline3 VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  postalcode VARCHAR(255),
  latitude FLOAT DEFAULT 0,
  longitude FLOAT DEFAULT 0,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER NOT NULL REFERENCES users(user_id),
  enabled BOOLEAN DEFAULT true,
  read_count INTEGER NOT NULL DEFAULT 0,
  read_date TIMESTAMP(3),
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL
);
CREATE INDEX badge_bad_cat_idx ON badge(badge_category_id);
CREATE INDEX badge_prj_cat_idx ON badge(project_category_id);

CREATE TABLE badge_view (
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  user_id INTEGER REFERENCES users(user_id),
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX badge_vw_idx ON badge_view(badge_id);

CREATE TABLE badge_comment (
  comment_id SERIAL PRIMARY KEY,
  badge_id INTEGER REFERENCES badge(badge_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX badge_cmt_idx ON badge_comment(badge_id);

CREATE TABLE badge_rating (
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
);
CREATE INDEX badge_rtg_idx ON badge_rating(badge_id);

CREATE TABLE badge_tag (
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  tag VARCHAR(255) NOT NULL,
  tag_count INT DEFAULT 0,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX badge_tag_idx ON badge_tag(badge_id);
CREATE INDEX badge_tag_count_idx ON badge_tag(tag_count);

CREATE TABLE badge_tag_log (
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  tag VARCHAR(255) NOT NULL,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX badge_tag_log_idx ON badge_tag_log(badge_id);
CREATE INDEX badge_tag_log_usr_idx ON badge_tag_log(user_id);

CREATE TABLE badgelink_project (
  id SERIAL PRIMARY KEY,
  badge_id INTEGER NOT NULL REFERENCES badge(badge_id),
  project_id INT NOT NULL REFERENCES projects(project_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classified_category (
  code SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  item_name VARCHAR(255) NOT NULL,
  logo_id INTEGER REFERENCES project_files(item_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true
);
CREATE INDEX classified_cat_prj_cat_idx ON classified_category(project_category_id);

CREATE TABLE project_classified (
  classified_id SERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  classified_category_id INTEGER REFERENCES classified_category(code),
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  amount FLOAT,
  amount_currency VARCHAR(5),
  publish_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  expiration_date TIMESTAMP(3),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER NOT NULL REFERENCES users(user_id),
  enabled BOOLEAN DEFAULT true,
  read_count INTEGER NOT NULL DEFAULT 0,
  read_date TIMESTAMP(3),
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
  view_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_classified_vw_idx ON project_classified_view(classified_id);

CREATE TABLE project_classified_comment (
  comment_id SERIAL PRIMARY KEY,
  classified_id INTEGER REFERENCES project_classified(classified_id) NOT NULL,
  comment TEXT NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed TIMESTAMP(3),
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX project_classified_cmt_idx ON project_classified_comment(classified_id);

CREATE TABLE project_classified_rating (
  classified_id INTEGER NOT NULL REFERENCES project_classified(classified_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
);
CREATE INDEX project_classified_rtg_idx ON project_classified_rating(classified_id);

CREATE TABLE project_classified_tag (
  classified_id INTEGER NOT NULL REFERENCES project_classified(classified_id),
  tag VARCHAR(255) NOT NULL,
  tag_count INT DEFAULT 0,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_classified_tag_idx ON project_classified_tag(classified_id);
CREATE INDEX project_classified_tag_count_idx ON project_classified_tag(tag_count);

CREATE TABLE project_classified_tag_log (
  classified_id INTEGER NOT NULL REFERENCES project_classified(classified_id),
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  tag VARCHAR(255) NOT NULL,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_classified_tag_log_idx ON project_classified_tag_log(classified_id);
CREATE INDEX project_classified_tag_log_usr_idx ON project_classified_tag_log(user_id);

