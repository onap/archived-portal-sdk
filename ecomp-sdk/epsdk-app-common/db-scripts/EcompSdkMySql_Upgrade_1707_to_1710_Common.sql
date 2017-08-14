use ecomp_sdk;

SET FOREIGN_KEY_CHECKS=0;

alter table fn_menu 
drop foreign key fk_fn_menu_ref_223_fn_funct;
alter table fn_menu 
drop index fn_menu_function_cd;

alter table fn_restricted_url 
drop foreign key fk_restricted_url_function_cd;
alter table fn_restricted_url 
drop index fk_restricted_url_function_cd;

alter table fn_role
modify role_name varchar(300) NOT NULL;

commit;