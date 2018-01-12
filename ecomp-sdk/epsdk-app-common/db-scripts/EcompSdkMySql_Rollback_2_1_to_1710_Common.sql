use ecomp_sdk;

DELETE FROM fn_restricted_url
where RESTRICTED_URL in('admin', 'get_role', 'get_role_functions', 'role_list/*', 'role_function_list/*', 'addRole', 'addRoleFunction', 'removeRole',
						'removeRoleFunction', 'profile/*', 'samplePage','workflows', 'workflows/list', 'workflows/addWorkflow', 'workflows/saveCronJob',
						'workflows/editWorkflow', 'workflows/removeWorkflow', 'workflows/removeAllWorkflows','role/saveRole.htm','post_search/process', 'post_search/search', 'report/wizard/retrieve_def_tab_wise_data/*',
						'report/wizard/retrieve_form_tab_wise_data/*', 'report/wizard/retrieve_sql_tab_wise_data/*', 'report/wizard/security/*', 'report/wizard/copy_report/*', 'report/wizard/save_def_tab_data/*', 'report/wizard/retrieve_data/true',
						'report/security/*');

commit;