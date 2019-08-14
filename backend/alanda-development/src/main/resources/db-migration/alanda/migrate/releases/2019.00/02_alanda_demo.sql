--liquibase formatted sql

--changeset fsa:alanda-demo-projecttype
Insert into PMC_PROJECTTYPE
 (GUID,VERSION,IDNAME,NAME,ALLOWEDTAGS,READRIGHTS,WRITERIGHTS,DELETERIGHTS,ALLOWEDPROCESSES,STARTPROCESS,OBJECTTYPE,ROLES,ALLOWED_SUBTYPES,ADDITIONAL_PROPERTIES,CONFIGURATION,LISTENERS,CREATERIGHTS,DETAILS_TEMPLATE,PROPERTIES_TEMPLATE,CREATION_PROPERTIES_TEMPLATE) values
 (1,1,'VACATION','Vacation Request','Tag1, Tag2, Tag3, Tag4','admin','admin','admin',null,'vacation-request','kk',null,null,null,null,'pmc-authorization','admin',null,'VACATION',null);


--changeset yk:2019-06-camunda-auth runOnChange:false
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (1,0,'project:create:VACATION');
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (2,0,'project:read:VACATION');
Insert into PMC_USER_PERMISSION (REF_USER,REF_PERMISSION) values (1,1);
Insert into PMC_USER_PERMISSION (REF_USER,REF_PERMISSION) values (1,2);

--changeset fsa:alanda-demo-users-and-groups
insert into pmc_group
(guid, groupname, longname, groupsource, created, createuser, version, active)
values (2, 'vacation-requestor', 'Vacation Requestor', 'alanda', sysdate, 1, 0, 1);

insert into pmc_group
(guid, groupname, longname, groupsource, created, createuser, version, active)
values (3, 'vacation-approver', 'Vacation Approver', 'alanda', sysdate, 1, 0, 1);

select *
from pmc_user;

insert into pmc_user
(guid, loginname, firstname, surname, email, source, version)
values (2, 'franzhr', 'Franz', 'HR', 'hr@alanda.io', 'alanda', 0);

insert into pmc_user
(guid, loginname, firstname, surname, email, source, version)
values (3, 'susihackler', 'Susi', 'Hackler', 'sh@alanda.io', 'alanda', 0);

insert into pmc_user
(guid, loginname, firstname, surname, email, source, version)
values (4, 'herbertputzer', 'Herbert', 'Putzer', 'hp@alanda.io', 'alanda', 0);

select *
from pmc_user_group;

insert into pmc_user_group
    (ref_user, ref_group, select_contact)
values (2, 3, 1);

insert into pmc_user_group
    (ref_user, ref_group, select_contact)
values (3, 2, 1);

insert into pmc_user_group
    (ref_user, ref_group, select_contact)
values (4, 2, 1);


--changeset fsa:alanda-demo-group-permissions
Insert into PMC_GROUP_PERMISSION (REF_GROUP,REF_PERMISSION) values (2,1);
Insert into PMC_GROUP_PERMISSION (REF_GROUP,REF_PERMISSION) values (2,2);
Insert into PMC_GROUP_PERMISSION (REF_GROUP,REF_PERMISSION) values (3,2);
