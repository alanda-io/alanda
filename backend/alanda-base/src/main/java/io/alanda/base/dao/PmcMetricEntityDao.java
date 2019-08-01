package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcMetricEntity;

public interface PmcMetricEntityDao extends CrudDao<PmcMetricEntity> {

  List<PmcMetricEntity> getProcessEntities(String processKey);
}
