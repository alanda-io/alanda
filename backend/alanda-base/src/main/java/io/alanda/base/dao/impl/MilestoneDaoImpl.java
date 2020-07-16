package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.MilestoneDao;
import io.alanda.base.entity.Milestone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MilestoneDaoImpl extends AbstractCrudDao<Milestone> implements MilestoneDao {
  private static final Logger log = LoggerFactory.getLogger(MilestoneDaoImpl.class);
  
  public MilestoneDaoImpl() {
    super();
  }

  public MilestoneDaoImpl(EntityManager em) {
    super(em);
  }

  @Override
  public EntityManager getEntityManager() {
    return em;
  }
  
  @Override
  protected Class<Milestone> getEntityClass() {
    return Milestone.class;
  }

  @Override
  public Milestone getMilestoneByIdName(String idName) {
    log.debug("Retrieving milestone by idName: {}", idName);
    
    return em.createQuery("select m from Milestone m where m.idName = :idName", Milestone.class)
        .setParameter("idName", idName)
        .getSingleResult();
  }

}
