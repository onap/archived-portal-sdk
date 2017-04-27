-- ---------------------------------------------------------------------------------------------------------------
-- This rollback script is for the OpenSource ECOMP SDK App database from version 1707 to 1610.2.
-- change size on 3 name columns from fn_user for the Opensource version
-- changed DS1 icon names to DS2 icon names
-- removed unnecessary fn_menu entries, updated fn_menu actions
-- ---------------------------------------------------------------------------------------------------------------

USE ecomp_sdk;

update fn_menu
set image_src = 'ion-home'
where menu_id = 2 and label = 'Home';

update fn_menu
set image_src = 'ion-android-apps'
where menu_id = 5000 and label = 'Sample Pages';

update fn_menu
set image_src = 'ion-ios-paper'
where menu_id = 8 and label = 'Reports';

update fn_menu
set image_src = 'ion-person'
where menu_id = 9 and label = 'Profile';

update fn_menu
set image_src = 'ion-gear-a'
where menu_id = 10 and label = 'Admin';

update fn_menu
set action = 'collaborate_list.htm'
where menu_id = 121 and label = 'Collaboration';

update fn_menu
set action = 'notebook.htm'
where menu_id = 150038 and label = 'Notebook';

update fn_menu
set action = 'admin'
where menu_id = 101 and label = 'Roles';

delete from fn_menu 
where menu_id = 150022 and label = 'Menus';

delete from fn_menu 
where menu_id = 89 and label = 'Import';

INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (13, 'Application Logout', 1, 130, 'app_logout.htm', 'menu_logout', 'Y', NULL, NULL, NULL, NULL, 'APP', 'N', 'icon-sign-out');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (92, 'Import User', 9, 30, null, 'menu_profile_import', 'Y', NULL, NULL, NULL, NULL, 'APP', 'N', NULL); 
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (103, 'Broadcast Messages', 10, 50, 'admin#/broadcast_list', 'menu_admin', 'Y', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/bubble.png'); 

alter table fn_user
	CHANGE COLUMN FIRST_NAME FIRST_NAME VARCHAR(25) NULL DEFAULT NULL ,
	CHANGE COLUMN MIDDLE_NAME MIDDLE_NAME VARCHAR(25) NULL DEFAULT NULL ,
	CHANGE COLUMN LAST_NAME LAST_NAME VARCHAR(25) NULL DEFAULT NULL;
    
commit;
