/*
 *   Postgresql
 *   $Id Exp$
 *
 *   Table: contact
 *   Sequence: contact_contact_id_seq
 */

CREATE TABLE contact (
  contact_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER DEFAULT -1,
  modified TIMESTAMP(3),
  modifiedby INTEGER DEFAULT -1,
  owner INTEGER DEFAULT -1,
  enabled BOOLEAN DEFAULT false,
  title VARCHAR(255),
  first_name VARCHAR(255),
  middle_name VARCHAR(255),
  last_name VARCHAR(255),
  suffix VARCHAR(255),
  file_as VARCHAR(255),
  categories VARCHAR(500),
  birthday TIMESTAMP(3) DEFAULT NULL,
  anniversary TIMESTAMP(3) DEFAULT NULL,
  spouse VARCHAR(255),
  children VARCHAR(255),
  home_telephone VARCHAR(20),
  home2_telephone VARCHAR(20),
  home_fax VARCHAR(20),
  business_telephone VARCHAR(20),
  business2_telephone VARCHAR(20),
  business_fax VARCHAR(20),
  mobile_telephone VARCHAR(20),
  pager_number VARCHAR(20),
  car_telephone VARCHAR(20),
  radio_telephone VARCHAR(20),
  email1_address VARCHAR(80),
  email2_address VARCHAR(80),
  email3_address VARCHAR(80),
  web_page VARCHAR(255),
  company_name VARCHAR(255),
  department VARCHAR(255),
  office_location VARCHAR(255),
  job_title VARCHAR(255),
  assistant_name VARCHAR(255),
  assistant_telephone VARCHAR(20),
  home_address_street VARCHAR(255),
  home_address_city VARCHAR(100),
  home_address_state VARCHAR(100),
  home_address_postal_code VARCHAR(50),
  home_address_country VARCHAR(100),
  other_address_street VARCHAR(255),
  other_address_city VARCHAR(100),
  other_address_state VARCHAR(100),
  other_address_postal_code VARCHAR(50),
  other_address_country VARCHAR(100),
  business_address_street VARCHAR(255),
  business_address_city VARCHAR(100),
  business_address_state VARCHAR(100),
  business_address_postal_code VARCHAR(50),
  business_address_country VARCHAR(100),
  body TEXT
);
  
/*
CREATE TABLE lookup_contact_category (
  code SERIAL PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  group_id INTEGER DEFAULT 0 NOT NULL,
  template_id INTEGER DEFAULT 0
);
*/
