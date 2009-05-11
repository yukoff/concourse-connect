
-- product 1
INSERT INTO catalog_product 
(product_name, price_description, details, base_price, small_image, enabled, billing_address_required, payment_required) VALUES
('Hosted Edition', 'Per month', 'With the hosted edition you can get started right away.  Your login to Team Elements is on servers in our data center, maintained and backed up by us, and includes the latest features of Team Elements.', 0, 'images/centric/Box-Hosted-Small.jpg', true, true, true);

INSERT INTO catalog_option
(option_name, option_type, enabled, default_value, additional_text) VALUES
('Users', 1, true, 1, '');

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, invalid) VALUES
(1, 'Less than 1 seat', null, 0, true, true);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_per_qty) VALUES
(1, 'Standard Price', 1, null, true, 7.95);


INSERT INTO catalog_product_config
(product_id, option_id) VALUES
(1, 1);


-- product 2
INSERT INTO catalog_product 
(product_name, price_description, details, base_price, small_image, enabled, contact_information_required) VALUES
('Enterprise Edition', 'Maintenance contract', 'Jump start your implementation of Team Elements with a lifetime maintenance contract.  The Enterprise Edition has no per-seat licensing fees -- the only charge is for continued product innovation.<br /><br />Maintenance covers priority access to:<ul><li>All new versions, major and minor</li><li>Bug fixes</li><li>Upgrade and migration scripts</li><li>Start-up and application support</li></ul>This is a one-time lifetime fee per user!  You only pay for additional users as you need them, but continue to receive updates per this maintenance contract.', 0, 'images/centric/Box-Enterprise-Small.jpg', true, true);

INSERT INTO catalog_option
(option_name, option_type, enabled, additional_text) VALUES
('Users', 1, true, '(minimum of 6)');

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, invalid) VALUES
(2, 'Less than 6', null, 5, true, true);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_per_qty) VALUES
(2, '6-25', 6, 25, true, 400);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_per_qty) VALUES
(2, '26-100', 26, 100, true, 375);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_per_qty) VALUES
(2, '101-250', 101, 250, true, 350);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_per_qty) VALUES
(2, '+250', 251, null, true, 325);

INSERT INTO catalog_product_config
(product_id, option_id) VALUES
(2, 2);

-- Option 3

INSERT INTO catalog_option
(option_name, option_type, enabled, additional_text) VALUES
('Term', 2, true, null);

INSERT INTO catalog_option_value (option_id, description, level, enabled) VALUES
(3, '1 Year', 1, true);

INSERT INTO catalog_option_value (option_id, description, level, enabled) VALUES
(3, '2 Years', 2, true);

INSERT INTO catalog_option_value (option_id, description, level, enabled) VALUES
(3, '4 Years', 3, true);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_multiplier, value_id) VALUES
(3, '1 Year', null, null, true, 1, 1);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_multiplier, value_id) VALUES
(3, '2 Years', null, null, true, 1.86, 2);

INSERT INTO catalog_option_price
(option_id, description, range_low, range_high, enabled, price_multiplier, value_id) VALUES
(3, '4 Years', null, null, true, 3.6, 3);

INSERT INTO catalog_product_config
(product_id, option_id) VALUES
(2, 3);

