-- MSSQL

CREATE TABLE catalog_category (
  category_id INT IDENTITY PRIMARY KEY,
  parent_id INTEGER REFERENCES catalog_category(category_id),
  category_name VARCHAR(255) NOT NULL,
  abbreviation VARCHAR(30),
  short_description TEXT,
  long_description TEXT,
  small_image_id INTEGER REFERENCES project_files(item_id),
  large_image_id INTEGER REFERENCES project_files(item_id),
  list_order INTEGER DEFAULT 10,
  enteredby INT NOT NULL REFERENCES users(user_id),
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modifiedby INT NOT NULL REFERENCES users(user_id),
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  start_date DATETIME,
  expiration_date DATETIME,
  enabled BIT NOT NULL DEFAULT 1
);

CREATE TABLE product_category_map (
  id INT IDENTITY PRIMARY KEY,
  category1_id INTEGER NOT NULL REFERENCES catalog_category(category_id),
  category2_id INTEGER NOT NULL REFERENCES catalog_category(category_id)
);

CREATE TABLE catalog_product (
  product_id INT IDENTITY PRIMARY KEY,
  product_name VARCHAR(255) NOT NULL,
  price_description VARCHAR(255) NOT NULL,
  details TEXT,
  base_price FLOAT NOT NULL,
  small_image VARCHAR(255),
  large_image VARCHAR(255),
  enabled BIT DEFAULT 0,
  billing_address_required BIT DEFAULT 0,
  shipping_address_required BIT DEFAULT 0,
  payment_required BIT DEFAULT 0,
  contact_information_required BIT DEFAULT 0,
  parent_id INT REFERENCES catalog_product(product_id),
  order_description VARCHAR(1024),
  enteredby INT NOT NULL REFERENCES users(user_id),
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modifiedby INT NOT NULL REFERENCES users(user_id),
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  start_date DATETIME,
  expiration_date DATETIME,
  small_image_id INTEGER REFERENCES project_files(item_id),
  large_image_id INTEGER REFERENCES project_files(item_id),
  product_sku VARCHAR(255),
  show_in_catalog BIT DEFAULT 1 NOT NULL,
  cart_enabled BIT DEFAULT 1 NOT NULL,
  action_text VARCHAR(25)
);

CREATE TABLE product_catalog_category_map (
  id INT IDENTITY PRIMARY KEY,
  product_id INTEGER NOT NULL REFERENCES catalog_product(product_id),
  category_id INTEGER NOT NULL REFERENCES catalog_category(category_id)
);

CREATE TABLE catalog_option (
  option_id INT IDENTITY PRIMARY KEY,
  option_name VARCHAR(255) NOT NULL,
  option_type INT NOT NULL,
  default_value VARCHAR(255),
  enabled BIT DEFAULT 0,
  additional_text VARCHAR(255),
  validation_script TEXT,
  option_sku_modifier VARCHAR(255),
  level INTEGER DEFAULT 0,
  option_text TEXT
);

CREATE TABLE catalog_option_value (
  value_id INT IDENTITY PRIMARY KEY,
  option_id INTEGER NOT NULL REFERENCES catalog_option(option_id),
  description VARCHAR(255) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 0
);

CREATE TABLE catalog_option_price (
  price_id INT IDENTITY PRIMARY KEY,
  option_id INT NOT NULL REFERENCES catalog_option(option_id),
  description VARCHAR(255),
  range_low INT,
  range_high INT,
  value_id INT REFERENCES catalog_option_value(value_id),
  enabled BIT DEFAULT 0,
  invalid BIT DEFAULT 0,
  price_amount FLOAT,
  price_per_qty FLOAT,
  price_multiplier FLOAT,
  price_qty_multiplier BIT DEFAULT 0,
  price_add_on FLOAT,
  range_block INT,
  invoice_text VARCHAR(255)
);

CREATE TABLE catalog_product_config (
  config_id INT IDENTITY PRIMARY KEY,
  product_id INT NOT NULL REFERENCES catalog_product(product_id),
  option_id INT NOT NULL REFERENCES catalog_option(option_id)
);

CREATE TABLE customer_order (
	order_id INT IDENTITY PRIMARY KEY,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ipaddress VARCHAR(30),
	browser VARCHAR(300),
  total_price FLOAT,
	processed DATETIME,
	order_by INT REFERENCES users(user_id)
);

CREATE TABLE customer_order_product (
  item_id INT IDENTITY PRIMARY KEY,
  order_id INT NOT NULL REFERENCES customer_order(order_id),
  product_id INT NOT NULL REFERENCES catalog_product(product_id),
	product_name VARCHAR(255) NOT NULL,
  price_description VARCHAR(255) NOT NULL,
  total_price FLOAT
);

CREATE TABLE customer_order_product_options (
  item_option_id INT IDENTITY PRIMARY KEY,
  item_id INT NOT NULL REFERENCES customer_order_product(item_id),
  option_id INT NOT NULL REFERENCES catalog_option(option_id),
  option_name VARCHAR(255) NOT NULL,
  option_value VARCHAR(255),
  price_amount FLOAT
);

CREATE TABLE customer_order_address (
  address_id INT IDENTITY PRIMARY KEY,
  order_id INT NOT NULL REFERENCES customer_order(order_id),
  address_type VARCHAR(255) NOT NULL,
	namefirst VARCHAR(255),
	namelast VARCHAR(255),
  organization VARCHAR(255),
  title VARCHAR(255),
	addressline1 VARCHAR(255),
	addressline2 VARCHAR(255),
	addressline3 VARCHAR(255),
	city VARCHAR(255),
	state VARCHAR(255),
	postal_code VARCHAR(255),
	country VARCHAR(255),
	phone_number VARCHAR(30),
	phone_extension VARCHAR(10),
	fax_number VARCHAR(30),
	email VARCHAR(256),
	primary_address BIT DEFAULT 0
);
	
CREATE TABLE customer_order_payment (
  payment_id INT IDENTITY PRIMARY KEY,
  order_id INT NOT NULL REFERENCES customer_order(order_id),
  payment_type VARCHAR(255) NOT NULL,
	credit_card_type VARCHAR(255),
	credit_card_number VARCHAR(255),
	credit_card_exp_month INT,
	credit_card_exp_year INT,
	processed DATETIME,
	charge_amount FLOAT
);

CREATE TABLE user_request (
	request_id INT IDENTITY PRIMARY KEY,
	request VARCHAR(255),
	namefirst VARCHAR(255),
	namelast VARCHAR(255),
	company_name VARCHAR(255),
	title VARCHAR(255),
	phone_number VARCHAR(30),
	phone_extension VARCHAR(10),
	email VARCHAR(255),
	ipaddress VARCHAR(30),
	browser VARCHAR(300),
	entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	processed DATETIME,
	addrline1 VARCHAR(255),
	addrline2 VARCHAR(255),
	addrline3 VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  postalcode VARCHAR(255),
  website VARCHAR(255),
  number_of_seats INTEGER,
  requested_url VARCHAR(255),
	language VARCHAR(255)
);

CREATE TABLE catalog_product_attachments (
  attachment_id INT IDENTITY PRIMARY KEY,
  product_id INT NOT NULL REFERENCES catalog_product(product_id),
  file_id INT NOT NULL REFERENCES project_files(item_id),
  allow_before_checkout BIT NOT NULL DEFAULT 0,
  allow_after_checkout BIT NOT NULL DEFAULT 0,
  send_as_email BIT NOT NULL DEFAULT 0,
  days_allowed INTEGER NOT NULL DEFAULT 0,
  hours_allowed INTEGER NOT NULL DEFAULT 1
);
