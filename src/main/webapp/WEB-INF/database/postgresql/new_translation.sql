
-- Language Translation script
CREATE TABLE language_pack (
  id SERIAL PRIMARY KEY,
  language_name VARCHAR(200) NOT NULL,
  language_locale VARCHAR(12),
  maintainer_id BIGINT REFERENCES users(user_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  percent_complete INTEGER DEFAULT 0 NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE language_team (
  id BIGSERIAL PRIMARY KEY,
  member_id BIGINT REFERENCES users(user_id) NOT NULL,
  language_pack_id INTEGER REFERENCES language_pack NOT NULL,
  allow_translate BOOLEAN DEFAULT false NOT NULL,
  allow_review BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE language_config (
  id SERIAL PRIMARY KEY,
  language_id INTEGER REFERENCES language_pack(id),
  config_name VARCHAR(300)
);
CREATE UNIQUE INDEX lang_conf_la_idx ON language_config (language_id, config_name);
CREATE INDEX lang_conf_lang_idx ON language_config(language_id);

CREATE TABLE language_dictionary (
  id BIGSERIAL PRIMARY KEY,
  config_id INTEGER REFERENCES language_config(id),
  param_name VARCHAR(300) NOT NULL,
  param_value1 TEXT,
  param_value2 TEXT,
  approved INTEGER DEFAULT -1 NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3),
  modifiedby BIGINT REFERENCES users(user_id),
  changed BOOLEAN DEFAULT false
);
CREATE UNIQUE INDEX lang_dict_cp_idx ON language_dictionary (config_id, param_name);
CREATE INDEX lang_dic_conf_idx ON language_dictionary(config_id);
CREATE INDEX lang_dic_parmv_idx ON language_dictionary(param_value1);
