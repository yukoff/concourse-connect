INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 1, 'Project Initialization');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 2, 'Analysis/Software Requirements');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 3, 'Specifications');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 4, 'Prototype');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 5, 'System Development');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 6, 'Testing');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 7, 'Training');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 8, 'Documentation');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 9, 'Deployment');
INSERT INTO lookup_project_activity (group_id, level, description) VALUES (1, 10, 'Post Implementation Review');

INSERT INTO lookup_project_status (group_id, level, description, type, graphic) VALUES
  (1, 1, 'Not Started', 1, 'box.gif');
INSERT INTO lookup_project_status (group_id, level, description, type, graphic) VALUES
  (1, 2, 'In Progress', 2, 'box.gif');
INSERT INTO lookup_project_status (group_id, level, description, type, graphic) VALUES
  (1, 5, 'On Hold', 5, 'box-hold.gif');
INSERT INTO lookup_project_status (group_id, level, description, type, graphic) VALUES
  (1, 6, 'Waiting on Reqs', 5, 'box-hold.gif');
INSERT INTO lookup_project_status (group_id, level, description, type, graphic) VALUES
  (1, 3, 'Complete', 3, 'box-checked.gif');
INSERT INTO lookup_project_status (group_id, level, description, type, graphic) VALUES
  (1, 4, 'Closed', 4, 'box-checked.gif');
  
INSERT INTO lookup_project_loe (group_id, level, description, default_item, base_value) VALUES (1, 10, 'Minute(s)', @FALSE@, 60);
INSERT INTO lookup_project_loe (group_id, level, description, default_item, base_value) VALUES (1, 20, 'Hour(s)', @TRUE@, 3600);
INSERT INTO lookup_project_loe (group_id, level, description, default_item, base_value) VALUES (1, 30, 'Day(s)', @FALSE@, 86400);
INSERT INTO lookup_project_loe (group_id, level, description, default_item, base_value) VALUES (1, 40, 'Week(s)', @FALSE@, 604800);
INSERT INTO lookup_project_loe (group_id, level, description, default_item, base_value) VALUES (1, 50, 'Month(s)', @FALSE@, 18144000);

INSERT INTO lookup_project_priority (group_id, level, description, type) VALUES (1, 1, 'Low', 10);
INSERT INTO lookup_project_priority (group_id, level, description, type, default_item) VALUES (1, 2, 'Normal', 20, @TRUE@);
INSERT INTO lookup_project_priority (group_id, level, description, type) VALUES (1, 3, 'High', 30);

INSERT INTO lookup_project_role (group_id, level, description) VALUES (1, 10, 'Admin');       -- 1
INSERT INTO lookup_project_role (group_id, level, description) VALUES (1, 14, 'Manager');     -- 2
INSERT INTO lookup_project_role (group_id, level, description) VALUES (1, 17, 'Champion');    -- 3
INSERT INTO lookup_project_role (group_id, level, description) VALUES (1, 20, 'VIP');         -- 4
INSERT INTO lookup_project_role (group_id, level, description) VALUES (1, 25, 'Member');      -- 5
INSERT INTO lookup_project_role (group_id, level, description) VALUES (1, 30, 'Participant'); -- 6
INSERT INTO lookup_project_role (group_id, level, description) VALUES (1, 100, 'Guest');      -- 7

