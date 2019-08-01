--liquibase formatted sql

--changeset fsa:init-pre-drop-synonyms endDelimiter://

BEGIN
  FOR cur_syn IN (SELECT synonym_name
                  FROM user_synonyms)
  LOOP
    BEGIN
      execute immediate 'DROP SYNONYM ' || cur_syn.synonym_name || ' FORCE';
    END;
  END LOOP;
END;
//
