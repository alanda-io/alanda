CREATE TABLE AL_CHECKLIST_TEMPLATE
(
    guid         NUMBER(38)    NOT NULL,
    name         VARCHAR2(100) NOT NULL,
    item_backend VARCHAR2(10)  NOT NULL,
    created      DATE,
    createuser   NUMBER(38, 0),
    lastupdate   DATE,
    updateuser   NUMBER(38, 0),
    PRIMARY KEY (guid)
);

CREATE TABLE AL_CHECKLIST_TEMPLATE_CHECKLIST
(
    guid                NUMBER(38)    NOT NULL,
    template NUMBER(38)    NOT NULL REFERENCES AL_CHECKLIST_TEMPLATE,
    user_task_def_key   VARCHAR2(100) NOT NULL,
    created             DATE,
    createuser          NUMBER(38, 0),
    lastupdate          DATE,
    updateuser          NUMBER(38, 0),
    PRIMARY KEY (guid)
);

CREATE TABLE AL_CHECKLIST
(
    guid                          NUMBER(38)    NOT NULL,
    template_checklist NUMBER(38)    NOT NULL REFERENCES AL_CHECKLIST_TEMPLATE_CHECKLIST,
    user_task                     NVARCHAR2(64) NOT NULL REFERENCES ACT_HI_TASKINST,
    created                       DATE,
    createuser                    NUMBER(38, 0),
    lastupdate                    DATE,
    updateuser                    NUMBER(38, 0),
    PRIMARY KEY (guid)
);

CREATE TABLE AL_CHECKLIST_ITEM_DEFINITION
(
    guid                           NUMBER(38)    NOT NULL,
    template                       NUMBER(38) REFERENCES AL_CHECKLIST_TEMPLATE,
    checklist NUMBER(38) REFERENCES AL_CHECKLIST,
    key                            VARCHAR2(100) NOT NULL,
    display_text                   VARCHAR2(100) NOT NULL,
    required                       NUMBER(1, 0)  NOT NULL
        CONSTRAINT
            al_checklist_item_definition_required_boolean CHECK (required IN (0, 1)),
    sort_order                     NUMBER(4, 0),
    created                        DATE,
    createuser                     NUMBER(38, 0),
    lastupdate                     DATE,
    updateuser                     NUMBER(38, 0),
    PRIMARY KEY (guid),
    CONSTRAINT al_checklist_only_template_or_taskinst
        CHECK ((template IS NOT NULL OR checklist IS NOT NULL)
            AND (template IS NULL OR checklist IS NULL))
);

CREATE TABLE AL_CHECKLIST_ITEM
(
    guid       NUMBER(38)   NOT NULL,
    definition NUMBER(38)   NOT NULL REFERENCES AL_CHECKLIST_ITEM_DEFINITION,
    status     NUMBER(1, 0) NOT NULL
        CONSTRAINT
            al_checklist_item_status_boolean CHECK (
            status IN (0, 1)),
    created    DATE,
    createuser NUMBER(38, 0),
    lastupdate DATE,
    updateuser NUMBER(38, 0),
    PRIMARY KEY (guid)
);