/* Permissions */
INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 10, 'Project Details');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 1, 10, 5, 'project-details-view', 'View project details');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 1, 20, 1, 'project-details-edit', 'Modify project details');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 1, 30, 1, 'project-details-delete', 'Delete project');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 20, 'Members');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 2, 10, 6, 'project-team-view', 'View members');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 2, 20, 1, 'project-team-view-email', 'See member email addresses');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 2, 30, 2, 'project-team-edit', 'Add and remove members');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 2, 40, 2, 'project-team-edit-role', 'Modify member role');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 2, 50, 2, 'project-team-tools', 'Grant access to tools');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 30, 'Blog');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 3, 10, 7, 'project-news-view', 'View currently published entries');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 3, 20, 4, 'project-news-view-unreleased', 'View unreleased entries');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 3, 30, 7, 'project-news-view-archived', 'View archived entries');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 3, 36, 6, 'project-news-add-comment', 'Comment on entry');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 3, 40, 3, 'project-news-add', 'Add entries');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 3, 50, 3, 'project-news-edit', 'Edit entries');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 3, 60, 2, 'project-news-delete', 'Delete entries');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 40, 'Plan/Outlines');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 4, 10, 5, 'project-plan-view', 'View outlines');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 4, 20, 3, 'project-plan-outline-add', 'Add an outline');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 4, 40, 3, 'project-plan-outline-edit', 'Modify details of an existing outline');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 4, 50, 2, 'project-plan-outline-delete', 'Delete an outline');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 4, 60, 3, 'project-plan-outline-modify', 'Make changes to an outline');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 4, 70, 3, 'project-plan-activities-assign', 'Re-assign activities');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 50, 'Lists');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 5, 10, 7, 'project-lists-view', 'View lists');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 5, 20, 3, 'project-lists-add', 'Add a list');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 5, 30, 3, 'project-lists-edit', 'Modify details of an existing list');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 5, 40, 2, 'project-lists-delete', 'Delete a list');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 5, 50, 3, 'project-lists-modify', 'Make changes to list items');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 60, 'Discussion');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 10, 7, 'project-discussion-forums-view', 'View discussion forums');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 20, 2, 'project-discussion-forums-add', 'Add discussion forum');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 30, 2, 'project-discussion-forums-edit', 'Modify discussion forum');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 40, 2, 'project-discussion-forums-delete', 'Delete discussion forum');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 50, 7, 'project-discussion-topics-view', 'View forum topics');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 60, 6, 'project-discussion-topics-add', 'Add forum topics');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 70, 2, 'project-discussion-topics-edit', 'Modify forum topics');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 80, 2, 'project-discussion-topics-delete', 'Delete forum topics');
-- Note the following permission is never used.
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 90,  6, 'project-discussion-messages-add', 'Post messages');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 100, 6, 'project-discussion-messages-reply', 'Reply to messages');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 110, 2, 'project-discussion-messages-edit', 'Modify existing messages');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 6, 120, 2, 'project-discussion-messages-delete', 'Delete messages');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 70, 'Issues');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 7, 10, 7, 'project-tickets-view', 'View issues');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 7, 20, 6, 'project-tickets-other', 'Access to issues created by others');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 7, 30, 6, 'project-tickets-add', 'Add an issue');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 7, 40, 3, 'project-tickets-edit', 'Modify an existing issue');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 7, 50, 3, 'project-tickets-assign', 'Assign issues');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 7, 60, 3, 'project-tickets-close', 'Close/re-open issues');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 7, 70, 2, 'project-tickets-delete', 'Delete issues');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 80, 'Documents');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 10, 7, 'project-documents-view', 'View documents');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 20, 2, 'project-documents-folders-add', 'Create folders');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 30, 2, 'project-documents-folders-edit', 'Modify folders');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 40, 2, 'project-documents-folders-delete', 'Delete folders');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 50, 4, 'project-documents-files-upload', 'Upload files');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 60, 6, 'project-documents-files-download', 'Download files');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 70, 2, 'project-documents-files-rename', 'Rename files');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 8, 80, 2, 'project-documents-files-delete', 'Delete files');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 90, 'Setup');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 9, 10, 1, 'project-setup-customize', 'Setup features');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 9, 20, 1, 'project-setup-permissions', 'Configure permissions');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 9, 30, 1, 'project-setup-style', 'Configure look and feel');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 35, 'Wiki');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 10, 10, 7, 'project-wiki-view', 'View wiki');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 10, 20, 4, 'project-wiki-add', 'Create and modify public content');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 10, 22, 3, 'project-wiki-locked-edit', 'Create and modify locked content');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 10, 30, 2, 'project-wiki-admin', 'Administrate the wiki');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 23, 'Dashboard');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 11, 10, 5, 'project-dashboard-view', 'View dashboard');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 11, 20, 2, 'project-dashboard-add', 'Create and modify content');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 11, 30, 2, 'project-dashboard-admin', 'Administrate the dashboard');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 27, 'Calendar');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 12, 10, 7, 'project-calendar-view', 'View calendar');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 12, 20, 3, 'project-calendar-add', 'Create and modify events');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 12, 20, 2, 'project-calendar-other', 'Modify events owned by other users');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 12, 30, 2, 'project-calendar-delete', 'Delete events');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 82, 'Promotions');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 13, 10, 7, 'project-ads-view', 'View public promotions');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 13, 14, 4, 'project-ads-private-view', 'View private promotions');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 13, 20, 2, 'project-ads-add', 'Create and modify promotions');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 13, 30, 2, 'project-ads-admin', 'Administrate promotions');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 84, 'Classifieds');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 14, 10, 7, 'project-classifieds-view', 'View classifieds');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 14, 20, 2, 'project-classifieds-add', 'Create and modify classifieds');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 14, 30, 2, 'project-classifieds-admin', 'Administrate classifieds');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 86, 'Badges');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 15, 10, 7, 'project-badges-view', 'View badges');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 15, 30, 2, 'project-badges-admin', 'Administrate badges');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 15, 'Profile');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 16, 10, 7, 'project-profile-view', 'View profile');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 16, 20, 2, 'project-profile-admin', 'Configure the profile');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 16, 30, 5, 'project-profile-images-add', 'Contribute images');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 16, 33, 2, 'project-profile-images-delete', 'Delete images sent by other users');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 88, 'Reviews');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 17, 10, 7, 'project-reviews-view', 'View reviews');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 17, 20, 6, 'project-reviews-add', 'Add reviews');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 17, 30, 1, 'project-reviews-admin', 'Administrate reviews');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 89, 'Messages');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 18, 10, 2, 'project-private-messages-view', 'View private messages');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 18, 20, 2, 'project-private-messages-reply', 'Reply to private messages');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 18, 30, 1, 'project-private_messages-delete', 'Delete private messages');

