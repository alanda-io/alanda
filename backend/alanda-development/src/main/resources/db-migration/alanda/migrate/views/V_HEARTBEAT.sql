--liquibase formatted sql

--changeset yk:v_heartbeat runOnChange:true

CREATE OR REPLACE FORCE VIEW "V_HEARTBEAT" ("NAME", "PRIORITY", "FINISHED_AT", "DEADLINE") AS
select d.name, d.PRIORITY, h.FINISHED_AT, h.DUE_DATE + (d.DURATION_MINUTES + d.TOLERANCE) / 1440 deadline
from PMC_HEARTBEAT_DEF d,
     PMC_HEARTBEAT h
where d.name = h.name;
