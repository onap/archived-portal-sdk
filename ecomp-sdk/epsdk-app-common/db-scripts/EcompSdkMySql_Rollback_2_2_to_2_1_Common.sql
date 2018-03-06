use ecomp_sdk;


ALTER TABLE fn_function
DROP COLUMN type;


ALTER TABLE fn_function
DROP COLUMN action;

commit;