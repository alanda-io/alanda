--liquibase formatted sql

--changeset yk:2018-13-phaseName runOnChange:false
ALTER TABLE PMC_PROJECT_PROCESS
    ADD PHASE_NAME varchar2(255);


--changeset yk:2018.13.00-pmc-reportconfig-update runOnChange:false
alter table pmc_reportconfig
    modify querystring varchar2(4000);


--changeset jlo:2018-17-01-create-mail-tables runOnChange:false
CREATE TABLE "MAIL"
(
    "GUID"         NUMBER(*, 0) NOT NULL,
    "CREATED"      DATE,
    "CREATEUSER"   NUMBER(*, 0),
    "LASTUPDATE"   DATE,
    "UPDATEUSER"   NUMBER(*, 0),
    "VERSION"      NUMBER(*, 0) NOT NULL,
    "MODULE_NAME"  VARCHAR2(50),
    "MODULE_ID"    VARCHAR2(50),
    "STATE"        VARCHAR2(10) NOT NULL,
    "RESOLUTION"   VARCHAR2(10),
    "ADDRESS_TO"   VARCHAR2(200),
    "ADDRESS_FROM" VARCHAR2(200),
    "ADDRESS_CC"   VARCHAR2(200),
    "SUBJECT"      VARCHAR2(500),
    "BODY"         CLOB,
    "MAIL"         CLOB,
    PRIMARY KEY ("GUID")
);

CREATE TABLE "MAIL_ATTACHMENT"
(
    "GUID"       NUMBER(*, 0)  NOT NULL,
    "CREATED"    DATE,
    "CREATEUSER" NUMBER(*, 0),
    "LASTUPDATE" DATE,
    "UPDATEUSER" NUMBER(*, 0),
    "VERSION"    NUMBER(*, 0)  NOT NULL,
    "REF_MAIL"   NUMBER(*, 0)  NOT NULL REFERENCES MAIL (GUID) ON DELETE CASCADE,
    "NAME"       VARCHAR2(100) NOT NULL,
    "MIME_TYPE"  VARCHAR2(100),
    "BLOB"       BLOB,
    PRIMARY KEY ("GUID")
);


--changeset yk:2019-02-01-update-table-2 runOnChange:false
ALTER TABLE PMC_USER
    MODIFY LOCKED NUMBER(1, 0) DEFAULT 0 NOT NULL;


--changeset yk:2018.19.00_hb runOnChange:false
CREATE TABLE PMC_HEARTBEAT
(
    GUID        number(38, 0) NOT NULL,
    NAME        VARCHAR2(30)  NOT NULL,
    FINISHED_AT date          NOT NULL,
    DUE_DATE    date          NOT NULL,
    DEADLINE    date          NOT NULL,

    CONSTRAINT heartbeat_pk PRIMARY KEY (guid),
    CONSTRAINT unique_hb_name UNIQUE (name)
);


--changeset yk:2018.19.00_hb1 runOnChange:false
CREATE TABLE PMC_HEARTBEAT_DEF
(
    GUID             number(38, 0) NOT NULL,
    VERSION          number(38, 0) NOT NULL,
    NAME             VARCHAR2(30)  NOT NULL,
    CRON             VARCHAR2(30)  NOT NULL,
    PRIORITY         number(38, 0) NOT NULL,
    DURATION_MINUTES number(38, 0) NOT NULL,
    TOLERANCE        number(38, 0) NOT NULL,
    CREATED          date,
    CREATEUSER       number(38, 0),
    LASTUPDATE       date,
    UPDATEUSER       number(38, 0),

    CONSTRAINT heartbeat_def_pk PRIMARY KEY (guid),
    CONSTRAINT unique_hb_def_name UNIQUE (name)
);


