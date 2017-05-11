-- ---------------------------------------------------------------------------------------------------------------
-- This script upgrades the OpenSource ECOMP SDK App database from version 1610.2 to 1707.
-- change size on 3 name columns from fn_user for the Opensource version
--
-- changed DS1 icon names to DS2 icon names
-- removed unnecessary fn_menu entries, updated fn_menu actions
-- ---------------------------------------------------------------------------------------------------------------

USE ecomp_sdk;

update fn_menu
set image_src = 'icon-building-home'
where menu_id = 2 and  label = 'Home';

update fn_menu
set image_src = 'icon-documents-book'
where menu_id = 5000 and label = 'Sample Pages';

update fn_menu
set image_src = 'icon-misc-piechart'
where menu_id = 8 and  label = 'Reports';

update fn_menu
set image_src = 'icon-people-oneperson'
where menu_id = 9 and  label = 'Profile';

update fn_menu
set image_src = 'icon-content-star'
where menu_id = 10 and label = 'Admin';

update fn_menu
set action = 'samplePage#/collaborate_list'
where menu_id = 121 and label = 'Collaboration';

update fn_menu
set action = 'samplePage#/notebook'
where menu_id = 150038 and label = 'Notebook';

update fn_menu
set action = 'admin#/admin'
where menu_id = 101 and label = 'Roles';

update fn_menu 
set action ='report#/report_wizard' 
where menu_id = 87 and label = 'Create Reports';

delete from fn_menu 
where menu_id = 13 and label = 'Application Logout';

delete from fn_menu 
where menu_id = 92 and label = 'Import User';

delete from fn_menu 
where menu_id = 103 and label = 'Broadcast Messages';

INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (89, 'Import', 8, 140, 'report#/report_import', 'menu_reports', 'Y', null, null, null, null, 'APP', 'N', null); 
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (150022, 'Menus', 10, 60, 'admin#/admin_menu_edit', 'menu_admin', 'Y', NULL, NULL, NULL, NULL, 'APP', 'N', NULL);

alter table fn_user
	CHANGE COLUMN FIRST_NAME FIRST_NAME VARCHAR(50) NULL DEFAULT NULL ,
	CHANGE COLUMN MIDDLE_NAME MIDDLE_NAME VARCHAR(50) NULL DEFAULT NULL ,
	CHANGE COLUMN LAST_NAME LAST_NAME VARCHAR(50) NULL DEFAULT NULL;
    
commit;
