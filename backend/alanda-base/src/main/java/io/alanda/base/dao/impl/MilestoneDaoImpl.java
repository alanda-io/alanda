package io.alanda.base.dao.impl;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.MilestoneDao;
import io.alanda.base.entity.Milestone;


public class MilestoneDaoImpl extends AbstractCrudDao<Milestone> implements MilestoneDao {
  
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
    
    return em.createQuery("select m from Milestone m where m.idName = :idName", Milestone.class)
        .setParameter("idName", idName)
        .getSingleResult();
  }

}
