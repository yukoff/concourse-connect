CREATE TABLE lookup_authentication_classes (
  code INT IDENTITY PRIMARY KEY,
  login_mode VARCHAR(300),
  login_authenticator VARCHAR(300),
  session_validator VARCHAR(300),
  enabled BIT DEFAULT 0,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP
);

--INSERT INTO lookup_authentication_classes (login_mode, login_authenticator, session_validator, enabled) VALUES ('Default', 'com.concursive.connect.web.modules.login.auth.LoginAuthenticator', 'com.concursive.connect.web.modules.login.auth.session.SessionValidator', true);
--INSERT INTO lookup_authentication_classes (login_mode, login_authenticator, session_validator, enabled) VALUES ('Token', 'com.concursive.connect.web.modules.login.auth.LoginAuthenticator', 'com.concursive.connect.web.modules.login.auth.session.TokenSessionValidator', true);
