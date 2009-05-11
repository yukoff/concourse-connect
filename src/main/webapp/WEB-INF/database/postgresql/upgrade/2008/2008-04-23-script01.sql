ALTER TABLE catalog_product ADD show_in_catalog BOOLEAN DEFAULT true NOT NULL;
ALTER TABLE catalog_product ADD cart_enabled BOOLEAN DEFAULT true NOT NULL;
ALTER TABLE catalog_product ADD action_text VARCHAR(25);

ALTER TABLE catalog_option ADD option_text TEXT;

ALTER TABLE user_request ADD addrline1 VARCHAR(255);
ALTER TABLE user_request ADD addrline2 VARCHAR(255);
ALTER TABLE user_request ADD addrline3 VARCHAR(255);
ALTER TABLE user_request ADD city VARCHAR(255);
ALTER TABLE user_request ADD state VARCHAR(255);
ALTER TABLE user_request ADD country VARCHAR(255);
ALTER TABLE user_request ADD postalcode VARCHAR(255);
ALTER TABLE user_request ADD website VARCHAR(255);
ALTER TABLE user_request ADD number_of_seats INTEGER;
ALTER TABLE user_request ADD requested_url VARCHAR(255);
ALTER TABLE user_request ADD language VARCHAR(255);
