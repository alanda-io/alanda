package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcMetricEntityDao;
import io.alanda.base.entity.PmcMetricEntity;

public class PmcMetricEntityDaoImpl extends AbstractCrudDao<PmcMetricEntity> implements PmcMetricEntityDao {

  public PmcMetricEntityDaoImpl() {
    super();
  }

  public PmcMetricEntityDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

  @Override
  public List<PmcMetricEntity> getProcessEntities(String processKey) {
    return em
      .createQuery(
        "SELECT DISTINCT e FROM PmcMetricEntity e INNER JOIN e.properties p WHERE p.key = :processKey AND p.value = :processKeyValue")
      .setParameter("processKey", "process-key")
      .setParameter("processKeyValue", processKey)
      .getResultList();
  }
}
