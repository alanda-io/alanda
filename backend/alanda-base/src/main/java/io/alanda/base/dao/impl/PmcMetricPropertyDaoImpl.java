package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcMetricPropertyDao;
import io.alanda.base.entity.PmcMetricProperty;

public class PmcMetricPropertyDaoImpl extends AbstractCrudDao<PmcMetricProperty> implements PmcMetricPropertyDao {

  public PmcMetricPropertyDaoImpl() {
    super();
  }

  public PmcMetricPropertyDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }

}
