--liquibase formatted sql

--changeset yk:2019-06-camunda-auth runOnChange:false
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (1,0,'project:create:VACATION');
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (2,0,'project:read:VACATION');
Insert into PMC_USER_PERMISSION (REF_USER,REF_PERMISSION) values (1,1);
Insert into PMC_USER_PERMISSION (REF_USER,REF_PERMISSION) values (1,2);