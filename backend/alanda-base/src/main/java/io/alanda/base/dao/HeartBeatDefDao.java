package io.alanda.base.dao;

import java.util.Optional;

import io.alanda.base.entity.PmcHeartBeatDef;

public interface HeartBeatDefDao extends CrudDao<PmcHeartBeatDef> {

  Optional<PmcHeartBeatDef> getHeartBeatDefByProcessKey(String key);

}
