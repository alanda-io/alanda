package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.alanda.base.dao.AbstractDao;
import io.alanda.base.dao.PmcCommentDao;
import io.alanda.base.entity.PmcComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PmcCommentDaoImpl extends AbstractDao<PmcComment> implements PmcCommentDao {
  private static final Logger log = LoggerFactory.getLogger(PmcCommentDaoImpl.class);
  
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
    log.debug("Retrieving all comments for process with instance id {}", processInstanceId);

    return em
        .createNamedQuery("PmcComment.loadByProcInstId", PmcComment.class)
        .setParameter(1, processInstanceId)
        .getResultList();
  }

  @Override
  public void insert(PmcComment pmcComment) {
    log.debug("Creating new comment: {}", pmcComment);

    getEntityManager().persist(pmcComment);
  }

  @Override
  public List<PmcComment> getAllForProcessInstanceIdAndRefObjectId(String processInstanceId, long refObjectId) {
    log.debug("Retrieving all comments for process with instance id {} and refObjectId {}", processInstanceId, refObjectId);
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
