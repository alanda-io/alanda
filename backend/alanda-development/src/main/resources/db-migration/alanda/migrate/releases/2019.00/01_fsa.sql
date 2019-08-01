--liquibase formatted sql

--changeset fsa:basic-camunda-user
Insert into PMC_GROUP (GUID, GROUPNAME, LONGNAME, GROUPSOURCE, CREATED, CREATEUSER, LASTUPDATE, UPDATEUSER, VERSION, SOURCEID, SOURCENAME,
                       ACTIVE, EMAIL, PHONE)
values (1, 'admin', 'Administrators', 'alanda', null, null, null, null, 0, null, null, 1, null, null);
Insert into PMC_USER (GUID, LOGINNAME, FIRSTNAME, SURNAME, EMAIL, MOBILE, LOCKED, CREATED, CREATEUSER, LASTUPDATE, UPDATEUSER, VERSION,
                      PASSWORD, LAST_LOGIN, PMC_DEPARTMENT, EXTERNALGUID, SOURCE, COMPANY)
values (1, 'alanda', 'Alan', 'Da', 'alanda@alanda.io', null, 0, null, null, null, null, 0, null, null, null, null, 'alanda', null);
Insert into PMC_USER_GROUP (REF_USER, REF_GROUP, SELECT_CONTACT)
values (1, 1, 1);

--changeset fsa:basic-camunda-auth
insert into ACT_RU_AUTHORIZATION (ID_, REV_, TYPE_, GROUP_ID_, RESOURCE_TYPE_, RESOURCE_ID_, PERMS_)
VALUES ('d99f1fbf-7cf6-11e3-9134-08002700741f', 1, 1, '1', '0', '*', '2147483647');
insert into ACT_RU_AUTHORIZATION (ID_, REV_, TYPE_, GROUP_ID_, RESOURCE_TYPE_, RESOURCE_ID_, PERMS_)
VALUES ('d99fbc00-7cf6-11e3-9134-08002700741f', 1, 1, '1', '1', '*', '2147483647');
insert into ACT_RU_AUTHORIZATION (ID_, REV_, TYPE_, GROUP_ID_, RESOURCE_TYPE_, RESOURCE_ID_, PERMS_)
VALUES ('d9a03131-7cf6-11e3-9134-08002700741f', 1, 1, '1', '2', '*', '2147483647');
insert into ACT_RU_AUTHORIZATION (ID_, REV_, TYPE_, GROUP_ID_, RESOURCE_TYPE_, RESOURCE_ID_, PERMS_)
VALUES ('d9a142a2-7cf6-11e3-9134-08002700741f', 1, 1, '1', '3', '*', '2147483647');
insert into ACT_RU_AUTHORIZATION (ID_, REV_, TYPE_, GROUP_ID_, RESOURCE_TYPE_, RESOURCE_ID_, PERMS_)
VALUES ('d9a1b7de-7cf6-11e3-9134-08002700741f', 1, 1, '1', '4', '*', '2147483647');
insert into ACT_RU_AUTHORIZATION (ID_, REV_, TYPE_, GROUP_ID_, RESOURCE_TYPE_, RESOURCE_ID_, PERMS_)
VALUES ('d99d4afc-7cf6-11e3-9134-08002700741f', 1, 1, '1', '2', '0', '2147483647');

--changeset yk:2019-06-camunda-auth runOnChange:false
Insert into ACT_RU_AUTHORIZATION (ID_,REV_,TYPE_,GROUP_ID_,USER_ID_,RESOURCE_TYPE_,RESOURCE_ID_,PERMS_) values ('2a9cf95e-885c-11e9-84b6-02420a640001',1,1,'1',null,6,'*',2147483647);
Insert into ACT_RU_AUTHORIZATION (ID_,REV_,TYPE_,GROUP_ID_,USER_ID_,RESOURCE_TYPE_,RESOURCE_ID_,PERMS_) values ('30bf8faf-885c-11e9-84b6-02420a640001',1,1,'1',null,8,'*',2147483647);
