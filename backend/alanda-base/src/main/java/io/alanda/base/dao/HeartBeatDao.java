package io.alanda.base.dao;

import java.util.Optional;

import io.alanda.base.entity.PmcHeartBeat;

public interface HeartBeatDao extends CrudDao<PmcHeartBeat> {

  Optional<PmcHeartBeat> getHeartBeatByProcessKey(String key);

}
