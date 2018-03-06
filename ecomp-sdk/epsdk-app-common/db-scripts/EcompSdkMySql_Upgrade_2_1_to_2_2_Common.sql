use ecomp_sdk;

SET FOREIGN_KEY_CHECKS=0;

alter table fn_function
add type VARCHAR(20) NULL DEFAULT NULL;


alter table fn_function
add action VARCHAR(20) NULL DEFAULT NULL;

commit;