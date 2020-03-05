--liquibase formatted sql

--changeset fsa:alanda-demo-projecttype
Insert into PMC_PROJECTTYPE
 (GUID,VERSION,IDNAME,NAME,ALLOWEDTAGS,READRIGHTS,WRITERIGHTS,DELETERIGHTS,ALLOWEDPROCESSES,STARTPROCESS,OBJECTTYPE,ROLES,ALLOWED_SUBTYPES,ADDITIONAL_PROPERTIES,CONFIGURATION,LISTENERS,CREATERIGHTS,DETAILS_TEMPLATE,PROPERTIES_TEMPLATE,CREATION_PROPERTIES_TEMPLATE) values
 (SEQ_GUID_PK.nextval,1,'VACATION','Vacation Request','Tag1, Tag2, Tag3, Tag4','admin','admin','admin',null,'vacation-request','kk',null,null,null,null,'pmc-authorization','admin',null,'VACATION',null);


--changeset yk:2019-06-camunda-auth runOnChange:false
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (SEQ_GUID_PK.nextval,0,'project:create:VACATION');
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (SEQ_GUID_PK.nextval,0,'project:read:VACATION');
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (SEQ_GUID_PK.nextval,0,'project:write:VACATION');
Insert into PMC_USER_PERMISSION (REF_USER,REF_PERMISSION) values ((SELECT guid FROM PMC_USER WHERE loginname = 'alanda'), (SELECT guid FROM PMC_PERMISSION WHERE KEY = 'project:create:VACATION'));
Insert into PMC_USER_PERMISSION (REF_USER,REF_PERMISSION) values ((SELECT guid FROM PMC_USER WHERE loginname = 'alanda'), (SELECT guid FROM PMC_PERMISSION WHERE KEY = 'project:read:VACATION'));
Insert into PMC_USER_PERMISSION (REF_USER,REF_PERMISSION) values ((SELECT guid FROM PMC_USER WHERE loginname = 'alanda'), (SELECT guid FROM PMC_PERMISSION WHERE KEY = 'project:write:VACATION'));