-- More permissions for the profile
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 16, 40, 2, 'project-profile-activity-add', 'Add activity');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 16, 50, 2, 'project-profile-activity-delete', 'Delete activity');

-- Contributions
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.wiki.contribution.ContributionCalculationForWikiPagesModified',     'Wiki pages added or updated', 10,              25);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.blog.contribution.ContributionCalculationForBlogsPublished',        'Blogs published', 20,                          20);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.reviews.contribution.ContributionCalculationForReviewsAdded',          'Reviews contributed', 30,                      15);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.discussion.contribution.ContributionCalculationForTopicsAnswered',        'Discussion topics answered', 40,               10);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.documents.contribution.ContributionCalculationForFilesUploaded',         'Files uploaded', 50,                            5);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.reviews.contribution.ContributionCalculationForReviewsLiked',          'Users who liked your review', 60,               3);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.profile.contribution.ContributionCalculationForProjectsAdded',         'Listings added', 70,                            2);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.discussion.contribution.ContributionCalculationForTopicsAdded',           'Discussion topics posted', 80,                  1);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.promotions.contribution.ContributionCalculationForAdsPlaced',             'Promotions placed', 90,                         1);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.classifieds.contribution.ContributionCalculationForClassifiedsPlaced',     'Classifieds placed', 100,                       1);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.blog.contribution.ContributionCalculationForCommentsOnBlog',        'Comments added to your blog', 110,              1);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.blog.contribution.ContributionCalculationForBlogCommentsAdded',     'Blog comments added', 120,                      1);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.documents.contribution.ContributionCalculationForFilesDownloaded',       'Users that downloaded your file', 130,          1);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.promotions.contribution.ContributionCalculationForMessagesToPromotions',  'Private messages inquiring a promotion', 140,   1);
INSERT INTO lookup_contribution (constant, description, level, points_awarded) VALUES ('com.concursive.connect.web.modules.classifieds.contribution.ContributionCalculationForMessagesToClassifieds', 'Private messages inquiring a classified', 150,  1);
