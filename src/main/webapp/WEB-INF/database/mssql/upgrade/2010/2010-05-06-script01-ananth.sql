-- Delete any existing webcast records
DELETE FROM project_webcast_rating;
DELETE FROM project_webcast;

ALTER TABLE project_webcast DROP webcast_constant;