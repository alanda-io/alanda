--liquibase formatted sql

--changeset fsa:init-pre-drop-procedures-and-functions endDelimiter://

BEGIN
  FOR cur_proc IN (SELECT OBJECT_NAME, OBJECT_TYPE
                    FROM USER_PROCEDURES
                    WHERE OBJECT_TYPE in ('PROCEDURE', 'FUNCTION'))
  LOOP
    BEGIN
      execute immediate 'DROP ' || cur_proc.OBJECT_TYPE || ' ' || cur_proc.OBJECT_NAME;
    END;
  END LOOP;
END;
//
