CREATE TABLE user_tag (
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  tag VARCHAR(255) NOT NULL,
  tag_count INT DEFAULT 0,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX user_tag_idx ON user_tag(user_id);
CREATE INDEX user_tag_count_idx ON user_tag(tag_count);

CREATE TABLE user_tag_log (
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  link_module_id INTEGER NOT NULL,
  link_item_id INTEGER NOT NULL,
  tag VARCHAR(255) NOT NULL,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX user_tag_log_idx ON user_tag_log(user_id);

DROP TABLE project_wiki_tags;

CREATE TABLE project_wiki_tag (
  wiki_id INTEGER NOT NULL REFERENCES project_wiki(wiki_id),
  tag VARCHAR(255) NOT NULL,
  tag_count INT DEFAULT 0,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_wiki_tag_idx ON project_wiki_tag(wiki_id);
CREATE INDEX project_wiki_tag_count_idx ON project_wiki_tag(tag_count);

CREATE TABLE project_wiki_tag_log (
  wiki_id INTEGER NOT NULL REFERENCES project_wiki(wiki_id),
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  tag VARCHAR(255) NOT NULL,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX project_wiki_tag_log_idx ON project_wiki_tag_log(wiki_id);
CREATE INDEX project_wiki_tag_log_usr_idx ON project_wiki_tag_log(user_id);
