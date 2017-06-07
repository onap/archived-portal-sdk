-- ---------------------------------------------------------------------------------------------------------------
-- This rollback script is for the OpenSource ECOMP SDK App database from version 1707 to 1610.2.
-- change size on 3 name columns from fn_user for the Opensource version
-- changed DS1 icon names to DS2 icon names
-- removed unnecessary fn_menu entries, updated fn_menu actions;  changes the schema name from ecomp_sdk to ecomp_sdk_os_os
-- ---------------------------------------------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS=1; 
CREATE DATABASE IF NOT EXISTS ecomp_sdk_os;

rename table ecomp_sdk.cr_favorite_reports to ecomp_sdk_os.cr_favorite_reports;
rename table ecomp_sdk.cr_filehist_log to ecomp_sdk_os.cr_filehist_log;
rename table ecomp_sdk.cr_folder to ecomp_sdk_os.cr_folder;
rename table ecomp_sdk.cr_folder_access to ecomp_sdk_os.cr_folder_access;
rename table ecomp_sdk.cr_hist_user_map to ecomp_sdk_os.cr_hist_user_map;
rename table ecomp_sdk.cr_lu_file_type to ecomp_sdk_os.cr_lu_file_type;
rename table ecomp_sdk.cr_raptor_action_img to ecomp_sdk_os.cr_raptor_action_img;
rename table ecomp_sdk.cr_raptor_pdf_img to ecomp_sdk_os.cr_raptor_pdf_img;
rename table ecomp_sdk.cr_remote_schema_info to ecomp_sdk_os.cr_remote_schema_info;
rename table ecomp_sdk.cr_report to ecomp_sdk_os.cr_report;
rename table ecomp_sdk.cr_report_access to ecomp_sdk_os.cr_report_access;
rename table ecomp_sdk.cr_report_dwnld_log to ecomp_sdk_os.cr_report_dwnld_log;
rename table ecomp_sdk.cr_report_email_sent_log to ecomp_sdk_os.cr_report_email_sent_log;
rename table ecomp_sdk.cr_report_file_history to ecomp_sdk_os.cr_report_file_history;
rename table ecomp_sdk.cr_report_log to ecomp_sdk_os.cr_report_log;
rename table ecomp_sdk.cr_report_schedule to ecomp_sdk_os.cr_report_schedule;
rename table ecomp_sdk.cr_report_schedule_users to ecomp_sdk_os.cr_report_schedule_users;
rename table ecomp_sdk.cr_report_template_map to ecomp_sdk_os.cr_report_template_map;
rename table ecomp_sdk.cr_schedule_activity_log to ecomp_sdk_os.cr_schedule_activity_log;
rename table ecomp_sdk.cr_table_join to ecomp_sdk_os.cr_table_join;
rename table ecomp_sdk.cr_table_role to ecomp_sdk_os.cr_table_role;
rename table ecomp_sdk.cr_table_source to ecomp_sdk_os.cr_table_source;
rename table ecomp_sdk.fn_lu_timezone to ecomp_sdk_os.fn_lu_timezone;
rename table ecomp_sdk.fn_user to ecomp_sdk_os.fn_user;
rename table ecomp_sdk.fn_role to ecomp_sdk_os.fn_role;
rename table ecomp_sdk.fn_audit_action to ecomp_sdk_os.fn_audit_action;
rename table ecomp_sdk.fn_audit_action_log to ecomp_sdk_os.fn_audit_action_log;
rename table ecomp_sdk.fn_lu_activity to ecomp_sdk_os.fn_lu_activity;
rename table ecomp_sdk.fn_audit_log to ecomp_sdk_os.fn_audit_log;
rename table ecomp_sdk.fn_broadcast_message to ecomp_sdk_os.fn_broadcast_message;
rename table ecomp_sdk.fn_chat_logs to ecomp_sdk_os.fn_chat_logs;
rename table ecomp_sdk.fn_chat_room to ecomp_sdk_os.fn_chat_room;
rename table ecomp_sdk.fn_chat_users to ecomp_sdk_os.fn_chat_users;
rename table ecomp_sdk.fn_datasource to ecomp_sdk_os.fn_datasource;
rename table ecomp_sdk.fn_function to ecomp_sdk_os.fn_function;
rename table ecomp_sdk.fn_lu_alert_method to ecomp_sdk_os.fn_lu_alert_method;
rename table ecomp_sdk.fn_lu_broadcast_site to ecomp_sdk_os.fn_lu_broadcast_site;
rename table ecomp_sdk.fn_lu_menu_set to ecomp_sdk_os.fn_lu_menu_set;
rename table ecomp_sdk.fn_lu_priority to ecomp_sdk_os.fn_lu_priority;
rename table ecomp_sdk.fn_lu_role_type to ecomp_sdk_os.fn_lu_role_type;
rename table ecomp_sdk.fn_lu_tab_set to ecomp_sdk_os.fn_lu_tab_set;
rename table ecomp_sdk.fn_lu_message_location to ecomp_sdk_os.fn_lu_message_location;
rename table ecomp_sdk.fn_menu to ecomp_sdk_os.fn_menu;
rename table ecomp_sdk.fn_org to ecomp_sdk_os.fn_org;
rename table ecomp_sdk.fn_restricted_url to ecomp_sdk_os.fn_restricted_url;
rename table ecomp_sdk.fn_role_composite to ecomp_sdk_os.fn_role_composite;
rename table ecomp_sdk.fn_role_function to ecomp_sdk_os.fn_role_function;
rename table ecomp_sdk.fn_tab to ecomp_sdk_os.fn_tab;
rename table ecomp_sdk.fn_tab_selected to ecomp_sdk_os.fn_tab_selected;
rename table ecomp_sdk.fn_user_pseudo_role to ecomp_sdk_os.fn_user_pseudo_role;
rename table ecomp_sdk.fn_user_role to ecomp_sdk_os.fn_user_role;
rename table ecomp_sdk.schema_info to ecomp_sdk_os.schema_info;
rename table ecomp_sdk.fn_app to ecomp_sdk_os.fn_app;
rename table ecomp_sdk.fn_workflow to ecomp_sdk_os.fn_workflow;
rename table ecomp_sdk.fn_schedule_workflows to ecomp_sdk_os.fn_schedule_workflows;
rename table ecomp_sdk.demo_bar_chart to ecomp_sdk_os.demo_bar_chart;
rename table ecomp_sdk.demo_bar_chart_inter to ecomp_sdk_os.demo_bar_chart_inter;
rename table ecomp_sdk.demo_line_chart to ecomp_sdk_os.demo_line_chart;
rename table ecomp_sdk.demo_pie_chart to ecomp_sdk_os.demo_pie_chart;
rename table ecomp_sdk.demo_util_chart to ecomp_sdk_os.demo_util_chart;
rename table ecomp_sdk.demo_scatter_chart to ecomp_sdk_os.demo_scatter_chart;
rename table ecomp_sdk.demo_scatter_plot to ecomp_sdk_os.demo_scatter_plot;
rename table ecomp_sdk.fn_qz_job_details to ecomp_sdk_os.fn_qz_job_details;
rename table ecomp_sdk.fn_qz_triggers to ecomp_sdk_os.fn_qz_triggers;
rename table ecomp_sdk.fn_qz_simple_triggers to ecomp_sdk_os.fn_qz_simple_triggers;
rename table ecomp_sdk.fn_qz_cron_triggers to ecomp_sdk_os.fn_qz_cron_triggers;
rename table ecomp_sdk.fn_qz_simprop_triggers to ecomp_sdk_os.fn_qz_simprop_triggers;
rename table ecomp_sdk.fn_qz_blob_triggers to ecomp_sdk_os.fn_qz_blob_triggers;
rename table ecomp_sdk.fn_qz_calendars to ecomp_sdk_os.fn_qz_calendars;
rename table ecomp_sdk.fn_qz_paused_trigger_grps to ecomp_sdk_os.fn_qz_paused_trigger_grps;
rename table ecomp_sdk.fn_qz_fired_triggers to ecomp_sdk_os.fn_qz_fired_triggers;
rename table ecomp_sdk.fn_qz_scheduler_state to ecomp_sdk_os.fn_qz_scheduler_state;
rename table ecomp_sdk.fn_qz_locks to ecomp_sdk_os.fn_qz_locks;
rename table ecomp_sdk.rcloudinvocation to ecomp_sdk_os.rcloudinvocation;
rename table ecomp_sdk.rcloudnotebook to ecomp_sdk_os.rcloudnotebook;

USE ecomp_sdk_os;

--
-- NAME: V_URL_ACCESS; TYPE: VIEW
--
CREATE VIEW v_url_access AS
 SELECT DISTINCT M.ACTION AS URL,
    M.FUNCTION_CD
   FROM FN_MENU M
  WHERE (M.ACTION IS NOT NULL)
UNION
 SELECT DISTINCT T.ACTION AS URL,
    T.FUNCTION_CD
   FROM FN_TAB T
  WHERE (T.ACTION IS NOT NULL)
UNION
 SELECT R.RESTRICTED_URL AS URL,
    R.FUNCTION_CD
   FROM FN_RESTRICTED_URL R;

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

update fn_menu
set action = 'report_wizard.htm?r_action=report.create'
where menu_id = 87 and label = 'Create Reports';

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

drop schema ecomp_sdk;

commit;
