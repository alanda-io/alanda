--liquibase formatted sql

--changeset fsa:init-pre-drop-types endDelimiter://

BEGIN
  FOR cur_proc IN (SELECT TYPE_NAME
                    FROM USER_TYPES)
  LOOP
    BEGIN
      execute immediate 'DROP TYPE ' || cur_proc.TYPE_NAME || ' FORCE';
    END;
  END LOOP;
END;
//
