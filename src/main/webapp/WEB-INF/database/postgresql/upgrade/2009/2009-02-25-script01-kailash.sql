ALTER TABLE project_wiki_comment ADD rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_wiki_comment ADD rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_wiki_comment ADD rating_avg FLOAT DEFAULT 0 NOT NULL;
ALTER TABLE project_wiki_comment ADD inappropriate_count INTEGER DEFAULT 0;

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

ALTER TABLE project_news_comment ADD rating_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_news_comment ADD rating_value INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE project_news_comment ADD rating_avg FLOAT DEFAULT 0 NOT NULL;
ALTER TABLE project_news_comment ADD inappropriate_count INTEGER DEFAULT 0;

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
