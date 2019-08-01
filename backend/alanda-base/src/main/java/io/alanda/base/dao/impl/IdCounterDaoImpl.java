package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import io.alanda.base.dao.AbstractDao;
import io.alanda.base.dao.IdCounterDao;
import io.alanda.base.entity.IdCounter;

public class IdCounterDaoImpl extends AbstractDao<IdCounter> implements IdCounterDao {

  @PersistenceContext(name = "pmcDB", unitName = "pmcDB")
  private EntityManager em;

  @Override
  public long getNext(String prefix) {
    IdCounter mc = getEntityManager().find(getEntityClass(), prefix, LockModeType.PESSIMISTIC_WRITE);
    if (mc == null) {
      mc = new IdCounter();
      mc.setPrefix(prefix);
      mc.setCurrentNumber(1);
      this.getEntityManager().persist(mc);
      System.out.println("Value for new prefix " + prefix + " is 1");
      return 1;
    }
    mc.setCurrentNumber(mc.getCurrentNumber() + 1);
    System.out.println("Value for prefix " + prefix + " is " + mc.getCurrentNumber());
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
