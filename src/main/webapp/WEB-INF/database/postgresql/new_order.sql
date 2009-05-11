-- PostgreSQL

CREATE TABLE catalog_category (
  category_id BIGSERIAL PRIMARY KEY,
  parent_id BIGINT REFERENCES catalog_category(category_id),
  category_name VARCHAR(255) NOT NULL,
  abbreviation VARCHAR(30),
  short_description TEXT,
  long_description TEXT,
  small_image_id BIGINT REFERENCES project_files(item_id),
  large_image_id BIGINT REFERENCES project_files(item_id),
  list_order INTEGER DEFAULT 10,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  start_date TIMESTAMP(3),
  expiration_date TIMESTAMP(3),
  enabled BOOLEAN DEFAULT true NOT NULL
);

CREATE TABLE product_category_map (
  id BIGSERIAL PRIMARY KEY,
  category1_id BIGINT REFERENCES catalog_category(category_id) NOT NULL,
  category2_id BIGINT REFERENCES catalog_category(category_id) NOT NULL
);

CREATE TABLE catalog_product (
  product_id BIGSERIAL PRIMARY KEY,
  product_name VARCHAR(255) NOT NULL,
  price_description VARCHAR(255) NOT NULL,
  details TEXT,
  base_price FLOAT NOT NULL,
  small_image VARCHAR(255),
  large_image VARCHAR(255),
  enabled BOOLEAN DEFAULT false,
  billing_address_required BOOLEAN DEFAULT false,
  shipping_address_required BOOLEAN DEFAULT false,
  payment_required BOOLEAN DEFAULT false,
  contact_information_required BOOLEAN DEFAULT false,
  parent_id BIGINT REFERENCES catalog_product(product_id),
  order_description VARCHAR(1024),
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  start_date TIMESTAMP(3),
  expiration_date TIMESTAMP(3),
  small_image_id BIGINT REFERENCES project_files(item_id),
  large_image_id BIGINT REFERENCES project_files(item_id),
  product_sku VARCHAR(255),
  show_in_catalog BOOLEAN DEFAULT true NOT NULL,
  cart_enabled BOOLEAN DEFAULT true NOT NULL,
  action_text VARCHAR(25)
);

CREATE TABLE product_catalog_category_map (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT REFERENCES catalog_product(product_id) NOT NULL,
  category_id BIGINT REFERENCES catalog_category(category_id) NOT NULL
);

CREATE TABLE catalog_option (
  option_id BIGSERIAL PRIMARY KEY,
  option_name VARCHAR(255) NOT NULL,
  option_type INTEGER NOT NULL,
  default_value VARCHAR(255),
  enabled BOOLEAN DEFAULT false,
  additional_text VARCHAR(255),
  validation_script TEXT,
  option_sku_modifier VARCHAR(255),
  level INTEGER DEFAULT 0,
  option_text TEXT
);

CREATE TABLE catalog_option_value (
  value_id BIGSERIAL PRIMARY KEY,
  option_id BIGINT REFERENCES catalog_option(option_id) NOT NULL,
  description VARCHAR(255) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT false
);

CREATE TABLE catalog_option_price (
  price_id BIGSERIAL PRIMARY KEY,
  option_id BIGINT REFERENCES catalog_option(option_id) NOT NULL,
  description VARCHAR(255),
  range_low INTEGER,
  range_high INTEGER,
  value_id BIGINT REFERENCES catalog_option_value(value_id),
  enabled BOOLEAN DEFAULT false,
  invalid BOOLEAN DEFAULT false,
  price_amount FLOAT,
  price_per_qty FLOAT,
  price_multiplier FLOAT,
  price_qty_multiplier BOOLEAN DEFAULT false,
  price_add_on FLOAT,
  range_block INTEGER,
  invoice_text VARCHAR(255)
);

CREATE TABLE catalog_product_config (
  config_id BIGSERIAL PRIMARY KEY,
  product_id BIGINT REFERENCES catalog_product(product_id) NOT NULL,
  option_id BIGINT REFERENCES catalog_option(option_id) NOT NULL
);

CREATE TABLE customer_order (
	order_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  ipaddress VARCHAR(30),
	browser VARCHAR(300),
  total_price FLOAT,
	processed TIMESTAMP(3),
	order_by BIGINT REFERENCES users(user_id)
);

CREATE TABLE customer_order_product (
  item_id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES customer_order(order_id) NOT NULL,
  product_id BIGINT REFERENCES catalog_product(product_id) NOT NULL,
	product_name VARCHAR(255) NOT NULL,
  price_description VARCHAR(255) NOT NULL,
  total_price FLOAT
);

CREATE TABLE customer_order_product_options (
  item_option_id BIGSERIAL PRIMARY KEY,
  item_id BIGINT REFERENCES customer_order_product(item_id) NOT NULL,
  option_id BIGINT REFERENCES catalog_option(option_id) NOT NULL,
  option_name VARCHAR(255) NOT NULL,
  option_value VARCHAR(255),
  price_amount FLOAT
);

CREATE TABLE customer_order_address (
  address_id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES customer_order(order_id) NOT NULL,
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
	primary_address BOOLEAN DEFAULT false
);
	
CREATE TABLE customer_order_payment (
  payment_id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES customer_order(order_id) NOT NULL,
  payment_type VARCHAR(255) NOT NULL,
	credit_card_type VARCHAR(255),
	credit_card_number VARCHAR(255),
	credit_card_exp_month INTEGER,
	credit_card_exp_year INTEGER,
	processed TIMESTAMP(3),
	charge_amount FLOAT
);

CREATE TABLE user_request (
	request_id BIGSERIAL PRIMARY KEY,
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
	entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	processed TIMESTAMP(3),
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
  attachment_id BIGSERIAL PRIMARY KEY,
  product_id BIGINT REFERENCES catalog_product(product_id) NOT NULL,
  file_id BIGINT REFERENCES project_files(item_id) NOT NULL,
  allow_before_checkout BOOLEAN DEFAULT false NOT NULL,
  allow_after_checkout BOOLEAN DEFAULT false NOT NULL,
  send_as_email BOOLEAN DEFAULT false NOT NULL,
  days_allowed INTEGER DEFAULT 0 NOT NULL,
  hours_allowed INTEGER DEFAULT 1 NOT NULL
);
