use ecomp_sdk;

alter table fn_menu  
add index fn_menu_function_cd (function_cd ASC);
alter table fn_menu
add constraint fk_fn_menu_ref_223_fn_funct
foreign key (function_cd)
references fn_function (function_cd);

alter table fn_restricted_url  
add index fk_restricted_url_function_cd (function_cd ASC);
alter table fn_restricted_url
add constraint fk_restricted_url_function_cd
foreign key (function_cd)
references fn_function (function_cd);


alter table fn_role
modify role_name varchar(50) NOT NULL;

commit;