--changeset fsa:alanda-demo-users-and-groups
insert into pmc_group
(guid, groupname, longname, groupsource, created, createuser, version, active)
values (SEQ_GUID_PK.nextval, 'team-red', 'Team Red', 'alanda', sysdate, (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'), 0, 1);

insert into pmc_group
(guid, groupname, longname, groupsource, created, createuser, version, active)
values (SEQ_GUID_PK.nextval, 'team-blue', 'Team Blue', 'alanda', sysdate, (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'), 0, 1);

Insert into PMC_ROLE (GUID, VERSION, NAME)
values (SEQ_GUID_PK.nextval, 0, 'vacation-requestor');
Insert into PMC_ROLE (GUID, VERSION, NAME)
values (SEQ_GUID_PK.nextval, 0, 'vacation-approver');

select *
from pmc_user;

insert into pmc_user
(guid, loginname, firstname, surname, email, source, version)
values (SEQ_GUID_PK.nextval, 'franzhr', 'Franz', 'HR', 'hr@alanda.io', 'alanda', 0);

insert into pmc_user
(guid, loginname, firstname, surname, email, source, version)
values (SEQ_GUID_PK.nextval, 'susihackler', 'Susi', 'Hackler', 'sh@alanda.io', 'alanda', 0);

insert into pmc_user
(guid, loginname, firstname, surname, email, source, version)
values (SEQ_GUID_PK.nextval, 'herbertputzer', 'Herbert', 'Putzer', 'hp@alanda.io', 'alanda', 0);

insert into pmc_user
(guid, loginname, firstname, surname, email, source, version)
values (SEQ_GUID_PK.nextval, 'hansleiden', 'Hans', 'Leiden', 'hl@alanda.io', 'alanda', 0);

select *
from pmc_user_group;

insert into pmc_user_group
    (ref_user, ref_group, select_contact)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'franzhr'), (SELECT guid FROM PMC_GROUP WHERE groupname = 'team-red'), (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'));

insert into pmc_user_group
    (ref_user, ref_group, select_contact)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'susihackler'), (SELECT guid FROM PMC_GROUP WHERE groupname = 'team-red'), (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'));

insert into pmc_user_group
    (ref_user, ref_group, select_contact)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'herbertputzer'), (SELECT guid FROM PMC_GROUP WHERE groupname = 'team-blue'), (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'));

insert into pmc_user_group
    (ref_user, ref_group, select_contact)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'hansleiden'), (SELECT guid FROM PMC_GROUP WHERE groupname = 'team-blue'), (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'));

select *
from pmc_user_role;

insert into pmc_user_role
    (ref_user, ref_role)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'franzhr'), (SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'));

insert into pmc_user_role
    (ref_user, ref_role)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'susihackler'), (SELECT guid FROM PMC_ROLE WHERE name = 'vacation-approver'));

insert into pmc_user_role
    (ref_user, ref_role)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'herbertputzer'), (SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'));

insert into pmc_user_role
    (ref_user, ref_role)
values ((SELECT guid FROM PMC_USER WHERE loginname = 'hansleiden'), (SELECT guid FROM PMC_ROLE WHERE name = 'vacation-approver'));

--changeset fsa:alanda-demo-group-permissions
Insert into PMC_ROLE_PERMISSION (REF_ROLE,REF_PERMISSION) values ((SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'), (SELECT guid FROM PMC_PERMISSION WHERE KEY = 'project:create:VACATION'));
Insert into PMC_ROLE_PERMISSION (REF_ROLE,REF_PERMISSION) values ((SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'), (SELECT guid FROM PMC_PERMISSION WHERE KEY = 'project:read:VACATION'));
Insert into PMC_ROLE_PERMISSION (REF_ROLE,REF_PERMISSION) values ((SELECT guid FROM PMC_ROLE WHERE name = 'vacation-approver'), (SELECT guid FROM PMC_PERMISSION WHERE KEY = 'project:create:VACATION'));

--changeset yko:alanda-demo-milestones
Insert into PMC_MILESTONE (GUID, IDNAME, DESCRIPTION, CREATED, CREATEUSER, LASTUPDATE, UPDATEUSER, VERSION) values (SEQ_GUID_PK.nextval, 'VACATION_START', 'Vacation Start', CURRENT_DATE, (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'), null, null, 1);
Insert into PMC_MILESTONE (GUID, IDNAME, DESCRIPTION, CREATED, CREATEUSER, LASTUPDATE, UPDATEUSER, VERSION) values (SEQ_GUID_PK.nextval, 'VACATION_END', 'Vacation End', CURRENT_DATE, (SELECT guid FROM PMC_USER WHERE loginname = 'alanda'), null, null, 1);

--changeset yko:alanda-demo-milestones-permissions
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (SEQ_GUID_PK.nextval,0,'ms:write:VACATION:*:ACTIVE:*:VACATION_START:fc');
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (SEQ_GUID_PK.nextval,0,'ms:write:VACATION:*:ACTIVE:*:VACATION_END:fc');
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (SEQ_GUID_PK.nextval,0,'ms:write:VACATION:*:ACTIVE:*:VACATION_START:act');
Insert into PMC_PERMISSION (GUID,VERSION,KEY) values (SEQ_GUID_PK.nextval,0,'ms:write:VACATION:*:ACTIVE:*:VACATION_END:act');
Insert into PMC_ROLE_PERMISSION (REF_ROLE,REF_PERMISSION) values ((SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'), (SELECT guid FROM PMC_PERMISSION WHERE key = 'ms:write:VACATION:*:ACTIVE:*:VACATION_START:fc'));
Insert into PMC_ROLE_PERMISSION (REF_ROLE,REF_PERMISSION) values ((SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'), (SELECT guid FROM PMC_PERMISSION WHERE key = 'ms:write:VACATION:*:ACTIVE:*:VACATION_END:fc'));
Insert into PMC_ROLE_PERMISSION (REF_ROLE,REF_PERMISSION) values ((SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'), (SELECT guid FROM PMC_PERMISSION WHERE key = 'ms:write:VACATION:*:ACTIVE:*:VACATION_START:act'));
Insert into PMC_ROLE_PERMISSION (REF_ROLE,REF_PERMISSION) values ((SELECT guid FROM PMC_ROLE WHERE name = 'vacation-requestor'), (SELECT guid FROM PMC_PERMISSION WHERE key = 'ms:write:VACATION:*:ACTIVE:*:VACATION_END:act'));
