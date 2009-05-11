CREATE TABLE lookup_authentication_classes (
  code BIGSERIAL PRIMARY KEY,
  login_mode VARCHAR(300),
  login_authenticator VARCHAR(300),
  session_validator VARCHAR(300),
  enabled BOOLEAN DEFAULT false,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

--INSERT INTO lookup_authentication_classes (login_mode, login_authenticator, session_validator, enabled) VALUES ('Default', 'com.concursive.connect.web.modules.login.auth.LoginAuthenticator', 'com.concursive.connect.web.modules.login.auth.session.SessionValidator', true);
--INSERT INTO lookup_authentication_classes (login_mode, login_authenticator, session_validator, enabled) VALUES ('Token', 'com.concursive.connect.web.modules.login.auth.LoginAuthenticator', 'com.concursive.connect.web.modules.login.auth.session.TokenSessionValidator', true);
