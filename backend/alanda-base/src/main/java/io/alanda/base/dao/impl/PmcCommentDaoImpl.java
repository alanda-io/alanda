package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.alanda.base.dao.AbstractDao;
import io.alanda.base.dao.PmcCommentDao;
import io.alanda.base.entity.PmcComment;

public class PmcCommentDaoImpl extends AbstractDao<PmcComment> implements PmcCommentDao {
  
  @PersistenceContext(name = "pmcDB", unitName = "pmcDB")
  private EntityManager em;
  
  
  public PmcCommentDaoImpl() {
    // TODO Auto-generated constructor stub
  }
  
  public PmcCommentDaoImpl(EntityManager em) {
    this.em = em;
  }
  
  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  public List<PmcComment> getAllForProcessInstanceId(String processInstanceId) {
    return em
        .createNamedQuery("PmcComment.loadByProcInstId", PmcComment.class)
        .setParameter(1, processInstanceId)
        .getResultList();
  }

  @Override
  public void insert(PmcComment pmcComment) {
    getEntityManager().persist(pmcComment);
  }

  @Override
  public List<PmcComment> getAllForProcessInstanceIdAndRefObjectId(String processInstanceId, long refObjectId) {
    return em
        .createNamedQuery("PmcComment.loadByProcInstIdAndRefObjectId", PmcComment.class)
        .setParameter(1, processInstanceId)
        .setParameter(2, refObjectId)
        .getResultList();
  }

  @Override
  protected Class<PmcComment> getEntityClass() {
    return PmcComment.class;
  }

}
