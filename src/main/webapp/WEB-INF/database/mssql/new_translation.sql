
-- Language Translation script
CREATE TABLE language_pack (
  id INT IDENTITY PRIMARY KEY,
  language_name VARCHAR(200) NOT NULL,
  language_locale VARCHAR(12),
  maintainer_id INTEGER REFERENCES users(user_id),
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  percent_complete INTEGER NOT NULL DEFAULT 0,
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE language_team (
  id INT IDENTITY PRIMARY KEY,
  member_id INTEGER REFERENCES users(user_id) NOT NULL,
  language_pack_id INTEGER REFERENCES language_pack NOT NULL,
  allow_translate BIT NOT NULL DEFAULT 0,
  allow_review BIT NOT NULL DEFAULT 0,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE language_config (
  id INT IDENTITY PRIMARY KEY,
  language_id INT REFERENCES language_pack(id),
  config_name VARCHAR(300)
);
CREATE UNIQUE INDEX lang_conf_la_idx ON language_config (language_id, config_name);
CREATE INDEX lang_conf_lang_idx ON language_config(language_id);

CREATE TABLE language_dictionary (
  id INT IDENTITY PRIMARY KEY,
  config_id INT REFERENCES language_config(id),
  param_name VARCHAR(300) NOT NULL,
  param_value1 TEXT,
  param_value2 TEXT,
  approved INT NOT NULL DEFAULT -1,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME,
  modifiedby INTEGER REFERENCES users(user_id),
  changed BIT DEFAULT 0
);
CREATE UNIQUE INDEX lang_dict_cp_idx ON language_dictionary (config_id, param_name);
CREATE INDEX lang_dic_conf_idx ON language_dictionary(config_id);
CREATE INDEX lang_dic_parmv_idx ON language_dictionary(param_value1);
