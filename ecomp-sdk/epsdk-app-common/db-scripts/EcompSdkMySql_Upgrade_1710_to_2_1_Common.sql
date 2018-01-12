use ecomp_sdk;

SET FOREIGN_KEY_CHECKS=0;

INSERT INTO fn_restricted_url VALUES('admin','menu_admin');
INSERT INTO fn_restricted_url VALUES('get_role','menu_admin');
INSERT INTO fn_restricted_url VALUES('get_role_functions','menu_admin');
INSERT INTO fn_restricted_url VALUES('role_list/*','menu_admin');
INSERT INTO fn_restricted_url VALUES('role_function_list/*','menu_admin');
INSERT INTO fn_restricted_url VALUES('addRole','menu_admin');
INSERT INTO fn_restricted_url VALUES('addRoleFunction','menu_admin');
INSERT INTO fn_restricted_url VALUES('removeRole','menu_admin');
INSERT INTO fn_restricted_url VALUES('removeRoleFunction','menu_admin');
INSERT INTO fn_restricted_url VALUES('profile/*','menu_admin');
INSERT INTO fn_restricted_url VALUES('samplePage','menu_sample');
INSERT INTO fn_restricted_url VALUES('workflows','menu_admin');
INSERT INTO fn_restricted_url VALUES('workflows/list','menu_admin');
INSERT INTO fn_restricted_url VALUES('workflows/addWorkflow','menu_admin');
INSERT INTO fn_restricted_url VALUES('workflows/saveCronJob','menu_admin');
INSERT INTO fn_restricted_url VALUES('workflows/editWorkflow','menu_admin');
INSERT INTO fn_restricted_url VALUES('workflows/removeWorkflow','menu_admin');
INSERT INTO fn_restricted_url VALUES('workflows/removeAllWorkflows','menu_admin');
INSERT INTO fn_restricted_url VALUES('role/saveRole.htm','menu_admin');
INSERT INTO fn_restricted_url VALUES('post_search/process','menu_admin');
INSERT INTO fn_restricted_url VALUES('post_search/search','menu_admin');
INSERT INTO fn_restricted_url VALUES('post_search/search','menu_profile');
INSERT INTO fn_restricted_url VALUES('report/wizard/retrieve_def_tab_wise_data/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/retrieve_form_tab_wise_data/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/retrieve_sql_tab_wise_data/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/security/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/copy_report/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/save_def_tab_data/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/retrieve_data/true','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/security/*','menu_reports');

commit;