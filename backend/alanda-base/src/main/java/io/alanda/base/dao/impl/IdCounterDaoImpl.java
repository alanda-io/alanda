package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import io.alanda.base.dao.AbstractDao;
import io.alanda.base.dao.IdCounterDao;
import io.alanda.base.entity.IdCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdCounterDaoImpl extends AbstractDao<IdCounter> implements IdCounterDao {
  private static final Logger log = LoggerFactory.getLogger(IdCounterDaoImpl.class);

  @PersistenceContext(name = "pmcDB", unitName = "pmcDB")
  private EntityManager em;

  @Override
  public long getNext(String prefix) {
    log.trace("Retrieving next id for prefix {}...", prefix);

    IdCounter mc = getEntityManager().find(getEntityClass(), prefix, LockModeType.PESSIMISTIC_WRITE);
    if (mc == null) {
      mc = new IdCounter();
      mc.setPrefix(prefix);
      mc.setCurrentNumber(1);
      this.getEntityManager().persist(mc);
      log.debug("...next id for prefix {} is 1", prefix);
      return 1;
    }
    mc.setCurrentNumber(mc.getCurrentNumber() + 1);
    log.debug("...next id for prefix {} is {}", prefix, mc.getCurrentNumber());
    return mc.getCurrentNumber();
  }

  @Override
  protected Class<IdCounter> getEntityClass() {
    return IdCounter.class;
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

}
